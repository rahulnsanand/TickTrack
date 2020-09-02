package com.theflopguyproductions.ticktrack;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Pair;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.concurrent.Callable;
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

    public Task<String> createTimerBackup(String filename) {
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
                } catch (IOException e) {
                    e.printStackTrace();
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
                } catch (IOException e) {
                    e.printStackTrace();
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

                return Pair.create(name, contents);
            }
        });
    }


    /**
     * Updates the file identified by {@code fileId} with the given {@code name} and {@code
     * content}.
     */
    public Task<Void> saveFile(String fileId, String name, String content) {
        return Tasks.call(mExecutor, () -> {
            // Create a File containing any metadata changes.
            File metadata = new File().setName(name);

            // Convert content to an AbstractInputStreamContent instance.
            ByteArrayContent contentStream = ByteArrayContent.fromString("text/json", content);

            // Update the metadata and contents.
            mDriveService.files().update(fileId, metadata, contentStream).execute();
            return null;
        });
    }

    /**
     * Returns a {@link FileList} containing all the visible files in the user's My Drive.
     *
     * <p>The returned list will only contain files visible to this app, i.e. those which were
     * created by this app. To perform operations on files not created by the app, the project must
     * request Drive Full Scope in the <a href="https://play.google.com/apps/publish">Google
     * Developer's Console</a> and be submitted to Google for verification.</p>
     */
    public Task<FileList> queryFiles() {
        return Tasks.call(mExecutor, new Callable<FileList>() {
            @Override
            public FileList call() throws Exception {
                return mDriveService.files().list().setSpaces("appDataFolder").execute();
            }
        });
    }

    /**
     * Returns an {@link Intent} for opening the Storage Access Framework file picker.
     */
    public Intent createFilePickerIntent() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/json");

        return intent;
    }

    /**
     * Opens the file at the {@code uri} returned by a Storage Access Framework {@link Intent}
     * created by {@link #createFilePickerIntent()} using the given {@code contentResolver}.
     */
    public Task<Pair<String, String>> openFileUsingStorageAccessFramework(
            ContentResolver contentResolver, Uri uri) {
        return Tasks.call(mExecutor, () -> {
            // Retrieve the document's display name from its metadata.
            String name;
            try (Cursor cursor = contentResolver.query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    name = cursor.getString(nameIndex);
                } else {
                    throw new IOException("Empty cursor returned for file.");
                }
            }

            // Read the document's contents as a String.
            String content;
            try (InputStream is = contentResolver.openInputStream(uri);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                content = stringBuilder.toString();
            }

            return Pair.create(name, content);
        });
    }
}
