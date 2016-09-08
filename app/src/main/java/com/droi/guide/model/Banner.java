package com.droi.guide.model;

import com.droi.sdk.core.DroiExpose;
import com.droi.sdk.core.DroiObject;

/**
 * Created by chenpei on 16/8/31.
 */
public class Banner extends DroiObject {
    @DroiExpose
    public int type;
    @DroiExpose
    private String refId;
    @DroiExpose
    private String imgUrl;
}
