package com.example.airin.spammersms;

import android.app.DownloadManager;
import android.util.Log;

import java.util.Random;

import static android.content.ContentValues.TAG;
import static com.example.airin.spammersms.MainActivity.*;

public class DBFunction {


    public void setSqliteQueue(final String amtid, final String msisdn,final String msg,final String shortcode,final String SenderId,final String UUID)
    {
        /*
        Thread thread = new Thread() {
            @Override
            public void run() {
                Log.i("Save Sqlite", "run: "+amtid);
                SMSRecord smsRecord= new SMSRecord();
                Random r = new Random();

                int a = r.nextInt((1000000000-10)+1)+10;
                Log.i("", "Random: "+a);
                smsRecord.setId(a);
                smsRecord.setAmtid(amtid);
                MainActivity.smsRecordDatabase.smsRecordDao().addUser(smsRecord);

            }
        };
        thread.start();
        */

        Thread thread = new Thread() {
            @Override
            public void run() {
                Log.i("Save Sqlite", "run: "+amtid);
                SMSValidation smsValidation= new SMSValidation();
                Random r = new Random();

                int a = r.nextInt((1000000000-10)+1)+10;
                Log.i("", "Random: "+a);
                smsValidation.setId(amtid);
                smsValidation.setMSISDN(msisdn);
                smsValidation.setMessage(msg);
                smsValidation.setSenderID(SenderId);
                smsValidation.setShortcode(shortcode);
                smsValidation.setLogId(UUID);
               // smsValidation.setAstatus();
                MainActivity.smsValidationDatabase.smsValidationDAO().addUser(smsValidation);
                //   MainActivity.smsRecordDatabase.smsRecordDao().addUser(smsRecord);

            }
        };
        thread.start();



    }

    public void sqliteDelete(final String amtid)
    {
        Thread thread = new Thread() {
            @Override
            public void run() {
                SMSValidation smsValidation= new SMSValidation();
                smsValidation.setId(amtid);
                //   smsValidation.setMSISDN("60122213509");
                // smsValidation.setMessage("This is GSM testing");
                //MainActivity.smsRecordDatabase.smsRecordDao().delete(smsRecord);
                MainActivity.smsValidationDatabase.smsValidationDAO().delete(smsValidation);
                sqliteGetdata();
            }
        };
        thread.start();

    }

    public void nukeTable()
    {
        Thread thread = new Thread() {
            @Override
            public void run() {
                MainActivity.smsValidationDatabase.smsValidationDAO().nukeTable();
            }
        };
        thread.start();

    }

    public void sqliteGetdata()
    {
        Thread thread = new Thread() {
            @Override
            public void run() {
                System.out.println("Hi Show Sqlite Size Validation"+MainActivity.smsValidationDatabase.smsValidationDAO().getAll().size());
            }
        };
        thread.start();
    }


    public void sqliteGetFailedSMS()
    {
        Thread thread = new Thread() {
            @Override
            public void run() {
            //    System.out.println("Sqlite Failed"+MainActivity.smsValidationDatabase.smsValidationDAO().checkFailedAmount("GAGAL").get(0).getClass());
              //  System.out.println("Sqlite Failed"+MainActivity.smsValidationDatabase.smsValidationDAO().checkFailedAmount("GAGAL").toString());
               // System.out.println("Sqlite Failed"+MainActivity.smsValidationDatabase.smsValidationDAO().checkFailedAmount("GAGAL").get(0).getStatusSend());
               // System.out.println("Sqlite Failed"+MainActivity.smsValidationDatabase.smsValidationDAO().checkFailedAmount("GAGAL").size());

            }
        };
        thread.start();
    }



}
