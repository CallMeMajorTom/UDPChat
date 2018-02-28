package UDPChat.Server;

//
// Source file for the server side. 
//
// Created by Sanny Syberfeldt
// Maintained by Marcus Brohede
//

import java.io.IOException;
import java.net.*;
//import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

public class Server {
	
    private ArrayList<ClientConnection> m_connectedClients = new ArrayList<ClientConnection>();
    private DatagramSocket m_socket;
    private DatagramPacket toSend;

    public static void main(String[] args) throws IOException{
	if(args.length < 1) {
	    System.err.println("Usage: java Server portnumber");
	    System.exit(-1);
	}
	try {
	    Server instance = new Server(Integer.parseInt(args[0]));
	    instance.listenForClientMessages();
	} catch(NumberFormatException e) {
	    System.err.println("Error: port number must be an integer.");
	    System.exit(-1);
	}
    }

    private Server(int portNumber) throws SocketException {
	// TODO: create a socket, attach it to port based on portNumber, and assign it to m_socket
    	m_socket = new DatagramSocket(portNumber);
    }

    private void listenForClientMessages() throws IOException {
	
    System.out.println("Waiting for client messages... ");

	do {
	    // TODO: Listen for client messages.
	    // On reception of message, do the following:
	    // * Unmarshal message
	    // * Depending on message type, either
	    //    - Try to create a new ClientConnection using addClient(), send 
	    //      response message to client detailing whether it was successful
	    //    - Broadcast the message to all connected users using broadcast()
	    //    - Send a private message to a user using sendPrivateMessage()
		String connect_success = "success!";
	    String connect_fail = "NameExist"; 
	    byte[] message = new byte[1024]; 
	    byte[] bytesToSend_m = new byte[1024];
	    DatagramPacket packet = new DatagramPacket(message, message.length);
		m_socket.receive(packet);
		
	    String received = new String(packet.getData(),0,packet.getLength());
	    //The format of received:/cmd <name of receiver>,message,TheNameOfSender
	    String[] seperated = null;
	    String[] seperated_2 = null;
	    seperated = received.split(",");
	    seperated_2 = seperated[0].split(" ");
	    
	    if(seperated[0].equalsIgnoreCase("/add"))//add Client(Display in console)
	    	if(addClient(seperated[1],packet.getAddress(),packet.getPort())){
	    		byte[] bytesToSend_s = new byte[1024];
	    		bytesToSend_s = connect_success.getBytes();
	    		toSend = new DatagramPacket(bytesToSend_s,bytesToSend_s.length,packet.getAddress(),packet.getPort());
	    		m_socket.send(toSend);
	    		String broadcast = seperated[1] + " enter the chatting-room";
		    	broadcast(broadcast);
	    	}
	    	else{//Name has been token
	    		byte[] bytesToSend_f = new byte[1024];
	    		bytesToSend_f = connect_fail.getBytes();
	    		toSend = new DatagramPacket(bytesToSend_f,bytesToSend_f.length,packet.getAddress(),packet.getPort());
	    		m_socket.send(toSend);
	    	} 
	    else if(seperated[0].equalsIgnoreCase("/broadcast")){//Broadcast(Display in everyone's GUI)
	    	//The format:NameOfBroadcaster:Message
	    	String broadcast_m = seperated[seperated.length-1] + ":";
	    	for(int j = 1;j < seperated.length-2;j++)
	    		broadcast_m = broadcast_m + seperated[j] +",";
	    	broadcast_m = broadcast_m + seperated[seperated.length-2];
	    	broadcast(broadcast_m);
	    }
	    else if(seperated_2[0].equalsIgnoreCase("/tell")){//PrivateMsg(Display in Sender and Receiver's GUI)
	    	//The format:Sender:Message
	    	String private_m = seperated[seperated.length-1] + ":";
	    	for(int j = 1;j < seperated.length-2;j++)
	    		private_m = private_m + seperated[j] +",";
	    	private_m = private_m + seperated[seperated.length-2];
	    	//***I modify the sendPrivateMessage() method to deal with the problem that the be-told one don't exist
	    	//sendPrivateMessage() method:
	    	//If the be-told one exist, send it and return true, otherwise return false
	    	if(!sendPrivateMessage(private_m, seperated_2[1]))
	    		private_m = "There is no " + seperated_2[1];
	    	//Inform the sender
	    	sendPrivateMessage(private_m,seperated[seperated.length-1]);
	    }
	    else if(seperated[0].equalsIgnoreCase("/list")) {//List the user's name(Display in Sender's GUI)
	    	//The format:====List====\nName1\nName2...\n============
	    	ClientConnection c;
	    	String List_of_name =  "====List====\n";
	    	for(Iterator<ClientConnection> itr = m_connectedClients.iterator(); itr.hasNext();) {
	    	    c = itr.next();
	    	    //***I add the getName() method, cause Name of class ClientConnection is private
	    	    List_of_name = List_of_name + c.getName() + "\n";  
	    	    }
	    	List_of_name = List_of_name + "============";
	    	bytesToSend_m = List_of_name.getBytes();
	    	toSend = new DatagramPacket(bytesToSend_m,bytesToSend_m.length,packet.getAddress(),packet.getPort());
    		m_socket.send(toSend);
	    }
	    else if(seperated[0].equalsIgnoreCase("/leave")){//Leave chatting-room(Display in everyone's GUI)
	    	//The format:TheNameOfLeaver leave
	    	String leave_m = seperated[1] + " leave";
	    	broadcast(leave_m);//inform everyone
	    	ClientConnection c;
	    	//Remove it from the ArrayList
	    	for(Iterator<ClientConnection> itr = m_connectedClients.iterator(); itr.hasNext();) {
	    	    c = itr.next(); 
	    	    if(c.getName().equals(seperated[1])){
	    	    	m_connectedClients.remove(c);
	    	    	break; 
	    	    }  	
	    }
	    }
	    else{//The Wrong Commond
	    	String wrong_m = "You sent wrong Commond";
	    	sendPrivateMessage(wrong_m,seperated[seperated.length-1]);//Inform the Sender
	    }
	} while (true);
    } 

    public boolean addClient(String name, InetAddress address, int port) {
	ClientConnection c;
	for(Iterator<ClientConnection> itr = m_connectedClients.iterator(); itr.hasNext();) {
	    c = itr.next();
	    if(c.hasName(name)) {
		return false; // Already exists a client with this name
	    }
	}
	m_connectedClients.add(new ClientConnection(name, address, port));
	return true;
    }

    public boolean sendPrivateMessage(String message, String name) throws IOException {
	ClientConnection c; 
	for(Iterator<ClientConnection> itr = m_connectedClients.iterator(); itr.hasNext();) {
	    c = itr.next();
	    if(c.hasName(name)) {
		c.sendMessage(message, m_socket);
		return true;
	    }
	}
	return false;
    }

    public void broadcast(String message) throws IOException {
	for(Iterator<ClientConnection> itr = m_connectedClients.iterator(); itr.hasNext();) {
	    itr.next().sendMessage(message, m_socket);
	}
    }
}
