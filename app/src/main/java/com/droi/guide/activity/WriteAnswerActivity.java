package com.droi.guide.activity;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.droi.guide.MyApplication;
import com.droi.guide.R;
import com.droi.guide.model.Article;
import com.droi.guide.model.GuideUser;
import com.droi.guide.model.Question;
import com.droi.guide.qiniu.Config;
import com.droi.guide.qiniu.StringMap;
import com.droi.guide.utils.CommonUtils;
import com.droi.guide.views.RichTextEditor;
import com.droi.sdk.DroiCallback;
import com.droi.sdk.DroiError;
import com.droi.sdk.core.DroiCondition;
import com.droi.sdk.core.DroiQuery;
import com.droi.sdk.core.DroiUser;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.droi.guide.views.RichTextEditor.EditData;

import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.droi.guide.fragment.AnswerFragment.QUESTION;

/**
 * Created by marek on 2016/8/23.
 */
public class WriteAnswerActivity extends FragmentActivity {

    String uploadToken;
    Context mContext;

    private static final int REQUEST_CODE_PICK_IMAGE = 1023;
    private static final int REQUEST_CODE_CAPTURE_CAMEIA = 1022;
    private RichTextEditor editor;
    private View btn1, btn2, btn3;
    private View.OnClickListener btnListener;
    @BindView(R.id.top_bar_title)
    TextView topBarTitle;
    @BindView(R.id.top_bar_back_btn)
    ImageButton topBarBack;

    Question question;

    private static final File PHOTO_DIR = new File(
            Environment.getExternalStorageDirectory() + "/DCIM/Camera");
    private File mCurrentPhotoFile;// 照相机拍照得到的图片

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mContext = this;
        setContentView(R.layout.activity_write_article);
        ButterKnife.bind(this);
        question = getIntent().getParcelableExtra(QUESTION);

        StringMap sm = new StringMap().put("endUser", "uid").putNotEmpty("returnBody", "");
        topBarTitle.setText(getString(R.string.answer_question));
        topBarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
                    dealEditData(editList);
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
    protected void dealEditData(List<EditData> editList) {
        StringBuilder sb = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        for (EditData itemData : editList) {
            if (itemData.inputStr != null) {
                String[] list = itemData.inputStr.split("\n");
                Log.i("test", "size:" + list.length);
                for (String s : list) {
                    if (s.isEmpty()) {
                        Log.i("test", "empty");
                    } else {
                        Log.i("test", "s:" + s);
                        sb.append("<p>" + s + "</p>");
                    }
                }
                Log.i("RichEditor", "commit inputStr=" + itemData.inputStr);
                sb2.append(itemData.inputStr);
            } else if (itemData.imagePath != null) {
                sb.append("<img src=\"" + itemData.imagePath + "\"/>");
                sb2.append("[图片]");
                Log.i("RichEditor", "commit imgePath=" + itemData.imagePath);
            }
        }
        Log.i("RichEditor", sb.toString());
        String brief;
        if (sb2.length() > 100) {
            brief = sb2.substring(0, 150);
        } else {
            brief = sb2.toString();
        }

        Article answer = new Article(question, DroiUser.getCurrentUser(GuideUser.class), sb.toString(), brief);
        answer.saveInBackground(new DroiCallback<Boolean>() {
            @Override
            public void result(Boolean aBoolean, DroiError droiError) {
                Log.i("RichEditor", aBoolean + "|" + droiError.isOk() + "|" + droiError.toString());
                DroiCondition cond = DroiCondition.cond("_Id", DroiCondition.Type.EQ, question.getObjectId());
                DroiQuery.Builder.newBuilder().query(Question.class).where(cond)
                        .inc("answerNum").build().runQueryInBackground(null);
                Toast.makeText(mContext.getApplicationContext(), R.string.send_success, Toast.LENGTH_SHORT);
                finish();
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
