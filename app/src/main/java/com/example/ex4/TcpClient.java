
package com.example.ex4;

import android.util.Log;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
/*
public class TcpClient {

    public static final String TAG = TcpClient.class.getSimpleName();
    public static final String SERVER_IP = "10.0.2.2"; //server IP address
    public static final int SERVER_PORT = 5402;
    // message to send to the server
    private String mServerMessage;
    // sends message received notifications
    private OnMessageReceived mMessageListener = null;
    // while this is true, the server will continue running
    private boolean mRun = false;
    // used to send messages
    private PrintWriter mBufferOut;
    // used to read messages from the server
    private BufferedReader mBufferIn;


    public TcpClient(OnMessageReceived listener) {
        mMessageListener = listener;
    }

    public void sendMessage(final String message) {
        Runnable runnable = new Runnable() {
            public void run() {
                if (mBufferOut != null) {
                    Log.d(TAG, "Sending: " + message);
                    mBufferOut.println(message);
                    mBufferOut.flush();
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }


    public void stopClient() {

        mRun = false;

        if (mBufferOut != null) {
            mBufferOut.flush();
            mBufferOut.close();
        }

        mMessageListener = null;
        mBufferIn = null;
        mBufferOut = null;
        mServerMessage = null;
    }

    public void run() {

        mRun = true;

        try {
            //here you must put your computer's IP address.
            InetAddress serverAddr = InetAddress.getByName(SERVER_IP);

            Log.d("TCP Client", "C: Connecting...");

            //create a socket to make the connection with the server
            Socket socket = new Socket(serverAddr, SERVER_PORT);

            try {

                //sends the message to the server
                mBufferOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                //receives the message which the server sends back
                mBufferIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));


                //in this while the client listens for the messages sent by the server
                while (mRun) {

                    mServerMessage = mBufferIn.readLine();

                    if (mServerMessage != null && mMessageListener != null) {
                        //call the method messageReceived from MyActivity class
                        mMessageListener.messageReceived(mServerMessage);
                    }

                }

                Log.d("RESPONSE FROM SERVER", "S: Received Message: '" + mServerMessage + "'");

            } catch (Exception e) {
                Log.e("TCP", "S: Error", e);
            } finally {
                //the socket must be closed. It is not possible to reconnect to this socket
                // after it is closed, which means a new socket instance has to be created.
                socket.close();
            }

        } catch (Exception e) {
            Log.e("TCP", "C: Error", e);
        }

    }

    //Declare the interface. The method messageReceived(String message) will must be implemented in the Activity
    //class at on AsyncTask doInBackground
    public interface OnMessageReceived {
        public void messageReceived(String message);
    }

}






*/



public class TcpClient {
    private static TcpClient ourInstance = null;
    private boolean stopClient = true;
    private PrintWriter mBufferOut;
    private BlockingQueue<Runnable> dispatchQueue
            = new LinkedBlockingQueue<>();
    
    static TcpClient getInstance() {
        if (ourInstance != null) {
            return ourInstance;
        } else {
            return ourInstance = new TcpClient();
        }
    }

    public void sendMessage(final String message) throws InterruptedException {

        dispatchQueue.put(() -> {
            if (mBufferOut != null) {
                mBufferOut.println(message);
                mBufferOut.flush();
            }
        });
    }

    void stopClient() {
        stopClient = true;
        if (mBufferOut != null) {
            mBufferOut.flush();
            mBufferOut.close();
        }
        mBufferOut = null;
    }

    void startClient(String server_IP, String server_Port) {
        try {

            InetAddress serverAddr = InetAddress.getByName(server_IP);
            Socket socket = new Socket(serverAddr, Integer.valueOf(server_Port));

            mBufferOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);


            stopClient = false;

           // new Thread(() -> {
            while (!stopClient || !dispatchQueue.isEmpty()) {
                try {
                    dispatchQueue.take().run();
                } catch (InterruptedException ignored) {
                }
            }
       //     }).start();

        } catch (Exception e) {
            stopClient = true;

        }

    }
}
