package com.croshe.android.base.views.control;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import java.lang.reflect.Field;


/**
 * Copyright (C) 2016 Cliff Ophalvens (Blogc.at)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author Cliff Ophalvens (Blogc.at)
 */
public class CrosheExpandableTextView extends android.support.v7.widget.AppCompatTextView
{
    // copy off TextView.LINES
    private static final int MAXMODE_LINES = 1;

    private OnExpandListener onExpandListener;
    private TimeInterpolator expandInterpolator;
    private TimeInterpolator collapseInterpolator;

    private final int maxLines;
    private long animationDuration;
    private boolean animating;
    private boolean expanded;
    private int collapsedHeight;

    public CrosheExpandableTextView(final Context context)
    {
        this(context, null);
    }

    public CrosheExpandableTextView(final Context context, final AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public CrosheExpandableTextView(final Context context, final AttributeSet attrs, final int defStyle)
    {
        super(context, attrs, defStyle);

        // read attributes
        // keep the original value of maxLines
        this.maxLines = this.getMaxLines();

        // create default interpolators
        this.expandInterpolator = new AccelerateDecelerateInterpolator();
        this.collapseInterpolator = new AccelerateDecelerateInterpolator();
    }

    @Override
    public int getMaxLines()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
        {
            return super.getMaxLines();
        }

        try
        {
            final Field mMaxMode = TextView.class.getField("mMaxMode");
            mMaxMode.setAccessible(true);
            final Field mMaximum = TextView.class.getField("mMaximum");
            mMaximum.setAccessible(true);

            final int mMaxModeValue = (int) mMaxMode.get(this);
            final int mMaximumValue = (int) mMaximum.get(this);

            return mMaxModeValue == MAXMODE_LINES ? mMaximumValue : -1;
        }
        catch (final Exception e)
        {
            return -1;
        }
    }

    /**
     * Toggle the expanded state of this {@link CrosheExpandableTextView}.
     * @return true if toggled, false otherwise.
     */
    public boolean toggle()
    {
        return this.expanded
                ? this.collapse()
                : this.expand();
    }

    /**
     * Expand this {@link CrosheExpandableTextView}.
     * @return true if expanded, false otherwise.
     */
    public boolean expand()
    {
        if (!this.expanded && !this.animating && this.maxLines >= 0)
        {
            this.animating = true;

            // notify listener
            if (this.onExpandListener != null)
            {
                this.onExpandListener.onExpand(this);
            }

            // get collapsed height
            this.measure
                    (
                            MeasureSpec.makeMeasureSpec(this.getMeasuredWidth(), MeasureSpec.EXACTLY),
                            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
                    );

            this.collapsedHeight = this.getMeasuredHeight();

            // set maxLines to MAX Integer, so we can calculate the expanded height
            this.setMaxLines(Integer.MAX_VALUE);

            // get expanded height
            this.measure
                    (
                            MeasureSpec.makeMeasureSpec(this.getMeasuredWidth(), MeasureSpec.EXACTLY),
                            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
                    );

            final int expandedHeight = this.getMeasuredHeight();

            // animate from collapsed height to expanded height
            final ValueAnimator valueAnimator = ValueAnimator.ofInt(this.collapsedHeight, expandedHeight);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
            {
                @Override
                public void onAnimationUpdate(final ValueAnimator animation)
                {
                    final ViewGroup.LayoutParams layoutParams = CrosheExpandableTextView.this.getLayoutParams();
                    layoutParams.height = (int) animation.getAnimatedValue();
                    CrosheExpandableTextView.this.setLayoutParams(layoutParams);
                }
            });

            valueAnimator.addListener(new AnimatorListenerAdapter()
            {
                @Override
                public void onAnimationEnd(final Animator animation)
                {
                    // if fully expanded, set height to WRAP_CONTENT, because when rotating the device
                    // the height calculated with this ValueAnimator isn't correct anymore
                    final ViewGroup.LayoutParams layoutParams = CrosheExpandableTextView.this.getLayoutParams();
                    layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    CrosheExpandableTextView.this.setLayoutParams(layoutParams);

                    // keep track of current status
                    CrosheExpandableTextView.this.expanded = true;
                    CrosheExpandableTextView.this.animating = false;
                }
            });

            // set interpolator
            valueAnimator.setInterpolator(this.expandInterpolator);

            // start the animation
            valueAnimator
                    .setDuration(this.animationDuration)
                    .start();

            return true;
        }

        return false;
    }

    /**
     * Collapse this {@link TextView}.
     * @return true if collapsed, false otherwise.
     */
    public boolean collapse()
    {
        if (this.expanded && !this.animating && this.maxLines >= 0)
        {
            this.animating = true;

            // notify listener
            if (this.onExpandListener != null)
            {
                this.onExpandListener.onCollapse(this);
            }

            // get expanded height
            final int expandedHeight = this.getMeasuredHeight();

            // animate from expanded height to collapsed height
            final ValueAnimator valueAnimator = ValueAnimator.ofInt(expandedHeight, this.collapsedHeight);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
            {
                @Override
                public void onAnimationUpdate(final ValueAnimator animation)
                {
                    final ViewGroup.LayoutParams layoutParams = CrosheExpandableTextView.this.getLayoutParams();
                    layoutParams.height = (int) animation.getAnimatedValue();
                    CrosheExpandableTextView.this.setLayoutParams(layoutParams);
                }
            });

            valueAnimator.addListener(new AnimatorListenerAdapter()
            {
                @Override
                public void onAnimationEnd(final Animator animation)
                {
                    // set maxLines to original value
                    CrosheExpandableTextView.this.setMaxLines(CrosheExpandableTextView.this.maxLines);

                    // if fully collapsed, set height to WRAP_CONTENT, because when rotating the device
                    // the height calculated with this ValueAnimator isn't correct anymore
                    final ViewGroup.LayoutParams layoutParams = CrosheExpandableTextView.this.getLayoutParams();
                    layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    CrosheExpandableTextView.this.setLayoutParams(layoutParams);

                    // keep track of current status
                    CrosheExpandableTextView.this.expanded = false;
                    CrosheExpandableTextView.this.animating = false;
                }
            });

            // set interpolator
            valueAnimator.setInterpolator(this.collapseInterpolator);

            // start the animation
            valueAnimator
                    .setDuration(this.animationDuration)
                    .start();

            return true;
        }

        return false;
    }

    /**
     * Sets the duration of the expand / collapse animation.
     * @param animationDuration duration in milliseconds.
     */
    public void setAnimationDuration(final long animationDuration)
    {
        this.animationDuration = animationDuration;
    }

    /**
     * Sets a listener which receives updates about this {@link CrosheExpandableTextView}.
     * @param onExpandListener the listener.
     */
    public void setOnExpandListener(final OnExpandListener onExpandListener)
    {
        this.onExpandListener = onExpandListener;
    }

    /**
     * Returns the {@link OnExpandListener}.
     * @return the listener.
     */
    public OnExpandListener getOnExpandListener()
    {
        return this.onExpandListener;
    }

    /**
     * Sets a {@link TimeInterpolator} for expanding and collapsing.
     * @param interpolator the interpolator
     */
    public void setInterpolator(final TimeInterpolator interpolator)
    {
        this.expandInterpolator = interpolator;
        this.collapseInterpolator = interpolator;
    }

    /**
     * Sets a {@link TimeInterpolator} for expanding.
     * @param expandInterpolator the interpolator
     */
    public void setExpandInterpolator(final TimeInterpolator expandInterpolator)
    {
        this.expandInterpolator = expandInterpolator;
    }

    /**
     * Returns the current {@link TimeInterpolator} for expanding.
     * @return the current interpolator, null by default.
     */
    public TimeInterpolator getExpandInterpolator()
    {
        return this.expandInterpolator;
    }

    /**
     * Sets a {@link TimeInterpolator} for collpasing.
     * @param collapseInterpolator the interpolator
     */
    public void setCollapseInterpolator(final TimeInterpolator collapseInterpolator)
    {
        this.collapseInterpolator = collapseInterpolator;
    }

    /**
     * Returns the current {@link TimeInterpolator} for collapsing.
     * @return the current interpolator, null by default.
     */
    public TimeInterpolator getCollapseInterpolator()
    {
        return this.collapseInterpolator;
    }

    /**
     * Is this {@link CrosheExpandableTextView} expanded or not?
     * @return true if expanded, false if collapsed.
     */
    public boolean isExpanded()
    {
        return this.expanded;
    }

    /**
     * Interface definition for a callback to be invoked when
     * a {@link CrosheExpandableTextView} is expanded or collapsed.
     */
    public interface OnExpandListener
    {
        /**
         * The {@link CrosheExpandableTextView} is being expanded.
         * @param view the textview
         */
        void onExpand(CrosheExpandableTextView view);

        /**
         * The {@link CrosheExpandableTextView} is being collapsed.
         * @param view the textview
         */
        void onCollapse(CrosheExpandableTextView view);
    }
}