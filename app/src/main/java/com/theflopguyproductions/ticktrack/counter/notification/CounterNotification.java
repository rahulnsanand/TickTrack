package com.theflopguyproductions.ticktrack.counter.notification;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.application.TickTrack;
import com.theflopguyproductions.ticktrack.counter.CounterData;
import com.theflopguyproductions.ticktrack.utils.TickTrackDatabase;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.ArrayList;

public class CounterNotification extends Service {

    private static int currentCounterPosition;

    private static String counterLabel;
    private static int counterValue;
    private static int counterRequestID;
    private static ArrayList<CounterData> counterDataList = new ArrayList<>();
    NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();

    private static final String TAG_COUNTER_SERVICE = "COUNTER_SERVICE";

    public static final String ACTION_START_COUNTER_SERVICE = "ACTION_START_COUNTER_SERVICE";

    public static final String ACTION_STOP_COUNTER_SERVICE = "ACTION_STOP_COUNTER_SERVICE";

    public static final String ACTION_PLUS = "ACTION_PLUS";

    public static final String ACTION_MINUS = "ACTION_MINUS";

    public CounterNotification() {
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG_COUNTER_SERVICE, "My foreground service onCreate().");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    public static ArrayList<CounterData> retrieveCounterList(SharedPreferences sharedPreferences){

        Gson gson = new Gson();
        String json = sharedPreferences.getString("CounterData", null);
        Type type = new TypeToken<ArrayList<CounterData>>() {}.getType();
        ArrayList<CounterData> counterDataArrayList = gson.fromJson(json, type);

        if(counterDataArrayList == null){
            counterDataArrayList = new ArrayList<>();
        }

        return counterDataArrayList;
    }

    public static void storeCounterList(SharedPreferences sharedPreferences){

        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(counterDataList);
        editor.putString("CounterData", json);
        editor.apply();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null)
        {
            String action = intent.getAction();

            initializeValues(intent);

            assert action != null;
            switch (action) {
                case ACTION_START_COUNTER_SERVICE:
                    startForegroundService();
                    Toast.makeText(getApplicationContext(), "Foreground service is started.", Toast.LENGTH_LONG).show();
                    break;
                case ACTION_STOP_COUNTER_SERVICE:
                    Toast.makeText(getApplicationContext(), "You click Cancel button.", Toast.LENGTH_LONG).show();
                    stopForegroundService();
                    break;
                case ACTION_PLUS:
                    Toast.makeText(getApplicationContext(), "You click Play button.", Toast.LENGTH_LONG).show();
                    plusButtonPressed();
                    break;
                case ACTION_MINUS:
                    Toast.makeText(getApplicationContext(), "You click Pause button.", Toast.LENGTH_LONG).show();
                    minusButtonPressed();
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void initializeValues(Intent intent) {
        currentCounterPosition = intent.getIntExtra("counterPosition",0);
        SharedPreferences sharedPreferences = getSharedPreferences("TickTrackData", MODE_PRIVATE);
        counterDataList = retrieveCounterList(sharedPreferences);
        counterLabel = counterDataList.get(currentCounterPosition).getCounterLabel();
        counterValue = counterDataList.get(currentCounterPosition).getCounterValue();
        counterRequestID = (int) counterDataList.get(currentCounterPosition).getCounterTimestamp().getTime();
    }

    private void minusButtonPressed() {
        if(counterValue>=1){
            counterValue-=1;
            counterDataList.get(currentCounterPosition).setCounterValue(counterValue);
            counterDataList.get(currentCounterPosition).setCounterTimestamp(new Timestamp(System.currentTimeMillis()));
            storeCounterList(getSharedPreferences("TickTrackData",MODE_PRIVATE));
            bigTextStyle.bigText("Value: "+counterValue);
            startForegroundService();
        }
    }

    private void plusButtonPressed() {
        counterValue+=1;
        counterDataList.get(currentCounterPosition).setCounterValue(counterValue);
        counterDataList.get(currentCounterPosition).setCounterTimestamp(new Timestamp(System.currentTimeMillis()));
        storeCounterList(getSharedPreferences("TickTrackData",MODE_PRIVATE));
        bigTextStyle.bigText("Value: "+counterValue);
        startForegroundService();
    }

    private void startForegroundService() {

        notificationCounterTrue();

        Intent intent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(this, counterRequestID, intent, 0);

        // Create notification builder.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        if (android.os.Build.VERSION. SDK_INT >= android.os.Build.VERSION_CODES. O ) {
            builder.setChannelId(TickTrack.COUNTER_NOTIFICATION);
        }

        // Make notification show big text.

        bigTextStyle.setBigContentTitle(counterLabel);
        bigTextStyle.bigText("Value: "+counterValue);
        // Set big text style.
        builder.setStyle(bigTextStyle);

        builder.setWhen(System.currentTimeMillis());
        builder.setSmallIcon(R.mipmap.ic_launcher);
        Bitmap largeIconBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_flag_red);
        builder.setLargeIcon(largeIconBitmap);

        builder.setPriority(Notification.PRIORITY_MAX);

        builder.setFullScreenIntent(pendingIntent, true);

        Intent plusIntent = new Intent(this, CounterNotification.class);
        plusIntent.setAction(ACTION_PLUS);
        PendingIntent pendingPlusIntent = PendingIntent.getService(this, counterRequestID, plusIntent, 0);
        NotificationCompat.Action plusAction = new NotificationCompat.Action(android.R.drawable.ic_media_play, "Plus", pendingPlusIntent);
        builder.addAction(plusAction);

        Intent minusIntent = new Intent(this, CounterNotification.class);
        minusIntent.setAction(ACTION_MINUS);
        PendingIntent pendingMinusIntent = PendingIntent.getService(this, counterRequestID, minusIntent, 0);
        NotificationCompat.Action minusAction = new NotificationCompat.Action(android.R.drawable.ic_media_pause, "Minus", pendingMinusIntent);
        builder.addAction(minusAction);

        Intent cancelIntent = new Intent(this, CounterNotification.class);
        cancelIntent.setAction(ACTION_STOP_COUNTER_SERVICE);
        PendingIntent pendingCancelIntent = PendingIntent.getService(this, counterRequestID, cancelIntent, 0);
        NotificationCompat.Action cancelAction = new NotificationCompat.Action(R.drawable.ic_round_tick_white_24, "Done", pendingCancelIntent);
        builder.addAction(cancelAction);

        builder.setOnlyAlertOnce(true);

        Notification notification = builder.build();


        startForeground(counterRequestID+1, notification);
    }

    private void stopForegroundService() {
        notificationCounterFalse();
        stopForeground(true);
        stopSelf();
    }

    private void notificationCounterTrue() {
        counterDataList.get(currentCounterPosition).setCounterPersistentNotification(true);
        storeCounterList(getSharedPreferences("TickTrackData",MODE_PRIVATE));
    }
    private void notificationCounterFalse() {
        counterDataList.get(currentCounterPosition).setCounterPersistentNotification(false);
        storeCounterList(getSharedPreferences("TickTrackData",MODE_PRIVATE));
    }


}
