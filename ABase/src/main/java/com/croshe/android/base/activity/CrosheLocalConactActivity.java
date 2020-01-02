package com.croshe.android.base.activity;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.croshe.android.base.R;
import com.croshe.android.base.entity.ContactEntity;
import com.croshe.android.base.listener.OnCrosheLetterListener;
import com.croshe.android.base.listener.PageDataCallBack;
import com.croshe.android.base.utils.BaseAppUtils;
import com.croshe.android.base.utils.DensityUtils;
import com.croshe.android.base.utils.DialogUtils;
import com.croshe.android.base.utils.SelfStrUtils;
import com.croshe.android.base.views.control.CrosheViewHolder;
import com.croshe.android.base.views.list.CrosheLetterRecyclerView;
import com.croshe.android.base.views.list.CrosheViewTypeEnum;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * 本地通讯录管理
 * 安徽创息软件科技有限公司-技术支持，http://wwww.croshe.com
 * Created by Janesen on 2017/7/20 20:58.
 */
public class CrosheLocalConactActivity extends CrosheBaseSlidingActivity implements OnCrosheLetterListener<ContactEntity> {


  private List<ContactEntity> localAllData = new ArrayList<>();

  /**
   * 读取通讯录
   */
  private static final int READ_CONTACT = 1;

  /**
   * 获取库Phon表字段
   **/
  public static final String[] PHONES_PROJECTION = new String[]{
          ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
          ContactsContract.CommonDataKinds.Phone.NUMBER,
          ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
          ContactsContract.CommonDataKinds.Phone._ID,
          ContactsContract.CommonDataKinds.Phone.CONTACT_LAST_UPDATED_TIMESTAMP,
          "sort_key"};


  /**
   * 所有通讯录列表
   */
  private Set<String> phones = new HashSet<>();

  protected CrosheLetterRecyclerView<ContactEntity> crosheLetterRecyclerView;
  private PageDataCallBack<ContactEntity> callBack;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    addPermission("android.permission.READ_CONTACTS");
    checkPermission();

    setContentView(R.layout.android_base_activity_contact);
    crosheLetterRecyclerView = getView(R.id.android_base_crosheLetterView);
    crosheLetterRecyclerView.setOnCrosheLetterListener(this);
    crosheLetterRecyclerView.getRecyclerView().setBottomFinalCount(1);

    crosheLetterRecyclerView.getRecyclerView().addItemDecoration(new ContactItemDecoration());
  }



  Handler handler = new Handler() {
    public void handleMessage(Message msg) {
      if (msg.what == READ_CONTACT) {
        ContactEntity contact = (ContactEntity) msg.obj;
        if (!phones.contains(contact.getContactPhone())) {
          phones.add(contact.getContactPhone());
          localAllData.add(contact);
          if (callBack != null) {
            callBack.appendData(contact);
          }
        }
      }
    }
  };



  /**
   * 获得手机通讯录联系人信息
   */
  private  void getPhoneContacts() {
    try {
      Log.d("STAG", "getPhoneContacts");

      ContentResolver resolver = context.getContentResolver();
      // 获取手机联系人
      Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PHONES_PROJECTION,
              null, null,
              "sort_key");
      if (phoneCursor != null) {
        int contactCount = 0;
        while (phoneCursor.moveToNext()) {
          String phoneNumber = phoneCursor.getString(1).replace(" ", "").replace("-", "").replace("+86", "");
          if (TextUtils.isEmpty(phoneNumber) || !SelfStrUtils.isPhoneLegal(phoneNumber)) {
            continue;
          }

          String contactName = phoneCursor.getString(0);

          int idIndex = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);
          int sortKeyIndex = phoneCursor.getColumnIndex("sort_key");

          String sortKey = phoneCursor.getString(sortKeyIndex);


          ContactEntity contact = new ContactEntity();
          contact.setContactName(contactName);
          contact.setAllLetter(SelfStrUtils.convertToPinyin(contactName).toLowerCase());
          contact.setAllFirstLetter(SelfStrUtils.convertToFirstPinyin(contactName).toLowerCase());
          contact.setSortKey(getSortKey(sortKey));
          contact.setSortKeyBySys(sortKey);
          contact.setContactPhone(phoneNumber);
          contact.setContactId(phoneCursor.getInt(idIndex));

          Message message = new Message();
          message.obj = contact;
          message.what = READ_CONTACT;
          handler.sendMessage(message);
          contactCount++;
        }

        if (contactCount == 0) {
          DialogUtils.confirm(context, "系统提醒",
                  "未获取到您的好友信息，可能原因是程序未获得通讯录权限，您可在设置-应用-" + BaseAppUtils.getApplicationName(context) + "-权限中开启通讯录权限，以正常使用含有通讯录的功能！",
                  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                      Uri packageURI = Uri.parse("package:" + getPackageName());
                      Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                      startActivity(intent);
                    }
                  }, new Runnable() {
                    @Override
                    public void run() {
                      CrosheLocalConactActivity.this.finish();
                    }
                  });
        }

        phoneCursor.close();
      } else {
        DialogUtils.confirm(context, "系统提醒", "在设置-应用-" + BaseAppUtils.getApplicationName(context) + "-权限中开启通讯录权限，以正常使用含有通讯录的功能！", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            Uri packageURI = Uri.parse("package:" + getPackageName());
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
            startActivity(intent);
          }
        });
      }
    } catch (SecurityException e) {
      DialogUtils.confirm(context, "系统提醒", "在设置-应用-" + BaseAppUtils.getApplicationName(context) + "-权限中开启通讯录权限，以正常使用含有通讯录的功能！", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
          Uri packageURI = Uri.parse("package:" + getPackageName());
          Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
          startActivity(intent);
        }
      });
    }
  }


  private static String getSortKey(String sortKeyString) {
    String key = sortKeyString.substring(0, 1).toUpperCase();
    if (key.matches("[A-Z]")) {
      return key;
    }
    String nextSort = SelfStrUtils.convertToFirstPinyin(sortKeyString);
    if (StringUtils.isNotEmpty(nextSort)) {
      nextSort = nextSort.substring(0, 1).toUpperCase();
      if (nextSort.matches("[A-Z]")) {
        return nextSort;
      }
    }
    return "#";
  }




  @Override
  public boolean onCreateOptionsMenu(final Menu menu) {
    getMenuInflater().inflate(R.menu.android_base_menu_friend, menu);

    final SearchView searchView = (SearchView) menu.findItem(R.id.android_base_contact_search).getActionView();
    searchView.setQueryHint("输入手机号码/昵称/字母拼音…");
    searchView.setMaxWidth((int) DensityUtils.getWidthInPx());

    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
      @Override
      public boolean onQueryTextSubmit(String s) {
        searchContact(s);
        return false;
      }

      @Override
      public boolean onQueryTextChange(String s) {
        searchContact(s);
        return false;
      }
    });
    searchView.setOnSearchClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        setTitle("搜索");

      }
    });
    searchView.setOnCloseListener(new SearchView.OnCloseListener() {
      @Override
      public boolean onClose() {
        setTitle("本地通讯录");
        searchContact("");
        return false;
      }
    });

    return super.onCreateOptionsMenu(menu);
  }


  /**
   * 搜素
   * @param searchKey
   */
  private void searchContact(String searchKey) {
    List<ContactEntity> searched = new ArrayList<>();
    for (ContactEntity contactEntity : localAllData) {
      String contactName = contactEntity.getContactName().trim();
      String allLetter = contactEntity.getAllLetter();
      String allFirstLetter = contactEntity.getAllFirstLetter();
      String phoneNumber = contactEntity.getContactPhone();
      if (StringUtils.isEmpty(searchKey)) {
        searched.add(contactEntity);
        continue;
      }

      if (contactName.contains(searchKey)
              || allLetter.contains(searchKey)
              || phoneNumber.startsWith(searchKey)
              || allFirstLetter.startsWith(searchKey)) {
        searched.add(contactEntity);
      }
    }
    callBack.loadData(1, searched);
  }


  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    return super.onOptionsItemSelected(item);
  }



  @Override
  public void getData(int page, PageDataCallBack<ContactEntity> callBack) {
    this.callBack = callBack;
    getPhoneContacts();
    callBack.loadDone();
  }

  @Override
  public int getItemViewLayout(ContactEntity obj, int position, int crosheViewType) {
    if (crosheViewType == CrosheViewTypeEnum.LetterView.ordinal()) {
      return  R.layout.android_base_item_letter;
    }else  if (crosheViewType == CrosheViewTypeEnum.FinalBottomView.ordinal()) {
      return  R.layout.android_base_item_bottom_info;
    }

    return R.layout.android_base_item_contact;
  }

  @Override
  public void onRenderView(final ContactEntity obj, int position, int crosheViewType, CrosheViewHolder holder) {
    if (crosheViewType == CrosheViewTypeEnum.LetterView.ordinal()) {
      holder.setTextView(R.id.android_base_tvLetter, obj.getSortKey());
      return;
    }else  if (crosheViewType == CrosheViewTypeEnum.FinalBottomView.ordinal()) {
      holder.setTextView(R.id.android_base_tvInfo, "共" + crosheLetterRecyclerView.getDataCount() + "位好友");
      return;
    }
    holder.setTextView(R.id.android_base_tvTitle, obj.getContactName());
    holder.setTextView(R.id.android_base_tvSubtitle, obj.getContactPhone());

    holder.onClick(R.id.llItem, new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        EventBus.getDefault().post(obj);
        finish();
      }
    });
  }

  @Override
  public String getLetter(ContactEntity obj) {
    return obj.getSortKey();
  }




  public class ContactItemDecoration extends RecyclerView.ItemDecoration {

    @Override
    public void getItemOffsets(final Rect outRect, final View view, final RecyclerView parent, RecyclerView.State state) {
      final int position = parent.getChildAdapterPosition(view);

      outRect.left = 0;
      outRect.right = 0;
      outRect.top = 0;
      outRect.bottom = 0;
      if (position != 0) {
        outRect.top = DensityUtils.dip2px(0.5f);
      }
    }

  }
}
