package com.theflopguyproductions.ticktrack.utils.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.counter.CounterData;
import com.theflopguyproductions.ticktrack.dialogs.ProgressBarDialog;
import com.theflopguyproductions.ticktrack.service.BackupRestoreService;
import com.theflopguyproductions.ticktrack.settings.SettingsActivity;
import com.theflopguyproductions.ticktrack.startup.StartUpActivity;
import com.theflopguyproductions.ticktrack.timer.TimerData;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackFirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FirebaseHelper {

    Context context;
    private static final String TAG = "FIREBASE_HELPER";

    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth mAuth;
    private GoogleSignInOptions googleSignInOptions;
    private TickTrackFirebaseDatabase tickTrackFirebaseDatabase;
    private TickTrackDatabase tickTrackDatabase;
    private FirebaseFirestore firebaseFirestore;
    private String action;
    private NotificationCompat.Builder notificationBuilder;
    private NotificationManagerCompat notificationManagerCompat;
    private ProgressBarDialog progressBarDialog;
    private JsonHelper jsonHelper;

    private boolean  isBackupComplete = false;

    public FirebaseHelper(Context context) {
        this.context = context;

        tickTrackDatabase = new TickTrackDatabase(context);
        tickTrackFirebaseDatabase = new TickTrackFirebaseDatabase(context);
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(context, googleSignInOptions);
        mAuth = FirebaseAuth.getInstance();
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

    public void signIn(Task<GoogleSignInAccount> completedTask, Activity activity) {
        progressBarDialog = new ProgressBarDialog(activity);
        progressBarDialog.show();
        progressBarDialog.setContentText("Signing in");
        progressBarDialog.titleText.setVisibility(View.GONE);
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            assert account != null;
            firebaseAuthWithGoogle(account.getIdToken(), activity);
        } catch (ApiException e) {
            progressBarDialog.dismiss();
            Toast.makeText(activity, "Sign in failed, try again", Toast.LENGTH_SHORT).show();
        }
    }
    public void firebaseAuthWithGoogle(String idToken, Activity activity) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        tickTrackFirebaseDatabase.storeCurrentUserEmail(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail());
                        tickTrackDatabase.storeStartUpFragmentID(3);
                        Intent intent = new Intent(activity, StartUpActivity.class);
                        intent.setAction(action);
                        activity.startActivity(intent);
                        progressBarDialog.dismiss();
                    } else {
                        progressBarDialog.dismiss();
                        tickTrackFirebaseDatabase.storeCurrentUserEmail(null);
                        Toast.makeText(activity, "Sign in failed, try again", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public void restoreInit() {

        tickTrackFirebaseDatabase.setRestoreInitMode(-1);

        firebaseFirestore.collection("TickTrackUsers").document(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if(queryDocumentSnapshots.exists()){
                        Toast.makeText(context, "Welcome back, "+ Objects.requireNonNull(mAuth.getCurrentUser()).getDisplayName(), Toast.LENGTH_SHORT).show();
                        checkIfDataExists();
                    } else {
                        Toast.makeText(context, "Welcome, "+ Objects.requireNonNull(mAuth.getCurrentUser()).getDisplayName(), Toast.LENGTH_SHORT).show();
                        setupInitUserData();
                    }
                }).addOnFailureListener(e -> {

                });
    }
    private void setupInitUserData() {
        Map<String, Object> user = new HashMap<>();
        user.put("accountCreateTime", System.currentTimeMillis());
        user.put("isProUser", false);
        user.put("backupExists", false);
        user.put("lastBackupTime", -1);
        user.put("timerCount", 0);
        user.put("counterCount", 0);
        user.put("themeMode", -1);
        user.put("emailID", Objects.requireNonNull(mAuth.getCurrentUser()).getEmail());
        user.put("localeCountry", context.getResources().getConfiguration().locale.getCountry());
        user.put("localeLanguage", context.getResources().getConfiguration().locale.getLanguage());
        user.put("deviceManufacturer", Build.MANUFACTURER);
        user.put("deviceModel", Build.MODEL);

        firebaseFirestore.collection("TickTrackUsers").document(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).get()
                .addOnSuccessListener(documentReference -> {
                    firebaseFirestore.collection("TickTrackUsers").document(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).set(user);
                    jsonHelper.initStorageFix();
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

    private void checkIfDataExists() {

        notificationBuilder.setContentTitle("Fetching TickTrack backup details");
        notificationBuilder.setContentText("In progress");
        notifyNotification();

        firebaseFirestore.collection("TickTrackUsers").document(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Map<String, Object> restoreMap = documentSnapshot.getData();
                    if (restoreMap != null) {
                        int timerCount = ((Long) Objects.requireNonNull(restoreMap.get("timerCount"))).intValue();
                        int counterCount = ((Long) Objects.requireNonNull(restoreMap.get("counterCount"))).intValue();
                        int themeMode = ((Long) Objects.requireNonNull(restoreMap.get("themeMode"))).intValue();
                        long lastBackup = ((Long) Objects.requireNonNull(restoreMap.get("lastBackupTime"))).intValue();
                        if (counterCount > 0) {
                            tickTrackFirebaseDatabase.storeRetrievedCounterCount(counterCount);
                        }
                        if (timerCount > 0) {
                            tickTrackFirebaseDatabase.storeRetrievedTimerCount(timerCount);
                        }
                        if (themeMode != -1) { //TODO ADD OTHER PREFERENCES AS || STATEMENTS
                            tickTrackFirebaseDatabase.foundPreferencesDataBackup(true);
                            tickTrackFirebaseDatabase.setRestoreThemeMode(themeMode);
                        }
                        if (lastBackup != -1) {
                            tickTrackFirebaseDatabase.storeRetrievedLastBackupTime(lastBackup);
                        }
                        tickTrackFirebaseDatabase.setRestoreInitMode(1);
                    } else {
                        setupInitUserData();
                    }
                })
                .addOnFailureListener(e -> System.out.println("ERROR FIREBASE" + e));
    }

    private ArrayList<TimerData> timerLocalDataList = new ArrayList<>();
    private ArrayList<CounterData> counterLocalDataList = new ArrayList<>();

    private void initPreferences() {
        tickTrackDatabase.setThemeMode(tickTrackFirebaseDatabase.getRestoreThemeMode());
        System.out.println("INITIALISED PREFERENCES");
    }

    public void backup() {

    }

    public void restore() {
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


    public boolean backupComplete(){
        return isBackupComplete;
    }

    public void signOut(Activity activity) {
        progressBarDialog = new ProgressBarDialog(activity);
        progressBarDialog.show();
        progressBarDialog.setContentText("Signing out");
        progressBarDialog.titleText.setVisibility(View.GONE);
        googleSignInClient.signOut().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                FirebaseAuth.getInstance().signOut();
                tickTrackFirebaseDatabase.storeCurrentUserEmail(null);
                tickTrackFirebaseDatabase.setRestoreInitMode(0);
                tickTrackFirebaseDatabase.setRestoreMode(false);
                tickTrackFirebaseDatabase.setBackupMode(false);
                tickTrackFirebaseDatabase.storeBackupCounterList(new ArrayList<>());
                tickTrackFirebaseDatabase.storeBackupTimerList(new ArrayList<>());
                Toast.makeText(activity, "Signed out", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(activity, "Not signed out, try again", Toast.LENGTH_SHORT).show();
            }
            progressBarDialog.dismiss();
        });
    }
    public boolean isUserSignedIn(){
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }


}
