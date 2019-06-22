package com.example.ex4;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    // Instance for local use.
    TcpClient mTcpClient;

    // On create set the instance and the main activity view.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTcpClient = TcpClient.getInstance();
        setContentView(R.layout.activity_main);
    }

    // On button connect, create joystick view with onMoved
    public void buttonConnect(View view) {

        // Get the ip, port from activity view, and create connect task class.
        String ip = ((TextView) findViewById(R.id.editIP)).getText().toString();
        String port = ((TextView) findViewById(R.id.editPort)).getText().toString();
        ConnectTask taskServer = new ConnectTask();

        // Execute connection task (async).
        taskServer.execute(ip, port, null);

        // Create Joystick view object, and add listener to the movement.
        JoystickView joystickView = new JoystickView(this);
        joystickView.setJoystickListener((aileron, elevator) -> {
            // Decouple between server update and joystick.
            // Joystick used only as view.
            try {

                // Send to server with the Flight-Gear protocol.
                mTcpClient.sendMessage(
                        "set /controls/flight/aileron " +
                                Double.valueOf(aileron).toString() + "\r\n" +
                                "set /controls/flight/elevator " +
                                Double.valueOf(elevator).toString() + "\r\n");

                // If interrupted, stop client.
            } catch (InterruptedException e) {
                mTcpClient.stopClient();

            } catch (Exception ignore) {
            }
        });
        setContentView(joystickView);
    }
}



