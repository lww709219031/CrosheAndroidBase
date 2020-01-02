package com.croshe.android.base.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import com.croshe.android.base.AConfig;
import com.croshe.android.base.AConstant;
import com.croshe.android.base.AIntent;
import com.croshe.android.base.R;
import com.croshe.android.base.activity.CrosheBrowserActivity;
import com.croshe.android.base.entity.dao.AppCacheEntity;
import com.croshe.android.base.entity.dao.AppCacheHelper;
import com.jaeger.library.StatusBarUtil;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by Janesen on 2017/6/25.
 */

public class BaseAppUtils {


    /**
     * 显示通知的ID
     */
    public final static Set<Integer> notifyIds = new HashSet<>();
    public final static String CHANNEL_ID = "croshe_channel_id_1";


    /**
     * 缓存信息
     */
    public static SharedPreferences memoryDataInfo;


    public static void setCacheValue(String key, Object value) {
        SharedPreferences.Editor edit = memoryDataInfo.edit();
        if (value == null) {
            edit.remove(key);
        } else {
            edit.putString(key, value.toString());
        }
        edit.commit();
    }


    public static String getCacheValue(String key) {
        return memoryDataInfo.getString(key, null);
    }

    public static String getCacheValue(String key, String defaultValue) {
        return memoryDataInfo.getString(key, defaultValue);
    }


    /**
     * 在本地数据库进行缓存
     *
     * @param key
     * @param value
     */
    public static void setCache(String key, String value) {
        AppCacheHelper.setCache(key, key, String.valueOf(value));
    }

    /**
     * 获得本地数据库的缓存
     *
     * @param key
     * @return
     */
    public static String getCache(String key) {
        AppCacheEntity cacheEntity = AppCacheHelper.getCache(key);
        if (cacheEntity == null) {
            return null;

        }
        return cacheEntity.getCacheContent();
    }

    /**
     * 获得本地数据库的缓存
     *
     * @param key
     * @return
     */
    public static String getCache(String key, String defaultValue) {
        AppCacheEntity cacheEntity = AppCacheHelper.getCache(key);
        if (cacheEntity == null) {
            return defaultValue;

        }
        return cacheEntity.getCacheContent();
    }

    /**
     * 震动
     *
     * @param duration
     */
    public static void vibrator(Context context, long duration) {
        Vibrator vb = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
        vb.vibrate(duration);
    }


    /**
     * 获取主题颜色
     *
     * @return
     */
    public static int getColorPrimary(Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        return typedValue.data;
    }

    /**
     * 获取主题颜色
     *
     * @return
     */
    public static int getDarkColorPrimary(Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorPrimaryDark, typedValue, true);
        return typedValue.data;
    }

    /**
     * 获取主题颜色
     *
     * @return
     */
    public static int getColorAccent(Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorAccent, typedValue, true);
        return typedValue.data;
    }


    /**
     * 获得程序的名称
     *
     * @param context
     * @return
     */
    public static String getApplicationName(Context context) {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = null;
        try {
            packageManager = context.getApplicationContext().getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            applicationInfo = null;
        }
        String applicationName =
                (String) packageManager.getApplicationLabel(applicationInfo);
        return applicationName;
    }


    /**
     * 配置 meta-data 值
     *
     * @param context
     * @param key
     * @param value
     */
    public static void setApplicationMetaData(Context context, String key, String value) {
        try {
            ApplicationInfo appi = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            appi.metaData.putString(key, value);


        } catch (PackageManager.NameNotFoundException e1) {
            e1.printStackTrace();
        }
    }


    /**
     * 检测应用是否安装
     *
     * @param packageName
     * @return
     */
    public static boolean checkAppInstall(String packageName) {
        return new File("/data/data/" + packageName)
                .exists();
    }


    /**
     * 打开百度导航
     *
     * @param context
     * @param lat
     * @param lnt
     */
    public static void startBaiduMap(Context context, double lat, double lnt) {
        try {
            Intent intent = new Intent();
            intent.setData(Uri.parse("baidumap://map/navi?location=" + lat + "," + lnt));

            if (checkAppInstall("com.baidu.BaiduMap")) {
                context.startActivity(intent); // 启动调用
            } else {
                Toast.makeText(context, "请您先安装百度地图软件！", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 打开高德导航
     *
     * @param context
     * @param lat
     * @param lnt
     */
    public static void startGaodeMap(Context context, double lat, double lnt) {
        try {
            Intent intent = new Intent("android.intent.action.VIEW",
                    Uri.parse("androidamap://navi?sourceApplication=" + getApplicationName(context) + "&lat=" + lat
                            + "&lon=" + lnt + "&dev=0&style=2"));
            intent.setPackage("com.autonavi.minimap");
            if (checkAppInstall("com.autonavi.minimap")) {
                context.startActivity(intent); // 启动调用
            } else {
                Toast.makeText(context, "请您先安装高德地图软件！", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 处理二维码扫描结果
     *
     * @param context
     * @param result
     */
    public static void doScannerResult(final Context context, final String result) {
        if (StringUtils.isEmpty(result)) {
            return;
        }
        if (AConfig.getOnScannerListener() != null) {
            if (AConfig.getOnScannerListener().onScannerResult(result)) {
                return;
            }
        }
        if (result.startsWith("http://")
                || result.startsWith("https://")) {
            Intent intent = new Intent(context, CrosheBrowserActivity.class);
            intent.putExtra(CrosheBrowserActivity.EXTRA_URL, result);
            context.startActivity(intent);
        } else if (result.startsWith("croshe://")) {
            Intent data = new Intent();
            data.putExtra(AConstant.EXTRA_DO_ACTION, result);
            EventBus.getDefault().post(data);
        } else {
            DialogUtils.confirm(context, "二维码识别", result, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData myClip = ClipData.newPlainText("text", result);
                    cm.setPrimaryClip(myClip);
                    Toast.makeText(context, "已复制到剪贴板中！", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    /**
     * 透明百分比转换成16进制
     *
     * @param val
     * @return
     */
    public static String getAlpha(int val) {
        return getAlpha(val / 100.0);
    }

    /**
     * 透明百分比转换成16进制
     *
     * @param percent
     * @return
     */
    public static String getAlpha(double percent) {
        int alpha = (int) Math.round(percent * 255);
        String hex = Integer.toHexString(alpha).toUpperCase();
        if (hex.length() < 2) {
            hex = "0" + hex;
        }
        return hex;
    }

    /**
     * 透明百分比转换成16进制
     *
     * @param alpha 0~255
     * @return
     */
    public static String getAlphaHex(int alpha) {
        String hex = Integer.toHexString(alpha).toUpperCase();
        if (hex.length() < 2) {
            hex = "0" + hex;
        }
        return hex;
    }


    /*
     * 获取程序 图标
     */
    public static Drawable getAppIcon(Context context) {
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(context.getPackageName(), 0);
            return info.loadIcon(context.getPackageManager());
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }
        return null;
    }


    /**
     * 弹出通知
     *
     * @param context
     * @param title
     * @param content
     */
    public static void notify(Context context, int notifyId, String title, CharSequence content) {
        notify(context, notifyId, title, content, ImageUtils.drawableToBitmap(BaseAppUtils.getAppIcon(context)), false);
    }


    /**
     * 弹出通知
     *
     * @param context
     * @param title
     * @param content
     */
    public static void notify(Context context, int notifyId, String title, CharSequence content, Intent intent) {
        notify(context, notifyId, title, content, ImageUtils.drawableToBitmap(BaseAppUtils.getAppIcon(context)),
                false, intent, null, -1);
    }


    /**
     * 弹出通知
     *
     * @param context
     * @param title
     * @param content
     */
    public static void notify(Context context, int notifyId, String title,
                              CharSequence content,
                              Intent intent,
                              int notifyDefaults) {
        notify(context, notifyId, title, content,
                ImageUtils.drawableToBitmap(BaseAppUtils.getAppIcon(context)), false, intent,
                null, notifyDefaults);
    }

    /**
     * 弹出通知
     *
     * @param context
     * @param title
     * @param content
     */
    public static void notify(Context context, int notifyId, String title,
                              CharSequence content,
                              Intent intent,
                              Intent deleteIntent,
                              int notifyDefaults) {
        notify(context, notifyId, title, content,
                ImageUtils.drawableToBitmap(BaseAppUtils.getAppIcon(context)), false, intent,
                deleteIntent, notifyDefaults);
    }


    /**
     * 弹出通知
     *
     * @param context
     * @param title
     * @param content
     */
    public static void notify(Context context, String title, CharSequence content) {
        notify(context, title.hashCode(), title, content, ImageUtils.drawableToBitmap(BaseAppUtils.getAppIcon(context)), false);
    }


    /**
     * 弹出通知
     *
     * @param context
     * @param title
     * @param content
     */
    public static void notify(Context context, String title, CharSequence content, boolean silence) {
        notify(context, title.hashCode(), title, content, ImageUtils.drawableToBitmap(BaseAppUtils.getAppIcon(context)), silence);
    }


    /**
     * 弹出通知
     *
     * @param context
     * @param title
     * @param content
     */
    public static void notify(Context context, String title, CharSequence content, Bitmap logo) {
        notify(context, title.hashCode(), title, content, logo, false);
    }


    /**
     * 弹出通知
     *
     * @param context
     * @param title
     * @param content
     */
    public static void notify(Context context, String title, CharSequence content, Bitmap logo, boolean silence) {
        notify(context, title.hashCode(), title, content, logo, silence);
    }

    /**
     * 弹出通知
     *
     * @param context
     * @param title
     * @param content
     */
    public static void notify(Context context, String title, CharSequence content, Bitmap logo, boolean silence, Intent intent) {
        notify(context, title.hashCode(), title, content, logo, silence, intent, null, -1);
    }


    /**
     * 弹出通知
     *
     * @param context
     * @param title
     * @param content
     */
    public static void notify(Context context, int notifyId, String title, CharSequence content, Bitmap logo, boolean silence, Intent intent) {
        notify(context, notifyId, title, content, logo, silence, intent, null, -1);
    }


    /**
     * 弹出通知
     *
     * @param context
     * @param title
     * @param content
     */
    public static void notify(Context context, int notifyId, String title, CharSequence content, Bitmap logo, Intent intent) {
        notify(context, notifyId, title, content, logo, false, intent, null, -1);
    }

    /**
     * 弹出通知
     *
     * @param context
     * @param title
     * @param content
     */
    public static void notify(Context context, int notifyId, String title, CharSequence content, Bitmap logo, Intent intent, int notifyDefaults) {
        notify(context, notifyId, title, content, logo, false, intent, null, notifyDefaults);
    }


    /**
     * 弹出通知
     *
     * @param context
     * @param title
     * @param content
     */
    public static void notify(Context context, int notifyId, String title, CharSequence content, Bitmap logo,
                              Intent intent,
                              Intent cancelIntent, int notifyDefaults) {
        notify(context, notifyId, title, content, logo, false, intent, cancelIntent, notifyDefaults);
    }

    /**
     * 弹出通知
     *
     * @param context
     * @param title
     * @param content
     */
    public static void notify(Context context, int notifyId, String title,
                              CharSequence content,
                              boolean silence,
                              Intent intent,
                              Intent cancelIntent, int notifyDefaults) {
        notify(context, notifyId, title, content, ImageUtils.drawableToBitmap(BaseAppUtils.getAppIcon(context)), silence, intent, cancelIntent, notifyDefaults);
    }

    /**
     * 弹出通知
     *
     * @param context
     * @param title
     * @param content
     */
    public static void notify(Context context, int notifyId, String title,
                              CharSequence content,
                              boolean silence,
                              Intent intent,
                              Intent cancelIntent, int notifyDefaults, int sounResource) {
        notify(context, notifyId, title, content, ImageUtils.drawableToBitmap(BaseAppUtils.getAppIcon(context)), silence, intent,
                cancelIntent, notifyDefaults, sounResource);
    }


    /**
     * 弹出通知
     *
     * @param context
     * @param title
     * @param content
     */
    public static void notify(Context context, int notifyId, String title, CharSequence content, Bitmap logo) {
        notify(context, notifyId, title, content, logo, false, null, null, -1);
    }

    /**
     * 弹出通知
     *
     * @param context
     * @param title
     * @param content
     */
    public static void notify(Context context, int notifyId, String title, CharSequence content, Bitmap logo, boolean silence) {
        notify(context, notifyId, title, content, logo, silence, null, null, -1);
    }


    /**
     * 弹出通知
     *
     * @param context
     * @param title
     * @param content
     */
    public static void notify(Context context, int notifyId, String title, CharSequence content,
                              Bitmap logo, boolean silence,
                              Intent launchIntent,
                              Intent cancelIntent,
                              int notifyDefaults) {

        notify(context, notifyId, title, content, logo, silence, launchIntent, cancelIntent, notifyDefaults, -1);

    }

    /**
     * 弹出通知
     *
     * @param context
     * @param title
     * @param content
     */
    public static void notify(Context context, int notifyId, String title, CharSequence content,
                              Bitmap logo, boolean silence,
                              Intent launchIntent,
                              Intent cancelIntent,
                              int notifyDefaults,
                              int soundResource) {
        try {
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (mNotificationManager == null) return;

            if (launchIntent == null) {
                launchIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
            }

            Notification.Builder builder = new Notification.Builder(context);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                mNotificationManager.deleteNotificationChannel(CHANNEL_ID);
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                        "CrosheChannel", NotificationManager.IMPORTANCE_HIGH);
                channel.enableLights(true);
                channel.setLightColor(Color.RED);
                channel.enableLights(true);
                channel.enableVibration(true);
                channel.setShowBadge(true);
                mNotificationManager.createNotificationChannel(channel);
                builder.setChannelId(CHANNEL_ID);
            }

            builder.setContentTitle(title)
                    .setContentText(content)
                    .setSmallIcon(R.drawable.push)
                    .setLargeIcon(logo)
                    .setAutoCancel(false)
                    .setContentIntent(PendingIntent.getActivity(context, 0, launchIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT));

            if (cancelIntent != null) {
                builder.setDeleteIntent(PendingIntent.getBroadcast(context, 0, cancelIntent,
                        PendingIntent.FLAG_ONE_SHOT));
            }

            Notification notification = builder.getNotification();

            notification.icon = R.drawable.push;
            notification.when = System.currentTimeMillis();
            notification.flags = Notification.FLAG_AUTO_CANCEL;

            if (!silence) {
                if (soundResource != -1) {
                    if (notifyDefaults == Notification.DEFAULT_ALL) {
                        notification.sound = Uri.parse("android.resource://" + context.getPackageName()
                                + "/" + soundResource);
                        notification.defaults = Notification.DEFAULT_VIBRATE;
                    } else if (notifyDefaults == Notification.DEFAULT_SOUND) {
                        notification.sound = Uri.parse("android.resource://" + context.getPackageName()
                                + "/" + soundResource);
                    } else {
                        notification.defaults = notifyDefaults;
                    }

                } else {
                    notification.defaults = Notification.DEFAULT_ALL;
                    if (notifyDefaults != -1) {
                        notification.defaults = notifyDefaults;
                    }
                }
            }

            if (!notifyIds.contains(notifyId)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    notification.priority = Notification.PRIORITY_MAX;
                    notification.tickerText = content;
                }
            }

            mNotificationManager.notify(notifyId, notification);

            notifyIds.add(notifyId);
        } catch (Exception e) {
        }
    }


    /**
     * 取消通知
     *
     * @param context
     * @param notifyId
     */
    public static void cancelNotify(Context context, int notifyId) {
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(notifyId);
        notifyIds.remove(notifyId);
    }


    /**
     * 检测intent是否存在
     *
     * @param context
     * @param intent
     * @return
     */
    public static boolean checkIntent(Context context, Intent intent) {
        return checkIntent(context, intent, true);

    }


    /**
     * 检测intent是否存在
     *
     * @param context
     * @param intent
     * @return
     */
    public static boolean checkIntent(Context context, Intent intent, boolean alert) {
        if (intent.resolveActivity(context.getPackageManager()) == null) {
            if (alert) {
                DialogUtils.alert(context, "系统提醒",
                        "未检测到" + intent.getAction() +
                                "，请在AndroidManifest.xml中配置！");
            }
            return false;
        }
        return true;
    }


    /**
     * 启动action方式的intent
     *
     * @param context
     * @param intent
     */
    public static void startActionIntent(Context context, Intent intent) {
        startActionIntent(context, intent, false);
    }


    /**
     * 启动action方式的intent
     *
     * @param context
     * @param intent
     */
    public static void startActionIntent(Context context, Intent intent, boolean alert) {
        intent.addCategory(AIntent.ACTION_CROSHE_DEFAULT);
        if (!BaseAppUtils.checkIntent(context, intent, false)) {
            intent.removeCategory(AIntent.ACTION_CROSHE_DEFAULT);
        }
        if (alert) {
            if (!BaseAppUtils.checkIntent(context, intent, true)) {
                return;
            }
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    /**
     * 获得文件的图标
     *
     * @param context
     * @param path
     * @return
     */
    public static Bitmap getFileIcon(Context context, String path) {
        String lowerPath = path.toLowerCase();
        if (lowerPath.endsWith(".apk")) {
            return ImageUtils.getDrawableBitmap(context, R.drawable.android_base_file_apk);
        } else if (lowerPath.endsWith(".xls") || lowerPath.endsWith(".xlsx")) {
            return ImageUtils.getDrawableBitmap(context, R.drawable.android_base_file_excel);
        } else if (lowerPath.endsWith(".docx") || lowerPath.endsWith(".doc")) {
            return ImageUtils.getDrawableBitmap(context, R.drawable.android_base_file_word);
        } else if (lowerPath.endsWith(".pdf")) {
            return ImageUtils.getDrawableBitmap(context, R.drawable.android_base_file_pdf);
        }
        return ImageUtils.getDrawableBitmap(context, R.drawable.android_base_file_file);
    }


    /**
     * 获得文件的图标
     *
     * @param context
     * @param path
     * @return
     */
    public static int getFileIconResource(Context context, String path) {
        String lowerPath = path.toLowerCase();
        if (lowerPath.endsWith(".apk")) {
            return R.drawable.android_base_file_apk;
        } else if (lowerPath.endsWith(".xls") || lowerPath.endsWith(".xlsx")) {
            return R.drawable.android_base_file_excel;
        } else if (lowerPath.endsWith(".docx") || lowerPath.endsWith(".doc")) {
            return R.drawable.android_base_file_word;
        } else if (lowerPath.endsWith(".pdf")) {
            return R.drawable.android_base_file_pdf;
        }
        return R.drawable.android_base_file_file;
    }


    /**
     * 获得apk的icon图标
     *
     * @param context
     * @param apkPath
     * @return
     */
    public static Drawable getApkIcon(Context context, String apkPath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(apkPath,
                PackageManager.GET_ACTIVITIES);
        if (info != null) {
            ApplicationInfo appInfo = info.applicationInfo;
            appInfo.sourceDir = apkPath;
            appInfo.publicSourceDir = apkPath;
            return appInfo.loadIcon(pm);
        }
        return null;
    }


    /**
     * 全屏
     *
     * @param context
     */
    public static void fullScreen(Context context) {
        fullScreen(context, true, false);
    }


    /**
     * 全屏
     *
     * @param context
     * @param fullScreen
     */
    public static void fullScreen(Context context, boolean fullScreen) {
        fullScreen(context, fullScreen, false);
    }

    /**
     * 全屏
     *
     * @param context
     * @param fullScreen
     * @param isLight
     */
    public static void fullScreen(Context context, boolean fullScreen, boolean isLight) {
        if (fullScreen) {
            if (context instanceof Activity) {
                Activity activity = (Activity) context;
                View decorView = activity.getWindow().getDecorView();
                int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                if (isLight) {
                    option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                }
                decorView.setSystemUiVisibility(option);

                if (Build.VERSION.SDK_INT >= 21) {
                    activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
                } else {
                    StatusBarUtil.setTranslucent(activity);
                }
            }
        } else {
            if (context instanceof Activity) {
                Activity activity = (Activity) context;
                View decorView = activity.getWindow().getDecorView();
                int option = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                decorView.setSystemUiVisibility(option);


                if (Build.VERSION.SDK_INT >= 21) {
                    activity.getWindow().setStatusBarColor(getDarkColorPrimary(context));
                } else {
                    StatusBarUtil.setColor(activity, getDarkColorPrimary(context));
                }
            }

        }
    }


    /**
     * 获得当前应用的版本
     *
     * @param context
     * @return
     */
    public static double getVersionCode(Context context) {
        double currVersionCode = 0;
        try {
            currVersionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return currVersionCode;
    }

    /**
     * 获得当前应用的版本
     *
     * @param context
     * @return
     */
    public static String getVersionName(Context context) {
        String currVersionCode = "1.0";
        try {
            currVersionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return currVersionCode;
    }


    /**
     * 拷贝文本内容
     *
     * @param context
     * @param text
     */
    public static void copyText(Context context, String text) {
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setText(text);
        // 创建普通字符型ClipData
        ClipData mClipData = ClipData.newPlainText("text", text);
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData);
    }


    /**
     * 判断Activity 是否在AndroidManifest 注册了
     *
     * @param context
     * @param className
     * @return
     */
    public static boolean checkActivityConfig(Context context, String className) {
        return checkActivityConfig(context, className, true);
    }

    /**
     * 判断Activity 是否在AndroidManifest 注册了
     *
     * @param context
     * @param className
     * @return
     */
    public static boolean checkActivityConfig(Context context, String className, boolean isAlert) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(),
                    PackageManager.GET_ACTIVITIES);
            if (packageInfo.activities != null) {
                for (ActivityInfo ai : packageInfo.activities) {
                    if (ai.name.equals(className)) {
                        return true;
                    }
                }

            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (isAlert) {
            DialogUtils.alert(context, "系统提醒",
                    "未检测到" + className +
                            "，请在AndroidManifest.xml中配置！");
        }
        return false;
    }


    public static String getImageCacheDir(Context context) {
        File filePath = new File(context.getFilesDir(),
                "Croshe/Image");
        if (!filePath.exists()) {
            filePath.mkdirs();
        }
        return filePath.getAbsolutePath();
    }


    /**
     * 返回添加到桌面快捷方式的Intent：
     * 1.给Intent指定action="com.android.launcher.INSTALL_SHORTCUT"
     * 2.给定义为Intent.EXTRA_SHORTCUT_INENT的Intent设置与安装时一致的action(必须要有)
     * 3.添加权限:com.android.launcher.permission.INSTALL_SHORTCUT
     */

    public static Intent getShortcutToDesktopIntent(Context context, int ic_launcher) {
        Intent intent = new Intent();
        intent.setClass(context, context.getClass());
        /*以下两句是为了在卸载应用的时候同时删除桌面快捷方式*/
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");

        Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        // 不允许重建
        shortcut.putExtra("duplicate", false);
        // 设置名字
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, context.getString(R.string.app_name));
        // 设置图标
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(context, ic_launcher));
        // 设置意图和快捷方式关联程序
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);

        return shortcut;

    }


    /**
     * 获得签名
     *
     * @param context
     * @param packageName
     * @return
     */
    public static Signature getPackageSignature(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> apps = pm.getInstalledPackages(PackageManager.GET_SIGNATURES);
        Iterator<PackageInfo> it = apps.iterator();
        while (it.hasNext()) {
            PackageInfo info = it.next();
            if (info.packageName.equals(packageName)) {
                return info.signatures[0];
            }
        }
        return null;
    }


    /**
     * 判断当前是否是debug模式
     *
     * @param context
     * @return
     */
    public static boolean isApkDebuggable(Context context) {
        try {
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {

        }
        return false;
    }

    /**
     * 是否运行着模拟器中
     *
     * @param context
     * @return
     */
    @SuppressLint("MissingPermission")
    public static boolean isEmulator(Context context) {
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String imei = tm.getDeviceId();
            if (imei != null && imei.equals("000000000000000")) {
                return true;
            }
            return (Build.MODEL.equals("sdk")) || (Build.MODEL.equals("google_sdk"));
        } catch (Exception ioe) {
        }
        return false;
    }

}
