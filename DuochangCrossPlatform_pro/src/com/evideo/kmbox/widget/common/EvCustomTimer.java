package com.evideo.kmbox.widget.common;

import android.os.Handler;

/**
 * 定时器
 */
public class EvCustomTimer {
	public static final int DEFAULT_TIMER_TIMEOUT = 60;//S

	private Handler mHandler = null;
	private int PERIOD_MS = 1000;//MS
	private int TIMEOUT_S = DEFAULT_TIMER_TIMEOUT;//S
	private IOnEventListener mOnTimerListener = null;
	private int mTimeOut = TIMEOUT_S;

	public EvCustomTimer() {
		mHandler = new Handler();
	}
	
	public void setPeriodTime(int periodSecond) {
		this.PERIOD_MS = periodSecond;
	}

	private Runnable mRunnable = new Runnable() {
		@Override
		public void run() {
			//要做的事�?
			if (mTimeOut <= 0) {
				if (mOnTimerListener != null) {
					mOnTimerListener.onEvent();
				}
				mTimeOut = TIMEOUT_S;
			}
			mTimeOut--;
			mHandler.postDelayed(this, PERIOD_MS);
		}
	};
	/**
	 * 启动定时器，使用默认超时时间(60s)，每60s执行1次listener
	 * @param listener 监听器
	 */
	public void start(IOnEventListener listener) {
		start(listener, DEFAULT_TIMER_TIMEOUT);
	}
	/**
	 * 启动定时器，每隔timeoutSeconds(单位为s)执行1次listener
	 * @param listener:为空时不启动
	 * @param timeoutSeconds：小于1时使用默认的60s
	 */
	public void start(IOnEventListener listener, int timeoutSeconds){
		if (listener == null) {
			return;
		}
		mOnTimerListener = listener;
		TIMEOUT_S = timeoutSeconds;
		if (TIMEOUT_S <= 1) {
			TIMEOUT_S = DEFAULT_TIMER_TIMEOUT;
		}
		mTimeOut = TIMEOUT_S;
		mHandler.postDelayed(mRunnable, PERIOD_MS);//每一秒执行一次runnable.
	}
	/**
	 * 重置定时器，将重新开始算超时时间
	 */
	public void reset() {
		mTimeOut = TIMEOUT_S;
	}
	/**
	 * 停止定时器
	 */
	public void stop() {
		mHandler.removeCallbacks(mRunnable);
	}
	
	/**
	 * [定时器事件]
	 */
	public interface IOnEventListener {
		/**
		 * [事件触发时动作]
		 * @param arg 参数
		 */
		public void onEvent(Object... arg);
	}
}
