package Server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.List;

import com.comp6231.project.model.BookData;
import com.comp6231.project.model.MessageData;

import Constants.HostNameAddress;
import Constants.constantValues;
import serverImp.conLib;

public class ConcordiaServer{

	public static void receive(conLib implementation) {
		DatagramSocket aSocket = null;
		try {
			aSocket = new DatagramSocket(constantValues.CONCORDIA_SERVER_PORT);
			System.out.println("Concordia Server Started");
			while (true) {
				byte[] buffer = new byte[1000];
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(request);
				String message = new String(request.getData());
				String [] data = message.split("-");
				String userID = data[0].trim();
				String itemID = data[1].trim();
				String itemName = data[3].trim();
				String newItemID = data[4].trim();
				String responseString = "";
				switch(data[2].trim()) {
				case "1": responseString = implementation.borrowItem(userID, itemID);
				break;
				case "2": responseString = implementation.findItem(userID, itemName, true);
				break;
				case "3": responseString = implementation.returnItem(userID, itemID);
				break;
				case "4": responseString = implementation.waitingList(userID, itemID);
				break;
				case "5": responseString = implementation.exchangeItem(userID, itemID, newItemID);
				break;
				case "6": responseString = implementation.bookPresent(userID, itemID, newItemID);
				break;
				case "7": responseString = implementation.bookBorrowed(userID, itemID);
				break;
				}
				DatagramPacket reply = new DatagramPacket(responseString.getBytes(), responseString.length(), request.getAddress(),
						request.getPort());
				aSocket.send(reply);
			}
		}catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
		} finally {
			if (aSocket != null)
				aSocket.close();
		}
	}

	private static void handleReplicaRequests(conLib concordiaImpl, String[] args) {
		try(DatagramSocket socket = new DatagramSocket(constantValues.REPLICA_CONCORDIA_SERVER_PORT)) {
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

	public static String replicaManagerImpl(MessageData messageData, conLib concordiaImpl) {
		String response = "";

		switch(messageData.getMethodName()) {

		case constantValues.ADD_ITEM:
			response=concordiaImpl.addItem(messageData.getUserId(), messageData.getItemId(), messageData.getItemName(), messageData.getQuantity());
			break;
		case constantValues.REMOVE_ITEM:
			response=concordiaImpl.removeItem(messageData.getUserId(), messageData.getItemId(), messageData.getQuantity());
			break;
		case constantValues.LIST_ITEM:
			response=concordiaImpl.listItemAvailability(messageData.getUserId());
			break;
		case constantValues.FIND_ITEM:
			response=concordiaImpl.findItem(messageData.getUserId(), messageData.getItemName(), false);
			break;
		case constantValues.BORROW_ITEM:
			response=concordiaImpl.borrowItem(messageData.getUserId(), messageData.getItemId());
			break;
		case constantValues.ADD_TO_WAIT:
			response = concordiaImpl.waitingList(messageData.getUserId(), messageData.getItemId());
			break;
		case constantValues.RETURN_ITEM:
			response=concordiaImpl.returnItem(messageData.getUserId(), messageData.getItemId());
			break;
		case constantValues.EXCHANGE_ITEM:
			response = concordiaImpl.exchangeItem(messageData.getUserId(), messageData.getItemId(), messageData.getNewItemId());
			break;
		case constantValues.CRASHED:
			response = constantValues.I_AM_ALIVE;
			break;
			//		case constantValues.GET_DATA:
			//			List<BookData> booksData = concordiaImpl.getConcordiaBooksList();
			//			try {
			//				ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			//				ObjectOutput objectOutput = new ObjectOutputStream(byteStream);
			//				objectOutput.writeObject(booksData);
			//				return byteStream.toByteArray();
			//			} catch (IOException e) {
			//				e.printStackTrace();
			//			}
		default: 
			response="Invalid request";

		}
		return response;

	}



	public static void main (String[] args ) {
		conLib conStub = new conLib();

		if(args.length > 0) {
			Runnable dataConsistentImpl = () ->{
				receiveDataConsistence(conStub);
			};
			new Thread(dataConsistentImpl).start();
		}

		Runnable task = () -> {
			receive(conStub);
		};

		Runnable replicaManagerImpl = () ->{
			handleReplicaRequests(conStub,args);
		};

		new Thread(replicaManagerImpl).start();

		Thread thread = new Thread(task);
		thread.start();

	}

	private static void receiveDataConsistence(conLib conStub) {
		try(DatagramSocket socket = new DatagramSocket(HostNameAddress.RECEIVE_DATA_FROM_REPLICA_PORT)){
			byte [] message = new byte[5000];
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