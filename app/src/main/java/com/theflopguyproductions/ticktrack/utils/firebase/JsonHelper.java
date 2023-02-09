package com.theflopguyproductions.ticktrack.utils.firebase;

import android.content.Context;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.common.primitives.Bytes;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.counter.CounterBackupData;
import com.theflopguyproductions.ticktrack.counter.CounterData;
import com.theflopguyproductions.ticktrack.settings.SettingsData;
import com.theflopguyproductions.ticktrack.timer.data.TimerBackupData;
import com.theflopguyproductions.ticktrack.timer.data.TimerData;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackFirebaseDatabase;

import org.checkerframework.checker.nullness.qual.RequiresNonNull;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class JsonHelper {

    Context context;
    TickTrackFirebaseDatabase tickTrackFirebaseDatabase;
    TickTrackDatabase tickTrackDatabase;
    private GDriveHelper gDriveHelper;

    FirebaseStorage backupStorage = FirebaseStorage.getInstance();
    StorageReference backupStorageReference = backupStorage.getReference();

    public JsonHelper(Context context) {
        this.context = context;
        tickTrackFirebaseDatabase = new TickTrackFirebaseDatabase(context);
        tickTrackDatabase = new TickTrackDatabase(context);
    }

    public void timerDataBackup(ArrayList<TimerData> timerData) {

        ArrayList<TimerBackupData> timerBackupData = new ArrayList<>();

        for (int i = 0; i < timerData.size(); i++) {
            if (!timerData.get(i).isQuickTimer()) {
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

        if (timerBackupData.size() > 0) {
            compareTimerList(timerBackupData);
        } else {
            tickTrackFirebaseDatabase.setTimerBackupComplete(true);
        }
    }

    private void compareTimerList(ArrayList<TimerBackupData> newBackupTimer) {
        ArrayList<TimerBackupData> oldBackupTimer = tickTrackFirebaseDatabase.retrieveBackupTimerList();
        if (oldBackupTimer.size() != newBackupTimer.size()) {
            timerArrayListToJsonObject(newBackupTimer);
            tickTrackFirebaseDatabase.storeBackupTimerList(newBackupTimer);
        } else {
            if (!newBackupTimer.equals(oldBackupTimer)) {
                timerArrayListToJsonObject(newBackupTimer);
                tickTrackFirebaseDatabase.storeBackupTimerList(newBackupTimer);
            }
        }
    }

    public void timerArrayListToJsonObject(ArrayList<TimerBackupData> timerData) {
        Gson gson = new Gson();
        String json = gson.toJson(timerData);
        saveTimerDataToJson(json);
    }

    public void saveTimerDataToJson(String timerObject) {
        uploadTimerToStorage(timerObject);

//        try {
//            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("timerBackupData.json", Context.MODE_PRIVATE));
//            outputStreamWriter.write(timerObject);
//            outputStreamWriter.close();
//            //uploadTimerToStorage(timerObject);
//        } catch (IOException e) {
//            Log.e("Exception", "File write failed: " + e);
//        }
    }


    long waitDuration = (long) 1000 * 60;
    boolean isTimerFirstLoop = false;
    long firstTimerLoopTimestamp = 0;
    private void uploadTimerToStorage(String json) {
        InputStream is = new ByteArrayInputStream(json.getBytes());
        UploadTask uploadTask = backupStorageReference.child("timerBackupData.json").putStream(is);
        uploadTask.addOnFailureListener(e -> {
                    if(!isTimerFirstLoop){
                        isTimerFirstLoop = true;
                        firstTimerLoopTimestamp = System.currentTimeMillis();
                    }

                    if((System.currentTimeMillis() - firstTimerLoopTimestamp < waitDuration)){
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException ex) {
                            e.printStackTrace();
                        }
                        uploadTimerToStorage(json);
                    } else {
                        tickTrackFirebaseDatabase.setBackupFailedMode(true);
                        System.out.println(e.getMessage());
                    }
                })
                .addOnSuccessListener(taskSnapshot -> {
                    System.out.println("DONE UPLOADING timerBackupData");
                    isTimerDone = true;
                    tickTrackFirebaseDatabase.setTimerBackupComplete(true);
                });
    }

    //TODO: uploadTimerToStorage and createTimerBackup Backups for GDrive
//    private void uploadTimerToStorage(String jsonObject) {
//
//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
//        if(account!=null){
//            GoogleAccountCredential credential =
//                    GoogleAccountCredential.usingOAuth2(
//                            context, Collections.singleton(DriveScopes.DRIVE_APPDATA));
//            credential.setSelectedAccount(account.getAccount());
//            Drive googleDriveService =
//                    new Drive.Builder(
//                            AndroidHttp.newCompatibleTransport(),
//                            new GsonFactory(),
//                            credential)
//                            .setApplicationName("TickTrack")
//                            .build();
//
//            gDriveHelper = new GDriveHelper(googleDriveService, context);
//            createTimerBackup(gDriveHelper, jsonObject);
//        }
//    }
//    private void createTimerBackup(GDriveHelper gDriveHelper, String jsonObject) {
//        gDriveHelper.createTimerBackup("timerBackup.json")
//                .addOnSuccessListener(s -> {
//                    if(s.first==1){
//                        openGDriveFile(gDriveHelper, s.second, jsonObject, "timerBackup.json");
//                    } else {
//                        try {
//                            Thread.sleep(10000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        createTimerBackup(gDriveHelper, jsonObject);
//                    }
//                });
//    }

    public void counterDataBackup(ArrayList<CounterData> counterData) {

        ArrayList<CounterBackupData> counterBackupData = new ArrayList<>();

        for (int i = 0; i < counterData.size(); i++) {
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

        if (counterBackupData.size() > 0) {
            compareCounterList(counterBackupData);
        } else {
            tickTrackFirebaseDatabase.setCounterBackupComplete(true);
        }
    }

    private void compareCounterList(ArrayList<CounterBackupData> newBackupCounter) {
        ArrayList<CounterBackupData> oldBackupTimer = tickTrackFirebaseDatabase.retrieveBackupCounterList();
        if (oldBackupTimer.size() != newBackupCounter.size()) {
            counterArrayListToJsonObject(newBackupCounter);
            tickTrackFirebaseDatabase.storeBackupCounterList(newBackupCounter);
        } else {
            if (!newBackupCounter.equals(oldBackupTimer)) {
                counterArrayListToJsonObject(newBackupCounter);
                tickTrackFirebaseDatabase.storeBackupCounterList(newBackupCounter);
            }
        }
    }

    public void counterArrayListToJsonObject(ArrayList<CounterBackupData> counterData) {
        Gson gson = new Gson();
        String json = gson.toJson(counterData);
        saveCounterDataToJson(json);
    }

    public void saveCounterDataToJson(String jsonObject) {
        uploadCounterToStorage(jsonObject);
//        try {
//            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("counterBackupData.json", Context.MODE_PRIVATE));
//            outputStreamWriter.write(jsonObject);
//            outputStreamWriter.close();
//            //uploadCounterToStorage(jsonObject);
//
//        } catch (IOException e) {
//            Log.e("Exception", "File write failed: " + e.toString());
//        }
    }
    boolean isCounterFirstLoop = false;
    long firstCounterLoopTimestamp = 0;
    private void uploadCounterToStorage(String json) {
        InputStream is = new ByteArrayInputStream(json.getBytes());
        UploadTask uploadTask = backupStorageReference.child("counterBackupData.json").putStream(is);
        uploadTask.addOnFailureListener(e -> {
                    if(!isCounterFirstLoop){
                        isCounterFirstLoop = true;
                        firstCounterLoopTimestamp = System.currentTimeMillis();
                    }

                    if((System.currentTimeMillis() - firstCounterLoopTimestamp < waitDuration)){
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException ex) {
                            e.printStackTrace();
                        }
                        uploadCounterToStorage(json);
                    } else {
                        tickTrackFirebaseDatabase.setBackupFailedMode(true);
                        System.out.println(e.getMessage());
                    }
                })
                .addOnSuccessListener(taskSnapshot -> {
                    System.out.println("DONE UPLOADING counterBackupData");
                    isCounterDone = true;
                    tickTrackFirebaseDatabase.setCounterBackupComplete(true);
                });
    }

    //TODO: UploadCounterToStorage and CreateCounterBackup For Backup
//    private void uploadCounterToStorage(String jsonObject) {
//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
//        if(account!=null){
//            GoogleAccountCredential credential =
//                    GoogleAccountCredential.usingOAuth2(
//                            context, Collections.singleton(DriveScopes.DRIVE_APPDATA));
//            credential.setSelectedAccount(account.getAccount());
//            Drive googleDriveService =
//                    new Drive.Builder(
//                            AndroidHttp.newCompatibleTransport(),
//                            new GsonFactory(),
//                            credential)
//                            .setApplicationName("TickTrack")
//                            .build();
//
//            gDriveHelper = new GDriveHelper(googleDriveService, context);
//            createCounterBackup(gDriveHelper, jsonObject);
//        }
//    }
//
//    private void createCounterBackup(GDriveHelper gDriveHelper, String jsonObject) {
//        gDriveHelper.createCounterBackup("counterBackup.json")
//                .addOnSuccessListener(s -> {
//                    if(s.first==1){
//                        openGDriveFile(gDriveHelper, s.second, jsonObject, "counterBackup.json");
//                    } else {
//                        try {
//                            Thread.sleep(10000);
//                        } catch (InterruptedException e) {
//
//                            e.printStackTrace();
//                        }
//                        createCounterBackup(gDriveHelper, jsonObject);
//                    }
//                });
//    }

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
        data.setMilestoneVibrate(tickTrackDatabase.isMilestoneVibrate());
        data.setSettingsChangeTime(tickTrackDatabase.getSettingsChangeTime());
        data.setSwitchedToFirebase(tickTrackDatabase.isSwitchedToFirebase());

        settingsData.add(data);

        Gson gson = new Gson();
        String json = gson.toJson(settingsData);

//
//        try {
//            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("settingsBackupData.json", Context.MODE_PRIVATE));
//            outputStreamWriter.write(json);
//            outputStreamWriter.close();
//
//
//
//        } catch (IOException e) {
//            Log.e("Exception", "File write failed: " + e);
//        }

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
        if (account != null) {
            //createSettingsBackup(gDriveHelper, json);
            createSettingsBackup(json);
        }

    }

    //TODO: preferencesDataBackup for GdriveBackup
//    private void preferencesDataBackup() {
//        ArrayList<SettingsData> settingsData = new ArrayList<>();
//
//        SettingsData data = new SettingsData();
//        data.setCounterBackupOn(tickTrackDatabase.getSharedPref(context).getBoolean("counterDataBackup", false));
//        data.setTimerBackupOn(tickTrackDatabase.getSharedPref(context).getBoolean("timerDataBackup", false));
//        data.setHapticFeedback(tickTrackDatabase.isHapticEnabled());
//        data.setLastBackupTime(System.currentTimeMillis());
//        data.setSyncDataFrequency(tickTrackDatabase.getSyncFrequency());
//        data.setThemeMode(tickTrackDatabase.getThemeMode());
//        data.setScreensaverClockStyle(tickTrackDatabase.getScreenSaverClock());
//        data.setSumDisplayed(tickTrackDatabase.isSumEnabled());
//        data.setMilestoneVibrate(tickTrackDatabase.isMilestoneVibrate());
//        data.setSettingsChangeTime(tickTrackDatabase.getSettingsChangeTime());
//        data.setSwitchedToFirebase(tickTrackDatabase.isSwitchedToFirebase());
//
//        settingsData.add(data);
//
//        Gson gson = new Gson();
//        String json = gson.toJson(settingsData);
//
//        try {
//            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("settingsBackupData.json", Context.MODE_PRIVATE));
//            outputStreamWriter.write(json);
//            outputStreamWriter.close();
//
//            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
//            if(account!=null){
//                GoogleAccountCredential credential =
//                        GoogleAccountCredential.usingOAuth2(
//                                context, Collections.singleton(DriveScopes.DRIVE_APPDATA));
//                credential.setSelectedAccount(account.getAccount());
//                Drive googleDriveService =
//                        new Drive.Builder(
//                                AndroidHttp.newCompatibleTransport(),
//                                new GsonFactory(),
//                                credential)
//                                .setApplicationName("TickTrack")
//                                .build();
//
//                gDriveHelper = new GDriveHelper(googleDriveService, context);
//                createSettingsBackup(gDriveHelper, json);
//            }
//
//        }
//        catch (IOException e) {
//            Log.e("Exception", "File write failed: " + e.toString());
//        }
//
//    }

    boolean isSettingsFirstLoop = false;
    long firstSettingsLoopTimestamp = 0;

    private void createSettingsBackup(String json) {
        InputStream is = new ByteArrayInputStream(json.getBytes());
        UploadTask uploadTask = backupStorageReference.child("settingsBackupData.json").putStream(is);
        uploadTask.addOnFailureListener(e -> {
                    if(!isSettingsFirstLoop){
                        isSettingsFirstLoop = true;
                        firstSettingsLoopTimestamp = System.currentTimeMillis();
                    }

                    if((System.currentTimeMillis() - firstSettingsLoopTimestamp < waitDuration)){
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException ex) {
                            e.printStackTrace();
                        }
                        createSettingsBackup(json);
                    } else {
                        tickTrackFirebaseDatabase.setBackupFailedMode(true);
                        System.out.println(e.getMessage());
                    }
                })
                .addOnSuccessListener(taskSnapshot -> {
                    System.out.println("DONE UPLOADING SETTINGS");
                    isPrefDone = true;
                    tickTrackFirebaseDatabase.setSettingsBackupComplete(true);
                });
    }




    boolean isTimerDone = true, isCounterDone = true, isPrefDone = false;
    Handler backupCheckHandler = new Handler(Looper.getMainLooper());
    Runnable backupCheckRunnable = new Runnable() {
        @Override
        public void run() {
            if(isTimerDone && isCounterDone && isPrefDone){
                tickTrackDatabase.setLastBackupSystemTime(System.currentTimeMillis());
                backupCheckHandler.removeCallbacks(backupCheckRunnable);
            } else if (tickTrackFirebaseDatabase.isBackupFailedMode()){
                backupCheckHandler.removeCallbacks(backupCheckRunnable);
            } else {
                backupCheckHandler.post(backupCheckRunnable);
            }
        }
    };

    //TODO: BACKUP DRIVE CODE BELOW
//    private void createSettingsBackup(GDriveHelper gDriveHelper, String json) {
//
//        gDriveHelper.createSettingsBackup("settingsBackup.json")
//                .addOnSuccessListener(s -> {
//                    if(s.first==1){
//                        openGDriveFile(gDriveHelper, s.second, json, "settingsBackup.json");
//                    } else {
//                        try {
//                            Thread.sleep(10000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        createSettingsBackup(gDriveHelper, json);
//                    }
//                });
//    }

    boolean isFirstLoop = false;
    long firstLoopTimestamp = 0;
    public void createBackup(){
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
        if(account!=null) {
            if(InternetChecker.isOnline(context)){
                if(!isFirstLoop){
                    isFirstLoop = true;
                    firstLoopTimestamp = System.currentTimeMillis();
                }

                backupToFirebaseStorage();

                // TODO: BACKUP CODE FOR GDRIVE BACKUP

//                GoogleAccountCredential credential =
//                        GoogleAccountCredential.usingOAuth2(
//                                context, Collections.singleton(DriveScopes.DRIVE_APPDATA));
//                credential.setSelectedAccount(account.getAccount());
//                Drive googleDriveService =
//                        new Drive.Builder(
//                                AndroidHttp.newCompatibleTransport(),
//                                new GsonFactory(),
//                                credential)
//                                .setApplicationName("TickTrack")
//                                .build();
//                gDriveHelper = new GDriveHelper(googleDriveService, context);
//
//                retrieveDataFromCloud(gDriveHelper);


            } else {
                if((System.currentTimeMillis() - firstLoopTimestamp < waitDuration)){
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    createBackup();
                } else {
                    tickTrackFirebaseDatabase.setBackupFailedMode(true);
                }
            }
        }
    }

    private void backupToFirebaseStorage() {
        System.out.println("Here 1 "+backupStorageReference.getPath());
        System.out.println(Objects.requireNonNull(Objects.requireNonNull(GoogleSignIn
                .getLastSignedInAccount(context)).getEmail()));

        backupStorageReference = backupStorageReference
                .child(Objects.requireNonNull(Objects.requireNonNull(GoogleSignIn
                        .getLastSignedInAccount(context)).getEmail()))
                .child("backup");

        UploadTask uploadTask;
        AssetManager am = context.getAssets();
        try {
            InputStream is = am.open("images/demo.webp");
            uploadTask = backupStorageReference.child("demo.webp").putStream(is);
            uploadTask
                    .addOnSuccessListener(taskSnapshot -> System.out.println("UPLOAD DONE <<<<<<<<<<<<<<<<<<<<"))
                    .addOnFailureListener(taskSnapshot -> System.out.println("FAILED!<<<<<<<<<<<<<<<<<<<<"));

        } catch (IOException e) {
            tickTrackFirebaseDatabase.setBackupFailedMode(true);
            throw new RuntimeException(e);
        }

        final long TWO_MEGABYTE_LIMIT = 2 * 1024 * 1024;
        //TODO 1: Retrieve Data From Storage If Exists
        backupStorageReference.listAll()
                .addOnSuccessListener(list -> {
                    for (StorageReference item : list.getItems()) {

                        System.out.println(item.getName());

                        switch (item.getName()) {
                            case "counterBackupData.json":

                                backupStorageReference.child("counterBackupData.json")
                                        .getBytes(TWO_MEGABYTE_LIMIT).addOnSuccessListener(bytes -> {
                                            // Data for "images/island.jpg" is returns, use this as needed

                                            String dataBytesToString
                                                    = new String(bytes,
                                                    StandardCharsets.UTF_8);
                                            JsonParser jsonParser = new JsonParser();
                                            JsonArray jsonArrayOutput
                                                    = (JsonArray)jsonParser.parse(
                                                    dataBytesToString);
                                            System.out.println("Output : "
                                                    + jsonArrayOutput);

                                            tickTrackFirebaseDatabase.storeCounterRestoreString(String.valueOf(jsonArrayOutput));
                                            restoreCounterDataSync();
                                }).addOnFailureListener(exception -> {
                                    tickTrackFirebaseDatabase.setBackupFailedMode(true);
                                });

                                break;
                            case "timerBackupData.json":
                                backupStorageReference.child("timerBackupData.json")
                                        .getBytes(TWO_MEGABYTE_LIMIT).addOnSuccessListener(bytes -> {
                                            // Data for "images/island.jpg" is returns, use this as needed

                                            String dataBytesToString
                                                    = new String(bytes,
                                                    StandardCharsets.UTF_8);
                                            JsonParser jsonParser = new JsonParser();
                                            JsonArray jsonArrayOutput
                                                    = (JsonArray)jsonParser.parse(
                                                    dataBytesToString);
                                            System.out.println("Output : "
                                                    + jsonArrayOutput);

                                            tickTrackFirebaseDatabase.storeTimerRestoreString(String.valueOf(jsonArrayOutput));
                                            restoreTimerDataSync();
                                }).addOnFailureListener(exception -> {
                                    tickTrackFirebaseDatabase.setBackupFailedMode(true);
                                });

                                break;
                            case "settingsBackupData.json":
                                backupStorageReference.child("settingsBackupData.json")
                                        .getBytes(TWO_MEGABYTE_LIMIT).addOnSuccessListener(bytes -> {
                                            // Data for "images/island.jpg" is returns, use this as needed

                                            String dataBytesToString
                                                    = new String(bytes,
                                                    StandardCharsets.UTF_8);
                                            JsonParser jsonParser = new JsonParser();
                                            JsonArray jsonArrayOutput
                                                    = (JsonArray)jsonParser.parse(
                                                    dataBytesToString);
                                            System.out.println("Output : "
                                                    + jsonArrayOutput);

                                            setupPreferencesForSync(String.valueOf(jsonArrayOutput), tickTrackFirebaseDatabase);
                                            restorePreferencesSync();
                                }).addOnFailureListener(exception -> {
                                    tickTrackFirebaseDatabase.setBackupFailedMode(true);
                                });

                                break;
                        }
                    }

                    if(tickTrackFirebaseDatabase.getSharedPref(context).getBoolean("counterDataBackup", true)){
                        isCounterDone = false;
                        counterDataBackup(tickTrackDatabase.retrieveCounterList());
                    }
                    if(tickTrackFirebaseDatabase.getSharedPref(context).getBoolean("timerDataBackup", true)){
                        isTimerDone = false;
                        timerDataBackup(tickTrackDatabase.retrieveTimerList());
                    }
                    if(tickTrackFirebaseDatabase.getSharedPref(context).getBoolean("preferencesDataBackup", true)){
                        isPrefDone = false;
                        preferencesDataBackup();
                    }

                    backupCheckHandler.post(backupCheckRunnable);
                })
                .addOnFailureListener(e -> {
                    tickTrackFirebaseDatabase.setBackupFailedMode(true);
                    System.out.println();
                });
    }
    private void setupPreferencesForSync(String contents, TickTrackFirebaseDatabase tickTrackFirebaseDatabase) {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<SettingsData>>() {}.getType();
        ArrayList<SettingsData> settingsData = gson.fromJson(contents, type);
        if(settingsData == null){
            settingsData = new ArrayList<>();
        }
        tickTrackFirebaseDatabase.storeSettingsRestoredData(settingsData);
    }

    private void retrieveDataFromCloud(GDriveHelper gDriveHelper) {
        gDriveHelper.checkDataForSync(tickTrackFirebaseDatabase)
                .addOnSuccessListener(isSuccess -> {
                    System.out.println("SYNC RESTORED DATA");
                    if(isSuccess==1){
                        clearDataSetup(gDriveHelper);
                    }
                }).addOnFailureListener(exception ->
                Log.e("SYNC", "Couldn't read file.", exception));
    }

    private void clearDataSetup(GDriveHelper gDriveHelper) {
        gDriveHelper.clearData().addOnSuccessListener(resultInt -> {
            if(resultInt==1){
                System.out.println("SYNC CLEAR DATA HAPPENED");
                if(tickTrackFirebaseDatabase.getSharedPref(context).getBoolean("counterDataBackup", true)){
                    counterDataBackup(tickTrackDatabase.retrieveCounterList());
                }
                if(tickTrackFirebaseDatabase.getSharedPref(context).getBoolean("timerDataBackup", true)){
                    timerDataBackup(tickTrackDatabase.retrieveTimerList());
                }
                if(tickTrackFirebaseDatabase.getSharedPref(context).getBoolean("preferencesDataBackup", true)){
                    preferencesDataBackup();
                }
            } else if(resultInt==0){
                System.out.println("SYNC EXCEPTION CAUGHT");
            } else {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("SYNC CLEAR DATA FAILED");
                clearDataSetup(gDriveHelper);
            }
        });
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
                            try {
                                Thread.sleep(10000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
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

    //TODO: OLD saveFileCode

//    private void saveFile(String fileID, GDriveHelper gDriveHelper, String jsonContent, String fileName) {
//        if (gDriveHelper != null) {
//            Log.d("TAG", "Saving " + fileID);
//            gDriveHelper.saveFile(fileID, fileName, jsonContent)
//                    .addOnSuccessListener(aVoid -> {
//                        if(aVoid==1){
//                            switch (fileName) {
//                                case "counterBackup.json":
//                                    System.out.println("Counter BACKUP DONE");
//                                    tickTrackFirebaseDatabase.setCounterBackupComplete(true);
//                                    break;
//                                case "timerBackup.json":
//                                    System.out.println("Timer BACKUP DONE");
//                                    tickTrackFirebaseDatabase.setTimerBackupComplete(true);
//                                    break;
//                                case "settingsBackup.json":
//                                    System.out.println("Settings BACKUP DONE");
//                                    tickTrackFirebaseDatabase.setSettingsBackupComplete(true);
//                                    break;
//                            }
//                        } else {
//                            saveFile(fileID, gDriveHelper, jsonContent, fileName);
//                        }
//                    })
//                    .addOnFailureListener(exception ->
//                            Log.e("TAG", "Unable to save file via REST.", exception));
//        }
//    }

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
        }

        tickTrackFirebaseDatabase.setCounterDownloadStatus(1);

    }
    private void mergeCounterData(CounterData counterData) {
        ArrayList<CounterData> counterLocalData = tickTrackDatabase.retrieveCounterList();
        for(int j=0; j<counterLocalData.size(); j++){
            if(counterData.getCounterID().equals(counterLocalData.get(j).getCounterID())){
                if (counterData.getCounterTimestamp() >= counterLocalData.get(j).getCounterTimestamp()) {
                    counterLocalData.get(j).setCounterID(counterData.getCounterID());
                    counterLocalData.get(j).setCounterLabel(counterData.getCounterLabel());
                    counterLocalData.get(j).setCounterValue(counterData.getCounterValue());
                    counterLocalData.get(j).setCounterTimestamp(counterData.getCounterTimestamp());
                    counterLocalData.get(j).setCounterFlag(counterData.getCounterFlag());
                    counterLocalData.get(j).setCounterSignificantCount(counterData.getCounterSignificantCount());
                    counterLocalData.get(j).setCounterSignificantExist(counterData.isCounterSignificantExist());
                    counterLocalData.get(j).setCounterSwipeMode(counterData.isCounterSwipeMode());
                    counterLocalData.get(j).setNegativeAllowed(counterData.isNegativeAllowed());
                    tickTrackDatabase.storeCounterList(counterLocalData);
                }
                return;
            }
        }

        counterLocalData.add(counterData);
        tickTrackDatabase.storeCounterList(counterLocalData);
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
        }
        tickTrackFirebaseDatabase.setTimerDownloadStatus(1);
    }
    private void mergeTimerData(TimerData timerData) {
        ArrayList<TimerData> timerLocalData = tickTrackDatabase.retrieveTimerList();
        for(int j=0; j<timerLocalData.size(); j++){
            if(timerData.getTimerID().equals(timerLocalData.get(j).getTimerID())){
                if (timerData.getTimerLastEdited() >= timerLocalData.get(j).getTimerLastEdited()) {
                    timerLocalData.get(j).setTimerLastEdited(timerData.getTimerLastEdited());
                    timerLocalData.get(j).setTimerFlag(timerData.getTimerFlag());
                    timerLocalData.get(j).setTimerLabel(timerData.getTimerLabel());
                    tickTrackDatabase.storeTimerList(timerLocalData);
                }
                return;
            }
        }

        timerLocalData.add(timerData);
        tickTrackDatabase.storeTimerList(timerLocalData);
    }


    public void restorePreferencesSync() {
        ArrayList<SettingsData> settingsData = tickTrackFirebaseDatabase.retrieveSettingsRestoredData();
        if(settingsData.size()>0){
            if(tickTrackDatabase.getSettingsChangeTime() < settingsData.get(0).getSettingsChangeTime()){
                tickTrackDatabase.setCounterDataBackup(settingsData.get(0).isCounterBackupOn());
                tickTrackDatabase.setTimerDataBackup(settingsData.get(0).isTimerBackupOn());
                tickTrackDatabase.setHapticEnabled(settingsData.get(0).isHapticFeedback());
                tickTrackDatabase.setLastBackupSystemTime(settingsData.get(0).getLastBackupTime());
                tickTrackDatabase.storeSyncFrequency(settingsData.get(0).getSyncDataFrequency());
                tickTrackDatabase.storeScreenSaverClock(settingsData.get(0).getScreensaverClockStyle());
                tickTrackDatabase.setMilestoneVibrate(settingsData.get(0).isMilestoneVibrate());
                tickTrackDatabase.setSumEnabled(settingsData.get(0).isSumDisplayed());
                System.out.println("SYNC INITIALISED PREFERENCES");
            }
        }
        tickTrackFirebaseDatabase.storeSettingsRestoredData(new ArrayList<>());
    }

    public void restoreCounterDataSync() {
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
        }
    }

    public void restoreTimerDataSync() {
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
        }
    }
}
