package com.theflopguyproductions.ticktrack.timer.service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.os.UserHandle;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.application.TickTrack;
import com.theflopguyproductions.ticktrack.timer.TimerData;
import com.theflopguyproductions.ticktrack.timer.ringer.TimerRingerActivity;
import com.theflopguyproductions.ticktrack.utils.TickTrackDatabase;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class TimerBroadcastReceiver extends BroadcastReceiver {

    public static final String ACTION_TIMER_BROADCAST = "ACTION_TIMER_BROADCAST";

    private ArrayList<TimerServiceData> timerServiceDataArrayList = new ArrayList<>();
    private ArrayList<TimerData> timerDataArrayList = new ArrayList<>();

    private int timerIDInteger;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {

            String toastText = "TickTrack Reboot";
            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();

        } else if(Objects.equals(intent.getAction(), ACTION_TIMER_BROADCAST)){

            String toastText = "Timer Received";
            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
            TickTrackDatabase tickTrackDatabase = new TickTrackDatabase(context);
            timerIDInteger = intent.getIntExtra("timerIntegerID",0);
            timerServiceDataArrayList = retrieveTimerServiceDataList(context.getSharedPreferences("TickTrackData",MODE_PRIVATE));

            Intent timerRingIntent = new Intent(context, TimerRingerActivity.class);

            showNotificationNew(context, "TITLE", "MESSAGE", timerRingIntent, 1);

            removeServiceData(context.getSharedPreferences("TickTrackData",MODE_PRIVATE));
            alterTimerData(tickTrackDatabase);
            if(isMyServiceRunning(TimerService.class, context)){
                stopNotificationService(context);
            }
        }

    }

    private static void showNotificationNew(final Context context, final String title, final String message, final Intent intent, final int notificationId) {
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context.getApplicationContext())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(Notification.PRIORITY_HIGH)
                .setContentTitle(title)
                .setContentText(message)
                .setVibrate(new long[0])
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true)
                .setOngoing(true);

        if (android.os.Build.VERSION. SDK_INT >= android.os.Build.VERSION_CODES. O ) {
            notificationBuilder.setChannelId(TickTrack.COUNTER_NOTIFICATION);
        }

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(notificationId, notificationBuilder.build());
    }

    private void alterTimerData(TickTrackDatabase tickTrackDatabase) {
        timerDataArrayList = tickTrackDatabase.retrieveTimerList();
        int currentPosition = -1;
        for(int i = 0; i < timerDataArrayList.size(); i++){
            if(timerDataArrayList.get(i).getTimerIntegerID()==timerIDInteger){
                currentPosition = i;
            }
        }
        if(currentPosition>=0){
            System.out.println(currentPosition+"<<<<<<<<< POSITION");
            timerDataArrayList.get(currentPosition).setTimerEndTimeInMillis(System.currentTimeMillis());
            System.out.println(timerDataArrayList.get(currentPosition).getTimerEndTimeInMillis()+"<<<< END IME IN MILLIS");
            //TODO HANDLE STOP SERVICE HERE
//            timerDataArrayList.get(currentPosition).setTimerPause(false);
//            timerDataArrayList.get(currentPosition).setTimerOn(false);
//            timerDataArrayList.get(currentPosition).setTimerReset(true);
            tickTrackDatabase.storeTimerList(timerDataArrayList);
        }
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
    private void removeServiceData(SharedPreferences sharedPreferences) {
        for(int i = 0; i < timerServiceDataArrayList.size(); i++){
            if(timerServiceDataArrayList.get(i).getTimerIDInteger()==timerIDInteger){
                timerServiceDataArrayList.remove(i);
                storeTimerServiceData(sharedPreferences);
                return;
            }
        }
    }
    private void stopNotificationService(Context context) {
        Intent intent = new Intent(context, TimerService.class);
        intent.setAction(TimerService.ACTION_STOP_TIMER_SERVICE);
        context.startService(intent);
    }
    private ArrayList<TimerServiceData> retrieveTimerServiceDataList(SharedPreferences sharedPreferences){

        Gson gson = new Gson();
        String json = sharedPreferences.getString("TimerServiceData", null);
        Type type = new TypeToken<ArrayList<TimerServiceData>>() {}.getType();
        ArrayList<TimerServiceData> timerServiceData = gson.fromJson(json, type);

        if(timerServiceData == null){
            timerServiceData = new ArrayList<>();
        }

        return timerServiceData;
    }
    private void storeTimerServiceData(SharedPreferences sharedPreferences){

        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(timerServiceDataArrayList);
        editor.putString("TimerServiceData", json);
        editor.apply();

    }
}
