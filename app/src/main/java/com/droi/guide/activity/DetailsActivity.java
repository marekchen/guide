package com.droi.guide.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.droi.guide.R;
import com.droi.guide.fragment.AnswerFragment;
import com.droi.guide.model.Article;
import com.droi.guide.model.FavoriteRelation;
import com.droi.guide.model.FollowPeopleRelation;
import com.droi.guide.model.GuideUser;
import com.droi.guide.model.Question;
import com.droi.guide.qiniu.Config;
import com.droi.guide.utils.CommonUtils;
import com.droi.guide.views.UWebView;
import com.droi.sdk.DroiCallback;
import com.droi.sdk.DroiError;
import com.droi.sdk.analytics.DroiAnalytics;
import com.droi.sdk.core.DroiCondition;
import com.droi.sdk.core.DroiQuery;
import com.droi.sdk.core.DroiQueryCallback;
import com.droi.sdk.core.DroiUser;

import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by marek on 2016/8/22.
 */

public class DetailsActivity extends AppCompatActivity {

    @BindView(R.id.avatar)
    ImageView ivAvatar;
    @BindView(R.id.author)
    TextView tvAuthor;
    @BindView(R.id.time)
    TextView tvTime;

    @BindView(R.id.top_bar_title)
    TextView topBarTitle;
    @BindView(R.id.top_bar_back_btn)
    ImageButton topBarBack;

    @BindView(R.id.webview)
    UWebView webView;
    @BindView(R.id.scrollView)
    ScrollView scrollView;

    @BindView(R.id.answer_favorite_image)
    ImageView favoriteImage;
    @BindView(R.id.answer_favorite_text)
    TextView favoriteTv;

    @BindView(R.id.follow_button)
    TextView followView;
    @BindView(R.id.answer_title)
    TextView title;

    FavoriteRelation mFavoriteAnswerRelation;
    FollowPeopleRelation mFollowPeopleRelation;

    private static final String TAG = "DetailsActivity";
    public static final String ANSWER = "ANSWER";
    Article answer;
    Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        mContext = this;
        answer = getIntent().getParcelableExtra(ANSWER);
        Date date = answer.getCreationTime();
        String time = CommonUtils.formatDate(date);
        tvTime.setText(time);

        topBarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        topBarTitle.setText(getString(R.string.answer_title));
        bindView(answer);
        fetchFavoriteRelation(answer.getObjectId());
        fetchFollowPeopleRelation();
        if (answer.author.isAnonymous()) {
            followView.setVisibility(View.GONE);
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

    private void bindView(final Article answer) {
        title.setText(answer.question.questionTitle);
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, AnswerListActivity.class);
                intent.putExtra(AnswerFragment.QUESTION, answer.question);
                startActivity(intent);
            }
        });
        if (answer.author.isAnonymous()) {
            tvAuthor.setText("匿名用户" + answer.author.getObjectId().substring(0, 5));
        } else {
            tvAuthor.setText(answer.author.getUserId());
        }
        if (answer.author.avatar != null) {
            answer.author.avatar.getInBackground(new DroiCallback<byte[]>() {
                @Override
                public void result(byte[] bytes, DroiError droiError) {
                    if (droiError.isOk()) {
                        if (bytes == null) {
                            Log.i(TAG, "bytes == null");
                        } else {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            ivAvatar.setImageBitmap(bitmap);
                        }
                    }
                }
            });
        }

        String css_url = "http://news-at.zhihu.com/css/news_qa.auto.css?v=4b3e3";
        String css = "";
        css += "<link rel=\"stylesheet\" href=\"" + css_url + "\"/>\n";
        String body = answer.body.replaceAll("<div class=\"img-place-holder\"></div>", "");
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

    @OnClick(R.id.answer_comment)
    void answerComment() {
        Intent intent = new Intent(DetailsActivity.this, CommentListActivity.class);
        intent.putExtra("answerId", answer.getObjectId());
        intent.putExtra("type", 1);
        startActivity(intent);
    }

    private void fetchFavoriteRelation(String answerId) {
        DroiCondition cond1 = DroiCondition.cond("articleId", DroiCondition.Type.EQ, answerId);
        DroiCondition cond2 = DroiCondition.cond("userId", DroiCondition.Type.EQ, DroiUser.getCurrentUser().getObjectId());
        DroiCondition cond3 = DroiCondition.cond("type", DroiCondition.Type.EQ, Article.TYPE_ANSWER);
        DroiCondition cond = cond1.and(cond2).and(cond3);
        DroiQuery query = DroiQuery.Builder.newBuilder().query(FavoriteRelation.class).where(cond).build();
        query.runQueryInBackground(new DroiQueryCallback<FavoriteRelation>() {
            @Override
            public void result(List<FavoriteRelation> list, DroiError droiError) {
                if (droiError.isOk()) {
                    if (list.size() == 1) {
                        favoriteTv.setText(getString(R.string.favoriting));
                        favoriteImage.setBackgroundResource(R.drawable.favorite_press);
                        mFavoriteAnswerRelation = list.get(0);
                    }
                }
            }
        });
    }

    private void fetchFollowPeopleRelation() {
        DroiCondition cond1 = DroiCondition.cond("followerId", DroiCondition.Type.EQ, DroiUser.getCurrentUser().getObjectId());
        DroiCondition cond2 = DroiCondition.cond("userId", DroiCondition.Type.EQ, answer.author.getObjectId());//

        DroiCondition cond = cond1.and(cond2);
        DroiQuery query = DroiQuery.Builder.newBuilder().query(FollowPeopleRelation.class).where(cond).build();
        query.runQueryInBackground(new DroiQueryCallback<FollowPeopleRelation>() {
            @Override
            public void result(List<FollowPeopleRelation> list, DroiError droiError) {
                if (droiError.isOk()) {
                    if (list.size() == 1) {
                        mFollowPeopleRelation = list.get(0);
                        if (mFollowPeopleRelation.isFollowing) {
                            followView.setText(R.string.following);
                            followView.setBackground(mContext.getResources().getDrawable(R.drawable.btn_following));
                            followView.setTextColor(mContext.getResources().getColor(R.color.text_gray));
                        }
                    }
                }
            }
        });
    }

    @OnClick(R.id.follow_button)
    void follow() {
        if (mFollowPeopleRelation == null || !mFollowPeopleRelation.isFollowing) {
            if (mFollowPeopleRelation == null) {
                mFollowPeopleRelation = new FollowPeopleRelation(GuideUser.getCurrentUser(GuideUser.class), DroiUser.getCurrentUser().getObjectId());
            }
            mFollowPeopleRelation.isFollowing = true;
            mFollowPeopleRelation.saveInBackground(new DroiCallback<Boolean>() {
                @Override
                public void result(Boolean aBoolean, DroiError droiError) {
                    if (aBoolean) {
                        followView.setText(R.string.following);
                        followView.setBackground(mContext.getResources().getDrawable(R.drawable.btn_following));
                        followView.setTextColor(mContext.getResources().getColor(R.color.text_gray));
                    } else {
                        mFollowPeopleRelation.isFollowing = false;
                    }
                }
            });
        } else {
            mFollowPeopleRelation.isFollowing = false;
            mFollowPeopleRelation.saveInBackground(new DroiCallback<Boolean>() {
                @Override
                public void result(Boolean aBoolean, DroiError droiError) {
                    if (aBoolean) {
                        followView.setText(R.string.add_follow);
                        followView.setBackground(mContext.getResources().getDrawable(R.drawable.btn_add_follow));
                        followView.setTextColor(mContext.getResources().getColor(R.color.button_press));
                    } else {
                        mFollowPeopleRelation.isFollowing = true;
                    }
                }
            });
        }
    }

    @OnClick(R.id.answer_favorite)
    void answerFavorite() {
        //这部分需要改用云代码做
        if (mFavoriteAnswerRelation == null) {
            mFavoriteAnswerRelation = new FavoriteRelation(answer, Article.TYPE_ANSWER, DroiUser.getCurrentUser().getObjectId());
            mFavoriteAnswerRelation.saveInBackground(new DroiCallback<Boolean>() {
                @Override
                public void result(Boolean aBoolean, DroiError droiError) {
                    if (aBoolean) {
                        DroiCondition cond = DroiCondition.cond("_Id", DroiCondition.Type.EQ, answer.getObjectId());
                        DroiQuery.Builder.newBuilder().update(Article.class).where(cond)
                                .inc("favoriteNum").build().runInBackground(null);
                        favoriteImage.setBackgroundResource(R.drawable.favorite_press);
                        favoriteTv.setText(getString(R.string.favoriting));
                    }
                }
            });
        } else {
            mFavoriteAnswerRelation.deleteInBackground(new DroiCallback<Boolean>() {
                @Override
                public void result(Boolean aBoolean, DroiError droiError) {
                    if (aBoolean) {
                        DroiCondition cond = DroiCondition.cond("_Id", DroiCondition.Type.EQ, answer.getObjectId());
                        DroiQuery.Builder.newBuilder().update(Article.class).where(cond)
                                .dec("favoriteNum").build().runInBackground(null);
                        favoriteImage.setBackgroundResource(R.drawable.favorite_normal);
                        favoriteTv.setText(getString(R.string.favorite));
                        mFavoriteAnswerRelation = null;
                    }
                }
            });
        }
    }
}
