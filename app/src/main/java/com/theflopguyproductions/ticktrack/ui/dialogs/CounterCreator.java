package com.theflopguyproductions.ticktrack.ui.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.ui.counter.CounterFragment;

import java.util.Random;

public class CounterCreator extends Dialog implements View.OnClickListener {

    public Activity activity;
    public Dialog dialog;

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
        counterLabel = (EditText) view.findViewById(R.id.counterLabelText);
        nebulaChip = (Chip) view.findViewById(R.id.nebulaTheme);
        gargantuaChip = (Chip) view.findViewById(R.id.gargantuaTheme);
        vortexChip = (Chip) view.findViewById(R.id.vortexTheme);
        unicornChip = (Chip) view.findViewById(R.id.unicornTheme);
        createButton = (Button) view.findViewById(R.id.createButton);
        cancelButton = (Button) view.findViewById(R.id.cancelButton);
        final ChipGroup chipGroup = (ChipGroup) view.findViewById(R.id.counterThemeGroup);

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

        createButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

    }


    public EditText counterLabel;
    public int counterColor= getRandomNumber(1,4);;

    public String lastModifiedDefault = "Created at";
    public ChipGroup chipGroup;
    public Chip randomChip;
    public Chip nebulaChip;
    public Chip vortexChip;
    public Chip gargantuaChip;
    public Chip unicornChip;

    public Button createButton;
    public Button cancelButton;

    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.createButton:
                CounterFragment.onDialogPositiveClick(counterLabel.getText().toString(),lastModifiedDefault,counterColor);
                counterColor= getRandomNumber(1,4);;
                break;
            case R.id.cancelButton:
                counterColor= getRandomNumber(1,4);;
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }
}
