/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2017年7月13日     hemm     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.model.httpd;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.evideo.kmbox.util.EvLog;

/**
 * [功能说明]
 */
public class ForceReadLocalInputStream extends FileInputStream {
    private static final String TAG = ForceReadLocalInputStream.class.getSimpleName();
    // 缓冲区实际数据大小
    private int mBufferValidSize = 0;
    // 缓冲区已被读取的大小
    private int mBufferHasReadSize = 0;
    // 缓冲区,32K大小
    private byte[] mBuffer = null;
    private long BUFFERSIZE = 32768;
    private File mFile = null;
    private long mFirstSectionSize = 0L;
    // 已经读取的文件大小
    private long mHasReadFileSize = 0L;
    // 已经读取的解密数据大小
    private long mHasReadDataSize = 0L;
    private long mSkipSize = 0;
    private long mSkippedSize = 0;
    private boolean mFirstSection = true;
    private long mTotalLengthOfNetWork = 0;
    private long mId = 0;

    public ForceReadLocalInputStream(String path, long id, long fileTotalLen) throws FileNotFoundException {
        super(path);
        mId = id;
        mTotalLengthOfNetWork = fileTotalLen;
        mBuffer = new byte[(int) BUFFERSIZE];
        mFile = new File(path);
    }

    @Override
    public void close() throws IOException {
        super.close();
        if (mFile != null) {
            super.close();
            mFile = null;
        }
        mBuffer = null;
    }

    // 返回解密后的长度
    public long getDataTotalLength() {
        long totallength = 0;
        if (mTotalLengthOfNetWork > 0) {
            totallength = mTotalLengthOfNetWork - (BUFFERSIZE - mFirstSectionSize);
        } else {
            totallength = mFile.length() - (BUFFERSIZE - mFirstSectionSize);
        }
        EvLog.i(TAG, mId + ",mTotalLengthOfNetWork:" + mTotalLengthOfNetWork + ",Totallength:" + totallength);
        return totallength;
    }

    public long skip(long skipSize) throws IOException {
        mSkipSize = skipSize;
        this.handleSkip();
        long skipNum = skipSize - mSkipSize;
        EvLog.i(TAG, "need skip:" + skipSize + ",real skip" + skipNum);
        return skipNum;
    }

    @Override
    public int available() {
        int available = (int) (getDataTotalLength() - mHasReadDataSize);
        EvLog.i(TAG, "available:" + available);
        return available;
    }

    private boolean handleSkip() throws IOException {
        if (mSkipSize <= 0) {
            return true;
        }

        long length = mFile.length();
        long available = length - (BUFFERSIZE - mFirstSectionSize) - mHasReadDataSize - mSkippedSize;

        if (mSkipSize > available) {
            return false;
        }

        long skippedSize = skipBuffer((int) mSkipSize);
        mSkippedSize += skippedSize;
        mSkipSize -= skippedSize;
        mHasReadDataSize += skippedSize;

        while (mSkipSize > 0) {
            if (mSkipSize <= BUFFERSIZE) {
                int dataSize = readDataFromFile();
                if (dataSize < 0) {
                    return false;
                } else if (dataSize == 0) {
                    return true;
                }

                skippedSize = skipBuffer((int) mSkipSize);

                if (skippedSize != mSkipSize) {
                    EvLog.e(KmHttpdServer.TAG, "skip buffer failed:mSkipSize=" + mSkipSize + ",skippedSize=" + skippedSize);
                    return false;
                }

                mSkippedSize += skippedSize;
                mSkipSize -= skippedSize;
                mHasReadDataSize += skippedSize;
            } else {
                int skipSection = (int) ((length - mHasReadFileSize) / BUFFERSIZE);

                if (skipSection == 0) {
                    break;
                }

                int expectedSkipSection = (int) (mSkipSize / BUFFERSIZE);
                if (expectedSkipSection < skipSection) {
                    skipSection = expectedSkipSection;
                }

                skippedSize = skipSection * BUFFERSIZE;
                if (skippedSize > 0) {
                    skippedSize = super.skip(skippedSize);
                    mSkipSize -= skippedSize;
                    mSkippedSize += skippedSize;
                    mHasReadFileSize += skippedSize;
                    mHasReadDataSize += skippedSize;
                }
            }
        }

        return true;
    }

    @Override
    public int read(byte[] buffer) throws IOException {
        return read(buffer, 0, buffer.length);
    }

    @Override
    public int read(byte[] buffer, int byteOffset, int byteCount)
            throws IOException {
        if (!handleSkip()) {
            EvLog.i(TAG, "read failed handleSkip");
            return -1;
        }

        if (mSkipSize > 0) {
            EvLog.i(TAG, "read failed mSkipSize > 0");
            return 0;
        }

        int requireLength = byteCount;
        //从解密缓冲区读取数据
        int readSize = readFromBuffer(buffer, byteOffset, requireLength);
        if (readSize == 0) {
            mBufferValidSize = 0;
            mBufferHasReadSize = 0;
            //从文件中读取数据,解密到解密缓冲区
            int dataSize = readDataFromFile();
            int sleepCount = 0;
            while (dataSize <= 0 && sleepCount <= 20) {
//                EvLog.i("mTotalLengthOfNetWork:" + mTotalLengthOfNetWork + ",mHasReadFileSize:" + mHasReadFileSize);
                if (mTotalLengthOfNetWork == mHasReadFileSize) {
                    EvLog.e(TAG, "read tail");
                    break;
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                dataSize = readDataFromFile();
                sleepCount++;
            }
            readSize = readFromBuffer(buffer, byteOffset, requireLength);
            mHasReadDataSize += readSize;
        }
        return readSize;
    }


    private int readFromBuffer(byte[] buffer, int offset, int size) {
        int remain = mBufferValidSize - mBufferHasReadSize;

        if (remain <= 0) {
            return 0;
        }

        int copySize = remain > size ? size : remain;
        System.arraycopy(mBuffer, mBufferHasReadSize, buffer, offset, copySize);

        mBufferHasReadSize += copySize;

        return copySize;
    }

    public boolean initFirstSection() {
        if (mFirstSection) {
            int dataSize;
            try {
                dataSize = readDataFromFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return false;
            }

            if (dataSize < 0) {
                EvLog.e("init first section failed:hasRead=" + mHasReadFileSize
                        + ",available size=" + mFile.length());
                return false;
            } else if (dataSize == 0) {
                EvLog.i("init first section,file is not encrypt ");
                return false;
            } else {
                EvLog.i("init first section,file is encrypt,dataSize:" + dataSize);
//                mIsFileEncrypt = true;
            }

            mFirstSectionSize = dataSize;
            mFirstSection = false;
        }
        return true;
    }

    private int skipBuffer(int size) {
        if (mBufferValidSize <= 0) {
            return 0;
        }

        if (size == mBufferValidSize) {
            mBufferValidSize = 0;
            return size;
        }

        int skip = size;

        if (skip > mBufferValidSize) {
            skip = mBufferValidSize;
        }

        mBufferValidSize = mBufferValidSize - skip;

        if (mBufferValidSize > 0) {
            System.arraycopy(mBuffer, skip, mBuffer, 0,
                    mBufferValidSize);
            // Log.d("qiangv", "copy buffer");
        }

        return skip;
    }

    private int readDataFromFile() throws IOException {
        long length = mFile.length();
        long available = length - mHasReadFileSize;

        if (available < 0) {
            EvLog.e(TAG, "local file length not enough, len:" + length + ",hasRead:" + mHasReadFileSize);
            return -1;
        } else if (available == 0) {
            return 0;
        }

        boolean needRead = false;
        if (mTotalLengthOfNetWork > 0) {
            needRead = (mTotalLengthOfNetWork - mHasReadFileSize) > BUFFERSIZE;
        } else {
            needRead = (length - mHasReadFileSize) > BUFFERSIZE;
        }

        if (available < BUFFERSIZE && needRead) {
            return 0;
        }

        long size = BUFFERSIZE;
        if (!needRead) {
            size = length - mHasReadFileSize;
        }

        if (size > BUFFERSIZE) {
            size = BUFFERSIZE;
        }

        int result = super.read(mBuffer, 0, (int) size);

        if (result == -1) {
            EvLog.e(TAG, "read error:" + result);
            return -1;
        }

        if (needRead && result != BUFFERSIZE) {
            return 0;
        }

        mHasReadFileSize += result;

        if (needRead) {
            int dataSize = mBuffer.length;
            if (dataSize < 0) {
                EvLog.e(KmHttpdServer.TAG, "read data failed:" + dataSize);
                return -1;
            } else if (dataSize == 0) {
                mBufferValidSize = result;
            } else {
                mBufferValidSize = dataSize;
            }
        } else {
            mBufferValidSize = result;
        }
        return mBufferValidSize;
    }
}

