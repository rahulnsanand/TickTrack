package com.theflopguyproductions.ticktrack.startup.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;

public class ThemeFragment extends Fragment {

    private OnThemeSetClickListener onThemeSetClickListener;

    private Button themeSetButton;

    private ImageButton darkThemeButton, lightThemeButton;
    private TickTrackDatabase tickTrackDatabase;
    private int themeMode = 1;
    private ConstraintLayout rootLayout;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_ticktrack_theme, container, false);
        themeSetButton = root.findViewById(R.id.ticktrackFragmentThemeContinueButton);
        darkThemeButton = root.findViewById(R.id.ticktrackFragmentThemeDarkThemeButton);
        lightThemeButton = root.findViewById(R.id.ticktrackFragmentThemeLightThemeButton);

        rootLayout = root.findViewById(R.id.ticktrackFragmentThemeRoot);
        tickTrackDatabase = new TickTrackDatabase(requireContext());
        setupTheme();

        themeSetButton.setOnClickListener(view -> onThemeSetClickListener.onThemeSetClickListener());
        return root;
    }

//    private void reverseAnimate() {
//        float progress = lottieAnimationView.getProgress();
//        ValueAnimator valueAnimator = ValueAnimator.ofFloat(-progress,0 ).setDuration((long) ( lottieAnimationView.getDuration()* progress));
//        valueAnimator.addUpdateListener(animation -> lottieAnimationView.setProgress(Math.abs((float)animation.getAnimatedValue())));
//        valueAnimator.start();
//    }

    private void setupTheme() {
        themeMode = tickTrackDatabase.getThemeMode();
        if(themeMode==1){
            lightThemeButton.setClickable(false);
            darkThemeButton.setClickable(true);
            rootLayout.setBackgroundResource(R.color.LightGray);
        } else {
            darkThemeButton.setClickable(false);
            lightThemeButton.setClickable(true);

            rootLayout.setBackgroundResource(R.color.Black);
        }

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
