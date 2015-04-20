package com.fqueue.common;

import java.util.Map;

public class ServiceType {

	public static ServiceType getInstance(){
		if (singleton==null){
			singleton = new ServiceType();
		}
		return singleton;
	}

	public final String getTellerReg() {
		return (String) objectKey[0];
	}
	public final String getTellerMulti() {
		return (String) objectKey[1];
	}
	public final String getCustomerService() {
		return (String) objectKey[2];
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
	
	private final Object[] objectKey = new Object[]{"0","1","2"};
	private final Object[] objectVal = new Object[]{"Teller Regular", "Teller Multi", "Customer Service"};
	private Map map = null;
	private static ServiceType singleton = null;

}
