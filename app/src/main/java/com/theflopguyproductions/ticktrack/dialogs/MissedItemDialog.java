package com.theflopguyproductions.ticktrack.dialogs;

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
import android.widget.Button;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.startup.StartUpActivity;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;

import java.util.Objects;

public class MissedItemDialog extends Dialog {

    private TickTrackDatabase tickTrackDatabase;
    private Activity activity;
    private TextView detailTextView, missedText, almostMissedText;
    private ConstraintLayout rootLayout;
    private Button goToOptimise, goToCancel;
    private int missedTimers=0, almostMissedTimers=0;

    public MissedItemDialog(Activity activity, int missedTimers, int almostMissedTimers){
        super(activity);
        this.activity = activity;
        this.missedTimers = missedTimers;
        this.almostMissedTimers = almostMissedTimers;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = activity.getLayoutInflater().inflate(R.layout.fragment_missed_things, new ConstraintLayout(activity), false);
        setContentView(view);
        Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        tickTrackDatabase = new TickTrackDatabase(getContext());
        missedText = view.findViewById(R.id.ticktrackMissingDialogMissedText);
        almostMissedText = view.findViewById(R.id.ticktrackMissingDialogAlmostMissedTimer);
        detailTextView = view.findViewById(R.id.ticktrackMissingDialogDetailText);
        rootLayout = view.findViewById(R.id.ticktrackMissingDialogRootLayout);
        goToOptimise = view.findViewById(R.id.ticktrackMissingDialogStartUpTimer);
        goToCancel = view.findViewById(R.id.ticktrackMissingDialogCancelTimer);

        setupTheme();

        getWindow().getAttributes().windowAnimations = R.style.acceptDialog;
        Animation transition_in_view = AnimationUtils.loadAnimation(getContext(), R.anim.from_right);
        view.setAnimation( transition_in_view );
        view.startAnimation( transition_in_view );

        goToOptimise.setOnClickListener(view12 -> {
            goToStartUpActivity();
            dismiss();
        });

        goToCancel.setOnClickListener(view1 -> dismiss());

    }

    private void goToStartUpActivity() {
        tickTrackDatabase.storeNotOptimiseBool(false);
        tickTrackDatabase.storeStartUpFragmentID(3);
        Intent intent = new Intent(activity, StartUpActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
    }

    private void setupTheme() {

        if(missedTimers>0){
            missedText.setVisibility(View.VISIBLE);
        } else {
            missedText.setVisibility(View.GONE);
        }

        if(almostMissedTimers>0){
            almostMissedText.setVisibility(View.VISIBLE);
        } else {
            almostMissedText.setVisibility(View.GONE);
        }

        int themeSet = tickTrackDatabase.getThemeMode();

        if(themeSet==1){
            rootLayout.setBackgroundResource(R.color.LightGray);
            detailTextView.setTextColor(activity.getResources().getColor(R.color.DarkText));
            goToOptimise.setBackgroundResource(R.drawable.button_selector_light);
            goToCancel.setBackgroundResource(R.drawable.button_selector_light);
        } else {
            rootLayout.setBackgroundResource(R.color.Gray);
            detailTextView.setTextColor(activity.getResources().getColor(R.color.LightText));
            goToOptimise.setBackgroundResource(R.drawable.button_selector_dark);
            goToCancel.setBackgroundResource(R.drawable.button_selector_dark);
        }
    }

}
