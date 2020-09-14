package com.theflopguyproductions.ticktrack.timer.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.SoYouADeveloperHuh;
import com.theflopguyproductions.ticktrack.dialogs.DeleteTimer;
import com.theflopguyproductions.ticktrack.dialogs.TimerEditDialog;
import com.theflopguyproductions.ticktrack.timer.data.TimerData;
import com.theflopguyproductions.ticktrack.timer.service.TimerRingService;
import com.theflopguyproductions.ticktrack.timer.service.TimerService;
import com.theflopguyproductions.ticktrack.ui.timer.TimerRecyclerFragment;
import com.theflopguyproductions.ticktrack.ui.utils.TickTrackAnimator;
import com.theflopguyproductions.ticktrack.ui.utils.TickTrackProgressBar;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackTimerDatabase;
import com.theflopguyproductions.ticktrack.utils.helpers.TickTrackThemeSetter;
import com.theflopguyproductions.ticktrack.utils.helpers.TimeAgo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class TimerActivity extends AppCompatActivity {

    public static final String ACTION_TIMER_NEW_ADDITION = "ACTION_TIMER_NEW_ADDITION";

    TickTrackTimerDatabase tickTrackTimerDatabase;
    TickTrackDatabase tickTrackDatabase;

    private ConstraintLayout timerActivityToolbar, timerActivityRootLayout;
    private TextView timerHourMinute, timerMillis, labelTextView;
    private ConstraintLayout resetFAB, plusOneFAB;
    private ConstraintLayout playPauseFAB;
    private ImageView playImage, pauseImage, stopImage;
    private ImageButton backButton, editButton, deleteButton;
    private TickTrackProgressBar timerProgressBar, timerProgressBarBackground;

    private int timerID;
    private Activity activity;
    private boolean isTimerRunning, isTimerPaused, isTimerNew = false, isTimerRinging;

    private ArrayList<TimerData> timerDataArrayList = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    float resumeMilliseconds;
    private int pauseHours, pauseMinutes, pauseSeconds, pauseMilliseconds;
    private String timerStringID;

    private long maxTimeInMillis, currentTimeInMillis;

    private CountDownTimer countDownTimer;
    private Handler progressBarPreHandler = new Handler();

    private ConstraintLayout startEndLayout;
    private TextView startTitle, endTitle, startData, endData;

    private void initVariables() {

        timerActivityRootLayout = findViewById(R.id.timerActivityRootLayout);
        timerActivityToolbar = findViewById(R.id.timerActivityToolbarLayout);
        timerHourMinute = findViewById(R.id.timerActivityTimeLeftTextView);
        timerMillis = findViewById(R.id.timerActivityTimeLeftMillisTextView);
        timerProgressBar = findViewById(R.id.timerActivityTimerProgressBar);
        timerProgressBarBackground = findViewById(R.id.timerActivityTimerProgressBarStatic);
        playPauseFAB = findViewById(R.id.timerActivityPlayPauseFAB);
        playImage = findViewById(R.id.timerActivityPlayImage);
        pauseImage = findViewById(R.id.timerActivityPauseImage);
        stopImage = findViewById(R.id.timerActivityStopImage);

        resetFAB = findViewById(R.id.timerActivityResetFAB);
        plusOneFAB = findViewById(R.id.timerActivityPlusOneFAB);
        backButton = findViewById(R.id.timerActivityBackImageButton);
        editButton = findViewById(R.id.timerActivityEditImageButton);
        deleteButton = findViewById(R.id.timerActivityDeleteImageButton);
        labelTextView = findViewById(R.id.timerActivityLabelTextView);
        startEndLayout = findViewById(R.id.timerActivityStartEndLayout);
        startTitle = findViewById(R.id.timerActivityStartTimeTitleText);
        endTitle = findViewById(R.id.timerActivityEndTimeTitleText);
        startData = findViewById(R.id.timerActivityStartTimeDataText);
        endData = findViewById(R.id.timerActivityEndTimeDataText);

        timerProgressBar.setLinearProgress(true);
        timerProgressBar.setSpinSpeed(2.500f);
        timerProgressBarBackground.setLinearProgress(true);
        timerProgressBarBackground.setSpinSpeed(2.500f);
        timerProgressBarBackground.setInstantProgress(1);


    }
    private void fabImageSetup(int i) {
        if(i==1){
            TickTrackAnimator.fabImageReveal(playImage);
            TickTrackAnimator.fabImageDissolve(pauseImage);
            TickTrackAnimator.fabImageDissolve(stopImage);
        } else if(i==2){
            TickTrackAnimator.fabImageDissolve(playImage);
            TickTrackAnimator.fabImageReveal(pauseImage);
            TickTrackAnimator.fabImageDissolve(stopImage);
        } else if (i==3){
            TickTrackAnimator.fabImageDissolve(playImage);
            TickTrackAnimator.fabImageDissolve(pauseImage);
            TickTrackAnimator.fabImageReveal(stopImage);
        }
    }
    private void setupOnClickListeners(){
        backButton.setOnClickListener(view -> onBackPressed());
        editButton.setOnClickListener(view -> {
            TimerEditDialog timerEditDialog = new TimerEditDialog(activity, timerDataArrayList.get(getCurrentTimerPosition()).getTimerLabel(), timerDataArrayList.get(getCurrentTimerPosition()).getTimerFlag());
            timerEditDialog.show();

            timerEditDialog.saveButton.setOnClickListener(view1 -> {
                if(timerEditDialog.labelInput.getText().toString().trim().length()>0){
                    timerDataArrayList.get(getCurrentTimerPosition()).setTimerLabel(timerEditDialog.labelInput.getText().toString());
                    labelTextView.setText(timerEditDialog.labelInput.getText().toString());
                }
                if(timerEditDialog.currentFlag!=timerDataArrayList.get(getCurrentTimerPosition()).getTimerFlag()){
                    timerDataArrayList.get(getCurrentTimerPosition()).setTimerFlag(timerEditDialog.currentFlag);
                    timerActivityToolbar.setBackgroundResource(timerActivityToolbarColor(timerEditDialog.currentFlag));
                    timerProgressBar.setBarColor(timerActivityToolbarColor(timerEditDialog.currentFlag));
                }
                tickTrackDatabase.storeTimerList(timerDataArrayList);
                timerEditDialog.dismiss();
            });
            timerEditDialog.cancelButton.setOnClickListener(view12 -> timerEditDialog.dismiss());
        });
        String deletedTimer = timerDataArrayList.get(getCurrentTimerPosition()).getTimerLabel();
        int timerId = timerDataArrayList.get(getCurrentTimerPosition()).getTimerIntID();
        deleteButton.setOnClickListener(view -> {
            DeleteTimer deleteTimer = new DeleteTimer(activity);
            deleteTimer.show();
            if(!"Set label".equals(deletedTimer)){
                deleteTimer.dialogMessage.setText("Delete timer "+deletedTimer+"?");
            } else {
                deleteTimer.dialogMessage.setText("Delete timer ?");
            }
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);


            deleteTimer.yesButton.setOnClickListener(view1 -> {
                TimerRecyclerFragment.deleteTimer(timerId, getCurrentTimerPosition(), activity, deletedTimer);
                onBackPressed();
                deleteTimer.dismiss();
            });
            deleteTimer.noButton.setOnClickListener(view1 -> {
                TimerRecyclerFragment.refreshRecyclerView();
                deleteTimer.dismiss();
                sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);

            });
            deleteTimer.setOnCancelListener(dialogInterface -> {
                TimerRecyclerFragment.refreshRecyclerView();
                deleteTimer.cancel();
                sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
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
            if(timerDataArrayList.get(i).getTimerID().equals(timerStringID)){
                timerID = timerDataArrayList.get(i).getTimerIntID();
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
        timerStringID = getIntent().getStringExtra("timerID");
        isTimerNew = Objects.equals(getIntent().getAction(), ACTION_TIMER_NEW_ADDITION);

        activity = this;
        tickTrackTimerDatabase = new TickTrackTimerDatabase(activity);
        tickTrackDatabase = new TickTrackDatabase(activity);

        timerDataArrayList = tickTrackDatabase.retrieveTimerList();

        setupOnClickListeners();
        setupFABListeners();

    }
    private void checkValuesInit() {

        if(getCurrentTimerPosition()!=-1 && timerID!=-1){

            booleanRefresh();

            if(!timerDataArrayList.get(getCurrentTimerPosition()).getTimerLabel().equals("Set label")){
                labelTextView.setText(timerDataArrayList.get(getCurrentTimerPosition()).getTimerLabel());
            } else {
                labelTextView.setText("Timer");
            }

            TickTrackThemeSetter.timerActivityTheme(activity,timerActivityToolbar, timerDataArrayList.get(getCurrentTimerPosition()).getTimerFlag(),timerActivityRootLayout,
                    timerHourMinute, timerMillis, timerProgressBarBackground, tickTrackDatabase, timerProgressBar);

        } else {
            onBackPressed();
        }
    }

    SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener = (sharedPreferences, s) ->  {
        timerDataArrayList = tickTrackDatabase.retrieveTimerList();
        if (s.equals("TimerData")){
            if(!timerDataArrayList.get(getCurrentTimerPosition()).isTimerRinging()){
                if(!(timerDataArrayList.get(getCurrentTimerPosition()).getTimerTempMaxTimeInMillis() !=-1)){
                    killAndResetTimer();
                }
            }

        }
    };

    private void booleanRefresh() {
        isTimerPaused = timerDataArrayList.get(getCurrentTimerPosition()).isTimerPause();
        isTimerRunning = timerDataArrayList.get(getCurrentTimerPosition()).isTimerOn();
        isTimerRinging = timerDataArrayList.get(getCurrentTimerPosition()).isTimerRinging();
    }

    private void checkConditions() {
        if (isTimerRunning){
            if(isTimerNew){
                presetStockValues();
                startTimer(currentTimeInMillis);
            } else if(isTimerPaused){
                presetPauseValues();
            } else {
                if((timerDataArrayList.get(getCurrentTimerPosition()).getTimerAlarmEndTimeInMillis() - SystemClock.elapsedRealtime())>0){

                    presetResumeValues();
                    startTimer(currentTimeInMillis);
                } else {
                    TickTrackAnimator.fabLayoutDissolve(resetFAB);
                    TickTrackAnimator.fabLayoutUnDissolve(plusOneFAB);
                    deleteButton.setVisibility(View.GONE);
                    editButton.setVisibility(View.GONE);
                    timerDataArrayList.get(getCurrentTimerPosition()).setTimerRinging(true);
                    timerDataArrayList.get(getCurrentTimerPosition()).setTimerNotificationOn(false);
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
                    killAndResetTimer();
                } else {
                    pauseTimer();
                }
            } else {
                startTimer(currentTimeInMillis);
            }
        });
        resetFAB.setOnClickListener(view -> resetTimer());
        plusOneFAB.setOnClickListener(view -> addOneTimer());
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

        sharedPreferences = tickTrackDatabase.getSharedPref(this);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);

        timerDataArrayList.get(getCurrentTimerPosition()).setTimerAlarmEndTimeInMillis(timeInMillis+SystemClock.elapsedRealtime());
        if(timerDataArrayList.get(getCurrentTimerPosition()).getTimerStartTimeInMillis()==-1){
            timerDataArrayList.get(getCurrentTimerPosition()).setTimerStartTimeInMillis(System.currentTimeMillis());
        }
        tickTrackDatabase.storeTimerList(timerDataArrayList);
        timerDataArrayList = tickTrackDatabase.retrieveTimerList();


        setupStartEndTime();

        tickTrackTimerDatabase.setAlarm(timerDataArrayList.get(getCurrentTimerPosition()).getTimerAlarmEndTimeInMillis(), timerID, false);
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
                    startEndLayout.setVisibility(View.GONE);
                    timerDataArrayList.get(getCurrentTimerPosition()).setTimerEndedTimeInMillis(SystemClock.elapsedRealtime());
                    tickTrackDatabase.storeTimerList(timerDataArrayList);
                    timerDataArrayList =  tickTrackDatabase.retrieveTimerList();
                    stopTimer();
                });
            }
        };

        countDownTimer.start();

        timerDataArrayList.get(getCurrentTimerPosition()).setTimerOn(true);
        timerDataArrayList.get(getCurrentTimerPosition()).setTimerPause(false);
        tickTrackDatabase.storeTimerList(timerDataArrayList);
        timerDataArrayList = tickTrackDatabase.retrieveTimerList();
        booleanRefresh();
        fabImageSetup(2);

        TickTrackAnimator.fabLayoutDissolve(resetFAB);
        deleteButton.setVisibility(View.GONE);
        editButton.setVisibility(View.GONE);
        TickTrackAnimator.fabLayoutUnDissolve(plusOneFAB);
    }

    DateFormat hourMinute = new SimpleDateFormat("hh:mm:ss");
    DateFormat dateFormat = new SimpleDateFormat("- dd/MM/yyyy");
    private void setupStartEndTime() {
        Calendar startCal = Calendar.getInstance();
        startCal.setTimeInMillis(timerDataArrayList.get(getCurrentTimerPosition()).getTimerStartTimeInMillis());
        int hourCheck = startCal.get(Calendar.HOUR_OF_DAY);
        String timeFormat = hourMinute.format(timerDataArrayList.get(getCurrentTimerPosition()).getTimerStartTimeInMillis());
        if(hourCheck>12){
            timeFormat += " pm ";
        } else {
            timeFormat += " am ";
        }

        startData.setText(timeFormat+dateFormat.format(timerDataArrayList.get(getCurrentTimerPosition()).getTimerStartTimeInMillis()));

        Calendar stopCal = Calendar.getInstance();
        stopCal.setTimeInMillis(timerDataArrayList.get(getCurrentTimerPosition()).getTimerStartTimeInMillis());
        int hourStopCheck = stopCal.get(Calendar.HOUR_OF_DAY);
        String timeStopFormat = hourMinute.format(System.currentTimeMillis()
        + (timerDataArrayList.get(getCurrentTimerPosition()).getTimerAlarmEndTimeInMillis() - SystemClock.elapsedRealtime()));
        if(hourStopCheck>12){
            timeStopFormat += " pm ";
        } else {
            timeStopFormat += " am ";
        }

        endData.setText(timeStopFormat + dateFormat.format(System.currentTimeMillis() +
                (timerDataArrayList.get(getCurrentTimerPosition()).getTimerAlarmEndTimeInMillis() - SystemClock.elapsedRealtime())));

        startEndLayout.setVisibility(View.VISIBLE);

    }


    private void pauseTimer() {

        countDownTimer.cancel();

        sharedPreferences = tickTrackDatabase.getSharedPref(this);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);


        int hours = (int) TimeUnit.MILLISECONDS.toHours(countDownTimerMillis);
        int minutes = (int) (TimeUnit.MILLISECONDS.toMinutes(countDownTimerMillis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(countDownTimerMillis)));
        int seconds = (int) (TimeUnit.MILLISECONDS.toSeconds(countDownTimerMillis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(countDownTimerMillis)));
        float milliseconds = (float) (TimeUnit.MILLISECONDS.toMillis(countDownTimerMillis) - TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(countDownTimerMillis)))/10;

        timerProgressBar.setProgress(getCurrentStep(countDownTimerMillis, maxTimeInMillis));
        String milliSecondLeft = String.format(Locale.getDefault(), "%02d", (int) Math.floor(milliseconds));
        timerMillis.setText(milliSecondLeft);

        timerDataArrayList.get(getCurrentTimerPosition()).setTimerOn(true);
        timerDataArrayList.get(getCurrentTimerPosition()).setTimerPause(true);
        timerDataArrayList.get(getCurrentTimerPosition()).setTimerNotificationOn(false);
        timerDataArrayList.get(getCurrentTimerPosition()).setTimerHourLeft(hours);
        timerDataArrayList.get(getCurrentTimerPosition()).setTimerMinuteLeft(minutes);
        timerDataArrayList.get(getCurrentTimerPosition()).setTimerSecondLeft(seconds);
        timerDataArrayList.get(getCurrentTimerPosition()).setTimerMilliSecondLeft(milliseconds);
        timerDataArrayList.get(getCurrentTimerPosition()).setTimerAlarmEndTimeInMillis(-1);

        tickTrackDatabase.storeTimerList(timerDataArrayList);
        timerDataArrayList = tickTrackDatabase.retrieveTimerList();
        booleanRefresh();

        postSetPauseValues();
        tickTrackTimerDatabase.cancelAlarm(timerID, false);

        if(timerStopHandler!=null && timerBlinkHandler!=null){
            timerStopHandler.removeCallbacks(runnable);
            timerBlinkHandler.removeCallbacks(blinkRunnable);
        }
    }
    private void resetTimer() {

        sharedPreferences = tickTrackDatabase.getSharedPref(this);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);

        timerDataArrayList.get(getCurrentTimerPosition()).setTimerOn(false);
        timerDataArrayList.get(getCurrentTimerPosition()).setTimerPause(false);
        timerDataArrayList.get(getCurrentTimerPosition()).setTimerEndedTimeInMillis(-1);
        timerDataArrayList.get(getCurrentTimerPosition()).setTimerTempMaxTimeInMillis(-1);
        timerDataArrayList.get(getCurrentTimerPosition()).setTimerStartTimeInMillis(-1);
        tickTrackDatabase.storeTimerList(timerDataArrayList);
        timerDataArrayList = tickTrackDatabase.retrieveTimerList();
        booleanRefresh();
        timerProgressBar.setProgress(1);
        presetStockValues();

        if(timerStopHandler!=null && timerBlinkHandler!=null){
            timerStopHandler.removeCallbacks(runnable);
            timerBlinkHandler.removeCallbacks(blinkRunnable);
        }
        startEndLayout.setVisibility(View.GONE);
    }

    private void presetPauseValues() {

        TickTrackAnimator.fabLayoutUnDissolve(resetFAB);
        deleteButton.setVisibility(View.VISIBLE);
        editButton.setVisibility(View.GONE);
        startEndLayout.setVisibility(View.GONE);
        TickTrackAnimator.fabLayoutDissolve(plusOneFAB);
        fabImageSetup(1);

        pauseHours = timerDataArrayList.get(getCurrentTimerPosition()).getTimerHourLeft();
        pauseMinutes = timerDataArrayList.get(getCurrentTimerPosition()).getTimerMinuteLeft();
        pauseSeconds = timerDataArrayList.get(getCurrentTimerPosition()).getTimerSecondLeft();
        pauseMilliseconds = (int) (timerDataArrayList.get(getCurrentTimerPosition()).getTimerMilliSecondLeft());

        currentTimeInMillis = TimeAgo.getTimerDataInMillis(pauseHours, pauseMinutes, pauseSeconds, pauseMilliseconds);

        if(timerDataArrayList.get(getCurrentTimerPosition()).getTimerTempMaxTimeInMillis() == -1){
            maxTimeInMillis = timerDataArrayList.get(getCurrentTimerPosition()).getTimerTotalTimeInMillis();
        } else {
            maxTimeInMillis = timerDataArrayList.get(getCurrentTimerPosition()).getTimerTempMaxTimeInMillis();
        }

        timerProgressBar.setInstantProgress(getCurrentStep(currentTimeInMillis, maxTimeInMillis));

        String resumeTimeLeft = String.format(Locale.getDefault(),"%02d:%02d:%02d", pauseHours,pauseMinutes,pauseSeconds);
        String resumeMillisLeft = String.format(Locale.getDefault(), "%02d", (int) Math.floor(pauseMilliseconds));

        timerHourMinute.setText(resumeTimeLeft);
        timerMillis.setText(resumeMillisLeft);
    }
    private void postSetPauseValues(){
        TickTrackAnimator.fabLayoutUnDissolve(resetFAB);
        TickTrackAnimator.fabLayoutDissolve(plusOneFAB);
        deleteButton.setVisibility(View.VISIBLE);
        editButton.setVisibility(View.GONE);
        startEndLayout.setVisibility(View.GONE);

        fabImageSetup(1);

        pauseHours = timerDataArrayList.get(getCurrentTimerPosition()).getTimerHourLeft();
        pauseMinutes = timerDataArrayList.get(getCurrentTimerPosition()).getTimerMinuteLeft();
        pauseSeconds = timerDataArrayList.get(getCurrentTimerPosition()).getTimerSecondLeft();
        pauseMilliseconds = (int) (timerDataArrayList.get(getCurrentTimerPosition()).getTimerMilliSecondLeft());

        currentTimeInMillis = countDownTimerMillis;

        String resumeTimeLeft = String.format(Locale.getDefault(),"%02d:%02d:%02d", pauseHours,pauseMinutes,pauseSeconds);
        String resumeMillisLeft = String.format(Locale.getDefault(), "%02d", (int) Math.floor(pauseMilliseconds));

        timerHourMinute.setText(resumeTimeLeft);
        timerMillis.setText(resumeMillisLeft);
    }



    private void presetResumeValues() {

        long resumeTimeInMillis = timerDataArrayList.get(getCurrentTimerPosition()).getTimerAlarmEndTimeInMillis() - SystemClock.elapsedRealtime();
        currentTimeInMillis = resumeTimeInMillis;
        TickTrackAnimator.fabLayoutDissolve(resetFAB);
        deleteButton.setVisibility(View.GONE);
        editButton.setVisibility(View.GONE);
        TickTrackAnimator.fabLayoutUnDissolve(plusOneFAB);
        fabImageSetup(2);

        int resumeHours = (int) TimeUnit.MILLISECONDS.toHours(resumeTimeInMillis);
        int resumeMinutes = (int) (TimeUnit.MILLISECONDS.toMinutes(resumeTimeInMillis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(resumeTimeInMillis)));
        int resumeSeconds = (int) (TimeUnit.MILLISECONDS.toSeconds(resumeTimeInMillis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(resumeTimeInMillis)));
        resumeMilliseconds = (float) (TimeUnit.MILLISECONDS.toMillis(resumeTimeInMillis) - TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(resumeTimeInMillis)))/10;

        if(timerDataArrayList.get(getCurrentTimerPosition()).getTimerTempMaxTimeInMillis() == -1){
            maxTimeInMillis = timerDataArrayList.get(getCurrentTimerPosition()).getTimerTotalTimeInMillis();
        } else {
            maxTimeInMillis = timerDataArrayList.get(getCurrentTimerPosition()).getTimerTempMaxTimeInMillis();
        }

        timerProgressBar.setInstantProgress(getCurrentStep(resumeTimeInMillis, maxTimeInMillis));

        String resumeTimeLeft = String.format(Locale.getDefault(),"%02d:%02d:%02d", resumeHours, resumeMinutes, resumeSeconds);
        String resumeMillisLeft = String.format(Locale.getDefault(), "%02d", (int) resumeMilliseconds);

        timerHourMinute.setText(resumeTimeLeft);
        timerMillis.setText(resumeMillisLeft);

    }

    private void stopTimer(){

        timerMillis.setVisibility(View.GONE);
        startEndLayout.setVisibility(View.GONE);
        timerStopHandler = new Handler();
        timerBlinkHandler = new Handler();

        timerDataArrayList.get(getCurrentTimerPosition()).setTimerRinging(true);
        timerProgressBar.setProgress(1);
        timerStopHandler.postDelayed(runnable, 0);
        timerBlinkHandler.postDelayed(blinkRunnable, 0);

        fabImageSetup(3);

        tickTrackDatabase.storeTimerList(timerDataArrayList);
        timerDataArrayList = tickTrackDatabase.retrieveTimerList();
        booleanRefresh();

        sharedPreferences = tickTrackDatabase.getSharedPref(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);

    }
    private void killAndResetTimer(){

        if(timerStopHandler!=null){ timerStopHandler.removeCallbacks(runnable); }
        if(timerBlinkHandler!=null){ timerBlinkHandler.removeCallbacks(blinkRunnable); }

        timerMillis.setVisibility(View.VISIBLE);
        timerProgressBar.setVisibility(View.VISIBLE);
        resetTimer();
        setupFABListeners();
        timerDataArrayList.get(getCurrentTimerPosition()).setTimerOn(false);
        timerDataArrayList.get(getCurrentTimerPosition()).setTimerPause(false);
        timerDataArrayList.get(getCurrentTimerPosition()).setTimerRinging(false);
        timerDataArrayList.get(getCurrentTimerPosition()).setTimerEndedTimeInMillis(-1);
        tickTrackDatabase.storeTimerList(timerDataArrayList);
        timerDataArrayList = tickTrackDatabase.retrieveTimerList();
        booleanRefresh();

        if(isMyServiceRunning(TimerRingService.class, activity)){
            stopRingService();
        }
    }

    private void addOneTimer(){
        timerDataArrayList = tickTrackDatabase.retrieveTimerList();
        if(timerDataArrayList.get(getCurrentTimerPosition()).isTimerRinging()){
            timerStopHandler.removeCallbacks(runnable);
            timerBlinkHandler.removeCallbacks(blinkRunnable);
            timerDataArrayList.get(getCurrentTimerPosition()).setTimerRinging(false);
            timerDataArrayList.get(getCurrentTimerPosition()).setTimerOn(true);
            timerDataArrayList.get(getCurrentTimerPosition()).setTimerPause(false);
            tickTrackDatabase.storeTimerList(timerDataArrayList);
            if(isMyServiceRunning(TimerRingService.class, activity)){
                stopRingService();
            }
        } else {
            if(countDownTimer!=null){
                countDownTimer.cancel();
            }
        }
        tickTrackTimerDatabase.cancelAlarm(timerID, false);
        countDownTimerMillis += 1000*2;
        maxTimeInMillis += 1000*2;
        timerDataArrayList.get(getCurrentTimerPosition()).setTimerTempMaxTimeInMillis(maxTimeInMillis);
        startTimer(countDownTimerMillis);
    }

    private Handler timerStopHandler;
    private Handler timerBlinkHandler;
    boolean isBlink = false;
    public Runnable runnable = new Runnable() {
        public void run() {
            long durationElapsed = SystemClock.elapsedRealtime() - timerDataArrayList.get(getCurrentTimerPosition()).getTimerEndedTimeInMillis();
            timerHourMinute.setText(updateStopTimeText(durationElapsed));
            timerStopHandler.postDelayed(this,500);
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

    private String updateStopTimeText(long countdownValue) {
        int hours = (int) TimeUnit.MILLISECONDS.toHours(countdownValue);
        int minutes = (int) (TimeUnit.MILLISECONDS.toMinutes(countdownValue) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(countdownValue)));
        int seconds = (int) (TimeUnit.MILLISECONDS.toSeconds(countdownValue) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(countdownValue)));

        return String.format(Locale.getDefault(),"- %02d:%02d:%02d", hours,minutes,seconds);
    }

    private void presetStockValues() {

        if(timerProgressBar.getProgress()!=1){
            progressBarPreHandler.postDelayed(() -> timerProgressBar.setProgress(1), 400);
        }
        TickTrackAnimator.fabLayoutDissolve(resetFAB);
        deleteButton.setVisibility(View.VISIBLE);
        editButton.setVisibility(View.VISIBLE);
        startEndLayout.setVisibility(View.GONE);
        TickTrackAnimator.fabLayoutDissolve(plusOneFAB);
        fabImageSetup(1);

        int staticHours = timerDataArrayList.get(getCurrentTimerPosition()).getTimerHour();
        int staticMinutes = timerDataArrayList.get(getCurrentTimerPosition()).getTimerMinute();
        int staticSeconds = timerDataArrayList.get(getCurrentTimerPosition()).getTimerSecond();
        int staticMilliseconds = 0;

        currentTimeInMillis = TimeAgo.getTimerDataInMillis(staticHours, staticMinutes, staticSeconds, staticMilliseconds);
        maxTimeInMillis = timerDataArrayList.get(getCurrentTimerPosition()).getTimerTotalTimeInMillis();

        String resumeTimeLeft = String.format(Locale.getDefault(),"%02d:%02d:%02d", staticHours,staticMinutes,staticSeconds);
        String resumeMillisLeft = String.format(Locale.getDefault(), "%02d", staticMilliseconds);

        timerHourMinute.setText(resumeTimeLeft);
        timerMillis.setText(resumeMillisLeft);
    }

    private void stopRingService() {
        Intent intent = new Intent(this, TimerRingService.class);
        intent.setAction(TimerRingService.ACTION_STOP_SERVICE_CHECK);
        startService(intent);
    }
    private boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    @Override
    protected void onResume() {
        super.onResume();
        timerDataArrayList = tickTrackDatabase.retrieveTimerList();
        checkValuesInit();
        timerDataArrayList.get(getCurrentTimerPosition()).setTimerNotificationOn(false);
        tickTrackDatabase.storeTimerList(timerDataArrayList);
        tickTrackTimerDatabase = new TickTrackTimerDatabase(activity);
        timerDataArrayList = tickTrackDatabase.retrieveTimerList();

        sharedPreferences = tickTrackDatabase.getSharedPref(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);

        if(tickTrackTimerDatabase.isMyServiceRunning(TimerService.class)){
            tickTrackTimerDatabase.stopNotificationService();
        }

        checkConditions();

    }
    @Override
    protected void onPause() {
        super.onPause();
        sharedPreferences = tickTrackDatabase.getSharedPref(this);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
        if(getCurrentTimerPosition()!=-1){
            if(timerDataArrayList.get(getCurrentTimerPosition()).isTimerOn() && !timerDataArrayList.get(getCurrentTimerPosition()).isTimerPause() && !timerDataArrayList.get(getCurrentTimerPosition()).isTimerRinging()){
                if(!isTimerRinging){
                    timerDataArrayList.get(getCurrentTimerPosition()).setTimerNotificationOn(true);
                    if(!tickTrackTimerDatabase.isMyServiceRunning(TimerService.class)){
                        tickTrackTimerDatabase.startNotificationService();
                    }
                } else {
                    timerDataArrayList.get(getCurrentTimerPosition()).setTimerNotificationOn(false);
                    if(tickTrackTimerDatabase.isMyServiceRunning(TimerService.class)){
                        tickTrackTimerDatabase.stopNotificationService();
                    }
                }
            } else {
                timerDataArrayList.get(getCurrentTimerPosition()).setTimerNotificationOn(false);
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
        tickTrackDatabase.storeCurrentFragmentNumber(2);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(timerStopHandler!=null && timerBlinkHandler!=null){
            timerStopHandler.removeCallbacks(runnable);
            timerBlinkHandler.removeCallbacks(blinkRunnable);
        }
        tickTrackDatabase.storeCurrentFragmentNumber(2);
        Intent intent = new Intent(activity, SoYouADeveloperHuh.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
