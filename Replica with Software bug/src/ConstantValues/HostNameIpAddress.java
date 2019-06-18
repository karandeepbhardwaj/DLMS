package ConstantValues;

import java.net.SocketAddress;

public class HostNameIpAddress {

	public static final String REPLICA1_HOSTNAME="SENEGAL";//reshma
	public static final String REPLICA2_HOSTNAME="GHANA";//sathish
	public static final String REPLICA3_HOSTNAME="GUINEA";//namita
	public static final String REPLICA4_HOSTNAME="LIBERIA";//karan
	public static final String SEQUENCER_HOSTNAME="GUINEA"; //namita
	public static final String FRONTEND_HOSTNAME="GHANA"; //satish
	
	public static final String REPLICA1_IPADDRESS="132.205.4.148";
	public static final String REPLICA2_IPADDRESS="132.205.4.149";
	public static final String REPLICA3_IPADDRESS="132.205.4.152";
	public static final String REPLICA4_IPADDRESS="";
	public static final String SEQUENCER_IPADDRESS="";
	
	public static final int REPLICA1_PORT=1210;
	public static final int REPLICA2_PORT=1212;
	public static final int REPLICA3_PORT=1214;
	public static final int REPLICA4_PORT=1216;
	public static final int SEQUNECER_PORT=1218;
	public static final int MULTICAST_PORT=1219;
	public static final int FRONTEND_PORT=1220;
	
	public static final String CRASHED = "CRASHED";
	public static final int REPLICA_TO_REPLICA_PORT = 2006;
	public static final String GET_DATA = "GET DATA";
	public static final String RESULT_ERROR = "Result Error";
	public static final String I_AM_ALIVE = "I am Alive";
}
