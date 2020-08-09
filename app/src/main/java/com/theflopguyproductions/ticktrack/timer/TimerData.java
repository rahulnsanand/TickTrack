package com.theflopguyproductions.ticktrack.timer;

import android.os.Handler;


import java.sql.Timestamp;

public class TimerData implements Comparable<TimerData> {


    int timerHour, timerMinute, timerSecond, timerIntegerID, timerFlag;
    int timerHourLeft, timerMinuteLeft, timerSecondLeft;
    float timerMilliSecondLeft;
    long timerAlarmEndTimeInMillis, timerTotalTimeInMillis, timerEndedTimeInMillis;
    Timestamp timerCreateTimeStamp;
    String timerStringID, timerLabel;
    boolean timerOn, timerPause, timerReset, isNew, timerStop;

    boolean isTimerRinging;
    boolean isTimerNotificationOn;
    private stoppedTimerListener stoppedTimerListener;
    Handler timerHandler;
    float updatedElapsedTime;

    public boolean isTimerRinging() {
        return isTimerRinging;
    }

    public void setTimerRinging(boolean timerRinging) {
        isTimerRinging = timerRinging;
    }

    public boolean isTimerNotificationOn() {
        return isTimerNotificationOn;
    }

    public void setTimerNotificationOn(boolean timerNotificationOn) {
        isTimerNotificationOn = timerNotificationOn;
    }

    public void setStoppedTimerListener(TimerData.stoppedTimerListener stoppedTimerListener) {
        this.stoppedTimerListener = stoppedTimerListener;
    }

    private Runnable stoppedTimerElapsedTimeRunnable = new Runnable() {
        @Override
        public void run() {
            if (isTimerRinging) {
                if (stoppedTimerListener != null) {
                    increment();
                    stoppedTimerListener.onTick(updatedElapsedTime);
                    System.out.println(timerLabel+updatedElapsedTime+"<<<<");
                }
            } else {
                timerHandler.removeCallbacks(this);
                return;
            }
            timerHandler.postDelayed(this, 100);
        }
    };

    private void increment() {
        updatedElapsedTime += 1;
    }

    public void start(long timerEndTimeInMillis) {
        if(timerEndTimeInMillis != -1){
            updatedElapsedTime = (System.currentTimeMillis() - timerEndTimeInMillis) / 1000F;
            isTimerRinging = true;
            timerHandler.post(stoppedTimerElapsedTimeRunnable);
        }
    }

    public interface stoppedTimerListener {
        void onTick(float currentValue);
    }

    public long getTimerEndedTimeInMillis() {
        return timerEndedTimeInMillis;
    }

    public void setTimerEndedTimeInMillis(long timerEndedTimeInMillis) {
        this.timerEndedTimeInMillis = timerEndedTimeInMillis;
    }

    public boolean isTimerStop() {
        return timerStop;
    }

    public void setTimerStop(boolean timerStop) {
        this.timerStop = timerStop;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public long getTimerTotalTimeInMillis() {
        return timerTotalTimeInMillis;
    }

    public void setTimerTotalTimeInMillis(long timerTotalTimeInMillis) {
        this.timerTotalTimeInMillis = timerTotalTimeInMillis;
    }

    public int getTimerHour() {
        return timerHour;
    }

    public void setTimerHour(int timerHour) {
        this.timerHour = timerHour;
    }

    public int getTimerMinute() {
        return timerMinute;
    }

    public void setTimerMinute(int timerMinute) {
        this.timerMinute = timerMinute;
    }

    public int getTimerSecond() {
        return timerSecond;
    }

    public void setTimerSecond(int timerSecond) {
        this.timerSecond = timerSecond;
    }

    public int getTimerIntegerID() {
        return timerIntegerID;
    }

    public void setTimerIntegerID(int timerIntegerID) {
        this.timerIntegerID = timerIntegerID;
    }

    public int getTimerFlag() {
        return timerFlag;
    }

    public void setTimerFlag(int timerFlag) {
        this.timerFlag = timerFlag;
    }

    public int getTimerHourLeft() {
        return timerHourLeft;
    }

    public void setTimerHourLeft(int timerHourLeft) {
        this.timerHourLeft = timerHourLeft;
    }

    public int getTimerMinuteLeft() {
        return timerMinuteLeft;
    }

    public void setTimerMinuteLeft(int timerMinuteLeft) {
        this.timerMinuteLeft = timerMinuteLeft;
    }

    public int getTimerSecondLeft() {
        return timerSecondLeft;
    }

    public void setTimerSecondLeft(int timerSecondLeft) {
        this.timerSecondLeft = timerSecondLeft;
    }

    public float getTimerMilliSecondLeft() {
        return timerMilliSecondLeft;
    }

    public void setTimerMilliSecondLeft(float timerMilliSecondLeft) {
        this.timerMilliSecondLeft = timerMilliSecondLeft;
    }

    public long getTimerAlarmEndTimeInMillis() {
        return timerAlarmEndTimeInMillis;
    }

    public void setTimerAlarmEndTimeInMillis(long timerAlarmEndTimeInMillis) {
        this.timerAlarmEndTimeInMillis = timerAlarmEndTimeInMillis;
    }

    public Timestamp getTimerCreateTimeStamp() {
        return timerCreateTimeStamp;
    }

    public void setTimerCreateTimeStamp(Timestamp timerCreateTimeStamp) {
        this.timerCreateTimeStamp = timerCreateTimeStamp;
    }

    public String getTimerStringID() {
        return timerStringID;
    }

    public void setTimerStringID(String timerStringID) {
        this.timerStringID = timerStringID;
    }

    public String getTimerLabel() {
        return timerLabel;
    }

    public void setTimerLabel(String timerLabel) {
        this.timerLabel = timerLabel;
    }

    public boolean isTimerOn() {
        return timerOn;
    }

    public void setTimerOn(boolean timerOn) {
        this.timerOn = timerOn;
    }

    public boolean isTimerPause() {
        return timerPause;
    }

    public void setTimerPause(boolean timerPause) {
        this.timerPause = timerPause;
    }

    public boolean isTimerReset() {
        return timerReset;
    }

    public void setTimerReset(boolean timerReset) {
        this.timerReset = timerReset;
    }

    @Override
    public int compareTo(TimerData timerData) {
        return 0;
    }
}
