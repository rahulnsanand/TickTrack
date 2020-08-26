package com.theflopguyproductions.ticktrack.utils.helpers;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

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
import com.theflopguyproductions.ticktrack.dialogs.ProgressBarDialog;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackFirebaseDatabase;

import java.util.Objects;

public class FirebaseHelper {

    Activity activity;
    private static final String TAG = "FIREBASE_TICK_TRACK";

    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth mAuth;
    private ProgressBarDialog progressBarDialog;
    private GoogleSignInOptions googleSignInOptions;
    private TickTrackFirebaseDatabase tickTrackFirebaseDatabase;
    private DatabaseReference rootDatabase;
    private FirebaseDatabase firebaseDatabase;
    private String currentUserID;


    public FirebaseHelper(Activity activity) {
        this.activity = activity;
        progressBarDialog = new ProgressBarDialog(activity);
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

    /**
     * THINGS REQUIRED HERE
     * 1. SIGN IN
     * 2. SIGN OUT
     * 3. IS USER SIGNED IN
     * 4.
     */

    public Intent getSignInIntent(){
        progressBarDialog.show();
        progressBarDialog.setContentText("Signing in");
        progressBarDialog.titleText.setVisibility(View.GONE);
        return googleSignInClient.getSignInIntent();
    }

    public void signIn(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            assert account != null;
            firebaseAuthWithGoogle(account.getIdToken());
        } catch (ApiException e) {
            Toast.makeText(activity, "Sign in failed, try again", Toast.LENGTH_SHORT).show();
            progressBarDialog.dismiss();
        }
    }

    public void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        tickTrackFirebaseDatabase.storeCurrentUserEmail(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail());
                        currentUserID = mAuth.getCurrentUser().getUid();
                        checkIfUserExists();
                    } else {
                        tickTrackFirebaseDatabase.storeCurrentUserEmail(null);
                        Toast.makeText(activity, "Sign in failed, try again", Toast.LENGTH_SHORT).show();
                        progressBarDialog.dismiss();
                    }

                });
    }

    private void checkIfUserExists() {
            rootDatabase.child("TickTrackUsers").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.child(currentUserID).exists()){
                        Toast.makeText(activity, "Welcome back, "+ Objects.requireNonNull(mAuth.getCurrentUser()).getDisplayName(), Toast.LENGTH_SHORT).show();
                        checkIfDataExists();
                    } else {
                        Toast.makeText(activity, "Welcome, "+ Objects.requireNonNull(mAuth.getCurrentUser()).getDisplayName(), Toast.LENGTH_SHORT).show();
                        setupInitUserData();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
    }

    private void setupInitUserData() {
        progressBarDialog.setContentText("Setting things up");
        rootDatabase.child("TickTrackBackups").child(currentUserID).child("isProUser").setValue(false).addOnCompleteListener(task -> {
            rootDatabase.child("TickTrackBackups").child(currentUserID).child("accountCreateTime").setValue(System.currentTimeMillis()).addOnCompleteListener(task1 -> {
               progressBarDialog.dismiss();
               //TODO CREATE FIRST BACKUP
            });
        });
    }

    private boolean timerBackupExists = false, counterBackupExists = false, preferencesBackupExists = true;
    private void checkIfDataExists() {
        progressBarDialog.setContentText("Looking for backup");
        rootDatabase.child("TickTrackBackups").child(currentUserID).child("TickTrackTimerData").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    retrieveTimerRestoreData();
                    timerBackupExists = true;
                } else {
                    timerBackupExists = false;
                }
                rootDatabase.child("TickTrackBackups").child(currentUserID).child("TickTrackCounterData").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            retrieveCounterRestoreData();
                            counterBackupExists = true;
                        } else {
                            counterBackupExists = false;
                        }
                        rootDatabase.child("TickTrackBackups").child(currentUserID).child("TickTrackPreferenceData").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    retrievePreferencesRestoreData();
                                    preferencesBackupExists = true;
                                } else {
                                    preferencesBackupExists = false;
                                }
                                progressBarDialog.dismiss();

                                //TODO GO TO RESTORE FRAGMENT

                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void retrievePreferencesRestoreData() {

    }

    private void retrieveCounterRestoreData() {

    }

    private void retrieveTimerRestoreData() {

    }

    public void signOut() {
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
