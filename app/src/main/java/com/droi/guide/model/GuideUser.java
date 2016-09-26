package com.droi.guide.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.droi.sdk.core.DroiExpose;
import com.droi.sdk.core.DroiFile;
import com.droi.sdk.core.DroiUser;

/**
 * Created by chenpei on 16/8/31.
 */
public class GuideUser extends DroiUser implements Parcelable{

    @DroiExpose
    public DroiFile avatar;
    @DroiExpose
    public String userName;

    public GuideUser(){

    }
}
