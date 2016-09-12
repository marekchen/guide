package com.droi.guide.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import com.droi.guide.R;
import com.droi.guide.adapter.OfficialGuideStepAdapter;
import com.droi.guide.model.FavoriteGuideRelation;
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

public class OfficialGuideActivity extends AppCompatActivity {

    @BindView(R.id.official_guide_lv)
    ListView listView;
    ArrayList<OfficialGuideStep> mOfficialGuideSteps;
    FavoriteGuideRelation mFavoriteGuideRelation;
    OfficialGuideStepAdapter mStepAdapter;
    OfficialGuide officialGuide;
    @BindView(R.id.official_guide_favorite)
    Button favoriteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official_guide);
        ButterKnife.bind(this);
        OfficialGuide officialGuide = getIntent().getParcelableExtra("officialGudie");
        fetchFavoriteGuideRelation(officialGuide.getObjectId());
        mOfficialGuideSteps = officialGuide.steps;
        mStepAdapter = new OfficialGuideStepAdapter(this, mOfficialGuideSteps);
        listView.setAdapter(mStepAdapter);
        officialGuide = new OfficialGuide();
    }

    private void fetchFavoriteGuideRelation(String guideId) {
        DroiCondition cond1 = DroiCondition.cond("guideId", DroiCondition.Type.EQ, guideId);
        DroiCondition cond2 = DroiCondition.cond("userId", DroiCondition.Type.EQ, DroiUser.getCurrentUser().getObjectId());
        DroiCondition cond = cond1.and(cond2);
        DroiQuery query = DroiQuery.Builder.newBuilder().query(FavoriteGuideRelation.class).where(cond).build();
        query.runQueryInBackground(new DroiQueryCallback<FavoriteGuideRelation>() {
            @Override
            public void result(List<FavoriteGuideRelation> list, DroiError droiError) {
                if (droiError.isOk()) {
                    if (list.size() == 1) {
                        favoriteButton.setText(getString(R.string.favorite));
                        mFavoriteGuideRelation = list.get(0);
                    }
                }
            }
        });
    }

    @OnClick(R.id.official_guide_favorite)
    void addFavorite() {
        if (mFavoriteGuideRelation == null) {
            mFavoriteGuideRelation = new FavoriteGuideRelation(officialGuide, DroiUser.getCurrentUser().getUserId());
            mFavoriteGuideRelation.saveInBackground(new DroiCallback<Boolean>() {
                @Override
                public void result(Boolean aBoolean, DroiError droiError) {
                    if (aBoolean) {
                        favoriteButton.setText(getString(R.string.favoriting));
                        officialGuide.favoriteNum += 1;
                        officialGuide.saveInBackground(null);
                    }
                }
            });
        } else {
            mFavoriteGuideRelation.deleteInBackground(new DroiCallback<Boolean>() {
                @Override
                public void result(Boolean aBoolean, DroiError droiError) {
                    if (aBoolean) {
                        favoriteButton.setText(getString(R.string.favorite));
                        officialGuide.favoriteNum -= 1;
                        officialGuide.saveInBackground(null);
                    }
                }
            });
        }
    }

    @OnClick(R.id.official_guide_comment)
    void toCommentActivity() {
        startActivity(new Intent(this, CommentListActivity.class));
    }
}
