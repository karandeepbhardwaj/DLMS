package com.comp6231.project.model;

import java.io.Serializable;

public class MessageData implements Serializable {
	
	private String userId;
	private String itemId;
	private String itemName;
	private int quantity;
	private String methodName;
	private boolean isFromOtherServer;
	private String newItemId;
	private int sequenceCounter;
	private int errorCounter;
	
	public int getErrorCounter() {
		return errorCounter;
	}

	public void setErrorCounter(int errorCounter) {
		this.errorCounter = errorCounter;
	}

	public String getNewItemId() {
		return newItemId;
	}

	public MessageData setNewItemId(String newItemId) {
		this.newItemId = newItemId;
		return this;
	}

	public boolean isFromOtherServer() {
		return isFromOtherServer;
	}

	public MessageData setFromOtherServer(boolean isFromOtherServer) {
		this.isFromOtherServer = isFromOtherServer;
		return this;
	}

	public String getUserId() {
		return userId;
	}

	public String getItemId() {
		return itemId;
	}

	public String getItemName() {
		return itemName;
	}

	public int getQuantity() {
		return quantity;
	}
	
	public String getMethodName() {
		return methodName;
	}

	public int getSequenceCounter() {
		return sequenceCounter;
	}

	public void setSequenceCounter(int sequenceCounter) {
		this.sequenceCounter = sequenceCounter;
	}

	public MessageData setUserId(String userId) {
		this.userId = userId;
		return this;
	}

	public MessageData setItemId(String itemId) {
		this.itemId = itemId;
		return this;
	}

	public MessageData setItemName(String itemName) {
		this.itemName = itemName;
		return this;
	}

	public MessageData setQuantity(int quantity) {
		this.quantity = quantity;
		return this;
	}

	public MessageData setMethodName(String methodName) {
		this.methodName = methodName;
		return this;
	}
	
}
