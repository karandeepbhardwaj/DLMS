package com.comp6231.project.model;

import java.util.ArrayList;
import java.util.List;

public class BookData {
	int quantity;
	String itemName;
	String itemId;
	List<String> waitingList;
	List<String> borrowedList;
	boolean isAccessible;

	public BookData(String itemId, String itemName, int quantity) {
		this.itemId = itemId;
		this.itemName = itemName;
		this.quantity = quantity;
		this.waitingList = new ArrayList<>();
		this.borrowedList = new ArrayList<>();
		isAccessible = true;
	}
	
	
	public boolean isAccessible() {
		return isAccessible;
	}


	public void setAccessible(boolean isAccessible) {
		this.isAccessible = isAccessible;
	}


	public void removeBorrowedMember(String userId) {
		this.borrowedList.remove(userId);
	}
	
	public void addBorrowMember(String userId) {
		this.borrowedList.add(userId);
	}
	
	public List<String> getBorrowedList() {
		return borrowedList;
	}

	public void setBorrowedList(List<String> borrowedList) {
		this.borrowedList = borrowedList;
	}

	public void decrementQuantity(int quantity) {
		this.quantity -= quantity;
	}
	
	public void incrementQuantity(int quantity) {
		this.quantity += quantity;
	}
	
	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public List<String> getWaitingList() {
		return waitingList;
	}

	public void setWaitingList(List<String> waitingList) {
		this.waitingList = waitingList;
	}
	
	public void addWaitingList(String userId) {
		this.waitingList.add(userId);
	}
}
