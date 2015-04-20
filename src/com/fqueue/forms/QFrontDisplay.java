package com.fqueue.forms;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import com.fqueue.admin.forms.PasswordConfirmation;
import com.fqueue.common.DataManager;
import com.fqueue.common.PrintPage;
import com.fqueue.common.TimeCategory;
import com.jeta.forms.components.panel.FormPanel;
import com.jeta.forms.gui.effects.ImagePainter;
import com.jeta.forms.gui.form.GridView;
import com.rubean.rcms.ui.RubeanImageButton;
import com.rubean.rcms.ui.RubeanLabel;
import com.rubean.rcms.ui.RubeanPanel;

public class QFrontDisplay extends RubeanPanel implements PropertyChangeListener {
	PasswordConfirmation pwdForm = new PasswordConfirmation();
	
	public QFrontDisplay() {
		border1 = new EtchedBorder(0);
		try {
			qInit();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
	
	public void init() {
		SwingUtilities.invokeLater(new Runnable() {
			
			public void run() {
//				adjustStatusLabels();
				
			}
			
		});
	}
	
	private void qInit() throws Exception {
		pwdForm.setVisible(false);
		man = new DataManager();
		currDate= getCurrentDate();
		HashMap mapCount = man.getCountServices();
		String formName = man.getServices();
		panel = new FormPanel("com/fqueue/res/Front_ver9.jfrm");
		setComponent_nonPriority();
		btnKasir.addActionListener(actionListener);
		btnRegistrasi.addActionListener(actionListener);
		
		gridFrontDisplay = (GridView) panel.getComponentByName("gridFrontDisplay");
		ImagePainter ip = new ImagePainter(new ImageIcon(QFrontDisplay.class.getResource("/com/fqueue/res/front_img_ver2.jpg")), 0, 0);
		gridFrontDisplay.setBackgroundPainter(ip);
		gridFrontDisplay.setOpaque(true);
		gridFrontDisplay.setVisible(true);
		
		timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Timer t = new Timer(1000, timeActionListener);
		
		t.start();
		this.add(panel);
		this.addKeyListener(keyListener);
		this.setFocusable(true);
	}
	
	private void adjustStatusLabels() {
		lblTime.setText(getTime());
	}
	
	private ActionListener timeActionListener = new ActionListener(){
		public void actionPerformed(ActionEvent e) {
			try {
				lblTime.setText(getTime());
				updateSumQueue("all",currDate);
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	};
	
	public String getTime() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
		time = sdf.format(cal.getTime());
		return time;
	}
	
	public String getCurrentDate(){
		Date date = new Date();
		date.setTime(System.currentTimeMillis());
		String strDate = dtFormat.format(date);
		return strDate;
	}		
	
	private ActionListener actionListener = new ActionListener(){
		
		public void actionPerformed(ActionEvent e) {
		try {
			if (e.getSource().equals(btnKasir)){
				setQueueNo(man.getMaxNo(man.Q_KASIR) + 1);
				insertData(man.Q_KASIR, getQueueNo());
				print(man.Q_KASIR, String.valueOf(getQueueNo()));
				updateSumQueue(man.Q_KASIR,currDate);
				updatePaperStatus(man.getParameter(man.paperCode));
				btnKasir.setFocusable(false);
			} else if (e.getSource().equals(btnRegistrasi)){
				setQueueNo(man.getMaxNo(man.Q_REGISTRASI) + 1);
				insertData(man.Q_REGISTRASI, getQueueNo());
				print(man.Q_REGISTRASI, String.valueOf(getQueueNo()));
				updateSumQueue(man.Q_REGISTRASI,currDate);
				updatePaperStatus(man.getParameter(man.paperCode));
				btnRegistrasi.setFocusable(false);
			} 
		} catch (Exception e2) {
			e2.printStackTrace();
		}
			
		}
	};
	
	
	private KeyListener keyListener = new KeyListener(){
		
		public void keyPressed(KeyEvent e) {
			
		}
		public void keyReleased(KeyEvent e) {		
			int no = 0;
			if(e.isControlDown() &&  e.getKeyCode()>=49 && e.getKeyCode()<=56){
				no = e.getKeyCode() - 48;
				
				if (!pwdForm.isConfirmed() ){
					pwdForm.displayScreen(true,"ctrl"+no);
				}
				else{
					pwdForm.setConfirmed(false);
				}					
			}
		}
		
		public void keyTyped(KeyEvent e) {}
		
	};
	
	/**
	 * @param service
	 * @throws SQLException 
	 */
	private void insertData(String service, int queueNo) throws Exception{
		String date = timeFormat.format(new Date());
		String dateDefault ="0000-00-00 00:00:00";
		String queueno = String.valueOf(queueNo);
		String category = TimeCategory.getCategory(new Date());
		String args[] = {queueno,date, dateDefault, dateDefault,dateDefault, man.START_STATUS,null, 
				String.valueOf((man.getMaxQueueIndex()+1)), man.NOT_CALLED, 
				category, null, null, man.getParameter(man.branchCode), man.START_COUNTER, service,null};
		
		
		String query = "INSERT INTO process VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		man.insertData(args, query);		
	}
	
	public void propertyChange(final PropertyChangeEvent e) {
		SwingUtilities.invokeLater(new Runnable() {
			
			public void run() {
				if (e.getPropertyName().equals("appServerOnline"))
					adjustStatusLabels();
				else if (e.getPropertyName().equals("updateQueue"))
					adjustStatusLabels();
			}
			
		});
	}
	
	static Class _mthclass$(String s) {
		try {
			return Class.forName(s);
		} catch (ClassNotFoundException classnotfoundexception) {
			throw new NoClassDefFoundError(classnotfoundexception.getMessage());
		}
	}
	
	private void print(String service, String queueNo) throws Exception {
		PrintPage pages = new PrintPage(queueNo, service, lblTime.getText());
		
		// Create Print Job  
		PrinterJob pjob = PrinterJob.getPrinterJob();  
		PageFormat pf = PrinterJob.getPrinterJob().defaultPage();  
		Paper p = new Paper();
		p.setImageableArea(6, 0, 200, 200);
		pf.setPaper(p);
		pjob.setJobName("Permata Antrian");  
		Book book = new Book();  
		book.append(pages, pf);  
		pjob.setPageable(book);  
		
		// Send print job to default printer  
		try {
			pjob.print();
		} catch (PrinterException e) {
			throw new Exception(e);
		}  
	}
	
	private void updatePaperStatus(String sumPaper) throws Exception{		
		String valPaper=null;
		int x=0;
		x= Integer.parseInt(sumPaper);
		valPaper = Integer.toString(x-1);
		if(valPaper!=null){
			String args[]={valPaper,man.paperCode};
			String queryUpdate ="update `parameter` set value=? where code =?";
			man.updateData(args, queryUpdate);
		}
	}
	
	
	private void updateSumQueue(String service,String currDate) throws Exception{
		if(service.equals("all")){
			if (lblKasir != null)
				lblKasir.setText(String.valueOf("jumlah antrian : "+ man.getSumQueueWait("'Kasir'", currDate)));
			if (lblRegistrasi != null)
				lblRegistrasi.setText(String.valueOf("jumlah antrian : "+ man.getSumQueueWait("'Registrasi'", currDate)));
		}else if(service.equals("Kasir")){
			lblKasir.setText(String.valueOf("jumlah antrian : "+man.getSumQueueWait("'Kasir'",currDate)));
		}else if(service.equals("Registrasi")){
			lblRegistrasi.setText(String.valueOf("jumlah antrian : "+man.getSumQueueWait("'Registrasi'",currDate)));
		}
	}
	
	public int getQueueCS() {
		return queueCS;
	}
	
	public void setQueueCS(int queueCS) {
		this.queueCS = queueCS;
	}
	
	public int getQueueTellerMulti() {
		return queueTellerMulti;
	}
	
	public void setQueueTellerMulti(int queueTellerMulti) {
		this.queueTellerMulti = queueTellerMulti;
	}
	
	public int getQueueTellerReg() {
		return queueTellerReg;
	}
	
	public void setQueueTellerReg(int queueTellerReg) {
		this.queueTellerReg = queueTellerReg;
	}
	
	public int getQueueNo() {
		return queueNo;
	}
	
	public void setQueueNo(int queueNo) {
		this.queueNo = queueNo;
	}
	
	private void setComponent_nonPriority(){
		try{
			btnKasir = (RubeanImageButton) panel.getComponentByName("btnKasir");
			btnKasir.setIcon(new ImageIcon(QFrontDisplay.class.getResource("/com/fqueue/res/btncs_small.png")));
			btnKasir.setPressedIcon(new ImageIcon(QFrontDisplay.class.getResource("/com/fqueue/res/btncsrollover_small.png")));
	
			btnRegistrasi = (RubeanImageButton)panel.getComponentByName("btnRegistrasi");
			btnRegistrasi.setIcon(new ImageIcon(QFrontDisplay.class.getResource("/com/fqueue/res/preferred/btncsprio_smaller.png")));
			btnRegistrasi.setPressedIcon(new ImageIcon(QFrontDisplay.class.getResource("/com/fqueue/res/preferred/btncsprio_smallerrollover.png")));
	
			lblTime = (RubeanLabel) panel.getComponentByName("lblTime");
			lblRegistrasi = (RubeanLabel) panel.getComponentByName("lblRegistrasi");
			lblKasir = (RubeanLabel) panel.getComponentByName("lblKasir");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private FormPanel panel = null;
	private RubeanImageButton btnKasir;
	private RubeanImageButton btnRegistrasi;
	private RubeanLabel lblTime;
	private RubeanLabel lblKasir;
	private RubeanLabel lblRegistrasi;
	private GridView gridFrontDisplay;
	
	private String time = "00:00:00";
	private SimpleDateFormat timeFormat = null;
	private int queueCS = 0;
	private int queueTellerReg = 0;
	private int queueTellerMulti = 0;
	private int queueNo = 0;
	private DataManager man = null;
	private SimpleDateFormat dtFormat = new SimpleDateFormat("yyyy-MM-dd");
	private String currDate=null;
	Border border1;
	

}
