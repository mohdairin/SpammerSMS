package com.example.airin.spammersms;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

@Entity(tableName = "smsValidation")
public class SMSValidation {
    @PrimaryKey
    @NonNull String  Id;

    @ColumnInfo(name = "MSISDN")
    private String MSISDN;

    @ColumnInfo(name = "Message")
    private String message;

    @ColumnInfo(name = "shortcode")
    private String shortcode;

    @ColumnInfo(name = "senderId")
    private String senderID;

    @ColumnInfo(name = "Astatus")
    private String Astatus;

    @ColumnInfo(name="sendingTime")
    private String senderTime;

    @ColumnInfo(name="statusSend")
    private String statusSend;

    @ColumnInfo(name= "logId")
    private String logId;

    public String getSenderTime() {
        return senderTime;
    }

    public void setSenderTime(String senderTime) {
        this.senderTime = senderTime;
    }

    public String getStatusSend() {
        return statusSend;
    }

    public void setStatusSend(String statusSend) {
        this.statusSend = statusSend;
    }

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }


    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getMSISDN() {
        return MSISDN;
    }

    public void setMSISDN(String MSISDN) {
        this.MSISDN = MSISDN;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public String getShortcode() {
        return shortcode;
    }

    public void setShortcode(String shortcode) {
        this.shortcode = shortcode;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getAstatus() {
        return Astatus;
    }

    public void setAstatus(String astatus) {
        Astatus = astatus;
    }




}