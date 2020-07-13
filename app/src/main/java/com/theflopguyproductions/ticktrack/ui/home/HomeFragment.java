package com.theflopguyproductions.ticktrack.ui.home;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.theflopguyproductions.ticktrack.R;

import java.text.SimpleDateFormat;

import static android.os.Looper.getMainLooper;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private TextView timeTextBig;
    final Handler someHandler = new Handler(getMainLooper());
    private SimpleDateFormat sdf = new SimpleDateFormat("hh:mma E, d MMMM ''yy");

    public static boolean isEnabled;
    public static boolean isDisabled;

    public boolean isEnabled() {
        return isEnabled;
    }


    public boolean isDisabled() {
        return isDisabled;
    }

    private static final String ENABLED_PARAM = "param1";
    private static final String DISABLED_PARAM = "param2";

    public HomeFragment() {
    }

    public static HomeFragment newInstance(boolean enabled, boolean disabled) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putBoolean(ENABLED_PARAM, enabled);
        args.putBoolean(DISABLED_PARAM, disabled);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isEnabled = getArguments().getBoolean(ENABLED_PARAM);
            isDisabled = getArguments().getBoolean(DISABLED_PARAM);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        timeTextBig = root.findViewById(R.id.expandedTimeText);

        someHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                timeTextBig.setText(sdf.format(System.currentTimeMillis()));
                someHandler.postDelayed(this, 1000);
            }
        }, 0);


        return root;
    }


    public void fabClicked() {

        Toast.makeText(getContext(),"Add Alarm",Toast.LENGTH_SHORT).show();

    }
}