package com.theflopguyproductions.ticktrack.utils;

public class StopTimerPoJo {
    private long startTime = 0;
    private boolean running = false;
    private long currentTime = 0;

    public void start() {
        this.startTime = System.currentTimeMillis();
        this.running = true;
    }

    public void stop() {
        this.running = false;
    }

    public void pause() {
        this.running = false;
        currentTime = System.currentTimeMillis() - startTime;
    }

    public void resume() {
        this.running = true;
        this.startTime = System.currentTimeMillis() - currentTime;
    }

    //elaspsed time in milliseconds
    public long getElapsedTimeMili() {
        long elapsed = 0;
        if (running) {
            elapsed = ((System.currentTimeMillis() - startTime) / 100) % 1000;
        }
        return elapsed;
    }

    //elaspsed time in seconds
    public long getElapsedTimeSecs() {
        long elapsed = 0;
        if (running) {
            elapsed = ((System.currentTimeMillis() - startTime) / 1000) % 60;
        }
        return elapsed;
    }
}