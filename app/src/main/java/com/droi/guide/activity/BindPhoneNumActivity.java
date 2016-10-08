package com.droi.guide.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.droi.guide.R;
import com.droi.guide.fragment.BackHandledFragment;
import com.droi.guide.fragment.BindEmailFragment;
import com.droi.guide.fragment.BindConfirmPinFragment;
import com.droi.guide.fragment.BindPhoneNumFragment;
import com.droi.guide.interfaces.BackHandlerInterface;
import com.droi.guide.interfaces.OnFragmentInteractionListener;
import com.droi.sdk.analytics.DroiAnalytics;
//import com.droi.sdk.analytics.DroiAnalytics;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chenpei on 16/9/12.
 */
public class BindPhoneNumActivity extends FragmentActivity implements OnFragmentInteractionListener, BackHandlerInterface {
    static FragmentManager fm;
    static Context context;
    private BackHandledFragment mBackHandedFragment;

    @BindView(R.id.top_bar_title)
    TextView topBarTitle;
    @BindView(R.id.top_bar_back_btn)
    ImageButton backArrowButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_bind);
        ButterKnife.bind(this);
        topBarTitle.setText(getString(R.string.mobile_bind));
        backArrowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        fm = getSupportFragmentManager();
        displayBindPhoneNumFragment();
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

    private static void displayBindPhoneNumFragment() {
        FragmentTransaction transaction = fm.beginTransaction();
        Fragment bindNumFragment = BindPhoneNumFragment.newInstance();
        transaction.replace(R.id.bind_container, bindNumFragment);
        transaction.commit();
    }

    public static void displayConfirmPinFragment() {
        FragmentTransaction transaction = fm.beginTransaction();
        Fragment confirmPinFragment = BindConfirmPinFragment.newInstance();
        transaction.replace(R.id.bind_container, confirmPinFragment);
        transaction.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onFragmentInteraction(int action) {
        switch (action) {
            case 0:
                displayBindPhoneNumFragment();
                break;
            case 1:
                displayConfirmPinFragment();
                break;
            case 2:
                finish();
                break;
        }
    }

    @Override
    public void setSelectedFragment(BackHandledFragment selectedFragment) {
        this.mBackHandedFragment = selectedFragment;
    }

    @Override
    public void onBackPressed() {
        Log.i("TAG", "onBackPressed");
        if (mBackHandedFragment == null) {
            Log.i("TAG", "mBackHandedFragment == null");
        }
        if (!mBackHandedFragment.onBackPressed()) {
            Log.i("TAG", "!onBackPressed");
        }
        if (mBackHandedFragment == null || !mBackHandedFragment.onBackPressed()) {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                Log.i("TAG", "1");
                super.onBackPressed();
            } else {
                Log.i("TAG", "2");
                getSupportFragmentManager().popBackStack();
            }
        } else {
            displayBindPhoneNumFragment();
        }
    }
}
