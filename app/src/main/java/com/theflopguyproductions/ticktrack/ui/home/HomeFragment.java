package com.theflopguyproductions.ticktrack.ui.home;

import android.os.Bundle;
import android.os.Handler;
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

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.theflopguyproductions.ticktrack.MainActivity;
import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.ui.utils.AnalogClock;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.os.Looper.getMainLooper;
import static android.text.format.DateUtils.SECOND_IN_MILLIS;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private TextView timeTextBig;
    final Handler someHandler = new Handler(getMainLooper());
    private SimpleDateFormat sdf = new SimpleDateFormat("hh:mma E, d MMMM ''yy");

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
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


    public void someFunction() {

        Toast.makeText(getContext(),"This worked",Toast.LENGTH_SHORT).show();

    }
}