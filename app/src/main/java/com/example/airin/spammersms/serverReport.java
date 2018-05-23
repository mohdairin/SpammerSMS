package com.example.airin.spammersms;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ConcurrentModificationException;

public class serverReport {
    private RequestQueue requestQueue;
    Context context;
    Util util=new Util();

//    serverReport s=new serverReport();
    DBFunction dbFunction=new DBFunction();



    public RequestQueue getRequestQueue(Context applicationContext) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(applicationContext);
          //  requestQueue = Volley.newRequestQueue(this.getApplicationContext());
        }
        return requestQueue;
    }

    public void sendReportToServer(final String MSISDN, final String AMTID, final String TMTID, final String Shortcode,
                                   final String SenderId, final String AStatus)
    {

        Thread thread = new Thread() {
            @Override
            public void run() {
                JSONObject object = new JSONObject();

                try {

                    object.put("Action","dn");
                    JSONObject obj=new JSONObject();
                    obj.put("MSISDN",MSISDN);
                    obj.put("AMTID",AMTID);
                    obj.put("TMTID","10219023123");
                    obj.put("Shortcode",Shortcode);
                    obj.put("SenderID",SenderId);
                    obj.put("AStatus","DELIVRD");
                    object.put("DN",obj);
                    object.toString();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.i("Before Send", "run: "+object.toString());

                String url1 = "http://gensuitedev3.genusis.com/api/gsm/";
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url1, object,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                //                   Log.i("Volley", "onResponseSuccess: " + response.toString());
                                if(response.toString().equals("{\"Status\":\"ok\"}"))
                                {
                                    //    Toast.makeText(getBaseContext(), "Report Success Sial",
                                    //          Toast.LENGTH_SHORT).show();
                                    Log.i("", "onResponse: Report Success Sial"+response);
                                }else {
                                    Log.i("Volley", "onResponse: "+response);

                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //      serverResp.setText("Error getting response");
                        Log.i("ReportServer", "onResponse: Error "+error);

                    }
                });
                jsonObjectRequest.setTag("b");
                requestQueue.add(jsonObjectRequest);
            }
        };
        thread.start();
    }

    public void serverAcknowledgment(String batchID, String gsm, String senderId){
        JSONObject json = new JSONObject();
        try {
            json.put("Action", "ack");
            json.put("BatchID", batchID);
            json.put("Shortcode", "gsm_celcom");
            json.put("SenderID", senderId);
            json.put("Status","ok");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url1 = "http://gensuitedev3.genusis.com/api/gsm/";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url1, json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("Acknowledgement", "onResponse: " + response.toString());
                        if(response.toString().equals("{\"Status\":\"ok\"}"))
                        {
                            Log.i("Acknowledgement", "Server Acknowledgment:"+response.toString());
                        }else
                        {
                            Log.i("Acknowledgement", "onResponse: No Data"+response.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Acknowledgement", "onErrorResponse: "+error);
            }
        });
        jsonObjectRequest.setTag("serverAcknowledgment");
        requestQueue.add(jsonObjectRequest);

    }

    public void sendReportToServerFull(final int loop, final String logid, final Context ctx)
    {

        Thread thread = new Thread() {
            @Override
            public void run() {
                JSONObject object = new JSONObject();
                try {
                    JSONArray jsonArray = new JSONArray();
                    object.put("Action","dn");


                    for(int i=0;i<loop;i++)
                    {

                        JSONObject obj=new JSONObject();
                        obj.put("MSISDN",MainActivity.smsValidationDatabase.smsValidationDAO().getListfromLogId(logid).get(i).getMSISDN());
                        obj.put("AMTID",MainActivity.smsValidationDatabase.smsValidationDAO().getListfromLogId(logid).get(i).getId());
                        obj.put("TMTID","");
                        obj.put("Shortcode",MainActivity.smsValidationDatabase.smsValidationDAO().getListfromLogId(logid).get(i).getShortcode());
                        obj.put("SenderID",MainActivity.smsValidationDatabase.smsValidationDAO().getListfromLogId(logid).get(i).getSenderID());
                        obj.put("statusSend",MainActivity.smsValidationDatabase.smsValidationDAO().getListfromLogId(logid).get(i).getStatusSend());
                        Log.i("DataCheck", "run: "+MainActivity.smsValidationDatabase.smsValidationDAO().getListfromLogId(logid).get(i).getStatusSend());
                        jsonArray.put(obj);
                        object.put("DN",jsonArray);
                        // object.toString();

                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.i("Before Send", "run: "+object.toString());
                util.writeToFile(object.toString(),ctx);
                util.generateNoteOnSD(ctx,"File.txt",object.toString());



                String url1 = "http://gensuitedev3.genusis.com/api/gsm/";
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url1, object,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                //                   Log.i("Volley", "onResponseSuccess: " + response.toString());
                                if(response.toString().equals("{\"Status\":\"ok\"}"))
                                {
                                    //    Toast.makeText(getBaseContext(), "Report Success Sial",
                                    //          Toast.LENGTH_SHORT).show();
                                    Log.i("", "onResponse: Report Success"+response);

                                }else {
                                    Log.i("Volley", "onResponse: "+response);

                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //      serverResp.setText("Error getting response");
                        Log.i("ReportServer", "onResponse: Error "+error);

                    }
                });
                jsonObjectRequest.setTag("b");
                requestQueue.add(jsonObjectRequest);

                MainActivity.smsValidationDatabase.smsValidationDAO().deleteSendingSMS(logid);
                dbFunction.sqliteGetdata();
            }
        };
        thread.start();
    }


}
