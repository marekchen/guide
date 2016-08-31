package com.droi.guide.activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.droi.guide.MyApplication;
import com.droi.guide.R;
import com.droi.guide.model.Body;
import com.droi.guide.model.DetailEntity;
import com.droi.guide.model.GuideUser;
import com.droi.guide.qiniu.Config;
import com.droi.guide.qiniu.StringMap;
import com.droi.guide.utils.CommonUtils;
import com.droi.guide.views.RichTextEditor;
import com.droi.sdk.DroiCallback;
import com.droi.sdk.DroiError;
import com.droi.sdk.core.DroiUser;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.droi.guide.views.RichTextEditor.EditData;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by marek on 2016/8/23.
 */
public class WriteArticleActivity extends FragmentActivity {

    String uploadToken;
    private Context mContext;

    private static final int REQUEST_CODE_PICK_IMAGE = 1023;
    private static final int REQUEST_CODE_CAPTURE_CAMEIA = 1022;
    private RichTextEditor editor;
    private View btn1, btn2, btn3;
    private View.OnClickListener btnListener;

    private static final File PHOTO_DIR = new File(
            Environment.getExternalStorageDirectory() + "/DCIM/Camera");
    private File mCurrentPhotoFile;// 照相机拍照得到的图片

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_write_article);
        StringMap sm = new StringMap().put("endUser", "uid").putNotEmpty("returnBody", "");

        uploadToken = MyApplication.auth.uploadToken("marek", null, 3600 * 24 * 60, sm);
        editor = (RichTextEditor) findViewById(R.id.richEditor);
        btnListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                editor.hideKeyBoard();
                if (v.getId() == btn1.getId()) {
                    // 打开系统相册
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");// 相片类型
                    startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
                } else if (v.getId() == btn2.getId()) {
                    // 打开相机
                    openCamera();
                } else if (v.getId() == btn3.getId()) {
                    Log.i("RichEditor", "commit title");
                    List<EditData> editList = editor.buildEditData();
                    // 下面的代码可以上传、或者保存，请自行实现
                    Log.i("RichEditor", "commit title2");
                    String title = editor.getTitle();
                    dealEditData(title, editList);
                }
            }
        };


        btn1 = findViewById(R.id.button1);
        btn2 = findViewById(R.id.button2);
        btn3 = findViewById(R.id.button3);

        btn1.setOnClickListener(btnListener);
        btn2.setOnClickListener(btnListener);
        btn3.setOnClickListener(btnListener);
    }

    /**
     * 负责处理编辑数据提交等事宜，请自行实现
     */
    protected void dealEditData(String title, List<EditData> editList) {
        Log.i("RichEditor", "commit title=" + title);
        StringBuilder sb = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        for (EditData itemData : editList) {
            if (itemData.inputStr != null) {
                sb.append("<p>" + itemData.inputStr + "</p>");
                Log.i("RichEditor", "commit inputStr=" + itemData.inputStr);
                sb2.append(itemData.inputStr);
            } else if (itemData.imagePath != null) {
                sb.append("<img src=\"" + itemData.imagePath + "\"/>");
                Log.i("RichEditor", "commit imgePath=" + itemData.imagePath);
            }
        }
        Log.i("RichEditor", sb.toString());
        DetailEntity article = new DetailEntity();
        article.title = title;
        if (sb2.length()>100) {
            article.brief = sb2.substring(0, 100);
        }else{
            article.brief = sb2.toString();
        }
        article.author = DroiUser.getCurrentUser(GuideUser.class);
        article.body =sb.toString();
        article.saveInBackground(new DroiCallback<Boolean>() {
            @Override
            public void result(Boolean aBoolean, DroiError droiError) {
                Log.i("RichEditor", aBoolean + "|" + droiError.isOk() + "|" + droiError.toString());
            }
        });
    }

    protected void openCamera() {
        try {
            // Launch camera to take photo for selected contact
            PHOTO_DIR.mkdirs();// 创建照片的存储目录
            mCurrentPhotoFile = new File(PHOTO_DIR, getPhotoFileName());// 给新照的照片文件命名
            final Intent intent = getTakePickIntent(mCurrentPhotoFile);
            startActivityForResult(intent, REQUEST_CODE_CAPTURE_CAMEIA);
        } catch (ActivityNotFoundException e) {
        }
    }

    public static Intent getTakePickIntent(File f) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        return intent;
    }

    /**
     * 用当前时间给取得的图片命名
     */
    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "'IMG'_yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date) + ".jpg";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_CODE_PICK_IMAGE) {

            upload(data);

        } else if (requestCode == REQUEST_CODE_CAPTURE_CAMEIA) {
            upload(data);
            insertBitmap(mCurrentPhotoFile.getAbsolutePath(), "");
        }
    }

    /**
     * 添加图片到富文本剪辑器
     *
     * @param imagePath
     */
    private void insertBitmap(String imagePath, String netUrl) {
        editor.insertImage(imagePath, netUrl);
    }

    /**
     * 根据Uri获取图片文件的绝对路径
     */
    public String getRealFilePath(final Uri uri) {
        if (null == uri) {
            return null;
        }

        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = getContentResolver().query(uri,
                    new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

/*    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

*//*        if (resultCode != RESULT_OK) {
            Toast.makeText(mContext, "获取图片失败", Toast.LENGTH_SHORT).show();
            return;
        }

        if (data != null) {
            upload(data);
        } else {
            Toast.makeText(mContext, "上传失败", Toast.LENGTH_SHORT).show();
        }*//*
    }*/

    private void upload(Intent data) {

        final Uri mImageCaptureUri = data.getData();
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
                                    String baseUrl = Config.BASE_URL + res.optString("key");
                                    String url = MyApplication.auth.privateDownloadUrl(baseUrl, 3600 * 24 * 60);
                                    insertBitmap(getRealFilePath(mImageCaptureUri), url);
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
