package com.croshe.android.base.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;

import java.lang.reflect.Field;
import java.text.DecimalFormat;

public class DensityUtils {

    /**
     * 获得屏幕的宽度 单位px
     * @return
     */
    public static final float getHeightInPx() {
        final float height = Resources.getSystem().getDisplayMetrics().heightPixels;
        return height;
    }

    /**
     * 获得屏幕的高度 单位px
     * @return
     */
    public static final float getWidthInPx() {
        final float width = Resources.getSystem().getDisplayMetrics().widthPixels;
        return width;
    }

    /**
     * 获得屏幕的高度 单位 dp
     * @return
     */
    public static final int getHeightInDp() {
        final float height = Resources.getSystem().getDisplayMetrics().heightPixels;
        int heightInDp = px2dip(height);
        return heightInDp;
    }

    /**
     * 获得屏幕的宽度 单位dp
     * @return
     */
    public static final int getWidthInDp() {
        final float height = Resources.getSystem().getDisplayMetrics().heightPixels;
        int widthInDp = px2dip(height);
        return widthInDp;
    }

    /**
     * 将单位dp的值转换为单位px的值
     * @param dpValue
     * @return
     */
    public static int dip2px(float dpValue) {
        final float scale =  Resources.getSystem().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    /**
     * 将单位px的值转换为单位dp的值
     * @param pxValue
     * @return
     */
    public static int px2dip(float pxValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将单位px的值转换为单位sp的值
     * @param pxValue
     * @return
     */
    public static int px2sp(float pxValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将单位sp的值转换为单位px的值
     * @param spValue
     * @return
     */
    public static int sp2px( float spValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (spValue * scale + 0.5f);
    }


    /**
     * 获得状态栏高度 单位px
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context){
        Class<?> c = null;

        Object obj = null;

        Field field = null;

        int x = 0, sbar = 0;
        try {

            c = Class.forName("com.android.internal.R$dimen");

            obj = c.newInstance();

            field = c.getField("status_bar_height");

            x = Integer.parseInt(field.get(obj).toString());

            sbar = Resources.getSystem().getDimensionPixelSize(x);

        } catch (Exception e1) {

            e1.printStackTrace();

        }

        return sbar;
    }

    /**
     * 获得控件的宽高
     * @return int[0] 宽  int[1] 高
     */
    public static int[] getViewSize(View view){
        LayoutParams rlp=view.getLayoutParams();
        int childEndWidth=rlp.width;
        int childEndHeight=rlp.height;
        if(childEndWidth<=0||childEndHeight<=0){
            view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            childEndWidth=view.getMeasuredWidth();
            childEndHeight=view.getMeasuredHeight();
        }

        return new int[]{childEndWidth,childEndHeight};
    }





    /**
     * 获得文件的大小字符串描述
     * @param size
     * @return
     */
    public static String getSizeByMB(long size)
    {
        size=(long) Math.ceil(size/1024.0);
        if(size<1024){
            return size+"KB";
        }else{
            double endSize=size/1024.0;

            DecimalFormat df = new DecimalFormat("#.0");
            return df.format(endSize)+"MB";
        }
    }

}
