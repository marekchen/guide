package com.droi.guide.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.droi.guide.R;
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_answer, parent, false);
            holder = new ViewHolder();
            holder.catorTextView = (TextView) convertView.findViewById(R.id.item_cator);
            holder.titleTextView = (TextView) convertView.findViewById(R.id.item_title);
            holder.briefTextView = (TextView) convertView.findViewById(R.id.item_brief);
            holder.countTextView = (TextView) convertView.findViewById(R.id.item_count);
            //holder.avatar
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        return convertView;
    }

    static class ViewHolder {
        TextView catorTextView;
        TextView titleTextView;
        TextView briefTextView;
        ImageView avatar;
        TextView countTextView;
    }
}

