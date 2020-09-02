package com.theflopguyproductions.ticktrack.utils.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.core.os.BuildCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.theflopguyproductions.ticktrack.counter.CounterData;
import com.theflopguyproductions.ticktrack.stopwatch.StopwatchData;
import com.theflopguyproductions.ticktrack.stopwatch.StopwatchLapData;
import com.theflopguyproductions.ticktrack.timer.TimerData;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class TickTrackDatabase {

    private SharedPreferences sharedPreferences;

    public SharedPreferences getSharedPref(Context context){
        Context storageContext;
        if (BuildCompat.isAtLeastN()) {
            final Context deviceContext = context.createDeviceProtectedStorageContext();
            if (!deviceContext.moveSharedPreferencesFrom(context,
                    "TickTrackData")) {
                Log.w("TAG", "Failed to migrate shared preferences.");
            }
            storageContext = deviceContext;
        } else {
            storageContext = context;
        }
        return storageContext
                .getSharedPreferences("TickTrackData", Context.MODE_PRIVATE);
    }

    public TickTrackDatabase(Context context) {
        Context storageContext;
        if (Build.VERSION.SDK_INT >= 24) {
            final Context deviceContext = context.createDeviceProtectedStorageContext();
            if (!deviceContext.moveSharedPreferencesFrom(context,
                    "TickTrackData")) {
                Log.w("TAG", "Failed to migrate shared preferences.");
            }
            storageContext = deviceContext;
        } else {
            storageContext = context;
        }
        sharedPreferences = storageContext
                .getSharedPreferences("TickTrackData", Context.MODE_PRIVATE);

    }

    public void storeFirstLaunch(boolean updateNumber){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("firstLaunch", updateNumber);
        editor.apply();
    }

    public boolean retrieveFirstLaunch() {
        return sharedPreferences.getBoolean("firstLaunch", true);
    }

    public void storeNotOptimiseBool(boolean updateNumber){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isNotOptimised", updateNumber);
        editor.apply();
    }

    public boolean retrieveNotOptimiseBool() {
        return sharedPreferences.getBoolean("isNotOptimised", false);
    }

    public void storeStartUpFragmentID(int updateNumber){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("startupFragmentNumber", updateNumber);
        editor.apply();
    }

    public int retrieveStartUpFragmentID() {
        return sharedPreferences.getInt("startupFragmentNumber", 0);
    }

    public void storeOptimiseRequestNumber(int updateNumber){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("optimiseRequestNumber", updateNumber);
        editor.apply();
    }

    public int retrieveOptimiseRequestNumber() {
        return sharedPreferences.getInt("optimiseRequestNumber", 0);
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

    public void setHapticEnabled(boolean value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isHapticEnabled", value);
        editor.apply();
    }
    public boolean isHapticEnabled(){
        return sharedPreferences.getBoolean("isHapticEnabled",true);
    }

    public void setWifiOnly(boolean value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isWifiOnly", value);
        editor.apply();
    }
    public boolean isWifiOnly(){
        return sharedPreferences.getBoolean("isWifiOnly",false);
    }

    public void storeSyncFrequency(int id){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("syncFrequency", id);
        editor.apply();
    }
    public int getSyncFrequency(){
        return sharedPreferences.getInt("syncFrequency",1);
    }

    public void setCounterDataBackup(boolean id){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("counterDataBackup", id);
        editor.apply();
    }
    public void setTimerDataBackup(boolean id){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("timerDataBackup", id);
        editor.apply();
    }
    public ArrayList<Integer> getBackupDataOptions(){
        ArrayList<Integer> options = new ArrayList<>();
        if(sharedPreferences.getBoolean("preferencesDataBackup", true)){
            options.add(1);
        }
        if(sharedPreferences.getBoolean("timerDataBackup", false)){
            options.add(2);
        }
        if(sharedPreferences.getBoolean("counterDataBackup", true)){
            options.add(3);
        }

        return options;
    }

    public void setLastBackupSystemTime(long timestamp){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("lastBackupTime", timestamp);
        editor.apply();
    }
    public long getLastBackupSystemTime(){
        return sharedPreferences.getLong("lastBackupTime",-1);
    }

}
