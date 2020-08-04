package com.theflopguyproductions.ticktrack.timer.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.style.TtsSpan;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.SoYouADeveloperHuh;
import com.theflopguyproductions.ticktrack.counter.notification.CounterNotificationService;
import com.theflopguyproductions.ticktrack.dialogs.DeleteTimer;
import com.theflopguyproductions.ticktrack.dialogs.TimerEditDialog;
import com.theflopguyproductions.ticktrack.timer.TimerData;
import com.theflopguyproductions.ticktrack.timer.service.TimerBroadcastReceiver;
import com.theflopguyproductions.ticktrack.timer.service.TimerService;
import com.theflopguyproductions.ticktrack.timer.service.TimerServiceData;
import com.theflopguyproductions.ticktrack.ui.timer.TimerRecyclerFragment;
import com.theflopguyproductions.ticktrack.ui.utils.TickTrackProgressBar;
import com.theflopguyproductions.ticktrack.utils.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.TickTrackThemeSetter;
import com.theflopguyproductions.ticktrack.utils.TimeAgo;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Locale;

public class TimerActivity extends AppCompatActivity {

    private ConstraintLayout timerActivityToolbar, timerActivityRootLayout;
    private TextView timerHourMinute, timerMillis, labelTextView;
    private FloatingActionButton playPauseFAB, resetFAB;
    private ImageButton backButton, editButton, deleteButton;
    private TickTrackProgressBar timerProgressBar, timerProgressBarBackground;

    private String timerIDString;
    private int timerIDInteger, timerCurrentPosition;
    private boolean isPause, isRunning, isReset, isNew = false;
    private Activity activity;

    private ArrayList<TimerData> timerDataArrayList = new ArrayList<>();

    private int resumeHours, resumeMinutes, resumeSeconds, resumeMilliseconds;
    private int pauseHours, pauseMinutes, pauseSeconds, pauseMilliseconds;
    private int staticHours, staticMinutes, staticSeconds, staticMilliseconds;
    private long resumeTimeInMillis, currentTimeInMillis;

    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        initVariables();

        System.out.println("onCreate");

        timerIDString = getIntent().getStringExtra("timerID");
        activity = this;
        timerDataArrayList = TickTrackDatabase.retrieveTimerList(activity);

        if(timerIDString!=null){

            setupCurrentTimerInitValues();

            if(isRunning && !isPause){

                if((timerDataArrayList.get(timerCurrentPosition).getTimerEndTimeInMillis()-System.currentTimeMillis())<0){
                    System.out.println("INVALID TIMER RAN");

                    timerDataArrayList.get(timerCurrentPosition).setTimerPause(false);
                    timerDataArrayList.get(timerCurrentPosition).setTimerOn(false);
                    timerDataArrayList.get(timerCurrentPosition).setTimerReset(true);
                    isPause = false;
                    isRunning = false;
                    isReset = true;
                    isNew = false;
                    TickTrackDatabase.storeTimerList(timerDataArrayList, activity);
                    prefixStaticTimerValues(TimeAgo.getTimerDataInMillis(timerDataArrayList.get(timerCurrentPosition).getTimerHour(),timerDataArrayList.get(timerCurrentPosition).getTimerMinute(),
                            timerDataArrayList.get(timerCurrentPosition).getTimerSecond(),0));
                } else {
                    //TODO TIMER IS RUNNING IN BACKGROUND
                    isPause = false;
                    isRunning = true;
                    prefixOnResumeValues();

                }

            } else if(isPause && isRunning){

                //TODO TIMER IS ON PAUSE
                prefixPauseTimerValues(timerDataArrayList.get(timerCurrentPosition).getTimeLeftInMillis());

            } else if(isReset && !isPause){

                //TODO TIMER IS RESET
                prefixStaticTimerValues(TimeAgo.getTimerDataInMillis(timerDataArrayList.get(timerCurrentPosition).getTimerHour(),timerDataArrayList.get(timerCurrentPosition).getTimerMinute(),
                        timerDataArrayList.get(timerCurrentPosition).getTimerSecond(),0));

            } else {

                //TODO RUN NEW TIMER

                System.out.println("ELSE RAN:");

                isNew = true;
                prefixStaticTimerValues(TimeAgo.getTimerDataInMillis(timerDataArrayList.get(timerCurrentPosition).getTimerHour(),timerDataArrayList.get(timerCurrentPosition).getTimerMinute(),
                        timerDataArrayList.get(timerCurrentPosition).getTimerSecond(),0));
                startTimer(timerDataArrayList.get(timerCurrentPosition).getTimerTotalTimeInMillis());
            }

            setupOnClickListeners();
            setupFABListeners();

        } else {
            onBackPressed();
        }
    }

    private void setupFABListeners(){
        playPauseFAB.setOnClickListener(view -> {
            if(isRunning && !isPause){
                pauseTimer();
            } else if(isRunning && !isReset) {
                startTimer(timerDataArrayList.get(timerCurrentPosition).getTimeLeftInMillis());
            } else if(isReset && !isRunning && !isPause){
                startTimer(timerDataArrayList.get(timerCurrentPosition).getTimerTotalTimeInMillis());
            }
        });

        resetFAB.setOnClickListener(view -> resetTimer());
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
                TickTrackDatabase.storeTimerList(timerDataArrayList,activity);
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

    private void prefixOnResumeValues() {
        resumeTimeInMillis = timerDataArrayList.get(timerCurrentPosition).getTimerEndTimeInMillis()-System.currentTimeMillis();
        resetFAB.setVisibility(View.INVISIBLE);
        playPauseFAB.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_round_pause_white_24));
        resumeHours = (int) (resumeTimeInMillis/(1000*60*60));
        resumeMinutes = (int) (resumeTimeInMillis%(1000*60*60))/(1000*60);
        resumeSeconds = (int) (resumeTimeInMillis%(1000*60*60))%(1000*60)/1000;
        resumeMilliseconds = (int) (resumeTimeInMillis%(1000*60*60))%(1000*60)/10%100;
        timerProgressBar.setInstantProgress(getCurrentStep(resumeTimeInMillis, timerDataArrayList.get(timerCurrentPosition).getTimerTotalTimeInMillis()));
        String resumeTimeLeft = String.format(Locale.getDefault(),"%02d:%02d:%02d", resumeHours,resumeMinutes,resumeSeconds);
        String resumeMillisLeft = String.format(Locale.getDefault(), "%02d", resumeMilliseconds);

        timerHourMinute.setText(resumeTimeLeft);
        timerMillis.setText(resumeMillisLeft);
        System.out.println("RESUME TIMER RAN");

    }

    private void prefixPauseTimerValues(long pauseTimeValues) {
        resetFAB.setVisibility(View.VISIBLE);
        playPauseFAB.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_round_play_white_24));
        pauseHours = (int) (pauseTimeValues/(1000*60*60));
        pauseMinutes = (int) (pauseTimeValues%(1000*60*60))/(1000*60);
        pauseSeconds = (int) (pauseTimeValues%(1000*60*60))%(1000*60)/1000;
        pauseMilliseconds = (int) (pauseTimeValues%(1000*60*60))%(1000*60)/10%100;
        timerProgressBar.setInstantProgress(getCurrentStep(pauseTimeValues, timerDataArrayList.get(timerCurrentPosition).getTimerTotalTimeInMillis()));
        String resumeTimeLeft = String.format(Locale.getDefault(),"%02d:%02d:%02d", pauseHours,pauseMinutes,pauseSeconds);
        String resumeMillisLeft = String.format(Locale.getDefault(), "%02d", pauseMilliseconds);

        timerHourMinute.setText(resumeTimeLeft);
        timerMillis.setText(resumeMillisLeft);
    }

    private void prefixStaticTimerValues(long staticTimeValues) {
        timerProgressBar.setProgress(1);
        resetFAB.setVisibility(View.INVISIBLE);
        playPauseFAB.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_round_play_white_24));
        staticHours = (int) (staticTimeValues/(1000*60*60));
        staticMinutes = (int) (staticTimeValues%(1000*60*60))/(1000*60);
        staticSeconds = (int) (staticTimeValues%(1000*60*60))%(1000*60)/1000;
        staticMilliseconds = (int) (staticTimeValues%(1000*60*60))%(1000*60)/10%100;

        String resumeTimeLeft = String.format(Locale.getDefault(),"%02d:%02d:%02d", staticHours,staticMinutes,staticSeconds);
        String resumeMillisLeft = String.format(Locale.getDefault(), "%02d", staticMilliseconds);

        timerHourMinute.setText(resumeTimeLeft);
        timerMillis.setText(resumeMillisLeft);
    }

    long timerMaxLength;
    private void startTimer(long timerValueInMillis) {

        currentTimeInMillis = timerValueInMillis;
        timerMaxLength = timerDataArrayList.get(timerCurrentPosition).getTimerTotalTimeInMillis();

        setAlarmTimer();

        countDownTimer = new CountDownTimer(currentTimeInMillis, 10) {
            @Override
            public void onTick(long l) {
                currentTimeInMillis = l;
                updateTimerTextView(currentTimeInMillis);
                timerProgressBar.setProgress(getCurrentStep(currentTimeInMillis, timerMaxLength));
            }
            @Override
            public void onFinish() {
                resetTimer();
                playPauseFAB.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_round_play_white_24));
                timerDataArrayList.get(timerCurrentPosition).setTimerOn(false);
                timerDataArrayList.get(timerCurrentPosition).setTimerPause(false);
                timerDataArrayList.get(timerCurrentPosition).setTimerReset(true);
                TickTrackDatabase.storeTimerList(timerDataArrayList,activity);
                timerDataArrayList  = TickTrackDatabase.retrieveTimerList(activity);
                isPause = timerDataArrayList.get(timerCurrentPosition).isTimerPause();
                isRunning = timerDataArrayList.get(timerCurrentPosition).isTimerOn();
            }
        }.start();

        timerDataArrayList.get(timerCurrentPosition).setTimerOn(true);
        timerDataArrayList.get(timerCurrentPosition).setTimerPause(false);
        timerDataArrayList.get(timerCurrentPosition).setTimerReset(false);
        TickTrackDatabase.storeTimerList(timerDataArrayList,activity);
        timerDataArrayList = TickTrackDatabase.retrieveTimerList(activity);
        isPause = timerDataArrayList.get(timerCurrentPosition).isTimerPause();
        isRunning = timerDataArrayList.get(timerCurrentPosition).isTimerOn();
        isReset = timerDataArrayList.get(timerCurrentPosition).isTimerReset();
        playPauseFAB.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_round_pause_white_24));
        resetFAB.setVisibility(View.INVISIBLE);
    }

    private void resetTimer() {
        timerDataArrayList.get(timerCurrentPosition).setTimerOn(false);
        timerDataArrayList.get(timerCurrentPosition).setTimerPause(false);
        timerDataArrayList.get(timerCurrentPosition).setTimerReset(true);
        isPause = timerDataArrayList.get(timerCurrentPosition).isTimerPause();
        isRunning = timerDataArrayList.get(timerCurrentPosition).isTimerOn();
        isReset = timerDataArrayList.get(timerCurrentPosition).isTimerReset();
        TickTrackDatabase.storeTimerList(timerDataArrayList,activity);
        prefixStaticTimerValues(timerDataArrayList.get(timerCurrentPosition).getTimerTotalTimeInMillis());
    }

    public void pauseTimer(){

        countDownTimer.cancel();

        int hours = (int) (currentTimeInMillis/(1000*60*60));
        int minutes = (int) (currentTimeInMillis%(1000*60*60))/(1000*60);
        int seconds = (int) (currentTimeInMillis%(1000*60*60))%(1000*60)/1000;
        int milliseconds = (int) (currentTimeInMillis%(1000*60*60))%(1000*60)/10%100;

        timerDataArrayList.get(timerCurrentPosition).setTimerOn(true);
        timerDataArrayList.get(timerCurrentPosition).setTimerPause(true);
        timerDataArrayList.get(timerCurrentPosition).setTimerReset(false);
        timerDataArrayList.get(timerCurrentPosition).setTimerHourLeft(hours);
        timerDataArrayList.get(timerCurrentPosition).setTimerMinuteLeft(minutes);
        timerDataArrayList.get(timerCurrentPosition).setTimerSecondLeft(seconds);
        timerDataArrayList.get(timerCurrentPosition).setTimerMilliSecondLeft(milliseconds);
        timerDataArrayList.get(timerCurrentPosition).setTimeLeftInMillis(currentTimeInMillis);
        TickTrackDatabase.storeTimerList(timerDataArrayList, activity);

        isPause = timerDataArrayList.get(timerCurrentPosition).isTimerPause();
        isRunning = timerDataArrayList.get(timerCurrentPosition).isTimerOn();
        isReset = timerDataArrayList.get(timerCurrentPosition).isTimerReset();

        prefixPauseTimerValues(currentTimeInMillis);

        cancelAlarmTimer();
        isNew = false;
    }

    private void updateTimerTextView(long countdownValue){
        int hours = (int) (countdownValue/(1000*60*60));
        int minutes = (int) (countdownValue%(1000*60*60))/(1000*60);
        int seconds = (int) (countdownValue%(1000*60*60))%(1000*60)/1000;
        int milliseconds = (int) (countdownValue%(1000*60*60))%(1000*60)/10%100;

        String timeLeft = String.format(Locale.getDefault(),"%02d:%02d:%02d", hours,minutes,seconds);
        String milliSecondLeft = String.format(Locale.getDefault(), "%02d", milliseconds);
        timerHourMinute.setText(timeLeft);
        timerMillis.setText(milliSecondLeft);
    }

    private void setupCurrentTimerInitValues() {
        for(int i =0; i < timerDataArrayList.size(); i++){
            if(timerDataArrayList.get(i).getTimerID().equals(timerIDString)){
                timerIDInteger = timerDataArrayList.get(i).getTimerIntegerID();
                isPause = timerDataArrayList.get(i).isTimerPause();
                isRunning = timerDataArrayList.get(i).isTimerOn();
                isReset = timerDataArrayList.get(i).isTimerReset();
                timerCurrentPosition = i;
                return;
            }
        }
    }

    private float getCurrentStep(long currentValue, long maxLength){
        return ((currentValue-0f)/(maxLength-0f)) *(1f-0f)+0f;
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
        timerProgressBar.setLinearProgress(true);
        timerProgressBar.setSpinSpeed(2.500f);
        timerProgressBarBackground.setLinearProgress(true);
        timerProgressBarBackground.setSpinSpeed(2.500f);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this,SoYouADeveloperHuh.class);
        intent.putExtra("FragmentID", 2);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(isRunning && !isPause && !isNew){
            System.out.println("START TIMER RAN");
            startTimer(resumeTimeInMillis);
        }
        SharedPreferences sharedPreferences = getSharedPreferences("TickTrackData",MODE_PRIVATE);
        timerServiceDataArrayList = retrieveTimerServiceDataList(sharedPreferences);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(isRunning && !isPause){
            timerDataArrayList.get(timerCurrentPosition).setTimerEndTimeInMillis(currentTimeInMillis+System.currentTimeMillis());
            timerDataArrayList.get(timerCurrentPosition).setTimerPause(false);
            timerDataArrayList.get(timerCurrentPosition).setTimerOn(true);
            timerDataArrayList.get(timerCurrentPosition).setTimerReset(false);
            TickTrackDatabase.storeTimerList(timerDataArrayList,activity);
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }
            SharedPreferences sharedPreferences = getSharedPreferences("TickTrackData",MODE_PRIVATE);
            addServiceData(sharedPreferences);
            if(!isMyServiceRunning(TimerService.class)){
                startNotificationService();
            }
        }
        TickTrackDatabase.storeTimerList(timerDataArrayList,activity);
    }

    private ArrayList<TimerServiceData> timerServiceDataArrayList = new ArrayList<>();
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void startNotificationService() {
        Intent intent = new Intent(this, TimerService.class);
        intent.setAction(TimerService.ACTION_START_TIMER_SERVICE);
        startService(intent);
    }

    private void addServiceData(SharedPreferences sharedPreferences) {

        for(int i = 0; i <timerServiceDataArrayList.size(); i++){
            if(timerServiceDataArrayList.get(i).getTimerIDInteger()==timerIDInteger){
                return;
            }
        }

        TimerServiceData timerServiceData = new TimerServiceData();
        timerServiceData.setEndTimeInMillis(currentTimeInMillis+System.currentTimeMillis());
        timerServiceData.setTimerIDInteger(timerIDInteger);
        timerServiceData.setTimerIDString(timerIDString);
        timerServiceDataArrayList.add(timerServiceData);
        storeTimerServiceData(sharedPreferences);

    }
    private ArrayList<TimerServiceData> retrieveTimerServiceDataList(SharedPreferences sharedPreferences){

        Gson gson = new Gson();
        String json = sharedPreferences.getString("TimerServiceData", null);
        Type type = new TypeToken<ArrayList<TimerServiceData>>() {}.getType();
        ArrayList<TimerServiceData> timerServiceData = gson.fromJson(json, type);

        if(timerServiceData == null){
            timerServiceData = new ArrayList<>();
        }

        return timerServiceData;
    }
    private void storeTimerServiceData(SharedPreferences sharedPreferences){

        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(timerServiceDataArrayList);
        editor.putString("TimerServiceData", json);
        editor.apply();

    }

    private void setAlarmTimer(){

        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(activity, TimerBroadcastReceiver.class);
        intent.setAction(TimerBroadcastReceiver.ACTION_TIMER_BROADCAST);
        intent.putExtra("timerIntegerID", timerIDInteger);
        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(activity, timerIDInteger, intent, 0);
        alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                currentTimeInMillis+System.currentTimeMillis(),
                alarmPendingIntent
        );
    }

    private void cancelAlarmTimer(){

        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(activity, TimerBroadcastReceiver.class);
        intent.setAction(TimerBroadcastReceiver.ACTION_TIMER_BROADCAST);
        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(activity, timerIDInteger, intent, 0);
        alarmManager.cancel(alarmPendingIntent);

        SharedPreferences sharedPreferences = getSharedPreferences("TickTrackData",MODE_PRIVATE);
        removeServiceData(sharedPreferences);
        timerServiceDataArrayList = retrieveTimerServiceDataList(sharedPreferences);
    }

    private void removeServiceData(SharedPreferences sharedPreferences) {
        for(int i = 0; i < timerServiceDataArrayList.size(); i++){
            if(timerServiceDataArrayList.get(i).getTimerIDInteger()==timerIDInteger){
                timerServiceDataArrayList.remove(i);
                storeTimerServiceData(sharedPreferences);
                return;
            }
        }
    }

}