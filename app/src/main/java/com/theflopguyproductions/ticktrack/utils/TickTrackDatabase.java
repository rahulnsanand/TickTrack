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

    public static void storeTimerList(ArrayList<TimerData> counterDataArrayList, Activity activity){

        SharedPreferences sharedPreferences = activity.getSharedPreferences("TickTrackData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(counterDataArrayList);
        editor.putString("TimerData", json);
        editor.apply();

    }

    public static ArrayList<TimerData> retrieveTimerList(Activity activity){

        SharedPreferences sharedPreferences = activity.getSharedPreferences("TickTrackData", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("TimerData", null);
        Type type = new TypeToken<ArrayList<TimerData>>() {}.getType();
        ArrayList<TimerData> counterDataArrayList = gson.fromJson(json, type);

        if(counterDataArrayList == null){
            counterDataArrayList = new ArrayList<>();
        }

        return counterDataArrayList;
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

}
