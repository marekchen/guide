package com.droi.guide.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.droi.guide.R;
import com.droi.guide.fragment.AnswerFragment;
import com.droi.guide.fragment.FollowPeopleFragment;
import com.droi.guide.fragment.QuestionFragment;
import com.droi.sdk.core.DroiUser;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chenpei on 16/9/20.
 */

public class MyFollowActivity extends FragmentActivity {

    @BindView(R.id.tabs)
    public TabLayout mTabLayout;
    @BindView(R.id.viewpager)
    public ViewPager mViewPager;
    @BindView(R.id.top_bar_title)
    TextView topBarTitle;
    @BindView(R.id.top_bar_back_btn)
    ImageButton topBarBack;

    private ArrayList<String> mTitleList = new ArrayList<>();//页卡标题集合

    ArrayList<Fragment> fragmentList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_favorite_follow);
        ButterKnife.bind(this);
        mTitleList.add("关注用户");
        mTitleList.add("关注问题");

        mTabLayout.setTabMode(TabLayout.MODE_FIXED);//设置tab模式，当前为系统默认模式
        for (int i = 0; i < mTitleList.size(); i++) {
            mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(i)));//添加tab选项卡
        }
        fragmentList = new ArrayList<>();
        Fragment btFragment1 = FollowPeopleFragment.newInstance(DroiUser.getCurrentUser().getObjectId());
        Fragment btFragment2 = QuestionFragment.newInstance(1,DroiUser.getCurrentUser().getObjectId());

        fragmentList.add(btFragment1);
        fragmentList.add(btFragment2);

        PagerAdapter mAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentList, mTitleList);
        mViewPager.setAdapter(mAdapter);//给ViewPager设置适配器
        mTabLayout.setupWithViewPager(mViewPager);//将TabLayout和ViewPager关联起来。
        mTabLayout.setTabsFromPagerAdapter(mAdapter);//给Tabs设置适配器
        Log.i("test", "oncreate-end");
        topBarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        topBarTitle.setText(R.string.fragment_mine_favorite);
    }

    class MyFragmentPagerAdapter extends FragmentPagerAdapter {
        ArrayList<Fragment> list;
        ArrayList<String> titles;

        public MyFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> list, ArrayList<String> titles) {
            super(fm);
            this.list = list;
            this.titles = titles;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Fragment getItem(int arg0) {
            return list.get(arg0);
        }
    }
}
