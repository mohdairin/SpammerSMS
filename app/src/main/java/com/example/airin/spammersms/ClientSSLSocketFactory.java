package com.example.airin.spammersms;

import android.net.SSLCertificateSocketFactory;

import javax.net.ssl.SSLContext;

public class ClientSSLSocketFactory  extends SSLCertificateSocketFactory {
    private static SSLContext sslContext;
    //private SSLContext sslContext;

    /**
     * @param handshakeTimeoutMillis
     * @deprecated Use {@link #getDefault(int)} instead.
     */
    public ClientSSLSocketFactory(int handshakeTimeoutMillis) {
        super(handshakeTimeoutMillis);
    }

}


