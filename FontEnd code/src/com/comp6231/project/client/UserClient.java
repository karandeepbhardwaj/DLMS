package com.comp6231.project.client;

import java.io.IOException;
import java.rmi.NotBoundException;

public class UserClient {

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
