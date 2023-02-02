package com.theflopguyproductions.ticktrack.startup;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.SoYouADeveloperHuh;
import com.theflopguyproductions.ticktrack.startup.fragments.AutoStartFragment;
import com.theflopguyproductions.ticktrack.startup.fragments.BatteryOptimiseFragment;
import com.theflopguyproductions.ticktrack.startup.fragments.IntroFragment;
import com.theflopguyproductions.ticktrack.startup.fragments.LoginFragment;
import com.theflopguyproductions.ticktrack.startup.fragments.NotificationFragment;
import com.theflopguyproductions.ticktrack.startup.fragments.RestoreFragment;
import com.theflopguyproductions.ticktrack.startup.fragments.ThemeFragment;
import com.theflopguyproductions.ticktrack.startup.service.OptimiserService;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.helpers.AutoStartPermissionHelper;
import com.theflopguyproductions.ticktrack.utils.helpers.PowerSaverHelper;

public class StartUpActivity extends AppCompatActivity implements IntroFragment.OnGetStartedClickListener, BatteryOptimiseFragment.BatteryOptimiseClickListener,
        ThemeFragment.OnThemeSetClickListener, AutoStartFragment.OnAutoStartSetClickListener, LoginFragment.LoginClickListeners, RestoreFragment.StartFreshListener,
        NotificationFragment.OnNotificationSetClickListener {

    public static final String ACTION_SETTINGS_ACCOUNT_ADD = "ACTION_SETTINGS_ACCOUNT_ADD";

    private TickTrackDatabase tickTrackDatabase;
    private ConstraintLayout rootLayout;
    private int optimiseRequestNumber = 0, themeMode = 1;
    private String receivedAction = null;

    @Override
    protected void onStart() {
        super.onStart();
        setupTheme();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        tickTrackDatabase = new TickTrackDatabase(this);
        optimiseRequestNumber = tickTrackDatabase.retrieveOptimiseRequestNumber();

        receivedAction = getIntent().getAction();
        System.out.println("STARTUP ACTIVITY RECEIVED " + receivedAction);

        optimiseRequestNumber += 1;
        tickTrackDatabase.storeOptimiseRequestNumber(optimiseRequestNumber);

        setContentView(R.layout.activity_start_up);
        rootLayout = findViewById(R.id.StartUpActivityRootLayout);

        int receivedFragmentID = tickTrackDatabase.retrieveStartUpFragmentID();
        if (tickTrackDatabase.retrieveFirstLaunch()) {
            openFragment(getFragment(1));
        } else {
            openFragment(getFragment(receivedFragmentID));
        }

        if (tickTrackDatabase.getThemeMode() == 1) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.HoloLightGray));
        } else {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.HoloBlack));
        }
    }

    private void setupTheme() {
        themeMode = tickTrackDatabase.getThemeMode();
        if (themeMode == 1) {
            rootLayout.setBackgroundResource(R.color.LightGray);
        } else {
            rootLayout.setBackgroundResource(R.color.Black);
        }
    }

    public Fragment getFragment(int id) {
        if (id == 1) {
            return new IntroFragment();
        } else if (id == 2) {
            return new LoginFragment(receivedAction);
        } else if (id == 3) {
            return new RestoreFragment(receivedAction);
        } else if (id == 4) {
            return new ThemeFragment();
        } else if (id == 5) {
            return new NotificationFragment();
        } else if (id == 6) {
            return new BatteryOptimiseFragment();
        } else if (id == 7) {
            return new AutoStartFragment();
        } else {
            return new IntroFragment();
        }
    }

    public void openFragment(Fragment fragment) {
        FragmentManager manager = this.getSupportFragmentManager();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, 0);
        transaction.replace(R.id.StartUpActivityFragmentContainer, fragment);
        transaction.commitNow();
        manager.popBackStack();
    }

    @Override
    public void onGetStartedClick() {
        tickTrackDatabase.storeFirstLaunch(false);
        openFragment(new LoginFragment(receivedAction));
    }


    @Override
    public void onLaterClickListener() {
        openFragment(new ThemeFragment());
    }

    @Override
    public void onStartFreshClickListener(boolean nextFragment) {
        if (nextFragment) {
            openFragment(new ThemeFragment());
        } else {
            if (tickTrackDatabase.retrieveFirstLaunch()) {
                openFragment(new BatteryOptimiseFragment());
            } else {
                startActivity(new Intent(this, SoYouADeveloperHuh.class));
            }
        }
    }

    @Override
    public void onThemeSetClickListener() {
        setupTheme();
        openFragment(new NotificationFragment());

    }

    @Override
    public void onBatteryOptimiseClickListener() {
        if (!isMyServiceRunning(OptimiserService.class)) {
            startCheckService();
        }
        Intent intent = PowerSaverHelper.prepareIntentForWhiteListingOfBatteryOptimization(this, getPackageName(), false);
        if (intent != null) {
            startActivity(intent);
        }
    }

    private void startCheckService() {
        Intent intent = new Intent(this, OptimiserService.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setAction(OptimiserService.ACTION_BATTERY_OPTIMISE_CHECK_START);
        startService(intent);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onAutoStartSetClickListener() {
        if (AutoStartPermissionHelper.getInstance().isAutoStartPermissionAvailable(this) && !AutoStartPermissionHelper.getInstance().getAutoStartPermission(this)) {
            AutoStartPermissionHelper.getInstance().getAutoStartPermission(this);
        } else {
            Intent intent = new Intent(this, SoYouADeveloperHuh.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    @Override
    public void OnNotificationSetupClickListener() {
        requestNotificationPermission();
    }

    private void requestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NOTIFICATION_POLICY) == PackageManager.PERMISSION_GRANTED)
            return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 2422);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_NOTIFICATION_POLICY}, 2422);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 2422) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Awesome!", Toast.LENGTH_LONG).show();
                openFragment(new BatteryOptimiseFragment());
            } else {
                Toast.makeText(this, "Oops.. you just denied the permission!", Toast.LENGTH_LONG).show();
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.POST_NOTIFICATIONS)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 2422);
                        openFragment(new NotificationFragment());
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_NOTIFICATION_POLICY}, 2422);
                        openFragment(new NotificationFragment());
                    }
                } else {
                    Toast.makeText(this, "You can always enable this later in Settings Tab", Toast.LENGTH_LONG).show();
                    openFragment(new BatteryOptimiseFragment());
                }

            }
        }
    }

    @Override
    public void onNotificationSkipClickListener() {
        Toast.makeText(this, "You can always enable this later in Settings Tab", Toast.LENGTH_LONG).show();
        openFragment(new BatteryOptimiseFragment());
    }

}