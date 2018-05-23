package com.example.airin.spammersms;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class Util {
    protected void sendEmail() {
        Thread thread=new Thread(){
            @Override
            public void run() {
                Log.i("FileLocation", "run: "+ Environment.getExternalStorageDirectory().toString()+ "notes/"+"File.txt");

                GMailSender sender = new GMailSender("gsmpostman@gmail.com","Airin123");
                try {
                    //  sender.sendMail("Error Log", "Hei Ini Ada Error", "gsmpostman@gmail.com", "airin@genusis.com","/storage/emulated/0/Notes/", "/storage/emulated/0/Notes/");
                    sender.sendMail("Error Log", "Hei Ini Ada Error", "gsmpostman@gmail.com", "airin@genusis.com", "/storage/emulated/0/Notes/File.txt");

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
        thread.start();
    }

    public void generateNoteOnSD(Context context, String sFileName, String sBody) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "Notes");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
//            Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeToFile(String data, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("config.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

}
