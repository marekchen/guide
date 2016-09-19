package com.droi.guide.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.droi.guide.R;
import com.droi.guide.utils.CommonUtils;
import com.droi.sdk.core.DroiExpose;
import com.droi.sdk.core.DroiObject;
import com.droi.sdk.core.DroiReference;

import java.util.Date;

/**
 * Created by chenpei on 16/9/3.
 */
public class Answer extends DroiObject {
    @DroiExpose
    public String questionId;
    @DroiExpose
    public Question question;
    @DroiExpose
    public String authorId;
    @DroiReference
    public GuideUser author;
    @DroiExpose
    public String body;
    @DroiExpose
    public String brief;
    @DroiExpose
    public int favoriteNum = 0;
    @DroiExpose
    public int likeNum = 0;
    @DroiExpose
    public int commentNum = 0;

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(questionId);
        parcel.writeParcelable(question, i);
        parcel.writeString(authorId);
        parcel.writeParcelable(author, i);
        parcel.writeString(body);
        parcel.writeString(brief);
        parcel.writeInt(favoriteNum);
        parcel.writeInt(commentNum);
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
        authorId = in.readString();
        author = in.readParcelable(GuideUser.class.getClassLoader());
        body = in.readString();
        brief = in.readString();
        favoriteNum = in.readInt();
        commentNum = in.readInt();
    }

    public Answer() {

    }

    public String getOtherInfo() {
        StringBuilder sb = new StringBuilder();
        int daysAgo = CommonUtils.getDiscrepantDays(new Date(), getCreationTime());
        if (daysAgo > 0) {
            sb.append(daysAgo + "天前");
            sb.append(" · ");
        }
        sb.append(favoriteNum + "收藏");
        sb.append(" · ");
        sb.append(commentNum + "评论");
        return sb.toString();
    }
}
