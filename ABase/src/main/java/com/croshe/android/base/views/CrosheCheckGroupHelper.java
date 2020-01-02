package com.croshe.android.base.views;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.croshe.android.base.ARecord;
import com.croshe.android.base.listener.OnCrosheCheckListener;
import com.croshe.android.base.utils.MD5Encrypt;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * 控件选择 工具类  可自动实现单选或多选
 * 安徽创息软件科技有限公司-技术支持，http://wwww.croshe.com
 * Created by Janesen on 2017/7/10 19:44.
 */
public class CrosheCheckGroupHelper implements View.OnClickListener, ViewGroup.OnHierarchyChangeListener {


    private String viewCode;

    private Context context;

    private OnCrosheCheckListener onCrosheCheckListener;
    private Set<View> checkedView = new HashSet<>();
    private boolean isMultiCheck;//是否多选
    private boolean isRecord;//是否记录
    private ViewGroup viewGroup;
    private Integer[] defaultCheckArray = new Integer[0];
    private Integer[] recordCheckArray = new Integer[0];
    private Set<Integer> checkedIndex = new HashSet<>();


    public static CrosheCheckGroupHelper newInstance() {
        return new CrosheCheckGroupHelper();
    }

    public static CrosheCheckGroupHelper newInstance(Context context) {
        return new CrosheCheckGroupHelper().setContext(context);
    }


    public void bind(ViewGroup viewGroup, OnCrosheCheckListener onCrosheCheckListener, Integer... checkedIndex) {
        this.context = viewGroup.getContext();
        this.viewGroup = viewGroup;
        this.onCrosheCheckListener = onCrosheCheckListener;
        this.defaultCheckArray = checkedIndex;
        if (StringUtils.isEmpty(viewCode)) {
            viewCode = MD5Encrypt.MD5(String.valueOf(viewGroup.hashCode()));
        }
        viewGroup.setOnHierarchyChangeListener(this);
        refresh();
    }


    @Override
    public void onClick(View v) {
        int index = getViewIndex(v);
        if (isMultiCheck) {
            if (checkedIndex.contains(index)) {
                if (onCrosheCheckListener != null) {
                    onCrosheCheckListener.onUnCheckView(viewGroup, v);
                }

                checkedIndex.remove(index);
                checkedView.remove(getViewByIndex(index));

                record();
                return;
            }
        } else {
            notifySingleUnCheck();
        }

        if (onCrosheCheckListener != null) {
            onCrosheCheckListener.onCheckedView(viewGroup, v);
        }
        checkedView.add(v);
        checkedIndex.add(index);

        record();
    }


    private void notifySingleUnCheck() {
        for (Integer integer : checkedIndex) {
            if (onCrosheCheckListener != null && getViewByIndex(integer) != null) {
                onCrosheCheckListener.onUnCheckView(viewGroup, getViewByIndex(integer));
            }
        }
        for (View view : checkedView) {
            if (onCrosheCheckListener != null && view != null) {
                onCrosheCheckListener.onUnCheckView(viewGroup, view);
            }
        }
        checkedIndex.clear();
        checkedView.clear();
    }


    private void notifyMultiUnCheck(int index) {
        if (checkedIndex.contains(index)) {
            if (onCrosheCheckListener != null) {
                onCrosheCheckListener.onUnCheckView(viewGroup, getViewByIndex(index));
            }
        }
    }

    private void refresh() {
        restore();
        checkedView.clear();
        checkedIndex.clear();
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View view = viewGroup.getChildAt(i);
            view.setOnClickListener(this);
            if (ArrayUtils.contains(defaultCheckArray, i) || ArrayUtils.contains(recordCheckArray, i)) {
                checkedView.add(view);
                checkedIndex.add(i);
                if (onCrosheCheckListener != null) {
                    onCrosheCheckListener.onCheckedView(viewGroup, view);
                }
            }
        }
    }


    private void record() {
        if (isRecord) {
            ARecord.get(viewCode).setAttr("checked", JSON.toJSONString(checkedIndex));
        }
    }


    private void restore() {
        if (isRecord) {
            String indexs = ARecord.get(viewCode).getString("checked", "");
            if (StringUtils.isNotEmpty(indexs)) {
                recordCheckArray = JSON.parseObject(indexs, Integer[].class);
            }
        }
    }


    /**
     * 重置
     */
    public void reset() {
        checkedView.clear();
        checkedIndex.clear();
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View view = viewGroup.getChildAt(i);
            view.setOnClickListener(this);
            if (ArrayUtils.contains(defaultCheckArray, i)) {
                checkedView.add(view);
                checkedIndex.add(i);
                if (onCrosheCheckListener != null) {
                    onCrosheCheckListener.onCheckedView(viewGroup, view);
                }
            } else {
                if (onCrosheCheckListener != null) {
                    onCrosheCheckListener.onUnCheckView(viewGroup, view);
                }
            }
        }
    }


    /**
     * 清除记忆的选择
     */
    public void clearRecord() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("CrosheCheck", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("indexs");
        editor.commit();
    }

    public int getViewIndex(View view) {
        int index = viewGroup.indexOfChild(view);
        if (viewGroup instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) viewGroup;
            index = recyclerView.getChildAdapterPosition(view);
        }
        return index;
    }


    public View getViewByIndex(int index) {
        View view = viewGroup.getChildAt(index);
        if (viewGroup instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) viewGroup;
            view = recyclerView.getLayoutManager().findViewByPosition(index);
        }
        return view;
    }

    public OnCrosheCheckListener getOnCrosheCheckListener() {
        return onCrosheCheckListener;
    }

    public void setOnCrosheCheckListener(OnCrosheCheckListener onCrosheCheckListener) {
        this.onCrosheCheckListener = onCrosheCheckListener;
    }

    public Set<View> getCheckedView() {
        return checkedView;
    }

    public void setCheckedView(Set<View> checkedView) {
        this.checkedView = checkedView;
    }

    public boolean isMultiCheck() {
        return isMultiCheck;
    }

    public CrosheCheckGroupHelper setMultiCheck(boolean multiCheck) {
        isMultiCheck = multiCheck;
        return this;
    }

    public ViewGroup getViewGroup() {
        return viewGroup;
    }

    public void setViewGroup(ViewGroup viewGroup) {
        this.viewGroup = viewGroup;
    }

    public boolean isChecked(int index) {
        return checkedIndex.contains(index);
    }

    public void check(int index) {
        onClick(getViewByIndex(index));
    }


    /**
     * 取消选中，当为多选的时候有效
     * @param index
     */
    public void unCheck(int index) {
        if (isMultiCheck) {
            checkedView.remove(getViewByIndex(index));
            checkedIndex.remove(index);
            notifyMultiUnCheck(index);
        }
    }

    public void clear() {
        checkedIndex.clear();
        checkedView.clear();
    }

    public boolean isRecord() {
        return isRecord;
    }

    public CrosheCheckGroupHelper setRecord(boolean record) {
        isRecord = record;
        record();
        return this;
    }

    @Override
    public void onChildViewAdded(View parent, View child) {
        refresh();
    }

    @Override
    public void onChildViewRemoved(View parent, View child) {

    }

    public Context getContext() {
        return context;
    }

    public CrosheCheckGroupHelper setContext(Context context) {
        this.context = context;
        return this;
    }


    public String getViewCode() {
        return viewCode;
    }

    public CrosheCheckGroupHelper setViewCode(String viewCode) {
        this.viewCode = viewCode;
        return this;
    }
}
