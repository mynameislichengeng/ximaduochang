/*
 * HTTPSocket.java
 *
 * Created on den 28 oktober 2004, 15:18
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
public abstract class HTTPSocket extends TcpSocket
{
    
    /** Creates a new instance of HTTPSocket */
    public HTTPSocket(SocketHandler h)
    {
        super(h);
        SetLineProtocol();
    }

    @Override
	public void OnRead()
    {
        super.OnRead();
        if (!m_header)
        {
            if (m_ibuf.limit() > 0)
            {
                byte[] buf = new byte[m_ibuf.limit()]; // huvva
                m_ibuf.get(buf);
                OnData(buf, buf.length);
                buf = null;
            }
        }
    } // OnRead

    @Override
	public void ReadLine()
    {
        if (m_ibuf.limit() > 0)
        {
            byte[] buf = new byte[m_ibuf.limit()]; // huvva
            m_ibuf.get(buf);
            for (int i = 0; i < buf.length; i++)
            {
                if (!m_header)
                {
                    int sz = buf.length - i;
                    byte[] buf2 = new byte[sz];
                    for (int j = 0; j < sz; j++)
                    {
                        buf2[j] = buf[j + i];
                    }
                    OnData(buf2, sz);
                    buf2 = null;
                    break;
                }
                switch (buf[i])
                {
                    case 13:
                        break;
                    case 10:
                        OnLine(m_line.toString());
                        m_line.delete(0, m_line.length());
                        break;
                    default:
                    {
                        String aChar = new Character((char)buf[i]).toString();
                        m_line.append(aChar);
                    }
                }
            }
            //
            buf = null;
        }
    } // ReadLine

    @Override
	public void OnLine(String line)
    {
        if (m_first)
        {
            Parse pa = new Parse(line);
            String str = pa.getword();
            if (str.length() >= 4 && str.substring(0,4).equals("HTTP")) // response
            {
                m_http_version = str;
                m_status = pa.getword();
                m_status_text = pa.getrest();
                m_response = true;
            }
            else // request
            {
                m_method = str;
                m_url = pa.getword();
                /** TODO split m_url => m_uri / m_query_string */
                m_http_version = pa.getword();
                m_request = true;
            }
            m_first = false;
            OnFirst();
            return;
        }
        if (line.length() == 0)
        {
            m_header = false;
            OnHeaderComplete();
            return;
        }
        Parse pa = new Parse(line, ":");
        String key = pa.nextToken();
        String value = pa.getrest();
        OnHeader(key, value);
    } // OnLine

    public abstract void OnData(byte[] buf,int l);

    public abstract void OnFirst();

    public abstract void OnHeader(String key,String value);
    
    public abstract void OnHeaderComplete();

    public boolean IsRequest()
    {
        return m_request;
    }
    public boolean IsResponse()
    {
        return m_response;
    }
    public String GetHttpVersion()
    {
        return m_http_version;
    }
    public String GetStatus()
    {
        return m_status;
    }
    public String GetStatusText()
    {
        return m_status_text;
    }
    public String GetMethod()
    {
        return m_method;
    }
    public String GetUrl()
    {
        return m_url;
    }
    public String GetUri()
    {
        return m_uri;
    }
    public String GetQueryString()
    {
        return m_query_string;
    }

    //
    private boolean m_first = true;
    private boolean m_header = true;
    private boolean m_request = false;
    private boolean m_response = false;
    // response
    private String m_http_version;
    private String m_status;
    private String m_status_text;
    // request
    private String m_method;
    private String m_url;
    private String m_uri;
    private String m_query_string;

}
