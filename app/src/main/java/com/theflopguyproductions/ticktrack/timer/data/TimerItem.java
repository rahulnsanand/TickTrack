package com.theflopguyproductions.ticktrack.timer.data;

public abstract class TimerItem {
    public static final int TYPE_GENERAL = 0;
    public static final int TYPE_QUICK_TIMER = 1;

    abstract public int getType();
}
