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

public class ThemeFragment extends Fragment {

    private OnThemeSetClickListener onThemeSetClickListener;

    private Button themeSetButton;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_ticktrack_theme, container, false);
        themeSetButton = root.findViewById(R.id.ticktrackThemeButton);

        themeSetButton.setOnClickListener(view -> onThemeSetClickListener.onThemeSetClickListener());

        return root;
    }

    public interface OnThemeSetClickListener {
        void onThemeSetClickListener();
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            onThemeSetClickListener = (OnThemeSetClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement " + OnThemeSetClickListener.class.getName());
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        onThemeSetClickListener = null;
    }
}
