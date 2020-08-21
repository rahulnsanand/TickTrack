package com.theflopguyproductions.ticktrack.startup.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

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
    private ImageView darkTick, lightTick;

    private ScrollView ticktrackFragmentThemeScrollView;
    private boolean isScrolled = false;

    private TextView titleFlavor, themeSubtext, customizeText, darkText, lightText, themeDetailText;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_ticktrack_theme, container, false);
        themeSetButton = root.findViewById(R.id.ticktrackFragmentThemeContinueButton);
        darkThemeButton = root.findViewById(R.id.ticktrackFragmentThemeDarkThemeButton);
        lightThemeButton = root.findViewById(R.id.ticktrackFragmentThemeLightThemeButton);
        darkTick = root.findViewById(R.id.ticktrackFragmentThemeDarkTickAnim);
        lightTick = root.findViewById(R.id.ticktrackFragmentThemeLightTickAnim);
        ticktrackFragmentThemeScrollView = root.findViewById(R.id.ticktrackFragmentThemeScrollView);

        titleFlavor = root.findViewById(R.id.ticktrackFragmentThemeTitleFlavor);
        themeSubtext = root.findViewById(R.id.ticktrackFragmentThemeSubText);
        customizeText = root.findViewById(R.id.ticktrackFragmentThemeCustomizeText);
        darkText = root.findViewById(R.id.ticktrackFragmentThemeDarkMatterText);
        lightText = root.findViewById(R.id.ticktrackFragmentThemeLightMatterText);
        themeDetailText = root.findViewById(R.id.ticktrackFragmentThemeHelpText);

        darkTick.setImageResource(R.drawable.ic_light_tick);
        lightTick.setImageResource(R.drawable.ic_light_tick);

        rootLayout = root.findViewById(R.id.ticktrackFragmentThemeRoot);
        tickTrackDatabase = new TickTrackDatabase(requireContext());
        tickTrackDatabase.storeStartUpFragmentID(2);
        setupTheme();

        ticktrackFragmentThemeScrollView.getViewTreeObserver()
                .addOnScrollChangedListener(() -> isScrolled = ticktrackFragmentThemeScrollView.getChildAt(0).getBottom()
                        <= (ticktrackFragmentThemeScrollView.getHeight() + ticktrackFragmentThemeScrollView.getScrollY()));


        darkThemeButton.setOnClickListener(view -> {
            tickTrackDatabase.setThemeMode(2);
            setupTheme();
        });

        lightThemeButton.setOnClickListener(view -> {
            tickTrackDatabase.setThemeMode(1);
            setupTheme();
        });

        themeSetButton.setOnClickListener(view -> {
            if (isScrolled || ticktrackFragmentThemeScrollView.getChildAt(0).getBottom()
                    <= (ticktrackFragmentThemeScrollView.getHeight() + ticktrackFragmentThemeScrollView.getScrollY())) {
                onThemeSetClickListener.onThemeSetClickListener();
            } else {
                ticktrackFragmentThemeScrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
        return root;
    }

//    private void reverseAnimate(LottieAnimationView lottieAnimationView) {
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
            lightTick.setVisibility(View.VISIBLE);
            darkTick.setVisibility(View.INVISIBLE);
            rootLayout.setBackgroundResource(R.color.LightGray);
            themeSetButton.setBackgroundResource(R.drawable.button_selector_white);

            titleFlavor.setTextColor(getResources().getColor(R.color.Accent));
            themeSubtext.setTextColor(getResources().getColor(R.color.DarkText));
            customizeText.setTextColor(getResources().getColor(R.color.Accent));
            darkText.setTextColor(getResources().getColor(R.color.DarkText));
            lightText.setTextColor(getResources().getColor(R.color.DarkText));
            themeDetailText.setTextColor(getResources().getColor(R.color.DarkText));

        } else {

            darkThemeButton.setClickable(false);
            lightThemeButton.setClickable(true);
            lightTick.setVisibility(View.INVISIBLE);
            darkTick.setVisibility(View.VISIBLE);
            rootLayout.setBackgroundResource(R.color.Black);
            themeSetButton.setBackgroundResource(R.drawable.round_rect_dark);

            titleFlavor.setTextColor(getResources().getColor(R.color.Accent));
            themeSubtext.setTextColor(getResources().getColor(R.color.LightText));
            customizeText.setTextColor(getResources().getColor(R.color.Accent));
            darkText.setTextColor(getResources().getColor(R.color.LightText));
            lightText.setTextColor(getResources().getColor(R.color.LightText));
            themeDetailText.setTextColor(getResources().getColor(R.color.LightText));

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
