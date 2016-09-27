package com.droi.guide.adapter;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.droi.guide.R;
import com.droi.guide.model.Article;
import com.droi.guide.openhelp.BaseRecycleViewAdapter;

/**
 * Created by chenpei on 16/9/5.
 */
public class ArticleAdapter extends BaseRecycleViewAdapter {
    private Context mContext;

    public ArticleAdapter(Context mContext) {
        super(mContext);
        this.mContext = mContext;
    }

    @Override
    public int getItemResource() {
        return R.layout.item_article;
    }

    @Override
    public void onBindItemViewHolder(BaseViewHolder holder, int position) {
        Log.i("test", "onBindItemViewHolder:" + position);
        final TextView titleTextView = holder.getView(R.id.item_title);
        final TextView briefTextView = holder.getView(R.id.item_brief);
        Article article = (Article) mList.get(position);
        if (article.type == Article.TYPE_ANSWER) {
            titleTextView.setText(article.question.questionTitle);
        } else {
            titleTextView.setText(article.title);
        }
        briefTextView.setText(article.brief);
    }
}

