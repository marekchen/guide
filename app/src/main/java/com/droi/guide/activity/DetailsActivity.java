package com.droi.guide.activity;

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
import com.droi.guide.model.Answer;
import com.droi.guide.model.FavoriteAnswerRelation;
import com.droi.guide.views.UWebView;
import com.droi.sdk.DroiCallback;
import com.droi.sdk.DroiError;
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
    UWebView webview;
    @BindView(R.id.scrollView)
    ScrollView scrollView;

    @BindView(R.id.answer_favorite_image)
    ImageView favoriteImage;
    @BindView(R.id.answer_favorite_text)
    TextView favoriteTv;

    FavoriteAnswerRelation mFavoriteAnswerRelation;
    private static final String TAG = "DetailsActivity";
    public static final String ANSWER = "ANSWER";
    Answer answer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        answer = getIntent().getParcelableExtra(ANSWER);
        Date date = answer.getCreationTime();
        String time = date.getDay() + " " + date.getHours() + ":" + date.getMinutes();
        tvTime.setText(time);

        topBarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        bindView(answer);
        fetchFavoriteRelation(answer.getObjectId());
    }

    private void bindView(Answer answer) {
        tvAuthor.setText(answer.author.getObjectId());
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
        webview.loadData(builder.toString(), "text/html;charset=UTF-8", "UTF-8");
    }

    @OnClick(R.id.answer_comment)
    void answerQuestion() {
        Intent intent = new Intent(DetailsActivity.this, CommentListActivity.class);
        intent.putExtra("answerId", answer.getObjectId());
        intent.putExtra("type", 1);
        startActivity(intent);
    }

    private void fetchFavoriteRelation(String answerId) {
        DroiCondition cond1 = DroiCondition.cond("answerId", DroiCondition.Type.EQ, answerId);
        DroiCondition cond2 = DroiCondition.cond("userId", DroiCondition.Type.EQ, DroiUser.getCurrentUser().getObjectId());
        DroiCondition cond = cond1.and(cond2);
        DroiQuery query = DroiQuery.Builder.newBuilder().query(FavoriteAnswerRelation.class).where(cond).build();
        query.runQueryInBackground(new DroiQueryCallback<FavoriteAnswerRelation>() {
            @Override
            public void result(List<FavoriteAnswerRelation> list, DroiError droiError) {
                if (droiError.isOk()) {
                    if (list.size() == 1) {
                        favoriteTv.setText(getString(R.string.favorite));
                        favoriteImage.setBackgroundResource(R.drawable.favorite_press);
                        mFavoriteAnswerRelation = list.get(0);
                    }
                }
            }
        });
    }

    @OnClick(R.id.answer_favorite)
    void answerFavorite() {
        if (mFavoriteAnswerRelation == null) {
            mFavoriteAnswerRelation = new FavoriteAnswerRelation(answer, DroiUser.getCurrentUser().getObjectId());
            mFavoriteAnswerRelation.fetchInBackground(new DroiCallback<Boolean>() {
                @Override
                public void result(Boolean aBoolean, DroiError droiError) {
                    if (aBoolean) {
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
                        favoriteImage.setBackgroundResource(R.drawable.favorite_normal);
                        favoriteTv.setText(getString(R.string.favorite));
                    }
                }
            });
        }
    }
}
