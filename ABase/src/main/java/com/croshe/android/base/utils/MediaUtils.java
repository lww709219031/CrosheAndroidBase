package com.croshe.android.base.utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

/**
 * Created by Janesen on 2017/5/2.
 */

public class MediaUtils {
    private static MediaPlayer mediaPlayer;

    private static Runnable onStop;
    private static String currPlayUrl;

    public static boolean justInCall;


    public static void playRaw(Context context, int resource) {
        playRaw(context, true, resource, null);
    }

    public static void playRaw(Context context, boolean isMaxVolume, int resource) {
        playRaw(context, isMaxVolume, resource, null);
    }

    public static void playRaw(Context context, int resource, final Runnable onStop) {
        playRaw(context, true, resource, onStop);
    }

    public static void playRaw(final Context context, boolean isMaxVolume, int resource, final Runnable onStop) {
        stopMedia(context);
        MediaUtils.onStop = onStop;

        mediaPlayer = MediaPlayer.create(context, resource);
        if (mediaPlayer != null) {
            mediaPlayer.start();

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopMedia(context);
                }
            });

        }
    }


    /**
     * 播放assets下的音频
     *
     * @param context
     * @param mp3Name
     */
    public static void playAssets(Context context, String mp3Name) {
        playAssets(context, mp3Name, true, null);

    }

    /**
     * 播放assets下的音频
     *
     * @param context
     * @param mp3Name
     * @param isMaxVolume
     */
    public static void playAssets(Context context, String mp3Name, boolean isMaxVolume) {
        playAssets(context, mp3Name, isMaxVolume, null);
    }

    /**
     * 播放assets下的音频
     *
     * @param context
     * @param mp3Name
     * @param isMaxVolume
     * @param onStop
     */
    public static void playAssets(final Context context, String mp3Name, boolean isMaxVolume, final Runnable onStop) {
        try {
            stopMedia(context);
            MediaUtils.onStop = onStop;
            AssetManager assetManager = context.getAssets();

            AssetFileDescriptor fileDescriptor = assetManager.openFd(mp3Name);

            mediaPlayer = new MediaPlayer();
            if (mediaPlayer != null) {
                mediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(),
                        fileDescriptor.getLength());
                mediaPlayer.prepare();
                mediaPlayer.start();

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        stopMedia(context);
                    }
                });

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 播放网络声音文件
     *
     * @param context
     * @param url
     */
    public static void playUrl(Context context, String url) {
        playUrl(context, url, true, null);
    }

    /**
     * 播放网络声音文件
     *
     * @param context
     * @param url
     */
    public static void playUrl(Context context, String url, Runnable onComplete) {
        playUrl(context, url, false, onComplete);
    }


    /**
     * 播放网络声音文件
     *
     * @param context
     * @param url
     * @param isMaxVolume
     */
    public static void playUrl(Context context, String url, boolean isMaxVolume) {
        playUrl(context, url, isMaxVolume, null);
    }

    /**
     * 播放网络声音文件
     *
     * @param context
     * @param url
     * @param isMaxVolume
     * @param onStop
     */
    public static void playUrl(final Context context, final String url, boolean isMaxVolume, final Runnable onStop) {
        playUrl(context, url, isMaxVolume, false, onStop);

    }

    /**
     * 播放网络声音文件
     *
     * @param context
     * @param url
     * @param isMaxVolume
     * @param onStop
     */
    public static void playUrl(final Context context, final String url, boolean isMaxVolume, boolean isInCallPlay, final Runnable onStop) {
        playUrl(context, url, isMaxVolume, isInCallPlay, onStop, null);
    }

        /**
         * 播放网络声音文件
         *
         * @param context
         * @param url
         * @param isMaxVolume
         * @param onStop
         */
    public static void playUrl(final Context context, final String url, boolean isMaxVolume, boolean isInCallPlay, final Runnable onStop,final Runnable onDone) {
        try {
            stopMedia(context);
            MediaUtils.onStop = onStop;

            if (StringUtils.isEmpty(url)) {
                stopMedia(context);
                return;
            }

            AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            if (mAudioManager != null) {
                if (isInCallPlay || justInCall) {
                    mAudioManager.setSpeakerphoneOn(false);
                    mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
                } else {
                    mAudioManager.setSpeakerphoneOn(true);
                    mAudioManager.setMode(AudioManager.MODE_NORMAL);
                }
            }

            mediaPlayer = new MediaPlayer();
            mediaPlayer.reset();
            if (isInCallPlay) {
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
            }else{
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            }
            if (url.startsWith("http://") || url.startsWith("https://")) {
                OKHttpUtils.getInstance().downFile(context, url, new OKHttpUtils.HttpDownFileCallBack() {
                    @Override
                    public boolean onDownLoad(long countLength, long downLength, String localPath) {
                        if (countLength == downLength) {
                            try {
                                currPlayUrl = localPath;
                                mediaPlayer.setDataSource(localPath);
                                mediaPlayer.prepare();
                                mediaPlayer.start();
                            } catch (Exception e) {
                            }
                        }
                        return true;
                    }

                    @Override
                    public void onDownFail(String message) {

                    }
                });
            } else {
                try {
                    currPlayUrl = url;
                    mediaPlayer.setDataSource(url);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (Exception e) {}
            }

            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    Log.d("STAG", "MediaPlayer-OnError-what:" + what);
                    return true;
                }
            });

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopMedia(context);
                    if (onDone != null) {
                        onDone.run();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 停止播放
     */
    public static void stopMedia(final Context context) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        } catch (Exception e) {
        } finally {
            if (onStop != null) {
                onStop.run();
                onStop = null;
            }
            currPlayUrl = null;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (context != null) {
                        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                        if (mAudioManager != null) {
                            mAudioManager.setSpeakerphoneOn(true);
                            mAudioManager.setMode(AudioManager.MODE_NORMAL);
                        }
                    }
                }
            }).start();
        }
    }

    /**
     * 重新播放
     *
     * @param context
     */
    public static void replayUrl(final Context context, boolean isInCall) {
        if (StringUtils.isNotEmpty(currPlayUrl)) {
            try {
                mediaPlayer.stop();
                mediaPlayer.reset();
                final AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                if (mAudioManager != null) {
                    if (isInCall) {
                        mAudioManager.setSpeakerphoneOn(false);
                        mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
                    } else {
                        mAudioManager.setSpeakerphoneOn(true);
                        mAudioManager.setMode(AudioManager.MODE_NORMAL);
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    }
                }
                mediaPlayer.setDataSource(currPlayUrl);
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    public static MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }


}
