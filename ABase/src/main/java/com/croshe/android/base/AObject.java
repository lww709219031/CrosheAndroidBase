package com.croshe.android.base;

import java.util.HashMap;
import java.util.Map;

/**
 * 对象传输类
 * 安徽创息软件科技有限公司-技术支持，http://wwww.croshe.com
 * Created by Janesen on 2018/1/17 14:58.
 */
public class AObject {

    private static Map<String, Object> mapObjects = new HashMap<>();

    public static void putObject(String key, Object object) {
        mapObjects.put(key, object);
    }


    public static <T> T getObject(String key) {
        return (T) mapObjects.get(key);
    }


}
