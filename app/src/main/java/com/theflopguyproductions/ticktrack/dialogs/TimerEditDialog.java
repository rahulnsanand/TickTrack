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
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;

import java.util.Objects;

public class TimerEditDialog extends BottomSheetDialog {

    private Activity activity;
    public EditText labelInput;
    public Button saveButton, cancelButton;
    public TextView labelTitle, flagTitle, mainTitle, characterCountText;
    private String currentLabel;
    public int currentFlag, themeSet = 1;
    private ChipGroup flagChipGroup;
    private Chip redChip, greenChip, orangeChip, purpleChip, blueChip;
    private TickTrackDatabase tickTrackDatabase;
    private ConstraintLayout rootLayout;

    public TimerEditDialog(Activity activity, String currentLabel, int currentFlag){
        super(activity);
        this.activity = activity;
        this.currentLabel = currentLabel;
        this.currentFlag = currentFlag;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_timer_edit_layout, new ConstraintLayout(activity), false);
        setContentView(view);
        Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width = Math.min(metrics.widthPixels, 1280);
        int height = -1; // MATCH_PARENT
        getWindow().setLayout(width, height);

        initVariables(view);

        tickTrackDatabase = new TickTrackDatabase(getContext());
        themeSet = tickTrackDatabase.getThemeMode();

        setupTheme();
        flagCheck();

        labelInput.addTextChangedListener(new TextWatcher() {
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

    private void setupTheme() {

        if(themeSet==1){
            rootLayout.setBackgroundResource(R.color.LightGray);
            mainTitle.setTextColor(activity.getResources().getColor(R.color.DarkText));
            labelTitle.setTextColor(activity.getResources().getColor(R.color.DarkText));
            flagTitle.setTextColor(activity.getResources().getColor(R.color.DarkText));
            cancelButton.setBackgroundResource(R.drawable.button_selector_light);
            saveButton.setBackgroundResource(R.drawable.button_selector_light);
            labelInput.setTextColor(activity.getResources().getColor(R.color.DarkText));
            labelInput.setHintTextColor(activity.getResources().getColor(R.color.GrayOnDark));
            characterCountText.setTextColor(activity.getResources().getColor(R.color.DarkText));
            redChip.setChipBackgroundColorResource(R.color.Clickable);
            greenChip.setChipBackgroundColorResource(R.color.Clickable);
            orangeChip.setChipBackgroundColorResource(R.color.Clickable);
            purpleChip.setChipBackgroundColorResource(R.color.Clickable);
            blueChip.setChipBackgroundColorResource(R.color.Clickable);
            redChip.setTextColor(activity.getResources().getColor(R.color.DarkText));
            greenChip.setTextColor(activity.getResources().getColor(R.color.DarkText));
            orangeChip.setTextColor(activity.getResources().getColor(R.color.DarkText));
            purpleChip.setTextColor(activity.getResources().getColor(R.color.DarkText));
            blueChip.setTextColor(activity.getResources().getColor(R.color.DarkText));
        } else {
            rootLayout.setBackgroundResource(R.color.Gray);
            mainTitle.setTextColor(activity.getResources().getColor(R.color.LightText));
            labelTitle.setTextColor(activity.getResources().getColor(R.color.LightText));
            flagTitle.setTextColor(activity.getResources().getColor(R.color.LightText));
            cancelButton.setBackgroundResource(R.drawable.button_selector_dark);
            saveButton.setBackgroundResource(R.drawable.button_selector_dark);
            labelInput.setTextColor(activity.getResources().getColor(R.color.LightText));
            labelInput.setHintTextColor(activity.getResources().getColor(R.color.GrayOnLight));
            characterCountText.setTextColor(activity.getResources().getColor(R.color.LightText));
            redChip.setChipBackgroundColorResource(R.color.GrayOnDark);
            greenChip.setChipBackgroundColorResource(R.color.GrayOnDark);
            orangeChip.setChipBackgroundColorResource(R.color.GrayOnDark);
            purpleChip.setChipBackgroundColorResource(R.color.GrayOnDark);
            blueChip.setChipBackgroundColorResource(R.color.GrayOnDark);
            redChip.setTextColor(activity.getResources().getColor(R.color.LightText));
            greenChip.setTextColor(activity.getResources().getColor(R.color.LightText));
            orangeChip.setTextColor(activity.getResources().getColor(R.color.LightText));
            purpleChip.setTextColor(activity.getResources().getColor(R.color.LightText));
            blueChip.setTextColor(activity.getResources().getColor(R.color.LightText));
        }
    }

    private void initVariables(View view) {
        saveButton = view.findViewById(R.id.timerEditDialogSaveButton);
        cancelButton = view.findViewById(R.id.timerEditDialogCancelButton);
        labelInput = view.findViewById(R.id.timerEditDialogLabelText);
        labelTitle = view.findViewById(R.id.timerEditDialogLabelTitleText);
        labelInput.setHint(currentLabel);
        flagTitle = view.findViewById(R.id.timerEditDialogFlagTitleText);
        mainTitle = view.findViewById(R.id.timerEditDialogTitleText);
        flagChipGroup = view.findViewById(R.id.timerEditDialogFlagChipGroup);
        redChip = view.findViewById(R.id.timerEditDialogRedFlag);
        greenChip = view.findViewById(R.id.timerEditDialogGreenFlag);
        orangeChip = view.findViewById(R.id.timerEditDialogOrangeFlag);
        purpleChip = view.findViewById(R.id.timerEditDialogPurpleFlag);
        blueChip = view.findViewById(R.id.timerEditDialogBlueFlag);
        rootLayout = view.findViewById(R.id.timerEditDialogRootLayout);
        characterCountText = view.findViewById(R.id.timerEditDialogCharacterCounter);

        setPrefixFlag();
    }

    private void setPrefixFlag(){
        if(currentFlag==1){
            redChip.setChecked(true);
        } else  if(currentFlag==2){
            greenChip.setChecked(true);
        } else if(currentFlag==3){
            orangeChip.setChecked(true);
        } else if(currentFlag==4){
            purpleChip.setChecked(true);
        } else  if(currentFlag==5){
            blueChip.setChecked(true);
        }
    }

    private void flagCheck() {
        flagChipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            Chip chip = flagChipGroup.findViewById(checkedId);
            if(chip != null){
                Toast.makeText(activity, chip.getText().toString(), Toast.LENGTH_LONG).show();
                if(chip.getText().toString().equals("Cherry")){
                    currentFlag=1;
                }
                else if(chip.getText().toString().equals("Lime")){
                    currentFlag=2;
                }
                else if(chip.getText().toString().equals("Peach")){
                    currentFlag=3;
                }
                else if(chip.getText().toString().equals("Plum")){
                    currentFlag=4;
                }
                else if(chip.getText().toString().equals("Berry")){
                    currentFlag = 5;
                } else {
                    currentFlag=0;
                }
            } else {
                currentFlag = 0;
            }
        });
    }

}
