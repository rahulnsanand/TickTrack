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

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.ui.home.HomeFragment;

public class StopwatchFragment extends Fragment {

    private StopwatchViewModel stopwatchViewModel;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public static StopwatchFragment newInstance(String param1, String param2) {
        StopwatchFragment fragment = new StopwatchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public StopwatchFragment() {
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
}