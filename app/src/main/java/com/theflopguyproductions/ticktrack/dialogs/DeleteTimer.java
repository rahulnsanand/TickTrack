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
import android.widget.Button;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;

import java.util.Objects;

public class DeleteTimer extends BottomSheetDialog {
    public Activity activity;
    private TickTrackDatabase tickTrackDatabase;
    public TextView dialogMessage, dialogTitle;
    private ConstraintLayout rootLayout;
    int themeSet = 1;

    public DeleteTimer(Activity activity){
        super(activity);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_counter_deletor, new ConstraintLayout(activity), false);
        setContentView(view);
        Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width = Math.min(metrics.widthPixels, 1280);
        int height = -1; // MATCH_PARENT
        getWindow().setLayout(width, height);
        tickTrackDatabase = new TickTrackDatabase(getContext());

        yesButton = view.findViewById(R.id.acceptDeleteCounterButton);
        noButton = view.findViewById(R.id.dismissDeleteCounterButton);
        dialogMessage = view.findViewById(R.id.deleteCounterTextView);
        rootLayout = view.findViewById(R.id.counterDeleteRootLayout);
        dialogTitle = view.findViewById(R.id.textView);
        themeSet = tickTrackDatabase.getThemeMode();

        setupTheme();


    }

    private void setupTheme() {
        if(themeSet==1){
            rootLayout.setBackgroundResource(R.color.LightGray);
            dialogTitle.setTextColor(activity.getResources().getColor(R.color.DarkText));
            yesButton.setBackgroundResource(R.drawable.button_selector_light);
            noButton.setBackgroundResource(R.drawable.button_selector_light);
            dialogMessage.setTextColor(activity.getResources().getColor(R.color.DarkText));
        } else {
            rootLayout.setBackgroundResource(R.color.Gray);
            dialogTitle.setTextColor(activity.getResources().getColor(R.color.LightText));
            yesButton.setBackgroundResource(R.drawable.button_selector_dark);
            noButton.setBackgroundResource(R.drawable.button_selector_dark);
            dialogMessage.setTextColor(activity.getResources().getColor(R.color.LightText));
        }
    }

    public Button yesButton;
    public Button noButton;

}
