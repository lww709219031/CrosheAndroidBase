package com.croshe.android.base.utils;

import android.app.Activity;
import android.app.Fragment;


/**
 * Fragment处理工具类
 * 安徽创息软件科技有限公司-技术支持，http://wwww.croshe.com
 * Created by Janesen on 2017/7/10 13:54.
 */
public class FragmentUtils {


    /**
     * 将Fragment显示到container中
     * @param containerId
     * @param fragment
     */
    public static void addToContainer(Activity context, int containerId, Fragment fragment) {
        android.app.FragmentTransaction transaction = context.getFragmentManager().beginTransaction();
        transaction.add(containerId, fragment);
        transaction.commit();
    }

}
