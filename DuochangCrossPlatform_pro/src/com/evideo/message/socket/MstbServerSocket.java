package com.evideo.message.socket;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import net.alhem.jsockets.ListenSocket;
import net.alhem.jsockets.Socket;
import net.alhem.jsockets.SocketHandler;
import com.evideo.kmbox.model.kmproxy.XMLMessage;
import com.evideo.kmbox.model.kmproxy.XMLMessageHandler;
import com.evideo.kmbox.util.EvLog;

public class MstbServerSocket extends ListenSocket
{
	public MstbServerSocket(SocketHandler h, Socket creator) {
		super(h, creator);
		// TODO Auto-generated constructor stub
		
	}
	
	public void broadcastMessage(XMLMessage msg)
	{
		ByteBuffer bufferMsg = msg.getMessage();
		
		Vector<Socket> vec = Handler().getAll();
		MstbServerMatchSocket socket;
		for (int i = 1; i < vec.size(); i++)
		{
			socket = (MstbServerMatchSocket)vec.get(i);
			socket.sendBuffer(bufferMsg, 0);
		}
	}
	
	public void sendMessageToClient(XMLMessage msg, MstbServerMatchSocket toSocket)
	{
		ByteBuffer bufferMsg = msg.getMessage();
		
    	if (toSocket != null)
    	{
    		toSocket.sendBuffer(bufferMsg, 0);
    	}
	}
	
    public void sendMessageToClient(XMLMessage msg)
    {
    	ByteBuffer bufferMsg = msg.getMessage();

    	Vector<Socket> vec = Handler().getAll();
    	MstbServerMatchSocket socket;
    	EvLog.d("sendMessageToClient", "vec.size():" + vec.size());
    	for (int i = 1; i < vec.size(); i++)
    	{
    		socket = (MstbServerMatchSocket)vec.get(i);
    		if (socket.getClientID() == msg.getClientID())
    		{
    			EvLog.d("sendMessageToClient", "msg.getClientID():" + msg.getClientID());
    			socket.sendBuffer(bufferMsg, 0);
    		}
    	}
    }
    
    public void postMessage(XMLMessage msg)
    {
    	OnXMLMsg(msg);
    }
    
	public void OnXMLMsg(XMLMessage xmlMsg)
	{
		EvLog.d("KmService OnXMLMsg", "" + xmlMsg.getMessage());

		//调用回调
		final int count = mCallbacks.size();
		for (int i=0; i < count; i++) {
		    if (mCallbacks.get(i) != null) {
		        mCallbacks.get(i).handleMessage(xmlMsg);
		    }
		}
//		mCallbacks.finishBroadcast();
	}

    @Override
	public void OnRead()
    {
    	super.OnRead();
    	MstbServerMatchSocket sms = (MstbServerMatchSocket)Handler().getLast();
    	sms.setServerSocket(this);
    } // OnRead
    

    
    public void addMessageHandler( XMLMessageHandler handler)
    {
    	if(handler != null){
    		mCallbacks.add(handler);
    	}
    }
    
    public void removeMessageHandler( XMLMessageHandler handler)
    {
    	if(handler != null){
    		mCallbacks.remove(handler);
    	}
    }
    
	private final List<XMLMessageHandler> mCallbacks= new ArrayList<XMLMessageHandler>();
}
