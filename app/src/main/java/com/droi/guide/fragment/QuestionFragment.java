package com.droi.guide.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.droi.guide.R;
import com.droi.guide.activity.AnswerListActivity;
import com.droi.guide.adapter.QuestionAdapter;
import com.droi.guide.model.FollowQuestionRelation;
import com.droi.guide.model.Question;
import com.droi.guide.adapter.BaseRecycleViewAdapter;
import com.droi.sdk.DroiError;
import com.droi.sdk.analytics.DroiAnalytics;
import com.droi.sdk.core.DroiCondition;
import com.droi.sdk.core.DroiQuery;
import com.droi.sdk.core.DroiQueryCallback;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class QuestionFragment extends Fragment {

    public static final String QUESTIONER = "QUESTIONER";
    public static final String FOLLOWER = "FOLLOWER";
    public static final String TYPE = "TYPE";
    public static final int TYPE_FOLLOWER = 1;
    public static final int TYPE_QUESTIONER = 2;

    private String questionerId;
    private String followerId;
    private int type = 0;
    private int offset = 0;
    private QuestionAdapter mQuestionAdapter = null;
    boolean isRefreshing = false;

    @BindView(R.id.fragment_lv)
    RecyclerView mRecyclerView;
    @BindView(R.id.fragment_swipe)
    SwipeRefreshLayout mSwipeRefreshLayout;

    public QuestionFragment() {
    }

    public static QuestionFragment newInstance(int type, String userId) {
        QuestionFragment fragment = new QuestionFragment();
        Bundle args = new Bundle();
        args.putInt(TYPE, type);
        if (type == 1) {
            args.putString(FOLLOWER, userId);
        } else if (type == 2) {
            args.putString(QUESTIONER, userId);
        }
        args.putInt(TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    public static QuestionFragment newInstance() {
        QuestionFragment fragment = new QuestionFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            questionerId = getArguments().getString(QUESTIONER);
            followerId = getArguments().getString(FOLLOWER);
            type = getArguments().getInt(TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_common, container, false);
        ButterKnife.bind(this, view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mQuestionAdapter = new QuestionAdapter(getActivity());
        mRecyclerView.setAdapter(mQuestionAdapter);
        mQuestionAdapter.notifyDataSetChanged();
        mQuestionAdapter.setOnRecycleViewItemClickListener(new BaseRecycleViewAdapter.OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getActivity(), AnswerListActivity.class);
                intent.putExtra(AnswerFragment.QUESTION, (Question) mQuestionAdapter.getList().get(position));
                startActivity(intent);
            }
        });
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                offset = 0;
                fetchQuestion();
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (type == 0) {
            offset = 0;
        }
        fetchQuestion();
        DroiAnalytics.onFragmentStart(getActivity(), "QuestionFragment");

    }

    @Override
    public void onPause() {
        super.onPause();
        DroiAnalytics.onFragmentEnd(getActivity(), "QuestionFragment");
    }

    private void fetchQuestion() {
        if (offset == 0) {
            setRefreshing(true);
        }
        if (isRefreshing) {
            return;
        }
        isRefreshing = true;
        if (type != 0 && followerId != null) {
            DroiCondition cond = DroiCondition.cond("followerId", DroiCondition.Type.EQ, followerId);
            DroiQuery query = DroiQuery.Builder.newBuilder().offset(offset).limit(10).where(cond).query(FollowQuestionRelation.class).build();
            query.runQueryInBackground(new DroiQueryCallback<FollowQuestionRelation>() {
                @Override
                public void result(List<FollowQuestionRelation> list, DroiError droiError) {
                    if (droiError.isOk()) {
                        if (list.size() > 0) {
                            if (offset == 0) {
                                mQuestionAdapter.clear();
                            }
                            for (FollowQuestionRelation qr : list) {
                                mQuestionAdapter.append(qr.question);
                            }
                            mQuestionAdapter.notifyDataSetChanged();
                            offset = mQuestionAdapter.getBasicItemCount();
                        }
                    } else {
                        //做请求失败处理
                        mQuestionAdapter.setHasFooter(false);
                    }
                    setRefreshing(false);
                    isRefreshing = false;
                }
            });
        } else if (type != 0 && questionerId != null) {
            DroiCondition cond = DroiCondition.cond("questionerId", DroiCondition.Type.EQ, questionerId);
            DroiQuery query = DroiQuery.Builder.newBuilder().offset(offset).limit(10).where(cond).query(Question.class).build();
            query.runQueryInBackground(new DroiQueryCallback<Question>() {
                @Override
                public void result(List<Question> list, DroiError droiError) {
                    if (droiError.isOk()) {
                        if (list.size() > 0) {
                            if (offset == 0) {
                                mQuestionAdapter.clear();
                            }
                            mQuestionAdapter.appendToList(list);
                            offset = mQuestionAdapter.getBasicItemCount();
                        }
                    } else {
                        //做请求失败处理
                        mQuestionAdapter.setHasFooter(false);
                    }
                    setRefreshing(false);
                    isRefreshing = false;
                }
            });
        } else {
            DroiQuery query = DroiQuery.Builder.newBuilder().offset(offset).limit(10).orderBy("answerNum", true).query(Question.class).build();
            query.runQueryInBackground(new DroiQueryCallback<Question>() {
                @Override
                public void result(List<Question> list, DroiError droiError) {
                    if (droiError.isOk()) {
                        if (list.size() > 0) {
                            if (offset == 0) {
                                mQuestionAdapter.clear();
                            }
                            mQuestionAdapter.appendToList(list);
                            offset = mQuestionAdapter.getBasicItemCount();
                        }
                    } else {
                        //做请求失败处理
                        mQuestionAdapter.setHasFooter(false);
                    }
                    setRefreshing(false);
                    isRefreshing = false;
                }
            });
        }
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
