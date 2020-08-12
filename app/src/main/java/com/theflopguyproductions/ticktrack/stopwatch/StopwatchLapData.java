package com.theflopguyproductions.ticktrack.stopwatch;

import com.theflopguyproductions.ticktrack.timer.TimerData;

public class StopwatchLapData implements Comparable<StopwatchLapData>{

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

    @Override
    public int compareTo(StopwatchLapData stopwatchLapData) {
        return 0;
    }
}
