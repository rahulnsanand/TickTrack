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
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.ui.utils.TickTrackProgressBar;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;

import java.util.Objects;

public class ProgressBarDialog extends Dialog {

    private Activity activity;

    public TickTrackProgressBar tickTrackProgressBar;
    public TextView titleText, contentText;
    private ConstraintLayout rootLayout;
    private TickTrackDatabase tickTrackDatabase;
    private int themeSet = 1;

    public ProgressBarDialog(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    private void initVariables(View view) {
        titleText = view.findViewById(R.id.progressBarTitleTickTrack);
        contentText = view.findViewById(R.id.progressBarContentTickTrack);
        tickTrackProgressBar = view.findViewById(R.id.tickTrackProgressBar);
        rootLayout = findViewById(R.id.progressBarTickTrackRootLayout);
        tickTrackProgressBar.spin();
    }

    private void setupTheme() {
        if(themeSet==1){
            rootLayout.setBackgroundResource(R.color.LightGray);
            titleText.setTextColor(activity.getResources().getColor(R.color.DarkText));
            contentText.setTextColor(activity.getResources().getColor(R.color.DarkText));
        } else {
            rootLayout.setBackgroundResource(R.color.Gray);
            contentText.setTextColor(activity.getResources().getColor(R.color.LightText));
            titleText.setTextColor(activity.getResources().getColor(R.color.LightText));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_progress_bar_layout, new ConstraintLayout(activity), false);
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

        setCancelable(false);

    }

    public void setTitleText(String titleText){
        this.titleText.setText(titleText);
    }

    public void setContentText(String contentText){
        this.contentText.setText(contentText);
    }

}
