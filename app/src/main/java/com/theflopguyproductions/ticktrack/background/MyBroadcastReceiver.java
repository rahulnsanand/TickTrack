package com.theflopguyproductions.ticktrack.background;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action=="GUCCISNOOZE"){
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context. NOTIFICATION_SERVICE ) ;
            Notification notification = intent.getParcelableExtra("notification") ;
            int i = intent.getIntExtra("Notification ID",0);
            if(notification!=null){
                System.out.println("HELLO");
                notificationManager.cancel(i);
            }
        }
    }
}
