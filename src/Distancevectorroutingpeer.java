import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

import javax.swing.text.html.HTMLDocument.Iterator;

/*
 * AUTHOR: VISHWAS TANTRY
 */
 class Routing implements Serializable {
	 
	 
	// Creating the routing table which  
	 //contains Ip address of destination and Destination object 
	 HashMap <String,Dest> routingtable=new <String,Dest> HashMap();
}
 
class Distancevectorroutingpeer{
	//initializing routing table with routing table r and packet p of the router
	//both are static since they need to be accessed from bith client and server threads
static Routing r=null;
static Packet packet=new Packet();
//to store network address
static String net[]=new String[2];
 Distancevectorroutingpeer() {	 
	 
	 
	 
 }
	 
	 public static void main(String args[]) throws InterruptedException, IOException  {
		 
		 
		 //port which the router is listening
		 String port=null;
		 //file object for reading the file
		 File config=null;
		 //line for passing through the file
		 String line=null;
		 //ip is used to obtain the four bytes of ip address
		 String ip[]=new String[4];
		 //to store the words and ip address separate
		 String sep[]=new String[2];
		 //to store neighbour's ip addresses		
		 ArrayList <String> neighbours=new <String> ArrayList();
		 int count=0;
		 
		
		 
		 //reading the file if no argument provided throw out error and exit
		 if(args.length>=0) {
			 //creating the file object from the name
			config=new File(args[0]);
			//BufferedReader object to read form the file stream
		    BufferedReader bf = new BufferedReader(new FileReader(config));
		    //to read from file
		    while (( line= bf.readLine()) != null) {
		    	//to extract address part
		    	if(line.contains("ADDRESS")) {		    
		    	sep=line.split(":");
		    	 //to separate the address with ip 
		    	 port=sep[1];
		    	 //to remove trailing spaces
		    	 port=port.trim();
		    
		    		
		    	}else if (line.contains("NEIGHBOR")) {
		           // to extract neighbor's ip
		    		sep=line.split(":");
		    		//to separate neighbor with ip	    		
			    	neighbours.add(sep[1].trim());	
			    	
		    	}else if(line.contains("NETWORK")) {
		    		
		    		//to sctract network info
		    		sep=line.split(":");
		    	    net[count++]=sep[1];
		    	}
		    	
		      }
		
		 }else{
			 //no file exit
			 System.out.println("No file found!!!!");
			 System.exit(1);
		 }
		 
		 //routing table for the router
		 r=new Routing();
		 //to fill the routing table with neighbor information
		 for(int i=0;i<neighbours.size();i++) {
			 Dest n=new Dest();
			 //updating cost to 1
			 n.cost=1;
			 n.nexthop=neighbours.get(i);
			 n.port=neighbours.get(i); 
			 r.routingtable.put(neighbours.get(i),n);
		 }
			
		 //inserting the routing table and source ip address in the packet used
		 //for sending updates
		 packet.source=port;
		 packet.src_route=r;
		
		// to track the time of the neighbor packets arriving at the router		 
		 HashMap<String,Long> timekeeper=new <String,Long> HashMap();
		 //creating a new thread for user input
		new Dynamicinput(r,packet).start();
		//to allow previous to print all the options to the user
		Thread.sleep(2000);
		//Displaying  initial configuration of the routing table
		System.out.println(" Initial conditions for router  1 ----");
		printtable(r.routingtable);
		
		//Creating a datagram socket to receive packets from the neighbor
		DatagramSocket datagramSocket = new DatagramSocket(convert(port));
		//starting the server thread for receiving the packets
		new ServerHelper(datagramSocket,r,packet,timekeeper).start();    
		
		
	    // creating the the client thread for sending routers updates 
		//neighbors 
		 while(true) {
			 
			 for(int i=0;i<neighbours.size();i++) {
				 //sends updates every 1 second
				 Thread.sleep(1000);
				 new ClientHelper(convert(neighbours.get(i)),packet).start();
			 }
		    
		 }
		 
				
	 }
	

	 
	 //this function is used to convert from ip address to port numbers
public static int convert(String ip) {
	String t[]=new String[4];
	//splitting the octet
    t= ip.split("\\.");	
    //using the last two octet left shifting 3 octet and adding with last octet
    int temp=Integer.parseInt(t[2]);
	int temp1=Integer.parseInt(t[3]);
	int port= (temp << 8)+ temp1;

	return port;
}


//This function is used for printing the routing table
public static void printtable(HashMap r) {
	         
	 Set<Integer> n=r.keySet();
	// System.out.println(n.size());
     java.util.Iterator<Integer> setiterator=n.iterator();
     System.out.println("From"+"                  ||"+"Neighbour"+"        || Cost"+"||  Next hop       "+"  || Subnet Mask");
	while(setiterator.hasNext()) {
		Dest temp=(Dest) r.get(setiterator.next());
				
		
		System.out.println(net[0]+"   ||  "+temp.port+"  ||  "+temp.cost+"  ||  "+temp.nexthop+"  ||  "+temp.subnet);
	}
	
}
	 
	 }