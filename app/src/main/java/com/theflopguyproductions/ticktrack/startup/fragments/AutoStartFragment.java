package com.theflopguyproductions.ticktrack.startup.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.theflopguyproductions.ticktrack.R;

public class AutoStartFragment extends Fragment {

    private OnAutoStartSetClickListener autoStartSetClickListener;


    private Button autoStartButton;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_ticktrack_autostart, container, false);
        autoStartButton = root.findViewById(R.id.ticktrackAutoStartButton);

        autoStartButton.setOnClickListener(view -> autoStartSetClickListener.onAutoStartSetClickListener());

        return root;
    }


    public interface OnAutoStartSetClickListener {
        void onAutoStartSetClickListener();
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            autoStartSetClickListener = (OnAutoStartSetClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement " + OnAutoStartSetClickListener.class.getName());
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        autoStartSetClickListener = null;
    }


}
