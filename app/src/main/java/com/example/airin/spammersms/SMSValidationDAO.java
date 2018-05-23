package com.example.airin.spammersms;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface SMSValidationDAO {

    // Adds a person to the database
    @Insert
    void addUser(SMSValidation smsValidation);

    // Removes a person from the database
    @Delete
    void delete(SMSValidation smsValidation);

    // Gets all people in the database
    @Query("SELECT * FROM smsValidation")
    List<SMSValidation> getAll();

    @Query("SELECT * FROM smsValidation WHERE Id LIKE :id")
    List<SMSValidation> checkSendAmtid(String id);

    @Query("SELECT * FROM smsValidation WHERE statusSend LIKE :failedId")
    List<SMSValidation> checkFailedAmount(String failedId);

    @Query("SELECT * FROM smsValidation WHERE logId LIKE :id")
    List<SMSValidation> getListfromLogId(String id);

    @Query("DELETE FROM smsValidation WHERE Id LIKE  :userId")
    void deleteId(String userId);

    @Query("DELETE FROM smsValidation")
    void nukeTable();

    @Query("Delete FROM smsValidation WHERE logId LIKE:logid")
    void deleteSendingSMS(String logid);

    @Query("UPDATE smsValidation SET sendingTime =:sTime, statusSend =:sendingStatus WHERE Id LIKE :Id")
    void Update(String sTime,String sendingStatus,String Id);







    // Gets all people in the database with a favorite color


}