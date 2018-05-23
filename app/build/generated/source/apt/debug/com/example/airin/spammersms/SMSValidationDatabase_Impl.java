package com.example.airin.spammersms;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.db.SupportSQLiteOpenHelper.Callback;
import android.arch.persistence.db.SupportSQLiteOpenHelper.Configuration;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.RoomOpenHelper;
import android.arch.persistence.room.RoomOpenHelper.Delegate;
import android.arch.persistence.room.util.TableInfo;
import android.arch.persistence.room.util.TableInfo.Column;
import android.arch.persistence.room.util.TableInfo.ForeignKey;
import android.arch.persistence.room.util.TableInfo.Index;
import java.lang.IllegalStateException;
import java.lang.Override;
import java.lang.String;
import java.util.HashMap;
import java.util.HashSet;

public class SMSValidationDatabase_Impl extends SMSValidationDatabase {
  private volatile SMSValidationDAO _sMSValidationDAO;

  protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration configuration) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(configuration, new RoomOpenHelper.Delegate(3) {
      public void createAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("CREATE TABLE IF NOT EXISTS `smsValidation` (`Id` TEXT NOT NULL, `MSISDN` TEXT, `Message` TEXT, `shortcode` TEXT, `senderId` TEXT, `Astatus` TEXT, `sendingTime` TEXT, `statusSend` TEXT, `logId` TEXT, PRIMARY KEY(`Id`))");
        _db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        _db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, \"d2b88271257fa05f6310611f0a805682\")");
      }

      public void dropAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("DROP TABLE IF EXISTS `smsValidation`");
      }

      protected void onCreate(SupportSQLiteDatabase _db) {
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onCreate(_db);
          }
        }
      }

      public void onOpen(SupportSQLiteDatabase _db) {
        mDatabase = _db;
        internalInitInvalidationTracker(_db);
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onOpen(_db);
          }
        }
      }

      protected void validateMigration(SupportSQLiteDatabase _db) {
        final HashMap<String, TableInfo.Column> _columnsSmsValidation = new HashMap<String, TableInfo.Column>(9);
        _columnsSmsValidation.put("Id", new TableInfo.Column("Id", "TEXT", true, 1));
        _columnsSmsValidation.put("MSISDN", new TableInfo.Column("MSISDN", "TEXT", false, 0));
        _columnsSmsValidation.put("Message", new TableInfo.Column("Message", "TEXT", false, 0));
        _columnsSmsValidation.put("shortcode", new TableInfo.Column("shortcode", "TEXT", false, 0));
        _columnsSmsValidation.put("senderId", new TableInfo.Column("senderId", "TEXT", false, 0));
        _columnsSmsValidation.put("Astatus", new TableInfo.Column("Astatus", "TEXT", false, 0));
        _columnsSmsValidation.put("sendingTime", new TableInfo.Column("sendingTime", "TEXT", false, 0));
        _columnsSmsValidation.put("statusSend", new TableInfo.Column("statusSend", "TEXT", false, 0));
        _columnsSmsValidation.put("logId", new TableInfo.Column("logId", "TEXT", false, 0));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSmsValidation = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesSmsValidation = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoSmsValidation = new TableInfo("smsValidation", _columnsSmsValidation, _foreignKeysSmsValidation, _indicesSmsValidation);
        final TableInfo _existingSmsValidation = TableInfo.read(_db, "smsValidation");
        if (! _infoSmsValidation.equals(_existingSmsValidation)) {
          throw new IllegalStateException("Migration didn't properly handle smsValidation(com.example.airin.spammersms.SMSValidation).\n"
                  + " Expected:\n" + _infoSmsValidation + "\n"
                  + " Found:\n" + _existingSmsValidation);
        }
      }
    }, "d2b88271257fa05f6310611f0a805682");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(configuration.context)
        .name(configuration.name)
        .callback(_openCallback)
        .build();
    final SupportSQLiteOpenHelper _helper = configuration.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  protected InvalidationTracker createInvalidationTracker() {
    return new InvalidationTracker(this, "smsValidation");
  }

  @Override
  public SMSValidationDAO smsValidationDAO() {
    if (_sMSValidationDAO != null) {
      return _sMSValidationDAO;
    } else {
      synchronized(this) {
        if(_sMSValidationDAO == null) {
          _sMSValidationDAO = new SMSValidationDAO_Impl(this);
        }
        return _sMSValidationDAO;
      }
    }
  }
}
