package com.theflopguyproductions.ticktrack.utils.database;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.core.os.BuildCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.theflopguyproductions.ticktrack.counter.CounterBackupData;
import com.theflopguyproductions.ticktrack.receivers.BackupScheduleReceiver;
import com.theflopguyproductions.ticktrack.settings.SettingsData;
import com.theflopguyproductions.ticktrack.timer.data.TimerBackupData;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class TickTrackFirebaseDatabase {

    private SharedPreferences sharedPreferences;
    private Context context;

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
        this.context = context;
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

    public void setPreferencesDataBackup(boolean id){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("preferencesDataBackup", id);
        editor.apply();
    }

    public void setRestoreInitMode(int value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("restoreInitMode", value);
        editor.apply();
    }
    public int isRestoreInitMode(){
        return sharedPreferences.getInt("restoreInitMode",0);
    }
    public void setRestoreMode(boolean value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("restoreMode", value);
        editor.apply();
    }
    public boolean isRestoreMode(){
        return sharedPreferences.getBoolean("restoreMode",false);
    }

    public void setBackupMode(boolean value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("backupMode", value);
        editor.apply();
    }
    public boolean isBackupMode(){
        return sharedPreferences.getBoolean("backupMode",false);
    }

    public void setCounterDataRestore(boolean id){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("foundCounterDataBackup", id);
        editor.apply();
    }
    public boolean isCounterDataRestored(){
        return sharedPreferences.getBoolean("foundCounterDataBackup",false);
    }
    public void setTimerDataRestore(boolean id){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("foundTimerDataBackup", id);
        editor.apply();
    }
    public boolean isTimerDataRestored(){
        return sharedPreferences.getBoolean("foundTimerDataBackup",false);
    }
    public void setCounterDataRestoreError(boolean id){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("foundCounterDataBackupError", id);
        editor.apply();
    }
    public boolean isCounterDataRestoreError(){
        return sharedPreferences.getBoolean("foundCounterDataBackupError",false);
    }
    public void setTimerDataBackupError(boolean id){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("foundTimerDataBackupError", id);
        editor.apply();
    }
    public boolean isTimerDataRestoreError(){
        return sharedPreferences.getBoolean("foundTimerDataBackupError",false);
    }

    public void foundPreferencesDataBackup(boolean id){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("foundPreferencesDataBackup", id);
        editor.apply();
    }

    public boolean hasPreferencesDataBackup(){
        return sharedPreferences.getBoolean("foundPreferencesDataBackup",false);
    }

    public void setRestoreThemeMode(int mode){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("restoreThemeMode", mode);
        editor.apply();
    }
    public int getRestoreThemeMode(){
        return sharedPreferences.getInt("restoreThemeMode",-1);
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
    public void storeRetrievedLastBackupTime(long timeStamp){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("retrievedLastBackupTime", timeStamp);
        editor.apply();
    }
    public long getRetrievedLastBackupTime(){
        return sharedPreferences.getLong("retrievedLastBackupTime", -1);
    }
    public void storeSettingsRestoredData(ArrayList<SettingsData> settingsData){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(settingsData);
        editor.putString("settingsRestoreData", json);
        editor.apply();
    }
    public ArrayList<SettingsData> retrieveSettingsRestoredData(){
        Gson gson = new Gson();
        String json = sharedPreferences.getString("settingsRestoreData", null);
        Type type = new TypeToken<ArrayList<SettingsData>>() {}.getType();
        ArrayList<SettingsData> settingsData = gson.fromJson(json, type);
        if(settingsData == null){
            settingsData = new ArrayList<>();
        }
        return settingsData;
    }

    public void setRestoreCompleteStatus(int mode){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("RestoreCompleteStatus", mode);
        editor.apply();
    }
    public int getRestoreCompleteStatus(){
        return sharedPreferences.getInt("RestoreCompleteStatus",0);
    }

    public void setCounterDownloadStatus(int mode){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("CounterDownloadStatus", mode);
        editor.apply();
    }
    public int getCounterDownloadStatus(){
        return sharedPreferences.getInt("CounterDownloadStatus",0);
    }
    public void setTimerDownloadStatus(int mode){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("TimerDownloadStatus", mode);
        editor.apply();
    }
    public int getTimerDownloadStatus(){
        return sharedPreferences.getInt("TimerDownloadStatus",0);
    }

    public void storeBackupTimerList(ArrayList<TimerBackupData> timerDataArrayList){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(timerDataArrayList);
        editor.putString("TimerBackupData", json);
        editor.apply();
    }
    public ArrayList<TimerBackupData> retrieveBackupTimerList(){
        Gson gson = new Gson();
        String json = sharedPreferences.getString("TimerBackupData", null);
        Type type = new TypeToken<ArrayList<TimerBackupData>>() {}.getType();
        ArrayList<TimerBackupData> timerDataArrayList = gson.fromJson(json, type);
        if(timerDataArrayList == null){
            timerDataArrayList = new ArrayList<>();
        }
        return timerDataArrayList;
    }
    public void storeBackupCounterList(ArrayList<CounterBackupData> counterBackupData){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(counterBackupData);
        editor.putString("CounterBackupData", json);
        editor.apply();
    }
    public ArrayList<CounterBackupData> retrieveBackupCounterList(){
        Gson gson = new Gson();
        String json = sharedPreferences.getString("TimerBackupData", null);
        Type type = new TypeToken<ArrayList<CounterBackupData>>() {}.getType();
        ArrayList<CounterBackupData> counterBackupData = gson.fromJson(json, type);
        if(counterBackupData == null){
            counterBackupData = new ArrayList<>();
        }
        return counterBackupData;
    }

    public void storeCounterRestoreString(String counterData){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("counterRestoreString", counterData);
        editor.apply();
    }
    public String getCounterRestoreString(){
        return sharedPreferences.getString("counterRestoreString", null);
    }
    public void storeTimerRestoreString(String timerData){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("timerRestoreString", timerData);
        editor.apply();
    }
    public String getTimerRestoreString(){
        return sharedPreferences.getString("timerRestoreString", null);
    }

    public void setCounterBackupComplete(boolean value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("counterBackupComplete", value);
        editor.apply();
    }
    public boolean isCounterBackupComplete(){
        return sharedPreferences.getBoolean("counterBackupComplete", false);
    }
    public void setTimerBackupComplete(boolean value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("timerBackupComplete", value);
        editor.apply();
    }
    public boolean isTimerBackupComplete(){
        return sharedPreferences.getBoolean("timerBackupComplete", false);
    }
    public void setSettingsBackupComplete(boolean value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("settingsBackupComplete", value);
        editor.apply();
    }
    public boolean isSettingsBackupComplete(){
        return sharedPreferences.getBoolean("settingsBackupComplete", false);
    }

    public void setBackUpAlarm(boolean isNowBackup){

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        Random r = new Random();
        int hourRandom = r.nextInt(24);
        int minuteRandom = r.nextInt(59);
        new TickTrackDatabase(context).storeBackupHour(hourRandom);
        new TickTrackDatabase(context).storeBackupMinute(minuteRandom);
        calendar.set(Calendar.HOUR_OF_DAY, hourRandom);
        calendar.set(Calendar.MINUTE, minuteRandom);
        calendar.set(Calendar.SECOND, 0);

        int intervalRange = new TickTrackDatabase(context).getSyncFrequency();
        long intervalTimeInMillis;
        if(intervalRange== SettingsData.Frequency.DAILY.getCode()){
            if(Calendar.getInstance().after(calendar)){
                calendar.add(Calendar.DATE, 1);
            }
            intervalTimeInMillis = 24*60*60*1000L;
        } else if(intervalRange== SettingsData.Frequency.MONTHLY.getCode()){
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            if(Calendar.getInstance().after(calendar)){
                calendar.add(Calendar.MONTH, 1);
            }
            intervalTimeInMillis = 30*24*60*60*1000L;
        } else if(intervalRange== SettingsData.Frequency.WEEKLY.getCode()){
            calendar.set(Calendar.DAY_OF_WEEK, 1);
            if(Calendar.getInstance().after(calendar)){
                calendar.add(Calendar.WEEK_OF_YEAR, 1);
            }
            intervalTimeInMillis = 7*24*60*60*1000L;
        } else {
            if(Calendar.getInstance().after(calendar)){
                calendar.add(Calendar.DATE, 1);
            }
            intervalTimeInMillis = 24*60*60*1000L;
        }
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(BackupScheduleReceiver.START_BACKUP_SCHEDULE);
        intent.setClassName("com.theflopguyproductions.ticktrack", "com.theflopguyproductions.ticktrack.receivers.BackupScheduleReceiver");
        intent.setPackage("com.theflopguyproductions.ticktrack");
//        intent.setClass(context, BackupScheduleReceiver.class);
//        intent.setAction(BackupScheduleReceiver.START_BACKUP_SCHEDULE);
//        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, 212206, intent, 0);
        alarmManager.setRepeating(
                AlarmManager.RTC,
//                calendar.getTimeInMillis(),
//                intervalTimeInMillis,
                System.currentTimeMillis()+6000,
                60*1000,
                alarmPendingIntent
        );

        if(isNowBackup){
            Intent intentNow = new Intent(BackupScheduleReceiver.START_BACKUP_SCHEDULE);
            intentNow.setClassName("com.theflopguyproductions.ticktrack", "com.theflopguyproductions.ticktrack.receivers.BackupScheduleReceiver");
            intentNow.setPackage("com.theflopguyproductions.ticktrack");
            context.sendBroadcast(intentNow);
        }
    }
    public void cancelBackUpAlarm(){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, BackupScheduleReceiver.class);
        intent.setAction(BackupScheduleReceiver.START_BACKUP_SCHEDULE);
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, 212206, intent, 0);
        alarmManager.cancel(alarmPendingIntent);
    }

    public void setDriveLinkFail(boolean id){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("setDriveLinkFail", id);
        editor.apply();
    }
    public boolean isDriveLinkFail(){
        return sharedPreferences.getBoolean("setDriveLinkFail",false);
    }

}
