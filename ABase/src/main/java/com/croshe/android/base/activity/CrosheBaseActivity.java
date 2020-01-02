package com.croshe.android.base.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.croshe.android.base.AConstant;
import com.croshe.android.base.BaseApplication;
import com.croshe.android.base.R;
import com.croshe.android.base.entity.AppVersionEntity;
import com.croshe.android.base.extend.content.CrosheIntent;
import com.croshe.android.base.server.BaseRequest;
import com.croshe.android.base.utils.BaseAppUtils;
import com.croshe.android.base.utils.DialogUtils;
import com.croshe.android.base.utils.ExitApplication;
import com.croshe.android.base.utils.ImageUtils;
import com.croshe.android.base.utils.ViewUtils;
import com.croshe.android.base.views.CrosheSoftKeyboardHelper;
import com.jaeger.library.StatusBarUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.functions.Consumer;


/**
 * 安徽创息软件科技有限公司-技术支持  http://www.croshe.com
 * Created by Janesen on 2017/6/24.
 */
@SuppressLint("CheckResult")
public  class CrosheBaseActivity extends AppCompatActivity implements View.OnClickListener {
  public static Context GlobalContext;

  //"android.permission.REQUEST_INSTALL_PACKAGES",

  private static final int REQUEST_INITPERMISSION = 0;
  private static final String[] defaultPermission = new String[]{
          "android.permission.READ_EXTERNAL_STORAGE",
          "android.permission.ACCESS_COARSE_LOCATION",
          "android.permission.ACCESS_FINE_LOCATION",
          "android.permission.READ_CONTACTS",
          "android.permission.WRITE_EXTERNAL_STORAGE",
          "android.permission.READ_PHONE_STATE",
          "android.permission.ACCESS_NETWORK_STATE",
          "android.permission.CHANGE_WIFI_STATE",
          "android.permission.CAMERA",
          Manifest.permission.READ_PHONE_STATE};

  /**
   * activity动画
   */
  public static int[] activityAnimations = new int[0];


  /**
   * 默认全局的权限
   */
  public static final Set<String> defaultPermissions = new HashSet<>();


  /**
   * 操作动作
   */
  public static final String EXTRA_DO_ACTION = "EXTRA_DO_ACTION";

  /**
   * 标题
   */
  public static final String EXTRA_TITLE = "EXTRA_TITLE";


  private List<ProgressDialog> progressDialogs = new ArrayList<>();

  protected boolean isActiivtyAnimation = true;


  protected Set<String> permissions = new HashSet<>();
  protected Handler handler;
  protected Context context;
  protected Toolbar toolbar = null;
  protected String title;
  protected String subTitle;
  protected TextView tvTitleView;
  protected String defaultTitle;
  private CrosheIntent intent;
  protected Menu optionMenu;
  protected boolean isEvent;
  protected  RxPermissions rxPermissions;
  protected boolean isJustTopActivity;//是否进行关联在此activity之上的

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.getWindow().getDecorView().setBackgroundColor(Color.WHITE);
    this.getWindow().getDecorView().setClickable(true);
    ExitApplication.addActivity(this);
    if (isActiivtyAnimation) {
      if (activityAnimations.length > 1) {
        overridePendingTransition(activityAnimations[0], activityAnimations[1]);
      }
    }
    rxPermissions = new RxPermissions(this);
    handler = new Handler();
    context = this;
    //记录键盘高度和监听
    new CrosheSoftKeyboardHelper(this.getWindow().getDecorView());
  }


  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    bringTop();
  }

  public void bringTop() {
    GlobalContext = this;
    if (isJustTopActivity) {
      ExitApplication.finishUpActivity(this);
    }
  }


  @Override
  public boolean onTouchEvent(MotionEvent event) {
    if (event.getAction() == MotionEvent.ACTION_CANCEL
            || event.getAction() == MotionEvent.ACTION_UP) {
      ViewUtils.closeKeyboard(getCurrentFocus());
    }
    return super.onTouchEvent(event);
  }



  protected String getExtraTitle(String defaultTitle) {
    if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(EXTRA_TITLE)) {
      return getIntent().getStringExtra(EXTRA_TITLE);
    }
    return defaultTitle;
  }


  public boolean fullScreen() {
    return fullScreen(false);
  }

  public boolean fullScreen(boolean windowUILight) {
    View decorView = getWindow().getDecorView();
    int option = 0;
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
      option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
              | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

      if (windowUILight) {
        option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
      }
    }
    decorView.setSystemUiVisibility(option);
    transparentStatusBar();
    if (Build.VERSION.SDK_INT >= 21) {
      return true;
    }
    return false;
  }


  /**
   * 将状态栏设为透明
   */
  public void transparentStatusBar() {
    if (Build.VERSION.SDK_INT >= 21) {
      getWindow().setStatusBarColor(Color.TRANSPARENT);
    } else {
      StatusBarUtil.setColor(this, getColorPrimary());
      StatusBarUtil.setTranslucent(this);
    }
  }

  public void setStatusBarLight(boolean isLight) {
    View decorView = getWindow().getDecorView();
    int option = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
    if (isLight) {
      option = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
              | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
    }
    decorView.setSystemUiVisibility(option);
  }

  public void onClickByBase(View v) {
    if (v.getId() == R.id.llBack) {
      finish();
    }
  }

  public void initToolBar() {
    if (findViewById(R.id.toolbar) != null) {
      toolbar = (Toolbar) findViewById(R.id.toolbar);
      setSupportActionBar(toolbar);
      toolbar.setTitleTextColor(findColor(R.color.colorTitle));
      toolbar.setNavigationOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          finish();
        }
      });
      if (StringUtils.isNotEmpty(title)) {
        toolbar.setTitle(title);
      }
      toolbar.setSubtitle(subTitle);
    }
    if (findViewById(R.id.tv_tab_title) != null) {
      tvTitleView = getView(R.id.tv_tab_title);
    }
    if (findViewById(R.id.android_base_tabTitle) != null) {
      tvTitleView = getView(R.id.android_base_tabTitle);
    }

    if (tvTitleView != null) {
      if (tvTitleView.getGravity() == (Gravity.TOP | Gravity.START)) {
        tvTitleView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
      }
      tvTitleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
      if (StringUtils.isNotEmpty(title)) {
        tvTitleView.setText(title);
      }
    }
  }


  public void setTitle(String title) {
    super.setTitle(title);
    this.title = title;
    if (getSupportActionBar() != null) {
      getSupportActionBar().setTitle(title);
    }
    if (tvTitleView != null) {
      tvTitleView.setText(title);
    }
  }

  public String getBarTitle() {
    return title;
  }


  public void resetTitle() {
    setTitle(defaultTitle);
  }

  /**
   * 取消全屏
   *
   * @return
   */
  public boolean cancelFullScreen() {
    View decorView = getWindow().getDecorView();
    int option = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
    decorView.setSystemUiVisibility(option);
    return true;
  }


  /**
   * 取消全屏
   *
   * @return
   */
  public boolean cancelFullScreen(int color) {
    View decorView = getWindow().getDecorView();
    int option = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
    decorView.setSystemUiVisibility(option);
    StatusBarUtil.setColor(this, color);
    setStatusBarHidden(false);
    return true;
  }


  /**
   * 设置状态栏显示或隐藏
   *
   * @param hidden
   */
  public void setStatusBarHidden(boolean hidden) {
    if (hidden) {
      WindowManager.LayoutParams lp = getWindow().getAttributes();
      lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
      getWindow().setAttributes(lp);
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    } else {
      WindowManager.LayoutParams lp = getWindow().getAttributes();
      lp.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
      getWindow().setAttributes(lp);
      getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }
  }


  /**
   * 添加权限
   *
   * @param permission
   */
  public void addPermission(String permission) {
    permissions.add(permission);
  }



  /**
   * 检测权限
   */
  public void checkPermission(String permission) {
    addPermission(permission);
    checkPermission();
  }

  /**
   * 检测权限
   */
  public void checkPermission(String permission, Consumer<Boolean> consumer) {
    addPermission(permission);
    checkPermission(consumer);
  }


  /**
   * 检测权限
   */
  public void checkPermission() {
    rxPermissions.request(getPermissions())
            .subscribe(new Consumer<Boolean>() {
              @Override
              public void accept(Boolean aBoolean) throws Exception {
                if (aBoolean) {
                  hasPermission();
                }
              }
            });
  }

  /**
   * 检测权限
   */
  public void checkPermission(Consumer<Boolean> consumer) {
    rxPermissions.request(getPermissions())
            .subscribe(consumer);
  }



  /**
   * 权限检测成功回调
   */
  public void hasPermission() {

  }


  /**
   * 根据ID获得控件
   *
   * @param id
   * @param <T>
   * @return
   */
  public <T> T getView(int id) {
    return (T) findViewById(id);
  }


  /**
   * 弹出Toast消息
   *
   * @param message
   */
  public void toast(CharSequence message) {
    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
  }


  /**
   * 弹出Toast消息
   *
   * @param message
   */
  public void toast_short(CharSequence message) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
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
    Vibrator vb = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
    vb.vibrate(duration);
  }

  private String[] getPermissions() {
    for (String s : defaultPermission) {
      permissions.add(s);
    }

    for (String permission : defaultPermissions) {
      permissions.add(permission);
    }
    return permissions.toArray(new String[]{});
  }


  /**
   * 获得activity的intent
   *
   * @param cls
   * @return
   */
  public CrosheIntent getActivity(Class<?> cls) {
    intent = CrosheIntent.newInstance(this);
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


  public int getColorPrimary() {
    return BaseAppUtils.getColorPrimary(context);
  }

  public int getDarkColorPrimary() {
    return BaseAppUtils.getDarkColorPrimary(context);
  }

  public int getColorAccent() {
    return BaseAppUtils.getColorAccent(context);
  }

  @Override
  public void onAttachedToWindow() {
    super.onAttachedToWindow();
    if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(EXTRA_TITLE)) {
      setTitle(getIntent().getStringExtra(EXTRA_TITLE));
    }
    defaultTitle = title;

    if (!EventBus.getDefault().isRegistered(this)) {
      EventBus.getDefault().register(this);
    }
    setNavigationIconColor(findColor(R.color.colorTitle));
    setTitleColor(findColor(R.color.colorTitle));
  }


  /**
   * 设置导航按钮的颜色
   *
   * @param color
   */
  public void setNavigationIconColor(int color) {
    if (toolbar != null && toolbar.getNavigationIcon() != null) {
      toolbar.setNavigationIcon(ImageUtils.tintDrawable(toolbar.getNavigationIcon(), ColorStateList.valueOf(color)));
    }
    ImageView imageView = getView(R.id.navigation_icon);
    if (imageView != null) {
      imageView.setImageDrawable(ImageUtils.tintDrawable(imageView.getDrawable(), ColorStateList.valueOf(color)));
    }
  }


  /**
   * 设置标题颜色
   *
   * @param color
   */
  public void setTitleColor(int color) {
    if (tvTitleView != null) {
      tvTitleView.setTextColor(color);
    }
    if (toolbar != null) {
      toolbar.setTitleTextColor(color);
    }
  }

  @Override
  public void setContentView(@LayoutRes int layoutResID) {
    super.setContentView(layoutResID);
    initToolBar();
  }

  @Override
  public void setContentView(View view) {
    super.setContentView(view);
    initToolBar();
  }


  @Override
  public void setContentView(View view, ViewGroup.LayoutParams params) {
    super.setContentView(view, params);
    initToolBar();
  }


  @Subscribe(priority = 100)
  public void onBaseMainEvent(final Intent intent) {
    final String do_action = StringUtils.defaultIfBlank(intent.getStringExtra(EXTRA_DO_ACTION), "NONE");
    if (do_action.equals(AConstant.ACTION_ALERT)) {
      if (ExitApplication.getTopActivity() != null) {
        if (ExitApplication.getTopActivity() == CrosheBaseActivity.this) {
          handler.post(new Runnable() {
            @Override
            public void run() {
              String alertContent = intent.getStringExtra(AConstant.ACTION_ALERT);
              DialogUtils.alert(context, "系统提醒", alertContent);
            }
          });
          EventBus.getDefault().cancelEventDelivery(intent);
        }
      }
    } else if (do_action.equals(AConstant.ACTION_SHOW_PROGRESS)) {
      if (ExitApplication.getTopActivity() != null) {
        if (ExitApplication.getTopActivity() == CrosheBaseActivity.this) {
          handler.post(new Runnable() {
            @Override
            public void run() {
              String alertContent = intent.getStringExtra(AConstant.ACTION_SHOW_PROGRESS);
              showProgress(alertContent);
            }
          });
          EventBus.getDefault().cancelEventDelivery(intent);
        }
      }
    } else if (do_action.equals(AConstant.ACTION_HIDE_PROGRESS)) {
      handler.post(new Runnable() {
        @Override
        public void run() {
          if (ExitApplication.getTopActivity() != null) {
            if (ExitApplication.getTopActivity() == CrosheBaseActivity.this) {
              hideProgress();
            }
          }
        }
      });
    } else {
      handler.post(new Runnable() {
        @Override
        public void run() {
          onDefaultEvent(do_action, intent);
        }
      });
    }
  }


  /**
   * 收到默认的事件
   *
   * @param action
   * @param intent
   */
  public void onDefaultEvent(String action, Intent intent) {

  }


  /**
   * 检测App版本
   */
  public void checkAppVersion() {
    checkAppVersion(false);
  }

  /**
   * 检测App版本
   */
  public void checkAppVersion(final boolean isToastTip) {
    checkAppVersion(isToastTip, false);
  }

  /**
   * 检测App版本
   */
  @SuppressLint("HandlerLeak")
  public void checkAppVersion(final boolean isToastTip, final boolean isJustShow) {
    checkAppVersion(isToastTip, isJustShow, false);
  }

  /**
   * 检测App版本
   */
  @SuppressLint("HandlerLeak")
  public void checkAppVersion(final boolean isToastTip, final boolean isJustShow, boolean isJustBrowser) {
    boolean haveInstallPermission;
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
      //先判断是否有安装未知来源应用的权限
      haveInstallPermission = getPackageManager().canRequestPackageInstalls();
      if(!haveInstallPermission){
        //弹框提示用户手动打开
        DialogUtils.confirm(context, "安装权限", "需要打开允许安装应用的权限，请去设置中开启此权限", new DialogInterface.OnClickListener() {
          @RequiresApi(api = Build.VERSION_CODES.O)
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {
            toInstallPermissionSettingIntent();
          }
        });
        return;
      }
    }

    final boolean finalIsJustBrowser = isJustBrowser;
    rxPermissions.request("android.permission.WRITE_EXTERNAL_STORAGE"
            , "android.permission.READ_EXTERNAL_STORAGE")
            .subscribe(new Consumer<Boolean>() {
              @Override
              public void accept(Boolean aBoolean) throws Exception {
                if (aBoolean) {
                  if (BaseApplication.Croshe_APP_Id == -1) {
                    DialogUtils.alert(context, "方法【checkAppVersion】错误", "请在Application类里为【BaseApplication.Croshe_APP_Id】设置值！");
                    return;
                  }
                  BaseRequest.checkVersion(new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                      super.handleMessage(msg);
                      AppVersionEntity appVersionEntity = (AppVersionEntity) msg.obj;
                      if (!BaseRequest.computeVersion(context, appVersionEntity, isJustShow,
                              finalIsJustBrowser) && isToastTip) {
                        toast("暂无新版本！");
                      }
                    }
                  }, BaseApplication.Croshe_APP_Id);
                }
              }
            });
  }



  /**
   * 开启安装未知来源权限
   */
  @RequiresApi(api = Build.VERSION_CODES.O)
  private void toInstallPermissionSettingIntent() {
    Uri packageURI = Uri.parse("package:"+getPackageName());
    Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,packageURI);
    startActivity(intent);
  }


  @Override
  public void onClick(View v) {

  }

  public int findColor(int colorResourceId) {
    return getResources().getColor(colorResourceId);
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    if (super.onCreateOptionsMenu(menu)) {
      this.optionMenu = menu;
      onOptionsMenuInitDone();
      return true;
    }
    return false;
  }

  public void onOptionsMenuInitDone() {

  }

  @Override
  protected void onResume() {
    super.onResume();
    bringTop();
  }

  @Override
  public void finish() {
    super.finish();
    if (isJustTopActivity) {
      ExitApplication.finishUnActivity(this);
    }else{
      ExitApplication.removeActivity(this);
    }
    if (isActiivtyAnimation) {
      if (activityAnimations.length > 3) {
        overridePendingTransition(activityAnimations[2], activityAnimations[3]);
      }
    }
  }


  /**
   * 只关闭自身
   */
  public void finishSelf() {
    super.finish();
    ExitApplication.removeActivity(this);
    if (isActiivtyAnimation) {
      if (activityAnimations.length > 3) {
        overridePendingTransition(activityAnimations[2], activityAnimations[3]);
      }
    }
  }

  @Override
  protected void onPause() {
    super.onPause();
    handler.post(new Runnable() {
      @Override
      public void run() {
        ViewUtils.closeKeyboard(getWindow().getDecorView());
      }
    });
  }


  public void displayImage(ImageView imageView, String url) {
    ImageUtils.displayImage(imageView, url);
  }

  public void displayImage(ImageView imageView, String url, int defaultImageResource) {
    ImageUtils.displayImage(imageView, url, defaultImageResource);
  }


  public void glideImage(ImageView imageView, String url) {
    ImageUtils.glideImage(imageView, url);
  }

  public void glideImage(ImageView imageView, String url, int width, int height) {
    ImageUtils.glideImage(imageView, url, width, height);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (EventBus.getDefault().isRegistered(this)) {
      EventBus.getDefault().unregister(this);
    }
  }

  public String getApplicationName() {
    return BaseAppUtils.getApplicationName(context);
  }


  public TextView setTextView(int viewId, Object content) {
    TextView textView = getView(viewId);
    if (content == null || textView == null) {
      return textView;
    }
    if (content instanceof CharSequence) {
      CharSequence charSequence = (CharSequence) content;
      textView.setText(charSequence);
    } else {
      textView.setText(Html.fromHtml(String.valueOf(content)));
    }
    return textView;
  }

  /**
   * 显示等待框
   *
   * @param message
   */
  public ProgressDialog showProgress(String message) {
    return showProgress(message, true);
  }


  /**
   * 显示等待框
   *
   * @param message
   */
  public ProgressDialog showProgress(String message,boolean cancelable) {
    try {
      ProgressDialog progressDialog = new ProgressDialog(context);
      progressDialog.setCanceledOnTouchOutside(false);
      progressDialog.setMessage(message);
      progressDialog.setCancelable(cancelable);
      progressDialog.show();
      progressDialogs.add(progressDialog);
      return progressDialog;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }


  /**
   * 关闭等待框
   */
  public void hideProgress() {
    if (progressDialogs != null) {
      for (ProgressDialog progressDialog : progressDialogs) {
        progressDialog.dismiss();
      }
      progressDialogs.clear();
    }
  }


  public String getActionName() {
    return this.getClass().getSimpleName();
  }


  public void alert(final String message) {
    handler.post(new Runnable() {
      @Override
      public void run() {
        DialogUtils.alert(ExitApplication.getContext(), "系统提醒", message);
      }
    });
  }

  public void alert(final String message, final Runnable onConfirmCallBack) {
    handler.post(new Runnable() {
      @Override
      public void run() {
        try {
          DialogUtils.alert(ExitApplication.getContext(), "系统提醒", message, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              if (onConfirmCallBack != null) {
                onConfirmCallBack.run();
              }
            }
          });
        } catch (Exception e) {}
      }
    });
  }


  public Context getContext() {
    return context;
  }

}
