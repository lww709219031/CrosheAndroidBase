package com.croshe.android.base.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.SlidingPaneLayout;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.croshe.android.base.AConfig;
import com.croshe.android.base.R;
import com.croshe.android.base.utils.DensityUtils;
import com.croshe.android.base.utils.ExitApplication;
import com.croshe.android.base.views.layout.CrosheSlidingPaneLayout;

import java.lang.reflect.Field;

/**
 * 安徽创息软件科技有限公司-技术支持  http://www.croshe.com
 */
public class CrosheBaseSlidingActivity extends CrosheBaseActivity implements SlidingPaneLayout.PanelSlideListener {
    private static final int DEFAULT_TRANSLATION_X = 300;
    private static final int DEFAULT_SHADOW_WIDTH = 30;
    private View mPreDecorView;
    protected CrosheSlidingPaneLayout mSlidingPaneLayout;
    private FrameLayout mContentView;
    private View mShadowView;
    protected int windowBackground = Color.WHITE;
    /**
     * Flag of whether SlidingPaneLayout initialize success.
     * If success, use SlidingPaneLayout as contentView,
     * otherwise use the contentView which set in Activity#setContentView().
     */
    protected boolean mInitSlidingSuccess;
    protected boolean isCheckBack = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            mSlidingPaneLayout = new CrosheSlidingPaneLayout(this);
            mSlidingPaneLayout.setMaxTouchX(AConfig.getSlideMaxTouchX());
            Field mOverhangSize = SlidingPaneLayout.class.getDeclaredField("mOverhangSize");
            mOverhangSize.setAccessible(true);
            mOverhangSize.set(mSlidingPaneLayout, 0);
            mSlidingPaneLayout.setPanelSlideListener(this);
            mSlidingPaneLayout.setSliderFadeColor(getResources().getColor(android.R.color.transparent));
            mInitSlidingSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
            mInitSlidingSuccess = false;
        }
        super.onCreate(savedInstanceState);

        this.getWindow().getDecorView().setBackgroundColor(windowBackground);
        mInitSlidingSuccess = Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH;
        if (!mInitSlidingSuccess) {
            return;
        }

        // frontContainer
        LinearLayout frontContainer = new LinearLayout(this);
        frontContainer.setOrientation(LinearLayout.HORIZONTAL);
        frontContainer.setBackgroundColor(Color.TRANSPARENT);
        frontContainer.setClickable(true);
        frontContainer.setLayoutParams(new ViewGroup.LayoutParams(getWindowManager().getDefaultDisplay().getWidth() + DEFAULT_SHADOW_WIDTH, ViewGroup.LayoutParams.MATCH_PARENT));
        // contentView
        mContentView = new FrameLayout(this);
        mContentView.setClickable(true);
        mContentView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        // shadowView
        mShadowView = new ImageView(this);
        mShadowView.setBackgroundResource(R.drawable.android_base_shadow_left);

        mShadowView.setLayoutParams(new LinearLayout.LayoutParams(DEFAULT_SHADOW_WIDTH, LinearLayout.LayoutParams.MATCH_PARENT));
        // add views to frontContainer
        frontContainer.addView(mShadowView);
        frontContainer.addView(mContentView);
        frontContainer.setTranslationX(-DEFAULT_SHADOW_WIDTH);

        mSlidingPaneLayout.addView(new View(this), 0);
        mSlidingPaneLayout.addView(frontContainer, 1);

        this.getWindow().setNavigationBarColor(Color.WHITE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Activity beforeActivity = ExitApplication.getBeforeActivity(this);
        if (beforeActivity != null) {
            mPreDecorView = beforeActivity.getWindow().getDecorView();
        }

    }

    @Override
    protected void onDestroy() {
        if (mInitSlidingSuccess) {
            if (mPreDecorView != null) {
                mPreDecorView.setTranslationX(0);
            }
        }
        super.onDestroy();
    }

    @Override
    public void setContentView(int id) {
        setContentView(getLayoutInflater().inflate(id, null));
    }

    @Override
    public void setContentView(View v) {
        setContentView(v, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    public void setContentView(View v, ViewGroup.LayoutParams params) {
        View statusHolder = null;
        if (v instanceof CoordinatorLayout) {
            v.setFitsSystemWindows(true);
            fullScreen(false);

            statusHolder = new View(getContext());
            statusHolder.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    DensityUtils.getStatusBarHeight(getContext())));
            statusHolder.setBackgroundColor(getDarkColorPrimary());

        }
        if (mInitSlidingSuccess) {
            mContentView.removeAllViews();
            if (statusHolder != null) {
                mContentView.addView(statusHolder);
            }
            mContentView.addView(v, params);

            super.setContentView(mSlidingPaneLayout, params);
        } else {
            super.setContentView(v, params);
        }
    }


    @Override
    public void onPanelClosed(View panel) {
        this.getWindow().getDecorView().setBackgroundColor(windowBackground);
        mContentView.setBackgroundColor(Color.TRANSPARENT);
        if (mPreDecorView != null) {
            mPreDecorView.setTranslationX(0);
        }
    }

    @Override
    public void onPanelOpened(View panel) {
        isCheckBack = false;
        finish();
        overridePendingTransition(0, 0);
    }

    @Override
    public void onPanelSlide(View panel, float slideOffset) {
        // Add any effect here
        this.getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
        mContentView.setBackgroundColor(windowBackground);

        if (mPreDecorView != null) {
            mPreDecorView.setTranslationX(slideOffset * (float) DEFAULT_TRANSLATION_X - (float) DEFAULT_TRANSLATION_X);
        }
        mShadowView.setAlpha(1 - slideOffset);
    }


    public void setSlideEnable(boolean slideEnable) {
        mSlidingPaneLayout.setSlidEnable(slideEnable);
    }

    public boolean isSlideEnable() {
        return mSlidingPaneLayout.isSlidEnable();
    }


    public int getResourceColor(int colorId) {
        return getResources().getColor(colorId);
    }

}
