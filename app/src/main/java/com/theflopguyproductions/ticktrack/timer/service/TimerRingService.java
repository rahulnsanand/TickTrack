package com.theflopguyproductions.ticktrack.timer.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.SoYouADeveloperHuh;
import com.theflopguyproductions.ticktrack.application.TickTrack;
import com.theflopguyproductions.ticktrack.timer.TimerData;

import java.util.ArrayList;

public class TimerRingService extends Service {

    public static final String ACTION_ADD_TIMER_FINISH = "";
    public static final String ACTION_KILL_ALL_TIMERS = "";

    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;
    private NotificationManagerCompat notificationManagerCompat;

    private static ArrayList<TimerServiceData> timerServiceDataArrayList = new ArrayList<>();
    private static ArrayList<TimerData> timerDataArrayList = new ArrayList<>();

    public TimerRingService(){
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("TAG_TIMER_RANG_SERVICE", "My foreground service onCreate().");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null) {

            String action = intent.getAction();


            assert action != null;
            switch (action) {
                case ACTION_ADD_TIMER_FINISH:
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void setupCustomNotification(){

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());

        Intent resultIntent = new Intent(this, SoYouADeveloperHuh.class);
        resultIntent.putExtra("FragmentID", 2);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationBuilder = new NotificationCompat.Builder(this, TickTrack.COUNTER_NOTIFICATION)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setOnlyAlertOnce(true)
                .setOngoing(true)
                .setContentIntent(resultPendingIntent);

        if (android.os.Build.VERSION. SDK_INT >= android.os.Build.VERSION_CODES. O ) {
            notificationBuilder.setChannelId(TickTrack.COUNTER_NOTIFICATION);
        }

    }
}
