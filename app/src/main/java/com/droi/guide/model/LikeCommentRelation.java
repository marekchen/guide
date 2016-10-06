package com.droi.guide.model;

import com.droi.sdk.core.DroiExpose;
import com.droi.sdk.core.DroiObject;

/**
 * Created by chenpei on 16/9/3.
 */
//赞 回答
public class LikeCommentRelation extends DroiObject {
    @DroiExpose
    public String commentId;
    @DroiExpose
    public String userId;

    public LikeCommentRelation(String commentId, String userId) {
        this.commentId = commentId;
        this.userId = userId;
    }

    public LikeCommentRelation() {

    }
}
