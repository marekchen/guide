package com.droi.guide.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.droi.guide.R;
import com.droi.guide.model.GuideUser;
import com.droi.guide.model.Question;
import com.droi.sdk.DroiCallback;
import com.droi.sdk.DroiError;
import com.droi.sdk.analytics.DroiAnalytics;
import com.droi.sdk.core.DroiUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WriteQuestionActivity extends AppCompatActivity {

    @BindView(R.id.question_title)
    EditText questionTitle;
    @BindView(R.id.question_content)
    EditText questionContent;

    @BindView(R.id.top_bar_title)
    TextView topBarTitle;
    @BindView(R.id.top_bar_back_btn)
    ImageButton topBarBack;

    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_question);
        mContext = this;
        ButterKnife.bind(this);
        topBarTitle.setText(getString(R.string.add_question));
        topBarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @OnClick(R.id.add_question)
    void addQuestion() {
        String questionTitleText = questionTitle.getText().toString();
        String questionContentText = questionContent.getText().toString();
        GuideUser user = DroiUser.getCurrentUser(GuideUser.class);
        if (questionTitleText != null && !questionTitleText.isEmpty()
                && questionContentText != null && !questionContentText.isEmpty()
                && user != null) {
            Question question = new Question(questionTitleText, questionContentText, user);
            question.saveInBackground(new DroiCallback<Boolean>() {
                @Override
                public void result(Boolean aBoolean, DroiError droiError) {
                    if (droiError.isOk()) {
                        finish();
                    } else {
                        Toast.makeText(mContext, "失败", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(mContext, "为空", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        DroiAnalytics.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        DroiAnalytics.onPause(this);
    }
}
