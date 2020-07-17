package com.theflopguyproductions.ticktrack.ui.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.ui.home.activity.alarm.CreateAlarmActivity;

import java.util.Objects;

public class YesNoDialog extends Dialog {

    Activity activity;

    public YesNoDialog(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    private TextView dialogTitle;
    public Button yesButton;
    public Button noButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        View view = activity.getLayoutInflater().inflate(R.layout.yes_no_dialog, new ConstraintLayout(activity), false);
        setContentView(view);
        Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        yesButton = view.findViewById(R.id.yesDialogButton);
        noButton = view.findViewById(R.id.noDialogButton);
        getWindow().getAttributes().windowAnimations = R.style.createdDialog;

        yesButton.setOnClickListener(view12 -> {
            ((CreateAlarmActivity)getContext()).saveAlarm();
            dismiss();
        });
        noButton.setOnClickListener(view1 -> {
            ((CreateAlarmActivity)getContext()).discardAlarm();
            dismiss();
        });
        setOnCancelListener(dialogInterface -> dismiss());
    }

}
