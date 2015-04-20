package com.fqueue.common;

import java.util.Map;

public class GenerateMonth {
	public static GenerateMonth getInstance(){
		if (singleton==null){
			singleton = new GenerateMonth();
		}
		return singleton;
	}

	public final Map getAllMethod(){
		if (map == null){
			int szArray = objectKey.length;
			for (int x=0;x<szArray;x++){
				map.put(objectKey[x], objectVal[x]);
			}
		}
		return map;
	}
	
	public final Object[] getAllValues(){
		return objectVal;
	}
	public final String getKey(int index){
		return (String) objectKey[index];
	}
	public final String getVal(int index){
		return (String) objectVal[index];
	}
	
	private final Object[] objectKey = new Object[]{"0","1","2","3","4","5","6","7","8","9","10","11","12"};
	private final Object[] objectVal = new Object[]{"January", "February", 
			"March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
	private Map map = null;
	private static GenerateMonth singleton = null;
}
