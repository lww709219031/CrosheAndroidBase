package com.croshe.android.base.listener;

import com.croshe.android.base.entity.red.RedEntity;

import java.io.Serializable;

/**
 * 安徽创息软件科技有限公司-技术支持，http://wwww.croshe.com
 * Created by Janesen on 2018/1/16 21:52.
 */
public interface OnCrosheRedTakeListener extends Serializable {

    /**
     * 领取成功
     * @param redEntity
     */
    void takeSuccess(RedEntity redEntity);
}
