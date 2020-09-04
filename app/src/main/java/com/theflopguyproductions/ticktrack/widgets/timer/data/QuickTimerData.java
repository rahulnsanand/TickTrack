package com.theflopguyproductions.ticktrack.widgets.timer.data;

public class QuickTimerData {

    int timerHour, timerMinute, timerSecond, timerIntID;
    int timerHourLeft, timerMinuteLeft, timerSecondLeft;
    float timerMilliSecondLeft;
    long timerAlarmEndTimeInMillis, timerEndTimeInMillis;
    long timerTotalTimeInMillis;
    long timerEndedTimeInMillis;

    long timerTempMaxTimeInMillis, timerStartTimeInMillis;
    boolean timerOn, timerPause;
    boolean isTimerRinging;
    boolean isTimerNotificationOn;

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

    public int getTimerIntID() {
        return timerIntID;
    }

    public void setTimerIntID(int timerIntID) {
        this.timerIntID = timerIntID;
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

    public long getTimerEndTimeInMillis() {
        return timerEndTimeInMillis;
    }

    public void setTimerEndTimeInMillis(long timerEndTimeInMillis) {
        this.timerEndTimeInMillis = timerEndTimeInMillis;
    }

    public long getTimerTotalTimeInMillis() {
        return timerTotalTimeInMillis;
    }

    public void setTimerTotalTimeInMillis(long timerTotalTimeInMillis) {
        this.timerTotalTimeInMillis = timerTotalTimeInMillis;
    }

    public long getTimerEndedTimeInMillis() {
        return timerEndedTimeInMillis;
    }

    public void setTimerEndedTimeInMillis(long timerEndedTimeInMillis) {
        this.timerEndedTimeInMillis = timerEndedTimeInMillis;
    }

    public long getTimerTempMaxTimeInMillis() {
        return timerTempMaxTimeInMillis;
    }

    public void setTimerTempMaxTimeInMillis(long timerTempMaxTimeInMillis) {
        this.timerTempMaxTimeInMillis = timerTempMaxTimeInMillis;
    }

    public long getTimerStartTimeInMillis() {
        return timerStartTimeInMillis;
    }

    public void setTimerStartTimeInMillis(long timerStartTimeInMillis) {
        this.timerStartTimeInMillis = timerStartTimeInMillis;
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
}
