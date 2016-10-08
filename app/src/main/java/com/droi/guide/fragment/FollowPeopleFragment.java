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
import android.widget.Toast;

import com.droi.guide.R;
import com.droi.guide.activity.DetailsActivity;
import com.droi.guide.adapter.PeopleAdapter;
import com.droi.guide.model.Article;
import com.droi.guide.model.FollowPeopleRelation;
import com.droi.guide.adapter.BaseRecycleViewAdapter;
import com.droi.sdk.DroiError;
import com.droi.sdk.analytics.DroiAnalytics;
import com.droi.sdk.core.DroiCondition;
import com.droi.sdk.core.DroiQuery;
import com.droi.sdk.core.DroiQueryCallback;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FollowPeopleFragment extends Fragment {

    public static final String USER = "USER";

    private String userId;

    private int offset = 0;
    private PeopleAdapter mPeopleAdapter = null;
    boolean isRefreshing = false;

    @BindView(R.id.fragment_lv)
    RecyclerView mRecyclerView;
    @BindView(R.id.fragment_swipe)
    SwipeRefreshLayout mSwipeRefreshLayout;

    public FollowPeopleFragment() {
    }

    public static FollowPeopleFragment newInstance(String userId) {
        FollowPeopleFragment fragment = new FollowPeopleFragment();
        Bundle args = new Bundle();
        args.putString(USER, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getString(USER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_common, container, false);
        ButterKnife.bind(this, view);

        final LinearLayoutManager mRecycleViewLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(mRecycleViewLayoutManager);
        mPeopleAdapter = new PeopleAdapter(this.getContext());
        mRecyclerView.setAdapter(mPeopleAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                offset = 0;
                fetchRelation();
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && mRecycleViewLayoutManager.findLastVisibleItemPosition() + 1
                        == mPeopleAdapter.getItemCount()) {
                    if (mPeopleAdapter.getBasicItemCount() != 0
                            && mPeopleAdapter.getBasicItemCount() >= 10) {
                        fetchRelation();
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
        fetchRelation();
        DroiAnalytics.onFragmentStart(getActivity(), "FollowPeopleFragment");

    }

    @Override
    public void onPause() {
        super.onPause();
        DroiAnalytics.onFragmentEnd(getActivity(), "FollowPeopleFragment");
    }

    private void fetchRelation() {
        if (offset == 0) {
            setRefreshing(true);
        }

        if (isRefreshing) {
            return;
        }
        isRefreshing = true;

        DroiCondition cond = DroiCondition.cond("followerId", DroiCondition.Type.EQ, userId);
        DroiQuery query = DroiQuery.Builder.newBuilder().limit(10).offset(offset).query(FollowPeopleRelation.class).where(cond).build();
        query.runQueryInBackground(new DroiQueryCallback<FollowPeopleRelation>() {
            @Override
            public void result(List<FollowPeopleRelation> list, DroiError droiError) {
                if (droiError.isOk()) {
                    if (list.size() > 0) {
                        if (offset == 0) {
                            mPeopleAdapter.clear();
                        }
                        mPeopleAdapter.appendToList(list);
                        offset = mPeopleAdapter.getBasicItemCount();
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
