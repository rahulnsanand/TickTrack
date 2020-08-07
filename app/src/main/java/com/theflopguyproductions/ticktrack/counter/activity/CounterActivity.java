package com.theflopguyproductions.ticktrack.counter.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.SoYouADeveloperHuh;
import com.theflopguyproductions.ticktrack.counter.CounterData;
import com.theflopguyproductions.ticktrack.counter.notification.CounterNotificationService;
import com.theflopguyproductions.ticktrack.dialogs.DeleteCounter;
import com.theflopguyproductions.ticktrack.dialogs.DeleteCounterFromActivity;
import com.theflopguyproductions.ticktrack.ui.counter.CounterFragment;
import com.theflopguyproductions.ticktrack.ui.lottie.LottieAnimationView;
import com.theflopguyproductions.ticktrack.ui.utils.swipebutton.SwipeButton;
import com.theflopguyproductions.ticktrack.utils.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.TickTrackThemeSetter;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.UUID;

public class CounterActivity extends AppCompatActivity {

    TickTrackDatabase tickTrackDatabase;

    private SwipeButton plusButton, minusButton;
    private ConstraintLayout plusButtonBig, minusButtonBig;
    private Switch buttonSwitch;
    private ScrollView counterActivityScrollView;
    private TextView CounterText, counterLabel, counterSwitchMode, plusText, minusText;
    private int currentCount, currentPosition;
    private ArrayList<CounterData> counterDataArrayList;
    ConstraintLayout toolbar, rootLayout, switchLayout, switchUpperDivider, switchLowerDivider;
    int flagColor;
    private ImageButton backButton, deleteButton, editButton;
    private Activity activity;
    LottieAnimationView lottieAnimationView;
    private SharedPreferences sharedPreferences;
    private String counterID;


    @Override
    protected void onStop() {
        super.onStop();
        sharedPreferences = this.getSharedPreferences("TickTrackData", MODE_PRIVATE);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        flagColor = counterDataArrayList.get(currentPosition).getCounterFlag();
        TickTrackThemeSetter.counterActivityTheme(this,toolbar, rootLayout, flagColor, plusButtonBig, minusButtonBig,
                plusText, minusText,
                plusButton, minusButton, counterActivityScrollView, counterSwitchMode, buttonSwitch, switchLayout, switchLowerDivider, switchUpperDivider, tickTrackDatabase);
        milestoneItIs();
    }



    SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener = (sharedPreferences, s) ->  {
        counterDataArrayList = tickTrackDatabase.retrieveCounterList();
        if (s.equals("CounterData")){
            CounterText.setText(""+counterDataArrayList.get(currentPosition).getCounterValue());
            currentCount = counterDataArrayList.get(currentPosition).getCounterValue();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter);


        CounterText = findViewById(R.id.counterActivityValueTextView);
        backButton = findViewById(R.id.counterActivityBackButton);
        editButton = findViewById(R.id.counterActivityEditButton);
        deleteButton = findViewById(R.id.counterActivityTrashButton);
        counterLabel = findViewById(R.id.counterActivityLabelTextView);
        plusButton = findViewById(R.id.plusbtn);
        minusButton = findViewById(R.id.minusbtn);
        plusButtonBig = findViewById(R.id.plusButton);
        minusButtonBig =findViewById(R.id.minusButton);
        buttonSwitch = findViewById(R.id.counterActivityButtonSwitch);
        lottieAnimationView = findViewById(R.id.counterActivityLottieAnimationView);
        sharedPreferences = this.getSharedPreferences("TickTrackData",MODE_PRIVATE);

        activity = this;
        tickTrackDatabase = new TickTrackDatabase(activity);

        counterSwitchMode = findViewById(R.id.counterActivitySwitchModeTextView);
        counterSwitchMode.setText("Swipe mode");
        counterActivityScrollView = findViewById(R.id.counterActivityScrollView);
        plusText = findViewById(R.id.counterActivityPlusText);
        minusText = findViewById(R.id.counterActivityMinusText);

        toolbar = findViewById(R.id.counterActivityToolbar);
        rootLayout = findViewById(R.id.counterActivityLayout);
        switchLayout = findViewById(R.id.counterActivitySwitchLayout);
        switchUpperDivider = findViewById(R.id.counterActivitySwitchLayoutUpperDivider);
        switchLowerDivider = findViewById(R.id.counterActivitySwitchLayoutLowerDivider);

        counterID = getIntent().getStringExtra("currentCounterPosition");
        counterDataArrayList = tickTrackDatabase.retrieveCounterList();
        currentPosition = getCurrentPosition();

        flagColor = counterDataArrayList.get(currentPosition).getCounterFlag();

        TickTrackThemeSetter.counterActivityTheme(this,toolbar, rootLayout, flagColor, plusButtonBig, minusButtonBig,
                plusText, minusText,
                plusButton, minusButton, counterActivityScrollView, counterSwitchMode, buttonSwitch,
                switchLayout, switchLowerDivider, switchUpperDivider, tickTrackDatabase);


        currentCount = counterDataArrayList.get(currentPosition).getCounterValue();
        counterLabel.setText(counterDataArrayList.get(currentPosition).getCounterLabel());

        CounterText.setText(""+counterDataArrayList.get(currentPosition).getCounterValue());

        backButton.setOnClickListener(view -> onBackPressed());

        buttonSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            changeButtonVisibility(compoundButton);
        });
        buttonSwitch.setChecked(counterDataArrayList.get(currentPosition).isCounterSwipeMode());
        setOnClickListeners();

        editButton.setOnClickListener(view -> {
            Intent intent = new Intent(activity, CounterEditActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            intent.putExtra("CurrentPosition",currentPosition);
            startActivity(intent);
        });

        deleteButton.setOnClickListener(view -> {
            DeleteCounterFromActivity counterDelete = new DeleteCounterFromActivity(activity, currentPosition, counterDataArrayList.get(currentPosition).getCounterLabel(),
                    counterDataArrayList.get(currentPosition).getCounterID(), sharedPreferenceChangeListener);

            counterDelete.show();
        });

        milestoneItIs();


        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);

    }

    private int getCurrentPosition() {
        for(int i = 0; i < counterDataArrayList.size(); i ++){
            if(counterDataArrayList.get(i).getCounterID().equals(counterID)){
                return i;
            }
        }
     return 0;
    }

    private final Handler mHandler = new Handler();

    private void milestoneItIs() {
        if(counterDataArrayList.get(currentPosition).getCounterSignificantCount()>0){
            if(currentCount==counterDataArrayList.get(currentPosition).getCounterSignificantCount()){
                lottieAnimationView.playAnimation();
                lottieAnimationView.setVisibility(View.VISIBLE);

                mHandler.postDelayed(() -> {
                    lottieAnimationView.cancelAnimation();
                    lottieAnimationView.setVisibility(View.INVISIBLE);
                }, 5000);

            } else {
                lottieAnimationView.cancelAnimation();
            }
        } else {
            lottieAnimationView.setVisibility(View.INVISIBLE);
            lottieAnimationView.cancelAnimation();
        }
    }

    private void changeButtonVisibility(CompoundButton compoundButton){
        if(compoundButton.isChecked()){
            counterDataArrayList.get(currentPosition).setCounterSwipeMode(true);
            tickTrackDatabase.storeCounterList(counterDataArrayList);
            counterSwitchMode.setText("Click mode");
            plusButtonBig.setVisibility(View.VISIBLE);
            minusButtonBig.setVisibility(View.VISIBLE);
            plusButton.setVisibility(View.INVISIBLE);
            minusButton.setVisibility(View.INVISIBLE);
        } else {
            counterDataArrayList.get(currentPosition).setCounterSwipeMode(false);
            tickTrackDatabase.storeCounterList(counterDataArrayList);
            counterSwitchMode.setText("Swipe mode");
            plusButtonBig.setVisibility(View.INVISIBLE);
            minusButtonBig.setVisibility(View.INVISIBLE);
            plusButton.setVisibility(View.VISIBLE);
            minusButton.setVisibility(View.VISIBLE);
        }
    }

    private void setOnClickListeners(){
        plusButton.setOnActiveListener(() -> {
            currentCount+=1;
            counterDataArrayList.get(currentPosition).setCounterValue(currentCount);
            counterDataArrayList.get(currentPosition).setCounterTimestamp(new Timestamp(System.currentTimeMillis()));
            CounterText.setText(""+counterDataArrayList.get(currentPosition).getCounterValue());
            tickTrackDatabase.storeCounterList(counterDataArrayList);
            milestoneItIs();
            refreshNotificationStatus();
        });

        minusButton.setOnActiveListener(() -> {
            if(currentCount>=1){
                currentCount-=1;
                counterDataArrayList.get(currentPosition).setCounterValue(currentCount);
                counterDataArrayList.get(currentPosition).setCounterTimestamp(new Timestamp(System.currentTimeMillis()));
                CounterText.setText(""+counterDataArrayList.get(currentPosition).getCounterValue());
                tickTrackDatabase.storeCounterList(counterDataArrayList);
                milestoneItIs();
                refreshNotificationStatus();
            }
        });
        plusButtonBig.setOnClickListener(view -> {
            currentCount+=1;
            counterDataArrayList.get(currentPosition).setCounterValue(currentCount);
            counterDataArrayList.get(currentPosition).setCounterTimestamp(new Timestamp(System.currentTimeMillis()));
            CounterText.setText(""+counterDataArrayList.get(currentPosition).getCounterValue());
            tickTrackDatabase.storeCounterList(counterDataArrayList);
            milestoneItIs();
            refreshNotificationStatus();
        });

        minusButtonBig.setOnClickListener(view -> {
            if(currentCount>=1){
                currentCount-=1;
                counterDataArrayList.get(currentPosition).setCounterValue(currentCount);
                CounterText.setText(""+counterDataArrayList.get(currentPosition).getCounterValue());
                tickTrackDatabase.storeCounterList(counterDataArrayList);
                milestoneItIs();
                refreshNotificationStatus();
            }
        });
    }
    private void refreshNotificationStatus() {
        if(counterDataArrayList.get(currentPosition).isCounterPersistentNotification()){
            Intent intent = new Intent(this, CounterNotificationService.class);
            intent.setAction(CounterNotificationService.ACTION_REFRESH_SERVICE);
            startService(intent);
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
        Intent intent = new Intent(this, SoYouADeveloperHuh.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}