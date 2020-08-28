package com.theflopguyproductions.ticktrack.utils.helpers;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.theflopguyproductions.ticktrack.counter.CounterBackupData;
import com.theflopguyproductions.ticktrack.counter.CounterData;
import com.theflopguyproductions.ticktrack.timer.TimerBackupData;
import com.theflopguyproductions.ticktrack.timer.TimerData;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class JsonHelper {

    public void timerDataBackup(ArrayList<TimerData> timerData){

        ArrayList<TimerBackupData> timerBackupData = new ArrayList<>();

        for(int i = 0; i<timerData.size(); i++){
            timerBackupData.get(i).setTimerCreateTimeStamp(timerData.get(i).getTimerCreateTimeStamp());
            timerBackupData.get(i).setTimerHour(timerData.get(i).getTimerHour());
            timerBackupData.get(i).setTimerMinute(timerData.get(i).getTimerMinute());
            timerBackupData.get(i).setTimerSecond(timerData.get(i).getTimerSecond());
            timerBackupData.get(i).setTimerLabel(timerData.get(i).getTimerLabel());
            timerBackupData.get(i).setTimerFlag(timerData.get(i).getTimerFlag());
            timerBackupData.get(i).setTimerTotalTimeInMillis(timerData.get(i).getTimerTotalTimeInMillis());
            timerBackupData.get(i).setTimerID(timerData.get(i).getTimerID());
        }

        if(timerBackupData.size()>0){
            timerArrayListToJsonObject(timerBackupData);
        }
    }

    public String timerArrayListToJsonObject(ArrayList<TimerBackupData> timerData){

        JsonArray timerJsonArray = new Gson().toJsonTree(timerData).getAsJsonArray();
        JsonObject timerJsonObject = new JsonObject();

        timerJsonObject.add("timerData", timerJsonArray);

        System.out.println(timerJsonObject.toString());

        return timerJsonObject.toString();

    }

    public void counterDataBackup(ArrayList<CounterData> counterData){

        ArrayList<CounterBackupData> counterBackupData = new ArrayList<>();

        for(int i = 0; i<counterData.size(); i++){
            counterBackupData.get(i).setCounterFlag(counterData.get(i).getCounterFlag());
            counterBackupData.get(i).setCounterID(counterData.get(i).getCounterID());
            counterBackupData.get(i).setCounterLabel(counterData.get(i).getCounterLabel());
            counterBackupData.get(i).setCounterSignificantCount(counterData.get(i).getCounterSignificantCount());
            counterBackupData.get(i).setCounterSignificantExist(counterData.get(i).isCounterSignificantExist());
            counterBackupData.get(i).setCounterSwipeMode(counterData.get(i).isCounterSwipeMode());
            counterBackupData.get(i).setCounterTimestamp(counterData.get(i).getCounterTimestamp());
            counterBackupData.get(i).setCounterValue(counterData.get(i).getCounterValue());
        }

        if(counterBackupData.size()>0){
            counterArrayListToJsonObject(counterBackupData);
        }
    }

    public String counterArrayListToJsonObject(ArrayList<CounterBackupData> counterData){

        JsonArray counterJsonArray = new Gson().toJsonTree(counterData).getAsJsonArray();
        JsonObject counterJsonObject = new JsonObject();

        counterJsonObject.add("counterData", counterJsonArray);

        System.out.println(counterJsonObject.toString());

        return counterJsonObject.toString();

    }

    public void saveTimerDataToJson(Context context, JsonObject timerObject){
        String timerJsonString = timerObject.toString();
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("timerBackupData.json", Context.MODE_PRIVATE));
            outputStreamWriter.write(timerJsonString);
            outputStreamWriter.close();
            uploadToStorage();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private void uploadToStorage() {



    }

    public void retrieveTimerDataFromJson(){

    }

    public void timerBackupMerge(){

    }

    public void saveCounterDataToJson(){

    }

    public void retrieveCounterDataFromJson(){

    }

    public void counterBackupMerge(){

    }

}
