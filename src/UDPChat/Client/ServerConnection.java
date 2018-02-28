/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package UDPChat.Client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Random;

/**
 *
 * @author brom
 */
public class ServerConnection {
	
	//Artificial failure rate of 30% packet loss
	static double TRANSMISSION_FAILURE_RATE = 0.3;
	
    private DatagramSocket m_socket = null;
    private InetAddress m_serverAddress = null;
    private int m_serverPort = -1;

    public ServerConnection(String hostName, int port) throws SocketException, UnknownHostException {
	m_serverPort = port;

	// TODO: 
	// * get address of host based on parameters and assign it to m_serverAddress
	// * set up socket and assign it to m_socket
		m_serverAddress = InetAddress.getByName(hostName);
		m_socket = new DatagramSocket();
	    m_socket.setSoTimeout(100000);
    }

    public boolean handshake(String name) throws IOException {
	// TODO:
	// * marshal connection message containing user name
	// * send message via socket
	// * receive response message from server
	// * unmarshal response message to determine whether connection was successful
	// * return false if connection failed (e.g., if user name was taken)
    	byte[] buf = new byte[256];
    	String cmd = "/add," + name;
    	buf = cmd.getBytes();
    	DatagramPacket packet = new DatagramPacket(buf, buf.length,m_serverAddress,m_serverPort);
		m_socket.send(packet);
		packet = new DatagramPacket(buf, buf.length);
		m_socket.receive(packet);
    	String received = new String(packet.getData(), 0, packet.getLength());
    	if(received.equalsIgnoreCase("NameExist")) 
    		return false;
    	else
    		System.out.println("Client received:" + received);
    		return true;
    }

    public String receiveChatMessage() throws IOException {
	// TODO: 
	// * receive message from server
	// * unmarshal message if necessary
	
	// Note that the main thread can block on receive here without
	// problems, since the GUI runs in a separate thread
	 
	// Update to return message contents
    byte[] message = new byte[256];
    DatagramPacket packet = new DatagramPacket(message, message.length);
    m_socket.receive(packet);
    String received = new String(packet.getData(),0,packet.getLength());
    return received;
    }

    public void sendChatMessage(String message,String name) throws IOException {
    	Random generator = new Random();
    	int i = 0;
    	double failure = generator.nextDouble();
    	byte[] buf = new byte[256];
    	String Msg = message + "," + name;
    	buf = Msg.getBytes();
    	DatagramPacket packet = new DatagramPacket(buf, buf.length,m_serverAddress,m_serverPort);
		// TODO: 
		// * marshal message if necessary
		// * send a chat message to the server
    	for(i = 0;i < 3; i++){//send the message for 3 times
    		if(failure > TRANSMISSION_FAILURE_RATE){
    			m_socket.send(packet);
    			break;
        	}
    		 
    		failure = generator.nextDouble();
    	}
    	if(i == 3) System.out.println("Message got lost");
    }

}
