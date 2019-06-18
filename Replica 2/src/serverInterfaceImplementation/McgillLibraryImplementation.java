package serverInterfaceImplementation;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Map.Entry;

import com.comp6231.project.model.BookData;

/*import java.util.//logging.FileHandler;
import java.util.//logging.//logger;
import java.util.//logging.SimpleFormatter;
*/import constants.ConstantValues;

/**
 * 
 * @author Namita Faujdar
 *
 */

public class McgillLibraryImplementation {

	private static HashMap<String, HashMap<String, Object>> dataStore = new HashMap<String, HashMap<String,Object>>();
	private static HashMap<String, ArrayList<String>> userBookMapping = new HashMap<String, ArrayList<String>>();
	private static HashMap<String, Queue<String>> waitList = new HashMap<>();
	private static Queue<String> userQueue = new LinkedList<String>();
	private static final String KEY_QUANTITY = "quantity";
	private static final String KEY_NAME = "name";
	private static HashMap<String, Boolean> checkBook = new HashMap<>();
	//private static //logger //log;

//	{
//
//		//item1
//		dataStore.put("MCG1111", new HashMap<String, Object>());
//		dataStore.get("MCG1111").put(KEY_NAME, "Java");
//		dataStore.get("MCG1111").put(KEY_QUANTITY, 5);
//		checkBook.put("MCG1111", true);
//		//item2
//		dataStore.put("MCG1234", new HashMap<String, Object>());
//		dataStore.get("MCG1234").put(KEY_NAME, "DS");
//		dataStore.get("MCG1234").put(KEY_QUANTITY, 6);
//		checkBook.put("MCG1234", true);
//		//item3
//		dataStore.put("MCG2222", new HashMap<String, Object>());
//		dataStore.get("MCG2222").put(KEY_NAME, "CN");
//		dataStore.get("MCG2222").put(KEY_QUANTITY, 4);
//		checkBook.put("MCG2222", true);
//		//item4
//		dataStore.put("MCG2345", new HashMap<String, Object>());
//		dataStore.get("MCG2345").put(KEY_NAME, "CA");
//		dataStore.get("MCG2345").put(KEY_QUANTITY, 3);
//		checkBook.put("MCG2345", true);
//		//item5
//		dataStore.put("MCG3456", new HashMap<String, Object>());
//		dataStore.get("MCG3456").put(KEY_NAME, "AI");
//		dataStore.get("MCG3456").put(KEY_QUANTITY, 7);
//		checkBook.put("MCG3456", true);
//		//item6
//		dataStore.put("MCG6789", new HashMap<String, Object>());
//		dataStore.get("MCG6789").put(KEY_NAME, "ML");
//		dataStore.get("MCG6789").put(KEY_QUANTITY, 2);
//		checkBook.put("MCG6789", true);
//	}

	public McgillLibraryImplementation() {
		super();
		/*//log = //logger.get//logger(McgillLibraryImplementation.class.getName());
		try {
			updateMcgServer//log(//log);
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}*/
	}

	
	public String addItem(String managerID, String itemID, String itemName, int quantity) {
		//log.info("Received request from " + managerID +" to add an item with book id " + itemID + " ,book name "+itemName+
			//	" & quantity "+ quantity);
		
		if (dataStore.containsKey(itemID)) {
			while(!checkBook.get(itemID));
			checkBook.put(itemID, false);
			int oldQuantity = (Integer) dataStore.get(itemID).get(KEY_QUANTITY);
			dataStore.get(itemID).put(KEY_QUANTITY, oldQuantity + quantity);
		} else {
			HashMap<String, Object> data = new HashMap<String, Object>();
			data.put(KEY_NAME, itemName);
			data.put(KEY_QUANTITY, quantity);
			dataStore.put(itemID, data);
		}
		checkBook.put(itemID, true);
		if(!(waitList.isEmpty() || waitList == null) && waitList.containsKey(itemID)) {
			String queue= waitList.get(itemID).peek();   
			if(queue != null) {
				bookAutoAssign(queue, itemID);
				//return "Item assigned to waitlist user.";
			}
		}  
		
		return "Item added to the library";
	}

	@SuppressWarnings("unlikely-arg-type")
	
	public String removeItem(String managerID, String itemID, int quantity) {
		//log.info("Received request from " + managerID +" to remove an item with book id " + itemID +
				//" & quantity "+ quantity);
		while(!checkBook.get(itemID));
		checkBook.put(itemID, false);
		int oldQuantity = (Integer) dataStore.get(itemID).get(KEY_QUANTITY);
		if(quantity < 0) {
			dataStore.remove(itemID);
			//Remove item to user's book mapping
			if(userBookMapping.containsValue(itemID)) {
				userBookMapping.remove(itemID);
			}
			checkBook.put(itemID, true);
			return "Item removed successfully";
		} else if(oldQuantity < quantity){
			checkBook.put(itemID, true);
			return "The quantity you entered is incorrect";
		}else {
			dataStore.get(itemID).put(KEY_QUANTITY, oldQuantity - quantity);
			checkBook.put(itemID, true);
			return "Item quantity decreased successfully";
		}
	}

	
	public String listItemAvailability(String managerID) {
		//log.info("Received request from " + managerID +" to list item");
		String itemId = null;
		String strings = "";
		ArrayList<Object> list = new ArrayList<Object>();
		for(Entry<String, HashMap<String, Object>> key: dataStore.entrySet()) {
			itemId = key.getKey();
			HashMap<String, Object> values = key.getValue();
			for(Entry<String, Object> subKey : values.entrySet()) {
				list.add(subKey.getValue());
			}
			strings += itemId.concat(" ").concat(list.get(1).toString()).concat(" ").concat(list.get(0).toString()).concat(",");
			list = new ArrayList<Object>();
		}
		return strings.substring(0, strings.length()-1);
	}

	
	public String waitingQueueList(String userId, String itemId) {
		
		if(itemId.substring(0, 3).equals("CON")) {
			return requestToOtherServers(userId,itemId,4, null, ConstantValues.CON_SERVER_PORT, null);
		}else if(itemId.substring(0, 3).equals("MON")) {
			return requestToOtherServers(userId,itemId,4, null, ConstantValues.MON_SERVER_PORT, null);
		}
		
		//log.info("Received request from " + userId +" to add in waitlist against book id "+itemId);
		if(((userQueue.isEmpty() || userQueue == null) && (waitList == null || waitList.isEmpty())) ||
				!waitList.get(itemId).contains(userId)) {
			userQueue.add(userId);
			waitList.put(itemId, userQueue);
			return "You are added to waiting list.";
		}else if(waitList.containsKey(itemId)){
			Queue<String> queue = waitList.get(itemId);
			for(int i=0; i < queue.size(); i++) {
				if(queue.contains(userId)) {
					break;
				}
			}
			return "You are already in waiting list.";
		}
		return "Book has been removed by Library Manager";
	}

	
	public String borrowItem(String userID, String itemID) {
		//log.info("Received request from " + userID +" for borrowing a book with book id "+itemID);
		
		if(!userID.substring(0,3).equals(ConstantValues.MCGILL) && 
				(userBookMapping != null || !userBookMapping.isEmpty()) && userBookMapping.containsKey(userID)) {
			
					ArrayList<String> name = userBookMapping.get(userID);
					for (String bookItemId : name) {
						if(bookItemId!=null && ConstantValues.MCGILL.contains(bookItemId.substring(0, 3))) {
							return "You cannot borrow more than 1 book";
						}
					}
		}
		
		if(itemID.substring(0, 3).equals(ConstantValues.MCGILL)) {
			if(!dataStore.containsKey(itemID)) return "Book doesn't exist";
			while(!checkBook.get(itemID));
			checkBook.put(itemID, false);

			int quantity = (Integer) dataStore.get(itemID).get(KEY_QUANTITY);
			if (quantity > 0 && dataStore.containsKey(itemID)) {
				quantity--;

				// Update Quantity
				dataStore.get(itemID).put(KEY_QUANTITY, quantity);

				// Add item to user's book id mapping
				if(userBookMapping == null || userBookMapping.isEmpty() || !userBookMapping.containsKey(userID)) {
					ArrayList<String> bookIds = new ArrayList<String>();
					bookIds.add(itemID);
					userBookMapping.put(userID, bookIds);
				}else if(userBookMapping.get(userID).contains(itemID)){
					return "You have already borrowed this book"; 
				}else if (userBookMapping.containsKey(userID)) {
					userBookMapping.get(userID).add(itemID);
				} 

				checkBook.put(itemID, true);
				return "Book issued successfully";
			} else if(quantity == 0) {
				if((userBookMapping != null || !userBookMapping.isEmpty()) && userBookMapping.containsKey(userID) &&
						userBookMapping.get(userID).contains(itemID)) {
					checkBook.put(itemID, true);
					return "You have already borrowed this book"; 
				}else {
					checkBook.put(itemID, true);
					return "Book not available. Do you want to be added in waiting list?(y/n)";
				}
			}else {
				checkBook.put(itemID, true);
				return "Book doesn't exist";
			}
		}else {

			return requestToOtherServers(userID, itemID, 2,null, 0, null);
		}
	}

	
	public String findItem(String userID, String itemName, boolean flag) { 
		//log.info("Received request from " + userID +" for finding a book with book name "+itemName);
		
			String valueString = null ;
			Integer quantity = null;
			String itemId = null;
			String resultString = "";
			for (String key : dataStore.keySet()) {
				itemId = key;
				valueString = (String) dataStore.get(key).get(KEY_NAME); 
				quantity = (Integer)dataStore.get(key).get(KEY_QUANTITY);
				if (itemName.equalsIgnoreCase(valueString)) {
					resultString += itemId.concat(" ").concat(Integer.toString(quantity));
				}
			}
			
			if(flag) {
			resultString += requestToOtherServers(userID, null, 1, itemName, ConstantValues.MON_SERVER_PORT, null).trim();
			resultString += requestToOtherServers(userID, null, 1,itemName,ConstantValues.CON_SERVER_PORT, null).trim();
			}
			return resultString; 
	}

	@SuppressWarnings("unlikely-arg-type")
	
	public String returnItem(String userID, String itemID) {
		//log.info("Received request from " + userID +" for returning a book with book id "+itemID);
		//while(!checkBook.get(itemID));
		if(itemID.substring(0, 3).equals(ConstantValues.MCGILL)) {
			//checkBook.put(itemID, false);
			if(!userID.equals(userBookMapping.containsKey(userID)) && !userBookMapping.get(userID).contains(itemID)) {
				return "Borrower should return the book.";
			}
			int quantity = (Integer) dataStore.get(itemID).get(KEY_QUANTITY);
			quantity++;

			// Update Quantity
			dataStore.get(itemID).put(KEY_QUANTITY, quantity);
			userBookMapping.get(userID).remove(itemID);

			//checkBook.put(itemID, true);
			if(!(waitList.isEmpty() || waitList == null) && waitList.containsKey(itemID)) {
				String queue= waitList.get(itemID).peek();   
				if(queue != null) {
					bookAutoAssign(queue, itemID);
					//return "Item assigned to waitlist user.";
				}
			} 

			return "Book Returned";
		}else{

			return requestToOtherServers(userID, itemID, 3,null, 0, null);
		}
	}

	private void bookAutoAssign(String userId, String itemID){
		//log.info("Book auto assigning");
		int qty = (Integer)dataStore.get(itemID).get(KEY_QUANTITY);
		
		while(waitList.size() != 0 && qty != 0) {
			qty--;
			dataStore.get(itemID).put(KEY_QUANTITY, qty);
			userQueue.poll();
			ArrayList<String> bookIds = new ArrayList<String>();
			bookIds.add(itemID);
			userBookMapping.put(userId, bookIds);
			if(userQueue == null || userQueue.isEmpty()) {
				break;
			}else {
				waitList.put(itemID,userQueue);
			}
		}
	}

	public String borrowedBookOrNot(String userId, String oldItemID, String newItemID) {
		return userBookMapping != null && !userBookMapping.isEmpty() && userBookMapping.containsKey(userId) &&
				userBookMapping.get(userId).contains(oldItemID) ? "true" : "false";
	}
	
	public String bookExistOrNot(String userID, String newItemID, String oldItemID) {  
		int quantity = 0;
		if(!userID.substring(0,3).equals(ConstantValues.MCGILL) && 
				(userBookMapping != null || !userBookMapping.isEmpty()) && userBookMapping.containsKey(userID)) {
			
					ArrayList<String> name = userBookMapping.get(userID);
					String borrowedBookId = null;
					for (String bookItemId : name) {
						if(bookItemId.contains(ConstantValues.MCGILL)) {
							borrowedBookId = bookItemId;
							break;
						}
					}
					if(borrowedBookId == null || (borrowedBookId != null && oldItemID.equals(borrowedBookId))) {
						return "true";
					}else {
						return "false";
					}
		}else{
			if(dataStore.containsKey(newItemID)) {
				//put semaphore here
				//while(!checkBook.get(newItemID));
				//checkBook.put(newItemID, false);
				for (String key : dataStore.keySet()) {
					quantity = (Integer)dataStore.get(key).get(KEY_QUANTITY);
					if (newItemID.equals(key) && quantity > 0) {
						return "true";
					}
				}
			}
		}
		return "false";
	}
	
	
	public String exchangeItem(String userId, String newItemID, String oldItemID) {
		//log.info("Received request from " + userId +" for exchanging "+ oldItemID+" with "+newItemID);
		String borrowedFlag = "false";
		String existFlag = "false";
		String returnBook = null;
		String bBook = null;
		if(oldItemID.substring(0, 3).equals(ConstantValues.MCGILL)) {
			borrowedFlag = borrowedBookOrNot(userId, oldItemID, newItemID);
		}else {
			borrowedFlag = requestToOtherServers(userId, oldItemID, 6,null, 0, newItemID);
		}
		if(newItemID.substring(0, 3).equals(ConstantValues.MCGILL)) {
			existFlag = bookExistOrNot(userId, newItemID, oldItemID);
		}else {
			existFlag = requestToOtherServers(userId, newItemID, 7,null, 0, oldItemID);
		}
		if("true".equals(borrowedFlag.trim()) && "true".equals(existFlag.trim())) {
			returnBook = returnItem(userId, oldItemID);
			bBook = borrowBook(userId, newItemID);
			System.out.println("Response "+returnBook+" "+ bBook);
			//checkBook.put(newItemID, true);
			return "Successfully Exchanged";
		}
		//checkBook.put(newItemID, true);
		return "Item can't exchange";
	}
	
	public String borrowBook(String userID, String itemID) {
		if(itemID.substring(0, 3).equals(ConstantValues.MCGILL)) {
			if(!dataStore.containsKey(itemID)) return "Book doesn't exist";
			//while(!checkBook.get(itemID));
			//checkBook.put(itemID, false);

			int quantity = (Integer) dataStore.get(itemID).get(KEY_QUANTITY);
			if (quantity > 0 && dataStore.containsKey(itemID)) {
				quantity--;

				// Update Quantity
				dataStore.get(itemID).put(KEY_QUANTITY, quantity);

				// Add item to user's book id mapping
				if(userBookMapping == null || userBookMapping.isEmpty() || !userBookMapping.containsKey(userID)) {
					ArrayList<String> bookIds = new ArrayList<String>();
					bookIds.add(itemID);
					userBookMapping.put(userID, bookIds);
				}else if(userBookMapping.get(userID).contains(itemID)){
					return "You have already borrowed this book"; 
				}else if (userBookMapping.containsKey(userID)) {
					userBookMapping.get(userID).add(itemID);
				} 

				//checkBook.put(itemID, true);
				return "Book issued!";
			} else if(quantity == 0) {
				if((userBookMapping != null || !userBookMapping.isEmpty()) && userBookMapping.containsKey(userID)) {
					//checkBook.put(itemID, true);
					if(userBookMapping.get(userID).contains(itemID)) {
					return "You have already borrowed this book"; 
					}else {
						return "";
					}
				}else {
					//checkBook.put(itemID, true);
					return "Book not available!"+"\n"+
							"Do you want to lent the book once available? Press 1 for yes, 2 for no: ";
				}
			}else {
				//checkBook.put(itemID, true);
				return "Book doesn't exist";
			}
		}else {
				return requestToOtherServers(userID, itemID, 2,null, 0, null);
		}
	}

	/*public void updateMcgServer//log(//logger //log) throws SecurityException, IOException {
		FileHandler fileHandler = new FileHandler(System.getProperty("user.dir")+"///logger/"+"mcgserver"+".//log", true);
		//log.addHandler(fileHandler);
		fileHandler.setFormatter(new SimpleFormatter());
	}*/
	
	private static int serverPortSelection(String str) {
		str = str.substring(0, 3);
		if (str.equals(ConstantValues.CONCORDIA)) {
			return ConstantValues.CON_SERVER_PORT;
		} else if (str.equals(ConstantValues.MCGILL)) {
			return ConstantValues.MCG_SERVER_PORT;
		} else if (str.equals(ConstantValues.MONTREAL)) {
			return ConstantValues.MON_SERVER_PORT;
		}
		return 0;
	}

	
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((dataStore == null) ? 0 : dataStore.hashCode());
		result = prime * result + ((userBookMapping == null) ? 0 : userBookMapping.hashCode());
		result = prime * result + ((waitList == null) ? 0 : waitList.hashCode());
		return result;
	}

	
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		McgillLibraryImplementation other = (McgillLibraryImplementation) obj;
		if (dataStore == null) {
			if (other.dataStore != null)
				return false;
		} else if (!dataStore.equals(other.dataStore))
			return false;
		if (userBookMapping == null) {
			if (other.userBookMapping != null)
				return false;
		} else if (!userBookMapping.equals(other.userBookMapping))
			return false;
		if (waitList == null) {
			if (other.waitList != null)
				return false;
		} else if (!waitList.equals(other.waitList))
			return false;
		return true;
	}

	public String requestToOtherServers(String userID, String itemId, int serverNumber,String itemName, int serPort, String oldItemID){
		//log.info("Requesting other server from mcgill ");
		int serverPort;
		if(itemId != null) {
			serverPort = serverPortSelection(itemId);
		}else {
			serverPort = serPort;
		}
		String stringServer = Integer.toString(serverNumber);
		DatagramSocket aSocket = null;
		String response = null;
		String bookName = itemName != null ? itemName : "Default";
		String bookId = itemId != null ?itemId :"Default";
		String oldBookId = oldItemID != null ?oldItemID :"Default";

		try {
			aSocket = new DatagramSocket();
			String message = userID.concat(" ").concat(bookId).concat(" ").concat(stringServer).concat(" ").concat(bookName).concat(" ").concat(oldBookId);
			InetAddress host = InetAddress.getByName("localhost");
			DatagramPacket sendPacket = new DatagramPacket(message.getBytes(), message.length(), host, serverPort);
			aSocket.send(sendPacket);
			//log.info("Request send " + sendPacket.getData());
			byte [] receiveBuffer = new byte[1500];
			DatagramPacket recievedPacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
			aSocket.receive(recievedPacket);
			response = new String(recievedPacket.getData());
			//log.info("Reply received" + response);
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if (aSocket != null)
				aSocket.close();
		}
		return response;
	}
	
	public void parseBookInfo(List<BookData> bookDatas) {
		System.out.println("parse");
		for (BookData bookData : bookDatas) {
			
				//innerMap.put(bookData.getItemName(), bookData.getQuantity());
			//bookData.getBorrowedList().stream().map(userQueue::add);
			
			dataStore.put(bookData.getItemId(), new HashMap<String, Object>());
			dataStore.get(bookData.getItemId()).put(KEY_NAME, bookData.getItemName());
			dataStore.get(bookData.getItemId()).put(KEY_QUANTITY, bookData.getQuantity());
			
			ArrayList<String> borrowedItemList = (ArrayList<String>) bookData.getBorrowedList();

			userBookMapping.put(bookData.getItemId(), borrowedItemList);
			Queue<String> waitingListQueue = new LinkedList<>();
			bookData.getWaitingList().stream().map(waitingListQueue::add);
			waitList.put(bookData.getItemId(), waitingListQueue);

			System.out.println(dataStore);
			System.out.println(userBookMapping);
			System.out.println(waitList);
			
		}
		
	}
}
