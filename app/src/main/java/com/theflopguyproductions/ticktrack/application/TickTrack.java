package com.theflopguyproductions.ticktrack.application;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.theflopguyproductions.ticktrack.timer.service.TimerBroadcastReceiver;

public class TickTrack extends Application {

    public static final String COUNTER_NOTIFICATION = "TICK_TRACK_COUNTER";
    public static final String STOPWATCH_NOTIFICATION = "TICK_TRACK_STOPWATCH";
    public static final String TIMER_RUNNING_NOTIFICATION = "TIMER_RUNNING_NOTIFICATION";
    public static final String TIMER_COMPLETE_NOTIFICATION = "TIMER_COMPLETE_NOTIFICATION";
    public static final String GENERAL_NOTIFICATION = "TICK_TRACK_GENERAL";

    @Override
    public void onCreate() {
        super.onCreate();

        if (android.os.Build.VERSION. SDK_INT >= android.os.Build.VERSION_CODES. O ) {

            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context. NOTIFICATION_SERVICE ) ;
            createCounterChannel(mNotificationManager);
            createGeneralChannel(mNotificationManager);
            createStopwatchChannel(mNotificationManager);
            createTimerChannel(mNotificationManager);
            createTimerCompleteChannel(mNotificationManager);

        }

        ComponentName receiver = new ComponentName(this, TimerBroadcastReceiver.class);
        PackageManager pm = this.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);



    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createCounterChannel(NotificationManager mNotificationManager) {

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes. CONTENT_TYPE_SONIFICATION )
                .setUsage(AudioAttributes. USAGE_ALARM )
                .build() ;

        int importance = NotificationManager. IMPORTANCE_HIGH ;
        NotificationChannel notificationChannel = new
                NotificationChannel( COUNTER_NOTIFICATION , "Counter Notifications" , importance) ;
        notificationChannel.enableLights( true ) ;
        notificationChannel.setLightColor(Color. BLUE ) ;
        notificationChannel.enableVibration( true ) ;
        notificationChannel.setVibrationPattern( new long []{ 100 , 200 , 300 , 400 , 500 , 400 , 300 , 200 , 400 }) ;

//            notificationChannel.setSound(sound , audioAttributes) ;

        assert mNotificationManager != null;
        mNotificationManager.createNotificationChannel(notificationChannel) ;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createTimerChannel(NotificationManager mNotificationManager) {

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes. CONTENT_TYPE_SONIFICATION )
                .setUsage(AudioAttributes. USAGE_ALARM )
                .build() ;

        int importance = NotificationManager.IMPORTANCE_LOW ;
        NotificationChannel notificationChannel = new
                NotificationChannel(TIMER_RUNNING_NOTIFICATION, "Timer Running Notifications" , importance) ;
        notificationChannel.enableLights( true ) ;
        notificationChannel.setLightColor(Color. BLUE ) ;
        notificationChannel.enableVibration( true ) ;
        notificationChannel.setVibrationPattern( new long []{ 300 , 300 , 300 , 300 , 300 , 300 , 300 , 300 , 300 }) ;

//            notificationChannel.setSound(sound , audioAttributes) ;

        assert mNotificationManager != null;
        mNotificationManager.createNotificationChannel(notificationChannel) ;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createTimerCompleteChannel(NotificationManager mNotificationManager) {

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes. CONTENT_TYPE_SONIFICATION )
                .setUsage(AudioAttributes. USAGE_ALARM )
                .build() ;

        int importance = NotificationManager. IMPORTANCE_HIGH ;
        NotificationChannel notificationChannel = new
                NotificationChannel(TIMER_COMPLETE_NOTIFICATION, "Timer Complete Notifications" , importance) ;
        notificationChannel.enableLights( true ) ;
        notificationChannel.setLightColor(Color. BLUE ) ;
        notificationChannel.enableVibration( true ) ;
        notificationChannel.setVibrationPattern( new long []{ 100 , 200 , 300 , 400 , 500 , 400 , 300 , 200 , 400 }) ;

//            notificationChannel.setSound(sound , audioAttributes) ;

        assert mNotificationManager != null;
        mNotificationManager.createNotificationChannel(notificationChannel) ;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createStopwatchChannel(NotificationManager mNotificationManager) {

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes. CONTENT_TYPE_SONIFICATION )
                .setUsage(AudioAttributes. USAGE_ALARM )
                .build() ;

        int importance = NotificationManager. IMPORTANCE_HIGH ;
        NotificationChannel notificationChannel = new
                NotificationChannel( STOPWATCH_NOTIFICATION , "Stopwatch Notifications" , importance) ;
        notificationChannel.enableLights( true ) ;
        notificationChannel.setLightColor(Color. BLUE ) ;
        notificationChannel.enableVibration( true ) ;
        notificationChannel.setVibrationPattern( new long []{ 100 , 200 , 300 , 400 , 500 , 400 , 300 , 200 , 400 }) ;

//            notificationChannel.setSound(sound , audioAttributes) ;

        assert mNotificationManager != null;
        mNotificationManager.createNotificationChannel(notificationChannel) ;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createGeneralChannel(NotificationManager mNotificationManager) {

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes. CONTENT_TYPE_SONIFICATION )
                .setUsage(AudioAttributes. USAGE_ALARM )
                .build() ;

        int importance = NotificationManager. IMPORTANCE_HIGH ;
        NotificationChannel notificationChannel = new
                NotificationChannel( GENERAL_NOTIFICATION , "TickTrack Notifications" , importance) ;
        notificationChannel.enableLights( true ) ;
        notificationChannel.setLightColor(Color. BLUE ) ;
        notificationChannel.enableVibration( true ) ;
        notificationChannel.setVibrationPattern( new long []{ 100 , 200 , 300 , 400 , 500 , 400 , 300 , 200 , 400 }) ;
        notificationChannel.setImportance(NotificationManager.IMPORTANCE_HIGH);

//            notificationChannel.setSound(sound , audioAttributes) ;

        assert mNotificationManager != null;
        mNotificationManager.createNotificationChannel(notificationChannel) ;

    }

}
