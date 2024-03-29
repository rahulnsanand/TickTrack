package com.theflopguyproductions.ticktrack.ui.timer;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.dialogs.SingleInputDialog;
import com.theflopguyproductions.ticktrack.timer.activity.TimerActivity;
import com.theflopguyproductions.ticktrack.timer.data.TimerData;
import com.theflopguyproductions.ticktrack.ui.utils.TickTrackAnimator;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.helpers.TickTrackThemeSetter;
import com.theflopguyproductions.ticktrack.utils.helpers.TimeAgo;
import com.theflopguyproductions.ticktrack.utils.helpers.UniqueIdGenerator;

import java.util.ArrayList;
import java.util.Objects;

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
    private ConstraintLayout timerCreateFAB;
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
                timerLabelTitle, timerFlagTitle, timerCreatorRootLayout, tickTrackDatabase,
                redFlag, greenFlag, orangeFlag, purpleFlag, blueFlag, timerCreateFAB);

        timeChangeListener();

        flagCheck();

        timerCreatorLabelLayout.setOnClickListener(view1 -> labelDialogSetup());

        timerCreatorFlagLayout.setOnClickListener(view12 -> toggleFlagExpandLayout());

        timerCreateFAB.setOnClickListener(view13 -> {
            if(hasChanged){
                createTimer();
            }
        });



        return view;
    }

    private void timeChangeListener() {
        hourDarkPicker.setOnValueChangedListener((numberPicker, i, i1) -> {
            pickedHour = hourDarkPicker.getValue();
            if(isCreateOn()){
                TickTrackAnimator.fabLayoutUnDissolve(timerCreateFAB);
                hasChanged=true;
            } else {
                TickTrackAnimator.fabLayoutDissolve(timerCreateFAB);
                hasChanged=false;
            }
        });
        minuteDarkPicker.setOnValueChangedListener((numberPicker, i, i1) -> {
            pickedMinute = minuteDarkPicker.getValue();
            if(isCreateOn()){
                TickTrackAnimator.fabLayoutUnDissolve(timerCreateFAB);
                hasChanged=true;
            } else {
                TickTrackAnimator.fabLayoutDissolve(timerCreateFAB);
                hasChanged=false;
            }
        });
        secondDarkPicker.setOnValueChangedListener((numberPicker, i, i1) -> {
            pickedSecond = secondDarkPicker.getValue();
            if(isCreateOn()){
                TickTrackAnimator.fabLayoutUnDissolve(timerCreateFAB);
                hasChanged=true;
            } else {
                TickTrackAnimator.fabLayoutDissolve(timerCreateFAB);
                hasChanged=false;
            }
        });

        hourLightPicker.setOnValueChangedListener((numberPicker, i, i1) -> {
            pickedHour = hourLightPicker.getValue();
            if(isCreateOn()){
                TickTrackAnimator.fabLayoutUnDissolve(timerCreateFAB);
                hasChanged=true;
            } else {
                TickTrackAnimator.fabLayoutDissolve(timerCreateFAB);
                hasChanged=false;
            }
        });
        minuteLightPicker.setOnValueChangedListener((numberPicker, i, i1) -> {
            pickedMinute = minuteLightPicker.getValue();
            if(isCreateOn()){
                TickTrackAnimator.fabLayoutUnDissolve(timerCreateFAB);
                hasChanged=true;
            } else {
                TickTrackAnimator.fabLayoutDissolve(timerCreateFAB);
                hasChanged=false;
            }
        });
        secondLightPicker.setOnValueChangedListener((numberPicker, i, i1) -> {
            pickedSecond = secondLightPicker.getValue();
            if(isCreateOn()){
                TickTrackAnimator.fabLayoutUnDissolve(timerCreateFAB);
                hasChanged=true;
            } else {
                TickTrackAnimator.fabLayoutDissolve(timerCreateFAB);
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
            timerFlagText.setText("Cherry");
        }
        else if(flagColor==2){
            timerFlagImage.setImageResource(R.drawable.ic_flag_green);
            greenFlag.setChecked(true);
            timerFlagText.setText("Lime");
        }
        else if(flagColor==3){
            timerFlagImage.setImageResource(R.drawable.ic_flag_orange);
            orangeFlag.setChecked(true);
            timerFlagText.setText("Peach");
        }
        else if(flagColor==4){
            timerFlagImage.setImageResource(R.drawable.ic_flag_purple);
            purpleFlag.setChecked(true);
            timerFlagText.setText("Plum");
        }
        else if(flagColor==5){
            timerFlagImage.setImageResource(R.drawable.ic_flag_blue);
            blueFlag.setChecked(true);
            timerFlagText.setText("Berry");
        } else {
            if(tickTrackDatabase.getThemeMode()==1){
                timerFlagImage.setImageResource(R.drawable.ic_round_flag_dark_24);
            } else {
                timerFlagImage.setImageResource(R.drawable.ic_round_flag_light_24);
            }
            timerFlagText.setText("Set flag");
        }
    }

    private void initVariables(View view) {

        timerCreateFAB = view.findViewById(R.id.timerCreateFragmentPlayFAB);

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
        new Handler().post(() -> {
            SingleInputDialog labelDialog = new SingleInputDialog(activity, timerLabelText.getText().toString());
            labelDialog.show();
            labelDialog.saveChangesText.setVisibility(View.GONE);
            labelDialog.inputText.setVisibility(View.VISIBLE);
            labelDialog.helperText.setVisibility(View.VISIBLE);
            if(labelDialog.inputText.requestFocus()){
                Objects.requireNonNull(labelDialog.getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
            labelDialog.inputText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT | InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE |InputType.TYPE_TEXT_FLAG_CAP_WORDS);
            labelDialog.okButton.setOnClickListener(view1 -> {
                if(labelDialog.inputText.getText().toString().trim().length() > 0){
                    timerLabelText.setText(labelDialog.inputText.getText().toString());
                }
                labelDialog.dismiss();
            });
            labelDialog.cancelButton.setOnClickListener(view12 -> {
                labelDialog.dismiss();
            });

        });
    }

    private void createTimer() {

        TimerData timerData = new TimerData();
        timerData.setTimerLastEdited(System.currentTimeMillis());
        timerData.setTimerFlag(flagCheck);
        timerData.setTimerHour(pickedHour);
        timerData.setTimerMinute(pickedMinute);
        timerData.setTimerSecond(pickedSecond);
        timerData.setTimerLabel(timerLabelText.getText().toString());
        timerData.setTimerID(UniqueIdGenerator.getUniqueTimerID());
        timerData.setTimerIntID(UniqueIdGenerator.getUniqueIntegerTimerID());
        timerData.setQuickTimer(false);
        timerData.setTimerStartTimeInMillis(-1);
        timerData.setTimerTotalTimeInMillis(TimeAgo.getTimerDataInMillis(pickedHour,pickedMinute,pickedSecond,0));
        timerData.setTimerPause(false);
        timerData.setTimerOn(true);
        timerDataArrayList.add(0,timerData);
        tickTrackDatabase.storeTimerList(timerDataArrayList);

        if(isFirst){
            tickTrackDatabase.setFirstTimer(false);
        }

        Intent timerIntent = new Intent(activity, TimerActivity.class);
        timerIntent.setAction(TimerActivity.ACTION_TIMER_NEW_ADDITION);
        timerIntent.putExtra("timerID", timerData.getTimerID());
        startActivity(timerIntent);

    }

    private boolean isCreateOn(){
        return !(pickedSecond == 0 && pickedHour == 0 && pickedMinute == 0);
    }


}