package com.theflopguyproductions.ticktrack.startup;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.SoYouADeveloperHuh;
import com.theflopguyproductions.ticktrack.startup.fragments.AutoStartFragment;
import com.theflopguyproductions.ticktrack.startup.fragments.BackupRestoreFragment;
import com.theflopguyproductions.ticktrack.startup.fragments.BatteryOptimiseFragment;
import com.theflopguyproductions.ticktrack.startup.fragments.GoogleSignInFragment;
import com.theflopguyproductions.ticktrack.startup.fragments.IntroFragment;
import com.theflopguyproductions.ticktrack.startup.fragments.ThemeFragment;
import com.theflopguyproductions.ticktrack.startup.service.OptimiserService;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.helpers.PowerSaverHelper;

public class StartUpActivity extends AppCompatActivity implements IntroFragment.OnGetStartedClickListener, BatteryOptimiseFragment.BatteryOptimiseClickListener,
        ThemeFragment.OnThemeSetClickListener, AutoStartFragment.OnAutoStartSetClickListener, BackupRestoreFragment.SetupBackupAccountClickListener,
        GoogleSignInFragment.LaterClickListener, GoogleSignInFragment.SignInClickListener {

    private TickTrackDatabase tickTrackDatabase;
    private ConstraintLayout rootLayout;
    private int optimiseRequestNumber = 0, themeMode = 1;

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

        optimiseRequestNumber += 1;
        tickTrackDatabase.storeOptimiseRequestNumber(optimiseRequestNumber);

        setContentView(R.layout.activity_start_up);
        rootLayout = findViewById(R.id.StartUpActivityRootLayout);

        int receivedFragmentID = tickTrackDatabase.retrieveStartUpFragmentID();
        if(tickTrackDatabase.retrieveFirstLaunch()){
            openFragment(getFragment(1));
        } else {
            openFragment(getFragment(receivedFragmentID));
        }
    }

    private void setupTheme() {
        themeMode = tickTrackDatabase.getThemeMode();
        if(themeMode==1){
            rootLayout.setBackgroundResource(R.color.LightGray);
        } else {
            rootLayout.setBackgroundResource(R.color.Black);
        }
    }

    public Fragment getFragment(int id){
        if(id==1){
            return new IntroFragment();
        } else if(id==2){
            return new GoogleSignInFragment();
        } else if(id==3){
            return new BackupRestoreFragment();
        } else if(id==4){
            return new ThemeFragment();
        } else if(id==5){
            return new BatteryOptimiseFragment();
        } else if(id==6){
            return new AutoStartFragment();
        } else {
            return new IntroFragment();
        }
    }

    public void openFragment(Fragment fragment) {
        FragmentManager manager = this.getSupportFragmentManager();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.from_right, 0);
        transaction.replace(R.id.StartUpActivityFragmentContainer, fragment);
        transaction.commitNow();
        manager.popBackStack();
    }

    @Override
    public void onGetStartedClick() {
        openFragment(new ThemeFragment());
    }

    @Override
    public void onBackupSetClickListener() {
        openFragment(new ThemeFragment());
    }

    @Override
    public void onSignInClickListener() {
        tickTrackDatabase.storeFirstLaunch(false);

    }

    @Override
    public void onLaterClickListener() {
        tickTrackDatabase.storeFirstLaunch(false);
        openFragment(new ThemeFragment());
    }

    @Override
    public void onThemeSetClickListener() {
        setupTheme();

        openFragment(new BatteryOptimiseFragment());
    }
    @Override
    public void onBatteryOptimiseClickListener() {
        if(!isMyServiceRunning(OptimiserService.class)){
            startCheckService();
        }
        Intent intent = PowerSaverHelper.prepareIntentForWhiteListingOfBatteryOptimization(this, getPackageName(), false);
        if(intent!=null){
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
        Intent intent = new Intent(this, SoYouADeveloperHuh.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }


}