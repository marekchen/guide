package com.droi.guide.model;

import com.droi.sdk.core.DroiExpose;
import com.droi.sdk.core.DroiObject;

import java.util.List;

/**
 * Created by chenpei on 16/9/3.
 */
public class OfficialGuide extends DroiObject{
    @DroiExpose
    public String location;
    @DroiExpose
    public String category;
    @DroiExpose
    public String title;
    @DroiExpose
    public String brief;
    @DroiExpose
    public List<OfficialGuideStep> steps;
}
