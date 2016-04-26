package nilanjan.remotecomputer;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by Nilanjan on 19-Oct-15.
 */
public class ImageTransferThread extends Thread {
    private Socket imageTransferSocket;
    private DataInputStream inputStream;
    private Handler mHandler;
    boolean connected=false;
    static byte[] receivedByte;
    boolean messageFlag=false;

    /**
     * Constructor to initialise class variables
     * @param imageTransferSocket The socket over which the screenshots will be transferred
     * @param mHandler Instance of the UI handler
     */
    ImageTransferThread(Socket imageTransferSocket, Handler mHandler) {
        this.imageTransferSocket = imageTransferSocket;
        this.mHandler = mHandler;
        try {
            inputStream = new DataInputStream(imageTransferSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to handle the data transfer, over wifi, concerning only the screenshots
     */
    @Override
    public void run() {
        super.run();
        Message message = mHandler.obtainMessage();
        message.obj = "Hello from Image Transfer Thread";
        message.what = 1;
        mHandler.sendMessage(message);
        connected = true;
        try {
            while (connected) {

                if (inputStream.available() > 0) {
                    int length = inputStream.readInt();
                    Log.d("Data Transfer: ", "Starting Data Transfer");
                    if (length > 0) {
                        receivedByte = new byte[length];
                        long time = System.currentTimeMillis();
                        inputStream.readFully(receivedByte);
                        if (time - System.currentTimeMillis() > 2000)
                            Log.d("File Read: ", "stuck");
                        Log.d("Incoming", Integer.toString(receivedByte.length));
                        messageFlag = true;
                    }

                }
                if (messageFlag) {
                    Log.d("Message", "Entered Block");
                    Message msg = mHandler.obtainMessage();
                    msg.what = 5;
                    msg.obj = receivedByte;
                    mHandler.sendMessage(msg);
                    messageFlag = false;
                }

            }
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
                imageTransferSocket.close();
                Log.d("ImageTransferThread", "Socket Closed");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
