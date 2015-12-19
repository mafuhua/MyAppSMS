package com.hellomc.myappsms;

import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.telephony.SmsManager;

import java.io.InputStream;
import java.util.ArrayList;

public class MyUtils {


    public static void printCursor(Cursor cursor) {

        if (cursor == null) {
            System.out.println("cursor == null");
            return;
        }

        if (cursor.getCount() == 0) {
            System.out.println("cursor.getCount() == 0");
            return;
        }


        System.out.println("cursor.getCount() ::" + cursor.getCount());
        while (cursor.moveToNext()) {
            System.out.println("��ǰ���±꣺" + cursor.getPosition());
            int colCount = cursor.getColumnCount();
            for (int i = 0; i < colCount; i++) {
                String colName = cursor.getColumnName(i); // ��ø��е�����
                String value = cursor.getString(i); // ��ø��е�ֵ
                System.out.println(colName + " : " + value);
            }
        }
//		cursor.close();
    }


    public static String getNameByAddress(Context ctx, String address) {
        String name = null;

        Uri uri = Phone.CONTENT_URI;

        Cursor cursor = ctx.getContentResolver().query(uri, new String[]{"display_name"},
                " data1 = ?", new String[]{address}, null);
        // Ĭ�Ϸ��ص�cursor ָ���һ�е���һ��
        if (cursor.moveToNext()) { // �ƶ��ɹ���˵��������
            name = cursor.getString(0); // ������һ��
        }
        cursor.close();

        return name;
    }

    public static String getContactIdByAddress(Context ctx, String address) {
        String contactId = null;


        Uri uri = Phone.CONTENT_URI;
        Cursor cursor = ctx.getContentResolver().query(uri, new String[]{"contact_id"},
                " data1 = ?", new String[]{address}, null);
        if (cursor.moveToNext()) {
            // ���������ϵ��
            contactId = cursor.getString(0);
        }
        cursor.close();
        return contactId;
    }


    public static Bitmap getFaceByContactId(Context ctx, String contactId) {
        Bitmap face = null;

        Uri faceUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, contactId);

        InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(ctx.getContentResolver(), faceUri);
        if (inputStream != null) {
            face = BitmapFactory.decodeStream(inputStream); // ����������ת��ΪͼƬ
        }

        return face;
    }

    public static void sendTextMsg(Context ctx, String address, String body) {
        SmsManager smsManager = SmsManager.getDefault();
        ArrayList<String> divideMessage = smsManager.divideMessage(body);
        for (String string : divideMessage) {
            Intent intent = new Intent("haha");
            PendingIntent sentIntent = PendingIntent.getBroadcast(ctx, 88, intent, PendingIntent.FLAG_ONE_SHOT);
            smsManager.sendTextMessage(
                    address,
                    null,
                    string,
                    sentIntent,
                    null);
        }
        insertMsgDB(ctx,address,body);


    }
    public static void insertMsgDB(Context ctx, String address, String body){
        ContentValues values = new ContentValues();
        values.put("address",address);
        values.put("body",body);
        values.put("type",2);
        ctx.getContentResolver().insert(MyContants.URI_SMS,values);
    }
}
