package com.example.ex4;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class MainActivity extends AppCompatActivity {

    // Instance for local use.
    TcpClient mTcpClient;
    private EditText mIP, mPort;

    // On create set the instance and the main activity view.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTcpClient = TcpClient.getInstance();
        setContentView(R.layout.activity_main);

        // Set edit texts (ip and port).
        mIP = (EditText) findViewById(R.id.editIP);
        mPort = (EditText) findViewById(R.id.editPort);

        // Add listeners.
        mIP.addTextChangedListener(mTextWatcher);
        mPort.addTextChangedListener(mTextWatcher);

        // Set in the first time.
        buttonEmptyFields();
    }

    // On Destroy close the server connection.
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTcpClient.stopClient();
    }

    // On button connect, create joystick view with onMoved
    public void buttonConnect(View view) {

        // Get the ip, port from activity view, and create connect task class.


       // String ip = ((TextView) findViewById(R.id.editIP)).getText().toString();
        //String port = ((TextView) findViewById(R.id.editPort)).getText().toString();
        ConnectTask taskServer = new ConnectTask();

        // Execute connection task (async).
        taskServer.execute(mIP.getText().toString(), mPort.getText().toString(), null);

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

    // Create observer for text change.
    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        // check Fields For Empty Values after text changed.
        @Override
        public void afterTextChanged(Editable editable) {
            buttonEmptyFields();
        }
    };

    // Change fields if by text length.
    void buttonEmptyFields(){
        Button b = (Button) findViewById(R.id.button);
        if(mPort.getText().toString().length() < 4|| mIP.getText().toString().length() < 4){
            b.setEnabled(false);
        } else {
            b.setEnabled(true);
        }
    }

/*
    // Save ip and port.
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        String ip = ((TextView) findViewById(R.id.editIP)).getText().toString();
        String port = ((TextView) findViewById(R.id.editPort)).getText().toString();

        savedInstanceState.putString("UserIP", ip);
        savedInstanceState.putString("UserPort",port);
    }

    // Restore ip and port.
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        ((TextView) findViewById(R.id.editIP)).setText(
                savedInstanceState.getString("UserIP"));
        ((TextView) findViewById(R.id.editPort)).setText(
                savedInstanceState.getString("UserPort"));

    }
    */
}

