package com.droi.guide.activity;

/**
 * Created by marek on 2016/8/22.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.droi.guide.R;
import com.droi.guide.model.DetailEntity;
import com.droi.guide.utils.JsonUtil;
import com.droi.guide.views.UWebView;
import com.droi.sdk.DroiCallback;
import com.droi.sdk.DroiError;
import com.droi.sdk.core.DroiCondition;
import com.droi.sdk.core.DroiQuery;
import com.droi.sdk.core.DroiQueryCallback;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by xiangzhihong on 2016/3/18 on 16:43.
 */
public class DetailsActivity extends AppCompatActivity {

    @BindView(R.id.avatar)
    ImageView ivAvatar;
    @BindView(R.id.article_title)
    TextView tvTitle;
    @BindView(R.id.author)
    TextView tvAuthor;

/*    @BindView(R.id.block_recommenders)
    LinearLayout blockRecommenders;*/

    @BindView(R.id.webview)
    UWebView webview;

    @BindView(R.id.nav_back)
    ImageButton navBack;
    @BindView(R.id.nav_title)
    TextView navTitle;
    @BindView(R.id.scrollView)
    ScrollView scrollView;

    private Context mContext;
    private static final String TAG = "DetailsActivity";
    public static final String ARTCLE = "article";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        mContext = DetailsActivity.this;
        //String articleId = getIntent().getStringExtra(ARTCLE_ID);
        DetailEntity news = getIntent().getParcelableExtra(ARTCLE);
        init();
        bindView(news);
    }

    private void init() {
        navTitle.setText("详情");
        navBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //getDetailData(articleId);
    }

    private void getDetailData(String articleId) {
        /*DroiCondition condition = DroiCondition.cond("_Id", DroiCondition.Type.EQ, articleId);
        DroiQuery droiQuery = DroiQuery.Builder.newBuilder().query(DetailEntity.class).where(condition).build();
        droiQuery.runQueryInBackground(new DroiQueryCallback<DetailEntity>() {
            @Override
            public void result(List<DetailEntity> list, DroiError droiError) {
                if (droiError.isOk()) {
                    Log.i("test", droiError.isOk() + "," + list.size());
                    if (list.size() != 0) {
                        final DetailEntity news = list.get(0);
                        Handler mainThread = new Handler(Looper.getMainLooper());
                        mainThread.post(new Runnable() {
                            @Override
                            public void run() {
                                bindView(news);
                            }
                        });
                    }
                }
            }
        });*/
        /*String url = "http://news-at.zhihu.com/api/4/news/3892357";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String content = response.body().string();
                Log.i("test", content);
                if (content != null) {
                    Handler mainThread = new Handler(Looper.getMainLooper());
                    mainThread.post(new Runnable() {
                        @Override
                        public void run() {
                            bindView(content);
                        }
                    });
                }
            }
        });*/
        /*AsyncHttpClient client = new AsyncHttpClient();
        client.get(mContext, uri, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, String content) {
                super.onSuccess(statusCode, content);
                bindView(content);
            }

            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
            }
        });*/
    }

    private void bindView(DetailEntity news) {
        //DetailEntity news = JsonUtil.getEntity(data, DetailEntity.class);
        //DetailEntity news = new DetailEntity();
        //news.body = data;
        tvTitle.setText(news.title);
        tvAuthor.setText(news.author.userName);
        if (news.author.avatar!=null) {
            news.author.avatar.getInBackground(new DroiCallback<byte[]>() {
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
        /*if (news.recommenders == null) {
            blockRecommenders.setVisibility(View.GONE);
        } else {
            blockRecommenders.removeViews(1, blockRecommenders.getChildCount() - 1);
            for (DetailEntity.Recommender rec : news.recommenders) {
                ImageView avatar = (ImageView) View.inflate(mContext, R.layout.item_recommender, null);
                String avertUri = rec.avatar;
                Picasso.with(mContext).load(avertUri).into(avatar);
                blockRecommenders.addView(avatar);
            }
        }*/

        //build a html content and load it with webview
        String css_url = "http://news-at.zhihu.com/css/news_qa.auto.css?v=4b3e3";
        String css = "";
        css += "<link rel=\"stylesheet\" href=\"" + css_url + "\"/>\n";
        /*for (String css_url : news.css) {
            css += "<link rel=\"stylesheet\" href=" + css_url + ">\n";
        }
        String js = "";
        for (String js_url : news.js) {
            js += "<script src=" + js_url + "/>\n";
        }*/
        String body = news.body.replaceAll("<div class=\"img-place-holder\"></div>", "");

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
}
