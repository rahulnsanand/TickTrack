package com.theflopguyproductions.ticktrack.timer;

import android.os.Handler;


import java.sql.Timestamp;

public class TimerData implements Comparable<TimerData> {


    int timerHour, timerMinute, timerSecond, timerID, timerFlag;
    int timerHourLeft, timerMinuteLeft, timerSecondLeft;
    float timerMilliSecondLeft;
    long timerAlarmEndTimeInMillis, timerTotalTimeInMillis, timerEndedTimeInMillis;
    Timestamp timerCreateTimeStamp;
    String timerLabel;
    boolean timerOn, timerPause;

    boolean isTimerRinging;
    boolean isTimerNotificationOn;


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

    public int getTimerID() {
        return timerID;
    }

    public void setTimerID(int timerID) {
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

    public Timestamp getTimerCreateTimeStamp() {
        return timerCreateTimeStamp;
    }

    public void setTimerCreateTimeStamp(Timestamp timerCreateTimeStamp) {
        this.timerCreateTimeStamp = timerCreateTimeStamp;
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
