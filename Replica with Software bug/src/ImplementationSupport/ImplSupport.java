package ImplementationSupport;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class ImplSupport {

	public String ipcConnection(String userID, String itemID, String methods, String itemName, int portNumber,
			String oldItemId) throws SocketException, UnknownHostException {
		String libString = itemID != null ? itemID : "0000";
		String bookName = itemName != null ? itemName : "XXXX";
		String oldBookId = oldItemId != null ? oldItemId : "0000";
		String response = null;
		String option = libString.substring(0, 3).toUpperCase();
		DatagramSocket aSocket = null;
		aSocket = new DatagramSocket();
		String msgString = userID.concat(",").concat(libString.toString()).concat(",").concat(methods).concat(",")
				.concat(bookName).concat(",").concat(oldBookId.toString());
		// System.out.println(msgString);
		byte[] message = msgString.getBytes();
		InetAddress aHost = InetAddress.getByName("localhost");
		if (itemName !=null) {
			try {

				DatagramPacket request = new DatagramPacket(message, msgString.length(), aHost, portNumber);
				aSocket.send(request);
				System.out.println("Request message sent from the client to server with port number " + portNumber
						+ " is: " + new String(request.getData()));
				byte[] buffer = new byte[1000];
				DatagramPacket reply = new DatagramPacket(buffer, buffer.length);

				aSocket.receive(reply);
				System.out.println("Reply received from the server with port number " + portNumber + " is: "
						+ new String(reply.getData()));
				response = new String(reply.getData());
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (aSocket != null)
					aSocket.close();
			}
		} else if (option.equals("CON")) {

			try {

				DatagramPacket request = new DatagramPacket(message, msgString.length(), aHost, portNumber);
				aSocket.send(request);
				System.out.println("Request message sent from the client to server with port number " + portNumber
						+ " is: " + new String(request.getData()));
				byte[] buffer = new byte[1000];
				DatagramPacket reply = new DatagramPacket(buffer, buffer.length);

				aSocket.receive(reply);
				System.out.println("Reply received from the server with port number " + portNumber + " is: "
						+ new String(reply.getData()));
				response = new String(reply.getData());
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (aSocket != null)
					aSocket.close();
			}
		}

		else if (option.equals("MCG")) {
			try {

				DatagramPacket request = new DatagramPacket(message, msgString.length(), aHost, portNumber);
				aSocket.send(request);
				System.out.println("Request message sent from the client to server with port number " + portNumber
						+ " is: " + new String(request.getData()));
				byte[] buffer = new byte[1000];
				DatagramPacket reply = new DatagramPacket(buffer, buffer.length);

				aSocket.receive(reply);
				System.out.println("Reply received from the server with port number " + portNumber + " is: "
						+ new String(reply.getData()));
				response = new String(reply.getData());
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (aSocket != null)
					aSocket.close();
			}
		}

		else if (option.equals("MON")) {
			try {

				DatagramPacket request = new DatagramPacket(message, msgString.length(), aHost, portNumber);
				aSocket.send(request);
				System.out.println("Request message sent from the client to server with port number " + portNumber
						+ " is: " + new String(request.getData()));
				byte[] buffer = new byte[1000];
				DatagramPacket reply = new DatagramPacket(buffer, buffer.length);

				aSocket.receive(reply);
				System.out.println("Reply received from the server with port number " + portNumber + " is: "
						+ new String(reply.getData()));
				response = new String(reply.getData());
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (aSocket != null)
					aSocket.close();
			}
		}
		return response;
	}
}
