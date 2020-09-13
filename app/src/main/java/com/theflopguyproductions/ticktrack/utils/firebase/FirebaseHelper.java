package com.theflopguyproductions.ticktrack.utils.firebase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.firebase.firestore.FirebaseFirestore;
import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.dialogs.ProgressBarDialog;
import com.theflopguyproductions.ticktrack.service.BackupRestoreService;
import com.theflopguyproductions.ticktrack.settings.SettingsActivity;
import com.theflopguyproductions.ticktrack.settings.SettingsData;
import com.theflopguyproductions.ticktrack.startup.StartUpActivity;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackFirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
                .requestScopes(new Scope(DriveScopes.DRIVE_APPDATA))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(context, googleSignInOptions);
        jsonHelper = new JsonHelper(context);
        firebaseFirestore = FirebaseFirestore.getInstance();
    }
    public void setAction(String action){
        this.action = action;
    }
    public void setupNotification(NotificationCompat.Builder notificationBuilder, NotificationManagerCompat notificationManagerCompat){
        this.notificationBuilder = notificationBuilder;
        this.notificationManagerCompat = notificationManagerCompat;
    }
    private void notifyNotification() {
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

                GoogleAccountCredential credential =
                        GoogleAccountCredential.usingOAuth2(
                                activity, Collections.singleton(DriveScopes.DRIVE_APPDATA));
                credential.setSelectedAccount(account.getAccount());
                Drive googleDriveService =
                        new Drive.Builder(
                                AndroidHttp.newCompatibleTransport(),
                                new GsonFactory(),
                                credential)
                                .setApplicationName("TickTrack")
                                .build();

                gDriveHelper = new GDriveHelper(googleDriveService, activity);

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
        tickTrackFirebaseDatabase.setRestoreInitMode(-1);
        notificationBuilder.setContentTitle("Signing in TickTrack Account");
        notificationBuilder.setContentText("In progress");
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
        if(account!=null){
            firebaseFirestore.collection("TickTrackUsers").document(Objects.requireNonNull(account.getEmail())).get()
                    .addOnSuccessListener(snapshot -> {
                        Map<String, Object> retrieveData;
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
                                tickTrackFirebaseDatabase.setDriveLinkFail(false);
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
                        firebaseFirestore.collection("TickTrackUsers").document(Objects.requireNonNull(account.getEmail())).collection("Devices").document(Build.MODEL).get()
                                .addOnSuccessListener(documentReference -> {
                                    firebaseFirestore.collection("TickTrackUsers").document(Objects.requireNonNull(account.getEmail())).collection("Devices").document(Build.MODEL).set(deviceMap);
                                    completedFragmentTask();
                                })
                                .addOnFailureListener(e -> {
                                    System.out.println("ERROR FIREBASE"+e);
                                });
                    }
                }).addOnFailureListener(e -> {
        });
    }

    private void setupInitUserData(GoogleSignInAccount account) {
        Map<String, Object> user = new HashMap<>();
        user.put("accountCreateTime", System.currentTimeMillis());
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
        stopRestoreService();
    }
    private void stopRestoreService() {
        Intent intent = new Intent(context, BackupRestoreService.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setAction(BackupRestoreService.RESTORE_SERVICE_STOP_FOREGROUND);
        intent.putExtra("receivedAction", action);
        context.startService(intent);
    }

    private void checkIfDataExists(GoogleSignInAccount account) {

        notificationBuilder.setContentTitle("Fetching TickTrack backup details");
        notificationBuilder.setContentText("In progress");
        notifyNotification();

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

        retrieveDataInit(gDriveHelper);

    }

    private void retrieveDataInit(GDriveHelper gDriveHelper) {
        gDriveHelper.checkData(tickTrackFirebaseDatabase)
                .addOnSuccessListener(isSuccess -> {
                    System.out.println("Success happened on restore INIT <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
                    if(isSuccess==1){
                        tickTrackFirebaseDatabase.setRestoreInitMode(1);
                    } else if(isSuccess==-1) {
                        retrieveDataInit(gDriveHelper);
                    }
                }).addOnFailureListener(exception ->
                Log.e("TAG", "Couldn't read file.", exception));
    }

    private void initPreferences() {
        ArrayList<SettingsData> settingsData = tickTrackFirebaseDatabase.retrieveSettingsRestoredData();
        tickTrackDatabase.setThemeMode(settingsData.get(0).getThemeMode());
        tickTrackDatabase.setCounterDataBackup(settingsData.get(0).isCounterBackupOn());
        tickTrackDatabase.setTimerDataBackup(settingsData.get(0).isTimerBackupOn());
        tickTrackDatabase.setHapticEnabled(settingsData.get(0).isHapticFeedback());
        tickTrackDatabase.setLastBackupSystemTime(settingsData.get(0).getLastBackupTime());
        tickTrackDatabase.storeSyncFrequency(settingsData.get(0).getSyncDataFrequency());
        System.out.println("INITIALISED PREFERENCES");
        tickTrackFirebaseDatabase.storeSettingsRestoredData(new ArrayList<>());
    }

    public void backup() {
        backupCheckHandler.post(backupCheckRunnable);
        JsonHelper jsonHelper = new JsonHelper(context);
        jsonHelper.createBackup();
    }

    Handler backupCheckHandler = new Handler();
    Runnable backupCheckRunnable = new Runnable() {
        @Override
        public void run() {
            if(tickTrackFirebaseDatabase.isCounterBackupComplete() && tickTrackFirebaseDatabase.isTimerBackupComplete() && tickTrackFirebaseDatabase.isSettingsBackupComplete()){
                tickTrackFirebaseDatabase.setBackupMode(false);
                backupCheckHandler.removeCallbacks(backupCheckRunnable);
            } else {
                backupCheckHandler.post(backupCheckRunnable);
            }
        }
    };

    public void restore() {
        notificationBuilder.setContentTitle("Restoring TickTrack Data");
        notificationBuilder.setContentText("In progress");
        restoreCheckHandler.post(restoreCheckRunnable);
        initPreferences();
        jsonHelper.restoreCounterData();
        jsonHelper.restoreTimerData();
    }

    Handler restoreCheckHandler = new Handler();
    Runnable restoreCheckRunnable = new Runnable() {
        @Override
        public void run() {
            if(tickTrackFirebaseDatabase.getCounterDownloadStatus()==1 && tickTrackFirebaseDatabase.getTimerDownloadStatus()==1){
                tickTrackFirebaseDatabase.setRestoreCompleteStatus(1);
                restoreCheckHandler.removeCallbacks(restoreCheckRunnable);
            } else if(tickTrackFirebaseDatabase.getCounterDownloadStatus()==-1 || tickTrackFirebaseDatabase.getTimerDownloadStatus()==-1) {
                //TODO HANDLE ERROR
                tickTrackFirebaseDatabase.setRestoreCompleteStatus(-1);
                restoreCheckHandler.removeCallbacks(restoreCheckRunnable);
            } else {
                restoreCheckHandler.post(restoreCheckRunnable);
            }
        }
    };

    public void signOut(Activity activity) {
        progressBarDialog = new ProgressBarDialog(activity);
        progressBarDialog.show();
        progressBarDialog.setContentText("Signing out");
        progressBarDialog.titleText.setVisibility(View.GONE);
        googleSignInClient.signOut().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                tickTrackFirebaseDatabase.cancelBackUpAlarm();
                tickTrackFirebaseDatabase.storeCurrentUserEmail(null);
                tickTrackFirebaseDatabase.setRestoreInitMode(0);
                tickTrackFirebaseDatabase.setRestoreMode(false);
                tickTrackFirebaseDatabase.setBackupMode(false);
                tickTrackFirebaseDatabase.storeBackupCounterList(new ArrayList<>());
                tickTrackFirebaseDatabase.storeBackupTimerList(new ArrayList<>());
                tickTrackDatabase.setNewDevice(false);
                Toast.makeText(activity, "Signed out", Toast.LENGTH_SHORT).show();
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
                tickTrackFirebaseDatabase.cancelBackUpAlarm();
                tickTrackFirebaseDatabase.storeCurrentUserEmail(null);
                tickTrackFirebaseDatabase.setRestoreInitMode(0);
                tickTrackFirebaseDatabase.setRestoreMode(false);
                tickTrackFirebaseDatabase.setBackupMode(false);
                tickTrackFirebaseDatabase.storeBackupCounterList(new ArrayList<>());
                tickTrackFirebaseDatabase.storeBackupTimerList(new ArrayList<>());
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

            clearData(gDriveHelper, progressBarDialog);

        }
    }

    private void clearData(GDriveHelper gDriveHelper, ProgressBarDialog progressBarDialog) {
        gDriveHelper.clearData().addOnSuccessListener(integer -> {
            if (integer == 1) {
                System.out.println("CLEAR DATA HAPPENED");
                progressBarDialog.dismiss();
            } else if (integer == 0) {
                System.out.println("EXCEPTION CAUGHT");
            } else {
                System.out.println("CLEAR DATA FAILED");
                clearData(gDriveHelper, progressBarDialog);
            }
        }).addOnFailureListener(e -> Toast.makeText(context, "Deletion Error", Toast.LENGTH_SHORT).show());
    }

}
