package com.theflopguyproductions.ticktrack.startup.fragments;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
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
import com.theflopguyproductions.ticktrack.settings.SettingsData;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.widgets.shortcuts.CreateTimerWidget;
import com.theflopguyproductions.ticktrack.widgets.shortcuts.QuickTimerWidget;
import com.theflopguyproductions.ticktrack.widgets.shortcuts.ScreensaverWidget;
import com.theflopguyproductions.ticktrack.widgets.shortcuts.StopwatchWidget;

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
        tickTrackDatabase.storeStartUpFragmentID(4);
        setupTheme();

        ticktrackFragmentThemeScrollView.getViewTreeObserver()
                .addOnScrollChangedListener(() -> isScrolled = ticktrackFragmentThemeScrollView.getChildAt(0).getBottom()
                        <= (ticktrackFragmentThemeScrollView.getHeight() + ticktrackFragmentThemeScrollView.getScrollY()));


        darkThemeButton.setOnClickListener(view -> {
            tickTrackDatabase.setThemeMode(SettingsData.Theme.DARK.getCode());
            setupTheme();
        });

        lightThemeButton.setOnClickListener(view -> {
            tickTrackDatabase.setThemeMode(SettingsData.Theme.LIGHT.getCode());
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
        updateWidgets();
    }
    private void updateWidgets() {
        Intent intent1 = new Intent(requireActivity(), CreateTimerWidget.class);
        intent1.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids1 = AppWidgetManager.getInstance(requireActivity().getApplication()).getAppWidgetIds(new ComponentName(requireActivity().getApplication(), CreateTimerWidget.class));
        intent1.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids1);
        requireActivity().sendBroadcast(intent1);
        Intent intent2 = new Intent(requireActivity(), QuickTimerWidget.class);
        intent2.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids2 = AppWidgetManager.getInstance(requireActivity().getApplication()).getAppWidgetIds(new ComponentName(requireActivity().getApplication(), QuickTimerWidget.class));
        intent2.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids2);
        requireActivity().sendBroadcast(intent2);
        Intent intent3 = new Intent(requireActivity(), ScreensaverWidget.class);
        intent3.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids3 = AppWidgetManager.getInstance(requireActivity().getApplication()).getAppWidgetIds(new ComponentName(requireActivity().getApplication(), ScreensaverWidget.class));
        intent3.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids3);
        requireActivity().sendBroadcast(intent3);
        Intent intent4 = new Intent(requireActivity(), StopwatchWidget.class);
        intent4.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids4 = AppWidgetManager.getInstance(requireActivity().getApplication()).getAppWidgetIds(new ComponentName(requireActivity().getApplication(), StopwatchWidget.class));
        intent4.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids4);
        requireActivity().sendBroadcast(intent4);
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
