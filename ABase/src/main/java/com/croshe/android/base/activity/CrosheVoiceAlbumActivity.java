package com.croshe.android.base.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Media;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.croshe.android.base.AConstant;
import com.croshe.android.base.R;
import com.croshe.android.base.extend.listener.CrosheAnimationListener;
import com.croshe.android.base.utils.DensityUtils;
import com.croshe.android.base.utils.MD5Encrypt;
import com.croshe.android.base.utils.MediaPlayerHelper;
import com.croshe.android.base.utils.ViewUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 选择本地语音文件
 * Created by Janesen on 16/7/9.
 */
public class CrosheVoiceAlbumActivity extends CrosheBaseSlidingActivity
        implements MediaPlayerHelper.OnPlayerListener {


    /**
     * 语音选择的上限 int
     */
    public static final String EXTRA_MAX_SELECT = AConstant.CrosheVoiceAlbumActivity.EXTRA_MAX_SELECT.name();


    /**
     * 音频的时长
     */
    public static final String RESULT_VOICE_DURATION = AConstant.CrosheVoiceAlbumActivity.RESULT_VOICE_DURATION.name();


    /**
     * 音频的路径
     */
    public static final String RESULT_VOICE_PATH = AConstant.CrosheVoiceAlbumActivity.RESULT_VOICE_PATH.name();


    /**
     * 音频的描述
     */
    public static final String RESULT_VOICE_DETAILS = AConstant.CrosheVoiceAlbumActivity.RESULT_VOICE_DETAILS.name();


    /**
     * 音频的时长 int[]
     */
    public static final String RESULT_VOICE_DURATIONS = AConstant.CrosheVoiceAlbumActivity.RESULT_VOICE_DURATIONS.name();


    /**
     * 音频的路径
     */
    public static final String RESULT_VOICE_PATHS = AConstant.CrosheVoiceAlbumActivity.RESULT_VOICE_PATHS.name();



    private static ContentResolver contentResolver;
    //Uri，指向external的database
    private Uri contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    //projection：选择的列; where：过滤条件; sortOrder：排序。
    private String[] projection = {
            Media._ID,
            Media.DISPLAY_NAME,
            Media.DATA,
            Media.ALBUM,
            Media.ARTIST,
            Media.DURATION,
            Media.SIZE,
            Media.DATE_MODIFIED
    };

    protected LinearLayout llAlbums;
    protected ScrollView scrollViewAlbum;

    protected RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    protected TextView tvCurrAlbumName, tvConfirm;

    protected AlbumAdapter adapter;
    protected AlbumEntity currAlbum = new AlbumEntity();

    private int maxSelect;

    private List<MusicInfo> checkedList = new ArrayList<>();
    private MusicInfo currMusicInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.android_base_activity_select_music);
        contentResolver = getContentResolver();
        initView();
        loadMusic();
    }

    public void initView() {
        if (getIntent().getExtras() != null) {
            maxSelect = getIntent().getExtras().getInt(EXTRA_MAX_SELECT, 10);
        }

        tvCurrAlbumName = (TextView) findViewById(R.id.tvCurrAlbumName);
        tvConfirm = (TextView) findViewById(R.id.tvConfirm);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        llAlbums = (LinearLayout) findViewById(R.id.llAlbums);
        scrollViewAlbum = (ScrollView) findViewById(R.id.scrollViewAlbum);
        linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemViewCacheSize(50);

        adapter = new AlbumAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DataItemDecoration());


        findViewById(R.id.sllAlbumBtn).setOnClickListener(this);
        findViewById(R.id.viewback).setOnClickListener(this);
        tvConfirm.setOnClickListener(this);
    }

    @Override
    public void onClick(final View v) {
        super.onClick(v);
        int i = v.getId();
        if (i == R.id.sllAlbumBtn || i == R.id.viewback) {
            showOrHideAlbumPanel();

        } else if (i == R.id.llAlbumItem) {
            showOrHideAlbumPanel();
            currAlbum = (AlbumEntity) v.getTag();
            tvCurrAlbumName.setText(currAlbum.getAlbumName());
            adapter.notifyDataSetChanged();

        } else if (i == R.id.tvConfirm) {

            if (checkedList.size() > 0) {
                Intent intent = new Intent();
                intent.putExtra(EXTRA_DO_ACTION, CrosheVoiceAlbumActivity.class.getSimpleName());
                intent.putExtras(getIntent());
                intent.putExtra(RESULT_VOICE_PATH, checkedList.get(0).getUrl());
                intent.putExtra(RESULT_VOICE_DURATION, checkedList.get(0).getDuration());
                intent.putExtra(RESULT_VOICE_DETAILS, checkedList.get(0).getTitle());


                ArrayList<Integer> durations = new ArrayList<>();
                List<String> paths = new ArrayList<>();
                for (MusicInfo musicInfo : checkedList) {
                    durations.add(musicInfo.getDuration());
                    paths.add(musicInfo.getUrl());
                }

                intent.putExtra(RESULT_VOICE_PATHS, paths.toArray(new String[]{}));
                intent.putIntegerArrayListExtra(RESULT_VOICE_DURATIONS, durations);


                setResult(RESULT_OK, intent);
                EventBus.getDefault().post(intent);
            }
            finish();

        }
    }


    /**
     * 显示或隐藏相册列表
     */
    public void showOrHideAlbumPanel() {
        TranslateAnimation animation = null;
        AlphaAnimation alphaAnimation = null;
        if (scrollViewAlbum.getVisibility() == View.VISIBLE) {
            alphaAnimation = new AlphaAnimation(1, 0);
            animation = new TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 0,
                    Animation.RELATIVE_TO_SELF, 0,
                    Animation.RELATIVE_TO_SELF, 0,
                    Animation.RELATIVE_TO_SELF, -1f);
            animation.setAnimationListener(new CrosheAnimationListener() {
                @Override
                public void onAnimationEnd(Animation arg0) {
                    super.onAnimationEnd(arg0);
                    scrollViewAlbum.setVisibility(View.GONE);
                    findViewById(R.id.frameLayoutAlbum).setVisibility(View.GONE);
                }
            });

        } else {
            findViewById(R.id.frameLayoutAlbum).setVisibility(View.VISIBLE);
            alphaAnimation = new AlphaAnimation(0, 1);
            scrollViewAlbum.setVisibility(View.VISIBLE);
            animation = new TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 0,
                    Animation.RELATIVE_TO_SELF, 0,
                    Animation.RELATIVE_TO_SELF, -1f,
                    Animation.RELATIVE_TO_SELF, 0);
        }
        alphaAnimation.setDuration(300);
        alphaAnimation.setFillAfter(true);
        animation.setDuration(300);
        animation.setFillAfter(true);
        findViewById(R.id.viewback).startAnimation(alphaAnimation);
        scrollViewAlbum.startAnimation(animation);
    }


    public void loadMusic() {
        Map<String, AlbumEntity> maps = new LinkedHashMap<>();
        AlbumEntity top100 = new AlbumEntity();
        top100.setAlbumName("最近100首");
        top100.setAlbumId(MD5Encrypt.MD5(top100.getAlbumName()));
        top100.setMusics(new ArrayList<MusicInfo>());
        maps.put(top100.getAlbumName(), top100);

        Cursor cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, null, null, "date_modified desc");
        if (cursor == null) {
            return;
        } else if (!cursor.moveToFirst()) {
        } else {
            int displayNameCol = cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME);
            int albumCol = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int idCol = cursor.getColumnIndex(Media._ID);
            int durationCol = cursor.getColumnIndex(Media.DURATION);
            int sizeCol = cursor.getColumnIndex(Media.SIZE);
            int artistCol = cursor.getColumnIndex(Media.ARTIST);
            int urlCol = cursor.getColumnIndex(Media.DATA);


            do {

                String title = cursor.getString(displayNameCol);
                String album = cursor.getString(albumCol);
                long id = cursor.getLong(idCol);
                int duration = cursor.getInt(durationCol);
                long size = cursor.getLong(sizeCol);
                String artist = cursor.getString(artistCol);
                String url = cursor.getString(urlCol);
                File file = new File(url);

                String dir = file.getParentFile().getName();

                if (!maps.containsKey(dir)) {
                    AlbumEntity albumEntity = new AlbumEntity();
                    albumEntity.setAlbumName(dir);
                    albumEntity.setAlbumId(MD5Encrypt.MD5(dir));
                    albumEntity.setMusics(new ArrayList<MusicInfo>());
                    maps.put(dir, albumEntity);
                }

                if (duration == 0) {//如果获取的时长为0 ，使用mediaplayer继续获取
                    MediaPlayer player = new MediaPlayer();
                    try {
                        player.setDataSource(url);
                        player.prepare();
                        duration = player.getDuration();
                    } catch (Exception e) {
                    } finally {
                        player.release();
                    }
                }

                MusicInfo musicInfo = new MusicInfo(id, title);
                musicInfo.setAlbum(album);
                musicInfo.setDuration(duration);
                musicInfo.setSize(size);
                musicInfo.setArtist(artist);
                musicInfo.setUrl(url);
                maps.get(dir).getMusics().add(musicInfo);

                if (top100.getMusics().size() < 100) {
                    top100.getMusics().add(musicInfo);
                }

            } while (cursor.moveToNext());
        }

        cursor.close();


        currAlbum = top100;
        adapter.notifyDataSetChanged();
        tvCurrAlbumName.setText(currAlbum.getAlbumName());

        for (String key : maps.keySet()) {
            AlbumEntity album = maps.get(key);

            View view = LayoutInflater.from(context).inflate(R.layout.android_base_view_item_album, null);
            TextView tvAlbumName = (TextView) view.findViewById(R.id.tvAlbumName);
            tvAlbumName.setText(album.getAlbumName());

            TextView tvAlbumCount = (TextView) view.findViewById(R.id.tvAlbumCount);
            tvAlbumCount.setText(album.getMusics().size() + "首");

            final ImageView imgAlbumIcon = (ImageView) view.findViewById(R.id.imgAlbum);
            imgAlbumIcon.setImageResource(R.drawable.android_base_icon_voice_group);

            view.findViewById(R.id.llAlbumItem).setOnClickListener(this);
            view.findViewById(R.id.llAlbumItem).setTag(album);
            llAlbums.addView(view);
        }
    }


    public class AlbumEntity implements Serializable {
        private String albumName;
        private String albumId;
        private List<MusicInfo> musics = new ArrayList<>();

        public String getAlbumName() {
            return albumName;
        }

        public void setAlbumName(String albumName) {
            this.albumName = albumName;
        }

        public String getAlbumId() {
            return albumId;
        }

        public void setAlbumId(String albumId) {
            this.albumId = albumId;
        }

        public List<MusicInfo> getMusics() {
            return musics;
        }

        public void setMusics(List<MusicInfo> musics) {
            this.musics = musics;
        }
    }

    public class MusicInfo implements Serializable {

        private long id;
        private String title;
        private String album;
        private int duration;
        private long size;
        private String artist;
        private String url;

        private int state;

        private int playingDuration;
        private double bufferingProgress;

        public MusicInfo() {

        }

        public MusicInfo(long pId, String pTitle) {
            id = pId;
            title = pTitle;
        }

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAlbum() {
            return album;
        }

        public void setAlbum(String album) {
            this.album = album;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public long getSize() {
            return size;
        }

        public void setSize(long size) {
            this.size = size;
        }

        public String getArtist() {
            return artist;
        }

        public void setArtist(String artist) {
            this.artist = artist;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getPlayingDuration() {
            return playingDuration;
        }

        public void setPlayingDuration(int playingDuration) {
            this.playingDuration = playingDuration;
        }

        public double getBufferingProgress() {
            return bufferingProgress;
        }

        public void setBufferingProgress(double bufferingProgress) {
            this.bufferingProgress = bufferingProgress;
        }
    }


    public class AlbumAdapter extends RecyclerView.Adapter<AlbumHolder> {

        @Override
        public AlbumHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.android_base_item_album_music, parent, false);
            return new AlbumHolder(view);
        }

        @Override
        public void onBindViewHolder(AlbumHolder holder, int position) {
            holder.setMusicInfo(currAlbum.getMusics().get(position));
        }

        @Override
        public int getItemCount() {
            return currAlbum.getMusics().size();
        }
    }


    public class AlbumHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {

        private MusicInfo musicInfo;
        private LinearLayout llAlbumItem;
        public ImageView imageView;
        private TextView tvAlbumCount, tvAlbumName, tvSize;
        private CheckBox cbCheck;

        private SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");

        public AlbumHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imgAlbum);
            tvAlbumCount = (TextView) itemView.findViewById(R.id.tvAlbumCount);
            tvAlbumName = (TextView) itemView.findViewById(R.id.tvAlbumName);
            tvSize = (TextView) itemView.findViewById(R.id.tvSize);
            llAlbumItem = (LinearLayout) itemView.findViewById(R.id.llAlbumItem);
            cbCheck = (CheckBox) itemView.findViewById(R.id.cbCheck);
            imageView.setImageResource(R.drawable.android_base_icon_music);


            itemView.findViewById(R.id.llAlbumItem).setOnClickListener(this);
            itemView.findViewById(R.id.llAlbumItem).setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (musicInfo.getState() == 0) {
                        MediaPlayerHelper.getInstance().playRecord(
                                musicInfo.getUrl(),
                                musicInfo.getDuration(),
                                null,
                                CrosheVoiceAlbumActivity.this);
                        currMusicInfo = musicInfo;
                    } else {
                        MediaPlayerHelper.getInstance().stopPlay();
                    }
                    return true;
                }
            });


            cbCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        if (checkedList.size() >= maxSelect) {
                            toast("已超出最大选择范围！");
                            cbCheck.setChecked(false);
                            return;
                        }
                        if (!checkedList.contains(musicInfo)) {
                            checkedList.add(musicInfo);
                        }
                    } else {
                        checkedList.remove(musicInfo);
                    }
                    tvConfirm.setText("确定(" + checkedList.size() + ")");
                    if (checkedList.size() == 0) {
                        tvConfirm.setText("确定");
                    }
                }
            });
        }

        public void initView() {
            if (musicInfo != null) {
                tvAlbumCount.setText(dateFormat.format(new Date(musicInfo.getDuration())));
                tvAlbumName.setText(musicInfo.getTitle());

                tvSize.setText(DensityUtils.getSizeByMB(musicInfo.getSize()));

                if (musicInfo.getState() == 1) {
                    llAlbumItem.setBackgroundColor(Color.parseColor("#eaecd9"));
                    tvAlbumCount.setText(dateFormat.format(new Date(musicInfo.getPlayingDuration())));
                } else if (musicInfo.getState() == -1) {
                    llAlbumItem.setBackgroundColor(Color.parseColor("#eaecd9"));
                    tvAlbumCount.setText(musicInfo.getBufferingProgress() + "%");
                } else {
                    ViewUtils.setViewSelectableItemBackground(llAlbumItem);
                }

                cbCheck.setChecked(checkedList.contains(musicInfo));
            }
        }

        public void setMusicInfo(MusicInfo musicInfo) {
            this.musicInfo = musicInfo;
            initView();
        }

        @Override
        public void onClick(View v) {
            int i = v.getId();
            if (i == R.id.llAlbumItem) {
                if (musicInfo.getState() != 0) {
                    MediaPlayerHelper.getInstance().stopPlay();
                    initView();
                    return;
                }
                cbCheck.setChecked(!cbCheck.isChecked());
            }
        }
    }

    @Override
    public void onBuffering(double progress) {
        if (currMusicInfo != null) {
            currMusicInfo.setState(-1);
            currMusicInfo.setBufferingProgress(progress);
            adapter.notifyItemChanged(currAlbum.getMusics().indexOf(currMusicInfo));
        }
    }


    @Override
    public void onPlaying(long duration) {
        if (currMusicInfo != null) {
            currMusicInfo.setState(1);
            currMusicInfo.setPlayingDuration((int) duration);
            adapter.notifyItemChanged(currAlbum.getMusics().indexOf(currMusicInfo));
        }
    }

    @Override
    public void onStopPlay() {
        if (currMusicInfo != null) {
            currMusicInfo.setState(0);
            adapter.notifyItemChanged(currAlbum.getMusics().indexOf(currMusicInfo));
        }
    }

    @Override
    public void onStartPlay() {
        if (currMusicInfo != null) {
            currMusicInfo.setState(1);
            adapter.notifyItemChanged(currAlbum.getMusics().indexOf(currMusicInfo));
        }
    }

    @Override
    public void finish() {
        super.finish();
        try {
            MediaPlayerHelper.getInstance().stopPlay();
        } catch (Exception e) {}
    }

    public class DataItemDecoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left = 0;
            outRect.right = 0;
            outRect.top = 0;
            outRect.bottom = 0;
            if (parent.getChildAdapterPosition(view) != 0) {
                outRect.top = DensityUtils.dip2px(0.5f);
            }
        }

    }


}
