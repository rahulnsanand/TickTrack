package com.theflopguyproductions.ticktrack.ui.stopwatch;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.theflopguyproductions.ticktrack.MainActivityToChange;
import com.theflopguyproductions.ticktrack.R;

public class StopwatchFragment extends Fragment {

    private StopwatchViewModel stopwatchViewModel;

    public static boolean fabState;

    public static boolean isEnabled() {
        return fabState;
    }



    private static final String FAB_STATE_PARAM = "param1";

    public StopwatchFragment() {
    }

    public static StopwatchFragment newInstance(boolean fabState) {
        StopwatchFragment fragment = new StopwatchFragment();
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
    public View onCreateView(@NonNull LayoutInflater inflater,@Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        stopwatchViewModel =
                ViewModelProviders.of(this).get(StopwatchViewModel.class);
        View root = inflater.inflate(R.layout.fragment_stopwatch, container, false);
        final TextView textView = root.findViewById(R.id.text_notifications);
        stopwatchViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
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
            MainActivityToChange.animateFAB(drawablePlay, MainActivityToChange.fab);
            fabState=false;
        }
        else{
            MainActivityToChange.animateFAB(drawablePause, MainActivityToChange.fab);
            fabState=true;
        }

    }
}