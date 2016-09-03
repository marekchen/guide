package com.droi.guide.model;

import com.droi.sdk.core.DroiExpose;
import com.droi.sdk.core.DroiObject;

/**
 * Created by chenpei on 16/8/31.
 */
public class Banner extends DroiObject {
    @DroiExpose
    private String id;
    @DroiExpose
    private String appId;
    @DroiExpose
    private String imgUrl;

    public void setId(String id) {
        this.id = id;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getId() {
        return id;
    }

    public String getAppId() {
        return appId;
    }
}
