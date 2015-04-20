package com.fqueue.admin.forms;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JScrollPane;
import com.jeta.forms.components.panel.FormPanel;
import com.fqueue.common.Connector;
import com.fqueue.common.DataManager;
import com.fqueue.common.SQLExecutor;
import com.rubean.rcms.ui.RubeanButton;
import com.rubean.rcms.ui.RubeanEditField;
import com.rubean.rcms.ui.RubeanLabel;
import com.rubean.rcms.ui.RubeanPanel;

public class AdminPrintPaper extends RubeanPanel{
	private FormPanel panel =null;
	private RubeanButton btnReset=null;
	private RubeanLabel lblMessage =null;
	private RubeanEditField txtSumPaper=null;
	private Connection conn = null;
	private SQLExecutor se = null;	
	private DataManager man=null;
	
	public AdminPrintPaper(){
		qinit();
	}
	
	public void qinit(){
		man = new DataManager();
		panel = new FormPanel("com/fqueue/admin/res/AdminPrintPaper.jfrm");
		JScrollPane scrollpane = new JScrollPane(panel);
		btnReset = (RubeanButton) panel.getComponentByName("btnReset");
		btnReset.addActionListener(actionListener);
		txtSumPaper = (RubeanEditField) panel.getComponentByName("txtSumPaper");
		txtSumPaper.setText(getSumQueue());
		lblMessage = (RubeanLabel) panel.getComponentByName("lblMessage");
		this.add(scrollpane);
	}
	
	private ActionListener actionListener = new ActionListener(){
		public void actionPerformed(ActionEvent e) {
			if(e.getSource().equals(btnReset)){
				resetPrintPaper();	
			} 
			
		}
		
	};
	
	private String getSumQueue(){
		ResultSet rs = null;
		String[] a = {man.paperCode};
		SQLExecutor se = new SQLExecutor();
		String sumPaper=null;
		try {
			conn = new Connector().getConnection();
			rs = se.selectPreparedStatement(conn, "select value from parameter where code=? ", a);
			if(rs.next()){
				sumPaper = rs.getString("value");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return sumPaper;		
	}
	
	private void resetPrintPaper(){
		try {
			se = new SQLExecutor();
			conn = new Connector().getConnection();
			String[] a = {man.getParameter(man.paperValue),man.paperCode};
			se.editPreparedStatement(conn,"update parameter set value =? where code =? ",a);
			txtSumPaper.setText(man.paperCount);
			lblMessage.setText("Reset Successfully");
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				if (conn !=null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}				
		}
		
	}
}
