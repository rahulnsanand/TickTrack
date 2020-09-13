package com.theflopguyproductions.ticktrack.startup.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.SoYouADeveloperHuh;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.helpers.PowerSaverHelper;

public class BatteryOptimiseFragment extends Fragment {

    private BatteryOptimiseClickListener batteryOptimiseClickListener;

    private Button optimiseButton;
    private com.theflopguyproductions.ticktrack.ui.lottie.LottieAnimationView lottieAnimationView;
    private TextView detailText, detailBoldText;
    private TickTrackDatabase tickTrackDatabase;
    private int themeMode = 0;
    private ConstraintLayout rootLayout;
    private ScrollView optimiseScroll;
    private boolean isScrolled = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        tickTrackDatabase = new TickTrackDatabase(requireContext());

        PowerSaverHelper.WhiteListedInBatteryOptimizations appWhiteListState
                = PowerSaverHelper.getIfAppIsWhiteListedFromBatteryOptimizations(requireContext(), requireContext().getPackageName());

        if(tickTrackDatabase.isNotOptimised() || appWhiteListState.equals(PowerSaverHelper.WhiteListedInBatteryOptimizations.WHITE_LISTED)
                || appWhiteListState.equals(PowerSaverHelper.WhiteListedInBatteryOptimizations.ERROR_GETTING_STATE) ){
            Intent intent = new Intent(requireContext(), SoYouADeveloperHuh.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return null;
        } else {

            View root = inflater.inflate(R.layout.fragment_ticktrack_battery_optimize, container, false);

            themeMode = tickTrackDatabase.getThemeMode();
            tickTrackDatabase.storeStartUpFragmentID(5);

            optimiseButton = root.findViewById(R.id.ticktrackFragmentOptimiseBatteryButton);
            lottieAnimationView = root.findViewById(R.id.ticktrackFragmentBatteryOptimiseLottie);
            detailText = root.findViewById(R.id.ticktrackFragmentOptimiseHelpText);
            detailBoldText = root.findViewById(R.id.ticktrackFragmentOptimiseBoldHelpText);
            rootLayout = root.findViewById(R.id.ticktrackFragmentOptimiseRootLayout);
            optimiseScroll = root.findViewById(R.id.ticktrackFragmentBatteryOptimiseScrollView);

            optimiseScroll.getViewTreeObserver()
                    .addOnScrollChangedListener(() -> {
                        isScrolled = optimiseScroll.getChildAt(0).getBottom()
                                <= (optimiseScroll.getHeight() + optimiseScroll.getScrollY());
                    });

            setupTheme();

            lottieAnimationView.setAnimation(R.raw.battery_optimisation_anim);
            lottieAnimationView.playAnimation();
            lottieAnimationView.loop(true);


            optimiseButton.setOnClickListener(view -> {
                if (isScrolled || optimiseScroll.getChildAt(0).getBottom()
                        <= (optimiseScroll.getHeight() + optimiseScroll.getScrollY())) {
                    batteryOptimiseClickListener.onBatteryOptimiseClickListener();
                } else {
                    optimiseScroll.fullScroll(View.FOCUS_DOWN);
                }
            });
            return root;
        }
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
