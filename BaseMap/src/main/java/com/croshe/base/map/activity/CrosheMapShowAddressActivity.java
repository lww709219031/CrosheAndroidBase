package com.croshe.base.map.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.PoiItem;
import com.croshe.android.base.AConfig;
import com.croshe.android.base.AConstant;
import com.croshe.android.base.BaseApplication;
import com.croshe.android.base.activity.CrosheBaseSlidingActivity;
import com.croshe.android.base.listener.OnCrosheMenuClick;
import com.croshe.android.base.utils.BaseAppUtils;
import com.croshe.android.base.utils.DensityUtils;
import com.croshe.android.base.utils.ImageUtils;
import com.croshe.android.base.utils.MD5Encrypt;
import com.croshe.android.base.views.menu.CrosheMenuItem;
import com.croshe.android.base.views.menu.CroshePopupMenu;
import com.croshe.base.map.R;


/**
 * Created by Janesen on 2017/5/8.
 */
public class CrosheMapShowAddressActivity extends CrosheBaseSlidingActivity implements AMapLocationListener, AMap.OnCameraChangeListener, AMap.OnMapTouchListener {

    public static final String EXTRA_POI_ITEM = "EXTRA_POI_ITEM";

    public static final String EXTRA_POI_LAT = AConstant.CrosheLocationInMapActivity.EXTRA_POI_LAT.name();

    public static final String EXTRA_POI_LNG = AConstant.CrosheLocationInMapActivity.EXTRA_POI_LNG.name();

    public static final String EXTRA_POI_ADDR =  AConstant.CrosheLocationInMapActivity.EXTRA_POI_ADDR.name();


    private MapView mMapView = null;
    private AMap aMap = null;
    public AMapLocationClientOption mLocationOption = null;
    public AMapLocationClient mLocationClient = null;

    private TextView tvLocation;
    private LatLng latLng;
    private Marker poiMarker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.android_base_map_activity_map_show_address);
        fullScreen(true);
        initToolBar();

        mMapView = (MapView) findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mMapView.getMap();
        }

        tvLocation = getView(R.id.tvAddress);
        initLocation();
    }


    public void initLocation() {
        aMap.setOnCameraChangeListener(this);
        aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
        aMap.setOnMapTouchListener(this);

        mLocationClient = new AMapLocationClient(getApplicationContext());
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setOnceLocation(true);
        mLocationOption.setOnceLocationLatest(true);
        mLocationOption.setNeedAddress(true);
        mLocationClient.setLocationListener(this);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);


        if (getIntent().getExtras() != null) {

            if (getIntent().getExtras().containsKey(EXTRA_POI_ITEM)) {
                PoiItem poiItem = getIntent().getParcelableExtra(EXTRA_POI_ITEM);
                latLng = new LatLng(poiItem.getLatLonPoint().getLatitude(), poiItem.getLatLonPoint().getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.icon(BitmapDescriptorFactory.
                        fromBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.android_base_map_position)));

                poiMarker = aMap.addMarker(markerOptions);

                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20), 1000, null);
            }

            if (getIntent().getExtras().containsKey(EXTRA_POI_LAT)
                    && getIntent().getExtras().containsKey(EXTRA_POI_LNG)
                    && getIntent().getExtras().containsKey(EXTRA_POI_ADDR)) {

                latLng = new LatLng(getIntent().getExtras().getDouble(EXTRA_POI_LAT),
                        getIntent().getExtras().getDouble(EXTRA_POI_LNG));

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.icon(getMarkBitmap());

                poiMarker = aMap.addMarker(markerOptions);
                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20), 1000, null);

                tvLocation.setText(getIntent().getExtras().getString(EXTRA_POI_ADDR));
            }
        } else {
            //启动定位
            aMap.setMyLocationEnabled(true);
        }
    }

    public BitmapDescriptor getMarkBitmap() {
        View view = LayoutInflater.from(context).inflate(R.layout.android_base_map_item_location_anchor, null);
        return BitmapDescriptorFactory.fromView(view);
    }


    public void onClickByShowMap(View v) {
        if (v.getId() == R.id.imgNavigation) {
            CroshePopupMenu.newInstance(context)
                    .setTitle("选择导航")
                    .setMenuWidth(DensityUtils.dip2px(300))
                    .setLineColor(Color.parseColor("#cccccc"))
                    .addItem("百度地图", "建议已安装百度地图用户使用", R.drawable.navigation_baidu, new OnCrosheMenuClick() {
                        @Override
                        public void onClick(CrosheMenuItem item, View view) {
                            BaseAppUtils.startBaiduMap(context, latLng.latitude, latLng.longitude);
                        }
                    })
                    .addItem("高德地图", "建议已安装高德地图用户使用", R.drawable.navigation_gaode, new OnCrosheMenuClick() {
                        @Override
                        public void onClick(CrosheMenuItem item, View view) {
                            BaseAppUtils.startGaodeMap(context, latLng.latitude, latLng.longitude);
                        }
                    })
                    .showFromCenterMask();
        }
    }


    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        LatLng latLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
        aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20), 1000, null);
        Log.d("STAG", "定位成功！");
    }

    @Override
    public void onTouch(MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setSlideEnable(false);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                setSlideEnable(true);
                break;
        }
    }

    @Override
    public void onOptionsMenuInitDone() {
        super.onOptionsMenuInitDone();
        if (BaseApplication.checkBaseFunction(AConstant.BaseFunctionEnum.图片分享)) {
            if (AConfig.getOnForwardListener() == null) {
                optionMenu.findItem(R.id.android_base_more).setVisible(false);
            }else{
                optionMenu.findItem(R.id.android_base_more).setVisible(true);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.android_base_map_menu_show_address, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.android_base_more) {
            CroshePopupMenu.newInstance(context)
                    .setLineColor(Color.parseColor("#cccccc"))
                    .addItem("发送给朋友", new OnCrosheMenuClick() {
                        @Override
                        public void onClick(CrosheMenuItem item, View view) {
                            forward();
                        }
                    }).showAnchorRight(findViewById(R.id.android_base_more));

        }
        return super.onOptionsItemSelected(item);
    }


    public void forward() {
        showProgress("请稍后……");
        poiMarker.setVisible(false);
        aMap.getMapScreenShot(new AMap.OnMapScreenShotListener() {
            @Override
            public void onMapScreenShot(Bitmap bitmap) {

            }

            @Override
            public void onMapScreenShot(Bitmap bitmap, int i) {
                hideProgress();
                poiMarker.setVisible(true);
                String shortPath = context.getFilesDir().getPath() + "/Croshe/MapScreenShop/" + MD5Encrypt.MD5(System.currentTimeMillis() + "MAP") + ".jpg";
                ImageUtils.compressImage(bitmap, shortPath, 50);

                AConfig.getOnForwardListener().onForwardLocation(tvLocation.getText().toString(),
                        latLng.latitude, latLng.longitude, shortPath);
            }
        });
    }



}
