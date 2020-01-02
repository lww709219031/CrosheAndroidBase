package com.croshe.android.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.croshe.android.base.activity.CrosheBrowserActivity;
import com.croshe.android.base.activity.CrosheDownListActivity;
import com.croshe.android.base.activity.CrosheVoiceAlbumActivity;
import com.croshe.android.base.activity.image.CrosheAlbumActivity;
import com.croshe.android.base.activity.image.CrosheCropImageActivity;
import com.croshe.android.base.activity.image.CrosheShowImageActivity;
import com.croshe.android.base.entity.MessageEntity;
import com.croshe.android.base.entity.share.ShareEntity;
import com.croshe.android.base.extend.glide.GlideApp;
import com.croshe.android.base.listener.OnCrosheForwardMoneyListener;
import com.croshe.android.base.listener.OnCrosheRedTakeListener;
import com.croshe.android.base.utils.BaseAppUtils;
import com.croshe.android.base.utils.DensityUtils;
import com.croshe.android.base.utils.DialogUtils;
import com.croshe.android.base.utils.FileUtils;
import com.croshe.android.base.utils.OKHttpUtils;
import com.croshe.android.base.views.menu.CrosheMenuItem;
import com.zxing.ZXingHelper;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 安徽创息软件科技有限公司-技术支持，http://wwww.croshe.com
 * Created by Janesen on 2017/7/22 15:54.
 */
public class AIntent {

  /**
   * 默认打开的action
   */
  public static final String ACTION_CROSHE_DEFAULT = "com.croshe.android.default";


  /**
   * 弹框提醒
   *
   * @param alertContent
   */
  public static void doAlert(String alertContent) {
    Intent event = new Intent();
    event.putExtra(AConstant.EXTRA_DO_ACTION, AConstant.ACTION_ALERT);
    event.putExtra(AConstant.ACTION_ALERT, alertContent);
    EventBus.getDefault().post(event);
  }


  /**
   * 等待框
   *
   * @param alertContent
   */
  public static void doShowProgress(String alertContent) {
    Intent event = new Intent();
    event.putExtra(AConstant.EXTRA_DO_ACTION, AConstant.ACTION_SHOW_PROGRESS);
    event.putExtra(AConstant.ACTION_SHOW_PROGRESS, alertContent);
    EventBus.getDefault().post(event);
  }


  /**
   * 等待框
   */
  public static void doHideProgress() {
    Intent event = new Intent();
    event.putExtra(AConstant.EXTRA_DO_ACTION, AConstant.ACTION_HIDE_PROGRESS);
    EventBus.getDefault().post(event);
  }


  /**
   * 打开应用程序的权限设置
   *
   * @param context
   */
  public static void startPermissionSet(Context context) {
    Intent intent = new Intent();
    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
    Uri uri = Uri.fromParts("package", context.getPackageName(), null);
    intent.setData(uri);
    context.startActivity(intent);
  }


  /**
   * 打开相册
   *
   * @param context
   */
  public static void startAlbum(Context context, Bundle... bundle) {
    Intent intent = new Intent(context.getPackageName() + "." + AConstant.CrosheAlbumActivity.class.getSimpleName());
    intent.putExtra(AConstant.EXTRA_DO_ACTION, CrosheAlbumActivity.class.getSimpleName());
    for (Bundle bundle1 : bundle) {
      intent.putExtras(bundle1);
    }
    BaseAppUtils.startActionIntent(context, intent, true);

  }

  /**
   * 打开录音文件
   *
   * @param context
   */
  public static void startVoiceAlbum(Context context, Bundle... bundle) {
    Intent intent = new Intent(context, CrosheVoiceAlbumActivity.class);
    intent.putExtra(AConstant.EXTRA_DO_ACTION, CrosheVoiceAlbumActivity.class.getSimpleName());
    for (Bundle bundle1 : bundle) {
      intent.putExtras(bundle1);
    }
    context.startActivity(intent);
  }


  /**
   * 打开剪裁图片
   *
   * @param context
   */
  public static void startCropImage(Context context, Bundle... bundle) {
    Intent intent = new Intent(context, CrosheCropImageActivity.class);
    intent.putExtra(AConstant.EXTRA_DO_ACTION, CrosheCropImageActivity.class.getSimpleName());
    for (Bundle bundle1 : bundle) {
      intent.putExtras(bundle1);
    }
    context.startActivity(intent);
  }

  /**
   * 打开剪裁图片
   *
   * @param context
   */
  public static void startCropImage(Context context, String imagePath) {
    Intent intent = new Intent(context, CrosheCropImageActivity.class);
    intent.putExtra(AConstant.EXTRA_DO_ACTION, CrosheCropImageActivity.class.getSimpleName());
    intent.putExtra(CrosheCropImageActivity.EXTRA_IMAGE_PATH, imagePath);
    context.startActivity(intent);
  }

  /**
   * 打开剪裁图片
   *
   * @param context
   */
  public static void startCropImage(Context context, String imagePath, Bundle... bundle) {
    Intent intent = new Intent(context, CrosheCropImageActivity.class);
    intent.putExtra(AConstant.EXTRA_DO_ACTION, CrosheCropImageActivity.class.getSimpleName());
    intent.putExtra(CrosheCropImageActivity.EXTRA_IMAGE_PATH, imagePath);
    for (Bundle bundle1 : bundle) {
      intent.putExtras(bundle1);
    }
    context.startActivity(intent);
  }


  /**
   * 打开视频录制
   *
   * @param context
   */
  public static void startRecordVideo(Context context, Bundle... bundle) {
    Intent intent = new Intent(context.getPackageName() + "." + AConstant.CrosheRecordVideoActivity.class.getSimpleName());
    intent.putExtra(AConstant.EXTRA_DO_ACTION, AConstant.CrosheRecordVideoActivity.class.getSimpleName());
    for (Bundle bundle1 : bundle) {
      intent.putExtras(bundle1);
    }
    if (bundle.length == 0) {
      File filePath = new File(Environment.getExternalStorageDirectory(),
              "Croshe/Video");
      if (!filePath.exists()) {
        filePath.mkdirs();
      }
      String videoPath = filePath.getAbsolutePath() + File.separator + System.currentTimeMillis() + ".mp4";
      intent.putExtra(AConstant.CrosheRecordVideoActivity.EXTRA_RECORD_VIDEO_PATH.name(), videoPath);
    }
    BaseAppUtils.startActionIntent(context, intent, true);
  }

  /**
   * 打开图片查看器
   */
  public static void startShowImage(Context context, String... imageUrls) {
    Intent intent = new Intent(context.getPackageName() + "." + AConstant.CrosheShowImageActivity.class.getSimpleName());
    intent.putExtra(AConstant.EXTRA_DO_ACTION, CrosheShowImageActivity.class.getSimpleName());
    intent.putExtra(CrosheShowImageActivity.EXTRA_IMAGES_PATH, imageUrls);
    BaseAppUtils.startActionIntent(context, intent, true);
  }

  /**
   * 打开图片查看器
   */
  public static void startShowImage(Context context, String firstImageUrl, String[] imageUrls) {
    Intent intent = new Intent(context.getPackageName() + "." + AConstant.CrosheShowImageActivity.class.getSimpleName());
    intent.putExtra(AConstant.EXTRA_DO_ACTION, CrosheShowImageActivity.class.getSimpleName());
    intent.putExtra(CrosheShowImageActivity.EXTRA_IMAGES_PATH, imageUrls);
    intent.putExtra(CrosheShowImageActivity.EXTRA_FIRST_PATH, firstImageUrl);
    BaseAppUtils.startActionIntent(context, intent, true);
  }

  /**
   * 打开图片查看器
   */
  public static void startShowImage(Context context, Bundle... bundle) {
    Intent intent = new Intent(context.getPackageName() + "." + AConstant.CrosheShowImageActivity.class.getSimpleName());
    intent.putExtra(AConstant.EXTRA_DO_ACTION, CrosheShowImageActivity.class.getSimpleName());
    for (Bundle bundle1 : bundle) {
      intent.putExtras(bundle1);
    }
    BaseAppUtils.startActionIntent(context, intent, true);
  }


  /**
   * 打开浏览器
   */
  public static void startBrowser(Context context, String url) {
    Intent intent = new Intent(context.getPackageName() + "." + AConstant.CrosheBrowserActivity.class.getSimpleName());
    intent.putExtra(CrosheBrowserActivity.EXTRA_URL, url);
    intent.putExtra(AConstant.EXTRA_DO_ACTION, AConstant.CrosheBrowserActivity.class.getSimpleName());
    BaseAppUtils.startActionIntent(context, intent, true);
  }


  /**
   * 调用系统分享图片
   *
   * @param context
   * @param url     图片路径
   */
  public static void shareImage(final Context context, String url) {
    final ProgressDialog progressDialog = new ProgressDialog(context);
    progressDialog.setMessage("初始化分享中，请稍后……");
    progressDialog.show();

    if (url.startsWith("http://") || url.startsWith("https://")) {
      OKHttpUtils.getInstance().downFile(context, url, new OKHttpUtils.HttpDownFileCallBack() {
        @Override
        public boolean onDownLoad(long countLength, long downLength, String localPath) {
          if (countLength == downLength) {
            progressDialog.dismiss();

            Uri imageUri = Uri.fromFile(new File(localPath));
            Log.d("STAG", "uri:" + imageUri);  //输出：file:///storage/emulated/0/test.jpg

            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
            shareIntent.setType("image/*");
            context.startActivity(Intent.createChooser(shareIntent, "分享到"));
          }
          return true;
        }

        @Override
        public void onDownFail(String message) {

        }
      });
    } else {
      Uri imageUri = Uri.fromFile(new File(url));
      Intent shareIntent = new Intent();
      shareIntent.setAction(Intent.ACTION_SEND);
      shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
      shareIntent.setType("image/*");
      context.startActivity(Intent.createChooser(shareIntent, "分享到"));
      progressDialog.dismiss();

    }
  }

  /**
   * 识别二维码
   */
  public static void doScannerQrCode(final Context context, String imagePath) {
    readQrCode(context, imagePath);
  }


  /**
   * 识别二维码
   */
  public static void readQrCode(final Context context, String url) {
    final ProgressDialog progressDialog = new ProgressDialog(context);
    progressDialog.setCanceledOnTouchOutside(false);
    progressDialog.setMessage("正在识别中，请稍后……");
    progressDialog.show();

    GlideApp.with(context.getApplicationContext())
            .asBitmap()
            .load(url)
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .fitCenter()
            .override(DensityUtils.dip2px(300), DensityUtils.dip2px(300))
            .listener(new RequestListener<Bitmap>() {
              @Override
              public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                progressDialog.dismiss();
                DialogUtils.alert(context, "系统提醒", "二维码识别错误，图片加载失败！");
                return false;
              }

              @Override
              public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                progressDialog.dismiss();
                String result = ZXingHelper.decodeImage(resource);
                if (StringUtils.isNotEmpty(result)) {
                  BaseAppUtils.doScannerResult(context, result);
                } else {
                  DialogUtils.alert(context, "系统提醒", "未识别出有效的二维码！");
                }
                return false;
              }
            }).submit();

  }


  /**
   * 调用系统分享图片
   *
   * @param context
   * @param url     图片路径
   */
  public static void saveImage(final Context context, String url) {
    final ProgressDialog progressDialog = new ProgressDialog(context);
    progressDialog.setMessage("初始化相册中，请稍后……");
    progressDialog.show();

    if (url.startsWith("http://") || url.startsWith("https://")) {
      OKHttpUtils.getInstance().downFile(context, url, new OKHttpUtils.HttpDownFileCallBack() {
        @Override
        public boolean onDownLoad(long countLength, long downLength, String localPath) {
          if (countLength == downLength) {
            progressDialog.dismiss();

            FileUtils.saveImageToGallery(context, new File(localPath));
            Toast.makeText(context, "保存成功！", Toast.LENGTH_LONG).show();
          }
          return true;
        }

        @Override
        public void onDownFail(String message) {

        }
      });
    } else {
      FileUtils.saveImageToGallery(context, new File(url));
      Toast.makeText(context, "保存成功！", Toast.LENGTH_LONG).show();
    }
  }


  /**
   * 打开单聊会话
   *
   * @param conversationId
   */
  public static void startSingleChat(Context context, String conversationId, Bundle... bundle) {
    Intent intent = new Intent(context.getPackageName() + "." + AConstant.CrosheChatActivity.class.getSimpleName());
    intent.putExtra(AConstant.CrosheChatActivity.EXTRA_CONVERSATION_ID.name(), conversationId);
    intent.putExtra(AConstant.CrosheChatActivity.EXTRA_CHAT_TYPE.name(), 0);
    intent.putExtra(AConstant.EXTRA_DO_ACTION, AConstant.CrosheChatActivity.class.getSimpleName());
    for (Bundle bundle1 : bundle) {
      intent.putExtras(bundle1);
    }
    BaseAppUtils.startActionIntent(context, intent, true);
  }

  /**
   * 打开群聊会话
   *
   * @param conversationId
   */
  public static void startChatGroup(Context context, String conversationId) {
    Intent intent = new Intent(context.getPackageName() + "." + AConstant.CrosheChatActivity.class.getSimpleName());
    intent.putExtra(AConstant.CrosheChatActivity.EXTRA_CONVERSATION_ID.name(), conversationId);
    intent.putExtra(AConstant.CrosheChatActivity.EXTRA_CHAT_TYPE.name(), 1);
    intent.putExtra(AConstant.EXTRA_DO_ACTION, AConstant.CrosheChatActivity.class.getSimpleName());

    BaseAppUtils.startActionIntent(context, intent, true);
  }


  /**
   * 打开会话
   *
   * @param context
   * @param conversationId
   */
  public static void startChat(Context context, String conversationId) {
    Intent intent = new Intent(context.getPackageName() + "." + AConstant.CrosheChatActivity.class.getSimpleName());
    intent.putExtra(AConstant.CrosheChatActivity.EXTRA_CONVERSATION_ID.name(), conversationId);
    intent.putExtra(AConstant.EXTRA_DO_ACTION, AConstant.CrosheChatActivity.class.getSimpleName());

    BaseAppUtils.startActionIntent(context, intent, true);
  }

  /**
   * 打开用户详情页
   *
   * @param context
   * @param userCode
   */
  public static void startUserInfo(Context context, String userCode) {
    Intent intent = new Intent(context.getPackageName() + "." + AConstant.CrosheUserInfoActivity.class.getSimpleName());
    intent.putExtra(AConstant.CrosheUserInfoActivity.EXTRA_USER_CODE.name(), userCode);
    intent.putExtra(AConstant.EXTRA_DO_ACTION, AConstant.CrosheUserInfoActivity.class.getSimpleName());

    BaseAppUtils.startActionIntent(context, intent, true);
  }


  /**
   * 打开单聊详情界面
   *
   * @param context
   * @param userCode
   */
  public static void startChatContact(Context context, String userCode) {
    Intent intent = new Intent(context.getPackageName() + "." + AConstant.CrosheChatContactActivity.class.getSimpleName());
    intent.putExtra(AConstant.CrosheChatContactActivity.EXTRA_USER_CODE.name(), userCode);
    intent.putExtra(AConstant.EXTRA_DO_ACTION, AConstant.CrosheChatContactActivity.class.getSimpleName());
    BaseAppUtils.startActionIntent(context, intent, true);
  }


  /**
   * 打开群聊详细界面
   *
   * @param context
   * @param groupCode
   */
  public static void startGroupInfo(Context context, String groupCode) {
    Intent intent = new Intent(context.getPackageName() + "." + AConstant.CrosheChatGroupInfoActivity.class.getSimpleName());
    intent.putExtra(AConstant.CrosheChatGroupInfoActivity.EXTRA_GROUP_CODE.name(), groupCode);
    intent.putExtra(AConstant.EXTRA_DO_ACTION, AConstant.CrosheChatGroupInfoActivity.class.getSimpleName());

    BaseAppUtils.startActionIntent(context, intent, true);
  }


  /**
   * 打开群详情介绍页
   *
   * @param context
   * @param groupCode
   */
  public static void startGroupDetails(Context context, String groupCode) {
    Intent intent = new Intent(context.getPackageName() + "." + AConstant.CrosheChatGroupDetailsActivity.class.getSimpleName());
    intent.putExtra(AConstant.CrosheChatGroupDetailsActivity.EXTRA_GROUP_CODE.name(), groupCode);
    intent.putExtra(AConstant.EXTRA_DO_ACTION, AConstant.CrosheChatGroupDetailsActivity.class.getSimpleName());

    BaseAppUtils.startActionIntent(context, intent, true);
  }


  /**
   * 打开群创建
   *
   * @param context
   */
  public static void startGroupCreate(Context context) {
    Intent intent = new Intent(context.getPackageName() + "." + AConstant.CrosheChatGroupCreateActivity.class.getSimpleName());

    BaseAppUtils.startActionIntent(context, intent, true);
  }

  /**
   * 打开加好友界面
   *
   * @param context
   * @param userCode
   */
  public static void startDoAddContact(Context context, String userCode, Bundle... bundle) {
    Intent intent = new Intent(context.getPackageName() + "." + AConstant.CrosheContactAddActivity.class.getSimpleName());
    intent.putExtra(AConstant.CrosheContactAddActivity.EXTRA_USER_CODE.name(), userCode);
    for (Bundle bundle1 : bundle) {
      intent.putExtras(bundle1);
    }
    BaseAppUtils.startActionIntent(context, intent, true);
  }


  /**
   * 打开本地通讯录
   *
   * @param context
   */
  public static void startLocalContact(Context context, Bundle... bundle) {
    Intent intent = new Intent(context.getPackageName() + "." + AConstant.CrosheLocalConactActivity.class.getSimpleName());
    for (Bundle bundle1 : bundle) {
      intent.putExtras(bundle1);
    }
    BaseAppUtils.startActionIntent(context, intent, true);
  }


  /**
   * 在地图中查看位置
   */
  public static void startLocationInMap(Context context, double latitude, double longitude, String address) {
    Intent intent = new Intent(context.getPackageName() + "." + AConstant.CrosheLocationInMapActivity.class.getSimpleName());
    intent.putExtra(AConstant.CrosheLocationInMapActivity.EXTRA_POI_LNG.name(), longitude);
    intent.putExtra(AConstant.CrosheLocationInMapActivity.EXTRA_POI_LAT.name(), latitude);
    intent.putExtra(AConstant.CrosheLocationInMapActivity.EXTRA_POI_ADDR.name(), address);
    intent.putExtra(AConstant.EXTRA_DO_ACTION, AConstant.CrosheLocationInMapActivity.class.getSimpleName());

    BaseAppUtils.startActionIntent(context, intent, true);
  }


  /**
   * 在地图中选择位置
   */
  public static void startSelectLocationInMap(Context context, Bundle... bundle) {
    Intent intent = new Intent(context.getPackageName() + "." + AConstant.CrosheSelectLocationInMap.class.getSimpleName());
    intent.putExtra(AConstant.EXTRA_DO_ACTION, AConstant.CrosheSelectLocationInMap.class.getSimpleName());

    for (Bundle bundle1 : bundle) {
      intent.putExtras(bundle1);
    }
    BaseAppUtils.startActionIntent(context, intent, true);
  }


  /**
   * 打开百度导航
   *
   * @param context
   * @param lat
   * @param lnt
   */
  public static void startBaiduMap(Context context, double lat, double lnt) {
    try {
      Intent intent = new Intent("intent://map/navi?location=" + lat + "," + lnt +
              "&type=TIME&src=thirdapp.navi.hndist.sydt#Intent;scheme=bdapp;" +
              "package=com.baidu.BaiduMap;end");
      if (BaseAppUtils.checkAppInstall("com.baidu.BaiduMap")) {
        context.startActivity(intent); // 启动调用
      } else {
        Toast.makeText(context, "请您先安装百度地图软件！", Toast.LENGTH_LONG).show();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 打开高德导航
   *
   * @param context
   * @param lat
   * @param lnt
   */
  public static void startGaodeMap(Context context, double lat, double lnt) {
    try {
      Intent intent = new Intent("android.intent.action.VIEW",
              android.net.Uri.parse("androidamap://navi?sourceApplication=" + BaseAppUtils.getApplicationName(context) + "&lat=" + lat
                      + "&lon=" + lnt + "&dev=0&style=2"));
      intent.setPackage("com.autonavi.minimap");
      if (BaseAppUtils.checkAppInstall("com.autonavi.minimap")) {
        context.startActivity(intent); // 启动调用
      } else {
        Toast.makeText(context, "请您先安装高德地图软件！", Toast.LENGTH_LONG).show();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  /**
   * 打开转发
   *
   * @param context
   */
  public static void startForward(Context context, Bundle... bundle) {
    Intent intent = new Intent(context.getPackageName() + "." + AConstant.CrosheForwardActivity.class.getSimpleName());
    intent.putExtra(AConstant.EXTRA_DO_ACTION, AConstant.CrosheForwardActivity.class.getSimpleName());

    for (Bundle bundle1 : bundle) {
      intent.putExtras(bundle1);
    }
    BaseAppUtils.startActionIntent(context, intent, true);
  }


  /**
   * 打开群成员界面
   *
   * @param context
   */
  public static void startChatGroupUser(Context context, Bundle... bundle) {
    Intent intent = new Intent(context.getPackageName() + "." + AConstant.CrosheChatGroupUsersActivity.class.getSimpleName());
    intent.putExtra(AConstant.EXTRA_DO_ACTION, AConstant.CrosheChatGroupUsersActivity.class.getSimpleName());

    for (Bundle bundle1 : bundle) {
      intent.putExtras(bundle1);
    }
    BaseAppUtils.startActionIntent(context, intent, true);
  }


  /**
   * 检测是否可以转发
   *
   * @param context
   * @return
   */
  public static boolean checkForward(Context context) {
    Intent intent = new Intent(context.getPackageName() + "." + AConstant.CrosheForwardActivity.class.getSimpleName());
    return BaseAppUtils.checkIntent(context, intent, false);
  }


  /**
   * 下载文件
   *
   * @param context
   */
  public static void doDownload(Context context, String fileName, String url) {
    CrosheDownListActivity.doDownLoad(context, url, fileName);

  }


  /**
   * 打开下载记录
   *
   * @param context
   */
  public static void startDownList(Context context) {
    Intent intent = new Intent(context.getPackageName() + "." + AConstant.CrosheDownListActivity.class.getSimpleName());
    BaseAppUtils.startActionIntent(context, intent, true);
  }


  /**
   * 打开添加好友界面
   *
   * @param context
   */
  public static void startAddContact(Context context) {
    Intent intent = new Intent(context.getPackageName() + "." + AConstant.AddContactActivity.class.getSimpleName());
    BaseAppUtils.startActionIntent(context, intent, true);
  }


  /**
   * 打开新的朋友界面
   *
   * @param context
   */
  public static void startNewContact(Context context) {
    Intent intent = new Intent(context.getPackageName() + "." + AConstant.CrosheContactNewActivity.class.getSimpleName());
    BaseAppUtils.startActionIntent(context, intent, true);
  }

  /**
   * 打开扫一扫
   *
   * @param context
   */
  public static void startScanner(Context context, Bundle... bundle) {
    Intent intent = new Intent(context.getPackageName() + "." + AConstant.CrosheScannerActivity.class.getSimpleName());
    for (Bundle bundle1 : bundle) {
      intent.putExtras(bundle1);
    }
    BaseAppUtils.startActionIntent(context, intent, true);
  }


  /**
   * 打开我的好友
   *
   * @param context
   */
  public static void startContact(Context context, Bundle... bundle) {
    Intent intent = new Intent(context.getPackageName() + "." + AConstant.CrosheContactActivity.class.getSimpleName());
    intent.putExtra(AConstant.EXTRA_DO_ACTION, AConstant.CrosheContactActivity.class.getSimpleName());

    for (Bundle bundle1 : bundle) {
      intent.putExtras(bundle1);
    }
    BaseAppUtils.startActionIntent(context, intent, true);
  }

  /**
   * 打开选择好友
   *
   * @param context
   */
  public static void startSelectContact(Context context, Bundle... bundle) {
    Intent intent = new Intent(context.getPackageName() + "." + AConstant.CrosheSelectContactActivity.class.getSimpleName());
    intent.putExtra(AConstant.EXTRA_DO_ACTION, AConstant.CrosheSelectContactActivity.class.getSimpleName());
    intent.putExtra("EXTRA_MULTI_CHECK", true);
    for (Bundle bundle1 : bundle) {
      intent.putExtras(bundle1);
    }
    BaseAppUtils.startActionIntent(context, intent, true);
  }


  /**
   * 打开群组列表
   *
   * @param context
   */
  public static void startGroup(Context context) {
    Intent intent = new Intent(context.getPackageName() + "." + AConstant.CrosheChatGroupActivity.class.getSimpleName());
    BaseAppUtils.startActionIntent(context, intent, true);
  }


  /**
   * 选择群组
   *
   * @param context
   */
  public static void startSelectGroup(Context context, Bundle... bundle) {
    Intent intent = new Intent(context.getPackageName() + "." + AConstant.CrosheSelectChatGroupActivity.class.getSimpleName());
    intent.putExtra(AConstant.EXTRA_DO_ACTION, AConstant.CrosheSelectChatGroupActivity.class.getSimpleName());
    intent.putExtra("EXTRA_CHECK", true);
    for (Bundle bundle1 : bundle) {
      intent.putExtras(bundle1);
    }
    BaseAppUtils.startActionIntent(context, intent, true);
  }

  /**
   * 打开我的收藏
   *
   * @param context
   */
  public static void startCollection(Context context, Bundle... bundle) {
    Intent intent = new Intent(context.getPackageName() + "." + AConstant.CrosheCollectionActivity.class.getSimpleName());
    intent.putExtra(AConstant.EXTRA_DO_ACTION, AConstant.CrosheCollectionActivity.class.getSimpleName());

    for (Bundle bundle1 : bundle) {
      intent.putExtras(bundle1);
    }
    BaseAppUtils.startActionIntent(context, intent, true);
  }


  /**
   * 打开聊天投诉界面
   *
   * @param context
   * @param bundle
   */
  public static void startComplainChat(Context context, Bundle... bundle) {
    Intent intent = new Intent(context.getPackageName() + "." + AConstant.CrosheComplainChatActivity.class.getSimpleName());
    intent.putExtra(AConstant.EXTRA_DO_ACTION, AConstant.CrosheComplainChatActivity.class.getSimpleName());

    for (Bundle bundle1 : bundle) {
      intent.putExtras(bundle1);
    }
    BaseAppUtils.startActionIntent(context, intent, true);
  }

  /**
   * 生成并查看二维码
   *
   * @param context
   */
  public static void startShowQRCode(Context context, String qrContent, Bundle... bundle) {
    Intent intent = new Intent(context.getPackageName() + "." + AConstant.CrosheShowQRCodeActivity.class.getSimpleName());
    intent.putExtra(AConstant.EXTRA_DO_ACTION, AConstant.CrosheShowQRCodeActivity.class.getSimpleName());
    intent.putExtra(AConstant.CrosheShowQRCodeActivity.EXTRA_QR_CONTENT.name(), qrContent);
    for (Bundle bundle1 : bundle) {
      intent.putExtras(bundle1);
    }
    BaseAppUtils.startActionIntent(context, intent, true);
  }


  /**
   * 打开第三方分享界面
   *
   * @param context
   * @param bundle
   */
  public static void startShare(Context context, Bundle... bundle) {
    startShare(context, null, bundle);

  }

  /**
   * 打开第三方分享界面
   *
   * @param context
   * @param bundle
   */
  public static void startShare(Context context, ShareEntity shareEntity, Bundle... bundle) {
    Intent intent = new Intent(context.getPackageName() + "." + AConstant.CrosheShareActivity.class.getSimpleName());
    intent.putExtra(AConstant.EXTRA_DO_ACTION, AConstant.CrosheShareActivity.class.getSimpleName());
    if (shareEntity != null) {
      intent.putExtra(AConstant.CrosheShareActivity.EXTRA_SHARE_DATA.name(), shareEntity);
    }
    for (Bundle bundle1 : bundle) {
      intent.putExtras(bundle1);
    }
    BaseAppUtils.startActionIntent(context, intent, true);
  }


  /**
   * 转发图片
   */
  public static void startForwardImage(String imageUrl) {
    MessageEntity messageEntity = new MessageEntity();
    messageEntity.setData(imageUrl);
    messageEntity.setType(MessageEntity.MessageType.Image);
    EventBus.getDefault().post(messageEntity);
  }


  /**
   * 转发图片
   *
   */
  public static void startForwardText(String content) {
    MessageEntity messageEntity = new MessageEntity();
    messageEntity.setData(content);
    messageEntity.setType(MessageEntity.MessageType.Text);
    EventBus.getDefault().post(messageEntity);
  }


  /**
   * 转发网页路径
   */
  public static void startForwardWebUrl(String webTitle, String webDetails, String webUrl, String webThumbUrl) {
    Map<String, Object> map = new HashMap<>();
    map.put("title", webTitle);
    map.put("content", webDetails);
    map.put("url", webUrl);
    map.put("imgUrl", webThumbUrl);

    MessageEntity messageEntity = new MessageEntity();
    messageEntity.setData(map);
    messageEntity.setType(MessageEntity.MessageType.Web);
    EventBus.getDefault().post(messageEntity);
  }


  /**
   * 发红包
   *
   * @param context
   * @param bundle
   */
  public static void startRed(Context context, Bundle... bundle) {
    Intent intent = new Intent(context.getPackageName() + "." + AConstant.CrosheRedActivity.class.getSimpleName());
    intent.putExtra(AConstant.EXTRA_DO_ACTION, AConstant.CrosheRedActivity.class.getSimpleName());

    for (Bundle bundle1 : bundle) {
      intent.putExtras(bundle1);
    }
    BaseAppUtils.startActionIntent(context, intent, true);
  }


  /**
   * 领取红包
   *
   * @param redId
   */
  public static void startRedInfo(final Object redId) {
    startRedInfo(redId, null);
  }

  /**
   * 领取红包
   *
   * @param redId
   */
  public static void startRedInfo(final Object redId, boolean isHideHead) {
    startRedInfo(redId, false, isHideHead, null);
  }

  /**
   * 领取红包
   *
   * @param redId
   */
  public static void startRedInfo(final Object redId, boolean isMultiRed, boolean isHideHead) {
    startRedInfo(redId, isMultiRed, isHideHead, null);
  }


  /**
   * 领取红包
   *
   * @param redId
   */
  public static void startRedInfo(final Object redId, OnCrosheRedTakeListener onCrosheRedTakeListener) {
    startRedInfo(redId, false, onCrosheRedTakeListener);
  }

  /**
   * 领取红包
   *
   * @param redId
   */
  public static void startRedInfo(final Object redId, boolean isMultiRed,
                                  OnCrosheRedTakeListener onCrosheRedTakeListener) {
    startRedInfo(redId, isMultiRed, false, onCrosheRedTakeListener);
  }

  /**
   * 领取红包
   *
   * @param redId
   */
  public static void startRedInfo(final Object redId, boolean isMultiRed,
                                  boolean isHideHead,
                                  OnCrosheRedTakeListener onCrosheRedTakeListener) {
    Intent intent = new Intent();
    intent.putExtra(AConstant.EXTRA_DO_ACTION, AConstant.CrosheRedTake.class.getSimpleName());
    intent.putExtra(AConstant.CrosheRedTake.EXTRA_RED_ID.name(), redId.toString());
    intent.putExtra(AConstant.CrosheRedTake.EXTRA_MULTI_RED.name(), isMultiRed);
    intent.putExtra(AConstant.CrosheRedTake.EXTRA_HIDE_HEAD.name(), isHideHead);
    intent.putExtra(AConstant.CrosheRedTake.EXTRA_RED_TAKE_LISTENER.name(), onCrosheRedTakeListener);
    EventBus.getDefault().post(intent);
  }


  /**
   * 查看红包记录
   *
   * @param context
   */
  public static void startRedRecord(final Context context, Bundle... bundle) {
    Intent intent = new Intent(context.getPackageName() + "." + AConstant.CrosheRedRecordActivity.class.getSimpleName());
    intent.putExtra(AConstant.EXTRA_DO_ACTION, AConstant.CrosheRedRecordActivity.class.getSimpleName());

    for (Bundle bundle1 : bundle) {
      intent.putExtras(bundle1);
    }
    BaseAppUtils.startActionIntent(context, intent, true);
  }


  /**
   * 启动支付界面
   *
   * @param activity
   * @param orderCode
   * @param orderMoney
   * @param orderTitle
   * @param onPaySuccess
   * @param otherMenus
   */
  public static void startPay(final Activity activity,
                              final String orderTitle,
                              final String orderCode,
                              final double orderMoney,
                              final Runnable onPaySuccess,
                              CrosheMenuItem... otherMenus) {
    AConfig.getOnPayListener().onChoosePay(activity,
            orderCode, orderMoney, orderTitle, onPaySuccess, otherMenus);
  }


  /**
   * 打开新消息提醒设置
   *
   * @param context
   */
  public static void startNoticeSet(final Context context) {
    Intent intent = new Intent(context.getPackageName() + "." + AConstant.CrosheMessageNoticeSetActivity.class.getSimpleName());
    intent.putExtra(AConstant.EXTRA_DO_ACTION, AConstant.CrosheMessageNoticeSetActivity.class.getSimpleName());
    BaseAppUtils.startActionIntent(context, intent, true);
  }


  /**
   * 打开勿扰模式设置
   *
   * @param context
   */
  public static void startSilenceSet(final Context context) {
    Intent intent = new Intent(context.getPackageName() + "." + AConstant.CrosheMessageSilenceActivity.class.getSimpleName());
    intent.putExtra(AConstant.EXTRA_DO_ACTION, AConstant.CrosheMessageSilenceActivity.class.getSimpleName());
    BaseAppUtils.startActionIntent(context, intent, true);
  }


  /**
   * 打开聊天设置
   *
   * @param context
   */
  public static void startChatSet(final Context context) {
    Intent intent = new Intent(context.getPackageName() + "." + AConstant.CrosheMessageChatSetActivity.class.getSimpleName());
    intent.putExtra(AConstant.EXTRA_DO_ACTION, AConstant.CrosheMessageChatSetActivity.class.getSimpleName());
    BaseAppUtils.startActionIntent(context, intent, true);
  }


  /**
   * 打开黑名单列表
   *
   * @param context
   */
  public static void startBlackList(final Context context) {
    Intent intent = new Intent(context.getPackageName() + "." + AConstant.CrosheBlackActivity.class.getSimpleName());
    intent.putExtra(AConstant.EXTRA_DO_ACTION, AConstant.CrosheBlackActivity.class.getSimpleName());
    BaseAppUtils.startActionIntent(context, intent, true);
  }


  /**
   * 打开发布动态
   *
   * @param context
   */
  public static void startAddDynamic(final Context context, Bundle... bundle) {
    Intent intent = new Intent(context.getPackageName() + "." + AConstant.CrosheAddDynamicActivity.class.getSimpleName());
    intent.putExtra(AConstant.EXTRA_DO_ACTION, AConstant.CrosheAddDynamicActivity.class.getSimpleName());
    for (Bundle bundle1 : bundle) {
      intent.putExtras(bundle1);
    }
    BaseAppUtils.startActionIntent(context, intent, true);
  }


  /**
   * 打开动态列表
   *
   * @param context
   */
  public static void startDynamicList(final Context context, Bundle... bundle) {
    Intent intent = new Intent(context.getPackageName() + "." + AConstant.CrosheDynamicListActivity.class.getSimpleName());
    intent.putExtra(AConstant.EXTRA_DO_ACTION, AConstant.CrosheDynamicListActivity.class.getSimpleName());

    for (Bundle bundle1 : bundle) {
      intent.putExtras(bundle1);
    }
    BaseAppUtils.startActionIntent(context, intent, true);
  }


  /**
   * 打开动态详情
   *
   * @param context
   */
  public static void startDynamicDetails(final Context context, int dynamicId, Bundle... bundle) {
    Intent intent = new Intent(context.getPackageName() + "." + AConstant.CrosheDynamicDetailsActivity.class.getSimpleName());
    intent.putExtra(AConstant.EXTRA_DO_ACTION, AConstant.CrosheDynamicListActivity.class.getSimpleName());
    intent.putExtra(AConstant.CrosheDynamicDetailsActivity.EXTRA_DYNAMIC_ID.name(), dynamicId);
    for (Bundle bundle1 : bundle) {
      intent.putExtras(bundle1);
    }
    BaseAppUtils.startActionIntent(context, intent, true);
  }


  /**
   * 打开用户的动态
   *
   * @param context
   */
  public static void startDynamicUser(final Context context, int userId, Bundle... bundle) {
    Intent intent = new Intent(context.getPackageName() + "." + AConstant.CrosheUserDynamicActivity.class.getSimpleName());
    intent.putExtra(AConstant.EXTRA_DO_ACTION, AConstant.CrosheUserDynamicActivity.class.getSimpleName());
    intent.putExtra(AConstant.CrosheUserDynamicActivity.EXTRA_USER_ID.name(), userId);
    for (Bundle bundle1 : bundle) {
      intent.putExtras(bundle1);
    }
    BaseAppUtils.startActionIntent(context, intent, true);
  }


  /**
   * 打开动态消息
   *
   * @param context
   */
  public static void startDynamicNotify(final Context context, Bundle... bundle) {
    Intent intent = new Intent(context.getPackageName() + "." + AConstant.CrosheDynamicNotifyActivity.class.getSimpleName());
    intent.putExtra(AConstant.EXTRA_DO_ACTION, AConstant.CrosheDynamicNotifyActivity.class.getSimpleName());
    for (Bundle bundle1 : bundle) {
      intent.putExtras(bundle1);
    }
    BaseAppUtils.startActionIntent(context, intent, true);
  }


  /**
   * 发起转账
   *
   * @param context
   */
  public static void startForwardMoney(final Context context, String targetUserCode, Bundle... bundle) {
    Intent intent = new Intent(context.getPackageName() + "." + AConstant.CrosheForwardMoneyActivity.class.getSimpleName());
    intent.putExtra(AConstant.EXTRA_DO_ACTION, AConstant.CrosheForwardMoneyActivity.class.getSimpleName());
    intent.putExtra(AConstant.CrosheForwardMoneyActivity.EXTRA_USER_CODE.name(),
            targetUserCode);
    for (Bundle bundle1 : bundle) {
      intent.putExtras(bundle1);
    }
    BaseAppUtils.startActionIntent(context, intent, true);
  }

  /**
   * 查看或领取转账
   *
   * @param context
   */
  public static void startForwardMoneyDetails(final Context context, int forwardId, OnCrosheForwardMoneyListener onCrosheForwardMoneyListener) {
    Intent intent = new Intent(context.getPackageName() + "." + AConstant.CrosheForwardMoneyDetailsActivity.class.getSimpleName());
    intent.putExtra(AConstant.EXTRA_DO_ACTION, AConstant.CrosheForwardMoneyDetailsActivity.class.getSimpleName());
    intent.putExtra(AConstant.CrosheForwardMoneyDetailsActivity.EXTRA_FORWARD_ID.name(),
            forwardId);
    AObject.putObject(AConstant.CrosheForwardMoneyDetailsActivity.EXTRA_FORWARD_LISTENER.name(), onCrosheForwardMoneyListener);
    BaseAppUtils.startActionIntent(context, intent, true);
  }


  /**
   * 拨打语音电话
   *
   * @param context
   * @param userCode
   */
  public static void startCallVoice(Context context, String userCode, String userName, String userHeadImg, Bundle... bundle) {
    Intent intent = new Intent(context.getPackageName() + "." + AConstant.CrosheCallVoiceActivity.class.getSimpleName());
    intent.putExtra(AConstant.EXTRA_DO_ACTION, AConstant.CrosheCallVoiceActivity.class.getSimpleName());
    intent.putExtra(AConstant.CrosheCallVoiceActivity.EXTRA_USER_CODE.name(),
            userCode);
    intent.putExtra(AConstant.CrosheCallVoiceActivity.EXTRA_USER_NAME.name(),
            userName);
    intent.putExtra(AConstant.CrosheCallVoiceActivity.EXTRA_USER_HEAD_IMG.name(),
            userHeadImg);
    for (Bundle bundle1 : bundle) {
      intent.putExtras(bundle1);
    }
    BaseAppUtils.startActionIntent(context, intent, true);
  }


  /**
   * 拨打视频电话
   *
   * @param context
   * @param userCode
   */
  public static void startCallVideo(Context context, String userCode, String userName, String userHeadImg, Bundle... bundle) {
    Intent intent = new Intent(context.getPackageName() + "." + AConstant.CrosheCallVideoActivity.class.getSimpleName());
    intent.putExtra(AConstant.EXTRA_DO_ACTION, AConstant.CrosheCallVideoActivity.class.getSimpleName());
    intent.putExtra(AConstant.CrosheCallVideoActivity.EXTRA_USER_CODE.name(),
            userCode);
    intent.putExtra(AConstant.CrosheCallVideoActivity.EXTRA_USER_NAME.name(),
            userName);
    intent.putExtra(AConstant.CrosheCallVideoActivity.EXTRA_USER_HEAD_IMG.name(),
            userHeadImg);
    for (Bundle bundle1 : bundle) {
      intent.putExtras(bundle1);
    }
    BaseAppUtils.startActionIntent(context, intent, true);
  }


  /**
   * 打开余额提现界面
   *
   * @param context
   */
  public static void startTakeMoney(Context context, Bundle... bundle) {
    Intent intent = new Intent(context.getPackageName() + "." + AConstant.CrosheTakeMoneyActivity.class.getSimpleName());
    intent.putExtra(AConstant.EXTRA_DO_ACTION, AConstant.CrosheTakeMoneyActivity.class.getSimpleName());
    for (Bundle bundle1 : bundle) {
      intent.putExtras(bundle1);
    }
    BaseAppUtils.startActionIntent(context, intent, true);
  }

  /**
   * 打开余额充值界面
   *
   * @param context
   */
  public static void startPayInMoney(Context context, Bundle... bundle) {
    Intent intent = new Intent(context.getPackageName() + "." + AConstant.CroshePayInMoneyActivity.class.getSimpleName());
    intent.putExtra(AConstant.EXTRA_DO_ACTION, AConstant.CroshePayInMoneyActivity.class.getSimpleName());
    for (Bundle bundle1 : bundle) {
      intent.putExtras(bundle1);
    }
    BaseAppUtils.startActionIntent(context, intent, true);
  }


  /**
   * 打开系统拨号界面
   *
   * @param context
   * @param phone
   */
  public static void startSysCallPhone(Context context, String phone) {
    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    context.startActivity(intent);
  }


  /**
   * 打开富文本编辑器
   * @param context
   * @param bundle
   */
  public static void startRichEditor(Context context, Bundle... bundle) {
    Intent intent = new Intent(context.getPackageName() + "." + AConstant.CrosheRichEditorActivity.class.getSimpleName());
    intent.putExtra(AConstant.EXTRA_DO_ACTION, AConstant.CrosheRichEditorActivity.class.getSimpleName());
    for (Bundle bundle1 : bundle) {
      intent.putExtras(bundle1);
    }
    BaseAppUtils.startActionIntent(context, intent, true);
  }

}
