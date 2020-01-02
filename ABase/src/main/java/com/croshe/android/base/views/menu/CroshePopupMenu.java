package com.croshe.android.base.views.menu;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.croshe.android.base.R;
import com.croshe.android.base.extend.dialog.CrosheDialog;
import com.croshe.android.base.extend.listener.CrosheAnimationListener;
import com.croshe.android.base.listener.OnCrosheMenuClick;
import com.croshe.android.base.listener.OnCrosheMenuListener;
import com.croshe.android.base.utils.BaseAppUtils;
import com.croshe.android.base.utils.DensityUtils;
import com.croshe.android.base.utils.ExitApplication;
import com.croshe.android.base.utils.ImageUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * 超级弹出菜单，可多功能使用
 * 安徽创息软件科技有限公司-技术支持  http://www.croshe.com
 * Created by Janesen on 2017/6/25.
 */

public class CroshePopupMenu implements View.OnClickListener {
    private static final String WindowTag = "WindowTag" + System.currentTimeMillis();


    private CrosheDialog dialog;
    private List<Object> items = new ArrayList<>();
    private Set<Integer> hiddenIndex = new HashSet<>();
    private Context context;

    private int lineColor = -1, lineMargin = DensityUtils.dip2px(10), positionMargin = DensityUtils.dip2px(10);
    private int x = 0, y = 0;
    private View contentView;
    private int itemTitleGravity = Gravity.LEFT;
    private String title;
    private boolean canFocus = true;
    private boolean showInWindow = false;
    private AnimEnum animEnum;
    private long animationDuration = 100;
    private int menuWidth = -1, menuHeight = -1, dialogWidth = -1, dialogHeight = -1;
    private float shadowAlpha = 0.5f;
    private Interpolator animInterpolator;
    private OnCrosheMenuListener onCrosheMenuShowListener;

    private View.OnClickListener onContainerClickListener;

    private boolean animation = true;
    private long onDownPressTime;
    private boolean fullHeight;
    private boolean showing;

    private boolean cleaverX = true, cleaverY = true;

    private boolean cancelable = true;//是否可被取消
    private boolean canceledOnTouchOutside = true;//
    private boolean fullScreen = false;//是否填充到整个屏幕
    private Animation closeAnimation;

    public static CroshePopupMenu newInstance(Context context) {
        CroshePopupMenu croshePopupMenu = new CroshePopupMenu();
        croshePopupMenu.context = context;
        return croshePopupMenu;
    }

    private CroshePopupMenu() {

    }


    /**
     * 设置选项，如果标题的选项已存在，则进行替换
     * @param title
     * @param onCrosheMenuClick
     * @return
     */
    public CroshePopupMenu setItem(String title, OnCrosheMenuClick onCrosheMenuClick) {
        boolean hasItem=false;
        for (Object item : items) {
            if (item instanceof CrosheMenuItem) {
                CrosheMenuItem menuItem = (CrosheMenuItem) item;
                if (menuItem.getTitle().equals(title)) {
                    menuItem.setOnClickListener(onCrosheMenuClick);
                    hasItem=true;
                    break;
                }
            }
        }
        if (!hasItem) {
            addItem(title, onCrosheMenuClick);
        }
        return this;
    }


    public CroshePopupMenu addItem(String title, OnCrosheMenuClick onCrosheMenuClick) {
        addItem(-1, title, null, -1, onCrosheMenuClick);
        return this;
    }

    public CroshePopupMenu addItem(int index, String title, OnCrosheMenuClick onCrosheMenuClick) {
        addItem(index, title, null, -1, onCrosheMenuClick);
        return this;
    }


    public CroshePopupMenu addItem(String title, String subTitle, OnCrosheMenuClick onCrosheMenuClick) {
        addItem(-1, title, subTitle, -1, onCrosheMenuClick);
        return this;
    }

    public CroshePopupMenu addItem(String title, String subTitle, int iconResource, OnCrosheMenuClick onCrosheMenuClick) {
        addItem(-1, title, subTitle, iconResource, onCrosheMenuClick);
        return this;
    }


    public CroshePopupMenu addItem(int index, String title, String subTitle, OnCrosheMenuClick onCrosheMenuClick) {
        addItem(index, title, subTitle, -1, onCrosheMenuClick);
        return this;
    }


    public CroshePopupMenu addItem(String title, int iconResource, OnCrosheMenuClick onCrosheMenuClick) {
        addItem(-1, title, null, iconResource, onCrosheMenuClick);
        return this;
    }

    public CroshePopupMenu addItem(String title, int iconResource, int iconColor, OnCrosheMenuClick onCrosheMenuClick) {
        addItem(-1, title, true, null, iconResource, iconColor, onCrosheMenuClick);
        return this;
    }

    public CroshePopupMenu addItem(String title, boolean closeAnim, int iconResource, OnCrosheMenuClick onCrosheMenuClick) {
        addItem(-1, title, closeAnim, null, iconResource, onCrosheMenuClick);
        return this;
    }

    public CroshePopupMenu addItem(int index, String title, int iconResource, OnCrosheMenuClick onCrosheMenuClick) {
        addItem(index, title, null, iconResource, onCrosheMenuClick);
        return this;
    }


    public CroshePopupMenu addItem(int index, String title, String subTitle, int iconResource, OnCrosheMenuClick onCrosheMenuClick) {
        addItem(index, title, true, subTitle, iconResource, onCrosheMenuClick);
        return this;
    }

    public CroshePopupMenu addItem(int index, String title,
                                   boolean closeAnim,
                                   String subTitle,
                                   int iconResource,
                                   OnCrosheMenuClick onCrosheMenuClick) {
        return addItem(index, title, closeAnim, subTitle, iconResource, -1, onCrosheMenuClick);
    }


    public CroshePopupMenu addItem(int index, String title,
                                   boolean closeAnim,
                                   String subTitle,
                                   int iconResource,
                                   int iconColor,
                                   OnCrosheMenuClick onCrosheMenuClick) {
        if (index < 0) {
            items.add(CrosheMenuItem.newInstance(title, subTitle, closeAnim, iconResource, onCrosheMenuClick).setIconColor(iconColor));
        } else {
            items.add(index, CrosheMenuItem.newInstance(title, subTitle, closeAnim, iconResource, onCrosheMenuClick).setIconColor(iconColor));
        }
        return this;
    }


    public CroshePopupMenu remove(int index) {
        items.remove(index);
        return this;
    }

    public CroshePopupMenu addItem(CrosheMenuItem item) {
        items.add(item);
        return this;
    }

    public CroshePopupMenu addItem(int index, CrosheMenuItem item) {
        items.add(Math.max(index, 0), item);
        return this;
    }

    public CroshePopupMenu addItem(int index, View item) {
        items.add(Math.max(index, 0), item);
        return this;
    }


    public CroshePopupMenu addItem(View item) {
        items.add(item);
        return this;
    }

    public CroshePopupMenu addItem(View item, LinearLayout.LayoutParams layoutParams) {
        item.setLayoutParams(layoutParams);
        items.add(item);
        return this;
    }


    public CroshePopupMenu addItem(int layoutResourceId) {
        View view = LayoutInflater.from(getContext()).inflate(layoutResourceId, null);
        items.add(view);
        return this;
    }


    public CroshePopupMenu addItem(int index, int layoutResourceId) {
        View view = LayoutInflater.from(getContext()).inflate(layoutResourceId, null);
        items.add(index, view);
        return this;
    }


    public CroshePopupMenu setItemHidden(int menuIndex, boolean hidden) {
        if (hidden) {
            hiddenIndex.add(menuIndex);
        } else {
            hiddenIndex.remove(menuIndex);
        }
        if (contentView != null) {
            View menuView = contentView.findViewWithTag("Menu" + menuIndex);
            if (menuView != null) {
                menuView.setVisibility(hidden ? View.GONE : View.VISIBLE);
            }
            View menuLine = contentView.findViewWithTag("MenuLine" + menuIndex);
            if (menuLine != null) {
                menuLine.setVisibility(hidden ? View.GONE : View.VISIBLE);
            }
        }
        return this;
    }


    public CroshePopupMenu setItemHidden(String menuName, boolean hidden) {
        int menuIndex = items.indexOf(getMenuItem(menuName));
        if (hidden) {
            hiddenIndex.add(menuIndex);
        } else {
            hiddenIndex.remove(menuIndex);
        }

        if (contentView != null) {
            View menuView = contentView.findViewWithTag("Menu" + menuIndex);
            if (menuView != null) {
                menuView.setVisibility(hidden ? View.GONE : View.VISIBLE);
            }
            View menuLine = contentView.findViewWithTag("MenuLine" + menuIndex);
            if (menuLine != null) {
                menuLine.setVisibility(hidden ? View.GONE : View.VISIBLE);
            }
        }
        return this;
    }


    /**
     * 绑定View的长按事件，弹出菜单
     *
     * @param v
     */
    public CroshePopupMenu bindLongClickForAnchorTop(View... v) {
        for (View view : v) {
            view.setClickable(true);
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    showAnchorTop(v);
                    return true;
                }
            });
        }
        return this;
    }

    /**
     * 绑定View的长按事件，弹出菜单
     *
     * @param v
     */
    public CroshePopupMenu bindLongClick(final View... v) {
        GestureDetector.SimpleOnGestureListener simpleOnGestureListener = new GestureDetector.SimpleOnGestureListener() {

            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
                BaseAppUtils.vibrator(getContext(), 10);
                show((int) e.getRawX(), (int) e.getRawY());
            }
        };

        final GestureDetector detector = new GestureDetector(getContext(), simpleOnGestureListener);

        for (View view : v) {
            view.setClickable(true);
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int action = event.getAction();
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            onDownPressTime = System.currentTimeMillis();
                            break;
                        case MotionEvent.ACTION_CANCEL:
                        case MotionEvent.ACTION_UP:
                            //视为长按
                            if (System.currentTimeMillis() - onDownPressTime > 500) {
                                return true;
                            }
                            break;
                    }
                    return detector.onTouchEvent(event);
                }
            });
        }
        return this;
    }

    /**
     * 绑定View的单击事件，弹出菜单
     *
     * @param v
     */
    public CroshePopupMenu bindClick(final View... v) {
        return bindTagClick(v);
    }


    /**
     * 绑定View的双击事件，弹出菜单
     *
     * @param v
     */
    public CroshePopupMenu bindDbClick(final View... v) {
        GestureDetector.SimpleOnGestureListener simpleOnGestureListener = new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTapEvent(MotionEvent e) {
                show((int) e.getRawX(), (int) e.getRawY());
                return super.onDoubleTapEvent(e);
            }
        };

        final GestureDetector detector = new GestureDetector(getContext(), simpleOnGestureListener);

        for (View view : v) {
            view.setClickable(true);
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return detector.onTouchEvent(event);
                }
            });
        }
        return this;
    }

    /**
     * 绑定View的单击事件，弹出菜单
     *
     * @param v
     */
    public CroshePopupMenu bindTagClick(final View... v) {
        GestureDetector.SimpleOnGestureListener simpleOnGestureListener = new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                show((int) e.getRawX(), (int) e.getRawY());
                return false;
            }
        };

        final GestureDetector detector = new GestureDetector(getContext(), simpleOnGestureListener);

        for (View view : v) {
            view.setClickable(true);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return detector.onTouchEvent(event);
                }
            });
        }
        return this;
    }

    /**
     * 在指定 View位置显示菜单
     *
     * @param v
     */
    public void show(View v) {
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        x = location[0];
        y = location[1] + v.getHeight();
        buildDialog();
    }

    /**
     * 在指定 View位置显示菜单
     *
     * @param v
     */
    public void showAnchorRight(View v) {
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        x = location[0] + v.getWidth();
        y = location[1] + v.getHeight();
        buildDialog();
    }


    /**
     * 在指定 View位置显示菜单
     *
     * @param v
     */
    public void showAnchorTop(View v) {
        cleaverX = false;
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        x = location[0];
        y = location[1] + positionMargin;
        buildDialog();
    }


    /**
     * 显示菜单
     *
     * @param x x坐标
     * @param y y坐标
     */
    public void show(int x, int y) {
        this.x = x;
        this.y = y;
        buildDialog();
    }


    /**
     * 从底部弹出
     */
    public void showFromBottom() {
        animEnum = AnimEnum.BTTranslateAnim;
        buildDialog();
    }

    /**
     * 从底部弹出
     */
    public void showFromBottom(int y) {
        this.x = 0;
        this.y = y;
        animEnum = AnimEnum.BTTranslateAnim;
        buildDialog();
    }


    /**
     * 从底部弹出
     */
    public void showFromBottom(View v) {
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        this.y = location[1] + v.getHeight();
        this.x = location[0];

        animEnum = AnimEnum.BTTranslateAnim;
        buildDialog();
    }


    /**
     * 从底部弹出，有透明遮罩层
     */
    public void showFromBottomMask() {
        animEnum = AnimEnum.BTMaskTranslateAnim;
        menuWidth = -1;
        buildDialog();
    }


    /**
     * 从屏幕中间弹出
     */
    public void showFromCenter() {
        animEnum = AnimEnum.CenterAnim;
        buildDialog();
    }


    /**
     * 从屏幕中间弹出
     */
    public void showFromCenterMask() {
        animEnum = AnimEnum.CenterMaskAnim;
        buildDialog();
    }


    /**
     * 从上部弹出
     */
    public void showFromTop() {
        animEnum = AnimEnum.TopTranslateAnim;
        menuWidth = -1;
        buildDialog();
    }


    /**
     * 从上部弹出
     */
    public void showFromTop(View v) {
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        this.y = location[1] + v.getHeight();
        this.x = location[0];
        animEnum = AnimEnum.TopTranslateAnim;
        buildDialog();
    }


    /**
     * 从上部弹出
     */
    public void showFromTop(int x, int y) {
        this.y = y;
        this.x = x;
        animEnum = AnimEnum.TopTranslateAnim;
        menuWidth = -1;
        buildDialog();
    }


    /**
     * 从上部弹出，有透明遮罩层
     */
    public void showFromTopMask() {
        animEnum = AnimEnum.TopMaskTranslateAnim;
        menuWidth = -1;
        buildDialog();
    }


    /**
     * 从屏幕的右侧滑出
     */
    public void showFromRightByScreen() {
        this.y = 0;
        this.x = 0;
        animEnum = AnimEnum.RightTranslateAnim;
        menuHeight = -1;
        buildDialog();
    }

    /**
     * 从屏幕的右侧滑出
     */
    public void showFromRightMaskByScreen() {
        this.y = 0;
        this.x = 0;
        animEnum = AnimEnum.RightMaskTranslateAnim;
        menuHeight = -1;
        buildDialog();
    }


    private void buildDialog() {
        try {
            if (onCrosheMenuShowListener != null) {
                onCrosheMenuShowListener.onBeforeShow(this);
            }

            contentView = buildItem();

            int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

            int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

            contentView.measure(width, height);

            int computeWidth = menuWidth;
            if (menuWidth == -1) {
                computeWidth = contentView.getMeasuredWidth();
            }
            menuHeight = contentView.getMeasuredHeight();


            if (isShowInWindow()) {
                if (getContext() instanceof Activity) {
                    Activity activity = (Activity) getContext();
                    ViewGroup viewGroup = (ViewGroup) activity.getWindow().getDecorView();

                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                    FrameLayout maskView = new FrameLayout(getContext());
                    if (animEnum == AnimEnum.BTTranslateAnim
                            || animEnum == AnimEnum.BTMaskTranslateAnim) {
                        layoutParams.gravity = Gravity.LEFT | Gravity.BOTTOM;
                        if (animEnum == AnimEnum.BTMaskTranslateAnim) {
                            maskView.setBackgroundColor(Color.parseColor("#66000000"));
                        }
                    } else if (animEnum == AnimEnum.TopTranslateAnim
                            || animEnum == AnimEnum.TopMaskTranslateAnim) {
                        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
                        if (animEnum == AnimEnum.TopMaskTranslateAnim) {
                            maskView.setBackgroundColor(Color.parseColor("#66000000"));
                        }
                    } else if (animEnum == AnimEnum.RightTranslateAnim
                            || animEnum == AnimEnum.RightMaskTranslateAnim) {
                        layoutParams.gravity = Gravity.RIGHT | Gravity.TOP;
                        if (animEnum == AnimEnum.RightMaskTranslateAnim) {
                            maskView.setBackgroundColor(Color.parseColor("#66000000"));
                        }
                    } else if (animEnum == AnimEnum.CenterAnim
                            || animEnum == AnimEnum.CenterMaskAnim) {
                        layoutParams.gravity = Gravity.CENTER;
                        if (animEnum == AnimEnum.CenterMaskAnim) {
                            maskView.setBackgroundColor(Color.parseColor("#66000000"));
                        }
                    } else {
                        computeAnimType(menuHeight, computeWidth);
                        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
                    }
                    layoutParams.leftMargin = x;
                    layoutParams.topMargin = y;//加上状态栏的高度


                    FrameLayout containerView = new FrameLayout(getContext());
                    if (fullScreen) {
                        int w = (int) DensityUtils.getWidthInPx();
                        int h = (int) (DensityUtils.getHeightInPx() - DensityUtils.getStatusBarHeight(getContext()));
                        ViewGroup.LayoutParams contentLayout = new ViewGroup.LayoutParams(w, h);
                        containerView.addView(contentView, contentLayout);
                    } else {
                        containerView.addView(contentView);
                    }
                    containerView.setClipChildren(true);

                    maskView.addView(containerView, layoutParams);
                    maskView.setTag(WindowTag);
                    viewGroup.addView(maskView, new ViewGroup.LayoutParams((int) DensityUtils.getWidthInPx(), (int) DensityUtils.getHeightInPx()));
                    maskView.bringToFront();

                    if (canFocus) {
                        maskView.setClickable(true);
                        maskView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                close();
                            }
                        });
                    }
                }
            } else {
                dialog = new CrosheDialog(getContext(), R.style.AndroidBaseDialogStyleA);
                dialog.setOnCloseListener(new CrosheDialog.OnCloseListener() {
                    @Override
                    public void onClose(Dialog dialog) {
                        if (onCrosheMenuShowListener != null) {
                            onCrosheMenuShowListener.close(CroshePopupMenu.this);
                        }
                    }
                });


                Window win = dialog.getWindow();
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();


                if (animEnum == AnimEnum.BTTranslateAnim
                        || animEnum == AnimEnum.BTMaskTranslateAnim) {
                    layoutParams.gravity = Gravity.LEFT | Gravity.BOTTOM;
                } else if (animEnum == AnimEnum.TopTranslateAnim
                        || animEnum == AnimEnum.TopMaskTranslateAnim) {
                    layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
                } else if (animEnum == AnimEnum.RightTranslateAnim
                        || animEnum == AnimEnum.RightMaskTranslateAnim) {
                    layoutParams.gravity = Gravity.RIGHT | Gravity.TOP;
                } else if (animEnum == AnimEnum.CenterAnim
                        || animEnum == AnimEnum.CenterMaskAnim) {
                    layoutParams.gravity = Gravity.CENTER;
                } else {
                    computeAnimType(menuHeight, computeWidth);
                    layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
                }
                layoutParams.x = x;
                layoutParams.y = y - DensityUtils.getStatusBarHeight(getContext());//去除状态栏的高度

                if (dialogWidth != -1) {
                    layoutParams.width = dialogWidth;
                }
                if (dialogHeight != -1) {
                    layoutParams.height = dialogHeight;
                }


                win.setAttributes(layoutParams);


                if (animEnum == AnimEnum.BTMaskTranslateAnim
                        || animEnum == AnimEnum.TopMaskTranslateAnim
                        || animEnum == AnimEnum.RightMaskTranslateAnim
                        || animEnum == AnimEnum.CenterMaskAnim) {
                    win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, PixelFormat.UNKNOWN);
                } else {
                    if (canFocus) {
                        win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, PixelFormat.TRANSPARENT);
                    } else {
                        win.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSPARENT);
                    }
                }

                dialog.setCanceledOnTouchOutside(canFocus);
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        menuHeight = -1;
                        menuWidth = -1;
                    }
                });
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        menuHeight = -1;
                        menuWidth = -1;
                    }
                });


                if (fullScreen) {
                    int w = (int) DensityUtils.getWidthInPx();
                    int h = (int) (DensityUtils.getHeightInPx() - DensityUtils.getStatusBarHeight(getContext()));
                    ViewGroup.LayoutParams contentLayout = new ViewGroup.LayoutParams(w, h);
                    dialog.setContentView(contentView, contentLayout);
                } else {
                    dialog.setContentView(contentView);
                }
                contentView.setOnClickListener(getOnContainerClickListener());

                dialog.setCancelable(cancelable);
                dialog.setCanceledOnTouchOutside(canceledOnTouchOutside);
                dialog.show();

            }
            if (animation) {
                startShowAnim();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            showing = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (onCrosheMenuShowListener != null) {
                        onCrosheMenuShowListener.onAfterShow(CroshePopupMenu.this);
                    }
                }
            }).start();
        }

    }

    private View buildItem() {
        boolean hasLine = false;

        View view;
        if (animEnum != null && animEnum == AnimEnum.BTTranslateAnim) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.android_base_panel_popup_menu_bottom, null);
            if (menuWidth == -1) {
                menuWidth = (int) DensityUtils.getWidthInPx();
            }
            view.findViewById(R.id.android_base_imgShadow).setAlpha(shadowAlpha);
        } else if (animEnum != null && animEnum == AnimEnum.BTMaskTranslateAnim) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.android_base_panel_popup_menu_bottom, null);
            if (menuWidth == -1) {
                menuWidth = (int) DensityUtils.getWidthInPx();
            }
            view.findViewById(R.id.android_base_imgShadow).setVisibility(View.GONE);
        } else if (animEnum != null && animEnum == AnimEnum.TopTranslateAnim) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.android_base_panel_popup_menu_top, null);
            if (menuWidth == -1) {
                menuWidth = (int) DensityUtils.getWidthInPx();
            }
            view.findViewById(R.id.android_base_imgShadow).setAlpha(shadowAlpha);
        } else if (animEnum != null && animEnum == AnimEnum.TopMaskTranslateAnim) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.android_base_panel_popup_menu_top, null);
            if (menuWidth == -1) {
                menuWidth = (int) DensityUtils.getWidthInPx();
            }
            view.findViewById(R.id.android_base_imgShadow).setVisibility(View.GONE);
        } else if (animEnum != null && animEnum == AnimEnum.RightTranslateAnim) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.android_base_panel_popup_menu_right, null);
            view.findViewById(R.id.android_base_imgShadow).setAlpha(shadowAlpha);
        } else if (animEnum != null && animEnum == AnimEnum.RightMaskTranslateAnim) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.android_base_panel_popup_menu_right, null);
            view.findViewById(R.id.android_base_imgShadow).setVisibility(View.GONE);
        } else {
            view = LayoutInflater.from(getContext()).inflate(R.layout.android_base_panel_popup_menu, null);
        }

        if (fullScreen) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.android_base_panel_popup_menu_full_screen, null);
        }

        LinearLayout llMenuContainer = view.findViewById(R.id.android_base_llMenuContainer);


        if (menuWidth != -1) {
            ViewGroup.LayoutParams layoutParams = llMenuContainer.getLayoutParams();
            layoutParams.width = menuWidth;
            llMenuContainer.setLayoutParams(layoutParams);
        }

        if (fullHeight) {
            menuHeight = (int) (DensityUtils.getHeightInPx() - y);
        }

        if (menuHeight != -1) {
            ViewGroup.LayoutParams layoutParams = llMenuContainer.getLayoutParams();
            layoutParams.height = menuHeight;
            llMenuContainer.setLayoutParams(layoutParams);
        }

        if (view.findViewById(R.id.android_base_animPanel) != null) {
            CardView cardView = (CardView) view.findViewById(R.id.android_base_animPanel);
            if (shadowAlpha == 0) {
                cardView.setCardElevation(0);
            }
        }

        if (StringUtils.isNotEmpty(title)) {
            TextView textView = new TextView(getContext());
            textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            int padding = DensityUtils.dip2px(10);
            textView.setPadding(padding, padding, padding, padding);
            textView.setText(title);
            textView.setTextColor(Color.parseColor("#cccccc"));
            textView.setTextSize(14f);
            textView.setGravity(itemTitleGravity);
            llMenuContainer.addView(textView);
        }


        for (int i = 0; i < items.size(); i++) {
            Object object = items.get(i);

            boolean isHidden = hiddenIndex.contains(i);


            if (object instanceof CrosheMenuItem) {
                CrosheMenuItem item = (CrosheMenuItem) object;
                View itemView = LayoutInflater.from(getContext()).inflate(R.layout.android_base_item_popup_menu, null);
                itemView.setTag("Menu" + i);

                TextView tvTitle = (TextView) itemView.findViewById(R.id.android_base_tvTitle);
                if (item.isHidden()) {
                    isHidden = true;
                }


                TextView tvSubtitle = (TextView) itemView.findViewById(R.id.android_base_tvSubtitle);

                ImageView imgIcon = (ImageView) itemView.findViewById(R.id.imgIcon);

                LinearLayout llItem = (LinearLayout) itemView.findViewById(R.id.llItem);
                llItem.setTag(item);
                llItem.setOnClickListener(this);
                llItem.setGravity(itemTitleGravity);

                tvTitle.setText(item.getTitle());

                if (StringUtils.isNotEmpty(item.getSubTitle())) {
                    tvSubtitle.setText(item.getSubTitle());
                } else {
                    tvSubtitle.setVisibility(View.GONE);
                }

                if (item.getIconResource() != -1) {
                    imgIcon.setImageResource(item.getIconResource());
                    if (item.getIconColor() != -1) {
                        Drawable newDrawable = ImageUtils.tintDrawable(imgIcon.getDrawable(),
                                ColorStateList.valueOf(item.getIconColor()));
                        imgIcon.setImageDrawable(newDrawable);
                    }
                } else {
                    imgIcon.setVisibility(View.GONE);
                }


                llMenuContainer.addView(itemView);
                if (isHidden) {
                    itemView.setVisibility(View.GONE);
                }
            } else if (object instanceof View) {
                View item = (View) object;
                if (item.getParent() != null) {
                    ViewGroup viewGroup = (ViewGroup) item.getParent();
                    viewGroup.removeView(item);
                }
                if (item.getLayoutParams() == null) {
                    item.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                }

                llMenuContainer.addView(item);
                if (isHidden) {
                    item.setVisibility(View.GONE);
                }
            }
            if (lineColor != -1) {
                View line = new View(getContext());
                LinearLayout.LayoutParams layoutParams1
                        = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        DensityUtils.dip2px(0.5f));
                layoutParams1.setMargins(lineMargin, 0, lineMargin, 0);
                line.setLayoutParams(layoutParams1);
                line.setBackgroundColor(lineColor);
                line.setTag("MenuLine" + i);

                llMenuContainer.addView(line);

                if (isHidden) {
                    line.setVisibility(View.GONE);
                }
                hasLine = true;
            }
        }
        if (hasLine) {
            llMenuContainer.removeViewAt(llMenuContainer.getChildCount() - 1);
        }
        return view;
    }


    @Override
    public void onClick(View v) {

        if (v.getTag() != null) {
            CrosheMenuItem item = (CrosheMenuItem) v.getTag();
            if (onCrosheMenuShowListener != null) {
                onCrosheMenuShowListener.onItemClick(item);
            }
            if (!item.isCloseAnim()) {
                dialog.setOnCloseAnim(null);
            }
            close();
            if (item.getOnClickListener() != null) {
                item.getOnClickListener().onClick(item, v);
            }
        }
    }

    private void computeAnimType(int height, int width) {

        int xState = 0;
        int yState = 0;
        if (cleaverX) {
            if (x > width) {
                if (x + width >= DensityUtils.getWidthInPx()) {
                    x = x - width;
                    xState = 1;
                }
            }
        }

        if (xState == 0) {
            x = x - positionMargin;
        }


        if (cleaverY) {
            if (y > height) {
                if (y + height >= DensityUtils.getHeightInPx()) {
                    y = y - height;
                    yState = 1;
                }
            }
        }

        if (yState == 0) {
            y = y - positionMargin;
        }

        if (xState == 0) {
            if (yState == 0) {
                animEnum = AnimEnum.LTRScaleAnim;
            } else {
                animEnum = AnimEnum.LBRScaleAnim;
            }
        } else {
            if (yState == 0) {
                animEnum = AnimEnum.RTLScaleAnim;
            } else {
                animEnum = AnimEnum.RBLScaleAnim;
            }
        }
    }

    private void startShowAnim() {

        Animation animation = null;
        if (animEnum == AnimEnum.LBRScaleAnim) {
            animation = new ScaleAnimation(
                    0.2f, 1.0f, 0.2f, 1.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f
            );
        } else if (animEnum == AnimEnum.LTRScaleAnim) {
            animation = new ScaleAnimation(
                    0.2f, 1.0f, 0.2f, 1.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f
            );
        } else if (animEnum == AnimEnum.RTLScaleAnim) {
            animation = new ScaleAnimation(
                    0.2f, 1.0f, 0.2f, 1.0f,
                    Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f
            );

        } else if (animEnum == AnimEnum.RBLScaleAnim) {
            animation = new ScaleAnimation(
                    0.2f, 1.0f, 0.2f, 1.0f,
                    Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 1.0f
            );
        } else if (animEnum == AnimEnum.BTTranslateAnim
                || animEnum == AnimEnum.BTMaskTranslateAnim) {

            animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                    0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF,
                    0.0f);
        } else if (animEnum == AnimEnum.TopTranslateAnim
                || animEnum == AnimEnum.TopMaskTranslateAnim) {
            animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                    0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF,
                    0.0f);
        } else if (animEnum == AnimEnum.RightTranslateAnim
                || animEnum == AnimEnum.RightMaskTranslateAnim) {
            animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                    1.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                    0.0f);
        }

        contentView.setVisibility(View.VISIBLE);
        if (animation != null) {
            animation.setFillAfter(true);
            animation.setDuration(animationDuration);
            animation.setInterpolator(new LinearInterpolator());
            if (contentView.findViewById(R.id.android_base_animPanel) != null) {
                contentView.findViewById(R.id.android_base_animPanel).startAnimation(animation);
            } else {
                contentView.startAnimation(animation);
            }
            buildCloseAnim();
        }
    }


    private void buildCloseAnim() {

//        Log.d("STAG", "animEnum:" + animEnum);
        if (animEnum == AnimEnum.LBRScaleAnim) {
            closeAnimation = new ScaleAnimation(
                    1.0f, 0.2f, 1.0f, 0.2f,
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f
            );
        } else if (animEnum == AnimEnum.LTRScaleAnim) {
            closeAnimation = new ScaleAnimation(
                    1.0f, 0.2f, 1.0f, 0.2f,
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f
            );
        } else if (animEnum == AnimEnum.RTLScaleAnim) {
            closeAnimation = new ScaleAnimation(
                    1.0f, 0.2f, 1.0f, 0.2f,
                    Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f
            );
        } else if (animEnum == AnimEnum.RBLScaleAnim) {
            closeAnimation = new ScaleAnimation(
                    1.0f, 0.2f, 1.0f, 0.2f,
                    Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 1.0f
            );
        } else if (animEnum == AnimEnum.BTTranslateAnim
                || animEnum == AnimEnum.BTMaskTranslateAnim) {

            closeAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                    0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                    1.0f);
        } else if (animEnum == AnimEnum.TopTranslateAnim
                || animEnum == AnimEnum.TopMaskTranslateAnim) {
            closeAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                    0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                    -1.0f);
        } else if (animEnum == AnimEnum.RightTranslateAnim
                || animEnum == AnimEnum.RightMaskTranslateAnim) {
            closeAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                    0.0f, Animation.RELATIVE_TO_SELF, 1.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                    0.0f);
        }

        if (closeAnimation != null) {
            closeAnimation.setDuration(animationDuration);
            closeAnimation.setInterpolator(new LinearInterpolator());
            if (dialog != null) {
                dialog.setOnCloseAnim(closeAnimation);
                if (contentView.findViewById(R.id.android_base_animPanel) != null) {
                    dialog.setAnimView(contentView.findViewById(R.id.android_base_animPanel));
                } else {
                    dialog.setAnimView(contentView);
                }
            }
        }
    }


    public void close() {
        try {
            if (dialog != null) {
                dialog.dismiss();
            }
            if (isShowInWindow()) {
                closeAnimation.setAnimationListener(new CrosheAnimationListener() {
                    @Override
                    public void onAnimationEnd(Animation arg0) {
                        super.onAnimationEnd(arg0);
                        if (onCrosheMenuShowListener != null) {
                            onCrosheMenuShowListener.close(CroshePopupMenu.this);
                        }
                        if (getContext() instanceof Activity) {
                            Activity activity = (Activity) getContext();
                            ViewGroup viewGroup = (ViewGroup) activity.getWindow().getDecorView();
                            viewGroup.removeView(viewGroup.findViewWithTag(WindowTag));
                        }
                    }
                });
                contentView.startAnimation(closeAnimation);
            }
        } finally {
            showing = false;
        }
    }


    public int getLineColor() {
        return lineColor;
    }

    public boolean isShowing() {
        return showing;
    }


    /**
     * 设置分割线的颜色
     *
     * @param lineColor
     * @return
     */
    public CroshePopupMenu setLineColor(int lineColor) {
        this.lineColor = lineColor;
        return this;
    }

    public int getItemTitleGravity() {
        return itemTitleGravity;
    }

    /**
     * 设置菜单选项对齐方式
     *
     * @param gravity
     * @return
     */
    public CroshePopupMenu setItemTitleGravity(int gravity) {
        this.itemTitleGravity = gravity;
        return this;
    }

    public long getAnimationDuration() {
        return animationDuration;
    }

    /**
     * 设置菜单动画的时间
     *
     * @param animationDuration
     * @return
     */
    public CroshePopupMenu setAnimationDuration(long animationDuration) {
        this.animationDuration = animationDuration;
        return this;
    }

    public String getTitle() {
        return title;
    }

    /**
     * 设置菜单的标题
     *
     * @param title
     * @return
     */
    public CroshePopupMenu setTitle(String title) {
        this.title = title;
        return this;
    }

    public int getMenuWidth() {
        if (contentView != null) {
            return contentView.getWidth();
        }
        return 0;
    }

    /**
     * 设置菜单的宽度
     *
     * @param menuWidth
     */
    public CroshePopupMenu setMenuWidth(int menuWidth) {
        this.menuWidth = menuWidth;
        return this;
    }


    public CroshePopupMenu setMenuHeight(int menuHeight) {
        this.menuHeight = menuHeight;
        return this;
    }

    public int getMenuHeight() {
        return menuHeight;
    }

    public int getLineMargin() {
        return lineMargin;
    }

    /**
     * 设置分割线的左右边距
     *
     * @param lineMargin
     * @return
     */
    public CroshePopupMenu setLineMargin(int lineMargin) {
        this.lineMargin = lineMargin;
        return this;
    }


    public List<Object> getItems() {
        return items;
    }


    public CrosheMenuItem getMenuItem(String title) {
        for (Object item : items) {
            if (item instanceof CrosheMenuItem) {
                CrosheMenuItem menuItem = (CrosheMenuItem) item;
                if (menuItem.getTitle().equals(title)) {
                    return menuItem;
                }
            }
        }
        return null;
    }


    public boolean isCanFocus() {
        return canFocus;
    }

    public CroshePopupMenu setCanFocus(boolean canFocus) {
        this.canFocus = canFocus;
        return this;
    }

    public float getShadowAlpha() {
        return shadowAlpha;
    }

    public CroshePopupMenu setShadowAlpha(float shadowAlpha) {
        this.shadowAlpha = shadowAlpha;
        return this;
    }


    public Interpolator getAnimInterpolator() {
        return animInterpolator;
    }

    public CroshePopupMenu setAnimInterpolator(Interpolator animInterpolator) {
        this.animInterpolator = animInterpolator;
        return this;
    }

    public boolean isFullHeight() {
        return fullHeight;
    }

    public CroshePopupMenu setFullHeight(boolean fullHeight) {
        this.fullHeight = fullHeight;
        return this;
    }

    public OnCrosheMenuListener getOnCrosheMenuShowListener() {
        return onCrosheMenuShowListener;
    }

    public CroshePopupMenu setOnCrosheMenuShowListener(OnCrosheMenuListener onCrosheMenuShowListener) {
        this.onCrosheMenuShowListener = onCrosheMenuShowListener;
        return this;
    }

    public boolean isAnimation() {
        return animation;
    }

    public CroshePopupMenu setAnimation(boolean animation) {
        this.animation = animation;
        return this;
    }

    public boolean isShowInWindow() {
        return showInWindow;
    }

    public CroshePopupMenu setShowInWindow(boolean showInWindow) {
        this.showInWindow = showInWindow;
        return this;
    }

    public CrosheMenuItem getItem(int index) {
        return (CrosheMenuItem) items.get(index);
    }

    public boolean isCancelable() {
        return cancelable;
    }

    public CroshePopupMenu setCancelable(boolean cancelable) {
        this.cancelable = cancelable;
        return this;
    }

    public boolean isCanceledOnTouchOutside() {
        return canceledOnTouchOutside;
    }

    public CroshePopupMenu setCanceledOnTouchOutside(boolean canceledOnTouchOutside) {
        this.canceledOnTouchOutside = canceledOnTouchOutside;
        return this;
    }

    public int size() {
        return items.size();
    }

    public Context getContext() {
        return ExitApplication.getContext();
    }

    public int getDialogWidth() {
        return dialogWidth;
    }

    public CroshePopupMenu setDialogWidth(int dialogWidth) {
        this.dialogWidth = dialogWidth;
        return this;
    }

    public int getDialogHeight() {
        return dialogHeight;
    }

    public CroshePopupMenu setDialogHeight(int dialogHeight) {
        this.dialogHeight = dialogHeight;
        return this;
    }

    public boolean isFullScreen() {
        return fullScreen;
    }

    public CroshePopupMenu setFullScreen(boolean fullScreen) {
        this.fullScreen = fullScreen;
        return this;
    }

    public View.OnClickListener getOnContainerClickListener() {
        return onContainerClickListener;
    }

    public CroshePopupMenu setOnContainerClickListener(View.OnClickListener onContainerClickListener) {
        this.onContainerClickListener = onContainerClickListener;
        return this;
    }

    private enum AnimEnum {
        LTRScaleAnim,
        LBRScaleAnim,
        RBLScaleAnim,
        RTLScaleAnim,
        BTTranslateAnim,
        BTMaskTranslateAnim,
        TopTranslateAnim,
        TopMaskTranslateAnim,
        CenterAnim,
        CenterMaskAnim,
        RightTranslateAnim,
        RightMaskTranslateAnim
    }
}
