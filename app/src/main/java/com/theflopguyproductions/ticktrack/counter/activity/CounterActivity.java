package com.theflopguyproductions.ticktrack.counter.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.counter.CounterData;
import com.theflopguyproductions.ticktrack.ui.utils.swipebutton.SwipeButton;
import com.theflopguyproductions.ticktrack.utils.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.TickTrackThemeSetter;

import java.sql.Timestamp;
import java.util.ArrayList;

public class CounterActivity extends AppCompatActivity {

    private SwipeButton plusButton, minusButton;
    private Button plusButtonBig, minusButtonBig;
    private Switch buttonSwitch;
    private ScrollView counterActivityScrollView;
    private TextView CounterText, counterLabel, counterSwitchMode;
    private int currentCount, currentPosition;
    private ArrayList<CounterData> counterDataArrayList;
    ConstraintLayout toolbar, rootLayout;
    int flagColor;
    //private ImageButton backButton;


    @Override
    protected void onResume() {
        super.onResume();
        flagColor = counterDataArrayList.get(currentPosition).getCounterFlag();
        TickTrackThemeSetter.counterActivityTheme(this,toolbar, rootLayout, flagColor, plusButtonBig, minusButtonBig, plusButton, minusButton, counterActivityScrollView, counterSwitchMode, buttonSwitch);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter);


        CounterText = findViewById(R.id.counterActivityValueTextView);
        //backButton = findViewById(R.id.alarmActivityBackButton);
        counterLabel = findViewById(R.id.counterActivityLabelTextView);
        plusButton = findViewById(R.id.plusbtn);
        minusButton = findViewById(R.id.minusbtn);
        plusButtonBig = findViewById(R.id.plusButton);
        minusButtonBig =findViewById(R.id.minusButton);
        buttonSwitch = findViewById(R.id.counterActivityButtonSwitch);
        buttonSwitch.setChecked(false);
        counterSwitchMode = findViewById(R.id.counterActivitySwitchModeTextView);
        counterSwitchMode.setText("Swipe mode");
        counterActivityScrollView = findViewById(R.id.counterActivityScrollView);

        toolbar = findViewById(R.id.counterActivityToolbar);
        rootLayout = findViewById(R.id.counterActivityLayout);
        currentPosition = getIntent().getIntExtra("currentCounterPosition",0);

        counterDataArrayList = TickTrackDatabase.retrieveCounterList(this);

        flagColor = counterDataArrayList.get(currentPosition).getCounterFlag();

        TickTrackThemeSetter.counterActivityTheme(this,toolbar, rootLayout, flagColor, plusButtonBig, minusButtonBig, plusButton, minusButton, counterActivityScrollView, counterSwitchMode, buttonSwitch);

        currentCount = counterDataArrayList.get(currentPosition).getCounterValue();
        counterLabel.setText(counterDataArrayList.get(currentPosition).getCounterLabel());

        CounterText.setText(""+counterDataArrayList.get(currentPosition).getCounterValue());

        //backButton.setOnClickListener(view -> finish());

        buttonSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            changeButtonVisibility(compoundButton);
        });

        setOnClickListeners();
    }

    private void changeButtonVisibility(CompoundButton compoundButton){
        if(compoundButton.isChecked()){
            counterSwitchMode.setText("Click mode");
            plusButtonBig.setVisibility(View.VISIBLE);
            minusButtonBig.setVisibility(View.VISIBLE);
            plusButton.setVisibility(View.INVISIBLE);
            minusButton.setVisibility(View.INVISIBLE);
        } else {
            counterSwitchMode.setText("Swipe mode");
            plusButtonBig.setVisibility(View.INVISIBLE);
            minusButtonBig.setVisibility(View.INVISIBLE);
            plusButton.setVisibility(View.VISIBLE);
            minusButton.setVisibility(View.VISIBLE);
        }
    }

    private void setOnClickListeners(){
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
        plusButtonBig.setOnClickListener(view -> {
            currentCount+=1;
            counterDataArrayList.get(currentPosition).setCounterValue(currentCount);
            counterDataArrayList.get(currentPosition).setCounterTimestamp(new Timestamp(System.currentTimeMillis()));
            CounterText.setText(""+counterDataArrayList.get(currentPosition).getCounterValue());
            TickTrackDatabase.storeCounterList(counterDataArrayList, this);
        });

        minusButtonBig.setOnClickListener(view -> {
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