package com.theflopguyproductions.ticktrack.counter.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.application.TickTrack;
import com.theflopguyproductions.ticktrack.counter.CounterData;
import com.theflopguyproductions.ticktrack.counter.activity.CounterActivity;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.widgets.counter.CounterWidget;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class CounterNotificationService extends Service {

    private int currentCounterPosition;

    private static String counterLabel;
    private static int counterValue;
    private static int counterRequestID;
    private static ArrayList<CounterData> counterDataList = new ArrayList<>();

    NotificationCompat.Builder notificationBuilder;
    NotificationManager notificationManager;

    private static final String TAG_COUNTER_SERVICE = "COUNTER_SERVICE";

    public static final String ACTION_START_COUNTER_SERVICE = "ACTION_START_COUNTER_SERVICE";
    public static final String ACTION_REFRESH_SERVICE = "ACTION_REFRESH_SERVICE";
    public static final String ACTION_STOP_COUNTER_SERVICE = "ACTION_STOP_COUNTER_SERVICE";
    public static final String ACTION_KILL_NOTIFICATIONS = "ACTION_KILL_NOTIFICATIONS";
    public static final String ACTION_PLUS = "ACTION_PLUS";

    public static final String ACTION_MINUS = "ACTION_MINUS";
    private NotificationManagerCompat notificationManagerCompat;

    private TickTrackDatabase tickTrackDatabase;

    public CounterNotificationService() {
    }


    @Override
    public void onCreate() {
        super.onCreate();
        tickTrackDatabase = new TickTrackDatabase(this);
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
        if(intent != null) {

            String action = intent.getAction();

            initializeValues();

            assert action != null;
            switch (action) {
                case ACTION_START_COUNTER_SERVICE:
                    if(currentCounterPosition!=-1) {
                        startForegroundService();
                    }
                    break;
                case ACTION_STOP_COUNTER_SERVICE:
                    stopForegroundService();
                    break;
                case ACTION_PLUS:
                    if(currentCounterPosition!=-1){
                        plusButtonPressed();
                        updateCounterWidgets();
                    }
                    break;
                case ACTION_MINUS:
                    if(currentCounterPosition!=-1){
                        minusButtonPressed();
                        updateCounterWidgets();
                    }
                    break;
                case ACTION_REFRESH_SERVICE:
                    if(currentCounterPosition!=-1){
                        refreshCountValue(tickTrackDatabase.getSharedPref(this));
                    }
                    break;
                case ACTION_KILL_NOTIFICATIONS:
                    killNotifications();
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void updateCounterWidgets() {
        Intent intent = new Intent(this, CounterWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), CounterWidget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        sendBroadcast(intent);
    }

    private void initializeValues() {
        SharedPreferences sharedPreferences = tickTrackDatabase.getSharedPref(this);
        counterDataList = retrieveCounterList(sharedPreferences);
        currentCounterPosition = getCurrentCounterNotificationID();
        if(currentCounterPosition!=-1){
            counterLabel = counterDataList.get(currentCounterPosition).getCounterLabel();
            counterValue = counterDataList.get(currentCounterPosition).getCounterValue();
            counterRequestID = (int) counterDataList.get(currentCounterPosition).getCounterTimestamp();
        }
        stopForeground(false);
    }

    private void minusButtonPressed() {
        if(counterValue>=1){
            counterValue-=1;
            counterDataList.get(currentCounterPosition).setCounterValue(counterValue);
            counterDataList.get(currentCounterPosition).setCounterTimestamp(System.currentTimeMillis());
            storeCounterList(tickTrackDatabase.getSharedPref(this));

            notificationBuilder.setContentText("Value: "+counterValue+"");

//            expandedView.setTextViewText(R.id.counterNotificationExpandedValueTextView, counterValue+"");
//            collapsedView.setTextViewText(R.id.counterNotificationCollapsedValueTextView, counterValue+"");

            notifyNotification();
        }
    }

    private void plusButtonPressed() {
        counterValue+=1;
        counterDataList.get(currentCounterPosition).setCounterValue(counterValue);
        counterDataList.get(currentCounterPosition).setCounterTimestamp(System.currentTimeMillis());
        storeCounterList(tickTrackDatabase.getSharedPref(this));
        notificationBuilder.setContentText("Value: "+counterValue+"");
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


        startForeground(TickTrack.COUNTER_NOTIFICATION_ID, notificationBuilder.build());
        Toast.makeText(this, "Notification created!", Toast.LENGTH_SHORT).show();
    }

    private void stopForegroundService() {
        stopSelf();
        notificationCounterFalse();
    }

    private void killNotifications(){
        stopForeground(false);
    }

    private void notificationCounterTrue() {
        counterDataList.get(currentCounterPosition).setCounterPersistentNotification(true);
        storeCounterList(tickTrackDatabase.getSharedPref(this));
    }
    private void notificationCounterFalse() {
        counterDataList.get(currentCounterPosition).setCounterPersistentNotification(false);
        storeCounterList(tickTrackDatabase.getSharedPref(this));
        setCurrentCounterNotificationID(null);
    }

    private void setupCustomNotification(){

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());

        Intent plusIntent = new Intent(this, CounterNotificationService.class);
        plusIntent.setAction(ACTION_PLUS);
        PendingIntent pendingPlusIntent = PendingIntent.getService(this, counterRequestID, plusIntent, 0);
        NotificationCompat.Action plusAction = new NotificationCompat.Action(R.drawable.ic_add_white_24, "Plus", pendingPlusIntent);

        Intent minusIntent = new Intent(this, CounterNotificationService.class);
        minusIntent.setAction(ACTION_MINUS);
        PendingIntent pendingMinusIntent = PendingIntent.getService(this, counterRequestID, minusIntent, 0);
        NotificationCompat.Action minusAction = new NotificationCompat.Action(R.drawable.ic_round_minus_white_24, "Minus", pendingMinusIntent);


        Intent cancelIntent = new Intent(this, CounterNotificationService.class);
        cancelIntent.setAction(ACTION_STOP_COUNTER_SERVICE);
        PendingIntent pendingCancelIntent = PendingIntent.getService(this, counterRequestID, cancelIntent, 0);
        NotificationCompat.Action cancelAction = new NotificationCompat.Action(R.drawable.ic_round_close_white_24, "Done", pendingCancelIntent);


        Intent resultIntent = new Intent(this, CounterActivity.class);
        resultIntent.putExtra("currentCounterPosition", currentCounterPosition);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationBuilder = new NotificationCompat.Builder(this,TickTrack.COUNTER_NOTIFICATION)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setOnlyAlertOnce(true)
                .setContentIntent(resultPendingIntent)
                .setColor(ContextCompat.getColor(this, R.color.Accent));

        notificationBuilder.addAction(cancelAction);
        notificationBuilder.addAction(minusAction);
        notificationBuilder.addAction(plusAction);

        notificationBuilder.setContentTitle(counterLabel);
        notificationBuilder.setContentText("Value: "+counterValue+"");

        if (android.os.Build.VERSION. SDK_INT >= android.os.Build.VERSION_CODES. O ) {
            notificationBuilder.setChannelId(TickTrack.COUNTER_NOTIFICATION);
        }

    }

    public void notifyNotification(){
        notificationManagerCompat.notify(TickTrack.COUNTER_NOTIFICATION_ID, notificationBuilder.build());
    }

    public int getCurrentCounterNotificationID(){
        SharedPreferences sharedPreferences = tickTrackDatabase.getSharedPref(this);
        return getCurrentPosition(sharedPreferences.getString("CounterNotificationID", null));
    }

    private int getCurrentPosition(String counterNotificationID) {
        for(int i = 0; i <counterDataList.size(); i++){
            if(counterNotificationID.equals(counterDataList.get(i).getCounterID())){
                return i;
            }
        }
        return -1;
    }

    public void setCurrentCounterNotificationID(String currentPosition) {
        SharedPreferences sharedPreferences = tickTrackDatabase.getSharedPref(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("CounterNotificationID", currentPosition);
        editor.apply();
    }

    public void refreshCountValue(SharedPreferences sharedPreferences){
        if(notificationBuilder!=null){
            retrieveCounterList(sharedPreferences);
            notificationBuilder.setContentText("Value: "+counterValue+"");
            notifyNotification();
        }
    }
}
