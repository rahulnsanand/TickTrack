package com.theflopguyproductions.ticktrack.ui.settings;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.ui.counter.CounterFragment;
import com.theflopguyproductions.ticktrack.utils.TickTrackDatabase;

import java.sql.Timestamp;
import java.util.Objects;

public class ThemeDialog extends Dialog {

    TickTrackDatabase tickTrackDatabase;

    private Activity activity;
    private RadioGroup themeGroup;
    private RadioButton darkButton, lightButton;
    private int themeMode;

    public ThemeDialog(Activity activity, int themeMode) {
        super(activity);
        this.activity = activity;
        this.themeMode = themeMode;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_theme_selector, new ConstraintLayout(activity), false);
        setContentView(view);

        Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().getAttributes().windowAnimations = R.style.createdDialog;

        tickTrackDatabase = new TickTrackDatabase(activity);

        themeGroup = view.findViewById(R.id.themeSettingRadioGroup);
        darkButton = view.findViewById(R.id.darkThemeRadioButton);
        lightButton = view.findViewById(R.id.lightThemeRadioButton);

        setupTheme();

        themeGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            if(themeGroup.getCheckedRadioButtonId()==R.id.darkThemeRadioButton){
                tickTrackDatabase.setThemeMode(2);

            } else {
                tickTrackDatabase.setThemeMode(1);
            }
            activity.startActivity(new Intent(activity,SettingsActivity.class));
        });

    }

    private void setupTheme() {
        if(themeMode == 1){
            lightButton.setChecked(true);
        } else {
            darkButton.setChecked(true);
        }
    }

}
