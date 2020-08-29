package com.theflopguyproductions.ticktrack.utils.helpers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.theflopguyproductions.ticktrack.counter.CounterBackupData;
import com.theflopguyproductions.ticktrack.counter.CounterData;
import com.theflopguyproductions.ticktrack.timer.TimerBackupData;
import com.theflopguyproductions.ticktrack.timer.TimerData;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackFirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;

public class JsonHelper {

    FirebaseStorage storage;
    Context context;
    TickTrackFirebaseDatabase tickTrackFirebaseDatabase;

    public JsonHelper(Context context) {
        this.context = context;
        this.storage = FirebaseStorage.getInstance();
        tickTrackFirebaseDatabase = new TickTrackFirebaseDatabase(context);
    }

    public void timerDataBackup(ArrayList<TimerData> timerData){

        ArrayList<TimerBackupData> timerBackupData = new ArrayList<>();

        for(int i = 0; i<timerData.size(); i++){
            TimerBackupData timerBackupData1 = new TimerBackupData();
            timerBackupData1.setTimerCreateTimeStamp(timerData.get(i).getTimerCreateTimeStamp());
            timerBackupData1.setTimerHour(timerData.get(i).getTimerHour());
            timerBackupData1.setTimerMinute(timerData.get(i).getTimerMinute());
            timerBackupData1.setTimerSecond(timerData.get(i).getTimerSecond());
            timerBackupData1.setTimerLabel(timerData.get(i).getTimerLabel());
            timerBackupData1.setTimerFlag(timerData.get(i).getTimerFlag());
            timerBackupData1.setTimerTotalTimeInMillis(timerData.get(i).getTimerTotalTimeInMillis());
            timerBackupData1.setTimerID(timerData.get(i).getTimerID());
            timerBackupData.add(timerBackupData1);
        }

        if(timerBackupData.size()>0){
            timerArrayListToJsonObject(timerBackupData);
            tickTrackFirebaseDatabase.storeBackupTimerList(timerBackupData);
        }
    }

    public void timerArrayListToJsonObject(ArrayList<TimerBackupData> timerData){

        JsonArray timerJsonArray = new Gson().toJsonTree(timerData).getAsJsonArray();
        JsonObject timerJsonObject = new JsonObject();

        timerJsonObject.add("timerData", timerJsonArray);


        System.out.println(timerJsonObject.toString());

        saveTimerDataToJson(timerJsonObject);

    }

    public void saveTimerDataToJson(JsonObject timerObject){
        String timerJsonString = timerObject.toString();
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("timerBackupData.json", Context.MODE_PRIVATE));
            outputStreamWriter.write(timerJsonString);
            outputStreamWriter.close();
            uploadTimerToStorage();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private void uploadTimerToStorage() {

        StorageReference storageRef = storage.getReference().child("TickTrackBackups").child("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("TimerData");
        StorageReference mountainsRef = storageRef.child("timerData.json");
        File directory = context.getFilesDir();
        File file = new File(directory, "timerBackupData.json");

        try {
            InputStream stream = new FileInputStream(file);
            UploadTask uploadTask = mountainsRef.putStream(stream);
            uploadTask.addOnFailureListener(exception -> {
                Toast.makeText(context, "Upload Failed", Toast.LENGTH_SHORT).show();
            }).addOnSuccessListener(taskSnapshot -> {
                Toast.makeText(context, "Upload Success", Toast.LENGTH_SHORT).show();
            });

        } catch (FileNotFoundException e) {
            Log.e("Exception", "File upload failed: " + e.toString());
        }

    }

    public void counterDataBackup(ArrayList<CounterData> counterData){

        ArrayList<CounterBackupData> counterBackupData = new ArrayList<>();

        for(int i = 0; i<counterData.size(); i++){
            CounterBackupData counterBackupData1 = new CounterBackupData();
            counterBackupData1.setCounterFlag(counterData.get(i).getCounterFlag());
            counterBackupData1.setCounterID(counterData.get(i).getCounterID());
            counterBackupData1.setCounterLabel(counterData.get(i).getCounterLabel());
            counterBackupData1.setCounterSignificantCount(counterData.get(i).getCounterSignificantCount());
            counterBackupData1.setCounterSignificantExist(counterData.get(i).isCounterSignificantExist());
            counterBackupData1.setCounterSwipeMode(counterData.get(i).isCounterSwipeMode());
            counterBackupData1.setCounterTimestamp(counterData.get(i).getCounterTimestamp());
            counterBackupData1.setCounterValue(counterData.get(i).getCounterValue());
            counterBackupData.add(counterBackupData1);
        }

        if(counterBackupData.size()>0){
            counterArrayListToJsonObject(counterBackupData);
            tickTrackFirebaseDatabase.storeBackupCounterList(counterBackupData);
        }
    }

    public void counterArrayListToJsonObject(ArrayList<CounterBackupData> counterData){

        JsonArray counterJsonArray = new Gson().toJsonTree(counterData).getAsJsonArray();
        JsonObject counterJsonObject = new JsonObject();

        counterJsonObject.add("counterData", counterJsonArray);

        System.out.println(counterJsonObject.toString());

        saveCounterDataToJson(counterJsonObject);

    }

    public void saveCounterDataToJson(JsonObject jsonObject){
        String counterJsonString = jsonObject.toString();
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("counterBackupData.json", Context.MODE_PRIVATE));
            outputStreamWriter.write(counterJsonString);
            outputStreamWriter.close();
            uploadCounterToStorage();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private void uploadCounterToStorage() {
        StorageReference storageRef = storage.getReference().child("TickTrackBackups").child("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("CounterData");
        StorageReference mountainsRef = storageRef.child("counterData.json");
        File directory = context.getFilesDir();
        File file = new File(directory, "counterBackupData.json");

        try {
            InputStream stream = new FileInputStream(file);
            UploadTask uploadTask = mountainsRef.putStream(stream);
            uploadTask.addOnFailureListener(exception -> {
                Toast.makeText(context, "Upload Failed", Toast.LENGTH_SHORT).show();
            }).addOnSuccessListener(taskSnapshot -> {
                Toast.makeText(context, "Upload Success", Toast.LENGTH_SHORT).show();
            });

        } catch (FileNotFoundException e) {
            Log.e("Exception", "File upload failed: " + e.toString());
        }
    }

    private void downloadCounterBackup(){
        StorageReference storageRef = storage.getReference().child("TickTrackBackups").child("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("CounterData");
        StorageReference mountainsRef = storageRef.child("counterData.json");
        File directory = context.getFilesDir();
        File file = new File(directory, "counterRetrievedData.json");

        mountainsRef.getFile(file).addOnFailureListener(exception -> {
            Toast.makeText(context, "Download Failed", Toast.LENGTH_SHORT).show();
        }).addOnSuccessListener(taskSnapshot -> {
            restoredCounterToArray();
            Toast.makeText(context, "Download Success", Toast.LENGTH_SHORT).show();
        });
    }

    private void restoredCounterToArray() {
        File directory = context.getFilesDir();
        File file = new File(directory, "counterRetrievedData.json");
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(file);
        System.out.println(json);
    }

    public void downloadTimerBackup(){
        StorageReference storageRef = storage.getReference().child("TickTrackBackups").child("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("TimerData");
        StorageReference mountainsRef = storageRef.child("timerData.json");

        File directory = context.getFilesDir();
        try {
            File local = File.createTempFile("timerRetrievedBackupData", "json", directory);
            mountainsRef.getFile(local).addOnFailureListener(exception -> {
                Toast.makeText(context, "Upload Failed", Toast.LENGTH_SHORT).show();
            }).addOnSuccessListener(taskSnapshot -> {
                restoredTimerToArray();
                Toast.makeText(context, "Upload Success", Toast.LENGTH_SHORT).show();
            });
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void restoredTimerToArray() {
        String json = null;

        File directory = context.getFilesDir();
        File file = new File(directory, "timerRetrievedBackupData.json");

        try {
            InputStream is = new FileInputStream(file);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
            JSONArray jsonArr = new JSONArray(json);
            ArrayList<TimerBackupData> timerBackupData = new ArrayList<>();
            for (int i = 0; i < jsonArr.length(); i++) {
                JSONObject jsonObj = jsonArr.getJSONObject(i);
                TimerBackupData timerBackupData1 = new TimerBackupData();
//                timerBackupData1.setTimerCreateTimeStamp(jsonObj.getString("timerCreateTimeStamp"));
                timerBackupData1.setTimerHour(jsonObj.getInt("timerHour"));
                timerBackupData1.setTimerMinute(jsonObj.getInt("timerMinute"));
                timerBackupData1.setTimerSecond(jsonObj.getInt("timerSecond"));
                timerBackupData1.setTimerLabel(jsonObj.getString("timerLabel"));
                timerBackupData1.setTimerFlag(jsonObj.getInt("timerFlag"));
                timerBackupData1.setTimerTotalTimeInMillis(jsonObj.getLong("timerTotalTimeInMillis"));
                timerBackupData1.setTimerID(jsonObj.getInt("timerID"));
                timerBackupData.add(timerBackupData1);
            }
            System.out.println(timerBackupData.size()+"");
        } catch (IOException | JSONException ex) {
            ex.printStackTrace();
        }


    }

}
