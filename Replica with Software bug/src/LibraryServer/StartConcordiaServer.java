package LibraryServer;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.List;

import com.comp6231.project.model.BookData;
import com.comp6231.project.model.MessageData;

import ConstantValues.ConstantVal;
import ConstantValues.HostNameIpAddress;
import ServerImpl.ConcordiaImpl;
import ServerImpl.MontrealImpl;

public class StartConcordiaServer {
	
	public static String replicaManagerImpl(MessageData messageData, ConcordiaImpl concordiaImpl) {
		String response = "";
		
		switch(messageData.getMethodName()) {
		
		case ConstantVal.ADD_ITEM:
			if (messageData.getErrorCounter() < 3) {
				response=concordiaImpl.addItemError(messageData.getUserId(), messageData.getItemId(), messageData.getItemName(), messageData.getQuantity());
			}else {
				response=concordiaImpl.addItem(messageData.getUserId(), messageData.getItemId(), messageData.getItemName(), messageData.getQuantity());
			}
			break;
		case ConstantVal.REMOVE_ITEM:
			response=concordiaImpl.removeItem(messageData.getUserId(), messageData.getItemId(), messageData.getQuantity());
			break;
		case ConstantVal.LIST_ITEM:
			response=concordiaImpl.listItemAvailability(messageData.getUserId());
			break;
		case ConstantVal.FIND_ITEM:
			response=concordiaImpl.findItem(messageData.getUserId(), messageData.getItemName());
			break;
		case ConstantVal.BORROW_ITEM:
			response=concordiaImpl.borrowItem(messageData.getUserId(), messageData.getItemId());
			break;
		case ConstantVal.RETURN_ITEM:
			response=concordiaImpl.returnItem(messageData.getUserId(), messageData.getItemId());
			break;
		case ConstantVal.ADD_TO_WAIT:
			response = concordiaImpl.queueImplementation( messageData.getItemId(),messageData.getUserId());
			break;

		case ConstantVal.EXCHANGE_ITEM:
			response=concordiaImpl.exchangeItem(messageData.getUserId(), messageData.getNewItemId(), messageData.getItemId());
			break;
		case HostNameIpAddress.CRASHED:
			response = HostNameIpAddress.I_AM_ALIVE;
			break;

		default: 
			response="Invalid request";
		}
		return response;
		
	}
	
	public static void receive(ConcordiaImpl concordiaImpl) {
		DatagramSocket aSocket = null;
		try {
			aSocket = new DatagramSocket(ConstantVal.CON_SERVER);
			
			String responseString="";
			while (true) {
				byte[] buffer = new byte[1000];
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(request);
				String message = new String(request.getData());
				String [] data = message.split(",");
				String userID = data[0].trim();
				String itemID = data[1].trim();
				String itemName = data[3].trim();
				String oldItemID = data[4].trim();
				switch(data[2].trim()) {
				case "1":
					responseString=concordiaImpl.borrowItem(userID, itemID);
					break;
				case "2":
					responseString=concordiaImpl.findItem(userID, itemName);
					break;
				case "3":
					responseString=concordiaImpl.returnItem(userID, itemID);
					break;
				case "4":
					responseString = concordiaImpl.queueImplementation(itemID, userID);
					break;
				case "5":
					responseString=concordiaImpl.exchangeItem(userID, itemID, oldItemID);
					break;
				case "6":
					responseString = concordiaImpl.oldItemReturn(userID, oldItemID, itemID);
					break;
				case "7":
					responseString=concordiaImpl.newItemBorrow(userID, oldItemID, itemID);
					break;
				
				}
				DatagramPacket reply = new DatagramPacket(responseString.getBytes(),responseString.length(), request.getAddress(),
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

	private static void handleReplicaRequests(ConcordiaImpl concordiaImpl, String[] args) {
		try(DatagramSocket socket = new DatagramSocket(ConstantVal.REPLICA_CON_SERVER)) {
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


	
	public static void main(String args[]) {
		try{
			
			ConcordiaImpl concordiaObj = new ConcordiaImpl();
			
			if(args.length > 0) {
			Runnable dataConsistentImpl = () ->{
				receiveDataConsistence(concordiaObj);
			};
			new Thread(dataConsistentImpl).start();
			}
			Runnable replicaManagerImpl = () ->{
				handleReplicaRequests(concordiaObj,args);
			};
			
			new Thread(replicaManagerImpl).start();

			Runnable task = () -> {
				receive(concordiaObj);
			};
			Thread thread = new Thread(task);
			thread.start();
	
			System.out.println("Concordia Server ready and waiting ...");
		
		} catch (Exception e) {
			System.err.println("ERROR: " + e);
			e.printStackTrace(System.out);
		}

		System.out.println("Concordia Server Exiting ...");

	}
	private static void receiveDataConsistence(ConcordiaImpl montrealLibraryImpl) {
		try(DatagramSocket socket = new DatagramSocket(ConstantVal.RECEIVE_DATA_FROM_REPLICA_PORT)){
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

