package com.comp6231.project.testCases;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.NotBoundException;

import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import com.comp6231.project.client.UserMethodsImplementation;
import com.comp6231.project.constants.LibraryManagementConstants;

import FrontEndIdl.FrontEnd;
import FrontEndIdl.FrontEndHelper;

public class TestClient {

	
	
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
	
	public String gotoServer() throws IOException{
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
	
	public static void main(String[] args) {
		UserMethodsImplementation validation = new UserMethodsImplementation();
		System.out.println("Enter user Id :");
		String userId;
		try {
			userId = validation.getUser();
			validation.showOperations(userId, args);
		} catch (IOException | NotBoundException e) {
			System.out.println("Something went wrong : "+e.getMessage());
		}
	}
}
