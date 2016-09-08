package com.droi.guide.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.droi.guide.R;
import com.droi.guide.adapter.AnswerAdapter;
import com.droi.guide.model.Answer;
import com.droi.guide.model.Comment;
import com.droi.guide.model.FollowQuestionRelation;
import com.droi.guide.model.Question;
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

public class AnswerListActivity extends AppCompatActivity {

    private int indexNum = 0;
    private ArrayList<Answer> mAnswers;
    private static final String QUESTION = "QUESTION";
    private AnswerAdapter mAnswerAdapter = null;
    private FollowQuestionRelation mFollowQuestionRelation;
    private Question question;
    @BindView(R.id.question_title)
    TextView questionTitle;
    @BindView(R.id.question_content)
    TextView questionContent;
    @BindView(R.id.question_follow_num)
    TextView questionFollowNum;
    @BindView(R.id.answer_lv)
    ListView listView;
    @BindView(R.id.answer_follow)
    Button followAnswerButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_list);
        ButterKnife.bind(this);
        question = getIntent().getParcelableExtra(QUESTION);
        questionTitle.setText(question.question);
        questionContent.setText(question.body);
        questionFollowNum.setText(question.followNum + "人关注");
        fetchFollowQuestionRelation(question.getObjectId());
        if (mAnswers == null) {
            mAnswers = new ArrayList<>();
        }
        if (mAnswers.isEmpty()) {
            fetchAnswer(question.getObjectId());
        }
        mAnswerAdapter = new AnswerAdapter(this, mAnswers);
        listView.setAdapter(mAnswerAdapter);
    }

    private void fetchAnswer(String questionId) {
        DroiCondition cond = DroiCondition.cond("questionId", DroiCondition.Type.EQ, questionId);
        DroiQuery query = DroiQuery.Builder.newBuilder().limit(10).offset(indexNum * 10).query(Answer.class).where(cond).build();
        query.runQueryInBackground(new DroiQueryCallback<Answer>() {
            @Override
            public void result(List<Answer> list, DroiError droiError) {
                if (droiError.isOk()) {
                    if (list.size() > 0) {
                        if (indexNum == 0) {
                            mAnswers.clear();
                        }
                        mAnswers.addAll(list);
                        mAnswerAdapter.notifyDataSetChanged();
                        indexNum++;
                    }
                } else {
                    //做请求失败处理
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
                        followAnswerButton.setText(getString(R.string.following));
                        mFollowQuestionRelation = list.get(0);
                    }
                }
            }
        });
    }

    @OnClick(R.id.answer_follow)
    void follow() {
        if (mFollowQuestionRelation == null) {
            mFollowQuestionRelation = new FollowQuestionRelation(question, DroiUser.getCurrentUser().getObjectId());
            mFollowQuestionRelation.fetchInBackground(new DroiCallback<Boolean>() {
                @Override
                public void result(Boolean aBoolean, DroiError droiError) {
                    if (aBoolean) {
                        followAnswerButton.setText(getString(R.string.following));
                    }
                }
            });
        } else {
            mFollowQuestionRelation.deleteInBackground(new DroiCallback<Boolean>() {
                @Override
                public void result(Boolean aBoolean, DroiError droiError) {
                    if (aBoolean) {
                        followAnswerButton.setText(getString(R.string.follow));
                    }
                }
            });
        }
    }

    @OnClick(R.id.answer_question)
    void toWriteAnswer() {
        startActivity(new Intent(this, WriteAnswerActivity.class));
    }
}
