package com.croshe.android.base.listener;

import com.croshe.android.base.views.control.CrosheViewHolder;

/**
 * Created by Janesen on 2017/6/25.
 */

public interface OnCrosheRecyclerDataListener<T> {
    /**
     * 获得数据集合
     *
     * @return
     */
    void getData(int page, final PageDataCallBack<T> callBack);

    /**
     * 获得显示的控件
     *
     * @return
     */
    int getItemViewLayout(T obj, int position,int crosheViewType);


    /**
     * 显示控件
     *
     * @param holder
     */
    void onRenderView(T obj, int position, int crosheViewType, CrosheViewHolder holder);



}
