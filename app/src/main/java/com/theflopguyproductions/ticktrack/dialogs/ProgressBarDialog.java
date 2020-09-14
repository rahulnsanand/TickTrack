package com.theflopguyproductions.ticktrack.dialogs;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.ui.utils.TickTrackProgressBar;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;

import java.util.Objects;

public class ProgressBarDialog extends BottomSheetDialog {

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

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_progress_bar_layout, new ConstraintLayout(activity), false);
        setContentView(view);
        Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width = Math.min(metrics.widthPixels, 1280);
        int height = -1; // MATCH_PARENT
        getWindow().setLayout(width, height);

        initVariables(view);

        tickTrackDatabase = new TickTrackDatabase(activity);
        themeSet = tickTrackDatabase.getThemeMode();

        setupTheme();

        setCancelable(false);

    }

    public void setTitleText(String titleText){
        this.titleText.setText(titleText);
    }

    public void setContentText(String contentText){
        this.contentText.setText(contentText);
    }

}
