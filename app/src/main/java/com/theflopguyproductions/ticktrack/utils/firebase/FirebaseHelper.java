package com.theflopguyproductions.ticktrack.utils.firebase;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.counter.CounterBackupData;
import com.theflopguyproductions.ticktrack.dialogs.ProgressBarDialog;
import com.theflopguyproductions.ticktrack.settings.SettingsActivity;
import com.theflopguyproductions.ticktrack.settings.SettingsData;
import com.theflopguyproductions.ticktrack.startup.StartUpActivity;
import com.theflopguyproductions.ticktrack.timer.data.TimerBackupData;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackFirebaseDatabase;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class FirebaseHelper {

    Context context;
    private static final String TAG = "FIREBASE_HELPER";

     private GoogleSignInClient googleSignInClient;
     private GoogleSignInOptions googleSignInOptions;
    private TickTrackFirebaseDatabase tickTrackFirebaseDatabase;
    private TickTrackDatabase tickTrackDatabase;
    private FirebaseFirestore firebaseFirestore;
    private String action;
    private NotificationCompat.Builder notificationBuilder;
    private NotificationManagerCompat notificationManagerCompat;
    private ProgressBarDialog progressBarDialog;
    private JsonHelper jsonHelper;
     private GDriveHelper gDriveHelper;

    public FirebaseHelper(Context context) {
        this.context = context;

        tickTrackDatabase = new TickTrackDatabase(context);
        tickTrackFirebaseDatabase = new TickTrackFirebaseDatabase(context);
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
//                .requestScopes(new Scope(DriveScopes.DRIVE_APPDATA))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(context, googleSignInOptions);
        jsonHelper = new JsonHelper(context);
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setupNotification(NotificationCompat.Builder notificationBuilder, NotificationManagerCompat notificationManagerCompat) {
        this.notificationBuilder = notificationBuilder;
        this.notificationManagerCompat = notificationManagerCompat;
    }

    private void notifyNotification() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManagerCompat.notify(6, notificationBuilder.build());
    }

    public Intent getSignInIntent(){
        return googleSignInClient.getSignInIntent();
    }

    public void signIn(Task<GoogleSignInAccount> completedTask, Activity activity, String receivedAction) {

        if(InternetChecker.isOnline(context)){
            progressBarDialog = new ProgressBarDialog(activity);
            progressBarDialog.show();
            progressBarDialog.setContentText("Signing in");
            progressBarDialog.titleText.setVisibility(View.GONE);
            try {
                progressBarDialog.dismiss();
                GoogleSignInAccount account = completedTask.getResult(ApiException.class);
                assert account != null;
                tickTrackFirebaseDatabase.storeCurrentUserEmail(account.getEmail());

//                GoogleAccountCredential credential =
//                        GoogleAccountCredential.usingOAuth2(
//                                activity, Collections.singleton(DriveScopes.DRIVE_APPDATA));
//                credential.setSelectedAccount(account.getAccount());
//                Drive googleDriveService =
//                        new Drive.Builder(
//                                AndroidHttp.newCompatibleTransport(),
//                                new GsonFactory(),
//                                credential)
//                                .setApplicationName("TickTrack")
//                                .build();

//                gDriveHelper = new GDriveHelper(googleDriveService, activity);

                Intent intent = new Intent(context, StartUpActivity.class);
                tickTrackDatabase.storeStartUpFragmentID(3);
                intent.setAction(receivedAction);
                context.startActivity(intent);
            } catch (ApiException e) {
                e.printStackTrace();
                progressBarDialog.dismiss();
                Toast.makeText(activity, "Sign in failed, try again", Toast.LENGTH_SHORT).show();
                if(StartUpActivity.ACTION_SETTINGS_ACCOUNT_ADD.equals(receivedAction)){
                    activity.startActivity(new Intent(activity, SettingsActivity.class));
                } else {
                    Intent intent = new Intent(context, StartUpActivity.class);
                    tickTrackDatabase.storeStartUpFragmentID(2);
                    context.startActivity(intent);
                }
            }
        } else {
            Toast.makeText(context, "Kindly Connect To Internet", Toast.LENGTH_SHORT).show();
            if(StartUpActivity.ACTION_SETTINGS_ACCOUNT_ADD.equals(receivedAction)){
                activity.startActivity(new Intent(activity, SettingsActivity.class));
            } else {
                Intent intent = new Intent(context, StartUpActivity.class);
                tickTrackDatabase.storeStartUpFragmentID(2);
                context.startActivity(intent);
            }
        }
    }

    public void restoreInit() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
        if(account!=null){
            firebaseFirestore.collection("TickTrackUsers").document(Objects.requireNonNull(account.getEmail())).get()
                    .addOnSuccessListener(snapshot -> {
                        Map<String, Object> retrieveData;

                        tickTrackFirebaseDatabase.setDriveLinkFail(false);

                        if(snapshot.exists()){
                            retrieveData = snapshot.getData();
                            if (retrieveData != null) {
                                if(Objects.equals(retrieveData.get("isAccountDeleted"), true)){
                                    Toast.makeText(context, "Welcome, "+ account.getDisplayName(), Toast.LENGTH_SHORT).show();
                                    setupInitUserData(account);
                                } else {
                                    Toast.makeText(context, "Welcome back, "+ account.getDisplayName(), Toast.LENGTH_SHORT).show();
                                    checkIfDataExists(account);
                                }
                            }
                        } else {
                            Toast.makeText(context, "Welcome, "+ account.getDisplayName(), Toast.LENGTH_SHORT).show();
                            setupInitUserData(account);
                        }
                        if(tickTrackDatabase.isNewDevice()){
                            deviceInfoUpdate(account);
                            tickTrackDatabase.setNewDevice(false);
                        }
                    }).addOnFailureListener(e -> {

            });
        }
    }

    private void deviceInfoUpdate(GoogleSignInAccount account) {
        firebaseFirestore.collection("TickTrackUsers").document(Objects.requireNonNull(account.getEmail())).collection("Devices").document(Build.MODEL).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if(!queryDocumentSnapshots.exists()){
                        Map<String, Object> deviceMap = new HashMap<>();
                        deviceMap.put("deviceAddMoment", System.currentTimeMillis());
                        deviceMap.put("deviceLocaleCountry", context.getResources().getConfiguration().locale.getCountry());
                        deviceMap.put("deviceLocaleLanguage", context.getResources().getConfiguration().locale.getLanguage());
                        deviceMap.put("deviceManufacturer", Build.MANUFACTURER);
                        deviceMap.put("deviceModel", Build.MODEL);
                        deviceMap.put("deviceSDKVersionInt", Build.VERSION.SDK_INT);
                        deviceMap.put("deviceSDKVersionCode", Build.VERSION.CODENAME);
                        firebaseFirestore.collection("TickTrackUsers").document(Objects.requireNonNull(account.getEmail())).collection("Devices").document(Build.MODEL).set(deviceMap);
                    }
                }).addOnFailureListener(e -> {
        });
    }

    private void setupInitUserData(GoogleSignInAccount account) {

        Map<String, Object> user = new HashMap<>();
        user.put("accountCreateTime", DateFormat.getDateTimeInstance().format(System.currentTimeMillis()));
        user.put("isAccountDeleted", false);
        user.put("accountDeleteTime", -1);
        user.put("isProUser", false);
        user.put("lastBackupTime", tickTrackDatabase.getLastBackupSystemTime());
        user.put("themeMode", tickTrackDatabase.getThemeMode());
        user.put("localeCountry", context.getResources().getConfiguration().locale.getCountry());
        user.put("localeLanguage", context.getResources().getConfiguration().locale.getLanguage());

        firebaseFirestore.collection("TickTrackUsers").document(Objects.requireNonNull(account.getEmail())).get()
                .addOnSuccessListener(documentReference -> {
                    firebaseFirestore.collection("TickTrackUsers").document(account.getEmail()).set(user);
                    completedFragmentTask();
                })
                .addOnFailureListener(e -> {
                    System.out.println("ERROR FIREBASE"+e);
                });

    }
    private void completedFragmentTask() {
        tickTrackFirebaseDatabase.setRestoreInitMode(1);
        if(StartUpActivity.ACTION_SETTINGS_ACCOUNT_ADD.equals(action)){
            Intent intent = new Intent(context, SettingsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else {
            tickTrackDatabase.storeStartUpFragmentID(4);
            Intent intent = new Intent(context, StartUpActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    FirebaseStorage backupStorage = FirebaseStorage.getInstance();
    StorageReference backupStorageReference = backupStorage.getReference();
    private void checkIfDataExists(GoogleSignInAccount account) {
//        GoogleAccountCredential credential =
//                GoogleAccountCredential.usingOAuth2(
//                        context, Collections.singleton(DriveScopes.DRIVE_APPDATA));
//        credential.setSelectedAccount(account.getAccount());
//        Drive googleDriveService =
//                new Drive.Builder(
//                        AndroidHttp.newCompatibleTransport(),
//                        new GsonFactory(),
//                        credential)
//                        .setApplicationName("TickTrack")
//                        .build();
//
//        gDriveHelper = new GDriveHelper(googleDriveService, context);

        retrieveDataInit(gDriveHelper);
    }

    private final Executor mExecutor = Executors.newSingleThreadExecutor();

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

    boolean isSettingsPresent = false, isCounterPresent = false, isTimerPresent = false;
    private void retrieveDataInit(GDriveHelper gDriveHelper) {
        final long TWO_MEGABYTE_LIMIT = 2 * 1024 * 1024;
        backupStorageReference = backupStorageReference
                .child(Objects.requireNonNull(Objects.requireNonNull(GoogleSignIn
                        .getLastSignedInAccount(context)).getEmail()))
                .child("backup");
        backupStorageReference.listAll()
                .addOnSuccessListener(list -> {
                    for (StorageReference item : list.getItems()) {


                        System.out.println(item.getName());

                        switch (item.getName()) {
                            case "counterBackupData.json":
                                isCounterPresent = true;

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
                                            System.out.println("Counter Output : "
                                                    + jsonArrayOutput);
                                            isCounterPresent = false;

                                            tickTrackFirebaseDatabase.storeCounterRestoreString(String.valueOf(jsonArrayOutput));
                                            setupCounterCount(String.valueOf(jsonArrayOutput), tickTrackFirebaseDatabase);
                                        }).addOnFailureListener(exception -> {
                                            // Handle any errors
                                            isCounterPresent = false;
                                        });

                                break;
                            case "timerBackupData.json":
                                isTimerPresent = true;
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
                                            System.out.println("Timer Output : "
                                                    + jsonArrayOutput);
                                            isTimerPresent = false;

                                            tickTrackFirebaseDatabase.storeTimerRestoreString(String.valueOf(jsonArrayOutput));
                                            setupTimerCount(String.valueOf(jsonArrayOutput), tickTrackFirebaseDatabase);

                                        }).addOnFailureListener(exception -> {
                                            // Handle any errors
                                            isTimerPresent = false;
                                        });

                                break;
                            case "settingsBackupData.json":
                                isSettingsPresent = true;
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
                                            System.out.println("Settings Output : "
                                                    + jsonArrayOutput);
                                            isSettingsPresent = false;

                                            setupPreferencesExist(String.valueOf(jsonArrayOutput), tickTrackFirebaseDatabase);

                                        }).addOnFailureListener(exception -> {
                                            // Handle any errors
                                            isSettingsPresent = false;
                                        });

                                break;
                        }
                    }
                    restoreCheckHandler.post(restoreCheckRunnable);
                })
                .addOnFailureListener(System.out::println);



//                .addOnSuccessListener(isSuccess -> {
//                    System.out.println("Success happened on restore INIT <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
//                }).addOnFailureListener(exception ->
//                Log.e("TAG", "Couldn't read file.", exception));

//        gDriveHelper.checkData(tickTrackFirebaseDatabase)
//                .addOnSuccessListener(isSuccess -> {
//                    System.out.println("Success happened on restore INIT <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
//                    if(isSuccess==1){
//                        tickTrackFirebaseDatabase.setRestoreInitMode(1);
//                    } else {
//                        tickTrackFirebaseDatabase.setRestoreInitMode(-1);
//                    }
//                }).addOnFailureListener(exception ->
//                Log.e("TAG", "Couldn't read file.", exception));
    }
    Handler restoreCheckHandler = new Handler(Looper.getMainLooper());
    Runnable restoreCheckRunnable = new Runnable() {
        @Override
        public void run() {
            if(isSettingsPresent || isTimerPresent || isCounterPresent){
                restoreCheckHandler.post(restoreCheckRunnable);
            } else {
                tickTrackFirebaseDatabase.setRestoreInitMode(1);
                restoreCheckHandler.removeCallbacks(restoreCheckRunnable);
            }
        }
    };


    private boolean isCounter = true, isTimer = true, isSettings = true;
    public void backup() {
        isCounter = tickTrackDatabase.getSharedPref(context).getBoolean("counterDataBackup", true);
        isTimer = tickTrackDatabase.getSharedPref(context).getBoolean("timerDataBackup", true);
        isSettings = tickTrackDatabase.getSharedPref(context).getBoolean("preferencesDataBackup", true);
        backupCheckHandler.post(backupCheckRunnable);
        JsonHelper jsonHelper = new JsonHelper(context);
        jsonHelper.createBackup();
    }

    Handler backupCheckHandler = new Handler(Looper.getMainLooper());
    Runnable backupCheckRunnable = new Runnable() {
        @Override
        public void run() {
            if(isCounter && isTimer && isSettings){
                if(tickTrackFirebaseDatabase.isCounterBackupComplete() && tickTrackFirebaseDatabase.isTimerBackupComplete() && tickTrackFirebaseDatabase.isSettingsBackupComplete()){
                    tickTrackFirebaseDatabase.setBackupMode(false);
                    tickTrackFirebaseDatabase.setBackupFailedMode(false);
                    backupCheckHandler.removeCallbacks(backupCheckRunnable);
                } else {
                    backupCheckHandler.removeCallbacks(backupCheckRunnable);
                    backupCheckHandler.post(backupCheckRunnable);
                }
            } else if (isTimer && isSettings){
                if(tickTrackFirebaseDatabase.isTimerBackupComplete() && tickTrackFirebaseDatabase.isSettingsBackupComplete()){
                    tickTrackFirebaseDatabase.setBackupMode(false);
                    tickTrackFirebaseDatabase.setBackupFailedMode(false);
                    backupCheckHandler.removeCallbacks(backupCheckRunnable);
                } else {
                    backupCheckHandler.removeCallbacks(backupCheckRunnable);
                    backupCheckHandler.post(backupCheckRunnable);
                }
            } else if (isCounter && isSettings){
                if(tickTrackFirebaseDatabase.isCounterBackupComplete() && tickTrackFirebaseDatabase.isSettingsBackupComplete()){
                    tickTrackFirebaseDatabase.setBackupMode(false);
                    tickTrackFirebaseDatabase.setBackupFailedMode(false);
                    backupCheckHandler.removeCallbacks(backupCheckRunnable);
                } else {
                    backupCheckHandler.removeCallbacks(backupCheckRunnable);
                    backupCheckHandler.post(backupCheckRunnable);
                }
            } else if (isSettings){
                if(tickTrackFirebaseDatabase.isSettingsBackupComplete()){
                    tickTrackFirebaseDatabase.setBackupMode(false);
                    tickTrackFirebaseDatabase.setBackupFailedMode(false);
                    backupCheckHandler.removeCallbacks(backupCheckRunnable);
                } else {
                    backupCheckHandler.removeCallbacks(backupCheckRunnable);
                    backupCheckHandler.post(backupCheckRunnable);
                }
            } else if (isTimer){
                if(tickTrackFirebaseDatabase.isTimerBackupComplete()){
                    tickTrackFirebaseDatabase.setBackupMode(false);
                    tickTrackFirebaseDatabase.setBackupFailedMode(false);
                    backupCheckHandler.removeCallbacks(backupCheckRunnable);
                } else {
                    backupCheckHandler.post(backupCheckRunnable);
                }
            } else if (isCounter){
                if(tickTrackFirebaseDatabase.isCounterBackupComplete()){
                    tickTrackFirebaseDatabase.setBackupMode(false);
                    tickTrackFirebaseDatabase.setBackupFailedMode(false);
                    backupCheckHandler.removeCallbacks(backupCheckRunnable);
                } else {
                    backupCheckHandler.removeCallbacks(backupCheckRunnable);
                    backupCheckHandler.post(backupCheckRunnable);
                }
            }

            if (tickTrackFirebaseDatabase.isBackupFailedMode()){
                backupCheckHandler.removeCallbacks(backupCheckRunnable);
            }
        }
    };

    public void stopHandler() {
        backupCheckHandler.removeCallbacks(backupCheckRunnable);
    }

    public void signOut(Activity activity) {
        progressBarDialog = new ProgressBarDialog(activity);
        progressBarDialog.show();
        progressBarDialog.setContentText("Signing out");
        progressBarDialog.titleText.setVisibility(View.GONE);
        googleSignInClient.signOut().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                tickTrackDatabase.setNewDevice(true);
                tickTrackFirebaseDatabase.cancelBackUpAlarm();
                tickTrackFirebaseDatabase.storeCurrentUserEmail(null);
                tickTrackFirebaseDatabase.setRestoreInitMode(0);
                tickTrackFirebaseDatabase.setRestoreMode(false);
                tickTrackFirebaseDatabase.setBackupMode(false);
                tickTrackFirebaseDatabase.setBackupFailedMode(false);
                tickTrackFirebaseDatabase.storeBackupCounterList(new ArrayList<>());
                tickTrackFirebaseDatabase.storeBackupTimerList(new ArrayList<>());
                tickTrackDatabase.setNewDevice(false);
                tickTrackDatabase.storeBackupHour(-1);
                tickTrackDatabase.storeBackupMinute(-1);
            } else {
                Toast.makeText(activity, "Not signed out, try again", Toast.LENGTH_SHORT).show();
            }
            progressBarDialog.dismiss();
        });
    }
    public void switchAccount(Activity activity) {
        progressBarDialog = new ProgressBarDialog(activity);
        progressBarDialog.show();
        progressBarDialog.setContentText("Signing out");
        progressBarDialog.titleText.setVisibility(View.GONE);
        googleSignInClient.signOut().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                tickTrackDatabase.setNewDevice(true);
                tickTrackFirebaseDatabase.cancelBackUpAlarm();
                tickTrackFirebaseDatabase.storeCurrentUserEmail(null);
                tickTrackFirebaseDatabase.setRestoreInitMode(0);
                tickTrackFirebaseDatabase.setRestoreMode(false);
                tickTrackFirebaseDatabase.setBackupMode(false);
                tickTrackFirebaseDatabase.setBackupFailedMode(false);
                tickTrackFirebaseDatabase.storeBackupCounterList(new ArrayList<>());
                tickTrackFirebaseDatabase.storeBackupTimerList(new ArrayList<>());
                tickTrackDatabase.storeBackupHour(-1);
                tickTrackDatabase.storeBackupMinute(-1);
                tickTrackDatabase.setNewDevice(false);
                Toast.makeText(activity, "Signed out", Toast.LENGTH_SHORT).show();
                tickTrackDatabase.storeStartUpFragmentID(2);
                Intent startUpSignInIntent = new Intent(activity, StartUpActivity.class);
                startUpSignInIntent.setAction(StartUpActivity.ACTION_SETTINGS_ACCOUNT_ADD);
                activity.startActivity(startUpSignInIntent);
            } else {
                Toast.makeText(activity, "Not signed out, try again", Toast.LENGTH_SHORT).show();
            }
            progressBarDialog.dismiss();
        });
    }
    public boolean isUserSignedIn(){
        return GoogleSignIn.getLastSignedInAccount(context) != null;
    }

    public void deleteBackup(Activity activity){
        progressBarDialog = new ProgressBarDialog(activity);
        progressBarDialog.show();
        progressBarDialog.setContentText("Deleting backup");
        progressBarDialog.titleText.setVisibility(View.GONE);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
        if(account!=null){
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

            clearData(gDriveHelper, progressBarDialog);

        }
    }


    private void clearData(GDriveHelper gDriveHelper, ProgressBarDialog progressBarDialog) {

        UploadTask uploadTask;
        AssetManager am = context.getAssets();
        try {
            InputStream is = am.open("images/demo.webp");
            uploadTask = backupStorageReference.child("demo.webp").putStream(is);
            uploadTask
                    .addOnSuccessListener(taskSnapshot -> {
                        backupStorageReference = backupStorage.getReference();

                        backupStorageReference = backupStorageReference
                                .child(Objects.requireNonNull(Objects.requireNonNull(GoogleSignIn
                                        .getLastSignedInAccount(context)).getEmail()))
                                .child("backup");

                        backupStorageReference.listAll()
                                .addOnSuccessListener(list -> {
                                    for (StorageReference item : list.getItems()) {

                                        System.out.println(item.getName());

                                        switch (item.getName()) {
                                            case "counterBackupData.json":
                                                isCounterDeleteFilePresent = true;
                                                backupStorageReference.child("counterBackupData.json")
                                                        .delete().addOnSuccessListener(bytes -> {
                                                            // Data for "images/island.jpg" is returns, use this as needed
                                                            isCounterDeleteFilePresent = false;

                                                        }).addOnFailureListener(exception -> {
                                                            // Try once more
                                                            backupStorageReference.child("counterBackupData.json")
                                                                    .delete().addOnSuccessListener(bytes -> {
                                                                        // Data for "images/island.jpg" is returns, use this as needed
                                                                        isCounterDeleteFilePresent = false;

                                                                    }).addOnFailureListener(exception1 -> {
                                                                        // Screw it
                                                                        isCounterDeleteFilePresent = false;
                                                                    });
                                                        });

                                                break;
                                            case "timerBackupData.json":
                                                isTimerDeleteFilePresent = true;
                                                backupStorageReference.child("timerBackupData.json")
                                                        .delete().addOnSuccessListener(bytes -> {
                                                            // Data for "images/island.jpg" is returns, use this as needed
                                                            isTimerDeleteFilePresent = false;

                                                        }).addOnFailureListener(exception -> {
                                                            // Handle any errors
                                                            backupStorageReference.child("timerBackupData.json")
                                                                    .delete().addOnSuccessListener(bytes -> {
                                                                        // Data for "images/island.jpg" is returns, use this as needed
                                                                        isTimerDeleteFilePresent = false;

                                                                    }).addOnFailureListener(exception1 -> {
                                                                        // Handle any errors
                                                                        isTimerDeleteFilePresent = false;
                                                                    });
                                                        });

                                                break;
                                            case "settingsBackupData.json":
                                                isSettingsDeleteFilePresent = true;
                                                backupStorageReference.child("settingsBackupData.json")
                                                        .delete().addOnSuccessListener(bytes -> {
                                                            // Data for "images/island.jpg" is returns, use this as needed
                                                            isSettingsDeleteFilePresent = false;

                                                        }).addOnFailureListener(exception -> {
                                                            // Handle any errors
                                                            backupStorageReference.child("settingsBackupData.json")
                                                                    .delete().addOnSuccessListener(bytes -> {
                                                                        // Data for "images/island.jpg" is returns, use this as needed
                                                                        isSettingsDeleteFilePresent = false;

                                                                    }).addOnFailureListener(exception1 -> {
                                                                        // Handle any errors
                                                                        isSettingsDeleteFilePresent = false;
                                                                    });

                                                        });

                                                break;
                                        }
                                    }

                                    deleteResetCheckHandler.post(deleteResetCheckRunnable);

                                })
                                .addOnFailureListener(System.out::println);



                    })
                    .addOnFailureListener(taskSnapshot -> System.out.println("FAILED!<<<<<<<<<<<<<<<<<<<<"));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

//        gDriveHelper.clearData().addOnSuccessListener(integer -> {
//            if (integer == 1) {
//                System.out.println("CLEAR DATA HAPPENED");
//                progressBarDialog.dismiss();
//            } else if (integer == 0) {
//                System.out.println("EXCEPTION CAUGHT");
//            } else {
//                System.out.println("CLEAR DATA FAILED");
//                try {
//                    Thread.sleep(10000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                clearData(gDriveHelper, progressBarDialog);
//            }
//        });
    }

    boolean isSettingsDeleteFilePresent = false, isTimerDeleteFilePresent = false, isCounterDeleteFilePresent = false;
    Handler deleteResetCheckHandler = new Handler(Looper.getMainLooper());
    Runnable deleteResetCheckRunnable = new Runnable() {
        @Override
        public void run() {
            if(isSettingsDeleteFilePresent || isTimerDeleteFilePresent || isCounterDeleteFilePresent){
                deleteResetCheckHandler.post(deleteResetCheckRunnable);
            } else {
                progressBarDialog.dismiss();
                deleteResetCheckHandler.removeCallbacks(deleteResetCheckRunnable);
            }
        }
    };

    public void stopDeleteResetHandler() {
        deleteResetCheckHandler.removeCallbacks(deleteResetCheckRunnable);
    }
}
