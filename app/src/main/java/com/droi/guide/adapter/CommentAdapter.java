package com.droi.guide.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.droi.guide.R;
import com.droi.guide.model.Answer;
import com.droi.guide.model.Comment;
import com.droi.guide.openhelp.BaseRecycleViewAdapter;
import com.droi.sdk.DroiCallback;
import com.droi.sdk.DroiError;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by chenpei on 2016/5/11.
 */
public class CommentAdapter extends BaseRecycleViewAdapter {
    private Context mContext;
    private ArrayList<Comment> mComments;

    public CommentAdapter(Context mContext, ArrayList<Comment> mComments) {
        super(mContext);
        this.mComments = mComments;
        this.mContext = mContext;
    }

    @Override
    public int getItemResource() {
        return R.layout.item_comment;
    }

    @Override
    public void onBindItemViewHolder(BaseRecycleViewAdapter.BaseViewHolder holder, int position) {

        final TextView nameTextView = holder.getView(R.id.item_name);
        final TextView timeTextView = holder.getView(R.id.item_time);
        final TextView contentTextView = holder.getView(R.id.item_content);
        final TextView countTextView = holder.getView(R.id.item_like_count);
        final ImageView avatarImageView = holder.getView(R.id.avatar);
        Log.i("test", "01");
        nameTextView.setText(mComments.get(position).commenter.getUserId());
        Log.i("test", "02");
        timeTextView.setText(mComments.get(position).getModifiedTime().toString());
        Log.i("test", "03");
        contentTextView.setText(mComments.get(position).comment);
        Log.i("test", "04");
        countTextView.setText(mComments.get(position).likeNum);
        Log.i("test", "05");
        if (mComments.get(position).commenter.avatar != null) {
            Log.i("test", "06");
            mComments.get(position).commenter.avatar.getUriInBackground(new DroiCallback<Uri>() {
                @Override
                public void result(Uri uri, DroiError droiError) {
                    Picasso.with(mContext).load(uri.getPath()).into(avatarImageView);
                }
            });
        }
    }
}
