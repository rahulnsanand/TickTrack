package com.theflopguyproductions.ticktrack.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.theflopguyproductions.ticktrack.ui.home.AlarmData;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;

import static android.content.Context.MODE_PRIVATE;

public class TickTrackAlarmManager {

    private static ArrayList<AlarmData> alarmDataArrayList;

    private static void loadAlarmData(Context context){

        SharedPreferences sharedPreferences = context.getSharedPreferences("TickTrackData", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("AlarmData", null);
        Type type = new TypeToken<ArrayList<AlarmData>>() {}.getType();
        alarmDataArrayList = gson.fromJson(json, type);

        if(alarmDataArrayList == null){
            alarmDataArrayList = new ArrayList<>();
        }

    }

    public static void setAlarm(int position, Context context){

        loadAlarmData(context);

        if (alarmDataArrayList.get(position).isAlarmOnOff()) {

            int hour, minute;
            hour = alarmDataArrayList.get(position).getAlarmHour();
            minute = alarmDataArrayList.get(position).getAlarmMinute();
            ArrayList<Calendar> customDateList = alarmDataArrayList.get(position).getRepeatCustomDates();
            ArrayList<Integer> weeklyRepeat = alarmDataArrayList.get(position).getRepeatDaysInWeek();

            System.out.println("========================================================================================");

            if(customDateList.size()>0 && !(weeklyRepeat.size() >0)){
                for (int j = 0; j < customDateList.size(); j++) {

                    AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.DAY_OF_MONTH, customDateList.get(j).get(Calendar.DAY_OF_MONTH));
                    cal.set(Calendar.MONTH, customDateList.get(j).get(Calendar.MONTH));
                    cal.set(Calendar.YEAR, customDateList.get(j).get(Calendar.YEAR));
                    cal.set(Calendar.HOUR_OF_DAY, hour);
                    cal.set(Calendar.MINUTE, minute);
                    System.out.println(">>>>>>>>>>  Date    "+cal.get(Calendar.DATE));
                    System.out.println(">>>>>>>>>>  Day     "+cal.get(Calendar.DAY_OF_MONTH));
                    System.out.println(">>>>>>>>>>  Month   "+cal.get(Calendar.MONTH));
                    System.out.println(">>>>>>>>>>  Year    "+cal.get(Calendar.YEAR));
                    System.out.println(">>>>>>>>>>  Hour    "+cal.get(Calendar.HOUR_OF_DAY));
                    System.out.println(">>>>>>>>>>  Minute  "+cal.get(Calendar.MINUTE));

                }
            }
            else if(weeklyRepeat.size()>0 && !(customDateList.size() >0)){
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.DATE,nextOccurrenceText(hour, minute, weeklyRepeat));
                cal.set(Calendar.HOUR_OF_DAY, hour);
                cal.set(Calendar.MINUTE, minute);

                System.out.println(">>>>>>>>>>  Date    "+cal.get(Calendar.DATE));
                System.out.println(">>>>>>>>>>  Month   "+cal.get(Calendar.MONTH));
                System.out.println(">>>>>>>>>>  Year    "+cal.get(Calendar.YEAR));
                System.out.println(">>>>>>>>>>  Hour    "+cal.get(Calendar.HOUR_OF_DAY));
                System.out.println(">>>>>>>>>>  Minute  "+cal.get(Calendar.MINUTE));
            }
            else{
                if(isToday(hour,minute)){
                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.DATE,getToday());
                    cal.set(Calendar.HOUR_OF_DAY, hour);
                    cal.set(Calendar.MINUTE, minute);

                    System.out.println(">>>>>>>>>>  Date    "+cal.get(Calendar.DATE));
                    System.out.println(">>>>>>>>>>  Month   "+cal.get(Calendar.MONTH));
                    System.out.println(">>>>>>>>>>  Year    "+cal.get(Calendar.YEAR));
                    System.out.println(">>>>>>>>>>  Hour    "+cal.get(Calendar.HOUR_OF_DAY));
                    System.out.println(">>>>>>>>>>  Minute  "+cal.get(Calendar.MINUTE));
                } else{
                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.DATE,getTomorrow());
                    cal.set(Calendar.HOUR_OF_DAY, hour);
                    cal.set(Calendar.MINUTE, minute);

                    System.out.println(">>>>>>>>>>  Date    "+cal.get(Calendar.DATE));
                    System.out.println(">>>>>>>>>>  Month   "+cal.get(Calendar.MONTH));
                    System.out.println(">>>>>>>>>>  Year    "+cal.get(Calendar.YEAR));
                    System.out.println(">>>>>>>>>>  Hour    "+cal.get(Calendar.HOUR_OF_DAY));
                    System.out.println(">>>>>>>>>>  Minute  "+cal.get(Calendar.MINUTE));
                }
            }

            System.out.println("========================================================================================");
        }

    }
    private static int getToday() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DATE);
    }
    private static int nextOccurrenceText(int setHour, int setMinute, ArrayList<Integer> receivedOnDays){
        Calendar today = Calendar.getInstance();
        int dayNumber = today.get(Calendar.DAY_OF_WEEK);
        Collections.sort(receivedOnDays);

        if(receivedOnDays.size()>0){
            if(receivedOnDays.contains(dayNumber)){
                if(isToday(setHour, setMinute)){
                    return today.get(Calendar.DATE);
                } else{
                    if(dayNumber==Collections.max(receivedOnDays)){
                        today.set(Calendar.DAY_OF_WEEK, Collections.min(receivedOnDays));
                        return today.get(Calendar.DATE)+7;
                    } else{
                        today.set(Calendar.DAY_OF_WEEK, nextOccurrenceRight(receivedOnDays,dayNumber));
                        return today.get(Calendar.DATE);
                    }
                }
            } else{
                if(dayNumber>Collections.max(receivedOnDays)){
                    today.set(Calendar.DAY_OF_WEEK, Collections.min(receivedOnDays));
                    return today.get(Calendar.DATE);
                } else if(dayNumber<Collections.max(receivedOnDays) && dayNumber>Collections.min(receivedOnDays)){
                    today.set(Calendar.DAY_OF_WEEK, nextOccurrenceRight(receivedOnDays,dayNumber));
                    return today.get(Calendar.DATE);
                } else if(dayNumber<Collections.min(receivedOnDays)){
                    today.set(Calendar.DAY_OF_WEEK, Collections.min(receivedOnDays));
                    return today.get(Calendar.DATE);
                }
            }
        } else{
            if(isToday(setHour, setMinute)){
                return today.get(Calendar.DATE);
            }
        }
        return today.get(Calendar.DATE)+1;
    }
    private static boolean isToday(int hour, int minute){
        Date date = new Date();
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        int time24 = calendar.get(Calendar.HOUR_OF_DAY);
        int timeMinute = calendar.get(Calendar.MINUTE);

        if(time24<hour){
            return true;
        }
        else if(time24==hour){
            return timeMinute < minute;
        }
        return false;
    }
    private static int getTomorrow(){
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DATE)+1;
    }
    private static int nextOccurrenceRight(ArrayList<Integer> receivedDays, int today){
        int nextOccurrenceValue=0;
        int checkCount = 0;

        if(receivedDays!=null){
            for(int i = 0; i < receivedDays.size(); i++){
                while(today<receivedDays.get(i) && checkCount==0){
                    nextOccurrenceValue=receivedDays.get(i);
                    checkCount++;
                }
            }
        }
        return nextOccurrenceValue;
    }

    public static void cancelAlarm(int position, Context context){
        loadAlarmData(context);


        if (!alarmDataArrayList.get(position).isAlarmOnOff()) {

            int hour, minute;
            hour = alarmDataArrayList.get(position).getAlarmHour();
            minute = alarmDataArrayList.get(position).getAlarmMinute();
            ArrayList<Calendar> customDateList = alarmDataArrayList.get(position).getRepeatCustomDates();
            ArrayList<Integer> weeklyRepeat = alarmDataArrayList.get(position).getRepeatDaysInWeek();

            System.out.println("==========================CANCEL=======================================");

            if(customDateList.size()>0 && !(weeklyRepeat.size() >0)){
                for (int j = 0; j < customDateList.size(); j++) {

                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.DAY_OF_MONTH, customDateList.get(j).get(Calendar.DAY_OF_MONTH));
                    cal.set(Calendar.MONTH, customDateList.get(j).get(Calendar.MONTH));
                    cal.set(Calendar.YEAR, customDateList.get(j).get(Calendar.YEAR));
                    cal.set(Calendar.HOUR_OF_DAY, hour);
                    cal.set(Calendar.MINUTE, minute);
                    System.out.println(">>>>>>>>>>  Date    "+cal.get(Calendar.DATE));
                    System.out.println(">>>>>>>>>>  Day     "+cal.get(Calendar.DAY_OF_MONTH));
                    System.out.println(">>>>>>>>>>  Month   "+cal.get(Calendar.MONTH));
                    System.out.println(">>>>>>>>>>  Year    "+cal.get(Calendar.YEAR));
                    System.out.println(">>>>>>>>>>  Hour    "+cal.get(Calendar.HOUR_OF_DAY));
                    System.out.println(">>>>>>>>>>  Minute  "+cal.get(Calendar.MINUTE));

                }
            }
            else if(weeklyRepeat.size()>0 && !(customDateList.size() >0)){
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.DATE,nextOccurrenceText(hour, minute, weeklyRepeat));
                cal.set(Calendar.HOUR_OF_DAY, hour);
                cal.set(Calendar.MINUTE, minute);

                System.out.println(">>>>>>>>>>  Date    "+cal.get(Calendar.DATE));
                System.out.println(">>>>>>>>>>  Month   "+cal.get(Calendar.MONTH));
                System.out.println(">>>>>>>>>>  Year    "+cal.get(Calendar.YEAR));
                System.out.println(">>>>>>>>>>  Hour    "+cal.get(Calendar.HOUR_OF_DAY));
                System.out.println(">>>>>>>>>>  Minute  "+cal.get(Calendar.MINUTE));
            }
            else{
                if(isToday(hour,minute)){
                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.DATE,getToday());
                    cal.set(Calendar.HOUR_OF_DAY, hour);
                    cal.set(Calendar.MINUTE, minute);

                    System.out.println(">>>>>>>>>>  Date    "+cal.get(Calendar.DATE));
                    System.out.println(">>>>>>>>>>  Month   "+cal.get(Calendar.MONTH));
                    System.out.println(">>>>>>>>>>  Year    "+cal.get(Calendar.YEAR));
                    System.out.println(">>>>>>>>>>  Hour    "+cal.get(Calendar.HOUR_OF_DAY));
                    System.out.println(">>>>>>>>>>  Minute  "+cal.get(Calendar.MINUTE));
                } else{
                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.DATE,getTomorrow());
                    cal.set(Calendar.HOUR_OF_DAY, hour);
                    cal.set(Calendar.MINUTE, minute);

                    System.out.println(">>>>>>>>>>  Date    "+cal.get(Calendar.DATE));
                    System.out.println(">>>>>>>>>>  Month   "+cal.get(Calendar.MONTH));
                    System.out.println(">>>>>>>>>>  Year    "+cal.get(Calendar.YEAR));
                    System.out.println(">>>>>>>>>>  Hour    "+cal.get(Calendar.HOUR_OF_DAY));
                    System.out.println(">>>>>>>>>>  Minute  "+cal.get(Calendar.MINUTE));
                }
            }

            System.out.println("========================================================================================");
        }
    }

}
