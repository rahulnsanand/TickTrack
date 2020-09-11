package com.theflopguyproductions.ticktrack.utils.database;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.core.os.BuildCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.theflopguyproductions.ticktrack.counter.CounterData;
import com.theflopguyproductions.ticktrack.counter.notification.CounterNotificationService;
import com.theflopguyproductions.ticktrack.service.BackupRestoreService;
import com.theflopguyproductions.ticktrack.startup.service.OptimiserService;
import com.theflopguyproductions.ticktrack.stopwatch.StopwatchData;
import com.theflopguyproductions.ticktrack.stopwatch.StopwatchLapData;
import com.theflopguyproductions.ticktrack.timer.data.TimerData;
import com.theflopguyproductions.ticktrack.timer.quick.QuickTimerData;
import com.theflopguyproductions.ticktrack.timer.service.TimerRingService;
import com.theflopguyproductions.ticktrack.timer.service.TimerService;
import com.theflopguyproductions.ticktrack.widgets.clock.ClockData;
import com.theflopguyproductions.ticktrack.widgets.counter.data.CounterWidgetData;

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

    public void setLockedBootComplete(boolean isLocked){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("lockedBootTime", isLocked);
        editor.apply();
    }
    public boolean isLockedBootComplete() {
        return sharedPreferences.getBoolean("lockedBootTime", false);
    }

    public void setNewDevice(boolean updateNumber){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("newDevice", updateNumber);
        editor.apply();
    }

    public boolean isNewDevice() {
        return sharedPreferences.getBoolean("newDevice", true);
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

    public void storeQuickTimerList(ArrayList<QuickTimerData> quickTimerDataArrayList){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(quickTimerDataArrayList);
        editor.putString("QuickTimerData", json);
        editor.apply();
    }
    public ArrayList<QuickTimerData> retrieveQuickTimerList(){
        Gson gson = new Gson();
        String json = sharedPreferences.getString("QuickTimerData", null);
        Type type = new TypeToken<ArrayList<QuickTimerData>>() {}.getType();
        ArrayList<QuickTimerData> quickTimerDataArrayList = gson.fromJson(json, type);
        if(quickTimerDataArrayList == null){
            quickTimerDataArrayList = new ArrayList<>();
        }
        return quickTimerDataArrayList;
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

    public void resetData(Context context) {
        ArrayList<TimerData> timerData = retrieveTimerList();
        TickTrackTimerDatabase tickTrackTimerDatabase = new TickTrackTimerDatabase(context);
        for(int i = 0; i < timerData.size(); i++){
            tickTrackTimerDatabase.cancelAlarm(timerData.get(i).getTimerIntID(), false);
        }

        ArrayList<QuickTimerData> quickTimerData = retrieveQuickTimerList();
        for(int i = 0; i < quickTimerData.size(); i++){
            tickTrackTimerDatabase.cancelAlarm(quickTimerData.get(i).getTimerIntID(), true);
        }

        if(isMyServiceRunning(BackupRestoreService.class, context)){
            stopService(BackupRestoreService.class, context);
        }
        if(isMyServiceRunning(OptimiserService.class, context)){
            stopService(OptimiserService.class, context);
        }
        if(isMyServiceRunning(TimerService.class, context)){
            stopService(TimerService.class, context);
        }
        if(isMyServiceRunning(TimerRingService.class, context)){
            stopService(TimerRingService.class, context);
        }
        if(isMyServiceRunning(CounterNotificationService.class, context)){
            stopService(CounterNotificationService.class, context);
        }

        storeTimerList(new ArrayList<>());
        storeCounterList(new ArrayList<>());
        storeCounterNumber(1);
        storeSyncFrequency(1);
        setHapticEnabled(true);
        setLastBackupSystemTime(-1);
        setThemeMode(1);
        setTimerDataBackup(true);
        setCounterDataBackup(true);

//        try {
//            Thread.sleep(500);
//            context.startActivity(new Intent(context, SettingsActivity.class));
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

    }

    private void stopService(Class<?> service, Context context) {
        Intent intent = new Intent(context, service);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if(service==BackupRestoreService.class){
            intent.setAction(BackupRestoreService.RESTORE_SERVICE_STOP_FOREGROUND);
        } else if(service== OptimiserService.class){
            intent.setAction(OptimiserService.ACTION_BATTERY_OPTIMISE_CHECK_STOP);
        } else if(service== TimerService.class){
            intent.setAction(TimerService.ACTION_STOP_TIMER_SERVICE);
        } else if(service== TimerRingService.class){
            intent.setAction(TimerRingService.ACTION_KILL_ALL_TIMERS);
        } else if(service== CounterNotificationService.class){
            intent.setAction(CounterNotificationService.ACTION_KILL_NOTIFICATIONS);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    public boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void storeCounterWidgetList(ArrayList<CounterWidgetData> counterWidgetData){

        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(counterWidgetData);
        editor.putString("CounterWidgetData", json);
        editor.apply();

    }
    public ArrayList<CounterWidgetData> retrieveCounterWidgetList(){

        Gson gson = new Gson();
        String json = sharedPreferences.getString("CounterWidgetData", null);
        Type type = new TypeToken<ArrayList<CounterWidgetData>>() {}.getType();
        ArrayList<CounterWidgetData> counterWidgetData = gson.fromJson(json, type);

        if(counterWidgetData == null){
            counterWidgetData = new ArrayList<>();
        }

        return counterWidgetData;
    }

    public void storeClockWidgetList(ArrayList<ClockData> clockDataArrayList){

        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(clockDataArrayList);
        editor.putString("ClockWidgetData", json);
        editor.apply();

    }
    public ArrayList<ClockData> retrieveClockWidgetList(){

        Gson gson = new Gson();
        String json = sharedPreferences.getString("ClockWidgetData", null);
        Type type = new TypeToken<ArrayList<ClockData>>() {}.getType();
        ArrayList<ClockData> clockData = gson.fromJson(json, type);

        if(clockData == null){
            clockData = new ArrayList<>();
        }

        return clockData;
    }

//    public void storeTimerWidgetList(ArrayList<TimerWidgetData> timerWidgetData){
//
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        Gson gson = new Gson();
//        String json = gson.toJson(timerWidgetData);
//        editor.putString("TimerWidgetData", json);
//        editor.apply();
//
//    }
//    public ArrayList<TimerWidgetData> retrieveTimerWidgetList(){
//
//        Gson gson = new Gson();
//        String json = sharedPreferences.getString("TimerWidgetData", null);
//        Type type = new TypeToken<ArrayList<TimerWidgetData>>() {}.getType();
//        ArrayList<TimerWidgetData> timerWidgetData = gson.fromJson(json, type);
//
//        if(timerWidgetData == null){
//            timerWidgetData = new ArrayList<>();
//        }
//
//        return timerWidgetData;
//    }

}
