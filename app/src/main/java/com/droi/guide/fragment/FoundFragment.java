package com.droi.guide.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.droi.guide.R;
import com.droi.guide.model.Article;
import com.droi.sdk.analytics.DroiAnalytics;

import java.util.ArrayList;
import java.util.List;

public class FoundFragment extends Fragment {
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private ArrayList<String> mTitleList = new ArrayList<>();//页卡标题集合
    private View mContentView;
    ArrayList<Fragment> fragmentList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        /**
         * 防止Fragment多次切换时调用onCreateView重新加载View
         */
        if (null == mContentView) {
            mContentView = inflater.inflate(R.layout.fragment_found, container, false);
            mViewPager = (ViewPager) mContentView.findViewById(R.id.viewpager);
            mTabLayout = (TabLayout) mContentView.findViewById(R.id.tabs);

            mTitleList.add("推荐");
            mTitleList.add("热门");
            mTitleList.add("收藏");

            mTabLayout.setTabMode(TabLayout.MODE_FIXED);//设置tab模式，当前为系统默认模式
            for (int i = 0; i < mTitleList.size(); i++) {
                mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(i)));//添加tab选项卡
            }
            fragmentList = new ArrayList<>();
            Fragment btFragment1 = ArticleFragment.newInstance(1);//recommend
            Fragment btFragment2 = QuestionFragment.newInstance();
            Fragment btFragment3 = ArticleFragment.newInstance(2);//favorite
            fragmentList.add(btFragment1);
            fragmentList.add(btFragment2);
            fragmentList.add(btFragment3);
            PagerAdapter mAdapter = new MyFragmentPagerAdapter(this.getChildFragmentManager(), fragmentList, mTitleList);
            mViewPager.setAdapter(mAdapter);//给ViewPager设置适配器
            mTabLayout.setupWithViewPager(mViewPager);//将TabLayout和ViewPager关联起来。
            mTabLayout.setTabsFromPagerAdapter(mAdapter);//给Tabs设置适配器
        } else {
            ViewGroup parent = (ViewGroup) mContentView.getParent();
            if (parent != null) {
                parent.removeView(mContentView);
            }
        }
        return mContentView;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        DroiAnalytics.onFragmentStart(getActivity(), "FoundFragment");

    }

    @Override
    public void onPause() {
        super.onPause();
        DroiAnalytics.onFragmentEnd(getActivity(), "FoundFragment");
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