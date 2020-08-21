package com.theflopguyproductions.ticktrack.startup.fragments;

import android.content.Context;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.theflopguyproductions.ticktrack.R;
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
    private ImageView autostartImageView;
    private Handler handler = new Handler();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_ticktrack_autostart, container, false);
        autoStartButton = root.findViewById(R.id.ticktrackFragmentAutoStartButton);
        autoStartScroll = root.findViewById(R.id.ticktrackFragmentAutoStartScroll);
        rootLayout = root.findViewById(R.id.ticktrackFragmentAutoStartRoot);
        helperText = root.findViewById(R.id.ticktrackFragmentAutoStartDetailText);
        helperSubText = root.findViewById(R.id.ticktrackFragmentAutoStartSubHelperText);
        autostartImageView = root.findViewById(R.id.ticktrackFragmentAutoStartImageView);

        handler.post(animatedRunnable);

        tickTrackDatabase = new TickTrackDatabase(requireContext());
        tickTrackDatabase.storeStartUpFragmentID(4);

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

    Runnable animatedRunnable = new Runnable() {
        @Override
        public void run() {
            animateStartUp(autostartImageView);
            handler.postDelayed(animatedRunnable,1000);
        }
    };

    private void animateStartUp(View view){
        ImageView v = (ImageView) view;
        Drawable d = v.getDrawable();
        if(d instanceof AnimatedVectorDrawableCompat){
            AnimatedVectorDrawableCompat avd = (AnimatedVectorDrawableCompat) d;
            avd.start();
        } else if ( d instanceof AnimatedVectorDrawable) {
            AnimatedVectorDrawable avd = (AnimatedVectorDrawable) d;
            avd.start();
        }
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
