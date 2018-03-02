package UDPChat.Client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.*;
import java.lang.Thread;


public class Client implements ActionListener {

    private String m_name = null;
    private Thread Heartbeat = null;

    private ChatGUI m_GUI = null;
    private ServerConnection m_connection = null;

    public static void main(String[] args) throws Throwable {
	if(args.length < 3) {
	    System.err.println("Usage: java Client serverhostname serverportnumber username");
	    System.exit(-1);
	}

	try {
	    Client instance = new Client(args[2]);
	    instance.connectToServer(args[0], Integer.parseInt(args[1]));
	} catch(NumberFormatException e) {
	    System.err.println("Error: port number must be an integer.");
	    System.exit(-1);
	}
    }

    private Client(String userName) {	
	m_name = userName;
	// Start up GUI (runs in its own thread)
	m_GUI = new ChatGUI(this, m_name);
	/*m_GUI.addWindowListener(new WindowAdapter(){
		public void windowClosing(WindowEvent e){
			try {
				m_connection.sendChatMessage("/disconnect",m_name);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}}});*/

    }
    
    
    private void connectToServer(String hostName, int port) throws Throwable {
	//Create a new server connection
    m_connection = new ServerConnection(hostName, port);
	if(m_connection.handshake(m_name)) {
	    //Start the Thread to send Heartbeat
        Heartbeat = new Thread(
                new Runnable(){
                    public void run(){
                        while (true) {
                            try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                            try {
                                m_connection.sendChatMessage("\r\n",m_name);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                });
        Heartbeat.start();
        //Start to listen for server message
	    listenForServerMessages();
	}
	else {//if the name is same, shutdown the GUI
	    System.err.println("Unable to connect to server");
	    m_GUI.shutdown();
	}
    }


    private void listenForServerMessages() throws Throwable {
	// Use the code below once m_connection.receiveChatMessage() has been implemented properly.
    // If it is a [leave] message, shut down the leave one's GUI.
    // otherwise, just display it.
	do {
		String tmp = m_connection.receiveChatMessage();
		if(tmp.equalsIgnoreCase(m_name+" leave")){

		    m_GUI.dispose();
		    Heartbeat.stop();
		    System.exit(1);
        }
		m_GUI.displayMessage(tmp);
	} while(true);
    }

    // Sole ActionListener method; acts as a callback from GUI when user hits enter in input field
    
    @Override
    public void actionPerformed(ActionEvent e) {
	// Since the only possible event is a carriage return in the text input field,
	// the text in the chat input field can   now be sent to the server.
	try {
		//***I modify the sendChatMessage() method to send the name of sender along with the message to simplify the implementation of subsequent functions
		m_connection.sendChatMessage( m_GUI.getInput(),m_name);
	} catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	m_GUI.clearInput();
    }
}
