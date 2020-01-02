package com.croshe.android.base.listener;

/**
 * 安徽创息软件科技有限公司-技术支持，http://wwww.croshe.com
 * Created by Janesen on 2017/7/19 23:17.
 */
public interface OnCrosheFaceListener {

    /**
     * 当点击表情符号的时候
     * @param path
     * @param isSmallFace 是否是小表情
     */
    void onFaceClick(String path, boolean isSmallFace);

    /**
     * 键盘删除
     */
    void onFaceDelete();


    /**
     * 加载更多表情
     */
    void onMoreFace();
}
