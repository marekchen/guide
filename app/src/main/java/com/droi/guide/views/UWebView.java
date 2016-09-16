package com.droi.guide.views;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class UWebView extends WebView {

    public UWebView(final Context context, AttributeSet attrs) {
        super(context, attrs);
        getSettings().setJavaScriptEnabled(true);
        getSettings().setDefaultTextEncodingName("UTF-8");
        addJavascriptInterface(new JsInterface(context), "app");
        setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                context.startActivity(intent);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                String js = "javascript:(function () {\n" +
                        "    var objs = document.getElementsByTagName(\"img\");\n" +
                        "    for (var i = 0; i < objs.length; i++) {\n" +
                        "        objs[i].onclick = function () {\n" +
                        "            window.app.displayImg(this.src);\n" +
                        "        }\n" +
                        "    }\n" +
                        "})()";
                view.loadUrl(js);
            }
        });
    }

    private class JsInterface {

        private Context context;

        public JsInterface(Context context) {
            this.context = context;
        }

        @JavascriptInterface
        public void displayImg(String url) {

        }
    }

}
