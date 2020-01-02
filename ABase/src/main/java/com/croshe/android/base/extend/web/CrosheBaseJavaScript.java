package com.croshe.android.base.extend.web;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import com.croshe.android.base.AConstant;
import com.croshe.android.base.AIntent;
import com.croshe.android.base.activity.CrosheBrowserActivity;
import com.croshe.android.base.activity.CrosheDownListActivity;
import com.croshe.android.base.entity.FileEntity;
import com.croshe.android.base.entity.SimpleHttpCallBack;
import com.croshe.android.base.server.BaseRequest;
import com.croshe.android.base.utils.BaseAppUtils;
import com.croshe.android.base.utils.DialogUtils;
import com.croshe.android.base.utils.FileUtils;
import com.croshe.android.base.utils.ImageUtils;
import com.croshe.android.base.utils.MD5Encrypt;
import com.croshe.android.base.utils.NumberUtils;
import com.croshe.android.base.utils.OKHttpUtils;
import com.croshe.android.base.views.control.CrosheWebView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;


/**
 * webview注入js方法的基本父类
 */
public class CrosheBaseJavaScript {
    private static String onlyCode;
    protected Gson gson = new Gson();
    protected WebView webView;
    protected Handler handler = new Handler(Looper.getMainLooper());
    private Class<? extends CrosheBaseJavaScript> targetClass;
    private Map<String, WebResponse> maps = new HashMap<String, WebResponse>();//当客户端执行webjs时回调对象
    protected Context context;
    protected Set<String> openedFunction = new HashSet<>();

    private WebResponse currResponse;

    private String regStr;

    public CrosheBaseJavaScript() {

    }

    public CrosheBaseJavaScript(WebView webView) {
        this(webView, null);
    }

    public CrosheBaseJavaScript(WebView webView, Class<? extends CrosheBaseJavaScript> targetClass) {
        this.webView = webView;
        this.targetClass = targetClass;
        webView.addJavascriptInterface(this, "app");
        String ua = webView.getSettings().getUserAgentString();
        webView.getSettings().setUserAgentString(ua + ";CROSHE".toLowerCase());
        webView.getSettings().setJavaScriptEnabled(true);
        context = webView.getContext();

    }

    public Class<?> getTargetClass() {
        return targetClass;
    }


    public boolean checkFunction(String function) {
        return openedFunction.contains(function);
    }

    public void openFunction(String function) {
        openedFunction.add(function);
    }

    public void setTargetClass(Class<? extends CrosheBaseJavaScript> targetClass) {
        this.targetClass = targetClass;
    }

    @JavascriptInterface
    public void doJavaScript(final String methodName, final String params) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    List<Object> paramsArray = gson.fromJson(URLDecoder.decode(params, "utf-8"), new TypeToken<List<Object>>() {
                    }.getType());
                    // List<Object> paramsArray= (List<Object>) JSON.parse(URLDecoder.decode(params, "utf-8"));
                    Class<?>[] paramsClass = new Class<?>[paramsArray.size()];
                    Object[] paramsValue = new Object[paramsArray.size()];
                    final String key = paramsArray.get(0).toString();//第一个参数 为key

                    paramsClass[0] = WebResponse.class;
                    paramsValue[0] = new WebResponse() {

                        @Override
                        public void callBack(final boolean success, final Object object) {
                            // TODO Auto-generated method stub
                            handler.post(new Runnable() {

                                @Override
                                public void run() {
                                    // TODO Auto-generated method stub
                                    Map<String, Object> callback = new HashMap<>();
                                    callback.put("success", success);
                                    callback.put("key", key);
                                    callback.put("result", object);
                                    callback.put("message", "操作成功！");
                                    webView.loadUrl("javascript:appJs.appCallBack('" + key + "','" + gson.toJson(callback) + "');");
                                }
                            });
                        }

                        @Override
                        public CrosheBaseJavaScript getBaseJavaScript() {
                            return CrosheBaseJavaScript.this;
                        }
                    };

                    for (int i = 1; i < paramsArray.size(); i++) {//从第二个参数开始
                        Object object = paramsArray.get(i);
                        paramsClass[i] = object.getClass();
                        paramsValue[i] = object;
                    }

                    if (methodName.equals("appCallBack")) {
                        appCallBack(paramsValue[1].toString(), paramsValue[2]);
                    } else {
                        if (targetClass != null) {
                            Method method = targetClass.getMethod(methodName, paramsClass);
                            CrosheBaseJavaScript crosheBaseJavaScript = targetClass.newInstance();
                            crosheBaseJavaScript.setWebView(webView);
                            crosheBaseJavaScript.context = webView.getContext();
                            method.invoke(crosheBaseJavaScript, paramsValue);
                        } else {
                            Method method = CrosheBaseJavaScript.class.getMethod(methodName, paramsClass);
                            CrosheBaseJavaScript baseJavaScript = CrosheBaseJavaScript.class.newInstance();
                            baseJavaScript.setWebView(webView);
                            baseJavaScript.context = webView.getContext();
                            method.invoke(baseJavaScript, paramsValue);
                        }
                    }
                } catch (Exception e) {
                    if (BaseAppUtils.isApkDebuggable(context)) {
                        webView.loadUrl("javascript:appJs.error('" + e.toString() + "');");
                    }
                }
            }
        });
    }


    /**
     * toast的消息
     *
     * @param webResponse
     * @param message
     */
    public void toast(WebResponse webResponse, String message) {
        Toast.makeText(webView.getContext(), message, Toast.LENGTH_LONG).show();
    }


    /**
     * 关闭浏览器窗口
     *
     * @param webResponse
     */
    public void closeWindow(WebResponse webResponse) {
        ((Activity) webView.getContext()).finish();
    }

    /**
     * 显示等待对话框
     *
     * @param response
     * @param msg
     */
    public void showLoadingDialog(WebResponse response, String msg) {
        try {
            AIntent.doShowProgress(msg);
        } catch (Exception e) {
        }
    }

    /**
     * 关闭等待对话框
     *
     * @param response
     */
    public void hideLoadingDialog(WebResponse response) {
        AIntent.doHideProgress();
    }


    @JavascriptInterface
    public void showImage(String url, String[] moreJson) {
        AIntent.startShowImage(context, url, moreJson);
    }


    /**
     * 下载文件
     *
     * @param fileName
     * @param url
     */
    @JavascriptInterface
    public void downFile(final String fileName, final String url) {
        DialogUtils.prompt(context, "下载文件", fileName,
                fileName, new DialogUtils.OnPromptCallBack() {
                    @Override
                    public void promptResult(boolean success, String text) {
                        if (success) {
                            CrosheDownListActivity.doDownLoad(context, url, text + "." + FileUtils.getFileExtName(url));
                        }
                    }
                });
    }


    /**
     * 关闭浏览器更多的功能
     */
    public void closeMore(WebResponse response) {
        EventBus.getDefault().post(CrosheBrowserActivity.ACTION_CLOSE_MORE);
    }


    /**
     * 计时器关闭
     * @param response
     * @param duration
     */
    public void closeTimer(WebResponse response, String duration) {
        EventBus.getDefault().post(CrosheBrowserActivity.ACTION_TIMER_CLOSE + "@" + duration);
    }


    /**
     * 选择文件
     *
     * @param response
     */
    public void selectFile(WebResponse response, String regStr) {
        this.currResponse = response;
        this.regStr = regStr;
        EventBus.getDefault().register(this);
        onlyCode = MD5Encrypt.MD5(String.valueOf(System.currentTimeMillis()));
        Bundle bundle = new Bundle();
        bundle.putString(AConstant.EXTRA_DO_ACTION, "WebSelectFile" + onlyCode);
        bundle.putBoolean(AConstant.CrosheAlbumActivity.EXTRA_VIDEO.name(), true);
        AIntent.startAlbum(webView.getContext(), bundle);
    }


    /**
     * 选择图片
     *
     * @param response
     */
    public void selectImage(WebResponse response, String maxSelect) {
        this.currResponse = response;
        EventBus.getDefault().register(this);
        onlyCode = MD5Encrypt.MD5(String.valueOf(System.currentTimeMillis()));
        Bundle bundle = new Bundle();
        bundle.putString(AConstant.EXTRA_DO_ACTION, "WebSelectFile" + onlyCode);
        bundle.putInt(AConstant.CrosheAlbumActivity.EXTRA_MAX_SELECT.name(), NumberUtils.formatToInt(maxSelect));
        AIntent.startAlbum(webView.getContext(), bundle);
    }


    /**
     * 隐藏等待遮罩
     * @param response
     */
    public void hideWaitMask(WebResponse response) {
        if (webView instanceof CrosheWebView) {
            CrosheWebView crosheWebView = (CrosheWebView) webView;
            crosheWebView.hideWaitMask();
        }
    }


    /**
     * 显示等待遮罩
     * @param response
     */
    public void showWaitMask(WebResponse response) {
        if (webView instanceof CrosheWebView) {
            CrosheWebView crosheWebView = (CrosheWebView) webView;
            crosheWebView.showWaitMask();
        }
    }


    /**
     * 发送事件
     *
     * @param response
     * @param action
     */
    public void postEvent(WebResponse response, String action) {
        EventBus.getDefault().post(action);
    }



    /**
     * 发送事件
     *
     * @param response
     * @param action
     */
    public void postNotify(WebResponse response, String action) {
        EventBus.getDefault().post(action);
    }


    /**
     * 打开浏览器
     * @param response
     * @param url
     */
    public void openBrowser(WebResponse response, String url) {
        AIntent.startBrowser(context, url);
    }



    /**
     * 执行webjs
     *
     * @param methodName 方法名
     * @param params     方法参数
     */
    public void execute(WebResponse response, String methodName, Object... params) {
        try {
            String key = MD5Encrypt.MD5(System.currentTimeMillis() + methodName);
            maps.put(key, response);
            String paramStr = "";
            for (Object obj : params) {
                if (obj instanceof Number) {
                    paramStr += "," + obj.toString() + "";
                } else {
                    paramStr += ",'" + obj.toString() + "'";
                }
            }
            if (paramStr.length() == 0) paramStr = ",";
            webView.loadUrl("javascript:appJs.androidExecute('" + key + "','" + methodName + "',\"" + paramStr.substring(1) + "\");");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }



    private void appCallBack(String key, Object value) {
        maps.get(key).callBack(true, value);
    }


    public WebView getWebView() {
        return webView;
    }

    public void setWebView(WebView webView) {
        this.webView = webView;
    }


    @Subscribe
    public void onEventByIntent(Intent intent) {
        String action = intent.getStringExtra(AConstant.EXTRA_DO_ACTION);
        if (action.equals("WebSelectFile" + onlyCode)) {
            EventBus.getDefault().unregister(this);

            final ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMessage("获取文件中，请稍后……");
            progressDialog.setCancelable(false);
            progressDialog.show();


            String[] paths = intent.getStringArrayExtra(AConstant.CrosheAlbumActivity.RESULT_IMAGES_PATH.name());
            String[] videoPaths = intent.getStringArrayExtra(AConstant.CrosheAlbumActivity.RESULT_VIDEO_PATH.name());

            if (paths.length > 0) {
                uploadImage(0, paths,progressDialog);
            }
            if (videoPaths.length > 0) {
                uploadVideo(0, videoPaths, progressDialog);
            }

            onlyCode = "";
        }
    }

    private void uploadImage(final int index, final String[] paths, final ProgressDialog progressDialog) {
        if (index >= paths.length) {
            progressDialog.dismiss();
            return;
        }
        final String path = paths[index];
        if (StringUtils.isNotEmpty(path) && StringUtils.isNotEmpty(regStr)) {
            if (!Pattern.matches(regStr, path)) {
                progressDialog.dismiss();
                return;
            }
        }
        final Map<String, Object> param = new HashMap<>();
        param.put("file", new File(path));

        OKHttpUtils.getInstance().post(BaseRequest.mainUrl + "uploadFile", param, new SimpleHttpCallBack<FileEntity>() {
            @Override
            public void onCallBackEntity(boolean success, String message, FileEntity data) {
                super.onCallBackEntity(success, message, data);
                if (currResponse == null) {
                    return;
                }
                if (success) {
                    currResponse.callBack(true, data);
                    uploadImage(index + 1, paths, progressDialog);
                }
            }
        });
    }

    private void uploadVideo(final int index, final String[] paths, final ProgressDialog progressDialog) {
        if (index >= paths.length) {
            progressDialog.dismiss();
            return;
        }
        final String path = paths[index];
        if (StringUtils.isNotEmpty(path) && StringUtils.isNotEmpty(regStr)) {
            if (!Pattern.matches(regStr, path)) {
                progressDialog.dismiss();
                return;
            }
        }
        Map<String, Object> param = new HashMap<>();
        Bitmap videoFirstImage = ImageUtils.getVideoFirstImage(path);

        String thumbPath = context.getFilesDir().getAbsolutePath() + "/Croshe/Video/Thumb/" + System.currentTimeMillis() + ".jpg";
        FileUtils.saveBitmapToPath(videoFirstImage, thumbPath, true);
        param.put("file.thumb", new File(thumbPath));
        param.put("file", new File(path));

        OKHttpUtils.getInstance().post(BaseRequest.mainUrl + "uploadFile", param, new SimpleHttpCallBack<FileEntity>() {
            @Override
            public void onCallBackEntity(boolean success, String message, FileEntity data) {
                super.onCallBackEntity(success, message, data);
                if (currResponse == null) {
                    return;
                }
                if (success) {
                    currResponse.callBack(true, data);
                    uploadVideo(index + 1, paths, progressDialog);
                }
            }
        });
    }


    /**
     * web回调
     *
     * @author Janesen
     */
    public interface WebResponse {
        void callBack(boolean success, Object object);

        CrosheBaseJavaScript getBaseJavaScript();
    }

}
