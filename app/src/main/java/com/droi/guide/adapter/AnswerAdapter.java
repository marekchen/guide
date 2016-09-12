package com.droi.guide.adapter;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.TextView;

import com.droi.guide.R;
import com.droi.guide.model.Answer;
import com.droi.guide.openhelp.BaseRecycleViewAdapter;
import com.droi.sdk.DroiCallback;
import com.droi.sdk.DroiError;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by chenpei on 16/9/5.
 */
public class AnswerAdapter extends BaseRecycleViewAdapter {
    private Context mContext;
    private ArrayList<Answer> mAnswers;

    public AnswerAdapter(Context mContext, ArrayList<Answer> mAnswers) {
        super(mContext);
        this.mAnswers = mAnswers;
        this.mContext = mContext;
    }

    @Override
    public int getItemResource() {
        return R.layout.item_answer;
    }

    @Override
    public void onBindItemViewHolder(BaseViewHolder holder, int position) {
            final TextView hotTextView = holder.getView(R.id.item_hot);
            final TextView titleTextView = holder.getView(R.id.item_title);
            final TextView briefTextView = holder.getView(R.id.item_brief);
            final TextView countTextView = holder.getView(R.id.item_count);
            final ImageView avatarImageView = holder.getView(R.id.avatar);

        briefTextView.setText(mAnswers.get(position).brief);
        countTextView.setText(mAnswers.get(position).likeNum);
        mAnswers.get(position).author.avatar.getUriInBackground(new DroiCallback<Uri>() {
            @Override
            public void result(Uri uri, DroiError droiError) {
                Picasso.with(mContext).load(uri.getPath()).into(avatarImageView);
            }
        });
    }
}

