package server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.List;

import com.comp6231.project.model.BookData;
import com.comp6231.project.model.MessageData;

import constants.ConstantValues;
import serverInterfaceImplementation.ConcordiaLibraryImplementation;

/**
 * 
 * @author Namita Faujdar
 *
 */

public class ConcordiaServer {

	public static void main(String[] args){

		ConcordiaLibraryImplementation conStub = new ConcordiaLibraryImplementation();

		if(args.length > 0) {
			Runnable dataConsistentImpl = () ->{
				receiveDataConsistence(conStub);
			};
			new Thread(dataConsistentImpl).start();
		}

		Runnable task = () -> {
			receiveRequestsFromOthers(conStub);
		};
		Runnable replicaManagerImpl = () ->{
			handleReplicaRequests(conStub,args);
		};


		Thread thread = new Thread(task);
		thread.start();
		Thread thread1 = new Thread(replicaManagerImpl);
		thread1.start();
	}


	private static void handleReplicaRequests(ConcordiaLibraryImplementation concordiaImpl, String[] args) {
		try(DatagramSocket socket = new DatagramSocket(ConstantValues.REPLICA_CON_PORT)) {
			System.out.println("Concordia Server started...");
			while(true) {
				byte [] message = new byte[1024];
				DatagramPacket recievedDatagramPacket = new DatagramPacket(message, message.length);
				socket.receive(recievedDatagramPacket);
				ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(recievedDatagramPacket.getData()));
				MessageData messageData = (MessageData) inputStream.readObject();
				inputStream.close();
				String response = replicaManagerImpl(messageData, concordiaImpl);
				DatagramPacket reply = new DatagramPacket(response.getBytes(), response.length(), recievedDatagramPacket.getAddress(),
						recievedDatagramPacket.getPort());
				socket.send(reply);
			}
		} catch (IOException | ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}



	public static String replicaManagerImpl(MessageData messageData, ConcordiaLibraryImplementation concordiaImpl) {
		String response = "";

		switch(messageData.getMethodName()) {

		case ConstantValues.ADD_ITEM:
			response=concordiaImpl.addItem(messageData.getUserId(), messageData.getItemId(), messageData.getItemName(), messageData.getQuantity());
			break;
		case ConstantValues.REMOVE_ITEM:
			response=concordiaImpl.removeItem(messageData.getUserId(), messageData.getItemId(), messageData.getQuantity());
			break;
		case ConstantValues.LIST_ITEM:
			response=concordiaImpl.listItemAvailability(messageData.getUserId());
			break;
		case ConstantValues.FIND_ITEM:
			response=concordiaImpl.findItem(messageData.getUserId(), messageData.getItemName(),true);
			break;
		case ConstantValues.BORROW_ITEM:
			response=concordiaImpl.borrowItem(messageData.getUserId(), messageData.getItemId());
			break;
		case ConstantValues.RETURN_ITEM:
			response=concordiaImpl.returnItem(messageData.getUserId(), messageData.getItemId());
			break;
		case ConstantValues.EXCHANGE_ITEM:
			concordiaImpl.exchangeItem(messageData.getUserId(), messageData.getNewItemId(), messageData.getItemId());
			break;
		case ConstantValues.CRASHED:
			response = ConstantValues.I_AM_ALIVE;
			break;
		case ConstantValues.ADD_TO_WAIT:
			response = concordiaImpl.waitingQueueList(messageData.getUserId(), messageData.getItemId());
			break;

		/*case ConstantValues.GET_DATA:
			List<BookData> booksData = concordiaImpl.getDataStore().keySet().stream().;
			try {
				ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
				ObjectOutput objectOutput = new ObjectOutputStream(byteStream);
				objectOutput.writeObject(booksData);
				return byteStream.toByteArray();
			} catch (IOException e) {
				e.printStackTrace();
			}*/
		default: 
			response="Invalid request";
		}
		return response;


	}



	private static void receiveRequestsFromOthers(ConcordiaLibraryImplementation conStub) {
		DatagramSocket aSocket = null;
		try {
			aSocket = new DatagramSocket(ConstantValues.CON_SERVER_PORT);
			System.out.println("Concordia server started.....");
			while (true) {
				byte[] buffer = new byte[1000];
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(request);
				String response = requestsFromOthers(new String(request.getData()), conStub);
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

	public static String requestsFromOthers(String data, ConcordiaLibraryImplementation cLibraryImplementation) {
		String[] receivedDataString = data.split(" ");
		String userId = receivedDataString[0].trim();
		String itemId = receivedDataString[1].trim();
		String methodNumber = receivedDataString[2].trim();
		String itemName = receivedDataString[3].trim();
		String oldItemId = receivedDataString[4].trim();
		switch(methodNumber) {
		case "1": return cLibraryImplementation.findItem(userId, itemName, false);
		case "2": return cLibraryImplementation.borrowItem(userId, itemId); 
		case "3": return cLibraryImplementation.returnItem(userId, itemId); 
		case "4": return cLibraryImplementation.waitingQueueList(userId, itemId);
		case "5": return cLibraryImplementation.exchangeItem(userId, itemId, oldItemId);
		case "6": return cLibraryImplementation.borrowedBookOrNot(userId, itemId, oldItemId);
		case "7": return cLibraryImplementation.bookExistOrNot(userId, itemId, oldItemId);
		}
		return "Incorrect";
	}

	private static void receiveDataConsistence(ConcordiaLibraryImplementation conStub) {
		try(DatagramSocket socket = new DatagramSocket(ConstantValues.RECEIVE_DATA_FROM_REPLICA_PORT)){
			byte [] message = new byte[1024];
			DatagramPacket recievedDatagramPacket = new DatagramPacket(message, message.length);
			socket.receive(recievedDatagramPacket);
			ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(recievedDatagramPacket.getData()));
			List<BookData> bookDatas = (List<BookData>) inputStream.readObject();
			conStub.parseBookInfo(bookDatas);
			inputStream.close();
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
