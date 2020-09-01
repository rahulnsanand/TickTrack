package com.theflopguyproductions.ticktrack.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.theflopguyproductions.ticktrack.service.BackupRestoreService;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackFirebaseDatabase;

public class BackupScheduleReceiver extends BroadcastReceiver {

    public static final String START_BACKUP_SCHEDULE = "START_BACKUP_SCHEDULE";

    private TickTrackFirebaseDatabase tickTrackFirebaseDatabase;
    @Override
    public void onReceive(Context context, Intent intent) {

        tickTrackFirebaseDatabase = new TickTrackFirebaseDatabase(context);

        startBackupService(context);

    }

    private void startBackupService(Context context) {
        tickTrackFirebaseDatabase.setBackupMode(true);
        Intent intent = new Intent(context, BackupRestoreService.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setAction(BackupRestoreService.RESTORE_SERVICE_START_BACKUP);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }
}
