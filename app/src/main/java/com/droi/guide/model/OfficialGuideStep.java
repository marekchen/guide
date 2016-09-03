package com.droi.guide.model;

import com.droi.sdk.core.DroiExpose;
import com.droi.sdk.core.DroiFile;
import com.droi.sdk.core.DroiObject;

import java.util.List;

/**
 * Created by chenpei on 16/9/3.
 */
public class OfficialGuideStep extends DroiObject {
    @DroiExpose
    int position;
    @DroiExpose
    String title;
    @DroiExpose
    String content;
    @DroiExpose
    List<DroiFile> pics;
}
