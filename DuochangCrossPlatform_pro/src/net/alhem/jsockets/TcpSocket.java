/*
 * TcpSocket.java
 *
 * Created on den 27 oktober 2004, 09:05
 */
/*
Copyright (C) 2004  Anders Hedstrom
 
This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.
 
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.
 
You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package net.alhem.jsockets;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * Buffered socket i/o
 * @author  Anders Hedstrom (grymse@alhem.net)
 */
public class TcpSocket extends Socket
{
    
    /** Creates a new instance of TcpSocket */
    public TcpSocket(SocketHandler h,int ilen,int olen)
    {
        super(h);
        m_ilen = ilen;
        m_ibuf = ByteBuffer.allocate(ilen).order(BYTEORDER);
        m_olen = olen;
        m_obuf = ByteBuffer.allocate(olen).order(BYTEORDER);
        m_line = new StringBuffer(4096);
    } // TcpSocket
    public TcpSocket(SocketHandler h)
    {
        this(h, 16384, 30000);
    }
    @Override
	public Socket Create()
    {
        return new TcpSocket(Handler(), m_ilen, m_olen);
    } // Create
    @Override
	public void OnInitialOps()
    {
        if (Connecting())
        {
            GetKey().interestOps(SelectionKey.OP_CONNECT);
        }
        else
        {
            GetKey().interestOps(SelectionKey.OP_READ);
        }
    } // OnInitialOps
    
    public void Open(String host,int port)
    {
        InetSocketAddress isa = new InetSocketAddress(host, port);
        try
        {
            SocketChannel sc = SocketChannel.open();
            //        SocketChannel sc = (SocketChannel)GetChannel();
            sc.configureBlocking(false);
            if (!sc.connect(isa))
            {
                SetConnecting();
            }
            else
            {
                // connection immediately ok
            }
            attach(sc);
        }
        catch (java.io.IOException e)
        {
            Handler().LogError(this, "Open", 0, e.toString(), SocketHandler.LOG_LEVEL_FATAL);
            SetCloseAndDelete();
        }
    } // Open
    
    @Override
	public void OnRead()
    {
        m_ibuf.clear();
        try
        {
            SocketChannel sc = (SocketChannel)GetChannel();
            int n = sc.read(m_ibuf);
            m_ibuf.flip();
            if (n == -1)
            {
                Handler().LogError(this, "OnRead", 0, "error while reading", SocketHandler.LOG_LEVEL_FATAL);
                SetCloseAndDelete();
            }
            else
                if (n == 0)
                {
                }
                else
                    if (n > 0 && n == m_ibuf.limit() )
                    {
                        OnRawData(m_ibuf, n);
                    }
                    else
                    {
                        Handler().LogError(this, "OnRead", 0, "ibuf.limit != n (buffer cleared)", SocketHandler.LOG_LEVEL_ERROR);
                        m_ibuf.clear();
                    }
        }
        catch (Exception e)
        {
            Handler().LogError(this, "OnRead", 0, e.toString(), SocketHandler.LOG_LEVEL_FATAL);
            SetCloseAndDelete();
        }
    } // OnRead
    
    @Override
	public void OnDelete()
    {
        SocketChannel sc = (SocketChannel)GetChannel();
        try
        {
            sc.close();
        }
        catch (IOException e)
        {
            Handler().LogError(this, "OnDelete", 0, e.toString(), SocketHandler.LOG_LEVEL_ERROR);
        }
    } // OnDelete
    
    @Override
	public void ReadLine()
    {
        for (int i = 0; i < m_ibuf.limit(); ++i)
        {
            byte b = m_ibuf.get( i );
            String aChar = new Character((char)b).toString();
            switch (b)
            {
                case 13: // ignore CR
                    break;
                case 10: // LF
                    OnLine( m_line.toString() );
                    m_line.delete(0, m_line.length());
                    break;
                default:
                    m_line.append(aChar);
            }
        } // while
    }
    
    public void Send(String str)
    {
        byte[] bbuf = str.getBytes();
        SendBuf(bbuf, bbuf.length);
    } // Send
    
    public void SendBuf(byte[] bbuf,int l)
    {
        int n = 0; // assume obuf empty
        m_obuf.clear(); // make assumption valid and break buffered output
        m_obuf.put(bbuf);
        if (n == 0)
        {
            OnWrite();
        }
    } // SendBuf
    
    @Override
	public void OnWrite()
    {
        m_obuf.flip();
        try
        {
            SocketChannel sc = (SocketChannel)GetChannel();
            int n = sc.write(m_obuf);
            System.out.println(n + " bytes written");
        }
        catch (Exception e)
        {
            Handler().LogError(this, "OnWrite", 0, e.toString(), SocketHandler.LOG_LEVEL_FATAL);
            SetCloseAndDelete();
        }
    } // OnWrite
    
    //
    private int m_ilen;
    private int m_olen;
    protected ByteBuffer m_ibuf;
    protected ByteBuffer m_obuf;
    protected StringBuffer m_line;
    protected static final ByteOrder BYTEORDER = ByteOrder.LITTLE_ENDIAN;
}
