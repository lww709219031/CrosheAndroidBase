package com.croshe.android.base.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.croshe.android.base.R;
import com.croshe.android.base.extend.adapter.CroshePageAdapter;
import com.croshe.android.base.views.control.CrosheViewPager;
import com.flyco.tablayout.SlidingTabLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 头部tab切换viewpager类
 * 安徽创息软件科技有限公司-技术支持，http://wwww.croshe.com
 * Created by Janesen on 2017/7/10 13:40.
 */
public class CrosheHeadTabView extends FrameLayout {
  private SlidingTabLayout slidingTabLayout;
  private CrosheViewPager crosheViewPager;
  private CroshePageAdapter pageAdapter;
  private boolean tabSpaceEqual;

  private List fragments = new ArrayList<>();
  private List<String> titles = new ArrayList<>();

  public CrosheHeadTabView(@NonNull Context context) {
    super(context);
    initView();
  }

  public CrosheHeadTabView(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    initView();
  }

  public CrosheHeadTabView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initView();
  }



  public void initView() {
    LayoutInflater.from(getContext()).inflate(R.layout.android_base_panel_head_tab, this);

    pageAdapter = new CroshePageAdapter();
    slidingTabLayout = (SlidingTabLayout)findViewById(R.id.android_base_tabLayout);
    crosheViewPager = (CrosheViewPager)findViewById(R.id.android_base_viewPager);

    pageAdapter.getItems().addAll(fragments);
    pageAdapter.getTitles().addAll(titles);


    slidingTabLayout.setTabSpaceEqual(titles.size() <= 4);

    if (tabSpaceEqual) {
      slidingTabLayout.setTabSpaceEqual(tabSpaceEqual);
    }

    crosheViewPager.setAdapter(pageAdapter);
    slidingTabLayout.setViewPager(crosheViewPager);
  }


  /**
   * 添加选项
   * @param title
   * @param item
   */
  public void addItem(String title, View item) {
    titles.add(title);
    fragments.add(item);
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
}
