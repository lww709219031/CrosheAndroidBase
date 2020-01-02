package com.croshe.android.base.views.list;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.croshe.android.base.listener.OnCrosheStickyListenter;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * 头部固定控件  安徽创息软件科技有限公司-技术支持  http://www.croshe.com
 * Created by Janesen on 2017/6/26.
 */
class CrosheStickyContainerView<T> extends LinearLayout {
    private OnCrosheStickyListenter<T> onCrosheStickyListenter;
    private RecyclerView.Adapter adapter;

    private TreeMap<String, Integer> stickyMapPosition = new TreeMap<>();
    private Map<Integer, RecyclerView.ViewHolder> stickyViews = new HashMap<>();
    private LinkedHashMap<View, Integer> stickyViewPoi = new LinkedHashMap<>();
    private int lastScrollY;
    private int lastPosition;



    public CrosheStickyContainerView(Context context) {
        super(context);
        initView();
    }

    public CrosheStickyContainerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public CrosheStickyContainerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }


    public void initView() {
        setOrientation(VERTICAL);
    }

    public void onScroll(int dy, int dx) {
        View lastVisible = null;

        TreeSet<Integer> stickyPosition = new TreeSet<>(stickyMapPosition.values());
        for (Integer position : stickyPosition) {
            if (onCrosheStickyListenter != null && adapter != null) {
                OnCrosheStickyListenter.StickyStateEnum stickyStateEnum = onCrosheStickyListenter.getStickyState(position);

                if (onCrosheStickyListenter != null && adapter != null) {
                    if (!stickyViews.containsKey(position)) {
                        RecyclerView.ViewHolder viewHolder = adapter.onCreateViewHolder(null,
                                adapter.getItemViewType(position));
                        if (viewHolder == null || viewHolder.itemView.getParent() != null) {
                            continue;
                        }
                        if (viewHolder != null && viewHolder.itemView != null) {
                            stickyViews.put(position, viewHolder);
                            stickyViewPoi.put(viewHolder.itemView, position);
                            viewHolder.itemView.setVisibility(GONE);
                            addView(viewHolder.itemView);
                        }
                    }
                }

                if (!stickyViews.containsKey(position)) continue;

                adapter.onBindViewHolder(stickyViews.get(position), position);
                View stickView = stickyViews.get(position).itemView;

                if (stickyStateEnum == OnCrosheStickyListenter.StickyStateEnum.IN_SHOW) {
                    int stickViewTop = onCrosheStickyListenter.getStickyTop(position);


                    if (dy > 0) {//向上
                        if (stickViewTop <= getLastVisibleHeight()) {
                            stickView.setVisibility(VISIBLE);
                        }
                    } else {//向下
                        if (stickViewTop >= computeStickyTopPosition(stickView, -1)) {
                            stickView.setVisibility(GONE);
                        }
                    }
                    lastVisible = getLastVisibleView();
                } else if (stickyStateEnum == OnCrosheStickyListenter.StickyStateEnum.OVER_SHOW) {
                    stickView.setVisibility(VISIBLE);
                    lastVisible = getLastVisibleView();
                }
            }
        }

        if (lastVisible != null) {
            if (dy > 0) {//向上
                if (computeStickyTopPosition(lastVisible, 1) > 0) {
                    if (lastVisible.getTop() == 0) return;
                    int scrollY = Math.abs(lastVisible.getTop()
                            - onCrosheStickyListenter.getStickyTop(lastPosition));
                    setScrollY(scrollY);
                    return;
                }
            } else {//向下
                if (computeStickyTopPosition(lastVisible, -1) < 0) {
                    scrollBy(0, dy);
                    return;
                }
            }

            if (countVisible() > 1) {
                if (lastVisible.getTop() == 0) return;
            }
            setScrollY(lastVisible.getTop());
            lastScrollY = getScrollY();
        } else {
            setScrollY(lastScrollY);
        }
    }


    public int computeStickyTopPosition(View stickView, int direction) {

        int[] parentLocation = new int[2];
        this.getLocationOnScreen(parentLocation);

        int[] stickLocation = new int[2];
        stickView.getLocationOnScreen(stickLocation);
        if (direction > 0) {
            if (stickLocation[1] <= 0) return 1;
        }
        return stickLocation[1] - parentLocation[1];
    }


    public int getLastVisibleHeight() {
        for (int i = this.getChildCount() - 1; i >= 0; i--) {
            if (this.getChildAt(i).getVisibility() != GONE) {
                int height = this.getChildAt(i).getHeight();
                return height;
            }
        }
        return 0;
    }


    public View getLastVisibleView() {
        for (int i = this.getChildCount() - 1; i >= 0; i--) {
            View view = this.getChildAt(i);
            if (view.getVisibility() != GONE) {
                lastPosition = stickyViewPoi.get(view);
                return view;
            }
        }
        return null;
    }


    public int countVisible() {
        int count = 0;
        for (int i = 0; i < this.getChildCount(); i++) {
            View view = this.getChildAt(i);
            if (view.getVisibility() != GONE) {
                count++;
            }
        }
        return count;
    }


    /**
     * 添加需要固定的控件位置
     *
     * @param position
     */
    public void addStickyPosition(int position) {
        addStickyPosition(String.valueOf(System.currentTimeMillis()), position);
    }

    /**
     * 添加需要固定的控件位置
     *
     * @param position
     */
    public void addStickyPosition(String key,int position) {
//        if (stickyMapPosition.containsKey(key)) {
//            removeStickyView(stickyMapPosition.get(key));
//        }
        stickyMapPosition.put(key, position);
    }


//    /**
//     * 清除固定的位置
//     */
//    public void clearStickyPosition() {
//        stickyMapPosition.clear();
//        stickyViews.clear();
//        stickyViewPoi.clear();
//        removeAllViews();
//    }


    public void removeStickyView(int position) {
        if (stickyViews.containsKey(position)) {
            removeView(stickyViews.get(position).itemView);
            stickyViews.remove(position);
        }
    }

    public OnCrosheStickyListenter<T> getOnCrosheStickyListenter() {
        return onCrosheStickyListenter;
    }

    public CrosheStickyContainerView setOnCrosheStickyListenter(OnCrosheStickyListenter<T> onCrosheStickyListenter) {
        this.onCrosheStickyListenter = onCrosheStickyListenter;
        return this;
    }

    public RecyclerView.Adapter getAdapter() {
        return adapter;
    }

    public CrosheStickyContainerView setAdapter(RecyclerView.Adapter adapter) {
        this.adapter = adapter;
        return this;
    }
}
