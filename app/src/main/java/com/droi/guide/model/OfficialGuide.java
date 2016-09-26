package com.droi.guide.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.droi.sdk.core.DroiExpose;
import com.droi.sdk.core.DroiObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenpei on 16/9/3.
 */
public class OfficialGuide extends DroiObject {
    @DroiExpose
    public String location;
    @DroiExpose
    public String category;
    @DroiExpose
    public String title;
    @DroiExpose
    public String brief;
    @DroiExpose
    public ArrayList<OfficialGuideStep> steps;
    @DroiExpose
    public int favoriteNum = 0;
    @DroiExpose
    public int likeNum = 0;
    @DroiExpose
    public int commentNum = 0;

    public OfficialGuide() {

    }
}
