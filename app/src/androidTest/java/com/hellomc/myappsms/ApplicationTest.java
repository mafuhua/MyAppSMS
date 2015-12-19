package com.hellomc.myappsms;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.ContactsContract;
import android.test.ApplicationTestCase;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    private Context ctx;
    private GroupHelper dbHelper;
    private String[] projection = new String[]{
            "sms.body AS snippet",
            " sms.thread_id AS thread_id ",
            " groups.msg_count AS msg_count ",
            " address  as address",
            " date as date"};

    public ApplicationTest() {
        super(Application.class);
    }

        public void test(){
            Uri uri = Uri.parse("content://sms/conversations");

            Cursor cursor = getContext().getContentResolver().query(uri, projection, null, null, null);

            MyUtils.printCursor(cursor);
        }
        public void testContact(){

            Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

            Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null);

            MyUtils.printCursor(cursor);
        }
    public void testFace() {

        dbHelper = new GroupHelper(ctx, "group.db", 1);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor cursor = database.query("group.db", null, null, null, null, null, null);
        System.out.println(cursor);


    }

    public void testonCreate(SQLiteDatabase db) {
        dbHelper = new GroupHelper(ctx, "group.db", 1);
        Cursor cursor = db.query("group.db", null, null, null, null, null, null);
        System.out.println(cursor);
      /*  GroupDao.getInstance(ctx);
        db.execSQL("create table groups(_id integer primary key autoincrement, group_name varchar(30));");

        db.execSQL("create table group_thread(_id integer primary key autoincrement, group_id integer,thread_id integer);");*/
    }

}