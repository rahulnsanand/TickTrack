package com.theflopguyproductions.ticktrack.counter.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.SoYouADeveloperHuh;
import com.theflopguyproductions.ticktrack.counter.CounterData;
import com.theflopguyproductions.ticktrack.ui.utils.swipebutton.SwipeButton;
import com.theflopguyproductions.ticktrack.utils.TickTrackDatabase;

import java.sql.Timestamp;
import java.util.ArrayList;

public class CounterActivity extends AppCompatActivity {

    private SwipeButton plusButton;
    private SwipeButton minusButton;
    private TextView CounterText;
    private int currentCount;
    private ArrayList<CounterData> counterDataArrayList;
    private int currentPosition;
    //private ImageButton backButton;
    private TextView counterLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter);


        CounterText = findViewById(R.id.counterActivityValueTextView);
        //backButton = findViewById(R.id.alarmActivityBackButton);
        counterLabel = findViewById(R.id.counterActivityLabelTextView);
        plusButton = findViewById(R.id.plusbtn);
        minusButton = findViewById(R.id.minusbtn);

        counterDataArrayList = TickTrackDatabase.retrieveCounterList(this);


        currentPosition = getIntent().getIntExtra("currentCounterPosition",0);
        currentCount = counterDataArrayList.get(currentPosition).getCounterValue();
        counterLabel.setText(counterDataArrayList.get(currentPosition).getCounterLabel());

        CounterText.setText(""+counterDataArrayList.get(currentPosition).getCounterValue());

        //backButton.setOnClickListener(view -> finish());

        plusButton.setOnActiveListener(() -> {
            currentCount+=1;
            counterDataArrayList.get(currentPosition).setCounterValue(currentCount);
            counterDataArrayList.get(currentPosition).setCounterTimestamp(new Timestamp(System.currentTimeMillis()));
            CounterText.setText(""+counterDataArrayList.get(currentPosition).getCounterValue());
            TickTrackDatabase.storeCounterList(counterDataArrayList, this);
        });

        minusButton.setOnActiveListener(() -> {
            if(currentCount>=1){
                currentCount-=1;
                counterDataArrayList.get(currentPosition).setCounterValue(currentCount);
                CounterText.setText(""+counterDataArrayList.get(currentPosition).getCounterValue());
                TickTrackDatabase.storeCounterList(counterDataArrayList, this);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}