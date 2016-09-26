package com.droi.guide.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.droi.guide.R;
import com.droi.guide.model.Comment;
import com.droi.guide.openhelp.BaseRecycleViewAdapter;
import com.droi.guide.utils.CommonUtils;
import com.droi.sdk.DroiCallback;
import com.droi.sdk.DroiError;
import com.squareup.picasso.Picasso;

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
        TextView nameTextView = holder.getView(R.id.item_name);
        TextView timeTextView = holder.getView(R.id.item_time);
        TextView contentTextView = holder.getView(R.id.item_content);
        final ImageView likeImageView = holder.getView(R.id.item_like_icon);
        LinearLayout likeLayout = holder.getView(R.id.item_like);
        TextView countTextView = holder.getView(R.id.item_like_count);
        final ImageView avatarImageView = holder.getView(R.id.avatar);

        Comment comment = (Comment) mList.get(position);
        nameTextView.setText(comment.commenter.getUserId());
        timeTextView.setText(CommonUtils.formatDate(comment.getModifiedTime()));
        contentTextView.setText(comment.comment);
        //fetchFollowQuestionRelation();
        likeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likeImageView.setBackgroundResource(R.drawable.like_press);
            }
        });
        countTextView.setText("" + comment.likeNum);
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
