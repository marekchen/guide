package com.droi.guide.model;

import com.droi.sdk.core.DroiExpose;
import com.droi.sdk.core.DroiObject;
import com.droi.sdk.core.DroiReference;

/**
 * Created by chenpei on 16/9/3.
 */
//关注人
public class FollowPeopleRelation extends DroiObject {
    @DroiExpose
    public String userId;//被关注者
    @DroiReference
    public GuideUser user;//被关注者
    @DroiExpose
    public String followerId;//关注者

    public FollowPeopleRelation(GuideUser user, String followerId) {
        this.user = user;
        this.userId = user.getObjectId();
        this.followerId = followerId;
    }
}
