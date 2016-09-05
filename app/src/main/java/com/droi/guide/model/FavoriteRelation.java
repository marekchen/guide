package com.droi.guide.model;

import com.droi.sdk.core.DroiExpose;
import com.droi.sdk.core.DroiObject;

/**
 * Created by chenpei on 16/9/3.
 */
//收藏
public class FavoriteRelation extends DroiObject {
    @DroiExpose
    public int type;//type=1:Answer;type=2:OfficialGuide
    @DroiExpose
    public String favoriteId;
    @DroiExpose
    public String userId;

    public FavoriteRelation(int type, String favoriteId, String userId) {
        this.type = type;
        this.favoriteId = favoriteId;
        this.userId = userId;
    }
}
