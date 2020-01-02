package com.croshe.android.base.views.list;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.croshe.android.base.R;
import com.croshe.android.base.listener.OnCrosheLetterListener;
import com.croshe.android.base.listener.OnCrosheRecyclerDataListener;
import com.croshe.android.base.listener.PageDataCallBack;
import com.croshe.android.base.utils.NumberUtils;
import com.croshe.android.base.views.control.CrosheViewHolder;
import com.croshe.android.base.views.control.CrosheWaveSideBarView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * 字母索引控件
 * 安徽创息软件科技有限公司-技术支持，http://wwww.croshe.com
 * Created by Janesen on 2017/6/28.
 */

public class CrosheLetterRecyclerView<T> extends FrameLayout implements CrosheWaveSideBarView.OnTouchLetterChangeListener, OnCrosheRecyclerDataListener<T> {

    private CrosheWaveSideBarView waveSideBarView;
    private CrosheRecyclerView<T> recyclerView;

    private OnCrosheLetterListener onCrosheLetterListener;

    private int page;

    private Map<String, List<T>> letterMapData = new HashMap<>();
    private Map<String, Integer> letterMapPosition = new HashMap<>();

    public CrosheLetterRecyclerView(@NonNull Context context) {
        super(context);
        initView();
    }

    public CrosheLetterRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public CrosheLetterRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }


    public void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.android_base_panel_letter, this);
        recyclerView = (CrosheRecyclerView<T>) findViewById(R.id.android_base_crosheRecyclerView);
        waveSideBarView = (CrosheWaveSideBarView) findViewById(R.id.android_base_waveSideBar);
        waveSideBarView.setOnTouchLetterChangeListener(this);
        recyclerView.setOnCrosheRecyclerDataListener(this);
    }


    @Override
    public void onLetterChange(String letter) {
        if (letter != null) {
            int position = NumberUtils.formatToInt(letterMapPosition.get(letter), -1);
            if (position >= 0) {
                recyclerView.moveToPosition(position);
            }
        }
    }


    public OnCrosheLetterListener getOnCrosheLetterListener() {
        return onCrosheLetterListener;
    }

    public void setOnCrosheLetterListener(OnCrosheLetterListener<T> onCrosheLetterListener) {
        this.onCrosheLetterListener = onCrosheLetterListener;
    }


    @Override
    public void getData(int page, final PageDataCallBack<T> callBack) {
        this.page = page;

        if (onCrosheLetterListener != null) {
            onCrosheLetterListener.getData(page, new PageDataCallBack<T>() {
                @Override
                public boolean loadData(int page, List<T> data, int appendIndex,
                                        boolean isLoadDone,
                                        boolean isStopLoading) {

                    if (page == -1) {
                        page = CrosheLetterRecyclerView.this.page;
                    }

                    if (page == 1) {
                        letterMapData.clear();
                        letterMapPosition.clear();
                    }

                    if (data.size() > 0) {
                        List<T> letterAllData = new ArrayList<T>();
                        for (T t : data) {
                            String letter = onCrosheLetterListener.getLetter(t);
                            if (!letterMapData.containsKey(letter)) {
                                List<T> list = new ArrayList<>();
                                list.add(t);
                                letterMapData.put(letter, list);
                            }
                            letterMapData.get(letter).add(t);
                        }

                        TreeSet<String> treeLetterKeys = new TreeSet<>(letterMapData.keySet());

                        int upCount = 0;
                        for (String letterKey : treeLetterKeys) {
                            List<T> letterData = letterMapData.get(letterKey);
                            letterAllData.addAll(letterData);
                            letterMapPosition.put(letterKey, upCount);
                            upCount += letterData.size();
                        }
                        callBack.loadData(1, letterAllData);
                    } else {
                        if (page == 1) {
                            callBack.loadData(1, new ArrayList<T>());
                        }
                    }


                    if (isLoadDone || data.size() == 0) {
                        callBack.loadDone();
                    }
                    return true;
                }

                @Override
                public boolean appendData(T data) {
                    String letter = onCrosheLetterListener.getLetter(data);
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
        if (onCrosheLetterListener != null) {
            if (crosheViewType == CrosheViewTypeEnum.DataView.ordinal()) {
                if (letterMapPosition.values().contains(position)) {
                    crosheViewType = CrosheViewTypeEnum.LetterView.ordinal();
                }
            }

            return onCrosheLetterListener.getItemViewLayout(obj, position, crosheViewType);
        }
        return -1;
    }

    @Override
    public void onRenderView(T obj, int position, int crosheViewType, CrosheViewHolder view) {
        if (onCrosheLetterListener != null) {
            if (crosheViewType == CrosheViewTypeEnum.DataView.ordinal()) {
                if (letterMapPosition.values().contains(position)) {
                    crosheViewType = CrosheViewTypeEnum.LetterView.ordinal();
                }
            }
            onCrosheLetterListener.onRenderView(obj, position, crosheViewType, view);
        }
    }


    public CrosheRecyclerView<T> getRecyclerView() {
        return recyclerView;
    }

    public void setRecyclerView(CrosheRecyclerView<T> recyclerView) {
        this.recyclerView = recyclerView;
    }


    public int getDataCount() {
        if (letterMapPosition.size() <= 0) {
            return 0;
        }
        return Math.max(recyclerView.getData().size() - letterMapPosition.size(), 0);
    }


    public void clearAllData() {
        letterMapPosition.clear();
        letterMapData.clear();
    }
}
