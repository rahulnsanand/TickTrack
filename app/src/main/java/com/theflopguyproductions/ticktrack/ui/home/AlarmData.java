package com.theflopguyproductions.ticktrack.ui.home;

import android.net.Uri;

import java.util.ArrayList;
import java.util.Calendar;

public class AlarmData implements Comparable<AlarmData> {

    int alarmHour;
    int alarmMinute;
    int alarmTheme;
    ArrayList<Calendar> calendarRepeatDays;
    ArrayList<Calendar> calendarRepeatWeeks;
    Uri alarmRingTone;

    String alarmLabel;
    boolean alarmVibrate;

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

    public ArrayList<Calendar> getCalendarRepeatDays() {
        return calendarRepeatDays;
    }

    public void setCalendarRepeatDays(ArrayList<Calendar> calendarRepeatDays) {
        this.calendarRepeatDays = calendarRepeatDays;
    }

    public ArrayList<Calendar> getCalendarRepeatWeeks() {
        return calendarRepeatWeeks;
    }

    public void setCalendarRepeatWeeks(ArrayList<Calendar> calendarRepeatWeeks) {
        this.calendarRepeatWeeks = calendarRepeatWeeks;
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
