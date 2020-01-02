package com.croshe.android.base.views.list;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.croshe.android.base.R;
import com.croshe.android.base.listener.OnCrosheGroupListener;
import com.croshe.android.base.listener.OnCrosheLoadChangeListener;
import com.croshe.android.base.listener.OnCrosheRecyclerDataListener;
import com.croshe.android.base.listener.PageDataCallBack;
import com.croshe.android.base.views.control.CrosheViewHolder;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * 带有头部索引控件
 * 安徽创息软件科技有限公司-技术支持，http://wwww.croshe.com
 * Created by Janesen on 2017/6/28.
 */

public class CrosheGroupRecyclerView<T> extends FrameLayout implements OnCrosheRecyclerDataListener<T>, OnCrosheLoadChangeListener {

    private CrosheRecyclerView<T> recyclerView;

    private OnCrosheGroupListener<T> onCrosheGroupListener;


    private Map<String, List<T>> groupMapData = new HashMap<>();
    private Map<String, Integer> groupMapPosition = new HashMap<>();

    private Set<String> openedGroup = new HashSet<>();
    private int page;


    public CrosheGroupRecyclerView(@NonNull Context context) {
        super(context);
        initView();
    }

    public CrosheGroupRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public CrosheGroupRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }


    public void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.android_base_panel_header, this);
        recyclerView = (CrosheRecyclerView<T>) findViewById(R.id.android_base_crosheRecyclerView);
        recyclerView.setOnCrosheRecyclerDataListener(this);
        recyclerView.addOnCrosheLoadChangeListener(this);
    }


    @Override
    public void getData(int page, final PageDataCallBack<T> callBack) {
        String lastGroupName = getLastGroupName();
        if (StringUtils.isNotEmpty(lastGroupName) && !isOpen(lastGroupName)) {//最后一个分组必须打开了，才能继续获得下一页数据
            callBack.cancelLoad(page);
            return;
        }

        this.page = page;
        if (onCrosheGroupListener != null) {
            onCrosheGroupListener.getData(page, new PageDataCallBack<T>() {
                @Override
                public boolean loadData(int page, List<T> data, int appendIndex,
                                        boolean isLoadDone,boolean isStopLoading) {

                    if (page == -1) {
                        page = CrosheGroupRecyclerView.this.page;
                    }

                    if (page == 1) {
                        groupMapData.clear();
                        groupMapPosition.clear();
                    }

                    if (data.size() > 0) {
                        for (T t : data) {
                            String header = onCrosheGroupListener.getHeader(t);
                            if (StringUtils.isEmpty(header)) {
                                header = "CROSHE";
                            }
                            if (!groupMapData.containsKey(header)) {
                                List<T> list = new ArrayList<>();
                                list.add(t);
                                groupMapData.put(header, list);
                            }
                            groupMapData.get(header).add(t);
                        }

                        List<T> letterAllData = refreshHeaderPosition(page);
                        callBack.loadData(1, letterAllData);
                    }
                    if (isLoadDone || data.size() == 0) {
                        callBack.loadDone();
                    }
                    return true;
                }

                @Override
                public boolean appendData(T data) {
                    String letter = onCrosheGroupListener.getHeader(data);
                    if (!groupMapData.containsKey(letter)) {
                        List<T> list = new ArrayList<>();
                        list.add(data);
                        groupMapData.put(letter, list);
                    }
                    groupMapData.get(letter).add(data);


                    List<T> letterAllData = refreshHeaderPosition();
                    callBack.loadData(1, letterAllData, true);

                    return super.appendData(data);
                }

                @Override
                public void loadDone() {
                    callBack.loadDone();
                    super.loadDone();
                }
            });
        }
    }

    @NonNull
    public List<T> refreshHeaderPosition() {
        return refreshHeaderPosition(-1);
    }

    @NonNull
    public List<T> refreshHeaderPosition(int page) {
        if (page == 1) {
            groupMapPosition.clear();
        }
        TreeSet<String> treeLetterKeys = new TreeSet<>(groupMapData.keySet());

        int upCount = 0;
        List<T> letterAllData = new ArrayList<T>();
        for (String letterKey : treeLetterKeys) {
            List<T> letterData = groupMapData.get(letterKey);
            if (isOpen(letterKey)) {
                letterAllData.addAll(letterData);
            } else {
                letterAllData.addAll(letterData.subList(0, 1));
            }
            groupMapPosition.put(letterKey, upCount);
            upCount += letterData.size();
        }
        return letterAllData;
    }

    @Override
    public int getItemViewLayout(T obj, int position, int crosheViewType) {
        if (onCrosheGroupListener != null) {
            if (crosheViewType == CrosheViewTypeEnum.DataView.ordinal()) {
                if (groupMapPosition.values().contains(position)) {
                    crosheViewType = CrosheViewTypeEnum.GroupHeaderView.ordinal();
                }
            }

            return onCrosheGroupListener.getItemViewLayout(obj, position, crosheViewType);
        }
        return -1;
    }

    @Override
    public void onRenderView(T obj, int position, int crosheViewType, CrosheViewHolder view) {
        if (onCrosheGroupListener != null) {
            if (crosheViewType == CrosheViewTypeEnum.DataView.ordinal()) {
                if (groupMapPosition.values().contains(position)) {
                    crosheViewType = CrosheViewTypeEnum.GroupHeaderView.ordinal();
                }
            }
            onCrosheGroupListener.onRenderView(obj, position, crosheViewType, view);
        }
    }


    /**
     * 打开分组
     *
     * @param group
     */
    public void openGroup(String group) {
        openedGroup.add(group);
        recyclerView.setData(refreshHeaderPosition(1));
        recyclerView.notifyDataChanged();
    }


    /**
     * 关闭分组
     *
     * @param group
     */
    public void closeGroup(String group) {
        openedGroup.remove(group);
        recyclerView.setData(refreshHeaderPosition(1));
        recyclerView.notifyDataChanged();
    }


    /**
     * 根据分组的索引获得分组的名称
     * @param groupIndex
     * @return
     */
    public String getGroupName(int groupIndex) {
        String[] groupHeads = groupMapPosition.keySet().toArray(new String[]{});
        if (groupIndex < groupHeads.length && groupIndex >= 0) {
            return groupHeads[groupIndex];
        }
        return null;
    }


    /**
     * 获得第一个分组的名称
     * @return
     */
    public String getFirstGroupName() {
        return getGroupName(0);
    }


    /**
     * 获得最后一个分组的名称
     * @return
     */
    public String getLastGroupName() {
        return getGroupName(groupMapPosition.keySet().size() - 1);
    }

    public boolean isOpen(String group) {
        return openedGroup.contains(group);
    }


    public boolean isHeader(int position) {
        return groupMapPosition.values().contains(position);
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


    public CrosheRecyclerView<T> getSuperRecyclerView() {
        return recyclerView;
    }


    public int getDataCount() {
        return recyclerView.getData().size() - groupMapPosition.size();
    }


    public OnCrosheGroupListener<T> getOnCrosheGroupListener() {
        return onCrosheGroupListener;
    }

    public void setOnCrosheGroupListener(OnCrosheGroupListener<T> onCrosheGroupListener) {
        this.onCrosheGroupListener = onCrosheGroupListener;
    }


    @Override
    public <T> void startLoadData(CrosheRecyclerView<T> crosheRecyclerView) {

    }

    @Override
    public <T> void onDataChange(CrosheRecyclerView<T> crosheRecyclerView) {
        refreshHeaderPosition(1);
    }

    @Override
    public <T> void stopLoadData(CrosheRecyclerView<T> crosheRecyclerView) {

    }
}
