package com.virtuoussoftware.apptly.content;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.security.PublicKey;

/**
 * ApptlyOpenHelper
 * Created by Aaron Sarazan on 8/26/12
 * <p/>
 * Copyright 2012 Virtuous Software. All rights reserved.
 */
public class ApptlyOpenHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "apptly.db";
    public static final int DATABASE_VERSION = 1;

    public static final String COLUMN__ID    ="_id";

    public static final String TABLE_POSTS  ="posts";
    public static final String COLUMN_ID    ="id";
    public static final String COLUMN_USER  ="user";
    public static final String COLUMN_HTML  ="html";
    public static final String COLUMN_DATE  ="date";

    public static final String TABLE_USERS          ="users";
    public static final String COLUMN_USER_ID       ="id";
    public static final String COLUMN_USER_UNAME    ="username";
    public static final String COLUMN_USER_NAME     ="name";

    private static final String POSTS_CREATE = "create table "
            + TABLE_POSTS   +"("
            + COLUMN__ID    +" integer primary key autoincrement, "
            + COLUMN_ID     +" integer, "
            + COLUMN_USER   +" integer, "
            + COLUMN_HTML   +" text not null, "
            + COLUMN_DATE   +" int);";

    private static final String USERS_CREATE = "create table "
            + TABLE_USERS           +"("
            + COLUMN__ID            +" integer primary key autoincrement, "
            + COLUMN_USER_ID        +" integer, "
            + COLUMN_USER_UNAME     +" text not null, "
            + COLUMN_USER_NAME      +" text not null);";

    public ApptlyOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(POSTS_CREATE);
        sqLiteDatabase.execSQL(USERS_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // TODO (asarazan)
    }
}
