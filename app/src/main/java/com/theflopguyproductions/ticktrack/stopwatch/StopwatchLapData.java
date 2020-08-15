package com.theflopguyproductions.ticktrack.stopwatch;


public class StopwatchLapData implements Comparable<StopwatchLapData>{

    int lapNumber;
    long lapTimeInMillis;
    long elapsedTimeInMillis;


    public long getElapsedTimeInMillis() {
        return elapsedTimeInMillis;
    }

    public void setElapsedTimeInMillis(long elapsedTimeInMillis) {
        this.elapsedTimeInMillis = elapsedTimeInMillis;
    }

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
        if(this.lapNumber < stopwatchLapData.getLapNumber()){
            return 1;
        } else {
            return 0;
        }
    }
}
