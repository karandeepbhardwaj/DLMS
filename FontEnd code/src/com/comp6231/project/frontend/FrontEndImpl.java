package com.comp6231.project.frontend;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.omg.CORBA.ORB;

import com.comp6231.project.constants.LibraryManagementConstants;
import com.comp6231.project.model.MessageData;
import com.comp6231.project.model.ReceivedToFE;

import FrontEndIdl.FrontEndPOA;

public class FrontEndImpl extends FrontEndPOA {

	ORB orb;
	static private long replicaOneTimer = 0;
	static private long replicaTwoTimer = 0;
	static private long replicaThreeTimer = 0;
	static private long replicaFourTimer = 0;
	static private int counter=0;

	public void setOrb(ORB orb) {
		this.orb = orb;
	}

	@Override
	public String addItem(String userId, String itemId, String itemName, int quantity) {
		return sendMessageToSequencer(new MessageData().setUserId(userId).setItemId(itemId).setItemName(itemName).setQuantity(quantity).setMethodName(LibraryManagementConstants.ADD_ITEM));
	}

	@Override
	public String listItemAvailability(String managerId) {
		return sendMessageToSequencer(new MessageData().setUserId(managerId).setMethodName(LibraryManagementConstants.LIST_ITEM));
	}

	@Override
	public String removeItem(String userId, String itemId, int quantity) {
		return sendMessageToSequencer(new MessageData().setUserId(userId).setItemId(itemId).setQuantity(quantity).setMethodName(LibraryManagementConstants.REMOVE_ITEM));
	}

	@Override
	public String borrowItem(String userId, String itemId) {
		return sendMessageToSequencer(new MessageData().setUserId(userId).setItemId(itemId).setMethodName(LibraryManagementConstants.BORROW_ITEM));
	}

	@Override
	public String addToWaitingList(String userId, String itemId) {
		return sendMessageToSequencer(new MessageData().setUserId(userId).setItemId(itemId).setMethodName(LibraryManagementConstants.ADD_TO_WAIT));
	}

	@Override
	public String findItem(String userId, String itemName, boolean fromOtherServer) {
		return sendMessageToSequencer(new MessageData().setUserId(userId).setItemName(itemName).setFromOtherServer(false).setMethodName(LibraryManagementConstants.FIND_ITEM));
	}

	@Override
	public String returnItem(String userId, String itemId) {
		return sendMessageToSequencer(new MessageData().setUserId(userId).setItemId(itemId).setMethodName(LibraryManagementConstants.RETURN_ITEM));
	}

	@Override
	public String exchangeItem(String userId, String newItemId, String oldItemId) {
		return sendMessageToSequencer(new MessageData().setUserId(userId).setItemId(oldItemId).setNewItemId(newItemId).setMethodName(LibraryManagementConstants.EXCHANGE_ITEM));
	}

	@Override
	public void shutdown() {
		orb.shutdown(false);
	}

	private String sendMessageToSequencer(MessageData messageData) {
		long startTime = System.currentTimeMillis();
		try(DatagramSocket socket = new DatagramSocket()) {
			InetAddress host = InetAddress.getByName(LibraryManagementConstants.SEQUENCER_HOSTNAME);
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			ObjectOutput objectOutput = new ObjectOutputStream(byteStream); 
			objectOutput.writeObject(messageData);
			DatagramPacket sendPacket = new DatagramPacket(byteStream.toByteArray(), byteStream.toByteArray().length, host, LibraryManagementConstants.SEQUNECER_PORT);
			socket.send(sendPacket);
			return waitForReplyFromReplicas(startTime);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return LibraryManagementConstants.SOME_THING_WENT_WRONG;
	}

	private String waitForReplyFromReplicas(long startTime) {
		List<ReceivedToFE> dataRecieved = new ArrayList<>();
		String messageToClient = LibraryManagementConstants.SOME_THING_WENT_WRONG;
		try(DatagramSocket socket = new DatagramSocket(LibraryManagementConstants.FRONT_END_PORT)) {
			while(true) {
				System.out.println("Timers : "+replicaOneTimer+" "+replicaTwoTimer+" "+replicaThreeTimer+" "+replicaFourTimer);
				byte [] message = new byte[3072];
				DatagramPacket recievedDatagramPacket = new DatagramPacket(message, message.length);
				if(dataRecieved.size() >= 3) {
					socket.setSoTimeout(getTimeOutTimer());
				}
				socket.receive(recievedDatagramPacket);
				ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(recievedDatagramPacket.getData()));
				ReceivedToFE messageData = (ReceivedToFE) inputStream.readObject();
				inputStream.close();
				System.out.println("Message Received From"+ messageData.getFromMessage()+" "+messageData.getMessage());
				checkTheTimer(messageData, startTime);

				dataRecieved.add(messageData);
				System.out.println(messageData.getFromMessage()+" "+messageData.getMessage());
				messageToClient = this.checkMessagesToSendToClient(dataRecieved, startTime);
				if(Objects.nonNull(messageToClient)) {
					return messageToClient;
				}else {
					messageToClient = LibraryManagementConstants.SOME_THING_WENT_WRONG;
				}
			}
		}catch(SocketTimeoutException exception) {
			messageToClient = this.checkMessagesToSendToClient(dataRecieved, startTime);
			if(Objects.nonNull(messageToClient)) {
				return messageToClient;
			}else {
				messageToClient = LibraryManagementConstants.SOME_THING_WENT_WRONG;
			}
		}catch (IOException | ClassNotFoundException exception) {
			exception.printStackTrace();
		}
		return messageToClient;
	}

	private int  getTimeOutTimer() {
		int timerToSend;
		long firstTimers = replicaOneTimer > replicaTwoTimer? replicaOneTimer: replicaTwoTimer;
		long lastTimers = replicaThreeTimer > replicaFourTimer? replicaThreeTimer: replicaFourTimer;
		long timer = firstTimers>lastTimers?firstTimers:lastTimers;
		timerToSend = (int) (timer == 0? 11000: 3 * timer);
		return timerToSend;
	}

	private void checkTheTimer(ReceivedToFE messageData, long startTime) {
		long endTime = System.currentTimeMillis() - startTime;
		System.out.println("Check Timer "+messageData.getFromMessage()+" "+counter++);
		switch (messageData.getFromMessage().toUpperCase()) {
		case LibraryManagementConstants.REPLICA1_HOSTNAME:
			if(endTime > replicaOneTimer)
				replicaOneTimer = endTime;
			break;
		case LibraryManagementConstants.REPLICA2_HOSTNAME:
			if(endTime > replicaTwoTimer)
				replicaTwoTimer = endTime;
			break;
		case LibraryManagementConstants.REPLICA3_HOSTNAME:
			if(endTime > replicaThreeTimer)
				replicaThreeTimer = endTime;
			break;
		case LibraryManagementConstants.REPLICA4_HOSTNAME:
			if(endTime > replicaFourTimer)
				replicaFourTimer = endTime;
			break;

		default:
			break;
		}
	}

	private void informSoftwareBug(ReceivedToFE receivedToFE) {
		String sendMessage = LibraryManagementConstants.RESULT_ERROR;
		System.out.println("Informing software bug to "+ receivedToFE.getFromMessage());
		try (DatagramSocket socket = new DatagramSocket()){
			InetAddress host = InetAddress.getByName(receivedToFE.getFromMessage().toUpperCase());
			DatagramPacket sendPacket = new DatagramPacket(sendMessage.getBytes(), sendMessage.getBytes().length, host, LibraryManagementConstants.TO_REPLICA_STRING_PORT);
			socket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String checkMessagesToSendToClient(List<ReceivedToFE> dataRecieved, long startTime) {
		String response = null;
		String [] replicasNames = {LibraryManagementConstants.REPLICA1_HOSTNAME, LibraryManagementConstants.REPLICA2_HOSTNAME, 
				LibraryManagementConstants.REPLICA3_HOSTNAME, LibraryManagementConstants.REPLICA4_HOSTNAME};
		Map<String, ReceivedToFE> receivedMessages = dataRecieved.stream().map(data -> data)
				.collect(Collectors.toMap(ReceivedToFE::getFromMessage, Function.identity()));
		boolean hasToWait = true;
		if(dataRecieved.size() >= 3) {
			boolean crashinform = false;
			for(int index = 0; index < replicasNames.length; index++) {
				if(!receivedMessages.containsKey(replicasNames[index])) {
					hasToWait  = checkWhetherToWaitForMessage(startTime, replicasNames[index]);
					if(!hasToWait) {
						informReplicasAboutCrash(replicasNames[index]);
						crashinform = true;
					}
				}
			}
			if(dataRecieved.size() >= 4 || (crashinform && dataRecieved.size() == 3)) {//replca count - 1
				Map<String, List<ReceivedToFE>> messagesReceived = dataRecieved.stream().collect(Collectors.groupingBy(ReceivedToFE::getMessage));
				for (Entry<String, List<ReceivedToFE>> message : messagesReceived.entrySet()) {
					System.out.println(message.getKey().trim()+" "+ message.getValue().size());
					if(message.getValue().size() >= 2) {// replicas count - 2
						response = message.getKey();
					}else {
						informSoftwareBug(message.getValue().get(0));
					}
				}
			}
			if(Objects.nonNull(response))
				response = response.trim();
		}
		return response;
	}

	private void informReplicasAboutCrash(String replicasName) {
		System.out.println("Informing crash to replica "+ replicasName);
		String sendMessage = LibraryManagementConstants.CRASHED.concat(",").concat(replicasName);
		try(DatagramSocket socket = new DatagramSocket()) {
			InetAddress host = InetAddress.getByName(replicasName);
			DatagramPacket sendPacket = new DatagramPacket(sendMessage.getBytes(), sendMessage.getBytes().length, host, LibraryManagementConstants.TO_REPLICA_STRING_PORT);
			socket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean checkWhetherToWaitForMessage(long startTime, String replicasName) {
		long timeDifference = System.currentTimeMillis() - startTime;
		System.out.println(replicasName+" "+timeDifference);
		long timer = getReplicaTimer(replicasName);
		if((timer != 0 && timeDifference > 2 * timer) || (timer == 0 && timeDifference > getTimeOutTimer()))
			return false;
		return true;
	}

	private long getReplicaTimer(String replicasName) {
		switch (replicasName) {
		case LibraryManagementConstants.REPLICA1_HOSTNAME: return replicaOneTimer;
		case LibraryManagementConstants.REPLICA2_HOSTNAME: return replicaTwoTimer;
		case LibraryManagementConstants.REPLICA3_HOSTNAME: return replicaThreeTimer;
		case LibraryManagementConstants.REPLICA4_HOSTNAME: return replicaFourTimer;
		default:
			break;
		}
		return 0;
	}

}
