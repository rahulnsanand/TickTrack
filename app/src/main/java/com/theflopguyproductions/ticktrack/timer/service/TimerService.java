package com.theflopguyproductions.ticktrack.timer.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.application.TickTrack;
import com.theflopguyproductions.ticktrack.timer.data.TimerData;
import com.theflopguyproductions.ticktrack.timer.quick.QuickTimerData;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;

import java.util.ArrayList;

public class TimerService extends Service {

    public static final String ACTION_STOP_TIMER_SERVICE = "ACTION_STOP_TIMER_SERVICE";
    public static final String ACTION_START_TIMER_SERVICE = "ACTION_START_TIMER_SERVICE";

    private NotificationCompat.Builder notificationBuilder;
    private NotificationManagerCompat notificationManagerCompat;
    private TickTrackDatabase tickTrackDatabase;

    private ArrayList<TimerData> timerDataArrayList = new ArrayList<>();
    private ArrayList<QuickTimerData> quickTimerDataArrayList = new ArrayList<>();

    private Handler timerServiceRefreshHandler = new Handler();
    private boolean isSetup = false;

    public TimerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        tickTrackDatabase = new TickTrackDatabase(this);
        Log.d("TAG_TIMER_SERVICE", "My foreground service onCreate().");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private int getAllOnTimers() {
        int result = 0;
        timerDataArrayList = tickTrackDatabase.retrieveTimerList();
        quickTimerDataArrayList = tickTrackDatabase.retrieveQuickTimerList();
        for(int i=0; i<timerDataArrayList.size(); i++){
            if(timerDataArrayList.get(i).isTimerNotificationOn()){
                result++;
            }
        }
        for(int i=0; i<quickTimerDataArrayList.size(); i++){
            if(quickTimerDataArrayList.get(i).isTimerOn()){
                result++;
            }
        }
        System.out.println("TIMER_SERVICE COUNT "+result);
        return result;
    }
    private void setupBaseNotification() {
        notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());

        notificationBuilder = new NotificationCompat.Builder(this, TickTrack.TIMER_COMPLETE_NOTIFICATION)
                .setSmallIcon(R.drawable.timer_notification_mini_icon)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(Notification.PRIORITY_MAX) //TODO CHANGE PRIORITY TO MIN
                .setVibrate(new long[0])
                .setOnlyAlertOnce(true)
                .setOngoing(true)
                .setColor(ContextCompat.getColor(this, R.color.Accent));

        notificationBuilder.setContentTitle("TickTrack Timer");
        notificationBuilder.setContentText("Timer Running");
        isSetup=true;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null) {

            String action = intent.getAction();

            initializeValues();

            assert action != null;
            switch (action) {
                case ACTION_START_TIMER_SERVICE:
                    startTimerService();
                    break;
                case ACTION_STOP_TIMER_SERVICE:
                    stopTimerService();
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void initializeValues() {
        stopForeground(false);
        if(!isSetup){
            setupBaseNotification();
        }
        if(getAllOnTimers()>0){
            startForeground(3, notificationBuilder.build());
            timerServiceRefreshHandler.post(refreshRunnable);
        }

    }

    private ArrayList<Long> endTimes = new ArrayList<>();

    private Runnable refreshRunnable = new Runnable() {
        public void run() {
            if(getAllOnTimers()>0){
                if(getAllOnTimers()==1){
                    System.out.println("RUNNING LOOP"+getAllOnTimers());
                    notificationBuilder.setContentText(getNextOccurrence());
                } else {
                    System.out.println("RUNNING LOOP"+getAllOnTimers());
                    notificationBuilder.setContentText(getAllOnTimers()+" timers running");
                }
                notificationManagerCompat.notify(3, notificationBuilder.build());
                timerServiceRefreshHandler.post(refreshRunnable);
            } else {
                timerServiceRefreshHandler.removeCallbacks(refreshRunnable);
                stopSelf();
                onDestroy();
            }
        }
    };

    private String getNextOccurrence() {
        return "GUCCI";
    }

    private void stopTimerService() {
        if(!(getAllOnTimers() >0)){
            timerServiceRefreshHandler.removeCallbacks(refreshRunnable);
            stopSelf();
            onDestroy();
        }
    }

    private void startTimerService() {

    }

}
