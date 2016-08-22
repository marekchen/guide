package com.droi.guide.model;

import java.util.List;

/**
 * Created by xiangzhihong on 2016/3/18 on 16:45.
 */
public class DetailEntity {
    public String body;
    public String image_source;
    public String title;
    //may not exist
    public String image;
    public String share_url;
    public List<String> js;
    public String ga_prefix;
    public int type;
    public int id;
    public List<String> css;
    //may not exist
    public List<Recommender> recommenders;

    public class Recommender {
       public String avatar;
    }
}
