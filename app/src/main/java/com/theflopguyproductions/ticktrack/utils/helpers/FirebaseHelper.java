package com.theflopguyproductions.ticktrack.utils.helpers;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.counter.CounterData;
import com.theflopguyproductions.ticktrack.dialogs.ProgressBarDialog;
import com.theflopguyproductions.ticktrack.settings.SettingsActivity;
import com.theflopguyproductions.ticktrack.startup.StartUpActivity;
import com.theflopguyproductions.ticktrack.startup.fragments.LoginFragment;
import com.theflopguyproductions.ticktrack.timer.TimerData;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackFirebaseDatabase;

import java.util.ArrayList;
import java.util.Objects;

public class FirebaseHelper {

    Activity activity;
    private static final String TAG = "FIREBASE_HELPER";

    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth mAuth;
    private GoogleSignInOptions googleSignInOptions;
    private TickTrackFirebaseDatabase tickTrackFirebaseDatabase;
    private TickTrackDatabase tickTrackDatabase;
    private DatabaseReference rootDatabase;
    private FirebaseDatabase firebaseDatabase;
    private String currentUserID, action;
    private LoginFragment.LoginClickListeners loginClickListeners;
    private ConstraintLayout retrieveDataLayout;

    private TextView title, subtitle;
    private Button laterButton, signInButton;


    public FirebaseHelper(Activity activity) {
        this.activity = activity;

        tickTrackDatabase = new TickTrackDatabase(activity);
        tickTrackFirebaseDatabase = new TickTrackFirebaseDatabase(activity);
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(activity, googleSignInOptions);
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        rootDatabase = firebaseDatabase.getReference();
    }

    public void setupGraphics(TextView title, TextView subtitle, String action, ConstraintLayout retrieveDataLayout,
                              Button laterButton, Button signInButton){
        this.title = title;
        this.subtitle = subtitle;
        this.action = action;
        this.retrieveDataLayout = retrieveDataLayout;
        this.laterButton = laterButton;
        this.signInButton = signInButton;
    }

    private void stockTitleValue() {
        title.setText("Add a Google account");
        subtitle.setText("Link an account for backup/restore");
        if (action.equals(StartUpActivity.ACTION_SETTINGS_ACCOUNT_ADD)){
            activity.startActivity(new Intent(activity, SettingsActivity.class));
        } else {
            signInButton.setVisibility(View.VISIBLE);
            laterButton.setVisibility(View.VISIBLE);
        }
    }

    public Intent getSignInIntent(){
        title.setText("Signing in...");
        subtitle.setText("Please wait");
        return googleSignInClient.getSignInIntent();
    }

    public void signIn(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            assert account != null;
            firebaseAuthWithGoogle(account.getIdToken());
        } catch (ApiException e) {
            Toast.makeText(activity, "Sign in failed, try again", Toast.LENGTH_SHORT).show();
            stockTitleValue();
        }
    }



    public void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        tickTrackFirebaseDatabase.storeCurrentUserEmail(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail());
                        currentUserID = mAuth.getCurrentUser().getUid();
                        title.setText("Checking for backup");
                        subtitle.setText("Please wait");
                        loginClickListeners.onRestoreListener();
//                        checkIfUserExists();

                    } else {
                        tickTrackFirebaseDatabase.storeCurrentUserEmail(null);
                        Toast.makeText(activity, "Sign in failed, try again", Toast.LENGTH_SHORT).show();
                        stockTitleValue();
                    }

                });
    }

    private void checkIfUserExists() {
            rootDatabase.child("TickTrackUsers").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.child(currentUserID).exists()){
                        Toast.makeText(activity, "Welcome back, "+ Objects.requireNonNull(mAuth.getCurrentUser()).getDisplayName(), Toast.LENGTH_SHORT).show();
                        title.setText("Retrieving backup data");
                        subtitle.setText("Please wait");
                        checkIfDataExists();
                    } else {
                        Toast.makeText(activity, "Welcome, "+ Objects.requireNonNull(mAuth.getCurrentUser()).getDisplayName(), Toast.LENGTH_SHORT).show();
                        title.setText("Creating new user");
                        subtitle.setText("Please wait");
                        setupInitUserData();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
    }

    private void setupInitUserData() {
        rootDatabase.child("TickTrackUsers").child(currentUserID).child("accountCreateTime").setValue(System.currentTimeMillis()).addOnCompleteListener(task ->
                rootDatabase.child("TickTrackUsers").child(currentUserID).child("isProUser").setValue(false).addOnCompleteListener(task12 ->
                                rootDatabase.child("TickTrackBackups").child(currentUserID).child("backupExists").setValue(false).addOnCompleteListener(task13 ->
                                        completedFragmentTask())));

    }

    private void completedFragmentTask() {
        if(action.equals(StartUpActivity.ACTION_SETTINGS_ACCOUNT_ADD)){
            activity.startActivity(new Intent(activity, SettingsActivity.class));
        } else {
            loginClickListeners.onLaterClickListener();
        }
    }

    DatabaseReference timerDatabase, counterDatabase, preferenceDatabase;
    private void checkIfDataExists() {

        timerDatabase = rootDatabase.child("TickTrackBackups").child(currentUserID).child("timerBackup");
        counterDatabase = rootDatabase.child("TickTrackBackups").child(currentUserID).child("counterBackup");
        preferenceDatabase = rootDatabase.child("TickTrackBackups").child(currentUserID).child("preferenceBackup");

        rootDatabase.child("TickTrackBackups").child(currentUserID).child("backupExists").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(Objects.equals(snapshot.getValue(), true)){
                    tickTrackFirebaseDatabase.setFirebaseBackupExists(true);

                    retrieveDataLayout.setVisibility(View.VISIBLE);

                    rootDatabase.child("TickTrackBackups").child(currentUserID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.child("timerBackup").exists()){
                                retrieveTimerRestoreData();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                    rootDatabase.child("TickTrackBackups").child(currentUserID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.child("counterBackup").exists()){
                                retrieveCounterRestoreData();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                    rootDatabase.child("TickTrackBackups").child(currentUserID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.child("preferenceBackup").exists()){
                                retrievePreferencesRestoreData();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });

                } else {
                    tickTrackFirebaseDatabase.setFirebaseBackupExists(false);
                    retrieveDataLayout.setVisibility(View.GONE);
                    title.setText("No backup found");

                    laterButton.setVisibility(View.VISIBLE);
                    if(action.equals(StartUpActivity.ACTION_SETTINGS_ACCOUNT_ADD)){
                        signInButton.setVisibility(View.VISIBLE);
                        signInButton.setText("Create a backup now");
                        signInButton.setOnClickListener(view -> createBackupNow());
                        laterButton.setText("Nah, schedule it for later");
                    } else {
                        laterButton.setText("Continue setup");
                    }

                    laterButton.setOnClickListener(view -> {
                        if(action.equals(StartUpActivity.ACTION_SETTINGS_ACCOUNT_ADD)){
                            activity.startActivity(new Intent(activity, SettingsActivity.class));
                        } else {
                            loginClickListeners.onLaterClickListener();
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private ArrayList<TimerData> timerLocalDataList = new ArrayList<>();
    private ArrayList<CounterData> counterLocalDataList = new ArrayList<>();
    private void createBackupNow() {

        timerLocalDataList = tickTrackDatabase.retrieveTimerList();
        counterLocalDataList = tickTrackDatabase.retrieveCounterList();

        rootDatabase.child("TickTrackBackups").child(currentUserID).child("backupExists").setValue(true);

        preferenceDatabase.child("themeMode").setValue(tickTrackDatabase.getThemeMode());

        timerDatabase.child("lastBackupTime").setValue(System.currentTimeMillis());
        timerDatabase.child("numberOfTimers").setValue(timerLocalDataList.size());
        DatabaseReference currentTimerDataRef;
        for(int i = 0; i<timerLocalDataList.size(); i++){
            String timerID = String.valueOf(timerLocalDataList.get(i).getTimerID());
            currentTimerDataRef = timerDatabase.child(timerID);
            currentTimerDataRef.child("timerHour").setValue(timerLocalDataList.get(i).getTimerHour());
            currentTimerDataRef.child("timerMinute").setValue(timerLocalDataList.get(i).getTimerMinute());
            currentTimerDataRef.child("timerSecond").setValue(timerLocalDataList.get(i).getTimerSecond());
            currentTimerDataRef.child("timerID").setValue(timerLocalDataList.get(i).getTimerID());
            currentTimerDataRef.child("timerFlag").setValue(timerLocalDataList.get(i).getTimerFlag());
            currentTimerDataRef.child("timerTotalTimeInMillis").setValue(timerLocalDataList.get(i).getTimerTotalTimeInMillis());
            currentTimerDataRef.child("timerCreateTimeStamp").setValue(timerLocalDataList.get(i).getTimerCreateTimeStamp());
            currentTimerDataRef.child("timerLabel").setValue(timerLocalDataList.get(i).getTimerLabel());
        }
    }

    long themeMode = 1;
    boolean counterVibrate = false;
    private void retrievePreferencesRestoreData() {
        preferenceDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("themeMode").exists())
                    themeMode = (long) snapshot.child("themeMode").getValue();
                if(snapshot.child("counterVibrate").exists())
                    counterVibrate = (boolean) snapshot.child("counterVibrate").getValue();
//                preference.setText("Preferences retrieved");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    ArrayList<CounterData> counterDataBackup = new ArrayList<>();
    int counterNumber = 0;
    private void retrieveCounterRestoreData() {
        counterDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("numberOfCounters").exists())
                    counterNumber = (int) snapshot.child("numberOfCounters").getValue();

//                if(counterNumber>1){
//                    counter.setText(counterNumber+" counters retrieved");
//                } else if (counterNumber==1){
//                    counter.setText(counterNumber+" counter retrieved");
//                } else {
//                    counter.setText("Counter data retrieved");
//                }

//                try {
//                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
//                        CounterData counterData= postSnapshot.getValue(CounterData.class);
//                        counterDataBackup.add(counterData);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    ArrayList<TimerData> timerDataBackup = new ArrayList<>();
    long timerNumber = 0;
    private void retrieveTimerRestoreData() {
        timerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("numberOfTimers").exists())
                    timerNumber = (long) snapshot.child("numberOfTimers").getValue();

//                if(timerNumber>1){
//                    timer.setText(timerNumber+" timers retrieved");
//                } else if (timerNumber==1){
//                    timer.setText(timerNumber+" timer retrieved");
//                } else {
//                    timer.setText("Timer data retrieved");
//                }

//                try {
//                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
//                        TimerData timerData= postSnapshot.getValue(TimerData.class);
//                        timerDataBackup.add(timerData);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    ProgressBarDialog progressBarDialog;
    public void signOut() {
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
