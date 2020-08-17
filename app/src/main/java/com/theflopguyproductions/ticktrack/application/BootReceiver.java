package com.theflopguyproductions.ticktrack.application;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "BOOOOOOOOOOOTH", Toast.LENGTH_SHORT).show();
    }
}
