package com.theflopguyproductions.ticktrack.ui.home.activity.alarm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.theflopguyproductions.ticktrack.MainActivityToChange;
import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.utils.TickTrackAlarmManager;

public class DismissAlarmActivity extends AppCompatActivity {

    Button dismissAlarmButton;
    TextView alarmTimeView, alarmLabelView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dismiss_alarm);

        dismissAlarmButton = findViewById(R.id.dismissAlarmButton);
        alarmLabelView = findViewById(R.id.alarmLabel);
        alarmTimeView = findViewById(R.id.alarmTime);

        dismissAlarmButton.setOnClickListener(view -> {
            TickTrackAlarmManager.stopAlarm();
            startActivity(new Intent(DismissAlarmActivity.this, MainActivityToChange.class));
        });

    }
}