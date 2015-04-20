/*
 * Created on Oct 20, 2008
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;

import com.jeta.forms.components.panel.FormPanel;
import com.mysql.jdbc.Connection;
import com.fqueue.common.Connector;
import com.fqueue.common.QValidateComponents;
import com.fqueue.common.SQLExecutor;
import com.fqueue.dto.Parameter;
import com.rubean.rcms.ui.RubeanButton;
import com.rubean.rcms.ui.RubeanEditField;
import com.rubean.rcms.ui.RubeanLabel;
import com.rubean.rcms.ui.RubeanPanel;
import com.rubean.rcms.ui.RubeanTable;
import com.rubean.ui.UiUtils;

/**
 * @author mmaulana
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AdminParameters extends RubeanPanel {
	public String screenVersion = "$Revision: 1.13 $";

	public AdminParameters(){
		qinit();
	}
	
	public void qinit(){
		panel = new FormPanel("com/fqueue/admin/res/AdminParameters.jfrm");
		JScrollPane scrollpane = new JScrollPane(panel);
		extenBottomButtonBar = (RubeanPanel) panel.getComponentByName("extenBottomButtonBar");
		lblMessage = (RubeanLabel) panel.getComponentByName("lblMessage");
		btnAdd = (RubeanButton) panel.getComponentByName("btnAdd");
		btnEdit = (RubeanButton) panel.getComponentByName("btnEdit");
		btnSave = (RubeanButton) panel.getComponentByName("btnSave");
		btnSave.setEnabled(false);
//		btnEdit.setEnabled(false);
		
		btnAdd.addActionListener(actionListener);
		btnEdit.addActionListener(actionListener);
		btnSave.addActionListener(actionListener);
			
		setTableFields();
		setEnabledFields(false);
		try {
			tblAdminParameters = new RubeanTable();
			tblAdminParameters = (RubeanTable) panel.getComponentByName("tblAdminParameters");
    		parameterTableModel = new ParameterTableModel(columnTitle, 0);
    		tblAdminParameters.setModel(parameterTableModel);
    		tblAdminParameters.getTableHeader().setResizingAllowed(true);
    		tblAdminParameters.addMouseListener(mouseListener);
								
			JScrollPane pane = (JScrollPane) panel.getComponentByName("tblViewScrollPane");
			if (pane!=null) {
				pane.getViewport().setBackground(Color.WHITE);
//				pane.setBorder(BorderFactory.createLineBorder(RubeanUI.getRubeanColor()));
			}
			this.add(scrollpane);
			
			extenBottomButtonBar.validate();
			refresh();

			//pwdForm.init();
			//pwdForm.setVisible(false);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void setTableFields(){
		txtCode = (RubeanEditField) QValidateComponents.getComponent(panel, "txtCode", RubeanEditField.TYPE_STRING, 10);
		txtValue = (RubeanEditField) QValidateComponents.getComponent(panel, "txtValue", RubeanEditField.TYPE_STRING, 20);		
		txtDescription = (RubeanEditField) QValidateComponents.getComponent(panel, "txtDesc", RubeanEditField.TYPE_STRING, 100);		
	}	

	public void refresh(){
		for(int i=0; i<parameterTableModel.getRowCount(); i++){
			parameterTableModel.removeRow(i);
		}
		results = getData("");
		for(int i=0; i<results.size(); i++){
			parameterTableModel.addRow(setValueTable(((Parameter) results.get(i)).getCode(), 
					((Parameter) results.get(i)).getValue(), 
					((Parameter) results.get(i)).getDescription()));
		}
	}
	
	private boolean checkNumberRange(Parameter dummy){
		boolean check = false;
		List lstResult = null;
		Parameter detail;
		String[] db = null;
		String[] in = null;
		String query="";
		
		if (dummy.getCode().equals("qcs"))
			query = "('qtreg','qtmulti','qtprio','qtprf','qcsprio','qcsprf')";
		else if (dummy.getCode().equals("qtreg"))
			query = "('qcs','qtmulti','qtprio','qtprf','qcsprio','qcsprf')";
		else if (dummy.getCode().equals("qtmulti"))
			query = "('qtreg','qcs','qtprio','qtprf','qcsprio','qcsprf')";
		else if (dummy.getCode().equals("qtprio"))
			query = "('qtreg','qtmulti','qcs','qtprf','qcsprio','qcsprf')";
		else if (dummy.getCode().equals("qtprf"))
			query = "('qtreg','qtmulti','qcs','qtprio','qcsprio','qcsprf')";
		else if (dummy.getCode().equals("qcsprio"))
			query = "('qtreg','qtmulti','qcs','qtprio','qtprf','qcsprf')";
		else if (dummy.getCode().equals("qcsprf"))
			query = "('qtreg','qtmulti','qcs','qtprio','qtprf','qcsprio')";
		
		
		if (dummy.getCode().equals("qcs") || dummy.getCode().equals("qtreg") || dummy.getCode().equals("qtmulti") || 
			dummy.getCode().equals("qtprio") || dummy.getCode().equals("qtprf") || dummy.getCode().equals("qcsprio") ||
			dummy.getCode().equals("qcsprf")){
			lstResult = getData(" WHERE code IN " + query);
			in = dummy.getValue().split("-");
			if (!lstResult.isEmpty()){				
				for(int intIndex = 0; intIndex < lstResult.size(); intIndex++){
					detail = (Parameter) lstResult.get(intIndex);
					db = detail.getValue().split("-");
					if (Integer.parseInt(in[0]) < Integer.parseInt(in[1])){ //range input ok
						if (Integer.parseInt(in[0]) > Integer.parseInt(db[1]) || //compare to other 
								Integer.parseInt(in[1]) < Integer.parseInt(db[0]))
							check = true;
						else {
							check = false;
							break;
						}
					} 
					else
						check = false;
				}
				return check;
			}
			else
				return true;
		}
		else
			return true;
	}
	
	private List getData(String Where){
		Parameter detail;
		Connection conn = null;
		List lstResult = new ArrayList();
		ResultSet rs = null;
		String[] a = {};
		SQLExecutor se = new SQLExecutor();
		
		try {
			conn = new Connector().getConnection();
			rs = se.selectPreparedStatement(conn, "SELECT * FROM Parameter" + Where, a);
			while (rs.next()){
				detail = new Parameter();
				detail.setCode(rs.getString("code"));
				detail.setValue(rs.getString("value"));
				detail.setDescription(rs.getString("description"));
				lstResult.add(detail);
			}
		}
		catch (SQLException sq) {
			if (conn == null)
//				setMessagePanel(sq.getMessage());
				lblMessage.setText(sq.getMessage());
			else
				sq.printStackTrace();
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
	
	private boolean validateFields(){
		String isRequired = " is required";
		lblMessage.setText("");
		if (!isValid(txtCode.getText().trim().length(), txtCode, "Code" + isRequired )) return false;
		if (!isValid(txtValue.getText().trim().length(), txtValue, "Value" + isRequired )) return false;
		if (!isValid(txtDescription.getText().trim().length(), txtDescription, "Description" + isRequired )) return false;			
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
	
	
	
	
	//----------------------------- table model
	class ParameterTableModel extends DefaultTableModel
    {
        public ParameterTableModel(Object[] title, int rowCount)
        {
            super(title, rowCount);
        }

        public boolean isCellEditable(int row, int column)
        {
            return false;
        }
    }
	
	private Object[] setValueTable(String code, String value, String description){
        ArrayList data = new ArrayList();
        data.add(0, code);
        data.add(1, value);
        data.add(2, description);
     
        return data.toArray();
    }
	
	private void clearFields(){
		txtCode.setText("");
		txtValue.setText("");
		txtDescription.setText("");
	}
	
	private void setEnabledFields (boolean val){
		txtCode.setEnabled(false);
		txtValue.setEnabled(val);
		txtDescription.setEnabled(val);
	}
	//----------------------------------------------------
	
	
	// Listener -------------------------------------------------
	private ActionListener actionListener = new ActionListener(){
		public void actionPerformed(ActionEvent e) {
//			setEnabledFields(false);
			txtValue.setForeground(Color.BLACK);
			setDone(false);
			if(e.getSource().equals(btnAdd)){
				if (btnAdd.getText().equalsIgnoreCase("Add")){
					clearFields();
					setEnabledFields(true);
					btnAdd.setText("Cancel");
					btnEdit.setEnabled(false);
					btnSave.setEnabled(true);
					btnSave.setText("Save");					
					tblAdminParameters.setEnabled(false);
				}else if (btnAdd.getText().equalsIgnoreCase("Cancel")){
					clearFields();
					setEnabledFields(false);
					btnAdd.setText("Add");
					btnEdit.setEnabled(true);
					btnSave.setEnabled(false);
					tblAdminParameters.setEnabled(true);
				}
			}else if(e.getSource().equals(btnEdit)){
				if (btnEdit.getText().equalsIgnoreCase("Edit")){
					txtCode.setText((String) tblAdminParameters.getValueAt(tblAdminParameters.getSelectedRow(), 0));
					txtValue.setText((String) tblAdminParameters.getValueAt(tblAdminParameters.getSelectedRow(), 1));
					txtDescription.setText((String) tblAdminParameters.getValueAt(tblAdminParameters.getSelectedRow(), 2));
					setEnabledFields(true);
					btnEdit.setText("Cancel");
					btnAdd.setEnabled(false);
					btnSave.setEnabled(true);
					btnSave.setText("Update");
					tblAdminParameters.setEnabled(false);
					
					if("password".equalsIgnoreCase(txtCode.getText()) || "passadmin".equalsIgnoreCase(txtCode.getText()) ){
						txtValue.setText("");
						txtValue.setForeground(Color.WHITE);
					}
					
				}else if (btnEdit.getText().equalsIgnoreCase("Cancel")){
//					clearFields();
					setEnabledFields(false);
					btnEdit.setText("Edit");
					btnAdd.setEnabled(true);
					btnSave.setEnabled(false);
					tblAdminParameters.setEnabled(true);
				}
			} else if (e.getSource().equals(btnSave)){
				if (validateFields()){
					try {
						if (btnSave.getText().equalsIgnoreCase("Save")){
							saveData();
						}else if (btnSave.getText().equalsIgnoreCase("Update")){
							if("password".equalsIgnoreCase(txtCode.getText().trim()) || "passadmin".equalsIgnoreCase(txtCode.getText().trim()) ){
								updatePassword(txtCode.getText().trim(),txtValue.getText().trim());								
							}
							else updateData();	
								
						}
					} catch (SQLException e1) {
						lblMessage.setText(e1.getMessage());
					}
					
					if (isDone() == true){
						btnAdd.setText("Add");
						btnEdit.setText("Edit");
						btnAdd.setEnabled(true);
						btnEdit.setEnabled(true);
						tblAdminParameters.setEnabled(true);
						btnSave.setEnabled(false);
					}
					setEnabledFields(false);
				}
			}
		}
	};
		
	private MouseListener mouseListener = new MouseAdapter(){

		public void mouseClicked(MouseEvent e) {
			if (tblAdminParameters.isEnabled() ==  true){
				txtCode.setText((String) tblAdminParameters.getValueAt(tblAdminParameters.getSelectedRow(), 0));
				txtValue.setText(tblAdminParameters.getValueAt(tblAdminParameters.getSelectedRow(), 1).toString());
				txtDescription.setText((String) tblAdminParameters.getValueAt(tblAdminParameters.getSelectedRow(), 2));
				
				//enable editBtn 
//				if ( "branchcode".equalsIgnoreCase(txtCode.getText()) ) btnEdit.setEnabled(true);
//				else btnEdit.setEnabled(false);
			}
		}
	};
	
	// -----------------------------------------------------------
	
	
	
	//database -------------------------------------------------------
		private void updateData() throws SQLException{
		try {
			if (validateFields()){
				se = new SQLExecutor();
				conn = new Connector().getConnection();
				String[] a = {txtCode.getText(),
						txtValue.getText(),
						txtDescription.getText(),
						(String) tblAdminParameters.getValueAt(tblAdminParameters.getSelectedRow(), 0)};
				dtoParam = new Parameter();
				dtoParam.setCode(txtCode.getText());
				dtoParam.setValue(txtValue.getText());
				dtoParam.setDescription(txtDescription.getText());
				if (checkNumberRange(dtoParam)){
					se.deletePreparedStatement(conn, "UPDATE parameter SET code=?,value=?,description=? WHERE code =?", a);
					lblMessage.setText("Parameter successfully updated");
					setDone(true);
					parameterTableModel.setValueAt(txtCode.getText(), tblAdminParameters.getSelectedRow(), 0);
					parameterTableModel.setValueAt(txtValue.getText(), tblAdminParameters.getSelectedRow(), 1);
					parameterTableModel.setValueAt(txtDescription.getText(), tblAdminParameters.getSelectedRow(), 2);
				}
				else
					lblMessage.setText("wrong number range");
			}
		} catch (SQLException e) {
			throw new SQLException(e.getMessage());
		} finally {
			try {
				if (conn !=null)
					conn.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}	
	}
		
	/**
	 * @author JoeN - 2009 Jul 3 16:55:39
	 * @param pwd
	 * <br> untuk update parameter data password
	 */
	private void updatePassword(String code,String pwd){
		Connection conn = null;
		PreparedStatement ps = null;
		String query = "update Parameter " +
						"set value = PASSWORD('"+pwd+"') " +
						"where code = '"+code+"' ";
		System.out.println("update password : "+query);

        try {
        	conn = new Connector().getConnection();
        	ps = conn.prepareStatement(query);
            ps.executeUpdate();
            setDone(true);
            txtValue.setText("");
        }
        catch (SQLException se) {
			if (conn == null) lblMessage.setText(se.getMessage());
			else	se.printStackTrace();
        }
		finally {
			try {
				if (conn !=null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}				
		}		 

	}

		
		
	private void saveData() throws SQLException{
		try {
			if (validateFields()){
				se = new SQLExecutor();
				conn = new Connector().getConnection();
				String[] a = {txtCode.getText(),
						txtValue.getText(),
						txtDescription.getText()};
				dtoParam = new Parameter();
				dtoParam.setCode(txtCode.getText());
				dtoParam.setValue(txtValue.getText());
				dtoParam.setDescription(txtDescription.getText());
				if (checkNumberRange(dtoParam)){
					se.addPreparedStatement(conn, "INSERT INTO parameter VALUES (?,?,?)", a);
					lblMessage.setText("Parameter successfully saved");
					setDone(true);
					parameterTableModel.addRow(setValueTable(txtCode.getText(), txtValue.getText(), txtDescription.getText()));
				}
				else
					lblMessage.setText("wrong number range");
			}
		} catch (NumberFormatException e) {
			throw new NumberFormatException(e.getMessage());
		} catch (SQLException e) {
			throw new SQLException(e.getMessage());
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
	
//----------------------------------------------------------------

	//setter getter
	public boolean isDone() {
		return done;
	}

	public void setDone(boolean done) {
		this.done = done;
	}

	private Parameter dtoParam = null;	
	private FormPanel panel = null;
	private List results = null;
	private boolean done = false;
	private RubeanEditField txtCode = null;
	private RubeanEditField txtValue = null;
	private RubeanEditField txtDescription = null;
	private RubeanPanel extenBottomButtonBar = null;
	private RubeanLabel lblMessage = null;
	private RubeanTable tblAdminParameters = null;
	private RubeanButton btnAdd = null;
	private RubeanButton btnEdit = null;
	private RubeanButton btnSave = null;
	private Connection conn = null;
	private SQLExecutor se = null;	
	private DefaultTableModel parameterTableModel = null;
	private String[] columnTitle = new String[]{"Code", "Value", "Description"};
	
	public boolean isValid = false; 
	final JFrame frame = new JFrame();

	public boolean isValid() {
		return isValid;
	}

	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}

}
