package com.croshe.android.base.views;

import android.view.View;
import android.view.ViewGroup;

/**
 * 安徽创息软件科技有限公司-技术支持，http://wwww.croshe.com
 * Created by Janesen on 2017/9/9 12:55.
 */
public class CrosheReplaceViewHelper {

    /**
     * 用来替换某个View，比如你可以用一个空页面去替换某个View 
     * @param targetView       被替换的那个View
     * @param replaceViewResId 要替换进去的布局LayoutId 
     * @return
     */
    public static void replaceView(View targetView, final int replaceViewResId) {
        replaceView(targetView, View.inflate(targetView.getContext(), replaceViewResId, null));
    }

    /**
     * 用来替换某个View，比如你可以用一个空页面去替换某个View 
     * @param targetView  被替换的那个View
     * @param replaceView 要替换进去的那个View 
     * @return
     */
    public static void replaceView(View targetView, final View replaceView) {
        if (targetView != null) {
            ViewGroup parentViewGroup = (ViewGroup) targetView.getParent();
            int index = parentViewGroup.indexOfChild(targetView);
            if (replaceView != null) {
                parentViewGroup.removeView(replaceView);
            }
            replaceView.setLayoutParams(targetView.getLayoutParams());

            parentViewGroup.addView(replaceView, index);
            parentViewGroup.removeView(targetView);
        }
    }

}