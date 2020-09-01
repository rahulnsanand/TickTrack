package com.theflopguyproductions.ticktrack;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.theflopguyproductions.ticktrack.application.TickTrack;
import com.theflopguyproductions.ticktrack.dialogs.MissedItemDialog;
import com.theflopguyproductions.ticktrack.settings.SettingsActivity;
import com.theflopguyproductions.ticktrack.startup.StartUpActivity;
import com.theflopguyproductions.ticktrack.stopwatch.service.StopwatchNotificationService;
import com.theflopguyproductions.ticktrack.ui.counter.CounterFragment;
import com.theflopguyproductions.ticktrack.ui.stopwatch.StopwatchFragment;
import com.theflopguyproductions.ticktrack.ui.timer.TimerFragment;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackFirebaseDatabase;
import com.theflopguyproductions.ticktrack.utils.font.TypefaceSpanSetup;
import com.theflopguyproductions.ticktrack.utils.helpers.AutoStartPermissionHelper;
import com.theflopguyproductions.ticktrack.utils.helpers.JsonHelper;
import com.theflopguyproductions.ticktrack.utils.helpers.PowerSaverHelper;
import com.theflopguyproductions.ticktrack.utils.helpers.TickTrackThemeSetter;

import java.util.Collections;

public class SoYouADeveloperHuh extends AppCompatActivity {

    TickTrackDatabase tickTrackDatabase;
    TimerManagementHelper timerManagementHelper;

    private Toolbar mainToolbar;
    private TextView tickTrackAppName;
    private BottomNavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        tickTrackDatabase = new TickTrackDatabase(this);
        timerManagementHelper = new TimerManagementHelper(this);
        int missedTimers = 0;
        int almostMissedTimers = timerManagementHelper.getAlmostMissedTimer();

        if(missedTimers>0 || almostMissedTimers >0){
            MissedItemDialog missedItemDialog = new MissedItemDialog(this, missedTimers, almostMissedTimers);
            missedItemDialog.setCancelable(true);
            missedItemDialog.show();
        }

        setContentView(R.layout.activity_so_you_a_developer_huh);

        mainToolbar = findViewById(R.id.mainActivityToolbar);
        tickTrackAppName = findViewById(R.id.ticktrackAppName);
        navView = findViewById(R.id.nav_view);
        navView.setItemIconTintList(null);
        navView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        setSupportActionBar(mainToolbar);
        setTitle("");
        int receivedFragmentID = tickTrackDatabase.retrieveCurrentFragmentNumber();

        if(new TickTrackFirebaseDatabase(this).isRestoreInitMode()==-1 || new TickTrackFirebaseDatabase(this).isRestoreInitMode()==1){
            goToStartUpActivity(3, true);
        } else if(PowerSaverHelper.getIfAppIsWhiteListedFromBatteryOptimizations(this, getPackageName())
                .equals(PowerSaverHelper.WhiteListedInBatteryOptimizations.NOT_WHITE_LISTED) || tickTrackDatabase.retrieveFirstLaunch()){

            goToStartUpActivity(5, false);

        } else {

            openFragment(getFragment(receivedFragmentID));

            boolean setHappen = AutoStartPermissionHelper.getInstance().getAutoStartPermission(getApplicationContext());
            boolean isAvailable = AutoStartPermissionHelper.getInstance().isAutoStartPermissionAvailable(this);

            requestSignIn();


        }
    }

    private static final String TAG = "MainActivity";

    private static final int REQUEST_CODE_SIGN_IN = 1;
    private static final int REQUEST_CODE_OPEN_DOCUMENT = 2;
    private void requestSignIn() {
        Log.d(TAG, "Requesting sign-in");

        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestScopes(new Scope(DriveScopes.DRIVE_APPDATA))
                        .build();
        GoogleSignInClient client = GoogleSignIn.getClient(this, signInOptions);

        // The result of the sign-in Intent is handled in onActivityResult.
        startActivityForResult(client.getSignInIntent(), REQUEST_CODE_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        switch (requestCode) {
            case REQUEST_CODE_SIGN_IN:
                if (resultCode == Activity.RESULT_OK && resultData != null) {
                    handleSignInResult(resultData);
                }
                break;

            case REQUEST_CODE_OPEN_DOCUMENT:
                if (resultCode == Activity.RESULT_OK && resultData != null) {
                    Uri uri = resultData.getData();
                    if (uri != null) {
                        openFileFromFilePicker(uri);
                    }
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, resultData);
    }
    private GDriveHelper mDriveServiceHelper;
    /**
     * Handles the {@code result} of a completed sign-in activity initiated from {@link
     * #requestSignIn()}.
     */
    private void handleSignInResult(Intent result) {
        GoogleSignIn.getSignedInAccountFromIntent(result)
                .addOnSuccessListener(googleAccount -> {
                    Log.d(TAG, "Signed in as " + googleAccount.getEmail());

                    // Use the authenticated account to sign in to the Drive service.
                    GoogleAccountCredential credential =
                            GoogleAccountCredential.usingOAuth2(
                                    this, Collections.singleton(DriveScopes.DRIVE_FILE));
                    credential.setSelectedAccount(googleAccount.getAccount());
                    Drive googleDriveService =
                            new Drive.Builder(
                                    AndroidHttp.newCompatibleTransport(),
                                    new GsonFactory(),
                                    credential)
                                    .setApplicationName("Drive API Migration")
                                    .build();

                    // The DriveServiceHelper encapsulates all REST API and SAF functionality.
                    // Its instantiation is required before handling any onClick actions.
                    mDriveServiceHelper = new GDriveHelper(googleDriveService, getApplicationContext());

                    query();

//                    createFile();
                })
                .addOnFailureListener(exception -> Log.e(TAG, "Unable to sign in.", exception));
    }

    /**
     * Opens the Storage Access Framework file picker using {@link #REQUEST_CODE_OPEN_DOCUMENT}.
     */
    private void openFilePicker() {
        if (mDriveServiceHelper != null) {
            Log.d(TAG, "Opening file picker.");

            Intent pickerIntent = mDriveServiceHelper.createFilePickerIntent();

            // The result of the SAF Intent is handled in onActivityResult.
            startActivityForResult(pickerIntent, REQUEST_CODE_OPEN_DOCUMENT);
        }
    }

    /**
     * Opens a file from its {@code uri} returned from the Storage Access Framework file picker
     * initiated by {@link #openFilePicker()}.
     */
    private void openFileFromFilePicker(Uri uri) {
        if (mDriveServiceHelper != null) {
            Log.d(TAG, "Opening " + uri.getPath());

            mDriveServiceHelper.openFileUsingStorageAccessFramework(getContentResolver(), uri)
                    .addOnSuccessListener(nameAndContent -> {
                        String name = nameAndContent.first;
                        String content = nameAndContent.second;

                        System.out.println(name);
                        System.out.println(content);

                        // Files opened through SAF cannot be modified.
                        setReadOnlyMode();
                    })
                    .addOnFailureListener(exception ->
                            Log.e(TAG, "Unable to open file from picker.", exception));
        }
    }

    /**
     * Creates a new file via the Drive REST API.
     */
    private void createFile() {
        if (mDriveServiceHelper != null) {
            Log.d(TAG, "Creating a file.");

            mDriveServiceHelper.createTimerBackup()
                    .addOnSuccessListener(this::readFile)
                    .addOnFailureListener(exception ->
                            Log.e(TAG, "Couldn't create file.", exception));
        }
    }

    /**
     * Retrieves the title and content of a file identified by {@code fileId} and populates the UI.
     */
    private void readFile(String fileId) {
        if (mDriveServiceHelper != null) {
            Log.d(TAG, "Reading file " + fileId);

            mDriveServiceHelper.readFile(fileId)
                    .addOnSuccessListener(nameAndContent -> {
                        String name = nameAndContent.first;
                        String content = nameAndContent.second;

                        System.out.println(name);
                        System.out.println(content);

                        setReadWriteMode(fileId);
                    })
                    .addOnFailureListener(exception ->
                            Log.e(TAG, "Couldn't read file.", exception));
        }
    }
    private String mOpenFileId;
    /**
     * Saves the currently opened file created via {@link #createFile()} if one exists.
     */
    private void saveFile() {
        if (mDriveServiceHelper != null && mOpenFileId != null) {
            Log.d(TAG, "Saving " + mOpenFileId);

            String fileName = "FILE NAME THIS IS";
            String fileContent = "FILE CONTENT THIS IS IS THIS";

            mDriveServiceHelper.saveFile(mOpenFileId, fileName, fileContent)
                    .addOnFailureListener(exception ->
                            Log.e(TAG, "Unable to save file via REST.", exception));
        }
    }



    /**
     * Queries the Drive REST API for files visible to this app and lists them in the content view.
     */
    private void query() {
        if (mDriveServiceHelper != null) {
            Log.d(TAG, "Querying for files.");

            mDriveServiceHelper.queryFiles()
                    .addOnSuccessListener(fileList -> {
                        StringBuilder builder = new StringBuilder();
                        for (File file : fileList.getFiles()) {
                            builder.append(file.getName()).append("\n");
                        }
                        String fileNames = builder.toString();

                        System.out.println("File List");
                        System.out.println(fileNames);

                        setReadOnlyMode();
                    })
                    .addOnFailureListener(exception -> Log.e(TAG, "Unable to query files.", exception));
        }
    }

    /**
     * Updates the UI to read-only mode.
     */
    private void setReadOnlyMode() {
        mOpenFileId = null;
    }

    /**
     * Updates the UI to read/write mode on the document identified by {@code fileId}.
     */
    private void setReadWriteMode(String fileId) {
        mOpenFileId = fileId;
        saveFile();
    }

    private void goToStartUpActivity(int id, boolean optimise) {
        tickTrackDatabase.storeNotOptimiseBool(optimise);
        tickTrackDatabase.storeStartUpFragmentID(id);
        Intent intent = new Intent(this, StartUpActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public Fragment getFragment(int id){
        if(id==1){
            return new CounterFragment();
        } else if(id==2){
            return new TimerFragment();
        } else if(id==3){
            return new StopwatchFragment();
        } else {
            return new CounterFragment();
        }
    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = item -> {
        switch (item.getItemId()) {
            case R.id.navigation_counter:
                if(!(tickTrackDatabase.retrieveCurrentFragmentNumber()==1 || tickTrackDatabase.retrieveCurrentFragmentNumber()==-1)) {
                    tickTrackDatabase.storeCurrentFragmentNumber(1);
                    openFragment(new CounterFragment());
                }
                return true;

            case R.id.navigation_stopwatch:
                if(!(tickTrackDatabase.retrieveCurrentFragmentNumber()==3)){
                    tickTrackDatabase.storeCurrentFragmentNumber(3);
                    openFragment(new StopwatchFragment());
                }
                return true;

            case R.id.navigation_timer:
                if(!(tickTrackDatabase.retrieveCurrentFragmentNumber()==2)) {
                    tickTrackDatabase.storeCurrentFragmentNumber(2);
                    openFragment(new TimerFragment());
                }
                return true;
        }
        return false;
    };

    public void openFragment(Fragment fragment) {
        FragmentManager manager = this.getSupportFragmentManager();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.nav_host_fragment, fragment);
        transaction.commit();
        manager.popBackStack();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.overflow_menu, menu);

        Typeface typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.roboto_regular);

        for (int i = 0; i < menu.size(); i++) {
            MenuItem mi = menu.getItem(i);
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu != null && subMenu.size() > 0) {
                for (int j = 0; j < subMenu.size(); j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem, typeface);
                }
            }
            applyFontToMenuItem(mi, typeface);
        }
        return super.onCreateOptionsMenu(menu);
    }

    private void applyFontToMenuItem(MenuItem mi, Typeface font) {
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new TypefaceSpanSetup("", font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.screensaverMenuItem:
                JsonHelper jsonHelper = new JsonHelper(getApplicationContext());
                jsonHelper.counterDataBackup(tickTrackDatabase.retrieveCounterList());
                jsonHelper.timerDataBackup(tickTrackDatabase.retrieveTimerList());
                Toast.makeText(getApplicationContext(),"Screensaver",Toast.LENGTH_SHORT).show();
                return false;

            case R.id.settingsMenuItem:
                Intent intent = new Intent(this, SettingsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return false;

            case R.id.feedbackMenuItem:
                Toast.makeText(getApplicationContext(),"Feedback",Toast.LENGTH_SHORT).show();
                return false;

            case R.id.aboutMenuItem:
                Toast.makeText(getApplicationContext(),"About",Toast.LENGTH_SHORT).show();
                return true;

            default:
                break;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(PowerSaverHelper.getIfAppIsWhiteListedFromBatteryOptimizations(this, getPackageName())
                .equals(PowerSaverHelper.WhiteListedInBatteryOptimizations.NOT_WHITE_LISTED) || tickTrackDatabase.retrieveFirstLaunch()){
            goToStartUpActivity(5, false);
        } else {
            if(isMyServiceRunning(StopwatchNotificationService.class, this)){
                stopNotificationService();
            }
            TickTrackThemeSetter.mainActivityTheme(navView, this, tickTrackDatabase, mainToolbar, tickTrackAppName);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(PowerSaverHelper.getIfAppIsWhiteListedFromBatteryOptimizations(this, getPackageName())
                .equals(PowerSaverHelper.WhiteListedInBatteryOptimizations.NOT_WHITE_LISTED) || tickTrackDatabase.retrieveFirstLaunch()){
            goToStartUpActivity(5, false);
        } else {
            if(isMyServiceRunning(StopwatchNotificationService.class, this)){
                stopNotificationService();
            }
            TickTrackThemeSetter.mainActivityTheme(navView, this, tickTrackDatabase, mainToolbar, tickTrackAppName);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(tickTrackDatabase.retrieveStopwatchData().size()>0){
            if(tickTrackDatabase.retrieveStopwatchData().get(0).isRunning()){
                if(!isMyServiceRunning(StopwatchNotificationService.class, this)){
                    setupCustomNotification();
                    notificationManagerCompat.notify(4,  notificationBuilder.build());
                    System.out.println("STOPWATCH RUNNING HAPPENED");
                    startNotificationService();
                }
            }
        }
    }

    public void stopNotificationService() {
        Intent intent = new Intent(this, StopwatchNotificationService.class);
        intent.setAction(StopwatchNotificationService.ACTION_STOP_STOPWATCH_SERVICE);
        startService(intent);
    }

    public void startNotificationService() {
        Intent intent = new Intent(this, StopwatchNotificationService.class);
        intent.setAction(StopwatchNotificationService.ACTION_START_STOPWATCH_SERVICE);
        startService(intent);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    private NotificationManagerCompat notificationManagerCompat;
    NotificationCompat.Builder notificationBuilder;
    private void setupCustomNotification(){

        notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());

        Intent resultIntent = new Intent(this, SoYouADeveloperHuh.class);
        resultIntent.putExtra("FragmentID", 3);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(4, PendingIntent.FLAG_UPDATE_CURRENT);


        Intent deleteIntent = new Intent(this, StopwatchNotificationService.class);
        deleteIntent.setAction(StopwatchNotificationService.ACTION_STOP_STOPWATCH_SERVICE);
        PendingIntent deletePendingIntent = PendingIntent.getService(this,
                4,
                deleteIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        notificationBuilder = new NotificationCompat.Builder(this, TickTrack.STOPWATCH_NOTIFICATION)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setOnlyAlertOnce(true)
                .setContentIntent(resultPendingIntent)
                .setDeleteIntent(deletePendingIntent)
                .setAutoCancel(true);


        if (android.os.Build.VERSION. SDK_INT >= android.os.Build.VERSION_CODES. O ) {
            notificationBuilder.setChannelId(TickTrack.STOPWATCH_NOTIFICATION);
        }

    }

}