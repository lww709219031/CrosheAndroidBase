package com.croshe.android.base.entity.dao;

import android.content.SharedPreferences;

import com.alibaba.fastjson.JSON;
import com.croshe.android.base.BaseApplication;
import com.croshe.android.base.utils.BaseAppUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * 安徽创息软件科技有限公司-技术支持，http://wwww.croshe.com
 * Created by Janesen on 2017/8/14 12:39.
 */
public class AppCacheHelper {

    /**
     * 获得缓存
     *
     * @param cacheKey
     * @return
     */
    public static AppCacheEntity getCache(String cacheKey) {
        try {
            if (BaseApplication.daoSession != null) {
                AppCacheEntity appCacheEntity = BaseApplication.daoSession.getAppCacheEntityDao().queryBuilder().where(
                        AppCacheEntityDao.Properties.CacheKey.eq(cacheKey)
                ).limit(1).unique();

                if (appCacheEntity.getValidate() > 0) {
                    long span = System.currentTimeMillis() - appCacheEntity.getTime();
                    if (span > appCacheEntity.getValidate()) {//失效
                        deleteCache(cacheKey);
                        return null;
                    }
                }

                return appCacheEntity;
            }
        } catch (Exception e) {}
        if (BaseAppUtils.memoryDataInfo != null) {//如果数据库不支持，则使用SharedPreferences进行缓存
            String cacheContent = BaseAppUtils.memoryDataInfo.getString(cacheKey, "");
            long time = BaseAppUtils.memoryDataInfo.getLong(cacheKey + "time", 0);
            long validate = BaseAppUtils.memoryDataInfo.getLong(cacheKey + "validate", 0);
            if (validate > 0) {
                long span = System.currentTimeMillis() - time;
                if (span > time) {//失效
                    deleteCache(cacheKey);
                    return null;
                }
            }
            if (StringUtils.isNotEmpty(cacheContent)) {
                AppCacheEntity appCacheEntity = new AppCacheEntity();
                appCacheEntity.setCacheId(System.currentTimeMillis());
                appCacheEntity.setCacheKey(cacheKey);
                appCacheEntity.setCacheContent(cacheContent);
                return appCacheEntity;
            }
        }
        return null;
    }


    /**
     * 设置缓存
     *
     * @param cacheKey
     */
    public static void setCache(String cacheTag, String cacheKey, String cacheContent) {
        setCache(cacheTag, cacheKey, cacheContent, -1);
    }

    /**
     * 设置缓存
     * @param cacheTag
     * @param cacheKey
     * @param cacheContent
     * @param validate 有效时长，单位毫秒
     */
    public static void setCache(String cacheTag, String cacheKey, String cacheContent,long validate) {
        try {
            if (BaseApplication.daoSession != null) {
                deleteCache(cacheKey);
                AppCacheEntity appCacheEntity = new AppCacheEntity();
                appCacheEntity.setCacheId(System.currentTimeMillis());
                appCacheEntity.setCacheKey(cacheKey);
                appCacheEntity.setCacheTag(cacheTag);
                appCacheEntity.setCacheContent(cacheContent);
                appCacheEntity.setTime(System.currentTimeMillis());
                appCacheEntity.setValidate(validate);
                BaseApplication.daoSession.getAppCacheEntityDao().insert(appCacheEntity);
                return;
            }
        } catch (Exception e) {//如果数据库不支持，则使用SharedPreferences进行缓存
        }
        if (BaseAppUtils.memoryDataInfo != null) {
            String cacheTagValues = BaseAppUtils.memoryDataInfo.getString(cacheTag, "[]");
            List<String> keys = JSON.parseArray(cacheTagValues, String.class);
            if (!keys.contains(cacheKey)) {
                keys.add(cacheKey);
            }
            SharedPreferences.Editor editor = BaseAppUtils.memoryDataInfo.edit();
            editor.putString(cacheKey, cacheContent);
            editor.putString(cacheTag, JSON.toJSONString(keys));
            editor.putLong(cacheKey + "validate", validate);
            editor.putLong(cacheKey + "time", System.currentTimeMillis());
            editor.commit();
        }
    }

    /**
     * 删除缓存
     *
     * @param cacheKey
     */
    public static void deleteCache(String cacheKey) {
        try {
            if (BaseApplication.daoSession != null) {
                String sqlStr = "delete from "
                        + AppCacheEntityDao.TABLENAME + " where " +
                        AppCacheEntityDao.Properties.CacheKey.columnName + " = '" + cacheKey + "' ";
                BaseApplication.daoSession.getDatabase().execSQL(sqlStr);
                return;
            }
        } catch (Exception e) {

        }
        if (BaseAppUtils.memoryDataInfo != null) {
            SharedPreferences.Editor editor = BaseAppUtils.memoryDataInfo.edit();
            editor.remove(cacheKey);
            editor.commit();
        }
    }

    /**
     * 删除缓存
     *
     * @param cacheTag
     */
    public static void deleteCacheByTag(String cacheTag) {
        try {
            if (BaseApplication.daoSession != null) {
                String sqlStr = "delete from "
                        + AppCacheEntityDao.TABLENAME + " where " +
                        AppCacheEntityDao.Properties.CacheTag.columnName + " = '" + cacheTag + "' ";
                BaseApplication.daoSession.getDatabase().execSQL(sqlStr);
            }
        } catch (Exception e) {
        }
        if (BaseAppUtils.memoryDataInfo != null) {
            String cacheTagValues = BaseAppUtils.memoryDataInfo.getString(cacheTag, "[]");
            List<String> keys = JSON.parseArray(cacheTagValues, String.class);
            SharedPreferences.Editor editor = BaseAppUtils.memoryDataInfo.edit();
            for (String key : keys) {
                editor.remove(key);
            }
            editor.remove(cacheTag);
            editor.commit();
        }

    }


}
