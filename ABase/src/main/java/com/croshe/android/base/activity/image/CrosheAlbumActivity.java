package com.croshe.android.base.activity.image;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Priority;
import com.croshe.android.base.AConfig;
import com.croshe.android.base.AConstant;
import com.croshe.android.base.R;
import com.croshe.android.base.activity.CrosheBaseSlidingActivity;
import com.croshe.android.base.extend.glide.GlideApp;
import com.croshe.android.base.extend.listener.CrosheAnimationListener;
import com.croshe.android.base.utils.DensityUtils;
import com.croshe.android.base.utils.MD5Encrypt;
import com.croshe.android.base.utils.NumberUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import io.reactivex.functions.Consumer;

/**
 *  相册
 *  安徽创息软件科技有限公司-技术支持  http://www.croshe.com
 * Created by Janesen on 16/5/28.
 */
@SuppressLint("CheckResult")
public class CrosheAlbumActivity extends CrosheBaseSlidingActivity implements View.OnClickListener {


    /**
     * 图片选择的上限 int
     */
    public static final String EXTRA_MAX_SELECT = AConstant.CrosheAlbumActivity.EXTRA_MAX_SELECT.name();

    /**
     * 是否包含视频
     */
    public static final String EXTRA_VIDEO = AConstant.CrosheAlbumActivity.EXTRA_VIDEO.name();


    /**
     * 只拍照
     */
    public static final String EXTRA_JUST_CAMERA = AConstant.CrosheAlbumActivity.EXTRA_JUST_CAMERA.name();



    /**
     * 返回的图片路径
     */
    public static final String RESULT_IMAGES_PATH =AConstant.CrosheAlbumActivity.RESULT_IMAGES_PATH.name();

    /**
     * 返回单个图片
     */
    public static final String RESULT_SINGLE_IMAGES_PATH = AConstant.CrosheAlbumActivity.RESULT_SINGLE_IMAGES_PATH.name();





    protected SimpleDateFormat videoDurationFormat = new SimpleDateFormat("mm:ss");

    protected RecyclerView recyclerView;
    protected GridLayoutManager gridLayoutManager;
    protected AlbumAdapter adapter;


    protected ScrollView scrollViewAlbum;
    protected LinearLayout llAlbums, llSelectedImages;
    protected TextView tvCurrAlbumName, tvConfirm;


    protected HorizontalScrollView hScrollView;
    protected String cameraSavePath;

    protected AlbumEntity currAlbum = new AlbumEntity();


    protected List<String> selectImagesPath = new ArrayList<>();
    protected int selectMaxCount;

    private Map<String, Long> videoDuration = new HashMap<>();

    protected boolean justCamera = false;
    private boolean isInitAlbum = false;

    private static final String[] STORE_IMAGES = {
            MediaStore.Images.Media.DISPLAY_NAME, // 显示的名
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.LONGITUDE, // 经度
            MediaStore.Images.Media._ID, // id
            MediaStore.Images.Media.BUCKET_ID, // dir id 目录
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME, // dir name 目录名字
            MediaStore.Images.Media.DATE_MODIFIED,//最后一次修改时间
            MediaStore.Images.Media.DATE_ADDED//最后一次修改时间
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.android_base_activity_album);
        fullScreen(true);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (getIntent().getExtras() != null) {
            selectMaxCount = getIntent().getExtras().getInt(EXTRA_MAX_SELECT, Integer.MAX_VALUE);
        } else {
            selectMaxCount = Integer.MAX_VALUE;
        }

        initView();
        checkData();
    }


    private void checkData() {
        rxPermissions.request("android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.CAMERA", "android.permission.READ_EXTERNAL_STORAGE")
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            initAlbums();
                        } else {
                            alert("请允许程序的权限！", new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                                    intent.setData(uri);
                                    startActivity(intent);
                                }
                            });
                        }
                    }
                });
    }

    public void initView() {
        tvCurrAlbumName = (TextView) findViewById(R.id.tvCurrAlbumName);
        tvConfirm = (TextView) findViewById(R.id.tvConfirm);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        scrollViewAlbum = (ScrollView) findViewById(R.id.scrollViewAlbum);
        llAlbums = (LinearLayout) findViewById(R.id.llAlbums);
        llSelectedImages = (LinearLayout) findViewById(R.id.llSelected);
        hScrollView = (HorizontalScrollView) findViewById(R.id.hScrollView);

        recyclerView.addItemDecoration(new AlbumItemDecoration());
        gridLayoutManager = new GridLayoutManager(context, 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter = new AlbumAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING || newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    GlideApp.with(getContext().getApplicationContext()).pauseRequests();
                } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    GlideApp.with(getContext().getApplicationContext()).resumeRequests();
                }
            }
        });
        recyclerView.setItemViewCacheSize(50);

        if (Build.VERSION.SDK_INT > 11) {
            llSelectedImages.setLayoutTransition(new LayoutTransition());
        }


        findViewById(R.id.sllAlbumBtn).setOnClickListener(this);
        findViewById(R.id.viewback).setOnClickListener(this);
        findViewById(R.id.llCamera).setOnClickListener(this);
        tvConfirm.setOnClickListener(this);

        if (getIntent().getExtras() != null) {
            justCamera = getIntent().getBooleanExtra(EXTRA_JUST_CAMERA, false);
            if (justCamera) {
                onClick(findViewById(R.id.llCamera));
            }
        }
    }

    public void refreshConfirmText() {
        if (llSelectedImages.getChildCount() > 0) {
            tvConfirm.setText("确定(" + llSelectedImages.getChildCount() + ")");
        } else {
            tvConfirm.setText("确定");
        }
    }


    @Override
    public void onClick(final View v) {
        int i = v.getId();
        if (i == R.id.sllAlbumBtn || i == R.id.viewback) {
            showOrHideAlbumPanel();

        } else if (i == R.id.llAlbumItem) {
            currAlbum = (AlbumEntity) v.getTag();
            showOrHideAlbumPanel();
            tvCurrAlbumName.setText(currAlbum.getAlbumName());
            adapter.notifyDataSetChanged();

        } else if (i == R.id.llCamera) {
            cameraSavePath = Environment.getExternalStorageDirectory().getPath() + "/Croshe/Camera/" + System.currentTimeMillis() + ".jpg";
            File file = new File(cameraSavePath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if (Build.VERSION.SDK_INT < 24) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
            } else {
                ContentValues contentValues = new ContentValues(1);
                contentValues.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
                Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            }
            intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
            intent.putExtra("outputFormat", "JPEG");
            startActivityForResult(intent, 3);

        } else if (i == R.id.sllRemove) {
            removeSelected(v);

        } else if (i == R.id.android_base_imgIcon) {
            Intent intent;
            try {
                Bundle bundle = new Bundle();
                JSONObject json = (JSONObject) v.getTag();
                bundle.putStringArray(CrosheShowImageActivity.EXTRA_IMAGES_PATH, selectImagesPath.toArray(new String[]{}));
                bundle.putString(CrosheShowImageActivity.EXTRA_FIRST_PATH, json.getString("imagePath"));
                intent = new Intent(context, CrosheShowImageActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (i == R.id.tvConfirm) {
            Intent intent;
            if (selectImagesPath.size() > 0) {

                List<String> imagePaths = new ArrayList<>();
                List<String> videoPaths = new ArrayList<>();

                for (String s : selectImagesPath) {
                    String path = s.replace("file://", "");
                    if (isVideo(path)) {
                        videoPaths.add(path);
                    }else{
                        imagePaths.add(path);
                    }
                }


                String[] images = imagePaths.toArray(new String[]{});

                String[] videos = videoPaths.toArray(new String[]{});


                intent = new Intent();
                intent.putExtra(EXTRA_DO_ACTION, this.getClass().getSimpleName());
                intent.putExtras(getIntent());

                intent.putExtra(RESULT_IMAGES_PATH, images);
                if (images.length > 0) {
                    intent.putExtra(RESULT_SINGLE_IMAGES_PATH, images[0]);
                }


                intent.putExtra(AConstant.CrosheAlbumActivity.RESULT_VIDEO_PATH.name(), videos);
                if (videos.length > 0) {
                    intent.putExtra(AConstant.CrosheAlbumActivity.RESULT_SINGLE_VIDEO_PATH.name(), videos[0]);
                }

                setResult(RESULT_OK, intent);

                EventBus.getDefault().post(intent);
            }
            finish();

        }
    }

    protected void removeSelected(View v) {
        try {
            JSONObject json = (JSONObject) v.getTag();
            int photoIndex = json.getInt("photoIndex");
            String imagePath = json.getString("imagePath");
            String albumId = json.getString("albumId");
            llSelectedImages.removeView((View) json.get("obj"));
            selectImagesPath.remove(imagePath);
            if (AConfig.AlbumAnim) {
                if (photoIndex >= 0 && currAlbum.getAlbumId().equals(albumId)) {
                    currAlbum.getImages().add(photoIndex, imagePath);
                    adapter.notifyItemInserted(photoIndex);
                }
            }else{
                adapter.notifyItemChanged(photoIndex);
            }
            refreshConfirmText();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获得视频资源
     */
    public AlbumEntity initVideo() {
        AlbumEntity videoAlbum = new AlbumEntity();
        videoAlbum.setAlbumName("小视频");
        videoAlbum.setAlbumId(MD5Encrypt.MD5(System.currentTimeMillis() + ""));
        Cursor cursor = MediaStore.Images.Media.query(getContentResolver(),
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI, STORE_IMAGES, null, " date_modified desc");

        if (cursor == null) {
            Toast.makeText(context, "相册打开失败，无法获取您手机的图册", Toast.LENGTH_LONG).show();
            return null;
        }

        while (cursor.moveToNext()) {
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
            File file = new File(path);
            if (file.length() > 8 * 1024*1024) continue;//大于10M的视频，跳过

            if (Pattern.matches(".+(.mp4)$", path.toLowerCase())) {
                videoAlbum.getImages().add(path);
            }
        }
        return videoAlbum;
    }

    /**
     * 获得相册列表
     */
    public void initAlbums() {
        if(isInitAlbum) return;
        isInitAlbum = true;
        Map<String, AlbumEntity> albums = new LinkedHashMap<String, AlbumEntity>();
        Cursor cursor = MediaStore.Images.Media.query(getContentResolver(),
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, STORE_IMAGES, null, " date_modified desc");

        if (cursor == null) {
            Toast.makeText(context, "相册打开失败，无法获取您手机的图册", Toast.LENGTH_LONG).show();
            return;
        }

        AlbumEntity albumTop100 = new AlbumEntity();
        albumTop100.setAlbumName("最近图片");
        albumTop100.setAlbumId(MD5Encrypt.MD5(System.currentTimeMillis() + ""));
        albums.put("top100", albumTop100);


        if (getIntent().getExtras()!=null&&getIntent().getExtras().containsKey(EXTRA_VIDEO)) {
            if (getIntent().getExtras().getBoolean(EXTRA_VIDEO, false)) {
                albums.put("video", initVideo());
            }
        }

        AlbumEntity albumGif = new AlbumEntity();
        albumGif.setAlbumName("GIF动图");
        albumGif.setAlbumId(MD5Encrypt.MD5(System.currentTimeMillis() + ""));
        albums.put("gifImage", albumGif);
        while (cursor.moveToNext()) {
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            String dir_id = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID));
            String dir = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
            File file = new File(path);
            if (file.length() < 30 * 1024) continue;//小于30kb的图片，一般是应用程序内功能的小图标，无需显示

            if (Pattern.matches(".+(.JPEG|.jpeg|.JPG|.jpg|.BMP|.bmp|.PNG|.png|.gif)$", path.toLowerCase())) {
                if (!albums.containsKey(dir_id)) {
                    AlbumEntity album = new AlbumEntity();
                    album.setAlbumName(dir);
                    album.setAlbumId(dir_id);
                    albums.put(dir_id, album);
                }
                AlbumEntity album = albums.get(dir_id);

                album.getImages().add("file://" + path);
                if (albumTop100.getImages().size() < 100) {
                    albumTop100.getImages().add("file://" + path);
                }

                if (path.toLowerCase().endsWith(".gif")) {
                    albumGif.getImages().add("file://" +path);
                }
            }
        }

        currAlbum = albumTop100;
        adapter.notifyDataSetChanged();
        tvCurrAlbumName.setText(currAlbum.getAlbumName());

        cursor.close();

        for (String key : albums.keySet()) {
            AlbumEntity album = albums.get(key);

            if (album.getImages().size() > 0) {
                View view = LayoutInflater.from(context).inflate(R.layout.android_base_view_item_album, null);
                TextView tvAlbumName = (TextView) view.findViewById(R.id.tvAlbumName);
                tvAlbumName.setText(album.getAlbumName());

                TextView tvAlbumCount = (TextView) view.findViewById(R.id.tvAlbumCount);
                tvAlbumCount.setText(album.getImages().size() + "张");

                final ImageView imgAlbumIcon = (ImageView) view.findViewById(R.id.imgAlbum);
                String imgPath = album.getImages().get(0);

                GlideApp.with(context.getApplicationContext())
                        .asBitmap()
                        .load(imgPath)
                        .override(DensityUtils.dip2px(40), DensityUtils.dip2px(40))
                        .into(imgAlbumIcon);

                view.findViewById(R.id.llAlbumItem).setOnClickListener(this);
                view.findViewById(R.id.llAlbumItem).setTag(album);

                llAlbums.addView(view);
            }
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


    @SuppressWarnings("serial")
    public class AlbumEntity implements Serializable {

        private String albumId;
        private String albumName;
        private List<String> images;


        public String getAlbumId() {
            return albumId;
        }

        public void setAlbumId(String albumId) {
            this.albumId = albumId;
        }

        public String getAlbumName() {
            return albumName;
        }

        public void setAlbumName(String albumName) {
            this.albumName = albumName;
        }

        public List<String> getImages() {
            if (images == null) {
                images = new ArrayList<String>();
            }
            return images;
        }
    }


    public class AlbumAdapter extends RecyclerView.Adapter<AlbumHolder> {

        @Override
        public AlbumHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.android_base_view_item_imageview,
                    parent, false);
            view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (DensityUtils.getWidthInPx() / 3)));
            return new AlbumHolder(view);
        }

        @Override
        public void onBindViewHolder(AlbumHolder holder, final int position) {
            final String imagePath = currAlbum.getImages().get(position);
            if (isVideo(imagePath)) {
                long duration = 0;
                if (videoDuration.containsKey(imagePath)) {
                    duration = videoDuration.get(imagePath);
                }else{
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            MediaMetadataRetriever mediaRetriever = new MediaMetadataRetriever();
                            mediaRetriever.setDataSource(formatPath(imagePath));
                            long duration = NumberUtils.formatToLong(mediaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
                            videoDuration.put(imagePath, duration);
                            mediaRetriever.release();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    notifyDataSetChanged();
                                }
                            });
                        }
                    }).start();
                }
                holder.llVideoContainer.setVisibility(View.VISIBLE);
                holder.tvVideoTime.setText(videoDurationFormat.format(new Date(duration)));

            } else {
                holder.llVideoContainer.setVisibility(View.GONE);
            }

            GlideApp.with(CrosheAlbumActivity.this)
                    .asBitmap()
                    .load(imagePath)
                    .placeholder(R.drawable.android_base_default_img)
                    .error(R.drawable.android_base_default_img)
                    .fitCenter()
                    .priority(Priority.HIGH)
                    .override(DensityUtils.dip2px(80), DensityUtils.dip2px(80))
                    .into(holder.imageView);
            if (isGIF(imagePath)) {
                holder.llGifContainer.setVisibility(View.VISIBLE);
            }else{
                holder.llGifContainer.setVisibility(View.GONE);
            }

            //当前图片已被选中
            if (selectImagesPath.contains(imagePath)) {
                holder.flChoose.setVisibility(View.VISIBLE);
            }else{
                holder.flChoose.setVisibility(View.INVISIBLE);
            }

        }

        @Override
        public int getItemCount() {
            return currAlbum.getImages().size();
        }
    }


    public class AlbumHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView imageView;
        public FrameLayout flChoose;
        public LinearLayout llVideoContainer;
        public LinearLayout llGifContainer;
        public TextView tvVideoTime;

        public AlbumHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            imageView.setOnClickListener(this);
            llVideoContainer = (LinearLayout) itemView.findViewById(R.id.llVideoContainer);
            llGifContainer = (LinearLayout) itemView.findViewById(R.id.llGifContainer);
            tvVideoTime = (TextView) itemView.findViewById(R.id.tvVideoTime);
            flChoose = (FrameLayout) itemView.findViewById(R.id.android_base_flChoose);

            llVideoContainer.setVisibility(View.GONE);

        }

        @Override
        public void onClick(View v) {
            if (getAdapterPosition() >= 0) {
                String imagePath = currAlbum.getImages().get(getAdapterPosition());
                if (!selectImagesPath.contains(imagePath)) {
                    selectImage(imagePath, getAdapterPosition());
                } else {
                    View viewWithTag = llSelectedImages.findViewWithTag(imagePath);
                    if (viewWithTag != null) {
                        removeSelected(viewWithTag.findViewById(R.id.sllRemove));
                    }
                }
            }
        }
    }


    public void selectImage(String imagePath, int photoIndex) {
        if (selectImagesPath.size() >= selectMaxCount) {
            Toast.makeText(context, "最多选择" + selectMaxCount + "张！", Toast.LENGTH_LONG).show();
            return;
        }
        selectImagesPath.add(imagePath);
        View view = LayoutInflater.from(context).inflate(R.layout.android_base_view_item_album_photo_select, null);

        if (isVideo(imagePath)) {
            view.findViewById(R.id.llVideoContainer).setVisibility(View.VISIBLE);
            TextView tvVideoTime = (TextView) view.findViewById(R.id.tvVideoTime);
            tvVideoTime.setText(videoDurationFormat.format(new Date(videoDuration.get(imagePath))));
        } else {
            view.findViewById(R.id.llVideoContainer).setVisibility(View.GONE);
        }

        if (isGIF(imagePath)) {
            view.findViewById(R.id.llGifContainer).setVisibility(View.VISIBLE);
        }else{
            view.findViewById(R.id.llGifContainer).setVisibility(View.GONE);
        }

        displaySelectedImage(imagePath, view, photoIndex);
        view.setTag(imagePath);
        llSelectedImages.addView(view);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                hScrollView.fullScroll(ScrollView.FOCUS_RIGHT);
            }
        }, 300);

        if (AConfig.AlbumAnim) {
            if (photoIndex >= 0) {
                currAlbum.getImages().remove(photoIndex);
                adapter.notifyItemRemoved(photoIndex);
            }
        }else{
            adapter.notifyItemChanged(photoIndex);
        }

        refreshConfirmText();

        if (selectMaxCount == 1) {
            onClick(findViewById(R.id.tvConfirm));
        }
    }


    private void displaySelectedImage(String imagePath, View view, int photoIndex) {
        ImageView imageView = (ImageView) view.findViewById(R.id.imgIcon);
        GlideApp.with(CrosheAlbumActivity.this)
                .asBitmap()
                .load(imagePath)
                .placeholder(R.drawable.android_base_default_img)
                .error(R.drawable.android_base_default_img)
                .override(DensityUtils.dip2px(50), DensityUtils.dip2px(50))
                .into(imageView);
        try {
            JSONObject json = new JSONObject();
            json.put("photoIndex", photoIndex);
            json.put("imagePath", imagePath);
            json.put("albumId", currAlbum.getAlbumId());
            json.put("obj", view);

            view.findViewById(R.id.android_base_imgIcon).setTag(json);
            view.findViewById(R.id.android_base_imgIcon).setOnClickListener(this);

            view.findViewById(R.id.sllRemove).setTag(json);
            view.findViewById(R.id.sllRemove).setOnClickListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public class AlbumItemDecoration extends RecyclerView.ItemDecoration {
        int space = DensityUtils.dip2px(1);

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.top = space;
            outRect.bottom = space;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 3) {
                selectImage("file://" + cameraSavePath, -1);
            }
        }else{
            if (requestCode == 3&&justCamera&&selectMaxCount==1) {
                finish();
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        recyclerView.removeAllViews();
        recyclerView = null;
    }


    public boolean isVideo(String path) {
        return path.toLowerCase().endsWith(".mp4");
    }

    public boolean isGIF(String path) {
        return path.toLowerCase().endsWith(".gif");
    }



    public String formatPath(String path) {
        return path.replace("file://", "");
    }


}
