package com.theflopguyproductions.ticktrack.stopwatch;

public class StopwatchLapData {

    int lapNumber;
    long lapTimeInMillis;

    public int getLapNumber() {
        return lapNumber;
    }

    public void setLapNumber(int lapNumber) {
        this.lapNumber = lapNumber;
    }

    public long getLapTimeInMillis() {
        return lapTimeInMillis;
    }

    public void setLapTimeInMillis(long lapTimeInMillis) {
        this.lapTimeInMillis = lapTimeInMillis;
    }
}
