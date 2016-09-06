package com.droi.guide.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.droi.guide.R;
import com.droi.guide.model.OfficialGuideStep;

import java.util.ArrayList;

/**
 * Created by chenpei on 16/9/5.
 */
public class OfficialGuideStepAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<OfficialGuideStep> mSteps;

    public OfficialGuideStepAdapter(Context mContext, ArrayList<OfficialGuideStep> mSteps) {
        this.mSteps = mSteps;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mSteps.size();
    }

    @Override
    public Object getItem(int position) {
        return mSteps.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_official_guide_step, parent, false);
            holder = new ViewHolder();
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        OfficialGuideStep step = mSteps.get(position);
        holder.stepTitle.setText(step.title);
        holder.stepContent.setText(step.content);
        /*if (step.pics!=null) {
            Picasso.with(mContext).load(item.getIcon()).into(holder.iconView);
        }*/
        return convertView;
    }

    static class ViewHolder {
        TextView stepTitle;
        TextView stepContent;
        ImageView stepPic;
    }
}

