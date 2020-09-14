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
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.SoYouADeveloperHuh;
import com.theflopguyproductions.ticktrack.settings.SettingsActivity;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;

public class ScreensaverActivity extends AppCompatActivity {

    private ConstraintLayout rootLayout;
    private LayoutInflater layoutInflater;
    private ConstraintLayout clockLayout;
    private ConstraintLayout buttonLayout;
    private Handler optionsDisplayHandler;
    private TextView dismissTextHelper;
    private Button settingsButton;

    private Runnable optionsDisplayRunnable = new Runnable() {
        long currentTime = SystemClock.elapsedRealtime();
        @Override
        public void run() {
            long loopTime = SystemClock.elapsedRealtime();
            long difference = loopTime-currentTime;
            if(difference<3000){
                optionsDisplayHandler.post(optionsDisplayRunnable);
            } else {
                hideOptionsDisplay();
                optionsDisplayHandler.removeCallbacks(optionsDisplayRunnable);
            }
        }
    };

    private boolean isOptionsOpen = false;
    private void hideOptionsDisplay() {
        if(isOptionsOpen){
            dismissTextHelper.setVisibility(View.GONE);
            buttonLayout.setVisibility(View.GONE);
            isOptionsOpen = false;
            optionsDisplayHandler.removeCallbacks(optionsDisplayRunnable);
        }
    }
    private void showOptionsDisplay() {
        if(!isOptionsOpen){
            optionsDisplayHandler.post(optionsDisplayRunnable);
            buttonLayout.setVisibility(View.VISIBLE);
            dismissTextHelper.setVisibility(View.VISIBLE);
            isOptionsOpen = true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_screensaver);
        rootLayout = findViewById(R.id.screenSaverRootLayout);
        buttonLayout = findViewById(R.id.screensaverButtonLayout);
        dismissTextHelper = findViewById(R.id.screensaverDismissText);
        settingsButton = findViewById(R.id.screensaverEditButton);

        rootLayout.setOnClickListener(view ->{
            if(isOptionsOpen){
                startActivity(new Intent(this, SoYouADeveloperHuh.class));
            } else {
                showOptionsDisplay();
            }
        });

        settingsButton.setOnClickListener(view -> {
                startActivityForResult(new Intent(this, SettingsActivity.class), 1012);
        });

        getWindow().setStatusBarColor(getResources().getColor(R.color.Accent));

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                |  View.SYSTEM_UI_FLAG_LOW_PROFILE);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        TickTrackDatabase tickTrackDatabase = new TickTrackDatabase(this);
        int style = tickTrackDatabase.getScreenSaverClock();

        layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater = LayoutInflater.from(ScreensaverActivity.this);

        clockLayout = (ConstraintLayout) findViewById(R.id.clockContainer);
        setupClock(style);

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