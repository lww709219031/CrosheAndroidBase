package com.croshe.android.base;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.croshe.android.base.entity.BaseLocationEntity;
import com.croshe.android.base.extend.web.CrosheBaseJavaScript;
import com.croshe.android.base.listener.OnCroshePayOrderListener;
import com.croshe.android.base.server.BaseRequest;
import com.croshe.android.base.views.CrosheBaseCommentView;
import com.croshe.android.base.views.CrosheBaseFaceView;
import com.croshe.android.base.views.list.CrosheRecyclerView;
import com.croshe.android.base.views.menu.CrosheMenuItem;

import java.util.HashSet;
import java.util.Set;

/**
 * 安徽创息软件科技有限公司-技术支持，http://wwww.croshe.com
 * Created by Janesen on 2017/8/17 14:27.
 */
public class AConfig {

    public static boolean AlbumAnim=true;//相册的动画


    public static Class<? extends CrosheBaseJavaScript> targetJavaScriptClass;

    public static float slideMaxTouchX=Float.MAX_VALUE;

    public static boolean browserSupport = true;

    public static boolean debug = false;

    public static boolean browserScheme = true;

    public static boolean browserMask = false;//默认显示mask

    public static boolean browserVUE = true;//是否显示vue界面

    public static View webWaitView;//浏览器等待界面

    public static OnPushListener onTagsListener;

    public static OnPayListener onPayListener;

    public static OnUserInfoListener onUserInfoListener;

    public static OnScannerListener onScannerListener;

    public static OnForwardListener onForwardListener;


    public static OnVideoViewListener onVideoViewListener;

    public static OnRenderRefreshViewListener onRenderRefreshView;


    public static OnReaderListener onReaderListener;


    public static OnLocationListener onLocationListener;


    public static OnViewPluginListener onViewPluginListener;

    public static OnFormatContentListener onFormatContentListener;


    /**
     * 初始化配置
     * @param mainUrl
     */
    public static void initConfig(String mainUrl) {
        BaseRequest.mainUrl = mainUrl;
    }

    /**
     * 获得当前设备所有的推送标签
     * @return
     */
    public static Set<String> getAllTags() {
        if (onTagsListener != null) {
            return onTagsListener.getAllTags();
        }
        return new HashSet<>();
    }

    public static OnPushListener getOnTagsListener() {
        if (onTagsListener == null) {
            onTagsListener = new OnPushListener() {
                @Override
                public Set<String> getAllTags() {
                    return null;
                }

                @Override
                public void setTags(String... tags) {

                }

                @Override
                public void addTags(String... tags) {

                }

                @Override
                public void removeTags(String... tags) {

                }
            };
        }
        return onTagsListener;
    }

    public static OnPayListener getOnPayListener() {
        return onPayListener;
    }

    public static void setOnPayListener(OnPayListener onPayListener) {
        AConfig.onPayListener = onPayListener;
    }

    public static void setOnTagsListener(OnPushListener onTagsListener) {
        AConfig.onTagsListener = onTagsListener;
    }

    public static OnUserInfoListener getOnUserInfoListener() {
        return onUserInfoListener;
    }

    public static void setOnUserInfoListener(OnUserInfoListener onUserInfoListener) {
        AConfig.onUserInfoListener = onUserInfoListener;
        if (onUserInfoListener != null) {
            getOnTagsListener().addTags(onUserInfoListener.getUserCode(), String.valueOf(onUserInfoListener.getUserId()));
        }
    }

    public static OnScannerListener getOnScannerListener() {
        return onScannerListener;
    }

    public static void setOnScannerListener(OnScannerListener onScannerListener) {
        AConfig.onScannerListener = onScannerListener;
    }

    public static OnForwardListener getOnForwardListener() {
        return onForwardListener;
    }

    public static void setOnForwardListener(OnForwardListener onForwardListener) {
        AConfig.onForwardListener = onForwardListener;
    }

    public static OnVideoViewListener getOnVideoViewListener() {
        return onVideoViewListener;
    }

    public static void setOnVideoViewListener(OnVideoViewListener onVideoViewListener) {
        AConfig.onVideoViewListener = onVideoViewListener;
    }

    public static OnRenderRefreshViewListener getOnRenderRefreshView() {
        return onRenderRefreshView;
    }

    public static void setOnRenderRefreshView(OnRenderRefreshViewListener onRenderRefreshView) {
        AConfig.onRenderRefreshView = onRenderRefreshView;
    }

    public static OnReaderListener getOnReaderListener() {
        return onReaderListener;
    }

    public static void setOnReaderListener(OnReaderListener onReaderListener) {
        AConfig.onReaderListener = onReaderListener;
    }

    public static OnLocationListener getOnLocationListener() {
        return onLocationListener;
    }

    public static void setOnLocationListener(OnLocationListener onLocationListener) {
        AConfig.onLocationListener = onLocationListener;
    }

    public static OnViewPluginListener getOnViewPluginListener() {
        return onViewPluginListener;
    }

    public static void setOnViewPluginListener(OnViewPluginListener onViewPluginListener) {
        AConfig.onViewPluginListener = onViewPluginListener;
    }

    public static OnFormatContentListener getOnFormatContentListener() {
        return onFormatContentListener;
    }

    public static void setOnFormatContentListener(OnFormatContentListener onFormatContentListener) {
        AConfig.onFormatContentListener = onFormatContentListener;
    }


    public static Class<? extends CrosheBaseJavaScript> getTargetJavaScriptClass() {
        return targetJavaScriptClass;
    }

    public static void setTargetJavaScriptClass(Class<? extends CrosheBaseJavaScript> targetJavaScriptClass) {
        AConfig.targetJavaScriptClass = targetJavaScriptClass;
    }

    public static float getSlideMaxTouchX() {
        return slideMaxTouchX;
    }

    public static void setSlideMaxTouchX(float slideMaxTouchX) {
        AConfig.slideMaxTouchX = slideMaxTouchX;
    }


    public static View getWebWaitView() {
        return webWaitView;
    }

    public static void setWebWaitView(View webWaitView) {
        AConfig.webWaitView = webWaitView;
    }

    public static boolean isBrowserSupport() {
        return browserSupport;
    }

    public static void setBrowserSupport(boolean browserSupport) {
        AConfig.browserSupport = browserSupport;
    }

    public static boolean isDebug() {
        return debug;
    }

    public static void setDebug(boolean debug) {
        AConfig.debug = debug;
    }

    public static boolean isBrowserScheme() {
        return browserScheme;
    }

    public static void setBrowserScheme(boolean browserScheme) {
        AConfig.browserScheme = browserScheme;
    }

    public static boolean isBrowserMask() {
        return browserMask;
    }

    public static void setBrowserMask(boolean browserMask) {
        AConfig.browserMask = browserMask;
    }


    public static boolean isBrowserVUE() {
        return browserVUE;
    }

    public static void setBrowserVUE(boolean browserVUE) {
        AConfig.browserVUE = browserVUE;
    }

    public  interface OnPushListener {

        /**
         * 获得当前设备所有的推送标签
         * @return
         */
        Set<String> getAllTags();

        /**
         * 设置推送的标签
         * @param tags
         */
        void setTags(String... tags);


        /**
         * 添加推送的标签
         * @param tags
         */
        void addTags(String... tags);


        /**
         * 移除推送的标签
         * @param tags
         */
        void removeTags(String... tags);

    }


    public interface  OnPayListener{
        /**
         * 选择支付方式
         */
        void onChoosePay(final Activity activity,
                         final String orderCode,
                         final double orderMoney,
                         final String orderTitle,
                         final Runnable onPaySuccess,
                         CrosheMenuItem... otherMenus);


        /**
         * 自动下单，发起支付
         *
         * @param activity
         * @param orderPrefix
         * @param orderMoney
         * @param orderTitle
         * @param onCroshePayOrderListener
         */
        void onOrderPay(final Activity activity,
                        final String orderPrefix,
                        final double orderMoney,
                        final String orderTitle,
                        final OnCroshePayOrderListener onCroshePayOrderListener);
    }


    public abstract static class OnUserInfoListener {
        public abstract int getUserId();

        public String getUserCode() {
            return null;
        }

        public String getUserMarkName() {
            return null;
        }
    }



    public interface OnScannerListener{
        boolean onScannerResult(String result);
    }

    public interface OnForwardListener{

        /**
         * 转发图片
         * @param imgUrl
         */
        void onForwardImage(String imgUrl);


        /**
         * 转发视频
         * @param videoUrl
         * @param thumbPath
         */
        void onForwardVideo(String videoUrl, String thumbPath);


        /**
         * 转发位置
         * @param address
         * @param lat
         * @param lng
         */
        void onForwardLocation(String address, double lat, double lng, String thumbPath);



    }


    public interface  OnVideoViewListener{
        View getVideoView(Context context, String thumbUrl,String url);
    }



    public interface OnRenderRefreshViewListener {
        <T> boolean onRenderView(CrosheRecyclerView<T> recyclerView);
    }



    public interface OnReaderListener{

        boolean checkReader(String url,String fileName);
    }


    public interface OnLocationListener{

        /**
         * 开始定位
         */
        void startLocation(OnLocationCallBack onLocationCallBack);
    }

    public interface OnLocationCallBack{
        void onLocation(BaseLocationEntity location);
    }


    public abstract static class OnViewPluginListener {
        public CrosheBaseCommentView getCommentView(Context context) {
            return null;
        }

        public CrosheBaseFaceView getFaceView(Context context) {
            return null;
        }
    }


    public interface OnFormatContentListener {
        CharSequence formatFaceContent(CharSequence content, int faceSize);
    }

}
