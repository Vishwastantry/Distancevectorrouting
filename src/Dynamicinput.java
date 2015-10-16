import java.util.Scanner;

/*
 * AUTHOR: VISHWAS TANTRY
 */
public class Dynamicinput extends Thread{

	
	//creating routing and packet object for updating
	Routing r=null;
	Packet packet=null;
	Dynamicinput(Routing r,Packet packet) {
		this.r=r;
		this.packet=packet;
	}
	
	
	
	public void run() {
		
		//user input
		System.out.println("Enter the changes in the following order :");
		System.out.println("1.Neighbour and press enter");
		System.out.println("2.Updated distance and press enter");
        System.out.println("3.Next hop and updated distance");
        System.out.println(" ");
		while(true){
			
			//taking user inputs
		Scanner sc=new Scanner(System.in);
		 String  port_no= sc.next();
		int distance=sc.nextInt();
		String nhop=sc.next();
		synchronized(r) {
			//for updating the distance between already present routers
			if(r.routingtable.get(port_no)!=null) {
		r.routingtable.get(port_no).cost=distance;	
		r.routingtable.get(port_no).nexthop=nhop;
		r.routingtable.get(port_no).port=port_no;
			}else{
				
				//creating a new routers
				Dest n=new Dest();
				n.cost=distance;
				n.nexthop=nhop;
				n.port=port_no;
				r.routingtable.put(port_no,n);
			}
			//updating packet
		packet.src_route=r;
		Distancevectorroutingpeer.printtable(r.routingtable);				
		}
		
		}
		
	}
}
