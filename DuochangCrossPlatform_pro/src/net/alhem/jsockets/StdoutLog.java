/*
 * StdLog.java
 *
 * Created on den 27 oktober 2004, 09:09
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
public class StdoutLog implements StdLog
{
    
    /** Creates a new instance of StdLog */
    public StdoutLog()
    {
    } // StdLog
    
    
    @Override
	public void error(SocketHandler h,
    Socket s,
    String user_text,
    int err,
    String sys_err,int loglevel)
    {
        String level = (loglevel == 0) ? "Info" :
            (loglevel == 1) ? "Warning" :
                (loglevel == 2) ? "Error" :
                    (loglevel == 3) ? "Fatal" : "Undefined (" + loglevel + ")";
        if (s != null)
        {
            System.out.println(level + ": " + h.getClass().getSimpleName() + "/" + s.toString() + ": " + user_text + "(" + err + "): " + sys_err);
        }
        else
        {
            System.out.println(level + ": " + h.getClass().getSimpleName() + ": " + user_text + "(" + err + "): " + sys_err);
        }
    } // error
}
