package com.droi.guide.model;

import com.droi.sdk.core.DroiExpose;
import com.droi.sdk.core.DroiObject;

/**
 * Created by chenpei on 16/9/3.
 */
public class FavoriteRelation extends DroiObject{
    @DroiExpose
    int type;
    @DroiExpose
    String favoriteId;
    @DroiExpose
    String userId;

    public FavoriteRelation(int type, String favoriteId, String userId) {
        this.type = type;
        this.favoriteId = favoriteId;
        this.userId = userId;
    }
}
