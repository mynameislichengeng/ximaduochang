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
//https://github.com/p120ph37/java-ring-buffer/blob/master/src/com/awirtz/util/RingBufferReader.java
/**
 * An interface describing the callback read method.  This is used by
 * {@link RingBuffer#reader(RingBufferReader, int)}
 * to allow more efficient pipelining.
 * 
 * @author Aaron Meriwether
 */
public interface RingBufferReader {
    
    /**
     * A callback method which {@link RingBuffer} will invoke.
     * This method will be invoked zero, one, or two times as dictated by
     * the RingBuffer logic in order to fulfill the
     * {@link RingBuffer#reader(RingBufferReader, int)} request.
     * 
     * @param byteArray The internal byte array of the RingBuffer object which
     * needs to be read from.
     * @param offset The offset in the byte array at which to begin reading.
     * @param length The number of bytes to read.
     */
    public void read(byte[] byteArray, int offset, int length);
}
