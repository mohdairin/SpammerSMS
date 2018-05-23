package com.example.airin.spammersms;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class DeliverReceiver extends BroadcastReceiver {
    DBFunction dbFunction=new DBFunction();
    @Override
    public void onReceive(Context context, Intent arg1) {
        String AMTID,am,am2;
        AMTID = arg1.getStringExtra("SMS_DELIVERED");
        am = arg1.getStringExtra("SMS_SENT");
        am2 = arg1.getStringExtra("id");
        switch (getResultCode()) {
            case Activity.RESULT_OK:
                Log.i("", "onReceive: "+arg1.getStringExtra("id"));
                //   Toast.makeText(getBaseContext(), "Message Delivered",
                //         Toast.LENGTH_SHORT).show();

                dbFunction.sqliteDelete(arg1.getStringExtra("id"));

                //sqliteDelete(AMTID);
               // nowSendingRate++;
                //Log.i("Amount Sending", "onReceive: "+nowSendingRate);

                break;
            case Activity.RESULT_CANCELED:
                break;
        }
    }
}
