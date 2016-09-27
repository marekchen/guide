package com.droi.guide.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.droi.guide.R;
import com.droi.guide.fragment.AnswerFragment;
import com.droi.guide.model.FollowQuestionRelation;
import com.droi.guide.model.Question;
import com.droi.sdk.DroiCallback;
import com.droi.sdk.DroiError;
import com.droi.sdk.core.DroiCondition;
import com.droi.sdk.core.DroiQuery;
import com.droi.sdk.core.DroiQueryCallback;
import com.droi.sdk.core.DroiUser;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AnswerListActivity extends FragmentActivity {

    private FollowQuestionRelation mFollowQuestionRelation;
    private Question question;

    @BindView(R.id.question_follow_text)
    TextView followQuestionText;
    @BindView(R.id.question_follow_image)
    ImageView followQuestionImage;
    @BindView(R.id.question_follow)
    LinearLayout followQuestion;

    @BindView(R.id.top_bar_title)
    TextView topBarTitle;
    @BindView(R.id.top_bar_back_btn)
    ImageButton topBarBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_list);
        ButterKnife.bind(this);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        question = getIntent().getParcelableExtra(AnswerFragment.QUESTION);
        fetchFollowQuestionRelation();
        Fragment answerFragment = AnswerFragment.newInstance(question);
        ft.replace(R.id.answer_frame, answerFragment);
        ft.commit();
        topBarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        String answerNum = question.answerNum + getString(R.string.answer_num);
        topBarTitle.setText(answerNum);
    }

    @Override
    protected void onResume() {
        super.onResume();
        question.fetchInBackground(new DroiCallback<Boolean>() {
            @Override
            public void result(Boolean aBoolean, DroiError droiError) {
                if (aBoolean){
                    String answerNum = question.answerNum + getString(R.string.answer_num);
                    topBarTitle.setText(answerNum);
                }
            }
        });
    }

    private void fetchFollowQuestionRelation() {
        if (question == null) {
            return;
        }
        DroiCondition cond1 = DroiCondition.cond("questionId", DroiCondition.Type.EQ, question.getObjectId());
        DroiCondition cond2 = DroiCondition.cond("followerId", DroiCondition.Type.EQ, DroiUser.getCurrentUser().getObjectId());
        DroiCondition cond = cond1.and(cond2);
        DroiQuery query = DroiQuery.Builder.newBuilder().query(FollowQuestionRelation.class).where(cond).build();
        query.runQueryInBackground(new DroiQueryCallback<FollowQuestionRelation>() {
            @Override
            public void result(List<FollowQuestionRelation> list, DroiError droiError) {
                if (droiError.isOk()) {
                    if (list.size() == 1) {
                        followQuestionText.setText(getString(R.string.following));
                        followQuestionImage.setBackgroundResource(R.drawable.follow_press);
                        mFollowQuestionRelation = list.get(0);
                    }
                }
            }
        });
    }

    @OnClick(R.id.question_follow)
    void follow() {
        followQuestion.setClickable(false);
        if (mFollowQuestionRelation == null) {
            mFollowQuestionRelation = new FollowQuestionRelation(question, DroiUser.getCurrentUser().getObjectId());
            mFollowQuestionRelation.saveInBackground(new DroiCallback<Boolean>() {
                @Override
                public void result(Boolean aBoolean, DroiError droiError) {
                    if (aBoolean) {
                        followQuestionImage.setBackgroundResource(R.drawable.follow_press);
                        followQuestionText.setText(getString(R.string.following));
                        DroiCondition cond = DroiCondition.cond("_Id", DroiCondition.Type.EQ, question.getObjectId());
                        DroiQuery.Builder.newBuilder().update(Question.class).where(cond)
                                .inc("followNum").build().runInBackground(null);

                    } else {
                        mFollowQuestionRelation = null;
                    }
                    followQuestion.setClickable(true);
                }
            });
        } else {
            mFollowQuestionRelation.deleteInBackground(new DroiCallback<Boolean>() {
                @Override
                public void result(Boolean aBoolean, DroiError droiError) {
                    if (aBoolean) {
                        followQuestionImage.setBackgroundResource(R.drawable.follow_normal);
                        followQuestionText.setText(getString(R.string.follow));
                        DroiCondition cond = DroiCondition.cond("_Id", DroiCondition.Type.EQ, question.getObjectId());
                        DroiQuery.Builder.newBuilder().update(Question.class).where(cond)
                                .dec("followNum").build().runInBackground(null);
                        mFollowQuestionRelation = null;
                    }
                    followQuestion.setClickable(true);
                }
            });
        }
    }

    @OnClick(R.id.answer_question)
    void toWriteAnswer() {
        Intent intent = new Intent(AnswerListActivity.this, WriteAnswerActivity.class);
        intent.putExtra(AnswerFragment.QUESTION, question);
        startActivity(intent);
    }
}
