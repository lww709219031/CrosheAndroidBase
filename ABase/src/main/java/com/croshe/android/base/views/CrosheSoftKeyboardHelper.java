package com.croshe.android.base.views;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;

import com.croshe.android.base.utils.BaseAppUtils;
import com.croshe.android.base.utils.DensityUtils;
import com.croshe.android.base.utils.NumberUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * 键盘监听工具
 * 安徽创息软件科技有限公司-技术支持，http://wwww.croshe.com
 * Created by Janesen on 2017/6/30.
 */
public class CrosheSoftKeyboardHelper implements ViewTreeObserver.OnGlobalLayoutListener {

    public interface SoftKeyboardStateListener {
        void onSoftKeyboardOpened(int keyboardHeightInPx);
        void onSoftKeyboardClosed();
    }

    private final List<SoftKeyboardStateListener> listeners = new LinkedList<SoftKeyboardStateListener>();
    private final View activityRootView;
    private int        lastSoftKeyboardHeightInPx;
    private boolean    isSoftKeyboardOpened;

    private int currDisplayHeight;


    public CrosheSoftKeyboardHelper(View activityRootView) {
        this(activityRootView, false);
    }

    public CrosheSoftKeyboardHelper(View activityRootView, boolean isSoftKeyboardOpened) {
        this.activityRootView     = activityRootView;
        this.isSoftKeyboardOpened = isSoftKeyboardOpened;
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    public void onGlobalLayout() {
        final Rect r = new Rect();
        //r will be populated with the coordinates of your view that area still visible.
        activityRootView.getWindowVisibleDisplayFrame(r);

        int displayHeight = r.bottom - r.top;


        if (currDisplayHeight - displayHeight > 100 && currDisplayHeight != r.right) {
            if (!isSoftKeyboardOpened) {
                isSoftKeyboardOpened = true;
                notifyOnSoftKeyboardOpened(Math.abs(currDisplayHeight - displayHeight));
            }
        } else {
            if (isSoftKeyboardOpened) {
                isSoftKeyboardOpened = false;
                notifyOnSoftKeyboardClosed();
            }
            currDisplayHeight = displayHeight;
        }
//
//        final int heightDiff = activityRootView.getRootView().getHeight() - (r.bottom - r.top);
//        if (!isSoftKeyboardOpened && heightDiff > 300) { // if more than 100 pixels, its probably a keyboard...
//            isSoftKeyboardOpened = true;
//            notifyOnSoftKeyboardOpened(heightDiff);
//        } else if (isSoftKeyboardOpened && heightDiff < 300) {
//            isSoftKeyboardOpened = false;
//            notifyOnSoftKeyboardClosed();
//        }
    }

    public void setIsSoftKeyboardOpened(boolean isSoftKeyboardOpened) {
        this.isSoftKeyboardOpened = isSoftKeyboardOpened;
    }

    public boolean isSoftKeyboardOpened() {
        return isSoftKeyboardOpened;
    }

    /**
     * Default value is zero (0)
     * @return last saved keyboard height in px
     */
    public int getLastSoftKeyboardHeightInPx() {
        return lastSoftKeyboardHeightInPx;
    }

    public void addSoftKeyboardStateListener(SoftKeyboardStateListener listener) {
        listeners.add(listener);
    }

    public void removeSoftKeyboardStateListener(SoftKeyboardStateListener listener) {
        listeners.remove(listener);
    }

    private void notifyOnSoftKeyboardOpened(int keyboardHeightInPx) {
        this.lastSoftKeyboardHeightInPx = keyboardHeightInPx;

        for (SoftKeyboardStateListener listener : listeners) {
            if (listener != null) {
                listener.onSoftKeyboardOpened(keyboardHeightInPx);
            }
        }
        BaseAppUtils.setCache("keyboardHeight", String.valueOf(keyboardHeightInPx));
    }

    private void notifyOnSoftKeyboardClosed() {
        for (SoftKeyboardStateListener listener : listeners) {
            if (listener != null) {
                listener.onSoftKeyboardClosed();
            }
        }
    }


    /**
     * 获得记录的键盘高度
     * @return
     */
    public static int getKeyboardHeight() {
        int keyboardHeight = NumberUtils.formatToInt(BaseAppUtils.getCache("keyboardHeight", String.valueOf(DensityUtils.dip2px(260))));
        if (keyboardHeight < DensityUtils.dip2px(100)) {
            return DensityUtils.dip2px(260);
        }
        return keyboardHeight;
    }
}