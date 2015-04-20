package com.fqueue.common;

import java.text.DecimalFormat;

public class NumberFormater {

	public String kursFormat(Object mny){
	String resultKurs="-";
	if (mny!=null){
//	DecimalFormat myFormatter = new DecimalFormat("###.##");
	DecimalFormat myFormatter = new DecimalFormat("#,#00.0#"); // 15,999.98
	resultKurs = myFormatter.format(mny); //"###.##" = 15345.99
	} 
	return resultKurs;
	}
}
