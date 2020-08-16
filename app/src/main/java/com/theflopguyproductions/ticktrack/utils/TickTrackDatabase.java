package com.theflopguyproductions.ticktrack.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.theflopguyproductions.ticktrack.counter.CounterData;
import com.theflopguyproductions.ticktrack.stopwatch.StopwatchData;
import com.theflopguyproductions.ticktrack.stopwatch.StopwatchLapData;
import com.theflopguyproductions.ticktrack.timer.TimerData;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class TickTrackDatabase {

    private SharedPreferences sharedPreferences;

    public TickTrackDatabase(Context context) {
        sharedPreferences = context.getSharedPreferences("TickTrackData", MODE_PRIVATE);
    }
    public void storeCurrentFragmentNumber(int updateNumber){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("CurrentFragment", updateNumber);
        editor.apply();
    }

    public int retrieveCurrentFragmentNumber() {
        return sharedPreferences.getInt("CurrentFragment", 1);
    }
    public void storeCounterNumber(int updateNumber){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("CounterNumber", updateNumber);
        editor.apply();
    }

    public int retrieveCounterNumber(){
        return sharedPreferences.getInt("CounterNumber", 1);
    }

    public void storeLapNumber(int updateNumber){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("LapNumber", updateNumber);
        editor.apply();
    }

    public int retrieveLapNumber(){
        return sharedPreferences.getInt("LapNumber", 1);
    }

    public void storeCounterList(ArrayList<CounterData> counterDataArrayList){

        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(counterDataArrayList);
        editor.putString("CounterData", json);
        editor.apply();

    }

    public ArrayList<CounterData> retrieveCounterList(){

        Gson gson = new Gson();
        String json = sharedPreferences.getString("CounterData", null);
        Type type = new TypeToken<ArrayList<CounterData>>() {}.getType();
        ArrayList<CounterData> counterDataArrayList = gson.fromJson(json, type);

        if(counterDataArrayList == null){
            counterDataArrayList = new ArrayList<>();
        }

        return counterDataArrayList;
    }

    public void setFirstTimer(boolean status){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isFirstTimer", status);
        editor.apply();
    }

    public boolean isFirstTimer(){
        return sharedPreferences.getBoolean("isFirstTimer", true);
    }

    public void storeTimerList(ArrayList<TimerData> timerDataArrayList){

        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(timerDataArrayList);
        editor.putString("TimerData", json);
        editor.apply();

    }

    public ArrayList<TimerData> retrieveTimerList(){

        Gson gson = new Gson();
        String json = sharedPreferences.getString("TimerData", null);
        Type type = new TypeToken<ArrayList<TimerData>>() {}.getType();
        ArrayList<TimerData> timerDataArrayList = gson.fromJson(json, type);

        if(timerDataArrayList == null){
            timerDataArrayList = new ArrayList<>();
        }

        return timerDataArrayList;

    }

    public int getThemeMode(){
        return sharedPreferences.getInt("ThemeMode", 1);
    }

    public void setThemeMode(int mode){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("ThemeMode", mode);
        editor.apply();
    }

    public String getCurrentCounterNotificationID(){
        return sharedPreferences.getString("CounterNotificationID", null);
    }

    public void setCurrentCounterNotificationID(String currentCounterID){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("CounterNotificationID", currentCounterID);
        editor.apply();
    }

    public ArrayList<StopwatchData> retrieveStopwatchData(){
        Gson gson = new Gson();
        String json = sharedPreferences.getString("StopwatchData", null);
        Type type = new TypeToken<ArrayList<StopwatchData>>() {}.getType();
        ArrayList<StopwatchData> stopwatchData = gson.fromJson(json, type);

        if(stopwatchData == null){
            stopwatchData = new ArrayList<>();
        }

        return stopwatchData;
    }

    public ArrayList<StopwatchLapData> retrieveStopwatchLapData(){
        Gson gson = new Gson();
        String json = sharedPreferences.getString("StopwatchLapData", null);
        Type type = new TypeToken<ArrayList<StopwatchLapData>>() {}.getType();
        ArrayList<StopwatchLapData> stopwatchLapData = gson.fromJson(json, type);

        if(stopwatchLapData == null){
            stopwatchLapData = new ArrayList<>();
        }

        return stopwatchLapData;
    }

    public void storeLapData(ArrayList<StopwatchLapData> stopwatchLapData){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(stopwatchLapData);
        editor.putString("StopwatchLapData", json);
        editor.apply();
    }

    public void storeStopwatchData(ArrayList<StopwatchData> stopwatchData){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(stopwatchData);
        editor.putString("StopwatchData", json);
        editor.apply();
    }


}
