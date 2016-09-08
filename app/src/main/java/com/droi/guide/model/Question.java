package com.droi.guide.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.droi.sdk.core.DroiExpose;
import com.droi.sdk.core.DroiObject;
import com.droi.sdk.core.DroiReference;

/**
 * Created by chenpei on 16/9/3.
 */
public class Question extends DroiObject {
    @DroiExpose
    public String question;
    @DroiExpose
    public String questionerId;
    @DroiReference
    public GuideUser questioner;
    @DroiExpose
    public String body;
    @DroiExpose
    public int followNum;

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(question);
        parcel.writeString(questionerId);
        parcel.writeParcelable(questioner, i);
        parcel.writeString(body);
        parcel.writeInt(followNum);
    }

    @Override
    public int describeContents() {
        return super.describeContents();
    }

    public static final Parcelable.Creator<Question> CREATOR = new Parcelable.Creator<Question>() {
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        public Question[] newArray(int size) {
            return new Question[size];
        }
    };

    private Question(Parcel in) {
        question = in.readString();
        questionerId = in.readString();
        questioner = in.readParcelable(GuideUser.class.getClassLoader());
        body = in.readString();
        followNum = in.readInt();
    }

    public Question() {

    }
}
