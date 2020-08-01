package com.theflopguyproductions.ticktrack.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.theflopguyproductions.ticktrack.R;

import java.util.Objects;

public class TimerEditDialog extends Dialog {

    private Activity activity;
    public EditText labelInput;
    public Button saveButton, cancelButton;
    public TextView labelTitle, flagTitle, mainTitle;
    private String currentLabel;
    public int currentFlag;
    private ChipGroup flagChipGroup;
    private Chip redChip, greenChip, orangeChip, purpleChip, blueChip;

    public TimerEditDialog(Activity activity, String currentLabel, int currentFlag){
        super(activity);
        this.activity = activity;
        this.currentLabel = currentLabel;
        this.currentFlag = currentFlag;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_timer_edit_layout, new ConstraintLayout(activity), false);
        setContentView(view);
        Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().getAttributes().windowAnimations = R.style.createdDialog;

        initVariables(view);

        flagCheck();

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
