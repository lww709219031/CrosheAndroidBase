package com.croshe.android.base.views.list;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.croshe.android.base.R;
import com.croshe.android.base.listener.OnCrosheLoadChangeListener;
import com.croshe.android.base.listener.OnCrosheRecyclerDataListener;
import com.croshe.android.base.listener.OnCrosheStickyListenter;
import com.croshe.android.base.listener.OnCrosheTRecyclerDataListener;
import com.croshe.android.base.listener.PageDataCallBack;
import com.croshe.android.base.views.control.CrosheViewHolder;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_IDLE;

/**
 * 列表控件  安徽创息软件科技有限公司-技术支持  http://www.croshe.com
 * Created by Janesen on 2017/6/25.
 */

public class CrosheRecyclerView<T> extends FrameLayout implements OnCrosheStickyListenter<T> {

    private String code = null;
    protected List<T> data = new ArrayList<>();

    protected Handler mHandler = new Handler();

    protected RecyclerView recyclerView;
    protected RecyclerView.LayoutManager layoutManager;
    protected DataAdapter adapter;
    protected int mPosition = 0;
    protected boolean move = false;
    protected boolean autoLoad = true;
    protected boolean autoNextPage = true;
    protected int page;
    protected boolean isLoadDone;
    private boolean isJustLoadDone;
    protected boolean isLoading;
    protected boolean isRefresh;
    protected boolean refreshEnable;

    private boolean isCheckLoad;
    protected boolean autoHeight;
    protected boolean autoWidth;
    protected Handler handler = new Handler(Looper.getMainLooper());

    protected View noData;
    protected String noDataTip;
    protected int noDataResourceId = -1;


    private int topFinalCount;
    private int bottomFinalCount;
    private int pageSize = -1;

    private PageDirection pageDirection = PageDirection.BOTTOM;

    private int itemPadding;


    //头部固定控制器
    protected CrosheStickyContainerView<T> crosheStickyContainerView;

    //数据适配器
    protected OnCrosheRecyclerDataListener onCrosheRecyclerDataListener;

    protected List<OnCrosheLoadChangeListener> onCrosheRecyclerChangeListeners = new ArrayList<>();

    public CrosheRecyclerView(@NonNull Context context) {
        super(context);
        initView();
    }

    public CrosheRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public CrosheRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }


    public enum PageDirection {
        TOP,
        BOTTOM
    }


    private void initView() {
        code = String.valueOf(System.currentTimeMillis()) + String.valueOf(this.hashCode());

        adapter = new DataAdapter();
        adapter.setHasStableIds(true);

        LayoutInflater.from(getContext()).inflate(R.layout.android_base_panel_list, this);
        recyclerView = (RecyclerView) findViewById(R.id.android_base_recyclerView);
        noData = LayoutInflater.from(getContext()).inflate(R.layout.android_base_panel_no_data, null);
        if (recyclerView == null) return;


        if (recyclerView.getBackground() == null) {
            recyclerView.setBackgroundColor(Color.TRANSPARENT);
        }

        recyclerView.addOnScrollListener(new RecyclerViewListener());
        layoutManager = new WrapContentLinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DataItemDecoration());
        recyclerView.setAdapter(adapter);


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                try {
                    switch (newState) {
                        case SCROLL_STATE_IDLE:
                            if (autoNextPage) {
                                if (pageDirection == PageDirection.BOTTOM) {
                                    if (!recyclerView.canScrollVertically(1)) {
                                        loadData(page + 1);
                                    }
                                } else {
                                    if (!recyclerView.canScrollVertically(-1)) {
                                        loadData(page + 1);
                                    }
                                }
                            }
                            break;

                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (crosheStickyContainerView != null) {
                    crosheStickyContainerView.onScroll(dy, dx);
                }
            }


        });

        noData.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        noData.setVisibility(GONE);

        addView(noData);

        initSticky();

    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (getBackground() == null) {
            setBackgroundColor(Color.TRANSPARENT);
        }
    }

    /**
     * 初始化固定容器
     */
    public void initSticky() {
        crosheStickyContainerView = new CrosheStickyContainerView(getContext());
        crosheStickyContainerView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        crosheStickyContainerView.setOnCrosheStickyListenter(this);
        crosheStickyContainerView.setAdapter(new DataAdapter());

        addView(crosheStickyContainerView);
        crosheStickyContainerView.bringToFront();
    }


    private void refreshLayout() {
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.MATCH_PARENT;
        if (autoHeight) {
            height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        if (autoWidth) {
            width = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(width, height);
        recyclerView.setLayoutParams(layoutParams);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (autoLoad) {
            if (!isCheckLoad) {
                isCheckLoad = true;
                loadData(1);
            }
        }
    }

    public void loadData(final int page) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (page == 1) {
                    isLoadDone = false;
                    isJustLoadDone = false;
                    isLoading = false;
                }


                if (isLoading) return;
                isLoading = true;
                CrosheRecyclerView.this.page = page;

                if (isLoadDone || isJustLoadDone) {
                    isLoading = false;
                    return;
                }

                if (onCrosheRecyclerDataListener != null) {

                    for (OnCrosheLoadChangeListener onCrosheRecyclerChangeListener : onCrosheRecyclerChangeListeners) {
                        onCrosheRecyclerChangeListener.startLoadData(CrosheRecyclerView.this);
                    }
                    onCrosheRecyclerDataListener.getData(page, pageDataCallBack);
                }
            }
        });
    }


    private PageDataCallBack pageDataCallBack = new PageDataCallBack<T>() {

        @Override
        public void cancelLoad(int page) {
            super.cancelLoad(page);
            isLoading = false;
            if (page > 0) {
                CrosheRecyclerView.this.page = page - 1;
            }
        }

        @Override
        public void clearData() {
            super.clearData();
            CrosheRecyclerView.this.data.clear();
            CrosheRecyclerView.this.page = 1;
            CrosheRecyclerView.this.isLoading = false;
            checkData();
        }

        @Override
        public void loadDone() {
            super.loadDone();
            isJustLoadDone = true;
            isLoadDone = true;
            isLoading = false;
            for (OnCrosheLoadChangeListener onCrosheRecyclerChangeListener : onCrosheRecyclerChangeListeners) {
                onCrosheRecyclerChangeListener.stopLoadData(CrosheRecyclerView.this);
            }
            checkData();
        }

        @Override
        public boolean appendData(final List<T> data, final int appendIndex) {
            isLoading = false;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (appendIndex >= 0) {
                        CrosheRecyclerView.this.data.addAll(appendIndex, data);
                    } else {
                        CrosheRecyclerView.this.data.addAll(data);
                    }
                    recyclerView.getRecycledViewPool().clear();
                    adapter.notifyDataSetChanged();
                    checkData();
                }
            });

            return true;
        }

        @Override
        public boolean appendData(final T data, final int appendIndex) {
            isLoading = false;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (appendIndex >= 0) {
                        CrosheRecyclerView.this.data.add(appendIndex, data);
                    } else {
                        CrosheRecyclerView.this.data.add(data);
                    }
                    recyclerView.getRecycledViewPool().clear();
                    adapter.notifyDataSetChanged();
                    checkData();
                }
            });
            return true;
        }

        @Override
        public boolean loadData(final int page,
                                final List<T> data,
                                final int appendIndex,
                                final boolean isLoadDone,
                                final boolean isStopLoading) {
            isLoading = !isStopLoading;
            if (data == null) {
                return false;
            }
            handler.post(new Runnable() {
                @Override
                public void run() {

                    int currPage = page;
                    if (currPage == -1) {
                        currPage = CrosheRecyclerView.this.page;
                    }

                    if (currPage == 1) {
                        CrosheRecyclerView.this.data.clear();
                    }

                    if (appendIndex >= 0) {
                        CrosheRecyclerView.this.data.addAll(appendIndex, data);
                    } else {
                        CrosheRecyclerView.this.data.addAll(data);
                    }

                    CrosheRecyclerView.this.isLoadDone = isLoadDone;

                    if (!isLoadDone) {
                        CrosheRecyclerView.this.isLoadDone = data.size() == 0;
                        if (pageSize > 0) {
                            CrosheRecyclerView.this.isLoadDone = data.size() < pageSize;
                        }
                    }

                    adapter.notifyDataSetChanged();

                    for (OnCrosheLoadChangeListener onCrosheRecyclerChangeListener : onCrosheRecyclerChangeListeners) {
                        onCrosheRecyclerChangeListener.stopLoadData(CrosheRecyclerView.this);
                    }
                    checkData();
                }
            });
            return true;
        }
    };


    public void checkData() {
        if (StringUtils.isNotEmpty(noDataTip)) {
            TextView tvNoData = (TextView) noData.findViewById(R.id.android_base_tvNoData);
            tvNoData.setText(noDataTip);
        }
        if (noDataResourceId != -1) {
            ImageView imgNoData = noData.findViewById(R.id.andorid_base_imgNoData);
            imgNoData.setImageResource(noDataResourceId);
        }

        if (recyclerView.getAdapter().getItemCount() == 0) {
            noData.setVisibility(VISIBLE);
        } else {
            noData.setVisibility(GONE);
        }
    }


    /**
     * 移动到指定位置
     *
     * @param position
     */
    public void moveToPosition(int position) {
        if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            mPosition = position;
            int firstItem = linearLayoutManager.findFirstVisibleItemPosition();
            int lastItem = linearLayoutManager.findLastVisibleItemPosition();
            if (position <= firstItem) {
                recyclerView.smoothScrollToPosition(position);
            } else if (position <= lastItem) {
                int top = recyclerView.getChildAt(position - firstItem).getTop();
                recyclerView.smoothScrollBy(0, top);
            } else {
                recyclerView.smoothScrollToPosition(position);
                move = true;
            }

        }
    }


    public void smoothScrollToPosition(int position) {
        recyclerView.smoothScrollToPosition(position);
    }


    public void scrollToPosition(int position) {
        recyclerView.scrollToPosition(position);
    }


    public int getVerticalScrollbarPosition() {
        return recyclerView.getVerticalScrollbarPosition();
    }

    public OnCrosheRecyclerDataListener<T> getOnCrosheRecyclerDataListener() {
        return onCrosheRecyclerDataListener;
    }

    /**
     * 设置数据适配器
     *
     * @param onCrosheRecyclerDataListener
     * @return
     */
    public CrosheRecyclerView setOnCrosheRecyclerDataListener(OnCrosheRecyclerDataListener<T>
                                                                      onCrosheRecyclerDataListener) {
        this.onCrosheRecyclerDataListener = onCrosheRecyclerDataListener;
        return this;
    }


    /**
     * 加载数据监听
     *
     * @param onCrosheRecyclerChangeListener
     */
    public CrosheRecyclerView addOnCrosheLoadChangeListener(OnCrosheLoadChangeListener
                                                                    onCrosheRecyclerChangeListener) {
        onCrosheRecyclerChangeListeners.add(onCrosheRecyclerChangeListener);
        return this;
    }


    public void removeStickHead(int position) {
        crosheStickyContainerView.removeStickyView(position);
    }

    public int getTopFinalCount() {
        return topFinalCount;
    }


    public List<T> getData() {
        return data;
    }


    public int getViewBottom(int index) {
        return recyclerView.getChildAt(index).getBottom();
    }

    public int getViewTop(int index) {
        return recyclerView.getChildAt(index).getTop();
    }

    public void setData(List<T> newData) {
        this.data = newData;
        isLoading = false;
    }

    /**
     * 设置头部常驻控件的数量
     *
     * @param topFinalCount
     */
    public CrosheRecyclerView setTopFinalCount(int topFinalCount) {
        this.topFinalCount = topFinalCount;
        return this;
    }

    public int getBottomFinalCount() {
        return bottomFinalCount;
    }

    /**
     * 设置底部常驻控件的数量
     *
     * @param bottomFinalCount
     */
    public CrosheRecyclerView setBottomFinalCount(int bottomFinalCount) {
        this.bottomFinalCount = bottomFinalCount;
        return this;
    }

    public int getPage() {
        return page;
    }

    public CrosheRecyclerView setPage(int page) {
        this.page = page;
        return this;
    }

    public boolean isLoadDone() {
        return isLoadDone;
    }

    public CrosheRecyclerView setLoadDone(boolean loadDone) {
        isLoadDone = loadDone;
        return this;
    }

    public RecyclerView getSuperRecyclerView() {
        return recyclerView;
    }


    public RecyclerView.LayoutManager getLayoutManager() {
        return layoutManager;
    }

    public CrosheRecyclerView setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        if (recyclerView == null) return this;
        this.layoutManager = layoutManager;
        recyclerView.setLayoutManager(layoutManager);
        return this;
    }

    public void setItemAnimator(RecyclerView.ItemAnimator itemAnimator) {
        if (recyclerView == null) return;
        recyclerView.setItemAnimator(itemAnimator);
    }

    private void notifyListenerDataChange() {
        for (OnCrosheLoadChangeListener onCrosheRecyclerChangeListener : onCrosheRecyclerChangeListeners) {
            onCrosheRecyclerChangeListener.onDataChange(CrosheRecyclerView.this);
        }
    }

    /**
     * 转换位置
     *
     * @param position
     * @return
     */
    public int convertPosition(int position) {
        return position - topFinalCount;
    }


    public int convertToRecyclerPosition(int position, int crosheViewType) {
        int justPosition = position;
        if (crosheViewType == CrosheViewTypeEnum.DataView.ordinal()) {
            justPosition = position + topFinalCount;
        } else if (crosheViewType == CrosheViewTypeEnum.FinalBottomView.ordinal()) {
            justPosition = position + topFinalCount + data.size();
        }
        return justPosition;
    }


    /**
     * 更新数据
     *
     * @param obj
     */
    public int notifyItemChanged(T obj) {
        return notifyItemChanged(obj, -1);
    }

    /**
     * 更新数据
     *
     * @param obj
     */
    public int notifyItemChanged(T obj, int crosheViewType) {
        if (data.indexOf(obj) >= 0) {
            return notifyItemChanged(data.indexOf(obj), crosheViewType);
        }
        return -1;
    }

    /**
     * 更新数据
     *
     * @param position
     */
    public int notifyItemChanged(final int position) {
        return notifyItemChanged(position, -1);
    }

    /**
     * 更新数据
     *
     * @param position
     */
    public int notifyItemChanged(final int position, final int crosheViewType) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                adapter.notifyItemChanged(convertToRecyclerPosition(position, crosheViewType));
                notifyListenerDataChange();
            }
        });
        return convertToRecyclerPosition(position, crosheViewType);
    }


    /**
     * 更新移除
     *
     * @param obj
     */
    public int notifyItemRemoved(T obj) {
        return notifyItemRemoved(obj, -1);
    }

    /**
     * 更新移除
     *
     * @param obj
     */
    public int notifyItemRemoved(T obj, int crosheViewType) {
        if (data.indexOf(obj) >= 0) {
            return notifyItemRemoved(data.indexOf(obj), crosheViewType);
        }
        return -1;
    }

    /**
     * 更新移除
     *
     * @param position
     */
    public int notifyItemRemoved(int position) {
        return notifyItemRemoved(position, -1);
    }

    /**
     * 更新移除
     *
     * @param position
     */
    public int notifyItemRemoved(int position, int crosheViewType) {
        if (position >= 0) {
            if (crosheViewType == CrosheViewTypeEnum.DataView.ordinal() || crosheViewType == -1) {
                data.remove(position);
            }
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                    notifyListenerDataChange();
                }
            });
        }
        return convertToRecyclerPosition(position, crosheViewType);
    }


    /**
     * 更新移除
     *
     * @param obj
     */
    public int notifyItemRemoved2(T obj) {
        return notifyItemRemoved2(data.indexOf(obj), -1);
    }


    /**
     * 更新移除
     *
     * @param position
     */
    public int notifyItemRemoved2(final int position) {
        return notifyItemRemoved2(position, -1);
    }


    /**
     * 更新移除
     *
     * @param position
     */
    public int notifyItemRemoved2(final int position, final int crosheViewType) {
        if (position >= 0) {
            if (crosheViewType == CrosheViewTypeEnum.DataView.ordinal() || crosheViewType == -1) {
                data.remove(position);
            }
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyItemRemoved(convertToRecyclerPosition(position, crosheViewType));
                    notifyListenerDataChange();
                }
            });
        }
        return convertToRecyclerPosition(position, crosheViewType);
    }

    /**
     * 更新移除
     *
     * @param obj
     */
    public int notifyItemRemoved2(T obj, int crosheViewType) {
        return notifyItemRemoved2(data.indexOf(obj), crosheViewType);
    }

    /**
     * 交换位置
     *
     * @param fromPosition
     * @param toPosition
     * @return
     */
    public int notifyMove(int fromPosition, int toPosition) {
        return notifyMove(fromPosition, toPosition, -1);
    }


    /**
     * 交换位置
     *
     * @param fromPosition
     * @param toPosition
     * @return
     */
    public int notifyMove(int fromPosition, int toPosition, int crosheViewType) {

        if (crosheViewType == CrosheViewTypeEnum.DataView.ordinal() || crosheViewType == -1) {
            T fromData = data.get(fromPosition);
            data.add(toPosition, fromData);
            data.remove(fromPosition + 1);
        }

        adapter.notifyItemMoved(convertToRecyclerPosition(fromPosition, crosheViewType), convertToRecyclerPosition(toPosition, crosheViewType));
        notifyListenerDataChange();
        return toPosition;
    }


    /**
     * 设置空数据的提醒
     *
     * @param text
     * @param iconResource
     */
    public void setNoData(String text, int iconResource) {
        this.noDataTip = text;
        this.noDataResourceId = iconResource;
    }


    /**
     * 设置空数据的提醒
     *
     * @param text
     */
    public void setNoData(String text) {
        this.noDataTip = text;
    }

    /**
     * 设置空数据的提醒
     *
     * @param iconResource
     */
    public void setNoData(int iconResource) {
        this.noDataResourceId = iconResource;
    }

    /**
     * 影藏空数据的图片提醒
     */
    public void hideNoDataImage() {
        noData.findViewById(R.id.andorid_base_imgNoData).setVisibility(GONE);
    }

    public void notifyDataChanged() {
        adapter.notifyDataSetChanged();
        notifyListenerDataChange();
    }


    //头部固定的处理-----开始
    @Override
    public int getStickyTop(int position) {
        View view = layoutManager.findViewByPosition(position);
        if (view != null) {
            return view.getTop();
        }
        return 0;
    }

    @Override
    public StickyStateEnum getStickyState(int position) {
        if (layoutManager instanceof LinearLayoutManager) {

            View view = layoutManager.findViewByPosition(position);
            if (view != null) {
                return StickyStateEnum.IN_SHOW;
            }


            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;

            if (linearLayoutManager.findFirstVisibleItemPosition() > position) {
                return StickyStateEnum.OVER_SHOW;
            } else if (linearLayoutManager.findFirstVisibleItemPosition() < position) {
                return StickyStateEnum.NOT_SHOW;
            }
        }
        return null;
    }

    @Override
    public T getStickyData(int position) {
        if (data.size() == 0 || position > data.size()) return null;
        return data.get(position);
    }
    //头部固定的处理-----结束


    public void reload() {
        loadData(page);
    }

    public DataAdapter getAdapter() {
        return adapter;
    }


    public void addItemDecoration(RecyclerView.ItemDecoration decor) {
        if (recyclerView == null) return;
        recyclerView.addItemDecoration(decor);
    }


    public PageDirection getPageDirection() {
        return pageDirection;
    }

    public void setPageDirection(PageDirection pageDirection) {
        this.pageDirection = pageDirection;
    }

    public PageDataCallBack getPageDataCallBack() {
        return pageDataCallBack;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    class RecyclerViewListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (move) {
                move = false;
                moveToPosition(mPosition);
            }
        }
    }


    public boolean isAutoHeight() {
        return autoHeight;
    }

    public boolean isAutoWidth() {
        return autoWidth;
    }

    public boolean isAutoLoad() {
        return autoLoad;
    }


    public boolean isRefresh() {
        return isRefresh;
    }

    public void setRefresh(boolean refresh) {
        isRefresh = refresh;
    }

    public void setAutoLoad(boolean autoLoad) {
        this.autoLoad = autoLoad;
    }

    public void setAutoWidth(boolean autoWidth) {
        this.autoWidth = autoWidth;
        refreshLayout();
    }

    public int getItemPadding() {
        return itemPadding;
    }

    public void setItemPadding(int itemPadding) {
        this.itemPadding = itemPadding;
    }

    public String getCode() {
        return code;
    }

    public boolean isRefreshEnable() {
        return refreshEnable;
    }

    public void setRefreshEnable(boolean refreshEnable) {
        this.refreshEnable = refreshEnable;
    }

    public void setAutoHeight(boolean autoHeight) {
        this.autoHeight = autoHeight;
        refreshLayout();
    }

    public boolean isLoading() {
        return isLoading;
    }

    public boolean isAutoNextPage() {
        return autoNextPage;
    }

    public void setAutoNextPage(boolean autoNextPage) {
        this.autoNextPage = autoNextPage;
    }

    public int getCrosheViewType(int position) {
        int crosheViewType;
        if (position < topFinalCount) {//头部
            crosheViewType = CrosheViewTypeEnum.FinalTopView.ordinal();

        } else if (position >= data.size() + topFinalCount) {//底部
            crosheViewType = CrosheViewTypeEnum.FinalBottomView.ordinal();
        } else {//中间数据
            crosheViewType = CrosheViewTypeEnum.DataView.ordinal();
        }
        return crosheViewType;
    }


    /**
     * 是否滚动到底部
     *
     * @return
     */
    public boolean isSlideToBottom() {
        if (recyclerView == null) return false;
        if (recyclerView.computeVerticalScrollExtent() + recyclerView.computeVerticalScrollOffset()
                >= recyclerView.computeVerticalScrollRange())
            return true;
        return false;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isCheckLoad = false;
    }


    /**
     * 释放资源
     */
    public void release() {
        if (recyclerView != null) {
            recyclerView.setAdapter(null);
            recyclerView.removeAllViews();
            recyclerView = null;
        }
        this.removeAllViews();
        System.gc();
    }


    public class DataAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private HashMap<Integer, Integer> viewTypeMap = new HashMap<>();

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType != -1 && viewType != 0) {
                View view = LayoutInflater.from(getContext()).inflate(viewTypeMap.get(viewType), parent, false);
                return new DataViewHolder<T>(view);
            }
            return null;
        }


        @Override
        public int getItemViewType(int position) {
            int returnPosition;
            Object returnData;
            int crosheViewType;

            if (position < topFinalCount) {//头部常驻控件
                crosheViewType = CrosheViewTypeEnum.FinalTopView.ordinal();
                returnPosition = position;
                returnData = null;
            } else if (position >= data.size() + topFinalCount) {//底部常驻控件
                crosheViewType = CrosheViewTypeEnum.FinalBottomView.ordinal();
                returnPosition = position - (data.size() + topFinalCount);
                returnData = null;
            } else {//服务器数据
                crosheViewType = CrosheViewTypeEnum.DataView.ordinal();
                returnPosition = position - topFinalCount;
                returnData = data.get(returnPosition);
            }
            if (onCrosheRecyclerDataListener != null) {
                int viewLayoutId = onCrosheRecyclerDataListener.
                        getItemViewLayout(returnData, returnPosition, crosheViewType);
                int viewType = viewLayoutId;
                if (onCrosheRecyclerDataListener instanceof OnCrosheTRecyclerDataListener) {
                    OnCrosheTRecyclerDataListener onCrosheTRecyclerDataListener = (OnCrosheTRecyclerDataListener) onCrosheRecyclerDataListener;
                    int newViewType = onCrosheTRecyclerDataListener.getItemViewType(returnData, returnPosition, crosheViewType);
                    if (newViewType != 0) {
                        viewType = newViewType;
                    }
                }
                viewTypeMap.put(viewType, viewLayoutId);
                return viewType;
            }
            return -1;
        }


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder != null) {
                if (holder instanceof DataViewHolder) {
                    DataViewHolder<T> dataViewHolder = (DataViewHolder) holder;
                    if (position >= data.size() + topFinalCount || position < topFinalCount) {
                        dataViewHolder.setObj(null, position);
                    } else {
                        dataViewHolder.setObj((T) data.get(position - topFinalCount), position);
                    }
                }
            }
        }

        @Override
        public int getItemCount() {
            return data.size() + topFinalCount + bottomFinalCount;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }


    class DataViewHolder<T> extends RecyclerView.ViewHolder {
        private CrosheViewHolder holder;

        public DataViewHolder(View itemView) {
            super(itemView);
            this.holder = CrosheViewHolder.build(itemView).setAdapter(adapter);
            this.holder.setSuperHolder(this);
        }

        public void setObj(T value, int position) {
            int crosheViewType;
            if (onCrosheRecyclerDataListener != null) {
                int returnPosition = position;

                if (position < topFinalCount) {//头部
                    crosheViewType = CrosheViewTypeEnum.FinalTopView.ordinal();
                    holder.setFixIndex(position);

                } else if (position >= data.size() + topFinalCount) {//底部
                    int bottomFixIndex = position - (data.size() + topFinalCount);

                    crosheViewType = CrosheViewTypeEnum.FinalBottomView.ordinal();
                    holder.setFixIndex(bottomFixIndex);
                    returnPosition = bottomFixIndex;
                } else {//中间数据
                    crosheViewType = CrosheViewTypeEnum.DataView.ordinal();
                    returnPosition = position - topFinalCount;
                }

                holder.setAdapterPosition(getAdapterPosition());

                //回调给调用者
                onCrosheRecyclerDataListener.onRenderView(value, returnPosition, crosheViewType, holder);


                if (holder.isSticky() && layoutManager instanceof LinearLayoutManager) {
                    if (crosheStickyContainerView == null) {
                        initSticky();
                    }
                    crosheStickyContainerView.addStickyPosition(holder.getStickyKey(), position);
                }
            }
        }

    }


    public class DataItemDecoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(final Rect outRect, final View view, final RecyclerView parent, RecyclerView.State state) {
            final int position = parent.getChildAdapterPosition(view);
            if (position != 0) {
                outRect.top = itemPadding;
            }
        }
    }


    public class WrapContentLinearLayoutManager extends LinearLayoutManager {
        public WrapContentLinearLayoutManager(Context context) {
            super(context);
        }

        public WrapContentLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        public WrapContentLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            try {
                super.onLayoutChildren(recycler, state);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public View getNoData() {
        return noData;
    }
}
