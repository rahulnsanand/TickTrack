package com.theflopguyproductions.ticktrack.background;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class MyBroadcastReceiver extends BroadcastReceiver {
    public static final String ACTION_ALARM = "en.proft.alarms.ACTION_ALARM";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (ACTION_ALARM.equals(intent.getAction())) {
            Toast.makeText(context, "Alarm....", Toast.LENGTH_LONG).show();
            Intent service1 = new Intent(context, MyAlarmService.class);
            context.startService(service1);
        }

    }
}
