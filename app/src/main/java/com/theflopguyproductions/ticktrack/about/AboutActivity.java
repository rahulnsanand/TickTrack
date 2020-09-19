package com.theflopguyproductions.ticktrack.about;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.SoYouADeveloperHuh;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.helpers.TickTrackThemeSetter;

public class AboutActivity extends AppCompatActivity {

    private ImageButton backButton;
    private ConstraintLayout rootLayout, toolbarLayout;
    private TextView storyText, versionText;
    private TickTrackDatabase tickTrackDatabase;


    @Override
    protected void onResume() {
        super.onResume();
        TickTrackThemeSetter.aboutActivityTheme(tickTrackDatabase, this, rootLayout, toolbarLayout, storyText, versionText);

    }

    private void initVariables(){
        rootLayout = findViewById(R.id.aboutActivityRootLayout);
        toolbarLayout = findViewById(R.id.aboutActivityToolbar);
        storyText = findViewById(R.id.aboutActivityTickTrackStory);
        versionText = findViewById(R.id.aboutActivityAppVersionText);
        backButton = findViewById(R.id.aboutActivityBackButton);
        tickTrackDatabase = new TickTrackDatabase(this);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        initVariables();
        backButton.setOnClickListener(view -> onBackPressed());

        if(tickTrackDatabase.getThemeMode()==1){
            getWindow().setNavigationBarColor(getResources().getColor(R.color.HoloLightGray) );
        } else {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.HoloBlack) );
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, SoYouADeveloperHuh.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }
}