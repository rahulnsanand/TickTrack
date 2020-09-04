package com.theflopguyproductions.ticktrack.timer.data;

public class QuickTimerItem extends TimerItem {

    private boolean isQuickTimer;

    public boolean isQuickTimer() {
        return isQuickTimer;
    }

    public void setQuickTimer(boolean isQuickTimer) {
        this.isQuickTimer = isQuickTimer;
    }

    @Override
    public int getType() {
        return TYPE_QUICK_TIMER;
    }
}
