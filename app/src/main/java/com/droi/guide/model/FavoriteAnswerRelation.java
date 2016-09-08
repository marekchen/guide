package com.droi.guide.model;

import com.droi.sdk.core.DroiExpose;
import com.droi.sdk.core.DroiObject;
import com.droi.sdk.core.DroiReference;

/**
 * Created by chenpei on 16/9/3.
 */
//收藏回答
public class FavoriteAnswerRelation extends DroiObject {
    @DroiExpose
    public String answerId;
    @DroiReference
    Answer answer;
    @DroiExpose
    public String userId;

    public FavoriteAnswerRelation(Answer answer, String userId) {
        this.answer = answer;
        this.answerId = answer.getObjectId();
        this.userId = userId;
    }
}
