
package com.example.ex4;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TcpClient {
    private static TcpClient ourInstance = null;
    private boolean stopClient = false;
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
        stopClient = false;
        if (mBufferOut != null) {
            mBufferOut.flush();
            mBufferOut.close();
        }
        mBufferOut = null;
    }

    void startClient(String server_IP, int server_Port) {
        stopClient = true;
        try {



            mBufferOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                    new Socket(InetAddress.getByName(server_IP), server_Port).getOutputStream())),
                    true);
            new Thread(() -> {
                while (!stopClient || !dispatchQueue.isEmpty()) {
                    try {
                        dispatchQueue.take().run();
                    } catch (InterruptedException ignored) {
                    }
                }
            }).start();

        } catch (Exception e) {
            stopClient = false;

        }

    }
}
