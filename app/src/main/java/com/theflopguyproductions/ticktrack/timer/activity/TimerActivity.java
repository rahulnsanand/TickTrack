package com.theflopguyproductions.ticktrack.timer.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.SoYouADeveloperHuh;
import com.theflopguyproductions.ticktrack.application.TickTrack;
import com.theflopguyproductions.ticktrack.timer.TimerData;
import com.theflopguyproductions.ticktrack.utils.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.TickTrackThemeSetter;

import java.util.ArrayList;

public class TimerActivity extends AppCompatActivity {

    private String timerID = null;
    private ArrayList<TimerData> timerDataArrayList = new ArrayList<>();
    private int currentPosition = 0;
    private ConstraintLayout timerActivityToolbar, timerActivityRootLayout;
    private Chronometer timerChronometer;
    private ProgressBar timerProgressBar;
    private FloatingActionButton playPauseFAB, resetFAB;
    private ImageButton backButton, editButton, deleteButton;
    private TextView labelTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        timerID = getIntent().getStringExtra("timerID");
        timerDataArrayList = TickTrackDatabase.retrieveTimerList(this);
        initVariables();
        if(timerID!=null){
            currentPosition = getCurrentPosition(timerID);
            TickTrackThemeSetter.timerActivityTheme(this, timerActivityToolbar, timerDataArrayList.get(currentPosition).getTimerFlag() , timerActivityRootLayout, timerChronometer);

            prefixValues();

            setOnClickListeners();

        } else {
            onBackPressed();
        }
    }

    private void prefixValues() {

        if(!timerDataArrayList.get(currentPosition).getTimerLabel().equals("Set label")){
            labelTextView.setText(timerDataArrayList.get(currentPosition).getTimerLabel());
        } else {
            labelTextView.setText("Timer");
        }


    }

    private void setOnClickListeners() {

        backButton.setOnClickListener(view -> onBackPressed());

    }

    private void initVariables() {
        timerActivityRootLayout = findViewById(R.id.timerActivityRootLayout);
        timerActivityToolbar = findViewById(R.id.timerActivityToolbarLayout);
        timerChronometer = findViewById(R.id.timerActivityTimeLeftTextView);
        timerProgressBar = findViewById(R.id.timerActivityTimerProgressBar);
        playPauseFAB = findViewById(R.id.timerActivityPlayPauseFAB);
        resetFAB = findViewById(R.id.timerActivityResetFAB);
        backButton = findViewById(R.id.timerActivityBackImageButton);
        editButton = findViewById(R.id.timerActivityEditImageButton);
        deleteButton = findViewById(R.id.timerActivityDeleteImageButton);
        labelTextView = findViewById(R.id.timerActivityLabelTextView);

        timerChronometer.setTypeface(ResourcesCompat.getFont(this,R.font.montserrat_medium));

        timerProgressBar.setProgress(50);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this,SoYouADeveloperHuh.class);
        intent.putExtra("FragmentID", 2);
        startActivity(intent);
    }

    private int getCurrentPosition(String timerID){
        for(int i =0; i < timerDataArrayList.size(); i++){
            if(timerDataArrayList.get(i).getTimerID().equals(timerID)){
                return i;
            }
        }
        return 0;
    }

}