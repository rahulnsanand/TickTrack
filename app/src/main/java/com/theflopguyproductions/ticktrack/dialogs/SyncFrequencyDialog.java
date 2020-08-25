package com.theflopguyproductions.ticktrack.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;

import java.util.Objects;

public class SyncFrequencyDialog extends Dialog {

    public SyncFrequencyDialog(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    private Activity activity;
    private TickTrackDatabase tickTrackDatabase;
    private  int themeSet = 1;

    private ConstraintLayout rootLayout;
    public RadioGroup radioGroup;
    public RadioButton monthlyRadioButton, weeklyRadioButton, dailyRadioButton;

    private void setupTheme() {
        if(themeSet==1){
            rootLayout.setBackgroundResource(R.color.LightGray);
            monthlyRadioButton.setTextColor(activity.getResources().getColor(R.color.DarkText));
            weeklyRadioButton.setTextColor(activity.getResources().getColor(R.color.DarkText));
            dailyRadioButton.setTextColor(activity.getResources().getColor(R.color.DarkText));
        } else {
            rootLayout.setBackgroundResource(R.color.Gray);
            monthlyRadioButton.setTextColor(activity.getResources().getColor(R.color.LightText));
            weeklyRadioButton.setTextColor(activity.getResources().getColor(R.color.LightText));
            dailyRadioButton.setTextColor(activity.getResources().getColor(R.color.LightText));
        }
    }

    private void initVariables(View view) {
        monthlyRadioButton = view.findViewById(R.id.syncDataSettingsTickTrackMonthlyRadio);
        weeklyRadioButton = view.findViewById(R.id.syncDataSettingsTickTrackWeeklyRadio);
        dailyRadioButton = view.findViewById(R.id.syncDataSettingsTickTrackDailyRadio);
        radioGroup = view.findViewById(R.id.syncDataSettingsTickTrackRadioGroup);
        rootLayout = findViewById(R.id.syncDataSettingsTickTrackRootLayout);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_settings_syncfreq_layout, new ConstraintLayout(activity), false);
        setContentView(view);
        Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        initVariables(view);

        tickTrackDatabase = new TickTrackDatabase(activity);
        themeSet = tickTrackDatabase.getThemeMode();

        setupTheme();

        getWindow().getAttributes().windowAnimations = R.style.acceptDialog;
        Animation transition_in_view = AnimationUtils.loadAnimation(getContext(), R.anim.from_right);
        view.setAnimation( transition_in_view );
        view.startAnimation( transition_in_view );

    }
}
