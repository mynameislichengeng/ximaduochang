package com.evideo.kmbox.model.playerctrl;

import java.util.List;

import com.evideo.kmbox.model.songinfo.KmPlayListItem;

public class KmPlayerCtrlType {
   
    
    /**
     * @brief : [解码器监听]
     */
    public interface IPlayerManagerListener {
        /**
         * \brief 状态变化通知
         * \param state 状态，参见状态机切换
         * \param param 参数
         */
        public void onStateChange(int state,  Object param );
        
        /**
         * [功能说明] 视频缓存变化通知
         * @param percent 变化百分比
         */
        public void onBufferingChange(int percent);
        
        /**
         * [功能说明] 更新下载状态
         */
       /* public void onDowningChange(int percent,float rate);*/
        public void updateDownState();
        
        /**
         * [功能说明] 更新下载百分比
         * @param percent 百分比
         * @param rate 下载速率
         */
        public void updateDownPercent(int percent,float rate);
        
        /**
         * [功能说明] 数据准备完毕，可以开始播放
         */
        public void onDataReady(int serialNum);
    };
    
    public interface IPlayerCtrlListener {
        
        /**
         * \brief 状态变化通知
         * \param state 状态，参见状态机切换
         * \param song  在播歌曲
         * \param addon 操作来源【机顶盒、手机等】
         */
        public void onStateChange(int state, Object param);
        
        /**
         * [功能说明] 视频播放缓冲通知
         * @param percent 百分比
         */
        public void onBufferingChange(int percent);
        
        /**
         * [功能说明] 视频资源下载变化通知
         * @param percent 百分比
         * @param speed 速度
         */
        public void onDowningPercentChange(int percent,float speed);
        
        /**
         * [功能说明] 工作模式切换
         */
        public void onWorkModeChange();
    };
    
    public interface IPlayList
    {
        /**
         * \brief  单增歌曲
         * \param  song 歌曲
         * \return 操作是否成功
         */
        public boolean addItem( KmPlayListItem song,boolean isAllowAlreadyExist);

        /**
         * \brief  删除歌曲
         * \param  id 歌曲列表流水号
         * \return 操作是否成功
         */
        public boolean delItem(int id);

        /**
         * \brief  更新歌曲
         * \param  song 歌曲
         * \return 操作是否成功
         */
        public boolean updateItem(KmPlayListItem song);

        /**
         * \brief  插入歌曲
         * \param  song 歌曲
         * \param  pos  插入位置
         * \return 操作是否成功
         * \note   【逻辑】在重复点歌情况下,如果歌曲已存在,则为Move\n
                    插入情况下,如果位置不存在,则在列表尾部插入
         */
//        public boolean insertItem(KmPlayListItem song, int pos);
        
        public boolean topItem(int index);

        /**
         * \brief  清空歌曲
         * \return 操作是否成功
         */
        public boolean clearList();

        /**
         * \brief  重置歌曲
         * \param  songlist 歌曲列表
         * \return 操作是否成功
         */
        public boolean resetData(List<KmPlayListItem> songlist);

        /**
         * \brief  获取歌曲列表
         */
        public List<KmPlayListItem> getData();

        /**
         * \brief  获取歌曲数目
         * \return 歌曲数目
         */
        public int getCount();

        /**
         * \brief  判断歌曲列表流水号是否存在
         * \param  id 歌曲列表流水号
         * \return 存在则返回歌曲列表位置,<0失败
         */
        public int getPos(int serialNum);
        
        public int getPosBySongId(int songId);
        
        public int getSerialNum(int songid);
        /**
         * \brief  根据歌曲列表流水号查找歌曲
         * \param  id 歌曲列表流水号
         * \param  song 输出歌曲(接口外面创建)
         * \return true 成功, false 失败
         */
        public KmPlayListItem getItemBySerialNum(int serialNum);
        /**
         * \brief  根据列表位置查找歌曲
         * \param  pos 列表位置
         * \param  song 输出歌曲(接口外面创建)
         * \return true 成功, false 失败
         */
        public KmPlayListItem getItemByPos(int pos);
        
//        public boolean isExistInList(int songID);
    };
    
    public interface PlayCtrlMsg {
        public static int MSG_UNKNOW = -1;
        /** 开始播放  */
        public static int MSG_START = 1;
        /** 恢复 */
        public static int MSG_PLAY = 3;
        /** 暂停 */
        public static int MSG_PAUSE = 5;
        /** 停止 */
        public static int MSG_STOP = 7;
        /** 原伴唱  */
        public static int MSG_AUDIO_SING_MODE = 8;
    };
    
    
    /** 播控工作模式 */
    public interface PlayMode{
        /** 正常工作模式 */
        public static final int MODE_NORMAL = 0;
        /** 重唱作模式 */
        public static final int MODE_REPLAY = 1;
        /** 回放模式 */
        public static final int MODE_PLAYBACK = 2;
    }
    
    public interface PlayDataState{
        //歌曲下载
        public static final int STATE_LOADING = 100;
        //歌曲准备完毕
        public static final int STATE_READY = 101;
        //歌曲缓冲阶段
        public static final int STATE_BUFFER = 102;
        //歌曲数据本地已完整存在
        public static final int STATE_COMPLETE = 103;
        
        public static final int STATE_NONE = 104;
        
        /** [数据出错] */
        public static final int STATE_ERROR = 105;
    }
    
    
    /** 播放器状态枚举 */
    public interface PlayerCtrlState
    {
        /** 空闲 */
        public static final int  STATE_IDLE = 0;
        /** 解码停止 */
        public static final int  STATE_STOP = 1;
        /** 出错 */
        public static final int  STATE_ERROR = 2;
        /** 【运行态】自动停止 */
        public static final int  STATE_AUTOSTOP = 3;
        /** 【运行态】解码准备中 */
        public static final int  STATE_PREPARING = 4;
        public static final int  STATE_PREPARED = 5;
        /** 【运行态】播放 */
        public static final int  STATE_PLAY = 6;
        /** 【运行态】暂停 */
        public static final int  STATE_PAUSE = 7;
        /** 【运行态】缓冲中 */
        public static final int  STATE_BUFFERING = 8;
    };
    
    public static class PlayErrorInfo {
        public int errorType;
        public int errorCode;
        public String errorMessage;
        public PlayErrorInfo(int errType,int errCode,String errMsg) {
            this.errorType = errType;
            this.errorCode = errCode;
            this.errorMessage = errMsg;
        }
        public PlayErrorInfo() {
            errorCode = 0;
            errorMessage = "";
        }
    }
}
