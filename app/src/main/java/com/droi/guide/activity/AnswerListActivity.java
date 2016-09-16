package com.droi.guide.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.droi.guide.R;
import com.droi.guide.adapter.AnswerAdapter;
import com.droi.guide.model.Answer;
import com.droi.guide.model.FollowQuestionRelation;
import com.droi.guide.model.Question;
import com.droi.guide.openhelp.BaseRecycleViewAdapter;
import com.droi.sdk.DroiCallback;
import com.droi.sdk.DroiError;
import com.droi.sdk.core.DroiCondition;
import com.droi.sdk.core.DroiQuery;
import com.droi.sdk.core.DroiQueryCallback;
import com.droi.sdk.core.DroiUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AnswerListActivity extends AppCompatActivity implements BaseRecycleViewAdapter.RequestLoadMoreListener {

    private boolean isFirstReq = false;
    private int indexNum = 0;
    private ArrayList<Answer> mAnswers;
    private static final String QUESTION = "QUESTION";
    private AnswerAdapter mAnswerAdapter = null;
    private FollowQuestionRelation mFollowQuestionRelation;
    private Question question;
    //    @BindView(R.id.question_title)
//    TextView questionTitle;
//    @BindView(R.id.question_content)
//    TextView questionContent;
//    @BindView(R.id.question_follow_num)
//    TextView questionFollowNum;
    @BindView(R.id.answer_lv)
    RecyclerView mRecyclerView;

    @BindView(R.id.answer_follow_text)
    TextView followAnswerText;

    @BindView(R.id.top_bar_title)
    TextView topBarTitle;
    @BindView(R.id.top_bar_back_btn)
    ImageButton topBarBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_list);
        ButterKnife.bind(this);
        topBarTitle.setText(getString(R.string.answers));
        topBarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
//        question = getIntent().getParcelableExtra(QUESTION);
//        questionTitle.setText(question.question);
//        questionContent.setText(question.body);
//        questionFollowNum.setText(question.followNum + "人关注");
//        fetchFollowQuestionRelation(question.getObjectId());
        if (mAnswers == null) {
            mAnswers = new ArrayList<>();
        }
        if (mAnswers.isEmpty()) {
            //fetchAnswer(question.getObjectId());
            fetchAnswer("11");
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAnswerAdapter = new AnswerAdapter(this);
        mRecyclerView.setAdapter(mAnswerAdapter);
        View view = LayoutInflater.from(this).inflate(R.layout.view_head_answer_list, mRecyclerView, false);
        mAnswerAdapter.addHeaderView(view);
        mAnswerAdapter.notifyDataSetChanged();
        mAnswerAdapter.setOnRecycleViewItemClickListener(new BaseRecycleViewAdapter.OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(AnswerListActivity.this, "click=" + position, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AnswerListActivity.this, DetailsActivity.class);
                intent.putExtra(DetailsActivity.ANSWER, mAnswers.get(position));
                startActivity(intent);
            }
        });

    }

    private void fetchAnswer(String questionId) {
        //DroiCondition cond = DroiCondition.cond("questionId", DroiCondition.Type.EQ, questionId);
        // DroiQuery query = DroiQuery.Builder.newBuilder().limit(10).offset(indexNum * 10).query(Answer.class).where(cond).build();
        DroiQuery query = DroiQuery.Builder.newBuilder().offset(indexNum * 10).query(Answer.class).build();
        query.runQueryInBackground(new DroiQueryCallback<Answer>() {
            @Override
            public void result(List<Answer> list, DroiError droiError) {
                if (droiError.isOk()) {
                    if (list.size() > 0) {
                        Log.i("test", "size:" + list.size());
                        if (indexNum == 0) {
                            mAnswers.clear();
                        }
                        mAnswers.addAll(list);
                        mAnswerAdapter.clear();
                        mAnswerAdapter.appendToList(mAnswers);
                        indexNum++;
                    }
                } else {
                    //做请求失败处理
                    mAnswerAdapter.setHasFooter(false);
                }
            }
        });
    }

    private void fetchFollowQuestionRelation(String questionId) {
        DroiCondition cond1 = DroiCondition.cond("questionId", DroiCondition.Type.EQ, questionId);
        DroiCondition cond2 = DroiCondition.cond("followerId", DroiCondition.Type.EQ, DroiUser.getCurrentUser().getObjectId());
        DroiCondition cond = cond1.and(cond2);
        DroiQuery query = DroiQuery.Builder.newBuilder().query(FollowQuestionRelation.class).where(cond).build();
        query.runQueryInBackground(new DroiQueryCallback<FollowQuestionRelation>() {
            @Override
            public void result(List<FollowQuestionRelation> list, DroiError droiError) {
                if (droiError.isOk()) {
                    if (list.size() == 1) {
                        followAnswerText.setText(getString(R.string.following));
                        mFollowQuestionRelation = list.get(0);
                    }
                }
            }
        });
    }

    @OnClick(R.id.answer_follow)
    void follow(LinearLayout answerFollow) {
        if (mFollowQuestionRelation == null) {
            mFollowQuestionRelation = new FollowQuestionRelation(question, DroiUser.getCurrentUser().getObjectId());
            mFollowQuestionRelation.fetchInBackground(new DroiCallback<Boolean>() {
                @Override
                public void result(Boolean aBoolean, DroiError droiError) {
                    if (aBoolean) {
                        followAnswerText.setText(getString(R.string.following));
                    }
                }
            });
        } else {
            mFollowQuestionRelation.deleteInBackground(new DroiCallback<Boolean>() {
                @Override
                public void result(Boolean aBoolean, DroiError droiError) {
                    if (aBoolean) {
                        followAnswerText.setText(getString(R.string.follow));
                    }
                }
            });
        }
    }

    @OnClick(R.id.answer_question)
    void toWriteAnswer(LinearLayout answerQuestion) {
        startActivity(new Intent(this, WriteAnswerActivity.class));
    }


    @Override
    public void onLoadMoreRequested() {
        fetchAnswer(question.getObjectId());
        if (isFirstReq) {
            mAnswerAdapter.setOnLoadMoreListener(10, AnswerListActivity.this); //get 10 items from quer each time!
            isFirstReq = false;
            mAnswerAdapter.setHasFooter(true);
        }
    }
}
