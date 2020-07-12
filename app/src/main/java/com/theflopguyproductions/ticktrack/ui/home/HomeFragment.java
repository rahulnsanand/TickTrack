package com.theflopguyproductions.ticktrack.ui.home;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.theflopguyproductions.ticktrack.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private Toolbar homeToolbar;
    private TextView timeTextBig;
    private TextView  timeTextSmall;
    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        homeToolbar = root.findViewById(R.id.miniToolbar);
        timeTextBig = root.findViewById(R.id.expandedTimeText);
        timeTextSmall = root.findViewById(R.id.miniTimeText);

        new Runnable(){

        }

        Thread clockThread = new Thread(){
            @Override
            public void run() {
                super.run();
                timeTextSmall.setText(sdf.format(new Date()));
                timeTextBig.setText(sdf.format(new Date()));
            }
        };
        clockThread.start();
        overflowMenuSetup();

        return root;
    }

    public void overflowMenuSetup(){
        homeToolbar.inflateMenu(R.menu.overflow_menu);

        homeToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.settings_menu:
                        Toast.makeText(getContext(),"Settings",Toast.LENGTH_SHORT).show();
                        // Not implemented here
                        return false;
                    case R.id.about_menu:
                        Toast.makeText(getContext(),"About",Toast.LENGTH_SHORT).show();
                        // Do Fragment menu item stuff here
                        return true;

                    default:
                        break;
                }

                return false;
            }
        });
    }

}