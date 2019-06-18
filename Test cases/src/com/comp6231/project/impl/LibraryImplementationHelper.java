package com.comp6231.project.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.stream.Collectors;

import com.comp6231.project.constants.LibraryManagementConstants;
import com.comp6231.project.model.BookData;


public class LibraryImplementationHelper {
	
	private FileHandler fileHandler;
	private static Logger logger;

	/**
	 * loadLibraryData method is used to load the initial library data from the file while server starts.
	 * @param booksDataFile
	 * @param logFile
	 * @return
	 * @throws IOException
	 */
	public Map<String, BookData> loadLibraryData(String booksDataFile, String logFile) throws IOException {
		createLogFile(logFile);
		logger.info("Loading library data...");
		List<BookData> bookDataList = new ArrayList<>();
		File booksFile = new File(booksDataFile);
		try {
			BufferedReader reader = new BufferedReader(new FileReader(booksFile));
			String line = "";
			while((line = reader.readLine()) != null) {
				String [] bookData = line.split(",");
				bookDataList.add(new BookData(bookData[0], bookData[1],Integer.parseInt(bookData[2])));
			}
			reader.close();
		} catch (FileNotFoundException e) {
			System.out.println("Specified file cannot be found");
		}
		return bookDataList.stream().collect(Collectors.toMap(BookData::getItemId, Function.identity()));
	}
	
	/**
	 * createLogFile is used to create the log file for servers.
	 * @param logFile
	 * @throws IOException
	 */
	private void createLogFile(String logFile) throws IOException {
		fileHandler = new FileHandler(logFile, true);
		logger = logger.getLogger("logFile");
		logger.addHandler(fileHandler);
		fileHandler.setFormatter(new SimpleFormatter());
		logger.info("Logger file created...");
	}
	
	/**
	 * inserLog is the used to insert the log data into file
	 * @param message
	 */
	public void insertLog(String message) {
		logger.setLevel(Level.INFO);
		logger.info(message);
	}
	
	/**
	 * addBookToLibrary is used to add the book to library by manager
	 * @param booksDataMap
	 * @param itemId
	 * @param itemName
	 * @param quantity
	 * @return
	 */
	public String addBookToLibrary(Map<String, BookData> booksDataMap, String itemId, String itemName,
			int quantity) {
		if(booksDataMap.containsKey(itemId.toUpperCase())) {
			BookData bookData = booksDataMap.get(itemId);
			isObjectAccessible(bookData);
			bookData.setAccessible(false);
			bookData.incrementQuantity(quantity);
			checkWaitingAndSendBook(bookData, itemId, booksDataMap);
			bookData.setAccessible(true);
		}else {
			BookData bookData = new BookData(itemId, itemName, quantity);
			booksDataMap.put(itemId, bookData);
		}
		return "Item added to the library";
	}
	
	/**
	 * isObjectAccessible method checks whether the book data is available to access if it is not available it keeps the thread on waiting.
	 * @param bookData
	 */
	private void isObjectAccessible(BookData bookData) {
		while(!bookData.isAccessible()) ;
	}
	
	/**
	 * checkWaitingAndSendBook method is used to send the available books to the waiting list people
	 * @param bookData
	 * @param itemId 
	 * @param booksDataMap 
	 */
	private void checkWaitingAndSendBook(BookData bookData, String itemId, Map<String, BookData> booksDataMap) {
		while(bookData.getWaitingList().size() != 0 && bookData.getQuantity() != 0) {
			List<String> waitingList = bookData.getWaitingList();
			boolean isUserFromOtherServers = !waitingList.get(0).substring(0, 3).equals(itemId.substring(0, 3));
			if(!canBorrowBook(isUserFromOtherServers, booksDataMap, waitingList.get(0))) {
				insertLog(bookData.getItemId()+" is assigned to "+waitingList.get(0));
				bookData.addBorrowMember(waitingList.get(0));
				bookData.decrementQuantity(1);
			}
			waitingList.remove(0);
		}
	}
	
	/**
	 * canBorrowBook checks whether the user can borrow the book or not.
	 * @param isFromOtherServers
	 * @param booksDataMap 
	 * @param userId 
	 * @return
	 */
	private boolean canBorrowBook(boolean isFromOtherServers, Map<String, BookData> booksDataMap, String userId) {
		if(!isFromOtherServers)
			return false;
		for (String itemId : booksDataMap.keySet()) {
			if(booksDataMap.get(itemId).getBorrowedList().contains(userId))
				return true;
		}
		return false;
	}
	
	/**
	 * listItemAvailable is used to list the library data by manager
	 * @param concordiaBooksData 
	 * @return 
	 */
	public String listItemAvailable(Map<String, BookData> booksDataMap) {
		String booksList = "";
		for(String itemId : booksDataMap.keySet()) {
			BookData bookData = booksDataMap.get(itemId);
			booksList += bookData.getItemId()+" "+bookData.getItemName()+" "+bookData.getQuantity()+",";
		}
		return booksList.substring(0, booksList.length()-1);
	}
	
	/**
	 * borrowItem is used to borrowItem from the library by user.
	 * @param booksDataMap
	 * @param userId
	 * @param itemId
	 * @param isFromOtherServers 
	 * @return
	 */
	public String borrowItem(Map<String, BookData> booksDataMap, String userId, String itemId, boolean isFromOtherServers) {
		String response;
		if(isFromOtherServers && canBorrowBook(isFromOtherServers, booksDataMap, userId)) {
			return "You cannot borrow more than 1 book";
		}
		if(booksDataMap.containsKey(itemId)) {
			BookData bookData = booksDataMap.get(itemId);
			isObjectAccessible(bookData);
			bookData.setAccessible(false);
			if(bookData.getBorrowedList().contains(userId)) {
				response = "You have already borrowed this book";
			}else if(bookData.getQuantity() > 0) {
				bookData.decrementQuantity(1);
				bookData.addBorrowMember(userId);
				response =  "Book issued successfully";
			}else {
				response = LibraryManagementConstants.WAITING_LIST_MESSAGE;
			}
			bookData.setAccessible(true);
		}else {
			response =  "Book doesn't exist";
		}
		return response;
	}
	
	public String requestOtherLibraryServers(String userId, String itemIdOrItemName, int requestId, String universitySeverCode) {
		String response="Couldn't get response";
		int serverPort;
		if(requestId != 3)
			serverPort = getServerPortBasedOnUser(itemIdOrItemName.substring(0, 3));
		else
			serverPort =  getServerPortBasedOnUser(universitySeverCode);
		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket();
			String message = getMessageString(requestId, itemIdOrItemName, userId);
			InetAddress host = InetAddress.getByName("localhost");
			DatagramPacket sendPacket = new DatagramPacket(message.getBytes(), message.length(), host, serverPort);
			socket.send(sendPacket);
			byte [] receiveBuffer = new byte[1500];
			DatagramPacket recievedPacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
			socket.receive(recievedPacket);
			response = new String(recievedPacket.getData());
		} catch (IOException e) {
			this.logger.info("Couldn't connect library server...");
		}finally {
			if(socket != null)
				socket.close();
		}
		return response.trim();

	}
	
	private int getServerPortBasedOnUser(String userId) {
		switch (userId) {
		case "CON" : return LibraryManagementConstants.CONCORDIA_SERVER_PORT;
		case "MCG" :return LibraryManagementConstants.MCGILL_SERVER_PORT;
		case "MON" : return LibraryManagementConstants.MONTREAL_SERVER_PORT;
		}
		return 0;
	}
	
	private String getMessageString(int requestId, String itemId, String userId) {
		return new StringBuilder().append(requestId).append(",").append(itemId).append(",").append(userId).toString();
	}
	
	/**
	 * addToWaitingList method is used to add users to waiting list
	 * @param booksDataMap
	 * @param userId
	 * @param itemId
	 * @return
	 */
	public String addToWaitingList(Map<String, BookData> booksDataMap, String userId, String itemId) {
		String response;
		if(booksDataMap.containsKey(itemId)) {
			BookData bookData = booksDataMap.get(itemId);
			isObjectAccessible(bookData);
			bookData.setAccessible(false);
			if(bookData.getQuantity() > 0) {
				bookData.decrementQuantity(1);
				bookData.addBorrowMember(userId);
				response =  "Book is available and has been issued successfully";
			}else {
				if(bookData.getWaitingList().contains(userId)) {
					response = "You are already in waiting list.";
				}else {
					bookData.addWaitingList(userId);
					response = "You are added to waiting list.";
				}
			}
			bookData.setAccessible(true);
		}else {
			response = "Book has been removed by Library Manager";
		}
		return response;
	}
	
	public String findItem(Map<String, BookData> booksDataMap, String userId, String itemName) {
		List<BookData> booksList = booksDataMap.values().stream().collect(Collectors.toList());
		Map<String, List<BookData>> itemNameMap = booksList.stream().collect(Collectors.groupingBy(BookData::getItemName));
		StringBuilder response = new StringBuilder();
		if(itemNameMap.containsKey(itemName)) {
			List<BookData> bookDataList = itemNameMap.get(itemName);
			bookDataList.stream().forEach(bookData -> {
				response.append(bookData.getItemId()+" "+bookData.getQuantity()+",");
			});
		}
		return response.toString();
	}
	
	/**
	 * returnItem method is used to return the book to library from user.
	 * @param booksDataMap
	 * @param userId
	 * @param itemId
	 * @return
	 */
	public String returnItem(Map<String, BookData> booksDataMap, String userId, String itemId) {
		String response;
		if(booksDataMap.containsKey(itemId)) {
			BookData bookData = booksDataMap.get(itemId);
			isObjectAccessible(bookData);
			bookData.setAccessible(false);
			if(!bookData.getBorrowedList().contains(userId)) {
				response = "Borrower should return the book";
			}else {
				bookData.incrementQuantity(1);
				bookData.removeBorrowedMember(userId);
				checkWaitingAndSendBook(bookData,itemId, booksDataMap);
				response = "Book Returned";
			}
			bookData.setAccessible(true);
		}else {
			response =  "Item ID not available";
		}
		return response;
	}
	
	/**
	 * removeItem method is used to remove the books from the library by manager
	 * @param booksDataMap
	 * @param itemId
	 * @param quantity
	 * @return
	 */
	public String removeItem(Map<String, BookData> booksDataMap, String itemId, int quantity) {
		String response;
		if(booksDataMap.containsKey(itemId)) {
			BookData bookData = booksDataMap.get(itemId);
			if(quantity < 0){
				while(bookData.getBorrowedList().size() != 0) {
					String userId = bookData.getBorrowedList().get(0);
					insertLog(itemId+" has been taken back from user "+userId);
					bookData.getBorrowedList().remove(0);
				}
				booksDataMap.remove(itemId);
				response = "Item removed successfully";
			}else if(bookData.getQuantity() >= quantity){
				bookData.decrementQuantity(quantity);
				response = "Item quantity decreased successfully";
			}else {
				response = "The quantity you entered is incorrect";
			}
		}else {
			return "Bookdata doesn't exist to delete";
		}
		return response;
	}

	/**
	 * exchangeItem method is to exchange a book for a user with another.
	 * @param userId
	 * @param newItemId
	 * @param oldItemId
	 * @param concordiaBooksData 
	 * @return
	 */
	public String exchangeItem(String userId, String newItemId, Map<String, BookData> bookData) {
		BookData book = bookData.get(newItemId);
		while(book.isAccessible()) ;
		book.decrementQuantity(1);
		book.addBorrowMember(userId.trim());
		book.setAccessible(true);
		return "Successfully Exchanged";
	}

	protected String checkUserTookBook(String userId, String oldItemId, Map<String, BookData> bookData) {
		return bookData.containsKey(oldItemId) && bookData.get(oldItemId).getBorrowedList().contains(userId) ? LibraryManagementConstants.TRUE:LibraryManagementConstants.FALSE;
	}

	public String hasBookInLibraryAndCanBorrow(String userId, String newItemId, String oldItemId, Map<String, BookData> booksData, boolean isFromOtherServer) {
		String response = LibraryManagementConstants.FALSE;
		if(isFromOtherServer) {
			String borrowedItemId = getBorrowedBookId(booksData, userId);
			if(borrowedItemId != null && !borrowedItemId.equals(oldItemId)) {
				return "Can't borrow book";
			}
		}
		if(booksData.containsKey(newItemId)) {
			BookData book = booksData.get(newItemId);
			isObjectAccessible(book);
			book.setAccessible(false);
			if(book.getQuantity() == 0) {
				book.setAccessible(true);
				response = "No books Available";
			}else {
				response = LibraryManagementConstants.TRUE;
			}
		}
		return response;
	}

	private String getBorrowedBookId(Map<String, BookData> booksData, String userId) {
		for (String itemId : booksData.keySet()) {
			if(booksData.get(itemId).getBorrowedList().contains(userId))
				return itemId;
		}
		return null;
	}
}
