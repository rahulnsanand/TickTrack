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
    Uri alarmRingTone;
    String alarmLabel;
    boolean alarmVibrate;

    @Override
    public String toString() {
        return "AlarmData{" +
                "alarmHour=" + alarmHour +
                ", alarmMinute=" + alarmMinute +
                ", alarmTheme=" + alarmTheme +
                ", calendarRepeatDays=" + repeatCustomDates +
                ", calendarRepeatWeeks=" + repeatDaysInWeek +
                ", alarmRingTone=" + alarmRingTone +
                ", alarmLabel='" + alarmLabel + '\'' +
                ", alarmVibrate=" + alarmVibrate +
                '}';
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

    public Uri getAlarmRingTone() {
        return alarmRingTone;
    }

    public void setAlarmRingTone(Uri alarmRingTone) {
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
