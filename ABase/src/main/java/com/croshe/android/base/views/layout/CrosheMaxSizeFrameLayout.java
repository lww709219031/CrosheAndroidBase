package com.croshe.android.base.views.layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.croshe.android.base.R;
import com.croshe.android.base.utils.DensityUtils;

/**
 * 安徽创息软件科技有限公司-技术支持，http://wwww.croshe.com
 * Created by Janesen on 2017/7/19 15:46.
 */
public class CrosheMaxSizeFrameLayout extends FrameLayout{

    private int maxHeight,maxWidth;
    private int mHeight,mWidth;

    public CrosheMaxSizeFrameLayout(Context context) {
        super(context);
        initView();
    }

    public CrosheMaxSizeFrameLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initValue(attrs);
        initView();
    }

    public CrosheMaxSizeFrameLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initValue(attrs);
        initView();
    }

    private void initView() {
        if (getBackground() == null) {
            setBackgroundColor(Color.TRANSPARENT);
        }
    }

    private void initValue(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CrosheMaxSizeFrameLayout);
            maxHeight = a.getDimensionPixelSize(R.styleable.CrosheMaxSizeFrameLayout_maxHeight,-1);
            maxWidth = a.getDimensionPixelSize(R.styleable.CrosheMaxSizeFrameLayout_maxWidth, -1);
            a.recycle();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (getMeasuredHeight() != 0 && getMeasuredHeight() != mHeight) {
            mHeight = getMeasuredHeight();
            if (maxHeight > 0 && mHeight > maxHeight) {
                ViewGroup.LayoutParams layoutParams = getLayoutParams();
                layoutParams.height = maxHeight;
                setLayoutParams(layoutParams);
            }
        }
        if (getMeasuredWidth() != 0 && getMeasuredWidth() != mWidth) {
            mWidth = getMeasuredWidth();
            if (maxWidth > 0 && mWidth > maxWidth) {
                ViewGroup.LayoutParams layoutParams = getLayoutParams();
                layoutParams.width = maxWidth;
                setLayoutParams(layoutParams);
            }
        }
    }

}
