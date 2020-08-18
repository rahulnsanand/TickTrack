package com.theflopguyproductions.ticktrack.startup;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.startup.fragments.IntroFragment;

import java.util.Objects;

public class StartUpActivity extends AppCompatActivity implements IntroFragment.OnGetStartedClickListener {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_start_up);
        Objects.requireNonNull(getSupportActionBar()).hide();



    }

    @Override
    public void onGetStartedClick() {

    }
}