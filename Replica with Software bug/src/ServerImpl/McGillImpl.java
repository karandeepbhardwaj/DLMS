package ServerImpl;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.stream.Collectors;

import org.omg.CORBA.ORB;

import com.comp6231.project.model.BookData;

import ConstantValues.ConstantVal;
import ImplementationSupport.ImplSupport;
import Interface.ServerInterface;

public class McGillImpl implements ServerInterface {
	private ORB orb;
	static HashMap<String, HashMap<String, Integer>> mainMap;
	HashMap<String, Integer> innerMap;
	static HashMap<String, List<String>> queueHashMap = new HashMap<String, List<String>>();
	static HashMap<String, ArrayList<String>> borrowedItemMap = new HashMap<String, ArrayList<String>>();
	static ArrayList<String> arrayList = new ArrayList<String>();

	HashMap<String, String> checkMap = null;
	Integer quantity;
	String itemName = null;
	String oldItemId = null;
	private String responseString;
	//static HashMap<String,Boolean> semaphoreCheck = new HashMap<String, Boolean>();
	public static Logger log = Logger.getLogger(McGillImpl.class.getName());
//	{
//		if (mainMap==null) {
//		mainMap = new HashMap<String, HashMap<String, Integer>>();
//		mainMap.put("mcg0001", new HashMap<String, Integer>());
//		mainMap.put("mcg0002", new HashMap<String, Integer>());
//		mainMap.put("mcg0003", new HashMap<String, Integer>());
//		mainMap.get("mcg0001").put("java", 1);
//		//semaphoreCheck.put("mcg0001",true);
//		mainMap.get("mcg0002").put("ds", 1);
//		//semaphoreCheck.put("mcg0002",true);
//		mainMap.get("mcg0003").put("network", 1);
//		//semaphoreCheck.put("mcg0003",true);
//	}
//	}

	public void setORB(ORB orb_val) {
		orb = orb_val;
	}

	@Override
	public String addItem(String managerID, String itemID, String itemName, int quantity) {
		// TODO Auto-generated method stub
		initLogger(log, managerID);
		if (mainMap == null) {
			mainMap = new HashMap<String, HashMap<String, Integer>>();
		}
		innerMap = mainMap.containsKey(itemID) ? mainMap.get(itemID) : new HashMap<>();
		if (!mainMap.containsKey(itemID)) {
			innerMap.put(itemName, quantity);
		} else {
			//while(!semaphoreCheck.get(itemID));
			//semaphoreCheck.put(itemID,false);

			quantity += innerMap.get(itemName);
			innerMap.put(itemName, quantity);
		}
		mainMap.put(itemID, innerMap);
		log.info("Added Item for manager"+managerID+" "+itemID+" "+itemName+" "+quantity);
		//semaphoreCheck.put(itemID,true);

		if (queueHashMap.containsKey(itemID)) {
			List<String> userIdString = queueHashMap.get(itemID);
			String userId = userIdString.get(0);
			queueHashMap.get(itemID).remove(0);
			if (queueHashMap.get(itemID) == null) {
				queueHashMap.remove(itemID);
			}
			borrowItem(userId, itemID);
		}

		return new String("Item added to the library");
	}

	@Override
	public String addItemError(String managerID, String itemID, String itemName, int quantity) {
		// TODO Auto-generated method stub
		initLogger(log, managerID);
		if (mainMap == null) {
			mainMap = new HashMap<String, HashMap<String, Integer>>();
		}
		innerMap = mainMap.containsKey(itemID) ? mainMap.get(itemID) : new HashMap<>();
		if (!mainMap.containsKey(itemID)) {
			innerMap.put(itemName, quantity);
		} else {
			//while(!semaphoreCheck.get(itemID));
			//semaphoreCheck.put(itemID,false);

			quantity += innerMap.get(itemName);
			innerMap.put(itemName, quantity);
		}
		mainMap.put(itemID, innerMap);
		log.info("Added Item for manager"+managerID+" "+itemID+" "+itemName+" "+quantity);
		//semaphoreCheck.put(itemID,true);

		if (queueHashMap.containsKey(itemID)) {
			List<String> userIdString = queueHashMap.get(itemID);
			String userId = userIdString.get(0);
			queueHashMap.get(itemID).remove(0);
			if (queueHashMap.get(itemID) == null) {
				queueHashMap.remove(itemID);
			}
			borrowItem(userId, itemID);
		}

		return new String("Successful");
	}

	public void shutdown() {
		orb.shutdown(false);
	}

	@Override
	public String removeItem(String managerID, String itemID, int quantity) {
		// TODO Auto-generated method stub
		//while(!semaphoreCheck.get(itemID));
		//semaphoreCheck.put(itemID,false);

		initLogger(log, managerID);

		innerMap = mainMap.containsKey(itemID) ? mainMap.get(itemID) : new HashMap<>();
		List<String> keysSetCollection = innerMap.keySet().stream().collect(Collectors.toList());
		Integer quantityExisted = innerMap.get(keysSetCollection.get(0));

		if (quantity > 0 && innerMap.size() > 0 && quantity <= quantityExisted) {
			System.out.println("Hello" + keysSetCollection);
			Set<String> innerKey = innerMap.keySet();
			String innerKeyString = ((String) innerKey.toArray()[0]);
			quantity = quantityExisted - quantity;
			innerMap.put(innerKeyString, quantity);
			log.info("Item removed" + managerID + " " + itemID + " " + quantity);
			//semaphoreCheck.put(itemID,true);
			return new String("Item quantity decreased successfully");

		} else if (quantity < 0 && mainMap.containsKey(itemID)) {
			mainMap.remove(itemID);
			log.info("Item removed completely" + managerID + " " + itemID + " " + quantity);
			//semaphoreCheck.put(itemID,true);
			return new String("Item removed successfully");
		} else if (quantity > quantityExisted) {
			log.info("quantity greater than existing qunatity");
			//semaphoreCheck.put(itemID,true);
			return "The quantity you entered is incorrect";
		}

		else {
			log.info("Entered quantity is incorrect");
			return new String("Entered quantity is incorrect");
		}

	}

	@Override
	public String listItemAvailability(String managerID) {
		// TODO Auto-generated method stub
		initLogger(log, managerID);
		String result = "";
		if (mainMap == null) {
			result = "No items Available";
			return result;
		} else {
			log.info("list items for " + managerID + " " + mainMap);
			for (Entry<String, HashMap<String, Integer>> bookData : mainMap.entrySet()) {
				 List<String> innerMapData = bookData.getValue().keySet().stream().collect(Collectors.toList());
				result +=bookData.getKey()+" "+innerMapData.get(0)+" "+bookData.getValue().get(innerMapData.get(0))+",";
			}
			return result.substring(0, result.length()-1);
		}
	}

	@Override
	public String borrowItem(String userID, String itemID) {
		// TODO Auto-generated method stub
		initLogger(log, userID);
		if ((!userID.substring(0, 3).toUpperCase().equals("MCG")) && !borrowedItemMap.isEmpty()
				&& borrowedItemMap.containsKey(userID)) {   /*itemID.equals(borrowedItemMap.get(userID))*/
			ArrayList<String> arrayList = borrowedItemMap.get(userID);
			for(String bookIdString : arrayList) {
				if(bookIdString != null && "CON".contains(bookIdString.substring(0, 3).toUpperCase()))
					return "You can borrow only one item";
			}
		}
		String methodsString = "1";
		HashMap<String, Integer> subMap = new HashMap<String, Integer>();
		String bookName = null;
		int qty = 0;
		if (itemID.substring(0, 3).toUpperCase().equals("MCG") && mainMap.containsKey(itemID)) {
			//while(!semaphoreCheck.get(itemID));
			//semaphoreCheck.put(itemID,false);

			subMap = mainMap.get(itemID);
			for (Map.Entry subEntry : subMap.entrySet()) {
				bookName = (String) subEntry.getKey();
				qty = (Integer) subEntry.getValue();
			}

			if (qty == 0 && itemID.substring(0, 3).toUpperCase().equals("MCG") && mainMap.containsKey(itemID)) {
				if (queueHashMap.isEmpty() || !queueHashMap.get(itemID).contains(userID)) {
					//semaphoreCheck.put(itemID, true);
					return "Book not available. Do you want to be added in waiting list?(y/n)";
				} else if (!queueHashMap.isEmpty() && queueHashMap.get(itemID).contains(userID)) {
					//while(!semaphoreCheck.get(itemID));
					//semaphoreCheck.put(itemID,true);

					return "You are already in the queue";
				}
			}

			if (mainMap != null && qty > 0) {
				HashMap<String, Integer> submap = mainMap.get(itemID);
				for (Map.Entry mapEntry : submap.entrySet()) {
					itemName = (String) mapEntry.getKey();
					quantity = (Integer) mapEntry.getValue();
				}

				quantity = quantity - 1;
				submap.put(itemName, quantity);
				mainMap.put(itemID, submap);

				arrayList.add(itemID);
				borrowedItemMap.put(userID, arrayList);

				log.info("borrowed successfully" + userID + " " + itemID);
				//semaphoreCheck.put(itemID,true);

				return "Book issued successfully";
			}
		} else if (!itemID.substring(0, 3).trim().toUpperCase().equals("MCG")) {
			ImplSupport objImplSupport = new ImplSupport();
			try {
				responseString = objImplSupport.ipcConnection(userID, itemID, methodsString, itemName,
						(itemID.substring(0, 3).trim().toUpperCase().equals("CON") ? ConstantVal.CON_SERVER
								: ConstantVal.MON_SERVER),
						oldItemId);
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return responseString;
		}
		return "Something is incorrect.";
	}

	@Override
	public String findItem(String userID, String itemName) {
		// TODO Auto-generated method stub
		HashMap<String, Integer> findItemMap = new HashMap<String, Integer>();
		HashMap<String, Integer> subMap = new HashMap<String, Integer>();
		String response = "";
		String bookName = null;
		String itemID = null;
		int qty = 0;
		initLogger(log, userID);
		String methodsString = "2";
		if (mainMap != null) {
			for (Map.Entry entry : mainMap.entrySet()) {
				itemID = (String) entry.getKey();
				subMap = (HashMap<String, Integer>) entry.getValue();
				for (Map.Entry subEntry : subMap.entrySet()) {
					bookName = (String) subEntry.getKey();
					qty = (Integer) subEntry.getValue();
					if (bookName.equals(itemName.trim())) {
						findItemMap.put(itemID, qty);
						response = itemID + " " + qty;
					}

				}
			}
		}
		log.info("Find Item" + userID + " " + itemName);
		return response;
	}

	@Override
	public String returnItem(String userID, String itemID) {
		// TODO Auto-generated method stub
		//String borrowItemIdString = borrowedItemMap.get(userID);
		String methodsString = "3";

		initLogger(log, userID);
		try {
			if (itemID.substring(0, 3).toUpperCase().equals("MCG")) {
				if (!borrowedItemMap.isEmpty() && borrowedItemMap.containsKey(userID) && borrowedItemMap.get(userID).contains(itemID)) {
					ArrayList<String> string = borrowedItemMap.get(userID);
					//					for(String borrowItemIdString : string) {
					if (string != null && string.contains(itemID)) {
						HashMap<String, Integer> submap = mainMap.get(itemID);
						for (Map.Entry mapEntry : submap.entrySet()) {
							itemName = (String) mapEntry.getKey();
							quantity = (Integer) mapEntry.getValue();
						}
						quantity = quantity + 1;
						submap.put(itemName, quantity);
						mainMap.put(itemID, submap);
						borrowedItemMap.get(userID).remove(itemID);
						if (queueHashMap.containsKey(itemID) && queueHashMap != null) {
							List<String> userIdToBorrow = queueHashMap.get(itemID);
							if (!userIdToBorrow.isEmpty()) {
								String firstUser = userIdToBorrow.get(0);

								queueHashMap.get(itemID).remove(0);
								borrowItem(firstUser, itemID);
							}
						}
					}
					//	}

				} else {
					log.info("item not available in borrow list");
					return "Item not Available in Borrowed List";
				}
			} else {
				ImplSupport objImplSupport = new ImplSupport();

				objImplSupport.ipcConnection(userID, itemID, methodsString, itemName,
						(itemID.substring(0, 3).trim().toUpperCase().equals("MON") ? ConstantVal.MON_SERVER
								: ConstantVal.CON_SERVER),
						oldItemId);
			}
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		log.info("Item return" + userID + " " + itemID);
		return "Book Returned";

	}

	@Override
	public String queueImplementation(String itemId, String userId) {
		// TODO Auto-generated method stub
		try {
			// String borrowItemIdString = borrowedItemMap.get(userId);
			if (itemId.substring(0, 3).toUpperCase().equals("MON")) {
				ImplSupport objImplSupport = new ImplSupport();

				return objImplSupport.ipcConnection(userId, itemId, "4", itemName, ConstantVal.MON_SERVER, oldItemId)
						.trim();

			} else if (itemId.substring(0, 3).toUpperCase().equals("CON")) {
				ImplSupport objImplSupport = new ImplSupport();
				return objImplSupport.ipcConnection(userId, itemId, "4", itemName, ConstantVal.CON_SERVER, oldItemId)
						.trim();
			}
			if (queueHashMap.containsKey(itemId)) {
				queueHashMap.get(itemId).add(userId);
			} else {
				List<String> waitingList = new ArrayList<String>();
				waitingList.add(userId);
				queueHashMap.put(itemId, waitingList);
			}
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "You are added to waiting list.";
	}

	@Override
	public String findItemOtherServers(String userID, String itemName) {
		// TODO Auto-generated method stub
		String responseString = "";
		String methodsString = "2";
		ImplSupport objImplSupport = new ImplSupport();
		String itemID = null;
		try {
			responseString += objImplSupport
					.ipcConnection(userID, itemID, methodsString, itemName, ConstantVal.CON_SERVER, oldItemId).trim();

			responseString += objImplSupport
					.ipcConnection(userID, itemID, methodsString, itemName, ConstantVal.MON_SERVER, oldItemId).trim();

		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return responseString;

	}

	private void initLogger(Logger log, String inputId) {
		FileHandler fileHandler;
		try {
			fileHandler = new FileHandler(System.getProperty("user.dir") + "/LogFiles/" + inputId + ".log", true);
			fileHandler.setFormatter(new SimpleFormatter());
			log.addHandler(fileHandler);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String oldItemReturn(String studentID, String oldItemID, String newItemID) {
		String response = "false";
		String userId = null;
		if (oldItemID.substring(0, 3).equalsIgnoreCase("MCG")) {
			if (borrowedItemMap.get(studentID).contains(oldItemID.trim())) {
				for (Entry<String, ArrayList<String>> setString : borrowedItemMap.entrySet()) {
					userId = setString.getKey();
				}

				//String userId = borrowedItemMap.get(oldItemID);
				if (userId.equals(studentID)) {
					response = "true";
					return response;
				}

			}
		} else if (oldItemID.substring(0, 3).equalsIgnoreCase("MON")
				|| oldItemID.substring(0, 3).equalsIgnoreCase("CON")) {
			ImplSupport objImplSupport = new ImplSupport();
			try {
				response = objImplSupport.ipcConnection(studentID, newItemID, "6", itemName,
						(oldItemID.substring(0, 3).trim().toUpperCase().equals("MON") ? ConstantVal.MON_SERVER
								: ConstantVal.CON_SERVER),
						oldItemID).trim();
				return response;
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return response;

	}

	public String newItemBorrow(String studentID, String oldItemID, String newItemID) {
		String response = "false";
		HashMap<String, Integer> subMap;
		Integer qty = null;
		if ((!studentID.substring(0, 3).toUpperCase().equals("MCG")) && !borrowedItemMap.isEmpty() //newly added line
				&& borrowedItemMap.containsKey(studentID) && borrowedItemMap.containsValue(newItemID)) {   /*itemID.equals(borrowedItemMap.get(userID))*/
			ArrayList<String> arrayList = borrowedItemMap.get(studentID);
			for(String bookIdString : arrayList) {
				if(bookIdString != null && "MCG".contains(bookIdString.substring(0, 3).toUpperCase()))
					return response;
			}
		}

		if (newItemID.substring(0, 3).equalsIgnoreCase("MCG")) {
			if (mainMap.containsKey(newItemID)) {
				subMap = mainMap.get(newItemID);

				for (Map.Entry subEntry : subMap.entrySet()) {
					qty = (Integer) subEntry.getValue();
				}
				if (qty > 0) {
					response = "true";
					return response;
				}else {
					response = "false";
				}

			}
		}

		else if (newItemID.substring(0, 3).equalsIgnoreCase("MON")
				|| newItemID.substring(0, 3).equalsIgnoreCase("CON")) {
			ImplSupport objImplSupport = new ImplSupport();
			try {
				response = objImplSupport.ipcConnection(studentID, newItemID, "7", itemName,
						(newItemID.substring(0, 3).trim().toUpperCase().equals("MON") ? ConstantVal.MON_SERVER
								: ConstantVal.CON_SERVER),
						oldItemID).trim();

			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return response;

	}

	@Override
	public String exchangeItem(String studentID, String newItemID, String oldItemID) {

		// TODO Auto-generated method stub
		String checkoldItemId, newItemId;
		McGillImpl mcGillImpl = new McGillImpl();
		initLogger(log, studentID);
		newItemId = mcGillImpl.newItemBorrow(studentID, oldItemID, newItemID);
		checkoldItemId = mcGillImpl.oldItemReturn(studentID, oldItemID, newItemID);
		if (newItemId.equals("true") && checkoldItemId.equals("true")) {
			returnItem(studentID, oldItemID);
			borrowItem(studentID, newItemID);
			log.info("Item "+newItemID+ "exchanged for "+oldItemID);
			return "Successfully Exchanged";
		}
		log.info("Item not Exchanged");
		return "Item can't exchange";

	}

	public void parseBookInfo(List<BookData> bookDatas) {
		System.out.println("parse");
		for (BookData bookData : bookDatas) {
			if (innerMap==null) {
			innerMap=new HashMap<String,Integer>();
			
			innerMap.put(bookData.getItemName(), bookData.getQuantity());
			}
			else {
				innerMap.put(bookData.getItemName(), bookData.getQuantity());
			}
			mainMap.put(bookData.getItemId(), innerMap);
			
			ArrayList<String> borrowedItemList = (ArrayList<String>) bookData.getBorrowedList();

			borrowedItemMap.put(bookData.getItemId(), borrowedItemList);
			queueHashMap.put(bookData.getItemId(), bookData.getWaitingList());

			System.out.println(mainMap);
			System.out.println(borrowedItemMap);
			System.out.println(queueHashMap);
			
		}
		
	}
}
