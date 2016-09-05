package com.droi.guide.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.droi.guide.R;
import com.droi.guide.adapter.CommentAdapter;
import com.droi.guide.model.Comment;
import com.droi.sdk.DroiError;
import com.droi.sdk.core.DroiCondition;
import com.droi.sdk.core.DroiQuery;
import com.droi.sdk.core.DroiQueryCallback;

import java.util.ArrayList;
import java.util.List;

public class CommentActivity extends AppCompatActivity {

    private int indexNum = 0;
    private ArrayList<Comment> mComments = null;
    private CommentAdapter mCommentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        String answerId=getIntent().getStringExtra("answerId");
        mCommentAdapter = new CommentAdapter(this,mComments);
        
    }

    private void fetchComment(String answerId){
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


}
