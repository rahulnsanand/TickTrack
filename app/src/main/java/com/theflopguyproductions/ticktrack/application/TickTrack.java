package com.theflopguyproductions.ticktrack.application;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.messaging.FirebaseMessaging;
import com.theflopguyproductions.ticktrack.service.CloudNotificationService;
import com.theflopguyproductions.ticktrack.utils.firebase.FirebaseHelper;

import java.util.Objects;

public class TickTrack extends Application implements Application.ActivityLifecycleCallbacks{

    public static final String COUNTER_NOTIFICATION = "TICK_TRACK_COUNTER";
    public static final String STOPWATCH_NOTIFICATION = "TICK_TRACK_STOPWATCH";
    public static final String TIMER_RUNNING_NOTIFICATION = "TIMER_RUNNING_NOTIFICATION";
    public static final String TIMER_MISSED_NOTIFICATION = "TIMER_MISSED_NOTIFICATION";
    public static final String TIMER_COMPLETE_NOTIFICATION = "TIMER_COMPLETE_NOTIFICATION";
    public static final String GENERAL_NOTIFICATION = "TICK_TRACK_GENERAL";
    public static final String MISCELLANEOUS_NOTIFICATION = "MISCELLANEOUS_NOTIFICATION";
    public static final String DATA_BACKUP_RESTORE_NOTIFICATION = "DATA_BACKUP_RESTORE_NOTIFICATION";
    public static final String PUSH_NOTIFICATION = "PUSH_NOTIFICATION";

    public static final int COUNTER_NOTIFICATION_ID = 1;
    public static final int TIMER_RUNNING_NOTIFICATION_ID = 2;
    public static final int TIMER_RINGING_NOTIFICATION_ID = 3;
    public static final int STOPWATCH_NOTIFICATION_ID = 4;
    public static final int TIMER_MISSED_NOTIFICATION_ID = 5;
    public static final int BACKUP_RESTORE_NOTIFICATION_ID = 6;
    public static final int MISCELLANEOUS_NOTIFICATION_ID = 7;
    public static final int GENERAL_NOTIFICATION_ID = 8;
    public static final int PUSH_NOTIFICATION_ID = 9;

    public static final String COUNTER_NOTIFICATION_DESCRIPTION = "No Sound, Counter feature";
    public static final String STOPWATCH_NOTIFICATION_DESCRIPTION = "No Sound, Stopwatch feature";
    public static final String TIMER_RUNNING_NOTIFICATION_DESCRIPTION = "No Sound, Ongoing Timer feature";
    public static final String TIMER_MISSED_NOTIFICATION_DESCRIPTION = "Make Sound, Missed Timers feature";
    public static final String TIMER_COMPLETE_NOTIFICATION_DESCRIPTION = "Make Sound, Elapsed Timer feature [Required]";
    public static final String GENERAL_NOTIFICATION_DESCRIPTION = "Make Sound, General App Alerts";
    public static final String MISCELLANEOUS_NOTIFICATION_DESCRIPTION = "Make Sound, Important App Alerts";
    public static final String DATA_BACKUP_RESTORE_NOTIFICATION_DESCRIPTION = "No Sound, Backup/Restore feature [Required]";
    public static final String PUSH_NOTIFICATION_DESCRIPTION = "Updates, Announcements and Other Important Notifications";


    @Override
    public void onCreate() {
        super.onCreate();


        if (android.os.Build.VERSION. SDK_INT >= android.os.Build.VERSION_CODES. O ) {

            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context. NOTIFICATION_SERVICE ) ;

            notificationGroupCreator(mNotificationManager);
            createCounterChannel(mNotificationManager);
            createGeneralChannel(mNotificationManager);
            createStopwatchChannel(mNotificationManager);
            createTimerChannel(mNotificationManager);
            createTimerCompleteChannel(mNotificationManager);
            createMiscellaneousChannel(mNotificationManager);
            createDataBackupRestoreChannel(mNotificationManager);
            createTimerMissedNotificationChannel(mNotificationManager);
            createPushNotificationChannel(mNotificationManager);

        }

        setupCrashAnalyticsBasicData();
        setupFirebaseCloudMessaging();

        System.out.println("ActivityManager: Displayed TickTrack OnCreate "+System.currentTimeMillis());

    }



    private void setupFirebaseCloudMessaging() {
        FirebaseMessaging.getInstance().subscribeToTopic("update_notifications")
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        FirebaseCrashlytics.getInstance().setCustomKey("General Notifications", true);
                    } else {
                        FirebaseCrashlytics.getInstance().setCustomKey("General Notifications", false);
                    }
                });
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    private void notificationGroupCreator(NotificationManager notificationManager) {
        String[] GroupIDs = {"Counter Group", "Timer Group", "Stopwatch Group", "General Group", "Miscellaneous Group", "Ultra Critical Group"};
        String[] groupName = {"Counter", "Timer", "Stopwatch", "General", "Miscellaneous", "Ultra Critical"};

        for(int i=0; i<GroupIDs.length; i++){
            NotificationChannelGroup notificationChannelGroup=
                    new  NotificationChannelGroup(GroupIDs[i], groupName[i]);
            notificationManager.createNotificationChannelGroup(notificationChannelGroup);
        }
    }

    private void setupCrashAnalyticsBasicData() {
        if(new FirebaseHelper(this).isUserSignedIn()){
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
            if(account!=null){
                FirebaseCrashlytics.getInstance().setUserId(Objects.requireNonNull(account.getEmail()));
            }
            FirebaseCrashlytics.getInstance().setCustomKey("isUserSignedIn", true);
        } else {
            FirebaseCrashlytics.getInstance().setCustomKey("isUserSignedIn", false);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createPushNotificationChannel(NotificationManager mNotificationManager) {
        int importance = NotificationManager. IMPORTANCE_HIGH ;
        NotificationChannel notificationChannel = new
                NotificationChannel( PUSH_NOTIFICATION , "Push Notifications" , importance) ;
        notificationChannel.enableLights( true ) ;
        notificationChannel.setLightColor(Color. BLUE ) ;
        notificationChannel.enableVibration( true ) ;
        notificationChannel.setVibrationPattern( new long []{ 100 , 100, 100}) ;
        notificationChannel.setImportance(NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.setGroup("General Group");
        notificationChannel.setDescription(PUSH_NOTIFICATION_DESCRIPTION);
        assert mNotificationManager != null;
        mNotificationManager.createNotificationChannel(notificationChannel) ;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createTimerMissedNotificationChannel(NotificationManager mNotificationManager) {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes. CONTENT_TYPE_SONIFICATION )
                .setUsage(AudioAttributes. USAGE_ALARM )
                .build() ;

        int importance = NotificationManager.IMPORTANCE_HIGH ;
        NotificationChannel notificationChannel = new
                NotificationChannel( TIMER_MISSED_NOTIFICATION , "Missed Timers" , importance) ;
        notificationChannel.enableLights( true ) ;
        notificationChannel.setLightColor(Color. RED ) ;
        notificationChannel.enableVibration( true ) ;
        notificationChannel.setGroup("Timer Group");
        notificationChannel.setVibrationPattern( new long []{ 100 , 100 , 100 , 100}) ;
        notificationChannel.setDescription(TIMER_MISSED_NOTIFICATION_DESCRIPTION);

//            notificationChannel.setSound(sound , audioAttributes) ;

        assert mNotificationManager != null;
        mNotificationManager.createNotificationChannel(notificationChannel) ;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createDataBackupRestoreChannel(NotificationManager mNotificationManager) {

        int importance = NotificationManager. IMPORTANCE_MIN ;
        NotificationChannel notificationChannel = new
                NotificationChannel( DATA_BACKUP_RESTORE_NOTIFICATION , "Backup [Do Not Disable]" , importance) ;
        notificationChannel.setGroup("Ultra Critical Group");
        notificationChannel.setDescription(DATA_BACKUP_RESTORE_NOTIFICATION_DESCRIPTION);
        assert mNotificationManager != null;
        mNotificationManager.createNotificationChannel(notificationChannel) ;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createMiscellaneousChannel(NotificationManager mNotificationManager) {

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes. CONTENT_TYPE_SONIFICATION )
                .setUsage(AudioAttributes. USAGE_ALARM )
                .build() ;

        int importance = NotificationManager. IMPORTANCE_HIGH ;
        NotificationChannel notificationChannel = new
                NotificationChannel( MISCELLANEOUS_NOTIFICATION , "Miscellaneous" , importance) ;
        notificationChannel.enableLights( true ) ;
        notificationChannel.setLightColor(Color. YELLOW ) ;
        notificationChannel.enableVibration( true ) ;
        notificationChannel.setGroup("Miscellaneous Group");
        notificationChannel.setDescription(MISCELLANEOUS_NOTIFICATION_DESCRIPTION);
        notificationChannel.setVibrationPattern( new long []{ 200 , 200 , 200 , 200 }) ;

//            notificationChannel.setSound(sound , audioAttributes) ;

        assert mNotificationManager != null;
        mNotificationManager.createNotificationChannel(notificationChannel) ;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createCounterChannel(NotificationManager mNotificationManager) {

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes. CONTENT_TYPE_SONIFICATION )
                .setUsage(AudioAttributes. USAGE_ALARM )
                .build() ;

        int importance = NotificationManager. IMPORTANCE_HIGH ;
        NotificationChannel notificationChannel = new
                NotificationChannel( COUNTER_NOTIFICATION , "Counter" , importance) ;
        notificationChannel.enableLights( true ) ;
        notificationChannel.setLightColor(Color. BLUE ) ;
        notificationChannel.enableVibration( true ) ;
        notificationChannel.setGroup("Counter Group");
        notificationChannel.setDescription(COUNTER_NOTIFICATION_DESCRIPTION);
        notificationChannel.setVibrationPattern( new long []{ 100 , 100 , 100}) ;

//            notificationChannel.setSound(sound , audioAttributes) ;

        assert mNotificationManager != null;
        mNotificationManager.createNotificationChannel(notificationChannel) ;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createTimerChannel(NotificationManager mNotificationManager) {

        int importance = NotificationManager.IMPORTANCE_MIN ;
        NotificationChannel notificationChannel = new
                NotificationChannel(TIMER_RUNNING_NOTIFICATION, "Ongoing Timer [Do Not Disable]" , importance) ;
        notificationChannel.setDescription(TIMER_RUNNING_NOTIFICATION_DESCRIPTION);
        notificationChannel.setGroup("Ultra Critical Group");
        assert mNotificationManager != null;
        mNotificationManager.createNotificationChannel(notificationChannel) ;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createTimerCompleteChannel(NotificationManager mNotificationManager) {

        int importance = NotificationManager. IMPORTANCE_HIGH ;
        NotificationChannel notificationChannel = new
                NotificationChannel(TIMER_COMPLETE_NOTIFICATION, "Ringing Timer [Do Not Disable]" , importance) ;
        notificationChannel.enableLights( true ) ;
        notificationChannel.setLightColor(Color. GREEN ) ;
        notificationChannel.enableVibration( true ) ;
        notificationChannel.setGroup("Ultra Critical Group");
        notificationChannel.setDescription(TIMER_COMPLETE_NOTIFICATION_DESCRIPTION);
        notificationChannel.setVibrationPattern( new long []{ 200 , 200 , 200 , 200 , 200 , 200 , 200 , 200 , 200 }) ;

        assert mNotificationManager != null;
        mNotificationManager.createNotificationChannel(notificationChannel) ;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createStopwatchChannel(NotificationManager mNotificationManager) {

        int importance = NotificationManager.IMPORTANCE_MIN ;
        NotificationChannel notificationChannel = new
                NotificationChannel( STOPWATCH_NOTIFICATION , "Stopwatch" , importance) ;
        notificationChannel.setDescription(STOPWATCH_NOTIFICATION_DESCRIPTION);
        notificationChannel.setGroup("Stopwatch Group");
        assert mNotificationManager != null;
        mNotificationManager.createNotificationChannel(notificationChannel) ;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createGeneralChannel(NotificationManager mNotificationManager) {

        int importance = NotificationManager. IMPORTANCE_HIGH ;
        NotificationChannel notificationChannel = new
                NotificationChannel( GENERAL_NOTIFICATION , "TickTrack Notification" , importance) ;
        notificationChannel.enableLights( true ) ;
        notificationChannel.setLightColor(Color. BLUE ) ;
        notificationChannel.enableVibration( true ) ;
        notificationChannel.setVibrationPattern( new long []{ 100 , 100, 100}) ;
        notificationChannel.setImportance(NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.setGroup("General Group");
        notificationChannel.setDescription(GENERAL_NOTIFICATION_DESCRIPTION);
        assert mNotificationManager != null;
        mNotificationManager.createNotificationChannel(notificationChannel) ;

    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        Intent restartService = new Intent(getApplicationContext(), CloudNotificationService.class);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(),91825,restartService,PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME,5000,pendingIntent);
    }
}
