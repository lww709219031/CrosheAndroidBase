package com.croshe.base.map.listener;

import com.amap.api.location.AMapLocation;

/**
 * 安徽创息软件科技有限公司-技术支持，http://wwww.croshe.com
 * Created by Janesen on 2017/8/3 21:06.
 */
public interface OnCrosheLocationListener {

    /**
     * 当定位成功
     * @param aMapLocation
     */
    void onLocation(AMapLocation aMapLocation);

}
