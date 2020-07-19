package com.theflopguyproductions.ticktrack;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.theflopguyproductions.ticktrack.background.MyBroadcastReceiver;
import com.theflopguyproductions.ticktrack.ui.counter.CounterFragment;
import com.theflopguyproductions.ticktrack.ui.home.HomeFragment;
import com.theflopguyproductions.ticktrack.ui.stopwatch.StopwatchFragment;
import com.theflopguyproductions.ticktrack.ui.timer.TimerFragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class MainActivityToChange extends AppCompatActivity {

    private static final String ACTION_SNOOZE = "GUCCISNOOZE";
    private Toolbar homeToolbar;
    private static BottomNavigationView bottomNavigationView;
    public static FloatingActionButton fab;
    private static ConstraintLayout mainActivityLayout;
    private static Context staticContext;

    private HomeFragment homeFragment = new HomeFragment();
    private static StopwatchFragment stopwatchFragment = new StopwatchFragment();
    private CounterFragment counterFragment = new CounterFragment();
    private static TimerFragment timerFragment = new TimerFragment();
    private static boolean isDisabled = false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        staticContext = this;

        bottomNavigationView = findViewById(R.id.nav_view);
        homeToolbar = findViewById(R.id.mainActivityToolbar);
        fab = findViewById(R.id.floatingButton);
        mainActivityLayout = findViewById(R.id.constraintLayout);

        bottomNavigationView.setItemIconTintList(null);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        openFragment(new HomeFragment());
        animateFAB(drawablePlus, fab);

        overflowMenuSetup();

        notificationExample();

        fab.setOnClickListener(view -> {
            for(Fragment fragment : getSupportFragmentManager().getFragments()) {
                if(fragment instanceof HomeFragment) {
                    ((HomeFragment) fragment).fabClicked();
                }
                if(fragment instanceof CounterFragment) {
                    ((CounterFragment) fragment).fabClicked();
                }
                if(fragment instanceof StopwatchFragment) {
                    ((StopwatchFragment) fragment).fabClicked();
                }
                if(fragment instanceof TimerFragment) {
                    ((TimerFragment) fragment).fabClicked();
                }
            }
        });


    }

    private void notificationExample() {

        createNotificationChannel();

        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification_test_layout);
        contentView.setImageViewResource(R.id.image, R.mipmap.ic_launcher);
        contentView.setTextViewText(R.id.title, "Custom notification");
        contentView.setTextViewText(R.id.text, "This is a custom layout");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "TESTING")
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setCustomContentView(contentView)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_VIBRATE;

        Intent snoozeIntent = new Intent(this, MyBroadcastReceiver.class);
        snoozeIntent.setAction(ACTION_SNOOZE);
        snoozeIntent.putExtra("notification", notification);
        snoozeIntent.putExtra("Notification ID", 0);
        PendingIntent snoozePendingIntent =
                PendingIntent.getBroadcast(this, 0, snoozeIntent, 0);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(0, notification);

    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "TESTING";
            String description = "Testing Content Channel Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("TESTING", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.navigation_home:
                            animateFAB(drawablePlus, fab);
                            openFragment(homeFragment);
                            return true;

                        case R.id.navigation_counter:
                            animateFAB(drawablePlus, fab);
                            openFragment(counterFragment);
                            return true;

                        case R.id.navigation_stopwatch:
                            if(StopwatchFragment.isEnabled()){
                                animateFAB(drawablePause,fab);
                                openFragment(stopwatchFragment);
                            }
                            else{
                                animateFAB(drawablePlay, fab);
                                openFragment(StopwatchFragment.newInstance(false));
                            }
                            return true;

                        case R.id.navigation_timer:
                            if(TimerFragment.isEnabled()){
                                animateFAB(drawablePause, fab);
                                openFragment(timerFragment);
                            }
                            else{
                                animateFAB(drawablePlay, fab);
                                openFragment(TimerFragment.newInstance(false));
                            }

                            return true;
                    }
                    return false;
                }
            };

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void overflowMenuSetup(){
        homeToolbar.inflateMenu(R.menu.overflow_menu);

        homeToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.settings_menu:
                        Toast.makeText(getApplicationContext(),"Settings",Toast.LENGTH_SHORT).show();
                        // Not implemented here
                        return false;
                    case R.id.about_menu:
                        Toast.makeText(getApplicationContext(),"About",Toast.LENGTH_SHORT).show();
                        // Do Fragment menu item stuff here
                        return true;

                    default:
                        break;
                }

                return false;
            }
        });
    }

    private int drawablePlus = R.drawable.ic_add_white_24;
    private int drawablePlay = R.drawable.ic_play_white_24;
    private int drawablePause = R.drawable.ic_pause_white_24;

    public static void animateFAB(final int icon, final FloatingActionButton fab){
        fab.animate()
                .setDuration(150)
                .scaleX(1.1f)
                .scaleY(1.1f)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {

                        fab.animate()
                                .setDuration(80)
                                .scaleX(0.5f)
                                .scaleY(0.5f)
                                .withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        fab.setImageResource(icon);
                                        isDisabled=true;
                                        fab.animate()
                                                .setDuration(80)
                                                .scaleX(1.1f)
                                                .scaleY(1.1f)
                                                .withEndAction(new Runnable() {
                                                    @Override
                                                    public void run() {

                                                        fab.animate()
                                                                .setDuration(80)
                                                                .scaleX(1)
                                                                .scaleY(1)
                                                                .start();
                                                    }
                                                })
                                                .start();
                                    }
                                })
                                .start();
                    }
                }).start();
    }

    private static void dissolveFAB(final FloatingActionButton fab){

        fab.animate()
                .setDuration(100)
                .scaleX(1.1f)
                .scaleY(1.1f)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        fab.animate()
                                .setDuration(250)
                                .scaleX(0)
                                .scaleY(0)
                                .withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        fab.setImageResource(0);
                                    }
                                })
                                .start();
                    }
                })
                .start();
    }

    public static void counterActivity(int position){
        CounterFragment.counterLayout(staticContext,position);
    }

    public static void editAlarmActivity(int adapterPosition) {
        HomeFragment.openEditActivity(staticContext,adapterPosition);
    }

    public static void alarmOnOffToggle(int adapterPosition, boolean check) {
        HomeFragment.toggleAlarmOnOff(adapterPosition, check);
    }

}