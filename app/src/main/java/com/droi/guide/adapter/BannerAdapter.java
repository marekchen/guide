package com.droi.guide.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.droi.guide.model.Banner;
import com.droi.sdk.DroiCallback;
import com.droi.sdk.DroiError;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by chenpei on 2016/5/11.
 */
public class BannerAdapter extends PagerAdapter {
    private Context mContext;
    private ArrayList<Banner> mBanners;

    public BannerAdapter(Context mContext, ArrayList<Banner> mBanners) {
        this.mContext = mContext;
        this.mBanners = mBanners;
    }

    @Override
    public int getCount() {
        return mBanners.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        final ImageView imageView = new ImageView(mContext);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        if (mBanners.get(position).imgUri == null) {
            mBanners.get(position).img.getUriInBackground(new DroiCallback<Uri>() {
                @Override
                public void result(Uri uri, DroiError droiError) {
                    mBanners.get(position).imgUri = uri;
                    Picasso.with(mContext).load(uri).into(imageView);
                }
            });
        } else {
            Picasso.with(mContext).load(mBanners.get(position).imgUri).into(imageView);
        }
        container.addView(imageView);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
