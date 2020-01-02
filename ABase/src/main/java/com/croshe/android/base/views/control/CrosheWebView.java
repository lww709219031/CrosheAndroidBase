package com.croshe.android.base.views.control;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.croshe.android.base.AConfig;
import com.croshe.android.base.AConstant;
import com.croshe.android.base.AIntent;
import com.croshe.android.base.BaseApplication;
import com.croshe.android.base.R;
import com.croshe.android.base.activity.CrosheDownListActivity;
import com.croshe.android.base.entity.MessageEntity;
import com.croshe.android.base.extend.web.CrosheBaseJavaScript;
import com.croshe.android.base.listener.OnCrosheMenuClick;
import com.croshe.android.base.server.BaseRequest;
import com.croshe.android.base.utils.BaseAppUtils;
import com.croshe.android.base.utils.DialogUtils;
import com.croshe.android.base.utils.ExitApplication;
import com.croshe.android.base.utils.FileUtils;
import com.croshe.android.base.utils.ImageUtils;
import com.croshe.android.base.utils.MD5Encrypt;
import com.croshe.android.base.utils.MimeTypeUtils;
import com.croshe.android.base.utils.OKHttpUtils;
import com.croshe.android.base.views.menu.CrosheMenuItem;
import com.croshe.android.base.views.menu.CroshePopupMenu;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 安徽创息软件科技有限公司-技术支持，http://wwww.croshe.com
 * Created by Janesen on 2017/7/22 13:49.
 */
public class CrosheWebView extends WebView implements DownloadListener {
    private String onlyCode;
    private GestureDetector detector = null;
    private boolean isInterceptLongClick;
    private float mY, mX;

    private WebChromeClient webChromeClient;
    private WebViewClient webViewClient;

    private static ValueCallback<Uri[]> valueCallback;
    private boolean isFileUrl = false;
    protected CrosheBaseJavaScript crosheBaseJavaScript;
    private boolean isOverUrl = true;
    private boolean isLongText = true;
    private boolean isLongClick = true;

    private boolean isAuthorization = false;
    private boolean isToAuthorizationUrl = false;

    private String currUrl;

    public CrosheWebView(Context context) {
        super(context);
        onlyCode = MD5Encrypt.MD5(String.valueOf(System.currentTimeMillis()));
    }

    public CrosheWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        onlyCode = MD5Encrypt.MD5(String.valueOf(System.currentTimeMillis()));
    }

    public CrosheWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        onlyCode = MD5Encrypt.MD5(String.valueOf(System.currentTimeMillis()));
    }


    GestureDetector.SimpleOnGestureListener simpleOnGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
            isInterceptLongClick = false;

            HitTestResult hitResult = getHitTestResult();
            if (hitResult == null) return;
            int type = hitResult.getType();
            if (type == HitTestResult.IMAGE_TYPE || type == HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
                isInterceptLongClick = true;
                String imageUrl = hitResult.getExtra();

                if (ImageUtils.isBase64Image(imageUrl)) {
                    Bitmap base64Bitmap = ImageUtils.base64toBitmap(imageUrl);
                    File file = new File(getContext().getFilesDir().getAbsolutePath()
                            + "/Croshe/Images/" + MD5Encrypt.MD5(imageUrl) + ".png");

                    FileUtils.saveBitmapToPath(base64Bitmap, file.getAbsolutePath(), true);
                    imageUrl = file.getAbsolutePath();
                }

                final String finalImageUrl = imageUrl;
                CroshePopupMenu.newInstance(getContext())
                        .addItem("查看图片", new OnCrosheMenuClick() {
                            @Override
                            public void onClick(CrosheMenuItem item, View view) {
                                AIntent.startShowImage(getContext(), finalImageUrl);
                            }
                        })
                        .addItem("保存图片", new OnCrosheMenuClick() {
                            @Override
                            public void onClick(CrosheMenuItem item, View view) {
                                AIntent.saveImage(getContext(), finalImageUrl);
                            }
                        })
                        .addItem("识别二维码", new OnCrosheMenuClick() {
                            @Override
                            public void onClick(CrosheMenuItem item, View view) {
                                AIntent.readQrCode(getContext(), finalImageUrl);
                            }
                        })
                        .addItem("分享给好友", new OnCrosheMenuClick() {
                            @Override
                            public void onClick(CrosheMenuItem item, View view) {
                                if (BaseApplication.checkBaseFunction(AConstant.BaseFunctionEnum.浏览器网页转发)) {
                                    MessageEntity messageEntity = new MessageEntity();
                                    messageEntity.setType(MessageEntity.MessageType.Image);
                                    messageEntity.setData(finalImageUrl);
                                    EventBus.getDefault().post(messageEntity);
                                } else {
                                    AIntent.shareImage(getContext(), finalImageUrl);
                                }
                            }
                        })
                        .show((int) e.getRawX(), (int) e.getRawY());

            } else if (type == HitTestResult.SRC_ANCHOR_TYPE) {
                isInterceptLongClick = true;
                final String linkUrl = hitResult.getExtra();
                CroshePopupMenu.newInstance(getContext())
                        .addItem("复制链接", new OnCrosheMenuClick() {
                            @Override
                            public void onClick(CrosheMenuItem item, View view) {
                                ClipboardManager cm = (ClipboardManager) view.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                                cm.setText(linkUrl);

                                // 创建普通字符型ClipData
                                ClipData mClipData = ClipData.newPlainText("url", linkUrl);

                                // 将ClipData内容放到系统剪贴板里。
                                cm.setPrimaryClip(mClipData);

                                Toast.makeText(view.getContext(), "复制成功！", Toast.LENGTH_LONG).show();
                            }
                        })
                        .addItem("浏览器打开", new OnCrosheMenuClick() {
                            @Override
                            public void onClick(CrosheMenuItem item, View view) {
                                Uri uri = Uri.parse(linkUrl);
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                getContext().startActivity(intent);
                            }
                        })
                        .show((int) e.getRawX(), (int) e.getRawY());
            } else {
                isInterceptLongClick = !isLongText;
            }
        }

    };

    @SuppressLint("SetJavaScriptEnabled")
    public void initView() {
        try {
            detector = new GestureDetector(getContext(), simpleOnGestureListener);
            this.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return isInterceptLongClick;
                }
            });
            this.setClickable(true);
            crosheBaseJavaScript = new CrosheBaseJavaScript(this);

            final WebSettings settings = getSettings();
            settings.setJavaScriptEnabled(true);
            settings.setDomStorageEnabled(true);

            settings.setAppCacheEnabled(false);
            settings.setAllowFileAccess(true);
            settings.setAllowContentAccess(true);

            settings.setPluginState(WebSettings.PluginState.ON);
            settings.setJavaScriptCanOpenWindowsAutomatically(true);

            settings.setCacheMode(WebSettings.LOAD_DEFAULT);
            settings.setAppCacheMaxSize(1024 * 1024 * 50);// 设置缓冲大小
            settings.setLoadWithOverviewMode(true);
            settings.setUseWideViewPort(true);
            settings.setSupportZoom(true);
            settings.setBuiltInZoomControls(true);
            settings.setDisplayZoomControls(false);
            settings.setLoadsImagesAutomatically(true);
            settings.setDatabaseEnabled(true);
            final String dbPath = getContext().getDir("webview-db", Context.MODE_PRIVATE).getPath();
            settings.setDatabasePath(dbPath);
            settings.setDefaultTextEncodingName("utf-8");
            settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                settings.setMediaPlaybackRequiresUserGesture(false);
            }

            final String cachePath = getContext().getDir("webview-cache", Context.MODE_PRIVATE).getPath();
            settings.setAppCachePath(cachePath);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                setWebContentsDebuggingEnabled(true);
            }
            setDownloadListener(this);

            initWebChromeClient();
            initWebViewClient();
        } catch (Exception e) {
        }
    }


    /**
     * 显示等待遮罩
     */
    public void showWaitMask() {
        hideWaitMask();
        View view = AConfig.getWebWaitView();
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.android_base_view_mask, null);
        }
        view.setTag("WaitMask");
        view.setClickable(true);
        if (view.getParent() != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            parent.removeView(view);
        }
        this.addView(view, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }


    /**
     * 隐藏等待遮罩
     */
    public void hideWaitMask() {
        View waitMask = this.findViewWithTag("WaitMask");
        if (waitMask != null) {
            this.removeView(waitMask);
        }
    }


    public void initWebChromeClient() {
        setWebChromeClient(new CrosheWebChromeClient());

    }


    public void initWebViewClient() {
        setWebViewClient(new CrosheWebViewClient());
    }


    @Override
    public void setWebChromeClient(WebChromeClient webChromeClient) {
        super.setWebChromeClient(webChromeClient);
        this.webChromeClient = webChromeClient;
    }

    @Override
    public void setWebViewClient(WebViewClient webViewClient) {
        super.setWebViewClient(webViewClient);
        this.webViewClient = webViewClient;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mY = event.getY();
                mX = event.getX();
                if (this.getScrollY() <= 0) {
                    this.scrollTo(getScrollX(), 2);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (event.getY() > mY) {
                    requestDisallowInterceptTouchEvent(true);
                }
                break;
        }
        if (isLongClick && detector != null) {
            detector.onTouchEvent(event);
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        //已经处于顶端
        if (t == 0) {
            requestDisallowInterceptTouchEvent(false);
        }
    }


    public void destroy() {
        ViewParent parent = getParent();
        if (parent != null) {
            ((ViewGroup) parent).removeView(this);
        }

        loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
        stopLoading();
        getSettings().setJavaScriptEnabled(false);
        clearHistory();
        clearCache(false);

        clearView();
        removeAllViews();
        try {
            super.destroy();
        } catch (Throwable ex) {
        } finally {
            releaseConfigCallback();
        }
    }

    // 解决WebView内存泄漏问题；
    private void releaseConfigCallback() {
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) { // JELLY_BEAN
                try {
                    Field field = WebView.class.getDeclaredField("mWebViewCore");
                    field = field.getType().getDeclaredField("mBrowserFrame");
                    field = field.getType().getDeclaredField("sConfigCallback");
                    field.setAccessible(true);
                    field.set(null, null);
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) { // KITKAT
                try {
                    Field sConfigCallback = Class.forName("android.webkit.BrowserFrame").getDeclaredField("sConfigCallback");
                    if (sConfigCallback != null) {
                        sConfigCallback.setAccessible(true);
                        sConfigCallback.set(null, null);
                    }
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
        }
    }


    /**
     * 清除缓存
     */
    public void clearCache() {
        this.clearCache(true);
    }

    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
        String fileName = getFileName(url, mimetype, contentDisposition);
        openFile(url, fileName);
        isFileUrl = true;
    }

    @Override
    public void loadUrl(final String url) {
        if (url.startsWith(BaseRequest.mainUrl) && StringUtils.isNotEmpty(BaseRequest.mainUrl)) {//必须是系统内的路径
            if (isAuthorization) {
                this.currUrl = url;
                super.loadUrl(formatUrl(url));
            } else {
                isAuthorization = true;
                isToAuthorizationUrl = true;
                super.loadUrl(BaseRequest.authorization(url));
            }
        } else {
            this.currUrl = url;
            super.loadUrl(formatUrl(url));
        }
        isFileUrl = false;

        if (url.startsWith("http://") || url.startsWith("https://")) {
            if (AConfig.isBrowserMask()) {
                showWaitMask();
            } else {
                hideWaitMask();
            }
        }
    }

    @Override
    public void reload() {
        if (AConfig.isBrowserMask()) {
            showWaitMask();
        } else {
            hideWaitMask();
        }
        super.reload();
    }

    @Override
    public void goBack() {
//        if (AConfig.isBrowserMask()) {
//            showWaitMask();
//        } else {
//            hideWaitMask();
//        }
        super.goBack();
    }

    @Override
    public void loadUrl(final String url, final Map<String, String> additionalHttpHeaders) {
        this.currUrl = url;
        if (url.startsWith(BaseRequest.mainUrl) && StringUtils.isNotEmpty(BaseRequest.mainUrl)) {//必须是系统内的路径
            if (isAuthorization) {
                super.loadUrl(formatUrl(url), additionalHttpHeaders);
            } else {
                isAuthorization = true;
                isToAuthorizationUrl = true;
                super.loadUrl(BaseRequest.authorization(url), additionalHttpHeaders);
            }
        } else {
            super.loadUrl(formatUrl(url), additionalHttpHeaders);
        }

        isFileUrl = false;

        if (url.startsWith("http://") || url.startsWith("https://")) {
            if (AConfig.isBrowserMask()) {
                showWaitMask();
            } else {
                hideWaitMask();
            }
        }
    }

    @Override
    public void loadData(String data, String mimeType, String encoding) {
        super.loadData(data, mimeType, encoding);
        isFileUrl = false;
    }

    @Override
    public void loadDataWithBaseURL(String baseUrl, String data, String mimeType, String encoding, String historyUrl) {
        super.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl);
        isFileUrl = false;
    }

    public void openFile(final String url, String fileName) {
        if (AConfig.getOnReaderListener() != null) {
            if (AConfig.getOnReaderListener().checkReader(url, fileName)) {
                return;
            }
        }
        DialogUtils.prompt(ExitApplication.getContext(), "下载文件", fileName, fileName, new DialogUtils.OnPromptCallBack() {
            @Override
            public void promptResult(boolean success, String text) {
                if (success) {
                    CrosheDownListActivity.doDownLoad(ExitApplication.getContext(), url, text + "." + FileUtils.getFileExtName(url));
                }
            }
        });
    }

    private String getFileName(String url, String mimeType, String contentDisposition) {
        try {
            if (StringUtils.isNotEmpty(contentDisposition)) {
                String reg = ".*filename=(.*)";
                Matcher m = Pattern.compile(reg).matcher(contentDisposition);
                if (m.find()) {
                    String urlName = URLDecoder.decode(m.group(1), "utf-8");
                    return urlName;
                }
            }
            if (FileUtils.isFileUrl(url)) {
                return URLDecoder.decode(FileUtils.getFileName(url), "utf-8");
            }
            return MD5Encrypt.MD5(url) + MimeTypeUtils.getFileExtendName(mimeType);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return MD5Encrypt.MD5(url);
    }


    public boolean isFileUrl() {
        return isFileUrl;
    }

    public void setFileUrl(boolean fileUrl) {
        isFileUrl = fileUrl;
    }


    /**
     * 更新cookie
     *
     * @param url
     * @param value
     */
    public void updateCookies(String url, String value) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) { // 2.3及以下
            CookieSyncManager.createInstance(getContext().getApplicationContext());
        }
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setCookie(url, value);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
            CookieSyncManager.getInstance().sync();
        }
    }


    public static class CrosheWebChromeClient extends WebChromeClient {

        @Override
        public boolean onJsAlert(WebView view, String url, String message,
                                 final JsResult result) {

            DialogUtils.alert(ExitApplication.getContext(), "系统提醒", message, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    result.confirm();
                }
            });
            return true;
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message,
                                   final JsResult result) {
            // TODO Auto-generated method stub

            DialogUtils.confirm(ExitApplication.getContext(), "系统提醒", message, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    result.confirm();
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    result.cancel();
                }
            });
            return true;
        }


        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, final JsPromptResult result) {
            DialogUtils.prompt(ExitApplication.getContext(), message, defaultValue, new DialogUtils.OnPromptCallBack() {
                @Override
                public void promptResult(boolean success, String text) {
                    if (success) {
                        result.confirm(text);
                    } else {
                        result.cancel();
                    }
                }
            });
            return true;
        }

        @Override
        public void onCloseWindow(WebView window) {
            super.onCloseWindow(window);
            if (window.getContext() instanceof Activity) {
                Activity activity = (Activity) window.getContext();
                activity.finish();
            }
        }


        //        @Override
//        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
//                                         FileChooserParams fileChooserParams) {
//            CrosheWebView crosheWebView = (CrosheWebView) webView;
//            valueCallback = filePathCallback;
//            Bundle bundle = new Bundle();
//            bundle.putString(AConstant.EXTRA_DO_ACTION, "WebSelectFile" + crosheWebView.onlyCode);
//            bundle.putInt(AConstant.CrosheAlbumActivity.EXTRA_MAX_SELECT.name(), 1);
//            AIntent.startAlbum(webView.getContext(), bundle);
//            return true;
//        }
    }


    public static class CrosheWebViewClient extends WebViewClient {

        public boolean shouldOverrideUrlLoading(final WebView view, String url) {
            boolean getImages = url.contains("@getImages");
            CrosheWebView crosheWebView = (CrosheWebView) view;

            url = url.replace("@getImages", "");
            if (FileUtils.isImageUrl(url)) {
                if (getImages) {
                    final String finalUrl = url;
                    crosheWebView.crosheBaseJavaScript.execute(new CrosheBaseJavaScript.WebResponse() {
                        @Override
                        public void callBack(boolean success, Object object) {
                            List<String> images = JSON.parseArray(JSON.toJSONString(object), String.class);
                            AIntent.startShowImage(view.getContext(), finalUrl, images.toArray(new String[]{}));
                        }

                        @Override
                        public CrosheBaseJavaScript getBaseJavaScript() {
                            return null;
                        }
                    }, "getImages");
                } else {
                    AIntent.startShowImage(view.getContext(), url);
                }
                return true;
            }
            if (FileUtils.isVideoUrl(url)) {
                AIntent.startShowImage(view.getContext(), url);
                return true;
            }
            if ("about:blank".equals(url)) {
                return true;                         //不需要处理空白页
            }

            if (url.toLowerCase().startsWith("https") || url.toLowerCase().startsWith("http")) {
                Map<String, Object> urlParams = OKHttpUtils.getUrlParams(url);
                if (urlParams.containsKey("target")) {
                    String target = urlParams.get("target").toString();
                    if (target.equals("_blank")) {
                        urlParams.remove("target");
                        AIntent.startBrowser(crosheWebView.getContext(), url.substring(0, url.indexOf("?")) + "?"
                                + OKHttpUtils.getInstance().convertToGet(urlParams));
                        return true;
                    }
                }
                if (crosheWebView.isOverUrl || crosheWebView.isToAuthorizationUrl) {
                    view.loadUrl(url);
                    crosheWebView.isToAuthorizationUrl = false;
                } else {
                    AIntent.startBrowser(view.getContext(), url);
                }
                return true;
            } else {
                if (AConfig.isBrowserScheme()) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        crosheWebView.getContext().startActivity(intent);
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
                return true;
            }
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            super.onReceivedSslError(view, handler, error);
            handler.proceed();// 接受所有网站的证书
        }


    }

    public String formatUrl(String url) {
        if (FileUtils.isFileUrl(url)) {
            return url;
        }
        if (url.startsWith("http://") || url.startsWith("https://")) {
            String firstChar = "?";
            if (url.contains("?")) {
                firstChar = "&";
            }
            if (!AConfig.isDebug()) {
                return url + firstChar + "from=CrosheBrowser&apv=" + BaseAppUtils.getVersionCode(getContext());
            } else {
                return url + firstChar + "from=CrosheBrowser&apv=" + System.currentTimeMillis();
            }
        }
        return url;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public boolean isOverUrl() {
        return isOverUrl;
    }

    public void setOverUrl(boolean overUrl) {
        isOverUrl = overUrl;
    }

    public CrosheBaseJavaScript getCrosheBaseJavaScript() {
        return crosheBaseJavaScript;
    }

    public void setCrosheBaseJavaScript(CrosheBaseJavaScript crosheBaseJavaScript) {
        this.crosheBaseJavaScript = crosheBaseJavaScript;
    }

    public boolean isLongText() {
        return isLongText;
    }

    public void setLongText(boolean longText) {
        isLongText = longText;
    }

    public boolean isLongClick() {
        return isLongClick;
    }

    public void setLongClick(boolean longClick) {
        isLongClick = longClick;
    }
}

