package com.example.tejashree.stegoimage;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;

public class Decode extends AppCompatActivity {

    Button bdecode,bstego;
    ImageView imageView;
    int REQUEST_CODE = 1;
    File fencoded,foriginal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decode);

        foriginal = new File(getCacheDir(),"temp.png");

        imageView = (ImageView)findViewById(R.id.img);
        bdecode = (Button)findViewById(R.id.bdecode);
        bstego = (Button)findViewById(R.id.bgallery);

        bstego.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*").setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), REQUEST_CODE);
            }
        });

        bdecode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fencoded.getAbsolutePath() != null)
                {
                    new DecodingBackground(getApplicationContext()).execute(fencoded,foriginal);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK)
        {
            Uri uri = data.getData();
            String s = getPath(uri);
            Log.d("Decode",s);
            fencoded = new File(s);
            imageView.setImageBitmap(Encode.decodeBitmapScaledDown(imageView,s));
        }
    }


    public String getPath(Uri uri) {
        // just some safety built in
        if( uri == null ) {
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if( cursor != null ) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        // this is our fallback here
        return uri.getPath();
    }

}
