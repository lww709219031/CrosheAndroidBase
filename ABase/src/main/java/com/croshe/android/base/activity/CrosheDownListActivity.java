package com.croshe.android.base.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.croshe.android.base.R;
import com.croshe.android.base.entity.DownEntity;
import com.croshe.android.base.entity.dao.AppCacheEntity;
import com.croshe.android.base.entity.dao.AppCacheHelper;
import com.croshe.android.base.listener.OnCrosheMenuClick;
import com.croshe.android.base.listener.OnCrosheRecyclerDataListener;
import com.croshe.android.base.listener.PageDataCallBack;
import com.croshe.android.base.listener.SimpleOnCrosheMenuListener;
import com.croshe.android.base.utils.BaseAppUtils;
import com.croshe.android.base.utils.DensityUtils;
import com.croshe.android.base.utils.FileUtils;
import com.croshe.android.base.utils.OKHttpUtils;
import com.croshe.android.base.views.control.CrosheViewHolder;
import com.croshe.android.base.views.list.CrosheRecyclerView;
import com.croshe.android.base.views.menu.CrosheMenuItem;
import com.croshe.android.base.views.menu.CroshePopupMenu;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 安徽创息软件科技有限公司-技术支持，http://wwww.croshe.com
 * Created by Janesen on 2017/7/28 20:35.
 */
public class CrosheDownListActivity extends CrosheBaseSlidingActivity implements OnCrosheRecyclerDataListener<DownEntity> {

  private static String CacheKey = "CrosheDownLoad";

  private CrosheRecyclerView<DownEntity> recyclerView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.android_base_activity_down_list);
    setTitle("下载列表");
    EventBus.getDefault().register(this);

    initView();
  }

  public void initView() {
    recyclerView = getView(R.id.android_base_crosheLetterView);
    recyclerView.setOnCrosheRecyclerDataListener(this);
    recyclerView.setItemPadding(DensityUtils.dip2px(1f));
    recyclerView.getSuperRecyclerView().setItemAnimator(null);
  }


  @Override
  public void getData(int page, PageDataCallBack<DownEntity> callBack) {

    List<DownEntity> list = getDownList();
    for (DownEntity downEntity : list) {
      downEntity.setStop(!OKHttpUtils.getInstance().exitsDownFile(downEntity.getUrl(), downEntity.getLocalPath()));
    }
    callBack.loadData(list, true);
  }

  @Override
  public int getItemViewLayout(DownEntity obj, int position, int crosheViewType) {
    return R.layout.android_base_item_down;
  }

  @Override
  public void onRenderView(final DownEntity obj, final int position, final int crosheViewType, CrosheViewHolder holder) {



    TextView tvName = holder.getView(R.id.android_base_tvName);
    TextView tvName2 = holder.getView(R.id.android_base_tvName2);

    final TextView tvTip = holder.getView(R.id.android_base_down_tip);
    TextView tvTip2 = holder.getView(R.id.android_base_down_tip2);


    FrameLayout progressView = holder.getView(R.id.android_base_progress);
    LinearLayout llMask = holder.getView(R.id.andorid_base_llMask);


    ViewGroup.LayoutParams maskLayout = llMask.getLayoutParams();
    maskLayout.width = (int) DensityUtils.getWidthInPx();
    llMask.setLayoutParams(maskLayout);


    double progress = obj.getProgress(context);

    int progressWidth = (int) (DensityUtils.getWidthInPx() * progress);

    ViewGroup.LayoutParams layout = progressView.getLayoutParams();
    layout.width = progressWidth;
    progressView.setLayoutParams(layout);


    ImageView fileIcon = holder.getView(R.id.android_base_file_icon);
    fileIcon.setImageBitmap(BaseAppUtils.getFileIcon(context, obj.getFileName()));

    ImageView downIcon = holder.getView(R.id.android_base_down_icon);


    int p = (int) (progress * 100);
    if (obj.isDownDone(context)) {

      if (obj.getFileName().toLowerCase().endsWith(".apk")) {
        fileIcon.setImageDrawable(BaseAppUtils.getApkIcon(context, obj.getLocalPath()));
      }
      tvTip.setText("已完成下载");
      progressView.setVisibility(View.GONE);
      downIcon.setVisibility(View.GONE);
    } else {
      if (!obj.isStop()) {
        tvTip.setText("已下载：" + p + "%");
      } else {
        tvTip.setText("已暂停");
      }
      progressView.setVisibility(View.VISIBLE);
      downIcon.setVisibility(View.VISIBLE);
    }


    tvName.setText(obj.getFileName());
    tvName2.setText(tvName.getText());
    tvTip2.setText(tvTip.getText());


    View view = holder.onClick(R.id.android_base_down, new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (obj.isDownDone(context)) {
          FileUtils.openFile(context, new File(obj.getLocalPath()));
          return;
        }
        if (obj.isStop()) {
          obj.startDown(context);
        } else {
          obj.stopDown();
        }
        recyclerView.notifyDataChanged();
      }
    });

    CroshePopupMenu popupMenu = CroshePopupMenu.newInstance(context);
    popupMenu.addItem("开始下载", new OnCrosheMenuClick() {
      @Override
      public void onClick(CrosheMenuItem item, View view) {
        obj.startDown(context);
        recyclerView.notifyDataChanged();
      }
    });
    popupMenu.addItem("停止下载", new OnCrosheMenuClick() {
      @Override
      public void onClick(CrosheMenuItem item, View view) {
        obj.stopDown();
        recyclerView.notifyDataChanged();
      }
    });
    popupMenu.addItem("打开文件", new OnCrosheMenuClick() {
      @Override
      public void onClick(CrosheMenuItem item, View view) {
        FileUtils.openFile(context, new File(obj.getLocalPath()));
      }
    });
    popupMenu.addItem("删除记录", new OnCrosheMenuClick() {
      @Override
      public void onClick(CrosheMenuItem item, View view) {
        deleteDownUrl(context, obj.getFileId(), false);
        recyclerView.notifyItemRemoved2(obj, crosheViewType);
        toast("删除成功！");
      }
    });
    popupMenu.addItem("彻底删除", new OnCrosheMenuClick() {
      @Override
      public void onClick(CrosheMenuItem item, View view) {
        deleteDownUrl(context, obj.getFileId(), true);
        recyclerView.notifyItemRemoved2(obj, crosheViewType);
        toast("删除成功！");
      }
    });
    if (obj.isDownDone(context)) {
      popupMenu.addItem("本地文件路径", new OnCrosheMenuClick() {
        @Override
        public void onClick(CrosheMenuItem item, View view) {
          toast(obj.getLocalPath());
        }
      });
    }

    popupMenu.bindLongClick(view).setOnCrosheMenuShowListener(new SimpleOnCrosheMenuListener() {
      @Override
      public void onBeforeShow(CroshePopupMenu croshePopupMenu) {
        super.onBeforeShow(croshePopupMenu);

        croshePopupMenu.setItemHidden(2, true);
        if (obj.isStop()) {
          croshePopupMenu.setItemHidden(1, true);
        } else {
          croshePopupMenu.setItemHidden(0, true);
        }

        if (obj.isDownDone(context)) {
          croshePopupMenu.setItemHidden(2, false);
          croshePopupMenu.setItemHidden(1, true);
          croshePopupMenu.setItemHidden(0, true);
        }
      }
    });

  }


  @Subscribe
  public void onEvent(DownEntity downEntity) {
    if (recyclerView.getData().indexOf(downEntity) >= 0) {
      recyclerView.notifyItemChanged(downEntity);
    } else {
      for (DownEntity entity : recyclerView.getData()) {
        if (entity.getUrl().equals(downEntity.getUrl())) {
          entity.update(downEntity);
          entity.setLinkDownEntity(downEntity);
          recyclerView.notifyItemChanged(entity);
          break;
        }
      }
    }
  }


  @Override
  protected void onDestroy() {
    super.onDestroy();
    EventBus.getDefault().unregister(this);
  }


  /**
   * 下载文件
   */
  public static void doDownLoad(Context context, String url) {
    doDownLoad(context, url, DownEntity.getFileName(url));

  }

  /**
   * 下载文件
   */
  public static void doDownLoad(Context context, String url, String fileName) {
    final DownEntity downEntity = new DownEntity();
    downEntity.setUrl(url);
    downEntity.setStop(false);
    downEntity.setFileName(fileName);
    recordDown(downEntity);

    downEntity.startDown(context);
//        Intent intent = new Intent(context, CrosheDownListActivity.class);
//        BaseAppUtils.notify(context, (int) downEntity.getFileId(), "文件下载", "正在下载文件：" + downEntity.getFileName() + "，点击查看下载列表！",
//                BaseAppUtils.getFileIcon(context, downEntity.getLocalPath()), intent);

  }


  /**
   * 记录下载的url
   */
  private static boolean recordDown(DownEntity downEntity) {
    AppCacheEntity cacheEntity = AppCacheHelper.getCache(CacheKey);
    Gson gson = new GsonBuilder().serializeNulls().create();
    List<DownEntity> downList = new ArrayList<>();
    if (cacheEntity != null) {
      Type listType = new TypeToken<ArrayList<DownEntity>>() {
      }.getType();
      downList = gson.fromJson(cacheEntity.getCacheContent(), listType);
    }
    boolean doAdd = true;
    for (DownEntity entity : downList) {
      if (entity.getUrl().equals(downEntity.getUrl())) {
        entity.setStop(downEntity.isStop());
        downEntity.setFileId(entity.getFileId());
        doAdd = false;
        break;
      }
    }
    if (doAdd) {
      downList.add(downEntity);
    }
    AppCacheHelper.setCache(CacheKey, CacheKey, gson.toJson(downList));
    return true;
  }


  /**
   * 删除下载记录
   *
   * @return
   */
  private static boolean deleteDownUrl(Context context, long fileId, boolean deleteLocalFile) {
    AppCacheEntity cacheEntity = AppCacheHelper.getCache(CacheKey);
    Gson gson = new GsonBuilder().serializeNulls().create();
    List<DownEntity> downList = new ArrayList<>();
    if (cacheEntity != null) {
      Type listType = new TypeToken<ArrayList<DownEntity>>() {
      }.getType();
      downList = gson.fromJson(cacheEntity.getCacheContent(), listType);
    }
    DownEntity waitRemove = new DownEntity();
    for (DownEntity downEntity : downList) {
      if (downEntity.getFileId() == fileId) {
        waitRemove = downEntity;
        break;
      }
    }
    downList.remove(waitRemove);
    AppCacheHelper.setCache(CacheKey, CacheKey, gson.toJson(downList));

    if (deleteLocalFile) {
      OKHttpUtils.getInstance().deleteFile(context, waitRemove.getUrl(), waitRemove.getLocalPath());
    }
    return true;
  }


  /**
   * 获得下载url
   *
   * @return
   */
  private static List<DownEntity> getDownList() {
    AppCacheEntity cacheEntity = AppCacheHelper.getCache(CacheKey);
    Gson gson = new GsonBuilder().serializeNulls().create();
    List<DownEntity> downList = new ArrayList<>();
    if (cacheEntity != null) {
      Type listType = new TypeToken<ArrayList<DownEntity>>() {
      }.getType();
      downList = gson.fromJson(cacheEntity.getCacheContent(), listType);
    }
    return downList;
  }

}
