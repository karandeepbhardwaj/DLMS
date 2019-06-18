package com.comp6231.project.replica;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import com.comp6231.project.constants.LibraryManagementConstants;
import com.comp6231.project.model.BookData;
import com.comp6231.project.model.MessageData;
import com.comp6231.project.model.ReceivedToFE;
import com.comp6231.project.server.ConcordiaLibraryServer;
import com.comp6231.project.server.McgillLibraryServer;
import com.comp6231.project.server.MontrealLibraryServer;

public class ReplicaManager {
	static int errorCounter = 0;

	public static void receiveFromSequencer() throws ClassNotFoundException {
		System.out.println("Getting the request from Sequencer");
		String responseFromServer = "";
		InetAddress address = null;

		try (MulticastSocket clientSocket = new MulticastSocket(LibraryManagementConstants.MULTICAST_PORT)){
			address = InetAddress.getByName(LibraryManagementConstants.INET_ADDR);
			clientSocket.joinGroup(address);
			while (true) {

				ReceivedToFE receievdToFE = new ReceivedToFE();
				byte[] buffer = new byte[1000];
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				clientSocket.receive(request);
				ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(request.getData()));
				MessageData messageData = (MessageData) inputStream.readObject();
				messageData.setErrorCounter(errorCounter);
				inputStream.close();
				System.out.println(messageData.getUserId()+" "+messageData.getItemId()+" "+messageData.getItemName()+" "+messageData.getQuantity()+" "+messageData.getSequenceCounter()+ " "+
						messageData.getMethodName());
				String userID=messageData.getUserId();

				if(userID.substring(0,3).equalsIgnoreCase(LibraryManagementConstants.CONCORDIA_CODE)) {
					responseFromServer = new String(sendToServer(messageData, LibraryManagementConstants.CONCORDIA_CODE));
				}else if(userID.substring(0,3).equalsIgnoreCase(LibraryManagementConstants.MCGILL_CODE)) {
					responseFromServer = new String(sendToServer(messageData, LibraryManagementConstants.MCGILL_CODE));
				}else if(userID.substring(0,3).equalsIgnoreCase(LibraryManagementConstants.MONTREAL_CODE)) {
					responseFromServer = new String(sendToServer(messageData, LibraryManagementConstants.MONTREAL_CODE));
				}
				System.out.println("Response "+responseFromServer.trim());
				if(!responseFromServer.trim().equals("Message Not received")) {
					receievdToFE.setMessage(responseFromServer);
					receievdToFE.setSequencerCounter(Integer.toString(messageData.getSequenceCounter()));
					receievdToFE.setFromMessage(LibraryManagementConstants.REPLICA2_HOSTNAME);
					int portNumber = LibraryManagementConstants.FRONT_END_PORT;

					sendToFrontEnd(receievdToFE, portNumber);
				}
			}

		}catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
		} 
	}

	private static int getServerPort(String serverCode) {
		switch (serverCode) {
		case LibraryManagementConstants.CONCORDIA_CODE: return LibraryManagementConstants.REPLICA_CONCORDIA_SERVER_PORT;
		case LibraryManagementConstants.MCGILL_CODE: return LibraryManagementConstants.REPLICA_MCGILL_SERVER_PORT;
		case LibraryManagementConstants.MONTREAL_CODE: return LibraryManagementConstants.REPLICA_MONTREAL_SERVER_PORT;
		}
		return 0;
	}

	private static byte[] sendToServer(MessageData messageData, String serverCode) {
		String response = LibraryManagementConstants.SOME_THING_WENT_WRONG;
		try(DatagramSocket socket = new DatagramSocket()) {
			socket.setSoTimeout(1000);
			InetAddress host = InetAddress.getByName(LibraryManagementConstants.REPLICA2_HOSTNAME);
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			ObjectOutput objectOutput = new ObjectOutputStream(byteStream); 
			objectOutput.writeObject(messageData);
			DatagramPacket sendPacket = new DatagramPacket(byteStream.toByteArray(), byteStream.toByteArray().length, host, getServerPort(serverCode));
			socket.send(sendPacket);
			byte [] buffer = new byte[1024];
			DatagramPacket receivedDatagram = new DatagramPacket(buffer, buffer.length);
			socket.receive(receivedDatagram);
			return receivedDatagram.getData();
		}catch(SocketTimeoutException exception) {
			response = "Message Not received";
		}catch (IOException e) {
			e.printStackTrace();
		}
		return response.getBytes();
	}

	public static void sendToFrontEnd(ReceivedToFE receivedToFE,int portNumber) throws IOException {
		try(DatagramSocket socket = new DatagramSocket()) {
			InetAddress host = InetAddress.getByName(LibraryManagementConstants.FRONT_END_HOSTNAME);
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			ObjectOutput objectOutput = new ObjectOutputStream(byteStream); 
			objectOutput.writeObject(receivedToFE);
			System.out.println(byteStream.toByteArray().length);
			DatagramPacket sendPacket = new DatagramPacket(byteStream.toByteArray(), byteStream.toByteArray().length,
					host, LibraryManagementConstants.FRONT_END_PORT);
			socket.send(sendPacket);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Runnable receiveFromSequencer = () ->{
			try {
				receiveFromSequencer();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		};
		new Thread(receiveFromSequencer).start();

		Runnable receiveStringMessages = () -> {
			handleStringMessages();
		};
		new Thread(receiveStringMessages).start();

		Runnable receiveMessageFromReplicas = () -> {
			handleReplicaToReplicaCommunication();
		};
		new Thread(receiveMessageFromReplicas).start();
	}

	private static void handleReplicaToReplicaCommunication() {
		try(DatagramSocket socket = new DatagramSocket(LibraryManagementConstants.REPLICA_TO_REPLICA_PORT)) {
			while(true) {
				byte [] message = new byte[3072];
				DatagramPacket recievedDatagramPacket = new DatagramPacket(message, message.length);
				socket.receive(recievedDatagramPacket);
				String receivedString = new String(recievedDatagramPacket.getData());
				MessageData messageData = new MessageData();
				messageData.setMethodName(LibraryManagementConstants.GET_DATA);
				byte[] receivedObject = sendToServer(messageData, receivedString.trim());
				DatagramPacket returnPacket = new DatagramPacket(receivedObject, receivedObject.length,recievedDatagramPacket.getAddress(), recievedDatagramPacket.getPort());
				socket.send(returnPacket);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void handleStringMessages() {
		try(DatagramSocket socket = new DatagramSocket(LibraryManagementConstants.TO_REPLICA_STRING_PORT)) { 
			while(true) {
				byte [] message = new byte[1000];
				DatagramPacket recievedDatagramPacket = new DatagramPacket(message, message.length);
				socket.receive(recievedDatagramPacket);
				String receivedData = new String(recievedDatagramPacket.getData());
				if(receivedData.trim().equals(LibraryManagementConstants.RESULT_ERROR)) {
					errorCounter++;
				}else if(receivedData.trim().contains(LibraryManagementConstants.CRASHED)){
					String [] crashedReplica = receivedData.split(",");
					String replicaName = crashedReplica[1].trim();
					if(replicaName.equals(LibraryManagementConstants.REPLICA2_HOSTNAME)) {//change according to replica
						checkTheReplicasAndStart();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void checkTheReplicasAndStart() {
		String [] replicaPorts = {LibraryManagementConstants.CONCORDIA_CODE, LibraryManagementConstants.MCGILL_CODE, LibraryManagementConstants.MONTREAL_CODE};
		for(String serverCode : replicaPorts) {
			MessageData messageData = new MessageData();
			messageData.setMethodName(LibraryManagementConstants.CRASHED);
			String response = new String(sendToServer(messageData, serverCode));
			if(!response.equals(LibraryManagementConstants.I_AM_ALIVE)) {
				MessageData data = new MessageData();
				data.setMethodName(LibraryManagementConstants.GET_DATA);
				List<BookData> booksData = sendToReplica(serverCode);
				startTheServer(serverCode);
				sentToDataConsistencePort(booksData, serverCode);
				System.out.println("Send data to Replica");
			}
		}
	}

	private static void sentToDataConsistencePort(List<BookData> booksData, String serverCode) {
		try(DatagramSocket socket = new DatagramSocket()){
			InetAddress host = InetAddress.getByName(LibraryManagementConstants.REPLICA2_HOSTNAME);
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			ObjectOutput objectOutput = new ObjectOutputStream(byteStream);
			objectOutput.writeObject(booksData);
			DatagramPacket sendPacket = new DatagramPacket(byteStream.toByteArray(), byteStream.toByteArray().length,
					host, LibraryManagementConstants.RECEIVE_DATA_FROM_REPLICA_PORT);
			socket.send(sendPacket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
	}


	private static List<BookData> sendToReplica(String serverCode) {
		List<BookData> messageData = new ArrayList<>();
		try (DatagramSocket socket = new DatagramSocket()){
			InetAddress host = InetAddress.getByName(LibraryManagementConstants.REPLICA2_HOSTNAME);
			DatagramPacket sendPacket = new DatagramPacket(serverCode.getBytes(), serverCode.getBytes().length, host, LibraryManagementConstants.REPLICA_TO_REPLICA_PORT);
			socket.send(sendPacket);
			byte [] buffer = new byte[5000];
			DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
			socket.receive(receivePacket);
			ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(receivePacket.getData()));
			messageData = (List<BookData>) inputStream.readObject();
			inputStream.close();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return messageData;
	}

	private static String startTheServer(String serverCode) {
		String [] stringArray = {"CRASH_START"};
		switch (serverCode) {
		case LibraryManagementConstants.CONCORDIA_CODE:ConcordiaLibraryServer.main(stringArray);
		return LibraryManagementConstants.CONCORDIA_CODE;
		case LibraryManagementConstants.MCGILL_CODE:McgillLibraryServer.main(stringArray);
		return LibraryManagementConstants.MCGILL_CODE;
		case LibraryManagementConstants.MONTREAL_CODE:MontrealLibraryServer.main(stringArray);
		return LibraryManagementConstants.MONTREAL_CODE;
		default:
			break;
		}
		return null;
	}
}

