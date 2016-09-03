package com.droi.guide.model;

import com.droi.sdk.core.DroiExpose;
import com.droi.sdk.core.DroiObject;

/**
 * Created by chenpei on 16/9/3.
 */
//关注问题
public class FollowQuestionRelation extends DroiObject{
    @DroiExpose
    String questionId;
    @DroiExpose
    String followerId;//关注者

    public FollowQuestionRelation(String questionId, String followerId) {
        this.questionId = questionId;
        this.followerId = followerId;
    }
}
