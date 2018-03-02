# UDPChat
A chatting room based on UDP

#### 1.The format of testing input.

> **<u>Example of the parameters:</u>** 
>
> `Server `- 25021 
>
> `client` - 127.0.0.1 25021 XiaotongJiang 
>
> **<u>Example of each request:</u>**
>
> `connection request` - Will be automatically send during the establishment of connection 
>
> `broadcast request`  - /broadcast,*message*  eg./broadcast,Hi,I'm Xiaotong Jiang 
>
> `private message`  - /tell *name*,*message*  eg./tell Alice,I'm Bob 
>
> `list request` - /list 
>
> `leave request`  - /leave

#### 2.Some details of my solution

##### The invocation style	- *at least once*

I choose *at least once* invocation style in order to improve reliability, although it will reduce efficiency. My solution is that if the message get lost, we will retransmit it, bue the times of retransmit should not exceed 5. If we have tried 5 times already, we will inform the client who sent this message and he will decide whether he will transmit again or not. In this way the transmission failure rate for a datagram will be decrease to $0.3^{5}  = 0.00243$, but actually it is not a strict *at least once* style cause it can't guarantee that the message be sent successfully either.

##### The failure model

| Failure                                                      | Solution                                                     |
| ------------------------------------------------------------ | :----------------------------------------------------------- |
| Omission failures-messages may be dropped occasionally.      | I use the at-least-once invocation style to reduce the omission failures which I have mentioned above. |
| Timesout-client wait for recieve forever.                    | I use *setSoTimeout(int timeout)* method. If the timeout is reached, a java.net.SocketTimeoutException will be thrown. |
| Crashed client-Client close the chat GUI for some reason(eg. power off or by accident)  or some unexpected problems about networks. | I use the heartbeat packet, which is sent every second, and if the server don't receive heartbeat packet of client_A for 2 second, it will believe the client_A has already crashed, remove it from the list of the member, and inform everyone else that the client disconnect. |
| Abnormal input-To be more friendly to users, it is necessary to inform user why the command can not be analysis properly. | I take the incorrect input, such as wrong commands, receiver that do not exist into consideration, and report it to users so that they can correct the command. |
| Ordering-Messages can sometimes be delivered out of sender order. | Cannot handle                                                |

##### The integrity and security

By using checksum, the probability that any message received is corrupted is very small, however,because the checksum mechanism is very simple, so it is easy to be modified on bad purpose.

#### 3.Summary

​	In thisassignment, I accomplish a chatting-room with some simple functions, althoughit is not perfect, but I give it my best shot. Actually it is a challenge for me because I'm not quite familiar with Java, but thanks for the code framework, it helps me a lot. 

​	From this challenge, I have better understanding on UDP protocol in theory, the communicate via datagrams send and receive packets of information don't have point-to-point channel, so the delivery of datagrams is not guaranteed, therefore it is much easier to achieve, which is one of its advantages, meanwhile, it will be faster and lighter than TCP because it don't need to establish the point-to-point channel and store the information about the channel.

​	Besides, by achieve the communication, I have better understanding on how to develop applications that communicate via datagrams in Java, which send and receive completely independent packets of information. Java API provides DatagramPacket and DatagramSocket. DatagramSocket supports sockets for sending and receiving UDP datagrams, while DatagramPacket include the information of message.
