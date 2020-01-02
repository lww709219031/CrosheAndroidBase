package com.croshe.android.base.listener;

import com.croshe.android.base.views.control.CrosheViewHolder;

/**
 * 安徽创息软件科技有限公司-技术支持，http://wwww.croshe.com
 * Created by Janesen on 2017/6/28.
 */

public interface OnCrosheGroupHeaderListener<M, T> {


    /**
     * 获得分组数据
     *
     * @param callBack
     */
    void getGroupData(GroupDataCallBack<M> callBack);

    /**
     * 获得分组下的子数据
     *
     * @param group
     * @param page
     * @param callBack
     */
    void getData(M group, int page, final PageDataCallBack<T> callBack);


    /**
     * 获得显示的控件
     *
     * @return
     */
    int getItemViewLayout(M group, T obj, int position, int crosheViewType);


    /**
     * 显示控件
     *
     * @param holder
     */
    void onRenderView(M group, T obj, int position, int crosheViewType, CrosheViewHolder holder);
}
