package com.croshe.android.base.views.layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.croshe.android.base.R;
import com.croshe.android.base.utils.DensityUtils;

import org.apache.commons.lang3.StringUtils;

/**
 * 横向布局的功能
 * <p>
 * 安徽创息软件科技有限公司-技术支持，http://wwww.croshe.com
 * Created by Janesen on 2017/6/26.
 */

public class CrosheVMenuLayout extends FrameLayout {


    private String text,subtext,tipNumber;
    private int leftIcon;

    private float textSize, subtextSize;
    private int textColor, subtextColor,
            topLineWidth,bottomLineWidth,leftLineWidth, rightLineWidth,
            lineColor,lineMarginTop,lineMarginBottom,lineMarginRight,lineMarginLeft;

    private int topIconWidth, topIconHeight;


    private ImageView leftImgIcon;
    private TextView tvTitle, tvSubtitle, tvNumber;

    private View topLineView,bottomLineView,leftLineView, rightLineView;


    public CrosheVMenuLayout(Context context) {
        super(context);
        initView();
    }

    public CrosheVMenuLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initValue(attrs);
        initView();
    }

    public CrosheVMenuLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initValue(attrs);
        initView();
    }


    public void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.android_base_item_vlayout_menu, this);
        if (getBackground() == null) {
            setBackgroundResource(R.drawable.android_base_btn_default_background);
        }
        setClickable(true);
        leftImgIcon = (ImageView) findViewById(R.id.android_base_leftImgIcon);
        tvTitle = (TextView) findViewById(R.id.android_base_tvTitle);
        tvSubtitle = (TextView) findViewById(R.id.android_base_tvSubtitle);
        tvNumber = (TextView) findViewById(R.id.android_base_tvNumber);


        leftLineView = findViewById(R.id.android_base_leftLineView);
        rightLineView = findViewById(R.id.rightLineView);
        topLineView = findViewById(R.id.android_base_topLineView);
        bottomLineView = findViewById(R.id.bottomLineView);


        initValue();
    }



    public void initValue() {


        tvTitle.setText(text);
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        tvTitle.setTextColor(textColor);


        tvSubtitle.setText(subtext);
        tvSubtitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, subtextSize);
        tvSubtitle.setTextColor(subtextColor);


        tvNumber.setText(tipNumber);

        if (StringUtils.isEmpty(tipNumber)) {
            tvNumber.setVisibility(GONE);
        }else{
            tvNumber.setVisibility(VISIBLE);
        }

        if (StringUtils.isEmpty(subtext)) {
            tvSubtitle.setVisibility(GONE);
        }else{
            tvSubtitle.setVisibility(VISIBLE);
        }



        LinearLayout.LayoutParams topLayoutParams = (LinearLayout.LayoutParams) topLineView.getLayoutParams();
        topLayoutParams.height = topLineWidth;
        topLayoutParams.setMargins(lineMarginLeft, lineMarginTop, lineMarginRight, lineMarginBottom);
        topLineView.setBackgroundColor(lineColor);


        LinearLayout.LayoutParams bottomLayoutParams = (LinearLayout.LayoutParams) bottomLineView.getLayoutParams();
        bottomLayoutParams.height = bottomLineWidth;
        bottomLayoutParams.setMargins(lineMarginLeft, lineMarginTop, lineMarginRight, lineMarginBottom);
        bottomLineView.setBackgroundColor(lineColor);

        LinearLayout.LayoutParams leftLayoutParams = (LinearLayout.LayoutParams) leftLineView.getLayoutParams();
        leftLayoutParams.width = leftLineWidth;
        leftLayoutParams.setMargins(lineMarginLeft, lineMarginTop, lineMarginRight, lineMarginBottom);
        leftLineView.setBackgroundColor(lineColor);

        LinearLayout.LayoutParams rightLayoutParams = (LinearLayout.LayoutParams) rightLineView.getLayoutParams();
        rightLayoutParams.width = rightLineWidth;
        rightLayoutParams.setMargins(lineMarginLeft, lineMarginTop, lineMarginRight, lineMarginBottom);
        rightLineView.setBackgroundColor(lineColor);

        if (leftLineWidth == 0) {
            leftLineView.setVisibility(GONE);
        }else{
            leftLineView.setVisibility(VISIBLE);
        }

        if (topLineWidth == 0) {
            topLineView.setVisibility(GONE);
        }else{
            topLineView.setVisibility(VISIBLE);
        }

        if (rightLineWidth == 0) {
            rightLineView.setVisibility(GONE);
        }else{
            rightLineView.setVisibility(VISIBLE);
        }

        if (bottomLineWidth == 0) {
            bottomLineView.setVisibility(GONE);
        }else{
            bottomLineView.setVisibility(VISIBLE);
        }


        if (leftIcon != -1) {
            leftImgIcon.setImageResource(leftIcon);
        }

        FrameLayout.LayoutParams iconLayoutParams = (FrameLayout.LayoutParams) leftImgIcon.getLayoutParams();
        iconLayoutParams.width = topIconWidth;
        iconLayoutParams.height = topIconHeight;
        leftImgIcon.setLayoutParams(iconLayoutParams);

    }

    private void initValue(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CrosheVMenuLayout);
            leftIcon = a.getResourceId(R.styleable.CrosheVMenuLayout_vmenu_topIcon, -1);
            text = a.getString(R.styleable.CrosheVMenuLayout_vmenu_text);
            subtext = a.getString(R.styleable.CrosheVMenuLayout_vmenu_subtext);

            textSize = a.getDimensionPixelSize(R.styleable.CrosheVMenuLayout_vmenu_textSize, DensityUtils.dip2px(16));
            subtextSize = a.getDimensionPixelSize(R.styleable.CrosheVMenuLayout_vmenu_subtextSize, DensityUtils.dip2px(16));


            topIconWidth = a.getDimensionPixelSize(R.styleable.CrosheVMenuLayout_vmenu_topIconWidth, DensityUtils.dip2px(24));
            topIconHeight = a.getDimensionPixelSize(R.styleable.CrosheVMenuLayout_vmenu_topIconHeight, DensityUtils.dip2px(24));



            textColor = a.getColor(R.styleable.CrosheVMenuLayout_vmenu_textColor, Color.parseColor("#444444"));
            subtextColor = a.getColor(R.styleable.CrosheVMenuLayout_vmenu_subtextColor, Color.parseColor("#cccccc"));


            tipNumber = a.getString(R.styleable.CrosheVMenuLayout_vmenu_tipNumber);

            topLineWidth = a.getDimensionPixelSize(R.styleable.CrosheVMenuLayout_vmenu_topLineWidth,0);
            bottomLineWidth = a.getDimensionPixelSize(R.styleable.CrosheVMenuLayout_vmenu_bottomLineWidth, 0);
            leftLineWidth = a.getDimensionPixelSize(R.styleable.CrosheVMenuLayout_vmenu_leftLineWidth, 0);
            rightLineWidth = a.getDimensionPixelSize(R.styleable.CrosheVMenuLayout_vmenu_rightLineWidth, 0);
            lineColor = a.getColor(R.styleable.CrosheVMenuLayout_vmenu_lineColor, Color.parseColor("#cccccc"));


            lineMarginLeft = a.getDimensionPixelSize(R.styleable.CrosheVMenuLayout_vmenu_lineMarginLeft, 0);
            lineMarginTop = a.getDimensionPixelSize(R.styleable.CrosheVMenuLayout_vmenu_lineMarginTop,0);
            lineMarginRight = a.getDimensionPixelSize(R.styleable.CrosheVMenuLayout_vmenu_lineMarginRight, 0);
            lineMarginBottom= a.getDimensionPixelSize(R.styleable.CrosheVMenuLayout_vmenu_lineMarginBottom, 0);

            a.recycle();
        }
    }



    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        initValue();
    }

    public String getSubtext() {
        return subtext;
    }

    public void setSubtext(String subtext) {
        this.subtext = subtext;
        initValue();
    }

    public String getTipNumber() {
        return tipNumber;
    }

    public void setTipNumber(String tipNumber) {
        this.tipNumber = tipNumber;
        initValue();
    }

    public int getLeftIcon() {
        return leftIcon;
    }

    public void setLeftIcon(int leftIcon) {
        this.leftIcon = leftIcon;
        initValue();
    }


    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
        initValue();
    }

    public float getSubtextSize() {
        return subtextSize;
    }

    public void setSubtextSize(float subtextSize) {
        this.subtextSize = subtextSize;
        initValue();
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        initValue();
    }

    public int getSubtextColor() {
        return subtextColor;
    }

    public void setSubtextColor(int subtextColor) {
        this.subtextColor = subtextColor;
        initValue();
    }

}
