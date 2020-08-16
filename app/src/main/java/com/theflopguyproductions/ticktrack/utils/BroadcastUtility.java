package com.theflopguyproductions.ticktrack.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class BroadcastUtility extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (Intent.ACTION_TIME_CHANGED.equals(intent.getAction()) || Intent.ACTION_TIMEZONE_CHANGED.equals(intent.getAction()) ) {

            String toastText = "TickTrack Time Changed";
            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();

        }
        String toastText = "TickTrack Time Changed OUTSIDE";
        Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
    }
}
