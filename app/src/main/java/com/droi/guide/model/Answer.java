package com.droi.guide.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.droi.sdk.core.DroiExpose;
import com.droi.sdk.core.DroiObject;
import com.droi.sdk.core.DroiReference;

/**
 * Created by chenpei on 16/9/3.
 */
public class Answer extends DroiObject {
    @DroiExpose
    public String questionId;
    @DroiExpose
    public Question question;
    @DroiReference
    public GuideUser author;
    @DroiExpose
    public String body;
    @DroiExpose
    public String brief;

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(questionId);
        parcel.writeParcelable(question, i);
        parcel.writeParcelable(author, i);
        parcel.writeString(body);
        parcel.writeString(brief);
    }

    @Override
    public int describeContents() {
        return super.describeContents();
    }

    public static final Parcelable.Creator<Answer> CREATOR = new Parcelable.Creator<Answer>() {
        public Answer createFromParcel(Parcel in) {
            return new Answer(in);
        }

        public Answer[] newArray(int size) {
            return new Answer[size];
        }
    };

    private Answer(Parcel in) {
        questionId = in.readString();
        question = in.readParcelable(Question.class.getClassLoader());
        author = in.readParcelable(GuideUser.class.getClassLoader());
        body = in.readString();
        brief = in.readString();
    }

    public Answer() {

    }
}
