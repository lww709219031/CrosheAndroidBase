package com.croshe.android.base.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.croshe.android.base.AConfig;
import com.croshe.android.base.AConstant;
import com.croshe.android.base.AIntent;
import com.croshe.android.base.BaseApplication;
import com.croshe.android.base.R;
import com.croshe.android.base.entity.MessageEntity;
import com.croshe.android.base.extend.web.CrosheBaseJavaScript;
import com.croshe.android.base.listener.OnCrosheMenuClick;
import com.croshe.android.base.utils.DensityUtils;
import com.croshe.android.base.utils.FileUtils;
import com.croshe.android.base.utils.ImageUtils;
import com.croshe.android.base.utils.NumberUtils;
import com.croshe.android.base.utils.OKHttpUtils;
import com.croshe.android.base.views.control.CrosheWebView;
import com.croshe.android.base.views.layout.CrosheSlidingUpPaneLayout;
import com.croshe.android.base.views.menu.CrosheMenuItem;
import com.croshe.android.base.views.menu.CroshePopupMenu;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 浏览器
 * 安徽创息软件科技有限公司-技术支持，http://wwww.croshe.com
 * Created by Janesen on 2017/7/4.
 */
public class CrosheBrowserActivity extends CrosheBaseSlidingActivity {

    public static String ACTION_CLOSE_MORE = "ACTION_CLOSE_MORE";

    public static String ACTION_TIMER_CLOSE = "ACTION_TIMER_CLOSE";

    public static final String EXTRA_URL = AConstant.CrosheBrowserActivity.EXTRA_URL.name();


    protected CrosheWebView webView;
    private CrosheSlidingUpPaneLayout crosheSlidingUpPaneLayout;
    private ProgressBar progressBar;
    private TextView tvTitle, tvWebInfo, tvTimer, tvSupport;
    private boolean isError, isShowMore = true, finish = true;
    private String firstImageUrl;
    private LinearLayout llRightContainer;
    private int maxDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.android_base_activity_browser);
        fullScreen(true);
        getSupportActionBar().setTitle("");
        initView();
        hideMoreAction();

        isCheckBack = true;
    }

    public void initView() {
        findViewById(R.id.android_base_imgMore).setVisibility(View.GONE);

        webView = getView(R.id.android_base_crosheWebView);

        tvTitle = getView(R.id.android_base_tvTitle);
        tvWebInfo = getView(R.id.android_base_web_info);
        tvSupport = getView(R.id.android_base_tvSupport);
        llRightContainer = getView(R.id.android_base_llRightContainer);

        tvTimer = getView(R.id.android_base_tvTimer);

        progressBar = findViewById(R.id.progressBar);
        crosheSlidingUpPaneLayout = findViewById(R.id.android_base_crosheSlidingUp);
        crosheSlidingUpPaneLayout.setCanOpen(false);
        crosheSlidingUpPaneLayout.setShadowResourceTop(R.drawable.android_base_shadow_top);
        crosheSlidingUpPaneLayout.setShadowHeight(DensityUtils.dip2px(20));


        findViewById(R.id.android_base_imgMore).setOnClickListener(this);
        findViewById(R.id.android_base_imgClose).setOnClickListener(this);

        configWebView();

        if (getIntent().getExtras() != null) {
            tvTitle.setText("加载中……");
            String url = getIntent().getStringExtra(AConstant.CrosheBrowserActivity.EXTRA_URL.name());

            if (StringUtils.isEmpty(url)) {
                showError();
                return;
            }
            webView.loadUrl(url);
        }

        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvTitle.length() > 10) {
                    toast(tvTitle.getText());
                }
            }
        });

        if (AConfig.isBrowserSupport()) {
            tvSupport.setVisibility(View.VISIBLE);
        } else {
            tvSupport.setVisibility(View.GONE);
        }
    }


    protected Map<String, String> getRequestHeader(String url) {
        return new HashMap<>();
    }

    private void configWebView() {
        webView.initView();
        if (AConfig.getTargetJavaScriptClass() != null) {
            webView.getCrosheBaseJavaScript().setTargetClass(AConfig.getTargetJavaScriptClass());
        }
        webView.requestFocus(View.FOCUS_DOWN);
        webView.setWebChromeClient(new CrosheWebView.CrosheWebChromeClient() {

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (!tvTitle.getText().equals(title)) {
                    tvTitle.setText(title);
                }
            }


            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (progressBar.getVisibility() == View.GONE) {
                    progressBar.bringToFront();
                    progressBar.setVisibility(View.VISIBLE);
                }
                progressBar.setProgress(newProgress);
                if (tvTitle.length() == 0) {
                    tvTitle.setText("加载中……");
                }


                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                }
            }

        });

        webView.setWebViewClient(new CrosheWebView.CrosheWebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
                if (webView.canGoBack()) {
                    findViewById(R.id.android_base_imgClose).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.android_base_imgClose).setVisibility(View.GONE);
                }

                if (isShowMore) {
                    findViewById(R.id.android_base_imgMore).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.android_base_imgMore).setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.bringToFront();
                progressBar.setVisibility(View.VISIBLE);
                hideError();
//                if (isShowMore) {
//                    tvWebInfo.setText("页面由 " + OKHttpUtils.getUrlHost(url) + " 提供");
//                } else {
//                    tvWebInfo.setText("版权归 " + getApplicationName() + " 所有");
//                }
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                if (url.contains("in.croshe.com") && url.contains(".png")) {
                    firstImageUrl = url;
                }
                if (StringUtils.isEmpty(firstImageUrl) && FileUtils.isStaticImageUrl(url)) {
                    firstImageUrl = url;
                }
                return super.shouldInterceptRequest(view, webView.formatUrl(url));
            }


        });
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.android_base_imgClose) {
            isCheckBack = false;
            CrosheBrowserActivity.this.finish();
        } else if (v.getId() == R.id.android_base_imgMore) {
            int[] location = new int[2];
            v.getLocationOnScreen(location);

            CroshePopupMenu popupMenu = CroshePopupMenu.newInstance(context)
                    .setLineColor(Color.parseColor("#cccccc"))
                    .setMenuWidth(DensityUtils.dip2px(200))
                    .addItem("关闭页面", false, R.drawable.android_base_ic_close_gray, new OnCrosheMenuClick() {
                        @Override
                        public void onClick(CrosheMenuItem item, View view) {
                            isCheckBack = false;
                            CrosheBrowserActivity.this.finish();
                        }
                    });
            popupMenu.addItem("复制网址", R.drawable.android_base_ic_copy, new OnCrosheMenuClick() {
                @Override
                public void onClick(CrosheMenuItem item, View view) {
                    ClipboardManager cm = (ClipboardManager) view.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    cm.setText(webView.getUrl());
                    // 创建普通字符型ClipData
                    ClipData mClipData = ClipData.newPlainText("url", webView.getUrl());
                    // 将ClipData内容放到系统剪贴板里。
                    cm.setPrimaryClip(mClipData);

                    Toast.makeText(view.getContext(), "复制成功！", Toast.LENGTH_LONG).show();
                }
            });
            popupMenu.addItem("刷新页面", R.drawable.android_base_ic_refresh, new OnCrosheMenuClick() {
                @Override
                public void onClick(CrosheMenuItem item, View view) {
                    webView.reload();
                }
            });
            popupMenu.addItem("清除缓存", R.drawable.android_base_ic_clear_cache, new OnCrosheMenuClick() {
                @Override
                public void onClick(CrosheMenuItem item, View view) {
                    webView.clearCache();
                    toast("清除成功！");
                }
            });
            popupMenu.addItem("在浏览器打开", R.drawable.android_base_ic_browser, new OnCrosheMenuClick() {
                @Override
                public void onClick(CrosheMenuItem item, View view) {
                    Uri uri = Uri.parse(webView.getUrl());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            });
            if (!isError && BaseApplication.checkBaseFunction(AConstant.BaseFunctionEnum.浏览器网页转发)) {
                popupMenu.addItem("分享给好友", R.drawable.android_base_ic_forward, new OnCrosheMenuClick() {
                    @Override
                    public void onClick(CrosheMenuItem item, View view) {
                        forward();
                    }
                });
            }
            popupMenu.addItem("下载记录", R.drawable.android_base_ic_download, new OnCrosheMenuClick() {
                @Override
                public void onClick(CrosheMenuItem item, View view) {
                    AIntent.startDownList(context);
                }
            });
            popupMenu.showAnchorRight(v);
        }
    }


    @Override
    public void finish() {
        if (!finish) {
            return;
        }
        if (isCheckBack) {
            if (webView.canGoBack()) {
                webView.goBack();
                return;
            }
        }

        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroy();
    }

    public void destroy() {
        try {
            if (webView != null) {
                webView.destroy();
            }
        } catch (Exception e) {
        }
    }


    public void showError() {
        tvTitle.setText("加载出错");
        findViewById(R.id.android_base_web_error).setVisibility(View.VISIBLE);
        findViewById(R.id.android_base_web_error).bringToFront();
        isError = true;
    }

    public void hideError() {
        findViewById(R.id.android_base_web_error).setVisibility(View.GONE);
        isError = false;
    }


    /**
     * 设置内嵌JS的类
     */
    public void setInnerJSClass(Class<? extends CrosheBaseJavaScript> targetClass) {
        webView.getCrosheBaseJavaScript().setTargetClass(targetClass);
    }


    /**
     * 转发
     */
    public void forward() {
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setType(MessageEntity.MessageType.Other);
        messageEntity.setAction("CHAT_ACTION_WEB_URL");

        Map<String, Object> webInfo = new HashMap<>();
        webInfo.put("imgUrl", firstImageUrl);
        webInfo.put("url", webView.getUrl());
        webInfo.put("title", webView.getTitle());
        webInfo.put("content", webView.getUrl());

        messageEntity.setData(webInfo);

        EventBus.getDefault().post(messageEntity);
    }


    /**
     * 在浏览器头部添加功能按钮
     *
     * @param view
     */
    public void addRightAction(View view) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(DensityUtils.dip2px(55), LinearLayout.LayoutParams.MATCH_PARENT);
        llRightContainer.addView(view, layoutParams);
    }


    @Subscribe
    public void onBrowserEvent(String action) {
        if (action.equals(ACTION_CLOSE_MORE)) {
            hideMoreAction();
        } else if (action.startsWith(ACTION_TIMER_CLOSE)) {
            startTimerClose(NumberUtils.formatToInt(action.split("@")[1]));
        }
    }

    /**
     * 启动计时关闭
     */
    public void startTimerClose(int maxDuration) {
        if (maxDuration == 0) {
            return;
        }
        tvTimer.setVisibility(View.VISIBLE);
        finish = false;
        setSlideEnable(false);
        toolbar.setNavigationIcon(null);
        this.maxDuration = maxDuration;
        runnable.run();
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            tvTimer.setText(String.valueOf(maxDuration));
            maxDuration--;
            if (maxDuration > 0) {
                handler.postDelayed(runnable, 1000);
            } else {
                finish = true;
                tvTimer.setVisibility(View.GONE);
                toolbar.setNavigationIcon(R.drawable.android_base_ic_back);
                toolbar.setNavigationIcon(ImageUtils.tintDrawable(toolbar.getNavigationIcon(), ColorStateList.valueOf(findColor(R.color.colorTitle))));
                setSlideEnable(true);
            }
        }
    };

    /**
     * 隐藏浏览器的更多操作
     */
    public void hideMoreAction() {
        isShowMore = false;
        findViewById(R.id.android_base_imgMore).setVisibility(View.GONE);
//        tvWebInfo.setText("版权归 " + getApplicationName() + " 所有");
//        tvSupport.setText("安徽创息软件科技提供技术支持");
        webView.setLongClick(false);
    }


    /**
     * 显示浏览器的更多操作
     */
    public void showMoreAction() {
        isShowMore = true;
        findViewById(R.id.android_base_imgMore).setVisibility(View.GONE);
//        tvSupport.setText("安徽创息软件浏览器C1内核提供技术支持");
        webView.setLongClick(true);
    }


    public boolean isImageUrl(String url) {
        return Pattern.matches(".+(.JPEG|.jpeg|.JPG|.jpg|.PNG|.png)$", url.toLowerCase());
    }


}

