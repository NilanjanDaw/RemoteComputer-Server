package remoteComputer;

import java.awt.AWTException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class MouseManager extends Thread {
	
	private DataInputStream in;
	private DataOutputStream out;
	private Socket socket;
	private String s="";
	private remouse rem;
	
	/**
	 * Constructor to initialize class variables
	 * @param socket The socket over which mouse event data will be transferred
	 */
	public MouseManager(Socket socket) {
		
		this.socket = socket;
		try {
			this.in = new DataInputStream(socket.getInputStream());
			this.out = new DataOutputStream(socket.getOutputStream());
			rem = new remouse();
		} catch (IOException | AWTException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Method to handle data transfer related to mouse events
	 */
	@Override
	public void run() {
		super.run();
		try{
			System.out.println(in.toString()+" "+in.readUTF());
			System.out.println("MouseManager started");
			while(!s.equals("-1"))
			 {
					String y=in.readUTF();
					if(y.length()!=0){
						
						if(y.equalsIgnoreCase("Mouse UP")){
							rem.getLocation();
						}
						else{
							 String x[]=y.split(" ");
							 if(x.length==2)
								 {
								 	//System.out.println(x[0]+" "+x[1]);
								 	rem.move(x);
								 }
							 else if(x.length==1)
								 rem.Click(Integer.parseInt(x[0]));
						}
						s=y;
						 //out.writeUTF("Closing server connection"+socket.getLocalAddress());
						 //return;
					}
			 }
		}catch(Exception e)
		 {
			 System.out.println("Error: "+e.getMessage());
			 e.printStackTrace();
			 return;
		 } finally {
			 if(socket!=null) {
				try {
					System.out.print("Closing Socket");
					in.close();
					out.close();
					socket.close();
					return;
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
			 }
		 }
		 
	}
}
