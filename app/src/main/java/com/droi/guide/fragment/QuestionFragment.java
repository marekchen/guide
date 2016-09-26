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
import com.droi.guide.model.Question;
import com.droi.guide.openhelp.BaseRecycleViewAdapter;
import com.droi.sdk.DroiError;
import com.droi.sdk.core.DroiQuery;
import com.droi.sdk.core.DroiQueryCallback;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class QuestionFragment extends Fragment {

    public static final String USER = "USER";

    private String userId;
    private int offset = 0;
    private QuestionAdapter mQuestionAdapter = null;
    boolean isRefreshing = false;

    @BindView(R.id.question_lv)
    RecyclerView mRecyclerView;
    @BindView(R.id.question_swipe)
    SwipeRefreshLayout mSwipeRefreshLayout;

    public QuestionFragment() {
    }

    public static QuestionFragment newInstance(String userId) {
        QuestionFragment fragment = new QuestionFragment();
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
        View view = inflater.inflate(R.layout.fragment_question, container, false);
        ButterKnife.bind(this, view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mQuestionAdapter = new QuestionAdapter(getActivity());
        mRecyclerView.setAdapter(mQuestionAdapter);
        mQuestionAdapter.notifyDataSetChanged();
        mQuestionAdapter.setOnRecycleViewItemClickListener(new BaseRecycleViewAdapter.OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(getActivity(), "click=" + position, Toast.LENGTH_SHORT).show();
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
        fetchQuestion();

        return view;
    }

    private void fetchQuestion() {
        if (offset == 0) {
            setRefreshing(true);
        }
        if (isRefreshing) {
            return;
        }
        isRefreshing = true;

        //DroiCondition cond = DroiCondition.cond("questionerId", DroiCondition.Type.EQ, userId);
        DroiQuery query = DroiQuery.Builder.newBuilder().offset(offset).limit(10).query(Question.class).build();
        query.runQueryInBackground(new DroiQueryCallback<Question>() {
            @Override
            public void result(List<Question> list, DroiError droiError) {
                if (droiError.isOk()) {
                    if (list.size() > 0) {
                        Log.i("test", "size:" + list.size());
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
