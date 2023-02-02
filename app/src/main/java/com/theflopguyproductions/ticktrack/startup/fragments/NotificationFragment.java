package com.theflopguyproductions.ticktrack.startup.fragments;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.startup.service.OptimiserService;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;

import java.util.Objects;

public class NotificationFragment extends Fragment {

    public NotificationFragment() {
        // Required empty public constructor
    }

    private OnNotificationSetClickListener notificationSetClickListener;

    private Button notificationSetButton, notificationSkipButton;
    private ScrollView notificationScroll;
    private boolean isScrolled = false;
    private ConstraintLayout rootLayout;
    private TextView helperText;
    private int themeMode = 1;
    private TickTrackDatabase tickTrackDatabase;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_notification, container, false);
        notificationSetButton = root.findViewById(R.id.ticktrackFragmentNotificationButton);
        notificationSkipButton = root.findViewById(R.id.ticktrackFragmentNotificationSkipButton);
        notificationScroll = root.findViewById(R.id.ticktrackFragmentNotificationScroll);
        rootLayout = root.findViewById(R.id.ticktrackFragmentNotificationRoot);
        helperText = root.findViewById(R.id.ticktrackFragmentNotificationDetailText);

        tickTrackDatabase = new TickTrackDatabase(requireContext());
        tickTrackDatabase.storeStartUpFragmentID(5);

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_NOTIFICATION_POLICY) == PackageManager.PERMISSION_GRANTED){
            notificationSetClickListener.OnNotificationSetupClickListener();
        }


        setupTheme();

        notificationScroll.getViewTreeObserver()
                .addOnScrollChangedListener(() -> isScrolled = notificationScroll.getChildAt(0).getBottom()
                        <= (notificationScroll.getHeight() + notificationScroll.getScrollY()));

        notificationSetButton.setOnClickListener(view -> {
            if (isScrolled || notificationScroll.getChildAt(0).getBottom()
                    <= (notificationScroll.getHeight() + notificationScroll.getScrollY())) {
                notificationSetClickListener.OnNotificationSetupClickListener();
            } else {
                notificationScroll.fullScroll(View.FOCUS_DOWN);
            }
        });

        notificationSkipButton.setOnClickListener(view -> {
            if (isScrolled || notificationScroll.getChildAt(0).getBottom()
                    <= (notificationScroll.getHeight() + notificationScroll.getScrollY())) {
                notificationSetClickListener.onNotificationSkipClickListener();
            } else {
                notificationScroll.fullScroll(View.FOCUS_DOWN);
            }
        });

        return root;
    }

    private void setupTheme() {

        themeMode = tickTrackDatabase.getThemeMode();

        if(themeMode==1){
            rootLayout.setBackgroundResource(R.color.LightGray);
            notificationSkipButton.setBackgroundResource(R.drawable.button_selector_white);

            helperText.setTextColor(getResources().getColor(R.color.DarkText));
        } else {
            rootLayout.setBackgroundResource(R.color.Black);
            notificationSkipButton.setBackgroundResource(R.drawable.round_rect_dark);

            helperText.setTextColor(getResources().getColor(R.color.LightText));
        }
    }
    private boolean isMyServiceRunning() {
       return true;
    }

    public interface OnNotificationSetClickListener {
        void OnNotificationSetupClickListener();

        void onNotificationSkipClickListener();
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            notificationSetClickListener = (OnNotificationSetClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context
                    + " must implement " + OnNotificationSetClickListener.class.getName());
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        notificationSetClickListener = null;
    }

}