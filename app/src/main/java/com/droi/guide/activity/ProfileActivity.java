package com.droi.guide.activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.droi.guide.R;
import com.droi.guide.fragment.BindEmailFragment;
import com.droi.guide.model.GuideUser;
import com.droi.guide.utils.CommonUtils;
import com.droi.sdk.DroiCallback;
import com.droi.sdk.DroiError;
//import com.droi.sdk.analytics.DroiAnalytics;
import com.droi.sdk.analytics.DroiAnalytics;
import com.droi.sdk.core.DroiFile;
import com.droi.sdk.core.DroiUser;

import java.io.ByteArrayOutputStream;
import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chenpei on 2016/5/30.
 */
public class ProfileActivity extends Activity implements View.OnClickListener {
    private static String TAG = "ProfileActivity";
    Context mContext;

    @BindView(R.id.change_head_icon)
    LinearLayout changeHeadIcon;
    @BindView(R.id.change_password)
    LinearLayout changePassword;
    @BindView(R.id.change_email)
    LinearLayout changeEmail;
    @BindView(R.id.change_mobile)
    LinearLayout changeMobile;
    @BindView(R.id.profile_logout)
    Button logoutButton;
    @BindView(R.id.user_name)
    TextView userNameText;
    @BindView(R.id.head_icon)
    ImageView headImageView;
    @BindView(R.id.btn_take_photo)
    Button takePhotoButton;
    @BindView(R.id.btn_pick_photo)
    Button pickPhotoButton;
    @BindView(R.id.btn_cancel)
    Button cancelButton;
    @BindView(R.id.pop_layout)
    LinearLayout popLayout;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.select_pic)
    View selectPic;
    @BindView(R.id.top_bar_title)
    TextView topBarTitle;
    @BindView(R.id.top_bar_back_btn)
    ImageButton topBarBack;

    @BindView(R.id.mobile)
    TextView mobileTextView;
    @BindView(R.id.email)
    TextView emailTextView;
    @BindView(R.id.bind_mobile)
    TextView bindMobileTextView;
    @BindView(R.id.bind_email)
    TextView bindEmailTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        mContext = this;
        initUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshView();
        DroiAnalytics.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        DroiAnalytics.onPause(this);
    }

    private void refreshView() {
        GuideUser user = DroiUser.getCurrentUser(GuideUser.class);
        if (user != null && user.isAuthorized() && !user.isAnonymous()) {
            userNameText.setText(user.getUserId());
            if (user.getEmail() != null) {
                emailTextView.setText(user.getEmail());
                emailTextView.setVisibility(View.VISIBLE);
                if (user.isEmailVerified()) {
                    bindEmailTextView.setText(getString(R.string.change));
                } else {
                    bindEmailTextView.setText(getString(R.string.not_verified));
                }
            } else {
                emailTextView.setVisibility(View.GONE);
                bindEmailTextView.setText(getString(R.string.bind));
            }
            if (user.getPhoneNumber() != null) {
                mobileTextView.setText(user.getPhoneNumber());
                mobileTextView.setVisibility(View.VISIBLE);
                if (user.isPhoneNumVerified()) {
                    bindMobileTextView.setText(getString(R.string.change));
                } else {
                    bindMobileTextView.setText(getString(R.string.not_verified));
                }
            } else {
                mobileTextView.setVisibility(View.GONE);
                bindMobileTextView.setText(getString(R.string.bind));
            }
            if (user.avatar != null) {
                user.avatar.getUri();
                user.avatar.getInBackground(new DroiCallback<byte[]>() {
                    @Override
                    public void result(byte[] bytes, DroiError error) {
                        if (error.isOk()) {
                            if (bytes == null) {
                                Log.i(TAG, "bytes == null");
                            } else {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                headImageView.setImageBitmap(bitmap);
                            }
                        }
                    }
                }, null);
            }
        } else {
            headImageView.setImageResource(R.drawable.profile_default_icon);
            userNameText.setText(R.string.fragment_mine_login);
        }
    }

    private void initUI() {
        topBarTitle.setText(getString(R.string.profile));
        logoutButton.setOnClickListener(this);
        changeHeadIcon.setOnClickListener(this);
        changePassword.setOnClickListener(this);
        changeEmail.setOnClickListener(this);
        changeMobile.setOnClickListener(this);
        pickPhotoButton.setOnClickListener(this);
        takePhotoButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        popLayout.setOnClickListener(this);
        topBarBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        DroiUser user = DroiUser.getCurrentUser();
        switch (v.getId()) {
            case R.id.top_bar_back_btn:
                finish();
                break;
            case R.id.change_head_icon:
                selectPic.setVisibility(View.VISIBLE);
                break;
            case R.id.change_password:
                Intent changePasswordIntent = new Intent(this, ChangePasswordActivity.class);
                startActivity(changePasswordIntent);
                break;
            case R.id.change_mobile:
                Intent changeMobileIntent = new Intent(this, BindPhoneNumActivity.class);
                startActivity(changeMobileIntent);
                break;
            case R.id.change_email:
                Intent changeEmailIntent = new Intent(this, BindEmailActivity.class);
                startActivity(changeEmailIntent);
                break;
            case R.id.btn_take_photo:
                try {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 1);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_pick_photo:
                try {
                    Intent intent;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                        intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                    } else {
                        intent = new Intent(Intent.ACTION_GET_CONTENT);
                    }
                    intent.setType("image/*");
                    startActivityForResult(intent, 2);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_cancel:
                selectPic.setVisibility(View.GONE);
                break;
            case R.id.profile_logout:
                //DroiAnalytics.onEvent(this, "logout");
                DroiError droiError;
                if (user != null && user.isAuthorized() && !user.isAnonymous()) {
                    droiError = user.logout();
                    if (droiError.isOk()) {
                        Toast.makeText(this, R.string.logout_success, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, R.string.logout_failed, Toast.LENGTH_SHORT).show();
                    }
                }
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult");
        if (resultCode != RESULT_OK) {
            Log.i(TAG, "resultCode != RESULT_OK");
            Toast.makeText(mContext, "获取图片失败", Toast.LENGTH_SHORT).show();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        popLayout.setVisibility(View.GONE);
        if (data != null) {
            upload(data);
        } else {
            Toast.makeText(mContext, "上传失败", Toast.LENGTH_SHORT).show();
            selectPic.setVisibility(View.GONE);
        }
    }

    private void upload(Intent data) {
        Log.i(TAG, "upload");
        Uri mImageCaptureUri = data.getData();
        if (mImageCaptureUri != null) {
            Log.i(TAG, "pick pic");
            Bitmap image;
            try {
                image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageCaptureUri);
                if (image != null) {
                    String path = CommonUtils.getPath(this, mImageCaptureUri);
                    Log.i("TEST", "path=" + path);
                    DroiFile headIcon = new DroiFile(new File(path));
                    GuideUser user = DroiUser.getCurrentUser(GuideUser.class);
                    user.avatar = headIcon;
                    user.saveInBackground(new DroiCallback<Boolean>() {
                        @Override
                        public void result(Boolean aBoolean, DroiError droiError) {
                            if (aBoolean) {
                                Toast.makeText(mContext, "上传成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(mContext, "上传失败", Toast.LENGTH_SHORT).show();
                            }
                            selectPic.setVisibility(View.GONE);
                        }
                    });
                } else {
                    Toast.makeText(mContext, "上传失败", Toast.LENGTH_SHORT).show();
                    selectPic.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(mContext, "上传失败", Toast.LENGTH_SHORT).show();
                selectPic.setVisibility(View.GONE);
            }
        } else {
            Log.i(TAG, "take photo");
            Bundle extras = data.getExtras();
            if (extras != null) {
                //这里是有些拍照后的图片是直接存放到Bundle中的所以我们可以从这里面获取Bitmap图片
                Bitmap image = extras.getParcelable("data");
                if (image != null) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    byte[] imageBytes = baos.toByteArray();
                    DroiFile headIcon = new DroiFile(imageBytes);
                    GuideUser user = DroiUser.getCurrentUser(GuideUser.class);
                    user.avatar = headIcon;
                    user.saveInBackground(new DroiCallback<Boolean>() {
                        @Override
                        public void result(Boolean aBoolean, DroiError droiError) {
                            if (aBoolean) {
                                Toast.makeText(mContext, "上传成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(mContext, "上传失败", Toast.LENGTH_SHORT).show();
                            }
                            selectPic.setVisibility(View.GONE);
                        }
                    });
                } else {
                    Toast.makeText(mContext, "上传失败", Toast.LENGTH_SHORT).show();
                    selectPic.setVisibility(View.GONE);
                }
            } else {
                Toast.makeText(mContext, "上传失败", Toast.LENGTH_SHORT).show();
                selectPic.setVisibility(View.GONE);
            }
        }
    }
}
