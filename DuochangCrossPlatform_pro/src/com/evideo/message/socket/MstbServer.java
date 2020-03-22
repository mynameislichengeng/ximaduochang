package com.evideo.message.socket;

import net.alhem.jsockets.SocketHandler;
import net.alhem.jsockets.StdLog;
import net.alhem.jsockets.StdoutLog;

import com.evideo.kmbox.model.kmproxy.XMLMessageHandler;
import com.evideo.kmbox.model.kmproxy.data.MessageStream;
import com.evideo.kmbox.util.EvLog;


public class MstbServer 
{
	private MyThread mThread;
	private SocketHandler m_sh;
	private MstbServerSocket mServerSocket;

	public MstbServer() {
//	    EvLog.i("MstbServer create---------------");
	}
	
	public MstbServerSocket getSocket() {
	    return mServerSocket;
	}
	
	public void init(int port)
	{
		StdLog log = new StdoutLog();
		m_sh = new SocketHandler(log);
		mServerSocket = new MstbServerSocket(m_sh, new MstbServerMatchSocket(m_sh ,
				MessageStream.sDefaultBufferSize, MessageStream.sDefaultBufferSize));
		if (mServerSocket.Bind(port) == 0) // listen on port 8000
		{
			m_sh.Add(mServerSocket);
		} else {
		    EvLog.e("mServerSocket bind failed : " + port);
		}
	}
	
	public void unInit()
	{
	    if (mServerSocket != null) {
	        mServerSocket.OnDelete();
	        mServerSocket = null;
	    }
	}
	
	private class MyThread extends Thread {
		private static final String TAG = "MstbServerCommu thread";
		private boolean mRunning = true;
		
		
		public MyThread(String threadName) {
			super(threadName);
			// TODO 自动生成的构造函数存根
		}

		@Override
		public void run() {
			while (mRunning) // forever
			{
				m_sh.Select(1, 0);
			}
			EvLog.i(TAG, "MstbServer thread finished");
		}
	}
	public boolean start(int port, String thredthreadName)
	{
	    EvLog.i(thredthreadName + " start bind port:" + port);
		init(port);
		mThread = new MyThread(thredthreadName);
		mThread.start();
		return false;
	}
	
	
	public void stop()
	{
		mThread.mRunning = false;
		unInit();
	}
	
	/*public void postMessage(final XMLMessage msg)
	{
		mhandler.sendMessage(mhandler.obtainMessage(0, msg));
		
	}
	
	public void broadcastMessage(final XMLMessage msg)
	{
		mhandler.sendMessage(mhandler.obtainMessage(1, msg));
	}
	
	public void sendMessageToClient(final XMLMessage msg)
	{
		mhandler.sendMessage(mhandler.obtainMessage(2, msg));
	}*/
	
	public void addMessageHandler( XMLMessageHandler handler)
	{
		mServerSocket.addMessageHandler( handler);
	}
	
	public void removeMessageHandler( XMLMessageHandler handler)
	{
		mServerSocket.removeMessageHandler( handler);
	}
	
	
	/*public static boolean isActive()
	{
		return false;		
	}*/
}
