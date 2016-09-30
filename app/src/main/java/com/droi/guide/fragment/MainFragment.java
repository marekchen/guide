package com.droi.guide.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.droi.guide.R;
import com.droi.guide.activity.ArticleActivity;
import com.droi.guide.activity.QuestionListActivity;
import com.droi.guide.activity.SearchActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainFragment extends Fragment {

    public MainFragment() {
    }

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.social)
    public void onSocialPressed() {
        Intent intent = new Intent(this.getActivity(), ArticleActivity.class);
        intent.putExtra(ArticleFragment.CATEGORY, "social");
        startActivity(intent);
    }
    @OnClick(R.id.education)
    public void onEduPressed() {
        Intent intent = new Intent(this.getActivity(), ArticleActivity.class);
        intent.putExtra(ArticleFragment.CATEGORY, "education");
        startActivity(intent);
    }
    @OnClick(R.id.credential)
    public void onCredentialPressed() {
        Intent intent = new Intent(this.getActivity(), ArticleActivity.class);
        intent.putExtra(ArticleFragment.CATEGORY, "credential");
        startActivity(intent);
    }
    @OnClick(R.id.wedding)
    public void onWeddingPressed() {
        Intent intent = new Intent(this.getActivity(), ArticleActivity.class);
        intent.putExtra(ArticleFragment.CATEGORY, "wedding");
        startActivity(intent);
    }
    @OnClick(R.id.transport)
    public void onTrasportPressed() {
        Intent intent = new Intent(this.getActivity(), ArticleActivity.class);
        intent.putExtra(ArticleFragment.CATEGORY, "transport");
        startActivity(intent);
    }
    @OnClick(R.id.other)
    public void onOtherPressed() {
        Intent intent = new Intent(this.getActivity(), ArticleActivity.class);
        intent.putExtra(ArticleFragment.CATEGORY, "other");
        startActivity(intent);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
