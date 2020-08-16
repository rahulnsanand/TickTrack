package com.theflopguyproductions.ticktrack.ui.settings;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;

import java.util.Objects;

public class ThemeDialog extends Dialog {

    TickTrackDatabase tickTrackDatabase;

    private Activity activity;
    private RadioGroup themeGroup;
    private RadioButton darkButton, lightButton;
    private ConstraintLayout rootLayout;
    private int themeMode;
    private TextView titleText;

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


        tickTrackDatabase = new TickTrackDatabase(activity);



        themeGroup = view.findViewById(R.id.themeSettingRadioGroup);
        darkButton = view.findViewById(R.id.darkThemeRadioButton);
        lightButton = view.findViewById(R.id.lightThemeRadioButton);
        rootLayout = view.findViewById(R.id.themeSettingDialogRootLayout);
        titleText = view.findViewById(R.id.themeSettingDialogTitle);

        setupTheme();

        getWindow().getAttributes().windowAnimations = R.style.acceptDialog;
        Animation transition_in_view = AnimationUtils.loadAnimation(getContext(), R.anim.from_right);
        view.setAnimation( transition_in_view );
        view.startAnimation( transition_in_view );

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
            rootLayout.setBackgroundResource(R.color.LightGray);
            darkButton.setTextColor(activity.getResources().getColor(R.color.DarkText));
            lightButton.setTextColor(activity.getResources().getColor(R.color.DarkText));
            titleText.setTextColor(activity.getResources().getColor(R.color.DarkText));
            lightButton.setChecked(true);
        } else {
            rootLayout.setBackgroundResource(R.color.Gray);
            darkButton.setTextColor(activity.getResources().getColor(R.color.LightText));
            lightButton.setTextColor(activity.getResources().getColor(R.color.LightText));
            titleText.setTextColor(activity.getResources().getColor(R.color.LightText));
            darkButton.setChecked(true);
        }
    }

}
