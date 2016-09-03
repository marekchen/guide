package com.droi.guide.model;

import com.droi.sdk.core.DroiExpose;
import com.droi.sdk.core.DroiObject;

/**
 * Created by chenpei on 16/9/3.
 */
//关注人
public class FollowPeopleRelation extends DroiObject{
    @DroiExpose
    String userId;//被关注者
    @DroiExpose
    String followerId;//关注者

    public FollowPeopleRelation(String userId, String followerId) {
        this.userId = userId;
        this.followerId = followerId;
    }
}
