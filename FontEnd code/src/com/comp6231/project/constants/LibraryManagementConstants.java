package com.comp6231.project.constants;

public class LibraryManagementConstants {
	
	public static final String REPLICA1_HOSTNAME="SENEGAL";//reshma system
	public static final String REPLICA2_HOSTNAME="GHANA"; // satish_system
	public static final String FRONT_END_HOSTNAME="GHANA"; // satish_system
	public static final String REPLICA3_HOSTNAME="GUINEA";// namita_system
	public static final String SEQUENCER_HOSTNAME="GUINEA";//namita_system
	public static final String REPLICA4_HOSTNAME="LIBERIA";//karan_system
	
	
//	public static final String REPLICA1_IPADDRESS="132.205.4.148";
//	public static final String REPLICA2_IPADDRESS="132.205.4.149";
//	public static final String REPLICA3_IPADDRESS="132.205.4.152";
//	public static final String SEQUENCER_IPADDRESS="132.205.4.152";
//	public static final String REPLICA4_IPADDRESS="";
	
	public static final int TO_REPLICA_STRING_PORT = 1212;
	public static final int SEQUNECER_PORT=1218;
	public static final int MULTICAST_PORT = 1219;
	public static final int FRONT_END_PORT = 1220;
	public static final int RECEIVE_DATA_FROM_REPLICA_PORT = 2010;
	
	public final static String INET_ADDR = "224.0.0.3";
	public final static int PORT = 8888;

	
	public static final String ADD_ITEM = "ADD_ITEM";
	public static final String REMOVE_ITEM = "REMOVE_ITEM";
	public static final String LIST_ITEM = "LIST_ITEM";
	public static final String FIND_ITEM = "FIND_ITEM";
	public static final String BORROW_ITEM = "BORROW_ITEM";
	public static final String RETURN_ITEM = "RETURN_ITEM";
	public static final String EXCHANGE_ITEM = "EXCHANGE_ITEM";
	public static final String ADD_TO_WAIT = "ADD_TO_WAIT";
	public static final String GET_DATA = "GET_DATA";
	

	public static final String SOME_THING_WENT_WRONG = "Something went wrong. Please try again!";
	
	
	public static final String CRASHED = "CRASHED";
	public static final String FRONT_END = "FRONT_END";
	public static final String THREE_CONSECUTIVE_ERRORS = "Three Consecutive Errors";
	public static final String RESULT_ERROR = "Result Error";
	public static final int REPLICA_CONCORDIA_SERVER_PORT = 2000;
	public static final int REPLICA_MCGILL_SERVER_PORT = 2002;
	public static final int REPLICA_MONTREAL_SERVER_PORT = 2004;
	public static final int REPLICA_TO_REPLICA_PORT = 2006;
	
	public static final String CONCORDIA_CODE = "CON";
	public static final String MCGILL_CODE = "MCG";
	public static final String MONTREAL_CODE = "MON";
	public static final String I_AM_ALIVE = "I am Alive";
	public static String USER_CODE = "U";
	public static String MANAGER_CODE = "M";
	public static String LOGGER_FOLDER="/logs";
	public static String CONCORDIA_INITAL_LOAD_FILE = "/resources/ConcordiaLibrary.txt";
	public static String MCGGILL_INITAL_LOAD_FILE = "/resources/McgillLibrary.txt";
	public static String MONTREAL_INITAL_LOAD_FILE = "/resources/MontrealLibrary.txt";
	public static String CONCORDIA_SERVER_LOG_FILE = "/log/concordia_server_log.log";
	public static String MCGILL_SERVER_LOG_FILE = "/log/mcgill_server_log.log";
	public static String MONTREAL_SERVER_LOG_FILE = "/log/montreal_server_log.log";
	public static String LOG_FOLDER = "/log/";
	public static String CONCORDIA_SERVER = "CONCORDIA_SERVER";
	public static String MCGILL_SERVER = "MCGILL_SERVER";
	public static String MONTREAL_SERVER = "MONTREAL_SERVER";
	public static String WAITING_LIST_MESSAGE="Book not available. Do you want to be added in waiting list?(y/n)";
	public static int CONCORDIA_SERVER_PORT = 5555;
	public static int MCGILL_SERVER_PORT = 6666;
	public static int MONTREAL_SERVER_PORT = 7777;
	public static String TRUE = "true";
	public static String FALSE = "false";

}
