package com.theflopguyproductions.ticktrack.about;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
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
    private ConstraintLayout githubButton, twitterButton, instaButton;

    private static final String INSTAGRAM_LINK = "https://www.instagram.com/theflopguy/";
    private static final String GITHUB_LINK = "https://github.com/rahulnsanand/TickTrack";
    private static final String TWITTER_LINK = "https://twitter.com/TheFlopGuy";


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
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            versionText.setText("V "+version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            versionText.setText("V 2.2.0.6");
        }
        backButton = findViewById(R.id.aboutActivityBackButton);
        githubButton = findViewById(R.id.aboutActivityGithubButton);
        twitterButton = findViewById(R.id.aboutActivityTwitterButton);
        instaButton = findViewById(R.id.aboutActivityInstagramButton);
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

        instaButton.setOnClickListener(view -> openInstagram());
        twitterButton.setOnClickListener(view -> openTwitter());
        githubButton.setOnClickListener(view -> openGithub());
    }

    private void openGithub(){
        Intent intent;
        try {
            this.getPackageManager().getPackageInfo("com.github.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(GITHUB_LINK));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            openBrowser(3);
        }
    }

    private void openTwitter(){
        Intent intent;
        try {
            this.getPackageManager().getPackageInfo("com.twitter.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(TWITTER_LINK));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            openBrowser(2);
        }
    }

    private void openInstagram(){
        try {
            Intent i= new Intent(Intent.ACTION_VIEW, Uri.parse(INSTAGRAM_LINK));
            i.setPackage("com.instagram.android");
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        } catch (Exception e) {
            openBrowser(1);
        }
    }

    private void openBrowser (int i) {
        Uri uriUrl;
        if(i==1){
            uriUrl = Uri.parse(INSTAGRAM_LINK);
        } else if (i==2){
            uriUrl = Uri.parse(TWITTER_LINK);
        } else {
            uriUrl = Uri.parse(GITHUB_LINK);
        }

//        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
//        startActivity(launchBrowser);

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(uriUrl);
        startActivity(intent);
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