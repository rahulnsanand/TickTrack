package com.theflopguyproductions.ticktrack.utils.firebase;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Pair;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAuthIOException;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.theflopguyproductions.ticktrack.counter.CounterBackupData;
import com.theflopguyproductions.ticktrack.service.BackupRestoreService;
import com.theflopguyproductions.ticktrack.settings.SettingsData;
import com.theflopguyproductions.ticktrack.timer.data.TimerBackupData;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackFirebaseDatabase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class GDriveHelper {
    private final Executor mExecutor = Executors.newSingleThreadExecutor();
    private final Drive mDriveService;
    private final Context context;

    public GDriveHelper(Drive driveService, Context context) {
        mDriveService = driveService;
        this.context = context;
    }

    public Task<Pair<Integer, String>> createTimerBackup(String filename) {
        return Tasks.call(mExecutor, () -> {

            if(InternetChecker.isOnline(context)){
                File fileMetadata = new File();
                fileMetadata.setName(filename);
                fileMetadata.setParents(Collections.singletonList("appDataFolder"));

                java.io.File directory = context.getFilesDir();
                java.io.File file = new java.io.File(directory, "timerBackupData.json");
                FileContent mediaContent = new FileContent("application/json", file);

                File uploadFile = new File();
                try {
                    uploadFile = mDriveService.files().create(fileMetadata, mediaContent)
                            .setFields("id")
                            .execute();
                    System.out.println("File ID: " + uploadFile.getId());
                    return Pair.create(1, uploadFile.getId());
                } catch (GoogleJsonResponseException e){
                    System.out.println("EXCEPTION createTimerBackup "+e);
                } catch (IOException e) {
                    System.out.println("An error occurred: " + e);
                    exceptionHandler();
                }
            } else {
                return Pair.create(-1, null);
            }
            return Pair.create(0, null);
        });
    }
    public Task<Pair<Integer, String>> createCounterBackup(String filename) {
        return Tasks.call(mExecutor, () -> {
            if(InternetChecker.isOnline(context)){
                File fileMetadata = new File();
                fileMetadata.setName(filename);
                fileMetadata.setParents(Collections.singletonList("appDataFolder"));

                java.io.File directory = context.getFilesDir();
                java.io.File file = new java.io.File(directory, "counterBackupData.json");
                FileContent mediaContent = new FileContent("application/json", file);

                File uploadFile = new File();
                try {
                    uploadFile = mDriveService.files().create(fileMetadata, mediaContent)
                            .setFields("id")
                            .execute();
                    System.out.println("File ID: " + uploadFile.getId());
                    return Pair.create(1, uploadFile.getId());
                } catch (GoogleJsonResponseException e){
                    System.out.println("EXCEPTION createCounterBackup "+e);
                } catch (IOException e) {
                    System.out.println("An error occurred: " + e);
                    exceptionHandler();
                }
            } else {
                return Pair.create(-1, null);
            }
            return Pair.create(0, null);
        });
    }
    public Task<Pair<Integer, String>> createSettingsBackup(String filename) {
        return Tasks.call(mExecutor, () -> {
            if(InternetChecker.isOnline(context)){
                File fileMetadata = new File();
                fileMetadata.setName(filename);
                fileMetadata.setParents(Collections.singletonList("appDataFolder"));

                java.io.File directory = context.getFilesDir();
                java.io.File file = new java.io.File(directory, "settingsBackupData.json");
                FileContent mediaContent = new FileContent("application/json", file);

                File uploadFile = new File();
                try {
                    uploadFile = mDriveService.files().create(fileMetadata, mediaContent)
                            .setFields("id")
                            .execute();
                    System.out.println("File ID: " + uploadFile.getId());
                    return Pair.create(1, uploadFile.getId());
                } catch (GoogleJsonResponseException e){
                    System.out.println("EXCEPTION createSettingsBackup "+e);
                } catch (IOException e) {
                    System.out.println("An error occurred: " + e);
                    exceptionHandler();
                }
            } else {
                return Pair.create(-1, null);
            }
            return Pair.create(0, null);
        });
    }

    /**
     * Opens the file identified by {@code fileId} and returns a {@link Pair} of its name and
     * contents.
     */
    public Task<Pair<String, String>> readFile(String fileId) {
        return Tasks.call(mExecutor, () -> {
            if(InternetChecker.isOnline(context)){
                // Retrieve the metadata as a File object.
                File metadata = mDriveService.files().get(fileId).execute();
                String name = metadata.getName();

                // Stream the file contents to a String.
                try (InputStream is = mDriveService.files().get(fileId).executeMediaAsInputStream();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    String contents = stringBuilder.toString();
                    System.out.println(contents);
                    return Pair.create(name, contents);
                } catch (GoogleJsonResponseException e){
                    e.printStackTrace();
                    System.out.println("EXCEPTION GOOGLE JSON RESPONSE "+e);
                    return Pair.create(null, null);
                } catch (IOException e){
                    e.printStackTrace();
                    return Pair.create(null, null);
                }
            } else {
                return Pair.create(null, null);
            }
        });
    }

    public Task<Integer> saveFile(String fileId, String name, String content) {
        return Tasks.call(mExecutor, () -> {
            if(InternetChecker.isOnline(context)){
                // Create a File containing any metadata changes.
                File metadata = new File().setName(name);

                // Convert content to an AbstractInputStreamContent instance.
                ByteArrayContent contentStream = ByteArrayContent.fromString("text/json", content);

                // Update the metadata and contents.
                try{
                    mDriveService.files().update(fileId, metadata, contentStream).execute();
                } catch (GoogleJsonResponseException e){
                    e.printStackTrace();
                    System.out.println("EXCEPTION GOOGLE JSON saveFile"+e);
                } catch (IOException e){
                    e.printStackTrace();
                    exceptionHandler();
                }

                return 1;
            } else {
                return -1;
            }
        });
    }
    public Task<Void> readAllFiles() {
        return Tasks.call(mExecutor, () -> {
                FileList files;
                try {
                    files = mDriveService.files().list()
                            .setSpaces("appDataFolder")
                            .setFields("nextPageToken, files(id, name)")
                            .setPageSize(10)
                            .execute();
                    for (File file : files.getFiles()) {
                        System.out.printf("Found file: %s (%s)\n",
                                file.getName(), file.getId());
                        readFile(file.getId());
                    }
                } catch (GoogleJsonResponseException e){
                    System.out.println("EXCEPTION readAllFiles "+e);
                } catch (IOException e) {
                    System.out.println("An error occurred: " + e);
                    exceptionHandler();
                }
            return null;
        });
    }

    public Task<Integer> clearData() {
        return Tasks.call(mExecutor, () -> {
            if(InternetChecker.isOnline(context)){
                try {
                    FileList files;
                    files = mDriveService.files().list()
                            .setSpaces("appDataFolder")
                            .setFields("nextPageToken, files(id, name)")
                            .setPageSize(10)
                            .execute();
                    for (File file : files.getFiles()) {
                        System.out.println(file.getName()+" FILE DELETED");
                        mDriveService.files().delete(file.getId()).execute();
                    }
                    return 1;
                } catch (GoogleJsonResponseException e){
                    System.out.println("EXCEPTION clearData "+e);
                } catch (GoogleAuthIOException e) {
                    System.out.println("An error occurred: " + e);
                    exceptionHandler();
                }
            } else {
                return -1;
            }
            return 0;
        });
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

    private void exceptionHandler() {
        new TickTrackFirebaseDatabase(context).cancelBackUpAlarm();
        new TickTrackFirebaseDatabase(context).setDriveLinkFail(true);
        if(isMyServiceRunning(BackupRestoreService.class, context)){
            stopBackupService();
        }
    }

    private void stopBackupService() {
        Intent intent = new Intent(context, BackupRestoreService.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setAction(BackupRestoreService.RESTORE_SERVICE_STOP_FOREGROUND);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    public Task<Integer> checkDataForSync(TickTrackFirebaseDatabase tickTrackFirebaseDatabase) {

        return Tasks.call(mExecutor, () -> {
            FileList files;
            String contents;
            try {
                files = mDriveService.files().list()
                        .setSpaces("appDataFolder")
                        .setFields("nextPageToken, files(id, name)")
                        .setPageSize(10)
                        .execute();
                for (File file : files.getFiles()) {
                    System.out.printf("Found file: %s (%s)\n",
                            file.getName(), file.getId());
                    try (InputStream is = mDriveService.files().get(file.getId()).executeMediaAsInputStream();
                         BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                        StringBuilder stringBuilder = new StringBuilder();
                        String line;

                        while ((line = reader.readLine()) != null) {
                            stringBuilder.append(line);
                        }
                        contents = stringBuilder.toString();

                        JsonHelper jsonHelper = new JsonHelper(context);
                        switch (file.getName()) {
                            case "counterBackup.json":
                                tickTrackFirebaseDatabase.storeCounterRestoreString(contents);
                                jsonHelper.restoreCounterDataSync();
                                break;
                            case "timerBackup.json":
                                tickTrackFirebaseDatabase.storeTimerRestoreString(contents);
                                jsonHelper.restoreTimerDataSync();
                                break;
                            case "settingsBackup.json":
                                setupPreferencesForSync(contents, tickTrackFirebaseDatabase);
                                jsonHelper.restorePreferencesSync();
                                break;
                        }

                    } catch (GoogleJsonResponseException e){
                        System.out.println("EXCEPTION checkDataForSync "+e);
                    } catch (IOException e) {
                        System.out.println("An error occurred: " + e);
                        exceptionHandler();
                    }
                }
                return 1;
            } catch (GoogleJsonResponseException e){
                System.out.println("EXCEPTION checkDataForSync "+e);
            } catch (IOException e) {
                System.out.println("An error occurred: " + e);
                exceptionHandler();
            }
            return 0;
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

    public Task<Integer> checkData(TickTrackFirebaseDatabase tickTrackFirebaseDatabase) {

        return Tasks.call(mExecutor, () -> {
            FileList files;
            String contents;
            try {
                files = mDriveService.files().list()
                        .setSpaces("appDataFolder")
                        .setFields("nextPageToken, files(id, name)")
                        .setPageSize(10)
                        .execute();
                for (File file : files.getFiles()) {
                    System.out.printf("Found file: %s (%s)\n",
                            file.getName(), file.getId());
                    try (InputStream is = mDriveService.files().get(file.getId()).executeMediaAsInputStream();
                         BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                        StringBuilder stringBuilder = new StringBuilder();
                        String line;

                        while ((line = reader.readLine()) != null) {
                            stringBuilder.append(line);
                        }
                        contents = stringBuilder.toString();
                        switch (file.getName()) {
                            case "counterBackup.json":
                                tickTrackFirebaseDatabase.storeCounterRestoreString(contents);
                                setupCounterCount(contents, tickTrackFirebaseDatabase);
                                break;
                            case "timerBackup.json":
                                tickTrackFirebaseDatabase.storeTimerRestoreString(contents);
                                setupTimerCount(contents, tickTrackFirebaseDatabase);
                                break;
                            case "settingsBackup.json":
                                setupPreferencesExist(contents, tickTrackFirebaseDatabase);
                                break;
                        }
                    } catch (GoogleJsonResponseException e){
                        System.out.println("EXCEPTION checkData "+e);
                    }  catch (IOException e) {
                        System.out.println("An error occurred: " + e);
                        exceptionHandler();
                    }
                }
                return 1;
            } catch (GoogleJsonResponseException e){
                System.out.println("EXCEPTION checkData "+e);
            }  catch (IOException e) {
                System.out.println("An error occurred: " + e);
                exceptionHandler();
            }
            return 0;
        });
    }

    private void setupPreferencesExist(String contents, TickTrackFirebaseDatabase tickTrackFirebaseDatabase) {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<SettingsData>>() {}.getType();
        ArrayList<SettingsData> settingsData = gson.fromJson(contents, type);
        if(settingsData == null){
            settingsData = new ArrayList<>();
        }
        tickTrackFirebaseDatabase.storeSettingsRestoredData(settingsData);
        tickTrackFirebaseDatabase.foundPreferencesDataBackup(true);
        tickTrackFirebaseDatabase.storeRetrievedLastBackupTime(settingsData.get(0).getLastBackupTime());
    }
    private void setupTimerCount(String contents, TickTrackFirebaseDatabase tickTrackFirebaseDatabase) {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<TimerBackupData>>() {}.getType();
        ArrayList<TimerBackupData> timerBackupData = gson.fromJson(contents, type);
        if(timerBackupData == null){
            timerBackupData = new ArrayList<>();
        }
        tickTrackFirebaseDatabase.storeRetrievedTimerCount(timerBackupData.size());
    }
    private void setupCounterCount(String contents, TickTrackFirebaseDatabase tickTrackFirebaseDatabase) {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<CounterBackupData>>() {}.getType();
        ArrayList<CounterBackupData> counterBackupData = gson.fromJson(contents, type);
        if(counterBackupData == null){
            counterBackupData = new ArrayList<>();
        }
        tickTrackFirebaseDatabase.storeRetrievedCounterCount(counterBackupData.size());
    }

}
