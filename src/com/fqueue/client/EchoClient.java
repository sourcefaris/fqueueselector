/*
 * Created on Sep 22, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.fqueue.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class EchoClient {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new EchoClient();
	}

	public EchoClient()
	{  
		String ServerName = "localhost";  //Server Name
		int portNumber = 9999;            //Port Number  
		try
		{  
			//Attempting to conenct to a TCP service for a given Server and Port number.
			Socket s = new Socket(ServerName,portNumber);
			//Connection Established .... 

			BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
			PrintWriter out = new PrintWriter (new OutputStreamWriter (s.getOutputStream()));

//			out.println ("UpdateNumber;2;10.87.19.123");
//			out.println ("Call;10.87.19.158;finish");
//			out.println ("Finish;10.87.19.164");
//			out.println ("Call;10.87.19.124;noshow");
			out.println ("Call;10.87.19.158;finish");
//			out.println ("Call;10.87.19.123;noshow");
//			out.println ("SendTeller;503;2008-11-07 09:21:15");
//			out.println ("SendTeller;10.87.19.163");
			
			out.flush();
			out.println ("BYE");
			out.flush();

			//Receive data from the server ....  
			while (true)
			{  
				String str = in.readLine();
				if (str == null) 
					break;
				else
					System.out.println(str);
			}//While

			//Closing socket
			s.close();

		}
		catch (UnknownHostException e)
		{
			System.err.println ("Could not resolve host name: " + e.getMessage());
			e.printStackTrace();
		}
		catch (IOException e)
		{  
			System.err.println ("A communication Error occured: " + 
					e.getClass().getName() + ":  " + e.getMessage());
			e.printStackTrace();
		}
		catch (SecurityException e)
		{
			System.err.println ("The security manager refused permission to " +
					"connect to the remote TCP Service: " +
					e.getMessage ()); 

		}
	}
}
