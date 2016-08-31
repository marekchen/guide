package com.droi.guide.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.droi.sdk.core.DroiExpose;
import com.droi.sdk.core.DroiFile;
import com.droi.sdk.core.DroiObject;
import com.droi.sdk.core.DroiReference;

import java.util.List;

/**
 * Created by xiangzhihong on 2016/3/18 on 16:45.
 */
public class DetailEntity extends DroiObject implements Parcelable{
    @DroiExpose
    public String title;

    @DroiReference
    public GuideUser author;

    @DroiExpose
    public String body;

    @DroiExpose
    public String brief;

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeParcelable(author,i);
        parcel.writeString(body);
        parcel.writeString(brief);
    }

    @Override
    public int describeContents() {
        return super.describeContents();
    }

    public static final Parcelable.Creator<DetailEntity> CREATOR = new Parcelable.Creator<DetailEntity>()
    {
        public DetailEntity createFromParcel(Parcel in)
        {
            return new DetailEntity(in);
        }

        public DetailEntity[] newArray(int size)
        {
            return new DetailEntity[size];
        }
    };

    private DetailEntity(Parcel in)
    {
        title = in.readString();
        author = in.readParcelable(GuideUser.class.getClassLoader());
        body = in.readString();
        brief =in.readString();
    }

    public DetailEntity(){

    }
}
