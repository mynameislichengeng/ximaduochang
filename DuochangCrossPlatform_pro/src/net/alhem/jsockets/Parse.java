/*
 * Parse.java
 *
 * Created on den 26 oktober 2004, 10:37
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
public class Parse extends java.util.StringTokenizer
{
    
    /** Creates a new instance of Parse */
    public Parse(String str)
    {
        super(str);
    }
    public Parse(String str,String split)
    {
        super(str, split);
    }
    public int nextInt()
    {
        String str = this.nextToken();
        return Integer.valueOf(str).intValue();
    }
    public double nextDouble()
    {
        String str = this.nextToken();
        return Double.valueOf(str).doubleValue();
    }
    
    public String getword()
    {
        return this.nextToken();
    }
    
    public String getrest()
    {
        StringBuffer tmp = new StringBuffer(1024);
        while (this.hasMoreTokens())
        {
            if (!tmp.equals(""))
            {
                tmp.append(" ");
            }
            tmp.append(nextToken());
        }
        return tmp.toString();
    }
}
