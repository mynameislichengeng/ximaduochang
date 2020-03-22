/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2016年12月27日     hemm     1.0        [修订说明]
 *
 */

package com.awirtz.util;

/**
 * [功能说明]
 */
/**
 * An interface describing the callback write method.  This is used by
 * {@link RingBuffer#writer(RingBufferWriter, int)}
 * to allow more efficient pipelining.
 * 
 * @author Aaron Meriwether
 */
public interface RingBufferWriter {
    
    /**
     * A callback method which {@link RingBuffer} will invoke.
     * This method will be invoked zero, one, or two times as dictated by
     * the RingBuffer logic in order to fulfill the
     * {@link RingBuffer#writer(RingBufferWriter, int)} request.
     * 
     * @param byteArray The internal byte array of the RingBuffer object which
     * needs to be written to.
     * @param offset The offset in the byte array at which to begin writing.
     * @param length The number of bytes to write.
     */
    public void write(byte[] byteArray, int offset, int length);
}
