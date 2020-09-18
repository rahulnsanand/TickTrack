package com.theflopguyproductions.ticktrack.about;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
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
    private Button contributeButton;
    private TickTrackDatabase tickTrackDatabase;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        rootLayout = findViewById(R.id.aboutActivityRootLayout);
        toolbarLayout = findViewById(R.id.aboutActivityToolbar);
        storyText = findViewById(R.id.aboutActivityTickTrackStory);
        versionText = findViewById(R.id.aboutActivityAppVersionText);
        contributeButton = findViewById(R.id.aboutActivityContributeButton);
        backButton = findViewById(R.id.aboutActivityBackButton);
        tickTrackDatabase = new TickTrackDatabase(this);

        TickTrackThemeSetter.aboutActivityTheme(tickTrackDatabase, this, rootLayout, toolbarLayout, storyText, versionText, contributeButton);

        backButton.setOnClickListener(view -> onBackPressed());
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, SoYouADeveloperHuh.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}