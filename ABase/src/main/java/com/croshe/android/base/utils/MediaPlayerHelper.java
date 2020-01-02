package com.croshe.android.base.utils;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class MediaPlayerHelper {

    public enum MediaPlayerStateEnum {
        buffering,
        playing,
        stop
    }


    private static MediaPlayerHelper playerUtils;

    public static MediaPlayerHelper getInstance() {
        if (playerUtils == null) {
            playerUtils = new MediaPlayerHelper();
        }
        return playerUtils;
    }

    public static MediaPlayerHelper getInstance(boolean isNew) {
        if (isNew) {
            return new MediaPlayerHelper();
        }
        if (playerUtils == null) {
            playerUtils = new MediaPlayerHelper();
        }
        return playerUtils;
    }


    private MediaPlayerHelper() {
        handler = new Handler(Looper.getMainLooper());
    }

    private SimpleDateFormat dateFormat = new SimpleDateFormat("mm’ss’’");
    private Handler handler;
    private long duration;
    private String recordPath = "";
    private MediaPlayer mediaPlayer;
    private Timer timer;
    private TextView durationTextView;
    private MediaPlayerStateEnum playerState;
    private OnPlayerListener onPlayerListener;


    private boolean isAsc = true;//是否 正序
    private boolean isCache = true;//是否缓存录音

    public void playRecord(String recordPath, final long duration, final TextView durationTextView) {
        playRecord(recordPath, duration, durationTextView, true, null);
    }

    public void playRecord(String recordPath, final long duration, final TextView durationTextView, final OnPlayerListener onPlayerListener) {
        playRecord(recordPath, duration, durationTextView, true, onPlayerListener);

    }


    public void playRecord(String recordPath, final long duration, final TextView durationTextView, boolean isAsc) {
        playRecord(recordPath, duration, durationTextView, isAsc, null);
    }

    private void playRecord(String recordPath, final long duration, final TextView durationTextView, boolean isAsc, final OnPlayerListener onPlayerListener) {
        notifyStop();

        this.isAsc = isAsc;
        this.onPlayerListener = onPlayerListener;
        this.recordPath = recordPath;
        this.duration = duration;
        if (durationTextView != null) {
            this.durationTextView = durationTextView;
        }
        if (playerState != MediaPlayerStateEnum.buffering) {
            if (recordPath.startsWith("http") || recordPath.startsWith("https")) {
                playerState = MediaPlayerStateEnum.buffering;
                String voicePath = durationTextView.getContext().getFilesDir().getAbsolutePath() + "/Croshe/Voice/" + MD5Encrypt.MD5(recordPath);
                File doneFile = new File(voicePath);
                if (doneFile.exists()) {
                    startPlayRecord(voicePath);
                    return;
                }
                if (durationTextView != null) {
                    durationTextView.setText("加载中…");
                }
                if (onPlayerListener != null) {
                    onPlayerListener.onBuffering(0);
                }
                OKHttpUtils.getInstance().downFile(durationTextView.getContext(), recordPath,
                        voicePath,
                        new OKHttpUtils.HttpDownFileCallBack() {
                            @Override
                            public boolean onDownLoad(final long countLength,
                                                      final long downLength,
                                                      final String localPath) {

                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (playerState != MediaPlayerStateEnum.stop) {
                                            if (countLength == downLength) {
                                                startPlayRecord(localPath);
                                                return;
                                            }
                                            double progress = (int) ((Double.valueOf(downLength) / countLength) * 100);
                                            if (durationTextView != null) {
                                                durationTextView.setText(progress + "%");
                                            }
                                            if (onPlayerListener != null) {
                                                onPlayerListener.onBuffering(progress);
                                            }
                                        }
                                    }
                                });
                                return playerState != MediaPlayerStateEnum.stop;
                            }

                            @Override
                            public void onDownFail(String message) {
                            }
                        });
            } else {
                startPlayRecord(recordPath);
            }
        }
    }

    /**
     * 播放录音
     *
     * @param recordPath
     */
    private void startPlayRecord(String recordPath) {
        doStop();
        playerState = MediaPlayerStateEnum.buffering;
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(recordPath);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer mp) {
                    // TODO Auto-generated method stub
                    doPlay();
                }
            });
            mediaPlayer.setOnInfoListener(new OnInfoListener() {

                @Override
                public boolean onInfo(MediaPlayer arg0, final int whatInfo, int arg2) {
                    // TODO Auto-generated method stub
                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            switch (whatInfo) {
                                case MediaPlayer.MEDIA_INFO_BUFFERING_START://卡顿
                                    if (MediaPlayerHelper.this.durationTextView != null) {
                                        MediaPlayerHelper.this.durationTextView.setText("缓冲…");
                                    }
                                    break;
                            }
                        }
                    });
                    return false;
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    doStop();
                    notifyStop();
                }
            });
        } catch (Exception e) {
        }
    }


    private void doPlay() {
        if (onPlayerListener != null) {
            onPlayerListener.onStartPlay();
        }

        mediaPlayer.start();
        playerState = MediaPlayerStateEnum.playing;
        TimerTask task = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (mediaPlayer != null && playerState != MediaPlayerStateEnum.stop) {
                                if (duration == 0) {
                                    duration = mediaPlayer.getDuration();
                                }
                                if (mediaPlayer.getCurrentPosition() <= duration * 1000) {
                                    if (onPlayerListener != null) {
                                        onPlayerListener.onPlaying(mediaPlayer.getCurrentPosition());
                                    }
                                    if (durationTextView != null) {
                                        if (isAsc) {
                                            String time = dateFormat.format(new Date(mediaPlayer.getCurrentPosition()));
                                            durationTextView.setText(time);
                                        } else {
                                            String time = dateFormat.format(new Date(duration - mediaPlayer.getCurrentPosition()));
                                            durationTextView.setText(time);
                                        }
                                    }
                                    return;
                                }
                            }
                            doStop();
                            notifyStop();
                        } catch (Exception e) {
                        }
                    }
                });
            }
        };
        timer = new Timer(true);
        timer.schedule(task, 0, 10);
    }

    /**
     * 停止播放
     */
    private void doStop() {
        if (timer != null) {
            timer.cancel();
        }

        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.release();
            } catch (Exception e) {
                mediaPlayer = null;
                System.gc();
            }
        }
        playerState = MediaPlayerStateEnum.stop;

        if (this.durationTextView != null) {
            String time = dateFormat.format(new Date(this.duration));
            durationTextView.setText(time);
        }

        if (!isCache) {
            File file = new File(recordPath);
            if (file.exists()) {
                file.delete();
            }
        }
    }


    public void stopPlay() {
        doStop();
        notifyStop();
    }


    private void notifyStop() {
        if (onPlayerListener != null) {
            onPlayerListener.onStopPlay();
        }
    }


    public long getDuration() {
        return duration;
    }


    public void setDuration(long duration) {
        this.duration = duration;
    }


    public Timer getTimer() {
        return timer;
    }


    public void setTimer(Timer timer) {
        this.timer = timer;
    }


    public TextView getDurationTextView() {
        return durationTextView;
    }


    public void setDurationTextView(TextView durationTextView) {
        this.durationTextView = durationTextView;
    }


    public String getRecordPath() {
        return recordPath;
    }


    public void setRecordPath(String recordPath) {
        this.recordPath = recordPath;
    }


    public MediaPlayerStateEnum getPlayerState() {
        return playerState;
    }


    public void setPlayerState(MediaPlayerStateEnum playerState) {
        this.playerState = playerState;
    }

    public SimpleDateFormat getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(SimpleDateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }

    public boolean isCache() {
        return isCache;
    }

    public void setCache(boolean cache) {
        isCache = cache;
    }


    public static boolean isPlaying() {
        if (playerUtils != null) {
            return playerUtils.playerState != MediaPlayerStateEnum.stop;
        }
        return false;
    }


    public interface OnPlayerListener {
        void onBuffering(double progress);
        void onPlaying(long duration);

        void onStopPlay();

        void onStartPlay();
    }

}
