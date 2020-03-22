/*
 * SocketHandler.java
 *
 * Created on den 25 oktober 2004, 14:23
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

import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import com.evideo.kmbox.util.EvLog;


/**
 *
 * @author  Anders Hedstrom (grymse@alhem.net)
 */
public class SocketHandler
{
    
    /** Creates a new instance of SocketHandler */
    public SocketHandler()
    {
        this(null);
    } // SocketHandler
    
    public SocketHandler(StdLog log)
    {
        m_log = log;
        try
        {
            m_selector = Selector.open();
        }
        catch (java.io.IOException e)
        {
            LogError(null, "SocketHandler", 0, e.toString(), SocketHandler.LOG_LEVEL_FATAL);
        }
        m_sockets = new Vector<Socket>();
        //
        System.out.println("m_log is " + ((m_log == null) ? "NULL" : "OK"));
        System.out.println("m_selector is " + ((m_selector == null) ? "NULL" : "OK"));
    } // SocketHandler
    
    public void LogError(Socket s,String usertxt,int errcode,String errtxt,int loglevel)
    {
        if (m_log != null)
        {
            m_log.error(this, s, usertxt, errcode, errtxt, loglevel);
        }
    } // LogError
    
    public void Select(long secs,long millisecs)
    {
        // See if we've had any activity -- either
        // an incoming connection, or incoming data on an
        // existing connection
        try
        {
            int num = m_selector.select(secs * 1000 + millisecs);
            
            // If we don't have any activity, loop around and wait
            // again
            if (num == 0)
            {
                return;
            }
            
            // Get the keys corresponding to the activity
            // that has been detected, and process them
            // one by one
            Set keys = m_selector.selectedKeys();
            EvLog.d("Get the keys size: " + keys.size());
            Iterator it = keys.iterator();

            while (it.hasNext())
            {
                // Get a key representing one of bits of I/O
                // activity
                SelectionKey key = (SelectionKey)it.next();
                
                // What kind of activity is it?
                if ((key.readyOps() & SelectionKey.OP_READ) == SelectionKey.OP_READ)
                {
                    SocketChannel ch = (SocketChannel)key.channel();
                    java.net.Socket ss = (java.net.Socket)ch.socket();
                    Socket s = (Socket)key.attachment();
                    if (s != null)
                    {
                        System.out.println(s + ": OnRead");
                        s.OnRead();
                        if (s.LineProtocol())
                        {
                            s.ReadLine(); // eat ibuf to m_line, calls OnLine
                        }
                    }
                }
                if ((key.readyOps() & SelectionKey.OP_WRITE) == SelectionKey.OP_WRITE)
                {
                    SocketChannel ch = (SocketChannel)key.channel();
                    java.net.Socket ss = (java.net.Socket)ch.socket();
                    Socket s = (Socket)key.attachment();
                    if (s != null)
                    {
                        System.out.println(s + ": OnWrite");
                        s.OnWrite();
                    }
                }
                if ((key.readyOps() & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT)
                {
                    ServerSocketChannel ch = (ServerSocketChannel)key.channel();
                    java.net.ServerSocket ss = (java.net.ServerSocket)ch.socket();
                    Socket s = (Socket)key.attachment();
                    if (s != null)
                    {
                        System.out.println(s + ": OnRead(ACCEPT)");
                        s.OnRead(); // ListenSocket.OnRead will call OnAccept on new Socket
                    }
                }
                if ((key.readyOps() & SelectionKey.OP_CONNECT) == SelectionKey.OP_CONNECT)
                {
                    SocketChannel ch = (SocketChannel)key.channel();
                    java.net.Socket ss = (java.net.Socket)ch.socket();
                    Socket s = (Socket)key.attachment();
                    if (s != null)
                    {
                        System.out.println(s + ": OnConnect");
                        ch.finishConnect();
                        s.SetConnecting(false);
                        s.GetKey().interestOps(SelectionKey.OP_READ);
                        s.OnConnect();
                    }
                }
                
            } // while
            keys.clear();
            
            // deregister
            it = m_selector.keys().iterator();
            boolean bRemoved = false;
            while (it.hasNext())
            {
                // Get a key representing one of bits of I/O
                // activity
                SelectionKey key = (SelectionKey)it.next();
                Socket p = (Socket)key.attachment();
                if (p.CloseAndDelete())
                {
                    p.OnDelete(); // OnDelete closes Channel
                    key.cancel();
                    m_sockets.remove(p); // no longer Valid
                    bRemoved = true;
                }
            } // while - check for delete
            if (bRemoved)
            {
                PrintSockets();
            }

        } catch ( java.io.IOException e)
        {
            LogError(null, "Select", 0, e.toString(), SocketHandler.LOG_LEVEL_ERROR);
        }
    } // Select
    
    public void Add(Socket x)
    {
    	if (m_sockets.contains(x))
    	{
    		return ;
    	}
    	
        SelectableChannel ch = x.GetChannel();
        try
        {
            SelectionKey key = ch.register( m_selector, ch.validOps(), x);
            x.SetKey(key);
            x.OnInitialOps();
            m_sockets.add(x);
            PrintSockets();
        } catch (Exception e)
        {
            LogError(x, "Add", 0, e.toString(), SocketHandler.LOG_LEVEL_ERROR);
        }
    } // Add
    
    public Socket getLast()
    {
    	return m_sockets.lastElement();
    }
    
    public Vector<Socket> getAll()
    {
    	return m_sockets;
    }

    public boolean Valid(Socket x)
    {
        return m_sockets.contains(x);
    } // Valid

    public void PrintSockets()
    {
        System.out.println(getClass().getSimpleName() + ": Current socket list");
        for (int i = 0; i < m_sockets.size(); i++)
        {
            Socket x = m_sockets.get(i);
            System.out.println(x);
        }
    } // PrintSockets
    
    // "defines"
    public static final int LOG_LEVEL_INFO = 0;
    public static final int LOG_LEVEL_WARNING = 1;
    public static final int LOG_LEVEL_ERROR = 2;
    public static final int LOG_LEVEL_FATAL = 3;
    
    //
    private Selector m_selector;
    private StdLog m_log;
    private Vector<Socket> m_sockets;
}
