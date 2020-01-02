package com.croshe.android.base.extend.content;

import android.content.Context;
import android.content.Intent;
import android.text.style.ClickableSpan;
import android.view.View;

import com.croshe.android.base.activity.CrosheBrowserActivity;
import com.croshe.android.base.views.layout.CrosheTouchListenerFrameLayout;

import java.net.URL;

/**
 * 安徽创息软件科技有限公司-技术支持，http://wwww.croshe.com
 * Created by Janesen on 2017/7/20 13:23.
 */
public class CrosheUrlSpan extends ClickableSpan {

    private Context context;
    private String url;

    public CrosheUrlSpan(Context context, String url) {
        this.context = context;
        this.url = url;
    }

    @Override
    public void onClick(View widget) {
        Intent intent = new Intent(context, CrosheBrowserActivity.class);
        intent.putExtra(CrosheBrowserActivity.EXTRA_URL, url);
        context.startActivity(intent);
    }


}
