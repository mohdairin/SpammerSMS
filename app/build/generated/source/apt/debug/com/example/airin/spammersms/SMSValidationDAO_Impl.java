package com.example.airin.spammersms;

import android.arch.persistence.db.SupportSQLiteStatement;
import android.arch.persistence.room.EntityDeletionOrUpdateAdapter;
import android.arch.persistence.room.EntityInsertionAdapter;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.RoomSQLiteQuery;
import android.arch.persistence.room.SharedSQLiteStatement;
import android.database.Cursor;
import java.lang.Override;
import java.lang.String;
import java.util.ArrayList;
import java.util.List;

public class SMSValidationDAO_Impl implements SMSValidationDAO {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter __insertionAdapterOfSMSValidation;

  private final EntityDeletionOrUpdateAdapter __deletionAdapterOfSMSValidation;

  private final SharedSQLiteStatement __preparedStmtOfDeleteId;

  private final SharedSQLiteStatement __preparedStmtOfNukeTable;

  private final SharedSQLiteStatement __preparedStmtOfDeleteSendingSMS;

  private final SharedSQLiteStatement __preparedStmtOfUpdate;

  public SMSValidationDAO_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSMSValidation = new EntityInsertionAdapter<SMSValidation>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `smsValidation`(`Id`,`MSISDN`,`Message`,`shortcode`,`senderId`,`Astatus`,`sendingTime`,`statusSend`,`logId`) VALUES (?,?,?,?,?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, SMSValidation value) {
        if (value.getId() == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindString(1, value.getId());
        }
        if (value.getMSISDN() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getMSISDN());
        }
        if (value.getMessage() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getMessage());
        }
        if (value.getShortcode() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getShortcode());
        }
        if (value.getSenderID() == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.getSenderID());
        }
        if (value.getAstatus() == null) {
          stmt.bindNull(6);
        } else {
          stmt.bindString(6, value.getAstatus());
        }
        if (value.getSenderTime() == null) {
          stmt.bindNull(7);
        } else {
          stmt.bindString(7, value.getSenderTime());
        }
        if (value.getStatusSend() == null) {
          stmt.bindNull(8);
        } else {
          stmt.bindString(8, value.getStatusSend());
        }
        if (value.getLogId() == null) {
          stmt.bindNull(9);
        } else {
          stmt.bindString(9, value.getLogId());
        }
      }
    };
    this.__deletionAdapterOfSMSValidation = new EntityDeletionOrUpdateAdapter<SMSValidation>(__db) {
      @Override
      public String createQuery() {
        return "DELETE FROM `smsValidation` WHERE `Id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, SMSValidation value) {
        if (value.getId() == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindString(1, value.getId());
        }
      }
    };
    this.__preparedStmtOfDeleteId = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM smsValidation WHERE Id LIKE  ?";
        return _query;
      }
    };
    this.__preparedStmtOfNukeTable = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM smsValidation";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteSendingSMS = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "Delete FROM smsValidation WHERE logId LIKE?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdate = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "UPDATE smsValidation SET sendingTime =?, statusSend =? WHERE Id LIKE ?";
        return _query;
      }
    };
  }

  @Override
  public void addUser(SMSValidation smsValidation) {
    __db.beginTransaction();
    try {
      __insertionAdapterOfSMSValidation.insert(smsValidation);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void delete(SMSValidation smsValidation) {
    __db.beginTransaction();
    try {
      __deletionAdapterOfSMSValidation.handle(smsValidation);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void deleteId(String userId) {
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteId.acquire();
    __db.beginTransaction();
    try {
      int _argIndex = 1;
      if (userId == null) {
        _stmt.bindNull(_argIndex);
      } else {
        _stmt.bindString(_argIndex, userId);
      }
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfDeleteId.release(_stmt);
    }
  }

  @Override
  public void nukeTable() {
    final SupportSQLiteStatement _stmt = __preparedStmtOfNukeTable.acquire();
    __db.beginTransaction();
    try {
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfNukeTable.release(_stmt);
    }
  }

  @Override
  public void deleteSendingSMS(String logid) {
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteSendingSMS.acquire();
    __db.beginTransaction();
    try {
      int _argIndex = 1;
      if (logid == null) {
        _stmt.bindNull(_argIndex);
      } else {
        _stmt.bindString(_argIndex, logid);
      }
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfDeleteSendingSMS.release(_stmt);
    }
  }

  @Override
  public void Update(String sTime, String sendingStatus, String Id) {
    final SupportSQLiteStatement _stmt = __preparedStmtOfUpdate.acquire();
    __db.beginTransaction();
    try {
      int _argIndex = 1;
      if (sTime == null) {
        _stmt.bindNull(_argIndex);
      } else {
        _stmt.bindString(_argIndex, sTime);
      }
      _argIndex = 2;
      if (sendingStatus == null) {
        _stmt.bindNull(_argIndex);
      } else {
        _stmt.bindString(_argIndex, sendingStatus);
      }
      _argIndex = 3;
      if (Id == null) {
        _stmt.bindNull(_argIndex);
      } else {
        _stmt.bindString(_argIndex, Id);
      }
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfUpdate.release(_stmt);
    }
  }

  @Override
  public List<SMSValidation> getAll() {
    final String _sql = "SELECT * FROM smsValidation";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final Cursor _cursor = __db.query(_statement);
    try {
      final int _cursorIndexOfId = _cursor.getColumnIndexOrThrow("Id");
      final int _cursorIndexOfMSISDN = _cursor.getColumnIndexOrThrow("MSISDN");
      final int _cursorIndexOfMessage = _cursor.getColumnIndexOrThrow("Message");
      final int _cursorIndexOfShortcode = _cursor.getColumnIndexOrThrow("shortcode");
      final int _cursorIndexOfSenderID = _cursor.getColumnIndexOrThrow("senderId");
      final int _cursorIndexOfAstatus = _cursor.getColumnIndexOrThrow("Astatus");
      final int _cursorIndexOfSenderTime = _cursor.getColumnIndexOrThrow("sendingTime");
      final int _cursorIndexOfStatusSend = _cursor.getColumnIndexOrThrow("statusSend");
      final int _cursorIndexOfLogId = _cursor.getColumnIndexOrThrow("logId");
      final List<SMSValidation> _result = new ArrayList<SMSValidation>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final SMSValidation _item;
        _item = new SMSValidation();
        final String _tmpId;
        _tmpId = _cursor.getString(_cursorIndexOfId);
        _item.setId(_tmpId);
        final String _tmpMSISDN;
        _tmpMSISDN = _cursor.getString(_cursorIndexOfMSISDN);
        _item.setMSISDN(_tmpMSISDN);
        final String _tmpMessage;
        _tmpMessage = _cursor.getString(_cursorIndexOfMessage);
        _item.setMessage(_tmpMessage);
        final String _tmpShortcode;
        _tmpShortcode = _cursor.getString(_cursorIndexOfShortcode);
        _item.setShortcode(_tmpShortcode);
        final String _tmpSenderID;
        _tmpSenderID = _cursor.getString(_cursorIndexOfSenderID);
        _item.setSenderID(_tmpSenderID);
        final String _tmpAstatus;
        _tmpAstatus = _cursor.getString(_cursorIndexOfAstatus);
        _item.setAstatus(_tmpAstatus);
        final String _tmpSenderTime;
        _tmpSenderTime = _cursor.getString(_cursorIndexOfSenderTime);
        _item.setSenderTime(_tmpSenderTime);
        final String _tmpStatusSend;
        _tmpStatusSend = _cursor.getString(_cursorIndexOfStatusSend);
        _item.setStatusSend(_tmpStatusSend);
        final String _tmpLogId;
        _tmpLogId = _cursor.getString(_cursorIndexOfLogId);
        _item.setLogId(_tmpLogId);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<SMSValidation> checkSendAmtid(String id) {
    final String _sql = "SELECT * FROM smsValidation WHERE Id LIKE ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (id == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, id);
    }
    final Cursor _cursor = __db.query(_statement);
    try {
      final int _cursorIndexOfId = _cursor.getColumnIndexOrThrow("Id");
      final int _cursorIndexOfMSISDN = _cursor.getColumnIndexOrThrow("MSISDN");
      final int _cursorIndexOfMessage = _cursor.getColumnIndexOrThrow("Message");
      final int _cursorIndexOfShortcode = _cursor.getColumnIndexOrThrow("shortcode");
      final int _cursorIndexOfSenderID = _cursor.getColumnIndexOrThrow("senderId");
      final int _cursorIndexOfAstatus = _cursor.getColumnIndexOrThrow("Astatus");
      final int _cursorIndexOfSenderTime = _cursor.getColumnIndexOrThrow("sendingTime");
      final int _cursorIndexOfStatusSend = _cursor.getColumnIndexOrThrow("statusSend");
      final int _cursorIndexOfLogId = _cursor.getColumnIndexOrThrow("logId");
      final List<SMSValidation> _result = new ArrayList<SMSValidation>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final SMSValidation _item;
        _item = new SMSValidation();
        final String _tmpId;
        _tmpId = _cursor.getString(_cursorIndexOfId);
        _item.setId(_tmpId);
        final String _tmpMSISDN;
        _tmpMSISDN = _cursor.getString(_cursorIndexOfMSISDN);
        _item.setMSISDN(_tmpMSISDN);
        final String _tmpMessage;
        _tmpMessage = _cursor.getString(_cursorIndexOfMessage);
        _item.setMessage(_tmpMessage);
        final String _tmpShortcode;
        _tmpShortcode = _cursor.getString(_cursorIndexOfShortcode);
        _item.setShortcode(_tmpShortcode);
        final String _tmpSenderID;
        _tmpSenderID = _cursor.getString(_cursorIndexOfSenderID);
        _item.setSenderID(_tmpSenderID);
        final String _tmpAstatus;
        _tmpAstatus = _cursor.getString(_cursorIndexOfAstatus);
        _item.setAstatus(_tmpAstatus);
        final String _tmpSenderTime;
        _tmpSenderTime = _cursor.getString(_cursorIndexOfSenderTime);
        _item.setSenderTime(_tmpSenderTime);
        final String _tmpStatusSend;
        _tmpStatusSend = _cursor.getString(_cursorIndexOfStatusSend);
        _item.setStatusSend(_tmpStatusSend);
        final String _tmpLogId;
        _tmpLogId = _cursor.getString(_cursorIndexOfLogId);
        _item.setLogId(_tmpLogId);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<SMSValidation> checkFailedAmount(String failedId) {
    final String _sql = "SELECT * FROM smsValidation WHERE statusSend LIKE ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (failedId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, failedId);
    }
    final Cursor _cursor = __db.query(_statement);
    try {
      final int _cursorIndexOfId = _cursor.getColumnIndexOrThrow("Id");
      final int _cursorIndexOfMSISDN = _cursor.getColumnIndexOrThrow("MSISDN");
      final int _cursorIndexOfMessage = _cursor.getColumnIndexOrThrow("Message");
      final int _cursorIndexOfShortcode = _cursor.getColumnIndexOrThrow("shortcode");
      final int _cursorIndexOfSenderID = _cursor.getColumnIndexOrThrow("senderId");
      final int _cursorIndexOfAstatus = _cursor.getColumnIndexOrThrow("Astatus");
      final int _cursorIndexOfSenderTime = _cursor.getColumnIndexOrThrow("sendingTime");
      final int _cursorIndexOfStatusSend = _cursor.getColumnIndexOrThrow("statusSend");
      final int _cursorIndexOfLogId = _cursor.getColumnIndexOrThrow("logId");
      final List<SMSValidation> _result = new ArrayList<SMSValidation>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final SMSValidation _item;
        _item = new SMSValidation();
        final String _tmpId;
        _tmpId = _cursor.getString(_cursorIndexOfId);
        _item.setId(_tmpId);
        final String _tmpMSISDN;
        _tmpMSISDN = _cursor.getString(_cursorIndexOfMSISDN);
        _item.setMSISDN(_tmpMSISDN);
        final String _tmpMessage;
        _tmpMessage = _cursor.getString(_cursorIndexOfMessage);
        _item.setMessage(_tmpMessage);
        final String _tmpShortcode;
        _tmpShortcode = _cursor.getString(_cursorIndexOfShortcode);
        _item.setShortcode(_tmpShortcode);
        final String _tmpSenderID;
        _tmpSenderID = _cursor.getString(_cursorIndexOfSenderID);
        _item.setSenderID(_tmpSenderID);
        final String _tmpAstatus;
        _tmpAstatus = _cursor.getString(_cursorIndexOfAstatus);
        _item.setAstatus(_tmpAstatus);
        final String _tmpSenderTime;
        _tmpSenderTime = _cursor.getString(_cursorIndexOfSenderTime);
        _item.setSenderTime(_tmpSenderTime);
        final String _tmpStatusSend;
        _tmpStatusSend = _cursor.getString(_cursorIndexOfStatusSend);
        _item.setStatusSend(_tmpStatusSend);
        final String _tmpLogId;
        _tmpLogId = _cursor.getString(_cursorIndexOfLogId);
        _item.setLogId(_tmpLogId);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<SMSValidation> getListfromLogId(String id) {
    final String _sql = "SELECT * FROM smsValidation WHERE logId LIKE ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (id == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, id);
    }
    final Cursor _cursor = __db.query(_statement);
    try {
      final int _cursorIndexOfId = _cursor.getColumnIndexOrThrow("Id");
      final int _cursorIndexOfMSISDN = _cursor.getColumnIndexOrThrow("MSISDN");
      final int _cursorIndexOfMessage = _cursor.getColumnIndexOrThrow("Message");
      final int _cursorIndexOfShortcode = _cursor.getColumnIndexOrThrow("shortcode");
      final int _cursorIndexOfSenderID = _cursor.getColumnIndexOrThrow("senderId");
      final int _cursorIndexOfAstatus = _cursor.getColumnIndexOrThrow("Astatus");
      final int _cursorIndexOfSenderTime = _cursor.getColumnIndexOrThrow("sendingTime");
      final int _cursorIndexOfStatusSend = _cursor.getColumnIndexOrThrow("statusSend");
      final int _cursorIndexOfLogId = _cursor.getColumnIndexOrThrow("logId");
      final List<SMSValidation> _result = new ArrayList<SMSValidation>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final SMSValidation _item;
        _item = new SMSValidation();
        final String _tmpId;
        _tmpId = _cursor.getString(_cursorIndexOfId);
        _item.setId(_tmpId);
        final String _tmpMSISDN;
        _tmpMSISDN = _cursor.getString(_cursorIndexOfMSISDN);
        _item.setMSISDN(_tmpMSISDN);
        final String _tmpMessage;
        _tmpMessage = _cursor.getString(_cursorIndexOfMessage);
        _item.setMessage(_tmpMessage);
        final String _tmpShortcode;
        _tmpShortcode = _cursor.getString(_cursorIndexOfShortcode);
        _item.setShortcode(_tmpShortcode);
        final String _tmpSenderID;
        _tmpSenderID = _cursor.getString(_cursorIndexOfSenderID);
        _item.setSenderID(_tmpSenderID);
        final String _tmpAstatus;
        _tmpAstatus = _cursor.getString(_cursorIndexOfAstatus);
        _item.setAstatus(_tmpAstatus);
        final String _tmpSenderTime;
        _tmpSenderTime = _cursor.getString(_cursorIndexOfSenderTime);
        _item.setSenderTime(_tmpSenderTime);
        final String _tmpStatusSend;
        _tmpStatusSend = _cursor.getString(_cursorIndexOfStatusSend);
        _item.setStatusSend(_tmpStatusSend);
        final String _tmpLogId;
        _tmpLogId = _cursor.getString(_cursorIndexOfLogId);
        _item.setLogId(_tmpLogId);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }
}
