package com.theflopguyproductions.ticktrack;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.theflopguyproductions.ticktrack.application.TickTrack;
import com.theflopguyproductions.ticktrack.dialogs.MissedItemDialog;
import com.theflopguyproductions.ticktrack.startup.StartUpActivity;
import com.theflopguyproductions.ticktrack.stopwatch.service.StopwatchNotificationService;
import com.theflopguyproductions.ticktrack.ui.counter.CounterFragment;
import com.theflopguyproductions.ticktrack.ui.settings.SettingsActivity;
import com.theflopguyproductions.ticktrack.ui.stopwatch.StopwatchFragment;
import com.theflopguyproductions.ticktrack.ui.timer.TimerFragment;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.font.TypefaceSpanSetup;
import com.theflopguyproductions.ticktrack.utils.helpers.AutoStartPermissionHelper;
import com.theflopguyproductions.ticktrack.utils.helpers.PowerSaverHelper;
import com.theflopguyproductions.ticktrack.utils.helpers.TickTrackThemeSetter;

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

        if(PowerSaverHelper.getIfAppIsWhiteListedFromBatteryOptimizations(this, getPackageName())
                .equals(PowerSaverHelper.WhiteListedInBatteryOptimizations.NOT_WHITE_LISTED) || tickTrackDatabase.retrieveFirstLaunch()){

            goToStartUpActivity();

        } else {

            int receivedFragmentID = tickTrackDatabase.retrieveCurrentFragmentNumber();
            openFragment(getFragment(receivedFragmentID));

            boolean setHappen = AutoStartPermissionHelper.getInstance().getAutoStartPermission(getApplicationContext());
            boolean isAvailable = AutoStartPermissionHelper.getInstance().isAutoStartPermissionAvailable(this);



        }
    }

    private void goToStartUpActivity() {
        tickTrackDatabase.storeNotOptimiseBool(false);
        tickTrackDatabase.storeStartUpFragmentID(5);
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
            goToStartUpActivity();
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
            goToStartUpActivity();
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