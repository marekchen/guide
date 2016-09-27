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
import android.widget.TextView;
import android.widget.Toast;

import com.droi.guide.R;
import com.droi.guide.activity.DetailsActivity;
import com.droi.guide.adapter.AnswerAdapter;
import com.droi.guide.model.Article;
import com.droi.guide.model.FavoriteRelation;
import com.droi.guide.model.Question;
import com.droi.guide.openhelp.BaseRecycleViewAdapter;
import com.droi.sdk.DroiError;
import com.droi.sdk.core.DroiCondition;
import com.droi.sdk.core.DroiQuery;
import com.droi.sdk.core.DroiQueryCallback;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AnswerFragment extends Fragment {

    public static final String QUESTION = "QUESTION";
    public static final String AUTHOR = "AUTHOR";
    public static final String USER = "USER";

    private Question question;
    private String authorId;
    private String userId;
    private int offset = 0;
    private AnswerAdapter mAnswerAdapter = null;
    boolean isRefreshing = false;

    @BindView(R.id.fragment_lv)
    RecyclerView mRecyclerView;
    @BindView(R.id.fragment_swipe)
    SwipeRefreshLayout mSwipeRefreshLayout;

    public AnswerFragment() {
    }

    public static AnswerFragment newInstance(Question question) {
        AnswerFragment fragment = new AnswerFragment();
        Bundle args = new Bundle();
        args.putParcelable(QUESTION, question);
        fragment.setArguments(args);
        return fragment;
    }

    public static AnswerFragment newInstance(String authorId) {
        AnswerFragment fragment = new AnswerFragment();
        Bundle args = new Bundle();
        args.putString(AUTHOR, authorId);
        fragment.setArguments(args);
        return fragment;
    }

    public static AnswerFragment newInstance(int type, String userId) {
        AnswerFragment fragment = new AnswerFragment();
        Bundle args = new Bundle();
        args.putString(USER, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            question = getArguments().getParcelable(QUESTION);
            authorId = getArguments().getString(AUTHOR);
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
        mAnswerAdapter = new AnswerAdapter(this.getContext());
        mRecyclerView.setAdapter(mAnswerAdapter);

        if (question != null) {
            String questionTitleText = question.questionTitle;
            String questionContentText = question.questionContent;
            String followNum = question.followNum + getString(R.string.follow);
            View headView = LayoutInflater.from(this.getActivity()).inflate(R.layout.view_head_answer_list, mRecyclerView, false);

            TextView questionTitle = (TextView) headView.findViewById(R.id.question_title);
            TextView questionContent = (TextView) headView.findViewById(R.id.question_content);
            TextView questionFollowNum = (TextView) headView.findViewById(R.id.question_follow_num);

            questionTitle.setText(questionTitleText);
            questionContent.setText(questionContentText);
            questionFollowNum.setText(followNum);

            mAnswerAdapter.addHeaderView(headView);
            mAnswerAdapter.notifyDataSetChanged();
        }

        mAnswerAdapter.setOnRecycleViewItemClickListener(new BaseRecycleViewAdapter.OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(getActivity(), "click=" + position, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra(DetailsActivity.ANSWER, (Article) mAnswerAdapter.getList().get(position));
                startActivity(intent);
            }
        });
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                offset = 0;
                fetchAnswer();
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && mRecycleViewLayoutManager.findLastVisibleItemPosition() + 1
                        == mAnswerAdapter.getItemCount()) {
                    if (mAnswerAdapter.getBasicItemCount() != 0
                            && mAnswerAdapter.getBasicItemCount() >= 10) {
                        fetchAnswer();
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
        Log.i("test","resume");
        fetchAnswer();
    }

    private void fetchAnswer() {
        if (offset == 0) {
            setRefreshing(true);
        }

        if (isRefreshing) {
            return;
        }
        isRefreshing = true;

        if (userId != null) {
            DroiCondition cond = DroiCondition.cond("userId", DroiCondition.Type.EQ, userId);
            DroiCondition cond2 = cond.and(DroiCondition.cond("type", DroiCondition.Type.EQ, Article.TYPE_ANSWER));
            DroiQuery query = DroiQuery.Builder.newBuilder().limit(10).offset(offset).query(FavoriteRelation.class).where(cond2).build();
            query.runQueryInBackground(new DroiQueryCallback<FavoriteRelation>() {
                @Override
                public void result(List<FavoriteRelation> list, DroiError droiError) {
                    if (droiError.isOk()) {
                        if (list.size() > 0) {
                            if (offset == 0) {
                                mAnswerAdapter.clear();
                            }
                            for (FavoriteRelation fr : list) {
                                mAnswerAdapter.append(fr.article);
                            }
                            mAnswerAdapter.notifyDataSetChanged();
                            offset = mAnswerAdapter.getBasicItemCount();
                        }
                    } else {
                        //做请求失败处理
                    }
                    setRefreshing(false);
                    isRefreshing = false;
                }
            });
        } else {
            DroiCondition cond;
            if (question != null) {
                cond = DroiCondition.cond("questionId", DroiCondition.Type.EQ, question.getObjectId());
            } else {
                cond = DroiCondition.cond("authorId", DroiCondition.Type.EQ, authorId);
            }
            DroiCondition cond2 = cond.and(DroiCondition.cond("type", DroiCondition.Type.EQ, Article.TYPE_ANSWER));
            DroiQuery query = DroiQuery.Builder.newBuilder().limit(10).offset(offset).query(Article.class).where(cond2).build();
            query.runQueryInBackground(new DroiQueryCallback<Article>() {
                @Override
                public void result(List<Article> list, DroiError droiError) {
                    if (droiError.isOk()) {
                        if (list.size() > 0) {
                            if (offset == 0) {
                                mAnswerAdapter.clear();
                            }
                            mAnswerAdapter.appendToList(list);
                            offset = mAnswerAdapter.getBasicItemCount();
                        }
                    } else {
                        //做请求失败处理
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
