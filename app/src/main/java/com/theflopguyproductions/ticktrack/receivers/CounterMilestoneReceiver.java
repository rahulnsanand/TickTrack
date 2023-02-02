package com.theflopguyproductions.ticktrack.receivers;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.SoYouADeveloperHuh;
import com.theflopguyproductions.ticktrack.application.TickTrack;
import com.theflopguyproductions.ticktrack.counter.CounterData;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;

import java.util.ArrayList;

public class CounterMilestoneReceiver extends BroadcastReceiver {

    public static final String ACTION_COUNTER_MILESTONE_REMINDER = "ACTION_COUNTER_MILESTONE_REMINDER";

    private int milestoneCount = 0;
    private long milestoneValue = 0L;
    private TickTrackDatabase tickTrackDatabase;


    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("RECEIVED COUNTER MILESTONE CHECK");
        tickTrackDatabase = new TickTrackDatabase(context);
        if (ACTION_COUNTER_MILESTONE_REMINDER.equals(intent.getAction())) {
            if (hasMilestone()) {
                if (milestoneCount == 1) {
                    buildNotification(true, context);
                } else if (milestoneCount > 1) {
                    buildNotification(false, context);
                }
            }
        }
    }

    private void buildNotification(boolean b, Context context) {
        Intent contentIntent = new Intent(context, SoYouADeveloperHuh.class);

        PendingIntent pendingIntent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(context, TickTrack.GENERAL_NOTIFICATION_ID, contentIntent, PendingIntent.FLAG_MUTABLE);
        } else {
            pendingIntent = PendingIntent.getActivity(context, TickTrack.GENERAL_NOTIFICATION_ID, contentIntent, PendingIntent.FLAG_ONE_SHOT);
        }

        NotificationCompat.Builder notificationBuilder;
        NotificationManagerCompat notificationManagerCompat;
        notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationBuilder = new NotificationCompat.Builder(context, TickTrack.GENERAL_NOTIFICATION)
                .setSmallIcon(R.drawable.ic_stat_ticktrack_logo_notification_icon)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(Notification.PRIORITY_MIN)
                .setCategory(NotificationCompat.CATEGORY_PROGRESS)
                .setVibrate(new long[0])
                .setOnlyAlertOnce(true)
                .setAutoCancel(true)
                .setColor(ContextCompat.getColor(context, R.color.Accent));
        notificationBuilder.setContentIntent(pendingIntent);
        notificationBuilder.setContentTitle("Milestones Awaiting!");
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if (b) {
            if (milestoneValue > 0) {
                notificationBuilder.setContentText("Just " + milestoneValue + " counts more!");
                notificationManagerCompat.notify(TickTrack.GENERAL_NOTIFICATION_ID, notificationBuilder.build());
            } else if(milestoneValue<0){
                notificationBuilder.setContentText("Just "+Math.abs(milestoneValue)+" counts less!");
                notificationManagerCompat.notify(TickTrack.GENERAL_NOTIFICATION_ID, notificationBuilder.build());
            }
        } else {
            notificationBuilder.setContentText("You've got "+milestoneCount+" counters waiting to count!");
            notificationManagerCompat.notify(TickTrack.GENERAL_NOTIFICATION_ID, notificationBuilder.build());
        }

    }

    private boolean hasMilestone() {
        ArrayList<CounterData> counterDataArrayList = tickTrackDatabase.retrieveCounterList();
        milestoneCount = 0;
        for (int i=0; i<counterDataArrayList.size(); i++){
            if(counterDataArrayList.get(i).isCounterSignificantExist()){
                milestoneCount++;
                milestoneValue = counterDataArrayList.get(i).getCounterValue()-counterDataArrayList.get(i).getCounterSignificantCount();
            }
        }
        return milestoneCount > 0;
    }
}
