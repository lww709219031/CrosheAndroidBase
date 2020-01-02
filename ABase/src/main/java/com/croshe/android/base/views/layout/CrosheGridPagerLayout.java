package com.croshe.android.base.views.layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.croshe.android.base.R;
import com.croshe.android.base.extend.adapter.CroshePageAdapter;
import com.rd.PageIndicatorView;

import java.util.ArrayList;
import java.util.List;

/**
 * 网格分页布局
 * 安徽创息软件科技有限公司-技术支持，http://wwww.croshe.com
 * Created by Janesen on 2017/7/18 19:39.
 */
public class CrosheGridPagerLayout extends FrameLayout {


    private PageIndicatorView pageIndicatorView;
    private ViewPager viewPager;

    private List<View> itemViews = new ArrayList<>();
    private int columnCount;
    private int rowCount;
    private int dotSelectColor,dotUnSelectColor;

    private int itemPadding;

    private ViewPager.OnPageChangeListener onPageChangeListener;


    public CrosheGridPagerLayout(@NonNull Context context) {
        super(context);
        initView();
    }

    public CrosheGridPagerLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initValue(attrs);
        initView();
    }

    public CrosheGridPagerLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initValue(attrs);
        initView();
    }


    public void initView() {

    }

    private void initValue(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CrosheGridPagerLayout);
            columnCount = a.getInt(R.styleable.CrosheGridPagerLayout_grid_columnCount, 3);
            rowCount = a.getInt(R.styleable.CrosheGridPagerLayout_grid_rowCount, 3);
            dotSelectColor = a.getColor(R.styleable.CrosheGridPagerLayout_grid_selectColor, -1);
            dotUnSelectColor = a.getColor(R.styleable.CrosheGridPagerLayout_grid_unSelectColor,  -1);

            a.recycle();
        }

    }


    private void initItemViews() {
        for (int i = 0; i < this.getChildCount(); i++) {
            View view = this.getChildAt(i);
            if (view.getId() == R.id.android_base_grid_pager) {
                continue;
            }
            itemViews.add(view);
        }
    }


    public void relayoutChild() {
        super.removeAllViews();

        int pageSize = columnCount * rowCount;
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.android_base_panel_gridpager, null);
        rootView.setId(R.id.android_base_grid_pager);

        super.addView(rootView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        viewPager = (ViewPager) rootView.findViewById(R.id.android_base_gridViewPager);

        pageIndicatorView = (PageIndicatorView)rootView.findViewById(R.id.android_base_pageIndicatorView);
        if (dotSelectColor != -1) {
            pageIndicatorView.setSelectedColor(dotSelectColor);
        }
        if (dotUnSelectColor != -1) {
            pageIndicatorView.setUnselectedColor(dotUnSelectColor);
        }

        List<View> views = new ArrayList<>();
        int page = itemViews.size() % pageSize == 0 ? itemViews.size() / pageSize : itemViews.size() / pageSize + 1;

        for (int i = 0; i < page; i++) {
            GridLayout gridLayout = new GridLayout(getContext());
            gridLayout.setColumnCount(columnCount);
            gridLayout.setRowCount(rowCount);
            gridLayout.setPadding(itemPadding, itemPadding, itemPadding, itemPadding);

            for (int j = 0; j < pageSize; j++) {
                //使用Spec定义子控件的位置和比重
                GridLayout.Spec rowSpec = GridLayout.spec(j / columnCount, 1f);
                GridLayout.Spec columnSpec = GridLayout.spec(j % columnCount, 1f);

                //将Spec传入GridLayout.LayoutParams并设置宽高为0，必须设置宽高，否则视图异常
                GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams(rowSpec, columnSpec);
                layoutParams.height = 0;
                layoutParams.width = 0;

                int childIndex = j + i * pageSize;
                if (childIndex < itemViews.size()) {
                    View childView = itemViews.get(childIndex);
                    if (childView.getParent() != null) {
                        ViewGroup viewGroup = (ViewGroup) childView.getParent();
                        viewGroup.removeView(childView);
                    }
                    gridLayout.addView(childView, layoutParams);
                } else {
                    View childView = new View(getContext());
                    gridLayout.addView(childView, layoutParams);
                }
            }
            views.add(gridLayout);
        }
        viewPager.setAdapter(new CroshePageAdapter(views));
        viewPager.addOnPageChangeListener(onPageChangeListener);

        pageIndicatorView.setViewPager(viewPager);
        if (page == 1) {
            pageIndicatorView.setVisibility(GONE);
        }else{
            pageIndicatorView.setVisibility(VISIBLE);
        }

    }


    public boolean isLastPage() {
        return viewPager.getCurrentItem() == viewPager.getAdapter().getCount() - 1;
    }


    public boolean isFirstPage() {
        return viewPager.getCurrentItem() == 0;
    }


    @Override
    public void addView(View child) {
        itemViews.add(child);
    }

    @Override
    public void removeView(View view) {
        itemViews.remove(view);
    }


    @Override
    public void addView(View child, int index) {
        if (index >= 0) {
            itemViews.add(index, child);
        }else{
            itemViews.add(child);
        }
    }


    @Override
    public void removeViewAt(int index) {
        itemViews.remove(index);
    }


    @Override
    public void removeAllViews() {
        for (View itemView : itemViews) {
            if (itemView.getParent() != null) {
                ViewGroup viewGroup = (ViewGroup) itemView.getParent();
                viewGroup.removeView(itemView);
            }
        }
        itemViews.clear();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        initItemViews();
        relayoutChild();
    }


    public List<View> getItemViews() {
        return itemViews;
    }


    public int getColumnCount() {
        return columnCount;
    }

    public void setColumnCount(int columnCount) {
        this.columnCount = columnCount;
    }

    public int getRowCount() {
        return rowCount;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    public int getDotSelectColor() {
        return dotSelectColor;
    }

    public void setDotSelectColor(int dotSelectColor) {
        this.dotSelectColor = dotSelectColor;
        if (dotSelectColor != -1) {
            pageIndicatorView.setSelectedColor(dotSelectColor);
        }
    }

    public int getDotUnSelectColor() {
        return dotUnSelectColor;
    }

    public void setDotUnSelectColor(int dotUnSelectColor) {
        this.dotUnSelectColor = dotUnSelectColor;
        if (dotUnSelectColor != -1) {
            pageIndicatorView.setUnselectedColor(dotUnSelectColor);
        }
    }

    public PageIndicatorView getPageIndicatorView() {
        return pageIndicatorView;
    }

    public void setPageIndicatorView(PageIndicatorView pageIndicatorView) {
        this.pageIndicatorView = pageIndicatorView;
    }

    public ViewPager getViewPager() {
        return viewPager;
    }

    public void setViewPager(ViewPager viewPager) {
        this.viewPager = viewPager;
    }

    public ViewPager.OnPageChangeListener getOnPageChangeListener() {
        return onPageChangeListener;
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        this.onPageChangeListener = onPageChangeListener;
    }

    public int getItemPadding() {
        return itemPadding;
    }

    public void setItemPadding(int itemPadding) {
        this.itemPadding = itemPadding;
    }
}
