package com.croshe.android.base.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.croshe.android.base.R;
import com.croshe.android.base.extend.adapter.CroshePageFragmentAdapter;
import com.croshe.android.base.views.control.CrosheViewPager;

import java.util.ArrayList;
import java.util.List;

import me.majiajie.pagerbottomtabstrip.NavigationController;
import me.majiajie.pagerbottomtabstrip.PageBottomTabLayout;

/**
 * 底部tab切换Activity
 * 安徽创息软件科技有限公司-技术支持，http://wwww.croshe.com
 * Created by Janesen on 2017/7/8 12:50.
 */
public abstract class CrosheBottomTabActivity extends CrosheBaseActivity {

  protected NavigationController mNavigationController;
  protected PageBottomTabLayout pageBottomTabLayout;
  protected CrosheViewPager viewPager;

  private View tabLine;
  private List<TabItem> tabItems = new ArrayList<>();


  private int tabLineColor = Color.parseColor("#444444");
  private int selectColor;
  private int unSelectColor;
  private int tabBackgroundColor;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.android_base_activity_bottom_tab);
    initView();
    checkPermission();
  }

  public void initView() {
    pageBottomTabLayout = getView(R.id.pageBottomTabLayout);
    viewPager = getView(R.id.android_base_viewPager);
    tabLine = getView(R.id.tabLine);
  }


  private void initActivity() {
    CroshePageFragmentAdapter selfFragmentPagerAdapter
            = new CroshePageFragmentAdapter(getSupportFragmentManager());
    initFragments(selfFragmentPagerAdapter.getFragments());
    viewPager.setAdapter(selfFragmentPagerAdapter);
    viewPager.setOffscreenPageLimit(4);

    PageBottomTabLayout.MaterialBuilder materialBuilder = pageBottomTabLayout.material();
    for (TabItem tabItem : tabItems) {
      materialBuilder.addItem(tabItem.getIconResource(),
              tabItem.getTitle(), getSelectColor());
    }
    mNavigationController = materialBuilder
            .setDefaultColor(getUnSelectColor())
            .build();
    if (mNavigationController != null) {
      mNavigationController.setupWithViewPager(viewPager);
    }

    tabLine.setBackgroundColor(tabLineColor);
    pageBottomTabLayout.setBackgroundColor(tabBackgroundColor);
  }


  @Override
  public void onAttachedToWindow() {
    super.onAttachedToWindow();
    initActivity();
  }



  /**
   * 初始化Fragment
   * @param fragments
   */
  public abstract void initFragments(List<Fragment> fragments);


  public int getSelectColor() {
    return selectColor;
  }

  /**
   * 选中时的颜色
   * @param selectColor
   */
  public void setSelectColor(int selectColor) {
    this.selectColor = selectColor;
  }

  public int getUnSelectColor() {
    return unSelectColor;
  }

  /**
   * 未选中时的颜色
   * @param unSelectColor
   */
  public void setUnSelectColor(int unSelectColor) {
    this.unSelectColor = unSelectColor;
  }

  /**
   * 添加底部tab
   * @param title
   * @param iconResource
   */
  public void addTabItem(String title, int iconResource) {
    tabItems.add(TabItem.getInstance(title, iconResource));
  }

  /**
   * 添加底部
   * @param tabItem
   */
  public void addTabItem(TabItem tabItem) {
    tabItems.add(tabItem);
  }

  public int getTabLineColor() {
    return tabLineColor;
  }

  public void setTabLineColor(int tabLineColor) {
    this.tabLineColor = tabLineColor;
  }

  public void setTouchViewPager(boolean touch) {
    viewPager.setLocked(!touch);
  }


  public int getTabBackgroundColor() {
    return tabBackgroundColor;
  }

  public void setTabBackgroundColor(int tabBackgroundColor) {
    this.tabBackgroundColor = tabBackgroundColor;
  }

  public PageBottomTabLayout getPageBottomTabLayout() {
    return pageBottomTabLayout;
  }

  public void setPageBottomTabLayout(PageBottomTabLayout pageBottomTabLayout) {
    this.pageBottomTabLayout = pageBottomTabLayout;
  }

  public CrosheViewPager getViewPager() {
    return viewPager;
  }

  public void setViewPager(CrosheViewPager viewPager) {
    this.viewPager = viewPager;
  }

  public static class  TabItem{
    private String title;
    private int iconResource;

    public static TabItem getInstance(String title, int iconResource) {
      TabItem tabItem = new TabItem();
      tabItem.setTitle(title);
      tabItem.setIconResource(iconResource);
      return tabItem;
    }

    public String getTitle() {
      return title;
    }

    public void setTitle(String title) {
      this.title = title;
    }

    public int getIconResource() {
      return iconResource;
    }

    public void setIconResource(int iconResource) {
      this.iconResource = iconResource;
    }
  }
}
