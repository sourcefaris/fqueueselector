/*
 * Created on Jun 9, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.fqueue.forms;

/**
 * @author atirta
 *
 */

import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.rubean.rcms.ui.RubeanButton;

/**
 * Title: Main Panel for Permata Queueing System : Copyright: Copyright (c) 2003 Company: PermataBank
 * 
 * @authorv : atirta
 * @version 1.0
 */

public class QMainPanel extends QFrontDisplay  {  //   AdminParameters{

	private Image logo;

	public JFrame frameParent;

	RubeanButton buttonIncomingMessage;

	RubeanButton buttonIncomingSpvRequest;

	ImageIcon iconButtonEmailOnline;

	ImageIcon iconButtonEmailOffline;

	ImageIcon iconButtonSpv1;

	ImageIcon iconButtonSpv2;

	Thread blinkerThread;

	boolean blink = false;

	public QMainPanel (JFrame frameParent) {
		super();
		this.frameParent = frameParent;
	}


	private String getIPAddress() {
		InetAddress addr;
		try {
			addr = InetAddress.getLocalHost();
			return addr.getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return null;
		}
	}


	public void propertyChange(final PropertyChangeEvent e) {
		try {
			propertyChange_(e);
		} catch (Exception ex) {
		}
	}

	public void propertyChange_(final PropertyChangeEvent e) {
//		super.propertyChange(e);
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
//				if (e.getPropertyName().equals(" ==== ")) {
//					sound.setRunning(true);
//					sound.play();
//					Object value = e.getNewValue();
//					buttonIncomingMessage.setIcon(iconButtonEmailOnline);
//					JOptionPane.showMessageDialog(frameParent,
//							"You have new message(s)\n" + value,
//							"Free Format Messaging",
//							JOptionPane.INFORMATION_MESSAGE);
//
//				}
			}
		});
	}


//	private void blink(boolean isBlink) {
//		blink = isBlink;
//		if (isBlink) {
//			if (blinkerThread != null && blinkerThread.isAlive()) {
//				blinkerThread.interrupt();
//			} else {
//				blinkerThread = new Blinker();
//				blinkerThread.start();
//			}
//		} else {
//			if (blinkerThread != null && blinkerThread.isAlive()) {
//				blinkerThread.interrupt();
//			}
//		}
//	}
//
//	private class Blinker extends Thread {
//		public void run() {
//			Color originalColor = buttonIncomingSpvRequest.getBackground();
//			while (blink) {
//				buttonIncomingSpvRequest.setBackground(Color.WHITE);
//				try {
//					if (blink)
//						sleep(700);
//				} catch (InterruptedException e1) {
//					buttonIncomingSpvRequest.setBackground(originalColor);
//					return;
//				}
//				buttonIncomingSpvRequest.setBackground(Color.YELLOW);
//				try {
//					if (blink)
//						sleep(700);
//				} catch (InterruptedException e1) {
//					buttonIncomingSpvRequest.setBackground(originalColor);
//					return;
//				}
//				buttonIncomingSpvRequest.setBackground(originalColor);
//			}
//		}
//	}

}