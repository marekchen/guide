package com.droi.guide.model;

import com.droi.sdk.core.DroiExpose;
import com.droi.sdk.core.DroiObject;
import com.droi.sdk.core.DroiReference;

/**
 * Created by chenpei on 16/9/3.
 */
public class Question extends DroiObject {
    @DroiExpose
    public String questionTitle;
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
    public String location; //上海等
    @DroiExpose
    public String category; //1.社会保障，2.教育就业，3.证件管理，4.婚育，5.房屋交通，6.其他

    public Question() {

    }

    public Question(String questionTitle, String questionContent, GuideUser questioner) {
        this.questionTitle = questionTitle;
        this.questionContent = questionContent;
        this.questioner = questioner;
        this.questionerId = questioner.getObjectId();
    }
}
