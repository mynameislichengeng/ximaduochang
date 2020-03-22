/*
 * Socket.java
 *
 * Created on den 25 oktober 2004, 14:36
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

import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;

/**
 *
 * @author  Anders Hedstrom (grymse@alhem.net)
 */
public abstract class Socket
{
    
    /** Creates a new instance of Socket */
    public Socket(SocketHandler h)
    {
        m_handler = h;
    } // Socket
    public abstract Socket Create();
    public abstract void OnInitialOps();

    public void OnRead()
    {
        Handler().LogError(this, "OnRead", 0, "not implemented", SocketHandler.LOG_LEVEL_INFO);
    } // OnRead
    public void OnWrite()
    {
        Handler().LogError(this, "OnWrite", 0, "not implemented", SocketHandler.LOG_LEVEL_INFO);
    } // OnWrite
    public void OnConnect()
    {
        Handler().LogError(this, "OnConnect", 0, "not implemented", SocketHandler.LOG_LEVEL_INFO);
    } // OnConnect
    public void OnAccept()
    {
        Handler().LogError(this, "OnAccept", 0, "not implemented", SocketHandler.LOG_LEVEL_INFO);
    } // OnAccept
    public void OnDelete()
    {
    } // OnDelete
    public void ReadLine()
    {
    } // ReadLine
    public void OnLine(String line)
    {
    } // OnLine
    public void OnRawData(ByteBuffer b,int len)
    {
    } // OnRawData

    // 
    public SocketHandler Handler()
    {
        return m_handler;
    } // Handler
    
    @Override
	public String toString()
    {
        return "This is a " + getClass().getSimpleName();
    } // toString

    /** Channel for this Socket */
    public void attach(SelectableChannel x)
    {
        m_ch = x;
    } // attach
    public SelectableChannel GetChannel()
    {
        return m_ch;
    } // GetChannel

    /** Selector SelectionKey */
    public void SetKey(SelectionKey key)
    {
        m_key = key;
    }
    public SelectionKey GetKey()
    {
        return m_key;
    }

    /** Close and delete flag */
    public void SetCloseAndDelete()
    {
        m_close_and_delete = true;
    }
    public boolean CloseAndDelete()
    {
        return m_close_and_delete;
    }
    
    /** Line protocol flag */
    public void SetLineProtocol()
    {
        m_line_protocol = true;
    }
    public boolean LineProtocol()
    {
        return m_line_protocol;
    }
    public void SetLineProtocol(boolean x)
    {
        m_line_protocol = x;
    }

    /** Connecting flag */
    public void SetConnecting()
    {
        m_connecting = true;
    }
    public void SetConnecting(boolean x)
    {
        m_connecting = x;
    }
    public boolean Connecting()
    {
        return m_connecting;
    }
    
    //
    private SocketHandler m_handler;
    private SelectableChannel m_ch;
    private SelectionKey m_key; // key in Selector
    private boolean m_close_and_delete = false;
    private boolean m_line_protocol = false;
    private boolean m_connecting = false;
}
