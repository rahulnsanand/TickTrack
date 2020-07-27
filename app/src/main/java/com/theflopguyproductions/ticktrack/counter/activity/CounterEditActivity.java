package com.theflopguyproductions.ticktrack.counter.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import com.theflopguyproductions.ticktrack.R;

public class CounterEditActivity extends AppCompatActivity {

    private int currentPosition;
    private ImageButton backButton, saveButton;
    private ImageView counterFlag;
    private Switch counterButtonSwitch, counterNotificationSwitch;
    private TextView counterLabel, counterValue, counterMilestone, counterButtonMode;
    private TextView counterLabelTitle, counterValueTitle, counterMilestoneTitle, counterButtonModeTitle, counterFlagTitle, counterNotificationTitle;
    private Button deleteButton;
    private ConstraintLayout counterLabelLayout, counterValueLayout, counterMilestoneLayout, counterFlagLayout, counterButtonModeLayout, counterNotificationLayout, counterEditRootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter_edit);
        currentPosition = getIntent().getIntExtra("CurrentPosition",0);

        backButton = findViewById(R.id.counterEditActivityBackButton);
        saveButton = findViewById(R.id.counterEditActivitySaveButton);
        counterFlag = findViewById(R.id.counterEditActivityFlagImageView);
        counterButtonSwitch = findViewById(R.id.counterEditActivityButtonModeSwitch);
        counterNotificationSwitch = findViewById(R.id.counterEditActivityNotificationSwitch);
        counterLabel = findViewById(R.id.counterEditActivityLabelTextView);
        counterValue = findViewById(R.id.counterEditActivityValueTextView);
        counterMilestone = findViewById(R.id.counterEditActivitySignificantTextView);
        counterButtonMode = findViewById(R.id.counterEditActivityButtonModeTextView);
        deleteButton = findViewById(R.id.counterEditActivityDeleteButton);
        counterLabelTitle = findViewById(R.id.counterEditActivityLabelTitle);
        counterValueTitle = findViewById(R.id.counterEditActivityValueTitle);
        counterMilestoneTitle = findViewById(R.id.counterEditActivitySignificantTitle);
        counterButtonModeTitle = findViewById(R.id.counterEditActivityButtonModeTitle);
        counterFlagTitle = findViewById(R.id.counterEditActivityFlagTitle);
        counterNotificationTitle = findViewById(R.id.counterEditActivityNotificationTitle);
        counterLabelLayout = findViewById(R.id.counterEditActivityLabelLayout);
        counterValueLayout = findViewById(R.id.counterEditActivityValueLayout);
        counterMilestoneLayout = findViewById(R.id.counterEditActivitySignificantLayout);
        counterFlagLayout = findViewById(R.id.counterEditActivityFlagLayout);
        counterButtonModeLayout = findViewById(R.id.counterEditActivityButtonModeLayout);
        counterNotificationLayout = findViewById(R.id.counterEditActivityNotificationLayout);
        counterEditRootLayout = findViewById(R.id.counterEditActivityRootLayout);


        counterLabelLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        counterValueLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        counterMilestoneLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        counterFlagLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        counterButtonModeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        counterNotificationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
            }
        });

    }
}