package com.droi.guide.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.droi.guide.R;
import com.droi.guide.adapter.AnswerAdapter;
import com.droi.guide.adapter.OfficialGuideStepAdapter;
import com.droi.guide.model.Answer;
import com.droi.guide.model.Comment;
import com.droi.guide.model.FavoriteRelation;
import com.droi.guide.model.OfficialGuide;
import com.droi.guide.model.OfficialGuideStep;
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
import butterknife.internal.ListenerMethod;

public class OfficialGuideActivity extends AppCompatActivity {

    @BindView(R.id.official_guide_lv)
    ListView listView;
    ArrayList<OfficialGuideStep> mOfficialGuideSteps;
    OfficialGuideStepAdapter mStepAdapter;
    OfficialGuide officialGuide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official_guide);
        ButterKnife.bind(this);
        mStepAdapter = new OfficialGuideStepAdapter(this, mOfficialGuideSteps);
        listView.setAdapter(mStepAdapter);
        officialGuide = new OfficialGuide();
    }

    @OnClick(R.id.official_guide_favorite)
    void addFavorite() {
        FavoriteRelation favoriteRelation = new FavoriteRelation(1, officialGuide.getObjectId(), DroiUser.getCurrentUser().getUserId());
        favoriteRelation.saveInBackground(new DroiCallback<Boolean>() {
            @Override
            public void result(Boolean aBoolean, DroiError droiError) {

            }
        });
        officialGuide.favoriteNum += 1;
        officialGuide.saveInBackground(null);
    }

    @OnClick(R.id.official_guide_comment)
    void toCommentActivity() {
        startActivity(new Intent(this, CommentActivity.class));
    }
}
