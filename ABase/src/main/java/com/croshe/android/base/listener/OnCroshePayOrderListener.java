package com.croshe.android.base.listener;

/**
 * 安徽创息软件科技有限公司-技术支持，http://wwww.croshe.com
 * Created by Janesen on 2017/7/17 21:28.
 */
public interface OnCroshePayOrderListener {

    void onPayResult(String orderCode, int payType, boolean success);

}
