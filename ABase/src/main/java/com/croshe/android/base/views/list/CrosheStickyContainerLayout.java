package com.croshe.android.base.views.list;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.croshe.android.base.listener.OnCrosheLoadChangeListener;

/**
 * 头部固定控件  安徽创息软件科技有限公司-技术支持  http://www.croshe.com
 * Created by Janesen on 2017/6/26.
 */
class CrosheStickyContainerLayout<T> extends LinearLayout {

    private static final String STICKY_TAG = "STICKY_TAG";

    private static final String WAIT_STICKY_TAG = "WAIT_STICKY_TAG";

    private CrosheRecyclerView<T> recyclerView;
    private OnCrosheStickyLayoutListener<T> onCrosheStickyLayoutListener;
    private LinearLayoutManager linearLayoutManager;


    public CrosheStickyContainerLayout(Context context) {
        super(context);
        setOrientation(VERTICAL);
    }

    public CrosheStickyContainerLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        initView();
    }

    public void initView() {
        recyclerView.getSuperRecyclerView().addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (Math.abs(dy) > 0) {
                    onVScroll(dy);
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
        recyclerView.addOnCrosheLoadChangeListener(new OnCrosheLoadChangeListener() {
            @Override
            public <T> void startLoadData(CrosheRecyclerView<T> crosheRecyclerView) {

            }

            @Override
            public <T> void onDataChange(CrosheRecyclerView<T> crosheRecyclerView) {
                reset();
                Log.d("STAG", "onDataChange");
            }

            @Override
            public <T> void stopLoadData(CrosheRecyclerView<T> crosheRecyclerView) {

            }
        });
        linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

    }


    public void onVScroll(int dy) {
        if (onCrosheStickyLayoutListener != null) {
            int firstPosition = linearLayoutManager.findFirstVisibleItemPosition();
            int lastPosition = linearLayoutManager.findLastVisibleItemPosition();

            for (int i = firstPosition; i < lastPosition; i++) {

                boolean isSticky = onCrosheStickyLayoutListener.onSticky(recyclerView.getStickyData(i),
                        i, recyclerView.getCrosheViewType(i));

                if (isSticky) {
                    if (dy >= 0) {//向上
                        setBottomView(i);
                        if (getWaitStickyTopDistance(dy) <= 0) {
                            resetSticky();
                        }
                    } else {
                        setTopView(i);
                        if (getWaitStickyTopDistance(dy) >= 0) {
                            resetSticky();
                        }
                    }
                }
            }

            if (isContainsWaitStickyView()) {
                scrollBy(0, dy);
            }
        }
    }


    /**
     * 重置固定的view
     */
    public void resetSticky() {
        View waitStickyView = findViewWithTag(WAIT_STICKY_TAG);
        if (waitStickyView != null) {
            View stickyView = findViewWithTag(STICKY_TAG);
            if (stickyView != null) {
                stickyView.setTag(null);
                stickyView.setVisibility(INVISIBLE);
            }
            waitStickyView.setTag(STICKY_TAG);
            setScrollY(waitStickyView.getTop());

        }
    }




    /**
     * 整体重置
     */
    public void reset() {
//        this.removeAllViews();
//        setScrollY(0);
//        Log.d("STAG", "滚动条位置：" + recyclerView.getVerticalScrollbarPosition());
//        int firstPosition = linearLayoutManager.findFirstVisibleItemPosition();
//        int lastPosition = linearLayoutManager.findLastVisibleItemPosition();
//
//        for (int i = firstPosition; i < lastPosition; i++) {
//            setBottomView(i);
//            if (getWaitStickyTopDistance(1) <= 0) {
//                resetSticky();
//            }
//        }
    }

    /**
     * 设置底部待固定的view
     *
     * @param position
     */
    public void setBottomView(int position) {
        if (!isContainsWaitStickyView()) {
            if (getItemTopDistance(position) <= getStickyViewHeight()) {
                View bottomView = findViewById(position);
                if (bottomView == null) {
                    RecyclerView.ViewHolder viewHolder = recyclerView.getAdapter().onCreateViewHolder(null,
                            recyclerView.getAdapter().getItemViewType(position));
                    viewHolder.itemView.setTag(WAIT_STICKY_TAG);
                    viewHolder.itemView.setId(position);
                    recyclerView.getAdapter().onBindViewHolder(viewHolder, position);
                    addView(viewHolder.itemView);
                } else {
                    bottomView.setVisibility(VISIBLE);
                    bottomView.setTag(WAIT_STICKY_TAG);

                }
            }
        }
    }

    /**
     * 设置底部待固定的view
     *
     * @param position
     */
    public void setTopView(int position) {
        if (getStickyViewIndex() - 1 >= 0 && getStickyViewId() == position) {
            if (getItemTopDistance(position) >= getStickyTopDistance()) {
                View waitView = getChildAt(getStickyViewIndex() - 1);
                waitView.setTag(WAIT_STICKY_TAG);
                waitView.setVisibility(VISIBLE);

                findViewById(getStickyViewId()).setVisibility(INVISIBLE);
            }
        }
    }


    /**
     * 是否存在等待固定view
     *
     * @return
     */
    public boolean isContainsWaitStickyView() {
        return findViewWithTag(WAIT_STICKY_TAG) != null;
    }


    /**
     * 是否存在固定view
     *
     * @return
     */
    public boolean isContainsStickyView() {
        return findViewWithTag(STICKY_TAG) != null;
    }



    /**
     * 计算等待固定的view与顶部边缘的距离
     *
     * @return
     */
    public int getWaitStickyTopDistance(int direction) {
        if (direction >= 0) {
            int stickyTop = getStickyTopDistance() + getStickyViewHeight();
            return stickyTop;
        }
        View waitStickyView = findViewWithTag(WAIT_STICKY_TAG);
        if (waitStickyView != null) {
            int[] stickyLocation = new int[2];
            waitStickyView.getLocationInWindow(stickyLocation);

            int[] parentLocation = new int[2];
            this.getLocationInWindow(parentLocation);
            return stickyLocation[1] - parentLocation[1];
        }
        return 0;
    }


    /**
     * 获得固定view的高度
     *
     * @return
     */
    public int getStickyViewHeight() {
        View stickyView = findViewWithTag(STICKY_TAG);
        if (stickyView == null) {
            return 0;
        }
        return stickyView.getHeight();
    }


    /**
     * 获得固定的view的index
     *
     * @return
     */
    public int getStickyViewIndex() {
        View stickyView = findViewWithTag(STICKY_TAG);
        if (stickyView == null) {
            return -1;
        }
        return indexOfChild(stickyView);
    }

    /**
     * 计算等待固定的view与顶部边缘的距离
     *
     * @return
     */
    public int getStickyTopDistance() {
        View stickyView = findViewWithTag(STICKY_TAG);

        if (stickyView != null) {
            int[] stickyLocation = new int[2];
            stickyView.getLocationInWindow(stickyLocation);

            int[] parentLocation = new int[2];
            this.getLocationInWindow(parentLocation);
            return stickyLocation[1] - parentLocation[1];
        }
        return 0;
    }


    /**
     * 获得固定view的Id
     *
     * @return
     */
    public int getStickyViewId() {
        View stickyView = findViewWithTag(STICKY_TAG);
        if (stickyView != null) {
            return stickyView.getId();
        }
        return -1;
    }


    /**
     * 计算position的view与顶部边缘的距离
     *
     * @param position
     * @return
     */
    public int getItemTopDistance(int position) {
        View view = linearLayoutManager.findViewByPosition(position);
        if (view != null) {
//            int[] itemLocation = new int[2];
//            view.getLocationInWindow(itemLocation);
//
//            int[] parentLocation = new int[2];
//            recyclerView.getLocationInWindow(parentLocation);
            return view.getTop();
        }
        return 0;
    }


    /**
     * 批量移除
     *
     * @param fromIndex
     * @param toIndex
     */
    public void removeRangeView(int fromIndex, int toIndex) {
        for (int i = fromIndex; i < toIndex; i++) {
            removeViewAt(i);
        }
    }


    public OnCrosheStickyLayoutListener<T> getOnCrosheStickyLayoutListener() {
        return onCrosheStickyLayoutListener;
    }

    public void setOnCrosheStickyLayoutListener(OnCrosheStickyLayoutListener<T> onCrosheStickyLayoutListener) {
        this.onCrosheStickyLayoutListener = onCrosheStickyLayoutListener;
    }

    public CrosheRecyclerView<T> getRecyclerView() {
        return recyclerView;
    }

    public void setRecyclerView(CrosheRecyclerView<T> recyclerView) {
        this.recyclerView = recyclerView;
    }

    public interface OnCrosheStickyLayoutListener<T> {
        boolean onSticky(T obj, int position, int crosheViewType);
    }

}
