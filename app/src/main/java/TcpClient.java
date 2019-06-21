import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class TcpClient {
    private static TcpClient ourInstance = null;
    private boolean mRun = false;
    private PrintWriter mBufferOut;

    public static TcpClient getInstance() {
        if (ourInstance != null) {
            return ourInstance;
        } else {
            return ourInstance = new TcpClient();
        }
    }

    private TcpClient() {
    }

    public void sendMessage(final String message) {
        Runnable runnable = () -> {
            if (mBufferOut != null) {
                mBufferOut.println(message);
                mBufferOut.flush();
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
        mBufferOut = null;
    }

    public void startClient(String server_IP, int server_Port) {
        mRun = true;
        try {
            mBufferOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                    new Socket(InetAddress.getByName(server_IP), server_Port).getOutputStream())),
                    true);
        } catch (Exception e) {
            mRun = false;
        }

    }
}
