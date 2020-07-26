package com.theflopguyproductions.ticktrack.counter;

import java.sql.Timestamp;

public class CounterData implements Comparable<CounterData>{

    int counterValue, counterFlag;
    String counterLabel;
    Timestamp counterTimestamp;

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
        return 0;
    }
}
