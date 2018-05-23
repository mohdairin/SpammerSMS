package com.example.airin.spammersms;

import android.accessibilityservice.AccessibilityService;
import android.app.Activity;
import android.app.PendingIntent;
import android.arch.persistence.room.Room;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    BroadcastReceiver sendBroadcastReceiver = new SentReceiver();
    BroadcastReceiver deliveryBroadcastReciever = new DeliverReceiver();

    private RequestQueue requestQueue;
    Util util=new Util();
    static TextView tvQueue,tvSendingSuccess,tvSendingFailed,tvSuccessDelivered,tvLogFile;
    EditText etUrl,etSimCard,etTelco,etShortcode,etService,etAction,etThreshold;
    ToggleButton tb;
    ScrollView sv;
    LinearLayout llQueue,llSetting;
    int smsIDsent=0;
    int smsIDreceive=0;
    int nowSendingRate=0;
    int succesSending=0;
    int failedSending=0;
    int reportTime=0;
    int successDeliver=0;
    int jsonSize=0;
    int queue=0;
    boolean gettingJson=true;
    String logid;

    public static SMSValidationDatabase smsValidationDatabase;

    DBFunction dbFunction= new DBFunction();
    serverReport serverReport=new serverReport();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        smsValidationDatabase= Room.databaseBuilder(getApplicationContext(),
                SMSValidationDatabase.class,"smsValidation").fallbackToDestructiveMigration()
                .build();
        init();
      //  sendEmail();
        //accessSqlite();
        // sqliteGetdata();
        //   nukeTable();
        dbFunction.sqliteGetdata();
        dbFunction.nukeTable();
        serverReport.getRequestQueue(getApplicationContext());

       // handler.postDelayed(runnable, 0);
        getRequestQueue();
        //getJson();
      //  setupSMS();
    }

    private void init() {
       System.out.println("Hi");
        TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(this);
        sv=findViewById(R.id.svScrollLog);

        llQueue=findViewById(R.id.linearlayoutQueue);
        llSetting=findViewById(R.id.linearlayoutSetting);
        tb=findViewById(R.id.toggle);

        tvSendingSuccess=findViewById(R.id.tvSuccessSending);
        tvSendingFailed=findViewById(R.id.tvFailedSending);
        tvSuccessDelivered=findViewById(R.id.tvSuccessDeliver);
        tvLogFile=findViewById(R.id.tvLogFile);

        etUrl=findViewById(R.id.etUrl);
        etAction=findViewById(R.id.etAction);
        etService=findViewById(R.id.etService);
        etShortcode=findViewById(R.id.etShortcode);
        etSimCard=findViewById(R.id.etNumberPhone);
        etTelco=findViewById(R.id.etTelco);
        etThreshold=findViewById(R.id.etThreshold);


        boolean isSIM1Ready = telephonyInfo.isSIM1Ready();
        boolean isSIM2Ready = telephonyInfo.isSIM2Ready();

        boolean isDualSIM = telephonyInfo.isDualSIM();

       // TextView tv = (TextView) findViewById(R.id.textView);

        tvQueue = (TextView) findViewById(R.id.textView);
      /*  tv.setText("DUAL SIM : " + isDualSIM + "\n" +
                " IS SIM1 READY : " + isSIM1Ready + "\n" +
                " IS SIM2 READY : " + isSIM2Ready + "\n");*/
    }

    private boolean isAccessibilityOn (Context context, Class<? extends AccessibilityService> clazz) {
        int accessibilityEnabled = 0;
        final String service = context.getPackageName () + "/" + clazz.getCanonicalName ();
        try {
            accessibilityEnabled = Settings.Secure.getInt (context.getApplicationContext ().getContentResolver (), Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException ignored) {  }

        TextUtils.SimpleStringSplitter colonSplitter = new TextUtils.SimpleStringSplitter (':');

        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString (context.getApplicationContext ().getContentResolver (), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                colonSplitter.setString (settingValue);
                while (colonSplitter.hasNext ()) {
                    String accessibilityService = colonSplitter.next ();

                    if (accessibilityService.equalsIgnoreCase (service)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private void openWhatsApp() {


       // startActivity(sendIntent);
        // change with required  application package

            if (!isAccessibilityOn (getApplicationContext(), WhatsappAccessibilityService.class)) { Intent intent1 = new Intent (Settings.ACTION_ACCESSIBILITY_SETTINGS);
                String smsNumber = "60125433024"; // E164 format without '+' sign
                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                //  Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
                sendIntent.setType("text/plain");
                sendIntent.putExtra(Intent.EXTRA_TEXT, "test");
                sendIntent.putExtra("jid", smsNumber + "@s.whatsapp.net"); //phone number without "+" prefix
                sendIntent.setPackage("com.whatsapp");
               // intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
              //  startActivity(sendIntent);
               startActivity(intent1);
            }else{
                Intent intent1 = new Intent (Settings.ACTION_ACCESSIBILITY_SETTINGS);
                String smsNumber = "60125433024"; // E164 format without '+' sign
                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                //  Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
                sendIntent.setType("text/plain");
                sendIntent.putExtra(Intent.EXTRA_TEXT, "test");
                sendIntent.putExtra("jid", smsNumber + "@s.whatsapp.net"); //phone number without "+" prefix
                sendIntent.setPackage("com.whatsapp");
                //intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(sendIntent);
               // startActivity(intent1);
            }




    }


    public void changeView(View view) {
        if(view == findViewById(R.id.button1)) {
           llSetting.setVisibility(View.GONE);
           llQueue.setVisibility(View.VISIBLE);

        }else if(view == findViewById(R.id.button2)){
            llSetting.setVisibility(View.VISIBLE);
            llQueue.setVisibility(View.GONE);
            loadSetting();
        }else{
            saveSetting();
            showSetting();
        }

    }

    public void startButton(View view) {
        //getJson();
        openWhatsApp();
    }

    public void killApp(View view) {
        finish();
    }




    private void loadSetting() {
        SharedPreferences prfs = getSharedPreferences("AUTHENTICATION_FILE_NAME", Context.MODE_PRIVATE);
        String Service = prfs.getString("SERVICE", "");
        String Simcard = prfs.getString("SIMCARDNUMBER", "");
        String URL= prfs.getString("URL", "");
        String Telco= prfs.getString("TELCO", "");
        String Shortcode= prfs.getString("SHORTCODE", "");
        String Action= prfs.getString("ACTION", "");;
        String Threshold= prfs.getString("THRESHOLD", "");

        etUrl.setText(URL);
        etSimCard.setText(Simcard);
        etThreshold.setText(Threshold);
        etTelco.setText(Telco);
        etService.setText(Service);
        etShortcode.setText(Shortcode);
        etAction.setText(Action);
    }

    public void saveSetting()
    {
        SharedPreferences preferences = getSharedPreferences("AUTHENTICATION_FILE_NAME", Context.MODE_WORLD_WRITEABLE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("URL",etUrl.getText().toString());
        editor.putString("SIMCARDNUMBER",etSimCard.getText().toString());
        editor.putString("THRESHOLD",etThreshold.getText().toString());
        editor.putString("TELCO",etTelco.getText().toString());
        editor.putString("SHORTCODE",etShortcode.getText().toString());
        editor.putString("SERVICE",etService.getText().toString());
        editor.putString("ACTION",etAction.getText().toString());
        editor.apply();

        /*Intent sendIntent = new Intent("android.intent.action.MAIN");
        sendIntent.setComponent(new ComponentName("com.whatsapp","com.whatsapp.Conversation"));
        sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators("60125433024"));
        startActivity(sendIntent);*/

 }

    public void showSetting()
    {
        SharedPreferences prfs = getSharedPreferences("AUTHENTICATION_FILE_NAME", Context.MODE_PRIVATE);
        String Astatus = prfs.getString("SERVICE", "");
        String simcard = prfs.getString("SIMCARDNUMBER", "");
        String url = prfs.getString("URL", "");
        Log.i("Show", "URL: "+url+"\nSimCardNumber:"+simcard);
    }

    public void sendingCompleteListener()
    {
        Thread thread = new Thread() {
            @Override
            public void run() {
                if(jsonSize==nowSendingRate)
                {
                    Log.i("", "run: Sending");
                   // sendReportToServerFull(nowSendingRate);
                }

            }
        };
        thread.start();

    }

    private void getJson() {

        DateFormat df = DateFormat.getTimeInstance();
        df.setTimeZone(TimeZone.getTimeZone("UTC"));

        SharedPreferences prfs = getSharedPreferences("AUTHENTICATION_FILE_NAME", Context.MODE_PRIVATE);
        String Service = prfs.getString("SERVICE", "");
        final String Simcard = prfs.getString("SIMCARDNUMBER", "");
        String URL= prfs.getString("URL", "");
        String Telco= prfs.getString("TELCO", "");
        String Shortcode= prfs.getString("SHORTCODE", "");
        String Action= prfs.getString("ACTION", "");;
        String Threshold= prfs.getString("THRESHOLD", "");

        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        final String utcTime = sdf.format(new Date());
        Log.i("Start", "getJson: "+utcTime);
        //gettingJson=true;

        JSONObject json = new JSONObject();
        try {
            json.put("Action", Action);
            json.put("Service", Service);
            json.put("Telco", Telco);
            json.put("Shortcode", Shortcode);
            json.put("SenderID", Simcard);

            /*
            json.put("Action", "mt");
            json.put("Service", "gsm");
            json.put("Telco", "maxis");
            json.put("Shortcode", "gsm_celcom");
            json.put("SenderID", "60120000002");*/
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url1 = "http://gensuitedev3.genusis.com/api/gsm/";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("Volley", "onResponse: " + response.toString());
                        if(response.toString().equals("{\"Status\":\"no_record\"}")
                                || response.toString().equals("{\"Status\":\"Invalid_Json\"}")
                                || response.toString().equals("{\"Status\":\"Invalid_Json\"}")
                                || response.toString().equals("{\"Status\":\"invalid_access\"}")
                                || response.toString().equals("\"invalid_action\"")
                                || response.toString().equals("No Data")) {
                            Toast.makeText(getBaseContext(), response.toString(),
                                    Toast.LENGTH_SHORT).show();

                            if(response.toString().equals("{\"Status\":\"no_record\"}"))
                            {
                                tvLogFile.append("\nTime:"+utcTime);
                                tvLogFile.append("\nRequest From Server:"+"Success");
                                tvLogFile.append("\nSMS Amount:"+0);
                                sv.fullScroll(ScrollView.FOCUS_DOWN);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        getJson();
                                    }
                                }, 10000);
                            }else if(response.toString().equals("{\"Status\":\"invalid_access\"}")){
                                tvLogFile.append("\nTime:"+utcTime);
                                tvLogFile.append("\nRequest From Server:"+"Failed");
                                tvLogFile.append("\nError Log:"+response.toString());
                                tvLogFile.append("\nSMS Amount:"+0);
                                sv.fullScroll(ScrollView.FOCUS_DOWN);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        getJson();
                                    }
                                }, 10000);
                            }
                        }else
                        {
                            gettingJson=true;
                            Gson gson = new Gson();
                            final MessageToSend messageToSend = gson.fromJson(String.valueOf(response), MessageToSend.class);
                            System.out.println("Amount Of Message: " + messageToSend.getMT().size());
                            int l;
                            int j;
                            int saveSqlite;
                            logid= UUID.randomUUID().toString();
                            jsonSize=messageToSend.getMT().size();
                            queue=jsonSize;

                            tvLogFile.append("\nTime:"+utcTime);
                            tvLogFile.append("\nRequest From Server:"+"Success");
                            tvLogFile.append("\nSMS Amount:"+messageToSend.getMT().size());
                            sv.fullScroll(ScrollView.FOCUS_DOWN);
                            for (saveSqlite=0;saveSqlite<messageToSend.getMT().size();saveSqlite++){
                                dbFunction.setSqliteQueue(messageToSend.getMT().get(saveSqlite).getAMTID().toString(),messageToSend.getMT().get(saveSqlite).getMSISDN().toString(),messageToSend.getMT().get(saveSqlite).getMsg().toString(),"gsm_celcom","0192886050",logid);
                            }
                            serverReport.serverAcknowledgment(messageToSend.getBatchID(),"gsm",Simcard);
                            for (l = 0; l <messageToSend.getMT().size()/2; l++) {
                                Log.i("Index Process 1", "onResponse: "+l);
                                sendSMS(0, messageToSend.getMT().get(l).getMsg(),messageToSend.getMT().get(l).getMSISDN(),messageToSend.getMT().get(l).getAMTID(),0);
                            }

                            for(j=messageToSend.getMT().size()/2; j < messageToSend.getMT().size(); j++)
                            {
                                Log.i("Index Process 2", "onResponse: "+j);
                                sendSMS(0, messageToSend.getMT().get(j).getMsg(),messageToSend.getMT().get(l).getMSISDN(),messageToSend.getMT().get(j).getAMTID(),1);
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getBaseContext(), "Error Please Check Parameter",
                        Toast.LENGTH_SHORT).show();
                tvLogFile.append("\nTime:"+utcTime);
                tvLogFile.append("\nRequest From Server:"+"Failed");
                tvLogFile.append("\nError Log:"+error.toString());
                tvLogFile.append("\nSMS Amount:"+0);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getJson();
                    }
                }, 10000);

            }
        });
        jsonObjectRequest.setTag("a");
        requestQueue.add(jsonObjectRequest);
        sendingCompleteListener();
    }

    public void restartProcess(){
        final long period = 10;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                getJson();
                // do your task here
            }
        }, 5000, 10000);
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(this.getApplicationContext());
        }
        return requestQueue;
    }

    public void setupSim(Object messageToSent){


        for (int l = 1; l <= 100; l++) {
          //  sendSMS(0, "From Sim 1" + l,"01120557886");
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int l = 1; l <= 25; l++) {

         //           sendSMS(1, "From Sim 2" + l,"01115250688");
                    //      sendSMS(1,"From Sim 2"+l);

                    //  sendSMS(1,"From Sim 2"+l);
                    Log.i("Index", "" + l);
                }
            }
        }, 5000);

    }

    public void sendSMS(final int index, final String messageText, final String MSISDN, final String AMTID, int process) {
        final ArrayList<Integer> simCardList = new ArrayList<>();
        SubscriptionManager subscriptionManager = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            subscriptionManager = SubscriptionManager.from(this);
        }
        List<SubscriptionInfo> subscriptionInfoList = new List<SubscriptionInfo>() {
            @Override
            public int size() {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public boolean contains(Object o) {
                return false;
            }

            @NonNull
            @Override
            public Iterator<SubscriptionInfo> iterator() {
                return null;
            }

            @NonNull
            @Override
            public Object[] toArray() {
                return new Object[0];
            }

            @NonNull
            @Override
            public <T> T[] toArray(@NonNull T[] ts) {
                return null;
            }

            @Override
            public boolean add(SubscriptionInfo subscriptionInfo) {
                return false;
            }

            @Override
            public boolean remove(Object o) {
                return false;
            }

            @Override
            public boolean containsAll(@NonNull Collection<?> collection) {
                return false;
            }

            @Override
            public boolean addAll(@NonNull Collection<? extends SubscriptionInfo> collection) {
                return false;
            }

            @Override
            public boolean addAll(int i, @NonNull Collection<? extends SubscriptionInfo> collection) {
                return false;
            }

            @Override
            public boolean removeAll(@NonNull Collection<?> collection) {
                return false;
            }

            @Override
            public boolean retainAll(@NonNull Collection<?> collection) {
                return false;
            }

            @Override
            public void clear() {

            }

            @Override
            public boolean equals(Object o) {
                return false;
            }

            @Override
            public int hashCode() {
                return 0;
            }

            @Override
            public SubscriptionInfo get(int i) {
                return null;
            }

            @Override
            public SubscriptionInfo set(int i, SubscriptionInfo subscriptionInfo) {
                return null;
            }

            @Override
            public void add(int i, SubscriptionInfo subscriptionInfo) {

            }

            @Override
            public SubscriptionInfo remove(int i) {
                return null;
            }

            @Override
            public int indexOf(Object o) {
                return 0;
            }

            @Override
            public int lastIndexOf(Object o) {
                return 0;
            }

            @NonNull
            @Override
            public ListIterator<SubscriptionInfo> listIterator() {
                return null;
            }

            @NonNull
            @Override
            public ListIterator<SubscriptionInfo> listIterator(int i) {
                return null;
            }

            @NonNull
            @Override
            public List<SubscriptionInfo> subList(int i, int i1) {
                return null;
            }
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            subscriptionInfoList = subscriptionManager
                    .getActiveSubscriptionInfoList();
        }
        for (SubscriptionInfo subscriptionInfo : subscriptionInfoList) {
            int subscriptionId = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                subscriptionId = subscriptionInfo.getSubscriptionId();
            }
            simCardList.add(subscriptionId);
        }

        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";
        String SENT2 = "SMS_SENT2";
        String DELIVERED2 = "SMS_DELIVERED2";



        final int smsToSendFrom;
        final int smsToSendFrom1 = simCardList.get(0);
        final int smsToSendFrom2 = simCardList.get(1);

        if(tb.isChecked()){
           smsToSendFrom=simCardList.get(0);
        }else
        {
            smsToSendFrom=simCardList.get(1);
        }

        //assign your desired sim to send sms, or user selected choice
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            final SmsManager smsManager = null;
            final SmsManager smsManager01 = null;
            if(index==0)
            {
                Intent delivery = new Intent("SMS_DELIVERED");
                delivery.putExtra("id",AMTID);
                Intent sending = new Intent("SMS_SENT");
                sending.putExtra("id",AMTID);
                final PendingIntent sentPI = PendingIntent.getBroadcast(this,smsIDsent++,sending, PendingIntent.FLAG_CANCEL_CURRENT);
                final PendingIntent deliveredPI = PendingIntent.getBroadcast(this,smsIDreceive++,delivery, PendingIntent.FLAG_CANCEL_CURRENT);

               registerReceiver(sendBroadcastReceiver, new IntentFilter(SENT));

               registerReceiver(deliveryBroadcastReciever, new IntentFilter(DELIVERED));

//                registerReceiver(sendBroadcastReceiver2, new IntentFilter(SENT2));
//
 //               registerReceiver(deliveryBroadcastReciever2, new IntentFilter(DELIVERED2));
                //Log.i("Sim No", "sendSMS: "+index+AMTID);
                if(process==0){
                    Thread thread = new Thread() {
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
                        @Override
                        public void run() {
                           // Log.i("Check Id", "Before Send: "+MainActivity.smsRecordDatabase.smsRecordDao().checkSendAmtid(AMTID).toString());
                            //Log.i("Check Id", "Random Id"+MainActivity.smsRecordDatabase.smsRecordDao().checkSendAmtid("xasxaweqe"));
                            if(MainActivity.smsValidationDatabase.smsValidationDAO().checkSendAmtid(AMTID).toString().equals("[]"))
                            {
                                Log.i("Message Already Sent", "run: "+AMTID);
                            }else{
                                //SendSMS
                                Log.i("Send Messagge", "Process 1 run: "+MainActivity.smsValidationDatabase.smsValidationDAO().checkSendAmtid(AMTID).get(0).getId()+" "+MainActivity.smsValidationDatabase.smsValidationDAO().checkSendAmtid(AMTID).get(0).getMSISDN());
                              //  Log.i("Send Messagge", "Process 1 run: "+MainActivity.smsValidationDatabase.smsValidationDAO().checkSendAmtid(AMTID).get(1).getId());
                                smsManager.getSmsManagerForSubscriptionId(smsToSendFrom)
                                        .sendTextMessage(MSISDN, null, messageText+AMTID, sentPI, deliveredPI);
                                Log.i("Message Sent To", "run: "+MSISDN);


                            }
                        }
                    };
                    thread.start();
                }else{
                        Thread thread = new Thread() {
                            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
                            @Override
                            public void run() {
                                if(MainActivity.smsValidationDatabase.smsValidationDAO().checkSendAmtid(AMTID).toString().equals("[]")) {
                                    Log.i("Message Already Sent", "run: "+AMTID);
                                }else{
                                    //SendSMS
                                    Log.i("Send Messagge", "Process 2 run: "+index+AMTID);
                                    smsManager01.getSmsManagerForSubscriptionId(smsToSendFrom)
                                          .sendTextMessage(MSISDN, null, messageText+AMTID, sentPI, deliveredPI);
                                }
                            }
                        };
                        thread.start();
                }


            } else{
                PendingIntent sentPI = PendingIntent.getBroadcast(this, 1, new Intent(
                        SENT2), 0);

                PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 1,
                        new Intent(DELIVERED2), 0);




           //     registerReceiver(sendBroadcastReceiver2, new IntentFilter(SENT2));

             //   registerReceiver(deliveryBroadcastReciever2, new IntentFilter(DELIVERED2));
                Log.i("Sending", "sendSMS: From Sim 2");

           //     SmsManager.getSmsManagerForSubscriptionId(smsToSendFrom2)
             //          .sendTextMessage(MSISDN, null, messageText, sentPI, deliveredPI);

             //   smsManager11.getSmsManagerForSubscriptionId(smsToSendFrom2)
               //         .sendTextMessage("01115910688", null, text, sentPI, deliveredPI);
            }
        }


    }

    public void toggleFunction(View view) {
        if(tb.isChecked())
        {
            tb.setText("Sim1");
        }else
        {
            tb.setText("Sim2");
        }
    }


    class DeliverReceiver extends BroadcastReceiver {
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
                    successDeliver++;

                  //  dbFunction.sqliteDelete(arg1.getStringExtra("id"));

                   //sqliteDelete(AMTID);
                  //  nowSendingRate++;
                    Log.i("Amount Sending", "onReceive: "+nowSendingRate);

                    break;
                case Activity.RESULT_CANCELED:

                    Log.i("Undelivered", "Delivered: "+nowSendingRate);

                    break;
            }

            tvSuccessDelivered.setText("Delivered:"+successDeliver);

        }
    }

    class SentReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, final Intent arg1) {



            Log.i("Message Sent ", "In Queue For Sending");
            String AMTID,am;
            AMTID = arg1.getStringExtra("SMS_Damn");
            am = arg1.getStringExtra("SMS_SENT");
            DateFormat df = DateFormat.getTimeInstance();
            df.setTimeZone(TimeZone.getTimeZone("UTC"));

            final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            final String utcTime = sdf.format(new Date());
            final String Failed="FAILED";
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    nowSendingRate++;
                    succesSending++;
                    queue--;
                    Log.i("Message Sent ", "In Queue For Sending "+arg1.getStringExtra("id"));
                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            String Delivered="DELIVRD";
                            if(queue==0)
                            {
                                if(gettingJson){
                                    serverReport.sendReportToServerFull(jsonSize,logid,getApplicationContext());
                                    util.sendEmail();
                                    reportTime++;
                                    Log.i("Report Time", "run: "+reportTime);
                                   getJson();
                                   gettingJson=false;
                                }
                            }

                            Log.i("Sending Rate", "run: \nSuccess="+succesSending+"" +
                                    "\nSending"+nowSendingRate+"\nFailedSending"+failedSending);

                            MainActivity.smsValidationDatabase.smsValidationDAO().Update(utcTime,Delivered,arg1.getStringExtra("id"));
                            Log.i("Get Time", "run: "+ MainActivity.smsValidationDatabase.smsValidationDAO().getAll().get(0).getSenderTime()+MainActivity.smsValidationDatabase.smsValidationDAO().getAll().get(0).getStatusSend()+MainActivity.smsValidationDatabase.smsValidationDAO().getAll().get(0).getLogId());
                            }
                    };
                    thread.start();
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    Toast.makeText(getBaseContext(), "Generic failure",
                            Toast.LENGTH_SHORT).show();
                    nowSendingRate++;
                    failedSending++;
                    queue--;

                    Thread thread2 = new Thread() {
                        @Override
                        public void run() {
                            Log.i("Error","Failed Report"+" AMTID:"+arg1.getStringExtra("id"));
                            MainActivity.smsValidationDatabase.smsValidationDAO().Update(utcTime,Failed,arg1.getStringExtra("id"));
                            dbFunction.sqliteGetFailedSMS();
                        }
                    };
                    thread2.start();




//                    MainActivity.smsValidationDatabase.smsValidationDAO().Update(utcTime,Failed,arg1.getStringExtra("id"));
                    Log.i("Error","Generic Failure Sim 1"+" AMTID:"+arg1);
                    //reportFailedToServer


                    Log.e("Error","Generic Failure Sim 1"+" AMTID:"+arg1);
                    break;
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                    Toast.makeText(getBaseContext(), "No service",
                            Toast.LENGTH_SHORT).show();
                    nowSendingRate++;
                    failedSending++;
                    queue--;



                    Thread thread3= new Thread() {
                        @Override
                        public void run() {
                            MainActivity.smsValidationDatabase.smsValidationDAO().Update(utcTime,Failed,arg1.getStringExtra("id"));

                        }
                    };
                    thread3.start();

                    Log.e("Error","No Service");
                    Log.i("Error","No Service Sim 1"+" AMTID:"+arg1);

                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU:
                    Toast.makeText(getBaseContext(), "Null PDU", Toast.LENGTH_SHORT)
                            .show();
                    Log.e("Error","Null PDU");
                    Log.i("Error","Null PDU"+" AMTID:"+arg1);
                    nowSendingRate++;
                    failedSending++;
                    queue--;
                    Thread thread4 = new Thread() {
                        @Override
                        public void run() {
                            MainActivity.smsValidationDatabase.smsValidationDAO().Update(utcTime,Failed,arg1.getStringExtra("id"));
                        }
                    };
                    thread4.start();

                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    Toast.makeText(getBaseContext(), "Radio off",
                            Toast.LENGTH_SHORT).show();
                    Log.i("Error","Radio Off"+" AMTID:"+arg1);
                    nowSendingRate++;
                    failedSending++;
                    queue--;
                    break;
            }

            tvSendingSuccess.setText("Success Send:"+succesSending);
            tvSendingFailed.setText("Failed Send:"+failedSending);
            tvQueue.setText("SMS Queue:"+queue);
        }
    }

}






