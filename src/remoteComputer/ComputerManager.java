package remoteComputer;

import java.awt.AWTException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ComputerManager extends Thread {

	 ServerSocket serverSocketImage, serverSocketMouse;
	 Socket socketImage = null,socketMouse = null ;
	 String s="";
	 /**
	  * Constructor to initialize class variables
	  * @param port The ports to listen to. port and port + 1
	  * @throws IOException
	  * @throws AWTException
	  */
	 public ComputerManager(int port)throws IOException, AWTException {
		 serverSocketImage = new ServerSocket(port + 1);
		 serverSocketImage.setSoTimeout(10000);
		 serverSocketMouse = new ServerSocket(port);
		 serverSocketMouse.setSoTimeout(10000);
	 }

	 /**
	  * Method to handle the connection with remote client
	  */
	 @Override
	 public void run()
	 {
		super.run();
		 while(socketImage == null || socketMouse == null)
		 {
			 try{
					 System.out.println("Waiting for client at port "+serverSocketMouse.getLocalPort());
					 socketMouse=serverSocketMouse.accept();
					 System.out.println("Waiting for client at port "+serverSocketImage.getLocalPort());
					 socketImage=serverSocketImage.accept();
					 System.out.println("Connected to "+ socketImage.getRemoteSocketAddress());
					 System.out.println("Connected to "+ socketMouse.getRemoteSocketAddress());
					 MouseManager mouseManager = new MouseManager(socketMouse);
					 ScreenManager screenManager = new ScreenManager(socketImage);
					 mouseManager.start();
					 screenManager.start();

				 }
			 catch(Exception e)
			 {
					 System.out.println("Error: "+e.getMessage());
					// e.printStackTrace();
					 //return;
			 } finally {
					try {
						if(socketImage != null)
							serverSocketImage.close();
						if(socketMouse != null)
							serverSocketMouse.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			 }
		 }
	 }

	 public static void main(String args[]) {

		 int port = (args.length > 0) ? Integer.parseInt(args[0]): 8081;
		 Thread t;
		try {
			t = new ComputerManager(port);
			t.start();
		} catch (IOException | AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 }
}
