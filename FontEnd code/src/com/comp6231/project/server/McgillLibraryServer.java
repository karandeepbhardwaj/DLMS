package com.comp6231.project.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.List;

import com.comp6231.project.constants.LibraryManagementConstants;
import com.comp6231.project.impl.McgillLibraryImpl;
import com.comp6231.project.model.BookData;
import com.comp6231.project.model.MessageData;

public class McgillLibraryServer {

	public static void main(String[] args) {
		McgillLibraryImpl mcgillLibraryImpl = new McgillLibraryImpl();

		if(args.length > 0) {
			Runnable dataConsistentImpl = () ->{
				receiveDataConsistence(mcgillLibraryImpl);
			};
			new Thread(dataConsistentImpl).start();
		}

		Runnable libraryServerRunnable = () ->{
			handlesRequestFromAnotherServers(mcgillLibraryImpl);
		};

		Runnable replicaManagerImpl = () ->{
			handleReplicaRequests(mcgillLibraryImpl,args);
		};

		new Thread(replicaManagerImpl).start();
		new Thread(libraryServerRunnable).start();
	}

	private static void handleReplicaRequests(McgillLibraryImpl mcgillLibraryImpl, String[] args) {
		try(DatagramSocket socket = new DatagramSocket(LibraryManagementConstants.REPLICA_MCGILL_SERVER_PORT)) {
			System.out.println("Mcgill Server started...");
			while(true) {
				byte [] message = new byte[1024];
				DatagramPacket recievedDatagramPacket = new DatagramPacket(message, message.length);
				socket.receive(recievedDatagramPacket);
				ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(recievedDatagramPacket.getData()));
				MessageData messageData = (MessageData) inputStream.readObject();
				inputStream.close();
				String response = replicaManagerImpl(messageData, mcgillLibraryImpl);
				DatagramPacket reply = new DatagramPacket(response.getBytes(), response.length(), recievedDatagramPacket.getAddress(),
						recievedDatagramPacket.getPort());
				socket.send(reply);
			}
		} catch (IOException | ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}

	public static String replicaManagerImpl(MessageData messageData, McgillLibraryImpl mcgillLibraryImpl) {
		String response = "";

		switch(messageData.getMethodName()) {

		case LibraryManagementConstants.ADD_ITEM:
			response=mcgillLibraryImpl.addItem(messageData.getUserId(), messageData.getItemId(), messageData.getItemName(), messageData.getQuantity());
			break;
		case LibraryManagementConstants.REMOVE_ITEM:
			response=mcgillLibraryImpl.removeItem(messageData.getUserId(), messageData.getItemId(), messageData.getQuantity());
			break;
		case LibraryManagementConstants.LIST_ITEM:
			response=mcgillLibraryImpl.listItemAvailability(messageData.getUserId());
			break;
		case LibraryManagementConstants.FIND_ITEM:
			response=mcgillLibraryImpl.findItem(messageData.getUserId(), messageData.getItemName(), false);
			break;
		case LibraryManagementConstants.BORROW_ITEM:
			response=mcgillLibraryImpl.borrowItem(messageData.getUserId(), messageData.getItemId());
			break;
		case LibraryManagementConstants.ADD_TO_WAIT:
			response = mcgillLibraryImpl.addToWaitingList(messageData.getUserId(), messageData.getItemId());
			break;
		case LibraryManagementConstants.RETURN_ITEM:
			response=mcgillLibraryImpl.returnItem(messageData.getUserId(), messageData.getItemId());
			break;
		case LibraryManagementConstants.EXCHANGE_ITEM:
			response = mcgillLibraryImpl.exchangeItem(messageData.getUserId(), messageData.getNewItemId(), messageData.getItemId());
			break;
		case LibraryManagementConstants.CRASHED:
			response = LibraryManagementConstants.I_AM_ALIVE;
			break;
		case LibraryManagementConstants.GET_DATA:
			List<BookData> booksData = mcgillLibraryImpl.getMcgillBooksList();
		default: 
			response="Invalid request";
		}
		return response;	
	}

	/**
	 * handlesRequestFromAnotherServers method to handle requests from the other servers
	 * @param registry 
	 * @param mcgillLibraryImpl 
	 * @throws IOException
	 */
	private static void handlesRequestFromAnotherServers(McgillLibraryImpl mcgillLibraryImpl){
		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket(LibraryManagementConstants.MCGILL_SERVER_PORT);
			System.out.println("McGill Server started...");
			while(true) {
				byte [] message = new byte[1000];
				DatagramPacket recievedDatagramPacket = new DatagramPacket(message, message.length);
				socket.receive(recievedDatagramPacket);
				String response = mcgillLibraryImpl.handleRequestFromOtherServer(new String(recievedDatagramPacket.getData()));
				DatagramPacket reply = new DatagramPacket(response.getBytes(), response.length(), recievedDatagramPacket.getAddress(),
						recievedDatagramPacket.getPort());
				socket.send(reply);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if(socket != null)
				socket.close();
		}
	}

	private static void receiveDataConsistence(McgillLibraryImpl montrealLibraryImpl) {
		try(DatagramSocket socket = new DatagramSocket(LibraryManagementConstants.RECEIVE_DATA_FROM_REPLICA_PORT)){
			byte [] message = new byte[1024];
			DatagramPacket recievedDatagramPacket = new DatagramPacket(message, message.length);
			socket.receive(recievedDatagramPacket);
			ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(recievedDatagramPacket.getData()));
			List<BookData> bookDatas = (List<BookData>) inputStream.readObject();
			montrealLibraryImpl.parseBookInfo(bookDatas);
			inputStream.close();
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
