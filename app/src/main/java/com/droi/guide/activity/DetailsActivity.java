package com.droi.guide.activity;

/**
 * Created by marek on 2016/8/22.
 */

import android.content.Context;
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
import com.droi.guide.views.UWebView;
import com.droi.sdk.DroiCallback;
import com.droi.sdk.DroiError;

import butterknife.ButterKnife;
import butterknife.BindView;


/**
 * Created by xiangzhihong on 2016/3/18 on 16:43.
 */
public class DetailsActivity extends AppCompatActivity {

    @BindView(R.id.avatar)
    ImageView ivAvatar;
    @BindView(R.id.author)
    TextView tvAuthor;

    @BindView(R.id.top_bar_title)
    TextView topBarTitle;
    @BindView(R.id.top_bar_back_btn)
    ImageButton topBarBack;

    @BindView(R.id.webview)
    UWebView webview;

    @BindView(R.id.scrollView)
    ScrollView scrollView;

    private Context mContext;
    private static final String TAG = "DetailsActivity";
    public static final String ANSWER = "ANSWER";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        mContext = DetailsActivity.this;
        Answer news = getIntent().getParcelableExtra(ANSWER);
        topBarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        bindView(news);
    }

    private void bindView(Answer answer) {
        //topBarTitle.setText(answer.question.question);
        tvAuthor.setText(answer.author.getUserId());
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


}
