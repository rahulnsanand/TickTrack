package com.theflopguyproductions.ticktrack.background;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.ui.home.activity.alarm.DismissAlarmActivity;

public class AlarmService extends IntentService {

    private static final int NOTIFICATION_ID = 1;
    private static final String TAG = "AlarmService";
    private NotificationManager notificationManager;
    private PendingIntent pendingIntent;


    public AlarmService() {
        super("AlarmService");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e(TAG, "Alarm Service has started.");
        Context context = this.getApplicationContext();
        notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent mIntent = new Intent(this, DismissAlarmActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("test", "test");
        mIntent.putExtras(bundle);
        pendingIntent = PendingIntent.getActivity(context, 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Resources res = this.getResources();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        builder.setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.ic_launcher_foreground))
                .setTicker("TITLE")
                .setAutoCancel(true)
                .setContentTitle("TITLE2")
                .setContentText("SUBJECT");

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());

        Log.e(TAG, "Notifications sent.");
    }
}