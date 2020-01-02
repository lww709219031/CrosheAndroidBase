package com.croshe.android.base.extend.content;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Janesen on 2017/6/25.
 */

public class CrosheIntent extends Intent {

    private Class<?> cls;
    private Activity activity;

    private CrosheIntent() {

    }

    public static CrosheIntent newInstance(Activity activity) {
        CrosheIntent crosheIntent = new CrosheIntent();
        crosheIntent.activity = activity;
        return crosheIntent;
    }

    public CrosheIntent getActivity(Class<?> cls) {
        this.cls = cls;
        return this;
    }


    @Override
    public CrosheIntent putExtra(String name, boolean value) {
        super.putExtra(name, value);
        return this;
    }

    @Override
    public CrosheIntent putExtra(String name, byte value) {
        super.putExtra(name, value);
        return this;
    }

    @Override
    public CrosheIntent putExtra(String name, char value) {
        super.putExtra(name, value);
        return this;
    }

    @Override
    public CrosheIntent putExtra(String name, short value) {
        super.putExtra(name, value);
        return this;
    }

    @Override
    public CrosheIntent putExtra(String name, int value) {
        super.putExtra(name, value);
        return this;
    }

    @Override
    public CrosheIntent putExtra(String name, long value) {
        super.putExtra(name, value);
        return this;
    }

    @Override
    public CrosheIntent putExtra(String name, float value) {
        super.putExtra(name, value);
        return this;
    }

    @Override
    public CrosheIntent putExtra(String name, double value) {
        super.putExtra(name, value);
        return this;
    }

    @Override
    public CrosheIntent putExtra(String name, String value) {
        super.putExtra(name, value);
        return this;
    }

    @Override
    public CrosheIntent putExtra(String name, CharSequence value) {
        super.putExtra(name, value);
        return this;
    }

    @Override
    public CrosheIntent putExtra(String name, Parcelable value) {
        super.putExtra(name, value);
        return this;
    }

    @Override
    public CrosheIntent putExtra(String name, Parcelable[] value) {
        super.putExtra(name, value);
        return this;
    }

    @Override
    public CrosheIntent putParcelableArrayListExtra(String name, ArrayList<? extends Parcelable> value) {
        super.putParcelableArrayListExtra(name, value);
        return this;
    }

    @Override
    public CrosheIntent putIntegerArrayListExtra(String name, ArrayList<Integer> value) {
        super.putIntegerArrayListExtra(name, value);
        return this;
    }

    @Override
    public CrosheIntent putStringArrayListExtra(String name, ArrayList<String> value) {
        super.putStringArrayListExtra(name, value);
        return this;
    }

    @Override
    public CrosheIntent putCharSequenceArrayListExtra(String name, ArrayList<CharSequence> value) {
        super.putCharSequenceArrayListExtra(name, value);
        return this;
    }

    @Override
    public CrosheIntent putExtra(String name, Serializable value) {
        super.putExtra(name, value);
        return this;
    }

    @Override
    public CrosheIntent putExtra(String name, boolean[] value) {
        super.putExtra(name, value);
        return this;
    }

    @Override
    public CrosheIntent putExtra(String name, byte[] value) {
        super.putExtra(name, value);
        return this;
    }

    @Override
    public CrosheIntent putExtra(String name, short[] value) {
        super.putExtra(name, value);
        return this;
    }

    @Override
    public CrosheIntent putExtra(String name, char[] value) {
        super.putExtra(name, value);
        return this;
    }

    @Override
    public CrosheIntent putExtra(String name, int[] value) {
        super.putExtra(name, value);
        return this;
    }

    @Override
    public CrosheIntent putExtra(String name, long[] value) {
        super.putExtra(name, value);
        return this;
    }

    @Override
    public CrosheIntent putExtra(String name, float[] value) {
        super.putExtra(name, value);
        return this;
    }

    @Override
    public CrosheIntent putExtra(String name, double[] value) {
        super.putExtra(name, value);
        return this;
    }


    @Override
    public CrosheIntent putExtra(String name, String[] value) {
        super.putExtra(name, value);
        return this;
    }

    @Override
    public CrosheIntent putExtra(String name, CharSequence[] value) {
        super.putExtra(name, value);
        return this;
    }

    @Override
    public CrosheIntent putExtra(String name, Bundle value) {
        super.putExtra(name, value);
        return this;
    }

    @Override
    public CrosheIntent putExtras(Intent src) {
        super.putExtras(src);
        return this;
    }

    @Override
    public CrosheIntent putExtras(Bundle extras) {
        super.putExtras(extras);
        return this;
    }

    public void startActivity() {
        if (cls == null) {
            throw new RuntimeException("请先调用getActivity()方法后在调用此方法！");
        }

        Intent intent = new Intent(activity, cls);
        if (this.getExtras() != null) {
            intent.putExtras(this.getExtras());
        }
        activity.startActivity(intent);
        cls = null;
    }

    public void startActivityForResult(int requestCode) {
        if (cls == null) {
            throw new RuntimeException("请先调用getActivity()方法后在调用此方法！");
        }
        Intent intent = new Intent(activity, cls);
        if (this.getExtras() != null) {
            intent.putExtras(this.getExtras());
        }
        activity.startActivityForResult(intent, requestCode);
        cls = null;
    }

}
