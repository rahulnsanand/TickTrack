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
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;

public class ScreensaverActivity extends AppCompatActivity {

    private ConstraintLayout rootLayout;
    private int dialogClock, hourHandClock, minuteHandClock;
    LayoutInflater layoutInflater;
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

        layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater = LayoutInflater.from(ScreensaverActivity.this);

        rr = (ConstraintLayout) findViewById(R.id.clockContainer);
        setupClock(style);

    }

    private void setupClock(int style) {
        rr.removeAllViews();
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
        rr.addView(v);
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }


}