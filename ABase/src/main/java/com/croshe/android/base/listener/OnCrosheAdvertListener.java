package com.croshe.android.base.listener;

import android.widget.ImageView;

import java.util.List;

/**
 * 广告的监听
 * 安徽创息软件科技有限公司-技术支持，http://wwww.croshe.com
 * Created by Janesen on 2017/7/8 10:41.
 */
public interface OnCrosheAdvertListener<T> {


    void getAdvertData(AdvertCallBack<T> callBack);

    boolean onRenderAdvertView(T data, int position, ImageView imageView);


    abstract class AdvertCallBack<T> {
        public abstract void loadData(List<T> data);
    }

}
