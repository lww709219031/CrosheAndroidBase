package com.croshe.base.map;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.amap.api.location.AMapLocation;
import com.croshe.android.base.AConfig;
import com.croshe.android.base.entity.BaseLocationEntity;
import com.croshe.android.base.utils.BaseAppUtils;
import com.croshe.android.base.utils.NumberUtils;
import com.croshe.base.map.activity.CrosheMapSelAddressActivity;
import com.croshe.base.map.activity.CrosheMapShowAddressActivity;
import com.croshe.base.map.listener.OnCrosheLocationListener;
import com.croshe.base.map.utils.CrosheMapUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Map;

/**
 * 地图初始化
 * 安徽创息软件科技有限公司-技术支持，http://wwww.croshe.com
 * Created by Janesen on 2017/7/2.
 */
public class MConfig {

    public static Context context;

    public static void initConfig(final Context context) {
        MConfig.context = context;
        EventBus.getDefault().register(new EventReceiver());
        checkMetaData();

        AConfig.setOnLocationListener(new AConfig.OnLocationListener() {
            @Override
            public void startLocation(final AConfig.OnLocationCallBack onLocationCallBack) {
                CrosheMapUtils.getInstance(context).startLocation(new OnCrosheLocationListener() {
                    @Override
                    public void onLocation(AMapLocation aMapLocation) {
                        if (onLocationCallBack != null) {
                            BaseLocationEntity locationEntity = new BaseLocationEntity();
                            locationEntity.setLatitude(aMapLocation.getLatitude());
                            locationEntity.setLongitude(aMapLocation.getLongitude());
                            locationEntity.setAddress(aMapLocation.getAddress());
                            onLocationCallBack.onLocation(locationEntity);
                        }
                    }
                });
            }
        });
    }


    private static void checkMetaData() {
        try {
            ApplicationInfo appInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);

            if (!appInfo.metaData.containsKey("com.amap.api.v2.apikey")) {
                BaseAppUtils.notify(context, "系统提醒", "高德地图的APIKey未配置，请在AndroidManifest.xml中进行配置！");
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }


    public static class EventReceiver {
        @Subscribe
        public void onEvent(Map<String, Object> data) {
            if (!data.containsKey("action")) {
                data.put("action", "null");
            }
            String action = data.get("action").toString();
            if (action.equals("selectMap")) {
                Intent intent = new Intent(context, CrosheMapSelAddressActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (data.containsKey("context")) {
                    ((Context) data.get("context")).startActivity(intent);
                } else {
                    context.startActivity(intent);
                }
            } else if (action.equals("showMap")) {

                double latitude = NumberUtils.formatToDouble(data.get("latitude"));
                double longitude = NumberUtils.formatToDouble(data.get("longitude"));
                String address = data.get("address").toString();

                Intent intent = new Intent(context, CrosheMapShowAddressActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(CrosheMapShowAddressActivity.EXTRA_POI_LAT, latitude);
                intent.putExtra(CrosheMapShowAddressActivity.EXTRA_POI_LNG, longitude);
                intent.putExtra(CrosheMapShowAddressActivity.EXTRA_POI_ADDR, address);

                if (data.containsKey("context")) {
                    ((Context) data.get("context")).startActivity(intent);
                } else {
                    context.startActivity(intent);
                }
            }
        }
    }
}
