package com.theflopguyproductions.ticktrack.timer.service;

import android.app.ActivityManager;
import android.app.KeyguardManager;
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
import android.media.Ringtone;
import android.media.RingtoneManager;
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
import com.theflopguyproductions.ticktrack.utils.database.TickTrackTimerDatabase;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TimerRingService extends Service {

    public static final String ACTION_ADD_TIMER_FINISH = "ACTION_ADD_TIMER_FINISH";
    public static final String ACTION_KILL_ALL_TIMERS = "ACTION_KILL_ALL_TIMERS";
    public static final String ACTION_STOP_SERVICE_CHECK = "ACTION_STOP_SERVICE_CHECK";
//    public static final String ACTION_ADD_ONE_MINUTE = "ACTION_ADD_ONE_MINUTE";

    private NotificationCompat.Builder notificationBuilder;
    private NotificationManagerCompat notificationManagerCompat;

    private Uri alarmSound;

    private static ArrayList<TimerData> timerDataArrayList = new ArrayList<>();
    private static ArrayList<QuickTimerData> quickTimerData = new ArrayList<>();

    private Handler refreshHandler = new Handler();
    private TickTrackDatabase tickTrackDatabase;
    private TickTrackTimerDatabase tickTrackTimerDatabase;
    private boolean isSetup = false, isSingle = false, isMulti = false;

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
        tickTrackTimerDatabase = new TickTrackTimerDatabase(this);
        timerDataArrayList = tickTrackDatabase.retrieveTimerList();
        quickTimerData = tickTrackDatabase.retrieveQuickTimerList();
        alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://" + getPackageName() + "/raw/timer_beep.mp3");
        Log.d("TAG_TIMER_RANG_SERVICE", "My foreground service onCreate().");

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
    private void stopTimerNotification(Context context) {
        Intent intent = new Intent(context, TimerService.class);
        intent.setAction(TimerService.ACTION_STOP_TIMER_SERVICE);
        context.startService(intent);
    }

    static MediaPlayer mediaPlayer;
    public static void playAlarmSound (Context context) {

        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                try {

                    String selectedUri = new TickTrackDatabase(context).getRingtoneURI();

                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setLooping(true);
                    mediaPlayer.setOnPreparedListener(mp -> mediaPlayer.start());

                    if(selectedUri!=null){
                        final Ringtone ringtone = RingtoneManager.getRingtone(context, Uri.parse(selectedUri));
                        if(ringtone==null){
                            AssetFileDescriptor afd = context.getResources().openRawResourceFd(R.raw.timer_beep);
                            if (afd == null) return false;
                            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                            afd.close();
                        } else {
                            mediaPlayer.setDataSource(context, Uri.parse(selectedUri));
                        }
                    } else {
                        AssetFileDescriptor afd = context.getResources().openRawResourceFd(R.raw.timer_beep);
                        if (afd == null) return false;
                        mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                        afd.close();
                    }

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
                    new TickTrackDatabase(context).storeRingtoneURI(null);
                    new TickTrackDatabase(context).storeRingtoneName("Default Ringtone");
                    playAlarmSound(context);
                }
                return false;
            }

        }.execute();
    }

    @SuppressWarnings("SuspiciousListRemoveInLoop")
    private void stopTimerRinging() {
        quickTimerData = tickTrackDatabase.retrieveQuickTimerList();
        ArrayList<QuickTimerData> newQuickTimerList = quickTimerData;
        int size = quickTimerData.size();
        int count = 0;
        while(count<size){
            System.out.println("WHILE RAN");
            for(int i=0; i<quickTimerData.size(); i++){
                System.out.println(quickTimerData.size()+" TIMER DATA SIZE");
                System.out.println(i+" TIMER DATA NUMBER");
                if(quickTimerData.get(i).isTimerRinging()){
                    quickTimerData.get(i).setTimerOn(false);
                    quickTimerData.get(i).setTimerPause(false);
                    quickTimerData.get(i).setTimerRinging(false);
                    tickTrackDatabase.storeQuickTimerList(quickTimerData);
                    removeQuickTimer(quickTimerData.get(i).getTimerID(), newQuickTimerList);
                }
            }
            count++;
        }

        if(!(newQuickTimerList.size() > 0)){
            tickTrackDatabase.storeQuickTimerList(new ArrayList<>());
        } else {
            tickTrackDatabase.storeQuickTimerList(newQuickTimerList);
        }

        for(int i = 0; i < timerDataArrayList.size(); i++){
            if(timerDataArrayList.get(i).isTimerRinging()){
                timerDataArrayList.get(i).setTimerOn(false);
                timerDataArrayList.get(i).setTimerPause(false);
                timerDataArrayList.get(i).setTimerNotificationOn(false);
                timerDataArrayList.get(i).setTimerRinging(false);
                timerDataArrayList.get(i).setTimerTempMaxTimeInMillis(-1);
                tickTrackDatabase.storeTimerList(timerDataArrayList);
                System.out.println("SOMETHING HAPPENED HERE STOP TIMER SHIT");
            }
        }

    }

    private void removeQuickTimer(String timerID, ArrayList<QuickTimerData> newTimerList) {
        for(int i=0; i<newTimerList.size(); i++){
            if(newTimerList.get(i).getTimerID().equals(timerID)){
                newTimerList.remove(i);
                return;
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
        return result;
    }
    private boolean isQuickTimerOn() {
        timerDataArrayList = tickTrackDatabase.retrieveTimerList();
        quickTimerData = tickTrackDatabase.retrieveQuickTimerList();
        for(int i = 0; i < timerDataArrayList.size(); i ++){
            if(timerDataArrayList.get(i).isTimerRinging()){
                return false;
            }
        }
        for(int i=0; i<quickTimerData.size(); i++){
            if(quickTimerData.get(i).isTimerRinging()){
                return true;
            }
        }
        return false;
    }
    private void updateStopTimeText(long UpdateTime) {

        int hours = (int) TimeUnit.MILLISECONDS.toHours(UpdateTime);
        int minutes = (int) (TimeUnit.MILLISECONDS.toMinutes(UpdateTime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(UpdateTime)));
        int seconds = (int) (TimeUnit.MILLISECONDS.toSeconds(UpdateTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(UpdateTime)));

        String hourLeft = String.format(Locale.getDefault(),"- %02d:%02d:%02d", hours,minutes,seconds);
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
                .setSmallIcon(R.drawable.ic_stat_timericonnotification)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
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
        Intent killTimerIntent = new Intent(this, TimerRingService.class);
        killTimerIntent.setAction(ACTION_KILL_ALL_TIMERS);
        PendingIntent killTimerPendingIntent = PendingIntent.getService(this, TickTrack.TIMER_RINGING_NOTIFICATION_ID, killTimerIntent, 0);
        NotificationCompat.Action killTimers = new NotificationCompat.Action(R.drawable.ic_stop_white_24, "Stop all", killTimerPendingIntent);

        Intent resultIntent = new Intent(this, TimerRingerActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationBuilder.clearActions();
        notificationBuilder.setContentIntent(resultPendingIntent);
        notificationBuilder.addAction(killTimers);
        isMulti=true;
        isSingle=false;
    }
    private void setupSingleTimerNotification() {
        if(timerDataArrayList.size()>0){
            if(!timerDataArrayList.get(getCurrentTimerPosition(getSingleOnTimer())).isQuickTimer()){
                String timerLabel = timerDataArrayList.get(getCurrentTimerPosition(getSingleOnTimer())).getTimerLabel();
                if(!timerLabel.equals("Set label")){
                    notificationBuilder.setContentTitle("TickTrack Timer: "+timerDataArrayList.get(getCurrentTimerPosition(getSingleOnTimer())).getTimerLabel());
                }
            }
        } else {
            notificationBuilder.setContentTitle("TickTrack Timer");
        }
        notificationBuilder.setContentText("- 00:00:00");

        Intent killTimerIntent = new Intent(this, TimerRingService.class);
        killTimerIntent.setAction(ACTION_KILL_ALL_TIMERS);
        PendingIntent killTimerPendingIntent = PendingIntent.getService(this, TickTrack.TIMER_RINGING_NOTIFICATION_ID, killTimerIntent, 0);
        NotificationCompat.Action killTimers = new NotificationCompat.Action(R.drawable.ic_stop_white_24, "Stop", killTimerPendingIntent);

//        Intent addAMinuteIntent = new Intent(this, TimerRingService.class);
//        addAMinuteIntent.setAction(ACTION_ADD_ONE_MINUTE);
//        PendingIntent addAMinutePendingIntent = PendingIntent.getService(this, TickTrack.TIMER_RINGING_NOTIFICATION_ID, addAMinuteIntent, 0);
//        NotificationCompat.Action addAMinute = new NotificationCompat.Action(R.drawable.ic_baseline_plus_one_white_24, "+1 Minute", addAMinutePendingIntent);

        Intent resultIntent;
        if(isQuickTimerOn()){
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
//        if(!timerDataArrayList.get(getCurrentTimerPosition(getSingleOnTimer())).isQuickTimer()){
//            notificationBuilder.addAction(addAMinute);
//        }
        isSingle=true;
        isMulti=false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if(intent != null) {

            String action = intent.getAction();
            System.out.println(action+">>>>>>>>>>>>>>>>>>>>>>>>98798798765432132132<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
            initializeValues();
            if(isMyServiceRunning(TimerService.class, this)){
                stopTimerNotification(this);
            }

            assert action != null;
            switch (action) {
                case ACTION_ADD_TIMER_FINISH:
                    startSoundAndForeground();
                    stopTimerRunningService();
                    break;
                case ACTION_KILL_ALL_TIMERS:
                    stopTimers();
                    break;
                case ACTION_STOP_SERVICE_CHECK:
                    stopIfPossible();
                    break;
//                case ACTION_ADD_ONE_MINUTE:
//                    addOneMinute();
//                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

//    private void addOneMinute() {
//        refreshHandler.removeCallbacks(refreshRunnable);
//        timerDataArrayList = tickTrackDatabase.retrieveTimerList();
//        if(!timerDataArrayList.get(getCurrentTimerPosition(getSingleOnTimer())).isQuickTimer()){
//            addOneTimer();
//        }
//        if(!(getAllOnTimers() > 1) && getAllOnTimers()>0){
//            try {
//                if(mediaPlayer.isPlaying()){
//                    mediaPlayer.stop();
//                    mediaPlayer.release();
//                }
//            } catch (Exception ignored) {
//            }
//            stopSelf();
//            onDestroy();
//        } else {
//            refreshHandler.post(refreshRunnable);
//        }
//    }
//
//    private void addOneTimer(){
//        if(timerDataArrayList.get(getCurrentTimerPosition(getSingleOnTimer())).isTimerRinging()){
//            long timerTotalTime = timerDataArrayList.get(getCurrentTimerPosition(getSingleOnTimer())).getTimerTotalTimeInMillis();
//            long timerTempTotalTime = timerTotalTime+(1000*2);
//            long newTimerAlarmEndTime = SystemClock.elapsedRealtime() + (1000*2);
//            timerDataArrayList.get(getCurrentTimerPosition(getSingleOnTimer())).setTimerRinging(false);
//            timerDataArrayList.get(getCurrentTimerPosition(getSingleOnTimer())).setTimerOn(true);
//            timerDataArrayList.get(getCurrentTimerPosition(getSingleOnTimer())).setTimerPause(false);
//            timerDataArrayList.get(getCurrentTimerPosition(getSingleOnTimer())).setTimerTempMaxTimeInMillis(timerTempTotalTime);
//            timerDataArrayList.get(getCurrentTimerPosition(getSingleOnTimer())).setTimerAlarmEndTimeInMillis(newTimerAlarmEndTime);
//            if(timerDataArrayList.get(getCurrentTimerPosition(getSingleOnTimer())).getTimerStartTimeInMillis()==-1){
//                timerDataArrayList.get(getCurrentTimerPosition(getSingleOnTimer())).setTimerStartTimeInMillis(System.currentTimeMillis());
//            }
//            tickTrackTimerDatabase.cancelAlarm(timerDataArrayList.get(getCurrentTimerPosition(getSingleOnTimer())).getTimerIntID(), false);
//            tickTrackTimerDatabase.setAlarm(newTimerAlarmEndTime,
//                    timerDataArrayList.get(getCurrentTimerPosition(getSingleOnTimer())).getTimerIntID(), false);
//            tickTrackDatabase.storeTimerList(timerDataArrayList);
//            System.out.println("ADD ONE TIMER ADDED"+newTimerAlarmEndTime);
//        }
//    }

    private void initializeValues() {
        stopForeground(false);
        if(!isSetup){
            setupBaseNotification();
        }
        if(getAllOnTimers() > 0){
            refreshHandler.postDelayed(refreshRunnable, 100);
        }
    }
    private void stopTimerRunningService() {
        Intent intent = new Intent(this, TimerService.class);
        intent.setAction(TimerService.ACTION_STOP_TIMER_SERVICE);
        startService(intent);
    }

    final Runnable refreshRunnable = new Runnable() {
        public void run() {
            if(getAllOnTimers()>0){
                if(getAllOnTimers() == 1){
                    if(!isSingle){
                        setupSingleTimerNotification();
                    }
                    if(!isQuickTimerOn()){
                        updateStopTimeText(SystemClock.elapsedRealtime() - timerDataArrayList.get(getCurrentTimerPosition(getSingleOnTimer())).getTimerEndedTimeInMillis());
                    } else {
                        updateStopTimeText(SystemClock.elapsedRealtime() - quickTimerData.get(getCurrentTimerPosition(getSingleOnTimer())).getTimerEndedTimeInMillis());
                    }

                } else {
                    if(!isMulti){
                        setupMultiTimerNotification();
                    }
                    notificationBuilder.setContentText(getAllOnTimers()+" timers complete");
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
            if(mediaPlayer!=null){
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
            stopSelf();
            stopForeground(false);
            onDestroy();
        }
    }

    private void startSoundAndForeground() {
        playAlarmSound(this);
        KeyguardManager myKM = (KeyguardManager) this.getSystemService(Context.KEYGUARD_SERVICE);
        if( myKM.isKeyguardLocked()) {
            Intent resultIntent = new Intent(this, TimerRingerActivity.class);
            PendingIntent timerRingPending = PendingIntent.getActivity(this, 2122, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            System.out.println("QUICK TIMER BROADCAST RINGER ACTIVITY START");
            notificationBuilder.setFullScreenIntent(timerRingPending, true);
        }
        startForeground(TickTrack.TIMER_RINGING_NOTIFICATION_ID, notificationBuilder.build());
        Toast.makeText(this, "Timer Received!", Toast.LENGTH_SHORT).show();
    }

    private void notifyNotification() {
        if(getAllOnTimers()>0){
            notificationManagerCompat.notify(TickTrack.TIMER_RINGING_NOTIFICATION_ID, notificationBuilder.build());
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
        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        refreshHandler.removeCallbacks(refreshRunnable);
        stopTimerRinging();
        stopSelf();
        stopForeground(false);
        onDestroy();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(false);
    }

}
