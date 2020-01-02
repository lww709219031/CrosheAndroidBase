package com.croshe.android.base.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.croshe.android.base.R;
import com.croshe.android.base.extend.adapter.CroshePageFragmentAdapter;
import com.croshe.android.base.utils.DensityUtils;
import com.croshe.android.base.views.control.CrosheViewPager;
import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.widget.MsgView;

import java.util.ArrayList;

/**
 * 头部tab切换viewpager类
 * 安徽创息软件科技有限公司-技术支持，http://wwww.croshe.com
 * Created by Janesen on 2017/7/10 13:40.
 */
public class CrosheHeadTabFragment extends CrosheBaseFragment {
    private SlidingTabLayout slidingTabLayout;
    private CrosheViewPager crosheViewPager;
    private CroshePageFragmentAdapter pageFragmentAdapter;

    private boolean tabSpaceEqual;
    private int textSize = 18;

    private int textSelectedColor = R.color.colorPrimary;
    private int indicatorColor = R.color.colorPrimary;

    private ArrayList<Fragment> fragments = new ArrayList<>();
    private ArrayList<String> titles = new ArrayList<>();

    private OnPageScrollListener onPageScrollListener;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.android_base_panel_head_tab, null);
        if (tabSpaceEqual) {
            v = inflater.inflate(R.layout.android_base_panel_head_equal_tab, null);
        }
        slidingTabLayout = (SlidingTabLayout) v.findViewById(R.id.android_base_tabLayout);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        pageFragmentAdapter = new CroshePageFragmentAdapter(getChildFragmentManager());

        crosheViewPager = (CrosheViewPager) getView().findViewById(R.id.android_base_viewPager);
        crosheViewPager.setOffscreenPageLimit(4);


        pageFragmentAdapter.getFragments().addAll(fragments);
        pageFragmentAdapter.getTitles().addAll(titles);

        slidingTabLayout.setTextsize(getTextSize());
        slidingTabLayout.setTextSelectColor(textSelectedColor);
        slidingTabLayout.setIndicatorColor(indicatorColor);

        crosheViewPager.setAdapter(pageFragmentAdapter);
        slidingTabLayout.setViewPager(crosheViewPager);
        crosheViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (onPageScrollListener != null) {
                    onPageScrollListener.onPageScrolled(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 设置标题
     *
     * @param title
     * @param index
     */
    public void setTitle(String title, int index) {
        pageFragmentAdapter.getTitles().set(index, title);
        pageFragmentAdapter.notifyDataSetChanged();
        slidingTabLayout.notifyDataSetChanged();
    }


    /**
     * 设置标题的小消息
     *
     * @param msg
     * @param position
     */
    public void setTitleMsg(String msg, int position) {
        MsgView msgView = slidingTabLayout.getMsgView(position);
        msgView.setText(msg);
        msgView.setVisibility(View.VISIBLE);
        int padding = DensityUtils.dip2px(5);
        msgView.setPadding(padding, DensityUtils.dip2px(2), padding, DensityUtils.dip2px(2));
    }


    /**
     * 添加选项
     *
     * @param title
     * @param fragment
     */
    public void addItem(String title, Fragment fragment) {
        titles.add(title);
        fragments.add(fragment);
    }


    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public SlidingTabLayout getSlidingTabLayout() {
        return slidingTabLayout;
    }

    public void setSlidingTabLayout(SlidingTabLayout slidingTabLayout) {
        this.slidingTabLayout = slidingTabLayout;
    }


    public boolean isTabSpaceEqual() {
        return tabSpaceEqual;
    }

    public void setTabSpaceEqual(boolean tabSpaceEqual) {
        this.tabSpaceEqual = tabSpaceEqual;
    }

    public CrosheViewPager getCrosheViewPager() {
        return crosheViewPager;
    }

    public void setCrosheViewPager(CrosheViewPager crosheViewPager) {
        this.crosheViewPager = crosheViewPager;
    }

    public ArrayList<Fragment> getFragments() {
        return fragments;
    }

    public void setFragments(ArrayList<Fragment> fragments) {
        this.fragments = fragments;
    }

    public ArrayList<String> getTitles() {
        return titles;
    }

    public void setTitles(ArrayList<String> titles) {
        this.titles = titles;
    }

    public void setTextSelectColor(int resourceColor) {
        textSelectedColor = resourceColor;
    }

    public void setIndicatorColor(int resourceColor) {
        indicatorColor = resourceColor;
    }

    public OnPageScrollListener getOnPageScrollListener() {
        return onPageScrollListener;
    }

    public void setOnPageScrollListener(OnPageScrollListener onPageScrollListener) {
        this.onPageScrollListener = onPageScrollListener;
    }

    public interface OnPageScrollListener {
        void onPageScrolled(int position);
    }
}
