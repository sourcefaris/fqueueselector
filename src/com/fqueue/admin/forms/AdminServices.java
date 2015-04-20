/*
 * Created on Sep 19, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.fqueue.admin.forms;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;
import com.jeta.forms.components.panel.FormPanel;
import com.mysql.jdbc.Connection;
import com.fqueue.common.Connector;
import com.fqueue.common.DataManager;
import com.fqueue.common.QValidateComponents;
import com.fqueue.common.SQLExecutor;
import com.fqueue.common.validateIP;
import com.fqueue.dto.Branchservicedetail;
import com.fqueue.dto.BranchservicedetailId;
import com.rubean.rcms.msf.OpenException;
import com.rubean.rcms.ui.RubeanButton;
import com.rubean.rcms.ui.RubeanEditField;
import com.rubean.rcms.ui.RubeanLabel;
import com.rubean.rcms.ui.RubeanPanel;
import com.rubean.rcms.ui.RubeanTable;
import com.rubean.ui.UiUtils;

/**
 * @author Helen
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AdminServices extends RubeanPanel{//extends SNSBaseForm {
	public String screenVersion = "$Revision: 1.29 $";
	Connector connector = new Connector();
	
	public AdminServices(){
		qinit();
	}
	public void qinit(){
		man = new DataManager();
		panel = new FormPanel("com/fqueue/admin/res/AdminServices.jfrm");
		JScrollPane scrollpane = new JScrollPane(panel);
		bottomButtonBar = (RubeanPanel) panel.getComponentByName("bottomButtonBar");
		lblMessage = (RubeanLabel) panel.getComponentByName("lblMessage");
		btnAdd = (RubeanButton) panel.getComponentByName("btnAdd");
		btnEdit = (RubeanButton) panel.getComponentByName("btnEdit");
		btnDelete = (RubeanButton) panel.getComponentByName("btnDelete");
		btnSave = (RubeanButton) panel.getComponentByName("btnSave");
		btnSave.setEnabled(false);
		
		btnAdd.addActionListener(actionListener);
		btnEdit.addActionListener(actionListener);
		btnDelete.addActionListener(actionListener);
		btnSave.addActionListener(actionListener);
		
		try {
			tblAdminServices = new RubeanTable();
			tblAdminServices = (RubeanTable) panel.getComponentByName("tblAdminServices");
    		serviceTableModel = new ServiceTableModel(columnTitle, 0);
    		tblAdminServices.setModel(serviceTableModel);
    		tblAdminServices.getTableHeader().setResizingAllowed(true);
    		tblAdminServices.addMouseListener(mouseListener);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		setTableFields();
		setEnabledFields(false);
		try {
			
			JScrollPane pane = (JScrollPane) panel.getComponentByName("tblViewScrollPane");
	    	if (pane!=null) {
	    	    pane.getViewport().setBackground(Color.WHITE);
//	    	    pane.setBorder(BorderFactory.createLineBorder(RubeanUI.getRubeanColor()));
	    	}
			this.add(scrollpane);

	        bottomButtonBar.validate();
	        
			refresh();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	class ServiceTableModel extends DefaultTableModel
    {
        public ServiceTableModel(Object[] title, int rowCount)
        {
            super(title, rowCount);
        }

        public boolean isCellEditable(int row, int column)
        {
            return false;
        }
    }
	
	private Object[] setValueTable(String branchCode, int counterNo, String serviceName, String ipAddress){
        ArrayList data = new ArrayList();
        data.add(0, branchCode);
        data.add(1, String.valueOf(counterNo));
        data.add(2, serviceName);
        data.add(3, ipAddress);
     
        return data.toArray();
    }
	
	private ActionListener actionListener = new ActionListener(){

		public void actionPerformed(ActionEvent e) {
			setDone(false);
//			setEnabledFields(false);
			if(e.getSource().equals(btnAdd)){
				if (btnAdd.getText().equalsIgnoreCase("Add")){
					clearFields();
			
					setEnabledFields(true);
					txtBranchCode.setText(man.getParameter(man.branchCode));
					btnAdd.setText("Cancel");
					btnEdit.setEnabled(false);
					btnDelete.setEnabled(false);
					btnSave.setEnabled(true);
					btnSave.setText("Save");
					tblAdminServices.setEnabled(false);
				}else if (btnAdd.getText().equalsIgnoreCase("Cancel")){
					clearFields();
					
					setEnabledFields(false);
					btnAdd.setText("Add");
					btnEdit.setEnabled(true);
					btnDelete.setEnabled(true);
					btnSave.setEnabled(false);
					tblAdminServices.setEnabled(true);
				}
			}else if(e.getSource().equals(btnDelete)){
				try {
					String[] a = {(String) serviceTableModel.getValueAt(tblAdminServices.getSelectedRow(), 0),
							serviceTableModel.getValueAt(tblAdminServices.getSelectedRow(), 1).toString()};
					
					conn = new Connector().getConnection();
					se = new SQLExecutor();
					se.deletePreparedStatement(conn, "DELETE FROM branchservicedetail WHERE branch_code =? AND counter_no=?", a);

					grantDB((String) serviceTableModel.getValueAt(tblAdminServices.getSelectedRow(), 3),false);
					serviceTableModel.removeRow(tblAdminServices.getSelectedRow());
					panel.repaint();
					
					lblMessage.setText("Branch Service successfully removed");
				} catch (SQLException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				} finally {
					try {
						if (conn !=null)
							conn.close();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				}	
			}else if(e.getSource().equals(btnEdit)){
				if (btnEdit.getText().equalsIgnoreCase("Edit")){
					txtBranchCode.setText((String) tblAdminServices.getValueAt(tblAdminServices.getSelectedRow(), 0));
					txtCounter.setText(tblAdminServices.getValueAt(tblAdminServices.getSelectedRow(), 1).toString());
					cboServiceName.setSelectedItem(tblAdminServices.getValueAt(tblAdminServices.getSelectedRow(), 2));
					txtIPAddress.setText((String) tblAdminServices.getValueAt(tblAdminServices.getSelectedRow(), 3));
					cboServiceName.setEnabled(true);
//					txtIPAddress.setEnabled(true);
					
					btnEdit.setText("Cancel");
					btnAdd.setEnabled(false);
					btnDelete.setEnabled(false);
					btnSave.setText("Update");
					btnSave.setEnabled(true);
					tblAdminServices.setEnabled(false);
				}else if (btnEdit.getText().equalsIgnoreCase("Cancel")){
					clearFields();
					
					setEnabledFields(false);
					btnEdit.setText("Edit");
					btnAdd.setEnabled(true);
					btnDelete.setEnabled(true);
					btnSave.setEnabled(false);
					tblAdminServices.setEnabled(true);
				}
			} else if (e.getSource().equals(btnSave)){
				if (validateFields()){
					try {
						if (btnSave.getText().equalsIgnoreCase("Save")){
							saveData();
						}else if (btnSave.getText().equalsIgnoreCase("Update")){
							updateData();	
						}
						
						if (isDone()==true){
							btnAdd.setText("Add");
							btnEdit.setText("Edit");
							btnAdd.setEnabled(true);
							btnEdit.setEnabled(true);
							btnDelete.setEnabled(true);
							tblAdminServices.setEnabled(true);
							btnSave.setEnabled(false);
							setEnabledFields(false);
						}
					} catch (SQLException e1) {
						lblMessage.setText(e1.getMessage());
					} catch (IOException e1) {
						lblMessage.setText(e1.getMessage());
					}
				}
			}
		}
	};
		
	private MouseListener mouseListener = new MouseAdapter(){

		public void mouseClicked(MouseEvent arg0) {
			if (tblAdminServices.isEnabled() == true){
				txtBranchCode.setText((String) tblAdminServices.getValueAt(tblAdminServices.getSelectedRow(), 0));
				txtCounter.setText(tblAdminServices.getValueAt(tblAdminServices.getSelectedRow(), 1).toString());
				cboServiceName.setSelectedItem(tblAdminServices.getValueAt(tblAdminServices.getSelectedRow(), 2));
				txtIPAddress.setText((String) tblAdminServices.getValueAt(tblAdminServices.getSelectedRow(), 3));
			}
		}
	};
	
	private void updateData() throws IOException, SQLException{
		try {
			if (validateFields()){
					serviceTableModel.setValueAt(txtBranchCode.getText(), tblAdminServices.getSelectedRow(), 0);
					serviceTableModel.setValueAt(txtCounter.getText(), tblAdminServices.getSelectedRow(), 1);
					serviceTableModel.setValueAt((String) cboServiceName.getSelectedItem(), tblAdminServices.getSelectedRow(), 2);
					serviceTableModel.setValueAt(txtIPAddress.getText(), tblAdminServices.getSelectedRow(), 3);

					se = new SQLExecutor();
					conn = new Connector().getConnection();
					String[] a = {(String) cboServiceName.getSelectedItem(),
							txtBranchCode.getText(),
							txtCounter.getText(),
							txtIPAddress.getText()};
					se.editPreparedStatement(conn, "UPDATE branchservicedetail SET service_name=? WHERE branch_code =? AND counter_no=? AND ip_address=?", a);
					grantDB(txtIPAddress.getText(),true);
					lblMessage.setText("Branch Service successfully updated");
					setDone(true);					
			}
		} catch (SQLException e) {
			throw new SQLException(e.getMessage());
		} catch (IOException e) {
			throw new IOException(e.getMessage());
		} finally {
			try {
				if (conn !=null)
					conn.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}	
	}
	
	private void saveData() throws SQLException, IOException{
		try {
			if (validateFields()){
				if(!validateBCCounterIP(1,txtBranchCode.getText(),txtCounter.getText())){
					if(!validateBCCounterIP(2, txtBranchCode.getText(), txtIPAddress.getText())){
						se = new SQLExecutor();
						conn = new Connector().getConnection();
						String[] a = {txtBranchCode.getText(),
								txtCounter.getText(),
								(String) cboServiceName.getSelectedItem(),
								txtIPAddress.getText()};
						se.addPreparedStatement(conn, "INSERT INTO branchservicedetail VALUES (?,?,?,?)", a);
						grantDB(txtIPAddress.getText(),true);
						lblMessage.setText("Branch Service successfully saved");
						setDone(true);
						serviceTableModel.addRow(setValueTable(txtBranchCode.getText(), Integer.parseInt(txtCounter.getText()), (String) cboServiceName.getSelectedItem(), txtIPAddress.getText()));	
					}else
						lblMessage.setText("IP "+txtIPAddress.getText()+" Already exist");	
				}else
					lblMessage.setText("Counter "+txtCounter.getText()+" Already exist");
				}
		} catch (NumberFormatException e) {
			throw new NumberFormatException(e.getMessage());
		} catch (SQLException e) {
			throw new SQLException(e.getMessage());
		} catch (IOException e) {
			throw new IOException(e.getMessage());
		} finally {
			try {
				if (conn !=null)
					conn.close();
			} catch (SQLException e1) {
//				setMessagePanel(e1.getMessage()); 
				e1.printStackTrace();
			}
		}	
	}

	private void clearFields(){
		txtBranchCode.setText("");
		txtCounter.setText("");
		cboServiceName.setSelectedItem(null);
		txtIPAddress.setText("");
	}
	
	private void setEnabledFields (boolean val){
		txtCounter.setEnabled(val);
		cboServiceName.setEnabled(val);
		txtIPAddress.setEnabled(val);
	}
	
	private void setTableFields(){
		txtBranchCode = (RubeanEditField) QValidateComponents.getComponent(panel, "txtBranchCode",RubeanEditField.TYPE_STRING, 4);
		txtCounter = (RubeanEditField) QValidateComponents.getComponent(panel, "txtCounter",RubeanEditField.TYPE_INTEGER,  2);		
		txtIPAddress = (RubeanEditField) QValidateComponents.getComponent(panel, "txtIPAddress",RubeanEditField.TYPE_STRING, 15);
		cboServiceName = (JComboBox) panel.getComponentByName("cboServiceName");
		fillCboServiceName();
		txtBranchCode.setEnabled(false);
				
	}
	
	private void fillCboServiceName() {
			
		cboServiceName.addItem("qcs");
		cboServiceName.addItem("qtreg");
		cboServiceName.addItem("qtmulti");
		cboServiceName.addItem("qbsm");
		if("convenSry".equals(man.getParameter("displayScr"))){
			cboServiceName.addItem("qcssry");
			cboServiceName.addItem("qtsry");
		}
			
	}
	
	
	public void open() throws OpenException {
		try { 
			refresh();
		} catch (Exception e){
			e.printStackTrace();
			throw new OpenException(e.getMessage());			
		}		
	}
	
	public void refresh(){
		for(int i=0; i<serviceTableModel.getRowCount(); i++){
			serviceTableModel.removeRow(i);
		}
		
		results = getData();
		for(int i=0; i<results.size(); i++){
			serviceTableModel.addRow(setValueTable(((Branchservicedetail) results.get(i)).getId().getBranchCode(), 
					((Branchservicedetail) results.get(i)).getId().getCounterNo(), 
					((Branchservicedetail) results.get(i)).getId().getServiceName(), 
					((Branchservicedetail) results.get(i)).getIpAddress()));
		}
	}
		
	public List getData(){
		Branchservicedetail detail;
		Connection conn = null;
		List lstResult = new ArrayList();
		ResultSet rs = null;
		String[] a = {};
		SQLExecutor se = new SQLExecutor();
		
		try {
			conn = new Connector().getConnection();
			rs = se.selectPreparedStatement(conn, "SELECT * FROM branchservicedetail ORDER BY counter_no", a);
			while (rs.next()){
				detail = new Branchservicedetail();
				detail.setId(new BranchservicedetailId(rs.getString("BRANCH_CODE"),rs.getInt("COUNTER_NO"), rs.getString("SERVICE_NAME")));
				detail.setIpAddress(rs.getString("IP_ADDRESS"));				
				lstResult.add(detail);
			}
		}
		catch (SQLException sq) {
			sq.printStackTrace();
			lblMessage.setText(sq.getMessage());
		} finally {
			try {
				if (rs != null) 
					rs.close();
				if (conn !=null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}				
		}		 
		return lstResult;
	}
	

	private boolean validateBCCounterIP(int code,String branch,String ipOrCounter){
		Connection conn = null;
		ResultSet rs = null;
		String[] a = {branch,ipOrCounter};
		SQLExecutor se = new SQLExecutor();
		String query="";
		boolean flag=false;
		String query1 ="select * from branchservicedetail b where b.BRANCH_CODE=?  and b.COUNTER_NO=? ";
		String query2 ="select * from branchservicedetail b where b.BRANCH_CODE=? and b.IP_ADDRESS=?";
		if(code==1){
			query = query1;
		}else
			query = query2;
		
		try {
			conn = new Connector().getConnection();
			rs = se.selectPreparedStatement(conn,query, a);
			if (rs.next()){
				flag=true;
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) 
					rs.close();
				if (conn !=null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}				
		}
		return flag;
	}
	
	private boolean validateFields(){
		String isRequired = " is required";
		validateIP valIP = new validateIP();
		if (!isValid(txtBranchCode.getText().trim().length(), txtBranchCode, "Branch Code" + isRequired )) return false;
		if (!isValid(txtCounter.getText().trim().length(), txtCounter, "Counter" + isRequired )) return false;
		if (!isValidCombo(cboServiceName.getSelectedIndex(),-1,cboServiceName,"Service name" + isRequired)) return false;
		if (!valIP.validateAnIpAddressWithRegularExpression(txtIPAddress.getText().toString())){
			lblMessage.setText("ip address is not valid");
			UiUtils.requestFocusLater(txtIPAddress);
			return false;
		}			
		return true;		
	}
	
	private boolean isValid(int len, Component c, String message){
		if (len>0) return true;
		else{
			UiUtils.requestFocusLater(c);
			lblMessage.setText(message);
			return false;
		}
	}	
	
    public boolean isValidCombo(int selectedCombo, int defaultIndex, Component c, String message){
    	if (selectedCombo==defaultIndex){ 
    		UiUtils.requestFocusLater(c);
    		lblMessage.setText(message);
    		return false;
    	}
    	return true;
    }
    
	private void grantDB(String ipAddr, boolean grant) throws IOException{
		//String datfile = "c:\\db" + new SimpleDateFormat("yyyyMMdd-hhmmss").format(new java.util.Date()) + ".bat";
		String datfile = System.getProperty("queue.dir")+"grantUser.sql";
		String user = connector.username; // "root";
		String pwd  = connector.password; // "password";
		String execS ="";
		
		File fOut = new File(datfile);
		FileOutputStream out = new FileOutputStream(fOut);
		Writer wr = new OutputStreamWriter(out);
		if (grant)
			wr.write("GRANT ALL PRIVILEGES ON queue.* TO root@" +  ipAddr + " IDENTIFIED BY 'password' ;" + "\r\n" +
					 "FLUSH PRIVILEGES;" + "\r\n");
		else
			wr.write("REVOKE ALL PRIVILEGES ON queue.* FROM root@" +  ipAddr + " IDENTIFIED BY 'password' ;" + "\r\n" +
					"FLUSH PRIVILEGES;" + "\r\n");
		wr.flush();
		wr.close();
		out.close();
		
		//to execute grant process
		Runtime runT = Runtime.getRuntime();
		try{
			execS = "mysql -u "+user+" -p"+pwd+" queue < "+datfile ;
			runT.exec( execS );
		}
		catch (IOException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}		
		finally{
			if (fOut.exists()) fOut.delete();
		}
	}

	
	//setter getter
	public boolean isDone() {
		return done;
	}
	public void setDone(boolean done) {
		this.done = done;
	}
	
	private FormPanel panel = null;
	private List results = null;
	private boolean done = false;
	private DataManager man = null;
	private RubeanEditField txtBranchCode = null;
	private RubeanEditField txtCounter = null;
	private JComboBox cboServiceName = null;
	private RubeanEditField txtIPAddress = null;
	private RubeanPanel bottomButtonBar = null;
	private RubeanLabel lblMessage = null;
	private RubeanTable tblAdminServices = null;
	private RubeanButton btnAdd = null;
	private RubeanButton btnEdit = null;
	private RubeanButton btnDelete = null;
	private RubeanButton btnSave = null;
	private Connection conn = null;
	private SQLExecutor se = null;	
	private DefaultTableModel serviceTableModel = null;
	private String[] columnTitle = new String[]{"Branch Code", "Counter No", "Service Name", "IP Address"};


}
