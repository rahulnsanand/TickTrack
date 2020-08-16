package com.theflopguyproductions.ticktrack;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.theflopguyproductions.ticktrack.ui.counter.CounterFragment;
import com.theflopguyproductions.ticktrack.ui.settings.SettingsActivity;
import com.theflopguyproductions.ticktrack.ui.stopwatch.StopwatchFragment;
import com.theflopguyproductions.ticktrack.ui.timer.TimerFragment;
import com.theflopguyproductions.ticktrack.utils.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.TickTrackThemeSetter;

public class SoYouADeveloperHuh extends AppCompatActivity {

    TickTrackDatabase tickTrackDatabase;

    private Toolbar mainToolbar;
    private TextView tickTrackAppName;
    private BottomNavigationView navView;
    private int defaultFragmentID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_so_you_a_developer_huh);
        mainToolbar = findViewById(R.id.mainActivityToolbar);
        tickTrackAppName = findViewById(R.id.ticktrackAppName);
        setSupportActionBar(mainToolbar);
        setTitle("");

        int receivedFragmentID = getIntent().getIntExtra("FragmentID",defaultFragmentID);

        openFragment(getFragment(receivedFragmentID));

        tickTrackDatabase = new TickTrackDatabase(this);

        navView = findViewById(R.id.nav_view);


//        overflowMenuSetup();

        navView.setItemIconTintList(null);
        navView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);


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
                openFragment(new CounterFragment());
                return true;

            case R.id.navigation_stopwatch:
                openFragment(new StopwatchFragment());
                return true;

            case R.id.navigation_timer:
                openFragment(new TimerFragment());
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

    public void overflowMenuSetup(){
        mainToolbar.inflateMenu(R.menu.overflow_menu);

        mainToolbar.setOnMenuItemClickListener(item -> {

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
        });
    }

    private void styleMenuButton() {
        // Find the menu item you want to style
        View view = findViewById(R.id.settingsMenuItem);

        // Cast to a TextView instance if the menu item was found
        if (view instanceof TextView) {
            ((TextView) view).setTextColor( Color.BLUE ); // Make text colour blue
            ((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_SP, 24); // Increase font size
            System.out.println("Changed");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.overflow_menu, menu);

        Typeface typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.sourcecodepro_medium);

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
        mNewTitle.setSpan(new CustomTypefaceSpan("", font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
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
        TickTrackThemeSetter.mainActivityTheme(navView, this, tickTrackDatabase, mainToolbar, tickTrackAppName);
    }

    @Override
    protected void onStart() {
        super.onStart();

        TickTrackThemeSetter.mainActivityTheme(navView, this, tickTrackDatabase, mainToolbar, tickTrackAppName);

    }

    @Override
    protected void onStop() {
        super.onStop();


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}