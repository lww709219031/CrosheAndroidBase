package com.croshe.android.base.views.list;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.croshe.android.base.AConfig;
import com.croshe.android.base.listener.OnCrosheLoadChangeListener;
import com.croshe.android.base.utils.BaseAppUtils;
import com.croshe.android.base.utils.DensityUtils;

/**
 *  可下拉刷新的列表控件 安徽创息软件科技有限公司-技术支持  http://www.croshe.com
 * Created by Janesen on 2017/6/25.
 */

public class CrosheSwipeRefreshRecyclerView<T> extends CrosheRecyclerView<T> implements OnCrosheLoadChangeListener {
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean onLoadRefresh = true;

    public CrosheSwipeRefreshRecyclerView(@NonNull Context context) {
        super(context);
        initView();
    }

    public CrosheSwipeRefreshRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public CrosheSwipeRefreshRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        if (AConfig.getOnRenderRefreshView() != null) {
            if (AConfig.getOnRenderRefreshView().onRenderView(this)) {
                return;
            }
        }
        removeView(recyclerView);
        super.setRefreshEnable(true);

        swipeRefreshLayout = new SwipeRefreshLayout(getContext());
        swipeRefreshLayout.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                loadData(1);
            }
        });
        addOnCrosheLoadChangeListener(this);
        swipeRefreshLayout.addView(recyclerView);
        swipeRefreshLayout.setColorSchemeColors(BaseAppUtils.getColorAccent(getContext()));
        swipeRefreshLayout.setDistanceToTriggerSync(DensityUtils.dip2px(258));
        addView(swipeRefreshLayout,0);
    }

    @Override
    public void setRefreshEnable(boolean refreshEnable) {
        swipeRefreshLayout.setEnabled(refreshEnable);
        super.setRefreshEnable(refreshEnable);
    }

    @Override
    public <T> void startLoadData(CrosheRecyclerView<T> crosheRecyclerView) {
        if (swipeRefreshLayout.isEnabled()&&onLoadRefresh) {
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(true);
                }
            });
        }
    }

    @Override
    public <T> void onDataChange(CrosheRecyclerView<T> crosheRecyclerView) {

    }

    @Override
    public <T> void stopLoadData(CrosheRecyclerView<T> crosheRecyclerView) {
        isRefresh = false;
        if (swipeRefreshLayout.isEnabled()) {
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        }
    }

    public boolean isOnLoadRefresh() {
        return onLoadRefresh;
    }

    public void setOnLoadRefresh(boolean onLoadRefresh) {
        this.onLoadRefresh = onLoadRefresh;
    }
}
