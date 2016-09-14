package com.droi.guide.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
    public int getCount() {
        return mComments.size();
    }

    @Override
    public Object getItem(int position) {
        return mComments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            //convertView = LayoutInflater.from(mContext).inflate(R.layout.item_app_info_list, parent, false);
            holder = new ViewHolder();

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
/*        final Comment item = mComments.get(position);
        Picasso.with(mContext).load(item.getIcon()).into(holder.iconView);
        holder.appNameView.setText(item.getName());
        holder.ratingBar.setRating(item.getRating());
        holder.installCountView.setText(CommonUtils.formatCont(item.getCount()));
        holder.briefView.setText(item.getBrief());
        holder.sizeView.setText(CommonUtils.formatSize(item.getSize()));*/
        return convertView;
    }

    static class ViewHolder {
        ImageView iconView;
        TextView appNameView;
        RatingBar ratingBar;
        TextView installCountView;
        TextView installButtonView;
        TextView briefView;
        TextView sizeView;
    }

//    private Context mContext;
//    private ArrayList<Answer> mAnswers;
//
//    public AnswerAdapter(Context mContext, ArrayList<Answer> mAnswers) {
//        super(mContext);
//        this.mAnswers = mAnswers;
//        this.mContext = mContext;
//    }
//
    @Override
    public int getItemResource() {
        return R.layout.item_answer;
    }

    @Override
    public void onBindItemViewHolder(BaseRecycleViewAdapter.BaseViewHolder holder, int position) {
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
