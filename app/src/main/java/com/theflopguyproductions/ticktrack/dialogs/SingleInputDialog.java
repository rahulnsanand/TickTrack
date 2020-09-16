package com.theflopguyproductions.ticktrack.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;

import java.util.Objects;

public class SingleInputDialog extends Dialog {

    private Activity activity;
    public EditText inputText;
    public Button okButton, cancelButton;
    public TextView helperText, saveChangesText, characterCountText;
    private String currentLabel;
    private TickTrackDatabase tickTrackDatabase;
    private ConstraintLayout rootLayout;
    int themeSet = 1;

    public SingleInputDialog(Activity activity, int style, String currentLabel){
        super(activity, style);
        this.activity = activity;
        this.currentLabel = currentLabel;
    }

    public SingleInputDialog(Activity activity, String currentLabel){
        super(activity);
        this.activity = activity;
        this.currentLabel = currentLabel;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_text_item, new ConstraintLayout(activity), false);
        setContentView(view);
        Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

//        WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
//        Display display = wm.getDefaultDisplay();
//        DisplayMetrics metrics = new DisplayMetrics();
//        display.getMetrics(metrics);
//        int width = Math.min(metrics.widthPixels, 1280);
//        int height = -1; // MATCH_PARENT
//        getWindow().setLayout(width, height);

        initVariables(view);

        tickTrackDatabase = new TickTrackDatabase(activity);
        themeSet = tickTrackDatabase.getThemeMode();

        setupTheme();

        inputText.addTextChangedListener(new TextWatcher() {
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
            rootLayout.setBackgroundResource(R.drawable.round_rect_light);
            helperText.setTextColor(activity.getResources().getColor(R.color.DarkText));
            helperText.setBackgroundResource(R.color.LightGray);
            okButton.setBackgroundResource(R.drawable.button_selector_light);
            cancelButton.setBackgroundResource(R.drawable.button_selector_light);
            inputText.setTextColor(activity.getResources().getColor(R.color.DarkText));
            inputText.setHintTextColor(activity.getResources().getColor(R.color.GrayOnDark));

        } else {
            rootLayout.setBackgroundResource(R.drawable.round_rect_dark);
            helperText.setBackgroundResource(R.color.Gray);
            helperText.setTextColor(activity.getResources().getColor(R.color.LightText));
            okButton.setBackgroundResource(R.drawable.button_selector_dark);
            cancelButton.setBackgroundResource(R.drawable.button_selector_dark);
            inputText.setTextColor(activity.getResources().getColor(R.color.LightText));
            inputText.setHintTextColor(activity.getResources().getColor(R.color.GrayOnLight));

        }
    }

    private void initVariables(View view) {
        okButton = view.findViewById(R.id.labelDialogOkButton);
        cancelButton = view.findViewById(R.id.labelDialogCancelButton);
        inputText = view.findViewById(R.id.labelDialogInputText);
        helperText = view.findViewById(R.id.labelDialogHelpText);
        inputText.setHint(currentLabel);
        saveChangesText = findViewById(R.id.labelDialogSaveChangesText);
        rootLayout = findViewById(R.id.singleItemDialogRootLayout);
        characterCountText = findViewById(R.id.labelDialogCharacterCount);
    }

}
