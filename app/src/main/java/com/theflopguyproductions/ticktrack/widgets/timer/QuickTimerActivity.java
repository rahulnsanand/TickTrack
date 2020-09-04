package com.theflopguyproductions.ticktrack.widgets.timer;

import android.os.Bundle;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.theflopguyproductions.ticktrack.R;

public class QuickTimerActivity extends AppCompatActivity {

    private Button cancelButton, startButton;
    private NumberPicker hourPicker, minutePicker, secondPicker;
    private TextView hourText, minuteText, secondText;

    public static final String ACTION_CREATE_CUSTOM_TIMER = "ACTION_CREATE_CUSTOM_TIMER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_timer);
    }
}