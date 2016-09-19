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

    public CommentAdapter(Context mContext) {
        super(mContext);
        this.mContext = mContext;
    }

    @Override
    public int getItemResource() {
        return R.layout.item_comment;
    }

    @Override
    public void onBindItemViewHolder(BaseRecycleViewAdapter.BaseViewHolder holder, int position) {
        Log.i("test", "comment");
        final TextView nameTextView = holder.getView(R.id.item_name);
        final TextView timeTextView = holder.getView(R.id.item_time);
        final TextView contentTextView = holder.getView(R.id.item_content);
        //final TextView countTextView = holder.getView(R.id.item_like_count);
        final ImageView avatarImageView = holder.getView(R.id.avatar);
        Comment comment = (Comment) mList.get(position);
        nameTextView.setText(comment.commenter.getUserId());
        timeTextView.setText(comment.getModifiedTime().toString());
        contentTextView.setText(comment.comment);
        //countTextView.setText(comment.likeNum);
        if (comment.commenter.avatar != null) {
            comment.commenter.avatar.getUriInBackground(new DroiCallback<Uri>() {
                @Override
                public void result(Uri uri, DroiError droiError) {
                    Picasso.with(mContext).load(uri.getPath()).into(avatarImageView);
                }
            });
        }
    }
}
