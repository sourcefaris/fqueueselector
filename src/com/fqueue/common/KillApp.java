/*
 * Created on Nov 12, 2009
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.fqueue.common;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author mmaulana
 *
 * kill queue application based on pid
 */
public class KillApp {
	
	public KillApp(String appType) {
		makeFile(appType);
	}
	
	private static long getPID() {
		String processName =
			java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
		return Long.parseLong(processName.split("@")[0]);
	}
	
	private void makeFile(String appType){		
		String fileName="/kill " + appType + ".bat";
		String desktopPath = System.getProperty("user.home")+"/Desktop";
		String filePath= desktopPath+fileName;
		String text="@echo off \n"+
		":begin \n"+
		"taskkill /pid "+ getPID() + " \n " + 
		":end ";
		BufferedWriter output=null;
		try {
			output = new BufferedWriter(new FileWriter(filePath));
			output.write(text);
			output.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
}
