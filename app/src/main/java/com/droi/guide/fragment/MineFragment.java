package com.droi.guide.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.droi.guide.R;
import com.droi.guide.activity.LoginActivity;
import com.droi.guide.activity.ProfileActivity;
import com.droi.guide.activity.WriteAnswerActivity;
import com.droi.guide.model.GuideUser;
import com.droi.guide.views.CircleImageView;
import com.droi.sdk.DroiCallback;
import com.droi.sdk.DroiError;
import com.droi.sdk.core.DroiUser;
import com.droi.sdk.feedback.DroiFeedback;
import com.droi.sdk.selfupdate.DroiUpdate;

/**
 * Created by chenpei on 2016/5/12.
 */
public class MineFragment extends Fragment implements View.OnClickListener {
    private static String TAG = "MineFragment";
    private Context mContext;
    private CircleImageView titleImg;
    private TextView nameTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        initUI(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshView();
        //DroiAnalytics.onFragmentStart(getActivity(), "MineFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        //DroiAnalytics.onFragmentEnd(getActivity(), "MineFragment");
    }

    /**
     * 当登录成功或者登出时刷新View
     */
    private void refreshView() {
        GuideUser user = DroiUser.getCurrentUser(GuideUser.class);
        if (user != null && user.isAuthorized() && !user.isAnonymous()) {
            nameTextView.setText(user.getUserId());
            if (user.avatar != null) {
                user.avatar.getInBackground(new DroiCallback<byte[]>() {
                    @Override
                    public void result(byte[] bytes, DroiError error) {
                        if (error.isOk()) {
                            if (bytes == null) {
                                Log.i(TAG, "bytes == null");
                            } else {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                titleImg.setImageBitmap(bitmap);
                            }
                        }
                    }
                }, null);
            }
        } else {
            titleImg.setImageResource(R.drawable.profile_default_icon);
            nameTextView.setText(R.string.fragment_mine_login);
        }
    }

    private void initUI(View view) {
        view.findViewById(R.id.mine_frag_update).setOnClickListener(this);
        view.findViewById(R.id.mine_frag_feedback).setOnClickListener(this);
        view.findViewById(R.id.mine_frag_download).setOnClickListener(this);
        view.findViewById(R.id.mine_frag_upload).setOnClickListener(this);
        view.findViewById(R.id.head_icon).setOnClickListener(this);
        titleImg = (CircleImageView) view.findViewById(R.id.head_icon);
        nameTextView = (TextView) view.findViewById(R.id.user_name);
    }

    @Override
    public void onClick(View v) {
        GuideUser user = DroiUser.getCurrentUser(GuideUser.class);
        switch (v.getId()) {
            case R.id.head_icon:
                if (user != null && user.isAuthorized() && !user.isAnonymous()) {
                    toProfile();
                } else {
                    toLogin();
                }
                break;
            case R.id.mine_frag_update:
                //手动更新
                DroiUpdate.manualUpdate(mContext);
                break;
            case R.id.mine_frag_feedback:
                //如果使用自己的账户系统
                DroiFeedback.setUserId("userId");
                //自定义部分颜色
                DroiFeedback.setTitleBarColor(Color.GREEN);
                DroiFeedback.setSendButtonColor(Color.GREEN, Color.GREEN);
                //打开反馈页面
                DroiFeedback.callFeedback(mContext);
                break;
            case R.id.mine_frag_download:
                toWrite();
                break;
            case R.id.mine_frag_upload:
                Log.i("TEST", "mine_frag_upload");
                //uploadBanner();
                //uploadAppInfo();
                //uploadAppType();
                break;
            default:
                break;
        }
    }

    /**
     * 转到登录页面
     */
    private void toLogin() {
        Intent loginIntent = new Intent(mContext, LoginActivity.class);
        startActivity(loginIntent);
    }

    /**
     * 转到个人信息页面
     */
    private void toProfile() {
        Intent profileIntent = new Intent(mContext, ProfileActivity.class);
        startActivity(profileIntent);
    }

    private void toWrite() {
        GuideUser user = DroiUser.getCurrentUser(GuideUser.class);
        if (user != null && user.isAuthorized() && !user.isAnonymous()) {
            Intent profileIntent = new Intent(mContext, WriteAnswerActivity.class);
            startActivity(profileIntent);
        } else {
            Toast.makeText(this.getContext(), R.string.login_first, Toast.LENGTH_LONG).show();
        }
    }
}
