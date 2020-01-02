package com.croshe.android.base.extend.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.ViewSwitcher;

import com.croshe.android.base.R;

import java.util.Calendar;

@SuppressLint("NewApi")
public class CrosheDateTimeDialog {

    public static Dialog selectDateTime(Context context, String title, OnDateTimeConfirm dateTimeConfirm) {
        if (context == null) {
            return null;
        }
        return createDialog(context, -1, title, 2, -1, dateTimeConfirm);
    }


    public static Dialog selectDateTime(Context context, String title, long minDate, OnDateTimeConfirm dateTimeConfirm) {
        if (context == null) {
            return null;
        }
        return createDialog(context, -1, title, 2, minDate, dateTimeConfirm);
    }


    public static Dialog selectDate(Context context, String title, OnDateTimeConfirm dateTimeConfirm) {
        if (context == null) {
            return null;
        }
        return createDialog(context, -1, title, 0, -1, dateTimeConfirm);
    }

    public static Dialog selectDate(Context context, String title, long minDate, OnDateTimeConfirm dateTimeConfirm) {
        if (context == null) {
            return null;
        }
        return createDialog(context, -1, title, 0, minDate, dateTimeConfirm);
    }

    public static Dialog selectDate(Context context, String title, long maxDate, long minDate, OnDateTimeConfirm dateTimeConfirm) {
        if (context == null) {
            return null;
        }
        return createDialog(context, -1, title, 0, minDate, maxDate, -1, dateTimeConfirm);
    }


    public static Dialog selectTime(Context context, String title, OnDateTimeConfirm dateTimeConfirm) {
        if (context == null) {
            return null;
        }
        return createDialog(context, -1, title, 1, -1, dateTimeConfirm);
    }

    public static Dialog selectTime(Context context, String title, long defaultMillis, OnDateTimeConfirm dateTimeConfirm) {
        if (context == null) {
            return null;
        }
        return createDialog(context, -1, title, 1, -1, -1, defaultMillis, dateTimeConfirm);
    }


    /**
     * 选择范围
     *
     * @return
     */
    public static Dialog selectRangeDate(Context context, String title, OnRangeDateTimeConfirm dateTimeConfirm) {
        if (context == null) {
            return null;
        }
        return createDialog(context, -1, title, 0, -1, -1, -1, true, dateTimeConfirm);
    }
    /**
     * 选择范围
     *
     * @return
     */
    public static Dialog selectRangeTime(Context context, String title, OnRangeDateTimeConfirm dateTimeConfirm) {
        if (context == null) {
            return null;
        }
        return createDialog(context, -1, title, 1, -1, -1, -1, true, dateTimeConfirm);
    }



    private static Dialog createDialog(final Context context, int icon, String title, int type,
                                       long minDate,
                                       final OnDateTimeConfirm dateTimeConfirm) {

        return createDialog(context, icon, title, type, minDate, -1, -1, dateTimeConfirm);

    }

    private static Dialog createDialog(final Context context, int icon, String title, int type,
                                       long minDate,
                                       long maxDate,
                                       long defaultDate,
                                       final OnDateTimeConfirm dateTimeConfirm) {
        return createDialog(context, icon, title, type, minDate, maxDate, defaultDate, false, dateTimeConfirm);
    }
    private static Dialog createDialog(final Context context,
                                       int icon, String title, int type,
                                       long minDate,
                                       long maxDate,
                                       long defaultDate,
                                       boolean isRange,
                                       final OnDateTimeConfirm dateTimeConfirm) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final Calendar startCal = Calendar.getInstance();
        final Calendar endCal = Calendar.getInstance();

        builder.setTitle(title);
        builder.setNegativeButton("取消", null);

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dateTimeConfirm != null) {
                    if (dateTimeConfirm instanceof OnRangeDateTimeConfirm) {
                        OnRangeDateTimeConfirm rangeDateTimeConfirm = (OnRangeDateTimeConfirm) dateTimeConfirm;
                        rangeDateTimeConfirm.selectedRangeDateTime(startCal, endCal);
                    }else{
                        dateTimeConfirm.selectedDateTime(startCal);
                    }
                }
            }
        });

        View contentView;

        if (isRange) {
            contentView = LayoutInflater.from(context).inflate(R.layout.android_base_view_date_range, null);

            final TextView android_base_start = contentView.findViewById(R.id.android_base_start);
            final TextView android_base_end = contentView.findViewById(R.id.android_base_end);
            final FrameLayout rangeContainer = contentView.findViewById(R.id.android_base_range_container);



            final View start = getView(context, type, minDate, maxDate, defaultDate, startCal);
            start.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

            final View end = getView(context, type, minDate, maxDate, defaultDate, endCal);
            end.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

            rangeContainer.addView(start);
            rangeContainer.addView(end);

            android_base_start.setBackgroundColor(Color.parseColor("#f7f7f7"));
            android_base_start.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    end.setVisibility(View.GONE);
                    start.setVisibility(View.VISIBLE);
                    start.bringToFront();
                    android_base_start.setBackgroundColor(Color.parseColor("#f7f7f7"));
                    TypedValue outValue = new TypedValue();
                    context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
                    android_base_end.setBackgroundResource(outValue.resourceId);
                }
            });

            android_base_end.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    start.setVisibility(View.GONE);
                    end.setVisibility(View.VISIBLE);
                    end.bringToFront();
                    android_base_end.setBackgroundColor(Color.parseColor("#f7f7f7"));
                    TypedValue outValue = new TypedValue();
                    context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
                    android_base_start.setBackgroundResource(outValue.resourceId);
                }
            });


        }else{
            contentView= getView(context, type, minDate, maxDate, defaultDate, startCal);
        }


        builder.setView(contentView);
        return builder.show();
    }

    @NonNull
    private static View getView(final Context context, int type, long minDate, long maxDate, long defaultDate, final Calendar calendar) {
        final View datetime = LayoutInflater.from(context).inflate(R.layout.android_base_view_datetime, null);
        final TextView android_base_time = datetime.findViewById(R.id.android_base_time);
        final TextView android_base_date = datetime.findViewById(R.id.android_base_date);
        android_base_date.setBackgroundColor(Color.parseColor("#f7f7f7"));


        final ViewSwitcher viewSwitcher = datetime.findViewById(R.id.viewSwitcher);

        android_base_date.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewSwitcher.getCurrentView().getId() != R.id.android_base_datePicker) {
                    viewSwitcher.showNext();
                    android_base_date.setBackgroundColor(Color.parseColor("#f7f7f7"));
                    TypedValue outValue = new TypedValue();
                    context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
                    android_base_time.setBackgroundResource(outValue.resourceId);
                }
            }
        });

        android_base_time.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewSwitcher.getCurrentView().getId() != R.id.android_base_timePicker) {
                    viewSwitcher.showNext();
                    android_base_time.setBackgroundColor(Color.parseColor("#f7f7f7"));
                    TypedValue outValue = new TypedValue();
                    context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
                    android_base_date.setBackgroundResource(outValue.resourceId);
                }
            }
        });

        if (type == 1) {
            if (viewSwitcher.getCurrentView().getId() != R.id.android_base_timePicker) {
                viewSwitcher.showNext();
            }
        }


        final DatePicker datePicker = datetime.findViewById(R.id.android_base_datePicker);
        if (minDate > 0) {
            datePicker.setMinDate(minDate);
        }
        if (maxDate > 0) {
            datePicker.setMaxDate(maxDate);
        }
        if (defaultDate > 0) {
            calendar.setTimeInMillis(defaultDate);
        }
        if (type == 0 || type == 2) {
            int year = calendar.get(Calendar.YEAR);
            int monthOfYear = calendar.get(Calendar.MONTH);
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

            android_base_date.setText(year + "年" + (monthOfYear + 1) + "月" + dayOfMonth + "日");
            datePicker.init(year, monthOfYear, dayOfMonth, new OnDateChangedListener() {

                public void onDateChanged(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                    android_base_date.setText(year + "年" + (monthOfYear + 1) + "月" + dayOfMonth + "日");
                    calendar.set(year, monthOfYear, dayOfMonth);
                }
            });

            android_base_date.setVisibility(View.VISIBLE);
        } else {
            android_base_date.setVisibility(View.GONE);
            datePicker.setVisibility(View.GONE);
        }

        TimePicker timePicker = datetime.findViewById(R.id.android_base_timePicker);
        if (type == 1 || type == 2) {
            android_base_time.setVisibility(View.VISIBLE);
            timePicker.setIs24HourView(true);
            int h = calendar.get(Calendar.HOUR_OF_DAY);
            int m = calendar.get(Calendar.MINUTE);
            timePicker.setCurrentHour(h);
            timePicker.setCurrentMinute(m);

            android_base_time.setText(String.format("%02d", h) + "时" + String.format("%02d", m) + "分");
            timePicker.setOnTimeChangedListener(new OnTimeChangedListener() {

                @Override
                public void onTimeChanged(TimePicker arg0, int h, int m) {
                    // TODO Auto-generated method stub
                    android_base_time.setText(String.format("%02d", h) + "时" + String.format("%02d", m) + "分");
                    calendar.set(Calendar.HOUR_OF_DAY, h);
                    calendar.set(Calendar.MINUTE, m);

                }
            });

        } else {
            android_base_time.setVisibility(View.GONE);
            timePicker.setVisibility(View.GONE);
        }
        return datetime;
    }


    public interface OnDateTimeConfirm {
        void selectedDateTime(Calendar calendar);
    }

    public static abstract class OnRangeDateTimeConfirm implements OnDateTimeConfirm {
        @Override
        public void selectedDateTime(Calendar calendar) {}

        public  abstract void selectedRangeDateTime(Calendar start, Calendar end);
    }

}
