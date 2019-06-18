package constants;

import java.net.SocketAddress;

/**
 * 
 * @author Namita Faujdar
 *
 */

public class ConstantValues {
	
	public static final String REPLICA1_HOSTNAME="SENEGAL";//reshma system
	public static final String REPLICA2_HOSTNAME="GHANA"; // satish_system
	public static final String FRONTEND_HOSTNAME="GHANA"; //satish system
	public static final String REPLICA3_HOSTNAME="GUINEA";// namita_system
	public static final String SEQUENCER_HOSTNAME="GUINEA";//namita_system
	public static final String REPLICA4_HOSTNAME="LIBERIA";//karan_system
	
	public static final String CRASHED = "CRASHED";
	public static final String I_AM_ALIVE = "I am Alive";
	 
	public static final String REPLICA1_IPADDRESS="132.205.4.148";
	public static final String REPLICA2_IPADDRESS="132.205.4.149";
	public static final String REPLICA3_IPADDRESS="132.205.4.152";
	public static final String SEQUENCER_IPADDRESS="132.205.4.152";
	public static final String REPLICA4_IPADDRESS="";
	
	public static final int TO_REPLICA_STRING_PORT = 1212;
	
	public static final int REPLICA1_PORT=1210;
	public static final int REPLICA2_PORT=1212;
	public static final int REPLICA3_PORT=1214;
	public static final int REPLICA4_PORT=1216;
	public static final int SEQUNECER_PORT=1218;
	public static final int MULTICAST_PORT=1219;

	
	public static final int CON_SERVER_PORT = 7777;
	public static final int MCG_SERVER_PORT = 6666;
	public static final int MON_SERVER_PORT = 5555;
	
	public static final int REPLICA_CON_PORT = 9876;
	public static final int REPLICA_MCG_PORT = 8765;
	public static final int REPLICA_MON_PORT = 7654;

	public static final String CONCORDIA = "CON";
	public static final String MONTREAL = "MON";
	public static final String MCGILL = "MCG";

	public static final String CON_SERVER_NAME = "Concordia";
	public static final String MON_SERVER_NAME = "Montreal";
	public static final String MCG_SERVER_NAME = "McGill";
	
	
	public static final String ADD_ITEM = "ADD_ITEM";
	public static final String REMOVE_ITEM = "REMOVE_ITEM";
	public static final String LIST_ITEM = "LIST_ITEM";
	
	public static final String FIND_ITEM = "FIND_ITEM";
	public static final String BORROW_ITEM = "BORROW_ITEM";
	public static final String RETURN_ITEM = "RETURN_ITEM";
	public static final String EXCHANGE_ITEM = "EXCHANGE_ITEM";
	
	public static final String ADD_TO_WAIT = "ADD_TO_WAIT";
	
	public final static String INET_ADDR = "224.0.0.3";
	public final static int PORT = 8888;
	
	public static final String REPLICA_ONE_NAME = "REPLICA_ONE";
	public static final String REPLICA_TWO_NAME = "REPLICA_TWO";
	public static final String REPLICA_THREE_NAME = "REPLICA_THREE";
	public static final String REPLICA_FOUR_NAME = "REPLICA_FOUR";
	public static final String THREE_CONSECUTIVE_ERRORS = "Three Consecutive Errors";
	public static final String RESULT_ERROR = "Result Error";
	
	public static final int FRONTEND_PORT=1220;
	
	public static final String SOME_THING_WENT_WRONG = "Something went wrong. Please try again!";
	public static final int REPLICA_TO_REPLICA_PORT = 2006;
	public static final String GET_DATA = "GET_DATA";
	public static final int RECEIVE_DATA_FROM_REPLICA_PORT = 2010;



}
