package com.croshe.android.base.entity;

/**
 * 两个数据实体返回
 * 安徽创息软件科技有限公司-技术支持，http://wwww.croshe.com
 * Created by Janesen on 2017/7/9 13:21.
 */
public class MultiData<E, M> {
    private E data;
    private M other;

    public E getData() {
        return data;
    }

    public void setData(E data) {
        this.data = data;
    }

    public M getOther() {
        return other;
    }

    public void setOther(M other) {
        this.other = other;
    }
}
