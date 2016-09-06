package com.droi.guide.model;

import com.droi.sdk.core.DroiExpose;
import com.droi.sdk.core.DroiObject;
import com.droi.sdk.core.DroiReference;

/**
 * Created by chenpei on 16/9/3.
 */
public class Comment extends DroiObject {
    @DroiExpose
    public String answerId;
    @DroiExpose
    public String commenterId;
    @DroiReference
    public GuideUser commenter;
    @DroiExpose
    public String comment;
}
