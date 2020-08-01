package com.theflopguyproductions.ticktrack.timer;

import java.sql.Timestamp;

public class TimerData implements Comparable<TimerData> {


    int timerHour, timerMinute, timerSecond, timerIntegerID, timerFlag;
    int timerHourLeft, timerMinuteLeft, timerSecondLeft, timerMilliSecondLeft;
    long timeLeftInMillis;
    Timestamp timerCreateTimeStamp;
    String timerID, timerLabel;
    boolean timerOn, timerPause;

    public int getTimerMilliSecondLeft() {
        return timerMilliSecondLeft;
    }

    public void setTimerMilliSecondLeft(int timerMilliSecondLeft) {
        this.timerMilliSecondLeft = timerMilliSecondLeft;
    }

    public boolean isTimerPause() {
        return timerPause;
    }

    public void setTimerPause(boolean timerPause) {
        this.timerPause = timerPause;
    }

    public long getTimeLeftInMillis() {
        return timeLeftInMillis;
    }

    public void setTimeLeftInMillis(long timeLeftInMillis) {
        this.timeLeftInMillis = timeLeftInMillis;
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

    public Timestamp getTimerCreateTimeStamp() {
        return timerCreateTimeStamp;
    }

    public void setTimerCreateTimeStamp(Timestamp timerCreateTimeStamp) {
        this.timerCreateTimeStamp = timerCreateTimeStamp;
    }

    public String getTimerID() {
        return timerID;
    }

    public void setTimerID(String timerID) {
        this.timerID = timerID;
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

    @Override
    public int compareTo(TimerData timerData) {
        return 0;
    }
}
