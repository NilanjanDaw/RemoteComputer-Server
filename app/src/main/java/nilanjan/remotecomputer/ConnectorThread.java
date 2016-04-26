package nilanjan.remotecomputer;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by Nilanjan2 on 19-Oct-15 at 6:07 PM.
 * Project Name: RemoteComputer
 */
public class ConnectorThread extends Thread {

    private String serverIP, serverPortImage, serverPortMouse;
    private boolean connected;
    boolean messageFlag=false;
    Handler mHandler;
    public static MouseThread mouseThread;
    public static ImageTransferThread imageTransferThread;

    /**
     * Constructor to initialise the class variables
     * @param serverIP The remote server IP
     * @param serverPortMouse The server port which will handle data transfers associated to mouse events
     * @param connected Flag variable to indicate state of connection
     * @param mHandler Instance of the UI handler
     */
    ConnectorThread(String serverIP, String serverPortMouse, boolean connected, Handler mHandler) {

        this.serverIP = serverIP;
        this.serverPortMouse = serverPortMouse;
        this.serverPortImage = Integer.toString(Integer.parseInt(serverPortMouse) + 1);
        this.connected=connected;
        this.mHandler = mHandler;
    }

    /**
     * Method to handle connection with remote server
     */
    @Override
    public void run() {
        try {

            InetAddress server = InetAddress.getByName(serverIP);
            Log.d("ConnectorThread", "Connection Started");
            final Socket socketImage = new Socket(server, Integer.parseInt(serverPortImage));
            final Socket socketMouse = new Socket(server, Integer.parseInt(serverPortMouse));
            Log.d("ConnectorThread", "Connected");
            Message message = mHandler.obtainMessage();
            message.obj = "connected";
            mouseThread = new MouseThread(socketMouse,mHandler);
            imageTransferThread = new ImageTransferThread(socketImage, mHandler);
            mouseThread.start();
            imageTransferThread.start();
            message.what = 2;
            mHandler.sendMessage(message);
        }catch (Exception e) {
            e.printStackTrace();
        }

    }


}

