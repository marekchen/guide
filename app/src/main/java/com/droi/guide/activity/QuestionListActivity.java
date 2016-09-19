package com.droi.guide.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.droi.guide.R;
import com.droi.guide.adapter.QuestionAdapter;
import com.droi.guide.model.Answer;
import com.droi.guide.model.FollowQuestionRelation;
import com.droi.guide.model.Question;
import com.droi.guide.openhelp.BaseRecycleViewAdapter;
import com.droi.sdk.DroiError;
import com.droi.sdk.core.DroiCondition;
import com.droi.sdk.core.DroiQuery;
import com.droi.sdk.core.DroiQueryCallback;
import com.droi.sdk.core.DroiUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class QuestionListActivity extends AppCompatActivity implements BaseRecycleViewAdapter.RequestLoadMoreListener {

    private boolean isFirstReq = false;
    private int indexNum = 0;

    private QuestionAdapter mQuestionAdapter = null;
    private FollowQuestionRelation mFollowQuestionRelation;

    @BindView(R.id.question_lv)
    RecyclerView mRecyclerView;
    @BindView(R.id.question_swipe)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.top_bar_title)
    TextView topBarTitle;
    @BindView(R.id.top_bar_back_btn)
    ImageButton topBarBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_list);
        ButterKnife.bind(this);
        //topBarTitle.setText(getString(R.string.answers));
        topBarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mQuestionAdapter = new QuestionAdapter(this);
        mRecyclerView.setAdapter(mQuestionAdapter);
        mQuestionAdapter.notifyDataSetChanged();
        mQuestionAdapter.setOnRecycleViewItemClickListener(new BaseRecycleViewAdapter.OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(QuestionListActivity.this, "click=" + position, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(QuestionListActivity.this, AnswerListActivity.class);
                intent.putExtra(AnswerListActivity.QUESTION, (Question) mQuestionAdapter.getList().get(position));
                startActivity(intent);
            }
        });
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                indexNum = 0;
                fetchQuestion();
            }
        });
        fetchQuestion();
    }

    private void fetchQuestion() {
        setRefreshing(true);
        //DroiCondition cond = DroiCondition.cond("questionId", DroiCondition.Type.EQ, questionId);
        // DroiQuery query = DroiQuery.Builder.newBuilder().limit(10).offset(indexNum * 10).query(Answer.class).where(cond).build();
        DroiQuery query = DroiQuery.Builder.newBuilder().offset(indexNum * 10).limit(10).query(Question.class).build();
        query.runQueryInBackground(new DroiQueryCallback<Question>() {
            @Override
            public void result(List<Question> list, DroiError droiError) {
                if (droiError.isOk()) {
                    if (list.size() > 0) {
                        Log.i("test", "size:" + list.size());
                        if (indexNum == 0) {
                            mQuestionAdapter.clear();
                        }
                        mQuestionAdapter.appendToList(list);
                        indexNum++;
                    }
                } else {
                    //做请求失败处理
                    mQuestionAdapter.setHasFooter(false);
                }
                setRefreshing(false);
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

   /* private void fetchFollowQuestionRelation(String questionId) {
        DroiCondition cond1 = DroiCondition.cond("questionId", DroiCondition.Type.EQ, questionId);
        DroiCondition cond2 = DroiCondition.cond("followerId", DroiCondition.Type.EQ, DroiUser.getCurrentUser().getObjectId());
        DroiCondition cond = cond1.and(cond2);
        DroiQuery query = DroiQuery.Builder.newBuilder().query(FollowQuestionRelation.class).where(cond).build();
        query.runQueryInBackground(new DroiQueryCallback<FollowQuestionRelation>() {
            @Override
            public void result(List<FollowQuestionRelation> list, DroiError droiError) {
                if (droiError.isOk()) {
                    if (list.size() == 1) {
                        followQuestionText.setText(getString(R.string.following));
                        mFavoriteAnswerRelation = list.get(0);
                    }
                }
            }
        });
    }*/

/*    @OnClick(R.id.answer_follow)
    void follow(LinearLayout answerFollow) {
        if (mFavoriteAnswerRelation == null) {
            mFavoriteAnswerRelation = new FollowQuestionRelation(question, DroiUser.getCurrentUser().getObjectId());
            mFavoriteAnswerRelation.fetchInBackground(new DroiCallback<Boolean>() {
                @Override
                public void result(Boolean aBoolean, DroiError droiError) {
                    if (aBoolean) {
                        followQuestionText.setText(getString(R.string.following));
                    }
                }
            });
        } else {
            mFavoriteAnswerRelation.deleteInBackground(new DroiCallback<Boolean>() {
                @Override
                public void result(Boolean aBoolean, DroiError droiError) {
                    if (aBoolean) {
                        followQuestionText.setText(getString(R.string.follow));
                    }
                }
            });
        }
    }

    @OnClick(R.id.answer_question)
    void toWriteAnswer(LinearLayout answerQuestion) {
        startActivity(new Intent(this, WriteAnswerActivity.class));
    }*/


    @Override
    public void onLoadMoreRequested() {
        //fetchAnswer(question.getObjectId());
        if (isFirstReq) {
            mQuestionAdapter.setOnLoadMoreListener(10, QuestionListActivity.this); //get 10 items from quer each time!
            isFirstReq = false;
            mQuestionAdapter.setHasFooter(true);
        }
    }
}
