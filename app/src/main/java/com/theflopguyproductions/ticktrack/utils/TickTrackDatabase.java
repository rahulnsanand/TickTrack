package com.theflopguyproductions.ticktrack.utils;

import android.app.Activity;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.theflopguyproductions.ticktrack.counter.CounterData;
import com.theflopguyproductions.ticktrack.timer.TimerData;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class TickTrackDatabase {

    public static void storeCounterNumber(Activity activity, int updateNumber){
        SharedPreferences sharedPreferences = activity.getSharedPreferences("TickTrackData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("CounterNumber", updateNumber);
        editor.apply();
    }

    public static int retrieveCounterNumber(Activity activity){
        SharedPreferences sharedPreferences = activity.getSharedPreferences("TickTrackData", MODE_PRIVATE);
        return sharedPreferences.getInt("CounterNumber", 1);
    }

    public static void storeCounterList(ArrayList<CounterData> counterDataArrayList, Activity activity){

        SharedPreferences sharedPreferences = activity.getSharedPreferences("TickTrackData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(counterDataArrayList);
        editor.putString("CounterData", json);
        editor.apply();

    }

    public static ArrayList<CounterData> retrieveCounterList(Activity activity){

        SharedPreferences sharedPreferences = activity.getSharedPreferences("TickTrackData", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("CounterData", null);
        Type type = new TypeToken<ArrayList<CounterData>>() {}.getType();
        ArrayList<CounterData> counterDataArrayList = gson.fromJson(json, type);

        if(counterDataArrayList == null){
            counterDataArrayList = new ArrayList<>();
        }

        return counterDataArrayList;
    }

    public static void setFirstTimer(Activity activity, boolean status){
        SharedPreferences sharedPreferences = activity.getSharedPreferences("TickTrackData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isFirstTimer", status);
        editor.apply();
    }

    public static boolean isFirstTimer(Activity activity){
        SharedPreferences sharedPreferences = activity.getSharedPreferences("TickTrackData", MODE_PRIVATE);
        return sharedPreferences.getBoolean("isFirstTimer", true);
    }

    public static void storeTimerList(ArrayList<TimerData> timerDataArrayList, Activity activity){

        SharedPreferences sharedPreferences = activity.getSharedPreferences("TickTrackData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(timerDataArrayList);
        editor.putString("TimerData", json);
        editor.apply();

    }

    public static ArrayList<TimerData> retrieveTimerList(Activity activity){

        SharedPreferences sharedPreferences = activity.getSharedPreferences("TickTrackData", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("TimerData", null);
        Type type = new TypeToken<ArrayList<TimerData>>() {}.getType();
        ArrayList<TimerData> timerDataArrayList = gson.fromJson(json, type);

        if(timerDataArrayList == null){
            timerDataArrayList = new ArrayList<>();
        }

        return timerDataArrayList;

    }

    public static int getThemeMode(Activity activity){
        SharedPreferences sharedPreferences = activity.getSharedPreferences("TickTrackData", MODE_PRIVATE);
        return sharedPreferences.getInt("ThemeMode", 1);
    }

    public static void setThemeMode(Activity activity, int mode){
        SharedPreferences sharedPreferences = activity.getSharedPreferences("TickTrackData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("ThemeMode", mode);
        editor.apply();
    }

    public static String getCurrentCounterNotificationID(Activity activity){
        SharedPreferences sharedPreferences = activity.getSharedPreferences("TickTrackData", MODE_PRIVATE);
        return sharedPreferences.getString("CounterNotificationID", null);
    }

    public static void setCurrentCounterNotificationID(Activity activity, String currentCounterID){
        SharedPreferences sharedPreferences = activity.getSharedPreferences("TickTrackData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("CounterNotificationID", currentCounterID);
        editor.apply();
    }

}
