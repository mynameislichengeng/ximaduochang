package com.evideo.kmbox.model.player;


import android.view.SurfaceHolder;

/**
 * \brief 媒体播放类
 */
public interface IKmPlayer {

    void openSoftDecode();

    void closeSoftDecodeMode();

    int getPlayerType();

    /**
     * \brief      销毁解码器
     * \return    0 成功,其他失败
     */
    int destroy();

    int setDisplay(SurfaceHolder holder);

    /**
     * \brief      设置播放源
     * \param     pszPath 播放源路径
     * \return    0 成功,其他失败
     */
    int setSource(String url);

    int setAudioTrackInfo(int org, int acc);

    /**
     * brief  原伴唱切换
     * return 操作成功或失败
     */
    boolean setAudioSingMode(int mode);

    /**
     * brief  获取原伴唱状态
     * return 原伴唱状态
     */
    int getAudioSingMode();

    /**
     * \brief      获取播放源
     * \param     strName 播放源路径
     * \return    0 成功,其他失败
     */
    String getSource();

    /**
     * \brief      设置事件处理接口类
     * \param     eventHandles 事件处理接口类
     * \return    0 成功,其他失败
     */
    int setListener(IKmPlayerEvent eventHandles);

    /**
     * \brief      播放
     * \return    0 成功,其他失败
     */
    int play();

    /**
     * \brief      暂停
     * \return    0 成功,其他失败
     */
    int pause();

    /**
     * \brief      停止
     * \return    0 成功,其他失败
     */
    int stop();

    /**
     * @return 当前播放时间
     * @brief 获取当前播放位置
     */
    int getCurrentPosition();

    /**
     * \brief      获取当前解码器状态
     * \return    当前解码器状态
     */
    int getState();

    /**
     * \brief      获取总时长(单位:毫秒)
     * \param     pTime 视频播放时间
     * \return    0 成功,其他失败
     */
    int getTotalTime();

    /**
     * \brief      跳转
     * \param     t 跳转位置
     * \return    0 成功,其他失败
     */
    int seekToTime(long time);

    /**
     * [功能说明] 设置音量
     *
     * @param vol 音量值
     * @return
     */
    int setVolume(float vol);

    /**
     * [功能说明] 获取音量
     *
     * @return 音量值
     */
    float getVolume();

    /**
     * [功能说明] 获取音频的播放时间
     *
     * @return
     */
    int getAudioTime();
}
