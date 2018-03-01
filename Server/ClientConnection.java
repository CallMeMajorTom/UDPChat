/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package UDPChat.Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Random;

/**
 * 
 * @author brom
 */
public class ClientConnection {
	
	static double TRANSMISSION_FAILURE_RATE = 0.3;
	
	private final String  m_name;
	private final InetAddress m_address;
	private final int m_port;

	public ClientConnection(String name, InetAddress address, int port) {
		m_name = name;
		m_address = address;
		m_port = port;
	}

	public void sendMessage(String message, DatagramSocket socket) throws IOException {
		
		Random generator = new Random();
		int i = 0;
    	double failure = generator.nextDouble();
		// TODO: send a message to this client using socket.
    	byte[] buf = new byte[256];
    	buf = message.getBytes();
    	DatagramPacket packet = new DatagramPacket(buf, buf.length,m_address,m_port);
    	while(failure < TRANSMISSION_FAILURE_RATE) {
    		failure = generator.nextDouble();
    		System.out.println("Send "+(i++)+" times");
    	}
    		socket.send(packet);

	}
 
	public boolean hasName(String testName) {
		return testName.equals(m_name);
	}
	

	public String getName() {
		return m_name;
	}
}
