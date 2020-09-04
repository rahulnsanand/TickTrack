package com.theflopguyproductions.ticktrack.timer.data;

public class GeneralTimerItem extends TimerItem {

    private TimerData timerData;

    public TimerData getTimerData() {
        return timerData;
    }

    public void setTimerData(TimerData timerData) {
        this.timerData = timerData;
    }

    @Override
    public int getType() {
        return TYPE_GENERAL;
    }


}
