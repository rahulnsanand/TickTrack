package com.theflopguyproductions.ticktrack.utils.firebase;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.theflopguyproductions.ticktrack.counter.CounterBackupData;
import com.theflopguyproductions.ticktrack.counter.CounterData;
import com.theflopguyproductions.ticktrack.settings.SettingsData;
import com.theflopguyproductions.ticktrack.timer.data.TimerBackupData;
import com.theflopguyproductions.ticktrack.timer.data.TimerData;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackFirebaseDatabase;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;

public class JsonHelper {

    Context context;
    TickTrackFirebaseDatabase tickTrackFirebaseDatabase;
    TickTrackDatabase tickTrackDatabase;
    private GDriveHelper gDriveHelper;

    public JsonHelper(Context context) {
        this.context = context;
        tickTrackFirebaseDatabase = new TickTrackFirebaseDatabase(context);
        tickTrackDatabase = new TickTrackDatabase(context);
    }

    public void timerDataBackup(ArrayList<TimerData> timerData){

        ArrayList<TimerBackupData> timerBackupData = new ArrayList<>();

        for(int i = 0; i<timerData.size(); i++){
            if(!timerData.get(i).isQuickTimer()){
                TimerBackupData timerBackupData1 = new TimerBackupData();
                timerBackupData1.setTimerLastEdited(timerData.get(i).getTimerLastEdited());
                timerBackupData1.setTimerHour(timerData.get(i).getTimerHour());
                timerBackupData1.setTimerMinute(timerData.get(i).getTimerMinute());
                timerBackupData1.setTimerSecond(timerData.get(i).getTimerSecond());
                timerBackupData1.setTimerLabel(timerData.get(i).getTimerLabel());
                timerBackupData1.setTimerFlag(timerData.get(i).getTimerFlag());
                timerBackupData1.setTimerTotalTimeInMillis(timerData.get(i).getTimerTotalTimeInMillis());
                timerBackupData1.setTimerID(timerData.get(i).getTimerID());
                timerBackupData1.setTimerIntID(timerData.get(i).getTimerIntID());
                timerBackupData.add(timerBackupData1);
            }
        }

        if(timerBackupData.size()>0){
            compareTimerList(timerBackupData);
        } else {
            tickTrackFirebaseDatabase.setTimerBackupComplete(true);
        }
    }
    private void compareTimerList(ArrayList<TimerBackupData> newBackupTimer) {
        ArrayList<TimerBackupData> oldBackupTimer = tickTrackFirebaseDatabase.retrieveBackupTimerList();
        if(oldBackupTimer.size()!=newBackupTimer.size()){
            timerArrayListToJsonObject(newBackupTimer);
            tickTrackFirebaseDatabase.storeBackupTimerList(newBackupTimer);
        } else {
            if(!newBackupTimer.equals(oldBackupTimer)){
                timerArrayListToJsonObject(newBackupTimer);
                tickTrackFirebaseDatabase.storeBackupTimerList(newBackupTimer);
            }
        }
    }
    public void timerArrayListToJsonObject(ArrayList<TimerBackupData> timerData){
        Gson gson = new Gson();
        String json = gson.toJson(timerData);
        saveTimerDataToJson(json);
    }
    public void saveTimerDataToJson(String timerObject){
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("timerBackupData.json", Context.MODE_PRIVATE));
            outputStreamWriter.write(timerObject);
            outputStreamWriter.close();
            uploadTimerToStorage(timerObject);
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
    private void uploadTimerToStorage(String jsonObject) {

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
        if(account!=null){
            GoogleAccountCredential credential =
                    GoogleAccountCredential.usingOAuth2(
                            context, Collections.singleton(DriveScopes.DRIVE_APPDATA));
            credential.setSelectedAccount(account.getAccount());
            Drive googleDriveService =
                    new Drive.Builder(
                            AndroidHttp.newCompatibleTransport(),
                            new GsonFactory(),
                            credential)
                            .setApplicationName("TickTrack")
                            .build();

            gDriveHelper = new GDriveHelper(googleDriveService, context);
            createTimerBackup(gDriveHelper, jsonObject);
        }
    }
    private void createTimerBackup(GDriveHelper gDriveHelper, String jsonObject) {
        gDriveHelper.createTimerBackup("timerBackup.json")
                .addOnSuccessListener(s -> {
                    if(s.first==1){
                        openGDriveFile(gDriveHelper, s.second, jsonObject, "timerBackup.json");
                        Toast.makeText(context, "Timer Upload Success", Toast.LENGTH_SHORT).show();
                    } else {
                        createTimerBackup(gDriveHelper, jsonObject);
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(context, "Timer Upload Success", Toast.LENGTH_SHORT).show());
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
            counterBackupData1.setNegativeAllowed(counterData.get(i).isNegativeAllowed());
            counterBackupData.add(counterBackupData1);
        }

        if(counterBackupData.size()>0){
            compareCounterList(counterBackupData);
        } else {
            tickTrackFirebaseDatabase.setCounterBackupComplete(true);
        }
    }
    private void compareCounterList(ArrayList<CounterBackupData> newBackupCounter) {
        ArrayList<CounterBackupData> oldBackupTimer = tickTrackFirebaseDatabase.retrieveBackupCounterList();
        if(oldBackupTimer.size()!=newBackupCounter.size()){
            counterArrayListToJsonObject(newBackupCounter);
            tickTrackFirebaseDatabase.storeBackupCounterList(newBackupCounter);
        } else {
            if(!newBackupCounter.equals(oldBackupTimer)){
                counterArrayListToJsonObject(newBackupCounter);
                tickTrackFirebaseDatabase.storeBackupCounterList(newBackupCounter);
            }
        }
    }
    public void counterArrayListToJsonObject(ArrayList<CounterBackupData> counterData){
        Gson gson = new Gson();
        String json = gson.toJson(counterData);
        saveCounterDataToJson(json);
    }
    public void saveCounterDataToJson(String jsonObject){
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("counterBackupData.json", Context.MODE_PRIVATE));
            outputStreamWriter.write(jsonObject);
            outputStreamWriter.close();
            uploadCounterToStorage(jsonObject);
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
    private void uploadCounterToStorage(String jsonObject) {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
        if(account!=null){
            GoogleAccountCredential credential =
                    GoogleAccountCredential.usingOAuth2(
                            context, Collections.singleton(DriveScopes.DRIVE_APPDATA));
            credential.setSelectedAccount(account.getAccount());
            Drive googleDriveService =
                    new Drive.Builder(
                            AndroidHttp.newCompatibleTransport(),
                            new GsonFactory(),
                            credential)
                            .setApplicationName("TickTrack")
                            .build();

            gDriveHelper = new GDriveHelper(googleDriveService, context);
            createCounterBackup(gDriveHelper, jsonObject);
        }
    }

    private void createCounterBackup(GDriveHelper gDriveHelper, String jsonObject) {
        gDriveHelper.createCounterBackup("counterBackup.json")
                .addOnSuccessListener(s -> {
                    if(s.first==1){
                        openGDriveFile(gDriveHelper, s.second, jsonObject, "counterBackup.json");
                        Toast.makeText(context, "Counter Upload Success", Toast.LENGTH_SHORT).show();
                    } else {
                        createCounterBackup(gDriveHelper, jsonObject);
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(context, "Counter Upload Success", Toast.LENGTH_SHORT).show());
    }
    private void preferencesDataBackup() {
        ArrayList<SettingsData> settingsData = new ArrayList<>();

        SettingsData data = new SettingsData();
        data.setCounterBackupOn(tickTrackDatabase.getSharedPref(context).getBoolean("counterDataBackup", false));
        data.setTimerBackupOn(tickTrackDatabase.getSharedPref(context).getBoolean("timerDataBackup", false));
        data.setHapticFeedback(tickTrackDatabase.isHapticEnabled());
        data.setLastBackupTime(System.currentTimeMillis());
        data.setSyncDataFrequency(tickTrackDatabase.getSyncFrequency());
        data.setThemeMode(tickTrackDatabase.getThemeMode());
        data.setScreensaverClockStyle(tickTrackDatabase.getScreenSaverClock());
        data.setSumDisplayed(tickTrackDatabase.isSumEnabled());

        settingsData.add(data);

        Gson gson = new Gson();
        String json = gson.toJson(settingsData);

        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("settingsBackupData.json", Context.MODE_PRIVATE));
            outputStreamWriter.write(json);
            outputStreamWriter.close();

            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
            if(account!=null){
                GoogleAccountCredential credential =
                        GoogleAccountCredential.usingOAuth2(
                                context, Collections.singleton(DriveScopes.DRIVE_APPDATA));
                credential.setSelectedAccount(account.getAccount());
                Drive googleDriveService =
                        new Drive.Builder(
                                AndroidHttp.newCompatibleTransport(),
                                new GsonFactory(),
                                credential)
                                .setApplicationName("TickTrack")
                                .build();

                gDriveHelper = new GDriveHelper(googleDriveService, context);
                createSettingsBackup(gDriveHelper, json);
            }

        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }

    }

    private void createSettingsBackup(GDriveHelper gDriveHelper, String json) {
        gDriveHelper.createSettingsBackup("settingsBackup.json")
                .addOnSuccessListener(s -> {
                    if(s.first==1){
                        openGDriveFile(gDriveHelper, s.second, json, "settingsBackup.json");
                        Toast.makeText(context, "Counter Upload Success", Toast.LENGTH_SHORT).show();
                    } else {
                        createSettingsBackup(gDriveHelper, json);
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(context, "Counter Upload Success", Toast.LENGTH_SHORT).show());
    }

    public void createBackup(){
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
        if(account!=null) {
            if(InternetChecker.isOnline(context)){
                GoogleAccountCredential credential =
                        GoogleAccountCredential.usingOAuth2(
                                context, Collections.singleton(DriveScopes.DRIVE_APPDATA));
                credential.setSelectedAccount(account.getAccount());
                Drive googleDriveService =
                        new Drive.Builder(
                                AndroidHttp.newCompatibleTransport(),
                                new GsonFactory(),
                                credential)
                                .setApplicationName("TickTrack")
                                .build();
                gDriveHelper = new GDriveHelper(googleDriveService, context);
                clearDataSetup(gDriveHelper);
            } else {
                createBackup();
            }
        }
    }

    private void clearDataSetup(GDriveHelper gDriveHelper) {
        gDriveHelper.clearData().addOnSuccessListener(resultInt -> {
            if(resultInt==1){
                System.out.println("CLEAR DATA HAPPENED");
                if(tickTrackFirebaseDatabase.getSharedPref(context).getBoolean("counterDataBackup", true)){
                    counterDataBackup(tickTrackDatabase.retrieveCounterList());
                }
                if(tickTrackFirebaseDatabase.getSharedPref(context).getBoolean("timerDataBackup", false)){
                    timerDataBackup(tickTrackDatabase.retrieveTimerList());
                }
                if(tickTrackFirebaseDatabase.getSharedPref(context).getBoolean("preferencesDataBackup", true)){
                    preferencesDataBackup();
                }
            } else if(resultInt==0){
                System.out.println("EXCEPTION CAUGHT");
            } else {
                System.out.println("CLEAR DATA FAILED");
                clearDataSetup(gDriveHelper);
            }
        }).addOnFailureListener(e -> Toast.makeText(context, "Deletion Error", Toast.LENGTH_SHORT).show());
    }




    private void openGDriveFile(GDriveHelper gDriveHelper, String fileId, String jsonContent, String fileName) {
        if (gDriveHelper != null) {
            Log.d("TAG", "Reading file " + fileId);

            gDriveHelper.readFile(fileId)
                    .addOnSuccessListener(nameAndContent -> {
                        if(nameAndContent.first!=null && nameAndContent.second!=null){
                            String name = nameAndContent.first;
                            String content = nameAndContent.second;

                            System.out.println(name);
                            System.out.println(content);

                            saveFile(fileId, gDriveHelper, jsonContent, fileName);
                        } else {
                            openGDriveFile(gDriveHelper, fileId, jsonContent, fileName);
                        }
                    })
                    .addOnFailureListener(exception ->
                            Log.e("TAG", "Couldn't read file.", exception));
        }
    }
    private void saveFile(String fileID, GDriveHelper gDriveHelper, String jsonContent, String fileName) {
        if (gDriveHelper != null) {
            Log.d("TAG", "Saving " + fileID);
            gDriveHelper.saveFile(fileID, fileName, jsonContent)
                    .addOnSuccessListener(aVoid -> {
                        if(aVoid==1){
                            switch (fileName) {
                                case "counterBackup.json":
                                    System.out.println("Counter BACKUP DONE");
                                    tickTrackFirebaseDatabase.setCounterBackupComplete(true);
                                    break;
                                case "timerBackup.json":
                                    System.out.println("Timer BACKUP DONE");
                                    tickTrackFirebaseDatabase.setTimerBackupComplete(true);
                                    break;
                                case "settingsBackup.json":
                                    System.out.println("Settings BACKUP DONE");
                                    tickTrackFirebaseDatabase.setSettingsBackupComplete(true);
                                    break;
                            }
                        } else {
                            saveFile(fileID, gDriveHelper, jsonContent, fileName);
                        }
                    })
                    .addOnFailureListener(exception ->
                            Log.e("TAG", "Unable to save file via REST.", exception));
        }
    }

    /**
     * Restoring functions of counter and timer
     */
    public void restoreCounterData(){

        String jsonString = tickTrackFirebaseDatabase.getCounterRestoreString();

        if(jsonString!=null){
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<CounterBackupData>>() {}.getType();
            ArrayList<CounterBackupData> counterBackupData = gson.fromJson(jsonString, type);
            if(counterBackupData == null){
                counterBackupData = new ArrayList<>();
            }

            tickTrackFirebaseDatabase.storeBackupCounterList(counterBackupData);

            for(int i=0; i<counterBackupData.size(); i++){
                CounterData newCounter = new CounterData();
                newCounter.setCounterID(counterBackupData.get(i).getCounterID());
                newCounter.setCounterLabel(counterBackupData.get(i).getCounterLabel());
                newCounter.setCounterValue(counterBackupData.get(i).getCounterValue());
                newCounter.setCounterTimestamp(counterBackupData.get(i).getCounterTimestamp());
                newCounter.setCounterFlag(counterBackupData.get(i).getCounterFlag());
                newCounter.setCounterSignificantCount(counterBackupData.get(i).getCounterSignificantCount());
                newCounter.setCounterSignificantExist(counterBackupData.get(i).isCounterSignificantExist());
                newCounter.setCounterSwipeMode(counterBackupData.get(i).isCounterSwipeMode());
                newCounter.setNegativeAllowed(counterBackupData.get(i).isNegativeAllowed());

                mergeCounterData(newCounter);

            }
            Toast.makeText(context, "Counter data restored", Toast.LENGTH_SHORT).show();
        }

        tickTrackFirebaseDatabase.setCounterDownloadStatus(1);

    }
    private void mergeCounterData(CounterData counterData) {
        boolean isNew = true;
        ArrayList<CounterData> counterLocalData = tickTrackDatabase.retrieveCounterList();
        for(int j=0; j<counterLocalData.size(); j++){
            if(counterData.getCounterID().equals(counterLocalData.get(j).getCounterID())){
                if(counterData.getCounterTimestamp()<=counterLocalData.get(j).getCounterTimestamp()){
                    isNew = false;
                }
            }
        }
        if(isNew){
            counterLocalData.add(counterData);
            tickTrackDatabase.storeCounterList(counterLocalData);
        }
    }

    public void restoreTimerData(){

        String jsonContent = tickTrackFirebaseDatabase.getTimerRestoreString();

        if(jsonContent!=null){
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<TimerBackupData>>() {}.getType();
            ArrayList<TimerBackupData> timerBackupData = gson.fromJson(jsonContent, type);

            if(timerBackupData == null){
                timerBackupData = new ArrayList<>();
            }

            tickTrackFirebaseDatabase.storeBackupTimerList(timerBackupData);

            for(int i=0; i<timerBackupData.size(); i++){
                TimerData newTimer = new TimerData();
                newTimer.setTimerID(timerBackupData.get(i).getTimerID());
                newTimer.setTimerIntID(timerBackupData.get(i).getTimerIntID());
                newTimer.setQuickTimer(false);
                newTimer.setTimerLastEdited(timerBackupData.get(i).getTimerLastEdited());
                newTimer.setTimerFlag(timerBackupData.get(i).getTimerFlag());
                newTimer.setTimerHour(timerBackupData.get(i).getTimerHour());
                newTimer.setTimerMinute(timerBackupData.get(i).getTimerMinute());
                newTimer.setTimerSecond(timerBackupData.get(i).getTimerSecond());
                newTimer.setTimerLabel(timerBackupData.get(i).getTimerLabel());
                newTimer.setTimerStartTimeInMillis(-1);
                newTimer.setTimerTotalTimeInMillis(timerBackupData.get(i).getTimerTotalTimeInMillis());
                newTimer.setTimerPause(false);
                newTimer.setTimerOn(false);

                mergeTimerData(newTimer);
            }
            Toast.makeText(context, "Timer data restored", Toast.LENGTH_SHORT).show();
        }
        tickTrackFirebaseDatabase.setTimerDownloadStatus(1);
    }
    private void mergeTimerData(TimerData timerData) {
        boolean isNew = true;
        ArrayList<TimerData> timerLocalData = tickTrackDatabase.retrieveTimerList();
        for(int j=0; j<timerLocalData.size(); j++){
            if(timerData.getTimerID().equals(timerLocalData.get(j).getTimerID())){
                if(timerData.getTimerLastEdited()<=timerLocalData.get(j).getTimerLastEdited()){
                    isNew = false;
                }
            }
        }
        if(isNew){
            timerLocalData.add(timerData);
            tickTrackDatabase.storeTimerList(timerLocalData);
        }
    }



}
