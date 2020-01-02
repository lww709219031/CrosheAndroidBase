package com.croshe.base.map.entity;

import java.io.Serializable;

/**
 * 安徽创息软件科技有限公司-技术支持，http://wwww.croshe.com
 * Created by Janesen on 2017/7/3.
 */

public class LocationEntity implements Serializable {

    private double latitude;
    private double longitude;
    private String address;
    private String mapShort;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMapShort() {
        return mapShort;
    }

    public void setMapShort(String mapShort) {
        this.mapShort = mapShort;
    }
}
