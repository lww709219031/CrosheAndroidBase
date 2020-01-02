package com.croshe.android.base.listener;

import android.view.View;
import android.view.ViewGroup;

/**
 * 安徽创息软件科技有限公司-技术支持，http://wwww.croshe.com
 * Created by Janesen on 2017/7/10 19:10.
 */
public interface OnCrosheCheckListener {


    void onCheckedView(ViewGroup viewGroup, View checkedView);

    void onUnCheckView(ViewGroup viewGroup, View uncheckView);


}
