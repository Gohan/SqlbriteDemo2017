package com.baozs.demos.sqlbritedemo2017.models;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by vashzhong on 2017/4/23.
 */

public class DatabaseManager extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "DemoDB";
    private static final int DB_VERSION = 3;

    private static final String CREATE_TABLE_JOKES_V1 = "CREATE TABLE IF NOT EXISTS joke_table(_id int primary key, hashid text, content text, unixtime int, updatetime text);";
    private static final String DROP_TABLE_JOKES_V1 = "DROP TABLE joke_table;";
    private static final String CREATE_TABLE_JOKES_V3 = "CREATE TABLE IF NOT EXISTS post_table(id int primary key, title text, content text, created int, last_modified int);";

    public DatabaseManager(Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_JOKES_V3);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 3) {
            db.execSQL(DROP_TABLE_JOKES_V1);
        }

        db.execSQL(CREATE_TABLE_JOKES_V3);
    }
}
