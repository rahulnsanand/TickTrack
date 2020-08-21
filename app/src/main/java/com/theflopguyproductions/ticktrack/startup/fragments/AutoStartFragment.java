package com.theflopguyproductions.ticktrack.startup.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.theflopguyproductions.ticktrack.R;

public class AutoStartFragment extends Fragment {

    private OnAutoStartSetClickListener autoStartSetClickListener;


    private Button autoStartButton;
    private ScrollView autostartScroll;
    private boolean isScrolled = false;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_ticktrack_autostart, container, false);
        autoStartButton = root.findViewById(R.id.ticktrackFragmentAutoStartButton);
        autostartScroll = root.findViewById(R.id.ticktrackFragmentAutoStartScroll);

        autostartScroll.getViewTreeObserver()
                .addOnScrollChangedListener(() -> isScrolled = autostartScroll.getChildAt(0).getBottom()
                        <= (autostartScroll.getHeight() + autostartScroll.getScrollY()));

        autoStartButton.setOnClickListener(view -> {
            if (isScrolled) {
                autoStartSetClickListener.onAutoStartSetClickListener();
            } else {
                autostartScroll.fullScroll(View.FOCUS_DOWN);
            }
        });

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
