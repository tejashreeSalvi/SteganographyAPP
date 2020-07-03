package com.example.tejashree.stegoimage;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import com.github.ybq.android.spinkit.sprite.CircleSprite;
import com.github.ybq.android.spinkit.sprite.Sprite;


public class Encode extends AppCompatActivity{

    Button gallery,secret;
    ImageView originalImage,secretImage;
    private int REQUEST_CODE = 1;
    private int REQUEST_SECRET = 2;
    public File fbase,fsecret,fstego;

    public static final String EXTRA_FILE_TAG = "ENCODED FILE";

    public ProgressBar pu;
    public Button bencoding;
    //Bitmap bitOriginal,bitSecret;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encode);
        originalImage = (ImageView) findViewById(R.id.originalImage);
        secretImage = (ImageView) findViewById(R.id.secretImage);

        pu = (ProgressBar)findViewById(R.id.p);
        gallery = (Button) findViewById(R.id.bgallery);
        secret = (Button)findViewById(R.id.bcamera);

        fstego = new File(getCacheDir(),"temp.png");

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(gallery.getText().toString().equals("BASE_IMAGE"))
                {
                    Intent intent = new Intent();
                    intent.setType("image/*").setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent,"Select Picture"), REQUEST_CODE);
                    try {
                        Thread.sleep(1000);
                        gallery.setText("SECRET_IMAGE");
                    }catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                }

                if(gallery.getText().toString().equals("SECRET_IMAGE"))
                {
                    Intent intent = new Intent();
                    intent.setType("image/*").setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent,"Select Picture"), REQUEST_SECRET);
                }
            }
        });

        secret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if((fbase.getAbsolutePath() != null) && (fsecret.getAbsolutePath() != null))
                {
                        Toast.makeText(getApplicationContext(),"God Plz Help...",Toast.LENGTH_SHORT).show();
                        Log.d("fbase",fbase.getAbsolutePath());
                        Log.d("fsecret",fsecret.getAbsolutePath());

                    Sprite s = new CircleSprite();

                        pu.setVisibility(View.VISIBLE);
                        new EncodingBackground(getApplicationContext()).execute(fbase,fsecret,fstego);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Plz select Images...",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public void call(int requestCode,String path)
    {
        if(requestCode == REQUEST_CODE)
        {
            fbase = new File(path);
            Log.d("Base :",fbase.getAbsolutePath());
        }

        if(requestCode == REQUEST_SECRET)
        {
            fsecret = new File(path);
            Log.d("Secret :",fsecret.getAbsolutePath());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE && data.getData() != null) {
            Uri uri = data.getData();
            String basePath = getPath(uri);
            call(requestCode,basePath);
            originalImage.setImageBitmap(decodeBitmapScaledDown(originalImage, basePath));
        }

        if (resultCode == RESULT_OK && requestCode == REQUEST_SECRET && data.getData() != null) {
            Uri uri = data.getData();
            String secretPath = getPath(uri);
            call(requestCode,secretPath);
            secretImage.setImageBitmap(decodeBitmapScaledDown(secretImage,secretPath));
        }
    }

    public static Bitmap decodeBitmapScaledDown(ImageView imageView, String filePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;

        Log.d("Options Width",String.valueOf(imageWidth));
        Log.d("Options Height",String.valueOf(imageHeight));


        //for later use
        //String imageType = options.outMimeType;

        int reqHeight = imageView.getMaxHeight();
        int reqWidth = imageView.getMaxWidth();

        int inSampleSize = 1;

        if (imageHeight > reqHeight || imageWidth > reqWidth) {

            final int halfHeight = imageHeight / 2;
            final int halfWidth = imageWidth / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        Log.d("Options Width",String.valueOf(imageWidth));
        Log.d("Options Height",String.valueOf(imageHeight));

        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
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



    //public static final String EXTRA_FILE_TAG = "ENCODED FILE";
/*
    public void encodeImage() {
        if (baseImage != null && secretImage != null) {
            try {
                Log.v("Path Base", "sending image to encoder");
                ProgressDialog progress = new ProgressDialog(this);
                progress.setTitle("Encoding");
                progress.setMessage("This may take a few minutes for large files...");
                progress.show();
                new EncodingBackground();
            } catch (OutOfMemoryError e) {
                Log.e("Path Base", "exception", e);
            }
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "You need two images", Toast
                    .LENGTH_SHORT);
            toast.show();
        }
    }
*/
}
