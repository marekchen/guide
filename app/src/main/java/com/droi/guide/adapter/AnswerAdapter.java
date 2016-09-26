package com.droi.guide.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.droi.guide.R;
import com.droi.guide.model.Article;
import com.droi.guide.openhelp.BaseRecycleViewAdapter;
import com.droi.sdk.DroiCallback;
import com.droi.sdk.DroiError;
import com.squareup.picasso.Picasso;

/**
 * Created by chenpei on 16/9/5.
 */
public class AnswerAdapter extends BaseRecycleViewAdapter {
    private Context mContext;

    public AnswerAdapter(Context mContext) {
        super(mContext);
        this.mContext = mContext;
    }

    @Override
    public int getItemResource() {
        return R.layout.item_answer;
    }

    @Override
    public void onBindItemViewHolder(BaseViewHolder holder, int position) {
        Log.i("test", "onBindItemViewHolder:" + position);
        final TextView nameTextView = holder.getView(R.id.item_name);
        final TextView contentTextView = holder.getView(R.id.item_content);
        final TextView bottomTextView = holder.getView(R.id.item_bottom);
        final ImageView avatarImageView = holder.getView(R.id.avatar);
        Article answer = (Article) mList.get(position);
        nameTextView.setText(answer.author.getUserId());
        contentTextView.setText(answer.brief);
        bottomTextView.setText(answer.getOtherInfo());
        if (answer.author.avatar != null) {
            answer.author.avatar.getUriInBackground(new DroiCallback<Uri>() {
                @Override
                public void result(Uri uri, DroiError droiError) {
                    Picasso.with(mContext).load(uri.getPath()).into(avatarImageView);
                }
            });
        }
    }
}

