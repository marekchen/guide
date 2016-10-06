package com.droi.guide;

import android.app.Application;

import com.droi.guide.model.Article;
import com.droi.guide.model.Banner;
import com.droi.guide.model.Comment;
import com.droi.guide.model.FavoriteRelation;
import com.droi.guide.model.FollowPeopleRelation;
import com.droi.guide.model.FollowQuestionRelation;
import com.droi.guide.model.GuideUser;
import com.droi.guide.model.LikeCommentRelation;
import com.droi.guide.model.Question;
import com.droi.guide.qiniu.Auth;
import com.droi.guide.qiniu.Config;
import com.droi.sdk.analytics.DroiAnalytics;
import com.droi.sdk.core.Core;
import com.droi.sdk.core.DroiObject;
import com.droi.sdk.core.DroiPermission;
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

        auth = Auth.create(Config.ACCESS_KEY, Config.SECRET_KEY);
        DroiObject.registerCustomClass(Article.class);
        DroiObject.registerCustomClass(Banner.class);
        DroiObject.registerCustomClass(Comment.class);
        DroiObject.registerCustomClass(FavoriteRelation.class);
        DroiObject.registerCustomClass(FavoriteRelation.class);
        DroiObject.registerCustomClass(FollowPeopleRelation.class);
        DroiObject.registerCustomClass(FollowQuestionRelation.class);
        DroiObject.registerCustomClass(GuideUser.class);
        DroiObject.registerCustomClass(LikeCommentRelation.class);
        DroiObject.registerCustomClass(OfficialGuideStep.class);
        DroiObject.registerCustomClass(Question.class);

        Core.initialize(this);
        DroiAnalytics.initialize(this);
        DroiUpdate.initialize(this);
        DroiFeedback.initialize(this);
        DroiPush.initialize(this);

        Core.initialize(this);
        DroiAnalytics.initialize(this);
        DroiUpdate.initialize(this);
        DroiFeedback.initialize(this);
        DroiPush.initialize(this);

        DroiPermission permission = DroiPermission.getDefaultPermission();
        if (permission == null)
            permission = new DroiPermission();
        // 设置默认权限为所有用户可读不可写
        permission.setPublicReadPermission(true);
        permission.setPublicWritePermission(true);
        DroiPermission.setDefaultPermission(permission);
    }
}
