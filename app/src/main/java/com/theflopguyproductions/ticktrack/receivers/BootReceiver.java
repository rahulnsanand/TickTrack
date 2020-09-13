package com.theflopguyproductions.ticktrack.receivers;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.SoYouADeveloperHuh;
import com.theflopguyproductions.ticktrack.application.TickTrack;
import com.theflopguyproductions.ticktrack.counter.CounterData;
import com.theflopguyproductions.ticktrack.counter.notification.CounterNotificationService;
import com.theflopguyproductions.ticktrack.stopwatch.StopwatchData;
import com.theflopguyproductions.ticktrack.stopwatch.StopwatchLapData;
import com.theflopguyproductions.ticktrack.stopwatch.service.StopwatchNotificationService;
import com.theflopguyproductions.ticktrack.timer.data.TimerData;
import com.theflopguyproductions.ticktrack.timer.quick.QuickTimerData;
import com.theflopguyproductions.ticktrack.timer.service.TimerRingService;
import com.theflopguyproductions.ticktrack.timer.service.TimerService;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackFirebaseDatabase;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackTimerDatabase;
import com.theflopguyproductions.ticktrack.utils.firebase.FirebaseHelper;

import java.util.ArrayList;

public class BootReceiver extends BroadcastReceiver {

    private static final String LOCKED_BOOT_COMPLETE = "android.intent.action.LOCKED_BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {

        TickTrackDatabase tickTrackDatabase = new TickTrackDatabase(context);

        System.out.println(intent.getAction()+" TIMER BOOT");

        if(LOCKED_BOOT_COMPLETE.equals(intent.getAction())){
            System.out.println(intent.getAction()+" TIMER BOOT");
            setupBootProcedure(context, tickTrackDatabase);
            tickTrackDatabase.setLockedBootComplete(true);
        } else {
            System.out.println(intent.getAction()+" TIMER BOOT ELSE");
            if(!tickTrackDatabase.isLockedBootComplete()){
                System.out.println(intent.getAction()+" TIMER BOOT ELSE NOT LOCKED");
                setupBootProcedure(context, tickTrackDatabase);
                tickTrackDatabase.setLockedBootComplete(false);
            }
            FirebaseHelper firebaseHelper = new FirebaseHelper(context);
            TickTrackFirebaseDatabase tickTrackFirebaseDatabase = new TickTrackFirebaseDatabase(context);
            if(firebaseHelper.isUserSignedIn() && !tickTrackFirebaseDatabase.isDriveLinkFail()){
                tickTrackFirebaseDatabase.setBackUpAlarm();
            }
        }
    }

    private void setupBootProcedure(Context context, TickTrackDatabase tickTrackDatabase) {
        reestablishTimers(context);

        ArrayList<CounterData> counterData = tickTrackDatabase.retrieveCounterList();
        checkNotification(context, counterData);

        ArrayList<StopwatchData> stopwatchData = tickTrackDatabase.retrieveStopwatchData();
        ArrayList<StopwatchLapData> stopwatchLapData = tickTrackDatabase.retrieveStopwatchLapData();
        if(stopwatchData.get(0).getStopwatchTimerStartTimeInMillis()!=-1){
            long timeElapsedOnBoot = System.currentTimeMillis() - stopwatchData.get(0).getStopwatchTimerStartTimeInMillis();
            stopwatchData.get(0).setStopwatchTimerStartTimeInRealTimeMillis(SystemClock.elapsedRealtime()-timeElapsedOnBoot);

            if(stopwatchData.get(0).isPause() && stopwatchData.get(0).isRunning()){
                long pauseElapsedOnBoot = System.currentTimeMillis() - stopwatchData.get(0).getLastPauseTimeInMillis();
                stopwatchData.get(0).setLastPauseTimeRealTimeInMillis(SystemClock.elapsedRealtime()-pauseElapsedOnBoot);
            }

            if(stopwatchLapData.size()>0){
                long lapElapsedOnBoot = System.currentTimeMillis() - stopwatchData.get(0).getProgressSystemValue();
                stopwatchData.get(0).setProgressValue(SystemClock.elapsedRealtime()-lapElapsedOnBoot);
                long lapLastUpdateTime = System.currentTimeMillis() - stopwatchLapData.get(0).getLastLapUpdateSystemTimeInMillis();
                stopwatchLapData.get(0).setLastLapUpdateRealtimeInMillis(SystemClock.elapsedRealtime() - lapLastUpdateTime);

            }

            tickTrackDatabase.storeStopwatchData(stopwatchData);
            tickTrackDatabase.storeLapData(stopwatchLapData);

            if(!isMyServiceRunning(StopwatchNotificationService.class, context)){
                startNotificationService(context);
            }
        }
    }

    public void reestablishTimers(Context activity){
        TickTrackDatabase tickTrackDatabase = new TickTrackDatabase(activity);
        ArrayList<TimerData> timerData = tickTrackDatabase.retrieveTimerList();
        TickTrackTimerDatabase tickTrackTimerDatabase = new TickTrackTimerDatabase(activity);
        int missedTimers = 0;

        for(int i = 0; i<timerData.size(); i++){
            timerData = tickTrackDatabase.retrieveTimerList();
            if(timerData.get(i).isTimerOn() && !timerData.get(i).isTimerPause() && !timerData.get(i).isTimerRinging()){ //TODO TIMER IS RUNNING, NOT RINGING
                //TODO CAN BE ELAPSED AND YET TO ELAPSE HERE
                if(timerData.get(i).getTimerStartTimeInMillis() != -1) {

                    long elapsedTime = System.currentTimeMillis() - timerData.get(i).getTimerStartTimeInMillis();

                    System.out.println("TIMER BOOT RUNNING NOT RINGINER ELAPSED TIME :"+elapsedTime);

                    if(elapsedTime<timerData.get(i).getTimerTotalTimeInMillis()){ //TODO TIMER YET TO ELAPSE

                        long nextAlarmStamp = SystemClock.elapsedRealtime()+(timerData.get(i).getTimerTotalTimeInMillis()-elapsedTime);

                        System.out.println("TIMER BOOT RUNNING YET TO NEXT ALARM TIME :"+nextAlarmStamp);

                        timerData.get(i).setTimerAlarmEndTimeInMillis(nextAlarmStamp);

                        tickTrackTimerDatabase.setAlarm(nextAlarmStamp, timerData.get(i).getTimerIntID(), false);

                        tickTrackDatabase.storeTimerList(timerData);

                        if(!isMyServiceRunning(TimerService.class, activity)){
                            tickTrackTimerDatabase.startNotificationService();
                        }

                    } else { //TODO TIMER ALREADY ELAPSED
                        timerData.get(i).setTimerOn(false);
                        timerData.get(i).setTimerPause(false);
                        timerData.get(i).setTimerRinging(false);
                        timerData.get(i).setTimerEndedTimeInMillis(-1);
                        timerData.get(i).setTimerStartTimeInMillis(-1);
                        tickTrackDatabase.storeTimerList(timerData);
                        missedTimers++;
                    }
                }

            } else if(timerData.get(i).isTimerOn() && !timerData.get(i).isTimerPause() && timerData.get(i).isTimerRinging()){ //TODO TIMER IS RINGING
                timerData.get(i).setTimerOn(false);
                timerData.get(i).setTimerPause(false);
                timerData.get(i).setTimerRinging(false);
                timerData.get(i).setTimerEndedTimeInMillis(-1);
                timerData.get(i).setTimerStartTimeInMillis(-1);
                tickTrackDatabase.storeTimerList(timerData);
                missedTimers++;
            }

        }

        ArrayList<QuickTimerData> quickTimerData = tickTrackDatabase.retrieveQuickTimerList();

        for(int i = 0; i<quickTimerData.size(); i++){
            quickTimerData = tickTrackDatabase.retrieveQuickTimerList();
            if(quickTimerData.get(i).isTimerOn() && !quickTimerData.get(i).isTimerPause() && !quickTimerData.get(i).isTimerRinging()){ //TODO TIMER IS RUNNING, NOT RINGING
                //TODO CAN BE ELAPSED AND YET TO ELAPSE HERE
                if(quickTimerData.get(i).getTimerStartTimeInMillis() != -1) {

                    long elapsedTime = System.currentTimeMillis() - quickTimerData.get(i).getTimerStartTimeInMillis();

                    if(elapsedTime<quickTimerData.get(i).getTimerTotalTimeInMillis()){ //TODO TIMER YET TO ELAPSE

                        long nextAlarmStamp = SystemClock.elapsedRealtime()+(quickTimerData.get(i).getTimerTotalTimeInMillis()-elapsedTime);

                        quickTimerData.get(i).setTimerAlarmEndTimeInMillis(nextAlarmStamp);

                        tickTrackTimerDatabase.setAlarm(nextAlarmStamp, quickTimerData.get(i).getTimerIntID(), true);

                        tickTrackDatabase.storeQuickTimerList(quickTimerData);

                        if(!isMyServiceRunning(TimerService.class, activity)){
                            tickTrackTimerDatabase.startNotificationService();
                        }

                    } else { //TODO TIMER ALREADY ELAPSED

                        quickTimerData.get(i).setTimerOn(false);
                        quickTimerData.get(i).setTimerPause(false);
                        quickTimerData.get(i).setTimerRinging(false);
                        quickTimerData.get(i).setTimerEndedTimeInMillis(-1);
                        quickTimerData.get(i).setTimerStartTimeInMillis(-1);
                        tickTrackDatabase.storeQuickTimerList(quickTimerData);
                        quickTimerData.remove(i);
                        tickTrackDatabase.storeQuickTimerList(quickTimerData);
                        missedTimers++;
                    }
                }
            } else if(quickTimerData.get(i).isTimerOn() && !quickTimerData.get(i).isTimerPause() && quickTimerData.get(i).isTimerRinging()){ //TODO TIMER IS RINGING
                quickTimerData.get(i).setTimerOn(false);
                quickTimerData.get(i).setTimerPause(false);
                quickTimerData.get(i).setTimerRinging(false);
                quickTimerData.get(i).setTimerEndedTimeInMillis(-1);
                quickTimerData.get(i).setTimerStartTimeInMillis(-1);
                tickTrackDatabase.storeQuickTimerList(quickTimerData);
                quickTimerData.remove(i);
                tickTrackDatabase.storeQuickTimerList(quickTimerData);
                missedTimers++;
            }
        }

        if(missedTimers>0){
            showMissedTimerNotification(activity, missedTimers, tickTrackDatabase);
        }

    }

    private NotificationCompat.Builder notificationBuilder;
    private NotificationManagerCompat notificationManagerCompat;
    private void showMissedTimerNotification(Context context, int missedTimers, TickTrackDatabase tickTrackDatabase) {

        Intent resultIntent;
        resultIntent = new Intent(context, SoYouADeveloperHuh.class);
        tickTrackDatabase.storeCurrentFragmentNumber(2);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationBuilder = new NotificationCompat.Builder(context, TickTrack.TIMER_MISSED_NOTIFICATION)
                .setSmallIcon(R.drawable.timer_notification_mini_icon)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(Notification.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setVibrate(new long[0])
                .setOnlyAlertOnce(true)
                .setColor(ContextCompat.getColor(context, R.color.Accent));

        notificationBuilder.setContentTitle("TickTrack Timer");
        notificationBuilder.setContentIntent(resultPendingIntent);
        if(missedTimers>1){
            notificationBuilder.setContentText("Missed "+missedTimers+" timers");
        } else {
            notificationBuilder.setContentText("Missed "+missedTimers+" timer");
        }

        notificationManagerCompat.notify(TickTrack.TIMER_MISSED_NOTIFICATION_ID, notificationBuilder.build());

    }

    private void startTimerRingNotificationService(Context context) {
        Intent intent = new Intent(context, TimerRingService.class);
        intent.setAction(TimerRingService.ACTION_ADD_TIMER_FINISH);
        context.startService(intent);
    }

    private void checkNotification(Context context, ArrayList<CounterData> counterData) {
        for(int i=0; i<counterData.size(); i++){
            if(counterData.get(i).isCounterPersistentNotification()){
                startCounterNotificationService(context);
            }
        }
    }

    public void startNotificationService(Context context) {
        Intent intent = new Intent(context, StopwatchNotificationService.class);
        intent.setAction(StopwatchNotificationService.ACTION_START_STOPWATCH_SERVICE);
        context.startService(intent);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void startCounterNotificationService(Context context) {
        Intent intent = new Intent(context, CounterNotificationService.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setAction(CounterNotificationService.ACTION_START_COUNTER_SERVICE);
        context.startService(intent);
    }

}
