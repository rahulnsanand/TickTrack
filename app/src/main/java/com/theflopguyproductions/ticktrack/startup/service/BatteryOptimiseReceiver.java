package com.theflopguyproductions.ticktrack.startup.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.theflopguyproductions.ticktrack.startup.StartUpActivity;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;

import java.util.Objects;

public class BatteryOptimiseReceiver extends BroadcastReceiver {

    public static final String ACTION_OPTIMISING_DONE_CHECK = "ACTION_OPTIMISING_DONE_CHECK";

    @Override
    public void onReceive(Context context, Intent intent) {
        if(Objects.equals(intent.getAction(), ACTION_OPTIMISING_DONE_CHECK)){
            TickTrackDatabase tickTrackDatabase = new TickTrackDatabase(context);
            tickTrackDatabase.storeStartUpFragmentID(7);
            tickTrackDatabase.setNotOptimised(true);

            Intent fireIntent = new Intent(context, StartUpActivity.class);
            fireIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(fireIntent);
        }
    }

}

