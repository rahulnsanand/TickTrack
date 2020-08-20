package com.theflopguyproductions.ticktrack.startup;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.SoYouADeveloperHuh;
import com.theflopguyproductions.ticktrack.startup.fragments.AutoStartFragment;
import com.theflopguyproductions.ticktrack.startup.fragments.BatteryOptimiseFragment;
import com.theflopguyproductions.ticktrack.startup.fragments.IntroFragment;
import com.theflopguyproductions.ticktrack.startup.fragments.ThemeFragment;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;

public class StartUpActivity extends AppCompatActivity implements IntroFragment.OnGetStartedClickListener, BatteryOptimiseFragment.BatteryOptimiseClickListener,
        ThemeFragment.OnThemeSetClickListener, AutoStartFragment.OnAutoStartSetClickListener {

    private TickTrackDatabase tickTrackDatabase;

    private int optimiseRequestNumber = 0;

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

        int receivedFragmentID = tickTrackDatabase.retrieveStartUpFragmentID();
        openFragment(getFragment(receivedFragmentID));

    }

    public Fragment getFragment(int id){
        if(id==1){
            return new IntroFragment();
        } else if(id==2){
            return new ThemeFragment();
        } else if(id==3){
            return new BatteryOptimiseFragment();
        } else if(id==4){
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
    public void onThemeSetClickListener() {
        openFragment(new BatteryOptimiseFragment());
    }
    @Override
    public void onBatteryOptimiseClickListener() {
        openFragment(new AutoStartFragment());
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