package com.croshe.android.base.listener;

/**
 * Created by Janesen on 2017/6/25.
 */

public interface OnCrosheTRecyclerDataListener<T> extends OnCrosheRecyclerDataListener<T> {

    /**
     * 获得显示的控件的类型
     * @return
     */
    int getItemViewType(T obj, int position, int crosheViewType);

}
