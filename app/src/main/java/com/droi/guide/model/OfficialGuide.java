package com.droi.guide.model;

import com.droi.sdk.core.DroiExpose;
import com.droi.sdk.core.DroiObject;

import java.util.List;

/**
 * Created by chenpei on 16/9/3.
 */
public class OfficialGuide extends DroiObject{
    @DroiExpose
    String location;
    @DroiExpose
    String title;
    @DroiExpose
    String brief;
    @DroiExpose
    List<OfficialGuideStep> steps;
}
