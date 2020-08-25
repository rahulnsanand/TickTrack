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
import android.widget.Button;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;

import java.util.Objects;

public class SettingsAccountLayout  extends Dialog {

    private Activity activity;

    private ConstraintLayout rootLayout;
    public ConstraintLayout addAccountLayout, disconnectAccountLayout, switchAccountLayout;
    private TextView switchAccountText, disconnectAccountText, addAccountText, currentMailText;
    private Button cancelButton;
    private TickTrackDatabase tickTrackDatabase;
    int themeSet = 1;

    public SettingsAccountLayout(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    private void initVariables(View view) {
        cancelButton = view.findViewById(R.id.dialogSettingsAccountCancelButton);
        switchAccountText = view.findViewById(R.id.dialogSettingsAccountSwitchAccountTextView);
        disconnectAccountText = view.findViewById(R.id.dialogSettingsAccountDisconnectAccountTextView);
        addAccountText = view.findViewById(R.id.dialogSettingsAccountAddTextView);
        currentMailText = findViewById(R.id.dialogSettingsAccountCurrentEmailTextView);
        rootLayout = findViewById(R.id.dialogSettingsAccountRootLayout);
        disconnectAccountLayout = findViewById(R.id.dialogSettingsAccountDisconnectAccountLayout);
        addAccountLayout = findViewById(R.id.dialogSettingsAccountAddAcountLayout);
        switchAccountLayout = findViewById(R.id.dialogSettingsAccountSwitchAccountLayout);
    }

    private void setupTheme() {
        if(themeSet==1){
            rootLayout.setBackgroundResource(R.color.LightGray);
            switchAccountText.setTextColor(activity.getResources().getColor(R.color.DarkText));
            disconnectAccountText.setTextColor(activity.getResources().getColor(R.color.DarkText));
            addAccountText.setTextColor(activity.getResources().getColor(R.color.DarkText));
            currentMailText.setTextColor(activity.getResources().getColor(R.color.DarkText));
            cancelButton.setBackgroundResource(R.drawable.button_selector_light);
            addAccountLayout.setBackgroundResource(R.drawable.clickable_layout_light_background);
            disconnectAccountLayout.setBackgroundResource(R.drawable.clickable_layout_light_background);
            switchAccountLayout.setBackgroundResource(R.drawable.clickable_layout_light_background);
        } else {
            rootLayout.setBackgroundResource(R.color.Gray);
            switchAccountText.setTextColor(activity.getResources().getColor(R.color.LightText));
            disconnectAccountText.setTextColor(activity.getResources().getColor(R.color.LightText));
            addAccountText.setTextColor(activity.getResources().getColor(R.color.LightText));
            currentMailText.setTextColor(activity.getResources().getColor(R.color.LightText));
            cancelButton.setBackgroundResource(R.drawable.button_selector_dark);
            addAccountLayout.setBackgroundResource(R.drawable.clickable_layout_dark_background);
            disconnectAccountLayout.setBackgroundResource(R.drawable.clickable_layout_dark_background);
            switchAccountLayout.setBackgroundResource(R.drawable.clickable_layout_dark_background);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_settings_account_layout, new ConstraintLayout(activity), false);
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

        cancelButton.setOnClickListener(view1 -> dismiss());

    }
}
