package com.fqueue.common;

import java.net.InetAddress;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.mysql.jdbc.Connection;

public class Connector {
	public String username = "root";
	public String password = "";
	private String myIP = "";
	public Connector() {
	}
	
	public Connection getConnection() throws SQLException{
		Connection conn = null;
		try{
			myIP=InetAddress.getLocalHost().getHostName();
			Class.forName("org.gjt.mm.mysql.Driver");
			conn = (Connection) DriverManager.getConnection("jdbc:mysql://localhost:3306/queue", username, password);
			
		}
		catch(SQLException sq){
			throw sq;
		}
		catch(Exception e){
			throw new SQLException(e.toString());
		}
		return conn;
	}
	public void closeConnection(Connection conn) throws SQLException{
		conn.close();
	}
}
