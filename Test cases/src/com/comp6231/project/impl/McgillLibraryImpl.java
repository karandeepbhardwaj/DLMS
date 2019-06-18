package com.comp6231.project.impl;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.comp6231.project.constants.LibraryManagementConstants;
import com.comp6231.project.model.BookData;

public class McgillLibraryImpl {
	
	private static Map<String, BookData> mcgillBooksData;
	LibraryImplementationHelper implementationHelper = new LibraryImplementationHelper();
	
	public McgillLibraryImpl(){
		super();
		try {
			mcgillBooksData = implementationHelper.loadLibraryData(System.getProperty("user.dir")+LibraryManagementConstants.MCGGILL_INITAL_LOAD_FILE,
					System.getProperty("user.dir")+LibraryManagementConstants.MCGILL_SERVER_LOG_FILE);
		} catch (IOException e) {
			System.out.println("Unknow error while starting the mcgill server");
		}
	}
	
	public String addItem(String managerId, String itemId, String itemName, int quantity) {
		implementationHelper.insertLog(managerId+" : Added "+itemId+" : "+itemName+" : "+ " : "+quantity);
		String response =  implementationHelper.addBookToLibrary(mcgillBooksData, itemId, itemName, quantity);
		implementationHelper.insertLog(managerId+" : Server Response Added "+response);
		return response;
	}
	
	public String listItemAvailability(String managerId) {
		implementationHelper.insertLog(managerId+" : Listed Items");
		String response = implementationHelper.listItemAvailable(mcgillBooksData);
		implementationHelper.insertLog(managerId+" : Listed Items :"+ response);
		return response;
	}
	
	public String borrowItem(String userId, String itemId) {
		String response;
		implementationHelper.insertLog(userId+" Borrow : "+itemId);
		boolean isFromOtherServers = userId.subSequence(0, 3).equals(LibraryManagementConstants.MCGILL_CODE) ? false : true;
		if(itemId.substring(0, 3).equals(LibraryManagementConstants.MCGILL_CODE))
			response = implementationHelper.borrowItem(mcgillBooksData, userId, itemId, isFromOtherServers);
		else
			response = implementationHelper.requestOtherLibraryServers(userId, itemId, 1, null);
		implementationHelper.insertLog(userId+" : Borrow Response"+" : "+itemId+" : "+response);
		return response;
	}
	
	public String addToWaitingList(String userId, String itemId) {
		String response;
		implementationHelper.insertLog(userId+": Added to waiting list for : "+itemId);
		if(itemId.substring(0, 3).equals(LibraryManagementConstants.MCGILL_CODE))
			response = implementationHelper.addToWaitingList(mcgillBooksData, userId, itemId);
		else
			response = implementationHelper.requestOtherLibraryServers(userId, itemId, 4, null);
		implementationHelper.insertLog(userId+": Added to waiting list for : "+itemId+" "+response);
		return response;
	}
	
	public String findItem(String userId, String itemName, boolean fromOtherServer) {
		String response = "";
		implementationHelper.insertLog(userId+" : Find : "+itemName);
		response += implementationHelper.findItem(mcgillBooksData, userId, itemName);
		if(!fromOtherServer) {
			response += implementationHelper.requestOtherLibraryServers(userId,itemName,3,LibraryManagementConstants.CONCORDIA_CODE);
			response += implementationHelper.requestOtherLibraryServers(userId,itemName,3,LibraryManagementConstants.MONTREAL_CODE);
			response = response.substring(0, response.length() - 1);
		}
		implementationHelper.insertLog(userId+"Find Response : "+ response);
		return response;
	}
	
	public String returnItem(String userId, String itemId) {
		String response;
		implementationHelper.insertLog(userId+" : Returned "+" : "+itemId);
		if(itemId.substring(0, 3).equals(LibraryManagementConstants.MCGILL_CODE))
			response = implementationHelper.returnItem(mcgillBooksData, userId, itemId);
		else
			response = implementationHelper.requestOtherLibraryServers(userId, itemId, 2, null);
		implementationHelper.insertLog(userId+" : Returned Response"+" : "+response);
		return response;
	}
	
	/**
	 * handleRequestFromOtherServer methods calls the respective methods by parsing the data from other servers
	 * @param dataFromAnotherServer
	 * @return
	 * @throws IOException 
	 * @throws SecurityException 
	 */
	public String handleRequestFromOtherServer(String dataFromAnotherServer) throws SecurityException, IOException {
		String[] dataString = dataFromAnotherServer.split(",");
		switch(dataString[0]) {
		case "1": return borrowItem(dataString[2].trim(), dataString[1]);
		case "2": return returnItem(dataString[2].trim(), dataString[1]);
		case "3": return findItem(dataString[2].trim(), dataString[1], true);
		case "4": return addToWaitingList(dataString[2].trim(), dataString[1]);
		case "5": return checkUserHasTakenBook(dataString[2].trim(), dataString[1]);
		case "6": return hasBookInLibraryAndCanBorrow(dataString[3].trim(), dataString[1], dataString[2], true);
		case "7":return exchangeItemBorrow(dataString[2].trim(), dataString[1]);
		}
		return "Unknown request";
	}
	
	public String removeItem(String userId, String itemId, int quantity) {
		implementationHelper.insertLog(userId+" : "+"Removed"+" : "+ itemId + " : "+ quantity);
		String response =  implementationHelper.removeItem(mcgillBooksData, itemId, quantity);
		implementationHelper.insertLog(userId+" : "+"Server Response Removed"+" : "+ itemId + " : "+ quantity);
		return response;
	}
	
	public String exchangeItem(String userId, String newItemId, String oldItemId) {
		implementationHelper.insertLog("Exchange Item New Item ID : "+newItemId+" Old Item ID : "+oldItemId);
		String hasTakenBook;
		if(oldItemId.substring(0, 3).equals(LibraryManagementConstants.MCGILL_CODE)){
			hasTakenBook = implementationHelper.checkUserTookBook(userId, oldItemId, mcgillBooksData);
		}else {
			hasTakenBook = implementationHelper.requestOtherLibraryServers(userId, oldItemId, 5, null);
		}
		
		String response;
		String hasNewBookInLibrary;
		if(newItemId.substring(0,3).equals(LibraryManagementConstants.MCGILL_CODE)) {
			hasNewBookInLibrary = implementationHelper.hasBookInLibraryAndCanBorrow(userId, newItemId,oldItemId, mcgillBooksData, false);
		}else {
			hasNewBookInLibrary = implementationHelper.requestOtherLibraryServers(userId, newItemId+","+oldItemId, 6, null);
		}
		
		if(hasNewBookInLibrary.equals(LibraryManagementConstants.TRUE) && hasTakenBook.equals(LibraryManagementConstants.TRUE)) {
			returnItem(userId, oldItemId);
			if(newItemId.substring(0,3).equals(LibraryManagementConstants.MCGILL_CODE)) {
				response = implementationHelper.exchangeItem(userId, newItemId, mcgillBooksData);
			}else {
				response = implementationHelper.requestOtherLibraryServers(userId, newItemId, 7, null);
			}
		}else {
			response = "Item can't exchange";
		}
		implementationHelper.insertLog("Exchange Item New Item ID : "+newItemId+" Old Item ID : "+oldItemId +" Response : "+response);
		return response;
	}
	
	private String exchangeItemBorrow(String userId, String itemId) {
		return implementationHelper.exchangeItem(userId, itemId, mcgillBooksData);
	}
	
	private String checkUserHasTakenBook(String userId, String itemId) {
		implementationHelper.insertLog("checkUser Has Taken Book userId :"+userId+" itemId : "+ itemId );
		String response = implementationHelper.checkUserTookBook(userId, itemId, mcgillBooksData);
		implementationHelper.insertLog("checkUser Has Taken Book userId :"+userId+" itemId : "+ itemId +"response "+response);
		return response;
	}
	
	private String hasBookInLibraryAndCanBorrow(String userId, String newItemId, String oldItemId, boolean isFromOtherServer) {
		implementationHelper.insertLog("checkUser Has Taken Book userId :"+userId+" itemId : "+ newItemId );
		String response = implementationHelper.hasBookInLibraryAndCanBorrow(userId, newItemId, oldItemId, mcgillBooksData, isFromOtherServer);
		implementationHelper.insertLog("checkUser Has Taken Book userId :"+userId+" itemId : "+ newItemId +"response "+response);
		return response;
	}

	public static Map<String, BookData> getMcgillBooksData() {
		return mcgillBooksData;
	}
	
	public List<BookData> getMcgillBooksList() {
		return mcgillBooksData.entrySet().stream().map(data -> data.getValue()).collect(Collectors.toList());
	}

	public void parseBookInfo(List<BookData> bookDatas) {
		mcgillBooksData = bookDatas.stream().map(data -> data).collect(Collectors.toMap(BookData::getItemId, Function.identity()));
	}
	
}
