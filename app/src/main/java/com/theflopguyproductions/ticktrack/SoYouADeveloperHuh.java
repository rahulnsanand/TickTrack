package com.theflopguyproductions.ticktrack;

import static android.app.AlarmManager.INTERVAL_DAY;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.theflopguyproductions.ticktrack.about.AboutActivity;
import com.theflopguyproductions.ticktrack.application.TickTrack;
import com.theflopguyproductions.ticktrack.contributors.ContributorsActivity;
import com.theflopguyproductions.ticktrack.dialogs.DeleteTimer;
import com.theflopguyproductions.ticktrack.receivers.CounterMilestoneReceiver;
import com.theflopguyproductions.ticktrack.screensaver.ScreensaverActivity;
import com.theflopguyproductions.ticktrack.settings.SettingsActivity;
import com.theflopguyproductions.ticktrack.startup.StartUpActivity;
import com.theflopguyproductions.ticktrack.stopwatch.service.StopwatchNotificationService;
import com.theflopguyproductions.ticktrack.ui.counter.CounterFragment;
import com.theflopguyproductions.ticktrack.ui.stopwatch.StopwatchFragment;
import com.theflopguyproductions.ticktrack.ui.timer.QuickTimerCreatorFragment;
import com.theflopguyproductions.ticktrack.ui.timer.TimerFragment;
import com.theflopguyproductions.ticktrack.ui.timer.TimerRecyclerFragment;
import com.theflopguyproductions.ticktrack.utils.RateUsUtil;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackFirebaseDatabase;
import com.theflopguyproductions.ticktrack.utils.font.TypefaceSpanSetup;
import com.theflopguyproductions.ticktrack.utils.helpers.AutoStartPermissionHelper;
import com.theflopguyproductions.ticktrack.utils.helpers.PowerSaverHelper;
import com.theflopguyproductions.ticktrack.utils.helpers.TickTrackThemeSetter;

import java.util.Objects;

public class SoYouADeveloperHuh extends AppCompatActivity implements QuickTimerCreatorFragment.QuickTimerCreateListener, TimerRecyclerFragment.RootLayoutClickListener {

    TickTrackDatabase tickTrackDatabase;

    private Toolbar mainToolbar;
    private TextView tickTrackAppName;
    private BottomNavigationView navView;
    private ConstraintLayout container;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_so_you_a_developer_huh);

        tickTrackDatabase = new TickTrackDatabase(this);

        mainToolbar = findViewById(R.id.mainActivityToolbar);
        tickTrackAppName = findViewById(R.id.ticktrackAppName);
        container = findViewById(R.id.container);
        navView = findViewById(R.id.nav_view);

        if (tickTrackDatabase.getThemeMode() == 1) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.HoloLightGray));
        } else {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.HoloBlack));
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        navView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        setSupportActionBar(mainToolbar);
        setTitle("");
        System.out.println("ActivityManager: Displayed SoYouDev OnPostCreate " + System.currentTimeMillis());
    }

    private void goToStartUpActivity(int id, boolean optimise) {
        tickTrackDatabase.setNotOptimised(optimise);
        tickTrackDatabase.storeStartUpFragmentID(id);
        Intent intent = new Intent(this, StartUpActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public Fragment getFragment(int id) {
        if (id == 1) {
            return new CounterFragment();
        } else if (id == 2) {
            return new TimerFragment();
        } else if (id == 3) {
            return new StopwatchFragment();
        } else {
            return new CounterFragment();
        }
    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = item -> {

        switch (item.getItemId()) {
            case R.id.navigation_counter:
                if (!(tickTrackDatabase.retrieveCurrentFragmentNumber() == 1 || tickTrackDatabase.retrieveCurrentFragmentNumber() == -1)) {
                    tickTrackDatabase.storeCurrentFragmentNumber(1);
                    openFragment(new CounterFragment());
                }
                item.setChecked(true);
                return true;

            case R.id.navigation_stopwatch:
                if (!(tickTrackDatabase.retrieveCurrentFragmentNumber() == 3)) {
                    tickTrackDatabase.storeCurrentFragmentNumber(3);
                    openFragment(new StopwatchFragment());
                }
                item.setChecked(true);
                return true;

            case R.id.navigation_timer:
                if (!(tickTrackDatabase.retrieveCurrentFragmentNumber() == 2)) {
                    tickTrackDatabase.storeCurrentFragmentNumber(2);
                    openFragment(new TimerFragment());
                }
                item.setChecked(true);
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

        Typeface typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.apercu_regular);

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
                startActivity(new Intent(this, ScreensaverActivity.class));
                return false;

            case R.id.settingsMenuItem:
                Intent intent = new Intent(this, SettingsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return false;

            case R.id.feedbackMenuItem:
                sendFeedback();
                return false;

            case R.id.aboutMenuItem:
                Intent intentAbout = new Intent(this, AboutActivity.class);
                intentAbout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentAbout);
                return true;
            case R.id.contributorsMenuItem:
                Intent intentContributor = new Intent(this, ContributorsActivity.class);
                intentContributor.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentContributor);
                return true;
            default:
                break;
        }
        return false;
    }

    private void sendFeedback() {

        String feedbackSubject = "TickTrack Feedback";
        String sendToEmail = "ticktrackapp@gmail.com";
        String feedbackMessage = "Hey Developer,\n\n\t";

        final Intent _Intent = new Intent(android.content.Intent.ACTION_SENDTO);
        _Intent.setData(Uri.parse("mailto:"));
        _Intent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{sendToEmail});
        _Intent.putExtra(android.content.Intent.EXTRA_SUBJECT, feedbackSubject);
        _Intent.putExtra(android.content.Intent.EXTRA_TEXT, feedbackMessage);
        if (_Intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(_Intent, 100);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        int restoreMore = new TickTrackFirebaseDatabase(this).isRestoreInitMode();
        if (restoreMore == -1 || restoreMore == 1 ||
                new TickTrackFirebaseDatabase(this).isRestoreMode()) {
            goToStartUpActivity(3, true);
        } else if (PowerSaverHelper.getIfAppIsWhiteListedFromBatteryOptimizations(this, getPackageName())
                .equals(PowerSaverHelper.WhiteListedInBatteryOptimizations.NOT_WHITE_LISTED)) {
            goToStartUpActivity(5, false);
        } else if (AutoStartPermissionHelper.getInstance().isAutoStartPermissionAvailable(this) && !tickTrackDatabase.isAutoStart()) {
            AutoStartPermissionHelper.getInstance().getAutoStartPermission(this);
            tickTrackDatabase.setAutoStart(true);
        } else if (tickTrackDatabase.retrieveFirstLaunch()) {
            goToStartUpActivity(1, false);
        } else {
            if (isMyServiceRunning(this)) {
                stopNotificationService();
            }
            Bundle extras = getIntent().getExtras();
            String shortcutAction;
            System.out.println(extras + "<<<<<<<<<<<<<<<<<<<<<<<<<");
            if (extras != null) {
                shortcutAction = (String) Objects.requireNonNull(getIntent().getExtras()).get("fragmentID");
                System.out.println(shortcutAction);
                if (shortcutAction != null) {
                    switch (shortcutAction) {
                        case "timerCreate":
                        case "quickTimerCreate":
                            tickTrackDatabase.storeCurrentFragmentNumber(2);
                            openFragment(new TimerFragment(shortcutAction));
                            break;
                        case "stopwatchCreate":
                            tickTrackDatabase.storeCurrentFragmentNumber(3);
                            openFragment(new StopwatchFragment(shortcutAction));
                            break;
                        case "counterCreate":
                            tickTrackDatabase.storeCurrentFragmentNumber(1);
                            openFragment(new CounterFragment(shortcutAction));
                            break;
                        case "screensaverCreate":
                            startActivity(new Intent(this, ScreensaverActivity.class));
                            break;
                    }
                    getIntent().removeExtra("fragmentID");
                }
                if (Objects.equals(extras.get("NOTIFICATION_TYPE"), "IS_UPDATE")) {
                    String versionCode = extras.getString("VERSION");
                    String isCompulsory = extras.getString("IS_COMPULSORY");
                    tickTrackDatabase.storeUpdateVersion(versionCode);
                    tickTrackDatabase.setUpdateCompulsion(isCompulsory);
                    tickTrackDatabase.setUpdateTime(System.currentTimeMillis());
                }
            } else {
                int currentFragment = tickTrackDatabase.retrieveCurrentFragmentNumber();
                if (currentFragment == 1) {
                    navView.setSelectedItemId(R.id.navigation_counter);
                } else if (currentFragment == 2) {
                    navView.setSelectedItemId(R.id.navigation_timer);
                } else if (currentFragment == 3) {
                    navView.setSelectedItemId(R.id.navigation_stopwatch);
                } else {
                    navView.setSelectedItemId(R.id.navigation_counter);
                }
                openFragment(getFragment(tickTrackDatabase.retrieveCurrentFragmentNumber()));

                if (tickTrackDatabase.notAlreadyDoneCheck()) {
                    int appLaunchNumber = tickTrackDatabase.getAppLaunchNumber();
                    if (appLaunchNumber <= 30) {
                        if (appLaunchNumber == 10 || appLaunchNumber == 20 || appLaunchNumber == 30) {
                            setupRateUsDialog();
                        }
                    } else {
                        if (appLaunchNumber % 10 == 0) {
                            setupRateUsDialog();
                        }
                    }
                    appLaunchNumber++;
                    tickTrackDatabase.setAppLaunchNumber(appLaunchNumber);
                }
            }


            int versionCode = BuildConfig.VERSION_CODE;
            String versionName = BuildConfig.VERSION_NAME;
            if (versionCode < tickTrackDatabase.getUpdateVersionCode()) {
                if (tickTrackDatabase.getUpdateTime() != -1
                        && !((System.currentTimeMillis() - tickTrackDatabase.getUpdateTime()) > (1000 * 60 * 60 * 24 * 7))
                        || tickTrackDatabase.getUpdateCompulsion().equals("true")) {
                    showUpdateDialog(tickTrackDatabase.getUpdateCompulsion());
                } else {
                    tickTrackDatabase.storeUpdateVersion(versionName);
                    tickTrackDatabase.setUpdateCompulsion("false");
                    tickTrackDatabase.setUpdateTime(-1);
                    tickTrackDatabase.storeUpdateVersionCode(versionCode);
                }
            } else {
                tickTrackDatabase.storeUpdateVersion(versionName);
                tickTrackDatabase.setUpdateCompulsion("false");
                tickTrackDatabase.setUpdateTime(-1);
                tickTrackDatabase.storeUpdateVersionCode(versionCode);
            }

        }
        System.out.println("ActivityManager: Displayed SoYouDev onResume " + System.currentTimeMillis());
    }

    private void showUpdateDialog(String updateCompulsion) {
        DeleteTimer deleteTimer = new DeleteTimer(this);
        deleteTimer.setCancelable(false);
        deleteTimer.show();
        if (updateCompulsion.equals("true")) {
            deleteTimer.noButton.setVisibility(View.GONE);
        }
        deleteTimer.yesButton.setText("Cool, let's update!");
        String currentVersion = tickTrackDatabase.getUpdateVersion();
        if (currentVersion != null) {
            deleteTimer.dialogMessage.setText("The latest version " + currentVersion + ", is now available for you!\nUpdate now so you don't miss out on awesome features and dreadful bug fixes!");
        } else {
            deleteTimer.dialogMessage.setText("The latest version is now available for you!\nUpdate now so you don't miss out on awesome features and dreadful bug fixes!");
        }
        deleteTimer.dialogTitle.setText("A newer more awesome version is available!");
        deleteTimer.yesButton.setOnClickListener(view -> {
            deleteTimer.dismiss();
            RateUsUtil rateUsUtil = new RateUsUtil(this);
            rateUsUtil.rateApp();
        });
        deleteTimer.noButton.setText("Nah, maybe later");

        int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;
        deleteTimer.noButton.setOnClickListener(view -> {
            deleteTimer.dismiss();
//            tickTrackDatabase.storeUpdateVersion(versionName);
//            tickTrackDatabase.setUpdateCompulsion("false");
//            tickTrackDatabase.setUpdateTime(-1);
//            tickTrackDatabase.storeUpdateVersionCode(versionCode);
        });
    }

    private void setupRateUsDialog() {
        DeleteTimer deleteTimer = new DeleteTimer(this);
        deleteTimer.show();
        deleteTimer.yesButton.setText("Alright, Let's do this");

        deleteTimer.dialogTitle.setText("Hey, This is awkward...");
        deleteTimer.dialogMessage.setText("It took a lot of effort and dedication to develop this app and keep it ad-free. Show us your support by just leaving a rating/feedback.");
        deleteTimer.yesButton.setOnClickListener(view -> {
            deleteTimer.dismiss();
            RateUsUtil rateUsUtil = new RateUsUtil(this);
            rateUsUtil.rateApp();
            tickTrackDatabase.setRatedPossibly(true);
        });
        if (tickTrackDatabase.isRatedPossibly()) {
            deleteTimer.noButton.setText("I've already done this");
            tickTrackDatabase.setAlreadyDoneCheck(true);
        } else {
            deleteTimer.noButton.setText("Nah, Maybe Later");
        }
        deleteTimer.noButton.setOnClickListener(view -> deleteTimer.dismiss());
    }

    @Override
    protected void onStart() {
        super.onStart();
        TickTrackThemeSetter.mainActivityTheme(navView, this, tickTrackDatabase, mainToolbar, tickTrackAppName, container);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, CounterMilestoneReceiver.class);
        intent.setAction(CounterMilestoneReceiver.ACTION_COUNTER_MILESTONE_REMINDER);
        PendingIntent alarmPendingIntent = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmPendingIntent = PendingIntent.getBroadcast(this, 321, intent, PendingIntent.FLAG_MUTABLE);
        } else {
            alarmPendingIntent = PendingIntent.getBroadcast(this, 321, intent, 0);
        }
        alarmManager.cancel(alarmPendingIntent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (tickTrackDatabase.retrieveStopwatchData().size() > 0) {
            if (tickTrackDatabase.retrieveStopwatchData().get(0).isRunning()) {
                System.out.println("STOPWATCH RUNNING HAPPENED");
                if (!isMyServiceRunning(this)) {
                    setupCustomNotification();
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    notificationManagerCompat.notify(4, notificationBuilder.build());
                    System.out.println("STOPWATCH RUNNING HAPPENED");
                    startNotificationService();
                }
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("ON STOP MAIN ACTIVITY");

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, CounterMilestoneReceiver.class);
        intent.setAction(CounterMilestoneReceiver.ACTION_COUNTER_MILESTONE_REMINDER);
        PendingIntent alarmPendingIntent = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmPendingIntent = PendingIntent.getBroadcast(this, 321, intent, PendingIntent.FLAG_MUTABLE);
        } else {
            alarmPendingIntent = PendingIntent.getBroadcast(this, 321, intent, 0);
        }
        alarmManager.setInexactRepeating (
                AlarmManager.RTC_WAKEUP,
                INTERVAL_DAY,
                INTERVAL_DAY*7L,
                alarmPendingIntent
        );
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

    private boolean isMyServiceRunning(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (StopwatchNotificationService.class.getName().equals(service.service.getClassName())) {
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
                null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            resultPendingIntent = stackBuilder.getPendingIntent(4, PendingIntent.FLAG_MUTABLE);
        } else {
            resultPendingIntent = stackBuilder.getPendingIntent(4, PendingIntent.FLAG_UPDATE_CURRENT);
        }


        Intent deleteIntent = new Intent(this, StopwatchNotificationService.class);
        deleteIntent.setAction(StopwatchNotificationService.ACTION_STOP_STOPWATCH_SERVICE);
        PendingIntent deletePendingIntent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            deletePendingIntent = PendingIntent.getService(this,
                    4,
                    deleteIntent,
                    PendingIntent.FLAG_MUTABLE);
        } else {
            deletePendingIntent = PendingIntent.getService(this,
                    4,
                    deleteIntent,
                    PendingIntent.FLAG_CANCEL_CURRENT);
        }

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

    @Override
    public void onCreatedListener() {
        openFragment(new TimerFragment());
    }

    @Override
    public void onRootLayoutClick() {
        System.out.println("CLICKED");
        TimerFragment.onRootLayoutClick();
    }
}