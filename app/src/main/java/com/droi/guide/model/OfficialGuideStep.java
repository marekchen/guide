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

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(position);
        parcel.writeString(title);
        parcel.writeString(content);
        parcel.writeParcelable(pics, i);
    }

    @Override
    public int describeContents() {
        return super.describeContents();
    }

    public static final Parcelable.Creator<OfficialGuideStep> CREATOR = new Parcelable.Creator<OfficialGuideStep>() {
        public OfficialGuideStep createFromParcel(Parcel in) {
            return new OfficialGuideStep(in);
        }

        public OfficialGuideStep[] newArray(int size) {
            return new OfficialGuideStep[size];
        }
    };

    private OfficialGuideStep(Parcel in) {
        position = in.readInt();
        title = in.readString();
        content = in.readString();
        pics = in.readParcelable(DroiFile.class.getClassLoader());
    }

    public OfficialGuideStep() {

    }
}
