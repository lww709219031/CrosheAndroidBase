package com.croshe.android.base.listener;

import com.croshe.android.base.views.list.CrosheRecyclerView;

/**
 * Created by Janesen on 2017/6/25.
 */

public interface OnCrosheLoadChangeListener {

    <T> void startLoadData(CrosheRecyclerView<T> crosheRecyclerView);

    <T> void onDataChange(CrosheRecyclerView<T> crosheRecyclerView);


    <T> void stopLoadData(CrosheRecyclerView<T> crosheRecyclerView);
}
