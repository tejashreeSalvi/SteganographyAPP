package com.example.tejashree.stegoimage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.util.Random;

public class ImageActivity extends AppCompatActivity {

    private static final String LOG_TAG = ImageActivity.class.getSimpleName();

    ImageView istego;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        istego = (ImageView)findViewById(R.id.istego);

        Intent intent = getIntent();
        String path = intent.getStringExtra(Encode.EXTRA_FILE_TAG);
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        istego.setImageBitmap(bitmap);

        Calendar cal = Calendar.getInstance();
        String name = ""+cal.get(Calendar.YEAR)+cal.get(Calendar.MONTH)+cal.get(Calendar.MINUTE)+cal.get(Calendar.SECOND);

        String root = Environment.getExternalStorageDirectory().toString();
        File tempFile = new File(root+"/encoded_Image");
        if(!tempFile.exists())
            tempFile.mkdirs();

        Random generator = new Random();
        int n = 1000;
        n = generator.nextInt(n);
        String fname = "temp"+n+".png";
        File file = new File(tempFile,fname);
        if(file.exists())
            file.delete();
        try {
            Log.d("Saved Image",file.getAbsolutePath());
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }
}
