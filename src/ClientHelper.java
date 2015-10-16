import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
/*
 * AUTHOR: VISHWAS TANTRY
 */
 class ClientHelper extends Thread {
	 int temp=0;
	 
	 Packet p=null;
	ClientHelper(int temp,Packet p) {
		this.temp=temp;
		this.p=p;
	}
	 public void run() {
		 try {
			 
			    //socket to accept send packets
			     DatagramSocket datagramSocket = new DatagramSocket();
			     //initializing bytearraystream
              	 ByteArrayOutputStream buffer=new ByteArrayOutputStream();
              	 //initializing objectoutputstream and passing bytearraystream
                 ObjectOutputStream data=new ObjectOutputStream(buffer);
                 //wrting the packet to the stream
				 data.writeObject(p);
				 //flushing the stream
				 data.flush();
				 //closing the stream
				 data.close();
				 
				 //to get ip address of the machine but it is localhost 
				 InetAddress receiverAddress = InetAddress.getLocalHost();
				 //converting it into bytearray
				 byte[] Buf= buffer.toByteArray();
				 //creating a packet and sending it to the socket
				 DatagramPacket packet = new DatagramPacket(
				        Buf ,Buf.length, receiverAddress,temp);
				 //sending the packet
				 datagramSocket.send(packet);	
				 
		} catch (IOException e) {
                e.printStackTrace();
                System.out.println("I am here");
		   
		}
		
	 
	 }
 }
				
		
		 
	 