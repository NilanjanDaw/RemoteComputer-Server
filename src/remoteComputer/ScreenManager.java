package remoteComputer;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.PointerInfo;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

public class ScreenManager extends Thread {
	
	private DataInputStream in;
	private DataOutputStream out;
	private Socket socket;
	private Dimension screen;
	private Robot robot;
	private BufferedImage bufferedImage;	
	public ScreenManager(Socket socket) {
		
		try {
			this.socket = socket;
			this.in = new DataInputStream(socket.getInputStream());
			this.out = new DataOutputStream(socket.getOutputStream());
			screen=Toolkit.getDefaultToolkit().getScreenSize();
			robot=new Robot();
		} catch (IOException | AWTException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		super.run();
		System.out.println("ScreenManager started");
		try {
			while(true) {
				System.out.println("Entered Image Transfer Block");
				BufferedImage blackSquare = new BufferedImage(10, 10, BufferedImage.TYPE_3BYTE_BGR);
		        for(int i = 0; i < blackSquare.getHeight(); i++){
		            for(int j = 0; j < blackSquare.getWidth(); j++){
		                blackSquare.setRGB(j, i, 128);
		            }
		        }
		        PointerInfo pointer = MouseInfo.getPointerInfo();
	            int x = (int) pointer.getLocation().getX();
	            int y = (int) pointer.getLocation().getY();
				bufferedImage =robot.createScreenCapture(new Rectangle((int)screen.getWidth(), 
							(int)screen.getHeight()));
				bufferedImage.getGraphics().drawImage(blackSquare, x, y, null);
				 File compressedImageFile = new File("Screen.jpg");
			      OutputStream os =new FileOutputStream(compressedImageFile);
			      Iterator<ImageWriter>writers =  ImageIO.getImageWritersByFormatName("jpg");
			      ImageWriter writer = (ImageWriter) writers.next();
			      ImageOutputStream ios = ImageIO.createImageOutputStream(os);
			      writer.setOutput(ios);
			      ImageWriteParam param = writer.getDefaultWriteParam();
			      param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			      param.setCompressionQuality(0.25f);
			      writer.write(null, new IIOImage(bufferedImage, null, null), param);
			      os.close();
			      ios.close();
			      writer.dispose();
				File file=new File("Screen.jpg");
				FileInputStream fileInputStream=new FileInputStream(file);
				byte image[]=new byte[(int) (file.length()+1)];
				System.out.println("File Double: "+file.length());
				fileInputStream.read(image);
				fileInputStream.close();
				if(out != null)
				{
					out.writeInt((int) (file.length()+1));
					out.write(image);
				}
				Thread.sleep(10);
			}
		 }catch(IOException e) {
			 System.out.println(e.getMessage());
		 } catch (InterruptedException e) {
			e.printStackTrace();
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
