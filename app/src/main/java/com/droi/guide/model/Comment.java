package com.droi.guide.model;

import com.droi.sdk.core.DroiExpose;
import com.droi.sdk.core.DroiObject;
import com.droi.sdk.core.DroiReference;

/**
 * Created by chenpei on 16/9/3.
 */
public class Comment extends DroiObject {
    @DroiExpose
    public String refId;
    @DroiExpose
    public int type;//1:官方攻略；2:回答
    @DroiExpose
    public String commenterId;
    @DroiReference
    public GuideUser commenter;
    @DroiExpose
    public String comment;
    @DroiExpose
    public int likeNum = 0;

    public Comment(String refId, int type, String comment, GuideUser commenter) {
        this.refId = refId;
        this.type = type;
        this.comment = comment;
        this.commenterId = commenter.getObjectId();
        this.commenter = commenter;
    }

    public Comment() {

    }
}
