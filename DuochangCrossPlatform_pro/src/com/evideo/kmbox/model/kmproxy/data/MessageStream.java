package com.evideo.kmbox.model.kmproxy.data;

import com.evideo.kmbox.model.kmproxy.XMLMessage;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;


public class MessageStream {
	public static final int sDefaultBufferSize = 4096;
	private byte mRawBuffer[] = new byte[sDefaultBufferSize];
	ByteBuffer mContentBuffer = ByteBuffer.wrap(mRawBuffer).order(BYTEORDER);
	protected static final ByteOrder BYTEORDER = ByteOrder.LITTLE_ENDIAN;

	public MessageStream()
	{
		mContentBuffer.clear();
	}

    public void In(ByteBuffer rawData, int size) {
        try {
            if (mContentBuffer.remaining() < (rawData.remaining())) {
                byte[] temp = null;
                if (mContentBuffer.position() != 0) {
                    mContentBuffer.flip();
                    temp = new byte[rawData.remaining() + mContentBuffer.remaining()];
                    mContentBuffer.get(temp, 0, mContentBuffer.remaining());
                    rawData.get(temp, mContentBuffer.remaining()+1, rawData.remaining());
                } else {
                    temp = new byte[rawData.remaining()];
                    rawData.get(temp, 0, rawData.remaining());
                }
                mContentBuffer = ByteBuffer.wrap(temp).order(BYTEORDER);
            } else {
                mContentBuffer.put(rawData);                
            }
        } catch (Exception e) {
            e.printStackTrace();
            mContentBuffer.clear();
        }
    }

	public boolean Out(XMLMessage message)
	{
		int size = -1;
        mContentBuffer.flip();
        if (mContentBuffer.remaining() == 0) {
            mContentBuffer.clear();
            return false;
        }
        
        try {
            for (int i = mContentBuffer.position(); i < mContentBuffer.limit(); i++) {
                if(mContentBuffer.get(i) == (byte)'K' && mContentBuffer.get(i+1) == (byte)'M'){
                    if(mContentBuffer.limit() - i >= 8){
                        mContentBuffer.position(i);
                        mContentBuffer.mark();
                        if(mContentBuffer.get() == (byte)'K' && mContentBuffer.get() == (byte)'M'){
                            size = mContentBuffer.getInt() + 8; 
                            if(size < 0 || size > mContentBuffer.remaining() + 6){
                                mContentBuffer.reset();
                                mContentBuffer.compact();
                                return false;
                            }else{
                                mContentBuffer.reset(); 
                                break;
                            }
                        }else{
                            mContentBuffer.reset();
                            mContentBuffer.compact();
                            return false;
                        }                           
                    }
                } else {
                    size = -1;
                }
            }
            
            if(size >= 0){
            	byte[] temp = new byte[size];
            	mContentBuffer.get(temp, 0, size);
            	ByteBuffer bu = ByteBuffer.wrap(temp).order(BYTEORDER);
            	message.setMessage(bu);
            	mContentBuffer.compact();
            	return true;
            } else {
                mContentBuffer.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
            mContentBuffer.clear();
        }	
        return false;
	}

}
