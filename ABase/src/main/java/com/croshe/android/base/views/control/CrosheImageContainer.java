package com.croshe.android.base.views.control;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;

/**
 * 安徽创息软件科技有限公司-技术支持，http://wwww.croshe.com
 * Created by Janesen on 2017/11/7 19:18.
 */
public class CrosheImageContainer extends FrameLayout {
    private ViewDragHelper dragHelper;
    private boolean close;
    private float lastY,lastX;
    private int vDirection;
    private int hDirection;
    private int backgroundAlpha;

    public CrosheImageContainer(@NonNull Context context) {
        super(context);
    }

    public CrosheImageContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CrosheImageContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        initView();
    }

    public void initView() {


        dragHelper = ViewDragHelper.create(this, new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                if (close) return false;
                if (child instanceof CrosheViewPager) {
                    return true;
                }
                return false;
            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                if (top <= 0) {
                    vDirection = 0;
                } else {
                    vDirection = 1;
                }
                return top;
            }

            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                if (left <= 0) {
                    hDirection = 0;
                } else {
                    hDirection = 1;
                }
                return left;
            }

            @Override
            public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
                super.onViewPositionChanged(changedView, left, top, dx, dy);

                float present = 1 - Math.abs(top)/ (getHeight() * 0.5f);
                if (getContext() instanceof Activity) {
                    backgroundAlpha = Math.max(Math.min((int) (255 * present), 255), 0);
                    ((Activity) getContext()).getWindow().getDecorView().getBackground().setAlpha(backgroundAlpha);
                }


                float ratioPresent = 1 - Math.abs(top) / (getHeight() * 2f);
                float ratio = ratioPresent * 360;
                if (lastX < getWidth() / 2) {
                    if (top < 0) {
                        changedView.setRotation(-ratio);
                    } else {
                        changedView.setRotation(ratio);
                    }
                } else {
                    if (top < 0) {
                        changedView.setRotation(ratio);
                    } else {
                        changedView.setRotation(-ratio);
                    }
                }

                if (vDirection == 0) {
                    if (changedView.getRotation() < 0) {
                        hDirection = 1;
                    }else{
                        hDirection = 0;
                    }
                }else{
                    if (changedView.getRotation() < 0) {
                        hDirection = 0;
                    }else{
                        hDirection = 1;
                    }
                }



                float maxScale = Math.min(present, 1.0f);
                float minScale = Math.max(0.2f, maxScale);

                changedView.setScaleX(minScale);
                changedView.setScaleY(minScale);
            }

            @Override
            public void onViewDragStateChanged(int state) {
                super.onViewDragStateChanged(state);
            }

            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                super.onViewReleased(releasedChild, xvel, yvel);
                if (releasedChild.getScaleX() <= 0.8f
                        || releasedChild.getScaleY() <= 0.8f) {
                    closeActivity(releasedChild);
                    return;
                }
                dragHelper.settleCapturedViewAt(0, 0);
                postInvalidate();
            }

            @Override
            public int getViewHorizontalDragRange(View child) {
                return Integer.MAX_VALUE;
            }

            @Override
            public int getViewVerticalDragRange(View child) {
                return Integer.MAX_VALUE;
            }

        });
        dragHelper.setMinVelocity(1f);

    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT_WATCH) {
            return super.onInterceptTouchEvent(ev);
        }
        final int action = MotionEventCompat.getActionMasked(ev);
        if (action == MotionEvent.ACTION_DOWN) {
            lastY = ev.getRawY();
            lastX = ev.getRawX();
        } else if (action == MotionEvent.ACTION_MOVE) {
            float spanY = Math.abs(ev.getRawY() - lastY);
            if (spanY < 180 || ev.getPointerCount() > 1) {
                return super.onInterceptTouchEvent(ev);
            }
        }
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            dragHelper.cancel();
            return false;
        }
        return dragHelper.shouldInterceptTouchEvent(ev);

    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        dragHelper.processTouchEvent(ev);
        return true;
    }


    public void closeActivity(final View dragView) {
        close = true;
        Animation scaleAnimation = computeAnimation();
        scaleAnimation.setDuration(200);
        scaleAnimation.setFillAfter(true);

        ValueAnimator animator = ObjectAnimator
                .ofInt(backgroundAlpha, 0)
                .setDuration(200);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int cVal = (int) animation.getAnimatedValue();
                ((Activity) getContext()).getWindow().getDecorView().getBackground().setAlpha(cVal);
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                setVisibility(GONE);
                ((Activity) getContext()).getWindow().getDecorView().setAlpha(0);
                ((Activity) getContext()).finish();
                ((Activity) getContext()).overridePendingTransition(0, 0);
            }
        });

        animator.start();
        dragView.startAnimation(scaleAnimation);
    }


    private Animation computeAnimation() {
        ScaleAnimation scaleAnimation = null;
        if (hDirection == 0) {
            //left
            if (vDirection == 0) {//top
                scaleAnimation = new ScaleAnimation(1, 0,
                        1, 0,
                        Animation.RELATIVE_TO_SELF, 0,
                        Animation.RELATIVE_TO_SELF, 0);
            } else {//bottom
                scaleAnimation = new ScaleAnimation(1, 0,
                        1, 0,
                        Animation.RELATIVE_TO_SELF, 0f,
                        Animation.RELATIVE_TO_SELF, 1);
            }
        } else {
            //right
            if (vDirection == 0) {
                scaleAnimation = new ScaleAnimation(1, 0,
                        1, 0,
                        Animation.RELATIVE_TO_SELF, 1f,
                        Animation.RELATIVE_TO_SELF, 0);
            } else {
                scaleAnimation = new ScaleAnimation(1, 0,
                        1, 0,
                        Animation.RELATIVE_TO_SELF, 1f,
                        Animation.RELATIVE_TO_SELF, 1f);
            }
        }

        return scaleAnimation;
    }

    @Override
    public void computeScroll() {
        //固定写法
        //此方法用于自动滚动,比如自动回滚到默认位置.
        if (dragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

}
