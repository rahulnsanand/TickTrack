package com.theflopguyproductions.ticktrack.service;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackFirebaseDatabase;
import com.theflopguyproductions.ticktrack.utils.helpers.FirebaseHelper;

import java.util.Objects;

public class AccountAddedService extends Service {

    private Activity activity;
    private FirebaseHelper firebaseHelper;
    private TickTrackFirebaseDatabase tickTrackFirebaseDatabase;
    private TickTrackDatabase tickTrackDatabase;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference rootDatabase;

    public static final String ACTION_ACCOUNT_ADDED = "ACTION_ACCOUNT_ADDED";
    public static final String ACTION_ACCOUNT_DATABASE_CHECK_COMPLETE = "ACTION_ACCOUNT_DATABASE_CHECK_COMPLETE";


    public AccountAddedService(Activity activity) {
        this.activity = activity;
        firebaseHelper = new FirebaseHelper(activity);
        tickTrackFirebaseDatabase = new TickTrackFirebaseDatabase(activity);
        tickTrackDatabase = new TickTrackDatabase(activity);
        firebaseDatabase = FirebaseDatabase.getInstance();
        rootDatabase = firebaseDatabase.getReference();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null) {

            String action = intent.getAction();

            assert action != null;
            switch (action) {
                case ACTION_ACCOUNT_ADDED:
                    checkForUserExist();
                    break;
                case ACTION_ACCOUNT_DATABASE_CHECK_COMPLETE:
//                    Intent broadcastIntent = new Intent(getApplicationContext(), BatteryOptimiseReceiver.class);
//                    broadcastIntent.setAction(BatteryOptimiseReceiver.ACTION_OPTIMISING_DONE_CHECK);
//                    sendBroadcast(broadcastIntent);
                    stopSelf();
                    onDestroy();
                    break;
            }
        }
        return START_STICKY;
    }

    private void checkForUserExist() {
        rootDatabase.child("TickTrackUsers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).exists()){
                    Toast.makeText(activity, "Welcome back, "+FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(activity, "Welcome, "+FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
