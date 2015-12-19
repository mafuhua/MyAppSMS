package com.hellomc.myappsms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class SmsSuccessReceiver extends BroadcastReceiver {
    public SmsSuccessReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "短信发送成功", Toast.LENGTH_SHORT).show();
    }
}
