package com.example.tejashree.stegoimage;


import android.content.Context;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
/**
 * Created by Tejashree on 23-09-2019.
 */

public class EncodingBackground extends AsyncTask<File, Integer, File> {


    private static final String LOG_TAG = EncodingBackground.class.getSimpleName();

    Bitmap stegoImage, baseImage, secretImage;
    public static final int OVERHEAD_SIZE = 64;

    private int pixelRow = 0;
    private int pixelCol = 0;
    private Context c;
    File fbase, fsecret, fstego;

    public EncodingBackground(Context c) {
        Toast.makeText(c, "Welcome to Encoding", Toast.LENGTH_SHORT).show();
        this.c = c;
      }


    @Override
    protected File doInBackground(File... params) {

        pixelRow = 0;
        pixelCol = 0;
        try {
            params[0] = FileUtils.convert(params[0], c.getCacheDir().getPath());
            Bitmap buffer = BitmapFactory.decodeFile(params[0].getPath()).copy(Bitmap.Config
                    .ARGB_8888, true);
            BufferedInputStream stream = new BufferedInputStream(new FileInputStream(params[1]));

            int numBitsPossible = ((buffer.getHeight() * buffer.getWidth()) * 3);

            if (numBitsPossible < ((params[1].length() * 8) + OVERHEAD_SIZE)) {
                return endEncode(params);
            }
            byte[] overhead = ByteBuffer.allocate(8).putLong(params[1].length()).array();

            int bitCount = 0;
            for (int i = 0; i < overhead.length; i++) {
                byte currentByte = overhead[i];
                for (int j = 7; j >= 0; j--) {
                    int bit = (currentByte & (0x1 << j)) >> j;
                    bit = bit & 0x01;
                    if (bitCount % 3 == 0) {
                        int red;
                        if (bit == 0) {
                            red = Color.red(buffer.getPixel(pixelCol, pixelRow)) & 0xFE;
                        } else {
                            red = Color.red(buffer.getPixel(pixelCol, pixelRow)) | 0x1;
                        }
                        buffer.setPixel(pixelCol, pixelRow, Color.argb(
                                Color.alpha(buffer.getPixel(pixelCol, pixelRow)), red,
                                Color.green(buffer.getPixel(pixelCol, pixelRow)),
                                Color.blue(buffer.getPixel(pixelCol, pixelRow))));
                    } else if (bitCount % 3 == 1) {
                        int blue;
                        if (bit == 0) {
                            blue = Color.blue(buffer.getPixel(pixelCol, pixelRow)) & 0xFE;
                        } else {
                            blue = Color.blue(buffer.getPixel(pixelCol, pixelRow)) | 0x1;
                        }
                        buffer.setPixel(pixelCol, pixelRow, Color.argb(
                                Color.alpha(buffer.getPixel(pixelCol, pixelRow)),
                                Color.red(buffer.getPixel(pixelCol, pixelRow)),
                                Color.green(buffer.getPixel(pixelCol, pixelRow)), blue));
                    } else {
                        int green;
                        if (bit == 0) {
                            green = Color.green(buffer.getPixel(pixelCol, pixelRow)) & 0xFE;
                        } else {
                            green = Color.green(buffer.getPixel(pixelCol, pixelRow)) | 0x1;
                        }
                        buffer.setPixel(pixelCol, pixelRow, Color.argb(
                                Color.alpha(buffer.getPixel(pixelCol, pixelRow)),
                                Color.red(buffer.getPixel(pixelCol, pixelRow)), green,
                                Color.blue(buffer.getPixel(pixelCol, pixelRow))));
                        incrementPixel(buffer.getWidth());
                    }
                    bitCount++;
                }
            }
            incrementPixel(buffer.getWidth());
            Log.v(LOG_TAG, "encoded overhead");

            bitCount = 0;
            int i = 0;
            int val = stream.read();
            while(val != -1) {
                byte currentByte = (byte)val;
                for (int j = 7; j >= 0; j--) {
                    int bit = (currentByte & (0x1 << j)) >> j;
                    bit = bit & 0x1;
                    if (bitCount % 3 == 0) {
                        int red;
                        if (bit == 0) {
                            red = Color.red(buffer.getPixel(pixelCol, pixelRow)) & 0xFE;
                        } else {
                            red = Color.red(buffer.getPixel(pixelCol, pixelRow)) | 0x1;
                        }
                        buffer.setPixel(pixelCol, pixelRow, Color.argb(
                                Color.alpha(buffer.getPixel(pixelCol, pixelRow)), red,
                                Color.green(buffer.getPixel(pixelCol, pixelRow)),
                                Color.blue(buffer.getPixel(pixelCol, pixelRow))));
                    } else if (bitCount % 3 == 1) {
                        int blue;
                        if (bit == 0) {
                            blue = Color.blue(buffer.getPixel(pixelCol, pixelRow)) & 0xFE;
                        } else {
                            blue = Color.blue(buffer.getPixel(pixelCol, pixelRow)) | 0x1;
                        }
                        buffer.setPixel(pixelCol, pixelRow, Color.argb(
                                Color.alpha(buffer.getPixel(pixelCol, pixelRow)),
                                Color.red(buffer.getPixel(pixelCol, pixelRow)),
                                Color.green(buffer.getPixel(pixelCol, pixelRow)), blue));
                    } else {
                        int green;
                        if (bit == 0) {
                            green = Color.green(buffer.getPixel(pixelCol, pixelRow)) & 0xFE;
                        } else {
                            green = Color.green(buffer.getPixel(pixelCol, pixelRow)) | 0x1;
                        }
                        buffer.setPixel(pixelCol, pixelRow, Color.argb(
                                Color.alpha(buffer.getPixel(pixelCol, pixelRow)),
                                Color.red(buffer.getPixel(pixelCol, pixelRow)), green,
                                Color.blue(buffer.getPixel(pixelCol, pixelRow))));
                        incrementPixel(buffer.getWidth());
                    }
                    bitCount++;
                }
                if (i % 1024 == 0) {
                    publishProgress((int) ((((double) bitCount) / ((double) (params[1].length() * 8))) * 100));
                }
                val = stream.read();
                i++;
            }

            FileOutputStream out = new FileOutputStream(params[2]);
            buffer.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (OutOfMemoryError e) {
            throw new OutOfMemoryError("Not Enough RAM");
        } catch (Exception e) {
            Log.e(LOG_TAG, "exception", e);
        }
        Log.v(LOG_TAG, "done encoding");
        return params[2];
    }

    private void incrementPixel(int length) {
        pixelCol++;
        if (pixelCol == length) {
            pixelCol = 0;
            pixelRow++;
        }
    }

    private File endEncode(File... params) {
        try {
            byte[] imageBinary = new byte[(int) params[0].length()];
            try {
                BufferedInputStream buf = new BufferedInputStream(new FileInputStream(params[0]));
                buf.read(imageBinary, 0, imageBinary.length);
                buf.close();
            } catch (Exception e) {
                Log.e(LOG_TAG, "exception", e);
            }

            byte[] toEncodeBinary = new byte[(int) params[1].length()];
            try {
                BufferedInputStream buf = new BufferedInputStream(new FileInputStream(params[1]));
                buf.read(toEncodeBinary, 0, toEncodeBinary.length);
                buf.close();
            } catch (Exception e) {
                Log.e(LOG_TAG, "exception", e);
            }

            byte[] bytes = new byte[imageBinary.length + toEncodeBinary.length + 4];
            byte[] length = ByteBuffer.allocate(8).putLong(toEncodeBinary.length).array();

            int count = 0;
            for (byte element: imageBinary) {
                bytes[count] = element;
                count++;
            }
            for (byte element: length) {
                bytes[count] = element;
                count++;
            }
            for (byte element: toEncodeBinary) {
                bytes[count] = element;
                count++;
            }
            FileOutputStream out = new FileOutputStream(params[2]);
            out.write(bytes);
            out.flush();
            out.close();

        } catch (OutOfMemoryError e) {
            throw new OutOfMemoryError("Not Enough RAM");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return params[2];
    }
    @Override
    protected void onPostExecute(File file) {
        Intent intent = new Intent(this.c, ImageActivity.class);
        intent.putExtra(Encode.EXTRA_FILE_TAG, file.getAbsolutePath());
        c.startActivity(intent);
    }
}
