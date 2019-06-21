package com.example.ex4;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void buttonConnect(View view) {

        TextView ip = findViewById(R.id.editIP);
        TextView port = findViewById(R.id.editPort);
        Intent intent = new Intent(this,
                ConnectActivity.class);
        intent.putExtra("ip", String.valueOf(ip.getText()));
        intent.putExtra("port", String.valueOf(port.getText()));
        startActivity(intent);
    }

}
