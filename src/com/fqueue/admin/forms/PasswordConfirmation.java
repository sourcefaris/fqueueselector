/*
 * Created on Oct 20, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.fqueue.admin.forms;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import com.jeta.forms.components.panel.FormPanel;
import com.mysql.jdbc.Connection;
import com.fqueue.common.Connector;
import com.rubean.rcms.ui.RubeanButton;
import com.rubean.rcms.ui.RubeanLabel;
import com.rubean.rcms.ui.RubeanPanel;
import com.rubean.rcms.ui.RubeanPasswordField;

public class PasswordConfirmation extends RubeanPanel                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         {
	public String screenVersion = "$Revision: 1.14 $";
	final JFrame frame = new JFrame();
	private FormPanel panel = null;
	public boolean confirmed = false;

	private RubeanPasswordField txtPassword = null;
	private RubeanLabel lblMessage = null;
	private RubeanButton btnOk = null;
	private RubeanButton btnCancel = null;
	private String keyEvent;

	
	public PasswordConfirmation(){
		init();
	}
	
	public void init(){
		panel = new FormPanel("com/fqueue/admin/res/PasswordConfirmation.jfrm");
		JScrollPane scrollpane = new JScrollPane(panel);
		txtPassword = (RubeanPasswordField) panel.getComponentByName("txtPassword");
		txtPassword.addKeyListener(keyListener);
		lblMessage = (RubeanLabel) panel.getComponentByName("lblMessage");
		btnOk = (RubeanButton) panel.getComponentByName("btnOk");
		btnOk.addActionListener( actionListener);
		btnCancel = (RubeanButton) panel.getComponentByName("btnCancel");
		btnCancel.addActionListener( actionListener);
		this.add(scrollpane);		
	}

	public void displayScreen(boolean stat, String key){
		String title = null;
		keyEvent = key;
		txtPassword.setText("");
		lblMessage.setText("");
		frame.getContentPane().add(panel);	
		if(key.equalsIgnoreCase("ctrl8")){
			final JFrame frameOps = new JFrame();
			final AdminPrintPaper mainform = new AdminPrintPaper();
			mainform.qinit();
			frameOps.getContentPane().add(mainform);
			title = "Admin Print Paper";
			displayOpsScreen(frameOps, title);
		}
		else{
			double d = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
			double d1 = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
			int x = 390;
			int y = 190;
			Dimension dim=new Dimension();
			dim.setSize(x,y); // 355, 174
			frame.setSize(dim);
			frame.setLocation((int)((d - x)/2D), (int)( (d1 - y)/2D ));
			frame.setTitle(title);
			frame.getContentPane().setBackground(Color.white);
			frame.setBackground(Color.white);
			frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			frame.setAlwaysOnTop(true);
			frame.setResizable(false);
			//frame.getRootPane().setWindowDecorationStyle(0);		
			frame.setVisible(stat);		
		}
	}
	
	
	
	public boolean cekPassword(String pwd,String status){
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String result = null;
		
		String query = "select * from Parameter where code = '"+status+"' " +
						"and value = PASSWORD('"+pwd+"')";
		//System.out.println("query : "+query);
		
		try {
			
			conn = new Connector().getConnection();
			ps = conn.prepareStatement(query);
			rs = ps.executeQuery(query);
			
			while (rs.next()){
				result = rs.getString("value");
			}
			
			if ( result != null  ){
				confirmed = true;
				System.out.println("Benar ");
			}
			else{
				confirmed = false;
				System.out.println("NULL ");
			}
		}
		catch (SQLException sq) {
			if (conn == null) lblMessage.setText(sq.getMessage());
			else	sq.printStackTrace();
			sq.printStackTrace();
		} 
		finally {
			try {
				if (rs != null)	 rs.close();
				if (conn !=null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}				
		}		 
		return confirmed;
	}
	
	private ActionListener actionListener = new ActionListener(){
		public void actionPerformed(ActionEvent e) {
			if(e.getSource().equals(btnOk)){
				validatePassword();
			}
			if(e.getSource().equals(btnCancel)){
				lblMessage.setText("");
				txtPassword.setText("");
				frame.setVisible(false);
			}
		}
	};
	
	public boolean isValid() {
		return confirmed;
	}

	public boolean isConfirmed() {
		return confirmed;
	}

	public void setConfirmed(boolean confirmed) {
		this.confirmed = confirmed;
	}

//--------------- operation form display
	
	private void callScreen(String key){
		final JFrame frameOps = new JFrame();
		String title = "";
		if ("ctrl1".equalsIgnoreCase(key)){
			final AdminParameters mainForm = new AdminParameters();
			title = "Admin Parameter";
			mainForm.qinit();
			frameOps.getContentPane().add(mainForm);
		} else if ("ctrl2".equalsIgnoreCase(key)){
			final AdminServices mainForm = new AdminServices();
			mainForm.qinit();
			frameOps.getContentPane().add(mainForm);
			title = "Admin Services";
		} 
		displayOpsScreen(frameOps, title);
	}
	
	private void displayOpsScreen(JFrame frame, String title){
		if(title.equalsIgnoreCase("Admin Print Paper")){
			double d = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
			double d1 = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
			int x = 385;
			int y = 185;
			Dimension dim=new Dimension();
			dim.setSize(x,y); // 355, 174
			frame.setSize(dim);
			frame.setLocation((int)((d - x)/2D), (int)( (d1 - y)/2D ));
			frame.setTitle(title);
			frame.getContentPane().setBackground(Color.white);
			frame.setBackground(Color.white);
			frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			frame.setAlwaysOnTop(true);
			frame.setResizable(false);
			frame.setVisible(true);
		}else{
			double d = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
			double d1 = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
			Dimension dim=new Dimension();
			dim.setSize(d-100,d1-100);
			frame.setSize(dim);
			frame.setLocation((int)((d - (double)frame.getWidth())/2D), 0);
			frame.setTitle(title);
			frame.getContentPane().setBackground(Color.white);
			frame.setBackground(Color.white);
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			frame.setExtendedState(Frame.MAXIMIZED_BOTH);
			frame.setResizable(false);
			frame.setAlwaysOnTop(true);
			frame.setVisible(true);	
			}
	}
	
	private void validatePassword(){
		String codeParam="password";
		lblMessage.setText("");
	
		if (cekPassword(txtPassword.getText().toString(),codeParam)){
			setConfirmed(false);				
			txtPassword.setText("Password Valid ");	
			frame.setVisible(false);
			callScreen(keyEvent);
		}
		else{
			lblMessage.setText("Password Not Valid !!!");
			txtPassword.setText("");
			}	
		
	}
	
	private KeyListener keyListener = new KeyListener(){
		public void keyPressed(KeyEvent e) {}
		
		public void keyReleased(KeyEvent e) {
			if( e.getKeyCode()==10){
				System.out.println("ENTER");
				validatePassword();				
			}
		}
		
		public void keyTyped(KeyEvent e) {}
		
	};


}
