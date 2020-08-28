package com.theflopguyproductions.ticktrack.utils.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.core.os.BuildCompat;

import java.util.ArrayList;

public class TickTrackFirebaseDatabase {

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
    public TickTrackFirebaseDatabase(Context context) {
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

    public void storeCurrentUserEmail(String email){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("currentUserEmail", email);
        editor.apply();
    }
    public String getCurrentUserEmail(){
        return sharedPreferences.getString("currentUserEmail","Add an account");
    }

    public void storeLastBackupSystemTime(long timestamp){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("lastBackupTime", timestamp);
        editor.apply();
    }
    public long getLastBackupSystemTime(){
        return sharedPreferences.getLong("lastBackupTime",-1);
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
    public void setPreferencesDataBackup(boolean id){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("preferencesDataBackup", id);
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

    public void setRestoreMode(boolean value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("restoreMode", value);
        editor.apply();
    }
    public boolean isRestoreMode(){
        return sharedPreferences.getBoolean("restoreMode",true);
    }

    public void setRestoreInitMode(boolean value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("restoreInitMode", value);
        editor.apply();
    }
    public boolean isRestoreInitMode(){
        return sharedPreferences.getBoolean("restoreInitMode",true);
    }

    public void foundCounterDataBackup(boolean id){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("foundCounterDataBackup", id);
        editor.apply();
    }
    public boolean hasCounterDataBackup(){
        return sharedPreferences.getBoolean("foundCounterDataBackup",false);
    }
    public void foundTimerDataBackup(boolean id){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("foundTimerDataBackup", id);
        editor.apply();
    }
    public boolean hasTimerDataBackup(){
        return sharedPreferences.getBoolean("foundTimerDataBackup",false);
    }
    public void foundPreferencesDataBackup(boolean id){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("foundPreferencesDataBackup", id);
        editor.apply();
    }
    public boolean hasPreferencesDataBackup(){
        return sharedPreferences.getBoolean("foundPreferencesDataBackup",false);
    }

    public void completeCounterDataRestore(boolean id){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("completeCounterDataRestore", id);
        editor.apply();
    }
    public boolean isCounterDataRestoreComplete(){
        return sharedPreferences.getBoolean("completeCounterDataRestore",false);
    }
    public void completeTimerDataRestore(boolean id){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("completeTimerDataRestore", id);
        editor.apply();
    }
    public boolean isTimerDataRestoreComplete(){
        return sharedPreferences.getBoolean("completeTimerDataRestore",false);
    }
    public void completePreferencesDataRestore(boolean id){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("completePreferencesDataRestore", id);
        editor.apply();
    }
    public boolean isPreferencesDataRestoreComplete(){
        return sharedPreferences.getBoolean("completePreferencesDataRestore",false);
    }

    public void storeRetrievedTimerCount(int id){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("retrievedTimerCount", id);
        editor.apply();
    }
    public int getRetrievedTimerCount(){
        return sharedPreferences.getInt("retrievedTimerCount",-1);
    }
    public void storeRetrievedCounterCount(int id){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("retrievedCounterCount", id);
        editor.apply();
    }
    public int getRetrievedCounterCount(){
        return sharedPreferences.getInt("retrievedCounterCount",-1);
    }

}
