package com.droi.guide.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.droi.guide.R;
import com.droi.guide.fragment.AnswerFragment;
import com.droi.guide.fragment.ArticleFragment;
import com.droi.guide.fragment.SearchFragment;
import com.droi.guide.model.Article;
import com.droi.guide.model.GuideUser;
import com.droi.sdk.core.DroiUser;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by marek on 2016/9/27.
 */

public class SearchActivity extends FragmentActivity {
    @BindView(R.id.top_bar_title)
    TextView topBarTitle;
    @BindView(R.id.top_bar_back_btn)
    ImageButton topBarBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        String keyword = getIntent().getStringExtra(ArticleFragment.KEYWORD);
        Fragment articleFragment = ArticleFragment.newInstance(keyword);
        ft.replace(R.id.article_frame, articleFragment);
        ft.commit();

        topBarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        topBarTitle.setText(R.string.activity_search_result);
    }
}
