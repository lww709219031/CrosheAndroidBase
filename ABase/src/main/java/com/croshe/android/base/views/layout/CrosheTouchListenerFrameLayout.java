package com.croshe.android.base.views.layout;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * Created by Janesen on 15/11/23.
 */
public class CrosheTouchListenerFrameLayout extends FrameLayout {

    private boolean isInterceptTouch;
    private boolean isStillIntercept;
    private boolean isStillListener;
    private OnTouchActionListener onTouchActionListener;

    private boolean flag;

    public CrosheTouchListenerFrameLayout(Context context) {
        super(context);
    }

    public CrosheTouchListenerFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public CrosheTouchListenerFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        flag=false;
        if (isInterceptTouch()) {
            switch (MotionEventCompat.getActionMasked(ev)) {
                case MotionEvent.ACTION_DOWN:
                    if (onTouchActionListener != null) {
                        onTouchActionListener.onTouchDown(ev);
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                    if (onTouchActionListener != null) {
                        onTouchActionListener.onTouchCancel(ev);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (onTouchActionListener != null) {
                        onTouchActionListener.onTouchUp(ev);
                    }
                    if (!isStillIntercept) {
                        setIsInterceptTouch(false);
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (onTouchActionListener != null) {
                        onTouchActionListener.onTouchMove(ev);
                    }
                    break;
            }
            flag=true;
            return true;
        }
        if (isStillListener) {
            switch (MotionEventCompat.getActionMasked(ev)) {
                case MotionEvent.ACTION_DOWN:
                    if (onTouchActionListener != null) {
                        onTouchActionListener.onTouchDown(ev);
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                    if (onTouchActionListener != null) {
                        onTouchActionListener.onTouchCancel(ev);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (onTouchActionListener != null) {
                        onTouchActionListener.onTouchUp(ev);
                    }
                    if (!isStillIntercept) {
                        setIsInterceptTouch(false);
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (onTouchActionListener != null) {
                        onTouchActionListener.onTouchMove(ev);
                    }
                    break;
            }
        }
        return super.dispatchTouchEvent(ev);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(flag){
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    public boolean isInterceptTouch() {
        return isInterceptTouch;
    }

    public void setIsInterceptTouch(boolean isInterceptTouch) {
        this.isInterceptTouch = isInterceptTouch;
    }

    public OnTouchActionListener getOnTouchActionListener() {
        return onTouchActionListener;
    }

    public void setOnTouchActionListener(OnTouchActionListener onTouchUp) {
        this.onTouchActionListener = onTouchUp;
    }

    public boolean isStillIntercept() {
        return isStillIntercept;
    }

    public void setStillIntercept(boolean stillIntercept) {
        isStillIntercept = stillIntercept;
    }

    public boolean isStillListener() {
        return isStillListener;
    }

    public void setStillListener(boolean stillListener) {
        isStillListener = stillListener;
    }

    public interface OnTouchActionListener {
        void onTouchDown(MotionEvent ev);

        void onTouchUp(MotionEvent ev);

        void onTouchMove(MotionEvent ev);

        void onTouchCancel(MotionEvent ev);
    }


    public static class SimpleTouchListener implements OnTouchActionListener {
        @Override
        public void onTouchDown(MotionEvent ev) {

        }

        @Override
        public void onTouchUp(MotionEvent ev) {

        }

        @Override
        public void onTouchMove(MotionEvent ev) {

        }

        @Override
        public void onTouchCancel(MotionEvent ev) {

        }
    }


}
