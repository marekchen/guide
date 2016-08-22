package com.droi.guide.activity;

/**
 * Created by marek on 2016/8/22.
 */

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.droi.guide.R;
import com.droi.guide.model.DetailEntity;
import com.droi.guide.utils.JsonUtil;
import com.droi.guide.views.UWebView;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by xiangzhihong on 2016/3/18 on 16:43.
 */
public class DetailsActivity extends AppCompatActivity {

    @BindView(R.id.sd_news_img)
    ImageView sdNewsImg;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_img_source)
    TextView tvImgSource;
    @BindView(R.id.block_story_img)
    FrameLayout blockStoryImg;
    @BindView(R.id.block_recommenders)
    LinearLayout blockRecommenders;
    @BindView(R.id.webview)
    UWebView webview;
    @BindView(R.id.nav_back)
    ImageButton navBack;
    @BindView(R.id.nav_title)
    TextView navTitle;
    @BindView(R.id.scrollView)
    ScrollView scrollView;

    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        mContext = DetailsActivity.this;
        init();
    }

    private void init() {
        navTitle.setText("详情");
        navBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getDetailData();
    }

    private void getDetailData() {
        String url = "http://news-at.zhihu.com/api/4/news/3892357";
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
        });
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

    private void bindView(String data) {
        DetailEntity news = JsonUtil.getEntity(data, DetailEntity.class);
        if (!TextUtils.isEmpty(news.image)) {
            Picasso.with(mContext).load(news.image).into(sdNewsImg);
        } else {
            blockStoryImg.setVisibility(View.GONE);
        }
        tvTitle.setText(news.title);
        tvImgSource.setText(news.image_source);
        if (news.recommenders == null) {
            blockRecommenders.setVisibility(View.GONE);
        } else {
            blockRecommenders.removeViews(1, blockRecommenders.getChildCount() - 1);
            for (DetailEntity.Recommender rec : news.recommenders) {
                ImageView avatar = (ImageView) View.inflate(mContext, R.layout.item_recommender, null);
                String avertUri = rec.avatar;
                Picasso.with(mContext).load(avertUri).into(avatar);
                blockRecommenders.addView(avatar);
            }
        }

        //build a html content and load it with webview
        String css = "";
        for (String css_url : news.css) {
            css += "<link rel=\"stylesheet\" href=" + css_url + ">\n";
        }
        String js = "";
        for (String js_url : news.js) {
            js += "<script src=" + js_url + "/>\n";
        }
        String body = news.body.replaceAll("<div class=\"img-place-holder\"></div>", "");

        StringBuilder builder = new StringBuilder();
        builder.append("<html>\n")
                .append("<head>\n")
                .append(css).append(js)
                .append("</head>\n")
                .append("<body>")
                .append(body)
                .append("</body>\n")
                .append("</html>");
        webview.loadData(builder.toString(), "text/html;charset=UTF-8", "UTF-8");
    }
}
