package com.comp6231.project.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.List;

import com.comp6231.project.constants.LibraryManagementConstants;
import com.comp6231.project.impl.MontrealLibraryImpl;
import com.comp6231.project.model.BookData;
import com.comp6231.project.model.MessageData;

public class MontrealLibraryServer {

	public static void main(String[] args) {
		MontrealLibraryImpl montrealLibraryImpl = new MontrealLibraryImpl();

		if(args.length > 0) {
			Runnable dataConsistentImpl = () ->{
				receiveDataConsistence(montrealLibraryImpl);
			};
			new Thread(dataConsistentImpl).start();
		}

		Runnable libraryServerRunnable = () ->{
			handlesRequestFromAnotherServers(montrealLibraryImpl);
		};
		Runnable replicaManagerImpl = () ->{
			handleReplicaRequests(montrealLibraryImpl,args);
		};

		new Thread(replicaManagerImpl).start();
		new Thread(libraryServerRunnable).start();
	}

	private static void handleReplicaRequests(MontrealLibraryImpl montrealLibraryImpl, String[] args) {
		try(DatagramSocket socket = new DatagramSocket(LibraryManagementConstants.REPLICA_MONTREAL_SERVER_PORT)) {
			System.out.println("Montreal Server started...");
			while(true) {
				byte [] message = new byte[1024];
				DatagramPacket recievedDatagramPacket = new DatagramPacket(message, message.length);
				socket.receive(recievedDatagramPacket);
				ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(recievedDatagramPacket.getData()));
				MessageData messageData = (MessageData) inputStream.readObject();
				inputStream.close();
				String response = replicaManagerImpl(messageData, montrealLibraryImpl);
				DatagramPacket reply = new DatagramPacket(response.getBytes(), response.length(), recievedDatagramPacket.getAddress(),
						recievedDatagramPacket.getPort());
				socket.send(reply);
			}
		} catch (IOException | ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}

	public static String replicaManagerImpl(MessageData messageData, MontrealLibraryImpl montrealLibraryImpl) {
		String response = "";

		switch(messageData.getMethodName()) {

		case LibraryManagementConstants.ADD_ITEM:
			response=montrealLibraryImpl.addItem(messageData.getUserId(), messageData.getItemId(), messageData.getItemName(), messageData.getQuantity());
			break;
		case LibraryManagementConstants.REMOVE_ITEM:
			response=montrealLibraryImpl.removeItem(messageData.getUserId(), messageData.getItemId(), messageData.getQuantity());
			break;
		case LibraryManagementConstants.LIST_ITEM:
			response=montrealLibraryImpl.listItemAvailability(messageData.getUserId());
			break;
		case LibraryManagementConstants.FIND_ITEM:
			response=montrealLibraryImpl.findItem(messageData.getUserId(), messageData.getItemName(), false);
			break;
		case LibraryManagementConstants.BORROW_ITEM:
			response=montrealLibraryImpl.borrowItem(messageData.getUserId(), messageData.getItemId());
			break;
		case LibraryManagementConstants.RETURN_ITEM:
			response=montrealLibraryImpl.returnItem(messageData.getUserId(), messageData.getItemId());
			break;
		case LibraryManagementConstants.EXCHANGE_ITEM:
			response = montrealLibraryImpl.exchangeItem(messageData.getUserId(), messageData.getNewItemId(), messageData.getItemId());
			break;
		case LibraryManagementConstants.CRASHED:
			response = LibraryManagementConstants.I_AM_ALIVE;
			break;
		case LibraryManagementConstants.GET_DATA:
			List<BookData> booksData = montrealLibraryImpl.getMontrealBooksList();
		default: 
			response="Invalid request";
		}
		return response;

	}

	private static void handlesRequestFromAnotherServers(MontrealLibraryImpl montrealLibraryImpl){
		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket(LibraryManagementConstants.MONTREAL_SERVER_PORT);
			System.out.println("Montreal Server started...");
			while(true) {
				byte [] message = new byte[1000];
				DatagramPacket recievedDatagramPacket = new DatagramPacket(message, message.length);
				socket.receive(recievedDatagramPacket);
				String response = montrealLibraryImpl.handleRequestFromOtherServer(new String(recievedDatagramPacket.getData()));
				DatagramPacket reply = new DatagramPacket(response.getBytes(), response.length(), recievedDatagramPacket.getAddress(),
						recievedDatagramPacket.getPort());
				socket.send(reply);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(socket != null)
				socket.close();
		}
	}

	private static void receiveDataConsistence(MontrealLibraryImpl montrealLibraryImpl) {
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
