package com.droi.guide.utils;


import com.google.gson.Gson;

public class JsonUtil {
    public static String getJson(Object object) {
        return new Gson().toJson(object);
    }

    public static <T> T getEntity(String json, Class<T> clazz) {
        return new Gson().fromJson(json, clazz);
    }

}
