/*
 * ListenSocket.java
 *
 * Created on den 25 oktober 2004, 14:21
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
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;



/**
 *
 * @author  Anders Hedstrom (grymse@alhem.net)
 */
public class ListenSocket extends Socket
{
    
    /** Creates a new instance of ListenSocket */
    public ListenSocket(SocketHandler h,Socket creator)
    {
        super(h);
        m_creator = creator;
    } //
    @Override
	public Socket Create()
    {
        return new ListenSocket(Handler(), m_creator); //
    } // Create
    @Override
	public void OnInitialOps()
    {
        GetKey().interestOps(SelectionKey.OP_ACCEPT);
    } // OnInitialOps
    
    public int Bind(int port)
    {
        // Instead of creating a ServerSocket,
        // create a ServerSocketChannel
        try
        {
            ServerSocketChannel ssc = ServerSocketChannel.open();
            
            // Set it to non-blocking, so we can use select
            ssc.configureBlocking( false );
            
            // Get the Socket connected to this channel, and bind it
            // to the listening port
            ServerSocket ss = ssc.socket();
            InetSocketAddress isa = new InetSocketAddress( port );
            ss.bind( isa );
            
            attach(ssc);
            return 0;
        } catch (java.io.IOException e)
        {
            Handler().LogError(this, "Bind", 0, e.toString(), SocketHandler.LOG_LEVEL_ERROR);
        }
        return -1;
    } // Bind
    
    @Override
	public void OnRead()
    {
        try
        {
            ServerSocketChannel ssc = (ServerSocketChannel)GetChannel();
            java.net.ServerSocket ss = (java.net.ServerSocket)ssc.socket();
            // It's an incoming connection.
            // Register this socket with the Selector
            // so we can listen for input on it
            try
            {
                java.net.Socket js = ss.accept();
                System.out.println( "Got connection from " + js);
                
                // Make sure to make it non-blocking, so we can
                // use a selector on it.
                SocketChannel sc = js.getChannel();
                sc.configureBlocking( false );
                
                // Register it with the selector, for reading
                //        sc.register( selector, SelectionKey.OP_READ );
                
                Socket s = m_creator.Create();
                System.out.println("New Socket object: " + s.toString() );
                s.attach(sc);
                Handler().Add(s);
                s.OnAccept();
            }
            catch (Exception e)
            {
                Handler().LogError(this, "OnRead", 0, e.toString(), SocketHandler.LOG_LEVEL_ERROR);
            }
        } catch (java.lang.NullPointerException e)
        {
            Handler().LogError(this, "OnRead", 0, e.toString(), SocketHandler.LOG_LEVEL_ERROR);
        }
    } // OnRead

    @Override
	public void OnDelete()
    {
        ServerSocketChannel sc = (ServerSocketChannel)GetChannel();
        try
        {
            if (sc != null) {
                sc.close();
                sc = null;
            }
        }
        catch (IOException e)
        {
            Handler().LogError(this, "OnDelete", 0, e.toString(), SocketHandler.LOG_LEVEL_ERROR);
        }
    } // OnDelete
    
    //
    protected Socket m_creator;
}
