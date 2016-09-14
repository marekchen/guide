package com.droi.guide.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import com.droi.guide.R;

import butterknife.BindView;

public class WriteCommentsActivity extends AppCompatActivity {

    String refId;
    int type = 1;

    @BindView(R.id.comment_edittext)
    EditText commentEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_comments);
        refId = getIntent().getStringExtra("officialId");
        if (refId == null) {
            type = 1;
            refId = getIntent().getStringExtra("answerId");
        }
    }
}
