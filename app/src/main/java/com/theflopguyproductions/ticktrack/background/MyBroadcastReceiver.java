package com.theflopguyproductions.ticktrack.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

import com.theflopguyproductions.ticktrack.ui.home.activity.alarm.DismissAlarmActivity;
import com.theflopguyproductions.ticktrack.utils.TickTrackAlarmManager;

import java.util.Objects;

public class MyBroadcastReceiver extends BroadcastReceiver {

    public static final String ACTION_ALARM = "me.proft.alarms.ACTION_ALARM";

    @Override
    public void onReceive(Context context, Intent intent) {

        System.out.println("RECEIVER RECEIVED SHIT");

        Log.e("TETE", "BroadcastReceiver has received alarm intent.");
        Intent service1 = new Intent(context, AlarmService.class);
        context.startService(service1);

        if (ACTION_ALARM.equals(intent.getAction())) {
            Toast.makeText(context, "Alarm....", Toast.LENGTH_LONG).show();
            getFilter();
            Intent dismissIntent = new Intent(context, DismissAlarmActivity.class);
            dismissIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(dismissIntent);
        }
        else if (Objects.equals(intent.getAction(), "android.intent.action.BOOT_COMPLETED")) {
            // Set the alarm here.
            TickTrackAlarmManager.resetAlarmOnBoot(context);
        }

    }

    public IntentFilter getFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        return filter;
    }
}
