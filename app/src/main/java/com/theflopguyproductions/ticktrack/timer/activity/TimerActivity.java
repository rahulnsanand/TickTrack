package com.theflopguyproductions.ticktrack.timer.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.SoYouADeveloperHuh;
import com.theflopguyproductions.ticktrack.dialogs.DeleteTimer;
import com.theflopguyproductions.ticktrack.dialogs.TimerEditDialog;
import com.theflopguyproductions.ticktrack.timer.TimerData;
import com.theflopguyproductions.ticktrack.ui.timer.TimerRecyclerFragment;
import com.theflopguyproductions.ticktrack.ui.utils.TickTrackProgressBar;
import com.theflopguyproductions.ticktrack.utils.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.TickTrackThemeSetter;
import com.theflopguyproductions.ticktrack.utils.TimeAgo;

import java.util.ArrayList;
import java.util.Locale;

public class TimerActivity extends AppCompatActivity {

    private String timerID = null;
    private ArrayList<TimerData> timerDataArrayList = new ArrayList<>();
    private int currentPosition = 0;
    private ConstraintLayout timerActivityToolbar, timerActivityRootLayout;
    private TextView timerHourMinute, timerMillis;
    private TickTrackProgressBar timerProgressBar, timerProgressBarBackground;
    private FloatingActionButton playPauseFAB, resetFAB;
    private ImageButton backButton, editButton, deleteButton;
    private TextView labelTextView;
    private Activity activity;
    private boolean isRunning = false, isPause = false;
    private CountDownTimer countDownTimer;

    private long timerLeftMillis, timerMaxLength;

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
            isRunning = timerDataArrayList.get(currentPosition).isTimerOn();
            isPause = timerDataArrayList.get(currentPosition).isTimerPause();
            TickTrackThemeSetter.timerActivityTheme(this, timerActivityToolbar, timerDataArrayList.get(currentPosition).getTimerFlag() ,
                    timerActivityRootLayout, timerHourMinute, timerMillis,timerProgressBarBackground);

            prefixValues();

            setOnClickListeners();

            updateTimerText();

            timerProgressBarBackground.setInstantProgress((float) 1);


            if(isRunning && !isPause){

                playPauseFAB.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_round_pause_white_24));
                startTimer();
            } else if(isPause){
                prefixPauseValues();
            } else {
                timerProgressBar.setInstantProgress((float) 1);
                playPauseFAB.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_round_play_white_24));
            }



        } else {
            onBackPressed();
        }
    }

    private void prefixPauseValues() {
        int hours = timerDataArrayList.get(currentPosition).getTimerHourLeft();
        int minutes = timerDataArrayList.get(currentPosition).getTimerMinuteLeft();
        int seconds = timerDataArrayList.get(currentPosition).getTimerSecondLeft();
        int milliseconds = timerDataArrayList.get(currentPosition).getTimerMilliSecondLeft();

        timerLeftMillis = TimeAgo.getTimerDataInMillis(hours,minutes,seconds);
        timerLeftMillis += milliseconds;

        timerProgressBar.setInstantProgress(getCurrentStep(timerLeftMillis, timerMaxLength));

        String timeLeft = String.format(Locale.getDefault(),"%02d:%02d:%02d", hours,minutes,seconds);
        String milliSecondLeft = String.format(Locale.getDefault(), "%02d", milliseconds);
        timerHourMinute.setText(timeLeft);
        timerMillis.setText(milliSecondLeft);
        playPauseFAB.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_round_play_white_24));
        resetFAB.setVisibility(View.VISIBLE);

    }

    private float getCurrentStep(long currentValue, long maxLength){
        return ((currentValue-0f)/(maxLength-0f)) *(1f-0f)+0f;
    }

    public void startTimer(){
        countDownTimer = new CountDownTimer(timerLeftMillis, 10) {
            @Override
            public void onTick(long l) {
                timerLeftMillis = l;
                updateTimerText();
                timerProgressBar.setProgress(getCurrentStep(timerLeftMillis, timerMaxLength));
            }
            @Override
            public void onFinish() {
                resetTimer();
                playPauseFAB.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_round_play_white_24));
                timerDataArrayList.get(currentPosition).setTimerOn(false);
                timerDataArrayList.get(currentPosition).setTimerPause(false);
                TickTrackDatabase.storeTimerList(timerDataArrayList,activity);
                isPause = timerDataArrayList.get(currentPosition).isTimerPause();
                isRunning = timerDataArrayList.get(currentPosition).isTimerOn();
            }
        }.start();
        timerDataArrayList.get(currentPosition).setTimerOn(true);
        timerDataArrayList.get(currentPosition).setTimerPause(false);
        TickTrackDatabase.storeTimerList(timerDataArrayList,activity);
        isPause = timerDataArrayList.get(currentPosition).isTimerPause();
        isRunning = timerDataArrayList.get(currentPosition).isTimerOn();
        playPauseFAB.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_round_pause_white_24));
        resetFAB.setVisibility(View.INVISIBLE);
    }

    private void updateTimerText() {

        int hours = (int) (timerLeftMillis/(1000*60*60));
        int minutes = (int) (timerLeftMillis%(1000*60*60))/(1000*60);
        int seconds = (int) (timerLeftMillis%(1000*60*60))%(1000*60)/1000;
        int milliseconds = (int) (timerLeftMillis%(1000*60*60))%(1000*60)/10%100;

        String timeLeft = String.format(Locale.getDefault(),"%02d:%02d:%02d", hours,minutes,seconds);
        String milliSecondLeft = String.format(Locale.getDefault(), "%02d", milliseconds);
        timerHourMinute.setText(timeLeft);
        timerMillis.setText(milliSecondLeft);
    }

    public void pauseTimer(){
        countDownTimer.cancel();

        int hours = (int) (timerLeftMillis/(1000*60*60));
        int minutes = (int) (timerLeftMillis%(1000*60*60))/(1000*60);
        int seconds = (int) (timerLeftMillis%(1000*60*60))%(1000*60)/1000;
        int milliseconds = (int) (timerLeftMillis%(1000*60*60))%(1000*60)/10%100;

        playPauseFAB.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_round_play_white_24));
        resetFAB.setVisibility(View.VISIBLE);
        timerDataArrayList.get(currentPosition).setTimerOn(true);
        timerDataArrayList.get(currentPosition).setTimerPause(true);
        timerDataArrayList.get(currentPosition).setTimerHourLeft(hours);
        timerDataArrayList.get(currentPosition).setTimerMinuteLeft(minutes);
        timerDataArrayList.get(currentPosition).setTimerSecondLeft(seconds);
        timerDataArrayList.get(currentPosition).setTimerMilliSecondLeft(milliseconds);
        TickTrackDatabase.storeTimerList(timerDataArrayList,activity);

        isPause = timerDataArrayList.get(currentPosition).isTimerPause();
        isRunning = timerDataArrayList.get(currentPosition).isTimerOn();
    }

    public void resetTimer(){

        timerLeftMillis = timerDataArrayList.get(currentPosition).getTimeLeftInMillis();
        updateTimerText();
        timerProgressBar.setProgress(1);
        resetFAB.setVisibility(View.INVISIBLE);

        timerDataArrayList.get(currentPosition).setTimerOn(false);
        timerDataArrayList.get(currentPosition).setTimerPause(false);
        TickTrackDatabase.storeTimerList(timerDataArrayList,activity);
    }

    private void prefixValues() {

        if(!timerDataArrayList.get(currentPosition).getTimerLabel().equals("Set label")){
            labelTextView.setText(timerDataArrayList.get(currentPosition).getTimerLabel());
        } else {
            labelTextView.setText("Timer");
        }

        timerLeftMillis = timerDataArrayList.get(currentPosition).getTimeLeftInMillis();
        timerMaxLength = timerLeftMillis;
        timerProgressBar.setLinearProgress(true);
        timerProgressBar.setSpinSpeed(2.500f);
        timerProgressBarBackground.setLinearProgress(true);
        timerProgressBarBackground.setSpinSpeed(2.500f);
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

        playPauseFAB.setOnClickListener(view -> {
            if(isRunning && !isPause){
                pauseTimer();
            } else {
                startTimer();
            }
        });

        resetFAB.setOnClickListener(view -> resetTimer());

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
        timerHourMinute = findViewById(R.id.timerActivityTimeLeftTextView);
        timerMillis = findViewById(R.id.timerActivityTimeLeftMillisTextView);
        timerProgressBar = findViewById(R.id.timerActivityTimerProgressBar);
        timerProgressBarBackground = findViewById(R.id.timerActivityTimerProgressBarStatic);
        playPauseFAB = findViewById(R.id.timerActivityPlayPauseFAB);
        resetFAB = findViewById(R.id.timerActivityResetFAB);
        backButton = findViewById(R.id.timerActivityBackImageButton);
        editButton = findViewById(R.id.timerActivityEditImageButton);
        deleteButton = findViewById(R.id.timerActivityDeleteImageButton);
        labelTextView = findViewById(R.id.timerActivityLabelTextView);




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