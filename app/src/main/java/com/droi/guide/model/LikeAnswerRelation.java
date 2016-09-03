package com.droi.guide.model;

import com.droi.sdk.core.DroiExpose;
import com.droi.sdk.core.DroiObject;

/**
 * Created by chenpei on 16/9/3.
 */
//赞 回答
public class LikeAnswerRelation extends DroiObject{
    @DroiExpose
    String answerId;
    @DroiExpose
    String userId;

    public LikeAnswerRelation(String answerId, String userId) {
        this.answerId = answerId;
        this.userId = userId;
    }
}
