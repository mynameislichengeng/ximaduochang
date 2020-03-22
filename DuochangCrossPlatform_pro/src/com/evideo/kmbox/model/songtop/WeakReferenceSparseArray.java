/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date        Author      Version     Description
 *  -----------------------------------------------
 *  2015-3-30     "zhouxinghua"     1.0         [修订说明]
 *
 */

package com.evideo.kmbox.model.songtop;

import java.lang.ref.WeakReference;

import android.util.SparseArray;

/**
 * [功能说明]弱引用的稀疏数组
 * @param <E> 保存数据类型
 */
public class WeakReferenceSparseArray<E> extends SparseArray<E> {
    
    private SparseArray<WeakReference<E>> mSparseArray;
    
    public WeakReferenceSparseArray() {
        mSparseArray = new SparseArray<WeakReference<E>>();
    }
    
    public WeakReferenceSparseArray(int initialCapacity) {
        mSparseArray = new SparseArray<WeakReference<E>>(initialCapacity);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void put(int key, E value) {
        WeakReference<E> weakReference = new WeakReference<E>(value);
        mSparseArray.put(key, weakReference);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public E get(int key) {
        WeakReference<E> weakReference = mSparseArray.get(key);
        if (weakReference != null) {
            return weakReference.get();
        } else {
            return null;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public E get(int key, E valueIfKeyNotFound) {
        WeakReference<E> weakReference = mSparseArray.get(key);
        if (weakReference != null) {
            return weakReference.get();
        } else {
            return valueIfKeyNotFound;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(int key) {
        mSparseArray.delete(key);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(int key) {
        mSparseArray.remove(key);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return mSparseArray.size();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int keyAt(int index) {
        return mSparseArray.keyAt(index);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public E valueAt(int index) {
        WeakReference<E> weakReference = mSparseArray.valueAt(index);
        if (weakReference != null) {
            return weakReference.get();
        } else {
            return null;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setValueAt(int index, E value) {
        WeakReference<E> weakReference = new WeakReference<E>(value);
        mSparseArray.setValueAt(index, weakReference);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int indexOfKey(int key) {
        return mSparseArray.indexOfKey(key);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int indexOfValue(E value) {
        int size = mSparseArray.size();
        for (int i = 0; i < size; i++) {
            WeakReference<E> weakReference = mSparseArray.valueAt(i);
            if (weakReference != null && weakReference.get() == value) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        mSparseArray.clear();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void append(int key, E value) {
        WeakReference<E> weakReference = new WeakReference<E>(value);
        mSparseArray.append(key, weakReference);
    }
    
}
