package com.droi.guide.model;

import com.droi.sdk.core.DroiExpose;
import com.droi.sdk.core.DroiObject;
import com.droi.sdk.core.DroiReference;

/**
 * Created by chenpei on 16/9/3.
 */
//关注问题
public class FollowQuestionRelation extends DroiObject {
    @DroiExpose
    public String questionId;
    @DroiReference
    public Question question;
    @DroiExpose
    public String followerId;//关注者

    public FollowQuestionRelation(Question question, String followerId) {
        this.questionId = question.getObjectId();
        this.question = question;
        this.followerId = followerId;
    }
}
