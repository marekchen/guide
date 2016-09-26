package com.droi.guide.model;

import com.droi.sdk.core.DroiExpose;
import com.droi.sdk.core.DroiObject;
import com.droi.sdk.core.DroiReference;

/**
 * Created by chenpei on 16/9/3.
 */
//收藏回答
public class FavoriteRelation extends DroiObject {
    @DroiExpose
    public String articleId;
    @DroiExpose
    public int type; //1:回答、2:官方guide
    @DroiReference
    public Article article;
    @DroiExpose
    public String userId;

    public FavoriteRelation(Article article, int type, String userId) {
        this.article = article;
        this.articleId = article.getObjectId();
        this.type = type;
        this.userId = userId;
    }

    public FavoriteRelation(){

    }
}
