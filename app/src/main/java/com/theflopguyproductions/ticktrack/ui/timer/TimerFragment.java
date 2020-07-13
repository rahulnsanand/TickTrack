package com.theflopguyproductions.ticktrack.ui.timer;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.theflopguyproductions.ticktrack.MainActivity;
import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.ui.home.HomeFragment;
import com.theflopguyproductions.ticktrack.ui.stopwatch.StopwatchViewModel;

public class TimerFragment extends Fragment {

    public static boolean fabState;
    private TimerViewModel timerViewModel;

    private static final String FAB_STATE_PARAM = "param1";

    public static boolean isEnabled() {
        return fabState;
    }

    public TimerFragment() {
    }

    public static TimerFragment newInstance(boolean fabState) {
        TimerFragment fragment = new TimerFragment();
        Bundle args = new Bundle();
        args.putBoolean(FAB_STATE_PARAM, fabState);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            fabState = getArguments().getBoolean(FAB_STATE_PARAM);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        timerViewModel =
                ViewModelProviders.of(this).get(TimerViewModel.class);
        View root = inflater.inflate(R.layout.fragment_timer, container, false);
        final TextView textView = root.findViewById(R.id.text_timer);
        timerViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }

    private int drawablePlay = R.drawable.ic_play_white_24;
    private int drawablePause = R.drawable.ic_pause_white_24;

    public void fabClicked() {

        if(fabState){
            MainActivity.animateFAB(drawablePlay, MainActivity.fab);
            fabState=false;
        }
        else{
            MainActivity.animateFAB(drawablePause, MainActivity.fab);
            fabState=true;
        }

    }

}