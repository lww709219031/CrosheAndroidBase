package com.croshe.android.base.views.layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.croshe.android.base.R;
import com.croshe.android.base.utils.DensityUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 圆角布局控件
 * 安徽创息软件科技有限公司-技术支持，http://wwww.croshe.com
 * Created by Janesen on 2017/6/27.
 */

public class CrosheRoundLayout extends FrameLayout {

    private float radius, leftTopRadius, leftBottomRadius, rightTopRadius, rightBottomRadius;

    private int strokeWidth;

    private int strokeColor;

    private float[] radiusArray = {0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f};


    private Paint paint,linePaint;
    private Paint pluPaint;

    public CrosheRoundLayout(@NonNull Context context) {
        super(context);
        initView();
    }

    public CrosheRoundLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initValue(attrs);
        initView();
    }

    public CrosheRoundLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initValue(attrs);
        initView();
    }


    public void initView() {
        setClipChildren(true);
        if (getBackground() == null) {
            setBackgroundColor(Color.TRANSPARENT);
        }

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));


        pluPaint = new Paint();
        pluPaint.setXfermode(null);


        initRoundPaintLine();
    }


    private void initValue(AttributeSet attrs) {
        leftTopRadius = 0f;
        rightBottomRadius = 0f;
        leftBottomRadius = 0f;
        rightTopRadius = 0f;

        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CrosheRoundLayout);
            radius = a.getDimensionPixelSize(R.styleable.CrosheRoundLayout_round_radius, DensityUtils.dip2px(5));
            leftTopRadius = a.getDimensionPixelSize(R.styleable.CrosheRoundLayout_round_leftTopRadius, (int) radius);
            rightTopRadius = a.getDimensionPixelSize(R.styleable.CrosheRoundLayout_round_rightTopRadius, (int) radius);
            leftBottomRadius = a.getDimensionPixelSize(R.styleable.CrosheRoundLayout_round_leftBottomRadius, (int) radius);
            rightBottomRadius = a.getDimensionPixelSize(R.styleable.CrosheRoundLayout_round_rightBottomRadius,(int) radius);
            strokeWidth = a.getDimensionPixelSize(R.styleable.CrosheRoundLayout_round_strokeWidth, 0);
            strokeColor = a.getColor(R.styleable.CrosheRoundLayout_round_strokeColor, Color.TRANSPARENT);
            a.recycle();
        }

        setRadius(leftTopRadius, rightTopRadius, rightBottomRadius, leftBottomRadius);
    }



    /**
     * 设置四个角的圆角半径
     */
    public void setRadius(float leftTop, float rightTop, float rightBottom, float leftBottom) {
        radiusArray[0] = leftTop;
        radiusArray[1] = leftTop;
        radiusArray[2] = rightTop;
        radiusArray[3] = rightTop;
        radiusArray[4] = rightBottom;
        radiusArray[5] = rightBottom;
        radiusArray[6] = leftBottom;
        radiusArray[7] = leftBottom;
        invalidate();
    }


    private void initRoundPaintLine() {
        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setStrokeWidth(strokeWidth);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setColor(strokeColor);
    }



    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.saveLayer(new RectF(0, 0, getWidth(), getHeight()), pluPaint, Canvas.ALL_SAVE_FLAG);
        super.dispatchDraw(canvas);
        Path path = new Path();
        path.addRoundRect(new RectF(0, 0, getWidth(), getHeight()), radiusArray, Path.Direction.CW);
        canvas.drawPath(path, linePaint);
        canvas.drawPath(path, paint);
        canvas.restore();
    }



    /**
     * 刷新布局
     */
    public void refreshDraw() {
        postInvalidate();
    }

    public float getLeftTopRadius() {
        return leftTopRadius;
    }

    public void setLeftTopRadius(float leftTopRadius) {
        this.leftTopRadius = leftTopRadius;
        refreshDraw();
    }

    public float getLeftBottomRadius() {
        return leftBottomRadius;
    }

    public void setLeftBottomRadius(float leftBottomRadius) {
        this.leftBottomRadius = leftBottomRadius;
        refreshDraw();
    }

    public float getRightTopRadius() {
        return rightTopRadius;
    }

    public void setRightTopRadius(float rightTopRadius) {
        this.rightTopRadius = rightTopRadius;
        refreshDraw();
    }

    public float getRightBottomRadius() {
        return rightBottomRadius;
    }

    public void setRightBottomRadius(float rightBottomRadius) {
        this.rightBottomRadius = rightBottomRadius;
        refreshDraw();
    }

    public int getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
        refreshDraw();
    }

    public int getStrokeColor() {
        return strokeColor;
    }

    public void setStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
        refreshDraw();
    }
}
