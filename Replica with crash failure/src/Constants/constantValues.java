package Constants;

import java.net.SocketAddress;

public class constantValues {
	
//	public static final String REPLICA1_IPADDRESS="132.205.4.148";
//	public static final String REPLICA2_IPADDRESS="132.205.4.149";
//	public static final String REPLICA3_IPADDRESS="132.205.4.152";
//	public static final String SEQUENCER_IPADDRESS="132.205.4.152";
//	public static final String REPLICA4_IPADDRESS="";
	
	
	
	
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
	public static final String CRASHED = "CRASHED";
	public static final String I_AM_ALIVE = "I am Alive";
	

	public static final String SOME_THING_WENT_WRONG = "Something went wrong. Please try again!";
	
	
	

	public static final String THREE_CONSECUTIVE_ERRORS = "Three Consecutive Errors";
	public static final String RESULT_ERROR = "Result Error";
	public static final int REPLICA_CONCORDIA_SERVER_PORT = 2001;
	public static final int REPLICA_MCGILL_SERVER_PORT = 2003;
	public static final int REPLICA_MONTREAL_SERVER_PORT = 2005;
	
	public static final String CONCORDIA_CODE = "CON";
	public static final String MCGILL_CODE = "MCG";
	public static final String MONTREAL_CODE = "MON";
	public static final String GET_DATA = "GET_DATA";
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
	public static int CONCORDIA_SERVER_PORT = 1234;
	public static int MCGILL_SERVER_PORT = 5678;
	public static int MONTREAL_SERVER_PORT = 9876;
	public static String TRUE = "true";
	public static String FALSE = "false";

}


