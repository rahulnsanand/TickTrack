package com.theflopguyproductions.ticktrack.stopwatch.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.SoYouADeveloperHuh;
import com.theflopguyproductions.ticktrack.application.TickTrack;
import com.theflopguyproductions.ticktrack.stopwatch.StopwatchData;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;

import java.util.ArrayList;

public class StopwatchNotificationService extends Service {

    public static final String ACTION_STOP_STOPWATCH_SERVICE = "ACTION_STOP_STOPWATCH_SERVICE";
    public static final String ACTION_START_STOPWATCH_SERVICE = "ACTION_START_STOPWATCH_SERVICE";
    public static final String ACTION_RESUME_STOPWATCH_SERVICE = "ACTION_RESUME_STOPWATCH_SERVICE";
    public static final String ACTION_RESET_STOPWATCH_SERVICE = "ACTION_RESET_STOPWATCH_SERVICE";
    public static final String ACTION_LAP_STOPWATCH_SERVICE = "ACTION_LAP_STOPWATCH_SERVICE";
    public static final String ACTION_PAUSE_STOPWATCH_SERVICE = "ACTION_PAUSE_STOPWATCH_SERVICE";


    private NotificationCompat.Builder notificationBuilder;
    private NotificationManagerCompat notificationManagerCompat;

    private static ArrayList<StopwatchData> stopwatchData;
    private TickTrackDatabase tickTrackDatabase;
    private TickTrackNotificationStopwatch tickTrackNotificationStopwatch;

    public StopwatchNotificationService() {
    }

    private void baseLineNotificationLayout(){

        notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());

        Intent resultIntent = new Intent(this, SoYouADeveloperHuh.class);
        resultIntent.putExtra("FragmentID", 4);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(4, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent deleteIntent = new Intent(this, StopwatchNotificationService.class);
        deleteIntent.setAction(StopwatchNotificationService.ACTION_STOP_STOPWATCH_SERVICE);
        PendingIntent deletePendingIntent = PendingIntent.getService(this,
                4,
                deleteIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        notificationBuilder = new NotificationCompat.Builder(this, TickTrack.STOPWATCH_NOTIFICATION)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setOnlyAlertOnce(true)
                .setContentIntent(resultPendingIntent)
                .setDeleteIntent(deletePendingIntent)
                .setOngoing(true);

        if (android.os.Build.VERSION. SDK_INT >= android.os.Build.VERSION_CODES. O ) {
            notificationBuilder.setChannelId(TickTrack.STOPWATCH_NOTIFICATION);
        }

    }

    private void setupLapPause(){


        Intent lapIntent = new Intent(this, StopwatchNotificationService.class);
        lapIntent.setAction(ACTION_LAP_STOPWATCH_SERVICE);
        PendingIntent pendingLapIntent = PendingIntent.getService(this, 5, lapIntent, 0);
        NotificationCompat.Action lapAction = new NotificationCompat.Action(R.drawable.ic_round_flag_light_24, "Lap", pendingLapIntent);

        Intent pauseIntent = new Intent(this, StopwatchNotificationService.class);
        pauseIntent.setAction(ACTION_PAUSE_STOPWATCH_SERVICE);
        PendingIntent pendingPauseIntent = PendingIntent.getService(this, 5, pauseIntent, 0);
        NotificationCompat.Action pauseAction = new NotificationCompat.Action(R.drawable.ic_round_pause_white_24, "Pause", pendingPauseIntent);

        notificationBuilder.addAction(pauseAction);
        notificationBuilder.addAction(lapAction);

        notificationManagerCompat.notify(4, notificationBuilder.build());
    }

    private void setupResumeReset(){

        Intent resumeIntent = new Intent(this, StopwatchNotificationService.class);
        resumeIntent.setAction(ACTION_RESUME_STOPWATCH_SERVICE);
        PendingIntent pendingPlusIntent = PendingIntent.getService(this, 5, resumeIntent, 0);
        NotificationCompat.Action resumeAction = new NotificationCompat.Action(R.drawable.ic_round_play_white_24, "Resume", pendingPlusIntent);

        Intent resetIntent = new Intent(this, StopwatchNotificationService.class);
        resetIntent.setAction(ACTION_RESET_STOPWATCH_SERVICE);
        PendingIntent pendingMinusIntent = PendingIntent.getService(this, 5, resetIntent, 0);
        NotificationCompat.Action resetAction = new NotificationCompat.Action(R.drawable.ic_stop_white_24, "Reset", pendingMinusIntent);


        notificationBuilder.addAction(resumeAction);
        notificationBuilder.addAction(resetAction);

        notificationManagerCompat.notify(4, notificationBuilder.build());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        tickTrackNotificationStopwatch = new TickTrackNotificationStopwatch(this);
        Log.d("TAG_TIMER_SERVICE", "My foreground service onCreate().");
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

            initializeValues();

            assert action != null;
            switch (action) {
                case ACTION_START_STOPWATCH_SERVICE:
                    startForegroundService();
                    break;
                case ACTION_STOP_STOPWATCH_SERVICE:
                    stopStopwatchService();
                    break;
                case ACTION_RESUME_STOPWATCH_SERVICE:
                    resumeStopwatchService();
                    break;
                case ACTION_RESET_STOPWATCH_SERVICE:
                    resetStopwatchService();
                    break;
                case ACTION_LAP_STOPWATCH_SERVICE:
                    lapStopwatchService();
                    break;
                case ACTION_PAUSE_STOPWATCH_SERVICE:
                    pauseStopwatchService();
                    break;
            }

        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void initializeValues(){

        tickTrackDatabase = new TickTrackDatabase(this);
        stopwatchData = tickTrackDatabase.retrieveStopwatchData();
        baseLineNotificationLayout();
        tickTrackNotificationStopwatch.setupNotificationStuff(notificationManagerCompat, notificationBuilder);

    }
    private void startForegroundService() {

        setupLayout();

        startForeground(4, notificationBuilder.build());
        Toast.makeText(this, "Stopwatch Notification created!", Toast.LENGTH_SHORT).show();

    }
    private void setupLayout(){

        notificationBuilder.clearActions();

        if(stopwatchData.size()>0){
            if(stopwatchData.get(0).isPause()){
                setupResumeReset();
                tickTrackNotificationStopwatch.pauseInit();
            } else {
                setupLapPause();
                tickTrackNotificationStopwatch.resumeInit();
            }
        }
    }

    private void pauseStopwatchService() {
        tickTrackNotificationStopwatch.pause();
        setupResumeReset();
    }

    private void lapStopwatchService() {
        tickTrackNotificationStopwatch.lap();
        setupLapPause();
    }

    private void resetStopwatchService() {
        tickTrackNotificationStopwatch.stop();
        stopStopwatchService();
    }

    private void resumeStopwatchService() {
        if(stopwatchData.get(0).isPause()){
            tickTrackNotificationStopwatch.resume();
            setupLapPause();
        }
    }

    private void stopStopwatchService() {
        stopForeground(false);
        stopSelf();
        onDestroy();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tickTrackNotificationStopwatch.killStopwatch();
    }

}
