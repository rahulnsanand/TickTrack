package com.theflopguyproductions.ticktrack.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.theflopguyproductions.ticktrack.utils.TickTrackAlarmManager;

import java.util.Objects;

public class MyBroadcastReceiver extends BroadcastReceiver {

    public static final String ACTION_ALARM = "me.proft.alarms.ACTION_ALARM";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (ACTION_ALARM.equals(intent.getAction())) {
            Toast.makeText(context, "Alarm....", Toast.LENGTH_LONG).show();
            Intent service1 = new Intent(context, MyAlarmService.class);
            context.startService(service1);
        }
        else if (Objects.equals(intent.getAction(), "android.intent.action.BOOT_COMPLETED")) {
            // Set the alarm here.
            TickTrackAlarmManager.resetAlarmOnBoot(context);
        }

    }
}
