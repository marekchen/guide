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
import com.droi.guide.fragment.ArticleFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by marek on 2016/9/27.
 */

public class ArticleActivity extends FragmentActivity {
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
        String category = getIntent().getStringExtra(ArticleFragment.CATEGORY);
        Fragment articleFragment = ArticleFragment.newInstance("", category);
        ft.replace(R.id.article_frame, articleFragment);
        ft.commit();

        topBarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (category.equalsIgnoreCase("social")) {
            topBarTitle.setText(R.string.category_social);
        }
        if (category.equalsIgnoreCase("education")) {
            topBarTitle.setText(R.string.category_education);
        }
        if (category.equalsIgnoreCase("credential")) {
            topBarTitle.setText(R.string.category_credential);
        }
        if (category.equalsIgnoreCase("wedding")) {
            topBarTitle.setText(R.string.category_wedding);
        }
        if (category.equalsIgnoreCase("transport")) {
            topBarTitle.setText(R.string.category_transport);
        }
        if (category.equalsIgnoreCase("other")) {
            topBarTitle.setText(R.string.category_other);
        }

    }
}
