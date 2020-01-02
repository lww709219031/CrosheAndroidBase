package com.croshe.android.base.activity;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.croshe.android.base.AConstant;
import com.croshe.android.base.AIntent;
import com.croshe.android.base.R;
import com.croshe.android.base.utils.BaseAppUtils;
import com.croshe.android.base.utils.ImageUtils;
import com.croshe.android.base.utils.MediaUtils;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.BarcodeView;
import com.zxing.view.CrosheViewFinderView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import io.reactivex.functions.Consumer;

import static com.croshe.android.base.AConstant.CrosheScannerActivity.RETURN_SCANNER;

/**
 * 安徽创息软件科技有限公司-技术支持，http://wwww.croshe.com
 * Created by Janesen on 2017/8/1 16:38.
 */
public class CrosheScannerActivity extends CrosheBaseSlidingActivity  {

    private static final int REQ_PERMISSION_CAMERA = 0;
    private BarcodeView barcodeView;
    private CrosheViewFinderView crosheViewFinderView;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.android_base_activity_scanner);
        barcodeView = findViewById(R.id.android_base_barcode_view);
        barcodeView.setUseTextureView(true);


        crosheViewFinderView = getView(R.id.android_base_finder_view);
        crosheViewFinderView.setCameraPreview(barcodeView);


        EventBus.getDefault().register(this);
        findViewById(R.id.android_base_scanner_album).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt(AConstant.CrosheAlbumActivity.EXTRA_MAX_SELECT.name(), 1);
                bundle.putString(AConstant.EXTRA_DO_ACTION, "ScannerAlbum");
                AIntent.startAlbum(CrosheScannerActivity.this,bundle);
            }
        });


        if (findViewById(R.id.toolbar) != null) {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitleTextColor(Color.WHITE);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            if (toolbar.getNavigationIcon() != null) {
                toolbar.setNavigationIcon(ImageUtils.tintDrawable(toolbar.getNavigationIcon(), ColorStateList.valueOf(getResources().getColor(R.color.colorTitle))));
            }
        }


        rxPermissions.request(Manifest.permission.CAMERA)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            decode();
                        }
                    }
                });
    }



    public void result(String content) {
        Vibrator vb = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
        vb.vibrate(300);
        MediaUtils.playRaw(this, R.raw.beep);

        Intent intent = new Intent();
        intent.putExtra(RETURN_SCANNER.name(), content);
        intent.putExtra(EXTRA_DO_ACTION, this.getClass().getSimpleName());
        intent.putExtras(getIntent());
        EventBus.getDefault().post(intent);
        finish();

        if (getIntent().getBooleanExtra(AConstant.CrosheScannerActivity.EXTRA_AUTO_CHECK_RESUlT.name(), false)) {
            BaseAppUtils.doScannerResult(context, content);
        }
    }


    @Subscribe
    public void onScannerEvent(Intent data) {
        String action = data.getStringExtra(AConstant.EXTRA_DO_ACTION);
        if (action.equals("ScannerAlbum")) {
            String path = data.getStringExtra(AConstant.CrosheAlbumActivity.RESULT_SINGLE_IMAGES_PATH.name());
            AIntent.doScannerQrCode(this, path);
        }
    }


    private void decode() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        crosheViewFinderView.drawViewfinder();
        barcodeView.decodeSingle(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                result(result.getText());
            }

            @Override
            public void possibleResultPoints(List<ResultPoint> resultPoints) {
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onPause() {
        barcodeView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        barcodeView.resume();
    }

}
