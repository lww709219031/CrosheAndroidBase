package com.croshe.android.base.views.control;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.croshe.android.base.R;
import com.croshe.android.base.utils.DensityUtils;


/**
 * 安徽创息软件科技有限公司-技术支持，http://wwww.croshe.com
 * Created by Janesen on 2017/6/29.
 */

public class CrosheBubbleLayout extends FrameLayout {


    private Paint paint;
    private Paint strokePaint;
    private float arrowWidth;
    private float radius;
    private float arrowHeight;
    private float arrowPosition;
    private float strokeWidth;
    private int strokeColor, bubbleColor;
    private ArrowDirection arrowDirection;
    private Paint pluPaint;


    public CrosheBubbleLayout(@NonNull Context context) {
        super(context);
        initView();
    }

    public CrosheBubbleLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initValue(attrs);
        initView();
    }

    public CrosheBubbleLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
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
        paint.setColor(bubbleColor);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));

        strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(strokeWidth);
        strokePaint.setStrokeJoin(Paint.Join.ROUND);
        strokePaint.setStrokeCap(Paint.Cap.SQUARE);
        strokePaint.setColor(strokeColor);
        strokePaint.setAntiAlias(true);

        pluPaint = new Paint();
        pluPaint.setXfermode(null);

    }


    private void initValue(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CrosheBubbleLayout);
            arrowPosition = a.getDimensionPixelSize(R.styleable.CrosheBubbleLayout_bubble_arrowPosition, DensityUtils.dip2px(10));
            arrowWidth = a.getDimensionPixelSize(R.styleable.CrosheBubbleLayout_bubble_arrowWidth, DensityUtils.dip2px(10));
            arrowHeight = a.getDimensionPixelSize(R.styleable.CrosheBubbleLayout_bubble_arrowHeight, DensityUtils.dip2px(10));
            strokeWidth = a.getDimensionPixelSize(R.styleable.CrosheBubbleLayout_bubble_strokeWidth, 0);
            radius = a.getDimensionPixelSize(R.styleable.CrosheBubbleLayout_bubble_radius, DensityUtils.dip2px(10));
            strokeColor = a.getColor(R.styleable.CrosheBubbleLayout_bubble_strokeColor, Color.parseColor("#cccccc"));
            bubbleColor = a.getColor(R.styleable.CrosheBubbleLayout_bubble_bubbleColor, Color.parseColor("#cccccc"));
            arrowDirection = fromInt(a.getInt(R.styleable.CrosheBubbleLayout_bubble_arrowDirection, ArrowDirection.LEFT.ordinal()));
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        postInvalidate();
    }


    @Override
    protected void dispatchDraw(Canvas canvas) {
        Path path = initPath(new RectF(0, 0, getWidth(), getHeight()), 0);
        if (getHeight() < DensityUtils.getHeightInPx()) {
            canvas.saveLayer(new RectF(0, 0, getWidth(), getHeight()), pluPaint, Canvas.ALL_SAVE_FLAG);
            super.dispatchDraw(canvas);
            if (strokeWidth > 0) {
                canvas.drawPath(path, strokePaint);
            }
            canvas.drawPath(path, paint);
            canvas.restore();
        } else {
            canvas.clipPath(path);
            super.dispatchDraw(canvas);
            if (strokeWidth > 0) {
                canvas.drawPath(path, strokePaint);
            }
        }
    }

    /**
     * 刷新布局
     */
    public void refreshDraw() {
        postInvalidate();
    }


    public float getArrowWidth() {
        return arrowWidth;
    }

    public void setArrowWidth(float arrowWidth) {
        this.arrowWidth = arrowWidth;
        refreshDraw();
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
        refreshDraw();

    }

    public float getArrowHeight() {
        return arrowHeight;
    }

    public void setArrowHeight(float arrowHeight) {
        this.arrowHeight = arrowHeight;
        refreshDraw();
    }

    public float getArrowPosition() {
        return arrowPosition;
    }

    public void setArrowPosition(float arrowPosition) {
        this.arrowPosition = arrowPosition;
        refreshDraw();
    }

    public float getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(float strokeWidth) {
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

    public int getBubbleColor() {
        return bubbleColor;
    }

    public void setBubbleColor(int bubbleColor) {
        this.bubbleColor = bubbleColor;
        refreshDraw();
    }

    public ArrowDirection getArrowDirection() {
        return arrowDirection;
    }

    public void setArrowDirection(ArrowDirection arrowDirection) {
        this.arrowDirection = arrowDirection;
        refreshDraw();
    }

    private Path initPath(RectF rectF, float strokeWidth) {
        switch (arrowDirection) {
            case LEFT:
                if (radius <= 0) {
                    return initLeftSquarePath(rectF, strokeWidth);
                }
                if (strokeWidth > 0 && strokeWidth > radius) {
                    return initLeftSquarePath(rectF, strokeWidth);
                }
                return initLeftRoundedPath(rectF, strokeWidth);
            case TOP:
                if (radius <= 0) {
                    return initTopSquarePath(rectF, strokeWidth);
                }

                if (strokeWidth > 0 && strokeWidth > radius) {
                    return initTopSquarePath(rectF, strokeWidth);
                }

                return initTopRoundedPath(rectF, strokeWidth);
            case RIGHT:
                if (radius <= 0) {
                    return initRightSquarePath(rectF, strokeWidth);
                }

                if (strokeWidth > 0 && strokeWidth > radius) {
                    return initRightSquarePath(rectF, strokeWidth);
                }
                return initRightRoundedPath(rectF, strokeWidth);
            case BOTTOM:
                if (radius <= 0) {
                    return initBottomSquarePath(rectF, strokeWidth);
                }

                if (strokeWidth > 0 && strokeWidth > radius) {
                    return initBottomSquarePath(rectF, strokeWidth);
                }
                return initBottomRoundedPath(rectF, strokeWidth);
        }
        return null;
    }

    private Path initLeftRoundedPath(RectF rect, float strokeWidth) {
        Path path = new Path();

        path.moveTo(arrowWidth + rect.left + radius + strokeWidth, rect.top + strokeWidth);

        path.lineTo(rect.width() - radius - strokeWidth, rect.top + strokeWidth);

        path.arcTo(new RectF(rect.right - radius, rect.top + strokeWidth, rect.right - strokeWidth,
                radius + rect.top), 270, 90);

        path.lineTo(rect.right - strokeWidth, rect.bottom - radius - strokeWidth);
        path.arcTo(new RectF(rect.right - radius, rect.bottom - radius,
                rect.right - strokeWidth, rect.bottom - strokeWidth), 0, 90);


        path.lineTo(rect.left + arrowWidth + radius + strokeWidth, rect.bottom - strokeWidth);


        path.arcTo(new RectF(rect.left + arrowWidth + strokeWidth, rect.bottom - radius,
                radius + rect.left + arrowWidth, rect.bottom - strokeWidth), 90, 90);

        path.lineTo(rect.left + arrowWidth + strokeWidth, arrowHeight + arrowPosition - (strokeWidth / 2));

        path.lineTo(rect.left + strokeWidth + strokeWidth, arrowPosition + arrowHeight / 2);


        path.lineTo(rect.left + arrowWidth + strokeWidth, arrowPosition + (strokeWidth / 2));

        path.lineTo(rect.left + arrowWidth + strokeWidth, rect.top + radius + strokeWidth);

        path.arcTo(new RectF(rect.left + arrowWidth + strokeWidth, rect.top + strokeWidth, radius
                + rect.left + arrowWidth, radius + rect.top), 180, 90);

        path.close();
        return path;
    }

    private Path initLeftSquarePath(RectF rect, float strokeWidth) {
        Path path = new Path();
        path.moveTo(arrowWidth + rect.left + strokeWidth, rect.top + strokeWidth);
        path.lineTo(rect.width() - strokeWidth, rect.top + strokeWidth);

        path.lineTo(rect.right - strokeWidth, rect.bottom - strokeWidth);

        path.lineTo(rect.left + arrowWidth + strokeWidth, rect.bottom - strokeWidth);


        path.lineTo(rect.left + arrowWidth + strokeWidth, arrowHeight + arrowPosition - (strokeWidth / 2));
        path.lineTo(rect.left + strokeWidth + strokeWidth, arrowPosition + arrowHeight / 2);
        path.lineTo(rect.left + arrowWidth + strokeWidth, arrowPosition + (strokeWidth / 2));

        path.lineTo(rect.left + arrowWidth + strokeWidth, rect.top + strokeWidth);


        path.close();
        return path;

    }


    private Path initTopRoundedPath(RectF rect, float strokeWidth) {
        Path path = new Path();
        path.moveTo(rect.left + Math.min(arrowPosition, radius) + strokeWidth, rect.top + arrowHeight + strokeWidth);
        path.lineTo(rect.left + arrowPosition + (strokeWidth / 2), rect.top + arrowHeight + strokeWidth);
        path.lineTo(rect.left + arrowWidth / 2 + arrowPosition, rect.top + strokeWidth + strokeWidth);
        path.lineTo(rect.left + arrowWidth + arrowPosition - (strokeWidth / 2), rect.top + arrowHeight + strokeWidth);
        path.lineTo(rect.right - radius - strokeWidth, rect.top + arrowHeight + strokeWidth);

        path.arcTo(new RectF(rect.right - radius,
                rect.top + arrowHeight + strokeWidth, rect.right - strokeWidth, radius + rect.top + arrowHeight), 270, 90);
        path.lineTo(rect.right - strokeWidth, rect.bottom - radius - strokeWidth);

        path.arcTo(new RectF(rect.right - radius, rect.bottom - radius,
                rect.right - strokeWidth, rect.bottom - strokeWidth), 0, 90);
        path.lineTo(rect.left + radius + strokeWidth, rect.bottom - strokeWidth);

        path.arcTo(new RectF(rect.left + strokeWidth, rect.bottom - radius,
                radius + rect.left, rect.bottom - strokeWidth), 90, 90);

        path.lineTo(rect.left + strokeWidth, rect.top + arrowHeight + radius + strokeWidth);

        path.arcTo(new RectF(rect.left + strokeWidth, rect.top + arrowHeight + strokeWidth, radius
                + rect.left, radius + rect.top + arrowHeight), 180, 90);

        path.close();
        return path;
    }

    private Path initTopSquarePath(RectF rect, float strokeWidth) {
        Path path = new Path();
        path.moveTo(rect.left + arrowPosition + strokeWidth, rect.top + arrowHeight + strokeWidth);

        path.lineTo(rect.left + arrowPosition + (strokeWidth / 2), rect.top + arrowHeight + strokeWidth);
        path.lineTo(rect.left + arrowWidth / 2 + arrowPosition, rect.top + strokeWidth + strokeWidth);
        path.lineTo(rect.left + arrowWidth + arrowPosition - (strokeWidth / 2), rect.top + arrowHeight + strokeWidth);
        path.lineTo(rect.right - strokeWidth, rect.top + arrowHeight + strokeWidth);

        path.lineTo(rect.right - strokeWidth, rect.bottom - strokeWidth);

        path.lineTo(rect.left + strokeWidth, rect.bottom - strokeWidth);


        path.lineTo(rect.left + strokeWidth, rect.top + arrowHeight + strokeWidth);

        path.lineTo(rect.left + arrowPosition + strokeWidth, rect.top + arrowHeight + strokeWidth);

        path.close();
        return path;
    }


    private Path initRightRoundedPath(RectF rect, float strokeWidth) {
        Path path = new Path();
        path.moveTo(rect.left + radius + strokeWidth, rect.top + strokeWidth);
        path.lineTo(rect.width() - radius - arrowWidth - strokeWidth, rect.top + strokeWidth);
        path.arcTo(new RectF(rect.right - radius - arrowWidth,
                rect.top + strokeWidth, rect.right - arrowWidth - strokeWidth, radius + rect.top), 270, 90);

        path.lineTo(rect.right - arrowWidth - strokeWidth, arrowPosition + (strokeWidth / 2));
        path.lineTo(rect.right - strokeWidth - strokeWidth, arrowPosition + arrowHeight / 2);
        path.lineTo(rect.right - arrowWidth - strokeWidth, arrowPosition + arrowHeight - (strokeWidth / 2));
        path.lineTo(rect.right - arrowWidth - strokeWidth, rect.bottom - radius - strokeWidth);

        path.arcTo(new RectF(rect.right - radius - arrowWidth, rect.bottom - radius,
                rect.right - arrowWidth - strokeWidth, rect.bottom - strokeWidth), 0, 90);
        path.lineTo(rect.left + arrowWidth + strokeWidth, rect.bottom - strokeWidth);

        path.arcTo(new RectF(rect.left + strokeWidth, rect.bottom - radius,
                radius + rect.left, rect.bottom - strokeWidth), 90, 90);

        path.arcTo(new RectF(rect.left + strokeWidth, rect.top + strokeWidth, radius
                + rect.left, radius + rect.top), 180, 90);
        path.close();
        return path;
    }

    private Path initRightSquarePath(RectF rect, float strokeWidth) {

        Path path = new Path();
        path.moveTo(rect.left + strokeWidth, rect.top + strokeWidth);
        path.lineTo(rect.width() - arrowWidth - strokeWidth, rect.top + strokeWidth);

        path.lineTo(rect.right - arrowWidth - strokeWidth, arrowPosition + (strokeWidth / 2));
        path.lineTo(rect.right - strokeWidth - strokeWidth, arrowPosition + arrowHeight / 2);
        path.lineTo(rect.right - arrowWidth - strokeWidth, arrowPosition + arrowHeight - (strokeWidth / 2));

        path.lineTo(rect.right - arrowWidth - strokeWidth, rect.bottom - strokeWidth);

        path.lineTo(rect.left + strokeWidth, rect.bottom - strokeWidth);
        path.lineTo(rect.left + strokeWidth, rect.top + strokeWidth);

        path.close();
        return path;
    }


    private Path initBottomRoundedPath(RectF rect, float strokeWidth) {

        Path path = new Path();
        path.moveTo(rect.left + radius + strokeWidth, rect.top + strokeWidth);
        path.lineTo(rect.width() - radius - strokeWidth, rect.top + strokeWidth);
        path.arcTo(new RectF(rect.right - radius,
                rect.top + strokeWidth, rect.right - strokeWidth, radius + rect.top), 270, 90);

        path.lineTo(rect.right - strokeWidth, rect.bottom - arrowHeight - radius - strokeWidth);
        path.arcTo(new RectF(rect.right - radius, rect.bottom - radius - arrowHeight,
                rect.right - strokeWidth, rect.bottom - arrowHeight - strokeWidth), 0, 90);

        path.lineTo(rect.left + arrowWidth + arrowPosition - (strokeWidth / 2), rect.bottom - arrowHeight - strokeWidth);
        path.lineTo(rect.left + arrowPosition + arrowWidth / 2, rect.bottom - strokeWidth - strokeWidth);
        path.lineTo(rect.left + arrowPosition + (strokeWidth / 2), rect.bottom - arrowHeight - strokeWidth);
        path.lineTo(rect.left + Math.min(radius, arrowPosition) + strokeWidth, rect.bottom - arrowHeight - strokeWidth);

        path.arcTo(new RectF(rect.left + strokeWidth, rect.bottom - radius - arrowHeight,
                radius + rect.left, rect.bottom - arrowHeight - strokeWidth), 90, 90);
        path.lineTo(rect.left + strokeWidth, rect.top + radius + strokeWidth);
        path.arcTo(new RectF(rect.left + strokeWidth, rect.top + strokeWidth, radius
                + rect.left, radius + rect.top), 180, 90);
        path.close();
        return path;
    }


    private Path initBottomSquarePath(RectF rect, float strokeWidth) {

        Path path = new Path();
        path.moveTo(rect.left + strokeWidth, rect.top + strokeWidth);
        path.lineTo(rect.right - strokeWidth, rect.top + strokeWidth);
        path.lineTo(rect.right - strokeWidth, rect.bottom - arrowHeight - strokeWidth);


        path.lineTo(rect.left + arrowWidth + arrowPosition - (strokeWidth / 2), rect.bottom - arrowHeight - strokeWidth);
        path.lineTo(rect.left + arrowPosition + arrowWidth / 2, rect.bottom - strokeWidth - strokeWidth);
        path.lineTo(rect.left + arrowPosition + (strokeWidth / 2), rect.bottom - arrowHeight - strokeWidth);
        path.lineTo(rect.left + arrowPosition + strokeWidth, rect.bottom - arrowHeight - strokeWidth);


        path.lineTo(rect.left + strokeWidth, rect.bottom - arrowHeight - strokeWidth);
        path.lineTo(rect.left + strokeWidth, rect.top + strokeWidth);
        path.close();
        return path;
    }


    public enum ArrowDirection {
        LEFT,//0
        TOP,//1
        BOTTOM,//2
        RIGHT//3
    }

    public ArrowDirection fromInt(int value) {
        for (ArrowDirection arrowDirection : ArrowDirection.values()) {
            if (value == arrowDirection.ordinal()) {
                return arrowDirection;
            }
        }
        return ArrowDirection.LEFT;
    }

}
