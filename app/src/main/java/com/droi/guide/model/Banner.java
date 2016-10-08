package com.droi.guide.model;

import android.net.Uri;

import com.droi.sdk.core.DroiFile;
import com.droi.sdk.core.DroiObject;
import com.droi.sdk.core.DroiReference;

/**
 * Created by chenpei on 16/8/31.
 */
public class Banner extends DroiObject {
    @DroiReference
    public DroiFile img;
    @DroiReference
    public Article ref;
    public Uri imgUri;
}
