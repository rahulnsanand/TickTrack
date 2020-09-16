package com.theflopguyproductions.ticktrack.dialogs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.theflopguyproductions.ticktrack.SoYouADeveloperHuh;
import com.theflopguyproductions.ticktrack.counter.notification.CounterNotificationService;
import com.theflopguyproductions.ticktrack.ui.counter.CounterFragment;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;

import java.util.Objects;

public class DeleteCounterFromActivity extends BottomSheetDialog {

    TickTrackDatabase tickTrackDatabase;

    public Activity activity;
    int position, themeSet = 1;
    private String counterName, counterID;
    TextView dialogMessage, dialogTitle;
    SharedPreferences sharedPreferences;
    private ConstraintLayout rootLayout;
    SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener;

    public DeleteCounterFromActivity(Activity activity, int position, String counterName, String counterID, SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener){
        super(activity);
        this.counterName = counterName;
        this.position = position;
        this.activity = activity;
        this.counterID = counterID;
        this.sharedPreferenceChangeListener = sharedPreferenceChangeListener;
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
        tickTrackDatabase = new TickTrackDatabase(activity);

        sharedPreferences  = tickTrackDatabase.getSharedPref(activity);

        yesButton = view.findViewById(R.id.acceptDeleteCounterButton);
        noButton = view.findViewById(R.id.dismissDeleteCounterButton);
        dialogMessage = view.findViewById(R.id.deleteCounterTextView);
        rootLayout = view.findViewById(R.id.counterDeleteRootLayout);
        dialogTitle = view.findViewById(R.id.textView);
        dialogMessage.setText("Delete counter "+counterName+"?");
        themeSet = tickTrackDatabase.getThemeMode();

        setupTheme();

        yesButton.setOnClickListener(view12 -> {
            killNotification();
            CounterFragment.deleteCounter(position, activity, counterName);
            activity.startActivity(new Intent(activity, SoYouADeveloperHuh.class));
            dismiss();
        });
        noButton.setOnClickListener(view1 -> {
            cancel();
        });
    }

    private void setupTheme() {
        if(themeSet==1){
            rootLayout.setBackgroundResource(R.color.LightGray);
            dialogTitle.setTextColor(activity.getResources().getColor(R.color.DarkText));
//            yesButton.setBackgroundResource(R.drawable.button_selector_light);
            noButton.setBackgroundResource(R.drawable.button_selector_light);
            dialogMessage.setTextColor(activity.getResources().getColor(R.color.DarkText));
        } else {
            rootLayout.setBackgroundResource(R.color.Gray);
            dialogTitle.setTextColor(activity.getResources().getColor(R.color.LightText));
//            yesButton.setBackgroundResource(R.drawable.button_selector_dark);
            noButton.setBackgroundResource(R.drawable.button_selector_dark);
            dialogMessage.setTextColor(activity.getResources().getColor(R.color.LightText));
        }
    }

    public void killNotification() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
        if(counterID.equals(tickTrackDatabase.getCurrentCounterNotificationID())){
            Intent intent = new Intent(activity, CounterNotificationService.class);
            intent.setAction(CounterNotificationService.ACTION_KILL_NOTIFICATIONS);
            activity.startService(intent);
        }
    }

    public Button yesButton;
    public Button noButton;

}
