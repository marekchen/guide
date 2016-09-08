package com.droi.guide.model;

import com.droi.sdk.core.DroiExpose;
import com.droi.sdk.core.DroiObject;
import com.droi.sdk.core.DroiReference;

/**
 * Created by chenpei on 16/9/3.
 */
//收藏官方攻略
public class FavoriteGuideRelation extends DroiObject {
    @DroiExpose
    public String guideId;
    @DroiReference
    OfficialGuide officialGuide;
    @DroiExpose
    public String userId;

    public FavoriteGuideRelation(OfficialGuide officialGuide, String userId) {
        this.officialGuide = officialGuide;
        this.guideId = officialGuide.getObjectId();
        this.userId = userId;
    }
}
