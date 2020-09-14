package com.theflopguyproductions.ticktrack.screensaver;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
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

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.SoYouADeveloperHuh;
import com.theflopguyproductions.ticktrack.settings.SettingsActivity;
import com.theflopguyproductions.ticktrack.ui.utils.TickTrackAnimator;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;

public class ScreensaverActivity extends AppCompatActivity {

    private static int DISPLAY_DURATION = 2500;
    private static final float DARK_MODE_ALPHA = 0.2f;
    private static final float LIGHT_MODE_ALPHA = 1f;
    private static final int FADE_DURATION = 200;

    public static final String ACTION_SCREENSAVER_EDIT = "ACTION_SCREENSAVER_EDIT";

    private ConstraintLayout rootLayout;
    private LayoutInflater layoutInflater;
    private ConstraintLayout clockLayout;
    private ConstraintLayout buttonLayout;
    private Handler optionsDisplayHandler = new Handler();
    private TextView dismissTextHelper;
    private Button settingsButton;
    private ConstraintLayout fabLayout;
    private ImageView darkImage, lightImage;

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

    @Override
    protected void onResume() {
        super.onResume();

        TickTrackAnimator.fabImageDissolve(lightImage);
        TickTrackAnimator.fabImageReveal(darkImage);
        isNightMode=false;

        isOptionsOpen = false;
        DISPLAY_DURATION = 500;
        showOptionsDisplay();

        getWindow().setStatusBarColor(getResources().getColor(R.color.Accent));
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
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }


}