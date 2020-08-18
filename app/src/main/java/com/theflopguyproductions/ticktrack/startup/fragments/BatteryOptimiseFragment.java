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

public class BatteryOptimiseFragment extends Fragment {

    private BatteryOptimiseClickListener batteryOptimiseClickListener;

    private Button optimiseButton;
    private com.theflopguyproductions.ticktrack.ui.lottie.LottieAnimationView lottieAnimationView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_ticktrack_battery_optimize, container, false);
        optimiseButton = root.findViewById(R.id.ticktrackBatteryButton);
        lottieAnimationView = root.findViewById(R.id.ticktrackFragmentBatteryOptimiseLottie);
        lottieAnimationView.setAnimation(R.raw.battery_optimisation_anim);
        lottieAnimationView.playAnimation();
        lottieAnimationView.loop(true);


        optimiseButton.setOnClickListener(view -> batteryOptimiseClickListener.onBatteryOptimiseClickListener());

        return root;
    }

    public interface BatteryOptimiseClickListener {
        void onBatteryOptimiseClickListener();
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            batteryOptimiseClickListener = (BatteryOptimiseClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement " + BatteryOptimiseClickListener.class.getName());
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        batteryOptimiseClickListener = null;
    }

}
