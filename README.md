Assignment 1-UDP Chat

1.The format of testing input.
Example of the parameters: 

Server- 25021 

client - 127.0.0.1 25021 XiaotongJiang 

Example of each request:

connection request - Will be automatically send during the establishment of connection 

broadcast request  - /broadcast,message  eg./broadcast,Hi,I'm Xiaotong Jiang 

private message  - /tell name,message  eg./tell Alice,I'm Bob 

list request - /list 

leave request  - /leave
2.Some details of my solution
The invocation style	- at least once
I choose at least once invocation style in order to improve reliability, although it will reduce efficiency. My solution is that if the message gets lost, we will retransmit it, but the times of retransmitting should not exceed 5. If we have tried 5 times already, we will inform the client who sent this message and he will decide whether he will transmit again or not. In this way the transmission failure rate for a datagram will be decreased to ​, but actually it is not a strict at least once style cause it can't guarantee that the message is sent successfully either.

The failure model
Failure	Solution
Omission failures-messages may be dropped occasionally.	I use the at-least-once invocation style to reduce the omission failures which I have mentioned above.
Timesout-client waits for receive forever.	I use setSoTimeout(int timeout) method. If the timeout is reached, a java.net.SocketTimeoutException will be thrown.
Crashed server-Client wait for server's reply forever	I use setSoTimeout(int timeout) method, if the server crashed and doesn't reply for a long time, a java.net.SocketTimeout-Exception will be thrown.
Crashed client-Client close the chat GUI for some reason(eg. power off or by accident)  or some unexpected problems about networks.	I use the heartbeat packet, which is sent every second, and if the server doesn't receive heartbeat packet of client_A for 2 seconds, it will believe the client_A has already crashed, remove it from the list of the member, and inform everyone else that the client disconnect.
Abnormal input-To be more friendly to users, it is necessary to inform user why the command cannot be analysis properly.	I take the incorrect input, such as wrong commands, the receiver that does not exist into consideration, and report it to users so that they can correct the command.
Ordering-Messages can sometimes be delivered out of sender order.	Cannot handle
The integrity and security
By using checksum, the probability that any message received is corrupted is very small, however,because the checksum mechanism is very simple, so it is easy to be modified on bad purpose.

3.Summary
​	In this assignment, I accomplish a chatting-room with some simple functions, although it is not perfect, I give it my best shot. Actually, it is a challenge for me because I'm not quite familiar with Java, but thanks for the code framework, it helps me a lot. 

​	 From this challenge, I have better understanding of UDP protocol in theory, the communicate via datagrams that send and receive packets of information don't have point-to-point channel, so the delivery of datagrams is not guaranteed, therefore it is much easier to achieve, which is one of its advantages, meanwhile, it will be faster and lighter than TCP because it doesn't need to establish the point-to-point channel and store the information about the channel.

​	Besides, by achieving the communication, I have a better understanding of how to develop applications that communicate via datagrams in Java, which send and receive completely independent packets of information. Java API provides DatagramPacket and DatagramSocket. DatagramSocket supports sockets for sending and receiving UDP datagrams, while DatagramPacket includes the information of the message.
