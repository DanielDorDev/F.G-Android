
package com.example.ex4;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


// Tcp client class, connect and disconnect from server, created as singleton.
class TcpClient {

    // Create instance value for local storage.
    private static TcpClient ourInstance = null;

    // Boolean for client connection.
    private volatile boolean stopClient = true;

    // Stream for output.
    private PrintWriter mBufferOut;

    // Queue for output operations.
    private BlockingQueue<Runnable> dispatchQueue
            = new LinkedBlockingQueue<>();

    // Return the singleton instance.
    static TcpClient getInstance() {
        if (ourInstance != null) {
            return ourInstance;
        } else {
            return ourInstance = new TcpClient();
        }
    }

    // Send message to the server.
    void sendMessage(final String message) throws InterruptedException {

        // Add the task t dispatch queue.
        dispatchQueue.put(() -> {
            if (mBufferOut != null) {
                mBufferOut.println(message);
                mBufferOut.flush();
            }
        });
    }

    // Stop client, set boolean, close buffer connection.
    void stopClient() {

        if (!stopClient) {
            stopClient = true;
            if (mBufferOut != null) {
                mBufferOut.flush();
                mBufferOut.close();
            }
            mBufferOut = null;
        }
    }

    // Create connection to server.
    void startClient(String server_IP, String server_Port) {
        try {

            // Create netAddress, create connection, use the connection to create buffer out.
            InetAddress serverAddress = InetAddress.getByName(server_IP);
            Socket socket = new Socket(serverAddress, Integer.valueOf(server_Port));
            mBufferOut = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream())), true);

            stopClient = false;

            // Run dispatch queue.
            new Thread(() -> {

                while (!stopClient || !dispatchQueue.isEmpty()) {
                    try {
                        dispatchQueue.take().run();
                    } catch (Exception ignored) {}
                }
            }).start();

            // If failed opening, stopClient.
        } catch (Exception e) {
            stopClient = true;
        }
    }
}