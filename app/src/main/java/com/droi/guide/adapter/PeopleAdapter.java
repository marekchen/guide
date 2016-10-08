package com.droi.guide.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.droi.guide.R;
import com.droi.guide.model.FollowPeopleRelation;
import com.droi.guide.model.GuideUser;
import com.droi.sdk.DroiCallback;
import com.droi.sdk.DroiError;
import com.squareup.picasso.Picasso;

/**
 * Created by marek on 2016/9/26.
 */

public class PeopleAdapter extends BaseRecycleViewAdapter {
    private Context mContext;

    public PeopleAdapter(Context mContext) {
        super(mContext);
        this.mContext = mContext;
    }

    @Override
    public int getItemResource() {
        return R.layout.item_people;
    }

    @Override
    public void onBindItemViewHolder(BaseRecycleViewAdapter.BaseViewHolder holder, int position) {
        final ImageView avatarImageView = holder.getView(R.id.item_avatar);
        final TextView nameTextView = holder.getView(R.id.item_name);
        final TextView followView = holder.getView(R.id.follow_button);

        final FollowPeopleRelation relation = (FollowPeopleRelation) mList.get(position);
        final GuideUser user = relation.user;
        if (user.isAnonymous()) {
            nameTextView.setText("匿名用户" + user.getObjectId().substring(0, 5));
        } else {
            nameTextView.setText(user.getUserId());
        }
        if (relation.isFollowing) {
            followView.setText(R.string.following);
            followView.setBackground(mContext.getResources().getDrawable(R.drawable.btn_following));
            followView.setTextColor(mContext.getResources().getColor(R.color.text_gray));
        }
        followView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!relation.isFollowing) {
                    relation.isFollowing = true;
                    relation.saveInBackground(new DroiCallback<Boolean>() {
                        @Override
                        public void result(Boolean aBoolean, DroiError droiError) {
                            if (aBoolean) {
                                followView.setText(R.string.following);
                                followView.setBackground(mContext.getResources().getDrawable(R.drawable.btn_following));
                                followView.setTextColor(mContext.getResources().getColor(R.color.text_gray));
                            } else {
                                relation.isFollowing = false;
                            }
                        }
                    });
                } else {
                    relation.isFollowing = false;
                    relation.saveInBackground(new DroiCallback<Boolean>() {
                        @Override
                        public void result(Boolean aBoolean, DroiError droiError) {
                            if (aBoolean) {
                                followView.setText(R.string.add_follow);
                                followView.setBackground(mContext.getResources().getDrawable(R.drawable.btn_add_follow));
                                followView.setTextColor(mContext.getResources().getColor(R.color.button_press));
                            } else {
                                relation.isFollowing = true;
                            }
                        }
                    });
                }
            }
        });
        if (user.avatar != null) {
            user.avatar.getUriInBackground(new DroiCallback<Uri>() {
                @Override
                public void result(Uri uri, DroiError droiError) {
                    Picasso.with(mContext).load(uri).into(avatarImageView);
                }
            });
        }
    }
}
