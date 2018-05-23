package com.example.airin.spammersms;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

/**
 * Created by airin on 27/04/2018.
 */
@Database(entities = {SMSValidation.class}, version =3)
public abstract class SMSValidationDatabase extends RoomDatabase
{
    public abstract SMSValidationDAO smsValidationDAO();
}