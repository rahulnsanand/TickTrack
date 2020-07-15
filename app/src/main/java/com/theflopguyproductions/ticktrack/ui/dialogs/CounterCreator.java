package com.theflopguyproductions.ticktrack.ui.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.ui.counter.CounterFragment;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class CounterCreator extends Dialog{

    public Activity activity;
    Animation fromRight;

    public CounterCreator(Activity activity){
        super(activity);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        View view = activity.getLayoutInflater().inflate(R.layout.counter_create_dialog, new ConstraintLayout(activity), false);
        setContentView(view);

        fromRight = AnimationUtils.loadAnimation(getContext(), R.anim.from_right);
        counterLabel = (EditText) view.findViewById(R.id.counterLabelText);
        createButton = (Button) view.findViewById(R.id.createButton);
        cancelButton = (Button) view.findViewById(R.id.cancelButton);
        final ChipGroup chipGroup = (ChipGroup) view.findViewById(R.id.counterThemeGroup);

        getWindow().getAttributes().windowAnimations = R.style.createdDialog;

        chipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                Chip chip = chipGroup.findViewById(checkedId);
                if(chip != null){
                    Toast.makeText(activity, chip.getText().toString(),Toast.LENGTH_LONG).show();
                    if(chip.getText().toString().equals("Nebula")){
                        counterColor=1;
                    }
                    if(chip.getText().toString().equals("Vortex")){
                        counterColor=3;
                    }
                    if(chip.getText().toString().equals("Gargantua")){
                        counterColor=2;
                    }
                    if(chip.getText().toString().equals("Unicorn")){
                        counterColor=4;
                    }
                    if(chip.getText().toString().equals("Random")){
                        counterColor = getRandomNumber(1,4);
                    }
                }
            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CounterFragment.onDialogPositiveClick(counterLabel.getText().toString(),lastModifiedDefault,counterColor);
                counterColor= getRandomNumber(1,5);
                dismiss();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counterColor= getRandomNumber(1,5);
                dismiss();
            }
        });
    }


    public EditText counterLabel;
    public int counterColor= getRandomNumber(1,5);

    public Timestamp lastModifiedDefault = new Timestamp(System.currentTimeMillis());

    public Button createButton;
    public Button cancelButton;

    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

}
