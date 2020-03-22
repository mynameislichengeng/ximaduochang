/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015-10-27     "zhanxingshan"     1.0        [修订说明]
 *
 */

package com.evideo.kmbox.model.tftpserver;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.SocketTimeoutException;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.commons.net.io.FromNetASCIIOutputStream;
import org.apache.commons.net.io.ToNetASCIIInputStream;
import org.apache.commons.net.tftp.TFTP;
import org.apache.commons.net.tftp.TFTPAckPacket;
import org.apache.commons.net.tftp.TFTPDataPacket;
import org.apache.commons.net.tftp.TFTPErrorPacket;
import org.apache.commons.net.tftp.TFTPPacket;
import org.apache.commons.net.tftp.TFTPPacketException;
import org.apache.commons.net.tftp.TFTPReadRequestPacket;
import org.apache.commons.net.tftp.TFTPWriteRequestPacket;

import com.evideo.kmbox.util.EvLog;

/**
 * [TftpServer服务]
 */
public class TftpServer implements Runnable {

    private static final String TAG = "TftpServer";
    /** [服务默认端口] */
    private static final int DEFAULT_TFTP_PORT = 69;
    /**
     * [TFTP服务类型]
     */
//    public static enum ServerMode { GET_ONLY, PUT_ONLY, GET_AND_PUT; }
    public static final int GET_ONLY = 0;
    public static final int PUT_ONLY = 1;
    public static final int GET_AND_PUT = 2;
    
    private HashSet<TFTPTransfer> mTransfers = new HashSet();
    /** [关闭服务标志位] */
    private volatile boolean mShutdownServer = false;
    /** [声明一个TFTP服务] */
    private TFTP mServerTftp;
    /** [TFTP服务读文件目录] */
    private File mServerReadDirectory;
    /** [TFTP服务写文件目录] */
    private File mServerWriteDirectory;
    /** [TFTP服务端口] */
    private int mPort;
    /** [声明一个Exception] */
    private Exception mServerException = null;
    /** [TFTP服务类型] */
    private int mMode;
    private static final PrintStream mNullStream = new PrintStream(
            new OutputStream() {
                @Override
                public void write(int b){}
                @Override
                public void write(byte[] b) throws IOException {}
            }
            );
    private PrintStream mLog;
    private PrintStream mLogError;
    private int mMaxTimeoutRetries = 3;
    /** [Socket超时时间] */
    private int mSocketTimeout;
    /** [声明一个线程] */
    private Thread mServerThread;


    /**
     * Start a TFTP Server on the default port (69). Gets and Puts occur in the specified
     * directories.
     *
     * The server will start in another thread, allowing this constructor to return immediately.
     *
     * If a get or a put comes in with a relative path that tries to get outside of the
     * serverDirectory, then the get or put will be denied.
     *
     * GET_ONLY mode only allows gets, PUT_ONLY mode only allows puts, and GET_AND_PUT allows both.
     * Modes are defined as int constants in this class.
     *
     * @param serverReadDirectory directory for GET requests
     * @param serverWriteDirectory directory for PUT requests
     * @param mode A value as specified above.
     * @throws IOException if the server directory is invalid or does not exist.
     */
    public TftpServer(File serverReadDirectory, File serverWriteDirectory, int mode)
            throws IOException {
        this(serverReadDirectory, serverWriteDirectory, DEFAULT_TFTP_PORT, mode, null, null);
    }

    /**
     * Start a TFTP Server on the specified port. Gets and Puts occur in the specified directory.
     *
     * The server will start in another thread, allowing this constructor to return immediately.
     *
     * If a get or a put comes in with a relative path that tries to get outside of the
     * serverDirectory, then the get or put will be denied.
     *
     * GET_ONLY mode only allows gets, PUT_ONLY mode only allows puts, and GET_AND_PUT allows both.
     * Modes are defined as int constants in this class.
     *
     * @param serverReadDirectory directory for GET requests
     * @param serverWriteDirectory directory for PUT requests
     * @param mode A value as specified above.
     * @param log Stream to write log message to. If not provided, uses System.out
     * @param errorLog Stream to write error messages to. If not provided, uses System.err.
     * @throws IOException if the server directory is invalid or does not exist.
     */
    public TftpServer(File serverReadDirectory, File serverWriteDirectory, int port, int mode,
            PrintStream log, PrintStream errorLog) throws IOException {
        mPort = port;
        mMode = mode;
        mLog = log == null ? mNullStream : log;
        mLogError = errorLog == null ? mNullStream : errorLog;
        launch(serverReadDirectory, serverWriteDirectory);
    }

    /**
     * Set the max number of retries in response to a timeout. Default 3. Min 0.
     *
     * @param retries
     */
    public void setMaxTimeoutRetries(int retries) {
        if (retries < 0) {
            throw new RuntimeException("Invalid Value");
        }
        mMaxTimeoutRetries = retries;
    }

    /**
     * Get the current value for maxTimeoutRetries
     */
    public int getMaxTimeoutRetries() {
        return mMaxTimeoutRetries;
    }

    /**
     * Set the socket timeout in milliseconds used in transfers. Defaults to the value here:
     * http://commons.apache.org/net/apidocs/org/apache/commons/net/tftp/TFTP.html#DEFAULT_TIMEOUT
     * (5000 at the time I write this) Min value of 10.
     */
    public void setSocketTimeout(int timeout) {
        if (timeout < 10) {
            throw new RuntimeException("Invalid Value");
        }
        mSocketTimeout = timeout;
    }

    /**
     * The current socket timeout used during transfers in milliseconds.
     */
    public int getSocketTimeout() {
        return mSocketTimeout;
    }

    /*
     * start the server, throw an error if it can't start.
     */
    private void launch(File serverReadDirectory, File serverWriteDirectory) throws IOException  {
        mLog.println("Starting TFTP Server on port " + mPort + ".  Read directory: "
                + serverReadDirectory + " Write directory: " + serverWriteDirectory
                + " Server Mode is " + mMode);
        mServerReadDirectory = serverReadDirectory.getCanonicalFile();
    	EvLog.d(TAG,"mServerReadDirectory-->" + mServerReadDirectory);
    	if (!mServerReadDirectory.exists() || !serverReadDirectory.isDirectory()) {
            throw new IOException("The server read directory " + mServerReadDirectory
                    + " does not exist");
        }
        mServerWriteDirectory = serverWriteDirectory.getCanonicalFile();
    	EvLog.d(TAG,"mServerReadDirectory-->" + mServerReadDirectory);

        if (!mServerWriteDirectory.exists() || !serverWriteDirectory.isDirectory()) {
            throw new IOException("The server write directory " + mServerWriteDirectory
                    + " does not exist");
        }

        mServerTftp = new TFTP();
        mSocketTimeout = mServerTftp.getDefaultTimeout();
        mServerTftp.setDefaultTimeout(0);
        mServerTftp.open(mPort);
        mServerThread = new Thread(this);
        mServerThread.setDaemon(true);
        mServerThread.start();
    }

    @Override
    protected void finalize() throws Throwable {
        shutdown();
    }

    /**
     * check if the server thread is still running.
     *
     * @return true if running, false if stopped.
     * @throws Exception throws the exception that stopped the server if the server is stopped from
     *             an exception.
     */
    public boolean isRunning() throws Exception {
        if (mShutdownServer && mServerException != null) {
            throw mServerException;
        }
        return !mShutdownServer;
    }

    public void run() {
        try {
            while (!mShutdownServer) {
                TFTPPacket tftpPacket;

                tftpPacket = mServerTftp.receive();

                TFTPTransfer tt = new TFTPTransfer(tftpPacket);
                synchronized(mTransfers) {
                    mTransfers.add(tt);
                }

                Thread thread = new Thread(tt);
                thread.setDaemon(true);
                thread.start();
            }
        } catch (Exception e) {
            if (!mShutdownServer) {
                mServerException = e;
                mLogError.println("Unexpected Error in TFTP Server - Server shut down! + " + e);
            }
        } finally {
            mShutdownServer = true; 
            if (mServerTftp != null && mServerTftp.isOpen()) {
                mServerTftp.close();
            }
        }
    }

    /**
     * Stop the tftp server (and any currently running transfers) and release all opened network
     * resources.
     */
    public void shutdown() {
        mShutdownServer = true;
        synchronized(mTransfers) {
            Iterator<TFTPTransfer> it = mTransfers.iterator();
            while (it.hasNext()) {
                it.next().shutdown();
            }
        } 
        
        try {
            mServerTftp.close();
        } catch (RuntimeException e) {
            // noop
        }
        
        if (mServerThread != null) {
//            try {
            mServerThread.interrupt();
            mServerThread = null;
           /* } catch (InterruptedException e) {
                // we've done the best we could, return
            }*/
        }
    }

    /*
     * An instance of an ongoing transfer.
     */
    private class TFTPTransfer implements Runnable {
        private TFTPPacket tftpPacket_;

        private boolean shutdownTransfer = false;

        TFTP transferTftp_ = null;

        public TFTPTransfer(TFTPPacket tftpPacket) {
            tftpPacket_ = tftpPacket;
        }

        public void shutdown() {
            shutdownTransfer = true;
            try  {
                transferTftp_.close();
            } catch (RuntimeException e)  {
                // noop
            }
        }

        public void run() {
            try {
                transferTftp_ = new TFTP();

                transferTftp_.beginBufferedOps();
                transferTftp_.setDefaultTimeout(mSocketTimeout);

                transferTftp_.open();

                if (tftpPacket_ instanceof TFTPReadRequestPacket) {
                    handleRead(((TFTPReadRequestPacket) tftpPacket_));
                }  else if (tftpPacket_ instanceof TFTPWriteRequestPacket) {
                    handleWrite((TFTPWriteRequestPacket) tftpPacket_);
                } else {
                    mLog.println("Unsupported TFTP request (" + tftpPacket_ + ") - ignored.");
                }
            } catch (Exception e) {
                if (!shutdownTransfer) {
                    mLogError
                            .println("Unexpected Error in during TFTP file transfer.  Transfer aborted. "
                                    + e);
                }
            } finally {
                try {
                    if (transferTftp_ != null && transferTftp_.isOpen()) {
                        transferTftp_.endBufferedOps();
                        transferTftp_.close();
                    }
                } catch (Exception e) {
                    // noop
                }
                synchronized(mTransfers) {
                    mTransfers.remove(this);
                }
            }
        }

        /*
         * Handle a tftp read request.
         */
        private void handleRead(TFTPReadRequestPacket trrp) throws IOException, TFTPPacketException {
            InputStream is = null;
            try {
                if (mMode == PUT_ONLY) {
                    transferTftp_.bufferedSend(new TFTPErrorPacket(trrp.getAddress(), trrp
                            .getPort(), TFTPErrorPacket.ILLEGAL_OPERATION,
                            "Read not allowed by server."));
                    return;
                }

                try {
               
                    is = new BufferedInputStream(new FileInputStream(buildSafeFile(
                            mServerReadDirectory, trrp.getFilename(), false)));
                } catch (FileNotFoundException e) {
                
                    transferTftp_.bufferedSend(new TFTPErrorPacket(trrp.getAddress(), trrp
                            .getPort(), TFTPErrorPacket.FILE_NOT_FOUND, e.getMessage()));
                    return;
                }  catch (Exception e) {
               
                    transferTftp_.bufferedSend(new TFTPErrorPacket(trrp.getAddress(), trrp
                            .getPort(), TFTPErrorPacket.UNDEFINED, e.getMessage()));
                    return;
                }

                if (trrp.getMode() == TFTP.NETASCII_MODE) {
              
                    is = new ToNetASCIIInputStream(is);
                }

                byte[] temp = new byte[TFTPDataPacket.MAX_DATA_LENGTH];

                TFTPPacket answer;

                int block = 1;
                boolean sendNext = true;

                int readLength = TFTPDataPacket.MAX_DATA_LENGTH;

                TFTPDataPacket lastSentData = null;

                // We are reading a file, so when we read less than the
                // requested bytes, we know that we are at the end of the file.
                while (readLength == TFTPDataPacket.MAX_DATA_LENGTH && !shutdownTransfer) {
               
                    if (sendNext) {
                   
                        readLength = is.read(temp);
                        if (readLength == -1) {
                       
                            readLength = 0;
                        }

                        lastSentData = new TFTPDataPacket(trrp.getAddress(), trrp.getPort(), block,
                                temp, 0, readLength);
                        transferTftp_.bufferedSend(lastSentData);
                    }

                    answer = null;

                    int timeoutCount = 0;

                    while (!shutdownTransfer
                            && (answer == null || !answer.getAddress().equals(trrp.getAddress()) || answer
                                    .getPort() != trrp.getPort())) {
                    
                        // listen for an answer.
                        if (answer != null) {
                       
                            // The answer that we got didn't come from the
                            // expected source, fire back an error, and continue
                            // listening.
                            mLog.println("TFTP Server ignoring message from unexpected source.");
                            transferTftp_.bufferedSend(new TFTPErrorPacket(answer.getAddress(),
                                    answer.getPort(), TFTPErrorPacket.UNKNOWN_TID,
                                    "Unexpected Host or Port"));
                        }
                        try  {
                      
                            answer = transferTftp_.bufferedReceive();
                        } catch (SocketTimeoutException e) {
                        
                            if (timeoutCount >= mMaxTimeoutRetries) {
                           
                                throw e;
                            }
                            // didn't get an ack for this data. need to resend
                            // it.
                            timeoutCount++;
                            transferTftp_.bufferedSend(lastSentData);
                            continue;
                        }
                    }

                    if (answer == null || !(answer instanceof TFTPAckPacket)) {
                   
                        if (!shutdownTransfer) {
                       
                            mLogError
                                    .println("Unexpected response from tftp client during transfer ("
                                            + answer + ").  Transfer aborted.");
                        }
                        break;
                    } else {
                    
                        // once we get here, we know we have an answer packet
                        // from the correct host.
                        TFTPAckPacket ack = (TFTPAckPacket) answer;
                        if (ack.getBlockNumber() != block) {
                        
                            /*
                             * The origional tftp spec would have called on us to resend the
                             * previous data here, however, that causes the SAS Syndrome.
                             * http://www.faqs.org/rfcs/rfc1123.html section 4.2.3.1 The modified
                             * spec says that we ignore a duplicate ack. If the packet was really
                             * lost, we will time out on receive, and resend the previous data at
                             * that point.
                             */
                            sendNext = false;
                        } else {
                        
                            // send the next block
                            block++;
                            if (block > 65535) {
                                // wrap the block number
                                block = 0;
                            }
                            sendNext = true;
                        }
                    }
                }
            } finally {
            
                try {
                    if (is != null) {
                        is.close();
                    }
                }  catch (IOException e) {
                    // noop
                }
            }
        }

        /*
         * handle a tftp write request.
         */
        private void handleWrite(TFTPWriteRequestPacket twrp) throws IOException,
                TFTPPacketException {
       
            OutputStream bos = null;
            try {
           
                if (mMode == GET_ONLY) {
                
                    transferTftp_.bufferedSend(new TFTPErrorPacket(twrp.getAddress(), twrp
                            .getPort(), TFTPErrorPacket.ILLEGAL_OPERATION,
                            "Write not allowed by server."));
                    return;
                }

                int lastBlock = 0;
                String fileName = twrp.getFilename();

                try {
               
                    File temp = buildSafeFile(mServerWriteDirectory, fileName, true);
                    if (temp.exists()) {
                   
                        transferTftp_.bufferedSend(new TFTPErrorPacket(twrp.getAddress(), twrp
                                .getPort(), TFTPErrorPacket.FILE_EXISTS, "File already exists"));
                        return;
                    }
                    bos = new BufferedOutputStream(new FileOutputStream(temp));

                    if (twrp.getMode() == TFTP.NETASCII_MODE) {
                   
                        bos = new FromNetASCIIOutputStream(bos);
                    }
                } catch (Exception e) {
                
                    transferTftp_.bufferedSend(new TFTPErrorPacket(twrp.getAddress(), twrp
                            .getPort(), TFTPErrorPacket.UNDEFINED, e.getMessage()));
                    return;
                }

                TFTPAckPacket lastSentAck = new TFTPAckPacket(twrp.getAddress(), twrp.getPort(), 0);
                transferTftp_.bufferedSend(lastSentAck);

                while (true) {
               
                    // get the response - ensure it is from the right place.
                    TFTPPacket dataPacket = null;

                    int timeoutCount = 0;

                    while (!shutdownTransfer
                            && (dataPacket == null
                                    || !dataPacket.getAddress().equals(twrp.getAddress()) || dataPacket
                                    .getPort() != twrp.getPort())) {
                  
                        // listen for an answer.
                        if (dataPacket != null) {
                       
                            // The data that we got didn't come from the
                            // expected source, fire back an error, and continue
                            // listening.
                            mLog.println("TFTP Server ignoring message from unexpected source.");
                            transferTftp_.bufferedSend(new TFTPErrorPacket(dataPacket.getAddress(),
                                    dataPacket.getPort(), TFTPErrorPacket.UNKNOWN_TID,
                                    "Unexpected Host or Port"));
                        } try  {
                       
                            dataPacket = transferTftp_.bufferedReceive();
                        } catch (SocketTimeoutException e)  {
                        
                            if (timeoutCount >= mMaxTimeoutRetries) {
                                throw e;
                            }
                            // It didn't get our ack. Resend it.
                            transferTftp_.bufferedSend(lastSentAck);
                            timeoutCount++;
                            continue;
                        }
                    }

                    if (dataPacket != null && dataPacket instanceof TFTPWriteRequestPacket) {
                   
                        // it must have missed our initial ack. Send another.
                        lastSentAck = new TFTPAckPacket(twrp.getAddress(), twrp.getPort(), 0);
                        transferTftp_.bufferedSend(lastSentAck);
                    } else if (dataPacket == null || !(dataPacket instanceof TFTPDataPacket)) {
                   
                        if (!shutdownTransfer) {
                       
                            mLogError
                                    .println("Unexpected response from tftp client during transfer ("
                                            + dataPacket + ").  Transfer aborted.");
                        }
                        break;
                    } else {
                   
                        int block = ((TFTPDataPacket) dataPacket).getBlockNumber();
                        byte[] data = ((TFTPDataPacket) dataPacket).getData();
                        int dataLength = ((TFTPDataPacket) dataPacket).getDataLength();
                        int dataOffset = ((TFTPDataPacket) dataPacket).getDataOffset();

                        if (block > lastBlock || (lastBlock == 65535 && block == 0)) {
                       
                            // it might resend a data block if it missed our ack
                            // - don't rewrite the block.
                            bos.write(data, dataOffset, dataLength);
                            lastBlock = block;
                        }

                        lastSentAck = new TFTPAckPacket(twrp.getAddress(), twrp.getPort(), block);
                        transferTftp_.bufferedSend(lastSentAck);
                        if (dataLength < TFTPDataPacket.MAX_DATA_LENGTH) {
                        
                            // end of stream signal - The tranfer is complete.
                            bos.close();

                            // But my ack may be lost - so listen to see if I
                            // need to resend the ack.
                            for (int i = 0; i < mMaxTimeoutRetries; i++) {
                                try {
                               
                                    dataPacket = transferTftp_.bufferedReceive();
                                } catch (SocketTimeoutException e) {
                               
                                    // this is the expected route - the client
                                    // shouldn't be sending any more packets.
                                    break;
                                }

                                if (dataPacket != null
                                        && (!dataPacket.getAddress().equals(twrp.getAddress()) || dataPacket
                                                .getPort() != twrp.getPort())) {
                               
                                    // make sure it was from the right client...
                                    transferTftp_
                                            .bufferedSend(new TFTPErrorPacket(dataPacket
                                                    .getAddress(), dataPacket.getPort(),
                                                    TFTPErrorPacket.UNKNOWN_TID,
                                                    "Unexpected Host or Port"));
                                } else {
                               
                                    // This means they sent us the last
                                    // datapacket again, must have missed our
                                    // ack. resend it.
                                    transferTftp_.bufferedSend(lastSentAck);
                                }
                            }

                            // all done.
                            break;
                        }
                    }
                }
            } finally {
                if (bos != null)  {
                    bos.close();
                }
            }
        }

        /*
         * Utility method to make sure that paths provided by tftp clients do not get outside of the
         * serverRoot directory.
         */
        private File buildSafeFile(File serverDirectory, String fileName, boolean createSubDirs)
                throws IOException {
       
            File temp = new File(serverDirectory, fileName);
            temp = temp.getCanonicalFile();

            if (!isSubdirectoryOf(serverDirectory, temp)) {
            
                throw new IOException("Cannot access files outside of tftp server root.");
            }

            // ensure directory exists (if requested)
            if (createSubDirs) {
           
                createDirectory(temp.getParentFile());
            }

            return temp;
        }

        /*
         * recursively create subdirectories
         */
        private void createDirectory(File file) throws IOException {
       
            File parent = file.getParentFile();
            if (parent == null) {
           
                throw new IOException("Unexpected error creating requested directory");
            }
            if (!parent.exists()) {
           
                // recurse...
                createDirectory(parent);
            }

            if (parent.isDirectory()) {
           
                if (file.isDirectory()) {
               
                    return;
                }
                boolean result = file.mkdir();
                if (!result) {
               
                    throw new IOException("Couldn't create requested directory");
                }
            } else {
            
                throw new IOException(
                        "Invalid directory path - file in the way of requested folder");
            }
        }

        /*
         * recursively check to see if one directory is a parent of another.
         */
        private boolean isSubdirectoryOf(File parent, File child) {
       
            File childsParent = child.getParentFile();
            if (childsParent == null) {
           
                return false;
            }
            if (childsParent.equals(parent)) {
           
                return true;
            } else {
           
                return isSubdirectoryOf(parent, childsParent);
            }
        }
    }

    /**
     * Set the stream object to log debug / informational messages. By default, this is a no-op
     *
     * @param log
     */
    public void setLog(PrintStream log) {
        this.mLog = log;
    }

    /**
     * Set the stream object to log error messsages. By default, this is a no-op
     *
     * @param logError
     */
    public void setLogError(PrintStream logError) {
        this.mLogError = logError;
    }
}  
 

