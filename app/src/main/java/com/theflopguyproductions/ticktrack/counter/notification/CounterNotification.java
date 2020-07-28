package com.theflopguyproductions.ticktrack.counter.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.application.TickTrack;

public class CounterNotification extends Service {

    private static final String TAG_COUNTER_SERVICE = "COUNTER_SERVICE";

    public static final String ACTION_START_COUNTER_SERVICE = "ACTION_START_COUNTER_SERVICE";

    public static final String ACTION_STOP_COUNTER_SERVICE = "ACTION_STOP_COUNTER_SERVICE";

    public static final String ACTION_PLUS = "ACTION_PLUS";

    public static final String ACTION_CANCEL = "ACTION_CANCEL";

    public static final String ACTION_MINUS = "ACTION_MINUS";

    public CounterNotification(){

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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null)
        {
            String action = intent.getAction();

            switch (action)
            {
                case ACTION_START_COUNTER_SERVICE:
                    startForegroundService();
                    Toast.makeText(getApplicationContext(), "Foreground service is started.", Toast.LENGTH_LONG).show();
                    break;
                case ACTION_STOP_COUNTER_SERVICE:
                    stopForegroundService();
                    Toast.makeText(getApplicationContext(), "Foreground service is stopped.", Toast.LENGTH_LONG).show();
                    break;
                case ACTION_PLUS:
                    Toast.makeText(getApplicationContext(), "You click Play button.", Toast.LENGTH_LONG).show();
                    break;
                case ACTION_MINUS:
                    Toast.makeText(getApplicationContext(), "You click Pause button.", Toast.LENGTH_LONG).show();
                    break;
                case ACTION_CANCEL:
                    Toast.makeText(getApplicationContext(), "You click Cancel button.", Toast.LENGTH_LONG).show();
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void startForegroundService()
    {
        Log.d(TAG_COUNTER_SERVICE, "Start foreground service.");

        // Create notification default intent.
        Intent intent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        // Create notification builder.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        if (android.os.Build.VERSION. SDK_INT >= android.os.Build.VERSION_CODES. O ) {
            builder.setChannelId(TickTrack.COUNTER_NOTIFICATION);
        }

        // Make notification show big text.
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.setBigContentTitle("Music player implemented by foreground service.");
        bigTextStyle.bigText("Android foreground service is a android service which can run in foreground always, it can be controlled by user via notification.");
        // Set big text style.
        builder.setStyle(bigTextStyle);

        builder.setWhen(System.currentTimeMillis());
        builder.setSmallIcon(R.mipmap.ic_launcher);
        Bitmap largeIconBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_flag_red);
        builder.setLargeIcon(largeIconBitmap);

        builder.setPriority(Notification.PRIORITY_MAX);

        builder.setFullScreenIntent(pendingIntent, true);

        // Add Play button intent in notification.
        Intent playIntent = new Intent(this, CounterNotification.class);
        playIntent.setAction(ACTION_PLUS);
        PendingIntent pendingPlayIntent = PendingIntent.getService(this, 0, playIntent, 0);
        NotificationCompat.Action playAction = new NotificationCompat.Action(android.R.drawable.ic_media_play, "Play", pendingPlayIntent);
        builder.addAction(playAction);

        // Add Pause button intent in notification.
        Intent pauseIntent = new Intent(this, CounterNotification.class);
        pauseIntent.setAction(ACTION_MINUS);
        PendingIntent pendingPrevIntent = PendingIntent.getService(this, 0, pauseIntent, 0);
        NotificationCompat.Action prevAction = new NotificationCompat.Action(android.R.drawable.ic_media_pause, "Pause", pendingPrevIntent);
        builder.addAction(prevAction);

        // Build the notification.
        Notification notification = builder.build();

        // Start foreground service.
        startForeground(1, notification);
    }

    private void stopForegroundService()
    {
        Log.d(TAG_COUNTER_SERVICE, "Stop foreground service.");

        // Stop foreground service and remove the notification.
        stopForeground(true);

        // Stop the foreground service.
        stopSelf();
    }


}
