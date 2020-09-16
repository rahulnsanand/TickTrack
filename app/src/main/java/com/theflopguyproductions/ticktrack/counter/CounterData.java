package com.theflopguyproductions.ticktrack.counter;

public class CounterData implements Comparable<CounterData>{

    int counterFlag, counterSignificantCount;
    long counterValue;
    boolean counterSignificantExist, counterSwipeMode, counterPersistentNotification, isNegativeAllowed;
    String counterLabel, counterID;
    long counterTimestamp;

    public boolean isNegativeAllowed() {
        return isNegativeAllowed;
    }

    public void setNegativeAllowed(boolean negativeAllowed) {
        isNegativeAllowed = negativeAllowed;
    }

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

    public long getCounterValue() {
        return counterValue;
    }

    public void setCounterValue(long counterValue) {
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

    public long getCounterTimestamp() {
        return counterTimestamp;
    }

    public void setCounterTimestamp(long counterTimestamp) {
        this.counterTimestamp = counterTimestamp;
    }

    @Override
    public int compareTo(CounterData counterData) {
        int check = 1;
        if(this.getCounterTimestamp()==counterData.getCounterTimestamp()){
            check=0;
        } else if (this.getCounterTimestamp()<counterData.getCounterTimestamp()){
            check=-1;
        }

        if(check<=0){
            if(check==0){
                return 0;
            } else {
                return 1;
            }
        }
        return -1;
    }

}
