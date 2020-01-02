package com.croshe.android.base.views.layout;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.croshe.android.base.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 安徽创息软件科技有限公司-技术支持，http://wwww.croshe.com
 * Created by Janesen on 2017/6/27.
 */

public class CrosheActivityLayout extends FrameLayout {

    private FrameLayout llContainer;
    public CrosheActivityLayout(@NonNull Context context) {
        super(context);
    }

    public CrosheActivityLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CrosheActivityLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void initView() {

        List<View> child = new ArrayList<>();
        for (int i = 0; i < this.getChildCount(); i++) {
            child.add(getChildAt(i));
        }
        removeAllViews();

        LayoutInflater.from(getContext()).inflate(R.layout.android_base_activity_layout, this);
        llContainer = (FrameLayout) findViewById(R.id.flContainer);

        for (View view : child) {
            llContainer.addView(view);
        }
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initView();
    }
}
