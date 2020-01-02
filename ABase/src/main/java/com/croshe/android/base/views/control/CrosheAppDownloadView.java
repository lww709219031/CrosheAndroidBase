package com.croshe.android.base.views.control;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.croshe.android.base.AIntent;
import com.croshe.android.base.ARecord;
import com.croshe.android.base.R;
import com.croshe.android.base.extend.dialog.CrosheDialog;
import com.croshe.android.base.utils.BaseAppUtils;
import com.croshe.android.base.utils.DensityUtils;
import com.croshe.android.base.utils.DialogUtils;
import com.croshe.android.base.utils.ExitApplication;
import com.croshe.android.base.utils.FileUtils;
import com.croshe.android.base.utils.MD5Encrypt;
import com.croshe.android.base.utils.OKHttpUtils;
import com.croshe.android.base.utils.SelfStrUtils;

import java.io.File;

/**
 * 检测更新的控件
 * 安徽创息软件科技有限公司-技术支持，http://wwww.croshe.com
 * Created by Janesen on 2017/7/26 11:27.
 */
public class CrosheAppDownloadView extends FrameLayout implements View.OnClickListener {


    private String appUrl;
    private double version;
    private View progressView;
    private TextView tvProgress, tvTitle, tvContent, tvProgress2;
    private boolean isJumpBrowser;

    private boolean isImportance;
    private boolean isStopDown;

    private FrameLayout flDownload;
    private LinearLayout llAction;

    private CrosheDialog dialog;


    public CrosheAppDownloadView(@NonNull Context context) {
        super(context);
        initView();
    }

    public CrosheAppDownloadView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public CrosheAppDownloadView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.android_base_panel_app_update, this);
        progressView = findViewById(R.id.android_base_progress);
        tvProgress = (TextView) findViewById(R.id.android_base_tvProgress);
        tvProgress2 = (TextView) findViewById(R.id.android_base_tvProgress2);

        tvTitle = (TextView) findViewById(R.id.android_base_tvTitle);
        tvContent = (TextView) findViewById(R.id.android_base_tvContent);

        flDownload = (FrameLayout) findViewById(R.id.android_base_flDownload);
        llAction = (LinearLayout) findViewById(R.id.android_base_llAction);

        findViewById(R.id.android_base_cancel).setOnClickListener(this);
        findViewById(R.id.android_base_ok).setOnClickListener(this);
        findViewById(R.id.android_base_down).setOnClickListener(this);
        findViewById(R.id.android_base_browser).setOnClickListener(this);


        findViewById(R.id.android_base_down).setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                isStopDown = true;
                flDownload.setVisibility(GONE);
                llAction.setVisibility(VISIBLE);

                dialog.setCanceledOnTouchOutside(!isImportance);
                dialog.setCancelable(!isImportance);

                return true;
            }
        });

    }

    private String downFilePath() {
        String fileName = SelfStrUtils.convertToPinyin(BaseAppUtils.getApplicationName(getContext()))
                .toUpperCase() + plainVersion() + ".apk";

        return Environment.getExternalStorageDirectory().getPath()
                + "/Croshe/APK/"
                + fileName;
    }


    private void download() {
        if (dialog != null) {
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
        }
        isStopDown = false;

        flDownload.setVisibility(VISIBLE);
        llAction.setVisibility(GONE);

        ViewGroup.LayoutParams layout = tvProgress2.getLayoutParams();
        layout.width = flDownload.getWidth();
        tvProgress2.setLayoutParams(layout);


        ARecord.get(getVCode()).setAttr("Downloading", true);

        doProgress(OKHttpUtils.getInstance().checkDownFile(getContext(), appUrl, downFilePath()));

        OKHttpUtils.getInstance().downFile(getContext(), appUrl, downFilePath(), new OKHttpUtils.HttpDownFileCallBack() {
            @Override
            public boolean onDownLoad(final long countLength, final long downLength, final String localPath) {
                try {
                    if (getHandler() != null) {
                        getHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                double progress = Double.valueOf(downLength) / countLength;
                                doProgress(progress);
                            }
                        });
                    }
                    if (countLength == downLength) {//下载完成
                        FileUtils.executeAPK(getContext().getPackageName(), getContext(), new File(localPath));
                        close();
                    }
                } catch (Throwable e) {
                    ARecord.get(getVCode()).setAttr("Downloading", false);
                    e.printStackTrace();
                }
                return !isStopDown;
            }

            @Override
            public void onDownFail(String message) {
                AIntent.doAlert(message);
                close();
            }
        });
    }


    private void doProgress(double progress) {
        int progressWidth = (int) (flDownload.getWidth() * progress);

        ViewGroup.LayoutParams layout = progressView.getLayoutParams();
        layout.width = progressWidth;
        progressView.setLayoutParams(layout);

        int p = (int) (progress * 100);
        tvProgress.setText("正在下载中：" + p + "%");

        if (isStopDown) {
            tvProgress.setText("已暂停下载，点击继续下载，长按返回！");
        }
        tvProgress2.setText(tvProgress.getText());

    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.android_base_cancel) {
            if (isImportance) {
                DialogUtils.confirm(getContext(), "系统提醒", "此次的更新为必要更新，否则将退出软件，您确定继续退出软件吗？", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        close();
                    }
                });
            } else {
                ARecord.get(getVCode()).setAttr("Waiting", true, 60 * 1000 * 30);//30分钟后可再次提醒
                close();
            }
        } else if (v.getId() == R.id.android_base_ok) {
            if (isJumpBrowser) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(appUrl);
                intent.setData(content_url);
                getContext().startActivity(intent);
            } else {
                download();
            }
        } else if (v.getId() == R.id.android_base_down) {
            if (isStopDown) {
                download();
            } else {
                isStopDown = true;
                tvProgress.setText("已暂停下载，点击继续下载，长按返回！");
            }
        } else if (v.getId() == R.id.android_base_browser) {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            Uri content_url = Uri.parse(appUrl);
            intent.setData(content_url);
            getContext().startActivity(intent);
        }
    }


    /**
     * 弹出更新界面
     *
     * @param url
     * @param title
     * @param description
     * @param isImportance 是否是重要更新
     */
    public void show(String url, String title, String description, double version, boolean isImportance) {
        show(url, title, description, version, isImportance, false);
    }

    /**
     * 弹出更新界面
     *
     * @param url
     * @param title
     * @param description
     * @param isImportance 是否是重要更新
     */
    public void show(String url, String title, String description, double version, boolean isImportance, boolean isJustShow) {
        this.appUrl = url;
        this.version = version;
        this.isImportance = isImportance;

        if (ARecord.get(getVCode()).getBoolean("Downloading", false)) {
            return;
        }

        if (!isJustShow) {
            if (ARecord.get(getVCode()).getBoolean("Waiting", false)) {
                return;
            }
        }


        if (isImportance) {
            findViewById(R.id.android_base_importance).setVisibility(VISIBLE);
        } else {
            findViewById(R.id.android_base_importance).setVisibility(GONE);
        }

        if (isJumpBrowser) {
            findViewById(R.id.android_base_browser).setVisibility(GONE);
        } else {
            findViewById(R.id.android_base_browser).setVisibility(VISIBLE);
        }


        tvTitle.setText(title);
        tvContent.setText(Html.fromHtml(description));
        dialog = new CrosheDialog(getContext(), R.style.AndroidBaseDialogStyleA);

        Window win = dialog.getWindow();
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

        win.setAttributes(layoutParams);


        dialog.setCanceledOnTouchOutside(!isImportance);
        dialog.setCancelable(!isImportance);
        dialog.setContentView(this, new LinearLayout.LayoutParams((int) DensityUtils.getWidthInPx(), ViewGroup.LayoutParams.WRAP_CONTENT));
        dialog.setOnCloseListener(new CrosheDialog.OnCloseListener() {
            @Override
            public void onClose(Dialog dialog) {
                ARecord.get(getVCode()).setAttr("Downloading", false);
            }
        });
        dialog.show();

    }

    public void close() {
        if (getHandler() != null) {
            getHandler().post(new Runnable() {
                @Override
                public void run() {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    if (isImportance) {
                        ExitApplication.exitApp();
                    }
                }
            });
        }
    }

    private String plainVersion() {
        return String.valueOf(version).replace(".", "") + getVCode();
    }

    public double getVersion() {
        return version;
    }

    public void setVersion(double version) {
        this.version = version;
    }


    public String getVCode() {
        return MD5Encrypt.MD5(version + appUrl);
    }

    /**
     * 删除当前版本的APK
     */
    public void deleteCurrVersionApk() {
        File file = new File(downFilePath());
        if (file.exists()) {
            file.delete();
        }
        ARecord.get(getVCode()).clear();
    }

    public boolean isJumpBrowser() {
        return isJumpBrowser;
    }

    public void setJumpBrowser(boolean jumpBrowser) {
        isJumpBrowser = jumpBrowser;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ARecord.get(getVCode()).setAttr("Downloading", false);
    }
}
