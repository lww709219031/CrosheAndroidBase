package com.croshe.android.base.views.list;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.croshe.android.base.R;
import com.croshe.android.base.listener.GroupDataCallBack;
import com.croshe.android.base.listener.OnCrosheGroupHeaderListener;
import com.croshe.android.base.listener.OnCrosheRecyclerDataListener;
import com.croshe.android.base.listener.PageDataCallBack;
import com.croshe.android.base.utils.NumberUtils;
import com.croshe.android.base.views.control.CrosheViewHolder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 带有头部索引控件
 * 安徽创息软件科技有限公司-技术支持，http://wwww.croshe.com
 * Created by Janesen on 2017/6/28.
 */

public class CrosheGroupHeaderRecyclerView<M, T> extends FrameLayout implements OnCrosheRecyclerDataListener<T>, CrosheStickyContainerLayout.OnCrosheStickyLayoutListener<T> {

    protected int layoutResource = R.layout.android_base_panel_header;

    private String code = null;
    private CrosheRecyclerView<T> recyclerView;
    private OnCrosheGroupHeaderListener<M, T> onCrosheGroupHeaderListener;
    private boolean isLoadGroupData;
    private List<M> groupData = new ArrayList<>();
    private Set<M> openGroup = new HashSet<>();
    private LinkedHashMap<M, List<T>> groupChildData = new LinkedHashMap<>();
    private LinkedHashMap<Integer, M> indexGroup = new LinkedHashMap<>();
    private Map<M, Integer> pageGroup = new LinkedHashMap<>();
    private Map<M, Boolean> doneGroup = new LinkedHashMap<>();


    public CrosheGroupHeaderRecyclerView(@NonNull Context context) {
        super(context);
        initView();
    }

    public CrosheGroupHeaderRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public CrosheGroupHeaderRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }


    public void initView() {
        code = String.valueOf(System.currentTimeMillis()) + String.valueOf(this.hashCode());

        LayoutInflater.from(getContext()).inflate(layoutResource, this);
        recyclerView = (CrosheRecyclerView<T>) findViewById(R.id.android_base_crosheRecyclerView);
        recyclerView.setOnCrosheRecyclerDataListener(this);


//        CrosheStickyContainerLayout<T> stickyContainerLayout = new CrosheStickyContainerLayout(getContext());
//        stickyContainerLayout.setRecyclerView(recyclerView);
//        stickyContainerLayout.setBackgroundColor(Color.parseColor("#66000000"));
//        stickyContainerLayout.setOnCrosheStickyLayoutListener(this);
//        this.addView(stickyContainerLayout, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

    }


    @Override
    public void getData(final int page, PageDataCallBack<T> callBack) {

        if (recyclerView.isRefresh()) {
            final M lastOpen = getLastOpenGroup();
            if (lastOpen == null) {
                isLoadGroupData = false;
            } else {
                groupChildData.remove(lastOpen);
                pageGroup.remove(lastOpen);
                doneGroup.remove(lastOpen);
            }
        }
        if (onCrosheGroupHeaderListener != null) {
            if (!isLoadGroupData) {
                onCrosheGroupHeaderListener.getGroupData(new GroupDataCallBack<M>() {
                    @Override
                    public void loadData(List<M> data) {
                        isLoadGroupData = true;
                        groupData.clear();
                        groupData.addAll(data);
                        recyclerView.getPageDataCallBack().loadData(1, buildRecycleData(), false);
                    }
                });
            } else {
                final M lastOpen = getLastOpenGroup();
                if (lastOpen == null) {
                    callBack.cancelLoad(page);
                    return;
                }
                loadGroupData(lastOpen);
            }
        }
    }

    private void loadGroupData(final M group) {
        if (doneGroup.containsKey(group) && doneGroup.get(group)) {
            recyclerView.getPageDataCallBack().loadData(new ArrayList<T>(), true);
            return;
        }

        final int doLoadPage = NumberUtils.formatToInt(pageGroup.get(group)) + 1;
        onCrosheGroupHeaderListener.getData(group, doLoadPage, new PageDataCallBack<T>() {

            @Override
            public void loadDone() {
                super.loadDone();
                doneGroup.put(group, true);
            }

            @Override
            public boolean loadData(int page, List<T> data, int appendIndex, boolean isLoadDone,boolean isStopLoading) {
                doneGroup.put(group, false);


                if (data.size() == 0 || isLoadDone) {
                    doneGroup.put(group, true);
                    recyclerView.getPageDataCallBack().loadData(new ArrayList<T>(), true);
                    return true;
                }

                if (!groupChildData.containsKey(group)) {
                    groupChildData.put(group, new ArrayList<T>());
                }

                PageListHelper.getInstance(code + String.valueOf(group.hashCode()), groupChildData.get(group))
                        .putList(doLoadPage, data);

                pageGroup.put(group, doLoadPage);

                recyclerView.getPageDataCallBack().loadData(1, buildRecycleData(), false);
                return true;
            }
        });
    }


    @Override
    public int getItemViewLayout(T obj, int position, int crosheViewType) {
        if (indexGroup.containsKey(position) || obj == null) {
            crosheViewType = CrosheViewTypeEnum.GroupHeaderView.ordinal();
        }
        return onCrosheGroupHeaderListener.getItemViewLayout(indexGroup.get(position), obj, position, crosheViewType);
    }

    @Override
    public void onRenderView(T obj, int position, int crosheViewType, CrosheViewHolder holder) {
        if (indexGroup.containsKey(position) || obj == null) {
            crosheViewType = CrosheViewTypeEnum.GroupHeaderView.ordinal();
        }
        M group = indexGroup.get(position);
        onCrosheGroupHeaderListener.onRenderView(group, obj, position, crosheViewType, holder);
        holder.setSticky(false);
    }


    /**
     * 构建recycleView的数据源
     */
    private List<T> buildRecycleData() {
        indexGroup.clear();
        List<T> allData = new ArrayList<>();
        for (M m : groupData) {
            indexGroup.put(allData.size(), m);
            if (openGroup.contains(m)) {
                List<T> list = new ArrayList<>();
                list.add(null);//用来显示group
                if (groupChildData != null && groupChildData.get(m) != null) {
                    list.addAll(groupChildData.get(m));
                }
                allData.addAll(list);
            } else {
                allData.add(null);
            }
        }
        recyclerView.setRefreshEnable(openGroup.size() > 0);
        return allData;
    }


    /**
     * 打开分组
     *
     * @param group
     */
    public void openGroup(M group) {
        openGroup.add(group);
        if (getGroupData(group).size() == 0) {
            loadGroupData(group);
        } else {
            recyclerView.setData(buildRecycleData());
            recyclerView.setLoadDone(false);
            recyclerView.notifyDataChanged();
        }
    }


    /**
     * 关闭分组
     *
     * @param group
     */
    public void closeGroup(M group) {
        openGroup.remove(group);
        recyclerView.setData(buildRecycleData());
        recyclerView.notifyDataChanged();
        recyclerView.scrollToPosition(0);
    }


    public int getGroupIndex(M group) {
        for (Integer integer : indexGroup.keySet()) {
            if (indexGroup.get(integer) == group) {
                return integer;
            }
        }
        return 0;
    }


    /**
     * 获得分组的数据
     *
     * @param group
     * @return
     */
    public List<T> getGroupData(M group) {
        if (!groupChildData.containsKey(group)) {
            groupChildData.put(group, new ArrayList<T>());
        }
        return groupChildData.get(group);
    }

    /**
     * 获得最后一个打开的分组
     *
     * @return
     */
    public M getLastOpenGroup() {
        for (int i = groupData.size() - 1; i >= 0; i--) {
            if (openGroup.contains(groupData.get(i))) {
                return groupData.get(i);
            }
        }
        return null;
    }

    public boolean isOpen(M group) {
        return openGroup.contains(group);
    }

    public int getDataCount() {
        return recyclerView.getData().size() - groupData.size();
    }


    public boolean isHeader(int position) {
        return indexGroup.values().contains(position);
    }


    /**
     * 向上找取数据的位置
     *
     * @param position
     * @return
     */
    public int convertToDataPositionUp(int position) {
        if (position < 0) {
            return 0;
        }
        if (isHeader(position)) {
            return convertToDataPositionUp(position--);
        }
        return position;
    }

    /**
     * 向下找取数据的位置
     *
     * @param position
     * @return
     */
    public int convertToDataPositionDown(int position) {
        if (position > recyclerView.data.size()) {
            return recyclerView.data.size() - 1;
        }
        if (isHeader(position)) {
            return convertToDataPositionDown(position++);
        }
        return position;
    }


    public OnCrosheGroupHeaderListener<M, T> getOnCrosheGroupHeaderListener() {
        return onCrosheGroupHeaderListener;
    }

    public void setOnCrosheGroupHeaderListener(OnCrosheGroupHeaderListener<M, T> onCrosheGroupHeaderListener) {
        this.onCrosheGroupHeaderListener = onCrosheGroupHeaderListener;
    }

    public CrosheRecyclerView<T> getSuperRecyclerView() {
        return recyclerView;
    }


    @Override
    public boolean onSticky(T obj, int position, int crosheViewType) {
        if (indexGroup.containsKey(position)) {
            return true;
        }
        return false;
    }


    public boolean isCloseAll() {
        return openGroup.size() == 0;
    }


    /**
     * 重新加载分组数据
     */
    public void reloadGroupData() {
        if (onCrosheGroupHeaderListener != null) {
            onCrosheGroupHeaderListener.getGroupData(new GroupDataCallBack<M>() {
                @Override
                public void loadData(List<M> data) {
                    isLoadGroupData = true;
                    groupData.clear();
                    groupData.addAll(data);
                    recyclerView.getPageDataCallBack().loadData(1, buildRecycleData(), false);
                }
            });
        }
    }
}
