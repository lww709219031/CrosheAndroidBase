package com.croshe.android.base;

import com.alibaba.fastjson.JSON;
import com.croshe.android.base.entity.dao.AppCacheEntity;
import com.croshe.android.base.entity.dao.AppCacheHelper;
import com.croshe.android.base.utils.MD5Encrypt;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 数据持久化记录工具类
 * 安徽创息软件科技有限公司-技术支持，http://wwww.croshe.com
 * Created by Janesen on 2017/11/16 20:59.
 */
public class ARecord {
    private static Map<String, ARecord> instance = new HashMap<>();

    private String dataKey;


    /**
     * 获得数据记录对象
     *
     * @param dataKey 数据标识key
     * @return
     */
    public static ARecord get(String dataKey) {
        if (!instance.containsKey(dataKey)) {
            instance.put(dataKey, new ARecord().setDataKey(dataKey));
        }
        return instance.get(dataKey);
    }

    private ARecord() {
    }

    private String getAttrKey(String attr) {
        return MD5Encrypt.MD5(dataKey + attr);
    }

    private void setData(String attr, Object value) {
        setData(attr, value, -1);
    }

    private void setData(String attr, Object value, long validate) {
        if (value != null) {
            AppCacheHelper.setCache(dataKey, getAttrKey(attr), value.toString(), validate);
        }else{
            AppCacheHelper.deleteCache(getAttrKey(attr));
        }
    }

    private void deleteData(String tag) {
        AppCacheHelper.deleteCacheByTag(tag);
    }

    private Object getData(String attr, Object defaultValue) {
        try {
            AppCacheEntity cacheData = AppCacheHelper.getCache(getAttrKey(attr));
            if (cacheData != null && cacheData.getCacheContent() != null) {
                return cacheData.getCacheContent();
            }
        } catch (Exception e) {
        }
        return defaultValue;
    }


    private ARecord setDataKey(String dataKey) {
        this.dataKey = dataKey;
        return this;
    }


    /**
     * 设置默认值
     *
     * @param value
     * @param <T>
     * @return
     */
    public <T> ARecord setDefault(T value) {
        setData(MD5Encrypt.MD5("default"), new Gson().toJson(value));
        return this;
    }

    /**
     * 获的默认值
     *
     * @param targetClass
     * @param <T>
     * @return
     */
    public <T> T getDefault(Class<T> targetClass) {
        return getObject(MD5Encrypt.MD5("default"), targetClass);
    }


    /**
     * 删除默认值
     *
     * @return
     */
    public ARecord removeDefault() {
        setData(MD5Encrypt.MD5("default"), null);
        return this;
    }

    /**
     * 删除所有数据
     */
    public ARecord removeAll() {
        deleteData(dataKey);
        return this;
    }


    /**
     * 删除属性
     *
     * @param attr
     * @return
     */
    public ARecord removeAttr(String attr) {
        setData(attr, null);
        return this;
    }

    public ARecord setAttr(String attr, String value) {
        setData(attr, value);
        return this;
    }

    public ARecord setAttr(String attr, String value, long validate) {
        setData(attr, value, validate);
        return this;
    }

    public ARecord setAttr(String attr, double value) {
        setData(attr, value);
        return this;
    }

    public ARecord setAttr(String attr, boolean value) {
        setData(attr, value);
        return this;
    }

    public ARecord setAttr(String attr, boolean value, long validate) {
        setData(attr, value, validate);
        return this;
    }

    public ARecord setAttr(String attr, float value) {
        setData(attr, value);
        return this;
    }

    public ARecord setAttr(String attr, float value, long validate) {
        setData(attr, value, validate);
        return this;
    }


    public ARecord setAttr(String attr, long value) {
        setData(attr, value);
        return this;
    }


    public ARecord setAttr(String attr, long value, long validate) {
        setData(attr, value, validate);
        return this;
    }

    public ARecord setAttr(String attr, int value) {
        setData(attr, value);
        return this;
    }

    public ARecord setAttr(String attr, int value, long validate) {
        setData(attr, value, validate);
        return this;
    }

    public <T> ARecord setAttr(String attr, List<T> list) {
        setData(attr, new Gson().toJson(list));
        return this;
    }


    public <T> ARecord setAttr(String attr, T target) {
        setData(attr, new Gson().toJson(target));
        return this;
    }


    public <T> ARecord setAttr(String attr, List<T> list, long validate) {
        setData(attr, new Gson().toJson(list), validate);
        return this;
    }


    public <T> ARecord setAttr(String attr, Set<T> set) {
        setData(attr, new Gson().toJson(set));
        return this;
    }

    public <T> ARecord setAttr(String attr, Set<T> set, long validate) {
        setData(attr, new Gson().toJson(set), validate);
        return this;
    }

    public String getString(String attr, String defaultValue) {
        return getData(attr, defaultValue).toString();
    }

    public String getString(String attr) {
        return getString(attr, null);
    }


    public boolean getBoolean(String attr, boolean defaultValue) {
        return Boolean.parseBoolean(getData(attr, defaultValue).toString());
    }

    public boolean getBoolean(String attr) {
        return getBoolean(attr, false);
    }


    public int getInt(String attr, int defaultValue) {
        return Integer.parseInt(getData(attr, defaultValue).toString());
    }

    public int getInt(String attr) {
        return getInt(attr, 0);
    }


    public double getDouble(String attr, double defaultValue) {
        return Double.parseDouble(getData(attr, defaultValue).toString());
    }

    public double getDouble(String attr) {
        return getDouble(attr, 0);
    }


    public float getFloat(String attr, float defaultValue) {
        return Float.parseFloat(getData(attr, defaultValue).toString());
    }

    public float getFloat(String attr) {
        return getFloat(attr, 0);
    }


    public long getLong(String attr, long defaultValue) {
        return Long.parseLong(getData(attr, defaultValue).toString());
    }

    public long getLong(String attr) {
        return getLong(attr, 0);
    }


    public <T> List<T> getList(String attr, Class<T> targetClass) {
        String jsonData = getData(attr, "[]").toString();
        return JSON.parseArray(jsonData, targetClass);
    }


    public <T> Set<T> getSet(String attr, Class<T> targetClass) {
        String jsonData = getData(attr, "[]").toString();
        return JSON.parseObject(jsonData, Set.class);
    }

    /**
     * 获得对象，如果不存在，则重新创建一个对象
     * @param attr
     * @param targetClass
     * @param <T>
     * @return
     */
    public <T> T getObject(String attr, Class<T> targetClass) {
        String jsonData = getString(attr, "{}");
        return JSON.parseObject(jsonData, targetClass);
    }

    /**
     * 获得对象，如果不存在，则返回null
     * @param attr
     * @param targetClass
     * @param <T>
     * @return
     */
    public <T> T getObjectNull(String attr, Class<T> targetClass) {
        String jsonData = getString(attr, "");
        if (jsonData.length() == 0) {
            return null;
        }
        return JSON.parseObject(jsonData, targetClass);
    }



    /**
     * 清除所有属性值
     */
    public void clear() {
        AppCacheHelper.deleteCacheByTag(dataKey);
    }

}
