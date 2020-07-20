package com.theflopguyproductions.ticktrack.ui.home.activity.alarm;

import androidx.appcompat.app.AppCompatActivity;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.view.WindowManager;
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1)
        {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
            KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
            if(keyguardManager!=null)
                keyguardManager.requestDismissKeyguard(this, null);
        }
        else
        {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        }
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