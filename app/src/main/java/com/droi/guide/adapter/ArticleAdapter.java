package com.droi.guide.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.TextView;

import com.droi.guide.R;
import com.droi.guide.activity.AnswerListActivity;
import com.droi.guide.activity.DetailsActivity;
import com.droi.guide.activity.OfficialGuideActivity;
import com.droi.guide.fragment.AnswerFragment;
import com.droi.guide.model.Article;

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
        final TextView titleTextView = holder.getView(R.id.item_title);
        final TextView briefTextView = holder.getView(R.id.item_brief);
        final Article article = (Article) mList.get(position);
        if (article.type == Article.TYPE_ANSWER) {
            titleTextView.setText(article.question.questionTitle);
        } else {
            titleTextView.setText(article.title);
        }
        briefTextView.setText(article.brief);
        if (article.type == Article.TYPE_ANSWER) {
            holder.itemView.setOnClickListener(null);
            titleTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, AnswerListActivity.class);
                    intent.putExtra(AnswerFragment.QUESTION, article.question);
                    mContext.startActivity(intent);
                }
            });
            briefTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, DetailsActivity.class);
                    intent.putExtra(DetailsActivity.ANSWER, article);
                    mContext.startActivity(intent);

                }
            });
            Bitmap b = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.answer);
            ImageSpan imgSpan = new ImageSpan(mContext, b);
            SpannableString spanString = new SpannableString("icon");
            spanString.setSpan(imgSpan, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            titleTextView.append(spanString);
        } else {
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, OfficialGuideActivity.class);
                    intent.putExtra(OfficialGuideActivity.OFFICIAL, article);
                    mContext.startActivity(intent);
                }
            };
            titleTextView.setOnClickListener(listener);
            briefTextView.setOnClickListener(listener);
            holder.itemView.setOnClickListener(listener);
        }
    }
}

