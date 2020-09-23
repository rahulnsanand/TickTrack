package com.theflopguyproductions.ticktrack.counter.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.SoYouADeveloperHuh;
import com.theflopguyproductions.ticktrack.counter.CounterData;
import com.theflopguyproductions.ticktrack.counter.notification.CounterNotificationService;
import com.theflopguyproductions.ticktrack.dialogs.DeleteCounterFromActivity;
import com.theflopguyproductions.ticktrack.dialogs.SingleInputDialog;
import com.theflopguyproductions.ticktrack.dialogs.SwipeDialog;
import com.theflopguyproductions.ticktrack.ui.counter.CounterFragment;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.helpers.TickTrackThemeSetter;

import java.util.ArrayList;
import java.util.Objects;

public class CounterEditActivity extends AppCompatActivity {

    TickTrackDatabase tickTrackDatabase;

//    private int currentPosition;
    private String counterID;
    private ImageButton backButton, deleteCounterButton;
    private ImageView counterFlag;
    private Switch counterButtonSwitch, counterNotificationSwitch, negativeValueSwitch;
    private TextView counterLabel, counterValue, counterMilestone, counterButtonMode;
    private TextView counterLabelTitle, counterValueTitle, counterMilestoneTitle, counterButtonModeTitle, counterFlagTitle, counterNotificationTitle, counterNotificationDetail, counterMilestoneDetail;
    private ImageButton saveChangesButton;
    private ConstraintLayout counterLabelLayout, counterValueLayout, counterMilestoneLayout, counterFlagLayout, counterButtonModeLayout, counterNotificationLayout, counterEditRootLayout, counterEditToolbarLayout, counterFlagGroupLayout;
    private ConstraintLayout counterLabelDivider, counterValueDivider, counterMilestoneDivider, counterFlagDivider, counterButtonModeDivider, counterNegativeLayout, negativeDivider;
    private ArrayList<CounterData> counterDataArrayList = new ArrayList<>();
    private Activity activity;
    private ChipGroup counterFlagGroup;
    private Chip cherryFlag, limeFlag, peachFlag, plumFlag, berryFlag;
    private boolean flagLayoutOpen, isChanged = false;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onStop() {
        super.onStop();
        sharedPreferences = tickTrackDatabase.getSharedPref(this);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
        tickTrackDatabase.storeCurrentFragmentNumber(1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter_edit);

        activity = this;
        tickTrackDatabase = new TickTrackDatabase(activity);
        counterDataArrayList = tickTrackDatabase.retrieveCounterList();
        counterID = getIntent().getStringExtra("CurrentPosition");
        int flagColor = counterDataArrayList.get(getCurrentPosition()).getCounterFlag();
        sharedPreferences = tickTrackDatabase.getSharedPref(this);

        initVariables();

        TickTrackThemeSetter.counterEditActivityTheme(this, counterLabelLayout, counterValueLayout, counterMilestoneLayout, counterFlagLayout,
                counterButtonModeLayout, counterNotificationLayout, counterEditRootLayout,
                counterLabel, counterValue, counterMilestone, counterButtonMode, counterNotificationDetail, counterMilestoneDetail, flagColor,
                counterLabelDivider, counterValueDivider, counterMilestoneDivider, counterFlagDivider, counterButtonModeDivider, tickTrackDatabase,
                cherryFlag, limeFlag, peachFlag, plumFlag, berryFlag, counterEditToolbarLayout, counterNegativeLayout, negativeDivider);

        setupPrefixValues();

        setupOnClickListeners();

        setupOnCheckedListeners();

        flagValueCheck();

        deleteCounterButton.setOnClickListener(view -> {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
            new Handler().post(() -> {
                DeleteCounterFromActivity counterDelete = new DeleteCounterFromActivity(activity, getCurrentPosition(), counterDataArrayList.get(getCurrentPosition()).getCounterLabel(),
                        counterDataArrayList.get(getCurrentPosition()).getCounterID());
                counterDelete.show();
                counterDelete.yesButton.setOnClickListener(view12 -> {
                    counterDelete.dismiss();
                    counterDelete.killNotification();
                    CounterFragment.deleteCounter(getCurrentPosition(), activity, counterDataArrayList.get(getCurrentPosition()).getCounterLabel());
                    activity.startActivity(new Intent(activity, SoYouADeveloperHuh.class));
                    Toast.makeText(activity, "Counter Deleted", Toast.LENGTH_SHORT).show();
                });
                counterDelete.setOnCancelListener(dialogInterface -> sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener));
            });

        });
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);

        if(tickTrackDatabase.getThemeMode()==1){
            getWindow().setNavigationBarColor(getResources().getColor(R.color.HoloLightGray) );
        } else {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.HoloBlack) );
        }
    }
    private int getCurrentPosition() {
        for(int i = 0; i < counterDataArrayList.size(); i ++){
            if(counterDataArrayList.get(i).getCounterID().equals(counterID)){
                return i;
            }
        }
        return 0;
    }
    private void setupOnCheckedListeners() {
        counterNotificationSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            if(compoundButton.isChecked()){
                counterDataArrayList.get(getCurrentPosition()).setCounterPersistentNotification(true);

            } else {
                counterDataArrayList.get(getCurrentPosition()).setCounterPersistentNotification(false);

            }
            isChanged = true;
        });
        counterButtonSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            if(compoundButton.isChecked()){
                counterDataArrayList.get(getCurrentPosition()).setCounterSwipeMode(true);
                counterButtonMode.setText("Switch to Swipe Mode");
            } else {
                counterDataArrayList.get(getCurrentPosition()).setCounterSwipeMode(false);
                counterButtonMode.setText("Switch to Click Mode");
            }
            isChanged = true;
        });
        negativeValueSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            if(compoundButton.isChecked()){
                counterDataArrayList.get(getCurrentPosition()).setNegativeAllowed(true);
            } else {
                counterDataArrayList.get(getCurrentPosition()).setNegativeAllowed(false);
            }
            isChanged = true;
        });
    }

    private void stopNotificationService() {
        Intent intent = new Intent(this, CounterNotificationService.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setAction(CounterNotificationService.ACTION_KILL_NOTIFICATIONS);
        startService(intent);
    }

    private void startNotificationService() {
        Intent intent = new Intent(this, CounterNotificationService.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setAction(CounterNotificationService.ACTION_START_COUNTER_SERVICE);
        startService(intent);
    }

    private void initVariables() {
        backButton = findViewById(R.id.counterEditActivityBackButton);
        deleteCounterButton = findViewById(R.id.counterEditActivityDeleteButton);
        counterFlag = findViewById(R.id.counterEditActivityFlagImageView);
        counterButtonSwitch = findViewById(R.id.counterEditActivityButtonModeSwitch);
        counterNotificationSwitch = findViewById(R.id.counterEditActivityNotificationSwitch);
        counterLabel = findViewById(R.id.counterEditActivityLabelTextView);
        counterValue = findViewById(R.id.counterEditActivityValueTextView);
        counterMilestone = findViewById(R.id.counterEditActivitySignificantTextView);
        counterButtonMode = findViewById(R.id.counterEditActivityButtonModeTextView);
        saveChangesButton = findViewById(R.id.counterEditActivitySaveButton);
        counterLabelTitle = findViewById(R.id.counterEditActivityLabelTitle);
        counterValueTitle = findViewById(R.id.counterEditActivityValueTitle);
        counterMilestoneTitle = findViewById(R.id.counterEditActivitySignificantTitle);
        counterButtonModeTitle = findViewById(R.id.counterEditActivityButtonModeTitle);
        counterFlagTitle = findViewById(R.id.counterEditActivityFlagTitle);
        counterNotificationTitle = findViewById(R.id.counterEditActivityNotificationTitle);
        counterLabelLayout = findViewById(R.id.counterEditActivityLabelLayout);
        counterValueLayout = findViewById(R.id.counterEditActivityValueLayout);
        counterMilestoneLayout = findViewById(R.id.counterEditActivitySignificantLayout);
        counterFlagLayout = findViewById(R.id.counterEditActivityFlagLayout);
        counterButtonModeLayout = findViewById(R.id.counterEditActivityButtonModeLayout);
        counterNotificationLayout = findViewById(R.id.counterEditActivityNotificationLayout);
        counterEditRootLayout = findViewById(R.id.counterEditActivityRootLayout);
        counterEditToolbarLayout = findViewById(R.id.counterEditActivityToolBarLayout);
        counterNotificationDetail = findViewById(R.id.counterEditActivityNotificationDetailTextView);
        counterMilestoneDetail = findViewById(R.id.counterEditActivitySignificantDescription);
        counterFlagGroupLayout = findViewById(R.id.counterEditActivityFlagChipLayout);
        negativeValueSwitch = findViewById(R.id.counterEditNegativeValueSwitch);
        counterNegativeLayout = findViewById(R.id.counterEditActivityNegativeLayout);

        counterLabelDivider = findViewById(R.id.counterEditActivityLabelDivider);
        counterValueDivider = findViewById(R.id.counterEditActivityValueDivider);
        counterMilestoneDivider = findViewById(R.id.counterEditActivityMilestoneDivider);
        counterFlagDivider = findViewById(R.id.counterEditActivityFlagDivider);
        counterButtonModeDivider = findViewById(R.id.counterEditActivityButtonModeDivider);
        negativeDivider = findViewById(R.id.counterEditActivityNegativeDivider);

        counterFlagGroup = findViewById(R.id.counterEditActivityFlagGroup);
        cherryFlag = findViewById(R.id.counterEditActivityCherryFlag);
        limeFlag = findViewById(R.id.counterEditActivityLimeFlag);
        peachFlag = findViewById(R.id.counterEditActivityPeachFlag);
        plumFlag = findViewById(R.id.counterEditActivityPlumFlag);
        berryFlag = findViewById(R.id.counterEditActivityBerryFlag);
    }

    SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener = (sharedPreferences, s) ->  {
        counterDataArrayList = tickTrackDatabase.retrieveCounterList();
        if (s.equals("CounterData")){
            counterValue.setText(""+counterDataArrayList.get(getCurrentPosition()).getCounterValue());
        }
    };

    private void setupPrefixValues() {
        setFlagColor(counterDataArrayList.get(getCurrentPosition()).getCounterFlag());

        counterLabel.setText(counterDataArrayList.get(getCurrentPosition()).getCounterLabel());
        counterValue.setText(""+counterDataArrayList.get(getCurrentPosition()).getCounterValue());
        if(counterDataArrayList.get(getCurrentPosition()).isCounterSignificantExist()){
            counterMilestone.setText(counterDataArrayList.get(getCurrentPosition()).getCounterSignificantCount()+"");
        } else {
            counterMilestone.setText("-");
        }
        counterButtonSwitch.setChecked(counterDataArrayList.get(getCurrentPosition()).isCounterSwipeMode());
        if(counterDataArrayList.get(getCurrentPosition()).isCounterSwipeMode()){
            counterButtonMode.setText("Swipe mode");
        } else {
            counterButtonMode.setText("Click mode");
        }
        counterNotificationSwitch.setChecked(counterDataArrayList.get(getCurrentPosition()).isCounterPersistentNotification());

        counterFlagGroupLayout.setVisibility(View.GONE);
        flagLayoutOpen = true;

        if(counterDataArrayList.get(getCurrentPosition()).isNegativeAllowed()){
            negativeValueSwitch.setChecked(true);
        } else {
            negativeValueSwitch.setChecked(false);
        }

    }
    private void setFlagColor(int flagColor) {
        if(flagColor==1){
            counterFlag.setImageResource(R.drawable.ic_flag_red);
            cherryFlag.setChecked(true);
        }
        else if(flagColor==2){
            counterFlag.setImageResource(R.drawable.ic_flag_green);
            limeFlag.setChecked(true);
        }
        else if(flagColor==3){
            counterFlag.setImageResource(R.drawable.ic_flag_orange);
            peachFlag.setChecked(true);
        }
        else if(flagColor==4){
            counterFlag.setImageResource(R.drawable.ic_flag_purple);
            plumFlag.setChecked(true);
        }
        else if(flagColor==5){
            counterFlag.setImageResource(R.drawable.ic_flag_blue);
            berryFlag.setChecked(true);
        } else {
            if(tickTrackDatabase.getThemeMode()==1){
                counterFlag.setImageResource(R.drawable.ic_round_flag_dark_24);
            } else {
                counterFlag.setImageResource(R.drawable.ic_round_flag_light_24);
            }
        }
    }

    private void setupOnClickListeners() {

        counterNegativeLayout.setOnClickListener(view -> {
            if(counterDataArrayList.get(getCurrentPosition()).isNegativeAllowed()){
                counterDataArrayList.get(getCurrentPosition()).setNegativeAllowed(false);
                negativeValueSwitch.setChecked(false);
            } else {
                counterDataArrayList.get(getCurrentPosition()).setNegativeAllowed(true);
                negativeValueSwitch.setChecked(true);
            }
            isChanged = true;
        });

        counterLabelLayout.setOnClickListener(view -> {
            new Handler().post(() -> {
                SingleInputDialog labelDialog = new SingleInputDialog(activity, counterLabel.getText().toString());
                labelDialog.show();
                labelDialog.saveChangesText.setVisibility(View.GONE);
                labelDialog.inputText.setVisibility(View.VISIBLE);
                if(labelDialog.inputText.requestFocus()){
                    Objects.requireNonNull(labelDialog.getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
                labelDialog.helperText.setVisibility(View.VISIBLE);
                labelDialog.inputText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT | InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE |InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                labelDialog.okButton.setOnClickListener(view1 -> {
                    counterLabel.setText(labelDialog.inputText.getText().toString());
                    labelDialog.dismiss();
                    isChanged = true;
                });
                labelDialog.cancelButton.setOnClickListener(view12 -> {
                    labelDialog.dismiss();
                });

            });
        });
        counterValueLayout.setOnClickListener(view -> {
            new Handler().post(() -> {
                SingleInputDialog labelDialog = new SingleInputDialog(activity, ""+counterValue.getText().toString());
                labelDialog.show();
                labelDialog.saveChangesText.setVisibility(View.GONE);
                labelDialog.inputText.setVisibility(View.VISIBLE);
                labelDialog.helperText.setVisibility(View.VISIBLE);
                labelDialog.characterCountText.setVisibility(View.GONE);
                if(labelDialog.inputText.requestFocus()){
                    Objects.requireNonNull(labelDialog.getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
                labelDialog.inputText.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_CLASS_NUMBER);
                labelDialog.helperText.setText("Value");
                labelDialog.inputText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(19)});
                labelDialog.okButton.setOnClickListener(view1 -> {
                    try {
                        long value = Long.parseLong(labelDialog.inputText.getText().toString());
                        counterValue.setText(value+"");
                        labelDialog.dismiss();
                        isChanged = true;
                    } catch (Exception e){
                        labelDialog.inputText.setBackgroundResource(R.drawable.label_edit_text_error);
                        new Handler().postDelayed(() -> labelDialog.inputText.setBackgroundResource(R.drawable.label_edit_text_accent), 2000);
                        Toast.makeText(activity, "Value must be less than 9223372036854775807", Toast.LENGTH_SHORT).show();
                    }
                });
                labelDialog.cancelButton.setOnClickListener(view12 -> {
                    labelDialog.dismiss();
                });

            });

        });
        counterMilestoneLayout.setOnClickListener(view -> {
            new Handler().post(() -> {
                SingleInputDialog labelDialog = new SingleInputDialog(activity, ""+counterMilestone.getText().toString());
                labelDialog.show();
                labelDialog.saveChangesText.setVisibility(View.GONE);
                labelDialog.inputText.setVisibility(View.VISIBLE);
                labelDialog.helperText.setVisibility(View.VISIBLE);
                labelDialog.characterCountText.setVisibility(View.GONE);
                labelDialog.inputText.setHint(counterMilestone.getText().toString());

                String currentValue = counterMilestone.getText().toString();

                if(labelDialog.inputText.requestFocus()){
                    Objects.requireNonNull(labelDialog.getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
                labelDialog.inputText.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_CLASS_NUMBER);
                labelDialog.helperText.setText("Milestone value");
                labelDialog.cancelButton.setText("RESET");

                labelDialog.okButton.setOnClickListener(view1 -> {

                    if(!labelDialog.inputText.getText().toString().equals("") && !labelDialog.inputText.getText().toString().equals("-")){

                        if(Integer.parseInt(labelDialog.inputText.getText().toString().replaceAll("[^\\d]", ""))!=0){

                            counterMilestone.setText(labelDialog.inputText.getText()+"");
                            isChanged = true;

                        } else {
                            if(currentValue.equals(labelDialog.inputText.getText().toString())){
                                counterMilestone.setText(currentValue);
                            } else {
                                counterMilestone.setText("-");
                            }
                        }

                    } else {
                        counterMilestone.setText("-");
                    }
                    labelDialog.dismiss();
                });
                labelDialog.cancelButton.setOnClickListener(view12 -> {
                    labelDialog.dismiss();
                    counterMilestone.setText("-");
                });

            });

        });

        counterFlagLayout.setOnClickListener(view -> flagGroupVisibilityToggle());
        counterButtonModeLayout.setOnClickListener(view -> toggleButtonMode());
        counterNotificationLayout.setOnClickListener(view -> toggleNotificationSwitch());
        saveChangesButton.setOnClickListener(view -> {
            saveData();
        });
        backButton.setOnClickListener(view -> onBackPressed());
    }

    private void flagGroupVisibilityToggle() {
        if(flagLayoutOpen){
            counterFlagGroupLayout.setVisibility(View.VISIBLE);
            flagLayoutOpen = false;
        } else {
            counterFlagGroupLayout.setVisibility(View.GONE);
            flagLayoutOpen = true;
        }
    }

    private void flagValueCheck(){
        counterFlagGroup.setOnCheckedChangeListener((group, checkedId) -> {
            Chip chip = counterFlagGroup.findViewById(checkedId);
            if(chip != null){
                if(chip.getText().toString().equals("Cherry")){
                    setFlagColor(1);
                    isChanged = true;
                }
                if(chip.getText().toString().equals("Lime")){
                    setFlagColor(2);
                    isChanged = true;
                }
                if(chip.getText().toString().equals("Peach")){
                    setFlagColor(3);
                    isChanged = true;
                }
                if(chip.getText().toString().equals("Plum")){
                    setFlagColor(4);
                    isChanged = true;
                }
                if(chip.getText().toString().equals("Berry")){
                    setFlagColor(5);
                    isChanged = true;
                }
            } else {
                setFlagColor(0);
                isChanged = true;
            }
        });
    }

    private void toggleNotificationSwitch() {
        if(counterNotificationSwitch.isChecked()){
            counterDataArrayList.get(getCurrentPosition()).setCounterPersistentNotification(false);
            counterNotificationSwitch.setChecked(false);
        } else {
            counterDataArrayList.get(getCurrentPosition()).setCounterPersistentNotification(true);
            counterNotificationSwitch.setChecked(true);

        }
        isChanged = true;
    }

    private void toggleButtonMode() {
        if(counterButtonSwitch.isChecked()){
            counterDataArrayList.get(getCurrentPosition()).setCounterSwipeMode(false);
            counterButtonMode.setText("Click mode");
            counterButtonSwitch.setChecked(false);
        } else {
            counterDataArrayList.get(getCurrentPosition()).setCounterSwipeMode(true);
            counterButtonMode.setText("Swipe mode");
            counterButtonSwitch.setChecked(true);
        }
        isChanged = true;
    }

    private void saveData(){
        if(isChanged){
            counterDataArrayList.get(getCurrentPosition()).setCounterLabel(counterLabel.getText().toString());
            counterDataArrayList.get(getCurrentPosition()).setCounterValue(Long.parseLong(counterValue.getText().toString()));
            if(!counterMilestone.getText().toString().equals("-")){
                counterDataArrayList.get(getCurrentPosition()).setCounterSignificantCount(Long.parseLong(counterMilestone.getText().toString()));
                counterDataArrayList.get(getCurrentPosition()).setCounterSignificantExist(true);
            } else {
                counterDataArrayList.get(getCurrentPosition()).setCounterSignificantCount(0);
                counterDataArrayList.get(getCurrentPosition()).setCounterSignificantExist(false);
            }
            counterDataArrayList.get(getCurrentPosition()).setCounterFlag(getCounterFlag());
            counterDataArrayList.get(getCurrentPosition()).setCounterSwipeMode(counterButtonSwitch.isChecked());
            if(counterNotificationSwitch.isChecked()){
                revertAllOtherChecks();
                counterDataArrayList.get(getCurrentPosition()).setCounterPersistentNotification(true);
                tickTrackDatabase.setCurrentCounterNotificationID(counterDataArrayList.get(getCurrentPosition()).getCounterID());
                startNotificationService();
            } else {
                counterDataArrayList.get(getCurrentPosition()).setCounterPersistentNotification(counterNotificationSwitch.isChecked());
            }

            tickTrackDatabase.storeCounterList(counterDataArrayList);

            isChanged = false;
        }
        onBackPressed();
    }

    private void revertAllOtherChecks() {
        stopNotificationService();
        for(int i = 0; i < counterDataArrayList.size(); i++){
            if(i!=getCurrentPosition()){
                counterDataArrayList.get(i).setCounterPersistentNotification(false);
            }
        }
    }

    private int getCounterFlag() {
        if(cherryFlag.isChecked()){
            return 1;
        } else if(limeFlag.isChecked()){
            return 2;
        } else if(peachFlag.isChecked()){
            return 3;
        } else if(plumFlag.isChecked()){
            return 4;
        } else if(berryFlag.isChecked()){
            return 5;
        } else {
            return 0;
        }
    }

    @Override
    public void onBackPressed() {
        if(!isChanged){
            Intent intent = new Intent(this, CounterActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("currentCounterPosition", counterID);
            startActivity(intent);
            overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
        } else {
            new Handler().post(() -> {
                SwipeDialog swipeDialog = new SwipeDialog(activity);

                swipeDialog.show();
                swipeDialog.dialogTitle.setVisibility(View.VISIBLE);
                swipeDialog.dialogMessage.setVisibility(View.GONE);
                swipeDialog.dialogTitle.setText("Save Changes?");
                swipeDialog.swipeButton.setText("Yes");
                swipeDialog.dismissButton.setText("No");
                swipeDialog.swipeButton.setOnClickListener(view1 -> {
                    saveData();
                    isChanged = false;
                });
                swipeDialog.dismissButton.setOnClickListener(view12 -> {
                    Intent intent = new Intent(activity, CounterActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("currentCounterPosition", counterID);
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
                });
            });

        }
        tickTrackDatabase.storeCurrentFragmentNumber(1);
    }
}