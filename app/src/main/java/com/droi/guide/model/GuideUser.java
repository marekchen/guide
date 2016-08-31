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

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(avatar,i);
        parcel.writeString(userName);
    }

    @Override
    public int describeContents() {
        return super.describeContents();
    }

    public static final Parcelable.Creator<GuideUser> CREATOR = new Parcelable.Creator<GuideUser>()
    {
        public GuideUser createFromParcel(Parcel in)
        {
            return new GuideUser(in);
        }

        public GuideUser[] newArray(int size)
        {
            return new GuideUser[size];
        }
    };

    private GuideUser(Parcel in)
    {
        avatar = in.readParcelable(DroiFile.class.getClassLoader());
        userName = in.readString();
    }

    public GuideUser(){

    }
}
