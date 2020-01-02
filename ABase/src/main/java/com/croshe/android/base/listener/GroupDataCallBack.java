package com.croshe.android.base.listener;

import java.util.List;

/**
 * 安徽创息软件科技有限公司-技术支持，http://wwww.croshe.com
 * Created by Janesen on 2017/9/8 16:06.
 */
public abstract class GroupDataCallBack<T> {

    public abstract void loadData(List<T> data);
}
