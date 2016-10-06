package com.droi.guide.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.droi.guide.R;
import com.droi.guide.model.GuideUser;
import com.droi.guide.model.Question;
import com.droi.sdk.DroiCallback;
import com.droi.sdk.DroiError;
import com.droi.sdk.analytics.DroiAnalytics;
import com.droi.sdk.core.DroiUser;

import java.util.ArrayList;
import java.util.List;

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
    @BindView(R.id.category_spinner)
    Spinner categorySpinner;

    Context mContext;
    private List<String> data_list;
    private ArrayAdapter<String> arr_adapter;
    String category = "social";

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
        //数据
        data_list = new ArrayList<String>();
        data_list.add(getString(R.string.category_social));
        data_list.add(getString(R.string.category_education));
        data_list.add(getString(R.string.category_credential));
        data_list.add(getString(R.string.category_wedding));
        data_list.add(getString(R.string.category_transport));
        data_list.add(getString(R.string.category_other));

        //适配器
        arr_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data_list);
        //设置样式
        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        category = "social";
                        break;
                    case 1:
                        category = "education";
                        break;
                    case 2:
                        category = "credential";
                        break;
                    case 3:
                        category = "wedding";
                        break;
                    case 4:
                        category = "transport";
                        break;
                    case 5:
                        category = "other";
                        break;
                    default:
                        category = "social";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });
        categorySpinner.setAdapter(arr_adapter);
    }

    @OnClick(R.id.add_question)
    void addQuestion() {
        String questionTitleText = questionTitle.getText().toString();
        String questionContentText = questionContent.getText().toString();
        GuideUser user = DroiUser.getCurrentUser(GuideUser.class);
        if (!questionTitleText.isEmpty() && !questionContentText.isEmpty() && user != null) {
            Question question = new Question(questionTitleText, questionContentText, user);
            question.category = category;
            question.location = "上海";
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
