package com.theflopguyproductions.ticktrack.utils.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.theflopguyproductions.ticktrack.service.RestoreService;
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
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            assert account != null;
            firebaseAuthWithGoogle(account.getIdToken(), activity);
        } catch (ApiException e) {
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
                        activity.startActivity(new Intent(activity, StartUpActivity.class));

                    } else {
                        tickTrackFirebaseDatabase.storeCurrentUserEmail(null);
                        Toast.makeText(activity, "Sign in failed, try again", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public void checkIfUserExists() {
        tickTrackFirebaseDatabase.setRestoreInitMode(true);

        firebaseFirestore.collection(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if(queryDocumentSnapshots.getDocuments().size()>0){
                        Toast.makeText(context, "Welcome back, "+ Objects.requireNonNull(mAuth.getCurrentUser()).getDisplayName(), Toast.LENGTH_SHORT).show();
//                        checkIfDataExists();
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

        firebaseFirestore.collection(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).add(user)
                .addOnSuccessListener(documentReference -> completedFragmentTask())
                .addOnFailureListener(e -> {  });

    }
    private void completedFragmentTask() {
        if(action.equals(StartUpActivity.ACTION_SETTINGS_ACCOUNT_ADD)){
            context.startActivity(new Intent(context, SettingsActivity.class));
        } else {
            tickTrackDatabase.storeStartUpFragmentID(4);
            context.startActivity(new Intent(context, StartUpActivity.class));
        }
        stopRestoreService();
    }
    private void stopRestoreService() {
        Intent intent = new Intent(context, RestoreService.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setAction(RestoreService.DATA_RESTORATION_STOP);
        context.startService(intent);
    }

//    private void checkIfDataExists() {
//
//        notificationBuilder.setContentTitle("Fetching TickTrack backup details");
//        notificationBuilder.setContentText("In progress");
//        notifyNotification();
//
//        timerDatabase = rootDatabase.child("TickTrackBackups").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).child("timerBackup");
//        counterDatabase = rootDatabase.child("TickTrackBackups").child(mAuth.getCurrentUser().getUid()).child("counterBackup");
//        preferenceDatabase = rootDatabase.child("TickTrackBackups").child(mAuth.getCurrentUser().getUid()).child("preferenceBackup");
//
//
//        tickTrackFirebaseDatabase.completeTimerDataRestore(false);
//        tickTrackFirebaseDatabase.completeCounterDataRestore(false);
//        tickTrackFirebaseDatabase.completePreferencesDataRestore(false);
//
//        rootDatabase.child("TickTrackBackups").child(mAuth.getCurrentUser().getUid()).child("backupExists").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(Objects.equals(snapshot.getValue(), true)){
//
//                    rootDatabase.child("TickTrackBackups").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            if(snapshot.child("timerBackup").exists()){
//                                tickTrackFirebaseDatabase.foundTimerDataBackup(true);
//                                timerDatabase.child("restoreComplete").setValue(false).addOnCompleteListener(task -> retrieveTimerRestoreData());
//                            } else {
//                                tickTrackFirebaseDatabase.foundTimerDataBackup(false);
//                            }
//                            rootDatabase.child("TickTrackBackups").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                    if(snapshot.child("counterBackup").exists()){
//                                        tickTrackFirebaseDatabase.foundCounterDataBackup(true);
//                                        counterDatabase.child("restoreComplete").setValue(false).addOnCompleteListener(task -> retrieveCounterRestoreData());
//
//                                    } else {
//                                        tickTrackFirebaseDatabase.foundCounterDataBackup(false);
//                                    }
//                                    rootDatabase.child("TickTrackBackups").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                            if(snapshot.child("preferenceBackup").exists()) {
//                                                tickTrackFirebaseDatabase.foundPreferencesDataBackup(true);
//                                                preferenceDatabase.child("restoreComplete").setValue(false).addOnCompleteListener(task -> retrievePreferencesRestoreData());
//
//                                            } else {
//                                                tickTrackFirebaseDatabase.foundPreferencesDataBackup(false);
//                                            }
//                                        }
//                                        @Override
//                                        public void onCancelled(@NonNull DatabaseError error) {
//                                        }
//                                    });
//                                }
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError error) {
//                                }
//                            });
//                        }
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//                        }
//                    });
//                } else {
//                    notificationBuilder.setContentTitle("Creating a backup now");
//                    notifyNotification();
//                    createBackupNow();
//                    stopRestoreService();
//
//                    tickTrackDatabase.storeStartUpFragmentID(4);
//                    context.startActivity(new Intent(context, StartUpActivity.class));
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }

    private void deletePreviousBackups() {

    }

    private ArrayList<TimerData> timerLocalDataList = new ArrayList<>();
    private ArrayList<CounterData> counterLocalDataList = new ArrayList<>();
//    private void createBackupNow() {
//
//        timerLocalDataList = tickTrackDatabase.retrieveTimerList();
//        counterLocalDataList = tickTrackDatabase.retrieveCounterList();
//
//        deletePreviousBackups();
//
//        rootDatabase.child("TickTrackBackups").child(mAuth.getCurrentUser().getUid()).child("backupExists").setValue(true);
//
//        preferenceDatabase.child("themeMode").setValue(tickTrackDatabase.getThemeMode());
//
//        timerDatabase.child("lastBackupTime").setValue(System.currentTimeMillis());
//        timerDatabase.child("numberOfTimers").setValue(timerLocalDataList.size());
//        DatabaseReference currentTimerDataRef;
//        for(int i = 0; i<timerLocalDataList.size(); i++){
//            String timerID = String.valueOf(timerLocalDataList.get(i).getTimerID());
//            currentTimerDataRef = timerDatabase.child(timerID);
//            currentTimerDataRef.child("timerHour").setValue(timerLocalDataList.get(i).getTimerHour());
//            currentTimerDataRef.child("timerMinute").setValue(timerLocalDataList.get(i).getTimerMinute());
//            currentTimerDataRef.child("timerSecond").setValue(timerLocalDataList.get(i).getTimerSecond());
//            currentTimerDataRef.child("timerID").setValue(timerLocalDataList.get(i).getTimerID());
//            currentTimerDataRef.child("timerFlag").setValue(timerLocalDataList.get(i).getTimerFlag());
//            currentTimerDataRef.child("timerTotalTimeInMillis").setValue(timerLocalDataList.get(i).getTimerTotalTimeInMillis());
//            currentTimerDataRef.child("timerCreateTimeStamp").setValue(timerLocalDataList.get(i).getTimerCreateTimeStamp());
//            currentTimerDataRef.child("timerLabel").setValue(timerLocalDataList.get(i).getTimerLabel());
//        }
//
//        counterDatabase.child("lastBackupTime").setValue(System.currentTimeMillis());
//        counterDatabase.child("numberOfCounters").setValue(counterLocalDataList.size());
//        DatabaseReference currentCounterDataRef;
//        for(int i = 0; i<counterLocalDataList.size(); i++){
//            String counterID = String.valueOf(counterLocalDataList.get(i).getCounterID());
//            currentCounterDataRef = counterDatabase.child(counterID);
//            currentCounterDataRef.child("counterValue").setValue(counterLocalDataList.get(i).getCounterValue());
//            currentCounterDataRef.child("counterFlag").setValue(counterLocalDataList.get(i).getCounterFlag());
//            currentCounterDataRef.child("counterSignificantCount").setValue(counterLocalDataList.get(i).getCounterSignificantCount());
//            currentCounterDataRef.child("counterSignificantExist").setValue(counterLocalDataList.get(i).isCounterSignificantExist());
//            currentCounterDataRef.child("counterSwipeMode").setValue(counterLocalDataList.get(i).isCounterSwipeMode());
//            currentCounterDataRef.child("counterLabel").setValue(counterLocalDataList.get(i).getCounterLabel());
//            currentCounterDataRef.child("counterID").setValue(counterLocalDataList.get(i).getCounterID());
//            currentCounterDataRef.child("counterTimestamp").setValue(counterLocalDataList.get(i).getCounterTimestamp());
//        }
//    }

//    long themeMode = 1;
//    boolean counterVibrate = false;
//    private void retrievePreferencesRestoreData() {
//        notificationBuilder.setContentTitle("Retrieving TickTrack Preferences");
//        notifyNotification();
//        preferenceDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(snapshot.child("themeMode").exists()){
//                    themeMode = (long) snapshot.child("themeMode").getValue();
//                    preferenceDatabase.child("restoreComplete").setValue(true).addOnCompleteListener(task -> tickTrackFirebaseDatabase.completePreferencesDataRestore(true));
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
//
//    long counterNumber = 0;
//    private void retrieveCounterRestoreData() {
//        notificationBuilder.setContentTitle( "Retrieving TickTrack Counters");
//        notifyNotification();
//        counterDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(snapshot.child("numberOfCounters").exists()){
//                    counterNumber = (long) snapshot.child("numberOfCounters").getValue();
//                    tickTrackFirebaseDatabase.storeRetrievedCounterCount((int) counterNumber);
//                    counterDatabase.child("restoreComplete").setValue(true).addOnCompleteListener(task -> tickTrackFirebaseDatabase.completeCounterDataRestore(true));
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
//
//    long timerNumber = 0;
//    private void retrieveTimerRestoreData() {
//        notificationBuilder.setContentTitle("Retrieving TickTrack Timers");
//        notifyNotification();
//        timerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(snapshot.child("numberOfTimers").exists()){
//                    timerNumber = (long) snapshot.child("numberOfTimers").getValue();
//                    tickTrackFirebaseDatabase.storeRetrievedTimerCount((int) timerNumber);
//                    timerDatabase.child("restoreComplete").setValue(true).addOnCompleteListener(task -> tickTrackFirebaseDatabase.completeTimerDataRestore(true));
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }

    public boolean dataRestoreInitCompleteCheck(){
        boolean counterCheck = false, timerCheck = false, preferenceCheck = false;
        if(tickTrackFirebaseDatabase.isPreferencesDataRestoreComplete()){
            if(tickTrackFirebaseDatabase.hasCounterDataBackup()){
                counterCheck = tickTrackFirebaseDatabase.isCounterDataRestoreComplete();
            } else {
                counterCheck = true;
            }
            if(tickTrackFirebaseDatabase.hasTimerDataBackup()){
                timerCheck = tickTrackFirebaseDatabase.isTimerDataRestoreComplete();
            } else {
                timerCheck = true;
            }
            if(tickTrackFirebaseDatabase.hasPreferencesDataBackup()){
                preferenceCheck = tickTrackFirebaseDatabase.isPreferencesDataRestoreComplete();
            } else {
                preferenceCheck = true;
            }
        }
        return counterCheck && timerCheck && preferenceCheck;
    }

    public boolean dataRestoreCompleteCheck() {
        boolean counterCheck = false, timerCheck = false, preferenceCheck = false;
        if(tickTrackFirebaseDatabase.isPreferencesDataRestoreComplete()){
            if(tickTrackFirebaseDatabase.hasCounterDataBackup()){
                counterCheck = tickTrackFirebaseDatabase.isCounterDataRestoreComplete();
            } else {
                counterCheck = true;
            }
            if(tickTrackFirebaseDatabase.hasTimerDataBackup()){
                timerCheck = tickTrackFirebaseDatabase.isTimerDataRestoreComplete();
            } else {
                timerCheck = true;
            }
            if(tickTrackFirebaseDatabase.hasPreferencesDataBackup()){
                preferenceCheck = tickTrackFirebaseDatabase.isPreferencesDataRestoreComplete();
            } else {
                preferenceCheck = true;
            }
        }
        return counterCheck && timerCheck && preferenceCheck;
    }

    ProgressBarDialog progressBarDialog;
    public void signOut(Activity activity) {
        progressBarDialog = new ProgressBarDialog(activity);
        progressBarDialog.show();
        progressBarDialog.setContentText("Signing out");
        progressBarDialog.titleText.setVisibility(View.GONE);
        googleSignInClient.signOut().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                FirebaseAuth.getInstance().signOut();
                tickTrackFirebaseDatabase.storeCurrentUserEmail(null);
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
