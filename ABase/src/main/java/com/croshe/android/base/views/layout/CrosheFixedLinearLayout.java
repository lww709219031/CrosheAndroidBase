package com.croshe.android.base.views.layout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.croshe.android.base.utils.ViewUtils;

/**
 * 安徽创息软件科技有限公司-技术支持，http://wwww.croshe.com
 * Created by Janesen on 2017/7/19 15:46.
 */
public class CrosheFixedLinearLayout extends LinearLayout{

    private int mHeight;

    public CrosheFixedLinearLayout(Context context) {
        super(context);
        initView();
    }

    public CrosheFixedLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public CrosheFixedLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
        if (getMeasuredHeight() != 0 && getMeasuredHeight() != mHeight) {
            mHeight = getMeasuredHeight();
            ViewGroup.LayoutParams layoutParams = getLayoutParams();
            layoutParams.height = mHeight;
            setLayoutParams(layoutParams);
        }
    }

}
