package com.mouzhai.excelreader.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 数据库帮助类
 * <p>
 * Created by Mouzhai on 2017/2/21.
 */

public class PasswordDBHelper extends SQLiteOpenHelper {

    private static final String DATA_BASE_NAME = "PasswordFromExcel.db";
    static final String TABLE_NAME = "Password";
    private static final int VERSION = 1;

    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
            "(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            " sn TEXT," +
            " pass INTEGER," +
            " mac TEXT," +
            " pno TEXT," +
            " encryption TEXT," +
            " date INTEGER," +
            " description INTEGER," +
            " key INTEGER" +
            ")";
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    private static PasswordDBHelper instance;

    private PasswordDBHelper(Context context) {
        super(context, DATA_BASE_NAME, null, VERSION);
    }

    static PasswordDBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new PasswordDBHelper(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        createTable(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        updateTable(sqLiteDatabase);
    }

    /**
     * 创建表
     */
    private void createTable(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    /**
     * 更新表
     */
    private void updateTable(SQLiteDatabase db) {
        db.execSQL(DROP_TABLE);
        createTable(db);
    }
}
