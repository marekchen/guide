package com.droi.guide.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.droi.guide.model.Answer;

import java.util.ArrayList;

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
}

