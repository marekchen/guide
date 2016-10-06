package com.droi.guide.model;

import com.droi.guide.utils.CommonUtils;
import com.droi.sdk.core.DroiExpose;
import com.droi.sdk.core.DroiObject;
import com.droi.sdk.core.DroiReference;

import java.util.ArrayList;
import java.util.Date;

import static android.R.attr.type;

/**
 * Created by chenpei on 16/9/3.
 */
public class Article extends DroiObject {

    public final static int TYPE_ANSWER = 1;
    public final static int TYPE_OFFICIAL_GUIDE = 2;

    @DroiExpose
    public int type; //1:回答、2:官方guide
    @DroiExpose
    public String location; //上海等
    @DroiExpose
    public String category; //1.社会保障，2.教育就业，3.证件管理，4.婚育，5.房屋交通，6.其他
    @DroiExpose
    public int favoriteNum = 0;
    @DroiExpose
    public int commentNum = 0;
    @DroiExpose
    public String brief;
    @DroiExpose
    public String body;

    //回答
    @DroiExpose
    public String questionId;
    @DroiReference
    public Question question;
    @DroiExpose
    public String authorId;
    @DroiReference
    public GuideUser author;

    //官方guide
    @DroiExpose
    public String title;

    public Article() {

    }

    public Article(Question question, GuideUser author, String body, String brief) {
        this.type = 1;
        this.question = question;
        this.questionId = question.getObjectId();
        this.author = author;
        this.authorId = author.getObjectId();
        this.body = body;
        this.brief = brief;
    }

    public Article(String title, String brief, String body) {
        this.type = 2;
        this.title = title;
        this.brief = brief;
        this.body = body;
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
