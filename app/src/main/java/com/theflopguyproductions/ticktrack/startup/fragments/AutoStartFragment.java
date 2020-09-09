package com.theflopguyproductions.ticktrack.startup.fragments;

import android.app.ActivityManager;
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
import com.theflopguyproductions.ticktrack.startup.service.OptimiserService;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;

public class AutoStartFragment extends Fragment {

    private OnAutoStartSetClickListener autoStartSetClickListener;


    private Button autoStartButton;
    private ScrollView autoStartScroll;
    private boolean isScrolled = false;
    private ConstraintLayout rootLayout;
    private TextView helperText, helperSubText;
    private int themeMode = 1;
    private TickTrackDatabase tickTrackDatabase;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_ticktrack_autostart, container, false);
        autoStartButton = root.findViewById(R.id.ticktrackFragmentAutoStartButton);
        autoStartScroll = root.findViewById(R.id.ticktrackFragmentAutoStartScroll);
        rootLayout = root.findViewById(R.id.ticktrackFragmentAutoStartRoot);
        helperText = root.findViewById(R.id.ticktrackFragmentAutoStartDetailText);
        helperSubText = root.findViewById(R.id.ticktrackFragmentAutoStartSubHelperText);

        tickTrackDatabase = new TickTrackDatabase(requireContext());
        tickTrackDatabase.storeStartUpFragmentID(6);

        if(isMyServiceRunning(OptimiserService.class)){
            stopCheckService();
        }

        setupTheme();

        autoStartScroll.getViewTreeObserver()
                .addOnScrollChangedListener(() -> isScrolled = autoStartScroll.getChildAt(0).getBottom()
                        <= (autoStartScroll.getHeight() + autoStartScroll.getScrollY()));

        autoStartButton.setOnClickListener(view -> {
            if (isScrolled || autoStartScroll.getChildAt(0).getBottom()
                    <= (autoStartScroll.getHeight() + autoStartScroll.getScrollY())) {
                autoStartSetClickListener.onAutoStartSetClickListener();
            } else {
                autoStartScroll.fullScroll(View.FOCUS_DOWN);
            }
        });

        return root;
    }

    private void setupTheme() {

        themeMode = tickTrackDatabase.getThemeMode();

        if(themeMode==1){
            rootLayout.setBackgroundResource(R.color.LightGray);
            autoStartButton.setBackgroundResource(R.drawable.button_selector_white);

            helperText.setTextColor(getResources().getColor(R.color.DarkText));
            helperSubText.setTextColor(getResources().getColor(R.color.DarkText));
        } else {
            rootLayout.setBackgroundResource(R.color.Black);
            autoStartButton.setBackgroundResource(R.drawable.round_rect_dark);

            helperSubText.setTextColor(getResources().getColor(R.color.LightText));
            helperText.setTextColor(getResources().getColor(R.color.LightText));
        }
    }

    private void stopCheckService() {
        Intent intent = new Intent(requireContext(), OptimiserService.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setAction(OptimiserService.ACTION_BATTERY_OPTIMISE_CHECK_STOP);
        requireContext().startService(intent);
    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) requireContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
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
