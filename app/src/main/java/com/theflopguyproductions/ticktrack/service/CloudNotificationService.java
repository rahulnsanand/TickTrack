package com.theflopguyproductions.ticktrack.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.theflopguyproductions.ticktrack.BuildConfig;
import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.application.TickTrack;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;

import java.util.Objects;

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class CloudNotificationService extends FirebaseMessagingService {

    private static final String TAG = "FIREBASE_MESSAGING";
    private TickTrackDatabase tickTrackDatabase;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "From: " + remoteMessage.getData().get("NOTIFICATION_TYPE"));
        Log.d(TAG, "From: " + remoteMessage.getData().get( "VERSION"));
        Log.d(TAG, "From: " + remoteMessage.getData().get("VERSION_CODE"));
        Log.d(TAG, "From: " + remoteMessage.getData().get("IS_COMPULSORY"));
        Log.d(TAG, "From: " + remoteMessage.getData().get("NOTIFICATION_TITLE"));
        Log.d(TAG, "From: " + remoteMessage.getData().get("NOTIFICATION_CONTENT"));


        tickTrackDatabase = new TickTrackDatabase(this);

        if (Objects.equals(remoteMessage.getData().get("NOTIFICATION_TYPE"), "IS_UPDATE")) {
            int versionCode = BuildConfig.VERSION_CODE;
            Log.d(TAG, "From: " + versionCode);
            if(versionCode < Integer.parseInt(Objects.requireNonNull(remoteMessage.getData().get("VERSION_CODE")))){
                tickTrackDatabase.storeUpdateVersion(remoteMessage.getData().get( "VERSION"));
                tickTrackDatabase.setUpdateCompulsion(remoteMessage.getData().get("IS_COMPULSORY"));
                tickTrackDatabase.setUpdateTime(System.currentTimeMillis());
                tickTrackDatabase.storeUpdateVersionCode(Integer.parseInt(Objects.requireNonNull(remoteMessage.getData().get("VERSION_CODE"))));
                sendUpdateNotification(remoteMessage.getData().get("NOTIFICATION_TITLE"), remoteMessage.getData().get("NOTIFICATION_CONTENT"));
            } else {
                tickTrackDatabase.storeUpdateVersion(remoteMessage.getData().get( "VERSION"));
                tickTrackDatabase.setUpdateCompulsion(remoteMessage.getData().get("IS_COMPULSORY"));
                tickTrackDatabase.setUpdateTime(System.currentTimeMillis());
                tickTrackDatabase.storeUpdateVersionCode(Integer.parseInt(Objects.requireNonNull(remoteMessage.getData().get("VERSION_CODE"))));
            }
        }
    }


    private void sendUpdateNotification(String title, String message) {

        Intent rateIntent;
        try {
            rateIntent = rateIntentForUrl("market://details");
        }
        catch (ActivityNotFoundException e) {
            rateIntent = rateIntentForUrl("https://play.google.com/store/apps/details");
        }

        PendingIntent pendingIntent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(this, TickTrack.PUSH_NOTIFICATION_ID, rateIntent, PendingIntent.FLAG_MUTABLE);
        } else {
            pendingIntent = PendingIntent.getActivity(this, TickTrack.PUSH_NOTIFICATION_ID, rateIntent, PendingIntent.FLAG_ONE_SHOT);
        }

        NotificationCompat.Builder notificationBuilder;
        NotificationManagerCompat notificationManagerCompat;
        notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationBuilder = new NotificationCompat.Builder(this, TickTrack.PUSH_NOTIFICATION)
                .setSmallIcon(R.drawable.ic_stat_ticktrack_logo_notification_icon)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(Notification.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setVibrate(new long[0])
                .setOnlyAlertOnce(true)
                .setAutoCancel(true)
                .setColor(ContextCompat.getColor(this, R.color.Accent));

        notificationBuilder.setContentTitle(title);
        notificationBuilder.setContentIntent(pendingIntent);
        notificationBuilder.setContentText(message);

        notificationManagerCompat.notify(TickTrack.PUSH_NOTIFICATION_ID, notificationBuilder.build());
    }

    private Intent rateIntentForUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("%s?id=%s", url, getPackageName())));
        int flags = Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
        if (Build.VERSION.SDK_INT >= 21) {
            flags |= Intent.FLAG_ACTIVITY_NEW_DOCUMENT;
        }
        else {
            //noinspection deprecation
            flags |= Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET;
        }
        intent.addFlags(flags);
        return intent;
    }

}
