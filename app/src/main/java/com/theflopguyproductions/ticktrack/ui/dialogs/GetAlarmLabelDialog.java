package com.theflopguyproductions.ticktrack.ui.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.ui.home.activity.alarm.CreateAlarmActivity;

import java.util.Objects;

public class GetAlarmLabelDialog extends Dialog {

    public Activity activity;
    private EditText alarmLabel;
    public Button yesButton;
    public Button noButton;

    public GetAlarmLabelDialog(Activity activity){
        super(activity);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        View view = activity.getLayoutInflater().inflate(R.layout.alarm_label_dialog, new ConstraintLayout(activity), false);
        setContentView(view);
        Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        yesButton = view.findViewById(R.id.okLabel);
        noButton = view.findViewById(R.id.cancelLabel);
        alarmLabel = view.findViewById(R.id.alarmLabelField);
        getWindow().getAttributes().windowAnimations = R.style.createdDialog;

        yesButton.setOnClickListener(view12 -> {
            ((CreateAlarmActivity) activity).setLabel(alarmLabel.getText().toString());
            dismiss();
        });
        noButton.setOnClickListener(view1 -> dismiss());
        setOnCancelListener(dialogInterface -> dismiss());
    }

}
