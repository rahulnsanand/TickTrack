package com.theflopguyproductions.ticktrack.ui.home.activity.alarm;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.theflopguyproductions.ticktrack.R;

public class AlarmCreator extends AppCompatActivity {

    ImageButton alarmBack;
    Button alarmSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_creator);

        alarmBack = (ImageButton) findViewById(R.id.alarmActivityBackButton);
        alarmBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        alarmSave = (Button) findViewById(R.id.alarmSaveButton);

    }
}