package com.droi.guide.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.droi.guide.R;
import com.droi.guide.fragment.LoginFragment;
import com.droi.sdk.analytics.DroiAnalytics;

public class LoginActivity extends FragmentActivity {
    static FragmentManager fm;
    static Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_login);
        fm = getSupportFragmentManager();
        displayLoginFragment();
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

    private static void displayLoginFragment() {
        FragmentTransaction transaction = fm.beginTransaction();
        Fragment loginFragment = new LoginFragment();
        transaction.replace(R.id.droi_login_container, loginFragment);
        transaction.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

