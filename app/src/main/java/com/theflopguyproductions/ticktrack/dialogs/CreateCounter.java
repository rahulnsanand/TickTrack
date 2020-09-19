package com.theflopguyproductions.ticktrack.dialogs;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.ui.counter.CounterFragment;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.helpers.UniqueIdGenerator;

import java.util.Objects;

public class CreateCounter extends BottomSheetDialog {

    private TickTrackDatabase tickTrackDatabase;

    public Activity activity;
    public int counterFlag= 0, themeSet = 1;
    public Button createCounterButton, cancelCounterButton;
    public EditText counterLabelText;
    private TextView titleText, counterFlagTitle, characterCountText, counterLabelTitleTextView;
    private ConstraintLayout rootLayout;
    private Chip redChip, greenCip, orangeChip, purpleChip, blueChip;
    public int counterNumber;
    public String counterName;

    public CreateCounter(Activity activity){
        super(activity);
        this.activity = activity;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        View view = activity.getLayoutInflater().inflate(R.layout.dialog_counter_creator, new ConstraintLayout(activity), false);
        Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(view);

        WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width = Math.min(metrics.widthPixels, 1280);
        int height = -1; // MATCH_PARENT
        getWindow().setLayout(width, height);

        tickTrackDatabase = new TickTrackDatabase(getContext());
        themeSet = tickTrackDatabase.getThemeMode();

        counterLabelText = view.findViewById(R.id.counterLabelInputText);
        createCounterButton = view.findViewById(R.id.createCounterButton);
        cancelCounterButton = view.findViewById(R.id.dismissCounterButton);
        titleText = view.findViewById(R.id.createCounterDialogTitle);
        counterFlagTitle = view.findViewById(R.id.counterFlagTextView);
        rootLayout = view.findViewById(R.id.counterCreateRootLayout);
        characterCountText = view.findViewById(R.id.counterCreatorLabelCharLength);
        counterLabelTitleTextView = view.findViewById(R.id.counterLabelTitleTextView);

        redChip = view.findViewById(R.id.redCounterFlag);
        greenCip = view.findViewById(R.id.greenCounterFlag);
        orangeChip = view.findViewById(R.id.orangeCounterFlag);
        purpleChip = view.findViewById(R.id.purpleCounterFlag);
        blueChip = view.findViewById(R.id.blueCounterFlag);


        final ChipGroup chipGroup = view.findViewById(R.id.counterFlagGroup);

        counterNumber = tickTrackDatabase.retrieveCounterNumber();
        counterName = "Counter "+ counterNumber;
        counterLabelText.setHint(counterName);

        setupTheme();

//        getWindow().getAttributes().windowAnimations = R.style.acceptDialog;
//        Animation transition_in_view = AnimationUtils.loadAnimation(getContext(), R.anim.enter_from_right);
//        view.setAnimation( transition_in_view );
//        view.startAnimation( transition_in_view );

        chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            Chip chip = chipGroup.findViewById(checkedId);
            if(chip != null){
                Toast.makeText(activity, chip.getText().toString(), Toast.LENGTH_LONG).show();
                if(chip.getText().toString().equals("Cherry")){
                    counterFlag=1;
                }
                else if(chip.getText().toString().equals("Lime")){
                    counterFlag=2;
                }
                else if(chip.getText().toString().equals("Peach")){
                    counterFlag=3;
                }
                else if(chip.getText().toString().equals("Plum")){
                    counterFlag=4;
                }
                else if(chip.getText().toString().equals("Berry")){
                    counterFlag = 5;
                } else {
                    counterFlag = 0;
                }
            } else {
                counterFlag = 0;
            }
        });

        createCounterButton.setOnClickListener(view1 -> {
            if(counterLabelText.getText().toString().trim().length() > 0){
                CounterFragment.createCounter(counterLabelText.getText().toString(),System.currentTimeMillis(),counterFlag, this.activity,0,0,false, false, false, UniqueIdGenerator.getUniqueCounterID());
            } else {
                tickTrackDatabase.storeCounterNumber(counterNumber+1);
                CounterFragment.createCounter(counterName, System.currentTimeMillis(),counterFlag, this.activity, 0,0,false, false, false, UniqueIdGenerator.getUniqueCounterID());
            }
            dismiss();
        });
        cancelCounterButton.setOnClickListener(view12 -> {
            dismiss();
        });


        counterLabelText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int length = charSequence.length();
                if(length>10 && length<15){
                    characterCountText.setTextColor(activity.getResources().getColor(R.color.roboto_calendar_circle_1));
                } else if (length==15){
                    characterCountText.setTextColor(activity.getResources().getColor(R.color.red_matte));
                } else {
                    if(themeSet==1) {
                        characterCountText.setTextColor(activity.getResources().getColor(R.color.DarkText));
                    } else {
                        characterCountText.setTextColor(activity.getResources().getColor(R.color.LightText));
                    }
                }
                characterCountText.setText(length+"/15");
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void setupTheme(){
        if(themeSet==1){
            rootLayout.setBackgroundResource(R.color.LightGray);
            titleText.setTextColor(activity.getResources().getColor(R.color.DarkText));
            counterFlagTitle.setTextColor(activity.getResources().getColor(R.color.LightDarkText));
            cancelCounterButton.setBackgroundResource(R.drawable.button_selector_light);
            counterLabelText.setTextColor(activity.getResources().getColor(R.color.DarkText));
            counterLabelText.setHintTextColor(activity.getResources().getColor(R.color.GrayOnDark));
            redChip.setChipBackgroundColorResource(R.color.Clickable);
            greenCip.setChipBackgroundColorResource(R.color.Clickable);
            orangeChip.setChipBackgroundColorResource(R.color.Clickable);
            purpleChip.setChipBackgroundColorResource(R.color.Clickable);
            blueChip.setChipBackgroundColorResource(R.color.Clickable);
            redChip.setTextColor(activity.getResources().getColor(R.color.DarkText));
            greenCip.setTextColor(activity.getResources().getColor(R.color.DarkText));
            orangeChip.setTextColor(activity.getResources().getColor(R.color.DarkText));
            purpleChip.setTextColor(activity.getResources().getColor(R.color.DarkText));
            blueChip.setTextColor(activity.getResources().getColor(R.color.DarkText));
            characterCountText.setTextColor(activity.getResources().getColor(R.color.DarkText));
            counterLabelTitleTextView.setTextColor(activity.getResources().getColor(R.color.LightDarkText));
        } else {
            rootLayout.setBackgroundResource(R.color.Gray);
            titleText.setTextColor(activity.getResources().getColor(R.color.LightText));
            counterFlagTitle.setTextColor(activity.getResources().getColor(R.color.DarkLightText));
            cancelCounterButton.setBackgroundResource(R.drawable.button_selector_dark);
            counterLabelText.setTextColor(activity.getResources().getColor(R.color.LightText));
            counterLabelText.setHintTextColor(activity.getResources().getColor(R.color.GrayOnLight));
            redChip.setChipBackgroundColorResource(R.color.GrayOnDark);
            greenCip.setChipBackgroundColorResource(R.color.GrayOnDark);
            orangeChip.setChipBackgroundColorResource(R.color.GrayOnDark);
            purpleChip.setChipBackgroundColorResource(R.color.GrayOnDark);
            blueChip.setChipBackgroundColorResource(R.color.GrayOnDark);
            redChip.setTextColor(activity.getResources().getColor(R.color.LightText));
            greenCip.setTextColor(activity.getResources().getColor(R.color.LightText));
            orangeChip.setTextColor(activity.getResources().getColor(R.color.LightText));
            purpleChip.setTextColor(activity.getResources().getColor(R.color.LightText));
            blueChip.setTextColor(activity.getResources().getColor(R.color.LightText));
            characterCountText.setTextColor(activity.getResources().getColor(R.color.LightText));
            counterLabelTitleTextView.setTextColor(activity.getResources().getColor(R.color.DarkLightText));
        }

    }

}
