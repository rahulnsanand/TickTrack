package com.theflopguyproductions.ticktrack.timer.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.SoYouADeveloperHuh;
import com.theflopguyproductions.ticktrack.application.TickTrack;
import com.theflopguyproductions.ticktrack.dialogs.DeleteTimer;
import com.theflopguyproductions.ticktrack.dialogs.TimerEditDialog;
import com.theflopguyproductions.ticktrack.timer.TimerData;
import com.theflopguyproductions.ticktrack.ui.timer.TimerRecyclerFragment;
import com.theflopguyproductions.ticktrack.ui.utils.TickTrackChronometer;
import com.theflopguyproductions.ticktrack.ui.utils.TickTrackProgressBar;
import com.theflopguyproductions.ticktrack.utils.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.TickTrackThemeSetter;

import java.util.ArrayList;

public class TimerActivity extends AppCompatActivity {

    private String timerID = null;
    private ArrayList<TimerData> timerDataArrayList = new ArrayList<>();
    private int currentPosition = 0;
    private ConstraintLayout timerActivityToolbar, timerActivityRootLayout;
    private TickTrackChronometer timerChronometer;
    private TickTrackProgressBar timerProgressBar, timerProgressBarBackground;
    private FloatingActionButton playPauseFAB, resetFAB;
    private ImageButton backButton, editButton, deleteButton;
    private TextView labelTextView;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        timerID = getIntent().getStringExtra("timerID");
        activity = this;
        timerDataArrayList = TickTrackDatabase.retrieveTimerList(activity);
        initVariables();
        if(timerID!=null){
            currentPosition = getCurrentPosition(timerID);
            TickTrackThemeSetter.timerActivityTheme(this, timerActivityToolbar, timerDataArrayList.get(currentPosition).getTimerFlag() , timerActivityRootLayout, timerChronometer,
                    timerProgressBarBackground);

            prefixValues();

            setOnClickListeners();

        } else {
            onBackPressed();
        }
    }

    private void prefixValues() {

        if(!timerDataArrayList.get(currentPosition).getTimerLabel().equals("Set label")){
            labelTextView.setText(timerDataArrayList.get(currentPosition).getTimerLabel());
        } else {
            labelTextView.setText("Timer");
        }


    }

    private void setOnClickListeners() {

        backButton.setOnClickListener(view -> onBackPressed());

        editButton.setOnClickListener(view -> {
            TimerEditDialog timerEditDialog = new TimerEditDialog(activity, timerDataArrayList.get(currentPosition).getTimerLabel(), timerDataArrayList.get(currentPosition).getTimerFlag());
            timerEditDialog.show();

            timerEditDialog.saveButton.setOnClickListener(view1 -> {
                if(timerEditDialog.labelInput.getText().toString().trim().length()>0){
                    timerDataArrayList.get(currentPosition).setTimerLabel(timerEditDialog.labelInput.getText().toString());
                    labelTextView.setText(timerEditDialog.labelInput.getText().toString());
                }
                if(timerEditDialog.currentFlag!=timerDataArrayList.get(currentPosition).getTimerFlag()){
                    timerDataArrayList.get(currentPosition).setTimerFlag(timerEditDialog.currentFlag);
                    timerActivityToolbar.setBackgroundResource(timerActivityToolbarColor(timerEditDialog.currentFlag));
                }
                TickTrackDatabase.storeTimerList(timerDataArrayList,activity);
                timerEditDialog.dismiss();
            });
            timerEditDialog.cancelButton.setOnClickListener(view12 -> timerEditDialog.dismiss());
        });
        String deletedTimer = timerDataArrayList.get(currentPosition).getTimerLabel();
        deleteButton.setOnClickListener(view -> {
            DeleteTimer deleteTimer = new DeleteTimer(activity);
            deleteTimer.show();
            if(!deletedTimer.equals("Set label")){
                deleteTimer.dialogMessage.setText("Delete timer "+deletedTimer+"?");
            } else {
                deleteTimer.dialogMessage.setText("Delete timer ?");
            }
            deleteTimer.yesButton.setOnClickListener(view1 -> {
                TimerRecyclerFragment.deleteTimer(currentPosition, activity, deletedTimer);
                onBackPressed();
                deleteTimer.dismiss();
            });
            deleteTimer.noButton.setOnClickListener(view1 -> {
                TimerRecyclerFragment.refreshRecyclerView();
                deleteTimer.dismiss();
            });
            deleteTimer.setOnCancelListener(dialogInterface -> {
                TimerRecyclerFragment.refreshRecyclerView();
                deleteTimer.cancel();
            });
        });

    }

    private static int timerActivityToolbarColor(int flagColor){
        if(flagColor == 1){
            return R.color.red_matte;
        } else if(flagColor == 2){
            return R.color.green_matte;
        } else if(flagColor == 3){
            return R.color.orange_matte;
        } else if(flagColor == 4){
            return R.color.purple_matte;
        } else if(flagColor == 5){
            return R.color.blue_matte;
        }
        return R.color.Accent;
    }
    private void initVariables() {

        timerActivityRootLayout = findViewById(R.id.timerActivityRootLayout);
        timerActivityToolbar = findViewById(R.id.timerActivityToolbarLayout);
        timerChronometer = findViewById(R.id.timerActivityTimeLeftTextView);
        timerProgressBar = findViewById(R.id.timerActivityTimerProgressBar);
        timerProgressBarBackground = findViewById(R.id.timerActivityTimerProgressBarStatic);
        playPauseFAB = findViewById(R.id.timerActivityPlayPauseFAB);
        resetFAB = findViewById(R.id.timerActivityResetFAB);
        backButton = findViewById(R.id.timerActivityBackImageButton);
        editButton = findViewById(R.id.timerActivityEditImageButton);
        deleteButton = findViewById(R.id.timerActivityDeleteImageButton);
        labelTextView = findViewById(R.id.timerActivityLabelTextView);

        timerChronometer.setTypeface(ResourcesCompat.getFont(this,R.font.montserrat_medium));

        timerProgressBar.setProgress(50f);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this,SoYouADeveloperHuh.class);
        intent.putExtra("FragmentID", 2);
        startActivity(intent);
    }

    private int getCurrentPosition(String timerID){
        for(int i =0; i < timerDataArrayList.size(); i++){
            if(timerDataArrayList.get(i).getTimerID().equals(timerID)){
                return i;
            }
        }
        return 0;
    }

}