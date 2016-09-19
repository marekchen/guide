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
    public String questiontTitle;
    @DroiExpose
    public String questionerId;
    @DroiReference
    public GuideUser questioner;
    @DroiExpose
    public String questionContent;
    @DroiExpose
    public int followNum = 0;
    @DroiExpose
    public int answerNum = 0;
    @DroiExpose
    public Category category;

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(questiontTitle);
        parcel.writeString(questionerId);
        parcel.writeParcelable(questioner, i);
        parcel.writeString(questionContent);
        parcel.writeInt(followNum);
        parcel.writeInt(answerNum);
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
        questiontTitle = in.readString();
        questionerId = in.readString();
        questioner = in.readParcelable(GuideUser.class.getClassLoader());
        questionContent = in.readString();
        followNum = in.readInt();
        answerNum = in.readInt();
    }

    public Question() {

    }

    public Question(String questionTitle, String questionContent, GuideUser questioner) {
        this.questiontTitle = questionTitle;
        this.questionContent = questionContent;
        this.questioner = questioner;
        this.questionerId = questioner.getObjectId();
    }
}
