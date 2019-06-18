package ConstantValues;

import java.net.SocketAddress;

public class ConstantVal {

	public static final int CON_SERVER=1348;
	public static final int MCGILL_SERVER=8977;
	public static final int MON_SERVER=1789;
	
	public static final int REPLICA_CON_SERVER=9876;
	public static final int REPLICA_MCGILL_SERVER=8765;
	public static final int REPLICA_MON_SERVER=7654;


	
	public static final String CON_SERVERNAME="ConcordiaServer";
	public static final String MCGILL_SERVERNAME="McgillServer";
	public static final String MON_SERVERNAME="MontrealServer";
	
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
	public static final int TO_REPLICA_STRING_PORT = 1212;
	
	public static final String REPLICA_ONE_NAME = "REPLICA_ONE";
	public static final String REPLICA_TWO_NAME = "REPLICA_TWO";
	public static final String REPLICA_THREE_NAME = "REPLICA_THREE";
	public static final String REPLICA_FOUR_NAME = "REPLICA_FOUR";
	public static final String THREE_CONSECUTIVE_ERRORS = "Three Consecutive Errors";
	public static final String RESULT_ERROR = "Result Error";

	public static final String CONCORDIA_CODE="CON";
	public static final String MONTREAL_CODE="MON";
	public static final String MCGILL_CODE="MCG";
	public static final int RECEIVE_DATA_FROM_REPLICA_PORT = 2010;
}
