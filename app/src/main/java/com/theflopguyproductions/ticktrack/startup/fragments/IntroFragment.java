package com.theflopguyproductions.ticktrack.startup.fragments;

import android.animation.Animator;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.ui.lottie.LottieAnimationView;

public class IntroFragment extends Fragment {

    private OnGetStartedClickListener getStartedClickListener;

    private Button getStartedButton;
    private LottieAnimationView lottieAnimationView;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_ticktrack_intro, container, false);
        getStartedButton = root.findViewById(R.id.TickTrackIntroGetStartedButton);
        getStartedButton.setVisibility(View.INVISIBLE);
        lottieAnimationView = root.findViewById(R.id.tickTrackIntroLogoAnimation);
        lottieAnimationView.setAnimation(R.raw.logo_animation_ticktrack);
        lottieAnimationView.playAnimation();
        lottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                getStartedButton.setVisibility(View.VISIBLE);
                lottieAnimationView.playAnimation();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        getStartedButton.setOnClickListener(view -> getStartedClickListener.onGetStartedClick());

        return root;
    }

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
