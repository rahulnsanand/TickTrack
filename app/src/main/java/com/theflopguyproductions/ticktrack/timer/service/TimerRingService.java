package com.theflopguyproductions.ticktrack.timer.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

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
import com.theflopguyproductions.ticktrack.timer.ringer.TimerRingerActivity;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TimerRingService extends Service {

    public static final String ACTION_ADD_TIMER_FINISH = "ACTION_ADD_TIMER_FINISH";
    public static final String ACTION_KILL_ALL_TIMERS = "ACTION_KILL_ALL_TIMERS";
    public static final String ACTION_STOP_SERVICE_CHECK = "ACTION_STOP_SERVICE_CHECK";

    private NotificationCompat.Builder notificationBuilder;
    private NotificationManagerCompat notificationManagerCompat;

    private Uri alarmSound;

    private static ArrayList<TimerData> timerDataArrayList = new ArrayList<>();
    private static ArrayList<QuickTimerData> quickTimerData = new ArrayList<>();

    private Handler refreshHandler = new Handler();
    private TickTrackDatabase tickTrackDatabase;
    private boolean isSetup = false;

    public TimerRingService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        tickTrackDatabase = new TickTrackDatabase(this);
        timerDataArrayList = tickTrackDatabase.retrieveTimerList();
        quickTimerData = tickTrackDatabase.retrieveQuickTimerList();
        alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://" + getPackageName() + "/raw/timer_beep.mp3");
        Log.d("TAG_TIMER_RANG_SERVICE", "My foreground service onCreate().");
    }

    static MediaPlayer mediaPlayer;
    public static void playAlarmSound (Context context) {

        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setLooping(true);
                    mediaPlayer.setOnPreparedListener(mp -> mediaPlayer.start());
                    AssetFileDescriptor afd = context.getResources().openRawResourceFd(R.raw.timer_beep);
                    if (afd == null) return false;
                    mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                    afd.close();

                    if (Build.VERSION.SDK_INT >= 21) {
                        mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_ALARM)
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .build());
                    } else {
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                    }
                    mediaPlayer.setVolume(1.0f, 1.0f);
                    mediaPlayer.prepare();
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }

        }.execute();
    }

    @SuppressWarnings("SuspiciousListRemoveInLoop")
    private void stopTimerRinging() {
        System.out.println("STOP TIMER SHIT HAPPENED");
        for(int i = 0; i < quickTimerData.size(); i++){
            if(quickTimerData.get(i).isTimerRinging()){
                if(quickTimerData.get(i).isQuickTimer()){
                    quickTimerData.get(i).setTimerOn(false);
                    quickTimerData.get(i).setTimerPause(false);
                    quickTimerData.get(i).setTimerNotificationOn(false);
                    quickTimerData.get(i).setTimerRinging(false);
                    tickTrackDatabase.storeQuickTimerList(quickTimerData);
                    quickTimerData.remove(i);
                }
                tickTrackDatabase.storeQuickTimerList(quickTimerData);
            }
        }

        for(int i = 0; i < timerDataArrayList.size(); i++){
            if(timerDataArrayList.get(i).isTimerRinging()){
                timerDataArrayList.get(i).setTimerOn(false);
                timerDataArrayList.get(i).setTimerPause(false);
                timerDataArrayList.get(i).setTimerNotificationOn(false);
                timerDataArrayList.get(i).setTimerRinging(false);
                tickTrackDatabase.storeTimerList(timerDataArrayList);
                System.out.println("SOMETHING HAPPENED HERE STOP TIMER SHIT");
            }
        }
    }
    private int getAllOnTimers() {
        timerDataArrayList = tickTrackDatabase.retrieveTimerList();
        quickTimerData = tickTrackDatabase.retrieveQuickTimerList();
        int result = 0;
        for(int i = 0; i < timerDataArrayList.size(); i ++){
            if(timerDataArrayList.get(i).isTimerRinging()){
                result++;
            }
        }
        for(int i=0; i<quickTimerData.size(); i++){
            if(quickTimerData.get(i).isTimerRinging()){
                result++;
            }
        }

        System.out.println("ALL ON TIMERS "+result);
        return result;
    }
    private void updateStopTimeText(long UpdateTime) {

        int hours = (int) TimeUnit.MILLISECONDS.toHours(UpdateTime);
        int minutes = (int) (TimeUnit.MILLISECONDS.toMinutes(UpdateTime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(UpdateTime)));
        int seconds = (int) (TimeUnit.MILLISECONDS.toSeconds(UpdateTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(UpdateTime)));

        String hourLeft = String.format(Locale.getDefault(),"-%02d:%02d:%02d", hours,minutes,seconds);
        notificationBuilder.setContentText(hourLeft);

    }
    private int getCurrentTimerPosition(int timerIntegerID){
        timerDataArrayList = tickTrackDatabase.retrieveTimerList();
        quickTimerData = tickTrackDatabase.retrieveQuickTimerList();
        for(int i = 0; i < timerDataArrayList.size(); i ++){
            if(timerDataArrayList.get(i).getTimerIntID()==timerIntegerID){
                return i;
            }
        }
        for(int i = 0; i < quickTimerData.size(); i ++){
            if(quickTimerData.get(i).getTimerIntID()==timerIntegerID){
                return i;
            }
        }
        return -1;
    }
    private int getSingleOnTimer() {
        timerDataArrayList = tickTrackDatabase.retrieveTimerList();
        quickTimerData = tickTrackDatabase.retrieveQuickTimerList();
        for(int i=0; i<timerDataArrayList.size(); i++){
            if(timerDataArrayList.get(i).isTimerRinging()){
                return timerDataArrayList.get(i).getTimerIntID();
            }
        }
        for(int i=0; i<quickTimerData.size(); i++){
            if(quickTimerData.get(i).isTimerRinging()){
                return quickTimerData.get(i).getTimerIntID();
            }
        }
        return -1;
    }

    private void setupBaseNotification() {
        notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());

        notificationBuilder = new NotificationCompat.Builder(this, TickTrack.TIMER_COMPLETE_NOTIFICATION)
                .setSmallIcon(R.drawable.timer_notification_mini_icon)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(Notification.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setVibrate(new long[0])
                .setOnlyAlertOnce(true)
                .setOngoing(true)
                .setColor(ContextCompat.getColor(this, R.color.Accent));

        notificationBuilder.setContentTitle("TickTrack Timer");
        notificationBuilder.setContentText("Timer Elapsed");
        isSetup=true;
    }
    private void setupMultiTimerNotification() {
        notificationBuilder.setContentTitle("TickTrack Timers");
        notificationBuilder.setContentText(getAllOnTimers()+" timers complete");
        Intent killTimerIntent = new Intent(this, TimerRingService.class);
        killTimerIntent.setAction(ACTION_KILL_ALL_TIMERS);
        PendingIntent killTimerPendingIntent = PendingIntent.getService(this, 3, killTimerIntent, 0);
        NotificationCompat.Action killTimers = new NotificationCompat.Action(R.drawable.ic_stop_white_24, "Stop all", killTimerPendingIntent);

        Intent resultIntent = new Intent(this, TimerRingerActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationBuilder.clearActions();
        notificationBuilder.setContentIntent(resultPendingIntent);
        notificationBuilder.addAction(killTimers);
    }
    private void setupSingleTimerNotification() {
        if(!timerDataArrayList.get(getCurrentTimerPosition(getSingleOnTimer())).isQuickTimer()){
            String timerLabel = timerDataArrayList.get(getCurrentTimerPosition(getSingleOnTimer())).getTimerLabel();
            if(!timerLabel.equals("Set label")){
                notificationBuilder.setContentTitle("TickTrack Timer: "+timerDataArrayList.get(getCurrentTimerPosition(getSingleOnTimer())).getTimerLabel());
            }
        } else {
            notificationBuilder.setContentTitle("TickTrack Timer");
        }

        Intent killTimerIntent = new Intent(this, TimerRingService.class);
        killTimerIntent.setAction(ACTION_KILL_ALL_TIMERS);
        PendingIntent killTimerPendingIntent = PendingIntent.getService(this, 3, killTimerIntent, 0);
        NotificationCompat.Action killTimers = new NotificationCompat.Action(R.drawable.ic_stop_white_24, "Stop", killTimerPendingIntent);

        Intent resultIntent;
        if(timerDataArrayList.get(getCurrentTimerPosition(getSingleOnTimer())).isQuickTimer()){
            resultIntent = new Intent(this, SoYouADeveloperHuh.class);
            tickTrackDatabase.storeCurrentFragmentNumber(2);
        } else {
            resultIntent = new Intent(this, TimerActivity.class);
            resultIntent.putExtra("timerID",timerDataArrayList.get(getCurrentTimerPosition(getSingleOnTimer())).getTimerID());
        }
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationBuilder.clearActions();
        notificationBuilder.setContentIntent(resultPendingIntent);
        notificationBuilder.addAction(killTimers);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null) {

            String action = intent.getAction();

            initializeValues();

            assert action != null;
            switch (action) {
                case ACTION_ADD_TIMER_FINISH:
                    startSoundAndForeground();
                    break;
                case ACTION_KILL_ALL_TIMERS:
                    stopTimers();
                    break;
                case ACTION_STOP_SERVICE_CHECK:
                    stopIfPossible();
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void initializeValues() {
        if(!isSetup){
            setupBaseNotification();
        }
        if(getAllOnTimers() > 0){
            refreshHandler.postDelayed(refreshRunnable, 100);
        }

        stopForeground(false);
    }

    final Runnable refreshRunnable = new Runnable() {
        public void run() {
            if(getAllOnTimers()>0){
                if(getAllOnTimers() == 1){
                    setupSingleTimerNotification();
                    updateStopTimeText(SystemClock.elapsedRealtime() - timerDataArrayList.get(getCurrentTimerPosition(getSingleOnTimer())).getTimerEndedTimeInMillis());
                } else {
                    setupMultiTimerNotification();
                }
                notifyNotification();
                System.out.println(getAllOnTimers()+" TIMER RINGING");
                refreshHandler.postDelayed(refreshRunnable, 100);
            } else {
                refreshHandler.removeCallbacks(refreshRunnable);
                stopIfPossible();
            }
        }
    };

    private void stopIfPossible() {
        if(!(getAllOnTimers()>0)){
            refreshHandler.removeCallbacks(refreshRunnable);
            stopSelf();
            onDestroy();
        }
    }

    private void startSoundAndForeground() {
        playAlarmSound(this);
        startForeground(3, notificationBuilder.build());
        Toast.makeText(this, "Timer Received!", Toast.LENGTH_SHORT).show();
    }

    private void notifyNotification() {
        if(getAllOnTimers()>0){
            notificationManagerCompat.notify(3, notificationBuilder.build());
        } else {
            stopRingerService();
        }
    }

    private void stopRingerService() {
        timerDataArrayList = tickTrackDatabase.retrieveTimerList();
        quickTimerData = tickTrackDatabase.retrieveQuickTimerList();
        if(!(getAllOnTimers() > 0)){
            stopTimers();
        }
    }
    private void stopTimers() {
        try {
            if(mediaPlayer.isPlaying()){
                mediaPlayer.stop();
                mediaPlayer.release();
            }
        } catch (Exception ignored) {
        }
        stopTimerRinging();
        refreshHandler.removeCallbacks(refreshRunnable);
        stopSelf();
        onDestroy();
    }

}
