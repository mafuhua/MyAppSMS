package com.hellomc.myappsms;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hellomc on 2015/12/1.
 */
public class GroupDao {
    private static GroupDao instance;
    private Context ctx;
    private GroupHelper dbHelper;
    private String table_group = "groups";
    private String table_group_thread = "group_thread";
    private Uri uri = Uri.parse("content://zz.itcast.cn.update.group");

    private GroupDao(Context ctx) {
        this.ctx = ctx;
        dbHelper = new GroupHelper(ctx, "group.db", 1);
    }

    public synchronized static GroupDao getInstance(Context ctx) {
        if (instance == null) {
            instance = new GroupDao(ctx);
        }
        return instance;
    }

    public void addNewGroup(String groupName) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("group_name", groupName);
        database.insert(table_group, null, values);
        ctx.getContentResolver().notifyChange(uri, null);

    }

    public void delete(String groupName) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        database.delete(table_group, "group_name=?", new String[]{groupName});
        ctx.getContentResolver().notifyChange(uri, null);
    }

    public void updateGroupName(String oldGroupName, String newGroupName) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("group_name", newGroupName);
        database.update(table_group, values, "group_name=?", new String[]{oldGroupName});
        ctx.getContentResolver().notifyChange(uri, null);
    }

    public Cursor getAllGroups() {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor cursor = database.query(table_group, null, null, null, null, null, null);
        cursor.setNotificationUri(ctx.getContentResolver(), uri);
        return cursor;
    }

    public List<Integer> getThreadByGroupId(int groupId) {
        List<Integer> threadIds = new ArrayList<>();
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.query(table_group_thread, null, "group_id = ?", new String[]{"" + groupId}, null, null, null);
        while (cursor.moveToNext()) {
            int threadId = cursor.getInt(cursor.getColumnIndex("thread_id"));
            threadIds.add(threadId);
        }
        cursor.close();
        return threadIds;
    }

    public void addThread2Group(int threadId, int groudId) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("group_id", groudId);
        values.put("thread_id", threadId);
        database.insert(table_group_thread, null, values);
    }
}
