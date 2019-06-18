package com.comp6231.project.testCases;

import static org.junit.Assert.*;
import org.junit.Test;

import com.comp6231.project.frontend.*;
import com.comp6231.project.model.MessageData;
import com.comp6231.project.constants.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ReplicaManagerTest {

    public static void sendRequest(MessageData messageData, String serverCode) throws IOException {
    	String itemId=messageData.getItemId();
    	String itemName=messageData.getItemName();
    	int quantity=messageData.getQuantity();
    	String msg = null;
        
    	InetAddress add = InetAddress.getByName("localhost");
        String ms = itemId + ":" + itemName + ":" + quantity + ":" + msg;
        byte[] data = ms.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(data, data.length, add, 6004);

        DatagramSocket socket = new DatagramSocket(LibraryManagementConstants.SEQUNECER_PORT);
        DatagramSocket recvSocket = new DatagramSocket(LibraryManagementConstants.FRONT_END_PORT);

        socket.send(sendPacket);

        byte[] recvData = new byte[1024];
        DatagramPacket recvPacket = new DatagramPacket(recvData, recvData.length);
        recvSocket.receive(recvPacket);

        String receiveMessage = new String(recvPacket.getData(), 0, recvPacket.getLength());

        System.out.println(receiveMessage);

        socket.close();
        recvSocket.close();

//        packageMsgAndSendToFE(socket, receiveMessage);
    }

    public static void main(String[] args) throws IOException {

//        sendRequest();
    }
}
