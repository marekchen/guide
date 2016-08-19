package com.droi.guide.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.droi.guide.R;
import com.droi.guide.fragment.FoundFragment;
import com.droi.guide.fragment.MainFragment;

public class MainActivity extends AppCompatActivity {

    public static final String MAIN_TAB_INDEX = "index";
    private FragmentTabHost mTabHost;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
/*        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/
        initTab();
    }

    private void initTab() {
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        mTabHost.getTabWidget().setDividerDrawable(null);
        mTabHost.addTab(mTabHost.newTabSpec("mainTab").setIndicator(getTabView(R.drawable.btn_home, R.string.activity_main_tab_home)),
                MainFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("appsTab").setIndicator(getTabView(R.drawable.btn_home, R.string.activity_main_tab_found)),
                FoundFragment.class, null);
    /*    mTabHost.addTab(mTabHost.newTabSpec("gamesTab").setIndicator(getTabView(R.drawable.btn_shop, R.string.activity_main_tab_game)),
                GameFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("mineTab").setIndicator(getTabView(R.drawable.btn_mine, R.string.activity_main_tab_mine)),
                MineFragment.class, null);*/
        mTabHost.setCurrentTab(getIntent().getIntExtra(MAIN_TAB_INDEX, 0));
    }

    private View getTabView(int imgId, int txtId) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.main_bottom_tab, null);
        ((ImageView) view.findViewById(R.id.main_bottom_tab_img)).setImageResource(imgId);
        ((TextView) view.findViewById(R.id.main_bottom_tab_label)).setText(txtId);
        return view;
    }
}
