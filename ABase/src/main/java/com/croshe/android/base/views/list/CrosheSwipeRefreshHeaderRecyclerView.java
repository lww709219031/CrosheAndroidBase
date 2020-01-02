package com.croshe.android.base.views.list;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.croshe.android.base.R;
import com.croshe.android.base.listener.OnCrosheHeaderListener;
import com.croshe.android.base.listener.OnCrosheRecyclerDataListener;
import com.croshe.android.base.listener.PageDataCallBack;
import com.croshe.android.base.views.control.CrosheViewHolder;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * 带有头部索引控件
 * 安徽创息软件科技有限公司-技术支持，http://wwww.croshe.com
 * Created by Janesen on 2017/6/28.
 */
public class CrosheSwipeRefreshHeaderRecyclerView<T> extends FrameLayout implements  OnCrosheRecyclerDataListener<T> {

    private CrosheSwipeRefreshRecyclerView<T> recyclerView;

    private OnCrosheHeaderListener<T> onCrosheHeaderListener;


    private Map<String, List<T>> letterMapData = new HashMap<>();
    private Map<String, Integer> letterMapPosition = new HashMap<>();

    private int page;

    public CrosheSwipeRefreshHeaderRecyclerView(@NonNull Context context) {
        super(context);
        initView();
    }

    public CrosheSwipeRefreshHeaderRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public CrosheSwipeRefreshHeaderRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }


    public void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.android_base_panel_header2, this);
        recyclerView = (CrosheSwipeRefreshRecyclerView<T>) findViewById(R.id.android_base_crosheRecyclerView);
        recyclerView.setOnCrosheRecyclerDataListener(this);
    }


    public OnCrosheHeaderListener<T> getOnCrosheHeaderListener() {
        return onCrosheHeaderListener;
    }

    public void setOnCrosheHeaderListener(OnCrosheHeaderListener<T> onCrosheHeaderListener) {
        this.onCrosheHeaderListener = onCrosheHeaderListener;
    }


    @Override
    public void getData(int page, final PageDataCallBack<T> callBack) {
        this.page = page;
        if (onCrosheHeaderListener != null) {
            onCrosheHeaderListener.getData(page, new PageDataCallBack<T>() {
                @Override
                public boolean loadData(int page, List<T> data, int appendIndex,
                                        boolean isLoadDone,boolean isStopLoading) {

                    if (page == -1) {
                        page = CrosheSwipeRefreshHeaderRecyclerView.this.page;
                    }

                    if (page == 1) {
                        letterMapData.clear();
                        letterMapPosition.clear();
                    }

                    if (data.size() > 0) {
                        for (T t : data) {
                            String letter = onCrosheHeaderListener.getHeader(t);
                            if (StringUtils.isEmpty(letter)) {
                                letter = "CROSHE";
                            }
                            if (!letterMapData.containsKey(letter)) {
                                List<T> list = new ArrayList<>();
                                list.add(t);
                                letterMapData.put(letter, list);
                            }
                            letterMapData.get(letter).add(t);
                        }

                        TreeSet<String> treeLetterKeys = new TreeSet<>(letterMapData.keySet());

                        int upCount = 0;
                        List<T> letterAllData = new ArrayList<T>();
                        for (String letterKey : treeLetterKeys) {
                            List<T> letterData = letterMapData.get(letterKey);
                            letterAllData.addAll(letterData);
                            letterMapPosition.put(letterKey, upCount);
                            upCount += letterData.size();
                        }

                        callBack.loadData(1, letterAllData);
                    }
                    if (isLoadDone || data.size() == 0) {
                        callBack.loadDone();
                    }
                    return true;
                }

                @Override
                public boolean appendData(T data) {
                    String letter = onCrosheHeaderListener.getHeader(data);
                    if (!letterMapData.containsKey(letter)) {
                        List<T> list = new ArrayList<>();
                        list.add(data);
                        letterMapData.put(letter, list);
                    }
                    letterMapData.get(letter).add(data);


                    TreeSet<String> treeLetterKeys = new TreeSet<>(letterMapData.keySet());

                    int upCount = 0;
                    List<T> letterAllData = new ArrayList<T>();
                    for (String letterKey : treeLetterKeys) {
                        List<T> letterData = letterMapData.get(letterKey);
                        letterAllData.addAll(letterData);
                        letterMapPosition.put(letterKey, upCount);
                        upCount += letterData.size();
                    }
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

    @Override
    public int getItemViewLayout(T obj, int position, int crosheViewType) {
        if (onCrosheHeaderListener != null) {
            if (crosheViewType == CrosheViewTypeEnum.DataView.ordinal()) {
                if (letterMapPosition.values().contains(position)) {
                    crosheViewType = CrosheViewTypeEnum.HeaderView.ordinal();
                }
            }

            return onCrosheHeaderListener.getItemViewLayout(obj, position, crosheViewType);
        }
        return -1;
    }

    @Override
    public void onRenderView(T obj, int position, int crosheViewType, CrosheViewHolder view) {
        if (onCrosheHeaderListener != null) {
            if (crosheViewType == CrosheViewTypeEnum.DataView.ordinal()) {
                if (letterMapPosition.values().contains(position)) {
                    crosheViewType = CrosheViewTypeEnum.HeaderView.ordinal();
                }
            }
            onCrosheHeaderListener.onRenderView(obj, position, crosheViewType, view);
        }
    }


    public CrosheRecyclerView<T> getSuperRecyclerView() {
        return recyclerView;
    }


    public int getDataCount() {
        return recyclerView.getData().size() - letterMapPosition.size();
    }


    public boolean isHeader(int position) {
        return letterMapPosition.values().contains(position);
    }
    /**
     * 向上找取数据的位置
     * @param position
     * @return
     */
    public int convertToDataPositionUp(int position) {
        if (isHeader(position)) {
            return convertToDataPositionUp(position--);
        }
        return position;
    }

    /**
     * 向下找取数据的位置
     * @param position
     * @return
     */
    public int convertToDataPositionDown(int position) {
        if (isHeader(position)) {
            return convertToDataPositionDown(position++);
        }
        return position;
    }


}
