package com.theflopguyproductions.ticktrack.counter.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.counter.CounterData;
import com.theflopguyproductions.ticktrack.dialogs.DeleteCounterFromActivity;
import com.theflopguyproductions.ticktrack.dialogs.SingleInputDialog;
import com.theflopguyproductions.ticktrack.utils.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.TickTrackThemeSetter;

import java.util.ArrayList;

public class CounterEditActivity extends AppCompatActivity {

    private int currentPosition;
    private ImageButton backButton, saveButton;
    private ImageView counterFlag;
    private Switch counterButtonSwitch, counterNotificationSwitch;
    private TextView counterLabel, counterValue, counterMilestone, counterButtonMode;
    private TextView counterLabelTitle, counterValueTitle, counterMilestoneTitle, counterButtonModeTitle, counterFlagTitle, counterNotificationTitle, counterNotificationDetail, counterMilestoneDetail;
    private Button deleteButton;
    private ConstraintLayout counterLabelLayout, counterValueLayout, counterMilestoneLayout, counterFlagLayout, counterButtonModeLayout, counterNotificationLayout, counterEditRootLayout, counterEditToolbarLayout;
    private ArrayList<CounterData> counterDataArrayList = new ArrayList<>();
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter_edit);

        counterDataArrayList = TickTrackDatabase.retrieveCounterList(this);
        currentPosition = getIntent().getIntExtra("CurrentPosition",0);
        int flagColor = counterDataArrayList.get(currentPosition).getCounterFlag();
        activity = this;

        initVariables();

        TickTrackThemeSetter.counterEditActivityTheme(this, counterLabelLayout, counterValueLayout, counterMilestoneLayout, counterFlagLayout,
                counterButtonModeLayout, counterNotificationLayout, counterEditRootLayout, counterEditToolbarLayout,
                counterLabel, counterValue, counterMilestone, counterButtonMode, counterNotificationDetail, counterMilestoneDetail, flagColor);

        setupPrefixValues();

        setupOnClickListeners();

        setupOnCheckedListeners();

    }

    private void setupOnCheckedListeners() {
        counterNotificationSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            if(compoundButton.isChecked()){
                counterDataArrayList.get(currentPosition).setCounterPersistentNotification(true);
            } else {
                counterDataArrayList.get(currentPosition).setCounterPersistentNotification(false);
            }
            TickTrackDatabase.storeCounterList(counterDataArrayList, activity);
        });
        counterButtonSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            if(compoundButton.isChecked()){
                counterDataArrayList.get(currentPosition).setCounterSwipeMode(true);
            } else {
                counterDataArrayList.get(currentPosition).setCounterSwipeMode(false);
            }
            TickTrackDatabase.storeCounterList(counterDataArrayList, activity);
        });
    }

    private void initVariables() {
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
        counterEditToolbarLayout = findViewById(R.id.counterEditActivityToolBarLayout);
        counterNotificationDetail = findViewById(R.id.counterEditActivityNotificationDetailTextView);
        counterMilestoneDetail = findViewById(R.id.counterEditActivitySignificantDescription);
    }

    private void setupPrefixValues() {
        setFlagColor(counterDataArrayList.get(currentPosition).getCounterFlag());

        counterLabel.setText(counterDataArrayList.get(currentPosition).getCounterLabel());
        counterValue.setText(""+counterDataArrayList.get(currentPosition).getCounterValue());
        if(counterDataArrayList.get(currentPosition).isCounterSignificantExist()){
            counterMilestone.setText(counterDataArrayList.get(currentPosition).getCounterSignificantCount()+"");
        } else {
            counterMilestone.setText("NA");
        }
        counterButtonSwitch.setChecked(counterDataArrayList.get(currentPosition).isCounterSwipeMode());
        if(counterDataArrayList.get(currentPosition).isCounterSwipeMode()==true){
            counterButtonMode.setText("Click mode");
        } else {
            counterButtonMode.setText("Swipe mode");
        }
        counterNotificationSwitch.setChecked(counterDataArrayList.get(currentPosition).isCounterPersistentNotification());

    }
    private void setFlagColor(int flagColor) {
        if(flagColor==1){
            counterFlag.setImageResource(R.drawable.ic_flag_red);
        }
        else if(flagColor==2){
            counterFlag.setImageResource(R.drawable.ic_flag_green);
        }
        else if(flagColor==3){
            counterFlag.setImageResource(R.drawable.ic_flag_orange);
        }
        else if(flagColor==4){
            counterFlag.setImageResource(R.drawable.ic_flag_purple);
        }
        else if(flagColor==5){
            counterFlag.setImageResource(R.drawable.ic_flag_blue);
        } else {
            if(TickTrackDatabase.getThemeMode(this)==1){
                counterFlag.setImageResource(R.drawable.ic_round_flag_dark_24);
            } else {
                counterFlag.setImageResource(R.drawable.ic_round_flag_light_24);
            }
        }
    }

    private void setupOnClickListeners() {

        counterLabelLayout.setOnClickListener(view -> {
            SingleInputDialog labelDialog = new SingleInputDialog(activity, counterDataArrayList.get(currentPosition).getCounterLabel());
            labelDialog.show();
            labelDialog.inputText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT | InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE |InputType.TYPE_TEXT_FLAG_CAP_WORDS);
            labelDialog.okButton.setOnClickListener(view1 -> {
                counterLabel.setText(labelDialog.inputText.getText().toString());
                storeArrayData();
                labelDialog.dismiss();
            });
            labelDialog.cancelButton.setOnClickListener(view12 -> labelDialog.dismiss());
        });

        counterValueLayout.setOnClickListener(view -> {
            SingleInputDialog labelDialog = new SingleInputDialog(activity, ""+counterDataArrayList.get(currentPosition).getCounterValue());
            labelDialog.show();
            labelDialog.inputText.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_CLASS_NUMBER);
            labelDialog.helperText.setText("Counter value");
            labelDialog.okButton.setOnClickListener(view1 -> {
                counterValue.setText(labelDialog.inputText.getText()+"");
                storeArrayData();
                labelDialog.dismiss();
            });
            labelDialog.cancelButton.setOnClickListener(view12 -> labelDialog.dismiss());
        });

        counterMilestoneLayout.setOnClickListener(view -> {
            SingleInputDialog labelDialog = new SingleInputDialog(activity, ""+counterDataArrayList.get(currentPosition).getCounterSignificantCount());
            labelDialog.show();
            labelDialog.inputText.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_CLASS_NUMBER);
            labelDialog.helperText.setText("Milestone value");
            labelDialog.okButton.setOnClickListener(view1 -> {
                if(!labelDialog.inputText.getText().toString().equals("")){
                    if(Integer.parseInt(labelDialog.inputText.getText().toString().replaceAll("[^\\d]", ""))!=0){
                        counterMilestone.setText(labelDialog.inputText.getText()+"");
                        counterDataArrayList.get(currentPosition).setCounterSignificantExist(true);
                    } else {
                        counterMilestone.setText("NA");
                    }
                } else{
                    counterMilestone.setText("NA");
                }
                storeArrayData();
                labelDialog.dismiss();
            });
            labelDialog.cancelButton.setOnClickListener(view12 -> labelDialog.dismiss());
        });

        counterFlagLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        counterButtonModeLayout.setOnClickListener(view -> toggleButtonMode());

        counterNotificationLayout.setOnClickListener(view -> toggleNotificationSwitch());

        deleteButton.setOnClickListener(view -> {
            DeleteCounterFromActivity counterDelete = new DeleteCounterFromActivity(activity, currentPosition, counterDataArrayList.get(currentPosition).getCounterLabel());
            counterDelete.show();
        });
        backButton.setOnClickListener(view -> onBackPressed());
    }

    private void toggleNotificationSwitch() {
        if(counterNotificationSwitch.isChecked()){
            counterDataArrayList.get(currentPosition).setCounterPersistentNotification(false);
            counterNotificationSwitch.setChecked(false);
        } else {
            counterDataArrayList.get(currentPosition).setCounterPersistentNotification(true);
            counterNotificationSwitch.setChecked(true);

        }
        TickTrackDatabase.storeCounterList(counterDataArrayList, activity);
    }

    private void toggleButtonMode() {
        if(counterButtonSwitch.isChecked()){
            counterDataArrayList.get(currentPosition).setCounterSwipeMode(false);
            counterButtonSwitch.setChecked(false);
        } else {
            counterDataArrayList.get(currentPosition).setCounterSwipeMode(true);
            counterButtonSwitch.setChecked(true);
        }
        TickTrackDatabase.storeCounterList(counterDataArrayList, activity);
    }

    private void storeArrayData(){
        counterDataArrayList.get(currentPosition).setCounterLabel(counterLabel.getText().toString());
        counterDataArrayList.get(currentPosition).setCounterValue(Integer.parseInt(counterValue.getText().toString()));
        if(!counterMilestone.getText().toString().equals("NA")){
            counterDataArrayList.get(currentPosition).setCounterSignificantCount(Integer.parseInt(counterMilestone.getText().toString()));
        } else {
            counterDataArrayList.get(currentPosition).setCounterSignificantCount(0);
            counterDataArrayList.get(currentPosition).setCounterSignificantExist(false);
        }

        TickTrackDatabase.storeCounterList(counterDataArrayList, activity);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, CounterActivity.class);
        intent.putExtra("currentCounterPosition", currentPosition);
        startActivity(intent);
        finish();
    }
}