package com.example.ex4;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;


public class MainActivity extends AppCompatActivity {
    TcpClient mTcpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTcpClient = TcpClient.getInstance();
        setContentView(R.layout.activity_main);

    }

    public void buttonConnect(View view) {

        String ip = ((TextView)findViewById(R.id.editIP)).getText().toString();

        String port = ((TextView)findViewById(R.id.editPort)).getText().toString();
        ConnectTask taskServer = new ConnectTask();
        taskServer.execute(ip, port, null);
        JoystickView joystickView = new JoystickView(this);
        joystickView.setOnJostickMovedListener(new JoystickListener() {
            @Override
            public void OnMoved(int pan, int tilt) {

                try {
                    mTcpClient.sendMessage("set /controls/flight/aileron " +
                            Integer.valueOf(pan).toString() + "set /controls/flight/elevator "  +
                            Integer.valueOf(tilt).toString()  );
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void OnReleased() { }

        });
        setContentView(joystickView);
    }

}



