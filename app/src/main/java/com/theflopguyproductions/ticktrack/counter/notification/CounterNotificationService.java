package com.theflopguyproductions.ticktrack.counter.notification;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.application.TickTrack;
import com.theflopguyproductions.ticktrack.counter.CounterData;
import com.theflopguyproductions.ticktrack.utils.TickTrackDatabase;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.ArrayList;

public class CounterNotificationService extends Service {

    private int currentCounterPosition;

    private static String counterLabel;
    private static int counterValue;
    private static int counterRequestID;
    private static ArrayList<CounterData> counterDataList = new ArrayList<>();
    NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();

    RemoteViews collapsedView, expandedView;
    Notification notification;
    NotificationCompat.Builder notificationBuilder;
    NotificationManager notificationManager;

    private static final String TAG_COUNTER_SERVICE = "COUNTER_SERVICE";

    public static final String ACTION_START_COUNTER_SERVICE = "ACTION_START_COUNTER_SERVICE";

    public static final String ACTION_STOP_COUNTER_SERVICE = "ACTION_STOP_COUNTER_SERVICE";

    public static final String ACTION_PLUS = "ACTION_PLUS";

    public static final String ACTION_MINUS = "ACTION_MINUS";
    private NotificationManagerCompat notificationManagerCompat;

    public CounterNotificationService() {
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

            initializeValues();

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

    private void initializeValues() {
        currentCounterPosition = getCurrentCounterNotificationID();
        SharedPreferences sharedPreferences = getSharedPreferences("TickTrackData", MODE_PRIVATE);
        counterDataList = retrieveCounterList(sharedPreferences);
        counterLabel = counterDataList.get(currentCounterPosition).getCounterLabel();
        counterValue = counterDataList.get(currentCounterPosition).getCounterValue();
        counterRequestID = (int) counterDataList.get(currentCounterPosition).getCounterTimestamp().getTime();
    }

    public int getFlag(){
        if(counterDataList.get(currentCounterPosition).getCounterFlag()==1){
            return R.drawable.ic_flag_red;
        } else if(counterDataList.get(currentCounterPosition).getCounterFlag()==2){
            return R.drawable.ic_flag_green;
        } else if(counterDataList.get(currentCounterPosition).getCounterFlag()==3){
            return R.drawable.ic_flag_orange;
        } else if(counterDataList.get(currentCounterPosition).getCounterFlag()==4){
            return R.drawable.ic_flag_purple;
        } else if(counterDataList.get(currentCounterPosition).getCounterFlag()==5){
            return R.drawable.ic_flag_blue;
        } else {
            return R.drawable.ic_round_flag_light_24;
        }
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
        expandedView.setTextViewText(R.id.counterNotificationExpandedValueTextView, counterValue+"");
        collapsedView.setTextViewText(R.id.counterNotificationCollapsedValueTextView, counterValue+"");
        notifyNotification();
    }

    private void startForegroundService() {

        notificationCounterTrue();
        setupCustomNotification();

//        Intent intent = new Intent();
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, counterRequestID, intent, 0);
//
//        // Create notification builder.
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
//        if (android.os.Build.VERSION. SDK_INT >= android.os.Build.VERSION_CODES. O ) {
//            builder.setChannelId(TickTrack.COUNTER_NOTIFICATION);
//        }
//
//        // Make notification show big text.
//
//        bigTextStyle.setBigContentTitle(counterLabel);
//        bigTextStyle.bigText("Value: "+counterValue);
//        // Set big text style.
//        builder.setStyle(bigTextStyle);
//
//        builder.setWhen(System.currentTimeMillis());
//        builder.setSmallIcon(R.mipmap.ic_launcher);
//        Bitmap largeIconBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_flag_red);
//        builder.setLargeIcon(largeIconBitmap);
//
//        builder.setPriority(Notification.PRIORITY_MAX);
//
//
//        Intent plusIntent = new Intent(this, CounterNotificationService.class);
//        plusIntent.setAction(ACTION_PLUS);
//        PendingIntent pendingPlusIntent = PendingIntent.getService(this, counterRequestID, plusIntent, 0);
//        NotificationCompat.Action plusAction = new NotificationCompat.Action(android.R.drawable.ic_media_play, "Plus", pendingPlusIntent);
//        builder.addAction(plusAction);
//
//        Intent minusIntent = new Intent(this, CounterNotificationService.class);
//        minusIntent.setAction(ACTION_MINUS);
//        PendingIntent pendingMinusIntent = PendingIntent.getService(this, counterRequestID, minusIntent, 0);
//        NotificationCompat.Action minusAction = new NotificationCompat.Action(android.R.drawable.ic_media_pause, "Minus", pendingMinusIntent);
//        builder.addAction(minusAction);
//
//        Intent cancelIntent = new Intent(this, CounterNotificationService.class);
//        cancelIntent.setAction(ACTION_STOP_COUNTER_SERVICE);
//        PendingIntent pendingCancelIntent = PendingIntent.getService(this, counterRequestID, cancelIntent, 0);
//        NotificationCompat.Action cancelAction = new NotificationCompat.Action(R.drawable.ic_round_tick_white_24, "Done", pendingCancelIntent);
//        builder.addAction(cancelAction);
//
//


        startForeground(1, notificationBuilder.build());
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
        setCurrentCounterNotificationID(0);
    }

    private void setupCustomNotification(){

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());

        collapsedView = new RemoteViews(getPackageName(),
                R.layout.notification_counter_collapsed_layout);
        expandedView = new RemoteViews(getPackageName(),
                R.layout.notification_counter_expanded_layout);

        collapsedView.setImageViewResource(R.id.counterNotificationCollapsedFlagImageView, getFlag());
        collapsedView.setTextViewText(R.id.counterNotificationCollapsedLabelTextView, counterLabel);
        collapsedView.setTextViewText(R.id.counterNotificationCollapsedValueTextView, counterValue+"");

        expandedView.setImageViewResource(R.id.counterNotificationExpandedFlagImageView, getFlag());
        expandedView.setTextViewText(R.id.counterNotificationExpandedLabelTextView, counterLabel);
        expandedView.setTextViewText(R.id.counterNotificationExpandedValueTextView, counterValue+"");

        Intent plusIntent = new Intent(this, CounterNotificationService.class);
        plusIntent.setAction(ACTION_PLUS);
        plusIntent.putExtra("counterPosition", currentCounterPosition);

        PendingIntent pendingPlusIntent = PendingIntent.getService(this, counterRequestID, plusIntent, 0);

        expandedView.setOnClickPendingIntent(R.id.counterNotificationExpandedPlusImageButton, pendingPlusIntent);

        notificationBuilder = new NotificationCompat.Builder(this,TickTrack.COUNTER_NOTIFICATION)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setCustomContentView(collapsedView)
                .setCustomBigContentView(expandedView)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setOnlyAlertOnce(true);;

    }

    public void notifyNotification(){
        notificationManagerCompat.notify(1, notificationBuilder.build());
    }

    public int getCurrentCounterNotificationID(){
        SharedPreferences sharedPreferences = getSharedPreferences("TickTrackData", MODE_PRIVATE);
        return sharedPreferences.getInt("CounterNotificationPosition", 0);
    }

    public void setCurrentCounterNotificationID(int currentPosition){
        SharedPreferences sharedPreferences = getSharedPreferences("TickTrackData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("CounterNotificationPosition", currentPosition);
        editor.apply();
    }

}
