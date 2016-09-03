package com.droi.guide.model;

import com.droi.sdk.core.DroiExpose;
import com.droi.sdk.core.DroiObject;
import com.droi.sdk.core.DroiReference;

/**
 * Created by chenpei on 16/9/3.
 */
public class Question extends DroiObject {
    @DroiExpose
    String question;
    @DroiReference
    GuideUser user;
    @DroiExpose
    String body;
}
