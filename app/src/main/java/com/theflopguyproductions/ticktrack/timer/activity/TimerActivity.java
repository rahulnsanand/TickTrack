package com.theflopguyproductions.ticktrack.timer.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.SoYouADeveloperHuh;
import com.theflopguyproductions.ticktrack.dialogs.DeleteTimer;
import com.theflopguyproductions.ticktrack.dialogs.TimerEditDialog;
import com.theflopguyproductions.ticktrack.timer.TimerData;
import com.theflopguyproductions.ticktrack.timer.service.TimerService;
import com.theflopguyproductions.ticktrack.ui.timer.TimerRecyclerFragment;
import com.theflopguyproductions.ticktrack.ui.utils.TickTrackProgressBar;
import com.theflopguyproductions.ticktrack.utils.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.TickTrackThemeSetter;
import com.theflopguyproductions.ticktrack.utils.TickTrackTimerDatabase;
import com.theflopguyproductions.ticktrack.utils.TimeAgo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class TimerActivity extends AppCompatActivity {

    public static final String ACTION_TIMER_NEW_ADDITION = "ACTION_TIMER_NEW_ADDITION";

    TickTrackTimerDatabase tickTrackTimerDatabase;
    TickTrackDatabase tickTrackDatabase;

    private ConstraintLayout timerActivityToolbar, timerActivityRootLayout;
    private TextView timerHourMinute, timerMillis, labelTextView;
    private FloatingActionButton playPauseFAB, resetFAB;
    private ImageButton backButton, editButton, deleteButton;
    private TickTrackProgressBar timerProgressBar, timerProgressBarBackground;

    private int timerID, timerCurrentPosition;
    private Activity activity;
    private boolean isTimerRunning, isTimerPaused, isTimerNew = false, isTimerRinging;

    private ArrayList<TimerData> timerDataArrayList = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    float resumeMilliseconds;
    private int pauseHours, pauseMinutes, pauseSeconds, pauseMilliseconds;

    private long maxTimeInMillis, currentTimeInMillis;

    private CountDownTimer countDownTimer;
    private Handler progressBarPreHandler = new Handler();

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

        timerProgressBar.setLinearProgress(true);
        timerProgressBar.setSpinSpeed(2.500f);
        timerProgressBarBackground.setLinearProgress(true);
        timerProgressBarBackground.setSpinSpeed(2.500f);
        timerProgressBarBackground.setInstantProgress(1);


    }
    private void setupOnClickListeners(){
        backButton.setOnClickListener(view -> onBackPressed());
        editButton.setOnClickListener(view -> {
            TimerEditDialog timerEditDialog = new TimerEditDialog(activity, timerDataArrayList.get(timerCurrentPosition).getTimerLabel(), timerDataArrayList.get(timerCurrentPosition).getTimerFlag());
            timerEditDialog.show();

            timerEditDialog.saveButton.setOnClickListener(view1 -> {
                if(timerEditDialog.labelInput.getText().toString().trim().length()>0){
                    timerDataArrayList.get(timerCurrentPosition).setTimerLabel(timerEditDialog.labelInput.getText().toString());
                    labelTextView.setText(timerEditDialog.labelInput.getText().toString());
                }
                if(timerEditDialog.currentFlag!=timerDataArrayList.get(timerCurrentPosition).getTimerFlag()){
                    timerDataArrayList.get(timerCurrentPosition).setTimerFlag(timerEditDialog.currentFlag);
                    timerActivityToolbar.setBackgroundResource(timerActivityToolbarColor(timerEditDialog.currentFlag));
                }
                tickTrackDatabase.storeTimerList(timerDataArrayList);
                timerEditDialog.dismiss();
            });
            timerEditDialog.cancelButton.setOnClickListener(view12 -> timerEditDialog.dismiss());
        });
        String deletedTimer = timerDataArrayList.get(timerCurrentPosition).getTimerLabel();
        deleteButton.setOnClickListener(view -> {
            DeleteTimer deleteTimer = new DeleteTimer(activity);
            deleteTimer.show();
            if(!deletedTimer.equals("Set label")){
                deleteTimer.dialogMessage.setText("Delete timer "+deletedTimer+"?");
            } else {
                deleteTimer.dialogMessage.setText("Delete timer ?");
            }
            deleteTimer.yesButton.setOnClickListener(view1 -> {
                TimerRecyclerFragment.deleteTimer(timerCurrentPosition, activity, deletedTimer);
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
    private int getCurrentTimerPosition() {
        for(int i = 0; i < timerDataArrayList.size(); i ++){
            if(timerDataArrayList.get(i).getTimerID()==timerID){
                return i;
            }
        }
        return -1;
    }
    private float getCurrentStep(long currentValue, long maxLength){
        return ((currentValue-0f)/(maxLength-0f)) *(1f-0f)+0f;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        initVariables();

        timerID = getIntent().getIntExtra("timerID", -1);
        isTimerNew = Objects.equals(getIntent().getAction(), ACTION_TIMER_NEW_ADDITION);

        activity = this;
        tickTrackTimerDatabase = new TickTrackTimerDatabase(activity);
        tickTrackDatabase = new TickTrackDatabase(activity);

        timerDataArrayList = tickTrackDatabase.retrieveTimerList();

        checkValuesInit();
        checkConditions();

        setupOnClickListeners();
        setupFABListeners();

    }
    private void checkValuesInit() {

        timerCurrentPosition = getCurrentTimerPosition();
        if(timerCurrentPosition!=-1 && timerID!=-1){

            booleanRefresh();

            if(!timerDataArrayList.get(timerCurrentPosition).getTimerLabel().equals("Set label")){
                labelTextView.setText(timerDataArrayList.get(timerCurrentPosition).getTimerLabel());
            } else {
                labelTextView.setText("Timer");
            }

            TickTrackThemeSetter.timerActivityTheme(activity,timerActivityToolbar, timerDataArrayList.get(timerCurrentPosition).getTimerFlag(),timerActivityRootLayout,
                    timerHourMinute, timerMillis, timerProgressBarBackground, tickTrackDatabase);

        } else {
            onBackPressed();
        }
    }

    SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener = (sharedPreferences, s) ->  {
        timerDataArrayList = tickTrackDatabase.retrieveTimerList();
        if (s.equals("TimerData")){
            Collections.sort(timerDataArrayList);
            timerCurrentPosition = getCurrentTimerPosition();
            if(!timerDataArrayList.get(timerCurrentPosition).isTimerRinging()){
                killAndResetTimer();
            }
        }
    };

    private void booleanRefresh() {
        isTimerPaused = timerDataArrayList.get(timerCurrentPosition).isTimerPause();
        isTimerRunning = timerDataArrayList.get(timerCurrentPosition).isTimerOn();
        isTimerRinging = timerDataArrayList.get(timerCurrentPosition).isTimerRinging();
        maxTimeInMillis = timerDataArrayList.get(timerCurrentPosition).getTimerTotalTimeInMillis();
    }

    private void checkConditions() {
        if (isTimerRunning){
            if(isTimerNew){
                presetStockValues();
                startTimer(currentTimeInMillis);
            } else if(isTimerPaused){
                presetPauseValues();
            } else {
                if((timerDataArrayList.get(timerCurrentPosition).getTimerAlarmEndTimeInMillis()-System.currentTimeMillis())>0){
                    presetResumeValues();
                    startTimer(currentTimeInMillis);
                } else {
                    resetFAB.setVisibility(View.INVISIBLE);
                    timerDataArrayList.get(timerCurrentPosition).setTimerRinging(true);
                    timerDataArrayList.get(timerCurrentPosition).setTimerNotificationOn(false);
                    tickTrackDatabase.storeTimerList(timerDataArrayList);
                    timerDataArrayList = tickTrackDatabase.retrieveTimerList();
                    booleanRefresh();
                    stopTimer();
                }
            }
        } else {
            presetStockValues();
        }
    }

    private void setupFABListeners(){
        playPauseFAB.setOnClickListener(view -> {
            if(isTimerRunning && !isTimerPaused){
                if(isTimerRinging){
                    System.out.println("KillTimer");
                    killAndResetTimer();
                } else {
                    System.out.println("PauseTimer");
                    pauseTimer();
                }
            } else {
                System.out.println("StartTimer");
                startTimer(currentTimeInMillis);
            }
        });
        resetFAB.setOnClickListener(view -> resetTimer());
    }

    private void updateTimerTextView(long countdownValue){
        int hours = (int) TimeUnit.MILLISECONDS.toHours(countdownValue);
        int minutes = (int) (TimeUnit.MILLISECONDS.toMinutes(countdownValue) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(countdownValue)));
        int seconds = (int) (TimeUnit.MILLISECONDS.toSeconds(countdownValue) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(countdownValue)));
        float milliseconds = (float) (TimeUnit.MILLISECONDS.toMillis(countdownValue) - TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(countdownValue)))/10;

        String hourLeft = String.format(Locale.getDefault(),"%02d:%02d:%02d", hours,minutes,seconds);
        String milliSecondLeft = String.format(Locale.getDefault(), "%02d", (int) Math.floor(milliseconds));

        timerHourMinute.setText(hourLeft);
        timerMillis.setText(milliSecondLeft);
    }
    long countDownTimerMillis;
    private void startTimer(long timeInMillis) {

        sharedPreferences = activity.getSharedPreferences("TickTrackData", MODE_PRIVATE);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);

        timerDataArrayList.get(timerCurrentPosition).setTimerAlarmEndTimeInMillis(timeInMillis+System.currentTimeMillis());
        tickTrackDatabase.storeTimerList(timerDataArrayList);
        timerDataArrayList = tickTrackDatabase.retrieveTimerList();


        tickTrackTimerDatabase.setAlarm(timerDataArrayList.get(timerCurrentPosition).getTimerAlarmEndTimeInMillis(), timerID);

        countDownTimerMillis = timeInMillis;

        countDownTimer = new CountDownTimer(countDownTimerMillis, 1) {
            @Override
            public void onTick(long l) {
                activity.runOnUiThread(()->{
                    countDownTimerMillis = l;
                    updateTimerTextView(countDownTimerMillis);
                    timerProgressBar.setProgress(getCurrentStep(countDownTimerMillis, maxTimeInMillis));
                });
            }
            @Override
            public void onFinish() {
                activity.runOnUiThread(() -> {
                    timerDataArrayList.get(timerCurrentPosition).setTimerEndedTimeInMillis(System.currentTimeMillis());
                    tickTrackDatabase.storeTimerList(timerDataArrayList);
                    timerDataArrayList =  tickTrackDatabase.retrieveTimerList();
                    stopTimer();
                });
            }
        };

        countDownTimer.start();

        timerDataArrayList.get(timerCurrentPosition).setTimerOn(true);
        timerDataArrayList.get(timerCurrentPosition).setTimerPause(false);
        tickTrackDatabase.storeTimerList(timerDataArrayList);
        timerDataArrayList =  tickTrackDatabase.retrieveTimerList();
        booleanRefresh();
        playPauseFAB.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_round_pause_white_24));
        resetFAB.setVisibility(View.INVISIBLE);

    }
    private void pauseTimer() {

        countDownTimer.cancel();

        sharedPreferences = activity.getSharedPreferences("TickTrackData", MODE_PRIVATE);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);

        int hours = (int) TimeUnit.MILLISECONDS.toHours(countDownTimerMillis);
        int minutes = (int) (TimeUnit.MILLISECONDS.toMinutes(countDownTimerMillis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(countDownTimerMillis)));
        int seconds = (int) (TimeUnit.MILLISECONDS.toSeconds(countDownTimerMillis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(countDownTimerMillis)));
        float milliseconds = (float) (TimeUnit.MILLISECONDS.toMillis(countDownTimerMillis) - TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(countDownTimerMillis)))/10;

        timerProgressBar.setProgress(getCurrentStep(countDownTimerMillis, maxTimeInMillis));
        String milliSecondLeft = String.format(Locale.getDefault(), "%02d", (int) Math.floor(milliseconds));
        timerMillis.setText(milliSecondLeft);

        timerDataArrayList.get(timerCurrentPosition).setTimerOn(true);
        timerDataArrayList.get(timerCurrentPosition).setTimerPause(true);
        timerDataArrayList.get(timerCurrentPosition).setTimerNotificationOn(false);
        timerDataArrayList.get(timerCurrentPosition).setTimerHourLeft(hours);
        timerDataArrayList.get(timerCurrentPosition).setTimerMinuteLeft(minutes);
        timerDataArrayList.get(timerCurrentPosition).setTimerSecondLeft(seconds);
        timerDataArrayList.get(timerCurrentPosition).setTimerMilliSecondLeft(milliseconds);
        timerDataArrayList.get(timerCurrentPosition).setTimerAlarmEndTimeInMillis(-1);

        tickTrackDatabase.storeTimerList(timerDataArrayList);
        timerDataArrayList = tickTrackDatabase.retrieveTimerList();
        booleanRefresh();

        postSetPauseValues();
        tickTrackTimerDatabase.cancelAlarm(timerID);

        if(timerStopHandler!=null && timerBlinkHandler!=null){
            timerStopHandler.removeCallbacks(runnable);
            timerBlinkHandler.removeCallbacks(blinkRunnable);
        }
    }
    private void resetTimer() {

        sharedPreferences = activity.getSharedPreferences("TickTrackData", MODE_PRIVATE);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);

        timerDataArrayList.get(timerCurrentPosition).setTimerOn(false);
        timerDataArrayList.get(timerCurrentPosition).setTimerPause(false);
        timerDataArrayList.get(timerCurrentPosition).setTimerEndedTimeInMillis(-1);
        tickTrackDatabase.storeTimerList(timerDataArrayList);
        timerDataArrayList = tickTrackDatabase.retrieveTimerList();
        booleanRefresh();
        timerProgressBar.setProgress(1);
        presetStockValues();

        if(timerStopHandler!=null && timerBlinkHandler!=null){
            timerStopHandler.removeCallbacks(runnable);
            timerBlinkHandler.removeCallbacks(blinkRunnable);
        }

    }

    private void presetPauseValues() {

        resetFAB.setVisibility(View.VISIBLE);
        playPauseFAB.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_round_play_white_24));

        pauseHours = timerDataArrayList.get(timerCurrentPosition).getTimerHourLeft();
        pauseMinutes = timerDataArrayList.get(timerCurrentPosition).getTimerMinuteLeft();
        pauseSeconds = timerDataArrayList.get(timerCurrentPosition).getTimerSecondLeft();
        pauseMilliseconds = (int) (timerDataArrayList.get(timerCurrentPosition).getTimerMilliSecondLeft());

        currentTimeInMillis = TimeAgo.getTimerDataInMillis(pauseHours, pauseMinutes, pauseSeconds, pauseMilliseconds);

        timerProgressBar.setInstantProgress(getCurrentStep(currentTimeInMillis, maxTimeInMillis));

        String resumeTimeLeft = String.format(Locale.getDefault(),"%02d:%02d:%02d", pauseHours,pauseMinutes,pauseSeconds);
        String resumeMillisLeft = String.format(Locale.getDefault(), "%02d", (int) Math.floor(pauseMilliseconds));

        timerHourMinute.setText(resumeTimeLeft);
        timerMillis.setText(resumeMillisLeft);
    }
    private void postSetPauseValues(){
        resetFAB.setVisibility(View.VISIBLE);
        playPauseFAB.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_round_play_white_24));

        pauseHours = timerDataArrayList.get(timerCurrentPosition).getTimerHourLeft();
        pauseMinutes = timerDataArrayList.get(timerCurrentPosition).getTimerMinuteLeft();
        pauseSeconds = timerDataArrayList.get(timerCurrentPosition).getTimerSecondLeft();
        pauseMilliseconds = (int) (timerDataArrayList.get(timerCurrentPosition).getTimerMilliSecondLeft());

        currentTimeInMillis = countDownTimerMillis;

        String resumeTimeLeft = String.format(Locale.getDefault(),"%02d:%02d:%02d", pauseHours,pauseMinutes,pauseSeconds);
        String resumeMillisLeft = String.format(Locale.getDefault(), "%02d", (int) Math.floor(pauseMilliseconds));

        timerHourMinute.setText(resumeTimeLeft);
        timerMillis.setText(resumeMillisLeft);
    }
    private void presetResumeValues() {

        long resumeTimeInMillis = timerDataArrayList.get(timerCurrentPosition).getTimerAlarmEndTimeInMillis() - System.currentTimeMillis();
        currentTimeInMillis = resumeTimeInMillis;
        resetFAB.setVisibility(View.INVISIBLE);
        playPauseFAB.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_round_pause_white_24));

        int resumeHours = (int) TimeUnit.MILLISECONDS.toHours(resumeTimeInMillis);
        int resumeMinutes = (int) (TimeUnit.MILLISECONDS.toMinutes(resumeTimeInMillis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(resumeTimeInMillis)));
        int resumeSeconds = (int) (TimeUnit.MILLISECONDS.toSeconds(resumeTimeInMillis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(resumeTimeInMillis)));
        resumeMilliseconds = (float) (TimeUnit.MILLISECONDS.toMillis(resumeTimeInMillis) - TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(resumeTimeInMillis)))/10;


        timerProgressBar.setInstantProgress(getCurrentStep(resumeTimeInMillis, maxTimeInMillis));

        String resumeTimeLeft = String.format(Locale.getDefault(),"%02d:%02d:%02d", resumeHours, resumeMinutes, resumeSeconds);
        String resumeMillisLeft = String.format(Locale.getDefault(), "%02d", (int) resumeMilliseconds);

        timerHourMinute.setText(resumeTimeLeft);
        timerMillis.setText(resumeMillisLeft);

    }

    private void stopTimer(){

        timerMillis.setVisibility(View.GONE);
        timerStopHandler = new Handler();
        timerBlinkHandler = new Handler();

        timerDataArrayList.get(timerCurrentPosition).setTimerRinging(true);
        if(timerDataArrayList.get(timerCurrentPosition).getTimerEndedTimeInMillis() != -1){
            long stopTimeRetrieve = timerDataArrayList.get(timerCurrentPosition).getTimerEndedTimeInMillis();
            UpdateTime = (System.currentTimeMillis() - stopTimeRetrieve) / 1000F;
        } else {
            timerDataArrayList.get(timerCurrentPosition).setTimerEndedTimeInMillis(System.currentTimeMillis());
            tickTrackDatabase.storeTimerList(timerDataArrayList);
            timerDataArrayList = tickTrackDatabase.retrieveTimerList();
            booleanRefresh();
            stopTimer();
        }
        timerProgressBar.setProgress(1);
        timerStopHandler.postDelayed(runnable, 0);
        timerBlinkHandler.postDelayed(blinkRunnable, 0);

        playPauseFAB.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_stop_white_24));

        tickTrackDatabase.storeTimerList(timerDataArrayList);
        timerDataArrayList = tickTrackDatabase.retrieveTimerList();
        booleanRefresh();

        sharedPreferences = activity.getSharedPreferences("TickTrackData", MODE_PRIVATE);
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);

    }
    private void killAndResetTimer(){

        timerStopHandler.removeCallbacks(runnable);
        timerBlinkHandler.removeCallbacks(blinkRunnable);

        timerMillis.setVisibility(View.VISIBLE);
        timerProgressBar.setVisibility(View.VISIBLE);
        resetTimer();
        setupFABListeners();
        timerDataArrayList.get(timerCurrentPosition).setTimerOn(false);
        timerDataArrayList.get(timerCurrentPosition).setTimerPause(false);
        timerDataArrayList.get(timerCurrentPosition).setTimerRinging(false);
        timerDataArrayList.get(timerCurrentPosition).setTimerEndedTimeInMillis(-1);
        tickTrackDatabase.storeTimerList(timerDataArrayList);
        timerDataArrayList = tickTrackDatabase.retrieveTimerList();
        booleanRefresh();
    }

    private Handler timerStopHandler;
    private Handler timerBlinkHandler;
    float UpdateTime = 0L;
    boolean isBlink = false;
    public Runnable runnable = new Runnable() {
        public void run() {
            UpdateTime += 1;
            updateStopTimeText();
            timerStopHandler.postDelayed(this,1000);
        }
    };
    public Runnable blinkRunnable = new Runnable(){
        public void run(){
            if(isBlink){
                isBlink = false;
                timerProgressBar.setVisibility(View.VISIBLE);
            } else {
                isBlink = true;
                timerProgressBar.setVisibility(View.INVISIBLE);
            }
            timerBlinkHandler.postDelayed(blinkRunnable, 500);
        }
    };

    private void updateStopTimeText() {

        float totalSeconds = UpdateTime-1;
        float hours = totalSeconds/3600;
        float minutes = totalSeconds/60%60;
        float seconds = totalSeconds%60;

        String hourLeft = String.format(Locale.getDefault(),"-%02d:%02d:%02d",(int)hours,(int)minutes,(int)seconds);
        timerHourMinute.setText(hourLeft);
    }

    private void presetStockValues() {

        if(timerProgressBar.getProgress()!=1){
            progressBarPreHandler.postDelayed(() -> timerProgressBar.setProgress(1), 400);
        }

        resetFAB.setVisibility(View.INVISIBLE);
        playPauseFAB.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_round_play_white_24));

        int staticHours = timerDataArrayList.get(timerCurrentPosition).getTimerHour();
        int staticMinutes = timerDataArrayList.get(timerCurrentPosition).getTimerMinute();
        int staticSeconds = timerDataArrayList.get(timerCurrentPosition).getTimerSecond();
        int staticMilliseconds = 0;

        currentTimeInMillis = TimeAgo.getTimerDataInMillis(staticHours, staticMinutes, staticSeconds, staticMilliseconds);

        String resumeTimeLeft = String.format(Locale.getDefault(),"%02d:%02d:%02d", staticHours,staticMinutes,staticSeconds);
        String resumeMillisLeft = String.format(Locale.getDefault(), "%02d", staticMilliseconds);

        timerHourMinute.setText(resumeTimeLeft);
        timerMillis.setText(resumeMillisLeft);
    }

    @Override
    protected void onStart() {
        super.onStart();
        TickTrackThemeSetter.timerActivityTheme(activity,timerActivityToolbar, timerDataArrayList.get(timerCurrentPosition).getTimerFlag(),timerActivityRootLayout,
                timerHourMinute, timerMillis, timerProgressBarBackground, tickTrackDatabase);
        timerDataArrayList.get(timerCurrentPosition).setTimerNotificationOn(false);
        tickTrackDatabase.storeTimerList(timerDataArrayList);
        if(tickTrackTimerDatabase.isMyServiceRunning(TimerService.class)){
            tickTrackTimerDatabase.stopNotificationService();
        }
        tickTrackTimerDatabase = new TickTrackTimerDatabase(activity);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sharedPreferences = activity.getSharedPreferences("TickTrackData", MODE_PRIVATE);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
        if(timerCurrentPosition!=-1){
            if(timerDataArrayList.get(timerCurrentPosition).isTimerOn() && !timerDataArrayList.get(timerCurrentPosition).isTimerPause() && !timerDataArrayList.get(timerCurrentPosition).isTimerRinging()){
                if(!isTimerRinging){
                    timerDataArrayList.get(timerCurrentPosition).setTimerNotificationOn(true);
                    if(!tickTrackTimerDatabase.isMyServiceRunning(TimerService.class)){
                        tickTrackTimerDatabase.startNotificationService();
                    }
                } else {
                    timerDataArrayList.get(timerCurrentPosition).setTimerNotificationOn(false);
                    if(tickTrackTimerDatabase.isMyServiceRunning(TimerService.class)){
                        tickTrackTimerDatabase.stopNotificationService();
                    }
                }
            } else {
                timerDataArrayList.get(timerCurrentPosition).setTimerNotificationOn(false);
                if(tickTrackTimerDatabase.isMyServiceRunning(TimerService.class)){
                    tickTrackTimerDatabase.stopNotificationService();
                }
            }
        }
        if(timerStopHandler!=null && timerBlinkHandler!=null){
            timerStopHandler.removeCallbacks(runnable);
            timerBlinkHandler.removeCallbacks(blinkRunnable);
        }
        if(countDownTimer!=null){
            countDownTimer.cancel();
        }
        tickTrackDatabase.storeTimerList(timerDataArrayList);
    }

    @Override
    protected void onStop() {
        super.onStop();
        sharedPreferences = activity.getSharedPreferences("TickTrackData", MODE_PRIVATE);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
        if(timerCurrentPosition!=-1){
            if(timerDataArrayList.get(timerCurrentPosition).isTimerOn() && !timerDataArrayList.get(timerCurrentPosition).isTimerPause()){
                if(!isTimerRinging){
                    timerDataArrayList.get(timerCurrentPosition).setTimerNotificationOn(true);
                    if(!tickTrackTimerDatabase.isMyServiceRunning(TimerService.class)){
                        tickTrackTimerDatabase.startNotificationService();
                    }
                } else {
                    timerDataArrayList.get(timerCurrentPosition).setTimerNotificationOn(false);
                    if(tickTrackTimerDatabase.isMyServiceRunning(TimerService.class)){
                        tickTrackTimerDatabase.stopNotificationService();
                    }
                }
            } else {
                timerDataArrayList.get(timerCurrentPosition).setTimerNotificationOn(false);
                if(tickTrackTimerDatabase.isMyServiceRunning(TimerService.class)){
                    tickTrackTimerDatabase.stopNotificationService();
                }
            }
        }
        if(timerStopHandler!=null && timerBlinkHandler!=null){
            timerStopHandler.removeCallbacks(runnable);
            timerBlinkHandler.removeCallbacks(blinkRunnable);
        }
        if(countDownTimer!=null){
            countDownTimer.cancel();
        }
        tickTrackDatabase.storeTimerList(timerDataArrayList);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(timerStopHandler!=null && timerBlinkHandler!=null){
            timerStopHandler.removeCallbacks(runnable);
            timerBlinkHandler.removeCallbacks(blinkRunnable);
        }
        Intent intent = new Intent(activity, SoYouADeveloperHuh.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("FragmentID", 2);
        startActivity(intent);
    }
}
