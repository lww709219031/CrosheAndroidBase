package com.croshe.android.base.views.layout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.croshe.android.base.listener.OnCrosheCheckListener;
import com.croshe.android.base.listener.OnCrosheRecyclerDataListener;
import com.croshe.android.base.listener.OnCrosheTypeListener;
import com.croshe.android.base.listener.PageDataCallBack;
import com.croshe.android.base.views.CrosheCheckGroupHelper;
import com.croshe.android.base.views.control.CrosheViewHolder;
import com.croshe.android.base.views.list.CrosheRecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 横向布局的多子集布局，类似于电商中商品类型布局
 * 安徽创息软件科技有限公司-技术支持，http://wwww.croshe.com
 * Created by Janesen on 2017/7/11 10:34.
 */
public class CrosheHTypeLayout<T> extends LinearLayout {

    private OnCrosheTypeListener<T> onCrosheTypeListener;
    private boolean isInit;

    private int maxLevel = Integer.MAX_VALUE;
    private T currParentData;
    private int currLevel;

    private boolean fullWidth;//是否平方宽度
    private boolean hasNextLevel = true;
    private boolean autoLoad = true;



    private Map<Integer, CrosheRecyclerView<T>> mapRecycler = new HashMap<>();

    private Map<Integer, CrosheCheckGroupHelper> mapCheck = new HashMap<>();


    public CrosheHTypeLayout(Context context) {
        super(context);
        initView();
    }

    public CrosheHTypeLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public CrosheHTypeLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }


    public void initView() {
        setOrientation(HORIZONTAL);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (getBackground() == null) {
            setBackgroundColor(Color.TRANSPARENT);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isInit) {
            isInit = true;
            if (autoLoad) {
                loadData();
            }
        }
    }

    public void loadData() {
        loadData(null, 1);
    }

    public void loadData(final T parentData, final int level) {
        if (onCrosheTypeListener == null || level > maxLevel) {
            return;
        }

        if (level > currLevel && !hasNextLevel) {
            return;
        }

        currParentData = parentData;
        currLevel = level;

        for (int i = 0; i < this.getChildCount(); i++) {
            if (i < level) {
                this.getChildAt(i).setVisibility(VISIBLE);
            } else {
                this.getChildAt(i).setVisibility(GONE);
            }
        }

        if (mapRecycler.containsKey(level)) {
            mapCheck.get(level).clear();
            mapRecycler.get(level).loadData(1);
            return;
        }

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        if (fullWidth) {
            layoutParams.width = 0;
            layoutParams.weight = 0.2f;
        }

        final CrosheRecyclerView<T> crosheRecyclerView = new CrosheRecyclerView<T>(getContext());
        crosheRecyclerView.setLayoutParams(layoutParams);

        crosheRecyclerView.setAutoWidth(!fullWidth);

        final CrosheCheckGroupHelper crosheCheckGroupHelper=CrosheCheckGroupHelper.newInstance();
        crosheCheckGroupHelper.bind(crosheRecyclerView.getSuperRecyclerView(),
                new OnCrosheCheckListener() {
                    @Override
                    public void onCheckedView(ViewGroup viewGroup, View checkedView) {
                        int position = crosheRecyclerView.getSuperRecyclerView().getChildAdapterPosition(checkedView);
                        T data = crosheRecyclerView.getData().get(position);
                        loadData(data, level + 1);
                        onCrosheTypeListener.onTypeCheckedView(data, level, checkedView);
                    }

                    @Override
                    public void onUnCheckView(ViewGroup viewGroup, View uncheckView) {
                        int position = crosheRecyclerView.getSuperRecyclerView().getChildAdapterPosition(uncheckView);
                        if (position > 0) {
                            T data = crosheRecyclerView.getData().get(position);
                            onCrosheTypeListener.onTypeUnCheckView(data, level, uncheckView);
                        } else {
                            onCrosheTypeListener.onTypeUnCheckView(null, level, uncheckView);
                        }
                    }
                });

        crosheRecyclerView.setOnCrosheRecyclerDataListener(new OnCrosheRecyclerDataListener<T>() {
            @Override
            public void getData(int page, final PageDataCallBack<T> callBack) {
                onCrosheTypeListener.getTypeData(currParentData, page, currLevel,
                        new OnCrosheTypeListener.TypeCallBack<T>() {
                            @Override
                            public void loadData(List<T> data, boolean hasNextLevel) {
                                callBack.loadData(data);
                                CrosheHTypeLayout.this.hasNextLevel = hasNextLevel;
                            }
                        });
            }

            @Override
            public int getItemViewLayout(T obj, int position, int crosheViewType) {
                return onCrosheTypeListener.getTypeItemViewLayout(obj, currLevel, position, crosheViewType);
            }

            @Override
            public void onRenderView(T obj, int position, int crosheViewType, CrosheViewHolder holder) {
                onCrosheTypeListener.onTypeRenderView(obj, position, currLevel, crosheViewType, holder);
                if (crosheCheckGroupHelper.isChecked(position)) {
                    onCrosheTypeListener.onTypeCheckedView(crosheRecyclerView.getData().get(position
                    ), currLevel, holder.getItemView());
                    crosheCheckGroupHelper.check(position);

                }else{
                    crosheCheckGroupHelper.unCheck(position);
                    onCrosheTypeListener.onTypeUnCheckView(crosheRecyclerView.getData().get(position
                    ), currLevel, holder.getItemView());
                }
            }
        });
        this.addView(crosheRecyclerView);
        crosheRecyclerView.loadData(1);
        mapRecycler.put(level, crosheRecyclerView);
        mapCheck.put(level, crosheCheckGroupHelper);
    }


    public OnCrosheTypeListener<T> getOnCrosheTypeListener() {
        return onCrosheTypeListener;
    }

    public void setOnCrosheTypeListener(OnCrosheTypeListener<T> onCrosheTypeListener) {
        this.onCrosheTypeListener = onCrosheTypeListener;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public boolean isFullWidth() {
        return fullWidth;
    }

    public void setFullWidth(boolean fullWidth) {
        this.fullWidth = fullWidth;
    }

    public boolean isAutoLoad() {
        return autoLoad;
    }

    public void setAutoLoad(boolean autoLoad) {
        this.autoLoad = autoLoad;
    }
}
