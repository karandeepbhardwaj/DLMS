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
import ServerImpl.MontrealImpl;

public class StartMontrealServer {
	
	public static String replicaManagerImpl(MessageData messageData, MontrealImpl montrealImpl) {
		String response = "";
		switch(messageData.getMethodName()) {

		case ConstantVal.ADD_ITEM:
			if (messageData.getErrorCounter() < 3) {
				response=montrealImpl.addItemError(messageData.getUserId(), messageData.getItemId(), messageData.getItemName(), messageData.getQuantity());
			}else {
				response=montrealImpl.addItem(messageData.getUserId(), messageData.getItemId(), messageData.getItemName(), messageData.getQuantity());
			}
			break;
		case ConstantVal.REMOVE_ITEM:
			response=montrealImpl.removeItem(messageData.getUserId(), messageData.getItemId(), messageData.getQuantity());
			break;
		case ConstantVal.LIST_ITEM:
			response=montrealImpl.listItemAvailability(messageData.getUserId());
			break;
		case ConstantVal.FIND_ITEM:
			response=montrealImpl.findItem(messageData.getUserId(), messageData.getItemName());
			break;
		case ConstantVal.BORROW_ITEM:
			response=montrealImpl.borrowItem(messageData.getUserId(), messageData.getItemId());
			break;
		case ConstantVal.RETURN_ITEM:
			response=montrealImpl.returnItem(messageData.getUserId(), messageData.getItemId());
			break;
		case ConstantVal.ADD_TO_WAIT:
			response = montrealImpl.queueImplementation( messageData.getItemId(),messageData.getUserId());
			break;
		case ConstantVal.EXCHANGE_ITEM:
			response=montrealImpl.exchangeItem(messageData.getUserId(), messageData.getNewItemId(), messageData.getItemId());
			break;
		case HostNameIpAddress.CRASHED:
			response = HostNameIpAddress.I_AM_ALIVE;
			break;

		default: 
			response="Invalid request";
		}
		
		return response;
	}
	
	public static void receive(MontrealImpl montrealImpl) {
		DatagramSocket aSocket = null;
		String responseString = "";
		try {
			aSocket = new DatagramSocket(ConstantVal.MON_SERVER);			
			while (true) {
				byte[] buffer = new byte[1000];
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(request);
				String message = new String(request.getData());
				String [] data = message.split(",");
				String userID = data[0].trim();
				String itemID = data[1].trim();
				String itemName = data[3].trim();
				String oldItemID= data[4].trim();
				switch(data[2].trim()) {
				case "1":
					responseString = montrealImpl.borrowItem(userID, itemID);
					break;
				case "2":
					responseString = montrealImpl.findItem(userID, itemName);
					break;
				case "3":
					responseString = montrealImpl.returnItem(userID, itemID);
					break;
				case "4":
					responseString = montrealImpl.queueImplementation(itemID, userID);
					break;
				case "5":
					responseString=montrealImpl.exchangeItem(userID, itemID, oldItemID);
					break;
				case "6":
					responseString = montrealImpl.oldItemReturn(userID, oldItemID, itemID);
					break;
				case "7":
					responseString=montrealImpl.newItemBorrow(userID, oldItemID, itemID);
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

	private static void handleReplicaRequests(MontrealImpl montrealImpl, String[] args) {
		try(DatagramSocket socket = new DatagramSocket(ConstantVal.REPLICA_MON_SERVER)) {
			System.out.println("Montreal Server started...");
			while(true) {
				byte [] message = new byte[1024];
				DatagramPacket recievedDatagramPacket = new DatagramPacket(message, message.length);
				socket.receive(recievedDatagramPacket);
				ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(recievedDatagramPacket.getData()));
				MessageData messageData = (MessageData) inputStream.readObject();
				inputStream.close();
				String response = replicaManagerImpl(messageData, montrealImpl);
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
			MontrealImpl montrealLibraryImpl = new MontrealImpl();
			
			if(args.length > 0) {
				Runnable dataConsistentImpl = () ->{
				receiveDataConsistence(montrealLibraryImpl);
			};
			new Thread(dataConsistentImpl).start();
			}
			Runnable replicaManagerImpl = () ->{
				handleReplicaRequests(montrealLibraryImpl,args);
			};
			
			new Thread(replicaManagerImpl).start();
			
			Runnable task = () -> {
				receive(montrealLibraryImpl);
			};
			Thread thread = new Thread(task);
			thread.start();		
		
			
			System.out.println("Montreal Server ready and waiting ...");

		
		} catch (Exception e) {
			System.err.println("ERROR: " + e);
			e.printStackTrace(System.out);
		}

		System.out.println("Montreal Server Exiting ...");

	}

	private static void receiveDataConsistence(MontrealImpl montrealLibraryImpl) {
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




