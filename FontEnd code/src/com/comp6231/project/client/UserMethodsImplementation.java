package com.comp6231.project.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.NotBoundException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import com.comp6231.project.constants.LibraryManagementConstants;

import FrontEndIdl.FrontEnd;
import FrontEndIdl.FrontEndHelper;

/**
 * UserMethodsImplementaion is used to implement the client operations which send the data to the respective servers.
 * @author Naga Satish Reddy
 */
public class UserMethodsImplementation {

	/**
	 * getUser method return the user Id by verifying the validations
	 * @return
	 * @throws IOException 
	 */
	public String getUser() throws IOException {
		String userId = null;
		boolean userValidation = true;
		do {
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			String givenInput = reader.readLine().toUpperCase().trim();
			String universityCode = givenInput.substring(0, 3);
			boolean universityCodeVerify = universityCode.equals(LibraryManagementConstants.CONCORDIA_CODE) || 
					universityCode.equals(LibraryManagementConstants.MCGILL_CODE) || 
					universityCode.equals(LibraryManagementConstants.MONTREAL_CODE);
			String userCode =  givenInput.substring(3, 4);
			boolean userCodeVerify = userCode.equals(LibraryManagementConstants.USER_CODE) || 
					userCode.equals(LibraryManagementConstants.MANAGER_CODE);
			try {
				Integer.parseInt(givenInput.substring(5));
				if(universityCodeVerify && userCodeVerify && givenInput.length() == 8) {
					userValidation = false;
					userId = givenInput;
				}else {
					System.out.println("Please insert valid ID : ");
				}
			}catch(NumberFormatException exception) {
				System.out.println("Please insert valid ID : ");
			}
		}while(userValidation);
		return userId.toUpperCase();
	}

	/**
	 * showOperations method is used to find the user type and show the respective operations.
	 * @param userId
	 * @param args 
	 * @throws NotBoundException
	 * @throws IOException
	 */
	public void showOperations(String userId, String[] args) throws NotBoundException, IOException {
		if(userId.subSequence(3, 4).equals(LibraryManagementConstants.MANAGER_CODE)) {
			showManagerOperations(userId, args);
		}else {
			showUserOperations(userId, args);
		}
	}

	/**
	 * showUserOpeartions is used to show operations available for user
	 * @param userId
	 * @param args 
	 * @throws IOException 
	 * @throws NotBoundException 
	 */
	private void showUserOperations(String userId, String[] args) throws IOException, NotBoundException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		while(true) {
			System.out.println("Please select from below actions");
			System.out.println("1. Borrow book");
			System.out.println("2. Return book");
			System.out.println("3. Find book");
			System.out.println("4. Exchange Book");
			switch(Integer.parseInt(reader.readLine().trim())) {
			case 1: borrowBook(userId, reader, args);
			break;
			case 2: returnBook(userId, reader, args);
			break;
			case 3: findBook(userId, reader, args);
			break;
			case 4: exchangeItem(userId, reader, args);
			break;
			}
		}
	}

	/**
	 * exchangeItem method is used to exchange the books for the user
	 * @param userId
	 * @param reader
	 * @param args
	 * @throws IOException 
	 */
	private void exchangeItem(String userId, BufferedReader reader, String[] args) throws IOException {
		System.out.println("Enter the New ItemId : ");
		String newItemId = verifyItemId(reader.readLine().trim(), reader);
		System.out.println("Enter the Old ItemId :");
		String oldItemId = verifyItemId(reader.readLine(), reader);
		this.insertUserLog(userId, "Exchange Item New Item ID : "+newItemId+" Old Item ID : "+oldItemId);
		FrontEnd libraryInterface = connectToFrontEnd(userId, args);
		String response = libraryInterface.exchangeItem(userId, newItemId, oldItemId);
		this.insertUserLog(userId, "Exchange Item New Item ID : "+newItemId+" Old Item ID : "+oldItemId +" Response : "+response);
		System.out.println("Exchange Item New Item ID : "+newItemId+" Old Item ID : "+oldItemId +" Response : "+response);
	}

	/**
	 * returnBook handles the request of user to return his book.
	 * @param userId
	 * @param reader
	 * @param args 
	 * @throws IOException
	 * @throws NotBoundException
	 */
	private void returnBook(String userId, BufferedReader reader, String[] args) throws IOException, NotBoundException {
		System.out.println("Please enter itemID");
		String itemId = verifyItemId(reader.readLine().trim(), reader);
		this.insertUserLog(userId, userId +" Return itemID : "+itemId);
		FrontEnd libraryInterface = connectToFrontEnd(userId, args);
		String response = libraryInterface.returnItem(userId, itemId);
		this.insertUserLog(userId, userId +" Return Response itemID : "+itemId+ " "+ response);
		System.out.println(response);
	}

	/**
	 * findBook is used to handles the user request to find the book.
	 * @param userId
	 * @param reader
	 * @param args 
	 * @throws IOException
	 * @throws NotBoundException
	 */
	private void findBook(String userId, BufferedReader reader, String[] args) throws IOException, NotBoundException {
		System.out.println("Please enter itemName");
		String itemName = reader.readLine().trim();
		this.insertUserLog(userId, "Find Book: "+ itemName);
		FrontEnd libraryInterface = connectToFrontEnd(userId, args);
		String response = libraryInterface.findItem(userId, itemName, false);
		this.insertUserLog(userId, "Server Response Find Book: "+ response);
		System.out.println(response);
	}

	/**
	 * borrowBook method handles the request from the user to borrow request
	 * @param userId
	 * @param reader 
	 * @param args 
	 * @throws IOException
	 * @throws NotBoundException
	 */
	private void borrowBook(String userId, BufferedReader reader, String[] args) throws IOException, NotBoundException {
		System.out.println("Please enter item ID : ");
		String itemId = verifyItemId(reader.readLine().trim(), reader);
		insertUserLog(userId, userId+" Requested Borrow Book "+itemId);
		FrontEnd libraryInterface = connectToFrontEnd(userId, args);
		String response = libraryInterface.borrowItem(userId, itemId);
		insertUserLog(userId, "Server Response: "+ response);
		if(response.equals(LibraryManagementConstants.WAITING_LIST_MESSAGE)) {
			System.out.println(response);
			String option = reader.readLine();
			insertUserLog(userId, userId+" Interested In WaitingList: "+option);
			if(option.substring(0, 1).toUpperCase().equals("Y")) {
				insertUserLog(userId, userId+" Interested In WaitingList "+itemId);
				response = libraryInterface.addToWaitingList(userId, itemId);
				insertUserLog(userId, userId+" Server Response Interested In WaitingList "+itemId);
			}else {
				response = "Not added to waiting list";
			}
		}
		System.out.println("Server Response : "+response);
	}

	/**
	 * showManagerOperations is used to show operations available for manager
	 * @param userId
	 * @param args 
	 * @throws NotBoundException 
	 * @throws IOException 
	 */
	private void showManagerOperations(String userId, String[] args) throws NotBoundException, IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		while(true) {
			System.out.println("Please select from below actions");
			System.out.println("1. Add Items");
			System.out.println("2. Remove Items");
			System.out.println("3. List Items Available");
			System.out.println("Note: In Remove Items if quantity is given negative number book will be removed completely from library");
			switch(Integer.parseInt(reader.readLine().trim())) {
			case 1 : addItemManager(userId, reader, args);
			break;
			case 2: removeItemsManager(userId, reader, args);
			break;
			case 3: listItemManager(userId, args);
			break;
			}
		}
	}
	
	/**
	 * removeItemsManager method is used to remove items from library by manager
	 * @param userId
	 * @param args 
	 * @param reader2 
	 * @throws NotBoundException
	 * @throws IOException
	 */
	private void removeItemsManager(String userId, BufferedReader reader, String[] args) throws NotBoundException, IOException {
		System.out.println("Please enter ItemID :");
		String itemId = verifyItemId(reader.readLine().trim(), reader);
		System.out.println("Please enter qunatity");
		int quantity = Integer.parseInt(reader.readLine());
		FrontEnd libraryInterface = connectToFrontEnd(userId, args);
		insertUserLog(userId, "Remove Book : "+itemId+" : Quantity : "+quantity );
		String response = libraryInterface.removeItem(userId, itemId.toUpperCase(), quantity);
		insertUserLog(userId, "Remove Book : "+itemId+" : Quantity : "+quantity );
		System.out.println("Server Response : "+response);
	}

	/**
	 * listItemManager method is used to get the list of items from library
	 * @param userId
	 * @param args 
	 * @param reader 
	 * @throws NotBoundException
	 * @throws IOException 
	 * @throws SecurityException 
	 */
	private void listItemManager(String userId, String[] args) throws NotBoundException, SecurityException, IOException {
		FrontEnd libraryInterface = connectToFrontEnd(userId, args);
		insertUserLog(userId, userId+" ListItemsAvailable ");
		String response = libraryInterface.listItemAvailability(userId);
		insertUserLog(userId, userId+"Server Response ListItemsAvailable : \n"+response);
		System.out.println("Server Response : \n"+response);
	}

	/**
	 * addItemManager takes the required data from manager and send to server.
	 * @param userId
	 * @param args 
	 * @param reader2 
	 * @throws NotBoundException
	 * @throws IOException 
	 */
	private void addItemManager(String userId, BufferedReader reader, String[] args) throws NotBoundException, IOException {
		FrontEnd frontEndServer = connectToFrontEnd(userId, args);
		if(frontEndServer != null) {
			System.out.println("Please enter itemID");
			String itemId = verifyItemId(reader.readLine().trim(), reader);
			System.out.println("Please item Name");
			String itemName = reader.readLine().trim();
			System.out.println("Please enter quantity");
			int quantity = Integer.parseInt(reader.readLine());
			insertUserLog(userId, "Add Item : "+itemId+" : "+ itemName+" : "+quantity);
			String response = frontEndServer.addItem(userId, itemId, itemName, quantity);
			insertUserLog(userId, "Server Response Add Item : "+response);
			System.out.println("Server Response : "+response);
		}else {
			System.out.println("Couldn't connect to server...");
		}
	}

	/**
	 * insertUserLog method is to log the data into the respective file
	 * @param managerId
	 * @param message
	 * @throws SecurityException
	 * @throws IOException
	 */
	public void insertUserLog(String userId, String message) throws SecurityException, IOException {
		FileHandler fileHandler = new FileHandler(System.getProperty("user.dir")+LibraryManagementConstants.LOG_FOLDER+userId+".log", true);
		Logger userLogger = Logger.getLogger(userId);
		userLogger.addHandler(fileHandler);
		fileHandler.setFormatter(new SimpleFormatter());
		userLogger.setLevel(Level.INFO);
		userLogger.info(message);
		fileHandler.close();
	}

	/**
	 * verifyItemId method is to check the itemId format
	 * @param itemId
	 * @param reader
	 * @return
	 * @throws IOException
	 */
	private String verifyItemId(String itemId, BufferedReader reader) throws IOException {
		boolean isValidItemName = false;
		while(!isValidItemName) {
			String universityCode = itemId.substring(0, 3).toUpperCase().trim();
			boolean universityCodeVerify = universityCode.equals(LibraryManagementConstants.CONCORDIA_CODE) || 
					universityCode.equals(LibraryManagementConstants.MCGILL_CODE) || 
					universityCode.equals(LibraryManagementConstants.MONTREAL_CODE) && itemId.length() == 7;
			try {
				Integer.parseInt(itemId.substring(4));
				if(universityCodeVerify) {
					isValidItemName = true;
				}else {
					System.out.println("Please provide valid itemId : ");
					itemId = reader.readLine().trim();
				}
			}catch(NumberFormatException exception) {
				System.out.println("Please provide valid itemId : ");
				itemId = reader.readLine().trim();
			}
		}
		return itemId.toUpperCase();
	}

	private FrontEnd connectToFrontEnd(String userId, String[] args) {
		ORB orb = ORB.init(args, null);
		try {
			NamingContextExt namingContext = NamingContextExtHelper.narrow(orb.resolve_initial_references("NameService"));
			return FrontEndHelper.narrow(namingContext.resolve_str(LibraryManagementConstants.FRONT_END));
		} catch (InvalidName | NotFound | CannotProceed | org.omg.CosNaming.NamingContextPackage.InvalidName e) {
			e.printStackTrace();
		}
		return null;
	}
}
