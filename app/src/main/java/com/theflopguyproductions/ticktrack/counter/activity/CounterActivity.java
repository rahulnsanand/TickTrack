package com.theflopguyproductions.ticktrack.counter.activity;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.SoYouADeveloperHuh;
import com.theflopguyproductions.ticktrack.counter.CounterData;
import com.theflopguyproductions.ticktrack.counter.notification.CounterNotificationService;
import com.theflopguyproductions.ticktrack.ui.lottie.LottieAnimationView;
import com.theflopguyproductions.ticktrack.ui.utils.swipebutton.SwipeButton;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.helpers.TickTrackThemeSetter;
import com.theflopguyproductions.ticktrack.widgets.counter.CounterWidget;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class CounterActivity extends AppCompatActivity {

    TickTrackDatabase tickTrackDatabase;

    private SwipeButton plusLightButton, minusLightButton, plusDarkButton, minusDarkButton;
    private ConstraintLayout plusButtonBig, minusButtonBig;
    private TextView CounterText, counterLabel, plusText, minusText;
    private int currentCount;
    private ArrayList<CounterData> counterDataArrayList;
    ConstraintLayout toolbar, rootLayout;
    int flagColor;
    private ImageButton backButton, shareButton, editButton;
    private Activity activity;
    LottieAnimationView lottieAnimationView;
    private SharedPreferences sharedPreferences;
    private String counterID;
    private Vibrator vibrator;

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
                plusText, minusText, plusLightButton, minusLightButton,
                plusDarkButton, minusDarkButton, tickTrackDatabase, CounterText);
        milestoneItIs();
    }



    SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener = (sharedPreferences, s) ->  {
        counterDataArrayList = tickTrackDatabase.retrieveCounterList();
        if (s.equals("CounterData")){
            setupCounterTextSize();
            CounterText.setText(""+counterDataArrayList.get(getCurrentPosition()).getCounterValue());
            currentCount = counterDataArrayList.get(getCurrentPosition()).getCounterValue();
        }
    };

    private void setupCounterTextSize() {
        int countValue = counterDataArrayList.get(getCurrentPosition()).getCounterValue();
        int numberOfDigits = countDigits(countValue);
        if(numberOfDigits<4){
            CounterText.setTextSize(150);
        } else if(numberOfDigits<5){
            CounterText.setTextSize(125);
        } else if(numberOfDigits<6){
            CounterText.setTextSize(100);
        } else if(numberOfDigits<7){
            CounterText.setTextSize(80);
        } else if(numberOfDigits<8){
            CounterText.setTextSize(65);
        } else if(numberOfDigits<9){
            CounterText.setTextSize(50);
        } else {
            CounterText.setTextSize(40);
        }
    }

    private int countDigits(int countValue){
        int count = 0;
        while(countValue != 0) {
            countValue /= 10;
            ++count;
        }
        return count;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter);


        CounterText = findViewById(R.id.counterActivityValueTextView);
        backButton = findViewById(R.id.counterActivityBackButton);
        editButton = findViewById(R.id.counterActivityEditButton);
        shareButton = findViewById(R.id.counterActivityShareButton);
        counterLabel = findViewById(R.id.counterActivityLabelTextView);
        plusLightButton = findViewById(R.id.plusbtn);
        minusLightButton = findViewById(R.id.minusbtn);
        plusDarkButton = findViewById(R.id.plusDarkbtn);
        minusDarkButton = findViewById(R.id.minusDarkbtn);
        plusButtonBig = findViewById(R.id.plusButton);
        minusButtonBig =findViewById(R.id.minusButton);
        lottieAnimationView = findViewById(R.id.counterActivityLottieAnimationView);
        vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
        activity = this;
        tickTrackDatabase = new TickTrackDatabase(activity);
        sharedPreferences = tickTrackDatabase.getSharedPref(this);

        plusText = findViewById(R.id.counterActivityPlusText);
        minusText = findViewById(R.id.counterActivityMinusText);

        toolbar = findViewById(R.id.counterActivityToolbar);
        rootLayout = findViewById(R.id.counterActivityLayout);

        counterID = getIntent().getStringExtra("currentCounterPosition");
        counterDataArrayList = tickTrackDatabase.retrieveCounterList();

        flagColor = counterDataArrayList.get(getCurrentPosition()).getCounterFlag();

        currentCount = counterDataArrayList.get(getCurrentPosition()).getCounterValue();
        counterLabel.setText(counterDataArrayList.get(getCurrentPosition()).getCounterLabel());

        setupCounterTextSize();
        CounterText.setText(""+counterDataArrayList.get(getCurrentPosition()).getCounterValue());

        backButton.setOnClickListener(view -> onBackPressed());

        setOnClickListeners();

        editButton.setOnClickListener(view -> {
            Intent intent = new Intent(activity, CounterEditActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            intent.putExtra("CurrentPosition", counterID);
            startActivity(intent);
        });

        shareButton.setOnClickListener(view -> {
            new Handler().post(this::getCounterDataInText);
        });

        milestoneItIs();

        changeButtonVisibility();

        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);

    }

    private void getCounterDataInText() {

        Locale locale ;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            locale = getResources().getConfiguration().getLocales().get(0);
        } else{
            locale = getResources().getConfiguration().locale;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss a, MMM dd, yyyy", locale);
        long editedTime = counterDataArrayList.get(getCurrentPosition()).getCounterTimestamp();

        String editTimeInText = simpleDateFormat.format(editedTime);

        String shareText = "---- Counter Data ----\n" +
                "Counter Name: "+counterDataArrayList.get(getCurrentPosition()).getCounterLabel()+"\n"+
                "Counter Value: "+counterDataArrayList.get(getCurrentPosition()).getCounterValue()+"\n"+
                "Counter Flag: "+counterActivityToolbarColor(counterDataArrayList.get(getCurrentPosition()).getCounterFlag())+"\n"+
                "Counter Last Edited: "+ editTimeInText;

        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "TickTrack Counter Data");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, shareText);
        startActivity(Intent.createChooser(intent, "Share using"));
    }

    private static String counterActivityToolbarColor(int flagColor){
        if(flagColor == 1){
            return "Cherry";
        } else if(flagColor == 2){
            return "Lime";
        } else if(flagColor == 3){
            return "Peach";
        } else if(flagColor == 4){
            return "Plum";
        } else if(flagColor == 5){
            return "Berry";
        }
        return "NA";
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

    private void changeButtonVisibility(){
        if(counterDataArrayList.get(getCurrentPosition()).isCounterSwipeMode()){
            tickTrackDatabase.storeCounterList(counterDataArrayList);
            plusButtonBig.setVisibility(View.VISIBLE);
            minusButtonBig.setVisibility(View.VISIBLE);
            plusLightButton.setVisibility(View.GONE);
            minusLightButton.setVisibility(View.GONE);
            plusDarkButton.setVisibility(View.GONE);
            minusDarkButton.setVisibility(View.GONE);

        } else {
            tickTrackDatabase.storeCounterList(counterDataArrayList);
            plusButtonBig.setVisibility(View.GONE);
            minusButtonBig.setVisibility(View.GONE);
            if(tickTrackDatabase.getThemeMode()==1){
                plusLightButton.setVisibility(View.VISIBLE);
                minusLightButton.setVisibility(View.VISIBLE);
                plusDarkButton.setVisibility(View.GONE);
                minusDarkButton.setVisibility(View.GONE);
            } else {
                plusLightButton.setVisibility(View.GONE);
                minusLightButton.setVisibility(View.GONE);
                plusDarkButton.setVisibility(View.VISIBLE);
                minusDarkButton.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setOnClickListeners(){
        plusLightButton.setOnActiveListener(() -> {
            currentCount+=1;
            counterDataArrayList.get(getCurrentPosition()).setCounterValue(currentCount);
            counterDataArrayList.get(getCurrentPosition()).setCounterTimestamp(System.currentTimeMillis());
            CounterText.setText(""+counterDataArrayList.get(getCurrentPosition()).getCounterValue());
            tickTrackDatabase.storeCounterList(counterDataArrayList);
            milestoneItIs();
            refreshNotificationStatus();
        });

        minusLightButton.setOnActiveListener(() -> {
            if(currentCount>=1){
                currentCount-=1;
                counterDataArrayList.get(getCurrentPosition()).setCounterValue(currentCount);
                counterDataArrayList.get(getCurrentPosition()).setCounterTimestamp(System.currentTimeMillis());
                CounterText.setText(""+counterDataArrayList.get(getCurrentPosition()).getCounterValue());
                tickTrackDatabase.storeCounterList(counterDataArrayList);
                milestoneItIs();
                refreshNotificationStatus();
            }
        });
        plusDarkButton.setOnActiveListener(() -> {
            currentCount+=1;
            counterDataArrayList.get(getCurrentPosition()).setCounterValue(currentCount);
            counterDataArrayList.get(getCurrentPosition()).setCounterTimestamp(System.currentTimeMillis());
            CounterText.setText(""+counterDataArrayList.get(getCurrentPosition()).getCounterValue());
            tickTrackDatabase.storeCounterList(counterDataArrayList);
            milestoneItIs();
            refreshNotificationStatus();
        });

        minusDarkButton.setOnActiveListener(() -> {
            if(currentCount>=1){
                currentCount-=1;
                counterDataArrayList.get(getCurrentPosition()).setCounterValue(currentCount);
                counterDataArrayList.get(getCurrentPosition()).setCounterTimestamp(System.currentTimeMillis());
                CounterText.setText(""+counterDataArrayList.get(getCurrentPosition()).getCounterValue());
                tickTrackDatabase.storeCounterList(counterDataArrayList);
                milestoneItIs();
                refreshNotificationStatus();
            }
        });
        plusButtonBig.setOnClickListener(view -> {
            currentCount+=1;
            counterDataArrayList.get(getCurrentPosition()).setCounterValue(currentCount);
            counterDataArrayList.get(getCurrentPosition()).setCounterTimestamp(System.currentTimeMillis());
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

        if(tickTrackDatabase.isHapticEnabled()){
            vibrator.vibrate(50);
        }

        if(counterDataArrayList.get(getCurrentPosition()).isCounterPersistentNotification()){
            Intent intent = new Intent(this, CounterNotificationService.class);
            intent.setAction(CounterNotificationService.ACTION_REFRESH_SERVICE);
            startService(intent);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Intent intent = new Intent(this, CounterWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), CounterWidget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        sendBroadcast(intent);
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