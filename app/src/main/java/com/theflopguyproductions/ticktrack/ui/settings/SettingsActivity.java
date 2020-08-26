package com.theflopguyproductions.ticktrack.ui.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.SoYouADeveloperHuh;
import com.theflopguyproductions.ticktrack.dialogs.ProgressBarDialog;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.helpers.TickTrackThemeSetter;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    private TickTrackDatabase tickTrackDatabase;
    private Activity activity;

    private ConstraintLayout themeLayout, googleAccountLayout, accountOptionsLayout, switchAccountOptionLayout, disconnectAccountOptionLayout;
    private TextView themeName, themeTitle, backupTitle, backupEmail;
    private ImageButton backButton;
    private ScrollView settingsScrollView;
    private int prevFragment = -1;
    private ProgressBarDialog progressDialog;

    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth mAuth;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){
            setupEmailText();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        themeLayout = findViewById(R.id.themeSettingsLayout);
        themeTitle = findViewById(R.id.themeSettingsLabel);
        themeName = findViewById(R.id.themeValueSettingsTextView);
        backButton = findViewById(R.id.settingsActivityBackButton);
        settingsScrollView = findViewById(R.id.settingsActivityScrollView);
        googleAccountLayout = findViewById(R.id.backupAccountLayout);
        backupTitle = findViewById(R.id.backupTitleSettingsTextView);
        backupEmail = findViewById(R.id.backupEmailSettingsTextView);
        accountOptionsLayout = findViewById(R.id.backupAccountOptionsLayout);
        switchAccountOptionLayout = findViewById(R.id.backupSettingsSwitchAccountLayout);
        disconnectAccountOptionLayout = findViewById(R.id.backupSettingsDisconnectAccountLayout);

        activity = this;

        progressDialog = new ProgressBarDialog(activity);
        tickTrackDatabase = new TickTrackDatabase(activity);
        prevFragment = tickTrackDatabase.retrieveCurrentFragmentNumber();

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        mAuth = FirebaseAuth.getInstance();

        setupEmailText();

        TickTrackThemeSetter.settingsActivityTheme(activity, themeTitle, themeName, settingsScrollView, themeLayout,
                tickTrackDatabase,backupTitle, backupEmail, googleAccountLayout, switchAccountOptionLayout, disconnectAccountOptionLayout);
        accountOptionsLayout.setVisibility(View.GONE);

        themeLayout.setOnClickListener(view -> {
            ThemeDialog themeDialog = new ThemeDialog(activity, tickTrackDatabase.getThemeMode());
            themeDialog.show();
        });

        googleAccountLayout.setOnClickListener(view -> {
            if(GoogleSignIn.getLastSignedInAccount(this)!=null){
                toggleGoogleAccountOptionsLayout();
            } else {
                progressDialog.show();
                progressDialog.setContentText("Signing in");
                progressDialog.titleText.setVisibility(View.GONE);
                backupEmail.setText("...");
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 1);
            }
        });

        disconnectAccountOptionLayout.setOnClickListener(view -> signOut());

        backButton.setOnClickListener(view -> onBackPressed());

    }

    private void setupEmailText() {
        if(GoogleSignIn.getLastSignedInAccount(this)!=null){
            backupEmail.setText(Objects.requireNonNull(GoogleSignIn.getLastSignedInAccount(this)).getEmail());
        } else {
            backupEmail.setText("Add a backup account");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
    
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            //TODO HANDLE THIS COOLLY
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            assert account != null;
            firebaseAuthWithGoogle(account.getIdToken());
        } catch (ApiException e) {
            Toast.makeText(activity, "Sign in failed, try again", Toast.LENGTH_SHORT).show();
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                    } else {
                        Toast.makeText(activity, "Sign in failed, try again", Toast.LENGTH_SHORT).show();
                    }
                    setupEmailText();
                    progressDialog.dismiss();
                });
    }

    private void signOut() {
        progressDialog.show();
        progressDialog.setContentText("Signing in");
        progressDialog.titleText.setVisibility(View.GONE);
        googleSignInClient.signOut()
                .addOnCompleteListener(this, task -> {
                    toggleGoogleAccountOptionsLayout();
                    progressDialog.dismiss();
                });
    }

    private boolean isOpen = false;
    private void toggleGoogleAccountOptionsLayout() {
        if(isOpen){
            accountOptionsLayout.setVisibility(View.GONE);
            setupEmailText();
            isOpen = false;
        } else {
            accountOptionsLayout.setVisibility(View.VISIBLE);
            isOpen = true;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, SoYouADeveloperHuh.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        tickTrackDatabase.storeCurrentFragmentNumber(prevFragment);
    }
}