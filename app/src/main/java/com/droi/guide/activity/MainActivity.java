package com.droi.guide.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.droi.guide.R;
import com.droi.guide.fragment.FoundFragment;
import com.droi.guide.fragment.MainFragment;
import com.droi.guide.fragment.MineFragment;
import com.droi.guide.fragment.SearchFragment;

public class MainActivity extends AppCompatActivity {

    public static final String MAIN_TAB_INDEX = "index";
    private FragmentTabHost mTabHost;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        initTab();
    }

    private void initTab() {
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        mTabHost.getTabWidget().setDividerDrawable(null);
        mTabHost.addTab(mTabHost.newTabSpec("mainTab").setIndicator(getTabView(R.drawable.btn_home, R.string.activity_main_tab_home)),
                MainFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("foundTab").setIndicator(getTabView(R.drawable.btn_find, R.string.activity_main_tab_found)),
                FoundFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("searchTab").setIndicator(getTabView(R.drawable.btn_find, R.string.activity_main_tab_search)),
                SearchFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("mineTab").setIndicator(getTabView(R.drawable.btn_mine, R.string.activity_main_tab_mine)),
                MineFragment.class, null);
        mTabHost.setCurrentTab(getIntent().getIntExtra(MAIN_TAB_INDEX, 0));
    }

    private View getTabView(int imgId, int txtId) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.view_main_bottom_tab, null);
        view.findViewById(R.id.main_bottom_tab_img).setBackgroundResource(imgId);
        ((TextView) view.findViewById(R.id.main_bottom_tab_label)).setText(txtId);
        return view;
    }
}
