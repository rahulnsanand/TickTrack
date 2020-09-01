package com.theflopguyproductions.ticktrack.utils.helpers;

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
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.gson.Gson;
import com.theflopguyproductions.ticktrack.GDriveHelper;
import com.theflopguyproductions.ticktrack.counter.CounterBackupData;
import com.theflopguyproductions.ticktrack.counter.CounterData;
import com.theflopguyproductions.ticktrack.timer.TimerBackupData;
import com.theflopguyproductions.ticktrack.timer.TimerData;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackFirebaseDatabase;

import java.io.IOException;
import java.io.OutputStreamWriter;
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

//
//    public void initStorageFix(){
//        ArrayList<TimerBackupData> timerData = new ArrayList<>();
//        Gson gson = new Gson();
//        String json = gson.toJson(timerData);
//        try {
//            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("timerBackupData.json", Context.MODE_PRIVATE));
//            outputStreamWriter.write(json);
//            outputStreamWriter.close();
//            uploadTimerToStorage();
//        }
//        catch (IOException e) {
//            Log.e("Exception", "File write failed: " + e.toString());
//        }
//        StorageReference storageRef = storage.getReference().child("TickTrackBackups").child("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("TimerData");
//        StorageReference mountainsRef = storageRef.child("timerData.json");
//        File directory = context.getFilesDir();
//        File file = new File(directory, "timerBackupData.json");
//
//        try {
//            InputStream stream = new FileInputStream(file);
//            UploadTask uploadTask = mountainsRef.putStream(stream);
//            uploadTask.addOnFailureListener(exception -> {
//                Toast.makeText(context, "Upload Failed", Toast.LENGTH_SHORT).show();
//            }).addOnSuccessListener(taskSnapshot -> {
//                Toast.makeText(context, "Upload Success", Toast.LENGTH_SHORT).show();
//                initCounterJsonUpload();
//            });
//
//        } catch (FileNotFoundException e) {
//            Log.e("Exception", "File upload failed: " + e.toString());
//        }
//    }
//    public void initCounterJsonUpload(){
//        ArrayList<CounterBackupData> counterData = new ArrayList<>();
//        Gson gson = new Gson();
//        String json = gson.toJson(counterData);
//        try {
//            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("counterBackupData.json", Context.MODE_PRIVATE));
//            outputStreamWriter.write(json);
//            outputStreamWriter.close();
//            uploadTimerToStorage();
//        }
//        catch (IOException e) {
//            Log.e("Exception", "File write failed: " + e.toString());
//        }
//        StorageReference storageRef = storage.getReference().child("TickTrackBackups").child("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("CounterData");
//        StorageReference mountainsRef = storageRef.child("counterData.json");
//        File directory = context.getFilesDir();
//        File file = new File(directory, "counterBackupData.json");
//
//        try {
//            InputStream stream = new FileInputStream(file);
//            UploadTask uploadTask = mountainsRef.putStream(stream);
//            uploadTask.addOnFailureListener(exception -> {
//                Toast.makeText(context, "Upload Failed", Toast.LENGTH_SHORT).show();
//            }).addOnSuccessListener(taskSnapshot -> {
//                Toast.makeText(context, "Upload Success", Toast.LENGTH_SHORT).show();
//            });
//
//        } catch (FileNotFoundException e) {
//            Log.e("Exception", "File upload failed: " + e.toString());
//        }
//    }

    public void timerDataBackup(ArrayList<TimerData> timerData){

        ArrayList<TimerBackupData> timerBackupData = new ArrayList<>();

        for(int i = 0; i<timerData.size(); i++){
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

        if(timerBackupData.size()>0){
            compareTimerList(timerBackupData);
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
                            context, Collections.singleton(DriveScopes.DRIVE_FILE));
            credential.setSelectedAccount(account.getAccount());
            Drive googleDriveService =
                    new Drive.Builder(
                            AndroidHttp.newCompatibleTransport(),
                            new GsonFactory(),
                            credential)
                            .setApplicationName("TicKTrack")
                            .build();

            gDriveHelper = new GDriveHelper(googleDriveService, context);
            gDriveHelper.createTimerBackup("timerBackup.json")
                    .addOnSuccessListener(s -> {
                        readFile(gDriveHelper, s, jsonObject, "timerBackup.json");
                        Toast.makeText(context, "Timer Upload Success", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(context, "Timer Upload Success", Toast.LENGTH_SHORT).show());

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
            compareCounterList(counterBackupData);
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
                            context, Collections.singleton(DriveScopes.DRIVE_FILE));
            credential.setSelectedAccount(account.getAccount());
            Drive googleDriveService =
                    new Drive.Builder(
                            AndroidHttp.newCompatibleTransport(),
                            new GsonFactory(),
                            credential)
                            .setApplicationName("TicKTrack")
                            .build();

            gDriveHelper = new GDriveHelper(googleDriveService, context);
            gDriveHelper.createCounterBackup("counterBackup.json")
                    .addOnSuccessListener(s -> {
                                readFile(gDriveHelper, s, jsonObject, "counterBackup.json");
                                Toast.makeText(context, "Counter Upload Success", Toast.LENGTH_SHORT).show();
                            })
                    .addOnFailureListener(e ->
                            Toast.makeText(context, "Counter Upload Success", Toast.LENGTH_SHORT).show());

        }
    }

    private void readFile(GDriveHelper gDriveHelper, String fileId, String jsonContent, String fileName) {
        if (gDriveHelper != null) {
            Log.d("TAG", "Reading file " + fileId);

            gDriveHelper.readFile(fileId)
                    .addOnSuccessListener(nameAndContent -> {
                        String name = nameAndContent.first;
                        String content = nameAndContent.second;

                        System.out.println(name);
                        System.out.println(content);

                        saveFile(fileId, gDriveHelper, jsonContent, fileName);
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
                        if(fileName.equals("counterBackup.json")){
                            tickTrackFirebaseDatabase.setCounterBackupComplete(true);
                        } else if(fileName.equals("timerBackup.json")){
                            tickTrackFirebaseDatabase.setTimerBackupComplete(true);
                        }
                    })
                    .addOnFailureListener(exception ->
                            Log.e("TAG", "Unable to save file via REST.", exception));
        }
    }

    /**
     * Restoring functions of counter and timer
     */
//    public void restoreCounterData(){
//        StorageReference storageRef = storage.getReference().child("TickTrackBackups").child("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("CounterData");
//        StorageReference counterRef = storageRef.child("counterData.json");
//        File directory = context.getFilesDir();
//        try {
//            File local = File.createTempFile("counterRetrievedBackupData", "json", directory);
//            counterRef.getFile(local).addOnFailureListener(exception -> {
//                int errorCode = ((StorageException) exception).getErrorCode();
//                if(errorCode==StorageException.ERROR_OBJECT_NOT_FOUND){
//                    tickTrackFirebaseDatabase.setCounterDownloadStatus(1);
//                } else {
//                    tickTrackFirebaseDatabase.setCounterDownloadStatus(-1);
//                }
//                Toast.makeText(context, "Counter Download Failed", Toast.LENGTH_SHORT).show();
//            }).addOnSuccessListener(taskSnapshot -> {
//                Toast.makeText(context, "Counter Download Success", Toast.LENGTH_SHORT).show();
//                try {
//                    InputStream is = new FileInputStream(local);
//                    BufferedReader buf = new BufferedReader(new InputStreamReader(is)); String line = buf.readLine();
//                    StringBuilder sb = new StringBuilder();
//                    while(line != null){
//                        sb.append(line);
//                        line = buf.readLine();
//                    }
//                    String fileAsString = sb.toString();
//                    Gson gson = new Gson();
//                    Type type = new TypeToken<ArrayList<CounterBackupData>>() {}.getType();
//                    ArrayList<CounterBackupData> counterBackupData = gson.fromJson(fileAsString, type);
//                    tickTrackFirebaseDatabase.storeBackupCounterList(counterBackupData);
//
//
//                    for(int i=0; i<counterBackupData.size(); i++){
//                        CounterData newCounter = new CounterData();
//                        newCounter.setCounterID(counterBackupData.get(i).getCounterID());
//                        newCounter.setCounterLabel(counterBackupData.get(i).getCounterLabel());
//                        newCounter.setCounterValue(counterBackupData.get(i).getCounterValue());
//                        newCounter.setCounterTimestamp(counterBackupData.get(i).getCounterTimestamp());
//                        newCounter.setCounterFlag(counterBackupData.get(i).getCounterFlag());
//                        newCounter.setCounterSignificantCount(counterBackupData.get(i).getCounterSignificantCount());
//                        newCounter.setCounterSignificantExist(counterBackupData.get(i).isCounterSignificantExist());
//                        newCounter.setCounterSwipeMode(counterBackupData.get(i).isCounterSwipeMode());
//
//                        mergeCounterData(newCounter);
//
//                    }
//                    Toast.makeText(context, "Counter data restored", Toast.LENGTH_SHORT).show();
//
//                    tickTrackFirebaseDatabase.setCounterDownloadStatus(1);
//                } catch (IOException ex) {
//                    //TODO HANDLE EXCEPTION
//                    ex.printStackTrace();
//                }
//            });
//        } catch (IOException e) {
//            //TODO HANDLE EXCEPTION
//            e.printStackTrace();
//        }
//    }
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

//    public void restoreTimerData(){
//        StorageReference storageRef = storage.getReference().child("TickTrackBackups").child("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("TimerData");
//        StorageReference timerRef = storageRef.child("timerData.json");
//
//        File directory = context.getFilesDir();
//        try {
//            File local = File.createTempFile("timerRetrievedBackupData", "json", directory);
//            timerRef.getFile(local).addOnFailureListener(exception -> {
//                int errorCode = ((StorageException) exception).getErrorCode();
//                if(errorCode==StorageException.ERROR_OBJECT_NOT_FOUND){
//                    tickTrackFirebaseDatabase.setTimerDownloadStatus(1);
//                } else {
//                    tickTrackFirebaseDatabase.setTimerDownloadStatus(-1);
//                }
//                Toast.makeText(context, "Download Failed", Toast.LENGTH_SHORT).show();
//            }).addOnSuccessListener(taskSnapshot -> {
//                try {
//                    InputStream is = new FileInputStream(local);
//                    BufferedReader buf = new BufferedReader(new InputStreamReader(is)); String line = buf.readLine();
//                    StringBuilder sb = new StringBuilder();
//                    while(line != null){
//                        sb.append(line);
//                        line = buf.readLine();
//                    }
//                    String fileAsString = sb.toString();
//                    Gson gson = new Gson();
//                    Type type = new TypeToken<ArrayList<TimerBackupData>>() {}.getType();
//                    ArrayList<TimerBackupData> timerBackupData = gson.fromJson(fileAsString, type);
//
//                    for(int i=0; i<timerBackupData.size(); i++){
//                        TimerData newTimer = new TimerData();
//                        newTimer.setTimerID(timerBackupData.get(i).getTimerID());
//                        newTimer.setTimerIntID(timerBackupData.get(i).getTimerIntID());
//                        newTimer.setTimerLastEdited(timerBackupData.get(i).getTimerLastEdited());
//                        newTimer.setTimerFlag(timerBackupData.get(i).getTimerFlag());
//                        newTimer.setTimerHour(timerBackupData.get(i).getTimerHour());
//                        newTimer.setTimerMinute(timerBackupData.get(i).getTimerMinute());
//                        newTimer.setTimerSecond(timerBackupData.get(i).getTimerSecond());
//                        newTimer.setTimerLabel(timerBackupData.get(i).getTimerLabel());
//                        newTimer.setTimerStartTimeInMillis(-1);
//                        newTimer.setTimerTotalTimeInMillis(timerBackupData.get(i).getTimerTotalTimeInMillis());
//                        newTimer.setTimerPause(false);
//                        newTimer.setTimerOn(false);
//
//                        mergeTimerData(newTimer);
//                    }
//                    Toast.makeText(context, "Timer data restored", Toast.LENGTH_SHORT).show();
//                } catch (IOException ex) {
//                    //TODO HANDLE EXCEPTION
//                    ex.printStackTrace();
//                }
//                Toast.makeText(context, "Download Success", Toast.LENGTH_SHORT).show();
//                tickTrackFirebaseDatabase.setTimerDownloadStatus(1);
//            });
//        } catch (IOException e) {
//            //TODO HANDLE EXCEPTION
//            e.printStackTrace();
//        }
//    }
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

    public void readAllFiles() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
        if (account != null) {
            GoogleAccountCredential credential =
                    GoogleAccountCredential.usingOAuth2(
                            context, Collections.singleton(DriveScopes.DRIVE_FILE));
            credential.setSelectedAccount(account.getAccount());
            Drive googleDriveService =
                    new Drive.Builder(
                            AndroidHttp.newCompatibleTransport(),
                            new GsonFactory(),
                            credential)
                            .setApplicationName("TicKTrack")
                            .build();
            FileList files;
            try {
                files = googleDriveService.files().list()
                        .setSpaces("appDataFolder")
                        .setFields("nextPageToken, files(id, name)")
                        .setPageSize(10)
                        .execute();
                for (File file : files.getFiles()) {
                    System.out.printf("Found file: %s (%s)\n",
                            file.getName(), file.getId());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
