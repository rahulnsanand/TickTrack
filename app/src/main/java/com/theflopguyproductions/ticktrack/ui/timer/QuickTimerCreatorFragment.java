package com.theflopguyproductions.ticktrack.ui.timer;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.timer.service.TimerService;
import com.theflopguyproductions.ticktrack.ui.utils.TickTrackAnimator;
import com.theflopguyproductions.ticktrack.ui.utils.numberpicker.DarkModeNumberPicker;
import com.theflopguyproductions.ticktrack.ui.utils.numberpicker.LightModeNumberPicker;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackTimerDatabase;
import com.theflopguyproductions.ticktrack.utils.helpers.QuickTimerPreset;
import com.theflopguyproductions.ticktrack.utils.helpers.TickTrackThemeSetter;

public class QuickTimerCreatorFragment extends Fragment {

    private FloatingActionButton oneMinute, twoMinute, fiveMinute, tenMinute, playFab;
    private DarkModeNumberPicker darkHourPicker, darkMinutePicker, darkSecondPicker;
    private LightModeNumberPicker lightHourPicker, lightMinutePicker, lightSecondPicker;
    private TextView hourText, minuteText, secondText;
    private ConstraintLayout rootLayout, customOptionsLayout;
    private Button customOptionsButton;
    private boolean hasChanged = false, isExpanded = false;
    private int pickedHour=0, pickedMinute=0, pickedSecond=0;
    private TickTrackDatabase tickTrackDatabase;
    private TickTrackTimerDatabase tickTrackTimerDatabase;
    private QuickTimerPreset quickTimerPreset;

    private void initVariables(View view) {

        oneMinute = view.findViewById(R.id.quickTimerFragmentPlayCustomOneQuickTimerFAB);
        twoMinute = view.findViewById(R.id.quickTimerFragmentPlayCustomTwoQuickTimerFAB);
        fiveMinute = view.findViewById(R.id.quickTimerFragmentPlayCustomFiveQuickTimerFAB);
        tenMinute = view.findViewById(R.id.quickTimerFragmentPlayCustomTenQuickTimerFAB);
        playFab = view.findViewById(R.id.quickTimerFragmentPlayCustomTimerFAB);
        darkHourPicker = view.findViewById(R.id.quickTimerFragmentPlayCustomDarkHourNumberPicker);
        darkMinutePicker = view.findViewById(R.id.quickTimerFragmentPlayCustomDarkMinuteNumberPicker);
        darkSecondPicker = view.findViewById(R.id.quickTimerFragmentPlayCustomDarkSecondNumberPicker);
        lightHourPicker = view.findViewById(R.id.quickTimerFragmentPlayCustomLightHourNumberPicker);
        lightMinutePicker = view.findViewById(R.id.quickTimerFragmentPlayCustomLightMinuteNumberPicker);
        lightSecondPicker = view.findViewById(R.id.quickTimerFragmentPlayCustomLightSecondNumberPicker);
        hourText = view.findViewById(R.id.quickTimerFragmentPlayCustomHourText);
        minuteText = view.findViewById(R.id.quickTimerFragmentPlayCustomMinuteText);
        secondText = view.findViewById(R.id.quickTimerFragmentPlayCustomSecondText);
        rootLayout = view.findViewById(R.id.quickTimerFragmentRootLayout);
        customOptionsLayout = view.findViewById(R.id.quickTimerFragmentCustomLayout);
        customOptionsButton = view.findViewById(R.id.quickTimerFragmentCustomTimerButton);

        darkHourPicker.setMaxValue(99);
        darkHourPicker.setMinValue(0);
        darkHourPicker.setValue(0);

        darkMinutePicker.setMinValue(0);
        darkMinutePicker.setMaxValue(59);
        darkMinutePicker.setValue(0);

        darkSecondPicker.setMaxValue(59);
        darkSecondPicker.setMinValue(0);
        darkSecondPicker.setValue(0);

        lightHourPicker.setMaxValue(99);
        lightHourPicker.setMinValue(0);
        lightHourPicker.setValue(0);

        lightMinutePicker.setMinValue(0);
        lightMinutePicker.setMaxValue(59);
        lightMinutePicker.setValue(0);

        lightSecondPicker.setMaxValue(59);
        lightSecondPicker.setMinValue(0);
        lightSecondPicker.setValue(0);

    }
    private boolean isCreateOn(){
        return !(pickedSecond == 0 && pickedHour == 0 && pickedMinute == 0);
    }
    private void timeChangeListener() {
        darkHourPicker.setOnValueChangedListener((numberPicker, i, i1) -> {
            pickedHour = darkHourPicker.getValue();
            if(isCreateOn()){
                TickTrackAnimator.fabUnDissolve(playFab);
                hasChanged=true;
            } else {
                TickTrackAnimator.fabDissolve(playFab);
                hasChanged=false;
            }
        });
        darkMinutePicker.setOnValueChangedListener((numberPicker, i, i1) -> {
            pickedMinute = darkMinutePicker.getValue();
            if(isCreateOn()){
                TickTrackAnimator.fabUnDissolve(playFab);
                hasChanged=true;
            } else {
                TickTrackAnimator.fabDissolve(playFab);
                hasChanged=false;
            }
        });
        darkSecondPicker.setOnValueChangedListener((numberPicker, i, i1) -> {
            pickedSecond = darkSecondPicker.getValue();
            if(isCreateOn()){
                TickTrackAnimator.fabUnDissolve(playFab);
                hasChanged=true;
            } else {
                TickTrackAnimator.fabDissolve(playFab);
                hasChanged=false;
            }
        });

        lightHourPicker.setOnValueChangedListener((numberPicker, i, i1) -> {
            pickedHour = lightHourPicker.getValue();
            if(isCreateOn()){
                TickTrackAnimator.fabUnDissolve(playFab);
                hasChanged=true;
            } else {
                TickTrackAnimator.fabDissolve(playFab);
                hasChanged=false;
            }
        });
        lightMinutePicker.setOnValueChangedListener((numberPicker, i, i1) -> {
            pickedMinute = lightMinutePicker.getValue();
            if(isCreateOn()){
                TickTrackAnimator.fabUnDissolve(playFab);
                hasChanged=true;
            } else {
                TickTrackAnimator.fabDissolve(playFab);
                hasChanged=false;
            }
        });
        lightSecondPicker.setOnValueChangedListener((numberPicker, i, i1) -> {
            pickedSecond = lightSecondPicker.getValue();
            if(isCreateOn()){
                TickTrackAnimator.fabUnDissolve(playFab);
                hasChanged=true;
            } else {
                TickTrackAnimator.fabDissolve(playFab);
                hasChanged=false;
            }
        });
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quick_timer_creator, container, false);
        tickTrackDatabase = new TickTrackDatabase(requireContext());
        tickTrackTimerDatabase = new TickTrackTimerDatabase(requireContext());
        quickTimerPreset = new QuickTimerPreset(requireContext());
        initVariables(view);
        timeChangeListener();
        setupOnClickListeners();

        TickTrackThemeSetter.quickTimerActivityTheme(getActivity(), tickTrackDatabase, lightHourPicker, lightMinutePicker, lightSecondPicker,
                darkHourPicker, darkMinutePicker, darkSecondPicker, hourText, minuteText, secondText, rootLayout);

        return view;
    }

    private void setupOnClickListeners() {

        oneMinute.setOnClickListener(view -> {
            quickTimerPreset.setOneMinuteTimer();
            if(!tickTrackTimerDatabase.isMyServiceRunning(TimerService.class)){
                tickTrackTimerDatabase.startNotificationService();
            }
            quickTimerCreateListener.onCreatedListener();
        });
        twoMinute.setOnClickListener(view -> {
            quickTimerPreset.setTwoMinuteTimer();
            if(!tickTrackTimerDatabase.isMyServiceRunning(TimerService.class)){
                tickTrackTimerDatabase.startNotificationService();
            }
            quickTimerCreateListener.onCreatedListener();
        });
        fiveMinute.setOnClickListener(view -> {
            quickTimerPreset.setFiveMinuteTimer();
            if(!tickTrackTimerDatabase.isMyServiceRunning(TimerService.class)){
                tickTrackTimerDatabase.startNotificationService();
            }
            quickTimerCreateListener.onCreatedListener();
        });
        tenMinute.setOnClickListener(view -> {
            quickTimerPreset.setTenMinuteTimer();
            if(!tickTrackTimerDatabase.isMyServiceRunning(TimerService.class)){
                tickTrackTimerDatabase.startNotificationService();
            }
            quickTimerCreateListener.onCreatedListener();
        });
        customOptionsButton.setOnClickListener(view -> toggleCustomOptions());
        playFab.setOnClickListener(view -> {
            if(hasChanged){
                quickTimerPreset.setCustomTimer(pickedHour, pickedMinute, pickedSecond);
                if(!tickTrackTimerDatabase.isMyServiceRunning(TimerService.class)){
                    tickTrackTimerDatabase.startNotificationService();
                }
                quickTimerCreateListener.onCreatedListener();
            }
        });

    }

    private void toggleCustomOptions() {
        if(isExpanded){
            customOptionsButton.setText("Enable Custom QuickTimer");
            customOptionsLayout.setVisibility(View.GONE);
            isExpanded=false;
        } else {
            customOptionsButton.setText("Disable Custom QuickTimer");
            customOptionsLayout.setVisibility(View.VISIBLE);
            isExpanded=true;
        }
    }

    private QuickTimerCreateListener quickTimerCreateListener;
    public interface QuickTimerCreateListener {
        void onCreatedListener();
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            quickTimerCreateListener = (QuickTimerCreateListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement " + QuickTimerCreateListener.class.getName());
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        quickTimerCreateListener = null;
    }

}
