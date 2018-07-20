package com.example.airin.spammersms;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import cz.msebera.android.httpclient.conn.ssl.SSLSocketFactory;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by AMCSB on 7/11/2017.
 */

public class Comms {
    //region URLS
    public static String CROWDERA_REPORT_LOCATION = "/api/gsm/";
    private static AsyncHttpClient client = new AsyncHttpClient();
    private static int SERVER_TIMEOUT = 60000;

    //region Traffic Server
    private static String CROWDERA_HOST = "https://gensuitedev3.genusis.com";
    private static String CROWDERA_PORT = "80";
    //endregion
    private static String CROWDERA_PATH = CROWDERA_HOST;
    //endregion

    private static String getCrowdEraAbsoluteUrl(String relativeUrl) {
        return CROWDERA_PATH + relativeUrl;
    }

    public static void postCrowdEra(Context ctx, String url, StringEntity entity, AsyncHttpResponseHandler responseHandler) {
        client = new AsyncHttpClient();
        client.setSSLSocketFactory(
                new SSLSocketFactory(getSslContext(),
                        SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER));
        client.setTimeout(SERVER_TIMEOUT);
        client.addHeader("Content-Type", "application/json;");
       // client.addHeader("Api-Key", "b9cc656b-ed02-4fcd-85b0-ffb4e058fa76");
        client.post(ctx, getCrowdEraAbsoluteUrl(url), entity, "application/json",
                responseHandler);
    }

    public static void cancel(Context ctx) {
        client.cancelRequests(ctx, true);
    }

    public static SSLContext getSslContext() {

        TrustManager[] byPassTrustManagers = new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }
        } };

        SSLContext sslContext=null;

        try {
            sslContext = SSLContext.getInstance("TLS");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            sslContext.init(null, byPassTrustManagers, new SecureRandom());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        return sslContext;
    }
}
