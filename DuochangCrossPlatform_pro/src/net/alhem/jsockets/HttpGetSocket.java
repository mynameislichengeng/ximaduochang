/*
 * HttpGetSocket.java
 *
 * Created on den 28 oktober 2004, 16:01
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




/**
 *
 * @author  Anders Hedstrom (grymse@alhem.net)
 */
public class HttpGetSocket extends HTTPSocket
{
    
    /** Creates a new instance of HttpGetSocket */
    public HttpGetSocket(SocketHandler h)
    {
        super(h);
    }
    @Override
	public Socket Create()
    {
        return new HttpGetSocket(Handler());
    }
    
    @Override
	public void OnConnect()
    {
        Send("GET / HTTP/1.0\r\n" +
        "Connection: close\r\n" +
        "Host: www.alhem.net\r\n" +
        "\r\n");
    }

    @Override
	public void OnFirst()
    {
        if (IsRequest())
        {
            System.out.println(" * HTTP Request>");
            System.out.println(GetMethod());
            System.out.println(GetUrl());
            System.out.println(GetHttpVersion());
        }
        if (IsResponse())
        {
            System.out.println(" * HTTP Response>");
            System.out.println(GetHttpVersion());
            System.out.println(GetStatus());
            System.out.println(GetStatusText());
        }
    }
    
    @Override
	public void OnHeader(String key,String value)
    {
        System.out.println(" * " + key + ": " + value);
    }
    
    @Override
	public void OnHeaderComplete()
    {
        System.out.println("Header Complete");
    }
    
    @Override
	public void OnData(byte[] buf,int l)
    {
    }
}
