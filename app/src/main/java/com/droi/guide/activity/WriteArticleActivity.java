package com.droi.guide.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.droi.guide.MyApplication;
import com.droi.guide.R;
import com.droi.guide.utils.CommonUtils;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * Created by marek on 2016/8/23.
 */
public class WriteArticleActivity extends Activity {

    String uploadToken;
    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_article);
        Log.i("test", "onCreate");
        mContext = this;
        uploadToken = MyApplication.auth.uploadToken("marek");
        findViewById(R.id.upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("test", "upload1");
                Intent intent;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                } else {
                    intent = new Intent(Intent.ACTION_GET_CONTENT);
                }
                intent.setType("image/*");
                Log.i("test", "upload2");
                startActivityForResult(intent, 2);
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != RESULT_OK) {
            Toast.makeText(mContext, "获取图片失败", Toast.LENGTH_SHORT).show();
            return;
        }

        if (data != null) {
            upload(data);
        } else {
            Toast.makeText(mContext, "上传失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void upload(Intent data) {
        Uri mImageCaptureUri = data.getData();
        if (mImageCaptureUri != null) {
            Bitmap image;
            try {
                image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageCaptureUri);
                if (image != null) {
                    Configuration config = new Configuration.Builder().build();
                    UploadManager uploadManager = new UploadManager();
                    String path = CommonUtils.getPath(this, mImageCaptureUri);
                    File file = new File(path);
                    String key = null;
                    String token = uploadToken;
                    uploadManager.put(file, key, token,
                            new UpCompletionHandler() {
                                @Override
                                public void complete(String key, ResponseInfo info, JSONObject res) {
                                    //res包含hash、key等信息，具体字段取决于上传策略的设置。
                                    Log.i("qiniu", "token:" + uploadToken);
                                    Log.i("qiniu", "key:" + key);
                                    Log.i("qiniu", "info" + info.toString());
                                    Log.i("qiniu", "res" + res.toString());
                                    String baseUrl = "http://7xoxqq.com1.z0.glb.clouddn.com/"+res.optString("key");
                                    String url = MyApplication.auth.privateDownloadUrl(baseUrl);
                                    Log.i("qiniu", "url" + url);

                                }
                            }, null);
                } else {
                    Toast.makeText(mContext, "上传失败", Toast.LENGTH_SHORT).show();
                    //selectPic.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(mContext, "上传失败", Toast.LENGTH_SHORT).show();
                //selectPic.setVisibility(View.GONE);
            }
        }
    }

}
