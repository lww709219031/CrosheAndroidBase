package com.croshe.android.base.server;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.croshe.android.base.BuildConfig;
import com.croshe.android.base.entity.AppVersionEntity;
import com.croshe.android.base.entity.SimpleHttpCallBack;
import com.croshe.android.base.utils.FileUtils;
import com.croshe.android.base.utils.MD5Encrypt;
import com.croshe.android.base.utils.OKHttpUtils;
import com.croshe.android.base.views.control.CrosheAppDownloadView;

import java.io.File;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 默认的公共请求
 * 安徽创息软件科技有限公司-技术支持，http://wwww.croshe.com
 * Created by Janesen on 2017/7/19 12:05.
 */
public class BaseRequest {

    public static String mainUrl = "";
    public static final String checkVersionUrl = "http://in.croshe.com/app/checkVersion?id=";

    /**
     * 检测版本
     *
     * @param handler
     */
    public static void checkVersion(final Handler handler, int appId) {
        OKHttpUtils.getInstance().get(checkVersionUrl + appId, new SimpleHttpCallBack() {
            @Override
            public void onResult(boolean success, String response) {
                if (response != null) {
                    try {
                        AppVersionEntity appVersionEntity = JSON.parseObject(response.toString(), AppVersionEntity.class);
                        Message msg = new Message();
                        msg.obj = appVersionEntity;
                        if (success) {
                            msg.arg1 = 0;
                        } else {
                            msg.arg1 = -1;
                        }
                        if (handler != null) {
                            handler.sendMessage(msg);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    /**
     * 计算版本
     *
     * @param appVersionEntity
     */
    public static boolean computeVersion(final Context context,
                                         final AppVersionEntity appVersionEntity) {
        return computeVersion(context, appVersionEntity, false);
    }

    /**
     * 计算版本
     *
     * @param appVersionEntity
     */
    public static boolean computeVersion(final Context context,
                                         final AppVersionEntity appVersionEntity,
                                         boolean isJustShow) {
        return computeVersion(context, appVersionEntity, isJustShow, false);
    }

    /**
     * 计算版本
     *
     * @param appVersionEntity
     */
    public static boolean computeVersion(final Context context,
                                         final AppVersionEntity appVersionEntity,
                                         boolean isJustShow, boolean isJustBrowser) {
        try {
            CrosheAppDownloadView crosheAppDownloadView = new CrosheAppDownloadView(context);
            crosheAppDownloadView.setVersion(appVersionEntity.getVersionCode());
            crosheAppDownloadView.setJumpBrowser(isJustBrowser);
            double currVersionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
            if (currVersionCode < appVersionEntity.getVersionCode()) {
                crosheAppDownloadView.show(
                        appVersionEntity.getDownUrl(),
                        "最新版本V" + appVersionEntity.getVersionName(),
                        appVersionEntity.getVersionDesc(),
                        appVersionEntity.getVersionCode(),
                        appVersionEntity.getVersionImportant() == 1,
                        isJustShow);
                return true;
            } else {
                crosheAppDownloadView.deleteCurrVersionApk();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 下载app
     *
     * @param context
     * @param url
     * @param title
     * @param description
     */
    public static void downloadApp(Context context, String url, final String title, String description) {
        Uri content_url = Uri.parse(url);
        try {
            if (Build.VERSION.SDK_INT > 9) {
                DownloadManager downloadManager = (DownloadManager) context.getSystemService(Activity.DOWNLOAD_SERVICE);
                DownloadManager.Request request = new DownloadManager.Request(content_url);
                request.setTitle(title);
                request.setDescription(description);
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);

                String fileName = MD5Encrypt.MD5(url + System.currentTimeMillis()) + ".apk";

                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
                request.setVisibleInDownloadsUi(true);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setMimeType("application/vnd.Android.package-archive");
                request.allowScanningByMediaScanner();


                final long myDownloadReference = downloadManager.enqueue(request);
                Toast.makeText(context, description, Toast.LENGTH_LONG).show();


                IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
                final String finalFileName = fileName;
                BroadcastReceiver receiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                        if (myDownloadReference == reference) {
                            FileUtils.executeAPK(BuildConfig.APPLICATION_ID, context, new File(Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DOWNLOADS + "/" + finalFileName));
                        }
                    }
                };
                context.registerReceiver(receiver, filter);
                return;
            }
        } catch (Exception e) {
        }

        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.setData(content_url);
        context.startActivity(intent);
    }


    /**
     * 网站授权
     */
    public static String authorization(String url) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("timestamp", System.currentTimeMillis());
            params.put("url", url);
            OKHttpUtils.getInstance().requestSign(params, mainUrl + "authorization");

            params.put("url", URLEncoder.encode(url, "utf-8"));
            String returnUrl = mainUrl + "authorization?" + OKHttpUtils.getInstance().convertToGet(params);
            return returnUrl;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

}
