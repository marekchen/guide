package com.droi.guide.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.droi.guide.R;
import com.droi.guide.adapter.CommentAdapter;
import com.droi.guide.model.Comment;
import com.droi.guide.model.GuideUser;
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

public class CommentListActivity extends AppCompatActivity {

    private int indexNum = 0;
    private ArrayList<Comment> mComments;
    private CommentAdapter mCommentAdapter;
    String refId;
    int type = 1;
    Context mContext;
    @BindView(R.id.comment_lv)
    RecyclerView listView;
    @BindView(R.id.comment)
    EditText commentEditText;

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
        if (mComments == null) {
            mComments = new ArrayList<>();
        }
        if (mComments.isEmpty()) {
            fetchComments();
        }
        mCommentAdapter = new CommentAdapter(this, mComments);
        listView.setAdapter(mCommentAdapter);
    }

    private void fetchComments() {
        /*DroiCondition cond1 = DroiCondition.cond("refId", DroiCondition.Type.EQ, refId);
        DroiCondition cond2 = DroiCondition.cond("type", DroiCondition.Type.EQ, type);
        DroiCondition cond = cond1.and(cond2);*/
        DroiQuery query = DroiQuery.Builder.newBuilder().query(Comment.class).build();
        query.runQueryInBackground(new DroiQueryCallback<Comment>() {
            @Override
            public void result(List<Comment> list, DroiError droiError) {
                if (droiError.isOk()) {
                    if (list.size() > 0) {
                        mComments.addAll(list);
                        mCommentAdapter.notifyDataSetChanged();
                    }
                } else {
                    //做请求失败处理
                }
            }
        });
    }

    @OnClick(R.id.send_comment)
    void sendComment() {
        String commentString = commentEditText.getText().toString();
        if (commentString == null || commentString.isEmpty()) {
            Toast.makeText(this, "评论不能为空", Toast.LENGTH_SHORT).show();
        } else {
            Log.i("test", "01" + commentString);
            final Comment comment = new Comment(refId, type, commentString, DroiUser.getCurrentUser(GuideUser.class));
            Log.i("test", "02");
            comment.saveInBackground(new DroiCallback<Boolean>() {
                @Override
                public void result(Boolean aBoolean, DroiError droiError) {
                    Log.i("test", "03");
                    if (aBoolean) {
                        Log.i("test", "04");
                        //Toast.makeText(mContext, "发送成功", Toast.LENGTH_SHORT).show();
                        Log.i("test", "05");
                        mComments.add(comment);
                        Log.i("test", "0");
                        mCommentAdapter.notifyDataSetChanged();
                        Log.i("test", "1");
                        commentEditText.setText("");
                        Log.i("test", "2");
                    } else {
                        Toast.makeText(mContext, "发送失败", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
