package com.croshe.android.base.listener;

import com.croshe.android.base.entity.BaseEntity;

import java.io.Serializable;

/**
 * 安徽创息软件科技有限公司-技术支持，http://wwww.croshe.com
 * Created by Janesen on 2018/1/17 13:26.
 */
public interface OnCrosheForwardMoneyListener extends Serializable {

    /**
     * 确认转账，或已收款
     */
    void onConfirmForwardMoney(BaseEntity forwardMoneyEntity);
}
