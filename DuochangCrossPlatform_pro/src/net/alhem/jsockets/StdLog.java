/*
 * StdLog.java
 *
 * Created on October 29, 2004, 12:35 PM
 */

package net.alhem.jsockets;

/**
 *
 * @author  Anders Hedstrom (grymse@alhem.net)
 */
public interface StdLog
{
    public void error(SocketHandler h,
    Socket s,
    String user_text,
    int err,
    String sys_err,int loglevel);
    
}
