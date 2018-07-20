package com.example.airin.spammersms;

import java.util.ArrayList;

/**
 * Created by airin on 26/04/2018.
 */

public class MessageToSend {
    String Status;
    String BatchID;
    ArrayList<ItemDTO> MT;
//asdawdawda

    public String getStatus() {
        return Status;
    }



    public String getBatchID() {
        return BatchID;
    }

    public void setBatchID(String batchID) {
        BatchID = batchID;
    }

    public ArrayList<ItemDTO> getMT() {
        return MT;
    }

    public void setMT(ArrayList<ItemDTO> MT) {
        this.MT = MT;
    }


    class ItemDTO
    {
        String AMTID;

        public String getAMTID() {
            return AMTID;
        }

        public void setAMTID(String AMTID) {
            this.AMTID = AMTID;
        }

        public String getMSISDN() {
            return MSISDN;
        }

        public void setMSISDN(String MSISDN) {
            this.MSISDN = MSISDN;
        }

        public String getMsgType() {
            return MsgType;
        }

        public void setMsgType(String msgType) {
            MsgType = msgType;
        }

        public String getMsg() {
            return Msg;
        }

        public void setMsg(String msg) {
            Msg = msg;
        }

        String MSISDN;
        String MsgType;
        String Msg;
    }

}
