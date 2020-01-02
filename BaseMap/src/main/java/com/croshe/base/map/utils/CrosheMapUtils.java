package com.croshe.base.map.utils;

import android.content.Context;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.croshe.base.map.listener.OnCrosheLocationListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 地图工具类
 * Created by Janesen on 16/1/28.
 */
public class CrosheMapUtils implements AMapLocationListener {
    private static CrosheMapUtils aMapUtils;

    private static List<CrosheMapUtils> currInstance = new ArrayList<>();
    public static CrosheMapUtils getInstance(Context context) {
        aMapUtils = new CrosheMapUtils();
        aMapUtils.context = context;
        return aMapUtils;
    }

    public static CrosheMapUtils newInstance(Context context) {
        CrosheMapUtils aMapUtils = new CrosheMapUtils();
        aMapUtils.context = context;
        currInstance.add(aMapUtils);
        return aMapUtils;
    }

    private CrosheMapUtils() {
    }

    private Context context;
    private OnCrosheLocationListener onCrosheLocationListener;
    private AMapLocationClient mLocationClient = null;
    public AMapLocationClientOption mLocationOption = null;
    private int locationInterval=2000;
    private boolean isAutoStop=true;
    public int getLocationInterval() {
        return locationInterval;
    }

    public void setLocationInterval(int locationInterval) {
        this.locationInterval = locationInterval;
    }

    public boolean isAutoStop() {
        return isAutoStop;
    }

    public void setAutoStop(boolean autoStop) {
        isAutoStop = autoStop;
    }

    /**
     * 开始定位
     */
    public void startLocation(OnCrosheLocationListener onCrosheLocationListener) {
        this.onCrosheLocationListener = onCrosheLocationListener;
        mLocationClient = new AMapLocationClient(context);
        mLocationClient.setLocationListener(this);
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setNeedAddress(true);
        mLocationOption.setOnceLocation(false);
        mLocationOption.setWifiActiveScan(true);
        mLocationOption.setMockEnable(false);
        mLocationOption.setInterval(locationInterval);
        mLocationClient.setLocationOption(mLocationOption);
        mLocationClient.startLocation();
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        onCrosheLocationListener.onLocation(aMapLocation);
        Locale.setDefault(Locale.CHINA);
        if (isAutoStop) {
            stopLocation();
        }
    }


    /**
     * 停止定位
     */
    public void stopLocation() {
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
    }


    /**
     * 停止所有定位
     */
    public static void stopAllLocation() {
        for (CrosheMapUtils selfMapUtils : currInstance) {
            selfMapUtils.setAutoStop(true);
            selfMapUtils.stopLocation();
        }
    }
}
