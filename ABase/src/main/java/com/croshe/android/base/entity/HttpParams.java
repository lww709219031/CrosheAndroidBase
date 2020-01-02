package com.croshe.android.base.entity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;

/**
 * http 请求参数
 * 安徽创息软件科技有限公司-技术支持，http://wwww.croshe.com
 * Created by Janesen on 2017/8/30 11:07.
 */
public class HttpParams {

    private Map<String, Object> params = new HashMap<>();


    public static HttpParams newInstance() {
        return new HttpParams();
    }


    public HttpParams put(String paramName, Object paramValue) {
        params.put(paramName, paramValue);
        return this;
    }


    public HttpParams put(Object entity) {
        String jsonData = new Gson().toJson(entity);
        Map<String, Object> map = new Gson().fromJson(jsonData, new TypeToken<Map<String, Object>>() {
        }.getType());
        params.putAll(map);
        return this;
    }


    public Map<String, Object> toMap() {
        return params;
    }
}
