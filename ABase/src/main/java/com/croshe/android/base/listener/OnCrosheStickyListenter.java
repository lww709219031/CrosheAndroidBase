package com.croshe.android.base.listener;

import android.view.View;

/**
 * Created by Janesen on 2017/6/26.
 */

public interface OnCrosheStickyListenter<T> {

    /**
     * 指定位置的sticky距离list头部的距离
     * @param position
     * @return
     */
    int getStickyTop(int position);


    /**
     * 指定位置的sticky距离是否显示
     * @param position
     * @return
     */
    StickyStateEnum getStickyState(int position);


    T getStickyData(int position);

     enum StickyStateEnum{
         IN_SHOW,//正在显示中
         OVER_SHOW,//已超过当前位置
         NOT_SHOW//还未显示
    }

}
