package com.droi.guide.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.droi.guide.R;
import com.droi.guide.adapter.AnswerAdapter;
import com.droi.guide.model.Answer;
import com.droi.guide.model.Comment;
import com.droi.guide.model.Question;
import com.droi.sdk.DroiError;
import com.droi.sdk.core.DroiCondition;
import com.droi.sdk.core.DroiQuery;
import com.droi.sdk.core.DroiQueryCallback;

import java.util.ArrayList;
import java.util.List;

public class AnswerListActivity extends AppCompatActivity {

    private int indexNum = 0;
    private ArrayList<Answer> mAnswers = null;
    private static final String QUESTION = "QUESTION";
    private AnswerAdapter mAnswerAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_list);
        Question question = getIntent().getParcelableExtra(QUESTION);
        fetchAnswer(question.getObjectId());
        mAnswerAdapter = new AnswerAdapter(this, mAnswers);
    }

    private void fetchAnswer(String questionId) {
        DroiCondition cond = DroiCondition.cond("questionId", DroiCondition.Type.EQ, questionId);
        DroiQuery query = DroiQuery.Builder.newBuilder().limit(10).offset(indexNum * 10).query(Comment.class).where(cond).build();
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

}
