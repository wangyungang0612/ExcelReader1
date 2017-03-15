package com.mouzhai.excelreader.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mouzhai.excelreader.model.Password;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据库增删改查功能
 * <p>
 * Created by Mouzhai on 2017/2/22.
 */

public class PasswordDao {

    private static PasswordDao instance;
    private PasswordDBHelper passwordDBHelper;
    private SQLiteDatabase database;

    private PasswordDao(Context context) {
        passwordDBHelper = PasswordDBHelper.getInstance(context);
        database = passwordDBHelper.getWritableDatabase();
    }

    public static PasswordDao getInstance(Context context) {
        if (instance == null) {
            instance = new PasswordDao(context);
        }
        return instance;
    }

    /**
     * 插入数据
     */
    void insertPassword(Password password) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("sn", password.getSn());
        contentValues.put("pass", password.getPass());
        contentValues.put("mac", password.getMac());
        contentValues.put("pno", password.getPno());
        contentValues.put("encryption", password.getEncryption());
        contentValues.put("date", String.valueOf(password.getDate()));
        contentValues.put("description", password.getDescription());
        contentValues.put("key", password.getKey());

        database.insert(PasswordDBHelper.TABLE_NAME, null, contentValues);
    }

    /**
     * 查询数据
     */
    public List<Password> queryPasswordBySn(String sn) {
        Cursor cursor = database.query(PasswordDBHelper.TABLE_NAME,
                new String[]{"sn, pass, mac, pno, encryption, date, description, key"},
                "sn = ?",
                new String[]{sn},
                null, null, null);
        List<Password> passwords = new ArrayList<>();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String mSn = cursor.getString(cursor.getColumnIndex("sn"));
                int pass = cursor.getInt(cursor.getColumnIndex("pass"));
                String mac = cursor.getString(cursor.getColumnIndex("mac"));
                String pno = cursor.getString(cursor.getColumnIndex("pno"));
                String encryption = cursor.getString(cursor.getColumnIndex("encryption"));
                String date = cursor.getString(cursor.getColumnIndex("date"));
                int description = cursor.getInt(cursor.getColumnIndex("description"));
                int key = cursor.getInt(cursor.getColumnIndex("key"));

                Password password = new Password(mSn, pass, mac, pno, encryption, date, description, key);
                passwords.add(password);
            }
            cursor.close();
        }
        return passwords;
    }
}
