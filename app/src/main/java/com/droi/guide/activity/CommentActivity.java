package com.droi.guide.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.droi.guide.R;
import com.droi.guide.adapter.CommentAdapter;
import com.droi.guide.model.Comment;
import com.droi.guide.model.LikeAnswerRelation;
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

public class CommentActivity extends AppCompatActivity {

    private int indexNum = 0;
    private ArrayList<Comment> mComments = null;
    private CommentAdapter mCommentAdapter;
    String answerId;
    Context mContext;
    @BindView(R.id.comment_edittext)
    EditText commentEditText;
    @BindView(R.id.comment_lv)
    ListView listView;

    LikeAnswerRelation mLikeAnswerRelation = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        ButterKnife.bind(this);
        answerId = getIntent().getStringExtra("answerId");
        mCommentAdapter = new CommentAdapter(this, mComments);
        fetchComments(answerId);
        fetchLikeAnswerRelation(answerId);
        listView.setAdapter(mCommentAdapter);
    }

    private void fetchComments(String answerId) {
        DroiCondition cond1 = DroiCondition.cond("answerId", DroiCondition.Type.EQ, answerId);
        DroiCondition cond2 = DroiCondition.cond("userId", DroiCondition.Type.EQ, DroiUser.getCurrentUser().getUserId());
        DroiCondition cond = cond1.and(cond2);
        DroiQuery query = DroiQuery.Builder.newBuilder().query(LikeAnswerRelation.class).where(cond).build();
        query.runQueryInBackground(new DroiQueryCallback<LikeAnswerRelation>() {
            @Override
            public void result(List<LikeAnswerRelation> list, DroiError droiError) {
                if (droiError.isOk()) {
                    if (list.size() > 0) {
                        mLikeAnswerRelation = list.get(0);
                    }
                } else {
                    //做请求失败处理
                }
            }
        });
    }

    private void fetchLikeAnswerRelation(String answerId) {
        DroiCondition cond = DroiCondition.cond("answerId", DroiCondition.Type.EQ, answerId);
        DroiQuery query = DroiQuery.Builder.newBuilder().limit(10).offset(indexNum * 10).query(Comment.class).where(cond).build();
        query.runQueryInBackground(new DroiQueryCallback<Comment>() {
            @Override
            public void result(List<Comment> list, DroiError droiError) {
                if (droiError.isOk()) {
                    if (list.size() > 0) {
                        if (indexNum == 0) {
                            mComments.clear();
                        }
                        mComments.addAll(list);
                        mCommentAdapter.notifyDataSetChanged();
                        indexNum++;
                    }
                } else {
                    //做请求失败处理
                }
            }
        });
    }

    @OnClick(R.id.comment_comment)
    void sendComment() {
        String commentString = commentEditText.getText().toString();
        if (commentString == null || commentString.isEmpty()) {
            Toast.makeText(this, "评论不能为空", Toast.LENGTH_SHORT).show();
        } else {
            Comment comment = new Comment();
            comment.answerId = answerId;
            comment.comment = commentString;
            comment.commenterId = DroiUser.getCurrentUser().getUserId();
            comment.saveInBackground(new DroiCallback<Boolean>() {
                @Override
                public void result(Boolean aBoolean, DroiError droiError) {
                    if (aBoolean) {
                        Toast.makeText(mContext, "发送成功", Toast.LENGTH_SHORT).show();
                        commentEditText.setText("");
                    } else {
                        Toast.makeText(mContext, "发送失败", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @OnClick(R.id.comment_like)
    void like() {
        if (mLikeAnswerRelation == null) {
            LikeAnswerRelation likeAnswerRelation = new LikeAnswerRelation(answerId, DroiUser.getCurrentUser().getUserId());
            likeAnswerRelation.fetchInBackground(null);
        } else {
            mLikeAnswerRelation.deleteInBackground(null);
        }
    }
}
