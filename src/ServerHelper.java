import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.TimeUnit;
/*
 * AUTHOR: VISHWAS TANTRY
 */
class ServerHelper extends Thread {
	
	//creating the routing,packet and neighbor packet object
	 Routing r=new Routing();
	 DatagramSocket datasocket=null;
	 ServerSocket serverSocket=null;
	 Packet packet=new Packet();
	 
	 static String monitor=new String();
	 Socket s=new Socket();
	 HashMap <String,Long>time=new <String,Long>HashMap();
	
	ServerHelper(DatagramSocket self,Routing r,Packet packet,HashMap m) {
		
		datasocket=self;
		this.r=r;
		this.packet=packet;
		time=m;

	}
	
	 public void run() {
		 
		 Packet mes=null;		 
				try{
					//creating a byte buffer
					byte[] buffer = new byte[8192];
					//creating the datagrampacket for accepting packets
					DatagramPacket packet_data = new DatagramPacket(buffer, buffer.length);	
				
					
					while(true) {
					 //the server is always running to accept packets
						//this is used to check time of the neighbor packets
						if(time!=null) {
							 Set<String> n=time.keySet();					 
							 java.util.Iterator<String> setiterator=n.iterator();
							 //iterates through the time hash map and updates it
							 //with current time
						     while(setiterator.hasNext()) {
						    	 String k=setiterator.next();
						    	 Long currenttime=System.currentTimeMillis();
						    	 //here it checks whether some packets have 
						    	 //exceeded the 6 seconds limit
						    	 long neighbourtime =time.get(k).longValue();
						    	 if((currenttime-neighbourtime)>6000) {
						    	
						    		 synchronized(r) {
						    			 //it updates the distance as infinity
						    		Dest temp= r.routingtable.get(k);
						    		//System.out.println(temp.port);
						    		temp.cost=16;
						    		temp.nexthop=k;
						    		temp.port=k;
						    		r.routingtable.put(k,temp);			    		
						    		packet.src_route=r;
						    	//	Distancevectorroutingpeer.printtable(r.routingtable);
						    		 }
						    	 }
						     }
						
						}
						
						
						//gets packts from neighbours
						datasocket.receive(packet_data);
						//inserts it in buffer
					    buffer=packet_data.getData();
					    //collects it in object input stream
			            ObjectInputStream  in=new ObjectInputStream(new ByteArrayInputStream(buffer));
			            //reads it in packet object
			            Packet rneighbours=(Packet) in.readObject();
			            //sends it in checktimer to update the time of the packet
			            checktimer(rneighbours,r,time);	
			            //processes the the packets to update the 
			            check(rneighbours,r,packet);	
					}         
			
			
				} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
				 
		 }
	 

	 
	 //updates the packet time
	 public static void checktimer(Packet neighbour,Routing r,HashMap m) {
		 
		 		 
		HashMap <String,Long>timekeeper=m;
		Long temp =(Long) timekeeper.get(neighbour.source); 
		if(temp!=null) {
			
		    timekeeper.put(neighbour.source,System.currentTimeMillis());
		}else{
			
			timekeeper.put(neighbour.source,System.currentTimeMillis());
		}
}
	 
	 //updates the routing table based on updates
	 public  static  void check(Packet p,Routing r,Packet selfpacket)  {
			
		  //neighbouring packet
			Packet neighbour=p;
//			 if(p.source.equals("000.000.023.115")){
//				 printtable(p.src_route.routingtable);
//			 }
			  
			//iterator through neighbor's packet 
		       Set<String> n=neighbour.src_route.routingtable.keySet();
		       java.util.Iterator<String> setiterator=n.iterator();
		    
		       
		       while(setiterator.hasNext()) {
		    	   
		    	   	String port_set=setiterator.next();
		    	   //if self port and then update your distance else enter loop
		    	   if(!port_set.equals(selfpacket.source))
		    	   {
		    		   
		    		   
		    		   //if new neighbor update your routing table
		    		   if(!r.routingtable.containsKey(port_set)) {
		    			 
		    			   Dest newneighbour=new Dest();
		    			   Dest oldneighbour=r.routingtable.get(neighbour.source);
		    			   Dest new_from_old=neighbour.src_route.routingtable.get(port_set);
		    		       newneighbour.nexthop=neighbour.source;
		    			   newneighbour.cost=oldneighbour.cost+new_from_old.cost;
		    			   newneighbour.port=port_set;
		    			   r.routingtable.put(newneighbour.port,newneighbour);
		    			   
		    			   System.out.println("  Added a new neighbour...Updated the routing table");
		    			   System.out.println("-----------");
		    			   System.out.println(" ");
		    			   
		    			  printtable(r.routingtable);
		    		   }else{
		    			   
		    			   
		    			   //if old apply DVR to update distance
		    			     Dest distantneighbour=new Dest();
		    			   Dest oldneighbour=r.routingtable.get(neighbour.source);
		    			   
		    			   Dest new_from_old=neighbour.src_route.routingtable.get(port_set); 
		    			   distantneighbour= r.routingtable.get(port_set);
		    				    		
		    			   //check for infinity condition and avoid it
		    			   if(new_from_old.cost>=16) {
		    				   
		    				   
		    				  Dest temp= r.routingtable.get(port_set);
		    				  
		    				  temp.cost= 16;
		    				  temp.nexthop=neighbour.source;
		    				  temp.port=port_set;
		    				  r.routingtable.put(port_set,temp);
		    			       selfpacket.src_route=r;
		    			       
		    			    printtable(r.routingtable);
		    			      
		    			   } 
		    			   else{       			   
		    				 
		    				   //check for change in the previous distance 
		    				   //your table
		    				   if(distantneighbour.nexthop==neighbour.source) {

		    					 synchronized(r) {		    					   
		    					   Dest temp= r.routingtable.get(port_set); 				  
				    				
				    				  temp.cost=oldneighbour.cost+ new_from_old.cost;
				    				  temp.nexthop=neighbour.source;
				    				  temp.port=port_set;
				    				   r.routingtable.put(port_set,temp);
				    			       selfpacket.src_route=r;	
		    					 }      
		    					 
		    				   }
		    				   
		    			   //dvr to update the distance
		    			   if(distantneighbour.cost > oldneighbour.cost+ neighbour.src_route.routingtable.get(port_set).cost) {
		    				   
		    				   distantneighbour.cost = oldneighbour.cost+ neighbour.src_route.routingtable.get(port_set).cost;
		    				   distantneighbour.nexthop=neighbour.source;
		    				   
		    				   System.out.println("Updated routing table ");
		    				   System.out.println("---------------");
		    				   Distancevectorroutingpeer. printtable(r.routingtable);
		    				   
		    				  
		    			   }
		    			   }
		    		   }
		    	   }else{
		    		   
		    		   
		    			   synchronized(r){
		    				   
		    				  //update your distance with your neighbor
		    			       r.routingtable.get(neighbour.source).cost=neighbour.src_route.routingtable.get(port_set).cost;
		    			       r.routingtable.get(neighbour.source).nexthop=neighbour.source;
		    			       
		    				   
		    			   
		    			   }
		    			   
		    			  
		    	   }
		       }
		       
		       
			 
			
		       		} 
	 
	 
	 //print your table 
	 public static void printtable(HashMap r) {
         
		 Set<Integer> n=r.keySet();
		 
	     java.util.Iterator<Integer> setiterator=n.iterator();
	     System.out.println("Neighbour"+"        || Cost"+"||  Next hop       "+"  || Subnet Mask");
		while(setiterator.hasNext()) {
			Dest temp=(Dest) r.get(setiterator.next());
					
			
		
		System.out.println(temp.port+"  ||  "+temp.cost+"  ||  "+temp.nexthop+"  ||  "+temp.subnet);
			
		}
		
		System.out.println("--------------");
	}
		 
		 }
	 

 