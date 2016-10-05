package com.droi.guide.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.droi.guide.R;
import com.droi.guide.model.Article;
import com.droi.guide.model.FavoriteRelation;
import com.droi.sdk.DroiCallback;
import com.droi.sdk.DroiError;
import com.droi.sdk.analytics.DroiAnalytics;
import com.droi.sdk.core.DroiCondition;
import com.droi.sdk.core.DroiQuery;
import com.droi.sdk.core.DroiQueryCallback;
import com.droi.sdk.core.DroiUser;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OfficialGuideActivity extends AppCompatActivity {

    @BindView(R.id.webview)
    WebView webView;
    FavoriteRelation mFavoriteGuideRelation;
    Article officialGuide;
    @BindView(R.id.official_guide_favorite)
    LinearLayout favoriteButton;
    @BindView(R.id.favorite_image)
    ImageView favoriteImage;
    @BindView(R.id.favorite_text)
    TextView favoriteTv;

    @BindView(R.id.top_bar_title)
    TextView topBarTitle;
    @BindView(R.id.top_bar_back_btn)
    ImageButton topBarBack;

    @BindView(R.id.official_guide_title)
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official_guide);
        ButterKnife.bind(this);
        officialGuide = getIntent().getParcelableExtra("officialGuide");
        fetchFavoriteGuideRelation(officialGuide.getObjectId());
        bindView(officialGuide);
        topBarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        topBarTitle.setText(getString(R.string.official_title));
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

    private void bindView(Article officialGuide) {
        title.setText(officialGuide.title);
        String css_url = "http://news-at.zhihu.com/css/news_qa.auto.css?v=4b3e3";
        String css = "";
        css += "<link rel=\"stylesheet\" href=\"" + css_url + "\"/>\n";
        String body = officialGuide.body.replaceAll("<div class=\"img-place-holder\"></div>", "");
        StringBuilder builder = new StringBuilder();
        builder.append("<html>\n")
                .append("<head>\n")
                .append(css)
                //.append(js)
                .append("</head>\n")
                .append("<body>")
                .append("<div class=\"main-wrap content-wrap\"><div class=\"content\">")
                .append(body)
                .append("</div></div>")
                .append("</body>\n")
                .append("</html>");
        Log.i("test", builder.toString());
        webView.loadData(builder.toString(), "text/html;charset=UTF-8", "UTF-8");
    }

    private void fetchFavoriteGuideRelation(String guideId) {
        DroiCondition cond1 = DroiCondition.cond("articleId", DroiCondition.Type.EQ, guideId);
        DroiCondition cond2 = DroiCondition.cond("userId", DroiCondition.Type.EQ, DroiUser.getCurrentUser().getObjectId());
        DroiCondition cond3 = DroiCondition.cond("type", DroiCondition.Type.EQ, Article.TYPE_OFFICIAL_GUIDE);
        DroiCondition cond = cond1.and(cond2).and(cond3);
        DroiQuery query = DroiQuery.Builder.newBuilder().query(FavoriteRelation.class).where(cond).build();
        query.runQueryInBackground(new DroiQueryCallback<FavoriteRelation>() {
            @Override
            public void result(List<FavoriteRelation> list, DroiError droiError) {
                if (droiError.isOk()) {
                    if (list.size() == 1) {
                        favoriteTv.setText(getString(R.string.favoriting));
                        favoriteImage.setBackgroundResource(R.drawable.favorite_press);
                        mFavoriteGuideRelation = list.get(0);
                    }
                }
            }
        });
    }

    @OnClick(R.id.official_guide_favorite)
    void addFavorite() {
        if (mFavoriteGuideRelation == null) {
            mFavoriteGuideRelation = new FavoriteRelation(officialGuide, Article.TYPE_OFFICIAL_GUIDE, DroiUser.getCurrentUser().getObjectId());
            mFavoriteGuideRelation.saveInBackground(new DroiCallback<Boolean>() {
                @Override
                public void result(Boolean aBoolean, DroiError droiError) {
                    if (aBoolean) {
                        DroiCondition cond = DroiCondition.cond("_Id", DroiCondition.Type.EQ, officialGuide.getObjectId());
                        DroiQuery.Builder.newBuilder().update(Article.class).where(cond)
                                .inc("favoriteNum").build().runInBackground(null);
                        favoriteTv.setText(getString(R.string.favoriting));
                        favoriteImage.setBackgroundResource(R.drawable.favorite_press);
                    }
                }
            });
        } else {
            mFavoriteGuideRelation.deleteInBackground(new DroiCallback<Boolean>() {
                @Override
                public void result(Boolean aBoolean, DroiError droiError) {
                    if (aBoolean) {
                        DroiCondition cond = DroiCondition.cond("_Id", DroiCondition.Type.EQ, officialGuide.getObjectId());
                        DroiQuery.Builder.newBuilder().update(Article.class).where(cond)
                                .dec("favoriteNum").build().runInBackground(null);
                        favoriteTv.setText(getString(R.string.favorite));
                        favoriteImage.setBackgroundResource(R.drawable.favorite_normal);
                        mFavoriteGuideRelation = null;
                    }
                }
            });
        }
    }

    @OnClick(R.id.official_guide_comment)
    void toCommentActivity() {
        Intent intent = new Intent(OfficialGuideActivity.this, CommentListActivity.class);
        intent.putExtra("officialId", officialGuide.getObjectId());
        intent.putExtra("type", 2);
        startActivity(intent);
    }
}
