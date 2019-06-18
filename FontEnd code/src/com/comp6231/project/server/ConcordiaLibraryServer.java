package com.comp6231.project.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.List;

import com.comp6231.project.constants.LibraryManagementConstants;
import com.comp6231.project.impl.ConcordiaLibraryImpl;
import com.comp6231.project.model.BookData;
import com.comp6231.project.model.MessageData;

public class ConcordiaLibraryServer {

	public static void main(String[] args) {
		ConcordiaLibraryImpl concordiaImpl = new ConcordiaLibraryImpl();
		if(args.length > 0) {
			Runnable dataConsistentImpl = () ->{
				receiveDataConsistence(concordiaImpl);
			};
			new Thread(dataConsistentImpl).start();
		}

		Runnable libraryServerRunnable = () ->{
			handlesRequestFromAnotherServers(concordiaImpl);
		};

		Runnable replicaManagerImpl = () ->{
			handleReplicaRequests(concordiaImpl,args);
		};

		new Thread(replicaManagerImpl).start();
		new Thread(libraryServerRunnable).start();
	}

	private static void handleReplicaRequests(ConcordiaLibraryImpl concordiaImpl, String[] args) {
		try(DatagramSocket socket = new DatagramSocket(LibraryManagementConstants.REPLICA_CONCORDIA_SERVER_PORT)) {
			System.out.println("Concordia Server started...");
			while(true) {
				byte [] message = new byte[1024];
				DatagramPacket recievedDatagramPacket = new DatagramPacket(message, message.length);
				socket.receive(recievedDatagramPacket);
				ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(recievedDatagramPacket.getData()));
				MessageData messageData = (MessageData) inputStream.readObject();
				inputStream.close();
				byte[] byteArray = replicaManagerImpl(messageData, concordiaImpl);
				DatagramPacket reply = new DatagramPacket(byteArray, byteArray.length, recievedDatagramPacket.getAddress(),
						recievedDatagramPacket.getPort());
				socket.send(reply);
			}
		} catch (IOException | ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}

	public static byte[] replicaManagerImpl(MessageData messageData, ConcordiaLibraryImpl concordiaImpl) {
		String response = "";

		switch(messageData.getMethodName()) {

		case LibraryManagementConstants.ADD_ITEM:
			response=concordiaImpl.addItem(messageData.getUserId(), messageData.getItemId(), messageData.getItemName(), messageData.getQuantity());
			break;
		case LibraryManagementConstants.REMOVE_ITEM:
			response=concordiaImpl.removeItem(messageData.getUserId(), messageData.getItemId(), messageData.getQuantity());
			break;
		case LibraryManagementConstants.LIST_ITEM:
			response=concordiaImpl.listItemAvailability(messageData.getUserId());
			break;
		case LibraryManagementConstants.FIND_ITEM:
			response=concordiaImpl.findItem(messageData.getUserId(), messageData.getItemName(), false);
			break;
		case LibraryManagementConstants.BORROW_ITEM:
			response=concordiaImpl.borrowItem(messageData.getUserId(), messageData.getItemId());
			break;
		case LibraryManagementConstants.ADD_TO_WAIT:
			response = concordiaImpl.addToWaitingList(messageData.getUserId(), messageData.getItemId());
			break;
		case LibraryManagementConstants.RETURN_ITEM:
			response=concordiaImpl.returnItem(messageData.getUserId(), messageData.getItemId());
			break;
		case LibraryManagementConstants.EXCHANGE_ITEM:
			response = concordiaImpl.exchangeItem(messageData.getUserId(), messageData.getNewItemId(), messageData.getItemId());
			break;
		case LibraryManagementConstants.CRASHED:
			response = LibraryManagementConstants.I_AM_ALIVE;
			break;
		case LibraryManagementConstants.GET_DATA:
			List<BookData> booksData = concordiaImpl.getConcordiaBooksList();
			try {
				ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
				ObjectOutput objectOutput = new ObjectOutputStream(byteStream);
				objectOutput.writeObject(booksData);
				return byteStream.toByteArray();
			} catch (IOException e) {
				e.printStackTrace();
			}
		default: 
			response="Invalid request";
		}
		return response.getBytes();
	}


	private static void handlesRequestFromAnotherServers(ConcordiaLibraryImpl concordiaLibraryImpl){
		try(DatagramSocket socket = new DatagramSocket(LibraryManagementConstants.CONCORDIA_SERVER_PORT)) {
			System.out.println("Concordia Server started...");
			while(true) {
				byte [] message = new byte[1000];
				DatagramPacket recievedDatagramPacket = new DatagramPacket(message, message.length);
				socket.receive(recievedDatagramPacket);
				String response = concordiaLibraryImpl.handleRequestFromOtherServer(new String(recievedDatagramPacket.getData()));
				DatagramPacket reply = new DatagramPacket(response.getBytes(), response.length(), recievedDatagramPacket.getAddress(),
						recievedDatagramPacket.getPort());
				socket.send(reply);
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	private static void receiveDataConsistence(ConcordiaLibraryImpl concordiaLibraryImpl) {
		try(DatagramSocket socket = new DatagramSocket(LibraryManagementConstants.RECEIVE_DATA_FROM_REPLICA_PORT)){
			byte [] message = new byte[1024];
			DatagramPacket recievedDatagramPacket = new DatagramPacket(message, message.length);
			socket.receive(recievedDatagramPacket);
			ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(recievedDatagramPacket.getData()));
			List<BookData> bookDatas = (List<BookData>) inputStream.readObject();
			concordiaLibraryImpl.parseBookInfo(bookDatas);
			inputStream.close();
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
