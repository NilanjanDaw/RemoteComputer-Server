package nilanjan.remotecomputer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import static android.widget.Toast.LENGTH_SHORT;

public class MainActivity extends AppCompatActivity {

    Button left,right,connect;
    EditText ip,port;
    ImageView screen;
    RelativeLayout connector;
    FrameLayout screenPad;
    String serverIP,serverPort;
    public boolean connected=false;
    static volatile int X;
    static volatile int Y;
    int startX,startY;
    long curTime;
    ConnectorThread connectorThread = null;
    MouseThread mouseThread = null;
    ImageTransferThread imageTransferThread = null;

    /**
     * Handler to update UI objects eg. the imageView screen
     */
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            if(message.what == 2) {
                showText(message.obj.toString());
                connector.setVisibility(View.GONE);
                screenPad.setEnabled(true);
                mouseThread = ConnectorThread.mouseThread;
                imageTransferThread = ConnectorThread.imageTransferThread;
            }

            else if (message.what == 5){
                byte[] imageByte = (byte[]) message.obj;
                Log.d("handler", "called");
                Log.d("Handler: ", Integer.toString(imageByte.length));

                Bitmap bitmap = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
                screen.setImageBitmap(bitmap);
                //firstImage=false;
            }
        }
    };

    /**
     * Method containing the action listeners
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        try {
            getActionBar().hide();
        }catch (NullPointerException e) {}
        try {
            connector = (RelativeLayout) findViewById(R.id.connector);
            screenPad = (FrameLayout) findViewById(R.id.screenPad);
            screenPad.setEnabled(false);
            left = (Button) findViewById(R.id.leftClick);
            /**
             * Listener to handle Left Clicks
             */
            left.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_DOWN:
                            left.setBackgroundColor(0xDFAA27);
                            //showText();
                            mouseThread.update("10");
                            break;
                        case MotionEvent.ACTION_UP:
                            left.setBackgroundColor(0xff5a595b);
                            mouseThread.update("11");
                    }
                    return true;
                }
            });
            right = (Button) findViewById(R.id.rightClick);
            /**
             * Method to handle Right Click
             */
            right.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_DOWN:
                            right.setBackgroundColor(0xDFAA27);
                            mouseThread.update("20");
                            break;
                        case MotionEvent.ACTION_UP:
                            right.setBackgroundColor(0xff5a595b);
                            mouseThread.update("21");
                            break;

                    }
                    return true;
                }
            });


            ip = (EditText) findViewById(R.id.ip);
            port = (EditText) findViewById(R.id.port);
            connect = (Button) findViewById(R.id.connect);
            connect.setOnClickListener(connectListener);

            screen = (ImageView) findViewById(R.id.imageView);
            /**
             * Callback Method to handle mouse pad drag and click events
             */
            screen.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    try {
                        switch (event.getActionMasked()) {

                            case MotionEvent.ACTION_DOWN:
                                startX = (int) event.getX();
                                startY = (int) event.getY();
                                curTime = System.currentTimeMillis();
                                break;
                            case MotionEvent.ACTION_MOVE:

                                X = (int) event.getX();
                                Y = (int) event.getY();
                                mouseThread.update(Integer.toString(X - startX) + " " + Integer.toString(Y - startY));
                                startX = X;
                                startY = Y;
                                break;
                            case MotionEvent.ACTION_UP:
                                long duration = System.currentTimeMillis() - curTime;
                                if (duration < 100) {
                                    Log.d("Click", Long.toString(duration));
                                    mouseThread.update("3");

                                }
                                break;

                        }
                        return true;
                    } catch (Exception e) {
                        showText(e.getMessage());
                        return false;
                    }

                }
            });
        }catch (Exception e) {
            showText(e.getMessage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    /**
     * Closes the sockets which are active
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mouseThread!=null)
            mouseThread.update("-1");
        if(imageTransferThread != null)
            imageTransferThread.connected = false;
    }

    /**
     * Listener to start the connection thread
     */
    private View.OnClickListener connectListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (!connected) {
                serverIP = ip.getText().toString();
                serverPort = port.getText().toString();
                if (!serverIP.equals("") && !serverPort.equals("")) {

                    connectorThread = new ConnectorThread(serverIP, serverPort, connected, handler);
                    connectorThread.start();
                }
                connect.setEnabled(false);
            }
        }
    };

    private void showText(String x){
        Toast.makeText(this, x, LENGTH_SHORT).show();
    }
}
