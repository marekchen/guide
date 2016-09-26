package com.droi.guide.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.droi.sdk.core.DroiExpose;
import com.droi.sdk.core.DroiFile;
import com.droi.sdk.core.DroiObject;

import java.util.List;

/**
 * Created by chenpei on 16/9/3.
 */
public class OfficialGuideStep extends DroiObject {
    @DroiExpose
    public int position;
    @DroiExpose
    public String title;
    @DroiExpose
    public String content;
    @DroiExpose
    public DroiFile pics;

    public OfficialGuideStep() {

    }
}
