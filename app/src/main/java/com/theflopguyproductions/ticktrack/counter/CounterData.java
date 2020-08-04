package com.theflopguyproductions.ticktrack.counter;

import android.os.CountDownTimer;

import androidx.annotation.NonNull;

import java.sql.Timestamp;

public class CounterData implements Comparable<CounterData>, Cloneable{

    int counterValue, counterFlag, counterSignificantCount;
    boolean counterSignificantExist, counterSwipeMode, counterPersistentNotification;
    String counterLabel, counterID;
    Timestamp counterTimestamp;

    public String getCounterID() {
        return counterID;
    }

    public void setCounterID(String counterID) {
        this.counterID = counterID;
    }

    public boolean isCounterPersistentNotification() {
        return counterPersistentNotification;
    }

    public void setCounterPersistentNotification(boolean counterPersistentNotification) {
        this.counterPersistentNotification = counterPersistentNotification;
    }

    public boolean isCounterSwipeMode() {
        return counterSwipeMode;
    }

    public void setCounterSwipeMode(boolean counterSwipeMode) {
        this.counterSwipeMode = counterSwipeMode;
    }

    public int getCounterSignificantCount() {
        return counterSignificantCount;
    }

    public void setCounterSignificantCount(int counterSignificantCount) {
        this.counterSignificantCount = counterSignificantCount;
    }

    public boolean isCounterSignificantExist() {
        return counterSignificantExist;
    }

    public void setCounterSignificantExist(boolean counterSignificantExist) {
        this.counterSignificantExist = counterSignificantExist;
    }

    public int getCounterValue() {
        return counterValue;
    }

    public void setCounterValue(int counterValue) {
        this.counterValue = counterValue;
    }

    public int getCounterFlag() {
        return counterFlag;
    }

    public void setCounterFlag(int counterFlag) {
        this.counterFlag = counterFlag;
    }

    public String getCounterLabel() {
        return counterLabel;
    }

    public void setCounterLabel(String counterLabel) {
        this.counterLabel = counterLabel;
    }

    public Timestamp getCounterTimestamp() {
        return counterTimestamp;
    }

    public void setCounterTimestamp(Timestamp counterTimestamp) {
        this.counterTimestamp = counterTimestamp;
    }

    @Override
    public int compareTo(CounterData counterData) {
        int check = this.getCounterTimestamp().compareTo(counterData.getCounterTimestamp());

        if(check<=0){
            if(check==0){
                return 0;
            } else {
                return 1;
            }
        }
        return -1;
    }

    @NonNull
    @Override
    public CounterData clone() {

        CounterData clone;
        try {
            clone = (CounterData) super.clone();

        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e); //should not happen
        }

        return clone;
    }
}
