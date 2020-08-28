package com.theflopguyproductions.ticktrack.service;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.application.TickTrack;
import com.theflopguyproductions.ticktrack.startup.StartUpActivity;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackFirebaseDatabase;
import com.theflopguyproductions.ticktrack.utils.helpers.FirebaseHelper;

public class RestoreService extends Service {

    private Activity activity;
    private FirebaseHelper firebaseHelper;
    private TickTrackFirebaseDatabase tickTrackFirebaseDatabase;
    private TickTrackDatabase tickTrackDatabase;


    public static final String DATA_RESTORATION_START = "DATA_RESTORATION_START";
    public static final String DATA_JSON_RESTORE_START = "DATA_JSON_RESTORE_START";
    public static final String DATA_RESTORATION_STOP = "DATA_RESTORATION_STOP";

    private NotificationCompat.Builder notificationBuilder;
    private NotificationManagerCompat notificationManagerCompat;

    private String receivedAction;

    public RestoreService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        firebaseHelper = new FirebaseHelper(this);
        tickTrackFirebaseDatabase = new TickTrackFirebaseDatabase(this);
        tickTrackDatabase = new TickTrackDatabase(this);

        setupCustomNotification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null) {

            String action = intent.getAction();

            receivedAction = intent.getStringExtra("receivedAction");
            firebaseHelper.setAction(receivedAction);
            System.out.println("RESTORE SERVICE RECEIVED "+receivedAction);

            assert action != null;
            switch (action) {
                case DATA_RESTORATION_START:
                    setupForeground();
                    startRestoration();
                    break;
                case DATA_JSON_RESTORE_START:
                    jsonRestorationStart();
                    break;
                case DATA_RESTORATION_STOP:
                    stopForegroundService();
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void jsonRestorationStart() {



    }

    private void setupForeground() {
        startForeground(6, notificationBuilder.build());
        Toast.makeText(this, "Restoring in background", Toast.LENGTH_SHORT).show();
    }

    private void startRestoration() {
        firebaseHelper.setupNotification(notificationBuilder, notificationManagerCompat);
        restoreInitCheckHandler.post(dataRestoreInitCheck);
        firebaseHelper.checkIfUserExists();
    }

    private void stopForegroundService() {
        stopForeground(true);
        stopSelf();
    }

    private void setupCustomNotification(){

        notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());

        Intent resultIntent = new Intent(this, StartUpActivity.class);
        resultIntent.putExtra("receivedAction", receivedAction);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(6, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationBuilder = new NotificationCompat.Builder(this, TickTrack.DATA_BACKUP_RESTORE_NOTIFICATION)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setOnlyAlertOnce(true)
                .setContentIntent(resultPendingIntent)
                .setProgress(0,0,true)
                .setColor(getResources().getColor(R.color.Accent));

        if (android.os.Build.VERSION. SDK_INT >= android.os.Build.VERSION_CODES. O ) {
            notificationBuilder.setChannelId(TickTrack.DATA_BACKUP_RESTORE_NOTIFICATION);
        }

    }

    public void notifyNotification(){
        notificationManagerCompat.notify(6, notificationBuilder.build());
    }

    Handler restoreInitCheckHandler = new Handler();
    Runnable dataRestoreInitCheck = new Runnable() {
        @Override
        public void run() {
            if(firebaseHelper.dataRestoreInitCompleteCheck()){
                tickTrackFirebaseDatabase.setRestoreInitMode(false);
                tickTrackFirebaseDatabase.setRestoreMode(false);
//                restoreCompleteCheckHandler.post(dataRestoreCompleteCheck);
            } else {
                restoreInitCheckHandler.post(dataRestoreInitCheck);
            }
        }
    };

    Handler restoreCompleteCheckHandler = new Handler();
    Runnable dataRestoreCompleteCheck = new Runnable() {
        @Override
        public void run() {
//            if(firebaseHelper.dataRestoreCompleteCheck()){
//                tickTrackFirebaseDatabase.setRestoreInitMode(false);
//                tickTrackFirebaseDatabase.setRestoreMode(true);
//            } else {
//                restoreCompleteCheckHandler.post(dataRestoreCompleteCheck);
//            }
        }
    };


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
