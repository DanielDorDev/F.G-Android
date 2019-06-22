package com.example.ex4;

import android.os.AsyncTask;

// Create async connection.
public class ConnectTask extends AsyncTask<String, Void, TcpClient> {

    // Create the connection in background.
    @Override
    protected TcpClient doInBackground(String... params) {

        //we create a TCPClient object
        TcpClient mTcpClient = TcpClient.getInstance();

        // params[0] = Ip, params[1] = Port.
        mTcpClient.startClient(params[0], params[1]);
        return mTcpClient;
    }

    @Override
    protected void onPostExecute(TcpClient tcpClient) {
        super.onPostExecute(tcpClient);
    }

    // If Cancelled try to stop the client (could be used to notify changes at progress).
    @Override
    protected void onCancelled() {
        super.onCancelled();
        TcpClient.getInstance().stopClient();
    }
}