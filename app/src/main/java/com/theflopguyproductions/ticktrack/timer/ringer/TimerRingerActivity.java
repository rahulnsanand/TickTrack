package com.theflopguyproductions.ticktrack.timer.ringer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;

import com.theflopguyproductions.ticktrack.R;


public class TimerRingerActivity extends AppCompatActivity {

    private ConstraintLayout rootLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_ringer);

        rootLayout = findViewById(R.id.timerRingActivityRootLayout);




    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }

}