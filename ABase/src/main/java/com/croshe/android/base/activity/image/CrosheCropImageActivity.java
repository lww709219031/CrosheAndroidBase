package com.croshe.android.base.activity.image;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.croshe.android.base.AConstant;
import com.croshe.android.base.R;
import com.croshe.android.base.activity.CrosheBaseSlidingActivity;
import com.croshe.android.base.extend.glide.GlideApp;
import com.croshe.android.base.utils.DensityUtils;
import com.croshe.android.base.utils.ImageUtils;
import com.isseiaoki.simplecropview.CropImageView;
import com.isseiaoki.simplecropview.callback.SaveCallback;

import org.greenrobot.eventbus.EventBus;

import java.io.File;


/**
 * 剪裁图片 安徽创息软件科技有限公司-技术支持  http://www.croshe.com
 * Created by Janesen on 16/5/28.
 */
public class CrosheCropImageActivity extends CrosheBaseSlidingActivity {


    public final static String EXTRA_IMAGE_PATH = AConstant.CrosheCropImageActivity.EXTRA_IMAGE_PATH.name();
    public final static String EXTRA_IMAGE_WIDTH = AConstant.CrosheCropImageActivity.EXTRA_IMAGE_WIDTH.name();
    public final static String EXTRA_IMAGE_HEIGHT = AConstant.CrosheCropImageActivity.EXTRA_IMAGE_HEIGHT.name();
    public final static String EXTRA_IMAGE_QUALITY = AConstant.CrosheCropImageActivity.EXTRA_IMAGE_QUALITY.name();
    public final static String EXTRA_CROP_FREE = AConstant.CrosheCropImageActivity.EXTRA_CROP_FREE.name();

    public final static String RESULT_IMAGE_PATH = AConstant.CrosheCropImageActivity.RESULT_IMAGE_PATH.name();

    private CropImageView cropImageView;
    private String imagePath;

    private Bundle bundle;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.android_base_activity_crop_image);
        fullScreen(true);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("剪裁图片");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        bundle = getIntent().getExtras();
        imagePath = bundle.getString(EXTRA_IMAGE_PATH);
        initView();
    }

    public void initView() {
        cropImageView = (CropImageView) findViewById(R.id.cropImageView);
        cropImageView.setMinFrameSizeInDp(150);

        cropImageView.setCropMode(CropImageView.CropMode.SQUARE);
        cropImageView.setCompressFormat(Bitmap.CompressFormat.JPEG);
        cropImageView.setCompressQuality(bundle.getInt(EXTRA_IMAGE_QUALITY, 100));
        cropImageView.setOutputMaxSize(DensityUtils.dip2px(bundle.getInt(EXTRA_IMAGE_WIDTH, 120)), DensityUtils.dip2px(bundle.getInt(EXTRA_IMAGE_HEIGHT, 120)));

        GlideApp.with(context.getApplicationContext())
                .load(ImageUtils.formatImagePath(imagePath))
                .into(cropImageView);


        findViewById(R.id.sllConfirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final File file = new File(context.getFilesDir() + "/Croshe/Crop/" + System.currentTimeMillis() + ".jpg");
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                cropImageView.startCrop(Uri.fromFile(file), null, new SaveCallback() {
                    @Override
                    public void onError(Throwable e) {
                        alert("图片剪裁失败！" + e.getLocalizedMessage());
                    }

                    @Override
                    public void onSuccess(Uri outputUri) {
                        Intent data = new Intent();
                        data.putExtra(EXTRA_IMAGE_PATH, file.getAbsolutePath());
                        data.putExtra(RESULT_IMAGE_PATH, file.getAbsolutePath());
                        data.putExtra(EXTRA_DO_ACTION, CrosheCropImageActivity.class.getSimpleName());
                        data.putExtras(getIntent());
                        setResult(RESULT_OK, data);
                        EventBus.getDefault().post(data);
                        finish();
                    }
                });

            }
        });

    }


}
