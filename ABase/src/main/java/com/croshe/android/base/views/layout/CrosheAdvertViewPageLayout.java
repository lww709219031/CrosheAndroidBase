package com.croshe.android.base.views.layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.croshe.android.base.R;
import com.croshe.android.base.extend.adapter.CroshePageAdapter;
import com.croshe.android.base.listener.OnCrosheAdvertListener;
import com.rd.PageIndicatorView;

import java.util.ArrayList;
import java.util.List;

/**
 * 常用的广告栏布局
 * 安徽创息软件科技有限公司-技术支持，http://wwww.croshe.com
 * Created by Janesen on 2017/7/8 10:29.
 */
public class CrosheAdvertViewPageLayout<T> extends FrameLayout {
    private OnCrosheAdvertListener onCrosheAdvertListener;
    private ViewPager viewPager;
    private PageIndicatorView pageIndicatorView;
    private OnCrosheAdvertListener.AdvertCallBack<T> advertCallBack;

    private int dotSelectColor,dotUnSelectColor;
    public CrosheAdvertViewPageLayout(@NonNull Context context) {
        super(context);
        initView();

    }

    public CrosheAdvertViewPageLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initValue(attrs);
        initView();
    }

    public CrosheAdvertViewPageLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initValue(attrs);
        initView();
    }


    public void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.android_base_panel_viewpager,this);
        viewPager = (ViewPager) findViewById(R.id.android_base_advertViewPager);
        pageIndicatorView = (PageIndicatorView)findViewById(R.id.android_base_pageIndicatorView);

        pageIndicatorView.setSelectedColor(dotSelectColor);
        pageIndicatorView.setUnselectedColor(dotUnSelectColor);
        pageIndicatorView.setViewPager(viewPager);
    }

    private void initValue(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CrosheAdvertViewPageLayout);
            dotSelectColor = a.getColor(R.styleable.CrosheAdvertViewPageLayout_dot_selectColor, -1);
            dotUnSelectColor = a.getColor(R.styleable.CrosheAdvertViewPageLayout_dot_unSelectColor,  -1);
            a.recycle();
        }
        advertCallBack = new OnCrosheAdvertListener.AdvertCallBack<T>() {
            @Override
            public void loadData(List<T> data) {
                List<View> views = new ArrayList<>();
                for (int i = 0; i < data.size(); i++) {
                    ImageView imageView = new ImageView(getContext());
                    imageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    if (onCrosheAdvertListener.onRenderAdvertView(data.get(i), i, imageView)) {
                        views.add(imageView);
                    }
                }
                CroshePageAdapter croshePageAdapter = new CroshePageAdapter(views);
                viewPager.setAdapter(croshePageAdapter);
                pageIndicatorView.setCount(data.size());
                croshePageAdapter.notifyDataSetChanged();
            }
        };
    }


    public void initData() {
        if (onCrosheAdvertListener != null) {
            onCrosheAdvertListener.getAdvertData(advertCallBack);
        }
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        initData();
    }


    public OnCrosheAdvertListener getOnCrosheAdvertListener() {
        return onCrosheAdvertListener;
    }

    public void setOnCrosheAdvertListener(OnCrosheAdvertListener<T> onCrosheAdvertListener) {
        this.onCrosheAdvertListener = onCrosheAdvertListener;
    }

    public OnCrosheAdvertListener.AdvertCallBack<T> getAdvertCallBack() {
        return advertCallBack;
    }
}
