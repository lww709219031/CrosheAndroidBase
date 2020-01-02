package com.croshe.android.base.views.control;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 *  安徽创息软件科技有限公司-技术支持  http://www.croshe.com
 */
public class CrosheViewPager extends ViewPager {
    private boolean isLocked;
    private Boolean animalOnChange;

    public CrosheViewPager(Context context) {
        super(context);
        isLocked = false;
    }

    public CrosheViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        isLocked = false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!isLocked) {
            try {
                return super.onInterceptTouchEvent(ev);
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return !isLocked && super.onTouchEvent(event);
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        if (animalOnChange != null) {
            smoothScroll = animalOnChange;
        }
        super.setCurrentItem(item, smoothScroll);
    }

    public void toggleLock() {
        isLocked = !isLocked;
    }

    public void setLocked(boolean isLocked) {
        this.isLocked = isLocked;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public Boolean getAnimalOnChange() {
        return animalOnChange;
    }

    public void setAnimalOnChange(Boolean animalOnChange) {
        this.animalOnChange = animalOnChange;
    }
}
