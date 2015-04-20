/*
 * Created on Sep 29, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.fqueue.common;

import java.util.regex.Pattern;

/**
 * @author mishima
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class validateIP {
	public boolean 
    validateAnIpAddressWithRegularExpression(String iPaddress){
        final Pattern IP_PATTERN = 
              Pattern.compile("(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?).(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?).(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?).(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)");
    return IP_PATTERN.matcher(iPaddress).matches();
}}


 