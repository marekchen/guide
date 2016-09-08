package com.droi.guide;

import android.app.Application;

import com.droi.guide.model.Answer;
import com.droi.guide.model.Banner;
import com.droi.guide.model.Comment;
import com.droi.guide.model.FavoriteAnswerRelation;
import com.droi.guide.model.FollowPeopleRelation;
import com.droi.guide.model.FollowQuestionRelation;
import com.droi.guide.model.GuideUser;
import com.droi.guide.model.LikeAnswerRelation;
import com.droi.guide.model.OfficialGuide;
import com.droi.guide.model.OfficialGuideStep;
import com.droi.guide.model.Question;
import com.droi.guide.qiniu.Auth;
import com.droi.guide.qiniu.Config;
import com.droi.sdk.analytics.DroiAnalytics;
import com.droi.sdk.core.Core;
import com.droi.sdk.core.DroiObject;
import com.droi.sdk.feedback.DroiFeedback;
import com.droi.sdk.push.DroiPush;
import com.droi.sdk.selfupdate.DroiUpdate;


/**
 * Created by chenpei on 2016/5/11.
 */
public class MyApplication extends Application {
    private static final String TAG = "MyApplication";
    public static Auth auth;

    @Override
    public void onCreate() {
        super.onCreate();
        Core.initialize(this);
        auth = Auth.create(Config.ACCESS_KEY,Config.SECRET_KEY);
        DroiObject.registerCustomClass(Answer.class);
        DroiObject.registerCustomClass(Banner.class);
        DroiObject.registerCustomClass(Comment.class);
        DroiObject.registerCustomClass(FavoriteAnswerRelation.class);
        DroiObject.registerCustomClass(FollowPeopleRelation.class);
        DroiObject.registerCustomClass(FollowQuestionRelation.class);
        DroiObject.registerCustomClass(LikeAnswerRelation.class);
        DroiObject.registerCustomClass(OfficialGuide.class);
        DroiObject.registerCustomClass(OfficialGuideStep.class);
        DroiObject.registerCustomClass(Question.class);
        DroiObject.registerCustomClass(GuideUser.class);
        DroiAnalytics.initialize(this);
        DroiUpdate.initialize(this);
        DroiFeedback.initialize(this);
        DroiPush.initialize(this);
 /*
        Log.i(TAG, "Core");
       //初始化
        Core.initialize(this);
        //注册DroiObject
        DroiObject.registerCustomClass(AppInfo.class);
        DroiObject.registerCustomClass(AppType.class);
        DroiObject.registerCustomClass(MyUser.class);
        DroiObject.registerCustomClass(Banner.class);
        DroiObject.registerCustomClass(Add.Request.class);
        DroiObject.registerCustomClass(Add.Response.class);

        Log.i(TAG, "DroiPush");
        //初始化
        DroiPush.initialize(this);
        //设置标签
        DroiPush.addTag(this, new String[]{"test1", "test2"}, false);
        DroiPush.removeTag(this, new String[]{"test2,test3"});
        //设置静默时间为0:30 到 8:00
        DroiPush.setSilentTime(this, 0, 30, 8, 0);

        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                //使用DroiPreference设置是否打开push功能
                boolean enablePush = DroiPreference.instance().getBoolean("push", true);
                DroiPush.setPushEnabled(getApplicationContext(), enablePush);
                //DroiCloudCache
                // 设定数据
                DroiCloudCache.set("keyName", "54678");
                // 由云端取回设定数据
                DroiError error = new DroiError();
                String value = DroiCloudCache.get("keyName", error);
            }
        };
        thread.start();

        //透传消息
        DroiPush.setMessageHandler(new DroiMessageHandler() {
            @Override
            public void onHandleCustomMessage(Context context, String s) {
                //透传消息处理
            }
        });

        Log.i(TAG, "DroiAnalytics");
        //初始化
        DroiAnalytics.initialize(this);
        //方式一:
        DroiAnalytics.enableActivityLifecycleCallbacks(this);
        //默认为true；关闭可以设为false
        DroiAnalytics.setCrashReport(true);
        //设置是否附带log，默认为false；Logger类记录的log才会被上传
        DroiAnalytics.setCrashReportWithLog(true);
        //Logger必须在DroiAnalytics初始化后使用
        Logger.i(TAG, "上传log");
        //发送策略，默认实时
        DroiAnalytics.setDefaultSendPolicy(SendPolicy.SCHEDULE);
        //设置非实时策略下，是否只在wifi下发送以及发送间隔(分钟)
        DroiAnalytics.setScheduleConfig(false, 5);

        Log.i(TAG, "DroiOauth");
        //初始化
        DroiOauth.initialize(this);
        //设置语言
        DroiOauth.setLanguage("zh_CN");

        Log.i(TAG, "DroiUpdate");
        //初始化
        DroiUpdate.initialize(this);
        //是否只在wifi下更新，默认true
        DroiUpdate.setUpdateOnlyWifi(true);
        //UI类型，默认BOTH
        DroiUpdate.setUpdateUIStyle(UpdateUIStyle.STYLE_BOTH);

        Log.i(TAG, "DroiFeedback");
        //初始化
        DroiFeedback.initialize(this);

        //权限设置
        DroiPermission permission = DroiPermission.getDefaultPermission();
        if (permission == null)
            permission = new DroiPermission();
        // 设置默认权限为所有用户可读不可写
        permission.setPublicReadPermission(true);
        permission.setPublicWritePermission(true);
        DroiPermission.setDefaultPermission(permission);

        // 设置目前登录的用户权限是可读可写
        *//*DroiUser user = DroiUser.getCurrentUser();
        permission.setUserReadPermission(user.getObjectId(), true);
        permission.setUserWritePermission(user.getObjectId(), true);*//*

        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG);*/
    }
}
