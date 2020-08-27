package com.theflopguyproductions.ticktrack.service;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference rootDatabase;

    public static final String DATA_RESTORATION_START = "DATA_RESTORATION_START";
    public static final String DATA_RESTORATION_STOP = "DATA_RESTORATION_STOP";

    private NotificationCompat.Builder notificationBuilder;
    private NotificationManagerCompat notificationManagerCompat;
    private RemoteViews notificationLayout;

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
        firebaseDatabase = FirebaseDatabase.getInstance();
        rootDatabase = firebaseDatabase.getReference();

        setupCustomNotification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null) {

            String action = intent.getAction();

            receivedAction = intent.getStringExtra("receivedAction");

            assert action != null;
            switch (action) {
                case DATA_RESTORATION_START:
                    setupForeground();
                    startRestoration();
                    break;
                case DATA_RESTORATION_STOP:
                    stopForegroundService();
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void setupForeground() {
        startForeground(1, notificationBuilder.build());
        Toast.makeText(this, "Restoring in background", Toast.LENGTH_SHORT).show();
    }

    private void startRestoration() {
        firebaseHelper.setupNotification(notificationBuilder, notificationManagerCompat, notificationLayout);
        firebaseHelper.checkIfUserExists();
    }

    private void stopForegroundService() {
        System.out.println("Stop Service called");
        stopForeground(true);
        stopSelf();
    }

    private void setupCustomNotification(){

        notificationLayout = new RemoteViews(getPackageName(), R.layout.backup_restore_notification_layout);
        notificationLayout.setTextViewText(R.id.backupRestoreNotificationLayoutTitleText,"Looking for TickTrack backup");
        notificationLayout.setTextViewText(R.id.backupRestoreNotificationLayoutContentText, "In progress");
        notificationLayout.setProgressBar(R.id.backupRestoreNotificationLayoutProgressBar,0,0,true);

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
                .setCustomContentView(notificationLayout)
                .setOnlyAlertOnce(true)
                .setContentIntent(resultPendingIntent);

        if (android.os.Build.VERSION. SDK_INT >= android.os.Build.VERSION_CODES. O ) {
            notificationBuilder.setChannelId(TickTrack.DATA_BACKUP_RESTORE_NOTIFICATION);
        }

    }

    public void notifyNotification(){
        notificationManagerCompat.notify(1, notificationBuilder.build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
