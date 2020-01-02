package com.croshe.android.base.listener;

import com.bumptech.glide.load.engine.GlideException;

/**
 * 安徽创息软件科技有限公司-技术支持，http://wwww.croshe.com
 * Created by Janesen on 2017/10/16 12:23.
 */
public interface OnCrosheImageProgressListener {
    void onProgress(String imageUrl, long bytesRead, long totalBytes, boolean isDone, GlideException exception);
}
