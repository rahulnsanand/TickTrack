package com.theflopguyproductions.ticktrack.counter;

public class CounterBackupData {

    int counterValue, counterFlag, counterSignificantCount;
    boolean counterSignificantExist, counterSwipeMode;
    String counterLabel, counterID;
    long counterTimestamp;

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

    public boolean isCounterSwipeMode() {
        return counterSwipeMode;
    }

    public void setCounterSwipeMode(boolean counterSwipeMode) {
        this.counterSwipeMode = counterSwipeMode;
    }

    public String getCounterLabel() {
        return counterLabel;
    }

    public void setCounterLabel(String counterLabel) {
        this.counterLabel = counterLabel;
    }

    public String getCounterID() {
        return counterID;
    }

    public void setCounterID(String counterID) {
        this.counterID = counterID;
    }

    public long getCounterTimestamp() {
        return counterTimestamp;
    }

    public void setCounterTimestamp(long counterTimestamp) {
        this.counterTimestamp = counterTimestamp;
    }
}
