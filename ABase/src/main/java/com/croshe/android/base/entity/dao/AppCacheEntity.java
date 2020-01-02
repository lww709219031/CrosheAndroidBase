package com.croshe.android.base.entity.dao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * 安徽创息软件科技有限公司-技术支持，http://wwww.croshe.com
 * Created by Janesen on 2017/8/14 12:04.
 */
@Entity
public class AppCacheEntity {
    @Id
    private long cacheId;

    private String cacheTag;

    private String cacheKey;

    private String cacheContent;

    private long validate=-1;//有效时长

    private long time = Integer.MAX_VALUE;//存入的时间 单位毫秒

    @Generated(hash = 1849006252)
    public AppCacheEntity(long cacheId, String cacheTag, String cacheKey,
            String cacheContent, long validate, long time) {
        this.cacheId = cacheId;
        this.cacheTag = cacheTag;
        this.cacheKey = cacheKey;
        this.cacheContent = cacheContent;
        this.validate = validate;
        this.time = time;
    }

    @Generated(hash = 1380468341)
    public AppCacheEntity() {
    }

    public long getCacheId() {
        return this.cacheId;
    }

    public void setCacheId(long cacheId) {
        this.cacheId = cacheId;
    }

    public String getCacheTag() {
        return this.cacheTag;
    }

    public void setCacheTag(String cacheTag) {
        this.cacheTag = cacheTag;
    }

    public String getCacheKey() {
        return this.cacheKey;
    }

    public void setCacheKey(String cacheKey) {
        this.cacheKey = cacheKey;
    }

    public String getCacheContent() {
        return this.cacheContent;
    }

    public void setCacheContent(String cacheContent) {
        this.cacheContent = cacheContent;
    }

    public long getValidate() {
        return validate;
    }

    public void setValidate(long validate) {
        this.validate = validate;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
