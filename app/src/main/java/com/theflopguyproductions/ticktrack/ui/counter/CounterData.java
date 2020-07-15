package com.theflopguyproductions.ticktrack.ui.counter;

import java.sql.Timestamp;

public class CounterData implements Comparable<CounterData> {
    int countValue;
    String counterLabel;
    int labelColor;
    Timestamp timestamp;

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public int getLabelColor() {
        return labelColor;
    }

    public void setLabelColor(int labelColor) {
        this.labelColor = labelColor;
    }

    public int getCountValue() {
        return countValue;
    }

    public void setCountValue(int countValue) {
        this.countValue = countValue;
    }

    public String getCounterLabel() {
        return counterLabel;
    }

    public void setCounterLabel(String counterLabel) {
        this.counterLabel = counterLabel;
    }

    @Override
    public int compareTo(CounterData counterData) {

        int check = this.getTimestamp().compareTo(counterData.getTimestamp());

        if(check<=0){
            if(check==0){
                return 0;
            }
            else{
                return 1;
            }
        }
        return -1;
    }
}
