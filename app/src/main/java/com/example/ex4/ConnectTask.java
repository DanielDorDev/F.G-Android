package com.example.ex4;

import android.os.AsyncTask;

public class ConnectTask extends AsyncTask<String, Void, TcpClient> {

    @Override
    protected TcpClient doInBackground(String... params) {

        //we create a TCPClient object
        TcpClient mTcpClient = TcpClient.getInstance();
        mTcpClient.startClient(params[0], params[1]);
        return mTcpClient;
    }

    @Override
    protected void onPostExecute(TcpClient tcpClient) {
        super.onPostExecute(tcpClient);
    }
}