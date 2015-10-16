import java.util.Scanner;

class Routers
		{
int  distance[]=new int[5];
int  neighbours[]=new int[5];
		}


public class Distancevector {
	
	public static void main(String args[]) {
		
		//int distancevector[][]=new int[5][5];
		System.out.println("Enter the number of the routers");
		
int number=0;
Scanner sc=new Scanner(System.in);
number=sc.nextInt();

int distancevector[][]={{0,4,9},
		                {4,0,3},
		                {9,3,0}};


   Routers routers[]=new Routers[number];
   
		for(int i=0;i<number;i++) {
			routers[i]=new Routers();
		      for(int j=0;j<number;j++) {		  
		distancevector[i][i]=0;
		System.out.print("Enter the value for "+i+" to "+j+"--------->");
		
		
		routers[i].distance[j]=distancevector[i][j];
		System.out.println(routers[i].distance[j]);
		routers[i].neighbours[j]=j;
		}
		}
		
		
        int count =1;
		while(count !=0) {
		
		count=0;
		for(int i=0;i<number;i++) 
		          for(int j=0;j<number;j++) 
		                      for(int k=0;k<number;k++) {		
		if(routers[i].distance[j]>(distancevector[i][k]+routers[k].distance[j]))
			
		{
			
			
		//System.out.println("this got "+(routers[i].distance[j])+" "+i+" "+j+"replaced by "+(distancevector[i][k]+routers[k].distance[j]));	
		routers[i].distance[j]=routers[i].distance[k]+routers[k].distance[j];
		//System.out.println(routers[i].distance[j]+"i "+i+"  j  "+j);
		routers[i].neighbours[j]=k;
		count++;
		}
		                      }

		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		for(int i=0;i<number;i++)
		{
		//System.out.print("distance of  router "+i);
		for(int j=0;j<number;j++)
			
		{			
			
			//System.out.print("from  "+i+" to"+j+"  "+routers[i].distance[j]+"  ");
			
			System.out.println("distance of  router "+(i)+" from router "+(j)+"is "+routers[i].distance[j]+" via "+(routers[i].neighbours[j]));
		}
		}
		}
		
	
	
		
		
		
		
	}


