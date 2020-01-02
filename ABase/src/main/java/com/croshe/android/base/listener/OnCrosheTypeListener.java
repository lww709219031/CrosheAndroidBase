package com.croshe.android.base.listener;

import android.view.View;

import com.croshe.android.base.views.control.CrosheViewHolder;

import java.util.List;

/**
 * 安徽创息软件科技有限公司-技术支持，http://wwww.croshe.com
 * Created by Janesen on 2017/7/11 10:46.
 */
public interface OnCrosheTypeListener<T> {


    /**
     * 获得数据
     * @param parentData
     * @param level
     * @param callBack
     */
    void getTypeData(T parentData, int page,int level, TypeCallBack<T> callBack);


    /**
     * 获得显示的控件
     *
     * @return
     */
    int getTypeItemViewLayout(T obj, int level, int position, int crosheViewType);


    /**
     * 显示控件
     *
     * @param holder
     */
    void onTypeRenderView(T obj, int position, int level, int crosheViewType, CrosheViewHolder holder);


    void onTypeCheckedView(T obj, int level, View view);

    void onTypeUnCheckView(T obj, int level, View view);



    abstract class TypeCallBack<T> {
        public  void loadData(List<T> data) {
            loadData(data, true);
        }
        public abstract void loadData(List<T> data,boolean hasNextLevel);
    }
}
