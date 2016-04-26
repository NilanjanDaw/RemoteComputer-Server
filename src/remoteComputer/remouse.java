package remoteComputer;
import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Robot;
import java.awt.event.InputEvent;

public class remouse {
	Robot robot;
	int X=0,Y=0;
	remouse() throws AWTException
	{
		 robot=new Robot();
	}
	/**
	 * Method to move mouse according to the data received
	 * @param args Array containing coordinates of destination mouse location
	 * @throws Exception
	 */
	public void move(String[] args)throws Exception {
		float dx=Float.parseFloat(args[0])/100;
		float dy=Float.parseFloat(args[1])/100;
		X = MouseInfo.getPointerInfo().getLocation().x;
		Y = MouseInfo.getPointerInfo().getLocation().y;
		for(int i=0;i<=100;i++)
		robot.mouseMove((int)(dx*i)+X, (int)(dy*i)+Y);
	}
	
	/**
	 * Method to execute mouse click events
	 * @param x Code to determine type of mouse action to be performed
	 * @throws Exception
	 */
	public void Click(int x)throws Exception {
		
		if(x==10)
		{
			robot.mousePress(InputEvent.BUTTON1_MASK);
			
		}
		if(x==11)
		{
			robot.mouseRelease(InputEvent.BUTTON1_MASK);
		}
		if(x==20)
		{
			robot.mousePress(InputEvent.BUTTON3_MASK);
			
		}
		if(x==21)
		{
			robot.mouseRelease(InputEvent.BUTTON3_MASK);
		}
		if(x==3)
		{
			robot.mousePress(InputEvent.BUTTON1_MASK);
			robot.mouseRelease(InputEvent.BUTTON1_MASK);
		}
		
	}
	public void getLocation() {
		
		X = MouseInfo.getPointerInfo().getLocation().x;
		Y = MouseInfo.getPointerInfo().getLocation().y;
	}

}
