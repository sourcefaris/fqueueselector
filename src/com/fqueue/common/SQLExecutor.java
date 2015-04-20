package com.fqueue.common;

import java.sql.*;

public class SQLExecutor extends Connector{

    public SQLExecutor() {
    }
    //insert into table
    public void addPreparedStatement(Connection conn, String sqlInsert, String[] args) throws SQLException {
        PreparedStatement psInsert = null;
        try {
            psInsert = conn.prepareStatement(sqlInsert);
            for (int i = 0; i < args.length; i++) {
                int iParam = i + 1;
                psInsert.setString(iParam, args[i]);
            }
            psInsert.executeUpdate();
        }
        catch (SQLException se) {
            throw se;
        }

    }

    //update table
    public void editPreparedStatement(Connection conn, String sqlUpdate, String[] args) throws SQLException {
        try {
            addPreparedStatement(conn, sqlUpdate, args);
        }
        catch (SQLException se) {
            throw se;
        }
        
    }

    //delete table
    public void deletePreparedStatement(Connection conn, String sqlUpdate, String[] args) throws SQLException {
        try {
            addPreparedStatement(conn, sqlUpdate, args);
        }
        catch (SQLException se) {
            throw se;
        }
    }

    //select from table
    public ResultSet selectPreparedStatement(Connection conn, String sqlSelect, String[] args) throws SQLException {
        PreparedStatement psSelect = null;
        ResultSet rs = null;
        try {
        	psSelect = conn.prepareStatement(sqlSelect);
        	for (int i = 0; i < args.length; i++) {
                int iParam = i + 1;
                psSelect.setString(iParam, args[i]);
            }
            rs = psSelect.executeQuery();
        }
        catch (SQLException se) {
            throw se;
        }
        return rs;
    }
    public static void main(String[] args) throws SQLException {

        SQLExecutor se = new SQLExecutor();
        Connector con = new Connector();
        Connection conn = null;

        try {
            conn = con.getConnection();
        }
        catch (SQLException sq) {
            System.out.println("Connection failed");
        }
        ResultSet rs2=null;
        String counter = null;
        String ipAddress = null;

        String[] a = {};
        rs2 = se.selectPreparedStatement(conn, "Select * from branchservicedetail", a);

        while ( rs2.next() ) {
           ipAddress = rs2.getString("IP_ADDRESS");
           System.out.println(ipAddress);
        }
        rs2.close();
   }
}