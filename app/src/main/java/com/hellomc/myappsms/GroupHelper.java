package com.hellomc.myappsms;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by hellomc on 2015/12/1.
 */
public class GroupHelper extends SQLiteOpenHelper {
    public GroupHelper(Context context, String name, int version) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table groups(_id integer primary key autoincrement, group_name varchar(30));");

        db.execSQL("create table group_thread(_id integer primary key autoincrement, group_id integer,thread_id integer);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
