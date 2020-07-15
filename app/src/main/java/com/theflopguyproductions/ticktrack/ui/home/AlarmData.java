package com.theflopguyproductions.ticktrack.ui.home;

public class AlarmData implements Comparable<AlarmData> {


    String alarmTime;
    String alarmLabel;
    boolean alarmMode;
    String alarmNextOccurrence;
    String alarmAmPm;
    int AlarmColor;

    public int getAlarmColor() {
        return AlarmColor;
    }

    public void setAlarmColor(int alarmColor) {
        AlarmColor = alarmColor;
    }

    public String getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(String alarmTime) {
        this.alarmTime = alarmTime;
    }

    public String getAlarmLabel() {
        return alarmLabel;
    }

    public void setAlarmLabel(String alarmLabel) {
        this.alarmLabel = alarmLabel;
    }

    public boolean isAlarmMode() {
        return alarmMode;
    }

    public void setAlarmMode(boolean alarmMode) {
        this.alarmMode = alarmMode;
    }

    public String getAlarmNextOccurrence() {
        return alarmNextOccurrence;
    }

    public void setAlarmNextOccurrence(String alarmNextOccurrence) {
        this.alarmNextOccurrence = alarmNextOccurrence;
    }

    public String getAlarmAmPm() {
        return alarmAmPm;
    }

    public void setAlarmAmPm(String alarmAmPm) {
        this.alarmAmPm = alarmAmPm;
    }

    @Override
    public int compareTo(AlarmData alarmData) {
        return 0;
    }
}
