package com.theflopguyproductions.ticktrack.background;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;

import com.theflopguyproductions.ticktrack.MainActivityToChange;
import com.theflopguyproductions.ticktrack.R;

public class MyAlarmService extends Service {
    //private NotificationManager mManager;
    public IBinder onBind(Intent arg0) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        long[] pattern = {500, 500, 500, 500, 500, 500, 500, 500, 500};
        NotificationManager nm = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
        //Yeni bir pendingIntent açıyoruz ve içerisine de yeni bir intent oluşturuyoruz
        PendingIntent pi = PendingIntent.getActivity(MyAlarmService.this, 0, new Intent(MyAlarmService.this, MainActivityToChange.class), 0);
        //Ardından notification adında bir notification yani bildirim oluşturuyoruz ve bilgilerini giriyoruz.
        Notification notification = new Notification.Builder(MyAlarmService.this)
                .setContentIntent(pi) // bildirime tıklayınca açılacak olan pendingintent
                .setContentTitle("Bildirim Ornegi")
                .setContentText("Bildirimin içindeki text")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true) // Bildirime tıklayınca bildirim yok olsun.
                .setSound(alarmSound)
                .setVibrate(pattern)
                .setLights(Color.BLUE, 500, 500)
                .addAction(R.mipmap.ic_launcher, "bildirimin en alt mesajı", pi) // bildirimi açınca altta bişeyler daha çıkarıyor.
                .build(); //Butun yukarda girilen özellikleri combine edip yeni notification objesi oluştur.

        nm.notify(1, notification);
    }

}