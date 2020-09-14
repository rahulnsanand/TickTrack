package com.theflopguyproductions.ticktrack.screensaver;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.SoYouADeveloperHuh;
import com.theflopguyproductions.ticktrack.ui.utils.AnalogClock;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;

public class ScreensaverActivity extends AppCompatActivity {

    private ConstraintLayout rootLayout;
    private AnalogClock analogClock;
    private int dialogClock, hourHandClock, minuteHandClock;
    LayoutInflater linf;
    ConstraintLayout rr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_screensaver);
        rootLayout = findViewById(R.id.screenSaverRootLayout);

        rootLayout.setOnClickListener(view ->
                startActivity(new Intent(this, SoYouADeveloperHuh.class)));

        getWindow().setStatusBarColor(getResources().getColor(R.color.Accent));

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                |  View.SYSTEM_UI_FLAG_LOW_PROFILE);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        TickTrackDatabase tickTrackDatabase = new TickTrackDatabase(this);
        int style = tickTrackDatabase.getScreenSaverClock();

        linf = (LayoutInflater) getApplicationContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        linf = LayoutInflater.from(ScreensaverActivity.this);

        rr = (ConstraintLayout) findViewById(R.id.clockContainer);

        final  View v = linf.inflate(R.layout.tick_track_clock_widget6, null);

        rr.removeAllViews();
        rr.addView(v);

//        setupClock(style);

    }

    private void setupClock(int style) {
        if(style==1){

            analogClock = new AnalogClock(this);
            analogClock.setupClockDrawables(R.drawable.white_hour_hand_unordinary, R.drawable.white_minute_hand_unordinary, R.drawable.transparent_dial_unordinary);
        } else if(style==2){

            analogClock = new AnalogClock(this);
            analogClock.setupClockDrawables(R.drawable.white_hour_hand_oxygeny, R.drawable.white_minute_hand_oxygeny, R.drawable.white_dial_oxygeny);
        } else if(style==3){

            analogClock = new AnalogClock(this);
            analogClock.setupClockDrawables(R.drawable.white_hour_hand_truly_minimal, R.drawable.white_minute_hand_truly_minimal, R.drawable.white_dial_truly_minimal);
        } else if(style==4){

            analogClock = new AnalogClock(this);
            analogClock.setupClockDrawables(R.drawable.white_hour_hand_simplistic, R.drawable.white_minute_hand_simplistic, R.drawable.white_dial_simplistic);
        } else if(style==5){

            analogClock = new AnalogClock(this);
            analogClock.setupClockDrawables(R.drawable.white_hour_hand_roman, R.drawable.white_minute_hand_roman, R.drawable.white_dial_roman);
        } else if(style==6){

            analogClock = new AnalogClock(this);
            analogClock.setupClockDrawables(R.drawable.white_hour_hand_funky, R.drawable.white_minute_hand_funky, R.drawable.white_dial_funky);
        } else {

            analogClock = new AnalogClock(this);
            analogClock.setupClockDrawables(R.drawable.white_hour_hand_unordinary, R.drawable.white_minute_hand_unordinary, R.drawable.transparent_dial_unordinary);
        }
        analogClock.onTimeChanged();
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }


}