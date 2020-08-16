package com.theflopguyproductions.ticktrack.counter.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.counter.CounterData;
import com.theflopguyproductions.ticktrack.counter.notification.CounterNotificationService;
import com.theflopguyproductions.ticktrack.dialogs.DeleteCounterFromActivity;
import com.theflopguyproductions.ticktrack.dialogs.SingleInputDialog;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.helpers.TickTrackThemeSetter;

import java.util.ArrayList;

public class CounterEditActivity extends AppCompatActivity {

    TickTrackDatabase tickTrackDatabase;

//    private int currentPosition;
    private String counterID;
    private ImageButton backButton, deleteCounterButton;
    private ImageView counterFlag;
    private Switch counterButtonSwitch, counterNotificationSwitch;
    private TextView counterLabel, counterValue, counterMilestone, counterButtonMode;
    private TextView counterLabelTitle, counterValueTitle, counterMilestoneTitle, counterButtonModeTitle, counterFlagTitle, counterNotificationTitle, counterNotificationDetail, counterMilestoneDetail;
    private Button saveChangesButton;
    private ConstraintLayout counterLabelLayout, counterValueLayout, counterMilestoneLayout, counterFlagLayout, counterButtonModeLayout, counterNotificationLayout, counterEditRootLayout, counterEditToolbarLayout, counterFlagGroupLayout;
    private ConstraintLayout counterLabelDivider, counterValueDivider, counterMilestoneDivider, counterFlagDivider, counterButtonModeDivider, counterNotificationDivider;
    private ArrayList<CounterData> counterDataArrayList = new ArrayList<>();
    private Activity activity;
    private ChipGroup counterFlagGroup;
    private Chip cherryFlag, limeFlag, peachFlag, plumFlag, berryFlag;
    private boolean flagLayoutOpen, isChanged = false;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onStop() {
        super.onStop();
        sharedPreferences = this.getSharedPreferences("TickTrackData", MODE_PRIVATE);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
        tickTrackDatabase.storeCurrentFragmentNumber(1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter_edit);

        activity = this;
        tickTrackDatabase = new TickTrackDatabase(activity);
        counterDataArrayList = tickTrackDatabase.retrieveCounterList();
        counterID = getIntent().getStringExtra("CurrentPosition");
        int flagColor = counterDataArrayList.get(getCurrentPosition()).getCounterFlag();
        sharedPreferences = this.getSharedPreferences("TickTrackData",MODE_PRIVATE);

        initVariables();

        TickTrackThemeSetter.counterEditActivityTheme(this, counterLabelLayout, counterValueLayout, counterMilestoneLayout, counterFlagLayout,
                counterButtonModeLayout, counterNotificationLayout, counterEditRootLayout,
                counterLabel, counterValue, counterMilestone, counterButtonMode, counterNotificationDetail, counterMilestoneDetail, flagColor,
                counterLabelDivider, counterValueDivider, counterMilestoneDivider, counterFlagDivider, counterButtonModeDivider, counterNotificationDivider, tickTrackDatabase,
                cherryFlag, limeFlag, peachFlag, plumFlag, berryFlag);

        setupPrefixValues();

        setupOnClickListeners();

        setupOnCheckedListeners();

        flagValueCheck();

        deleteCounterButton.setOnClickListener(view -> {

            DeleteCounterFromActivity counterDelete = new DeleteCounterFromActivity(activity, getCurrentPosition(), counterDataArrayList.get(getCurrentPosition()).getCounterLabel(),
                    counterDataArrayList.get(getCurrentPosition()).getCounterID(), sharedPreferenceChangeListener);
            counterDelete.show();
        });
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    }
    private int getCurrentPosition() {
        for(int i = 0; i < counterDataArrayList.size(); i ++){
            if(counterDataArrayList.get(i).getCounterID().equals(counterID)){
                return i;
            }
        }
        return 0;
    }
    private void setupOnCheckedListeners() {
        counterNotificationSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            if(compoundButton.isChecked()){
                counterDataArrayList.get(getCurrentPosition()).setCounterPersistentNotification(true);

            } else {
                counterDataArrayList.get(getCurrentPosition()).setCounterPersistentNotification(false);

            }
            isChanged = true;
        });
        counterButtonSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            if(compoundButton.isChecked()){
                counterDataArrayList.get(getCurrentPosition()).setCounterSwipeMode(true);
                counterButtonMode.setText("Swipe mode");
            } else {
                counterDataArrayList.get(getCurrentPosition()).setCounterSwipeMode(false);
                counterButtonMode.setText("Click mode");
            }
            isChanged = true;
        });
    }

    private void stopNotificationService() {
        Intent intent = new Intent(this, CounterNotificationService.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setAction(CounterNotificationService.ACTION_KILL_NOTIFICATIONS);
        startService(intent);
    }

    private void startNotificationService() {
        Intent intent = new Intent(this, CounterNotificationService.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        tickTrackDatabase.setCurrentCounterNotificationID(counterDataArrayList.get(getCurrentPosition()).getCounterID());
        intent.setAction(CounterNotificationService.ACTION_START_COUNTER_SERVICE);
        startService(intent);
    }

    private void initVariables() {
        backButton = findViewById(R.id.counterEditActivityBackButton);
        deleteCounterButton = findViewById(R.id.counterEditActivitySaveButton);
        counterFlag = findViewById(R.id.counterEditActivityFlagImageView);
        counterButtonSwitch = findViewById(R.id.counterEditActivityButtonModeSwitch);
        counterNotificationSwitch = findViewById(R.id.counterEditActivityNotificationSwitch);
        counterLabel = findViewById(R.id.counterEditActivityLabelTextView);
        counterValue = findViewById(R.id.counterEditActivityValueTextView);
        counterMilestone = findViewById(R.id.counterEditActivitySignificantTextView);
        counterButtonMode = findViewById(R.id.counterEditActivityButtonModeTextView);
        saveChangesButton = findViewById(R.id.counterEditActivityDeleteButton);
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
        counterFlagGroupLayout = findViewById(R.id.counterEditActivityFlagChipLayout);

        counterLabelDivider = findViewById(R.id.counterEditActivityLabelDivider);
        counterValueDivider = findViewById(R.id.counterEditActivityValueDivider);
        counterMilestoneDivider = findViewById(R.id.counterEditActivityMilestoneDivider);
        counterFlagDivider = findViewById(R.id.counterEditActivityFlagDivider);
        counterButtonModeDivider = findViewById(R.id.counterEditActivityButtonModeDivider);
        counterNotificationDivider = findViewById(R.id.counterEditActivityNotificationDivider);

        counterFlagGroup = findViewById(R.id.counterEditActivityFlagGroup);
        cherryFlag = findViewById(R.id.counterEditActivityCherryFlag);
        limeFlag = findViewById(R.id.counterEditActivityLimeFlag);
        peachFlag = findViewById(R.id.counterEditActivityPeachFlag);
        plumFlag = findViewById(R.id.counterEditActivityPlumFlag);
        berryFlag = findViewById(R.id.counterEditActivityBerryFlag);
    }

    SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener = (sharedPreferences, s) ->  {
        counterDataArrayList = tickTrackDatabase.retrieveCounterList();
        if (s.equals("CounterData")){
            counterValue.setText(""+counterDataArrayList.get(getCurrentPosition()).getCounterValue());
        }
    };

    private void setupPrefixValues() {
        setFlagColor(counterDataArrayList.get(getCurrentPosition()).getCounterFlag());

        counterLabel.setText(counterDataArrayList.get(getCurrentPosition()).getCounterLabel());
        counterValue.setText(""+counterDataArrayList.get(getCurrentPosition()).getCounterValue());
        if(counterDataArrayList.get(getCurrentPosition()).isCounterSignificantExist()){
            counterMilestone.setText(counterDataArrayList.get(getCurrentPosition()).getCounterSignificantCount()+"");
        } else {
            counterMilestone.setText("NA");
        }
        counterButtonSwitch.setChecked(counterDataArrayList.get(getCurrentPosition()).isCounterSwipeMode());
        if(counterDataArrayList.get(getCurrentPosition()).isCounterSwipeMode()){
            counterButtonMode.setText("Swipe mode");
        } else {
            counterButtonMode.setText("Click mode");
        }
        counterNotificationSwitch.setChecked(counterDataArrayList.get(getCurrentPosition()).isCounterPersistentNotification());

        counterFlagGroupLayout.setVisibility(View.GONE);
        flagLayoutOpen = true;

    }
    private void setFlagColor(int flagColor) {
        if(flagColor==1){
            counterFlag.setImageResource(R.drawable.ic_flag_red);
            counterEditToolbarLayout.setBackgroundResource(counterActivityToolbarColor(flagColor));
            cherryFlag.setChecked(true);
        }
        else if(flagColor==2){
            counterFlag.setImageResource(R.drawable.ic_flag_green);
            counterEditToolbarLayout.setBackgroundResource(counterActivityToolbarColor(flagColor));
            limeFlag.setChecked(true);
        }
        else if(flagColor==3){
            counterFlag.setImageResource(R.drawable.ic_flag_orange);
            counterEditToolbarLayout.setBackgroundResource(counterActivityToolbarColor(flagColor));
            peachFlag.setChecked(true);
        }
        else if(flagColor==4){
            counterFlag.setImageResource(R.drawable.ic_flag_purple);
            counterEditToolbarLayout.setBackgroundResource(counterActivityToolbarColor(flagColor));
            plumFlag.setChecked(true);
        }
        else if(flagColor==5){
            counterFlag.setImageResource(R.drawable.ic_flag_blue);
            counterEditToolbarLayout.setBackgroundResource(counterActivityToolbarColor(flagColor));
            berryFlag.setChecked(true);
        } else {
            counterEditToolbarLayout.setBackgroundResource(R.color.Accent);
            if(tickTrackDatabase.getThemeMode()==1){
                counterFlag.setImageResource(R.drawable.ic_round_flag_dark_24);
            } else {
                counterFlag.setImageResource(R.drawable.ic_round_flag_light_24);
            }
        }
    }

    private static int counterActivityToolbarColor(int flagColor){
        if(flagColor == 1){
            return R.color.red_matte;
        } else if(flagColor == 2){
            return R.color.green_matte;
        } else if(flagColor == 3){
            return R.color.orange_matte;
        } else if(flagColor == 5){
            return R.color.blue_matte;
        } else if(flagColor == 4){
            return R.color.purple_matte;
        }
        return R.color.Accent;
    }

    private void setupOnClickListeners() {

        counterLabelLayout.setOnClickListener(view -> {
            SingleInputDialog labelDialog = new SingleInputDialog(activity, counterLabel.getText().toString());
            labelDialog.show();
            labelDialog.saveChangesText.setVisibility(View.INVISIBLE);
            labelDialog.inputText.setVisibility(View.VISIBLE);
            labelDialog.helperText.setVisibility(View.VISIBLE);
            labelDialog.inputText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT | InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE |InputType.TYPE_TEXT_FLAG_CAP_WORDS);
            labelDialog.okButton.setOnClickListener(view1 -> {
                counterLabel.setText(labelDialog.inputText.getText().toString());
                labelDialog.dismiss();
                isChanged = true;
            });
            labelDialog.cancelButton.setOnClickListener(view12 -> labelDialog.dismiss());
        });
        counterValueLayout.setOnClickListener(view -> {
            SingleInputDialog labelDialog = new SingleInputDialog(activity, ""+counterDataArrayList.get(getCurrentPosition()).getCounterValue());
            labelDialog.show();
            labelDialog.saveChangesText.setVisibility(View.INVISIBLE);
            labelDialog.inputText.setVisibility(View.VISIBLE);
            labelDialog.helperText.setVisibility(View.VISIBLE);
            labelDialog.inputText.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_CLASS_NUMBER);
            labelDialog.helperText.setText("Counter value");
            labelDialog.okButton.setOnClickListener(view1 -> {
                counterValue.setText(labelDialog.inputText.getText()+"");
                labelDialog.dismiss();
                isChanged = true;
            });
            labelDialog.cancelButton.setOnClickListener(view12 -> labelDialog.dismiss());
        });
        counterMilestoneLayout.setOnClickListener(view -> {
            SingleInputDialog labelDialog = new SingleInputDialog(activity, ""+counterDataArrayList.get(getCurrentPosition()).getCounterSignificantCount());
            labelDialog.show();
            labelDialog.saveChangesText.setVisibility(View.INVISIBLE);
            labelDialog.inputText.setVisibility(View.VISIBLE);
            labelDialog.helperText.setVisibility(View.VISIBLE);
            labelDialog.inputText.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_CLASS_NUMBER);
            labelDialog.helperText.setText("Milestone value");

            labelDialog.okButton.setOnClickListener(view1 -> {

                if(!labelDialog.inputText.getText().toString().equals("")){

                    if(Integer.parseInt(labelDialog.inputText.getText().toString().replaceAll("[^\\d]", ""))!=0){

                        counterMilestone.setText(labelDialog.inputText.getText()+"");
                        isChanged = true;

                    } else {

                        counterMilestone.setText("NA");

                    }

                } else {

                    counterMilestone.setText("NA");

                }

                labelDialog.dismiss();
            });

            labelDialog.cancelButton.setOnClickListener(view12 -> labelDialog.dismiss());
        });

        counterFlagLayout.setOnClickListener(view -> flagGroupVisibilityToggle());
        counterButtonModeLayout.setOnClickListener(view -> toggleButtonMode());
        counterNotificationLayout.setOnClickListener(view -> toggleNotificationSwitch());
        saveChangesButton.setOnClickListener(view -> {
            saveData();
        });
        backButton.setOnClickListener(view -> onBackPressed());
    }

    private void flagGroupVisibilityToggle() {
        if(flagLayoutOpen){
            counterFlagGroupLayout.setVisibility(View.VISIBLE);
            flagLayoutOpen = false;
        } else {
            counterFlagGroupLayout.setVisibility(View.GONE);
            flagLayoutOpen = true;
        }
    }

    private void flagValueCheck(){
        counterFlagGroup.setOnCheckedChangeListener((group, checkedId) -> {
            Chip chip = counterFlagGroup.findViewById(checkedId);
            if(chip != null){
                if(chip.getText().toString().equals("Cherry")){
                    setFlagColor(1);
                    isChanged = true;
                }
                if(chip.getText().toString().equals("Lime")){
                    setFlagColor(2);
                    isChanged = true;
                }
                if(chip.getText().toString().equals("Peach")){
                    setFlagColor(3);
                    isChanged = true;
                }
                if(chip.getText().toString().equals("Plum")){
                    setFlagColor(4);
                    isChanged = true;
                }
                if(chip.getText().toString().equals("Berry")){
                    setFlagColor(5);
                    isChanged = true;
                }
            } else {
                setFlagColor(0);
                isChanged = true;
            }
        });
    }

    private void toggleNotificationSwitch() {
        if(counterNotificationSwitch.isChecked()){
            counterDataArrayList.get(getCurrentPosition()).setCounterPersistentNotification(false);
            counterNotificationSwitch.setChecked(false);
        } else {
            counterDataArrayList.get(getCurrentPosition()).setCounterPersistentNotification(true);
            counterNotificationSwitch.setChecked(true);

        }
        isChanged = true;
    }

    private void toggleButtonMode() {
        if(counterButtonSwitch.isChecked()){
            counterDataArrayList.get(getCurrentPosition()).setCounterSwipeMode(false);
            counterButtonMode.setText("Click mode");
            counterButtonSwitch.setChecked(false);
        } else {
            counterDataArrayList.get(getCurrentPosition()).setCounterSwipeMode(true);
            counterButtonMode.setText("Swipe mode");
            counterButtonSwitch.setChecked(true);
        }
        isChanged = true;
    }

    private void saveData(){
        if(isChanged){
            counterDataArrayList.get(getCurrentPosition()).setCounterLabel(counterLabel.getText().toString());
            counterDataArrayList.get(getCurrentPosition()).setCounterValue(Integer.parseInt(counterValue.getText().toString()));
            if(!counterMilestone.getText().toString().equals("NA")){
                counterDataArrayList.get(getCurrentPosition()).setCounterSignificantCount(Integer.parseInt(counterMilestone.getText().toString()));
                counterDataArrayList.get(getCurrentPosition()).setCounterSignificantExist(true);
            } else {
                counterDataArrayList.get(getCurrentPosition()).setCounterSignificantCount(0);
                counterDataArrayList.get(getCurrentPosition()).setCounterSignificantExist(false);
            }
            counterDataArrayList.get(getCurrentPosition()).setCounterFlag(getCounterFlag());
            counterDataArrayList.get(getCurrentPosition()).setCounterSwipeMode(counterButtonSwitch.isChecked());
            if(counterNotificationSwitch.isChecked()){
                counterDataArrayList.get(getCurrentPosition()).setCounterPersistentNotification(counterNotificationSwitch.isChecked());
                revertAllOtherChecks();
                startNotificationService();
            } else {
                counterDataArrayList.get(getCurrentPosition()).setCounterPersistentNotification(counterNotificationSwitch.isChecked());
            }
            tickTrackDatabase.storeCounterList(counterDataArrayList);

            isChanged = false;
        }
        onBackPressed();
    }

    private void revertAllOtherChecks() {
        stopNotificationService();
        for(int i = 0; i < counterDataArrayList.size(); i++){
            if(i!=getCurrentPosition()){
                counterDataArrayList.get(i).setCounterPersistentNotification(false);
            }
        }
    }

    private int getCounterFlag() {
        if(cherryFlag.isChecked()){
            return 1;
        } else if(limeFlag.isChecked()){
            return 2;
        } else if(peachFlag.isChecked()){
            return 3;
        } else if(plumFlag.isChecked()){
            return 4;
        } else if(berryFlag.isChecked()){
            return 5;
        } else {
            return 0;
        }
    }

    @Override
    public void onBackPressed() {
        if(!isChanged){
            Intent intent = new Intent(this, CounterActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("currentCounterPosition", counterID);
            startActivity(intent);
        } else {
            SingleInputDialog labelDialog = new SingleInputDialog(activity, counterDataArrayList.get(getCurrentPosition()).getCounterLabel());
            labelDialog.show();
            labelDialog.saveChangesText.setVisibility(View.VISIBLE);
            labelDialog.inputText.setVisibility(View.INVISIBLE);
            labelDialog.helperText.setVisibility(View.INVISIBLE);
            labelDialog.okButton.setText("Yes");
            labelDialog.cancelButton.setText("No");
            labelDialog.okButton.setOnClickListener(view1 -> {
                saveData();
                isChanged = false;
            });
            labelDialog.cancelButton.setOnClickListener(view12 -> {
                Intent intent = new Intent(this, CounterActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("currentCounterPosition", counterID);
                startActivity(intent);
            });
        }
        tickTrackDatabase.storeCurrentFragmentNumber(1);
    }
}