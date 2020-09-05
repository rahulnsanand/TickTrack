package com.theflopguyproductions.ticktrack.widgets.timer;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackTimerDatabase;
import com.theflopguyproductions.ticktrack.utils.helpers.TickTrackThemeSetter;

public class QuickTimerActivity extends AppCompatActivity {

    private Button cancelButton, startButton, customTimerButton;
    private NumberPicker hourPickerLight, minutePickerLight, secondPickerLight;
    private NumberPicker hourPickerDark, minutePickerDark, secondPickerDark;
    private TextView hourText, minuteText, secondText;
    private FloatingActionButton oneMinuteButton, twoMinuteButton, fiveMinuteButton, tenMinuteButton;
    private ConstraintLayout rootLayout, customLayout, optionsLayout;
    private boolean isCustomTimerOn = false;
    private String receivedAction = null;
    private TickTrackDatabase tickTrackDatabase;
    private TickTrackTimerDatabase tickTrackTimerDatabase;

    public static final String ACTION_CREATE_CUSTOM_TIMER = "ACTION_CREATE_CUSTOM_TIMER";

    @Override
    protected void onResume() {
        super.onResume();
        TickTrackThemeSetter.quickTimerActivityTheme(this, tickTrackDatabase, hourPickerLight, minutePickerLight, secondPickerLight, hourPickerDark, minutePickerDark, secondPickerDark,
                hourText, minuteText, secondText, rootLayout);
    }

    private void initVariables() {
        cancelButton = findViewById(R.id.quickTimerActivityCancelButton);
        startButton = findViewById(R.id.quickTimerActivityCustomCreateButton);
        customTimerButton = findViewById(R.id.quickTimerActivityEnableCustomOptionsButton);
        hourPickerLight = findViewById(R.id.quickTimerActivityHourPickerLight);
        minutePickerLight = findViewById(R.id.quickTimerActivityMinutePickerLight);
        secondPickerLight = findViewById(R.id.quickTimerActivitySecondPickerLight);
        hourPickerDark = findViewById(R.id.quickTimerActivityHourPickerDark);
        minutePickerDark = findViewById(R.id.quickTimerActivityMinutePickerDark);
        secondPickerDark = findViewById(R.id.quickTimerActivitySecondPickerDark);
        hourText = findViewById(R.id.quickTimerActivityHourText);
        minuteText = findViewById(R.id.quickTimerActivityMinuteText);
        secondText = findViewById(R.id.quickTimerActivitySecondText);
        oneMinuteButton = findViewById(R.id.quickTimerActivityOneMinuteButton);
        twoMinuteButton = findViewById(R.id.quickTimerActivityTwoMinuteButton);
        fiveMinuteButton = findViewById(R.id.quickTimerActivityFiveMinuteButton);
        tenMinuteButton = findViewById(R.id.quickTimerActivityTenMinuteButton);
        rootLayout = findViewById(R.id.quickTimerActivityRootLayout);
        customLayout = findViewById(R.id.quickTimerActivityCustomLayout);
        optionsLayout = findViewById(R.id.quickTimerActivityQuickOptionsLayout);

        tickTrackDatabase = new TickTrackDatabase(this);
        tickTrackTimerDatabase = new TickTrackTimerDatabase(this);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_timer);
        initVariables();
        setupClickListeners();
        receivedAction = getIntent().getAction();
        if(ACTION_CREATE_CUSTOM_TIMER.equals(receivedAction)){
            toggleCustomTimer();
        }

    }

    private void setupClickListeners() {
        cancelButton.setOnClickListener(view -> finish());
        customTimerButton.setOnClickListener(view -> toggleCustomTimer());
    }

    private void toggleCustomTimer() {
        if(isCustomTimerOn){
            customTimerButton.setEnabled(false);
            optionsLayout.setVisibility(View.VISIBLE);
            customTimerButton.setText("Show Custom QuickTimer Option");
            startButton.setVisibility(View.GONE);
            customLayout.setVisibility(View.GONE);
            customTimerButton.setEnabled(true);
            isCustomTimerOn = false;
        } else {
            customTimerButton.setEnabled(false);
            optionsLayout.setVisibility(View.GONE);
            customTimerButton.setText("Show QuickTimer Options");
            startButton.setVisibility(View.VISIBLE);
            customLayout.setVisibility(View.VISIBLE);
            customTimerButton.setEnabled(true);
            isCustomTimerOn = true;
        }
    }

    private void createTimer() {

//        TimerData timerData = new TimerData();
//        timerData.setTimerLastEdited(System.currentTimeMillis());
//        timerData.setTimerHour(pickedHour);
//        timerData.setTimerMinute(pickedMinute);
//        timerData.setTimerSecond(pickedSecond);
//        timerData.setTimerID(UniqueIdGenerator.getUniqueTimerID());
//        timerData.setTimerIntID(UniqueIdGenerator.getUniqueIntegerTimerID());
//        timerData.setQuickTimer(true);
//        timerData.setTimerStartTimeInMillis(-1);
//        timerData.setTimerTotalTimeInMillis(TimeAgo.getTimerDataInMillis(pickedHour,pickedMinute,pickedSecond,0));
//        timerData.setTimerPause(false);
//        timerData.setTimerOn(true);
//        timerDataArrayList.add(0,timerData);
//        tickTrackDatabase.storeTimerList(timerDataArrayList);
//
//        Intent timerIntent = new Intent(activity, TimerActivity.class);
//        timerIntent.setAction(TimerActivity.ACTION_TIMER_NEW_ADDITION);
//        timerIntent.putExtra("timerID", timerData.getTimerID());
//        startActivity(timerIntent);

    }

}