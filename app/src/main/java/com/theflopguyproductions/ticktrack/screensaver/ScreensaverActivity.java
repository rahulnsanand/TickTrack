package com.theflopguyproductions.ticktrack.screensaver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.SoYouADeveloperHuh;
import com.theflopguyproductions.ticktrack.settings.SettingsActivity;
import com.theflopguyproductions.ticktrack.ui.utils.TickTrackAnimator;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackFirebaseDatabase;
import com.theflopguyproductions.ticktrack.utils.firebase.FirebaseHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ScreensaverActivity extends AppCompatActivity {

    private static int DISPLAY_DURATION = 2500;
    private static final float DARK_MODE_ALPHA = 0.2f;
    private static final float LIGHT_MODE_ALPHA = 1f;
    private static final int FADE_DURATION = 200;

    public static final String ACTION_SCREENSAVER_EDIT = "ACTION_SCREENSAVER_EDIT";
    public static final String ACTION_TIME_CHANGE_ANALOG = "ACTION_TIME_CHANGE_ANALOG";

    private ConstraintLayout rootLayout;
    private LayoutInflater layoutInflater;
    private ConstraintLayout clockLayout;
    private ConstraintLayout buttonLayout;
    private Handler optionsDisplayHandler = new Handler();
    private TextView dismissTextHelper;
    private Button settingsButton;
    private ConstraintLayout fabLayout;
    private ImageView darkImage, lightImage;
    private TextView dateText, salutationText;

    private Runnable optionsDisplayRunnable = new Runnable() {
        @Override
        public void run() {
            long loopTime = SystemClock.elapsedRealtime();
            long difference = loopTime-currentTime;
            if(difference < DISPLAY_DURATION){
                optionsDisplayHandler.post(optionsDisplayRunnable);
            } else {
                hideOptionsDisplay();
                optionsDisplayHandler.removeCallbacks(optionsDisplayRunnable);
            }
        }
    };

    private boolean isOptionsOpen = false;
    long currentTime;
    private void hideOptionsDisplay() {
        if(isOptionsOpen){
            dismissTextHelper.setVisibility(View.GONE);
            buttonLayout.setVisibility(View.GONE);
            isOptionsOpen = false;
            optionsDisplayHandler.removeCallbacks(optionsDisplayRunnable);
            DISPLAY_DURATION = 2500;
        }
    }
    private void showOptionsDisplay() {
        if(!isOptionsOpen){
            currentTime = SystemClock.elapsedRealtime();
            optionsDisplayHandler.post(optionsDisplayRunnable);
            buttonLayout.setVisibility(View.VISIBLE);
            dismissTextHelper.setVisibility(View.VISIBLE);
            isOptionsOpen = true;
        }
    }

    private boolean isNightMode = false;
    private void toggleNightMode(){
        currentTime = SystemClock.elapsedRealtime();
        if(isNightMode){
            TickTrackAnimator.fabImageDissolve(lightImage);
            TickTrackAnimator.fabImageReveal(darkImage);
            isNightMode=false;
        } else {
            TickTrackAnimator.fabImageDissolve(darkImage);
            TickTrackAnimator.fabImageReveal(lightImage);
            isNightMode=true;
        }
        setupClock(clockStyle);
        setupLayout();
    }

    private void setupLayout() {
        if(isNightMode){
            fabLayout.animate().setDuration(FADE_DURATION).alpha(DARK_MODE_ALPHA).start();
            settingsButton.animate().setDuration(FADE_DURATION).alpha(DARK_MODE_ALPHA).start();
        } else {
            fabLayout.animate().setDuration(FADE_DURATION).alpha(LIGHT_MODE_ALPHA).start();
            settingsButton.animate().setDuration(FADE_DURATION).alpha(LIGHT_MODE_ALPHA).start();
        }
    }

    private void setupTimeText() {

        System.out.println("SETUP CALLED");

        Calendar fiveAm = Calendar.getInstance();
        fiveAm.set(Calendar.HOUR_OF_DAY, 5);
        fiveAm.set(Calendar.MINUTE, 0);
        fiveAm.set(Calendar.SECOND, 0);

        Calendar twelvePm = Calendar.getInstance();
        twelvePm.set(Calendar.HOUR_OF_DAY, 12);
        twelvePm.set(Calendar.MINUTE, 0);
        twelvePm.set(Calendar.SECOND, 0);

        Calendar fivePm = Calendar.getInstance();
        fivePm.set(Calendar.HOUR_OF_DAY, 17);
        fivePm.set(Calendar.MINUTE, 0);
        fivePm.set(Calendar.SECOND, 0);

        Calendar twelveAm = Calendar.getInstance();
        twelveAm.set(Calendar.HOUR_OF_DAY, 0);
        twelveAm.set(Calendar.MINUTE, 0);
        twelveAm.set(Calendar.SECOND, 0);

        Calendar fiveAmNext = Calendar.getInstance();
        fiveAmNext.set(Calendar.HOUR_OF_DAY, 5);
        fiveAmNext.add(Calendar.DATE, 1);
        fiveAmNext.set(Calendar.MINUTE, 0);
        fiveAmNext.set(Calendar.SECOND, 0);

        Calendar fivePmPrev = Calendar.getInstance();
        fivePmPrev.set(Calendar.HOUR_OF_DAY, 17);
        fivePmPrev.add(Calendar.DATE, -1);
        fivePmPrev.set(Calendar.MINUTE, 0);
        fivePmPrev.set(Calendar.SECOND, 0);

        Calendar twelveAmNext = Calendar.getInstance();
        twelveAmNext.set(Calendar.HOUR_OF_DAY, 0);
        twelveAmNext.add(Calendar.DATE, 1);
        twelveAmNext.set(Calendar.MINUTE, 0);
        twelveAmNext.set(Calendar.SECOND, 0);

        if(Calendar.getInstance().before(twelvePm) && Calendar.getInstance().after(fiveAm)){
            setupSalutation(1);
        } else if (Calendar.getInstance().before(fivePm) && Calendar.getInstance().after(twelvePm)){
            setupSalutation(2);
        } else if (Calendar.getInstance().before(twelveAmNext) && Calendar.getInstance().after(fivePm)){
            setupSalutation(3);
        } else if (Calendar.getInstance().before(fiveAm) && Calendar.getInstance().after(twelveAm)){
            setupSalutation(3);
        } else {
            setupSalutation(0);
        }

        setupDate();

    }

    private void setupDate() {
        Locale locale ;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            locale = getResources().getConfiguration().getLocales().get(0);
        } else{
            locale = getResources().getConfiguration().locale;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E, MMM dd yyyy", locale);

        long currentTime = System.currentTimeMillis();

        String currentTimeDate = simpleDateFormat.format(currentTime);
        dateText.setText(currentTimeDate);
    }

    private void setupSalutation(int i) {
        String salutation = "";
        FirebaseHelper firebaseHelper = new FirebaseHelper(this);
        boolean isDriveFail = new TickTrackFirebaseDatabase(this).isDriveLinkFail();
        if(i==1){
            salutation += "Good morning, ";
        } else if(i==2){
            salutation += "Good afternoon, ";
        } else if(i==3){
            salutation += "Good evening, ";
        } else {
            System.out.println("SALUTATION INT ZERO");
            salutationText.setVisibility(View.GONE);
            return;
        }
        if(firebaseHelper.isUserSignedIn()){
            if(!isDriveFail){
                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
                if(account!=null){
                    String firstName = account.getDisplayName();
                    salutation += firstName;
                    salutationText.setText(salutation);
                    salutationText.setVisibility(View.VISIBLE);
                }
            } else {
                System.out.println("DRIVE FAILED");
                salutationText.setVisibility(View.GONE);
            }
        } else {
            salutationText.setText(salutation);
            salutationText.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        setupScreensaverAlarms();

        setupTimeText();

        TickTrackDatabase tickTrackDatabase = new TickTrackDatabase(this);
        clockStyle = tickTrackDatabase.getScreenSaverClock();
        setupClock(clockStyle);

        TickTrackAnimator.fabImageDissolve(lightImage);
        TickTrackAnimator.fabImageReveal(darkImage);
        isNightMode=false;

        isOptionsOpen = false;
        DISPLAY_DURATION = 500;
        showOptionsDisplay();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                |  View.SYSTEM_UI_FLAG_LOW_PROFILE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    private int clockStyle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_screensaver);
        rootLayout = findViewById(R.id.screenSaverRootLayout);
        buttonLayout = findViewById(R.id.screensaverButtonLayout);
        dismissTextHelper = findViewById(R.id.screensaverDismissText);
        settingsButton = findViewById(R.id.screensaverEditButton);
        fabLayout = findViewById(R.id.screensaverFabLayout);
        darkImage = findViewById(R.id.screensaverNightImage);
        lightImage = findViewById(R.id.screensaverDayImage);
        dateText = findViewById(R.id.screensaverDateDayText);
        salutationText = findViewById(R.id.screensaverSalutationText);

        fabLayout.setOnClickListener(view -> toggleNightMode());
        rootLayout.setOnClickListener(view ->{
            if(isOptionsOpen){
                startActivity(new Intent(this, SoYouADeveloperHuh.class));
            } else {
                showOptionsDisplay();
            }
        });

        settingsButton.setOnClickListener(view -> {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            settingsIntent.setAction(ACTION_SCREENSAVER_EDIT);
                startActivityForResult(settingsIntent, 1012);
        });

        TickTrackDatabase tickTrackDatabase = new TickTrackDatabase(this);
        clockStyle = tickTrackDatabase.getScreenSaverClock();

        layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater = LayoutInflater.from(ScreensaverActivity.this);

        clockLayout = (ConstraintLayout) findViewById(R.id.clockContainer);
        setupClock(clockStyle);

    }
    private void setupClock(int style) {
        clockLayout.removeAllViews();
        final  View v;
        if(style==1){
            v = layoutInflater.inflate(R.layout.tick_track_clock_widget1, null);
        } else if(style==2){
            v = layoutInflater.inflate(R.layout.tick_track_clock_widget2, null);
        } else if(style==3){
            v = layoutInflater.inflate(R.layout.tick_track_clock_widget3, null);
        } else if(style==4){
            v = layoutInflater.inflate(R.layout.tick_track_clock_widget4, null);
        } else if(style==5){
            v = layoutInflater.inflate(R.layout.tick_track_clock_widget5, null);
        } else if(style==6){
            v = layoutInflater.inflate(R.layout.tick_track_clock_widget6, null);
        } else {
            v = layoutInflater.inflate(R.layout.tick_track_clock_widget1, null);
        }
        if(isNightMode){
            v.animate().setDuration(FADE_DURATION).alpha(DARK_MODE_ALPHA).start();
        } else {
            v.animate().setDuration(FADE_DURATION).alpha(LIGHT_MODE_ALPHA).start();
        }
        clockLayout.addView(v);
    }

    @Override
    protected void onPause() {
        super.onPause();
        optionsDisplayHandler.removeCallbacks(optionsDisplayRunnable);
        unregisterReceiver(timeChangeReceiver);
        cancelScreensaverAlarms();
    }

    private void setupScreensaverAlarms(){
        Calendar dayChange = Calendar.getInstance();
        dayChange.setTimeInMillis(System.currentTimeMillis());

        dayChange.set(Calendar.HOUR_OF_DAY, 0);
        dayChange.set(Calendar.MINUTE, 0);
        dayChange.set(Calendar.SECOND, 0);
        if(Calendar.getInstance().after(dayChange)){
            dayChange.add(Calendar.DATE, 1);
        }

        AlarmManager dayAlarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent dayTimeChange = new Intent(ScreensaverActivity.ACTION_TIME_CHANGE_ANALOG);
        PendingIntent dayPendIntent = PendingIntent.getBroadcast(this, 500, dayTimeChange, 0);
        dayAlarm.setRepeating(
                AlarmManager.RTC_WAKEUP,
                dayChange.getTimeInMillis(),
                24*60*60*1000L,
                dayPendIntent
        );

        Calendar morningAlarm = Calendar.getInstance();
        morningAlarm.setTimeInMillis(System.currentTimeMillis());

        morningAlarm.set(Calendar.HOUR_OF_DAY, 5);
        morningAlarm.set(Calendar.MINUTE, 0);
        morningAlarm.set(Calendar.SECOND, 0);
        if(Calendar.getInstance().after(morningAlarm)){
            morningAlarm.add(Calendar.DATE, 1);
        }

        AlarmManager morningAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent morningIntent = new Intent(ScreensaverActivity.ACTION_TIME_CHANGE_ANALOG);
        PendingIntent morningPendIntent = PendingIntent.getBroadcast(this, 505, morningIntent, 0);
        morningAlarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                morningAlarm.getTimeInMillis(),
                24*60*60*1000L,
                morningPendIntent
        );

        Calendar afternoonAlarm = Calendar.getInstance();
        afternoonAlarm.setTimeInMillis(System.currentTimeMillis());

        afternoonAlarm.set(Calendar.HOUR_OF_DAY, 12);
        afternoonAlarm.set(Calendar.MINUTE, 0);
        afternoonAlarm.set(Calendar.SECOND, 0);
        if(Calendar.getInstance().after(afternoonAlarm)){
            afternoonAlarm.add(Calendar.DATE, 1);
        }

        AlarmManager afternoonAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent afternoonIntent = new Intent(ScreensaverActivity.ACTION_TIME_CHANGE_ANALOG);
        PendingIntent afternoonPendIntent = PendingIntent.getBroadcast(this, 1205, afternoonIntent, 0);
        afternoonAlarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                afternoonAlarm.getTimeInMillis(),
                24*60*60*1000L,
                afternoonPendIntent
        );

        Calendar eveningAlarm = Calendar.getInstance();
        eveningAlarm.setTimeInMillis(System.currentTimeMillis());

        eveningAlarm.set(Calendar.HOUR_OF_DAY, 17);
        eveningAlarm.set(Calendar.MINUTE, 0);
        eveningAlarm.set(Calendar.SECOND, 0);
        if(Calendar.getInstance().after(eveningAlarm)){
            eveningAlarm.add(Calendar.DATE, 1);
        }

        AlarmManager eveningAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent eveningIntent = new Intent(ScreensaverActivity.ACTION_TIME_CHANGE_ANALOG);
        PendingIntent eveningPendIntent = PendingIntent.getBroadcast(this, 1705, eveningIntent, 0);
        eveningAlarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                eveningAlarm.getTimeInMillis(),
                24*60*60*1000L,
                eveningPendIntent
        );

        final IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_TIME_CHANGE_ANALOG);
        registerReceiver(timeChangeReceiver, filter);

        System.out.println("ALARM SET");

    }

    private void cancelScreensaverAlarms(){

        System.out.println("ALARM CANCELED");

        AlarmManager eveningAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent eveningIntent = new Intent(this, ScreensaverActivity.class);
        eveningIntent.setAction(ScreensaverActivity.ACTION_TIME_CHANGE_ANALOG);
        PendingIntent eveningPendIntent = PendingIntent.getBroadcast(this, 1705, eveningIntent, 0);
        eveningAlarmManager.cancel(eveningPendIntent);

        AlarmManager afternoonAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent afternoonIntent = new Intent(this, ScreensaverActivity.class);
        afternoonIntent.setAction(ScreensaverActivity.ACTION_TIME_CHANGE_ANALOG);
        PendingIntent afternoonPendIntent = PendingIntent.getBroadcast(this, 1205, afternoonIntent, 0);
        afternoonAlarmManager.cancel(afternoonPendIntent);

        AlarmManager morningAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent morningIntent = new Intent(this, ScreensaverActivity.class);
        morningIntent.setAction(ScreensaverActivity.ACTION_TIME_CHANGE_ANALOG);
        PendingIntent morningPendIntent = PendingIntent.getBroadcast(this, 505, morningIntent, 0);
        morningAlarmManager.cancel(morningPendIntent);

        AlarmManager dayAlarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent dayTimeChange = new Intent(this, ScreensaverActivity.class);
        dayTimeChange.setAction(ScreensaverActivity.ACTION_TIME_CHANGE_ANALOG);
        PendingIntent dayPendIntent = PendingIntent.getBroadcast(this, 500, dayTimeChange, 0);
        dayAlarm.cancel(dayPendIntent);
    }

    private final BroadcastReceiver timeChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("RECEIVED BROAD");
            setupTimeText();
        }
    };

}