/*
 * Created on 18/09/2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.fqueue.forms;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.fqueue.admin.forms.PasswordConfirmation;
import com.jeta.forms.components.panel.FormPanel;
import com.fqueue.common.DataManager;
import com.fqueue.common.KillApp;
import com.rubean.rcms.ui.RubeanButton;
import com.rubean.rcms.ui.RubeanLabel;
import com.rubean.rcms.ui.RubeanPasswordField;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MainSelector{
	final JFrame frame = new JFrame();
	final JFrame framePass = new JFrame();
	private FormPanel panel = null;
	private RubeanPasswordField txtPassword = null;
	private RubeanLabel lblMessage = null;
	private RubeanButton btnOk = null;
	private RubeanButton btnCancel = null;
	private DataManager man=null;
	public static void main(String[] args) {
		new MainSelector();	
	}
	
	public MainSelector(){
		final QFrontDisplay mainForm = new QFrontDisplay();
		//		final AdminParameters mainForm = new QMainPanel(frame);
		
		mainForm.init();
		//		mainForm.qinit();
		initPassword();
		frame.getContentPane().add(mainForm);
		
		double d = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		double d1 = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		Dimension dim=new Dimension();
		dim.setSize(d,d1);
		frame.setSize(dim);
		frame.setTitle("Queuing Selector");
		frame.setUndecorated(true);
		frame.getContentPane().setBackground(Color.white);
		frame.setBackground(Color.white);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		System.out.println("Queue main display before window listener");
		frame.addMouseListener(mouseListener);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent windowevent) {
				if (JOptionPane.showConfirmDialog(frame, "Close Application ?", "Exit Confirmation", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					try {
						System.exit(0);
					} catch (Exception e) {
						System.exit(1);
					}
				}
			}
		});
		System.out.println("Queue main display before show");	
		//        frame.show();

		frame.setExtendedState(Frame.MAXIMIZED_BOTH);
		frame.setResizable(false);
		frame.setAlwaysOnTop(true);
		frame.setVisible(true);
		System.out.println("Queue main display after show");
		new KillApp("selector");
		man = new DataManager();
		if(man.getParameter(man.paperCode).equals("")||man.getParameter(man.paperValue).equals(""))
			man.insertPaperRow();
	}
	
	private void initPassword(){
	 	
		panel = new FormPanel("com/fqueue/admin/res/PasswordConfirmation.jfrm");
		txtPassword = (RubeanPasswordField)panel.getComponentByName("txtPassword");
		btnOk = (RubeanButton) panel.getComponentByName("btnOk");
		lblMessage = (RubeanLabel) panel.getComponentByName("lblMessage");
		btnOk.addActionListener(actionListener);
		btnCancel = (RubeanButton) panel.getComponentByName("btnCancel");
		btnCancel.addActionListener(actionListener);
		framePass.getContentPane().add(panel);
	}
		
		private void displayPassword(boolean stat){
			String title=null;
			txtPassword.setText("");
			lblMessage.setText("");
			double d = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
			double d1 = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
			int x = 390;
			int y = 190;
			Dimension dim=new Dimension();
			dim.setSize(x,y); // 355, 174
			framePass.setSize(dim);
			framePass.setLocation((int)((d - x)/2D), (int)( (d1 - y)/2D ));
			framePass.setTitle(title);
			framePass.getContentPane().setBackground(Color.white);
			framePass.setBackground(Color.white);
			framePass.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			framePass.setAlwaysOnTop(true);
			framePass.setResizable(false);
			//frame.getRootPane().setWindowDecorationStyle(0);		
			framePass.setVisible(stat);	
//			txtPassword.setFocusable(true);
		}
	
	private MouseListener mouseListener = new MouseListener(){

		public void mouseClicked(MouseEvent evt) {
			
//			if(evt.isMetaDown()){
//				displayPassword(true);
////				frame.setState(Frame.ICONIFIED);
//			}
			if(evt.getClickCount()==2){
				displayPassword(true);
			}
			
		}

		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		public void mousePressed(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		public void mouseReleased(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
		
	};
	
	private ActionListener actionListener = new ActionListener(){

		public void actionPerformed(ActionEvent e) {
			if (e.getSource().equals(btnOk)){
				PasswordConfirmation pwd = new PasswordConfirmation();
				if(pwd.cekPassword(txtPassword.getText().toString(),"password")){	
					framePass.setVisible(false);
					frame.setState(Frame.ICONIFIED);
					txtPassword.setText("");
					lblMessage.setText("");
				}else{
					lblMessage.setText("Password not Valid..!!");
					txtPassword.setText("");
					}
			}else if (e.getSource().equals(btnCancel)){
				txtPassword.setText("");
				lblMessage.setText("");
			}
		}
		
	};
}


