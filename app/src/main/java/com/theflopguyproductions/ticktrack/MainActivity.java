package com.theflopguyproductions.ticktrack;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.theflopguyproductions.ticktrack.ui.counter.CounterFragment;
import com.theflopguyproductions.ticktrack.ui.home.HomeFragment;
import com.theflopguyproductions.ticktrack.ui.stopwatch.StopwatchFragment;
import com.theflopguyproductions.ticktrack.ui.timer.TimerFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    private Toolbar homeToolbar;
    private BottomNavigationView bottomNavigationView;
    public FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.nav_view);
        homeToolbar = findViewById(R.id.mainActivityToolbar);
        fab = findViewById(R.id.floatingButton);

        bottomNavigationView.setItemIconTintList(null);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        openFragment(HomeFragment.newInstance("", ""));
        floatingActionButton("Home",1);

        //floatingActionButton("Home",1);
//
//        final NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//
//        NavigationUI.setupWithNavController(bottomNavigationView, navController);
//
//        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
//            @Override
//            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
//
//                floatingActionButton(String.valueOf(navController.getCurrentDestination().getId()));
//            }
//        });

        overflowMenuSetup();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(Fragment fragment : getSupportFragmentManager().getFragments()) {
                    if(fragment instanceof HomeFragment) {
                        ((HomeFragment) fragment).someFunction();
                    }
                }
            }
        });


    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.navigation_home:
                            floatingActionButton("Home",1);
                            openFragment(HomeFragment.newInstance("", ""));
                            return true;
                        case R.id.navigation_counter:
                            floatingActionButton("Counter",2);
                            openFragment(CounterFragment.newInstance("", ""));
                            return true;
                        case R.id.navigation_stopwatch:
                            floatingActionButton("Stopwatch",3);
                            openFragment(StopwatchFragment.newInstance("", ""));
                            return true;
                        case R.id.navigation_timer:
                            floatingActionButton("Timer",4);
                            openFragment(TimerFragment.newInstance("", ""));
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
    private int drawablePlay = R.drawable.ic_baseline_play_arrow_24;;


    public void floatingActionButton(final String text, int fabStat){
        if(fabStat==1){
            animateFAB(drawablePlus, fab);
        }
        if(fabStat==2){
            dissolveFAB(fab);
        }
        if(fabStat==3){
            animateFAB(drawablePlay, fab);
        }
        if(fabStat==4){
            animateFAB(drawablePlay, fab);
        }


    }



    private void animateFAB(final int icon, final FloatingActionButton fab){
        fab.animate()
                .setDuration(100)
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

    private void dissolveFAB(final FloatingActionButton fab){
        fab.animate()
                .setDuration(100)
                .scaleX(1.1f)
                .scaleY(1.1f)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {

                        fab.animate()
                                .setDuration(80)
                                .scaleX(0)
                                .scaleY(0)
                                .start();
                    }
                })
                .start();
    }

}