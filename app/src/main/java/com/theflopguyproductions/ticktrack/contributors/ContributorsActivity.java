package com.theflopguyproductions.ticktrack.contributors;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.helpers.TickTrackThemeSetter;

public class ContributorsActivity extends AppCompatActivity {



    private TickTrackDatabase tickTrackDatabase;

    @Override
    protected void onResume() {
        super.onResume();
        TickTrackThemeSetter.setupContributorsTheme(tickTrackDatabase, this);
    }

    private void initVariables() {
        tickTrackDatabase = new TickTrackDatabase(this);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contributors);
        initVariables();

    }

}
