package com.nerdcoredevelopment.inappbillingdemo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatImageView;

/* TODO -> !! Caution !! - Always make sure to use a testing id for debugging and
           testing purposes. Also ensure to use the Actual id just before creating a
           release AAB or APK as then we want to have an actual Admob App Id
*/
public class IntroActivity extends AppCompatActivity {

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_intro);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        AppCompatImageView developerLogoImageView = findViewById(R.id.developer_logo_image_view);
        developerLogoImageView.animate().alpha(1).setDuration(2000).start();

        new CountDownTimer(3000, 10000) {
            @Override
            public void onTick(long millisUntilFinished) {}
            @Override
            public void onFinish() {
                startMainActivity();
            }
        }.start();
    }

    private void startMainActivity() {
        Intent intent = new Intent(IntroActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}