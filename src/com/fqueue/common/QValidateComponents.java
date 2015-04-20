/*
 * Created on Feb 27, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.fqueue.common;

import java.awt.Component;

import javax.swing.JComponent;

import com.jeta.forms.components.panel.FormPanel;
import com.rubean.rcms.ui.RubeanEditField;

/**
 * @author pkristia
 *
 * to validate any commons components
 */
public class QValidateComponents {
	
	public static JComponent getComponent(FormPanel panel, String compName, int typeField, int precision){
		final RubeanEditField rubeanEditField = (RubeanEditField)panel.getComponentByName(compName);
		Component comp = panel.getComponentByName(compName);
		
		if (comp instanceof RubeanEditField) {
			rubeanEditField.setGroupingUsed(Boolean.FALSE);
			if(typeField > -1) {
				rubeanEditField.setType(typeField);
			}

			if(precision > 0) {
	            rubeanEditField.setColumns(precision);
	            rubeanEditField.setPrecision(precision);
	        }
			
//			rubeanEditField.addKeyListener(new ComponentKeyListener());
			return rubeanEditField;
		}
		return null;
	}

}
