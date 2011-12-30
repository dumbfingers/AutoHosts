package com.yeyaxi.AutoHosts;

/**
 *  GNU GPL v3
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *  
 */

/**
 * Su.java
 * 
 * This file comes from the OpenSource Project n1torch (http://code.google.com/p/n1torch/)
 * 
 */

import java.io.DataOutputStream;
import java.io.IOException;


public class Su {
/*
 * This class handles 'su' functionality. Ensures that the command can be run, then
 * handles run/pipe/return flow.
 */
	public boolean can_su;
	public String su_bin_file;
	
	public Su() {
		this.can_su = true;
		this.su_bin_file = "/system/xbin/su";
		if (this.Run("echo"))
			return;
		this.su_bin_file = "/system/bin/su";
		if (this.Run("echo"))
			return;
		this.su_bin_file = "";
		this.can_su = false;	
	}
    public boolean Run(String command) {
    	DataOutputStream os = null;
    	try {
			Process process = Runtime.getRuntime().exec(this.su_bin_file);
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes(command + "\n");
			os.flush();
			os.writeBytes("exit\n");
			os.flush();
			process.waitFor();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	return false;
    }	
}
