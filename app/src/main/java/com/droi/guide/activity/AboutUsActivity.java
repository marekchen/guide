package com.droi.guide.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.droi.guide.R;
import com.droi.sdk.analytics.DroiAnalytics;

public class AboutUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
    }

    @Override
    protected void onResume() {
        super.onResume();
        DroiAnalytics.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        DroiAnalytics.onPause(this);
    }
}
