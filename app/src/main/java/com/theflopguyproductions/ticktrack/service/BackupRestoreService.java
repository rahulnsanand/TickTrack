package com.theflopguyproductions.ticktrack.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.firestore.FirebaseFirestore;
import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.application.TickTrack;
import com.theflopguyproductions.ticktrack.startup.StartUpActivity;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackFirebaseDatabase;
import com.theflopguyproductions.ticktrack.utils.firebase.FirebaseHelper;
import com.theflopguyproductions.ticktrack.utils.firebase.JsonHelper;

import java.text.DateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BackupRestoreService extends Service {

    private FirebaseHelper firebaseHelper;
    private TickTrackFirebaseDatabase tickTrackFirebaseDatabase;
    private TickTrackDatabase tickTrackDatabase;
    private JsonHelper jsonHelper;

    public static final String RESTORE_SERVICE_STOP_FOREGROUND = "RESTORE_SERVICE_STOP_FOREGROUND";
    public static final String RESTORE_SERVICE_START_INIT_RETRIEVE = "RESTORE_SERVICE_START_INIT_RETRIEVE";
    public static final String RESTORE_SERVICE_START_RESTORE = "RESTORE_SERVICE_START_RESTORE";
    public static final String RESTORE_SERVICE_START_BACKUP = "RESTORE_SERVICE_START_BACKUP";
    public static final String CANCEL_RESTORE_SERVICE = "CANCEL_RESTORE_SERVICE";

    private NotificationCompat.Builder notificationBuilder;
    private NotificationManagerCompat notificationManagerCompat;

    private String receivedAction;

    /**
     * DEBUG VALUES
     */

    private FirebaseFirestore firebaseFirestore;

    public BackupRestoreService() {
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
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null) {

            String action = intent.getAction();

            receivedAction = intent.getStringExtra("receivedAction");
            firebaseHelper.setAction(receivedAction);
            System.out.println("RESTORE SERVICE RECEIVED "+receivedAction);

            Intent resultIntent = new Intent(this, StartUpActivity.class);
            resultIntent.putExtra("receivedAction", receivedAction);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addNextIntentWithParentStack(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(TickTrack.BACKUP_RESTORE_NOTIFICATION_ID, PendingIntent.FLAG_UPDATE_CURRENT);

            assert action != null;
            switch (action) {
                case RESTORE_SERVICE_STOP_FOREGROUND:
                case CANCEL_RESTORE_SERVICE:
                    stopForegroundService();
                    break;
                case RESTORE_SERVICE_START_INIT_RETRIEVE:
                    notificationBuilder.setContentIntent(resultPendingIntent);
                    setupForeground();
                    startInitRestore();
                    break;
                case RESTORE_SERVICE_START_RESTORE:
                    notificationBuilder.setContentIntent(resultPendingIntent);
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
        startForeground(TickTrack.BACKUP_RESTORE_NOTIFICATION_ID, notificationBuilder.build());
    }

    Handler restoreCheckHandler = new Handler();
    Runnable dataRestoreCheck = new Runnable() {
        @Override
        public void run() {
            if(tickTrackFirebaseDatabase.getRestoreCompleteStatus()==1){
                System.out.println("RESTORE COMPLETE");
                restoreCheckHandler.removeCallbacks(dataRestoreCheck);
                stopForegroundService();
                prefixFirebaseVariables();
            } else if(tickTrackFirebaseDatabase.getRestoreCompleteStatus()==-1){
                System.out.println("RESTORE COMPLETION ERROR");
            } else {
                System.out.println("RESTORE COMPLETION NOT");
                restoreCheckHandler.post(dataRestoreCheck);
            }
        }
    };
    private void prefixFirebaseVariables() {
        tickTrackFirebaseDatabase.setRestoreMode(false);
        tickTrackFirebaseDatabase.setCounterDownloadStatus(0);
        tickTrackFirebaseDatabase.setTimerDownloadStatus(0);
        tickTrackFirebaseDatabase.setRestoreCompleteStatus(0);
        tickTrackFirebaseDatabase.storeTimerRestoreString("");
        tickTrackFirebaseDatabase.storeCounterRestoreString("");
        tickTrackFirebaseDatabase.setCounterBackupComplete(false);
        tickTrackFirebaseDatabase.setTimerBackupComplete(false);
        tickTrackFirebaseDatabase.setSettingsBackupComplete(false);
        tickTrackFirebaseDatabase.setBackupMode(false);
    }

    private void startInitRestore() {
        prefixFirebaseVariables();
        tickTrackFirebaseDatabase.setRestoreInitMode(-1);
        firebaseHelper.setupNotification(notificationBuilder, notificationManagerCompat);
        restoreCheckHandler.post(dataRestoreCheck);
        firebaseHelper.restoreInit();
    }
    private void startRestoration() {
        tickTrackFirebaseDatabase.setRestoreInitMode(0);
        tickTrackFirebaseDatabase.setRestoreMode(true);
        Toast.makeText(this, "Restoring in background", Toast.LENGTH_SHORT).show();
        firebaseHelper.setupNotification(notificationBuilder, notificationManagerCompat);
        firebaseHelper.restore();
    }

    Handler backupCheckHandler = new Handler();
    Runnable dataBackupCheck = new Runnable() {
        @Override
        public void run() {
            if(!tickTrackFirebaseDatabase.isBackupMode()){
                System.out.println("BACKUP OVER");
                stopForegroundService();
                prefixFirebaseVariables();
                backupCheckHandler.removeCallbacks(dataBackupCheck);
            } else {
                backupCheckHandler.post(dataBackupCheck);
            }
        }
    };
    private void startBackup() {
        updateFirebaseData();
        notificationBuilder.setContentTitle("TickTrack backup");
        notificationBuilder.setContentText("In progress");
        notifyNotification();
        backupCheckHandler.post(dataBackupCheck);
        firebaseHelper.backup();
    }

    private void updateFirebaseData() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account!=null){
            firebaseFirestore.collection("TickTrackUsers Backup Debug").document(Objects.requireNonNull(account.getEmail())).get()
                    .addOnSuccessListener(snapshot -> {
                        Map<String, Object> retrieveData = new HashMap<>();
                        if(snapshot.exists()){
                            retrieveData = snapshot.getData();
                            if (retrieveData == null) {
                                retrieveData = new HashMap<>();
                            }
                        }
                        retrieveData.put("Backup "+retrieveData.size(), DateFormat.getDateTimeInstance().format(System.currentTimeMillis()));
                        firebaseFirestore.collection("TickTrackUsers Backup Debug")
                                .document(Objects.requireNonNull(account.getEmail())).set(retrieveData);
                    }).addOnFailureListener(e -> {

            });
        }
    }

    private void stopForegroundService() {
        backupCheckHandler.removeCallbacks(dataBackupCheck);
        restoreCheckHandler.removeCallbacks(dataRestoreCheck);

        stopForeground(false);
        stopSelf();
        onDestroy();
    }

    private void setupCustomNotification(){

        notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());

        Intent cancelIntent = new Intent(this, BackupRestoreService.class);
        cancelIntent.setAction(CANCEL_RESTORE_SERVICE);
        PendingIntent pendingCancelIntent = PendingIntent.getService(this, 10, cancelIntent, 0);
        NotificationCompat.Action cancelAction = new NotificationCompat.Action(R.drawable.ic_round_close_white_24, "Cancel", pendingCancelIntent);

        notificationBuilder = new NotificationCompat.Builder(this, TickTrack.DATA_BACKUP_RESTORE_NOTIFICATION)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setOnlyAlertOnce(true)
                .setProgress(0,0,true)
                .setColor(getResources().getColor(R.color.Accent));

        notificationBuilder.addAction(cancelAction);

        if (android.os.Build.VERSION. SDK_INT >= android.os.Build.VERSION_CODES. O ) {
            notificationBuilder.setChannelId(TickTrack.DATA_BACKUP_RESTORE_NOTIFICATION);
        }

    }

    public void notifyNotification(){
        notificationManagerCompat.notify(TickTrack.BACKUP_RESTORE_NOTIFICATION_ID, notificationBuilder.build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
