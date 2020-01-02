package com.croshe.android.base.fragment;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.croshe.android.base.extend.content.CrosheIntent;
import com.croshe.android.base.utils.BaseAppUtils;
import com.croshe.android.base.utils.DialogUtils;
import com.croshe.android.base.utils.ImageUtils;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import static com.croshe.android.base.AConstant.EXTRA_DO_ACTION;

/**
 * 安徽创息软件科技有限公司-技术支持，http://wwww.croshe.com
 * Created by Janesen on 2017/7/10 14:28.
 */
public class CrosheBaseFragment extends Fragment implements View.OnClickListener {

    private List<ProgressDialog> progressDialogs = new ArrayList<>();
    private Bundle extras = new Bundle();
    private CrosheIntent intent;
    protected Handler handler = new Handler();
    protected Context context;

    public Bundle getExtras() {
        setArguments(extras);
        return extras;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public void displayImage(ImageView imageView, String url) {
        ImageUtils.displayImage(imageView, url);
    }

    public void glideImage(ImageView imageView, String url) {
        ImageUtils.glideImage(imageView, url);
    }

    public void glideImage(ImageView imageView, String url, int width, int height) {
        ImageUtils.glideImage(imageView, url, width, height);
    }


    /**
     * 附加到控件上
     *
     * @param viewId
     */
    public void attach(FragmentManager fragmentManager, int viewId) {
        if (getFragmentManager() == null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(viewId, this);
            transaction.commit();
        } else {
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.show(this);
            fragmentTransaction.commit();
        }
    }


    /**
     * 移除控件
     */
    public void unAttach() {
        if (getFragmentManager() != null) {
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.remove(this);
            fragmentTransaction.commit();
        }
    }


    /**
     * 影藏
     */
    public void hide() {
        if (getFragmentManager() != null) {
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.hide(this);
            fragmentTransaction.commit();
        }
    }


    /**
     * 显示等待框
     *
     * @param message
     */
    public void showProgress(String message) {
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage(message);
        progressDialog.show();
        progressDialogs.add(progressDialog);
    }


    /**
     * 关闭等待框
     */
    public void hideProgress() {
        for (ProgressDialog progressDialog : progressDialogs) {
            progressDialog.dismiss();
        }
        progressDialogs.clear();
    }


    public String getApplicationName() {
        return BaseAppUtils.getApplicationName(getContext());
    }


    public int findColor(int colorResourceId) {
        return getResources().getColor(colorResourceId);
    }


    public int getColorPrimary() {
        return BaseAppUtils.getColorPrimary(getContext());
    }

    public int getColorAccent() {
        return BaseAppUtils.getColorAccent(getContext());
    }


    /**
     * 根据ID获得控件
     *
     * @param id
     * @param <T>
     * @return
     */
    public <T> T getView(int id) {
        return (T) getView().findViewById(id);
    }


    public final View findViewById(int id) {
        return getView().findViewById(id);
    }

    /**
     * 弹出Toast消息
     *
     * @param message
     */
    public void toast(CharSequence message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }


    /**
     * 震动
     */
    public void vibrator() {
        vibrator(300);
    }

    /**
     * 震动
     *
     * @param duration
     */
    public void vibrator(long duration) {
        Vibrator vb = (Vibrator) getContext().getSystemService(Service.VIBRATOR_SERVICE);
        vb.vibrate(duration);
    }


    /**
     * 获得activity的intent
     *
     * @param cls
     * @return
     */
    public CrosheIntent getActivity(Class<?> cls) {
        intent = CrosheIntent.newInstance(getActivity());
        intent.getActivity(cls);
        return intent;
    }

    /**
     * 打开已获得的activity
     */
    public void startActivity() {
        intent.startActivity();
    }

    /**
     * 打开已获得的activity
     */
    public void startActivityFor(int requestCode) {
        intent.startActivityForResult(requestCode);
    }


    public void alert(String message) {
        DialogUtils.alert(getContext(), "系统提醒", message);
    }


    @Subscribe(priority = 100, threadMode = ThreadMode.POSTING)
    public void onBaseFrameEvent(final Intent intent) {
        final String do_action = StringUtils.defaultIfBlank(intent.getStringExtra(EXTRA_DO_ACTION), "NONE");
        handler.post(new Runnable() {
            @Override
            public void run() {
                onDefaultEvent(do_action, intent);
            }
        });
    }


    /**
     * 收到默认的事件
     *
     * @param action
     * @param intent
     */
    public void onDefaultEvent(String action, Intent intent) {

    }


    @Override
    public void onClick(View v) {

    }

    /**
     * 设置透明状态栏
     */
    public void setStatusBar() {
        if (Build.VERSION.SDK_INT >= 21) {//21表示5.0
            Window window = getActivity().getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= 19) {//19表示4.4
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //虚拟键盘也透明
            //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }
}
