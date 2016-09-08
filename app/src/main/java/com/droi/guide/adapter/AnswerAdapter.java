package com.droi.guide.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.droi.guide.R;
import com.droi.guide.model.Answer;
import com.droi.sdk.DroiCallback;
import com.droi.sdk.DroiError;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * Created by chenpei on 16/9/5.
 */
public class AnswerAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Answer> mAnswers;

    public AnswerAdapter(Context mContext, ArrayList<Answer> mAnswers) {
        this.mAnswers = mAnswers;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mAnswers.size();
    }

    @Override
    public Object getItem(int position) {
        return mAnswers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_answer, parent, false);
            holder = new ViewHolder();
            holder.hotTextView = (TextView) convertView.findViewById(R.id.item_hot);
            holder.titleTextView = (TextView) convertView.findViewById(R.id.item_title);
            holder.briefTextView = (TextView) convertView.findViewById(R.id.item_brief);
            holder.countTextView = (TextView) convertView.findViewById(R.id.item_count);
            holder.avatarImageView = (ImageView) convertView.findViewById(R.id.avatar);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (position == 0) {
            holder.hotTextView.setVisibility(View.VISIBLE);
        } else {
            holder.hotTextView.setVisibility(View.GONE);
        }
        holder.briefTextView.setText(mAnswers.get(position).brief);
        holder.countTextView.setText(mAnswers.get(position).likeNum);
        mAnswers.get(position).author.avatar.getUriInBackground(new DroiCallback<Uri>() {
            @Override
            public void result(Uri uri, DroiError droiError) {
                Picasso.with(mContext).load(uri.getPath()).into(holder.avatarImageView);
            }
        });
        return convertView;
    }

    static class ViewHolder {
        TextView hotTextView;
        TextView titleTextView;
        TextView briefTextView;
        ImageView avatarImageView;
        TextView countTextView;
    }
}

