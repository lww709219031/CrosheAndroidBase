package com.croshe.android.base.entity.red;

import com.croshe.android.base.server.BaseRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 安徽创息软件科技有限公司-技术支持，http://wwww.croshe.com
 * Created by Janesen on 2017/8/31 21:02.
 */
public class RedEntity implements Serializable {

    private int redId;
    private int userId;
    private double redMoney;
    private String redTitle;
    private int redType;//0 普通 1手气

    private String redTypeStr;
    private int redCount;
    private String orderCode;
    private String redDateTime;
    private int redState;//0 领取中  1已领完
    private String redStateStr;

    private int takeId;
    private double takeMoney;
    private String takeUserNickName;
    private String takeUserCode;
    private String takeDateTime;

    private String userCode;
    private String userNickName;
    private String userHeadImg;

    private String redQuestion;
    private String redAnswer;


    private int sendType;//0 好友 1 群
    private int countTake;//已领取
    private int redValidTime;//有效时间，单位小时


    private double redLatitude;
    private double redLongitude;


    public static RedEntity objectFromData(String str) {

        return new Gson().fromJson(str, RedEntity.class);
    }

    public static List<RedEntity> arrayTaskEntityFromData(String str) {

        Type listType = new TypeToken<ArrayList<RedEntity>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
    }


    public int getRedId() {
        return redId;
    }

    public void setRedId(int redId) {
        this.redId = redId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public double getRedMoney() {
        return redMoney;
    }

    public void setRedMoney(double redMoney) {
        this.redMoney = redMoney;
    }

    public String getRedTitle() {
        return redTitle;
    }

    public void setRedTitle(String redTitle) {
        this.redTitle = redTitle;
    }

    public int getRedType() {
        return redType;
    }

    public void setRedType(int redType) {
        this.redType = redType;
    }

    public int getRedCount() {
        return redCount;
    }

    public void setRedCount(int redCount) {
        this.redCount = redCount;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getRedDateTime() {
        return redDateTime;
    }

    public void setRedDateTime(String redDateTime) {
        this.redDateTime = redDateTime;
    }

    public int getTakeId() {
        return takeId;
    }

    public void setTakeId(int takeId) {
        this.takeId = takeId;
    }


    public boolean isTake() {
        return takeId > 0;
    }

    public String getUserNickName() {
        return userNickName;
    }

    public void setUserNickName(String userNickName) {
        this.userNickName = userNickName;
    }

    public String getUserHeadImg() {
        return userHeadImg;
    }

    public void setUserHeadImg(String userHeadImg) {
        this.userHeadImg = userHeadImg;
    }

    public double getTakeMoney() {
        return takeMoney;
    }

    public void setTakeMoney(double takeMoney) {
        this.takeMoney = takeMoney;
    }

    public String getUserHeadImgUrl() {
        if (StringUtils.isEmpty(userHeadImg)) return userHeadImg;
        if (userHeadImg.startsWith("http://") || userHeadImg.startsWith("https://")) {
            return userHeadImg;
        }
        return BaseRequest.mainUrl + userHeadImg;
    }


    public int getSendType() {
        return sendType;
    }

    public void setSendType(int sendType) {
        this.sendType = sendType;
    }

    public int getRedState() {
        return redState;
    }

    public void setRedState(int redState) {
        this.redState = redState;
    }

    public int getCountTake() {
        return countTake;
    }

    public void setCountTake(int countTake) {
        this.countTake = countTake;
    }

    public int getRedValidTime() {
        return redValidTime;
    }

    public void setRedValidTime(int redValidTime) {
        this.redValidTime = redValidTime;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getTakeUserNickName() {
        return takeUserNickName;
    }

    public void setTakeUserNickName(String takeUserNickName) {
        this.takeUserNickName = takeUserNickName;
    }

    public String getTakeUserCode() {
        return takeUserCode;
    }

    public void setTakeUserCode(String takeUserCode) {
        this.takeUserCode = takeUserCode;
    }

    public String getRedStateStr() {
        return redStateStr;
    }

    public void setRedStateStr(String redStateStr) {
        this.redStateStr = redStateStr;
    }

    public String getRedTypeStr() {
        return redTypeStr;
    }

    public void setRedTypeStr(String redTypeStr) {
        this.redTypeStr = redTypeStr;
    }

    public String getTakeDateTime() {
        return takeDateTime;
    }

    public void setTakeDateTime(String takeDateTime) {
        this.takeDateTime = takeDateTime;
    }


    public double getRedLatitude() {
        return redLatitude;
    }

    public void setRedLatitude(double redLatitude) {
        this.redLatitude = redLatitude;
    }

    public double getRedLongitude() {
        return redLongitude;
    }

    public void setRedLongitude(double redLongitude) {
        this.redLongitude = redLongitude;
    }

    public String getRedQuestion() {
        return redQuestion;
    }

    public void setRedQuestion(String redQuestion) {
        this.redQuestion = redQuestion;
    }

    public String getRedAnswer() {
        return redAnswer;
    }

    public void setRedAnswer(String redAnswer) {
        this.redAnswer = redAnswer;
    }
}
