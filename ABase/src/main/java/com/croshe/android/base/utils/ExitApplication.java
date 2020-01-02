package com.croshe.android.base.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.croshe.android.base.activity.CrosheBaseActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import me.leolin.shortcutbadger.ShortcutBadger;

public class ExitApplication {

    static Handler handler = new Handler(Looper.getMainLooper());
    static List<Runnable> releaseRunnable = new ArrayList<>();
    static List<Activity> activityList = new LinkedList<>();

    static boolean isExit;


    public static void addReleaseRunnable(Runnable runnable) {
        releaseRunnable.add(runnable);
    }

    public static void addActivity(Activity activity) {
        if (!activityList.contains(activity)) {
            activityList.add(activity);
        }
        isExit = false;
    }


    public static Context getContext() {
        if (activityList.size() > 0) {
            return activityList.get(activityList.size() - 1);
        }
        return null;
    }


    public static Activity getTopActivity() {
        if (activityList.size() == 0) {
            return null;
        }
        return activityList.get(activityList.size() - 1);
    }

    /**
     * 错误方法，没有闪退
     *
     * @param activity
     */
    public static void finishUnActivity(Activity activity) {
        if (isExit) return;
        int startIndex = activityList.indexOf(activity);
        if (startIndex >= 0) {
            List<Activity> waitRemove = new ArrayList<>();
            for (int i = startIndex; i < activityList.size(); i++) {
                waitRemove.add(activityList.get(i));
            }
            activityList.removeAll(waitRemove);

            for (Activity activity1 : waitRemove) {
                if (activity1 != activity) {
                    activity1.finish();
                }
            }
        }
    }


    public static void finishUpActivity(Activity activity) {
        if (isExit) return;
        int startIndex = activityList.indexOf(activity);
        if (startIndex >= 0) {
            List<Activity> waitRemove = new ArrayList<>();
            for (int i = startIndex + 1; i < activityList.size(); i++) {
                waitRemove.add(activityList.get(i));
            }
            activityList.removeAll(waitRemove);

            for (Activity activity1 : waitRemove) {
                if (activity1 != activity) {
                    activity1.finish();
                }
            }
        }
    }


    /**
     * 退出app
     */
    public static void exitApp() {
        isExit = true;
        try {
            List<Activity> news = new ArrayList<>(Arrays.asList(new Activity[activityList.size()]));
            Collections.copy(news, activityList);
            if (news.size() > 0) {
                ShortcutBadger.applyCount(news.get(0), 0);
            }
            for (Activity activity : news) {
                if (activity instanceof CrosheBaseActivity) {
                    CrosheBaseActivity crosheBaseActivity = (CrosheBaseActivity) activity;
                    crosheBaseActivity.finishSelf();
                } else {
                    activity.finish();
                }
            }
        } catch (Exception e) {
        } finally {
        }
        release();
    }


    /**
     * 释放资源
     */
    public static void release() {
        for (Runnable runnable : releaseRunnable) {
            if (runnable != null) {
                handler.post(runnable);
            }
        }
        System.gc();
    }


    public static boolean isExited() {
        return isExit;
    }


    /**
     * 错误方法
     *
     * @param activityClass
     */
    public static void finishActivity(Class<? extends Activity> activityClass) {
        isExit = true;
        List<Activity> waitRemove = new ArrayList<>();
        for (Activity activity : activityList) {
            if (activity.getClass() == activityClass) {
                activity.finish();
                waitRemove.add(activity);
            }
        }
        activityList.removeAll(waitRemove);
    }

    public static void removeActivity(Activity activity) {
        activityList.remove(activity);
    }

    /**
     * 错误方法
     *
     * @param activityClass
     */
    public static void finishUnActivity(Class<? extends Activity> activityClass) {
        isExit = true;
        List<Activity> waitRemove = new ArrayList<>();
        for (Activity activity : activityList) {
            if (activity.getClass() != activityClass) {
                activity.finish();
                waitRemove.add(activity);
            }
        }
        activityList.removeAll(waitRemove);
    }

    public static Activity getBeforeActivity(Activity activity) {
        for (int i = 0; i < activityList.size(); i++) {
            if (i != 0) {
                if (activityList.get(i) == activity) {
                    return activityList.get(i - 1);
                }
            }
        }
        return null;
    }

}
