package com.droi.guide.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.droi.guide.R;
import com.droi.guide.model.Answer;
import com.droi.guide.openhelp.BaseRecycleViewAdapter;
import com.droi.sdk.DroiCallback;
import com.droi.sdk.DroiError;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

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
        Log.i("test","onBindItemViewHolder");
        final TextView nameTextView = holder.getView(R.id.item_name);
        final TextView contentTextView = holder.getView(R.id.item_content);
        final TextView bottomTextView = holder.getView(R.id.item_bottom);
        final ImageView avatarImageView = holder.getView(R.id.avatar);
        List<Answer> mAnswers = mList;
        nameTextView.setText(mAnswers.get(position).author.getUserId());
        contentTextView.setText(mAnswers.get(position).brief);
        bottomTextView.setText(mAnswers.get(position).getOtherInfo());
        if (mAnswers.get(position).author.avatar != null) {
            mAnswers.get(position).author.avatar.getUriInBackground(new DroiCallback<Uri>() {
                @Override
                public void result(Uri uri, DroiError droiError) {
                    Picasso.with(mContext).load(uri.getPath()).into(avatarImageView);
                }
            });
        }
    }
}

