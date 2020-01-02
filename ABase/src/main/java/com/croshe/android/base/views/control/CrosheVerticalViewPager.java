package com.croshe.android.base.views.control;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 垂直方向
 * 安徽创息软件科技有限公司-技术支持  http://www.croshe.com
 */
public class CrosheVerticalViewPager extends ViewPager {

    private boolean isLocked;

    public CrosheVerticalViewPager(Context context) {
        super(context);
        isLocked = false;
        setPageTransformer(false, new DefaultTransformer());
    }

    public CrosheVerticalViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        isLocked = false;
        setPageTransformer(false, new DefaultTransformer());
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!isLocked) {
            try {
                boolean intercept = super.onInterceptTouchEvent(swapTouchEvent(ev));
                //If not intercept, touch event should not be swapped.
                swapTouchEvent(ev);
                return intercept;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return !isLocked && super.onTouchEvent(swapTouchEvent(event));
    }


    private MotionEvent swapTouchEvent(MotionEvent event) {
        float width = getWidth();
        float height = getHeight();

        float swappedX = (event.getY() / height) * width;
        float swappedY = (event.getX() / width) * height;

        event.setLocation(swappedX, swappedY);

        return event;
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


    public class DefaultTransformer implements ViewPager.PageTransformer {

        @Override
        public void transformPage(View view, float position) {
            float alpha = 0;
            if (0 <= position && position <= 1) {
                alpha = 1 - position;
            } else if (-1 < position && position < 0) {
                alpha = position + 1;
            }
            view.setAlpha(alpha);
            view.setTranslationX(view.getWidth() * -position);
            float yPosition = position * view.getHeight();
            view.setTranslationY(yPosition);
        }
    }
}
