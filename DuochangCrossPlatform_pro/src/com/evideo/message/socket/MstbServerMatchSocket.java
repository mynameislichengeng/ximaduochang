package com.evideo.message.socket;

import java.nio.ByteBuffer;

import net.alhem.jsockets.SocketHandler;
import net.alhem.jsockets.TcpSocket;

import com.evideo.kmbox.model.kmproxy.XMLMessage;
import com.evideo.kmbox.model.kmproxy.data.MessageStream;
import com.evideo.kmbox.util.EvLog;

public class MstbServerMatchSocket extends TcpSocket
{
	public enum ClientSourceType
	{
		ClientType_eUnKnown,
		ClientType_eSTB,
		ClientType_eMobile
	};

	private MessageStream mMsgStream = new MessageStream();
	private MstbServerSocket mServerSocket;
	private static int sClientCount = 0;
	private int mClientID = 0;
	

	public MstbServerMatchSocket(SocketHandler h) {
		super(h);
		//SetLineProtocol();
		// TODO Auto-generated constructor stub
	}
	
	public MstbServerMatchSocket(SocketHandler h,int ilen,int olen){
		super(h, ilen, olen);
	}

	@Override
	public MstbServerMatchSocket Create()
	{
		MstbServerMatchSocket socket = new MstbServerMatchSocket(Handler(), 
				MessageStream.sDefaultBufferSize, MessageStream.sDefaultBufferSize);
		socket.setClientID(generateClientID()); 
		return socket; //
	} // Create

	public int getClientID()
	{
		return mClientID;
	}

	private void setClientID(int id)
	{
		mClientID = id;
	}

	private int generateClientID()
	{
		return sClientCount++;
	}
	
	private byte[] TrimByte(byte[] temp){

		try {
			if(temp != null){
				int startpos = 0;
				int endpos   = temp.length - 1;
				for(int i = 0; i < temp.length; i++){
					if(endpos >= startpos){
						if(temp[startpos] == (byte)0){
							startpos++;
						}
						if(temp[endpos] == (byte)0){
							endpos--;
						}
						if(temp[startpos] != (byte)0 && temp[endpos] != 0){
							break;
						}
					}else{
						break;
					}
				}
				if(endpos >= startpos){
					byte[] trimbyte = new byte[endpos - startpos + 1];
					for(int j = 0; j <= (endpos - startpos); j++){
						trimbyte[j] = temp[startpos + j];
					}				
					return trimbyte;
				}
			}
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return null;
	}

	public void sendBuffer(ByteBuffer buffer,int l)
	{
		ByteBuffer bu = buffer;
		byte[] temp = TrimByte(bu.array());
		//EvLog.d("kservice_send", TypeConvert.getString(temp));
		SendBuf(temp, 0);
	}

	//    public void sendXMLMessage(XMLMessage msg)
	//    {
	//    	ByteBuffer bufferMsg = packXMLMessageBuffer(msg);
	//    	
	//    	SendBuf(bufferMsg.array(), 0);
	//    }


	
	@Override
	public void OnAccept()
	{
		//Send("Welcome\r\n");
//		EvLog.d("onAccept", "welcom");
	}

	@Override
	public void OnRawData(ByteBuffer b,int len)
	{
		XMLMessage xmlMsg = new XMLMessage();
		mMsgStream.In(b, len);
		while (mMsgStream.Out(xmlMsg))
		{
			xmlMsg.setClientID(this.getClientID());
			this.getServerSocket().OnXMLMsg(xmlMsg);
		}
	} // OnRawData

	public void setServerSocket(MstbServerSocket ss)
	{
		mServerSocket = ss;
	}

	public final MstbServerSocket getServerSocket()
	{
		return mServerSocket;
	}

	@Override
	public void OnLine(String line)
	{
		Send("You said: " + line + "\r\n");
		if (line.equals("quit"))
		{
			Send("Goodbye!\r\n");
			SetCloseAndDelete();
		}
	}
}
