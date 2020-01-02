package com.croshe.android.base.views.menu;

import com.croshe.android.base.listener.OnCrosheMenuClick;
import com.croshe.android.base.utils.MD5Encrypt;

/**
 * Created by Janesen on 2017/6/25.
 */

public class CrosheMenuItem {

    public static CrosheMenuItem newInstance(String title, OnCrosheMenuClick onClickListener) {
        return newInstance(title, null, -1, onClickListener);
    }

    public static CrosheMenuItem newInstance(String title, String subTitle, OnCrosheMenuClick onClickListener) {
        return newInstance(title, subTitle, -1, onClickListener);
    }

    public static CrosheMenuItem newInstance(String title, int iconResource, OnCrosheMenuClick onClickListener) {
        return newInstance(title, null, iconResource, onClickListener);
    }

    public static CrosheMenuItem newInstance(String title, String subTitle,
                                             int iconResource, OnCrosheMenuClick onClickListener) {
        return newInstance(title, subTitle, true, iconResource, onClickListener);
    }

    public static CrosheMenuItem newInstance(String title, String subTitle,
                                             boolean closeAnim,
                                             int iconResource, OnCrosheMenuClick onClickListener) {
        CrosheMenuItem crosheMenuItem = new CrosheMenuItem();
        crosheMenuItem
                .setTitle(title)
                .setCloseAnim(closeAnim)
                .setSubTitle(subTitle)
                .setIconResource(iconResource)
                .setOnClickListener(onClickListener);
        return crosheMenuItem;
    }


    private String code;
    private String title;
    private String subTitle;
    private int iconResource;
    private int iconColor;
    private OnCrosheMenuClick onClickListener;
    private int titleColor;
    private float fontSize;

    private boolean hidden;
    private boolean closeAnim = true;

    private CrosheMenuItem() {
    }


    public String getTitle() {
        return title;
    }

    public CrosheMenuItem setTitle(String title) {
        this.title = title;
        return this;
    }

    public int getIconResource() {
        return iconResource;
    }


    /**
     * 设置标题的图标
     *
     * @param iconResource
     * @return
     */
    public CrosheMenuItem setIconResource(int iconResource) {
        this.iconResource = iconResource;
        return this;
    }

    public OnCrosheMenuClick getOnClickListener() {
        return onClickListener;
    }

    public void setOnClickListener(OnCrosheMenuClick onClickListener) {
        this.onClickListener = onClickListener;
    }

    public String getCode() {
        return MD5Encrypt.MD5(title);
    }

    public String getSubTitle() {
        return subTitle;
    }

    public CrosheMenuItem setSubTitle(String subTitle) {
        this.subTitle = subTitle;
        return this;
    }

    public int getTitleColor() {
        return titleColor;
    }

    /**
     * 设置标题的颜色
     *
     * @param titleColor
     * @return
     */
    public CrosheMenuItem setTitleColor(int titleColor) {
        this.titleColor = titleColor;
        return this;
    }

    public float getFontSize() {
        return fontSize;
    }

    /**
     * 设置标题的大小
     *
     * @param fontSize
     * @return
     */
    public CrosheMenuItem setFontSize(float fontSize) {
        this.fontSize = fontSize;
        return this;
    }


    public boolean isCloseAnim() {
        return closeAnim;
    }

    public CrosheMenuItem setCloseAnim(boolean closeAnim) {
        this.closeAnim = closeAnim;
        return this;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }


    public int getIconColor() {
        return iconColor;
    }

    public CrosheMenuItem setIconColor(int iconColor) {
        this.iconColor = iconColor;
        return this;
    }

    public void callOnClick() {
        if (getOnClickListener() != null) {
            getOnClickListener().onClick(this, null);
        }
    }
}
