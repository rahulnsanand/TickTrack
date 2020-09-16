package com.theflopguyproductions.ticktrack.widgets.clock;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.helpers.TickTrackThemeSetter;

import java.util.ArrayList;

public class ClockWidgetConfigActivity extends AppCompatActivity {

    private ConstraintLayout rootLayout;
    private TextView titleText, optionText;
    private ImageView unordinaryImage, oxygenyImage, minimalImage, simplisticImage, romanImage, funkyImage;
    private ImageView unordinaryImageCheck, oxygenyImageCheck, minimalImageCheck, simplisticImageCheck, romanImageCheck, funkyImageCheck;
    private ImageView phoneImage;
    private Button saveButton;
    private int clockTheme = 1;
    private boolean isNotNew = false;

    private TickTrackDatabase tickTrackDatabase;
    private static int clockWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    private void initVariables(){
        rootLayout = findViewById(R.id.clockWidgetConfigRootLayout);
        titleText = findViewById(R.id.clockWidgetConfigTitleText);
        optionText = findViewById(R.id.clockWidgetConfigOptionChosenTitle);
        unordinaryImage = findViewById(R.id.clockWidgetConfigUnordinaryImage);
        oxygenyImage = findViewById(R.id.clockWidgetConfigOxygenyImage);
        minimalImage = findViewById(R.id.clockWidgetConfigTrulyMinimalImage);
        simplisticImage = findViewById(R.id.clockWidgetConfigSimplisticImage);
        romanImage = findViewById(R.id.clockWidgetConfigRomanImage);
        funkyImage = findViewById(R.id.clockWidgetConfigFunkyImage);
        unordinaryImageCheck = findViewById(R.id.clockWidgetConfigUnordinaryImageCheck);
        oxygenyImageCheck = findViewById(R.id.clockWidgetConfigOxygenyImageCheck);
        minimalImageCheck = findViewById(R.id.clockWidgetConfigTrulyMinimalImageCheck);
        simplisticImageCheck = findViewById(R.id.clockWidgetConfigSimplisticImageCheck);
        romanImageCheck = findViewById(R.id.clockWidgetConfigRomanImageCheck);
        funkyImageCheck = findViewById(R.id.clockWidgetConfigFunkyImageCheck);
        phoneImage = findViewById(R.id.clockWidgetConfigOptionChosenImage);
        saveButton = findViewById(R.id.clockWidgetConfigSaveButton);
        tickTrackDatabase = new TickTrackDatabase(this);
    }
    private int getClockTheme(int clockWidgetId){
        clockDataArrayList = tickTrackDatabase.retrieveClockWidgetList();
        for(int i=0; i<clockDataArrayList.size(); i++){
            if(clockDataArrayList.get(i).getClockWidgetId()==clockWidgetId){
                return clockDataArrayList.get(i).getClockTheme();
            }
        }
        return -1;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock_widget_config);
        initVariables();

        Intent clockIntent = getIntent();
        Bundle extras = clockIntent.getExtras();
        if(extras!=null){
            System.out.println("THIS OPENED");
            clockWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, clockWidgetId);
        setResult(RESULT_CANCELED, resultValue);

        int retrieveTheme = getClockTheme(clockWidgetId);
        System.out.println("THIS OPENED");
        if(clockWidgetId!=AppWidgetManager.INVALID_APPWIDGET_ID && retrieveTheme!=-1){
            isNotNew = true;
            presetValues(clockWidgetId, retrieveTheme);
        }
        System.out.println(clockWidgetId+"++++++++++++++"+retrieveTheme);

        if(clockWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID){
            finish();
        }

        setupInitValues();
        setupClickListeners();

        TickTrackThemeSetter.clockWidgetSetupTheme(rootLayout, optionText, tickTrackDatabase, this);

        saveButton.setOnClickListener(view -> confirmSelection());
    }

    private void setupClickListeners() {
        unordinaryImage.setOnClickListener(view -> {
            clockTheme=1;
            setupInitValues();
        });
        oxygenyImage.setOnClickListener(view -> {
            clockTheme=2;
            setupInitValues();
        });
        minimalImage.setOnClickListener(view -> {
            clockTheme=3;
            setupInitValues();
        });
        simplisticImage.setOnClickListener(view -> {
            clockTheme=4;
            setupInitValues();
        });
        romanImage.setOnClickListener(view -> {
            clockTheme=5;
            setupInitValues();
        });
        funkyImage.setOnClickListener(view -> {
            clockTheme=6;
            setupInitValues();
        });
    }

    private void setupInitValues() {
        if(clockTheme==1){
            setupTheme(1);
        } else if (clockTheme==2){
            setupTheme(2);
        } else if (clockTheme==3){
            setupTheme(3);
        } else if (clockTheme==4){
            setupTheme(4);
        } else if (clockTheme==5){
            setupTheme(5);
        } else if (clockTheme==6){
            setupTheme(6);
        } else {
            setupTheme(1);
        }
    }

    private void setupTheme(int i) {
        removeAllCheck();
        if(i==1){
            optionText.setText("Unordinary");
            phoneImage.setImageResource(R.drawable.ic_unordinaryphone);
            unordinaryImageCheck.setVisibility(View.VISIBLE);
        } else if (i==2){
            optionText.setText("Oxygeny");
            phoneImage.setImageResource(R.drawable.ic_oxygenyphone);
            oxygenyImageCheck.setVisibility(View.VISIBLE);
        } else if (i==3){
            optionText.setText("Truly Minimal");
            phoneImage.setImageResource(R.drawable.ic_trulyminimalphone);
            minimalImageCheck.setVisibility(View.VISIBLE);
        } else if (i==4){
            optionText.setText("Simplistic");
            phoneImage.setImageResource(R.drawable.ic_simplisticphone);
            simplisticImageCheck.setVisibility(View.VISIBLE);
        } else if (i==5){
            optionText.setText("Roman");
            phoneImage.setImageResource(R.drawable.ic_romanphone);
            romanImageCheck.setVisibility(View.VISIBLE);
        } else if (i==6){
            optionText.setText("Funky");
            phoneImage.setImageResource(R.drawable.ic_funkyphone);
            funkyImageCheck.setVisibility(View.VISIBLE);
        } else {
            optionText.setText("Unordinary");
            phoneImage.setImageResource(R.drawable.ic_unordinaryphone);
            unordinaryImageCheck.setVisibility(View.VISIBLE);
        }
    }

    private void removeAllCheck() {
        unordinaryImageCheck.setVisibility(View.INVISIBLE);
        oxygenyImageCheck.setVisibility(View.INVISIBLE);
        minimalImageCheck.setVisibility(View.INVISIBLE);
        simplisticImageCheck.setVisibility(View.INVISIBLE);
        romanImageCheck.setVisibility(View.INVISIBLE);
        funkyImageCheck.setVisibility(View.INVISIBLE);
    }

    private void presetValues(int clockId, int retrieveTheme) {
        clockWidgetId = clockId;
        clockTheme = retrieveTheme;
    }

    private void confirmSelection(){

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);

        Intent intent = new Intent(this, ClockWidgetConfigActivity.class);
        intent.putExtra("clockId", clockWidgetId);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, clockWidgetId, intent, 0);

        addClockData();

        RemoteViews views = setupWidgetTheme();
        views.setOnClickPendingIntent(R.id.clockWidgetRootLayout, pendingIntent);

        appWidgetManager.updateAppWidget(clockWidgetId, views);

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, clockWidgetId);
        setResult(RESULT_OK, resultValue);
        updateWidget();

        if (!isNotNew) {
            finish();
        } else {
            onClose();
        }
    }

    public void onClose(){
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
    private RemoteViews setupWidgetTheme() {
        if(clockTheme==1){
            return new RemoteViews(getPackageName(), R.layout.tick_track_clock_widget1);
        } else if (clockTheme==2){
            return new RemoteViews(getPackageName(), R.layout.tick_track_clock_widget2);
        } else if (clockTheme==3){
            return new RemoteViews(getPackageName(), R.layout.tick_track_clock_widget3);
        } else if (clockTheme==4){
            return new RemoteViews(getPackageName(), R.layout.tick_track_clock_widget4);
        } else if (clockTheme==5){
            return new RemoteViews(getPackageName(), R.layout.tick_track_clock_widget5);
        } else if (clockTheme==6){
            return new RemoteViews(getPackageName(), R.layout.tick_track_clock_widget6);
        } else {
            return new RemoteViews(getPackageName(), R.layout.tick_track_clock_widget1);
        }
    }

    private void updateWidget(){
        Intent intent = new Intent(this, TickTrackClockWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance(this).getAppWidgetIds(new ComponentName(this, TickTrackClockWidget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        sendBroadcast(intent);
    }
    private ArrayList<ClockData> clockDataArrayList;
    private void addClockData() {
        clockDataArrayList = tickTrackDatabase.retrieveClockWidgetList();
        if(isNotNew){
            for(int i=0; i<clockDataArrayList.size(); i++){
                if(clockDataArrayList.get(i).getClockWidgetId()==clockWidgetId){
                    clockDataArrayList.get(i).setClockTheme(clockTheme);
                }
            }

        } else {
            ClockData clockData = new ClockData();
            clockData.setClockTheme(clockTheme);
            clockData.setClockWidgetId(clockWidgetId);
            clockDataArrayList.add(clockData);
        }
        tickTrackDatabase.storeClockWidgetList(clockDataArrayList);
    }
}