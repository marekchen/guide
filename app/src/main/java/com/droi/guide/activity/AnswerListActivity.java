package com.droi.guide.activity;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
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

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AnswerListActivity extends AppCompatActivity implements BaseRecycleViewAdapter.RequestLoadMoreListener {

    private boolean isFirstReq = true;
    private int indexNum = 0;
    public static final String QUESTION = "QUESTION";
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
    RecyclerView mRecyclerView;
    @BindView(R.id.answer_swipe)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.question_follow_text)
    TextView followQuestionText;
    @BindView(R.id.question_follow_image)
    ImageView followQuestionImage;

    @BindView(R.id.top_bar_title)
    TextView topBarTitle;
    @BindView(R.id.top_bar_back_btn)
    ImageButton topBarBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_list);
        ButterKnife.bind(this);
        topBarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        question = getIntent().getParcelableExtra(QUESTION);
        final String questionId = question.getObjectId();
        String answerNum = question.answerNum + getString(R.string.answer_num);
        String questionTitleText = question.questiontTitle;
        String questionContentText = question.questionContent;
        String followNum = question.followNum + getString(R.string.follow_num);
        topBarTitle.setText(answerNum);
        questionTitle.setText(questionTitleText);
        questionContent.setText(questionContentText);
        questionFollowNum.setText(followNum);

        final LinearLayoutManager mRecycleViewLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mRecycleViewLayoutManager);
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
                intent.putExtra(DetailsActivity.ANSWER, (Answer) mAnswerAdapter.getList().get(position));
                startActivity(intent);
            }
        });
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                indexNum = 0;
                fetchAnswer(questionId);
            }
        });
        fetchAnswer(questionId);
        fetchFollowQuestionRelation(questionId);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && mRecycleViewLayoutManager.findLastVisibleItemPosition() + 1
                        == mAnswerAdapter.getItemCount()) {
                    if (mAnswerAdapter.getBasicItemCount() != 0
                            && mAnswerAdapter.getBasicItemCount() >= 10) {
                        fetchAnswer(questionId);
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private void fetchAnswer(String questionId) {
        setRefreshing(true);
        //DroiCondition cond = DroiCondition.cond("questionId", DroiCondition.Type.EQ, questionId);
        // DroiQuery query = DroiQuery.Builder.newBuilder().limit(10).offset(indexNum * 10).query(Answer.class).where(cond).build();
        DroiQuery query = DroiQuery.Builder.newBuilder().offset(indexNum * 10).limit(10).query(Answer.class).build();
        query.runQueryInBackground(new DroiQueryCallback<Answer>() {
            @Override
            public void result(List<Answer> list, DroiError droiError) {
                if (droiError.isOk()) {
                    if (list.size() > 0) {
                        if (indexNum == 0) {
                            mAnswerAdapter.clear();
                        }
                        mAnswerAdapter.appendToList(list);
                        indexNum++;
                    }
                } else {
                    //做请求失败处理
                }
                setRefreshing(false);
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
                        followQuestionText.setText(getString(R.string.following));
                        followQuestionImage.setBackgroundResource(R.drawable.answer_press);
                        mFollowQuestionRelation = list.get(0);
                    }
                }
            }
        });
    }

    @OnClick(R.id.question_follow)
    void follow() {
        if (mFollowQuestionRelation == null) {
            mFollowQuestionRelation = new FollowQuestionRelation(question, DroiUser.getCurrentUser().getObjectId());
            mFollowQuestionRelation.fetchInBackground(new DroiCallback<Boolean>() {
                @Override
                public void result(Boolean aBoolean, DroiError droiError) {
                    if (aBoolean) {
                        followQuestionImage.setBackgroundResource(R.drawable.answer_press);
                        followQuestionText.setText(getString(R.string.following));

                    }
                }
            });
        } else {
            mFollowQuestionRelation.deleteInBackground(new DroiCallback<Boolean>() {
                @Override
                public void result(Boolean aBoolean, DroiError droiError) {
                    if (aBoolean) {
                        followQuestionImage.setBackgroundResource(R.drawable.answer_normal);
                        followQuestionText.setText(getString(R.string.follow));
                    }
                }
            });
        }
    }

    @OnClick(R.id.answer_question)
    void toWriteAnswer() {
        startActivity(new Intent(this, WriteAnswerActivity.class));
    }

    @Override
    public void onLoadMoreRequested() {
    }

    void setRefreshing(final boolean b) {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(b);
            }
        });
    }
}
