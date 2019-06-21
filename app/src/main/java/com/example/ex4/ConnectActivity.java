package com.example.ex4;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ConnectActivity extends AppCompatActivity {
    TcpClient clientFlight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        clientFlight = TcpClient.getInstance();
        Intent intent = getIntent();
        clientFlight.startClient(intent.getStringExtra("ip"),
                Integer.valueOf( intent.getStringExtra("port")));



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (clientFlight != null) {
            clientFlight.stopClient();
        }
    }
}
