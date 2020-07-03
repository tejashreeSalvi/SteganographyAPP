package com.example.tejashree.stegoimage;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button encode,decode;
    ImageView image;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        encode = (Button) findViewById(R.id.bencode);
        decode = (Button) findViewById(R.id.bdecode);

        Toast.makeText(getApplicationContext(),"Encode",Toast.LENGTH_SHORT).show();

        //encode button...
        encode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                   startActivity(new Intent(getApplicationContext(),Encode.class));
            }
        });

        //decode button...

        decode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(),Decode.class));
            }
        });
    }
}
