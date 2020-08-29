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
import com.theflopguyproductions.ticktrack.utils.helpers.JsonHelper;

public class RestoreService extends Service {

    private Activity activity;
    private FirebaseHelper firebaseHelper;
    private TickTrackFirebaseDatabase tickTrackFirebaseDatabase;
    private TickTrackDatabase tickTrackDatabase;
    private JsonHelper jsonHelper;

    public static final String RESTORE_SERVICE_STOP_FOREGROUND = "RESTORE_SERVICE_STOP_FOREGROUND";
    public static final String RESTORE_SERVICE_START_INIT_RETRIEVE = "RESTORE_SERVICE_START_INIT_RETRIEVE";
    public static final String RESTORE_SERVICE_START_RESTORE = "RESTORE_SERVICE_START_RESTORE";
    public static final String RESTORE_SERVICE_START_BACKUP = "RESTORE_SERVICE_START_BACKUP";

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
        jsonHelper = new JsonHelper(this);
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
                case RESTORE_SERVICE_STOP_FOREGROUND:
                    stopForegroundService();
                    break;
                case RESTORE_SERVICE_START_INIT_RETRIEVE:
                    setupForeground();
                    startInitRestore();
                    break;
                case RESTORE_SERVICE_START_RESTORE:
                    setupForeground();
                    startRestoration();
                    break;
                case RESTORE_SERVICE_START_BACKUP:
                    setupForeground();
                    startBackup();
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }
    private void setupForeground() {
        System.out.println("FOREGROUND BEGINS");
        startForeground(6, notificationBuilder.build());
    }

    private void startInitRestore() {
        firebaseHelper.setupNotification(notificationBuilder, notificationManagerCompat);
        restoreInitCheckHandler.post(dataRestoreInitCheck);
        firebaseHelper.restoreInit();
    }

    private void startBackup() {
        firebaseHelper.setupNotification(notificationBuilder, notificationManagerCompat);
        backupCheckHandler.post(dataBackupCheck);
        firebaseHelper.backup();
    }

    private void startRestoration() {
        tickTrackFirebaseDatabase.setRestoreMode(true);
        Toast.makeText(this, "Restoring in background", Toast.LENGTH_SHORT).show();
        firebaseHelper.setupNotification(notificationBuilder, notificationManagerCompat);
        restoreCheckHandler.post(dataRestoreCheck);
        firebaseHelper.restore();
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
    Handler restoreCheckHandler = new Handler();
    Handler backupCheckHandler = new Handler();

    Runnable dataRestoreInitCheck = new Runnable() {
        @Override
        public void run() {
            if(firebaseHelper.restoreInitComplete()){
                System.out.println("RESTORE INIT COMPLETE");
                tickTrackFirebaseDatabase.setRestoreInitMode(false);
                restoreInitCheckHandler.removeCallbacks(dataRestoreInitCheck);
                stopForegroundService();
            } else {
                System.out.println("RESTORE INIT COMPLETION CHECKING");
                restoreInitCheckHandler.post(dataRestoreInitCheck);
            }
        }
    };
    Runnable dataRestoreCheck = new Runnable() {
        @Override
        public void run() {
            if(firebaseHelper.restorationComplete()){
                System.out.println("RESTORE COMPLETE");
                tickTrackFirebaseDatabase.setRestoreMode(false);
                restoreCheckHandler.removeCallbacks(dataRestoreCheck);
                stopForegroundService();
            } else {
                System.out.println("RESTORE COMPLETION CHECKING");
                restoreCheckHandler.post(dataRestoreCheck);
            }
        }
    };
    Runnable dataBackupCheck = new Runnable() {
        @Override
        public void run() {
            if(firebaseHelper.backupComplete()){
                tickTrackFirebaseDatabase.setBackupMode(false);
                backupCheckHandler.removeCallbacks(dataBackupCheck);
            } else {
                backupCheckHandler.post(dataBackupCheck);
            }
        }
    };


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
