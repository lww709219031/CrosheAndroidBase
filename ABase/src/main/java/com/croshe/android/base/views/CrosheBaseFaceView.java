package com.croshe.android.base.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.croshe.android.base.extend.glide.GlideFilePrefix;
import com.croshe.android.base.listener.OnCrosheFaceListener;

/**
 * 安徽创息软件科技有限公司-技术支持，http://wwww.croshe.com
 * Created by Janesen on 2017/12/19 13:43.
 */
public abstract class CrosheBaseFaceView extends FrameLayout {
    protected OnCrosheFaceListener onCrosheFaceListener;

    /**
     * 获得相对assets文件的路径，例如：face/face_01.png
     *
     * @param faceUrl
     * @return
     */
    public static String getAssetsFacePath(String faceUrl) {
        return faceUrl.replace(GlideFilePrefix.AssetsPrefix + "face/", "");
    }


    /**
     * 获得表情的占位符
     *
     * @param faceUrl
     * @return
     */
    public static String getFacePlaceHolder(String faceUrl) {
        return "[@" + getAssetsFacePath(faceUrl) + "@]";
    }

    public CrosheBaseFaceView(@NonNull Context context) {
        super(context);
    }

    public CrosheBaseFaceView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CrosheBaseFaceView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public OnCrosheFaceListener getOnCrosheFaceListener() {
        return onCrosheFaceListener;
    }

    public void setOnCrosheFaceListener(OnCrosheFaceListener onCrosheFaceListener) {
        this.onCrosheFaceListener = onCrosheFaceListener;
    }
}
