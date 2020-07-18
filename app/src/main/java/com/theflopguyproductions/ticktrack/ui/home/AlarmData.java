package com.theflopguyproductions.ticktrack.ui.home;

import android.net.Uri;

import java.util.ArrayList;
import java.util.Date;

public class AlarmData implements Comparable<AlarmData> {

    int alarmHour;
    int alarmMinute;
    int alarmTheme;
    ArrayList<Date> repeatCustomDates;
    ArrayList<Integer> repeatDaysInWeek;
    String alarmRingTone;
    String alarmLabel;
    boolean alarmVibrate;
    boolean alarmOnOff;

    public boolean isAlarmOnOff() {
        return alarmOnOff;
    }

    public void setAlarmOnOff(boolean alarmOnOff) {
        this.alarmOnOff = alarmOnOff;
    }

    public int getAlarmHour() {
        return alarmHour;
    }

    public void setAlarmHour(int alarmHour) {
        this.alarmHour = alarmHour;
    }

    public int getAlarmMinute() {
        return alarmMinute;
    }

    public void setAlarmMinute(int alarmMinute) {
        this.alarmMinute = alarmMinute;
    }

    public int getAlarmTheme() {
        return alarmTheme;
    }

    public void setAlarmTheme(int alarmTheme) {
        this.alarmTheme = alarmTheme;
    }

    public ArrayList<Date> getRepeatCustomDates() {
        return repeatCustomDates;
    }

    public void setRepeatCustomDates(ArrayList<Date> repeatCustomDates) {
        this.repeatCustomDates = repeatCustomDates;
    }

    public ArrayList<Integer> getRepeatDaysInWeek() {
        return repeatDaysInWeek;
    }

    public void setRepeatDaysInWeek(ArrayList<Integer> repeatDaysInWeek) {
        this.repeatDaysInWeek = repeatDaysInWeek;
    }

    public String getAlarmRingTone() {
        return alarmRingTone;
    }

    public void setAlarmRingTone(String alarmRingTone) {
        this.alarmRingTone = alarmRingTone;
    }

    public String getAlarmLabel() {
        return alarmLabel;
    }

    public void setAlarmLabel(String alarmLabel) {
        this.alarmLabel = alarmLabel;
    }

    public boolean isAlarmVibrate() {
        return alarmVibrate;
    }

    public void setAlarmVibrate(boolean alarmVibrate) {
        this.alarmVibrate = alarmVibrate;
    }

    @Override
    public int compareTo(AlarmData alarmData) {
        return 0;
    }
}
