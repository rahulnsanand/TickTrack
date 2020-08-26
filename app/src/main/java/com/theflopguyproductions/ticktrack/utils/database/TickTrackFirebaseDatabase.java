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


}
