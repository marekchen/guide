package com.droi.guide.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.droi.guide.R;
import com.droi.guide.activity.ArticleActivity;
import com.droi.guide.activity.DetailsActivity;
import com.droi.guide.activity.OfficialGuideActivity;
import com.droi.guide.adapter.BannerAdapter;
import com.droi.guide.model.Banner;
import com.droi.guide.views.Indicator;
import com.droi.sdk.DroiError;
import com.droi.sdk.analytics.DroiAnalytics;
import com.droi.sdk.core.DroiQuery;
import com.droi.sdk.core.DroiQueryCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainFragment extends Fragment {

    ArrayList<Banner> mBanners;
    BannerAdapter mBannerAdapter;

    @BindView(R.id.banner_layout)
    RelativeLayout bannerLayout;
    @BindView(R.id.head_indicator_layout)
    Indicator indicatorLayout;
    @BindView(R.id.head_view_pager)
    ViewPager viewPager;

    public MainFragment() {
    }

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);
        initBanner(view);
        return view;
    }

    /**
     * 初始化Banner信息展示，Banner主要用于展示推广活动信息
     *
     * @param view
     */
    private void initBanner(final View view) {
        if (mBanners == null) {
            mBanners = new ArrayList<Banner>();
        }
        if (mBanners.isEmpty()) {
            fetchBannerData(bannerLayout, indicatorLayout);
        }
        indicatorLayout.setCount(mBanners.size());
        indicatorLayout.select(0);
        viewPager = (ViewPager) view.findViewById(R.id.head_view_pager);
        mBannerAdapter = new BannerAdapter(getActivity(), mBanners);
        viewPager.setAdapter(mBannerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                indicatorLayout.select(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        final GestureDetector tapGestureDetector = new GestureDetector(getActivity(), new TapGestureListener());
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                tapGestureDetector.onTouchEvent(event);
                return false;
            }
        });
    }

    /**
     * 查询Banner信息
     *
     * @param bannerLayout
     * @param indicatorLayout
     */
    private void fetchBannerData(final RelativeLayout bannerLayout, final Indicator indicatorLayout) {
        DroiQuery query = DroiQuery.Builder.newBuilder().limit(4).query(Banner.class).build();
        query.runQueryInBackground(new DroiQueryCallback<Banner>() {
            @Override
            public void result(List<Banner> list, DroiError droiError) {
                //mAppTypeAdapter.notifyDataSetChanged();
                if (droiError.isOk()) {
                    if (list.size() > 0) {
                        mBanners.clear();
                        mBanners.addAll(list);
                        mBannerAdapter.notifyDataSetChanged();
                        bannerLayout.setVisibility(View.VISIBLE);
                        indicatorLayout.setCount(mBanners.size());
                        indicatorLayout.select(0);
                    }
                } else {
                    //做请求失败处理
                }
            }
        });
    }

    @OnClick(R.id.social)
    public void onSocialPressed() {
        Intent intent = new Intent(this.getActivity(), ArticleActivity.class);
        intent.putExtra(ArticleFragment.CATEGORY, "social");
        startActivity(intent);
    }

    @OnClick(R.id.education)
    public void onEduPressed() {
        Intent intent = new Intent(this.getActivity(), ArticleActivity.class);
        intent.putExtra(ArticleFragment.CATEGORY, "education");
        startActivity(intent);
    }

    @OnClick(R.id.credential)
    public void onCredentialPressed() {
        Intent intent = new Intent(this.getActivity(), ArticleActivity.class);
        intent.putExtra(ArticleFragment.CATEGORY, "credential");
        startActivity(intent);
    }

    @OnClick(R.id.wedding)
    public void onWeddingPressed() {
        Intent intent = new Intent(this.getActivity(), ArticleActivity.class);
        intent.putExtra(ArticleFragment.CATEGORY, "wedding");
        startActivity(intent);
    }

    @OnClick(R.id.transport)
    public void onTrasportPressed() {
        Intent intent = new Intent(this.getActivity(), ArticleActivity.class);
        intent.putExtra(ArticleFragment.CATEGORY, "transport");
        startActivity(intent);
    }

    @OnClick(R.id.other)
    public void onOtherPressed() {
        Intent intent = new Intent(this.getActivity(), ArticleActivity.class);
        intent.putExtra(ArticleFragment.CATEGORY, "other");
        startActivity(intent);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        DroiAnalytics.onFragmentStart(getActivity(), "MainFragment");

    }

    @Override
    public void onPause() {
        super.onPause();
        DroiAnalytics.onFragmentEnd(getActivity(), "MainFragment");
    }

    /**
     * 设置Banner点击事件
     */
    class TapGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Banner banner = mBanners.get(viewPager.getCurrentItem());
            Log.i("test","viewPager.getCurrentItem()"+viewPager.getCurrentItem());
            Intent intent;
            if (banner != null && banner.ref != null) {
                if (banner.ref.type == 1) {
                    intent = new Intent(getActivity(), DetailsActivity.class);
                    intent.putExtra(DetailsActivity.ANSWER, banner.ref);
                } else {
                    intent = new Intent(getActivity(), OfficialGuideActivity.class);
                    intent.putExtra(OfficialGuideActivity.OFFICIAL, banner.ref);
                }
                startActivity(intent);
            }
            return true;
        }
    }
}
