package com.theflopguyproductions.ticktrack.startup.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;

public class BatteryOptimiseFragment extends Fragment {

    private BatteryOptimiseClickListener batteryOptimiseClickListener;

    private Button optimiseButton;
    private com.theflopguyproductions.ticktrack.ui.lottie.LottieAnimationView lottieAnimationView;
    private TextView detailText, detailBoldText;
    private TickTrackDatabase tickTrackDatabase;
    private int themeMode = 0;
    private ConstraintLayout rootLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_ticktrack_battery_optimize, container, false);

        tickTrackDatabase = new TickTrackDatabase(requireContext());
        themeMode = tickTrackDatabase.getThemeMode();

        optimiseButton = root.findViewById(R.id.ticktrackFragmentOptimiseBatteryButton);
        lottieAnimationView = root.findViewById(R.id.ticktrackFragmentBatteryOptimiseLottie);
        detailText = root.findViewById(R.id.ticktrackFragmentOptimiseHelpText);
        detailBoldText = root.findViewById(R.id.ticktrackFragmentOptimiseBoldHelpText);
        rootLayout = root.findViewById(R.id.ticktrackFragmentOptimiseRootLayout);

        setupTheme();

        lottieAnimationView.setAnimation(R.raw.battery_optimisation_anim);
        lottieAnimationView.playAnimation();
        lottieAnimationView.loop(true);


        optimiseButton.setOnClickListener(view -> batteryOptimiseClickListener.onBatteryOptimiseClickListener());

        return root;
    }

    public void setupTheme(){
        themeMode = tickTrackDatabase.getThemeMode();
        if(themeMode==1){
            rootLayout.setBackgroundResource(R.color.LightGray);
            detailText.setTextColor(getResources().getColor(R.color.DarkText) );
            detailBoldText.setTextColor(getResources().getColor(R.color.DarkText) );
            optimiseButton.setBackgroundResource(R.drawable.button_selector_white);
        } else {
            rootLayout.setBackgroundResource(R.color.Black);
            detailText.setTextColor(getResources().getColor(R.color.LightText) );
            detailBoldText.setTextColor(getResources().getColor(R.color.LightText) );
            optimiseButton.setBackgroundResource(R.drawable.round_rect_dark);
        }
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
