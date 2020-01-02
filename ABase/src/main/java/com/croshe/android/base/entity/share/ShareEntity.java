package com.croshe.android.base.entity.share;

import android.text.Html;

import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * 安徽创息软件科技有限公司-技术支持，http://wwww.croshe.com
 * Created by Janesen on 2017/8/13 19:45.
 */
public class ShareEntity implements Serializable {
    protected boolean doShare=true;
    protected String title;//标题
    protected String content;//内容
    protected String thumbUrl;//缩略图
    protected String url;

    public String getTitle() {
        if (StringUtils.isNotEmpty(title)) {
            if (title.length() > 20) {
                return title.substring(0, 19) + "……";
            }
        }
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        if (StringUtils.isEmpty(content)) {
            return "";
        }
        if (content!=null&&content.length() > 100) {
            return Html.fromHtml(content.substring(0, 100)).toString();
        }
        return  Html.fromHtml(content).toString();
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public ShareTypeEnum getShareType() {
        if (this instanceof ShareTextEntity) {
            return ShareTypeEnum.TEXT;
        }else if (this instanceof ShareImageEntity) {
            return ShareTypeEnum.IMAGE;
        }else if (this instanceof ShareVoiceEntity) {
            return ShareTypeEnum.VOICE;
        }else if (this instanceof ShareWebEntity) {
            return ShareTypeEnum.WEB;
        }else if (this instanceof ShareVideoEntity) {
            return ShareTypeEnum.VIDEO;
        }
        return ShareTypeEnum.TEXT;
    }

    public boolean isDoShare() {
        return doShare;
    }

    public ShareEntity setDoShare(boolean doShare) {
        this.doShare = doShare;
        return this;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public enum ShareTypeEnum{
        TEXT,
        IMAGE,
        VOICE,
        VIDEO,
        WEB,
    }

}

