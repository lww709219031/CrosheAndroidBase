package com.croshe.android.base.entity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Janesen on 16/4/25.
 */
public class AppVersionEntity implements Serializable {


    private String versionDesc;
    private double versionCode;
    private int disable;
    private int versionImportant;
    private String versionName;
    private String versionImportantDesc;
    private String downUrl;
    private boolean success;

    public static AppVersionEntity objectFromData(String str) {

        return new Gson().fromJson(str, AppVersionEntity.class);
    }

    public static List<AppVersionEntity> arrayAppVersionEntityFromData(String str) {

        Type listType = new TypeToken<ArrayList<AppVersionEntity>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
    }

    public String getVersionDesc() {
        return versionDesc;
    }

    public void setVersionDesc(String versionDesc) {
        this.versionDesc = versionDesc;
    }

    public double getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(double versionCode) {
        this.versionCode = versionCode;
    }

    public int getDisable() {
        return disable;
    }

    public void setDisable(int disable) {
        this.disable = disable;
    }

    public int getVersionImportant() {
        return versionImportant;
    }

    public void setVersionImportant(int versionImportant) {
        this.versionImportant = versionImportant;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getVersionImportantDesc() {
        return versionImportantDesc;
    }

    public void setVersionImportantDesc(String versionImportantDesc) {
        this.versionImportantDesc = versionImportantDesc;
    }

    public String getDownUrl() {
        return downUrl;
    }

    public void setDownUrl(String downUrl) {
        this.downUrl = downUrl;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
