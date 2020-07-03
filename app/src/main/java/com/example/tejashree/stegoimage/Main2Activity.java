package com.example.tejashree.stegoimage;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Main2Activity extends AppCompatActivity {

    public Button btnLinkencode,btnlinkdecode,btnlinkqrcodegen,btnlinkqrcodescan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        btnLinkencode = (Button)findViewById(R.id.linkencode);
        btnLinkencode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LinkActivity.class);
                startActivity(intent);
            }
        });

        btnlinkdecode = (Button)findViewById(R.id.linkdecode);
        btnlinkdecode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LinkActivity2.class);
                startActivity(intent);
            }
        });

        btnlinkqrcodegen = (Button)findViewById(R.id.linkqrcodegen);
        btnlinkqrcodegen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Qrcode.class);
                startActivity(intent);
            }
        });

        btnlinkqrcodescan = (Button)findViewById(R.id.linkqrcodescan);
        btnlinkqrcodescan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Scanner.class);
                startActivity(intent);
            }
        });

    }
}
