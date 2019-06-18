package Interface;

public interface ServerInterface {
	public String addItem(String managerID,String itemID,String itemName,int quantity);
	public String addItemError(String managerID,String itemID,String itemName,int quantity);
	public String removeItem (String managerID,String itemID,int quantity);
	public String listItemAvailability (String managerID);
	public String borrowItem (String userID,String itemID);
	public String findItem (String userID,String itemName); 
	public String returnItem (String userID,String itemID);  
	public String queueImplementation(String itemId,String userId);
	public String findItemOtherServers(String userID,String itemName);
	public String exchangeItem (String studentID,String newItemID,String oldItemID); 


}
