package com.croshe.android.base.views.list;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.croshe.android.base.R;

/**
 * 带有头部索引控件
 * 安徽创息软件科技有限公司-技术支持，http://wwww.croshe.com
 * Created by Janesen on 2017/6/28.
 */

public class CrosheSwipeRefreshGroupHeaderRecyclerView<M, T> extends CrosheGroupHeaderRecyclerView<M, T>{


    public CrosheSwipeRefreshGroupHeaderRecyclerView(@NonNull Context context) {
        super(context);
    }

    public CrosheSwipeRefreshGroupHeaderRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CrosheSwipeRefreshGroupHeaderRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void initView() {
        layoutResource = R.layout.android_base_panel_swipe_refresh_header;
        super.initView();
    }
}
