package com.theflopguyproductions.ticktrack.ui.counter.activity.counter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.ui.counter.CounterData;
import com.theflopguyproductions.ticktrack.ui.utils.swipebutton.OnActiveListener;
import com.theflopguyproductions.ticktrack.ui.utils.swipebutton.SwipeButton;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.ArrayList;

public class CounterActivity extends AppCompatActivity {


    private SwipeButton plusButton;
    private SwipeButton minusButton;
    private TextView CounterText;
    private int currentCount;
    private Vibrator hapticFeed;
    private ArrayList<CounterData> counterDataArrayList;
    private int currentPosition;
    private ImageButton backButton;
    private TextView counterLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter);


        CounterText = findViewById(R.id.counterText);
        backButton = findViewById(R.id.alarmActivityBackButton);
        counterLabel = findViewById(R.id.counterLabelToolbar);
        plusButton = findViewById(R.id.plusbtn);
        minusButton = findViewById(R.id.minusbtn);
        hapticFeed = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);

        SharedPreferences sharedPreferences = getSharedPreferences("TickTrackData", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("CounterData", null);
        Type type = new TypeToken<ArrayList<CounterData>>() {}.getType();
        counterDataArrayList = gson.fromJson(json, type);


        currentPosition = getIntent().getIntExtra("currentCounterPosition",0);
        currentCount = counterDataArrayList.get(currentPosition).getCountValue();
        counterLabel.setText(counterDataArrayList.get(currentPosition).getCounterLabel());

        CounterText.setText(""+counterDataArrayList.get(currentPosition).getCountValue());

        backButton.setOnClickListener(view -> finish());

        plusButton.setOnActiveListener(() -> {
            currentCount+=1;
            counterDataArrayList.get(currentPosition).setCountValue(currentCount);
            counterDataArrayList.get(currentPosition).setTimestamp(new Timestamp(System.currentTimeMillis()));
            hapticFeed.vibrate(50);
            CounterText.setText(""+counterDataArrayList.get(currentPosition).getCountValue());
            storeData();
        });

        minusButton.setOnActiveListener(() -> {
            if(currentCount>=1){
                hapticFeed.vibrate(50);
                currentCount-=1;
                counterDataArrayList.get(currentPosition).setCountValue(currentCount);
                CounterText.setText(""+counterDataArrayList.get(currentPosition).getCountValue());
                storeData();
            }
        });

    }

    private void storeData() {
        SharedPreferences sharedPreferences = getSharedPreferences("TickTrackData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(counterDataArrayList);
        editor.putString("CounterData", json);
        editor.apply();

    }
}