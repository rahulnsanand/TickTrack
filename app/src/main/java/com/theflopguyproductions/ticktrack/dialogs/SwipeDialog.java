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

public class SwipeDialog extends BottomSheetDialog {

    private Activity activity;
    private TickTrackDatabase tickTrackDatabase;
    private ConstraintLayout rootLayout;
    public TextView dialogTitle, dialogMessage;
    public Button swipeButton;
    public Button dismissButton;
    int themeSet = 1;

    public SwipeDialog(Activity activity){
        super(activity);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_swipe, new ConstraintLayout(activity), false);
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

        tickTrackDatabase = new TickTrackDatabase(getContext());
        themeSet = tickTrackDatabase.getThemeMode();

        setupTheme();
    }

    private void setupTheme() {

        if(themeSet==1){
            rootLayout.setBackgroundResource(R.color.LightGray);
            dialogMessage.setTextColor(activity.getResources().getColor(R.color.LightDarkText));
            dismissButton.setBackgroundResource(R.drawable.button_selector_white);
            dismissButton.setTextColor(activity.getResources().getColor(R.color.DarkText) );

        } else {
            rootLayout.setBackgroundResource(R.color.Gray);
            dialogMessage.setTextColor(activity.getResources().getColor(R.color.DarkLightText));
            dismissButton.setBackgroundResource(R.drawable.button_selector_dark);
            dismissButton.setTextColor(activity.getResources().getColor(R.color.LightText) );
        }
    }

    private void initVariables(View view) {
        dialogMessage = view.findViewById(R.id.swipeDialogMessageText);
        rootLayout = view.findViewById(R.id.swipeDialogRootLayout);
        dialogTitle = view.findViewById(R.id.swipeDialogTitleText);
        swipeButton = view.findViewById(R.id.swipeDialogSwipeButton);
        dismissButton = view.findViewById(R.id.swipeDialogDismissButton);
    }


}
