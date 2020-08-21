package com.theflopguyproductions.ticktrack.startup.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.ui.lottie.LottieAnimationView;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;

public class IntroFragment extends Fragment {

    private OnGetStartedClickListener getStartedClickListener;

    private Button getStartedButton;
    private LottieAnimationView lottieAnimationView;
    private TextView tickTrackName;
    private ConstraintLayout rootLayout;
    private Handler displayStuff = new Handler();
    private TickTrackDatabase tickTrackDatabase;
    private int themeMode = 0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_ticktrack_intro, container, false);

        tickTrackDatabase = new TickTrackDatabase(requireContext());
        themeMode = tickTrackDatabase.getThemeMode();

        getStartedButton = root.findViewById(R.id.TickTrackFragmentIntroGetStartedButton);
        rootLayout = root.findViewById(R.id.ticktrackFragmentIntroRootLayout);
        tickTrackName = root.findViewById(R.id.tickTrackFragmentIntroTitleText);
        lottieAnimationView = root.findViewById(R.id.tickTrackIntroLogoAnimation);

        setupTheme();
        tickTrackName.setVisibility(View.INVISIBLE);
        getStartedButton.setVisibility(View.INVISIBLE);

        lottieAnimationView.setAnimation(R.raw.logo_animation_ticktrack);
        lottieAnimationView.playAnimation();

        getStartedButton.setVisibility(View.VISIBLE);

        displayStuff.postDelayed(displayRunnable, 3500);

        getStartedButton.setOnClickListener(view -> getStartedClickListener.onGetStartedClick());

        return root;
    }

    private void setupTheme() {
        themeMode = tickTrackDatabase.getThemeMode();
        if(themeMode==1){
            rootLayout.setBackgroundResource(R.color.LightGray);
            tickTrackName.setTextColor(getResources().getColor(R.color.DarkText) );
            getStartedButton.setBackgroundResource(R.drawable.button_selector_white);
        } else {
            rootLayout.setBackgroundResource(R.color.Black);
            tickTrackName.setTextColor(getResources().getColor(R.color.LightText) );
            getStartedButton.setBackgroundResource(R.drawable.round_rect_dark);
        }
    }

    Runnable displayRunnable = new Runnable() {
        @Override
        public void run() {
            getStartedButton.setVisibility(View.VISIBLE);
            tickTrackName.setVisibility(View.VISIBLE);
        }
    };


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            getStartedClickListener = (OnGetStartedClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement " + OnGetStartedClickListener.class.getName());
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        getStartedClickListener = null;
    }
    public interface OnGetStartedClickListener {
        void onGetStartedClick();
    }
}
