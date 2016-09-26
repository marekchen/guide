package com.droi.guide.activity;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.droi.guide.R;
import com.droi.guide.adapter.AnswerAdapter;
import com.droi.guide.adapter.CommentAdapter;
import com.droi.guide.model.Comment;
import com.droi.guide.model.GuideUser;
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

public class CommentListActivity extends AppCompatActivity {

    private int offset = 0;
    private CommentAdapter mCommentAdapter;
    String refId;
    int type = 0;
    Context mContext;
    boolean isRefreshing = false;

    @BindView(R.id.comment_lv)
    RecyclerView mRecyclerView;
    @BindView(R.id.comment_swipe)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.comment)
    EditText commentEditText;

    @BindView(R.id.top_bar_title)
    TextView topBarTitle;
    @BindView(R.id.top_bar_back_btn)
    ImageButton topBarBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        ButterKnife.bind(this);
        refId = getIntent().getStringExtra("officialId");
        if (refId == null) {
            type = 1;
            refId = getIntent().getStringExtra("answerId");
        }

        final LinearLayoutManager mRecycleViewLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mRecycleViewLayoutManager);
        mCommentAdapter = new CommentAdapter(this);
        mRecyclerView.setAdapter(mCommentAdapter);

        topBarTitle.setText(getString(R.string.comment));
        topBarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                offset = 0;
                fetchComments();
            }
        });
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && mRecycleViewLayoutManager.findLastVisibleItemPosition() + 1
                        == mCommentAdapter.getItemCount()) {
                    if (mCommentAdapter.getBasicItemCount() != 0
                            && mCommentAdapter.getBasicItemCount() >= 10) {
                        fetchComments();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchComments();
    }

    private void fetchComments() {

        if (offset == 0) {
            setRefreshing(true);
        }
        if (isRefreshing) {
            return;
        }
        isRefreshing = true;

        DroiCondition cond1 = DroiCondition.cond("refId", DroiCondition.Type.EQ, refId);
        DroiCondition cond2 = DroiCondition.cond("type", DroiCondition.Type.EQ, type);
        DroiCondition cond = cond1.and(cond2);
        DroiQuery query = DroiQuery.Builder.newBuilder().limit(10).offset(offset).query(Comment.class).where(cond).build();
        query.runQueryInBackground(new DroiQueryCallback<Comment>() {
            @Override
            public void result(List<Comment> list, DroiError droiError) {
                if (droiError.isOk()) {
                    if (list.size() > 0) {
                        if (offset == 0) {
                            mCommentAdapter.clear();
                        }
                        mCommentAdapter.appendToList(list);
                        offset = mCommentAdapter.getBasicItemCount();
                        //mCommentAdapter.notifyDataSetChanged();
                    }
                } else {
                    //做请求失败处理
                }
                isRefreshing = false;
                setRefreshing(false);
            }
        });
    }

    @OnClick(R.id.send_comment)
    void sendComment() {
        String commentString = commentEditText.getText().toString();
        if (commentString == null || commentString.isEmpty()) {
            Toast.makeText(this, "评论不能为空", Toast.LENGTH_SHORT).show();
        } else {
            final Comment comment = new Comment(refId, type, commentString, DroiUser.getCurrentUser(GuideUser.class));
            comment.saveInBackground(new DroiCallback<Boolean>() {
                @Override
                public void result(Boolean aBoolean, DroiError droiError) {
                    if (aBoolean) {
                        commentEditText.setText("");
                        fetchComments();
                    } else {
                        Toast.makeText(mContext, "发送失败", Toast.LENGTH_SHORT).show();
                    }
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
}
