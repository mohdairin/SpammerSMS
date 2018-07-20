package com.example.airin.spammersms;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.arch.persistence.room.Room;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
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
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

import cz.msebera.android.httpclient.entity.StringEntity;
import okhttp3.MediaType;

public class MainActivity extends AppCompatActivity {

    public static final MediaType MEDIA_TYPE =
            MediaType.parse("application/json");
    public static SMSValidationDatabase smsValidationDatabase;
    static TextView tvQueue, tvSendingSuccess, tvSendingFailed, tvSuccessDelivered, tvLogFile
            ,tvSimUse;
    private static RequestQueue requestQueue;
    Comms Communication;
    BroadcastReceiver sendBroadcastReceiver = new SentReceiver();
    BroadcastReceiver deliveryBroadcastReciever = new DeliverReceiver();
    BroadcastReceiver sendBroadcastReceiverMulti = new MultiSmsSentReceiver();
    BroadcastReceiver deliveryBroadcastRecieverMulti = new MultiSmsDeliveredReceiver();
    Util util = new Util();
    EditText etUrl,etClientId, etSimCard, etTelco, etShortcode,
            etService, etAction, etThreshold, etSMSLimit,etSMSLimit2;
    ToggleButton tb;
    ScrollView sv;
    LinearLayout llQueue, llSetting;
    int smsIDsent = 0;
    int smsIDreceive = 0;
    int nowSendingRate = 0;
    int succesSending = 0;
    int failedSending = 0;
    int reportTime = 0;
    int successDeliver = 0;
    int jsonSize = 0;
    int queue = 0;
    int sim=0;
    int minusMultiArray=0;
    boolean gettingJson = true;
    String logid;
    DBFunction dbFunction = new DBFunction();
    serverReport serverReport = new serverReport();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
/*
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert");
        builder.setMessage("Sim 1 and 2 Limit Reach");

        // add a button
        builder.setPositiveButton("OK", null);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();*/

        permissionCheck();
        smsValidationDatabase = Room.databaseBuilder(getApplicationContext(),
                SMSValidationDatabase.class, "smsValidation").fallbackToDestructiveMigration()
                .build();
        init();
        //  sendEmail();
        dbFunction.sqliteGetdata();
        dbFunction.nukeTable();
        serverReport.getRequestQueue(getApplicationContext());

        getRequestQueue();
    }

    public void permissionCheck() {

        if (Build.VERSION.SDK_INT >= 23) {
            RuntimePermissionHelper runtimePermissionHelper;
            runtimePermissionHelper = RuntimePermissionHelper.getInstance(this);
            if (runtimePermissionHelper.isAllPermissionAvailable()) {
// All permissions available. Go with the flow
            } else {
// Few permissions not granted. Ask for ungranted permissions
                runtimePermissionHelper.setActivity(this);
                runtimePermissionHelper.requestPermissionsIfDenied();
            }
        } else {
// SDK below API 23. Do nothing just go with the flow.
        }
    }


    private void init() {
        System.out.println("Hi");
        TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(this);
        sv = findViewById(R.id.svScrollLog);

        llQueue = findViewById(R.id.linearlayoutQueue);
        llSetting = findViewById(R.id.linearlayoutSetting);
        tb = findViewById(R.id.toggle);

        tvSendingSuccess = findViewById(R.id.tvSuccessSending);
        tvSendingFailed = findViewById(R.id.tvFailedSending);
        tvSuccessDelivered = findViewById(R.id.tvSuccessDeliver);
        tvLogFile = findViewById(R.id.tvLogFile);
        tvSimUse= findViewById(R.id.tvSimUse);


        etUrl = findViewById(R.id.etUrl);
        etAction = findViewById(R.id.etAction);
        etService = findViewById(R.id.etService);
        etShortcode = findViewById(R.id.etShortcode);
        etSimCard = findViewById(R.id.etNumberPhone);
        etTelco = findViewById(R.id.etTelco);
        etThreshold = findViewById(R.id.etThreshold);
        etSMSLimit = findViewById(R.id.etSMSLimit);
        etSMSLimit2 = findViewById(R.id.etSMSLimit2);
        etClientId= findViewById(R.id.etClientId);



        boolean isSIM1Ready = telephonyInfo.isSIM1Ready();
        boolean isSIM2Ready = telephonyInfo.isSIM2Ready();

        boolean isDualSIM = telephonyInfo.isDualSIM();

        // TextView tv = (TextView) findViewById(R.id.textView);

        tvQueue = (TextView) findViewById(R.id.textView);
      /*  tv.setText("DUAL SIM : " + isDualSIM + "\n" +
                " IS SIM1 READY : " + isSIM1Ready + "\n" +
                " IS SIM2 READY : " + isSIM2Ready + "\n");*/
    }

    public void changeView(View view) {
        if (view == findViewById(R.id.button1)) {
            llSetting.setVisibility(View.GONE);
            llQueue.setVisibility(View.VISIBLE);

        } else if (view == findViewById(R.id.button2)) {
            llSetting.setVisibility(View.VISIBLE);
            llQueue.setVisibility(View.GONE);
            loadSetting();
        } else {
            saveSetting();
            showSetting();
        }

    }

    private void loadSetting() {
        SharedPreferences prfs = getSharedPreferences("AUTHENTICATION_FILE_NAME", Context.MODE_PRIVATE);
        String Service = prfs.getString("SERVICE", "");
        String Simcard = prfs.getString("SIMCARDNUMBER", "");
        String URL = prfs.getString("URL", "");
        String ClientID = prfs.getString("CLIENTID", "");

        String Telco = prfs.getString("TELCO", "");
        String Shortcode = prfs.getString("SHORTCODE", "");
        String Action = prfs.getString("ACTION", "");
        String Threshold = prfs.getString("THRESHOLD", "");
        String SmsLimit = prfs.getString("SMSLIMIT", "");
        String SmsLimit2 = prfs.getString("SMSLIMIT2", "");


        etUrl.setText(URL);
        etClientId.setText(ClientID);
        etSimCard.setText(Simcard);
        etThreshold.setText(Threshold);
        etTelco.setText(Telco);
        etService.setText(Service);
        etShortcode.setText(Shortcode);
        etAction.setText(Action);
        etSMSLimit.setText(SmsLimit);
        etSMSLimit2.setText(SmsLimit2);

    }

    public void saveSetting() {
        SharedPreferences preferences = getSharedPreferences("AUTHENTICATION_FILE_NAME", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("CLIENTID", etClientId.getText().toString());
        editor.putString("URL", etUrl.getText().toString());
        editor.putString("SIMCARDNUMBER", etSimCard.getText().toString());
        editor.putString("THRESHOLD", etThreshold.getText().toString());
        editor.putString("TELCO", etTelco.getText().toString());
        editor.putString("SHORTCODE", etShortcode.getText().toString());
        editor.putString("SERVICE", etService.getText().toString());
        editor.putString("ACTION", etAction.getText().toString());
        editor.putString("SMSLIMIT", etSMSLimit.getText().toString());
        editor.putString("SMSLIMIT2", etSMSLimit2.getText().toString());

        editor.apply();

    }

    public void showSetting() {
        SharedPreferences prfs = getSharedPreferences("AUTHENTICATION_FILE_NAME", Context.MODE_PRIVATE);
        String Astatus = prfs.getString("SERVICE", "");
        String simcard = prfs.getString("SIMCARDNUMBER", "");
        String url = prfs.getString("URL", "");
        Log.i("Show", "URL: " + url + "\nSimCardNumber:" + simcard);
    }


    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(this.getApplicationContext());
        }
        return requestQueue;
    }

    private void trustEveryone() {
        try {
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[]{new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(
                    context.getSocketFactory());
        } catch (Exception e) { // should never happen
            e.printStackTrace();
        }
    }


    public void startButton(View view) {
        SharedPreferences prfs = getSharedPreferences("AUTHENTICATION_FILE_NAME", Context.MODE_PRIVATE);
        final String smsLimit=prfs.getString("SMSLIMIT","");
        int smsLimitAmount = Integer.parseInt(smsLimit);
        int jsonAmountDownload=smsLimitAmount-nowSendingRate;

        if(jsonAmountDownload>nowSendingRate)
        {
            getJson(0);
            //   Log.i("SMSLimit", "Now Sending Rate: "+nowSendingRate);
            // Log.i("SMSLimit", "onResponse: "+smsLimit);
        }else{

            Toast.makeText(MainActivity.this, "Full Already", Toast.LENGTH_SHORT).show();
//            getJson(1);
        }

    }

    public String unicodeFormat(String text) {
        String utf8Text = text;
        String utf89Text = "RM0.00 FROM 2L DESIGN KEPONG \u4eb2\u7231\u7684\u987e\u5ba2,\u60a8\u8ba2\u7684\u8863\u670d\u5df2\u5230\u8fbe\u672c\u5e97,\u8bf7\u643a\u5e26\u6536\u636e\u65b9\u4fbf\u9886\u53d6.\u8c22\u8c22";
        byte[] bytes = utf8Text.getBytes(StandardCharsets.UTF_8);
        byte[] bytes1 = utf89Text.getBytes(StandardCharsets.UTF_8);

        String convertText = new String(bytes, StandardCharsets.UTF_8);
        String text1 = new String(bytes1, StandardCharsets.UTF_8);

        Log.i("8", "startButton: " + text1);
        Log.i("8", "startButton: " + convertText);

        return convertText;
    }

    public void sentMultipartSms(final int sim, String text, final String MSISDN, final String AMTID, int process) {
        minusMultiArray=0;

        final SmsManager sm = SmsManager.getDefault();

        final ArrayList<String> parts = sm.divideMessage(text);
        final int numParts = parts.size();

      //  queue=queue+numParts;
        //jsonSize=jsonSize+numParts;

        Log.i("DivideMessage", "sentMultipartSms: "+text.length()+" Process"+process+"Que:"+queue);




        final ArrayList<PendingIntent> sentIntents = new ArrayList<PendingIntent>();
        final ArrayList<PendingIntent> deliveryIntents = new ArrayList<PendingIntent>();

        registerReceiver(sendBroadcastReceiverMulti, new IntentFilter("SENT"));
        registerReceiver(deliveryBroadcastRecieverMulti, new IntentFilter("DELIVERED"));


        final PendingIntent sentPI = PendingIntent.getBroadcast(getApplicationContext(), 0,
                new Intent(getApplicationContext(), MultiSmsSentReceiver.class), 0);

        final PendingIntent deliveredPI = PendingIntent.getBroadcast(getApplicationContext(), 0,
                new Intent(getApplicationContext(), MultiSmsDeliveredReceiver.class), 0);



        if (process == 0) {
            Thread thread = new Thread() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
                @Override
                public void run() {
//Testing Purpose
                    //  smsManager.getSmsManagerForSubscriptionId(smsToSendFrom)
                    //        .sendTextMessage(MSISDN, null, messageText+AMTID, sentPI, deliveredPI);
                    //Log.i("Message Sent To", "run: "+MSISDN);

                    //            smsManager.getSmsManagerForSubscriptionId(smsToSendFrom)
                    //                  .sendTextMessage(MSISDN, null, messageText + AMTID, sentPI, deliveredPI);
                    //    ArrayList<String> parts = smsManager.divideMessage(messageText);
                    //  smsManager.sendMultipartTextMessage(MSISDN,null,parts,sentIntents,deliveryIntents);



         //          Log.i("Check Id", "Before Send: "+MainActivity.smsRecordDatabase.smsRecordDao().checkSendAmtid(AMTID).toString());
  //                  Log.i("Check Id", "Random Id"+MainActivity.smsRecordDatabase.smsRecordDao().checkSendAmtid("xasxaweqe"));
                    if(MainActivity.smsValidationDatabase.smsValidationDAO().checkSendAmtid(AMTID).toString().equals("[]"))
                    {
                        queue--;
                        Log.i("Message Already Sent", "run: "+AMTID);
                    }else{
                        int size = 0;

                      //  Log.i("SizeArray", "run: "+AMTID);


                        for (int i = 0; i < numParts; i++) {

                      //     queue++;
                        //   jsonSize++;
                            sentIntents.add(
                                    PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("SENT"),
                                            0));
                            deliveryIntents.add(i, deliveredPI);
                            size++;

                        }

                        queue--;

                        minusMultiArray=size;

                       // queue--;




                        Log.i("SizeArray", "run: "+size+" Queue"+queue+" JsonSize"+jsonSize+" Parts"+numParts);



                        sm.getSmsManagerForSubscriptionId(sim).sendMultipartTextMessage(MSISDN, null, parts, sentIntents, deliveryIntents);
                        //SendSMS
                        Log.i("Send Messagge", "Process 1 run: "+MainActivity.smsValidationDatabase.smsValidationDAO().checkSendAmtid(AMTID).get(0).getId()+" "+MainActivity.smsValidationDatabase.smsValidationDAO().checkSendAmtid(AMTID).get(0).getMSISDN());
                        //  Log.i("Send Messagge", "Process 1 run: "+MainActivity.smsValidationDatabase.smsValidationDAO().checkSendAmtid(AMTID).get(1).getId());
                      //  smsManager.getSmsManagerForSubscriptionId(smsToSendFrom)
                        //        .sendTextMessage(MSISDN, null, messageText+AMTID, sentPI, deliveredPI);
                        //Log.i("Message Sent To", "run: "+MSISDN);


                    }

                }
            };
            thread.start();
        } else {
            Thread thread = new Thread() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
                @Override
                public void run() {
                    if (MainActivity.smsValidationDatabase.smsValidationDAO().checkSendAmtid(AMTID).toString().equals("[]")) {
                        Log.i("Message Already Sent", "run: " + AMTID);
                    } else {
                        //SendSMS
                        //    Log.i("Send Messagge", "Process 2 run: "+index+AMTID);
                        //      smsManager01.getSmsManagerForSubscriptionId(smsToSendFrom)
                        //              .sendTextMessage(MSISDN, null, messageText+AMTID, sentPI, deliveredPI);
                        //          ArrayList<String> parts = smsManager01.divideMessage(message);
                        //            smsManager01.sendMultipartTextMessage(MSISDN,null,"",null,null);
                    }
                }
            };
            thread.start();
        }
    }

    public void killApp(View view) {
        finish();
    }

    public void sendingCompleteListener() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                if (jsonSize == nowSendingRate) {
                    Log.i("", "run: Sending");
                    // sendReportToServerFull(nowSendingRate);
                }

            }
        };
        thread.start();

    }


    private void getJson(final int simNumber) {

        DateFormat df = DateFormat.getTimeInstance();
        df.setTimeZone(TimeZone.getTimeZone("UTC"));



        SharedPreferences prfs = getSharedPreferences("AUTHENTICATION_FILE_NAME", Context.MODE_PRIVATE);
        final String Service = prfs.getString("SERVICE", "");
        final String ClientID = prfs.getString("CLIENTID", "");

        final String Simcard = prfs.getString("SIMCARDNUMBER", "");
        String URL = prfs.getString("URL", "");
        final String Telco = prfs.getString("TELCO", "");
        final String Shortcode = prfs.getString("SHORTCODE", "");
        final String Action = prfs.getString("ACTION", "");
        String Threshold = prfs.getString("THRESHOLD", "");


        final String smsLimit;

        if(simNumber==0){
            smsLimit=prfs.getString("SMSLIMIT","");
        }else{
            smsLimit=prfs.getString("SMSLIMIT2","");
        }


        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        final String utcTime = sdf.format(new Date());
        Log.i("Sim Number", "Currently Sim: " + simNumber);
        int thresholdForJson;


        int threshold=Integer.parseInt(Threshold);
        int limitCounter=Integer.parseInt(smsLimit)-nowSendingRate;
        /*if(minusMultiArray==0)
        {
            limitCounter
        }else{
            limitCounter=Integer.parseInt(smsLimit)-nowSendingRate+minusMultiArray;
        }*/


        if(limitCounter>threshold){
            thresholdForJson=threshold;
            Log.i("Threshold", "getJson: "+thresholdForJson);
        }else{
            thresholdForJson=limitCounter;
            Log.i("Threshold", "getJson: "+thresholdForJson);

        }


        //gettingJson=true;

        JSONObject json = new JSONObject();
        try {
            json.put("ClientID",ClientID);
            json.put("Action", Action);
            json.put("Service", Service);
            json.put("Telco", Telco);
            json.put("Shortcode", Shortcode);
            json.put("SenderID", Simcard);
            json.put("Threshold",thresholdForJson);

            /*
            json.put("Action", "mt");
            json.put("Service", "gsm");
            json.put("Telco", "maxis");
            json.put("Shortcode", "gsm_celcom");
            json.put("SenderID", "60120000002");*/
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Map<String, String> data = new HashMap<String, String>();
        data.put("Action", Action);
        data.put("Service", Service);
        data.put("Telco", Telco);
        data.put("Shortcode", Shortcode);
        data.put("SenderID", Simcard);

        JSONObject parameters = new JSONObject(data);


        String url1 = "http://gensuitedev3.genusis.com/api/gsm/";
        Log.i("CheckURL", "getJson: " + json);

        HttpsTrustManager.allowAllSSL();

        Log.i("Check Url", "getJson: " + URL);
        Log.i("", "getJson: " + parameters);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("Volley", "onResponse: " + response.toString());
                        if (response.toString().equals("{\"Status\":\"no_record\"}")
                                || response.toString().equals("{\"Status\":\"invalid_json\"}")
                                || response.toString().equals("{\"Status\":\"Invalid_Json\"}")
                                || response.toString().equals("{\"Status\":\"invalid_access\"}")
                                || response.toString().equals("{\"Status\":\"failed_trans\"}")
                                || response.toString().equals("\"invalid_action\"")
                                || response.toString().equals("No Data")) {
                            Toast.makeText(getBaseContext(), response.toString(),
                                    Toast.LENGTH_SHORT).show();

                            if (response.toString().equals("{\"Status\":\"no_record\"}")) {
                                SharedPreferences prfs = getSharedPreferences("AUTHENTICATION_FILE_NAME", Context.MODE_PRIVATE);
                                final String Threshold = prfs.getString("THRESHOLD", "");
                                final String smsLimit=prfs.getString("SMSLIMIT","");

                                Log.i("Start", "getJson: " + utcTime);

                                tvLogFile.append("\nTime:" + utcTime);
                                tvLogFile.append("\nRequest From Server:" + "Success");
                                tvLogFile.append("\nSMS Amount:" + 0);
                                sv.fullScroll(ScrollView.FOCUS_DOWN);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        int thresholdForJson;

                                        int threshold=Integer.parseInt(Threshold);

                                        int limitCounter=Integer.parseInt(smsLimit)-nowSendingRate;

                                        Log.i("ChangeSim", "run: "+nowSendingRate);

                                        if(limitCounter>threshold){
                                            thresholdForJson=threshold;
                                            Log.i("Threshold", "getJson: "+thresholdForJson);
                                            getJson(0);
                                        }else if(nowSendingRate==Integer.parseInt(smsLimit)){

                                            nowSendingRate=0;
                                            if(simNumber==1)
                                            {
                                                Log.i("Finish", "No more Sending");
                                            }else
                                            {
                                                thresholdForJson=limitCounter;
                                                Log.i("Threshold", "Sim Change: "+"Change Sim 2");
                                                getJson(1);
                                                sim=1;
                                            }
                                        }else{
                                            //Log.i("Threshold", "getJson: "+"DownloadLess");
                                          //  getJson(0);

                                        }
                                    }
                                }, 10000);
                            } else if (response.toString().equals("{\"Status\":\"invalid_access\"}")) {
                                tvLogFile.append("\nTime:" + utcTime);
                                tvLogFile.append("\nRequest From Server:" + "Failed");
                                tvLogFile.append("\nError Log:" + response.toString());
                                tvLogFile.append("\nSMS Amount:" + 0);
                                sv.fullScroll(ScrollView.FOCUS_DOWN);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        getJson(0);
                                    }
                                }, 10000);
                            }
                        } else {
                            gettingJson = true;
                            Gson gson = new Gson();
                            final MessageToSend messageToSend = gson.fromJson(String.valueOf(response), MessageToSend.class);
                         //   System.out.println("Amount Of Message: " + messageToSend.getMT().size());
                            int l;
                            int j;
                            int saveSqlite;
                            logid = UUID.randomUUID().toString();
                            jsonSize = messageToSend.getMT().size();
                            queue = jsonSize;

                            tvLogFile.append("\nTime:" + utcTime);
                            tvLogFile.append("\nRequest From Server:" + "Success");
                            tvLogFile.append("\nSMS Amount:" + messageToSend.getMT().size());
                            sv.fullScroll(ScrollView.FOCUS_DOWN);
                            for (saveSqlite = 0; saveSqlite < messageToSend.getMT().size(); saveSqlite++) {
                                dbFunction.setSqliteQueue(messageToSend.getMT().get(saveSqlite).getAMTID().toString(), messageToSend.getMT().get(saveSqlite).getMSISDN().toString(), messageToSend.getMT().get(saveSqlite).getMsg().toString(), "gsm_celcom", "0192886050", logid);
                            }
                            serverReport.serverAcknowledgment(messageToSend.getBatchID(), "gsm", Simcard);
                            int smsLimitAmount = Integer.parseInt(smsLimit);

                            Log.i("SMSLimit", "onResponse: "+smsLimit);

                            if(nowSendingRate == smsLimitAmount)
                            {
                                Toast.makeText(MainActivity.this, "Full Already", Toast.LENGTH_SHORT).show();
                            }else{
                                for (l = 0; l < messageToSend.getMT().size() / 2; l++) {
                                    Log.i("SMSLimit", "Now Sending Rate: "+nowSendingRate);
                                    Log.i("SMSLimit", "onResponse: "+smsLimit);
                                    if(nowSendingRate > smsLimitAmount)
                                    {
                                     //   Log.i("SMSLimit", "Now Sending Rate: "+nowSendingRate);
                                       // Log.i("SMSLimit", "onResponse: "+smsLimit);
                                        Toast.makeText(MainActivity.this, "Full Already", Toast.LENGTH_SHORT).show();
                                    }else{
                                        if(messageToSend.getMT().get(l).getMsgType().equals("U")){
                                            String formatUnicode=unicodeFormat(messageToSend.getMT().get(l).getMsg());
                                            if(formatUnicode.length()>160)
                                            {
                                              //  jsonSize++;
                                               // queue++;
                                                sentMultipartSms(simNumber,formatUnicode,messageToSend.getMT().get(l).getMSISDN(),messageToSend.getMT().get(l).getAMTID(),0);
                                            }else{
                                                //SendNormal
                                                sendSMS(simNumber, formatUnicode, messageToSend.getMT().get(l).getMSISDN(), messageToSend.getMT().get(l).getAMTID(), 0);

                                            }
                                        }else{
                                            if(messageToSend.getMT().get(l).getMsg().length()>160)
                                            {
                                                Log.i("LongSMS", "onResponse: ");
                                            //    jsonSize++;
                                             //   queue++;

                                                sentMultipartSms(simNumber,messageToSend.getMT().get(l).getMsg(),messageToSend.getMT().get(l).getMSISDN(),messageToSend.getMT().get(l).getAMTID(),0);
                                            }else{
                                                //SendNormal
                                                sendSMS(simNumber, messageToSend.getMT().get(l).getMsg(), messageToSend.getMT().get(l).getMSISDN(), messageToSend.getMT().get(l).getAMTID(), 0);
                                            }

                                        }
                                    }

                                }

                                for (j = messageToSend.getMT().size() / 2; j < messageToSend.getMT().size(); j++) {
                                    Log.i("Index Process 2", "onResponse: " + j);
                                    // sendSMS(0, messageToSend.getMT().get(j).getMsg(), messageToSend.getMT().get(l).getMSISDN(), messageToSend.getMT().get(j).getAMTID(), 1);
                                    if(messageToSend.getMT().get(j).getMsgType().equals("U")){
                                        String formatUnicode=unicodeFormat(messageToSend.getMT().get(j).getMsg());
                                        if(formatUnicode.length()>160)
                                        {
                                      //      jsonSize++;
                                        //    queue++;
                                            sentMultipartSms(simNumber,formatUnicode,messageToSend.getMT().get(j).getAMTID(),messageToSend.getMT().get(j).getMSISDN(),0);
                                        }else{
                                            //SendNormal
                                            sendSMS(simNumber, formatUnicode, messageToSend.getMT().get(j).getMSISDN(), messageToSend.getMT().get(j).getAMTID(), 0);

                                        }
                                    }else{
                                        if(messageToSend.getMT().get(j).getMsg().length()>160)
                                        {
                                         //   jsonSize++;
                                           // queue++;
                                            sentMultipartSms(simNumber,messageToSend.getMT().get(j).getMsg(),messageToSend.getMT().get(j).getMSISDN(),messageToSend.getMT().get(j).getAMTID(),0);
                                        }else{
                                            //SendNormal
                                            sendSMS(simNumber, messageToSend.getMT().get(j).getMsg(), messageToSend.getMT().get(j).getMSISDN(), messageToSend.getMT().get(j).getAMTID(), 0);

                                        }

                                    }
                                }
                            }


                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getBaseContext(), "Error Please Check Parameter",
                        Toast.LENGTH_SHORT).show();
                tvLogFile.append("\nTime:" + utcTime);
                tvLogFile.append("\nRequest From Server:" + "Failed");
                tvLogFile.append("\nError Log:" + error.toString());
                tvLogFile.append("\nSMS Amount:" + 0);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getJson(0);
                    }
                }, 10000);

            }
        });
        jsonObjectRequest.setTag("a");
        requestQueue.add(jsonObjectRequest);
        sendingCompleteListener();
    }

    public void restartProcess() {
        final long period = 10;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                getJson(1);
                // do your task here
            }
        }, 5000, 10000);
    }

    public void setupSim(Object messageToSent) {


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
       // final int smsToSendFrom1 = simCardList.get(0);
        //smsToSendFrom = simCardList.get(1);


        int smsToSendFrom2;
     //   smsToSendFrom = simCardList.get(0);
        TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(this);

        if(index==0)
        {
            smsToSendFrom = simCardList.get(0);
        }else
        {
           smsToSendFrom = simCardList.get(1);
        }


        boolean isDualSIM = telephonyInfo.isDualSIM();
        if(isDualSIM)
        {
            boolean isSIM2Ready = telephonyInfo.isSIM2Ready();
            if(isSIM2Ready){
                smsToSendFrom2 = simCardList.get(1);
            }
        }else{
            Log.i("Dual Sim", "sendSMS: "+"No");
        }


        //assign your desired sim to send sms, or user selected choice
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            final SmsManager smsManager = null;
            final SmsManager smsManager01 = null;
            if (index != 5) {
                String SENT1 = "SMS_SENT";
                String DELIVERED1 = "SMS_DELIVERED";

            //    final PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(
              //          SENT1), 0);

              //  final PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                      //  new Intent(DELIVERED1), 0);

                registerReceiver(sendBroadcastReceiver, new IntentFilter(SENT1));

                registerReceiver(deliveryBroadcastReciever, new IntentFilter(DELIVERED1));



                Intent delivery = new Intent("SMS_DELIVERED");
                delivery.putExtra("id", AMTID);
                Intent sending = new Intent("SMS_SENT");
                sending.putExtra("id", AMTID);
               final PendingIntent sentPI = PendingIntent.getBroadcast(this, smsIDsent++, sending, PendingIntent.FLAG_CANCEL_CURRENT);
                final PendingIntent deliveredPI = PendingIntent.getBroadcast(this, smsIDreceive++, delivery, PendingIntent.FLAG_CANCEL_CURRENT);

            //    final PendingIntent sentIntents = PendingIntent.getBroadcast(this, smsIDsent++, sending, PendingIntent.FLAG_CANCEL_CURRENT);
              //  final PendingIntent deliveryIntents =  PendingIntent.getBroadcast(this, smsIDreceive++, delivery, PendingIntent.FLAG_CANCEL_CURRENT);

                registerReceiver(sendBroadcastReceiver, new IntentFilter(SENT));

                registerReceiver(deliveryBroadcastReciever, new IntentFilter(DELIVERED));

                //             registerReceiver(sendBroadcastReceiver2, new IntentFilter(SENT2));
//
                //               registerReceiver(deliveryBroadcastReciever2, new IntentFilter(DELIVERED2));
                //Log.i("Sim No", "sendSMS: "+index+AMTID);
                if (process == 0) {
                    Thread thread = new Thread() {
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
                        @Override
                        public void run() {
//Testing Purpose
                            //  smsManager.getSmsManagerForSubscriptionId(smsToSendFrom)
                            //        .sendTextMessage(MSISDN, null, messageText+AMTID, sentPI, deliveredPI);
                            //Log.i("Message Sent To", "run: "+MSISDN);

                //            smsManager.getSmsManagerForSubscriptionId(smsToSendFrom)
                //            smsManager.getSmsManagerForSubscriptionId(smsToSendFrom)
                  //                  .sendTextMessage(MSISDN, null, messageText + AMTID, sentPI, deliveredPI);
                            //    ArrayList<String> parts = smsManager.divideMessage(messageText);
                            //  smsManager.sendMultipartTextMessage(MSISDN,null,parts,sentIntents,deliveryIntents);



                           // Log.i("Check Id", "Before Send: "+MainActivity.smsRecordDatabase.smsRecordDao().checkSendAmtid(AMTID).toString());
                            //Log.i("Check Id", "Random Id"+MainActivity.smsRecordDatabase.smsRecordDao().checkSendAmtid("xasxaweqe"));
                            if(MainActivity.smsValidationDatabase.smsValidationDAO().checkSendAmtid(AMTID).toString().equals("[]"))
                            {
                                Log.i("Message Already Sent", "run: "+AMTID);
                                queue--;
                            }else{
                                //SendSMS
                                Log.i("Send Messagge", "Process 1 run: "+MainActivity.smsValidationDatabase.smsValidationDAO().checkSendAmtid(AMTID).get(0).getId()+" "+MainActivity.smsValidationDatabase.smsValidationDAO().checkSendAmtid(AMTID).get(0).getMSISDN());
                              //  Log.i("Send Messagge", "Process 1 run: "+MainActivity.smsValidationDatabase.smsValidationDAO().checkSendAmtid(AMTID).get(1).getId());
                                smsManager.getSmsManagerForSubscriptionId(smsToSendFrom)
                                        .sendTextMessage(MSISDN, null, messageText, sentPI, deliveredPI);



                            }
                        }
                    };
                    thread.start();
                } else {
                    Thread thread = new Thread() {
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
                        @Override
                        public void run() {
                            if (MainActivity.smsValidationDatabase.smsValidationDAO().checkSendAmtid(AMTID).toString().equals("[]")) {
                                Log.i("Message Already Sent", "run: " + AMTID);
                            } else {
                                //SendSMS
                                //    Log.i("Send Messagge", "Process 2 run: "+index+AMTID);
                                //      smsManager01.getSmsManagerForSubscriptionId(smsToSendFrom)
                                //              .sendTextMessage(MSISDN, null, messageText+AMTID, sentPI, deliveredPI);
                                //          ArrayList<String> parts = smsManager01.divideMessage(message);
                                //            smsManager01.sendMultipartTextMessage(MSISDN,null,"",null,null);
                            }
                        }
                    };
                    thread.start();
                }


            } else {
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
        if (tb.isChecked()) {
            tb.setText("Sim1");
        } else {
            tb.setText("Sim2");
        }
    }

    public void saveSetting(View view) {
        saveSetting();
    }


    class DeliverReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent arg1) {
            Log.i("", "Deliver MSG: " + arg1.getStringExtra("id"));


            String AMTID, am, am2;
            AMTID = arg1.getStringExtra("SMS_DELIVERED");
            am = arg1.getStringExtra("SMS_SENT");
            am2 = arg1.getStringExtra("id");
            Log.i("", "Deliver MSG: " + arg1.getStringExtra("id"));

            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    Log.i("", "onReceive: " + arg1.getStringExtra("id"));
                    //   Toast.makeText(getBaseContext(), "Message Delivered",
                    //         Toast.LENGTH_SHORT).show();
                    successDeliver++;

                    //  dbFunction.sqliteDelete(arg1.getStringExtra("id"));

                    //sqliteDelete(AMTID);
                    //  nowSendingRate++;
                    Log.i("Amount Sending", "onReceive: " + nowSendingRate);

                    break;
                case Activity.RESULT_CANCELED:

                    Log.i("Undelivered", "Delivered: " + nowSendingRate);

                    break;
            }

            tvSuccessDelivered.setText("Delivered:" + successDeliver);

        }
    }

    class SentReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, final Intent arg1) {


            Log.i("Message Sent ", "In Queue For Sending");
            String AMTID, am;
            AMTID = arg1.getStringExtra("SMS_Damn");
            am = arg1.getStringExtra("SMS_SENT");
            DateFormat df = DateFormat.getTimeInstance();
            df.setTimeZone(TimeZone.getTimeZone("UTC"));

            final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            final String utcTime = sdf.format(new Date());
            final String Failed = "FAILED";
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    nowSendingRate++;
                    succesSending++;
                    queue--;
                    Log.i("Message Sent ", "In Queue For Sending " + arg1.getStringExtra("id"));
                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            String Delivered = "DELIVRD";
                            if (queue == 0) {
                                if (gettingJson) {
                                   serverReport.sendReportToServerFull(jsonSize, logid, getApplicationContext());
                                    util.sendEmail();
                                    reportTime++;
                                    Log.i("Report Time", "run: " + reportTime);
                                    if(sim==0){
                                        getJson(0);
                                    }else{
                                        getJson(1);
                                    }
                                        // getJson(0);
                                    gettingJson = false;
                                }
                            }

                            Log.i("Sending Rate", "run: \nSuccess=" + succesSending + "" +
                                    "\nSending" + nowSendingRate + "\nFailedSending" + failedSending);

                            MainActivity.smsValidationDatabase.smsValidationDAO().Update(utcTime, Delivered, arg1.getStringExtra("id"));
//                            Log.i("Get Time", "run: "+ MainActivity.smsValidationDatabase.smsValidationDAO().getAll().get(0).getSenderTime()+MainActivity.smsValidationDatabase.smsValidationDAO().getAll().get(0).getStatusSend()+MainActivity.smsValidationDatabase.smsValidationDAO().getAll().get(0).getLogId());
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
                            Log.i("Error", "Failed Report" + " AMTID:" + arg1.getStringExtra("id"));
                            MainActivity.smsValidationDatabase.smsValidationDAO().Update(utcTime, Failed, arg1.getStringExtra("id"));
                            dbFunction.sqliteGetFailedSMS();
                        }
                    };
                    thread2.start();


//                    MainActivity.smsValidationDatabase.smsValidationDAO().Update(utcTime,Failed,arg1.getStringExtra("id"));
                    Log.i("Error", "Generic Failure Sim 1" + " AMTID:" + arg1);
                    //reportFailedToServer


                    Log.e("Error", "Generic Failure Sim 1" + " AMTID:" + arg1);
                    break;
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                    Toast.makeText(getBaseContext(), "No service",
                            Toast.LENGTH_SHORT).show();
                    nowSendingRate++;
                    failedSending++;
                    queue--;


                    Thread thread3 = new Thread() {
                        @Override
                        public void run() {
                            MainActivity.smsValidationDatabase.smsValidationDAO().Update(utcTime, Failed, arg1.getStringExtra("id"));

                        }
                    };
                    thread3.start();

                    Log.e("Error", "No Service");
                    Log.i("Error", "No Service Sim 1" + " AMTID:" + arg1);

                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU:
                    Toast.makeText(getBaseContext(), "Null PDU", Toast.LENGTH_SHORT)
                            .show();
                    Log.e("Error", "Null PDU");
                    Log.i("Error", "Null PDU" + " AMTID:" + arg1);
                    nowSendingRate++;
                    failedSending++;
                    queue--;
                    Thread thread4 = new Thread() {
                        @Override
                        public void run() {
                            MainActivity.smsValidationDatabase.smsValidationDAO().Update(utcTime, Failed, arg1.getStringExtra("id"));
                        }
                    };
                    thread4.start();

                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    Toast.makeText(getBaseContext(), "Radio off",
                            Toast.LENGTH_SHORT).show();
                    Log.i("Error", "Radio Off" + " AMTID:" + arg1);
                    nowSendingRate++;
                    failedSending++;
                    queue--;
                    break;
            }

            tvSendingSuccess.setText("Success Send:" + succesSending);
            tvSendingFailed.setText("Failed Send:" + failedSending);
            tvQueue.setText("SMS Queue:" + queue);
            tvSuccessDelivered.setText("Delivered:" + successDeliver);
            String simUse;
            if(sim==0)
            {
                simUse="1";
            }else{
                simUse="2";
            }
            tvSimUse.setText("Sim in use:"+simUse);

        }
    }

    public class MultiSmsSentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, final Intent arg1) {


            Log.i("Message Sent ", "In Queue For Sending");
            String AMTID, am;
            AMTID = arg1.getStringExtra("SMS_Damn");
            am = arg1.getStringExtra("SMS_SENT");
            DateFormat df = DateFormat.getTimeInstance();
            df.setTimeZone(TimeZone.getTimeZone("UTC"));

            final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            final String utcTime = sdf.format(new Date());
            final String Failed = "FAILED";
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                  //  nowSendingRate++;
                 //   succesSending++;
                  //  queue--;
                    Log.i("Queue Minus", "In Queue For Sending " + arg1.getStringExtra("id"));
                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            String Delivered = "DELIVRD";
                            if (queue == 0) {
                                if (gettingJson) {
                                    serverReport.sendReportToServerFull(jsonSize, logid, getApplicationContext());
                                    util.sendEmail();
                                    reportTime++;
                                    Log.i("Report Time", "run: " + reportTime);
                                    getJson(0
                                    );
                                    gettingJson = false;
                                }
                            }

                            Log.i("Sending Rate", "run: \nSuccess=" + succesSending + "" +
                                    "\nSending" + nowSendingRate + "\nFailedSending" + failedSending);

                            MainActivity.smsValidationDatabase.smsValidationDAO().Update(utcTime, Delivered, arg1.getStringExtra("id"));
//                            Log.i("Get Time", "run: "+ MainActivity.smsValidationDatabase.smsValidationDAO().getAll().get(0).getSenderTime()+MainActivity.smsValidationDatabase.smsValidationDAO().getAll().get(0).getStatusSend()+MainActivity.smsValidationDatabase.smsValidationDAO().getAll().get(0).getLogId());
                        }
                    };
                    thread.start();
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    Toast.makeText(getBaseContext(), "Generic failure",
                            Toast.LENGTH_SHORT).show();
              //      nowSendingRate++;
                    failedSending++;
                    Log.i("GenericFailure", "onReceive: "+"MultiSMS");
               //     queue--;

                    Thread thread2 = new Thread() {
                        @Override
                        public void run() {
                            Log.i("Error", "Failed Report" + " AMTID:" + arg1.getStringExtra("id"));
                            MainActivity.smsValidationDatabase.smsValidationDAO().Update(utcTime, Failed, arg1.getStringExtra("id"));
                            dbFunction.sqliteGetFailedSMS();
                        }
                    };
                    thread2.start();


//                    MainActivity.smsValidationDatabase.smsValidationDAO().Update(utcTime,Failed,arg1.getStringExtra("id"));
                    Log.i("Error", "Generic Failure Sim 1" + " AMTID:" + arg1);
                    //reportFailedToServer


                    Log.e("Error", "Generic Failure Sim 1" + " AMTID:" + arg1);
                    break;
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                    Toast.makeText(getBaseContext(), "No service",
                            Toast.LENGTH_SHORT).show();
                //    nowSendingRate++;
                    failedSending++;
      //              queue--;


                    Thread thread3 = new Thread() {
                        @Override
                        public void run() {
                            MainActivity.smsValidationDatabase.smsValidationDAO().Update(utcTime, Failed, arg1.getStringExtra("id"));

                        }
                    };
                    thread3.start();

                    Log.e("Error waklu", "No Service");
                    Log.i("Error", "No Service Sim 1" + " AMTID:" + arg1);

                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU:
                    Toast.makeText(getBaseContext(), "Null PDU", Toast.LENGTH_SHORT)
                            .show();
                    Log.e("Error", "Null PDU");
                    Log.i("Error", "Null PDU" + " AMTID:" + arg1);
                  //  nowSendingRate++;
                    failedSending++;
      //              queue--;
                    Thread thread4 = new Thread() {
                        @Override
                        public void run() {
                            MainActivity.smsValidationDatabase.smsValidationDAO().Update(utcTime, Failed, arg1.getStringExtra("id"));
                        }
                    };
                    thread4.start();

                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    Toast.makeText(getBaseContext(), "Radio off",
                            Toast.LENGTH_SHORT).show();
                    Log.i("Error", "Radio Off" + " AMTID:" + arg1);
                 //   nowSendingRate++;
                    failedSending++;
      //              queue--;
                    break;
            }
            String simUse;

            tvSendingSuccess.setText("Success Send:" + succesSending);
            tvSendingFailed.setText("Failed Send:" + failedSending);
            tvQueue.setText("SMS Queue:" + queue);
            if(sim==0)
            {
                simUse="1";
            }else{
                simUse="2";
            }
            tvSimUse.setText("Sim Use"+simUse);
        }
    }

    public class MultiSmsDeliveredReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent arg1) {
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    Toast.makeText(context, "SMS delivered", Toast.LENGTH_SHORT).show();
                    break;
                case Activity.RESULT_CANCELED:
                    Toast.makeText(context, "SMS not delivered", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

    }

      /*
    public void getAmtelJson(String requestParam) {
        SharedPreferences prfs = getSharedPreferences("AUTHENTICATION_FILE_NAME", Context.MODE_PRIVATE);
        final String Service = prfs.getString("SERVICE", "");
        final String Simcard = prfs.getString("SIMCARDNUMBER", "");
        String URL = prfs.getString("URL", "");
        final String Telco = prfs.getString("TELCO", "");
        final String Shortcode = prfs.getString("SHORTCODE", "");
        final String Action = prfs.getString("ACTION", "");
        String Threshold = prfs.getString("THRESHOLD", "");
        String smsLimit=prfs.getString("SMSLIMIT","");

        StringEntity entity = null;
        try {
           entity = new StringEntity("{\n" +
                    "    \"Action\":" + Action +
                    "    \"Service\":" + Service +
                    "    \"Shortcode\":" + Shortcode +
                    "    \"SenderID\": " + Simcard +
                    "    \"Telco\": " + Telco +
                    "}");



            entity = new StringEntity("{\n" +
                    "    \"Action\": \"mt\",\n" +
                    "    \"Service\": \"gsm\",\n" +
                    "    \"Shortcode\": \"gsm_celcom\",\n" +
                    "    \"SenderID\": \"63839\",\n" +
                    "    \"Telco\": \"celcom\"\n" +
                    "}");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Log.i("", "getAmtelJson: "+entity.toString());
        trustEveryone();
        Communication.postCrowdEra(this.getBaseContext(), Communication.CROWDERA_REPORT_LOCATION, entity, new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.i("Volley", "onResponse: " + response.toString());
                if (response.toString().equals("{\"Status\":\"no_record\"}")
                        || response.toString().equals("{\"Status\":\"invalid_json\"}")
                        || response.toString().equals("{\"Status\":\"Invalid_Json\"}")
                        || response.toString().equals("{\"Status\":\"invalid_access\"}")
                        || response.toString().equals("\"invalid_action\"")
                        || response.toString().equals("No Data")) {
                    Toast.makeText(getBaseContext(), response.toString(),
                            Toast.LENGTH_SHORT).show();

                    if (response.toString().equals("{\"Status\":\"no_record\"}")) {

                        //     tvLogFile.append("\nTime:" + utcTime);
                        tvLogFile.append("\nRequest From Server:" + "Success");
                        tvLogFile.append("\nSMS Amount:" + 0);
                        sv.fullScroll(ScrollView.FOCUS_DOWN);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getJson(0);
                            }
                        }, 10000);
                    } else if (response.toString().equals("{\"Status\":\"invalid_access\"}")) {
                        //   tvLogFile.append("\nTime:" + utcTime);
                        tvLogFile.append("\nRequest From Server:" + "Failed");
                        tvLogFile.append("\nError Log:" + response.toString());
                        tvLogFile.append("\nSMS Amount:" + 0);
                        sv.fullScroll(ScrollView.FOCUS_DOWN);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getJson(0);
                            }
                        }, 10000);
                    }
                } else {
                    gettingJson = true;
                    Gson gson = new Gson();
                    final MessageToSend messageToSend = gson.fromJson(String.valueOf(response), MessageToSend.class);
                    System.out.println("Amount Of Message: " + messageToSend.getMT().size());
                    int l;
                    int j;
                    int saveSqlite;
                    logid = UUID.randomUUID().toString();
                    jsonSize = messageToSend.getMT().size();
                    queue = jsonSize;

                    //  tvLogFile.append("\nTime:" + utcTime);
                    tvLogFile.append("\nRequest From Server:" + "Success");
                    tvLogFile.append("\nSMS Amount:" + messageToSend.getMT().size());
                    sv.fullScroll(ScrollView.FOCUS_DOWN);
                    for (saveSqlite = 0; saveSqlite < messageToSend.getMT().size(); saveSqlite++) {
                        dbFunction.setSqliteQueue(messageToSend.getMT().get(saveSqlite).getAMTID().toString(), messageToSend.getMT().get(saveSqlite).getMSISDN().toString(), messageToSend.getMT().get(saveSqlite).getMsg().toString(), "gsm_celcom", "0192886050", logid);
                    }
                    serverReport.serverAcknowledgment(messageToSend.getBatchID(), "gsm", Simcard);

                    for (l = 0; l < messageToSend.getMT().size() / 2; l++) {
                        if(messageToSend.getMT().get(l).getMsgType().equals("U")){
                            String formatUnicode=unicodeFormat(messageToSend.getMT().get(l).getMsg());
                            if(formatUnicode.length()>160)
                            {
                                sentMultipartSms(0,formatUnicode,messageToSend.getMT().get(l).getMSISDN(),messageToSend.getMT().get(l).getAMTID(),0);
                            }else{
                                //SendNormal
                                sendSMS(0, formatUnicode, messageToSend.getMT().get(l).getMSISDN(), messageToSend.getMT().get(l).getAMTID(), 0);

                            }
                        }else{
                            if(messageToSend.getMT().get(l).getMsg().length()>160)
                            {
                                sentMultipartSms(0,messageToSend.getMT().get(l).getMsg(),messageToSend.getMT().get(l).getMSISDN(),messageToSend.getMT().get(l).getAMTID(),0);
                            }else{
                                //SendNormal
                                sendSMS(0, messageToSend.getMT().get(l).getMsg(), messageToSend.getMT().get(l).getMSISDN(), messageToSend.getMT().get(l).getAMTID(), 0);

                            }

                        }
                    }

                    for (j = messageToSend.getMT().size() / 2; j < messageToSend.getMT().size(); j++) {
                        Log.i("Index Process 2", "onResponse: " + j);
                       // sendSMS(0, messageToSend.getMT().get(j).getMsg(), messageToSend.getMT().get(l).getMSISDN(), messageToSend.getMT().get(j).getAMTID(), 1);
                        if(messageToSend.getMT().get(j).getMsgType().equals("U")){
                            String formatUnicode=unicodeFormat(messageToSend.getMT().get(l).getMsg());
                            if(formatUnicode.length()>160)
                            {
                                sentMultipartSms(0,messageToSend.getMT().get(j).getMsg(),messageToSend.getMT().get(j).getMSISDN(),messageToSend.getMT().get(j).getAMTID(),0);
                            }else{
                                //SendNormal
                                sendSMS(0, formatUnicode, messageToSend.getMT().get(j).getMSISDN(), messageToSend.getMT().get(j).getAMTID(), 0);

                            }
                        }else{
                            if(messageToSend.getMT().get(j).getMsg().length()>160)
                            {
                                sentMultipartSms(0,messageToSend.getMT().get(j).getMsg(),messageToSend.getMT().get(j).getMSISDN(),messageToSend.getMT().get(l).getAMTID(),0);
                            }else{
                                //SendNormal
                                sendSMS(0, messageToSend.getMT().get(j).getMsg(), messageToSend.getMT().get(j).getMSISDN(), messageToSend.getMT().get(j).getAMTID(), 0);

                            }

                        }
                    }
                }

            }

            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                Log.e("", "onFailure: " + errorResponse);

                //spinner.setVisibility(View.GONE);
                //progressSpinner.setVisibility(View.INVISIBLE);
                //uploadRaw.setEnabled(true);

            }

            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                Log.i("", "onFailure: " + responseString);
                //  spinner.setVisibility(View.GONE);
                // progressSpinner.setVisibility(View.INVISIBLE);
                //uploadRaw.setEnabled(true);
            }
        });
    }

    */





}






