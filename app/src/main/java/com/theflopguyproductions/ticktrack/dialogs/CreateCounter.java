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
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.ui.counter.CounterFragment;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.helpers.UniqueIdGenerator;

import java.sql.Timestamp;
import java.util.Objects;

public class CreateCounter extends Dialog {

    private TickTrackDatabase tickTrackDatabase;

    public Activity activity;
    public int counterFlag= 0;
    public Button createCounterButton, cancelCounterButton;
    public EditText counterLabelText;


    public CreateCounter(Activity activity){
        super(activity);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_counter_creator, new ConstraintLayout(activity), false);
        setContentView(view);
        Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        tickTrackDatabase = new TickTrackDatabase(getContext());

        counterLabelText = view.findViewById(R.id.counterLabelInputText);
        createCounterButton = view.findViewById(R.id.createCounterButton);
        cancelCounterButton = view.findViewById(R.id.dismissCounterButton);
        final ChipGroup chipGroup = view.findViewById(R.id.counterFlagGroup);

        int counterNumber = tickTrackDatabase.retrieveCounterNumber();
        String counterName = "Counter "+ counterNumber;
        counterLabelText.setHint(counterName);

        getWindow().getAttributes().windowAnimations = R.style.createdDialog;

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
                CounterFragment.createCounter(counterLabelText.getText().toString(),new Timestamp(System.currentTimeMillis()),counterFlag, this.activity,0,0,false, false, false, UniqueIdGenerator.getUniqueCounterID());
            } else {
                tickTrackDatabase.storeCounterNumber(counterNumber+1);
                CounterFragment.createCounter(counterName,new Timestamp(System.currentTimeMillis()),counterFlag, this.activity, 0,0,false, false, false, UniqueIdGenerator.getUniqueCounterID());
            }
            dismiss();
        });
        cancelCounterButton.setOnClickListener(view12 -> {
            dismiss();
        });
    }

}
