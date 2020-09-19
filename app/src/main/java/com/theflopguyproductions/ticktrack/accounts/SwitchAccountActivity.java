package com.theflopguyproductions.ticktrack.accounts;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;

public class SwitchAccountActivity extends AppCompatActivity {

    private TickTrackDatabase tickTrackDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch_account);

        tickTrackDatabase = new TickTrackDatabase(this);

        if(tickTrackDatabase.getThemeMode()==1){
            getWindow().setNavigationBarColor(getResources().getColor(R.color.HoloLightGray) );
        } else {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.HoloBlack) );
        }
    }
}