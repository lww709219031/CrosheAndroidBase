package com.croshe.android.base.views.control;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.VideoView;

import org.apache.commons.lang3.StringUtils;

/**
 *  安徽创息软件科技有限公司-技术支持  http://www.croshe.com
 * Created by Janesen on 2017/5/12.
 */

public class CrosheVideoView extends VideoView {


    private static CrosheVideoView currPlayVideoView;

    /**
     * 开始播放
     */
    public static final int PLAY_STATE_START = 1;

    /**
     * 暂停播放
     */
    public static final int PLAY_STATE_PAUSE = 2;


    /**
     * 停止播放
     */
    public static final int PLAY_STATE_STOP = 3;


    /**
     * 缓冲中
     */
    public static final int PLAY_STATE_BUFFERING = 4;


    /**
     * 完成
     */
    public static final int PLAY_STATE_COMPLETE = 5;

    private OnPlayStateListener onPlayStateListener;

    private boolean isInitPath = false;
    private boolean isBufferDone = false;
    private boolean isLocalPath = false;

    public CrosheVideoView(Context context) {
        super(context);
        initView();
    }

    public CrosheVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public CrosheVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public void initView() {
        this.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mp, int what, int extra) {

                        if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                            if (onPlayStateListener != null) {
                                onPlayStateListener.onPlayState(CrosheVideoView.this, PLAY_STATE_BUFFERING);
                            }
                            return true;
                        } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END
                                ||what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                            isBufferDone = true;
                            if (onPlayStateListener != null) {
                                onPlayStateListener.onPlayState(CrosheVideoView.this, PLAY_STATE_START);
                            }
                            return true;
                        }
                        return false;
                    }
                });
            }
        });
        this.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (onPlayStateListener != null) {
                    onPlayStateListener.onPlayState(CrosheVideoView.this, PLAY_STATE_COMPLETE);
                }
            }
        });

    }


    @Override
    public void setVideoPath(String path) {
        super.setVideoPath(path);
        isInitPath = true;
        isLocalPath = true;
    }

    @Override
    public void setVideoURI(Uri uri) {
        super.setVideoURI(uri);
        isInitPath = true;
        if (StringUtils.isNotEmpty(uri.getScheme())) {
            if (uri.getScheme().toLowerCase().startsWith("http") || uri.getScheme().toLowerCase().startsWith("https")) {
                isLocalPath = false;
                return;
            }
        }
        isLocalPath = true;
    }

    @Override
    public void start() {
        super.start();
        if (currPlayVideoView != null && currPlayVideoView != this) {
            currPlayVideoView.stopPlayback();
        }

        if (isLocalPath && isBufferDone) {
            if (onPlayStateListener != null) {
                onPlayStateListener.onPlayState(CrosheVideoView.this, PLAY_STATE_START);
            }
        }

        currPlayVideoView = this;
    }

    @Override
    public void pause() {
        super.pause();
        if (onPlayStateListener != null) {
            onPlayStateListener.onPlayState(this, PLAY_STATE_PAUSE);
        }
    }

    @Override
    public void stopPlayback() {
        super.stopPlayback();
        if (onPlayStateListener != null) {
            onPlayStateListener.onPlayState(this, PLAY_STATE_STOP);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        try {
            this.stopPlayback();
            currPlayVideoView = null;
        } catch (Exception e) {}
    }

    public OnPlayStateListener getOnPlayStateListener() {
        return onPlayStateListener;
    }

    public void setOnPlayStateListener(OnPlayStateListener onPlayStateListener) {
        this.onPlayStateListener = onPlayStateListener;
    }

    public interface  OnPlayStateListener{

        void onPlayState(CrosheVideoView selfVideoView, int state);
    }

    public boolean isInitPath() {
        return isInitPath;
    }

    public void setInitPath(boolean initPath) {
        isInitPath = initPath;
    }

}
