package server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import com.comp6231.project.model.MessageData;

import constants.ConstantValues;
import serverInterfaceImplementation.McgillLibraryImplementation;
import serverInterfaceImplementation.MontrealLibraryImplementation;

/**
 * 
 * @author Namita Faujdar
 *
 */

public class MontrealServer {

	public static void main(String[] args){

		MontrealLibraryImplementation monStub = new MontrealLibraryImplementation();
		
		if(args.length > 0) {
			Runnable dataConsistentImpl = () ->{
				receiveDataConsistence(monStub);
			};
			new Thread(dataConsistentImpl).start();
		}
		Runnable task = () -> {
				receiveRequestsFromOthers(monStub);
		};
		Runnable replicaManagerImpl = () ->{
			handleReplicaRequests(monStub,args);
		};
		
		Thread thread = new Thread(task);
		thread.start();
		Thread thread1 = new Thread(replicaManagerImpl);
		thread1.start();
		}
	
	private static void handleReplicaRequests(MontrealLibraryImplementation monStub, String[] args) {
		try(DatagramSocket socket = new DatagramSocket(ConstantValues.REPLICA_MON_PORT)) {
			System.out.println("Montreal Server started...");
			while(true) {
				byte [] message = new byte[1024];
				DatagramPacket recievedDatagramPacket = new DatagramPacket(message, message.length);
				socket.receive(recievedDatagramPacket);
				ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(recievedDatagramPacket.getData()));
				MessageData messageData = (MessageData) inputStream.readObject();
				inputStream.close();
				String response = replicaManagerImpl(messageData, monStub);
				DatagramPacket reply = new DatagramPacket(response.getBytes(), response.length(), recievedDatagramPacket.getAddress(),
						recievedDatagramPacket.getPort());
				socket.send(reply);
			}
		} catch (IOException | ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public static String replicaManagerImpl(MessageData messageData, MontrealLibraryImplementation monStub) {
		String response = "";
		
		switch(messageData.getMethodName()) {
		
		case ConstantValues.ADD_ITEM:
			response=monStub.addItem(messageData.getUserId(), messageData.getItemId(), messageData.getItemName(), messageData.getQuantity());
			break;
		case ConstantValues.REMOVE_ITEM:
			response=monStub.removeItem(messageData.getUserId(), messageData.getItemId(), messageData.getQuantity());
			break;
		case ConstantValues.LIST_ITEM:
			response=monStub.listItemAvailability(messageData.getUserId());
			break;
		case ConstantValues.FIND_ITEM:
			response=monStub.findItem(messageData.getUserId(), messageData.getItemName(),true);
			break;
		case ConstantValues.BORROW_ITEM:
			response=monStub.borrowItem(messageData.getUserId(), messageData.getItemId());
			break;
		case ConstantValues.RETURN_ITEM:
			response=monStub.returnItem(messageData.getUserId(), messageData.getItemId());
			break;
		case ConstantValues.EXCHANGE_ITEM:
			response = monStub.exchangeItem(messageData.getUserId(), messageData.getNewItemId(), messageData.getItemId());
			break;
		case ConstantValues.ADD_TO_WAIT:
			response = monStub.waitingQueueList(messageData.getUserId(), messageData.getItemId());
			break;
		default: 
			response="Invalid request";
		}
		return response;
		
	}

	private static void receiveRequestsFromOthers(MontrealLibraryImplementation monStub) {
		DatagramSocket aSocket = null;
		try {
			aSocket = new DatagramSocket(ConstantValues.MON_SERVER_PORT);
			System.out.println("Montreal server started.....");
			while (true) {
				byte[] buffer = new byte[1000];
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(request);
				String response = requestsFromOthers(new String(request.getData()), monStub);
				DatagramPacket reply = new DatagramPacket(response.getBytes(), response.length(), request.getAddress(),
						request.getPort());
				aSocket.send(reply);
			}
		} catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
		} finally {
			if (aSocket != null)
				aSocket.close();
		}
	}

	public static String requestsFromOthers(String data, MontrealLibraryImplementation monLibraryImplementation) {
			String[] receivedDataString = data.split(" ");
			String userId = receivedDataString[0].trim();
			String itemId = receivedDataString[1].trim();
			String methodNumber = receivedDataString[2].trim();
			String itemName = receivedDataString[3].trim();
			String oldItemId = receivedDataString[4].trim();
			switch(methodNumber) {
			case "1": return monLibraryImplementation.findItem(userId, itemName, false);
			case "2": return monLibraryImplementation.borrowItem(userId, itemId); 
			case "3": return monLibraryImplementation.returnItem(userId, itemId); 
			case "4": return monLibraryImplementation.waitingQueueList(userId, itemId);
			case "5": return monLibraryImplementation.exchangeItem(userId, itemId, oldItemId);
			case "6": return monLibraryImplementation.borrowedBookOrNot(userId, itemId, oldItemId);
			case "7": return monLibraryImplementation.bookExistOrNot(userId, itemId, oldItemId);
			}
		return "Incorrect";
	}
	
	private static void receiveDataConsistence(MontrealLibraryImplementation monStub) {
		try(DatagramSocket socket = new DatagramSocket(ConstantValues.REPLICA_CON_PORT)) {
			System.out.println("Concordia Server started...");
			while(true) {
				byte [] message = new byte[1024];
				DatagramPacket recievedDatagramPacket = new DatagramPacket(message, message.length);
				socket.receive(recievedDatagramPacket);
				ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(recievedDatagramPacket.getData()));
				MessageData messageData = (MessageData) inputStream.readObject();
				inputStream.close();
				String response = replicaManagerImpl(messageData, monStub);
				DatagramPacket reply = new DatagramPacket(response.getBytes(), response.length(), recievedDatagramPacket.getAddress(),
						recievedDatagramPacket.getPort());
				socket.send(reply);
			}
		} catch (IOException | ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}
	
	}
}