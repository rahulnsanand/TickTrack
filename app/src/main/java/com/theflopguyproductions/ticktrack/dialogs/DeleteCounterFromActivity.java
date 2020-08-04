package com.theflopguyproductions.ticktrack.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.SoYouADeveloperHuh;
import com.theflopguyproductions.ticktrack.application.TickTrack;
import com.theflopguyproductions.ticktrack.counter.activity.CounterActivity;
import com.theflopguyproductions.ticktrack.counter.notification.CounterNotificationService;
import com.theflopguyproductions.ticktrack.ui.counter.CounterFragment;
import com.theflopguyproductions.ticktrack.utils.TickTrackDatabase;

import java.util.Objects;

public class DeleteCounterFromActivity extends Dialog {

    TickTrackDatabase tickTrackDatabase;

    public Activity activity;
    int position;
    private String counterName, counterID;
    TextView dialogMessage;
    SharedPreferences sharedPreferences;
    SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener;

    public DeleteCounterFromActivity(Activity activity, int position, String counterName, String counterID, SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener){
        super(activity);
        this.counterName = counterName;
        this.position = position;
        this.activity = activity;
        this.counterID = counterID;
        this.sharedPreferences = activity.getSharedPreferences("TickTrackData", Context.MODE_PRIVATE);
        this.sharedPreferenceChangeListener = sharedPreferenceChangeListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_counter_deletor, new ConstraintLayout(activity), false);
        setContentView(view);
        Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        tickTrackDatabase = new TickTrackDatabase(activity);

        yesButton = view.findViewById(R.id.acceptDeleteCounterButton);
        noButton = view.findViewById(R.id.dismissDeleteCounterButton);
        dialogMessage = view.findViewById(R.id.deleteCounterTextView);

        dialogMessage.setText("Delete counter "+counterName+"?");
        getWindow().getAttributes().windowAnimations = R.style.createdDialog;

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

    public void killNotification() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
        if(tickTrackDatabase.getCurrentCounterNotificationID().equals(counterID)){
            Intent intent = new Intent(activity, CounterNotificationService.class);
            intent.setAction(CounterNotificationService.ACTION_KILL_NOTIFICATIONS);
            activity.startService(intent);
        }
    }

    public Button yesButton;
    public Button noButton;

}
