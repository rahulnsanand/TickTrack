package com.theflopguyproductions.ticktrack.ui.timer;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.SoYouADeveloperHuh;
import com.theflopguyproductions.ticktrack.dialogs.SingleInputDialog;
import com.theflopguyproductions.ticktrack.timer.TimerData;
import com.theflopguyproductions.ticktrack.timer.activity.TimerActivity;
import com.theflopguyproductions.ticktrack.utils.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.TickTrackThemeSetter;
import com.theflopguyproductions.ticktrack.utils.TimeAgo;
import com.theflopguyproductions.ticktrack.utils.UniqueIdGenerator;

import java.sql.Timestamp;
import java.util.ArrayList;

public class TimerCreatorFragment extends Fragment {

    TickTrackDatabase tickTrackDatabase;

    private ArrayList<TimerData> timerDataArrayList= new ArrayList<>();
    private Activity activity;

    private ConstraintLayout timerCreatorRootLayout, timerCreatorFlagLayout, timerCreatorFlagExpandLayout, timerCreatorLabelLayout;
    private ImageView timerFlagImage;
    private TextView timerLabelTitle, timerLabelText, timerFlagTitle, timerFlagText, timerHoursText, timerMinutesText, timerSecondsText;
    private ChipGroup timerCreateFlagChipGroup;
    private Chip redFlag, greenFlag, orangeFlag, purpleFlag, blueFlag;
    private NumberPicker hourDarkPicker, minuteDarkPicker, secondDarkPicker, hourLightPicker, minuteLightPicker, secondLightPicker;
    private FloatingActionButton timerCreateFAB, timerDiscardFAB;
    private boolean hasChanged = false, isExpanded = false, isFirst = true;
    private int pickedHour=0, pickedMinute=0, pickedSecond=0, flagCheck = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_timer_creator, container, false);
        activity = getActivity();

        assert activity != null;
        tickTrackDatabase = new TickTrackDatabase(activity);
        isFirst = tickTrackDatabase.isFirstTimer();

        timerDataArrayList = tickTrackDatabase.retrieveTimerList();

        initVariables(view);

        TickTrackThemeSetter.timerCreateTheme(activity, hourDarkPicker, minuteDarkPicker, secondDarkPicker,hourLightPicker, minuteLightPicker, secondLightPicker, timerHoursText, timerMinutesText, timerSecondsText,
                timerLabelTitle, timerFlagTitle, timerCreatorRootLayout, tickTrackDatabase);

        timeChangeListener();

        flagCheck();

        timerCreatorLabelLayout.setOnClickListener(view1 -> labelDialogSetup());

        timerCreatorFlagLayout.setOnClickListener(view12 -> toggleFlagExpandLayout());

        timerCreateFAB.setOnClickListener(view13 -> {
            if(hasChanged){
                createTimer();
            }
        });

        timerDiscardFAB.setOnClickListener(view14 -> discardTimer());

        return view;
    }

    private void timeChangeListener() {
        hourDarkPicker.setOnValueChangedListener((numberPicker, i, i1) -> {
            pickedHour = hourDarkPicker.getValue();
            if(isCreateOn()){
                timerCreateFAB.setVisibility(View.VISIBLE);
                hasChanged=true;
            } else {
                timerCreateFAB.setVisibility(View.INVISIBLE);
                hasChanged=false;
            }
        });
        minuteDarkPicker.setOnValueChangedListener((numberPicker, i, i1) -> {
            pickedMinute = minuteDarkPicker.getValue();
            if(isCreateOn()){
                timerCreateFAB.setVisibility(View.VISIBLE);
                hasChanged=true;
            } else {
                timerCreateFAB.setVisibility(View.INVISIBLE);
                hasChanged=false;
            }
        });
        secondDarkPicker.setOnValueChangedListener((numberPicker, i, i1) -> {
            pickedSecond = secondDarkPicker.getValue();
            if(isCreateOn()){
                timerCreateFAB.setVisibility(View.VISIBLE);
                hasChanged=true;
            } else {
                timerCreateFAB.setVisibility(View.INVISIBLE);
                hasChanged=false;
            }
        });

        hourLightPicker.setOnValueChangedListener((numberPicker, i, i1) -> {
            pickedHour = hourLightPicker.getValue();
            if(isCreateOn()){
                timerCreateFAB.setVisibility(View.VISIBLE);
                hasChanged=true;
            } else {
                timerCreateFAB.setVisibility(View.INVISIBLE);
                hasChanged=false;
            }
        });
        minuteLightPicker.setOnValueChangedListener((numberPicker, i, i1) -> {
            pickedMinute = minuteLightPicker.getValue();
            if(isCreateOn()){
                timerCreateFAB.setVisibility(View.VISIBLE);
                hasChanged=true;
            } else {
                timerCreateFAB.setVisibility(View.INVISIBLE);
                hasChanged=false;
            }
        });
        secondLightPicker.setOnValueChangedListener((numberPicker, i, i1) -> {
            pickedSecond = secondLightPicker.getValue();
            if(isCreateOn()){
                timerCreateFAB.setVisibility(View.VISIBLE);
                hasChanged=true;
            } else {
                timerCreateFAB.setVisibility(View.INVISIBLE);
                hasChanged=false;
            }
        });


    }

    private void toggleFlagExpandLayout() {
        if(isExpanded){
            timerCreatorFlagExpandLayout.setVisibility(View.GONE);
            isExpanded = false;
        } else {
            timerCreatorFlagExpandLayout.setVisibility(View.VISIBLE);
            isExpanded = true;
        }
    }

    private void flagCheck() {
        setFlagColor(flagCheck);
        timerCreateFlagChipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            Chip chip = timerCreateFlagChipGroup.findViewById(checkedId);
            if(chip != null){
                Toast.makeText(activity, chip.getText().toString(), Toast.LENGTH_LONG).show();
                if(chip.getText().toString().equals("Cherry")){
                    flagCheck=1;
                    setFlagColor(flagCheck);
                }
                else if(chip.getText().toString().equals("Lime")){
                    flagCheck=2;
                    setFlagColor(flagCheck);
                }
                else if(chip.getText().toString().equals("Peach")){
                    flagCheck=3;
                    setFlagColor(flagCheck);
                }
                else if(chip.getText().toString().equals("Plum")){
                    flagCheck=4;
                    setFlagColor(flagCheck);
                }
                else if(chip.getText().toString().equals("Berry")){
                    flagCheck = 5;
                    setFlagColor(flagCheck);
                } else {
                    flagCheck=0;
                    setFlagColor(flagCheck);
                }

            } else {
                flagCheck = 0;
                setFlagColor(flagCheck);
            }
        });
    }

    private void setFlagColor(int flagColor) {
        if(flagColor==1){
            timerFlagImage.setImageResource(R.drawable.ic_flag_red);
            redFlag.setChecked(true);
        }
        else if(flagColor==2){
            timerFlagImage.setImageResource(R.drawable.ic_flag_green);
            greenFlag.setChecked(true);
        }
        else if(flagColor==3){
            timerFlagImage.setImageResource(R.drawable.ic_flag_orange);
            orangeFlag.setChecked(true);
        }
        else if(flagColor==4){
            timerFlagImage.setImageResource(R.drawable.ic_flag_purple);
            purpleFlag.setChecked(true);
        }
        else if(flagColor==5){
            timerFlagImage.setImageResource(R.drawable.ic_flag_blue);
            blueFlag.setChecked(true);
        } else {
            if(tickTrackDatabase.getThemeMode()==1){
                timerFlagImage.setImageResource(R.drawable.ic_round_flag_dark_24);
            } else {
                timerFlagImage.setImageResource(R.drawable.ic_round_flag_light_24);
            }
        }
    }

    private void initVariables(View view) {

        timerCreateFAB = view.findViewById(R.id.timerCreateFragmentPlayFAB);
        timerDiscardFAB = view.findViewById(R.id.timerCreateFragmentDiscardFAB);
        hourDarkPicker = view.findViewById(R.id.timerCreatorFragmentHourDarkNumberPicker);
        minuteDarkPicker = view.findViewById(R.id.timerCreatorFragmentMinuteDarkNumberPicker);
        secondDarkPicker = view.findViewById(R.id.timerCreatorFragmentSecondDarkNumberPicker);
        hourLightPicker = view.findViewById(R.id.timerCreatorFragmentHourLightNumberPicker);
        minuteLightPicker = view.findViewById(R.id.timerCreatorFragmentMinuteLightNumberPicker);
        secondLightPicker = view.findViewById(R.id.timerCreatorFragmentSecondLightNumberPicker);
        redFlag = view.findViewById(R.id.timerCreatorRedFlag);
        greenFlag = view.findViewById(R.id.timerCreatorGreenFlag);
        orangeFlag = view.findViewById(R.id.timerCreatorOrangeFlag);
        purpleFlag = view.findViewById(R.id.timerCreatorPurpleFlag);
        blueFlag = view.findViewById(R.id.timerCreatorBlueFlag);
        timerCreateFlagChipGroup = view.findViewById(R.id.timerCreatorFragmentFlagChipGroup);
        timerLabelTitle = view.findViewById(R.id.timerCreatorFragmentLabelTitleTextView);
        timerLabelText = view.findViewById(R.id.timerCreatorFragmentLabelTextView);
        timerFlagTitle = view.findViewById(R.id.timerCreatorFragmentFlagTitleTextView);
        timerFlagText = view.findViewById(R.id.timerCreatorFragmentFlagTextView);
        timerHoursText = view.findViewById(R.id.timerCreatorFragmentHourLabelTextView);
        timerMinutesText = view.findViewById(R.id.timerCreatorFragmentMinutesLabelTextView);
        timerSecondsText = view.findViewById(R.id.timerCreatorFragmentSecondsLabelTextView);
        timerFlagImage = view.findViewById(R.id.timerCreatorFragmentFlagImageView);
        timerCreatorRootLayout = view.findViewById(R.id.timerCreatorRootLayout);
        timerCreatorFlagLayout = view.findViewById(R.id.timerCreatorFragmentFlagRootLayout);
        timerCreatorFlagExpandLayout = view.findViewById(R.id.timerCreatorFragmentFlagOptionsRootLayout);
        timerCreatorLabelLayout = view.findViewById(R.id.timerCreatorFragmentLabelRootLayout);

        timerCreatorFlagExpandLayout.setVisibility(View.GONE);
        timerCreateFAB.setVisibility(View.INVISIBLE); //TODO ANIMATE FAB DISSOLVE

        if(isFirst){
            timerDiscardFAB.setVisibility(View.INVISIBLE);
        } else {
            timerDiscardFAB.setVisibility(View.VISIBLE);
        }

        hourDarkPicker.setMaxValue(99);
        hourDarkPicker.setMinValue(0);
        hourDarkPicker.setValue(0);

        minuteDarkPicker.setMinValue(0);
        minuteDarkPicker.setMaxValue(59);
        minuteDarkPicker.setValue(0);

        secondDarkPicker.setMaxValue(59);
        secondDarkPicker.setMinValue(0);
        secondDarkPicker.setValue(0);

        hourLightPicker.setMaxValue(99);
        hourLightPicker.setMinValue(0);
        hourLightPicker.setValue(0);

        minuteLightPicker.setMinValue(0);
        minuteLightPicker.setMaxValue(59);
        minuteLightPicker.setValue(0);

        secondLightPicker.setMaxValue(59);
        secondLightPicker.setMinValue(0);
        secondLightPicker.setValue(0);

    }

    private void labelDialogSetup(){
        SingleInputDialog labelDialog = new SingleInputDialog(activity, timerLabelText.getText().toString());
        labelDialog.show();
        labelDialog.saveChangesText.setVisibility(View.INVISIBLE);
        labelDialog.inputText.setVisibility(View.VISIBLE);
        labelDialog.helperText.setVisibility(View.VISIBLE);
        labelDialog.inputText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT | InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE |InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        labelDialog.okButton.setOnClickListener(view1 -> {
            if(labelDialog.inputText.getText().toString().trim().length() > 0){
                timerLabelText.setText(labelDialog.inputText.getText().toString());
            }
            labelDialog.dismiss();
        });
        labelDialog.cancelButton.setOnClickListener(view12 -> labelDialog.dismiss());
    }

    private void createTimer() {

        TimerData timerData = new TimerData();
        timerData.setTimerCreateTimeStamp(new Timestamp(System.currentTimeMillis()));
        timerData.setTimerFlag(flagCheck);
        timerData.setTimerHour(pickedHour);
        timerData.setTimerMinute(pickedMinute);
        timerData.setTimerSecond(pickedSecond);
        timerData.setTimerLabel(timerLabelText.getText().toString());
        timerData.setTimerID(UniqueIdGenerator.getUniqueTimerID());
        timerData.setTimerIntegerID(UniqueIdGenerator.getUniqueIntegerTimerID());
        timerData.setTimeLeftInMillis(TimeAgo.getTimerDataInMillis(pickedHour,pickedMinute,pickedSecond,0));
        timerData.setTimerTotalTimeInMillis(TimeAgo.getTimerDataInMillis(pickedHour,pickedMinute,pickedSecond,0));
        timerDataArrayList.add(0,timerData);
        tickTrackDatabase.storeTimerList(timerDataArrayList);

        if(isFirst){
            tickTrackDatabase.setFirstTimer(false);
        }

        Intent timerIntent = new Intent(activity, TimerActivity.class);
        timerIntent.putExtra("timerID", timerData.getTimerID());
        startActivity(timerIntent);

    }

    private void discardTimer(){
        Intent intent = new Intent(activity, SoYouADeveloperHuh.class);
        intent.putExtra("FragmentID", 2);
        startActivity(intent);
    }

    private boolean isCreateOn(){
        return !(pickedSecond == 0 && pickedHour == 0 && pickedMinute == 0);
    }


}