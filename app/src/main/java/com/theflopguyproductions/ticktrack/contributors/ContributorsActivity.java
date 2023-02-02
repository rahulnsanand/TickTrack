package com.theflopguyproductions.ticktrack.contributors;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.SoYouADeveloperHuh;
import com.theflopguyproductions.ticktrack.ui.utils.TickTrackCircleImageView;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.utils.helpers.TickTrackThemeSetter;


/**
 * This Activity showcases all the contributors github handle, along with their profile picture.
 * To get yourself up here, make a valuable contribution. May it be addition of a new feature
 * OR fixing/improvising on an existing feature.
 *
 * CAUTION: Your data will only be update on the next release, no cloud data is downloaded onto
 * the users device to prevent excess data reads.
 *
 * visit https://github.com/theflopguy/TickTrack for further details
 */

public class ContributorsActivity extends AppCompatActivity {

    private TextView descriptionText;
    private ConstraintLayout rootLayout, learnMoreLayout,contributorsActivityToolbar;
    private TickTrackDatabase tickTrackDatabase;
    private ImageButton backButtonContributorsActivity;
    private static final String GITHUB_LINK = "https://github.com/theflopguy/TickTrack";

    @Override
    protected void onResume() {
        super.onResume();
        TickTrackThemeSetter.setupContributorsTheme(tickTrackDatabase, this, rootLayout, descriptionText,contributorsActivityToolbar,
                theflopguyLayout);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, SoYouADeveloperHuh.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }
    private void initVariables() {
        tickTrackDatabase = new TickTrackDatabase(this);
        backButtonContributorsActivity = findViewById(R.id.backButtonContributorsActivity);
        descriptionText = findViewById(R.id.contributorsActivityDescriptionText);
        rootLayout = findViewById(R.id.contributorsRootLayout);
        learnMoreLayout = findViewById(R.id.contributorsActivityMainButton);
        contributorsActivityToolbar = findViewById(R.id.contributorsActivityToolbar);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contributors);
        initVariables();
        learnMoreLayout.setOnClickListener(view -> openTickTrackGithub(GITHUB_LINK));
        backButtonContributorsActivity.setOnClickListener(view -> onBackPressed());
        setupContributors();
    }
    private void openTickTrackGithub(String url) {
        Intent intent;
        try {
            this.getPackageManager().getPackageInfo("com.github.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivityForResult(intent, 10);
        } catch (Exception e) {
            openBrowser(url);
        }
    }
    private void openBrowser (String url) {
        Uri uriUrl = Uri.parse(url);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(uriUrl);
        startActivityForResult(intent, 10);
    }

    private ConstraintLayout theflopguyLayout;
    private void setupContributors() {
        theflopguySetup();
    }

    private TickTrackCircleImageView theflopguyDrawable;
    private void theflopguySetup() {
        theflopguyDrawable =  findViewById(R.id.imageView9);
        theflopguyLayout = findViewById(R.id.contributorsActivityTheFlopGuyLayout);
        String theflopguyURL = "https://github.com/theflopguy";
        theflopguyLayout.setOnClickListener(view -> openTickTrackGithub(theflopguyURL));
        Glide.with(this).load("https://avatars.githubusercontent.com/u/41159815?v=4").into(theflopguyDrawable);
    }
}
