package com.croshe.android.base.activity.image;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.croshe.android.base.AConfig;
import com.croshe.android.base.AConstant;
import com.croshe.android.base.AIntent;
import com.croshe.android.base.BaseApplication;
import com.croshe.android.base.R;
import com.croshe.android.base.activity.CrosheBaseSlidingActivity;
import com.croshe.android.base.extend.glide.GlideApp;
import com.croshe.android.base.extend.glide.ProgressManager;
import com.croshe.android.base.extend.listener.CrosheOnPageChangeListener;
import com.croshe.android.base.listener.OnCrosheImageProgressListener;
import com.croshe.android.base.listener.OnCrosheMenuClick;
import com.croshe.android.base.utils.DensityUtils;
import com.croshe.android.base.utils.FileUtils;
import com.croshe.android.base.utils.ImageUtils;
import com.croshe.android.base.utils.MD5Encrypt;
import com.croshe.android.base.utils.OKHttpUtils;
import com.croshe.android.base.views.control.CrosheCircleProgressView;
import com.croshe.android.base.views.control.CrosheVideoView;
import com.croshe.android.base.views.menu.CrosheMenuItem;
import com.croshe.android.base.views.menu.CroshePopupMenu;
import com.danikula.videocache.HttpProxyCacheServer;
import com.jaeger.library.StatusBarUtil;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;


/**
 * 查看图片、视频（.mp4）或GIF、 安徽创息软件科技有限公司-技术支持  http://www.croshe.com
 * Created by Janesen on 16/5/28.
 */
public class CrosheShowImageActivity extends CrosheBaseSlidingActivity {

    public final static String EXTRA_IMAGES_PATH = AConstant.CrosheShowImageActivity.EXTRA_IMAGES_PATH.name();
    public final static String EXTRA_IMAGES_DETAIL = AConstant.CrosheShowImageActivity.EXTRA_IMAGES_DETAIL.name();
    public final static String EXTRA_THUMB_IMAGES_PATH = AConstant.CrosheShowImageActivity.EXTRA_THUMB_IMAGES_PATH.name();
    public final static String EXTRA_FIRST_PATH = AConstant.CrosheShowImageActivity.EXTRA_FIRST_PATH.name();
    public final static String EXTRA_FIRST_INDEX = AConstant.CrosheShowImageActivity.EXTRA_FIRST_INDEX.name();
    public final static String EXTRA_SCHEME = AConstant.CrosheShowImageActivity.EXTRA_SCHEME.name();
    public final static String EXTRA_FILE_NAME = AConstant.CrosheShowImageActivity.EXTRA_FILE_NAME.name();

    private HttpProxyCacheServer proxyCacheServer;

    private Handler mHandler = new Handler();
    private ViewPager viewPager;
    private String[] paths = new String[0];
    private String[] thumbPaths = new String[0];
    private String[] details = new String[0];
    private String[] fileNames = new String[]{};
    private String firstPath;
    private String scheme = "";

    private TextView tvNumber;
    private Context context;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        windowBackground = Color.BLACK;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.android_base_activity_show_images);
        context = this;
        if (fullScreen(false)) {
            findViewById(R.id.viewHolder).setVisibility(View.VISIBLE);
        } else {
            StatusBarUtil.setColor(this, Color.BLACK);
            findViewById(R.id.viewHolder).setVisibility(View.GONE);
        }
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            paths = bundle.getStringArray(EXTRA_IMAGES_PATH);
            if (bundle.containsKey(EXTRA_FIRST_PATH)) {
                firstPath = bundle.getString(EXTRA_FIRST_PATH);
            }
            if (bundle.containsKey(EXTRA_THUMB_IMAGES_PATH)) {
                thumbPaths = bundle.getStringArray(EXTRA_THUMB_IMAGES_PATH);
            }
            if (bundle.containsKey(EXTRA_FILE_NAME)) {
                fileNames = bundle.getStringArray(EXTRA_FILE_NAME);
            }
            if (bundle.containsKey(EXTRA_IMAGES_DETAIL)) {
                details = bundle.getStringArray(EXTRA_IMAGES_DETAIL);
            }
            if (bundle.containsKey(EXTRA_SCHEME)) {
                scheme = bundle.getString(EXTRA_SCHEME);
            }
        }
        initView();
    }

    public void initView() {
        viewPager = (ViewPager) findViewById(R.id.android_base_viewPager);
        tvNumber = (TextView) findViewById(R.id.android_base_tvNumber);

        findViewById(R.id.android_base_llMore).setOnClickListener(this);

        ShowImageAdapter showImageAdapter = new ShowImageAdapter();
        viewPager.setAdapter(showImageAdapter);
        viewPager.addOnPageChangeListener(new CrosheOnPageChangeListener() {

            @Override
            public void onPageSelected(int index) {
                // TODO Auto-generated method stub
                super.onPageSelected(index);
                tvNumber.setText((index + 1) + "/" + paths.length);
            }
        });
        tvNumber.setText("1/" + paths.length);
        viewPager.setCurrentItem(getIntent().getExtras().getInt(EXTRA_FIRST_INDEX, 0));


        if (firstPath != null) {
            for (int i = 0; i < paths.length; i++) {
                if (paths[i].equals(firstPath)) {
                    viewPager.setCurrentItem(i);
                }
            }
        }
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.android_base_llMore) {

            int[] location = new int[2];
            v.getLocationOnScreen(location);
            getPopupMenu().showAnchorRight(v);
        }
    }


    private CroshePopupMenu getPopupMenu() {

        final CroshePopupMenu popupMenu = CroshePopupMenu.newInstance(context)
                .setLineColor(Color.parseColor("#cccccc"))
                .setMenuWidth(DensityUtils.dip2px(180))
                .addItem("保存到相册", R.drawable.android_base_ic_album, new OnCrosheMenuClick() {
                    @Override
                    public void onClick(CrosheMenuItem item, View view) {
                        save(viewPager.getCurrentItem());
                    }
                });

        if (BaseApplication.checkBaseFunction(AConstant.BaseFunctionEnum.相册识别二维码)) {
            popupMenu.addItem("识别二维码", R.drawable.android_base_ic_qrcode, new OnCrosheMenuClick() {
                @Override
                public void onClick(CrosheMenuItem item, View view) {
                    AIntent.readQrCode(context, getImagePath(paths[viewPager.getCurrentItem()]));
                }
            });
        }

        if (BaseApplication.checkBaseFunction(AConstant.BaseFunctionEnum.图片分享)) {
            if (AConfig.getOnForwardListener() != null) {
                popupMenu.addItem("分享给朋友", R.drawable.android_base_ic_forward, new OnCrosheMenuClick() {
                    @Override
                    public void onClick(CrosheMenuItem item, View view) {
                        String path = paths[viewPager.getCurrentItem()];
                        String thumbPath = null;
                        if (viewPager.getCurrentItem() < thumbPaths.length) {
                            thumbPath = thumbPaths[viewPager.getCurrentItem()];
                        }
                        if (isVideo(path) || isVideo(getFileName(viewPager.getCurrentItem()))) {
                            AConfig.getOnForwardListener().onForwardVideo(path, thumbPath);
                        } else {
                            AConfig.getOnForwardListener().onForwardImage(path);
                        }
                    }
                });
            }
        }

        return popupMenu;
    }

    private void save(int position) {
        try {
            if (position < 0) {
                return;
            }
            if (isVideo(paths[position]) || isVideo(getFileName(position))) {
                saveVideo(getImagePath(paths[position]));
            } else {
                saveImage(getImagePath(paths[position]));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getFileName(int position) {
        if (position < fileNames.length) {
            return fileNames[position];
        }
        return "";
    }


    private void saveImage(final String url) {
        try {
            showProgress("保存到相册中，请稍后……");
            Glide.with(context.getApplicationContext())
                    .asFile()
                    .load(url)
                    .listener(new RequestListener<File>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<File> target, boolean isFirstResource) {
                            alert("保存失败：" + e.getLocalizedMessage());
                            hideProgress();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(File resource, Object model, Target<File> target,
                                                       DataSource dataSource, boolean isFirstResource) {
                            try {
                                String ext = FileUtils.getFileExtName(url);

                                if (StringUtils.isEmpty(ext)) {
                                    ext = ".jpg";
                                }

                                if (!ext.startsWith("\\.")) {
                                    ext = "." + ext;
                                }

                                String path = Environment.getExternalStorageDirectory().getPath() + "/Croshe/DCIM/" +
                                        MD5Encrypt.MD5(resource.getAbsolutePath()) + ext;

                                File targetFile = new File(path);
                                if (!targetFile.exists()) {
                                    if (!targetFile.getParentFile().exists()) {
                                        targetFile.getParentFile().mkdirs();
                                    }
                                    org.apache.commons.io.FileUtils.copyFile(resource, targetFile);
                                }

                                FileUtils.saveImageToGallery(context, targetFile);
                                toast("保存成功！" + targetFile.getAbsolutePath());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            hideProgress();
                            return false;
                        }
                    }).submit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveVideo(String url) {
        try {
            if (url.startsWith("http://") || url.startsWith("https://")) {
                showProgress("保存到相册中，请稍后……");
                HttpProxyCacheServer proxy = getProxyCacheServer();
                String proxyUrl = proxy.getProxyUrl(url);

                OKHttpUtils.getInstance().downFile(context, proxyUrl, new OKHttpUtils.HttpDownFileCallBack() {
                    @Override
                    public boolean onDownLoad(long countLength, long downLength, final String localPath) {
                        if (countLength == downLength) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    hideProgress();
                                    try {
                                        String path = Environment.getExternalStorageDirectory().getPath() + "/Croshe/DCIM/" +
                                                MD5Encrypt.MD5(localPath) + ".mp4";

                                        File targetFile = new File(path);
                                        if (!targetFile.exists()) {
                                            if (!targetFile.getParentFile().exists()) {
                                                targetFile.getParentFile().mkdirs();
                                            }
                                            org.apache.commons.io.FileUtils.copyFile(new File(localPath), targetFile);
                                        }

                                        FileUtils.saveImageToGallery(context, targetFile);
                                        toast("保存成功！" + targetFile.getAbsolutePath());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                        return true;
                    }

                    @Override
                    public void onDownFail(String message) {
                        hideProgress();
                    }
                });
            } else {
                String path = Environment.getExternalStorageDirectory().getPath() + "/Croshe/DCIM/" +
                        MD5Encrypt.MD5(url) + ".mp4";
                File targetFile = new File(path);
                if (!targetFile.exists()) {
                    if (!targetFile.getParentFile().exists()) {
                        targetFile.getParentFile().mkdirs();
                    }
                    org.apache.commons.io.FileUtils.copyFile(new File(url), targetFile);
                }


                FileUtils.saveImageToGallery(context, targetFile);
                toast("保存成功！" + targetFile.getAbsolutePath());
            }
        } catch (Exception e) {
        }
    }


    public HttpProxyCacheServer getProxyCacheServer() {
        if (proxyCacheServer == null) {
            proxyCacheServer = new HttpProxyCacheServer(this);
        }
        return proxyCacheServer;
    }

    class ShowImageAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return paths.length;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            // TODO Auto-generated method stub
            return arg0 == arg1;
        }


        @Override
        public void destroyItem(ViewGroup container, int position,
                                Object object) {
            View view = (View) object;
            try {
                if (view.findViewById(R.id.photoView) != null) {
                    PhotoView photoView = (PhotoView) view.findViewById(R.id.photoView);
                    photoView.setImageBitmap(null);
                } else if (view.findViewById(R.id.android_base_videoView) != null) {
                    VideoView videoView = (VideoView) view.findViewById(R.id.android_base_videoView);
                    videoView.stopPlayback();
                }
            } catch (Exception e) {
            } finally {
                System.gc();
                container.removeView(view);
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            // TODO Auto-generated method stub
            String path = paths[position];
            String fileName = "";
            if (position < fileNames.length) {
                fileName = fileNames[position];
            }
            View contentView;
            if (isVideo(path) || isVideo(fileName)) {
                contentView = getVideoView(position);
            } else {
                contentView = getPhotoView(position);
            }
            container.addView(contentView);
            return contentView;
        }


        @NonNull
        private View getPhotoView(final int position) {
            final View contentView = LayoutInflater.from(context).inflate(R.layout.android_base_item_show_imageview, null);
            final PhotoView photoView = contentView.findViewById(R.id.photoView);
            final CrosheCircleProgressView progressView = (CrosheCircleProgressView) contentView.findViewById(R.id.android_base_progressView);
            progressView.setVisibility(View.VISIBLE);
            photoView.setMinimumScale(0.5f);
            progressView.setProgress(1);
            try {
                final RequestOptions myOptions = new RequestOptions()
                        .placeholder(R.drawable.android_base_default_img)
                        .fitCenter();
                final String loadImageUrl = getImagePath(paths[position]);
                ProgressManager.addProgressListener(new OnCrosheImageProgressListener() {
                    @Override
                    public void onProgress(String imageUrl, final long bytesRead, final long totalBytes, final boolean isDone, GlideException exception) {
                        final int percent = (int) ((bytesRead * 1.0f / totalBytes) * 100.0f);
                        if (loadImageUrl.equals(imageUrl)) {
                            if (isDone) {
                                ProgressManager.removeProgressListener(this);
                            }
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressView.setProgress(percent);
                                    if (isDone) {
                                        progressView.setVisibility(View.GONE);
                                    } else {
                                        progressView.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                        }
                    }
                });

                photoView.setImageResource(R.drawable.android_base_default_img);
                Glide.with(context.getApplicationContext())
                        .load(loadImageUrl)
                        .apply(myOptions)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                e.printStackTrace();
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressView.setVisibility(View.GONE);
                                    }
                                });
                                return false;
                            }
                        })
                        .into(photoView);
                photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                    @Override
                    public void onPhotoTap(View view, float x, float y) {
                        CrosheShowImageActivity.this.finish();
                    }
                });

                photoView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        vibrator(20);
                        getPopupMenu()
                                .setItemTitleGravity(Gravity.CENTER)
                                .showFromBottomMask();
                        return true;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

            return contentView;
        }

        @NonNull
        private View getVideoView(final int position) {
            final String path = paths[position];
            String thumbPath = null;
            if (path.toLowerCase().startsWith("http://") || path.toLowerCase().startsWith("https://")) {
                if (position < thumbPaths.length) {
                    thumbPath = thumbPaths[position];
                }
            }

            if (AConfig.getOnVideoViewListener() != null) {
                return AConfig.getOnVideoViewListener().getVideoView(context, thumbPath, path);
            }
            final View contentView = LayoutInflater.from(context).inflate(R.layout.android_base_item_show_videoview, null);


            final PhotoView imgThumb = contentView.findViewById(R.id.android_base_imgThumb);
            if (StringUtils.isNotEmpty(thumbPath)) {
                GlideApp.with(context.getApplicationContext())
                        .load(thumbPath)
                        .into(imgThumb);
            }else{
                GlideApp.with(context.getApplicationContext())
                        .load(path)
                        .into(imgThumb);
            }



            final CrosheVideoView videoView = (CrosheVideoView) contentView.findViewById(R.id.android_base_videoView);
            videoView.setBackgroundColor(Color.BLACK);
            videoView.setVisibility(View.INVISIBLE);

            videoView.setOnPlayStateListener(new CrosheVideoView.OnPlayStateListener() {
                @Override
                public void onPlayState(CrosheVideoView selfVideoView, int state) {
                    contentView.findViewById(R.id.android_base_tvProgress).setVisibility(View.GONE);
                    switch (state) {
                        case CrosheVideoView.PLAY_STATE_START:
                            contentView.findViewById(R.id.imgPlay).setVisibility(View.GONE);
                            imgThumb.setVisibility(View.GONE);
                            contentView.findViewById(R.id.android_base_tvProgress).setVisibility(View.GONE);
                            videoView.setBackgroundColor(Color.TRANSPARENT);
                            break;
                        case CrosheVideoView.PLAY_STATE_PAUSE:
                            contentView.findViewById(R.id.imgPlay).setVisibility(View.VISIBLE);
                            contentView.findViewById(R.id.imgPlay).bringToFront();
                            break;
                        case CrosheVideoView.PLAY_STATE_BUFFERING:
                            contentView.findViewById(R.id.imgPlay).setVisibility(View.GONE);
                            contentView.findViewById(R.id.android_base_tvProgress).setVisibility(View.VISIBLE);
                            break;
                        case CrosheVideoView.PLAY_STATE_STOP:
                        case CrosheVideoView.PLAY_STATE_COMPLETE:
                            imgThumb.setVisibility(View.VISIBLE);
                            contentView.findViewById(R.id.imgPlay).setVisibility(View.VISIBLE);

                            break;
                    }
                }
            });

            contentView.findViewById(R.id.android_base_flVideoPlay).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    videoView.setVisibility(View.VISIBLE);
                    contentView.findViewById(R.id.imgPlay).setVisibility(View.GONE);
                    contentView.findViewById(R.id.android_base_tvProgress).setVisibility(View.VISIBLE);
                    boolean isLocal = true;
                    if (!videoView.isInitPath()) {
                        HttpProxyCacheServer proxy = getProxyCacheServer();
                        String proxyUrl = proxy.getProxyUrl(path);
                        if (proxyUrl.toLowerCase().startsWith("http://") || proxyUrl.toLowerCase().startsWith("https://")) {
                            isLocal = false;
                            videoView.setVideoURI(Uri.parse(proxyUrl));
                        } else {
                            isLocal = true;
                            videoView.setVideoPath(path);
                        }
                    }
                    if (isLocal) {
                        if (videoView.isPlaying()) {
                            videoView.pause();
                        } else {
                            videoView.start();
                        }
                    }
                }
            });


            return contentView;
        }
    }


    public boolean isVideo(String path) {
        return path.toLowerCase().endsWith(".mp4");
    }


    public boolean isGIF(String path) {
        return path.toLowerCase().endsWith(".gif");
    }

    public boolean isImg(String path) {
        return Pattern.matches(".+(.JPEG|.jpeg|.JPG|.jpg|.BMP|.bmp|.PNG|.png|.gif)$", path.toLowerCase());
    }


    public String getImagePath(String imagePath) {
        String path = scheme + imagePath;

        if (StringUtils.isEmpty(scheme)) {
            path = ImageUtils.formatImagePath(path);
        }
        return path;
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new ContextWrapper(newBase) {
            @Override
            public Object getSystemService(String name) {
                if (Context.AUDIO_SERVICE.equals(name))
                    return getApplicationContext().getSystemService(name);
                return super.getSystemService(name);
            }
        });
    }


}
