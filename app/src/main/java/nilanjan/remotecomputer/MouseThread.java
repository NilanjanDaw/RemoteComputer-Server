package nilanjan.remotecomputer;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by Nilanjan2 on 19-Oct-15.
 */
public class MouseThread extends Thread {

    private Socket mouseSocket;
    private DataOutputStream outputStream;
    private Handler mHandler;
    private boolean connected;
    public volatile String msg="";
    private String msgOld="";
    MouseThread(Socket mouseSocket, Handler mHandler) {
        this.mouseSocket = mouseSocket;
        this.mHandler = mHandler;
        try {
            outputStream = new DataOutputStream(mouseSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the message field for any new message to be sent
     * @param x The new message
     */
    public void update(String x)
    {
        this.msg=x;
    }

    /**
     * Method to handle the data transfer, over wifi concerning mouse events only
     */
    @Override
    public void run() {
        super.run();
        Message message = mHandler.obtainMessage();
        message.obj = "Hello from Mouse Thread";
        connected = true;
        message.what = 1;
        mHandler.sendMessage(message);
        try {
            while (connected) {
                if (!msg.equals(msgOld)) {

                    // PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
                    outputStream.writeUTF(msg);
                    msgOld = msg;
                }
                if (msg.equals("-1")) {
                    outputStream.writeUTF("-1");
                    break;
                }
            }
        } catch (Exception e) {
            Log.e("MouseThread", e.getMessage());
        } finally {
            try {
                outputStream.close();
                mouseSocket.close();
                connected = false;
                Log.d("MouseThread", "SocketClosed");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
