package com.theflopguyproductions.ticktrack.timer.data;

public class TimerData implements Comparable<TimerData> {


    int timerHour, timerMinute, timerSecond, timerFlag, timerIntID;
    int timerHourLeft, timerMinuteLeft, timerSecondLeft;
    float timerMilliSecondLeft;
    long timerAlarmEndTimeInMillis, timerEndTimeInMillis;
    long timerTotalTimeInMillis;
    long timerEndedTimeInMillis;

    long timerTempMaxTimeInMillis, timerStartTimeInMillis;
    long timerLastEdited;
    String timerLabel, timerID;
    boolean timerOn, timerPause;

    boolean isTimerRinging;
    boolean isTimerNotificationOn;

    boolean isQuickTimer;

    public boolean isQuickTimer() {
        return isQuickTimer;
    }

    public void setQuickTimer(boolean quickTimer) {
        isQuickTimer = quickTimer;
    }

    public int getTimerIntID() {
        return timerIntID;
    }

    public void setTimerIntID(int timerIntID) {
        this.timerIntID = timerIntID;
    }

    public long getTimerEndTimeInMillis() {
        return timerEndTimeInMillis;
    }

    public void setTimerEndTimeInMillis(long timerEndTimeInMillis) {
        this.timerEndTimeInMillis = timerEndTimeInMillis;
    }

    public long getTimerStartTimeInMillis() {
        return timerStartTimeInMillis;
    }

    public void setTimerStartTimeInMillis(long timerStartTimeInMillis) {
        this.timerStartTimeInMillis = timerStartTimeInMillis;
    }

    public long getTimerTempMaxTimeInMillis() {
        return timerTempMaxTimeInMillis;
    }

    public void setTimerTempMaxTimeInMillis(long timerTempMaxTimeInMillis) {
        this.timerTempMaxTimeInMillis = timerTempMaxTimeInMillis;
    }

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

    public long getTimerEndedTimeInMillis() {
        return timerEndedTimeInMillis;
    }

    public void setTimerEndedTimeInMillis(long timerEndedTimeInMillis) {
        this.timerEndedTimeInMillis = timerEndedTimeInMillis;
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

    public String getTimerID() {
        return timerID;
    }

    public void setTimerID(String timerID) {
        this.timerID = timerID;
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

    public long getTimerLastEdited() {
        return timerLastEdited;
    }

    public void setTimerLastEdited(long timerLastEdited) {
        this.timerLastEdited = timerLastEdited;
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

    @Override
    public int compareTo(TimerData timerData) {
        return Long.compare(this.timerTotalTimeInMillis, timerData.getTimerTotalTimeInMillis());
    }



}
