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
    public int favoriteNum;

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(location);
        parcel.writeString(category);
        parcel.writeString(title);
        parcel.writeString(brief);
        parcel.writeList(steps);
        parcel.writeInt(favoriteNum);
    }

    @Override
    public int describeContents() {
        return super.describeContents();
    }

    public static final Parcelable.Creator<OfficialGuide> CREATOR = new Parcelable.Creator<OfficialGuide>() {
        public OfficialGuide createFromParcel(Parcel in) {
            return new OfficialGuide(in);
        }

        public OfficialGuide[] newArray(int size) {
            return new OfficialGuide[size];
        }
    };

    private OfficialGuide(Parcel in) {
        location = in.readString();
        category = in.readString();
        title = in.readString();
        brief = in.readString();
        in.readList(steps, OfficialGuideStep.class.getClassLoader());
        favoriteNum = in.readInt();
    }

    public OfficialGuide() {

    }
}
