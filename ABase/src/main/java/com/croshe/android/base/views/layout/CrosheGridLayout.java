package com.croshe.android.base.views.layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.croshe.android.base.R;
import com.croshe.android.base.utils.DensityUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 网格布局
 * 安徽创息软件科技有限公司-技术支持，http://wwww.croshe.com
 * Created by Janesen on 2017/6/27.
 */

public class CrosheGridLayout extends LinearLayout {

    private int hCount, vCount;
    private int vLineWidth, hLineWidth;
    private int lineColor;

    private List<View> views = new ArrayList<>();


    public CrosheGridLayout(Context context) {
        super(context);
        initView();
    }

    public CrosheGridLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initValue(attrs);
        initView();
    }

    public CrosheGridLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initValue(attrs);
        initView();
    }


    public void initView() {
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);

    }


    private void initValue(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CrosheGridLayout);
            vCount = a.getInt(R.styleable.CrosheGridLayout_grid_vCount, 3);
            hCount = a.getInt(R.styleable.CrosheGridLayout_grid_hCount, 3);
            vLineWidth = a.getDimensionPixelSize(R.styleable.CrosheGridLayout_grid_vLineWidth, 0);
            hLineWidth = a.getDimensionPixelSize(R.styleable.CrosheGridLayout_grid_hLineWidth, 0);
            lineColor = a.getColor(R.styleable.CrosheGridLayout_grid_lineColor, Color.parseColor("#cccccc"));
            a.recycle();
        }

    }

    /**
     * 重新布局子控件
     */
    public void relayoutChild() {
        this.removeAllViews();

        if (views.size() > 0) {
            for (int i = 0; i < vCount; i++) {
                LinearLayout linearLayout = new LinearLayout(getContext());
                linearLayout.setOrientation(HORIZONTAL);
                linearLayout.setGravity(Gravity.CENTER);
                LinearLayout.LayoutParams vlp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                vlp.weight = 1.0f / vCount;
                vlp.gravity = Gravity.CENTER;


                boolean isContinue = true;
                for (int j = 0; j < hCount; j++) {

                    View child;
                    int index = j + i * hCount;
                    LinearLayout.LayoutParams hlp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
                    hlp.weight = 1.0f / hCount;
                    hlp.gravity = Gravity.CENTER_VERTICAL;
                    if (index < views.size()) {
                        child = views.get(index);
                        if (child.getVisibility() == GONE) {
                            child = new View(getContext());
                        }
                    } else {
                        child = new View(getContext());
                        isContinue = false;
                    }

                    if (child.getParent() != null) {
                        ViewGroup viewGroup = (ViewGroup) child.getParent();
                        viewGroup.removeView(child);
                    }


                    child.setLayoutParams(hlp);
                    linearLayout.addView(child);

                    if (vLineWidth != 0 && j != hCount - 1) {
                        View hLine = new View(getContext());
                        hLine.setBackgroundColor(lineColor);
                        LinearLayout.LayoutParams hLineLp = new LinearLayout.LayoutParams(vLineWidth, ViewGroup.LayoutParams.MATCH_PARENT);
                        hLine.setLayoutParams(hLineLp);
                        linearLayout.addView(hLine);
                    }
                }
                linearLayout.setLayoutParams(vlp);
                this.addView(linearLayout);

                if (hLineWidth != 0 && i != vCount - 1) {
                    View vLine = new View(getContext());
                    vLine.setBackgroundColor(lineColor);
                    LinearLayout.LayoutParams hLineLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, hLineWidth);
                    vLine.setLayoutParams(hLineLp);
                    this.addView(vLine);
                }

                if (!isContinue) break;
            }
        }
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        views.clear();
        for (int i = 0; i < getChildCount(); i++) {
            views.add(getChildAt(i));
        }
        relayoutChild();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d("STAG", "onDraw");
    }


    public void addGridView(View child) {
        views.add(child);
        relayoutChild();
    }

    public void addGridView(View child, int index) {
        views.add(index, child);
        relayoutChild();
    }


    public void removeGridView(View child) {
        views.remove(child);
        relayoutChild();
    }

    public void removeGridView(int index) {
        views.remove(index);
        relayoutChild();
    }


    public int indexOfGridView(View child) {
        return views.indexOf(child);
    }


    public View getGridView(int index) {
        return views.get(index);
    }


}
