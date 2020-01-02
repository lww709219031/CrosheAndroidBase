package com.croshe.android.base.views.control;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;

import com.croshe.android.base.AConfig;
import com.croshe.android.base.utils.DensityUtils;

/**
 * 安徽创息软件科技有限公司-技术支持，http://wwww.croshe.com
 * Created by Janesen on 2017/9/22 14:36.
 */
public class CrosheEditText extends AppCompatEditText {
    public CrosheEditText(Context context) {
        super(context);
    }

    public CrosheEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CrosheEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTextContextMenuItem(int id) {
        boolean result=super.onTextContextMenuItem(id);
        if (id == android.R.id.paste) {
            refreshText();
        }
        return result;
    }


    public void refreshText() {
        try {
            int endSelection = this.getSelectionEnd();
            this.setText(AConfig.getOnFormatContentListener().formatFaceContent(this.getText(), DensityUtils.dip2px(24)));
            this.setSelection(Math.min(Math.max(endSelection, 0), length()));
        } catch (Exception e) {}
    }


}
