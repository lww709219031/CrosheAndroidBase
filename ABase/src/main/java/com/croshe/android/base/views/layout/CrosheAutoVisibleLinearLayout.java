package com.croshe.android.base.views.layout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * 自动隐藏显示的linearlayout布局
 * 安徽创息软件科技有限公司-技术支持，http://wwww.croshe.com
 * Created by Janesen on 2017/7/27 17:50.
 */
public class CrosheAutoVisibleLinearLayout extends LinearLayout {
    public CrosheAutoVisibleLinearLayout(Context context) {
        super(context);
    }

    public CrosheAutoVisibleLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public CrosheAutoVisibleLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        if (getBackground() == null) {
            setBackgroundColor(Color.TRANSPARENT);
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        checkChild();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

    }

    private void checkChild() {
        int goneCount = 0;
        for (int i = 0; i < this.getChildCount(); i++) {
            if (getChildAt(i).getVisibility() == GONE) {
                goneCount++;
            }
        }
        if (getChildCount() == goneCount) {
            setVisibility(GONE);
        } else {
            setVisibility(VISIBLE);
        }
    }
}
