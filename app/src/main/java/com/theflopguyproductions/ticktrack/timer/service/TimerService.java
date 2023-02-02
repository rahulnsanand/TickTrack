package com.theflopguyproductions.ticktrack.timer.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.SoYouADeveloperHuh;
import com.theflopguyproductions.ticktrack.application.TickTrack;
import com.theflopguyproductions.ticktrack.timer.activity.TimerActivity;
import com.theflopguyproductions.ticktrack.timer.data.TimerData;
import com.theflopguyproductions.ticktrack.timer.quick.QuickTimerData;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

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
            if(quickTimerDataArrayList.get(i).isTimerOn() && !quickTimerDataArrayList.get(i).isTimerRinging()){
                result++;
            }
        }
        System.out.println("On Timer "+result);

        if(result==0){
            notificationManagerCompat.cancelAll();
            timerServiceRefreshHandler.removeCallbacks(refreshRunnable);
            stopSelf();
            stopForeground(true);
            onDestroy();
        }
        return result;
    }
    private void setupBaseNotification() {
        notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());

        notificationBuilder = new NotificationCompat.Builder(this, TickTrack.TIMER_RUNNING_NOTIFICATION)
                .setSmallIcon(R.drawable.ic_stat_timericonnotification)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(Notification.PRIORITY_MIN)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setOnlyAlertOnce(true)
                .setOngoing(true)
                .setAutoCancel(true)
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
            startForeground(TickTrack.TIMER_RUNNING_NOTIFICATION_ID, notificationBuilder.build());
            timerServiceRefreshHandler.post(refreshRunnable);
        }
    }

    private Runnable refreshRunnable = new Runnable() {
        public void run() {
            if(getAllOnTimers()>0){
                if(getAllOnTimers()==1){
                    if(!isSingle){
                        setupSingleTimerNotificationLayout();
                    }
                    notificationBuilder.setContentText("Next timer in "+getNextOccurrence());

                } else {
                    if(!isMulti){
                        setupMultiTimerNotificationLayout();
                    }
                    notificationBuilder.setContentText(getAllOnTimers()+" timers running");
                }
                notificationManagerCompat.notify(TickTrack.TIMER_RUNNING_NOTIFICATION_ID, notificationBuilder.build());
                timerServiceRefreshHandler.post(refreshRunnable);
            }
        }
    };

    private boolean isMulti = false;
    private void setupMultiTimerNotificationLayout() {
        Intent resultIntent;
        resultIntent = new Intent(this, SoYouADeveloperHuh.class);
        tickTrackDatabase.storeCurrentFragmentNumber(2);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent =
                null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_MUTABLE);
        } else {
            resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        notificationBuilder.setContentIntent(resultPendingIntent);
        isMulti = true;
        isSingle = false;
    }

    private boolean isSingle = false;
    private void setupSingleTimerNotificationLayout() {
        timerDataArrayList = tickTrackDatabase.retrieveTimerList();

        Intent resultIntent;
        if(isQuickTimerCheck()){
            resultIntent = new Intent(this, SoYouADeveloperHuh.class);
            tickTrackDatabase.storeCurrentFragmentNumber(2);
        } else {
            resultIntent = new Intent(this, TimerActivity.class);
            resultIntent.putExtra("timerID",timerDataArrayList.get(getCurrentTimerPosition(getSingleOnTimer())).getTimerID());
        }
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent =
                null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_MUTABLE);
        } else {
            resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        notificationBuilder.setContentIntent(resultPendingIntent);
        isSingle = true;
        isMulti = false;
    }

    private boolean isQuickTimerCheck() {
        quickTimerDataArrayList = tickTrackDatabase.retrieveQuickTimerList();
        for(int i=0; i<quickTimerDataArrayList.size(); i++){
            if(quickTimerDataArrayList.get(i).isTimerOn() && !quickTimerDataArrayList.get(i).isTimerRinging()){
                return true;
            }
        }
        return false;
    }

    private int getCurrentTimerPosition(int timerIntegerID){
        timerDataArrayList = tickTrackDatabase.retrieveTimerList();
        quickTimerDataArrayList = tickTrackDatabase.retrieveQuickTimerList();
        for(int i = 0; i < timerDataArrayList.size(); i ++){
            if(timerDataArrayList.get(i).getTimerIntID()==timerIntegerID){
                return i;
            }
        }
        for(int i = 0; i < quickTimerDataArrayList.size(); i ++){
            if(quickTimerDataArrayList.get(i).getTimerIntID()==timerIntegerID){
                return i;
            }
        }
        return -1;
    }
    private int getSingleOnTimer() {
        timerDataArrayList = tickTrackDatabase.retrieveTimerList();
        quickTimerDataArrayList = tickTrackDatabase.retrieveQuickTimerList();
        for(int i=0; i<timerDataArrayList.size(); i++){
            if(timerDataArrayList.get(i).isTimerNotificationOn()){
                return timerDataArrayList.get(i).getTimerIntID();
            }
        }
        for(int i=0; i<quickTimerDataArrayList.size(); i++){
            if(quickTimerDataArrayList.get(i).isTimerOn() && !quickTimerDataArrayList.get(i).isTimerRinging()){
                return quickTimerDataArrayList.get(i).getTimerIntID();
            }
        }
        return -1;
    }

    private String getNextOccurrence() {

        long nextRingTime = getNextStartTime();

        int hours = (int) TimeUnit.MILLISECONDS.toHours(nextRingTime);
        int minutes = (int) (TimeUnit.MILLISECONDS.toMinutes(nextRingTime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(nextRingTime)));


        if(hours==0){
            if(minutes==0){
                return "less than a minute";
            } else if(minutes>1){
                return "less than "+(minutes+1)+" minutes";
            } else if(minutes==1){
                return "less than "+(minutes+1)+" minutes";
            }
        } else if (hours==1){
            if(minutes==0){
                return "less than an hr and a min";
            } else if(minutes>2){
                return "less than an hr and "+(minutes+1)+" mins";
            } else if(minutes==1){
                return "less than an hr and "+(minutes+1)+" mins";
            }
        } else if (hours>1){
            if(minutes==0){
                return "less than "+hours+" hrs and a min";
            } else if(minutes>1){
                return "less than "+hours+" hrs and "+(minutes+1)+" mins";
            } else if(minutes==1){
                return "less than  "+hours+" hrs and "+(minutes+1)+" mins";
            }
        }


        return String.format(Locale.getDefault()," less than %02d:%02d", hours,minutes);
    }

    private long getNextStartTime() {
        ArrayList<Long> endTimes = new ArrayList<>();
        timerDataArrayList = tickTrackDatabase.retrieveTimerList();
        quickTimerDataArrayList = tickTrackDatabase.retrieveQuickTimerList();

        for(int i=0; i<timerDataArrayList.size(); i++){
            if(timerDataArrayList.get(i).isTimerOn() && !timerDataArrayList.get(i).isTimerPause() && !timerDataArrayList.get(i).isTimerRinging()){
                endTimes.add(timerDataArrayList.get(i).getTimerTotalTimeInMillis() - (System.currentTimeMillis()
                                -timerDataArrayList.get(i).getTimerStartTimeInMillis()));
            }
        }
        for(int i=0; i<quickTimerDataArrayList.size(); i++){
            if(quickTimerDataArrayList.get(i).isTimerOn() && !quickTimerDataArrayList.get(i).isTimerRinging()){
                endTimes.add(quickTimerDataArrayList.get(i).getTimerTotalTimeInMillis() - (System.currentTimeMillis()
                        -quickTimerDataArrayList.get(i).getTimerStartTimeInMillis()));
            }
        }

        if(endTimes.size()>0){
            return Collections.min(endTimes);
        }

        return -1;
    }

    private void stopTimerService() {
        if(getAllOnTimers() == 0){
            timerServiceRefreshHandler.removeCallbacks(refreshRunnable);
            stopSelf();
            stopForeground(false);
            onDestroy();
        }
    }

    private void startTimerService() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(false);
    }
}
