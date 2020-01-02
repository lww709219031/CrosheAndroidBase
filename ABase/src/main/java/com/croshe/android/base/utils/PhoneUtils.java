package com.croshe.android.base.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;

import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Janesen on 2017/3/4.
 */

public class PhoneUtils {

    private TelephonyManager telephonyManager;
    /**
     * 国际移动用户识别码
     */
    private String IMSI;
    private Context cxt;

    public PhoneUtils(Context context) {
        cxt = context;
        telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
    }

    /**
     * 获取电话号码
     */
    public String getNativePhoneNumber() {
        String NativePhoneNumber = null;
        NativePhoneNumber = telephonyManager.getLine1Number();
        return NativePhoneNumber;
    }

    /**
     * 获取手机服务商信息
     */
    public String getProvidersName() {
        String ProvidersName = "N/A";
        try {
            IMSI = telephonyManager.getSubscriberId();
            // IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。
            System.out.println(IMSI);
            if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {
                ProvidersName = "中国移动";
            } else if (IMSI.startsWith("46001")) {
                ProvidersName = "中国联通";
            } else if (IMSI.startsWith("46003")) {
                ProvidersName = "中国电信";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ProvidersName;
    }

    public String getPhoneInfo() {
        TelephonyManager tm = (TelephonyManager) cxt.getSystemService(Context.TELEPHONY_SERVICE);
        StringBuilder sb = new StringBuilder();

        sb.append("\nDeviceId(IMEI) = " + tm.getDeviceId());
        sb.append("\nDeviceSoftwareVersion = " + tm.getDeviceSoftwareVersion());
        sb.append("\nLine1Number = " + tm.getLine1Number());
        sb.append("\nNetworkCountryIso = " + tm.getNetworkCountryIso());
        sb.append("\nNetworkOperator = " + tm.getNetworkOperator());
        sb.append("\nNetworkOperatorName = " + tm.getNetworkOperatorName());
        sb.append("\nNetworkType = " + tm.getNetworkType());
        sb.append("\nPhoneType = " + tm.getPhoneType());
        sb.append("\nSimCountryIso = " + tm.getSimCountryIso());
        sb.append("\nSimOperator = " + tm.getSimOperator());
        sb.append("\nSimOperatorName = " + tm.getSimOperatorName());
        sb.append("\nSimSerialNumber = " + tm.getSimSerialNumber());
        sb.append("\nSimState = " + tm.getSimState());
        sb.append("\nSubscriberId(IMSI) = " + tm.getSubscriberId());
        sb.append("\nVoiceMailNumber = " + tm.getVoiceMailNumber());
        return sb.toString();
    }


    /**
     * 获得SIM卡的标示
     *
     * @return
     */
    @SuppressLint("MissingPermission")
    public String getSIMCode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) { //大于等于Android 5.1.0 L版本
            SubscriptionManager sub = (SubscriptionManager) cxt.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
            List<SubscriptionInfo> info = sub.getActiveSubscriptionInfoList();
            int count = sub.getActiveSubscriptionInfoCount();
            if (count > 0) {
                String icc1 = info.get(0).getIccId();
                return icc1;
            } else {
                return null;
            }
        } else { //小于5.1.0 以下的版本
            return telephonyManager.getSimSerialNumber();
        }

    }

    /**
     * 获得设备的标识`
     *
     * @return
     */
    @SuppressLint("MissingPermission")
    public String getDeviceCode() {
        return telephonyManager.getDeviceId();
    }


    /**
     * 获得设备的UUID
     *
     * @return
     */
    @SuppressLint("MissingPermission")
    public String getDeviceUUID() {
        final String tmDevice, tmSerial, tmPhone, androidId;
        tmDevice = "" + telephonyManager.getDeviceId();
        tmSerial = "" + telephonyManager.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(cxt.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String uniqueId = deviceUuid.toString();
        return uniqueId;
    }


    /**
     * 判断是否手机号码
     *
     * @param phone
     * @return
     */
    public static boolean isPhoneNumber(String phone) {
        String regExp = "1(3|4|5|6|7|8)[0-9]{9}";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(phone);
        return m.matches();
    }


    /**
     * 判断当前设备是手机还是平板，代码来自 Google I/O App for Android
     *
     * @param context
     * @return 平板返回 True，手机返回 False
     */
    public static boolean isPad(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }


}
