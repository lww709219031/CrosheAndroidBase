package com.croshe.android.base.listener;

import java.util.List;

/**
 * 安徽创息软件科技有限公司-技术支持，http://wwww.croshe.com
 * Created by Janesen on 2017/6/28.
 */

public interface OnCrosheLetterListener<T> extends OnCrosheRecyclerDataListener<T> {


    /**
     * 获得字母
     *
     * @return
     */
    String getLetter(T obj);

}
