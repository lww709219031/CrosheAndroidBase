package com.croshe.android.base.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.croshe.android.base.R;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SaturationBar;
import com.larswerkman.holocolorpicker.ValueBar;

import java.util.ArrayList;
import java.util.List;

public class DialogUtils {

    private static Handler handler = new Handler(Looper.getMainLooper());

    public static AlertDialog.Builder alert(Context context, String title, String message) {
        return alert(context, title, message, null);
    }

    public static AlertDialog.Builder alert(Context context, String title, String message, OnClickListener confirm) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (title != null) {
            builder.setTitle(title);
        }
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setNegativeButton("确定", confirm);
        builder.show();
        return builder;
    }


    public static AlertDialog confirm(Context context, String title, String message, OnClickListener confirm) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("确定", confirm);
        builder.setNegativeButton("取消", null);
        builder.show();
        return builder.create();
    }

    public static AlertDialog confirm(Context context, String title, String message, OnClickListener confirm, OnClickListener cancel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("确定", confirm);
        builder.setNegativeButton("取消", cancel);
        builder.show();
        return builder.create();
    }

    public static AlertDialog confirm(Context context, String title, String message, OnClickListener confirm, final Runnable cancel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("确定", confirm);
        builder.setNegativeButton("取消", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (cancel != null) {
                    cancel.run();
                }
            }
        });
        builder.setCancelable(false);
        builder.show();
        return builder.create();
    }

    public static void prompt(Context context, String title, String hint, final OnPromptCallBack promptCallBack) {
        prompt(context, title, -1, null, hint, -1,  -Integer.MAX_VALUE, Integer.MAX_VALUE, promptCallBack);
    }

    public static void prompt(Context context, int inputType, String title, String hint, final OnPromptCallBack promptCallBack) {
        prompt(context, title, inputType, null, hint, -1, -Integer.MAX_VALUE, Integer.MAX_VALUE, promptCallBack);
    }

    public static void prompt(Context context, int inputType, String title, String hint, int maxLength, final OnPromptCallBack promptCallBack) {
        prompt(context, title, inputType, null, hint, maxLength, -Integer.MAX_VALUE, Integer.MAX_VALUE, promptCallBack);
    }

    public static void prompt(Context context, int inputType, String title, String hint, double maxValue, final OnPromptCallBack promptCallBack) {
        prompt(context, title, inputType, null, hint, -1, -Integer.MAX_VALUE, maxValue, promptCallBack);
    }

    public static void prompt(Context context, int inputType, String title, String hint, double minValue, double maxValue, final OnPromptCallBack promptCallBack) {
        prompt(context, title, inputType, null, hint, -1, minValue, maxValue, promptCallBack);
    }

    public static void prompt(Context context, String title, String hint, int maxLength, final OnPromptCallBack promptCallBack) {
        prompt(context, title, -1, null, hint, maxLength,  -Integer.MAX_VALUE, Integer.MAX_VALUE, promptCallBack);
    }

    public static void prompt(Context context, String title, int maxLength, final OnPromptCallBack promptCallBack) {
        prompt(context, title, -1, null, null, maxLength,  -Integer.MAX_VALUE, Integer.MAX_VALUE, promptCallBack);
    }

    public static void prompt(Context context, String title,String defaultValue, String hint, final OnPromptCallBack promptCallBack) {
        prompt(context, title, -1, defaultValue, hint, -1,  -Integer.MAX_VALUE, Integer.MAX_VALUE, promptCallBack);
    }

    public static void prompt(final Context context, final String title, final int inputType, String defaultValue, String hint, final int maxLength, final double minValue, final double maxValue, final OnPromptCallBack promptCallBack) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);

        final FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        frameLayout.setPadding(DensityUtils.dip2px(20), DensityUtils.dip2px(20), DensityUtils.dip2px(20), 0);

        final EditText editText = new EditText(context);
        editText.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, DensityUtils.dip2px(LayoutParams.WRAP_CONTENT)));
        editText.setText(defaultValue);
        editText.setMaxLines(3);
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editText.requestFocus();
        if (inputType != -1) {
            editText.setInputType(inputType);
        }
        editText.setHint("请输入…");
        if (hint != null) {
            editText.setHint(hint);
        }

        final TextView tvCount = new TextView(context);

        if (maxLength > 0) {
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
            tvCount.setText("0/" + maxLength);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
            layoutParams.setMargins(0, 0, DensityUtils.dip2px(10), 0);
            tvCount.setLayoutParams(layoutParams);
            frameLayout.addView(tvCount);
        }


        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                tvCount.setText(s.length() + "/" + maxLength);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });

        frameLayout.addView(editText);
        builder.setView(frameLayout);
        builder.setNegativeButton("取消", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                if (promptCallBack != null) {
                    promptCallBack.promptResult(false, null);
                }
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editText.getApplicationWindowToken(), 0);
            }
        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(((Activity) context).getWindow().getDecorView().getWindowToken(), 0);
                    }
                }, 100);
            }
        });
        builder.setPositiveButton("确定", null);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (inputType == -2) {//判断汉字
                        String text = editText.getText().toString();
                        if (!text.equals(text.replaceAll("[\\u4e00-\\u9fa5]", ""))) {
                            EditUtils.checkNull(null, "不可包含汉字！", editText.getContext());
                            return true;
                        }
                    }

                    if (inputType == (InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL)
                            || inputType == InputType.TYPE_CLASS_NUMBER) {
                        String text = editText.getText().toString();
                        if (NumberUtils.formatToDouble(text) < minValue) {
                            EditUtils.checkNull(null, "数值不可小于" + minValue, editText.getContext());
                            return true;
                        }
                        if (NumberUtils.formatToDouble(text) > maxValue) {
                            EditUtils.checkNull(null, "数值不可大于" + maxValue, editText.getContext());
                            return true;
                        }

                    }
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editText.getApplicationWindowToken(), 0);
                    alertDialog.dismiss();
                    if (promptCallBack != null) {
                        if (editText.getText() != null && editText.getText().length() != 0) {
                            promptCallBack.promptResult(true, editText.getText().toString());
                        } else {
                            promptCallBack.promptResult(false, null);
                        }
                    }
                    return true;
                }
                return false;
            }
        });
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inputType == -2) {//判断汉字
                    String text = editText.getText().toString();
                    if (!text.equals(text.replaceAll("[\\u4e00-\\u9fa5]", ""))) {
                        EditUtils.checkNull(null, "不可包含汉字！", editText.getContext());
                        return;
                    }
                }
                if (inputType == (InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL)) {
                    String text = editText.getText().toString();
                    if (NumberUtils.formatToDouble(text) < minValue) {
                        EditUtils.checkNull(null, "数值不可小于" + minValue, editText.getContext());
                        return;
                    }
                    if (NumberUtils.formatToDouble(text) > maxValue) {
                        EditUtils.checkNull(null, "数值不可大于" + maxValue, editText.getContext());
                        return;
                    }
                }


                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editText.getApplicationWindowToken(), 0);
                alertDialog.dismiss();
                if (promptCallBack != null) {
                    if (editText.getText() != null && editText.getText().length() != 0) {
                        promptCallBack.promptResult(true, editText.getText().toString());
                    } else {
                        promptCallBack.promptResult(false, null);
                    }
                }

            }
        });

        Handler mHandler = new Handler(Looper.getMainLooper());
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
            }
        }, 100);
    }


    /**
     * 显示选择项
     *
     * @param context
     * @param title
     * @param options
     * @param defaultIndex
     * @param onCheckCallBack
     */
    public static void checkItems(Context context, String title, final String[] options, int defaultIndex, final OnCheckCallBack onCheckCallBack) {

        final int[] whichValue = new int[]{0};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setNegativeButton("取消", null);
        builder.setSingleChoiceItems(options, defaultIndex, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                whichValue[0] = which;
            }
        });
        builder.setPositiveButton("确定", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                if (onCheckCallBack != null) {
                    onCheckCallBack.onCheck(options[whichValue[0]], whichValue[0]);
                }
            }
        });
        builder.show();
    }


    /**
     * 显示选择项
     *
     * @param context
     * @param title
     * @param options
     */
    public static void checkMultiItems(Context context, String title, final String[] options, final OnCheckMultiCallBack onCheckMultiCallBack) {

        final boolean[] whichValue = new boolean[options.length];

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setNegativeButton("取消", null);

        builder.setMultiChoiceItems(options, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                whichValue[i] = b;
            }
        });

        builder.setPositiveButton("确定", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                if (onCheckMultiCallBack != null) {
                    List<String> values = new ArrayList<>();
                    List<Integer> valueIndex = new ArrayList<>();
                    for (int i = 0; i < whichValue.length; i++) {
                        if (whichValue[i]) {
                            values.add(options[i]);
                            valueIndex.add(i);
                        }
                    }
                    onCheckMultiCallBack.onCheck(values.toArray(new String[]{}), valueIndex.toArray(new Integer[]{}));
                }
            }
        });
        builder.show();
    }



    /**
     * 选择颜色
     * @param context
     * @param title
     * @param onSelectColor
     */
    public static void selectColor(Context context, String title, final OnSelectColor onSelectColor) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        View view = LayoutInflater.from(context).inflate(R.layout.android_base_panel_color, null);


        final ColorPicker colorPicker = view.findViewById(R.id.android_base_colorPicker);

        OpacityBar opacityBar = view.findViewById(R.id.android_base_opacityBar);

        SaturationBar saturationBar = view.findViewById(R.id.android_base_saturationBar);
        ValueBar valueBar = view.findViewById(R.id.android_base_valueBar);

        colorPicker.setShowOldCenterColor(false);
        if (onSelectColor != null) {
            colorPicker.setColor(onSelectColor.getDefaultColor());
            colorPicker.setOldCenterColor(onSelectColor.getDefaultColor());
        }

        colorPicker.addOpacityBar(opacityBar);
        colorPicker.addSaturationBar(saturationBar);
        colorPicker.addValueBar(valueBar);

        colorPicker.setOnColorChangedListener(new ColorPicker.OnColorChangedListener() {
            @Override
            public void onColorChanged(int color) {
                if (onSelectColor != null) {
                    onSelectColor.onChangeColor(color);
                }
            }
        });
        builder.setView(view);

        builder.setNegativeButton("取消", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (onSelectColor != null) {
                    onSelectColor.onSelectColor(onSelectColor.getDefaultColor());
                }
            }
        });

        builder.setPositiveButton("确定", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                if (onSelectColor != null) {
                    onSelectColor.onSelectColor(colorPicker.getColor());
                }
            }
        });
        builder.show();
    }




    public interface OnPromptCallBack {
        void promptResult(boolean success, String text);
    }

    public interface OnCheckCallBack {
        void onCheck(String value, int which);
    }

    public interface OnCheckMultiCallBack {
        void onCheck(String[] value, Integer[] which);
    }

    public interface OnSelectColor{
        int getDefaultColor();

        void onSelectColor(int color);

        void onChangeColor(int color);
    }

    public static class InputFilterMinMax implements InputFilter {

        private double min, max;

        public InputFilterMinMax(double min, double max) {
            this.min = min;
            this.max = max;
        }

        public InputFilterMinMax(String min, String max) {
            this.min = Integer.parseInt(min);
            this.max = Integer.parseInt(max);
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            try {
                double input = Double.parseDouble(dest.toString() + source.toString());
                if (input > max) {
                    return String.valueOf(max);
                }

                if (input < min) {
                    return String.valueOf(min);
                }
            } catch (NumberFormatException nfe) {
            }
            return "";
        }
    }
}
