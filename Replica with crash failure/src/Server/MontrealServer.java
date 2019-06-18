package Server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.List;

import com.comp6231.project.model.BookData;
import com.comp6231.project.model.MessageData;

import Constants.HostNameAddress;
import Constants.constantValues;
import serverImp.monLib;

public class MontrealServer {

	public static void receive(monLib implementation) {
		DatagramSocket aSocket = null;
		try {
			aSocket = new DatagramSocket(constantValues.MONTREAL_SERVER_PORT);
			System.out.println("Montreal Server Started");
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
	
	private static void handleReplicaRequests(monLib monLib, String[] args) {
		try(DatagramSocket socket = new DatagramSocket(constantValues.REPLICA_MONTREAL_SERVER_PORT)) {
			System.out.println("Montreal Server started...");
			while(true) {
				byte [] message = new byte[1024];
				DatagramPacket recievedDatagramPacket = new DatagramPacket(message, message.length);
				socket.receive(recievedDatagramPacket);
				ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(recievedDatagramPacket.getData()));
				MessageData messageData = (MessageData) inputStream.readObject();
				inputStream.close();
				String response = replicaManagerImpl(messageData, monLib);
				DatagramPacket reply = new DatagramPacket(response.getBytes(), response.length(), recievedDatagramPacket.getAddress(),
						recievedDatagramPacket.getPort());
				socket.send(reply);
			}
		} catch (IOException | ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public static String replicaManagerImpl(MessageData messageData, monLib monLib) {
		String response = "";
		
		switch(messageData.getMethodName()) {
		
		case constantValues.ADD_ITEM:
			response=monLib.addItem(messageData.getUserId(), messageData.getItemId(), messageData.getItemName(), messageData.getQuantity());
			break;
		case constantValues.REMOVE_ITEM:
			response=monLib.removeItem(messageData.getUserId(), messageData.getItemId(), messageData.getQuantity());
			break;
		case constantValues.LIST_ITEM:
			response=monLib.listItemAvailability(messageData.getUserId());
			break;
		case constantValues.FIND_ITEM:
			response=monLib.findItem(messageData.getUserId(), messageData.getItemName(), false);
			break;
		case constantValues.BORROW_ITEM:
			response=monLib.borrowItem(messageData.getUserId(), messageData.getItemId());
			break;
		case constantValues.ADD_TO_WAIT:
			response = monLib.waitingList(messageData.getUserId(), messageData.getItemId());
			break;
		case constantValues.RETURN_ITEM:
			response=monLib.returnItem(messageData.getUserId(), messageData.getItemId());
			break;
		case constantValues.EXCHANGE_ITEM:
			response = monLib.exchangeItem(messageData.getUserId(), messageData.getItemId(), messageData.getNewItemId());
			break;
		case constantValues.CRASHED:
			response = constantValues.I_AM_ALIVE;
			break;
		default: 
			response="Invalid request";
		}
		return response;
		
	}



	public static void main (String[] args ) {
			monLib monStub = new monLib();
			
			if(args.length > 0) {
				Runnable dataConsistentImpl = () ->{
					receiveDataConsistence(monStub);
				};
				new Thread(dataConsistentImpl).start();
			}
			Runnable task = () -> {
				receive(monStub);
			};
			Runnable replicaManagerImpl = () ->{
				handleReplicaRequests(monStub,args);
			};
			
			new Thread(replicaManagerImpl).start();
			Thread thread = new Thread(task);
			thread.start();
	}
	
	private static void receiveDataConsistence(monLib monStub) {
		try(DatagramSocket socket = new DatagramSocket(HostNameAddress.RECEIVE_DATA_FROM_REPLICA_PORT)){
			byte [] message = new byte[5000];
			DatagramPacket recievedDatagramPacket = new DatagramPacket(message, message.length);
			socket.receive(recievedDatagramPacket);
			ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(recievedDatagramPacket.getData()));
			List<BookData> bookDatas = (List<BookData>) inputStream.readObject();
			monStub.parseBookInfo(bookDatas);
			inputStream.close();
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}