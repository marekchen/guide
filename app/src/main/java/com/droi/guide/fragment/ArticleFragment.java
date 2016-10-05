package com.droi.guide.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.droi.guide.R;
import com.droi.guide.activity.OfficialGuideActivity;
import com.droi.guide.adapter.ArticleAdapter;
import com.droi.guide.adapter.BaseRecycleViewAdapter;
import com.droi.guide.model.Article;
import com.droi.sdk.DroiError;
import com.droi.sdk.analytics.DroiAnalytics;
import com.droi.sdk.core.DroiCondition;
import com.droi.sdk.core.DroiQuery;
import com.droi.sdk.core.DroiQueryCallback;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArticleFragment extends Fragment {

    public static final String LOCATION = "LOCATION";
    public static final String CATEGORY = "CATEGORY";
    public static final String KEYWORD = "KEYWORD";
    public static final String FOUND_TYPE = "FOUND_TYPE";

    String location;
    String category;
    String keyword;
    int type = 0;
    private int offset = 0;
    private ArticleAdapter mArticleAdapter = null;
    boolean isRefreshing = false;

    @BindView(R.id.fragment_lv)
    RecyclerView mRecyclerView;
    @BindView(R.id.fragment_swipe)
    SwipeRefreshLayout mSwipeRefreshLayout;

    public ArticleFragment() {
    }

    public static ArticleFragment newInstance(String location, String category) {
        ArticleFragment fragment = new ArticleFragment();
        Bundle args = new Bundle();
        args.putString(LOCATION, location);
        args.putString(CATEGORY, category);
        fragment.setArguments(args);
        return fragment;
    }

    public static ArticleFragment newInstance(String keyword) {
        ArticleFragment fragment = new ArticleFragment();
        Bundle args = new Bundle();
        args.putString(KEYWORD, keyword);
        fragment.setArguments(args);
        return fragment;
    }

    public static ArticleFragment newInstance(int type) {
        ArticleFragment fragment = new ArticleFragment();
        Bundle args = new Bundle();
        args.putInt(FOUND_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            location = getArguments().getString(LOCATION);
            category = getArguments().getString(CATEGORY);
            keyword = getArguments().getString(KEYWORD);
            type = getArguments().getInt(FOUND_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_common, container, false);
        ButterKnife.bind(this, view);

        final LinearLayoutManager mRecycleViewLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(mRecycleViewLayoutManager);
        mArticleAdapter = new ArticleAdapter(this.getContext());
        mRecyclerView.setAdapter(mArticleAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                offset = 0;
                fetchArticle();
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && mRecycleViewLayoutManager.findLastVisibleItemPosition() + 1
                        == mArticleAdapter.getItemCount()) {
                    if (mArticleAdapter.getBasicItemCount() != 0
                            && mArticleAdapter.getBasicItemCount() >= 10) {
                        fetchArticle();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (type != 0) {
            offset=0;
        }
        fetchArticle();
        DroiAnalytics.onFragmentStart(getActivity(),"ArticleFragment");

    }

    @Override
    public void onPause() {
        super.onPause();
        DroiAnalytics.onFragmentEnd(getActivity(),"ArticleFragment");
    }

    private void fetchArticle() {
        if (offset == 0) {
            setRefreshing(true);
        }

        if (isRefreshing) {
            return;
        }
        isRefreshing = true;
        DroiQuery query;
        if (type != 0) {
            if (type == 1) {
                query = DroiQuery.Builder.newBuilder().limit(10).offset(offset).orderBy("commentNum", true).query(Article.class).build();
            } else {
                query = DroiQuery.Builder.newBuilder().limit(10).offset(offset).orderBy("favoriteNum", true).query(Article.class).build();
            }
        } else {
            DroiCondition cond;
            if (keyword == null || keyword.isEmpty()) {
                if (location == null || location.isEmpty()) {
                    cond = DroiCondition.cond("category", DroiCondition.Type.EQ, category);
                } else {
                    DroiCondition cond1 = DroiCondition.cond("location", DroiCondition.Type.EQ, location);
                    cond = cond1.and(DroiCondition.cond("category", DroiCondition.Type.EQ, category));
                }
            } else {
                DroiCondition cond1 = DroiCondition.cond("brief", DroiCondition.Type.CONTAINS, keyword);
                DroiCondition cond2 = DroiCondition.cond("title", DroiCondition.Type.CONTAINS, keyword);
                DroiCondition cond3 = DroiCondition.cond("body", DroiCondition.Type.CONTAINS, keyword);
                cond = cond1.or(cond2).or(cond3);
            }
            query = DroiQuery.Builder.newBuilder().limit(10).offset(offset).query(Article.class).where(cond).build();
        }
        query.runQueryInBackground(new DroiQueryCallback<Article>() {
            @Override
            public void result(List<Article> list, DroiError droiError) {
                if (droiError.isOk()) {
                    if (list.size() > 0) {
                        if (offset == 0) {
                            mArticleAdapter.clear();
                        }
                        mArticleAdapter.appendToList(list);
                        mArticleAdapter.notifyDataSetChanged();
                        offset = mArticleAdapter.getBasicItemCount();
                    }
                } else {
                    //做请求失败处理
                }
                setRefreshing(false);
                isRefreshing = false;
            }
        });
    }

    void setRefreshing(final boolean b) {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(b);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
