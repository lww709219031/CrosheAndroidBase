package com.croshe.android.base.entity;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;

import com.croshe.android.base.activity.CrosheDownListActivity;
import com.croshe.android.base.utils.BaseAppUtils;
import com.croshe.android.base.utils.OKHttpUtils;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;

/**
 * 安徽创息软件科技有限公司-技术支持，http://wwww.croshe.com
 * Created by Janesen on 2017/7/28 20:43.
 */
public class DownEntity extends BaseEntity {

    private long fileId = System.currentTimeMillis();
    private String url;
    private double progress;
    private boolean isStop = true;
    private String errorMsg;
    private int notifyDuration;
    private String fileName;

    private transient DownEntity linkDownEntity;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public double getProgress(Context context) {
        return OKHttpUtils.getInstance().checkDownFile(context, url, getLocalPath());
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    public boolean isStop() {
        return isStop;
    }

    public void setStop(boolean stop) {
        isStop = stop;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getFileName() {
        if (StringUtils.isNotEmpty(fileName)) {
            return fileName;
        }
        return url.substring(url.lastIndexOf("/") + 1);
    }

    public String getLocalPath() {
        String localPath = Environment.getExternalStorageDirectory().getPath()
                + "/Croshe/DownLoad/" + getFileName();
        return localPath;
    }

    public boolean isDownDone(Context context) {
        return OKHttpUtils.getInstance().checkDownFile(context, url, getLocalPath()) >= 1;
    }


    /**
     * 开始下载
     */
    public void startDown(final Context context) {
        isStop = false;
        EventBus.getDefault().post(DownEntity.this);

        Intent intent = new Intent(context, CrosheDownListActivity.class);
        int p = (int) (OKHttpUtils.getInstance().checkDownFile(context, url, getLocalPath()) * 100);
        BaseAppUtils.notify(context, (int) getFileId(), "文件下载",
                "正在下载文件：" + getFileName() + "，点击查看下载列表！" + p + "%",
                BaseAppUtils.getFileIcon(context, getLocalPath()), true, intent);


        OKHttpUtils.getInstance().downFile(context, url, getLocalPath(), new OKHttpUtils.HttpDownFileCallBack() {
            @Override
            public boolean onDownLoad(long countLength, long downLength, String localPath) {
                progress = Double.valueOf(downLength) / countLength;
                errorMsg = "";
                notifyDuration++;
                if (notifyDuration >= 500) {
                    EventBus.getDefault().post(DownEntity.this);
                    notifyDuration = 0;

                    int p = (int) (progress * 100);
                    Intent intent = new Intent(context, CrosheDownListActivity.class);
                    BaseAppUtils.notify(context, (int) getFileId(), "文件下载",
                            "正在下载文件：" + getFileName() + "，点击查看下载列表！" + p + "%",
                            BaseAppUtils.getFileIcon(context, localPath), true, intent);
                }

                if (isStop) {
                    Intent intent = new Intent(context, CrosheDownListActivity.class);
                    BaseAppUtils.notify(context, (int) getFileId(), "文件下载",
                            "已暂停下载文件：" + getFileName() + "，点击查看下载列表！",
                            BaseAppUtils.getFileIcon(context, localPath), true, intent);
                }
                if (countLength == downLength) {
                    isStop = true;
                    EventBus.getDefault().post(DownEntity.this);
                    Intent intent = new Intent(context, CrosheDownListActivity.class);
                    BaseAppUtils.notify(context, (int) getFileId(), "文件下载", getFileName() + "已完成下载！",
                            BaseAppUtils.getFileIcon(context, localPath), true, intent);
                }
                return !isStop;
            }

            @Override
            public void onDownFail(String message) {
                errorMsg = message;
                isStop = true;
            }
        });
    }


    public void stopDown() {
        isStop = true;
        if (linkDownEntity != null) {
            linkDownEntity.setStop(true);
        }
    }

    public DownEntity getLinkDownEntity() {
        return linkDownEntity;
    }

    public void setLinkDownEntity(DownEntity linkDownEntity) {
        this.linkDownEntity = linkDownEntity;
    }

    public void update(DownEntity downEntity) {
        this.progress = downEntity.progress;
        this.url = downEntity.url;
        this.isStop = downEntity.isStop;
    }

    public static String getLocalPath(String url) {
        String localPath = Environment.getExternalStorageDirectory().getPath()
                + "/Croshe/DownLoad/" + getFileName(url);
        return localPath;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileId() {
        return fileId;
    }

    public void setFileId(long fileId) {
        this.fileId = fileId;
    }

    public static String getFileName(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }


}
