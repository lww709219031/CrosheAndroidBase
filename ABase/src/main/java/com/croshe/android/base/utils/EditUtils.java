package com.croshe.android.base.utils;

import android.app.Service;
import android.content.Context;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Janesen on 16/7/7.
 */
public class EditUtils {

    /**
     * 判空，若为空，显示message
     *
     * @param message
     */
    public static Object checkNull(Object obj, String message, Context context) {
        if (obj instanceof EditText) {
            EditText et = (EditText) obj;
            if (et.getText() == null || et.getText().toString().length() == 0) {
                Vibrator vb = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
                vb.vibrate(300);
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                et.requestFocus();
                ViewUtils.openKeyboard(et);
                return null;
            }
            return et.getText().toString();
        }

        if (obj instanceof TextView) {
            TextView et = (TextView) obj;
            if (et.getText() == null || et.getText().toString().length() == 0) {
                Vibrator vb = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
                vb.vibrate(300);
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                return null;
            }
            return et.getText().toString();
        }

        if (obj == null) {
            Vibrator vb = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
            vb.vibrate(300);
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            return null;
        }
        return obj;
    }



    public static void inputEdit(EditText editText, String content) {
        if (editText == null || content == null) {
            return;
        }

        int start = editText.getSelectionStart();
        int end = editText.getSelectionEnd();
        if (start < 0) {
            editText.append(content);
        } else {
            editText.getText().replace(Math.min(start, end), Math.max(start, end), content, 0, content.length());
        }
    }

    public static void deleteEdit(EditText editText) {
        KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL);
        editText.onKeyDown(KeyEvent.KEYCODE_DEL, event);
    }


    /**
     * 获得编辑的框的值
     *
     * @param edit
     * @return
     */
    public static String getEditOrTextViewValue(View edit) {
        if (edit instanceof EditText) {
            EditText et = (EditText) edit;
            if (et.getText() != null) {
                return et.getText().toString();
            }
        }

        if (edit instanceof TextView) {
            TextView et = (TextView) edit;
            if (et.getText() != null) {
                return et.getText().toString();
            }
        }
        return "";
    }



    /**
     * 设置编辑的框的值
     * @param edit
     * @return
     */
    public static void setEditOrTextViewValue(View edit,Object value) {
        if (edit instanceof EditText) {
            EditText et = (EditText) edit;
            if (value != null) {
                et.setText(value.toString());
            }
        }

        if (edit instanceof TextView) {
            TextView et = (TextView) edit;
            if (value != null) {
                et.setText(value.toString());
            }
        }
    }


    /**
     * 判断编辑框是否赋值或编辑了
     * @param edit
     * @return
     */
    public static boolean isEdited(View edit) {
        if (edit instanceof EditText) {
            EditText et = (EditText) edit;
            return et.length() > 0;
        }

        if (edit instanceof TextView) {
            TextView et = (TextView) edit;
            return et.length() > 0;
        }
        return false;
    }


}
