package com.droi.guide.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.droi.guide.R;
import com.droi.guide.model.Answer;
import com.droi.guide.model.Question;
import com.droi.guide.openhelp.BaseRecycleViewAdapter;
import com.droi.sdk.DroiCallback;
import com.droi.sdk.DroiError;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by chenpei on 16/9/5.
 */
public class QuestionAdapter extends BaseRecycleViewAdapter {
    private Context mContext;

    public QuestionAdapter(Context mContext) {
        super(mContext);
        this.mContext = mContext;
    }

    @Override
    public int getItemResource() {
        return R.layout.item_question;
    }

    @Override
    public void onBindItemViewHolder(BaseViewHolder holder, int position) {
        Log.i("test", "onBindItemViewHolder");
        final TextView nameTextView = holder.getView(R.id.item_name);
        final TextView contentTextView = holder.getView(R.id.item_content);
        final TextView titleTextView = holder.getView(R.id.item_title);
        final TextView bottomTextView = holder.getView(R.id.item_bottom);
        final ImageView avatarImageView = holder.getView(R.id.avatar);
        List<Question> mQuestions = mList;
        nameTextView.setText(mQuestions.get(position).questioner.getUserId());
        titleTextView.setText(mQuestions.get(position).questiontTitle);
        contentTextView.setText(mQuestions.get(position).questionContent);
        //bottomTextView.setText(mQuestions.get(position).getOtherInfo());
        if (mQuestions.get(position).questioner.avatar != null) {
            mQuestions.get(position).questioner.avatar.getUriInBackground(new DroiCallback<Uri>() {
                @Override
                public void result(Uri uri, DroiError droiError) {
                    Picasso.with(mContext).load(uri.getPath()).into(avatarImageView);
                }
            });
        }
    }
}

