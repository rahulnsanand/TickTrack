package com.theflopguyproductions.ticktrack.counter.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.SoYouADeveloperHuh;
import com.theflopguyproductions.ticktrack.counter.CounterData;
import com.theflopguyproductions.ticktrack.counter.notification.CounterNotificationService;
import com.theflopguyproductions.ticktrack.dialogs.DeleteCounterFromActivity;
import com.theflopguyproductions.ticktrack.ui.lottie.LottieAnimationView;
import com.theflopguyproductions.ticktrack.ui.utils.swipebutton.SwipeButton;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.helpers.TickTrackThemeSetter;

import java.sql.Timestamp;
import java.util.ArrayList;

public class CounterActivity extends AppCompatActivity {

    TickTrackDatabase tickTrackDatabase;

    private SwipeButton plusButton, minusButton;
    private ConstraintLayout plusButtonBig, minusButtonBig;
    private Switch buttonSwitch;
    private ScrollView counterActivityScrollView;
    private TextView CounterText, counterLabel, counterSwitchMode, plusText, minusText;
    private int currentCount;
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
        sharedPreferences = tickTrackDatabase.getSharedPref(this);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
        tickTrackDatabase.storeCurrentFragmentNumber(1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        flagColor = counterDataArrayList.get(getCurrentPosition()).getCounterFlag();
        TickTrackThemeSetter.counterActivityTheme(this,toolbar, rootLayout, flagColor, plusButtonBig, minusButtonBig,
                plusText, minusText,
                plusButton, minusButton, counterActivityScrollView, counterSwitchMode, buttonSwitch, switchLayout, switchLowerDivider, switchUpperDivider, tickTrackDatabase);
        milestoneItIs();
    }



    SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener = (sharedPreferences, s) ->  {
        counterDataArrayList = tickTrackDatabase.retrieveCounterList();
        if (s.equals("CounterData")){
            CounterText.setText(""+counterDataArrayList.get(getCurrentPosition()).getCounterValue());
            currentCount = counterDataArrayList.get(getCurrentPosition()).getCounterValue();
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
        sharedPreferences = tickTrackDatabase.getSharedPref(this);

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

        flagColor = counterDataArrayList.get(getCurrentPosition()).getCounterFlag();

        TickTrackThemeSetter.counterActivityTheme(this,toolbar, rootLayout, flagColor, plusButtonBig, minusButtonBig,
                plusText, minusText,
                plusButton, minusButton, counterActivityScrollView, counterSwitchMode, buttonSwitch,
                switchLayout, switchLowerDivider, switchUpperDivider, tickTrackDatabase);


        currentCount = counterDataArrayList.get(getCurrentPosition()).getCounterValue();
        counterLabel.setText(counterDataArrayList.get(getCurrentPosition()).getCounterLabel());

        CounterText.setText(""+counterDataArrayList.get(getCurrentPosition()).getCounterValue());

        backButton.setOnClickListener(view -> onBackPressed());

        buttonSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            changeButtonVisibility(compoundButton);
        });
        buttonSwitch.setChecked(counterDataArrayList.get(getCurrentPosition()).isCounterSwipeMode());
        setOnClickListeners();

        editButton.setOnClickListener(view -> {
            Intent intent = new Intent(activity, CounterEditActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            intent.putExtra("CurrentPosition", counterID);
            startActivity(intent);
        });

        deleteButton.setOnClickListener(view -> {
            DeleteCounterFromActivity counterDelete = new DeleteCounterFromActivity(activity, getCurrentPosition(), counterDataArrayList.get(getCurrentPosition()).getCounterLabel(),
                    counterDataArrayList.get(getCurrentPosition()).getCounterID(), sharedPreferenceChangeListener);

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
        if(counterDataArrayList.get(getCurrentPosition()).getCounterSignificantCount()>0){
            if(currentCount==counterDataArrayList.get(getCurrentPosition()).getCounterSignificantCount()){
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
            counterDataArrayList.get(getCurrentPosition()).setCounterSwipeMode(true);
            tickTrackDatabase.storeCounterList(counterDataArrayList);
            counterSwitchMode.setText("Click mode");
            plusButtonBig.setVisibility(View.VISIBLE);
            minusButtonBig.setVisibility(View.VISIBLE);
            plusButton.setVisibility(View.INVISIBLE);
            minusButton.setVisibility(View.INVISIBLE);
        } else {
            counterDataArrayList.get(getCurrentPosition()).setCounterSwipeMode(false);
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
            counterDataArrayList.get(getCurrentPosition()).setCounterValue(currentCount);
            counterDataArrayList.get(getCurrentPosition()).setCounterTimestamp(new Timestamp(System.currentTimeMillis()));
            CounterText.setText(""+counterDataArrayList.get(getCurrentPosition()).getCounterValue());
            tickTrackDatabase.storeCounterList(counterDataArrayList);
            milestoneItIs();
            refreshNotificationStatus();
        });

        minusButton.setOnActiveListener(() -> {
            if(currentCount>=1){
                currentCount-=1;
                counterDataArrayList.get(getCurrentPosition()).setCounterValue(currentCount);
                counterDataArrayList.get(getCurrentPosition()).setCounterTimestamp(new Timestamp(System.currentTimeMillis()));
                CounterText.setText(""+counterDataArrayList.get(getCurrentPosition()).getCounterValue());
                tickTrackDatabase.storeCounterList(counterDataArrayList);
                milestoneItIs();
                refreshNotificationStatus();
            }
        });
        plusButtonBig.setOnClickListener(view -> {
            currentCount+=1;
            counterDataArrayList.get(getCurrentPosition()).setCounterValue(currentCount);
            counterDataArrayList.get(getCurrentPosition()).setCounterTimestamp(new Timestamp(System.currentTimeMillis()));
            CounterText.setText(""+counterDataArrayList.get(getCurrentPosition()).getCounterValue());
            tickTrackDatabase.storeCounterList(counterDataArrayList);
            milestoneItIs();
            refreshNotificationStatus();
        });

        minusButtonBig.setOnClickListener(view -> {
            if(currentCount>=1){
                currentCount-=1;
                counterDataArrayList.get(getCurrentPosition()).setCounterValue(currentCount);
                CounterText.setText(""+counterDataArrayList.get(getCurrentPosition()).getCounterValue());
                tickTrackDatabase.storeCounterList(counterDataArrayList);
                milestoneItIs();
                refreshNotificationStatus();
            }
        });
    }
    private void refreshNotificationStatus() {
        if(counterDataArrayList.get(getCurrentPosition()).isCounterPersistentNotification()){
            Intent intent = new Intent(this, CounterNotificationService.class);
            intent.setAction(CounterNotificationService.ACTION_REFRESH_SERVICE);
            startService(intent);
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
        tickTrackDatabase.storeCurrentFragmentNumber(1);
        Intent intent = new Intent(this, SoYouADeveloperHuh.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}