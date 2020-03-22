package com.evideo.kmbox.presenter;

import java.util.ArrayList;
import java.util.List;

import com.evideo.kmbox.BaseApplication;
import com.evideo.kmbox.thread.TerminableThreadPool;
import com.evideo.kmbox.util.EvLog;

/**
 * [分页数据加载模块]
 * @param <T> 数据类型
 */
public abstract class PageLoadPresenter<T> extends BasePresenter {
    
    /** [一页的大小] */
    private int mPageSize;
    
    /** [加载页码] */
    private int mLoadPage;
    /** [起始页] */
    private int mStartPage;
    /** [最后页] */
    private int mLastPage;
    /** [总页数，根据mPageSize和mTotalNum计算得出] */
    private int mPageNum = -1;
    /** [数据总数] */
    private int mTotalNum;
    
    private final List<T> mDatas = new ArrayList<T>();
    
    /** [是否在加载数据中，请勿在子线程中修改该变量的值] */
    private boolean isLoading = false;
    
    private TerminableThreadPool mTerminableThreadPool;
    
    private IPageLoadCallback<T> mPageLoadCallback;
    
    private ILoadCacheDataCallback<T> mLoadCacheDataCallback;
    
    private boolean isStop = false;
    
    public PageLoadPresenter(int pageSize, IPageLoadCallback<T> pageLoadCallback) {
        mPageSize = pageSize;
        mPageLoadCallback = pageLoadCallback;
        init();
    }
    
    public void setLoadCacheDataCallback(ILoadCacheDataCallback<T> loadCacheDataCallback) {
        mLoadCacheDataCallback = loadCacheDataCallback;
    }
    
    private void init() {
        mLoadPage = 1;
        mStartPage = mLoadPage;
        mLastPage = mLoadPage;
        isLoading = false;
    }
    
    public int getLoadPage() {
        return mLoadPage;
    }
    
    public int getPageSize() {
        return mPageSize;
    }
    
    public void setStartPage(int startPage) {
        mStartPage = startPage;
    }
    
    public void setLastPage(int lastPage) {
        mLastPage = lastPage;
    }
    
    public void setTotalNum(int totalNum) {
        if (totalNum > 0) {
            mTotalNum = totalNum;
            mPageNum = (int) Math.ceil(((double)totalNum) / mPageSize);
        }
    }
    
    public int getTotalNum() {
        return mTotalNum;
    }
    
    public List<T> getDatas() {
        return mDatas;
    }
    
    /**
     * [首次加载数据时调用]
     */
    public void loadData() {
        mLoadPage = 1;
        mDatas.clear();
        loadData(true, true);
    }
    
    /**
     * [加载下一页数据]
     */
    public void loadNextPage() {
        if (isLoading || isStop) {
            return;
        }
        if (mLastPage < mPageNum) {
            mLoadPage = mLastPage + 1;
            loadData(false, true);
        }
    }
    
    /**
     * [加载上一页数据]
     */
    public void loadPrePage() {
        if (isLoading || isStop) {
            return;
        }
        if (mStartPage > 1) {
            mLoadPage = mStartPage - 1;
            loadData(false, false);
        }
    }
    
    /**
     * [加载指定页（跳页）]
     * @param page 指定页
     */
    public void loadPage(int page) {
        if (isLoading || isStop) {
            return;
        }
        if (page > mPageNum) {
            return;
        }
        mLoadPage = page;
        loadData(true, false);
        
    }
    
    private void loadData(final boolean isReset, final boolean isNext) {
        if(isLoading || isStop) {
            return;
        } else {
            isLoading = true;
        }
        Runnable runnable = new Runnable() {
            boolean isSuccess = false;
            Exception exception = null;
            @Override
            public void run() {
                runInUI(BaseApplication.getHandler(),new Runnable() {
                    @Override
                    public void run() {
                        performPreLoadData(isReset, isNext);
                    }
                });
                
                final List<T> tempDatas = new ArrayList<T>();
                PageDataInfo<T> dataInfo = null;
                try {
                    dataInfo = getData(mLoadPage, mPageSize);
                } catch (Exception e) {
                    exception = e;
                }
                if(dataInfo != null && dataInfo.datas != null) {
                    isSuccess = true;
                    
                    if (mPageNum == -1) {
                        setTotalNum(dataInfo.totalNum);
                    }
                    
                    tempDatas.addAll(dataInfo.datas);
//                    EvLog.d("loadDatas size: " + tempDatas.size());
                }
                
                runInUI(BaseApplication.getHandler(),new Runnable() {
                    @Override
                    public void run() {
                        if (!isLoading || isStop) {
                            return;
                        }
                        if (isSuccess) {
                            
                            // 第一次加载或者跳页
                            if (isReset) {
                                mDatas.clear();
                                mStartPage = mLastPage = mLoadPage;
                            }
                            
                            if (isNext) {
                                mLastPage = mLoadPage;
                                mDatas.addAll(tempDatas);
                            } else {
                                mStartPage = mLoadPage;
                                mDatas.addAll(0, tempDatas);
                            }
                        }
                        EvLog.d("mDatas size: " + mDatas.size());
                        if (!isStop) {
                            performPostLoadData(exception, isReset, isNext, tempDatas);
                        }
                        isLoading = false;
                    }
                });
            }
        };
        
        mTerminableThreadPool = new TerminableThreadPool(runnable);
        mTerminableThreadPool.start();
        
    }
    
    public void loadCacheData() {
        if (isLoading || isStop) {
            return;
        } else {
            isLoading = true;
        }
        Runnable runnable = new Runnable() {
            boolean isSuccess = false;
            Exception exception = null;
            @Override
            public void run() {
                runInUI(BaseApplication.getHandler(),new Runnable() {
                    @Override
                    public void run() {
                        if (mLoadCacheDataCallback != null) {
                            mLoadCacheDataCallback.onPreLoadCacheData();
                        }
                    }
                });
                
                final List<T> tempDatas = new ArrayList<T>();
                PageDataInfo<T> dataInfo = null;
                
                try {
                    dataInfo = getCacheData();
                } catch (Exception e) {
                    exception = e;
                }
                
                if(dataInfo != null && dataInfo.datas != null) {
                    isSuccess = true;
                    setTotalNum(dataInfo.totalNum);
                    tempDatas.addAll(dataInfo.datas);
//                    EvLog.d("loadDatas size: " + tempDatas.size());
                }
                
                runInUI(BaseApplication.getHandler(),new Runnable() {
                    @Override
                    public void run() {
                        if (!isLoading || isStop) {
                            return;
                        }
                        if (isSuccess) {
                            mDatas.clear();
                            mDatas.addAll(tempDatas);
                            // 目前只有缓存从头开始的数据
                            // TODO 后续若要缓存跳页的数据，此部分逻辑要修改
                            mStartPage = 1;
                            mLoadPage = mLastPage = (int) Math.ceil(((double)tempDatas.size()) / mPageSize);
                        }
                        if (!isStop && mLoadCacheDataCallback != null) {
                            mLoadCacheDataCallback.onPostLoadCacheData(exception, tempDatas);
                        }
                        isLoading = false;
                    }
                });
            }
        };
        
        mTerminableThreadPool = new TerminableThreadPool(runnable);
        mTerminableThreadPool.start();
    }
    
    public void stopTask() {
        isStop = true;
        cancelLoadTask();
    }
    
    public void cancelLoadTask() {
        if(mTerminableThreadPool != null) {
            mTerminableThreadPool.cancel();
        }
    }
    
    public boolean isCancel() {
        if(mTerminableThreadPool == null) {
            return false;
        }
        return mTerminableThreadPool.isCancel();
    }
    
    protected PageDataInfo<T> getCacheData() throws Exception {
        return null;
    }
    
    public abstract PageDataInfo<T> getData(int loadPage, int pageSize) throws Exception;
    
    protected void performPreLoadData(boolean isReset, boolean isNext, Object... requestParams) {
//        EvLog.d("performPreLoadData isReset: " + isReset + " isNext: " + isNext);
        if(mPageLoadCallback != null) {
            mPageLoadCallback.onPreLoadData(isReset, isNext);
        }
    }
    
    protected void performPostLoadData(Exception e, boolean isReset, 
            boolean isNext, List<T> loadDatas) {
//        EvLog.d("performPostLoadData isReset: " + isReset + " isNext: " + isNext);
        if(mPageLoadCallback != null) {
            mPageLoadCallback.onPostLoadData(e, isReset, isNext, loadDatas);
        }
    }
    
    public interface ILoadCacheDataCallback<T> {
        
        public void onPreLoadCacheData();
        
        public void onPostLoadCacheData(Exception e, List<T> datas);
    }
    
    public interface IPageLoadCallback<T> {
        
        public void onPreLoadData(boolean isReset, boolean isNext);
        
        public void onPostLoadData(Exception e, boolean isReset, 
                boolean isNext, List<T> datas);
    }

}
