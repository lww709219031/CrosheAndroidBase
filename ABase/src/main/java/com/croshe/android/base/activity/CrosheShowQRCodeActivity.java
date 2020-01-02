package com.croshe.android.base.activity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.croshe.android.base.AConstant;
import com.croshe.android.base.BaseApplication;
import com.croshe.android.base.R;
import com.croshe.android.base.entity.MessageEntity;
import com.croshe.android.base.extend.glide.GlideApp;
import com.croshe.android.base.listener.OnCrosheMenuClick;
import com.croshe.android.base.utils.BaseAppUtils;
import com.croshe.android.base.utils.DensityUtils;
import com.croshe.android.base.utils.FileUtils;
import com.croshe.android.base.utils.MD5Encrypt;
import com.croshe.android.base.views.menu.CrosheMenuItem;
import com.croshe.android.base.views.menu.CroshePopupMenu;
import com.zxing.ZXingHelper;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;

import java.io.File;

/**
 * 安徽创息软件科技有限公司-技术支持，http://wwww.croshe.com
 * Created by Janesen on 2017/8/13 17:05.
 */
public class CrosheShowQRCodeActivity extends CrosheBaseSlidingActivity {

    /**
     * 二维码内容
     */
    public static final String EXTRA_QR_CONTENT = "EXTRA_QR_CONTENT";

    /**
     * 二维码logo
     */
    public static final String EXTRA_QR_LOGO = "EXTRA_QR_LOGO";

    /**
     * 二维码提示标题
     */
    public static final String EXTRA_QR_TITLE = "EXTRA_QR_TITLE";

    /**
     * 二维码提示子标题
     */
    public static final String EXTRA_QR_SUBTITLE = "EXTRA_QR_SUBTITLE";


    private ImageView imgQRCode, imgLogo;
    private TextView tvQRTitle, tvQRSubTitle, tvAppName;
    private String qrContent, qrLogo;
    private CardView cardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.android_base_activity_qrcode);
        setTitle("查看二维码");
        initView();
    }


    public void initView() {
        imgQRCode = getView(R.id.android_base_imgQRCode);
        imgLogo = getView(R.id.android_base_imgLogo);
        tvQRTitle = getView(R.id.android_base_tvQRTitle);
        tvQRSubTitle = getView(R.id.android_base_tvQRSubtitle);
        tvAppName = getView(R.id.android_base_tvAppName);
        cardView = getView(R.id.android_base_qrCard);

        findViewById(R.id.android_base_imgMore).setOnClickListener(this);

        initValue();
    }


    public void initValue() {
        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().containsKey(EXTRA_QR_TITLE)) {
                tvQRTitle.setText(getIntent().getStringExtra(EXTRA_QR_TITLE));
            }
            if (getIntent().getExtras().containsKey(EXTRA_QR_SUBTITLE)) {
                tvQRSubTitle.setText(getIntent().getStringExtra(EXTRA_QR_SUBTITLE));
            }
            if (getIntent().getExtras().containsKey(EXTRA_QR_LOGO)) {
                qrLogo = getIntent().getStringExtra(EXTRA_QR_LOGO);
            }

            qrContent = getIntent().getExtras().getString(EXTRA_QR_CONTENT);
        }
        tvAppName.setText(getApplicationName());
        buildQRImage();
        imgLogo.setImageDrawable(BaseAppUtils.getAppIcon(context));

        getPopupMenu().bindLongClick(imgQRCode);

    }


    private void buildQRImage() {
        if (StringUtils.isNotEmpty(qrLogo)) {
            String url;
            if (qrLogo.startsWith("http://") || qrLogo.startsWith("https://") || qrLogo.startsWith("file://")) {
                url = qrLogo;
            } else {
                url = "file://" + qrLogo;
            }

            GlideApp.with(context.getApplicationContext())
                    .asBitmap()
                    .override(DensityUtils.dip2px(100), DensityUtils.dip2px(100))
                    .load(url)
                    .listener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                            makeQRImage(null);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                            makeQRImage(resource);
                            return false;
                        }
                    }).submit();
        }else{
            makeQRImage(null);
        }
    }


    private void makeQRImage(final Bitmap logo) {
        final String qrImagePath = getFilesDir().getPath() + "/Croshe/QRImage/"
                + MD5Encrypt.MD5(qrContent + qrLogo) + ".png";
        final String imgUri = "file://" + qrImagePath;
        File file = new File(qrImagePath);
        if (file.exists()) {
            GlideApp.with(context.getApplicationContext())
                    .load(imgUri)
                    .into(imgQRCode);
        }else{
            showProgress("生成二维码中，请稍后……");
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap qrImage;
                    if (logo != null) {
                        qrImage = ZXingHelper.createQRCode(qrContent,
                                DensityUtils.dip2px((float) (DensityUtils.dip2px(250) * 0.7)),
                                DensityUtils.dip2px((float) (DensityUtils.dip2px(250) * 0.7)),
                                logo);
                    } else {
                        qrImage = ZXingHelper.createQRCode(qrContent,
                                DensityUtils.dip2px((float) (DensityUtils.dip2px(250)*0.7)));
                    }
                    final Bitmap finalQrImage = qrImage;
                    FileUtils.saveBitmapToPath(finalQrImage, qrImagePath, true);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            GlideApp.with(context.getApplicationContext())
                                    .load(imgUri)
                                    .into(imgQRCode);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    hideProgress();
                }
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.android_base_imgMore) {
            getPopupMenu().showAnchorRight(v);
        }
    }


    public CroshePopupMenu getPopupMenu() {
        CroshePopupMenu popupMenu = CroshePopupMenu.newInstance(context)
                .addItem("保存二维码", new OnCrosheMenuClick() {
                    @Override
                    public void onClick(CrosheMenuItem item, View view) {
                        saveQrCode();
                    }
                });

        if (BaseApplication.checkBaseFunction(AConstant.BaseFunctionEnum.浏览器网页转发)) {
            popupMenu.addItem("分享给朋友", new OnCrosheMenuClick() {
                @Override
                public void onClick(CrosheMenuItem item, View view) {
                    shareToFriend();
                }
            });
        }
        return popupMenu;
    }


    private void saveQrCode() {
        Bitmap bitmap = Bitmap.createBitmap(cardView.getWidth(), cardView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        cardView.draw(canvas);
        File file = new File(context.getFilesDir().getAbsolutePath()
                + "/Croshe/Images/" + MD5Encrypt.MD5(qrContent) + ".png");
        FileUtils.saveBitmapToPath(bitmap, file.getAbsolutePath(), true);

        FileUtils.saveImageToGallery(context, file);
        toast("保存成功！");
    }


    private void shareToFriend() {
        Bitmap bitmap = Bitmap.createBitmap(cardView.getWidth(), cardView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        cardView.draw(canvas);

        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/Croshe/Images/" + MD5Encrypt.MD5(qrContent) + ".jpg");
        FileUtils.saveBitmapToPath(bitmap, file.getAbsolutePath(), true);

        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setType(MessageEntity.MessageType.Image);
        messageEntity.setData(file.getAbsolutePath());
        EventBus.getDefault().post(messageEntity);

    }
}
