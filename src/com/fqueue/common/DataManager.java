package com.fqueue.common;

import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.mysql.jdbc.Connection;
import com.fqueue.dto.Servicerole;

public class DataManager {
	private SQLExecutor se = new SQLExecutor();
	private Connector con = new Connector();
	private Connection conn = null;
	private String query = null;
	public final String Q_REGISTRASI = "Registrasi";
	public final String Q_KASIR = "Kasir";
	public final String Q_KLINIK_ANAK = "Klinik Anak";
	public final String Q_KLINIK_OBSGYN = "Klinik Obsgyn";
	public final String Q_KLINIK_BEDAH = "Klinik Bedah";
	public final String Q_KLINIK_THT_MATA = "Klinik THT dan Mata";
	public final String Q_KLINIK_SYARAF = "Klinik Syaraf";
	public final String Q_KLINIK_PENYAKIT_DALAM = "Klinik Penyakit Dalam";
	
	public final String STATUS_FINISH_SERVED = "selesai";
	public final String STATUS_NOSHOW = "noshow";
	public final String STATUS_SERVED = "dilayani";
	private final int STATUS_WS_OPEN = 1;
	public final int STATUS_WS_CLOSE = 0;
	public final int STATUS_WS_UNDEFINED = 2;
	public final String START_COUNTER = "0";
	public final String START_STATUS = "-";
	public final String NOT_CALLED = "0";
	
	/*describe all variable code at parameter table*/
	public final String moviePathCode="moviepath";
	public final String muteCode ="sound";
	public final String voicePathCode="voicepath";
	public final String screenCode="screen";
	public final String callhitCode="callhit";
	public final String branchCode ="branchcode";
	public final String paperCode ="paper";
	public final String paperValue = "papervalue";
	public String paperCount ="600"; 
	private final String SQL_SCRIPT= "/com/fqueue/common/"+System.getProperty("script.sql");
	
	
	public DataManager(){
		try {
			conn = con.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void insertData(String[] args, String query){
		try {
			se.addPreparedStatement(conn, query, args);
		} catch (SQLException e) {
			System.out.println("error insert data");
			e.printStackTrace();
		}
	}
	
	public void updateData(String[] args, String query){
		try {
			se.editPreparedStatement(conn, query, args);
		} catch (SQLException e) {
			System.out.println("error update data");
			e.printStackTrace();
		}
	}
	
	public void deleteData(String[] args, String query){
		try {
			se.deletePreparedStatement(conn, query, args);
		} catch (SQLException e) {
			System.out.println("error delete data");
			e.printStackTrace();
		}
	}
	
	public List getData(String[] args, String query){
		ResultSet rs = null;
		List listData = new ArrayList();
		try {
			rs = se.selectPreparedStatement(conn, query, args);
			while (rs.next()){
				listData.add(rs);
			}
		} catch (SQLException e) {
			System.out.println("error get data");
			e.printStackTrace();
		}
		
		return listData;
	}
	
	// QFrontDisplay's queries -----------------------------------------
		
	public HashMap getCountServices(){
		query = "select service_name, count(*) from branchservicedetail group by service_name";
		String args[] = {};
		HashMap mapCount = new HashMap();
		
		try {
			ResultSet rs = se.selectPreparedStatement(conn, query, args);
			while (rs.next())
				mapCount.put(rs.getString("service_name"),rs.getString("count(*)"));	
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return mapCount;		
	}
	
	/**
	 * 
	 * @return the screen type. If return 'standard', means set to standard 4:3 screen.
	 * If return 'wide', means set to wide screen (16:9). 
	 * This value is set in table PARAMETER
	 */

	/**
	 * 
	 * @return max call hit allowed (set in table PARAMETER)
	 */

	/* this function handle for getmoviePath,getVoicePath,getesttimeTeller,getestimatetimeCs,
	 *  getScreen,getBranchCode,getCallHit,getMuteSound 
	 * */
	public String getParameter(String paramCode){
		query = "select value from parameter where code = ?";
		String args[] = {paramCode};
		String parameterValue = "";
		try {
			ResultSet rs = se.selectPreparedStatement(conn, query, args);
			if (rs.next()){
				parameterValue = rs.getString("value");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return parameterValue;
	}
		
	/**
	 * 
	 * @param service
	 * @return the current last queue number on the list
	 */
	
	public int getMaxNo(String service){
		
		query = "Select max(NO) from process " +
		"where SERVICE = ? " +
		"and curdate() = DATE_FORMAT(process.`TIME`,GET_FORMAT(DATE,'ISO')) "; 
		
		String args[] = {service};
		int maxNo = 0;
		try {
			ResultSet rs = se.selectPreparedStatement(conn, query, args);
			while (rs.next()){
				maxNo = rs.getInt("max(NO)");
			}
			HashMap map = new HashMap();
			map = getQMinMax(service);
			if (maxNo == 0 ){
				maxNo = (Integer.parseInt((String) map.get("min"))) - 1;
			}
			
			if (maxNo == Integer.parseInt((String) map.get("max"))){
				query="select no from process "+
				"where SERVICE = ? "+ 
				"and curdate() = DATE_FORMAT(process.`TIME`,GET_FORMAT(DATE,'ISO')) "+ 
				"and time=(select max(time) from process where SERVICE =? "+ 
				"and curdate() = DATE_FORMAT(process.`TIME`,GET_FORMAT(DATE,'ISO')) ) ";
				String args1[] ={service,service};
				ResultSet rs1 = se.selectPreparedStatement(conn, query, args1);
				while(rs1.next()){
					maxNo = rs1.getInt("no");
				}
				if (maxNo == Integer.parseInt((String) map.get("max")))
					maxNo = (Integer.parseInt((String) map.get("min"))) - 1;
				
			}
//			System.out.println("max No "+maxNo);
			return maxNo;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return maxNo;
	}
	
	public int getMaxQueueIndex(){
		query = "Select max(QUEUE_INDEX) from process";
		String args[] = {};
		int maxQueueIndex = 0;
		try {
			ResultSet rs = se.selectPreparedStatement(conn, query, args);
			while (rs.next()){
				maxQueueIndex = rs.getInt("max(QUEUE_INDEX)");	
			}
			return maxQueueIndex;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return maxQueueIndex;
	}
	
	public int getNoOfQueue(String group, String queueNo){
		query = "select count(*) from process where process.SERVICE = ? and NO < ? and STATUS = ? " +
		"and curdate() = DATE_FORMAT(TIME,GET_FORMAT(DATE,'ISO')) ";
		String args[] = {group, queueNo, "-"};
		int noOfQueue = 0;
		try {
			ResultSet rs = se.selectPreparedStatement(conn, query, args);
			while (rs.next()){
				noOfQueue = rs.getInt("count(*)");	
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return noOfQueue;
	}
	
	
	/**
	 * @param code (type of service)
	 * @return the minimum and maximum value of queue number for each service
	 * example: input code = qcs
	 * 			return:	start of queue number = 501
	 * 					end of queue number = 999 
	 * 			(return value depends on value on table PARAMETER)
	 */
	public HashMap getQMinMax(String code){
		query = "select VALUE from parameter where CODE = ?";
		String args[] = {code};
		String minMax;
		try {
			ResultSet rs = se.selectPreparedStatement(conn, query, args);
			while (rs.next()){
				minMax = rs.getString("VALUE");
				String value[] = minMax.split("-");
				HashMap map = new HashMap();
				map.put("min", value[0]);
				map.put("max", value[1]);
				return map;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public int getTotalCounter(String service){
		query = "SELECT count(*) FROM branchservicedetail where service_name = ? ";
		String args[] = {service};
		int totalCounter = 0;
		try {
			ResultSet rs = se.selectPreparedStatement(conn, query, args);
			while (rs.next()){
				totalCounter = rs.getInt("count(*)");	
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return totalCounter;
	}
	
	public List getTimeLeftOnCounterServing(String service){
		query = "SELECT timediff(sysdate(),process.`TIME_LAST_CALL`) as TIMEPASS " +
		"FROM process where curdate() = DATE_FORMAT(TIME,GET_FORMAT(DATE,'ISO')) " +
		"and process.`NO` IN (SELECT process.`NO` FROM process " +
		" 					WHERE process.`SERVICE` = ? " +
		"  					AND process.`STATUS` = ? " +
		"					AND curdate() = DATE_FORMAT(TIME,GET_FORMAT(DATE,'ISO'))) " +
		"and process.`SERVICE` = ? " ;
		String args[] = {service, STATUS_SERVED,service};
		Time timePass = null;
		List listTimeLeft = new ArrayList();
		try {
			ResultSet rs = se.selectPreparedStatement(conn, query, args);
			while (rs.next()){
				timePass = rs.getTime("TIMEPASS");
				listTimeLeft.add(timePass);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return listTimeLeft;
	}
	
	public List getCurrentCounterServing(String service){
		query = "SELECT process.`NO` FROM process " +
		"WHERE process.`SERVICE` = ? AND process.`STATUS` = ? " +
		"AND curdate() = DATE_FORMAT(TIME,GET_FORMAT(DATE,'ISO'))" ;
		String args[] = {service, STATUS_SERVED};
		int totalCounter = 0;
		List listCounterServing = new ArrayList();
		try {
			ResultSet rs = se.selectPreparedStatement(conn, query, args);
			while (rs.next()){
				totalCounter = rs.getInt("process.`NO`");	
				listCounterServing.add(String.valueOf(totalCounter));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return listCounterServing;
	}
	// -----------------------------------------------------------------
	
	
	// Service Role's queries -----------------------------------------------
	public List getAllServices(){
		query = "select service_id, role from servicerole ";
		
		String args[] = {};
		List listServices = new ArrayList();
		Servicerole dto = null;
		try {
			ResultSet rs = se.selectPreparedStatement(conn, query, args);
			while (rs.next()){
				dto = new Servicerole();
				dto.setServiceId(rs.getString("service_id"));
				dto.setRole(rs.getString("role"));
				listServices.add(dto);	
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return listServices;
	}
	
	
	/*
	 * 
	 */
	public String getServices(){
		String screenType="";
		query = "select count(*) as value from branchservicedetail where service_name= 'qtmulti'";

		String args[] = {};
		String formName="";
		try {
			ResultSet rs = se.selectPreparedStatement(conn, query, args);
			while (rs.next()){
				screenType = rs.getString("value");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return screenType ;
	}
	
	public String getServiceRole(String service){
		query = "select role from servicerole where service_id = ?";
		
		String args[] = {service};
		String servicerole = null;
		try {
			ResultSet rs = se.selectPreparedStatement(conn, query, args);
			while (rs.next()){
				servicerole = rs.getString("role");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return servicerole;
	}
	
	// -----------------------------------------------------------------------
	
	
	// Call Display Screen queries -----------------------------------------------
	
	/**
	 * @param ipAddress
	 * @return the counter number of given ip address
	 * 
	 */
	public int getCounterNo(String ipAddress){
		query = "select COUNTER_NO from branchservicedetail where IP_ADDRESS = ?";
		String args[] = {ipAddress};
		int counterNo = 0;
		try {
			ResultSet rs = se.selectPreparedStatement(conn, query, args);
			while (rs.next()){
				counterNo = rs.getInt("COUNTER_NO");	
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return counterNo;
	}
	
	/**
	 * 
	 * @param ipAddress
	 * @return the service name of given ip address
	 */
	public String getServiceName(String ipAddress){
		query = "select SERVICE_NAME from branchservicedetail where IP_ADDRESS = ?";
		String args[] = {ipAddress};
		String serviceName = null;
		try {
			ResultSet rs = se.selectPreparedStatement(conn, query, args);
			while (rs.next()){
				serviceName = rs.getString("SERVICE_NAME");	
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return serviceName;
	}

	/**
	 * 
	 * @return the text to display on bottom of Call Display Screen (set in table PARAMETER)
	 */
	public String getRunningText(){
		query = "select description from parameter where code = ? ";
		
		String args[] = {"runtext"};
		String runText = null;
		try {
			ResultSet rs = se.selectPreparedStatement(conn, query, args);
			while (rs.next()){
				runText = rs.getString("description");	
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return runText;
	}
		
	/**
	 * 
	 * @param counterNo
	 * @return the previous customer who served in this counter
	 */
	public String getQueueBefore(String counterNo){
		query = "SELECT max(NO) FROM process where STATUS = ? " +
		"and curdate() = DATE_FORMAT(process.`TIME`,GET_FORMAT(DATE,'ISO')) " +
		"and COUNTER_NO = ?";
		String args[] = {STATUS_SERVED, counterNo};
		int queueBefore = 0;
		try {
			ResultSet rs = se.selectPreparedStatement(conn, query, args);
			while (rs.next()){
				queueBefore = rs.getInt("max(NO)");	
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return String.valueOf(queueBefore);
	}
		
	/**
	 * 
	 * @param queueNo
	 * @return current total call hit of the customer 
	 */
	public int getCustomerCallHit(String queueNo){
		query = "select CALL_HIT from process where process.`NO` = ? " +
		"and STATUS <> ? and curdate() = DATE_FORMAT(TIME,GET_FORMAT(DATE,'ISO'))";
		String args[] = {queueNo, STATUS_FINISH_SERVED};
		int custCallHit = 0;
		try {
			ResultSet rs = se.selectPreparedStatement(conn, query, args);
			while (rs.next()){
				custCallHit = rs.getInt("CALL_HIT");	
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return custCallHit;
	}
	
	/**
	 * 
	 * @param service
	 * @return the next queue number to call 
	 */
	public Map getNextQueueNo(String service){
		query="select No,service from "+ 
			  "(select No,service,time from process where "+ 
			  "process.`SERVICE` in("+service+") "+ 
			  "and curdate() = DATE_FORMAT(TIME,GET_FORMAT(DATE,'ISO')) "+ 
			  "and process.`COUNTER_NO`=?)as tbl1 inner join "+
			  "(select min(time) as timemin from process where process.`SERVICE` in("+service+") "+ 
			  "and curdate() = DATE_FORMAT(TIME,GET_FORMAT(DATE,'ISO')) "+ 
			  "and process.`COUNTER_NO` =? group by service) as tbl2 "+
			  "on tbl1.time=tbl2.timemin ";  
		
		Map map= new HashMap();
		String counterNo = "0";
		String args[] = {counterNo,counterNo};
		int calledQueueNo = 0;
		try {
			ResultSet rs = se.selectPreparedStatement(conn, query, args);
			while (rs.next()){
				if(!map.containsKey(rs.getString("service"))){
					map.put(rs.getString("service"),Integer.valueOf(rs.getInt("No")));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
//		return calledQueueNo;
		return map;
	}
	
	public int getRecallQueueNo(String service, String counterNo){
		query = "select NO from process where process.`SERVICE` in ("+service+")  " +
		"and curdate() = DATE_FORMAT(TIME,GET_FORMAT(DATE,'ISO')) " +
		"and process.`COUNTER_NO` = ? " +
		"and process.`STATUS` = ?";
		String args[] = {counterNo, STATUS_SERVED};
		int calledQueueNo = 0;
		try {
			ResultSet rs = se.selectPreparedStatement(conn, query, args);
			while (rs.next()){
				calledQueueNo = rs.getInt("NO");	
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return calledQueueNo;
	}
	
	//----------------------------------------------------------------------
	
		
	// Waiting Time Report ---------------------------------------------------------
	public Integer getTotalCustomer(String service, String date, String status, String type){
		if (type.equals("daily")){
			query = "select count(*) from process where service = ? " +
			"and status = ? and " +
			"DATE_FORMAT(?,GET_FORMAT(DATE,'ISO')) = " +
			"DATE_FORMAT(process.`TIME`,GET_FORMAT(DATE,'ISO')) ";
		} else {
			query = "select count(*) from process where service = ? " +
			"and status = ? and " +
			"DATE_FORMAT(process.`TIME`,'%M %Y') = ?";
		}
		
		
		String args[] = {service, status, date};
		int totalCust = 0;
		try {
			ResultSet rs = se.selectPreparedStatement(conn, query, args);
			while (rs.next()){
				totalCust = rs.getInt("count(*)");	
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return Integer.valueOf(totalCust);
	}
	
	public Integer getTotalTicketsTaken(String service, String date, String type){
		if (type.equals("daily")){
			query = "select count(*) from process where service = ? " +
			"and DATE_FORMAT(?,GET_FORMAT(DATE,'ISO')) = " +
			"DATE_FORMAT(process.`TIME`,GET_FORMAT(DATE,'ISO')) ";
		}
		else {
			query = "select count(*) from process where service = ? " +
			"and DATE_FORMAT(process.`TIME`,'%M %Y') = ?";
		}
		
		String args[] = {service, date};
		int totalTicketsTaken = 0;
		try {
			ResultSet rs = se.selectPreparedStatement(conn, query, args);
			while (rs.next()){
				totalTicketsTaken = rs.getInt("count(*)");	
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return Integer.valueOf(totalTicketsTaken);
	}
	//-------------------------------------------------------------
	
	// Daily & Weekly Category Report ----------------------------------------
	public List getOpenWorkstation(String service, String date){
		query = "select perioddaily.`PERIOD_DAILY_ID` as perioddailyid, perioddaily.`PERIOD_DAILY_NAME` as perioddailyname, " +
		"workstation.`COUNTER_NO` as counterno, workstation.`OPEN_STATUS` as openstatus " +
		"from workstation, perioddaily " +
		"where workstation.`PERIOD` = perioddaily.`PERIOD_DAILY_NAME` and " +
		"workstation.`SERVICE_NAME` = ? and " +
		"DATE_FORMAT(workstation.`OPEN_DATE`,GET_FORMAT(DATE,'ISO')) = ? ";
		
		String args[] = {service,date};
		String branchcode = null;
		HashMap map = null;
		List listOpenWS = new ArrayList();
		try {
			ResultSet rs = se.selectPreparedStatement(conn, query, args);
			while (rs.next()){
				map = new HashMap();
				map.put("perioddailyid", Integer.valueOf(rs.getInt("perioddailyid")));
				map.put("perioddailyname", rs.getString("perioddailyname"));
				map.put("counterno", Integer.valueOf(rs.getInt("counterno")));
				map.put("openstatus", Integer.valueOf(rs.getInt("openstatus")));
				listOpenWS.add(map);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return listOpenWS;
	}
	
	/**
	 * 
	 * @return map of period_daily_id and period_daily_name
	 */
	public List getAllPeriodDaily(){
		query = "select * from perioddaily";
		
		String args[] = {};
		HashMap map = null;
		List listPeriodDaily = new ArrayList();
		try {
			ResultSet rs = se.selectPreparedStatement(conn, query, args);
			while (rs.next()){
				map = new HashMap();
				map.put(Integer.valueOf(rs.getInt("PERIOD_DAILY_ID")), rs.getString("PERIOD_DAILY_NAME"));
				listPeriodDaily.add(map);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return listPeriodDaily;
	}
	
	/**
	 * 
	 * @param service
	 * @param period_name
	 * @return open status of workstation based on period, counter no and open date
	 */
	public int getStatusWorkstation(String counterno, String period, String date){
		query = "select open_status from workstation where " +
		"counter_no = ? and " +
		"period = (select period_daily_name from perioddaily where period_daily_id = ?) and " +
		"DATE_FORMAT(workstation.`OPEN_DATE`,GET_FORMAT(DATE,'ISO')) = ? ";
		
		String args[] = {counterno, period, date};
		int openStatus = STATUS_WS_UNDEFINED;
		try {
			ResultSet rs = se.selectPreparedStatement(conn, query, args);
			while (rs.next()){
				openStatus = rs.getInt("open_status");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return openStatus;
	}
	
	/**
	 * 
	 * @param service
	 * @return list of counter no based on service
	 */
	public List getListCounter(String service){
		query = "select counter_no from branchservicedetail where service_name = ?";
		
		String args[] = {service};
		List listCounter = new ArrayList();
		try {
			ResultSet rs = se.selectPreparedStatement(conn, query, args);
			while (rs.next()){
				listCounter.add(String.valueOf(rs.getInt("counter_no")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return listCounter;
	}
	
	public List getNoOfOpenWorkstation(String service, String date){
		query = "select count(*) from workstation where service_name = ? and " +
		"status_open = ? and " +
		"DATE_FORMAT(workstation.`OPEN_DATE`,GET_FORMAT(DATE,'ISO')) = ? ";
		
		String args[] = {service, String.valueOf(STATUS_WS_OPEN), date};
		List listCounter = new ArrayList();
		try {
			ResultSet rs = se.selectPreparedStatement(conn, query, args);
			while (rs.next()){
				listCounter.add(String.valueOf(rs.getInt("counter_no")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return listCounter;
	}
	
	public void insertPaperRow(){
		
		String args[] = {paperCode,paperCount,"print count down"};
		String args2[] = {paperValue,paperCount,"Total size of print paper"};
		Object obj[] = {args,args2};
		String queryinsert = "insert into parameter (CODE,VALUE,DESCRIPTION) values (?,?,?)";
		for(int i=0;i<2;i++){	
			insertData((String[]) obj[i],queryinsert);
		}
	}
	
	public int getSumQueueWait(String service,String strDate){
		query="select count(*) as sumQueue from process where status='-' and service in ("+service+") and DATE_FORMAT(process.`TIME`,GET_FORMAT(DATE,'ISO'))=?";
		int sumQueue=0;
		String args[]={strDate};
		try {
			ResultSet rs = se.selectPreparedStatement(conn, query, args);
			while (rs.next()){
				if(rs.getInt("sumQueue")!=0){
					sumQueue=rs.getInt("sumQueue");
				}else
					sumQueue=0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception ex){
			ex.printStackTrace();
		}
		return sumQueue;
		
	}
	
	public HashMap getSumQueueBranchInfo()throws Exception{
		query="select count(service_name) as sumService, service_name as service from branchservicedetail group by SERVICE_NAME";
		String args[]={};
		HashMap map = new HashMap();
		ResultSet rs =null;
		try {
			rs = se.selectPreparedStatement(conn, query, args);
			while (rs.next()){
				map.put(rs.getString("service"),Integer.valueOf(rs.getInt("sumService")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new Exception(e);
		}finally{
			if(rs!=null)
				rs.close();
			
		}
		return map;
	}
	// ---------------------------------------------------------------
	
	public String getQueueWaitTime(String queueNo,boolean recall) throws Exception {
		
		query = "SELECT TIMEDIFF("+(recall?"time_call":"NOW()")+",time) as 'timediff' FROM PROCESS WHERE queue_index=(SELECT MAX(queue_index) FROM process WHERE CURDATE() = DATE_FORMAT(process.`TIME`,GET_FORMAT(DATE,'ISO')) AND no=?);";
		// String result = "00:00:00";
		String[] args = { queueNo };
		String result = null;
		// DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		ResultSet rs=null;
		try {
			rs = se.selectPreparedStatement(conn, query, args);
			if (rs.next()) {
				result = rs.getString("timediff").substring(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		}finally{
			
			if(rs!=null)
				rs.close();
		}
		return result;
	}
	
}
