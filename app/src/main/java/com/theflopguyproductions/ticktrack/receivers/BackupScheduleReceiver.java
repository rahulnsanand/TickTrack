package com.theflopguyproductions.ticktrack.receivers;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.theflopguyproductions.ticktrack.service.BackupRestoreService;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackFirebaseDatabase;
import com.theflopguyproductions.ticktrack.utils.firebase.FirebaseHelper;

public class BackupScheduleReceiver extends BroadcastReceiver {

    public static final String START_BACKUP_SCHEDULE = "com.theflopguyproductions.ticktrack.START_BACKUP_SCHEDULE";

    private TickTrackFirebaseDatabase tickTrackFirebaseDatabase;

    @Override
    public void onReceive(Context context, Intent intent) {

        tickTrackFirebaseDatabase = new TickTrackFirebaseDatabase(context);
        FirebaseHelper firebaseHelper = new FirebaseHelper(context);

        if(firebaseHelper.isUserSignedIn() && !isMyServiceRunning(BackupRestoreService.class, context)){
            startBackupService(context);
        }

    }

    private void startBackupService(Context context) {
        tickTrackFirebaseDatabase.setBackupMode(true);
        tickTrackFirebaseDatabase.setCounterBackupComplete(false);
        tickTrackFirebaseDatabase.setTimerBackupComplete(false);
        tickTrackFirebaseDatabase.setSettingsBackupComplete(false);
        Intent intent = new Intent(context, BackupRestoreService.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setAction(BackupRestoreService.RESTORE_SERVICE_START_BACKUP);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    public boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
