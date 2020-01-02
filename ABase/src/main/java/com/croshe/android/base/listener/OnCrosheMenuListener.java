package com.croshe.android.base.listener;

import com.croshe.android.base.views.menu.CrosheMenuItem;
import com.croshe.android.base.views.menu.CroshePopupMenu;

/**
 * Created by Janesen on 2017/6/25.
 */

public interface OnCrosheMenuListener {
    void onBeforeShow(CroshePopupMenu croshePopupMenu);

    void onAfterShow(CroshePopupMenu croshePopupMenu);

    void onBeforeClose(CroshePopupMenu croshePopupMenu);

    void close(CroshePopupMenu croshePopupMenu);

    void onItemClick(CrosheMenuItem item);
}
