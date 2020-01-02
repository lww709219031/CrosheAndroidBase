package com.croshe.android.base.entity.share;

import org.apache.commons.lang3.StringUtils;

/**
 * 安徽创息软件科技有限公司-技术支持，http://wwww.croshe.com
 * Created by Janesen on 2017/8/13 19:46.
 */
public class ShareImageEntity  extends ShareEntity {


    private String imageUrl;//图片的路径


    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    @Override
    public String getThumbUrl() {
        if (StringUtils.isEmpty(thumbUrl)) {
            return getImageUrl();
        }
        return super.getThumbUrl();
    }
}
