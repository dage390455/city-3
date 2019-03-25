package com.sensoro.smartcity.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.adapter.model.MonitoringPointRcContentAdapterModel;
import com.sensoro.smartcity.model.Elect3DetailModel;
import com.sensoro.smartcity.server.bean.DeviceTypeStyles;
import com.sensoro.smartcity.server.bean.MergeTypeStyles;
import com.sensoro.smartcity.server.bean.SensorStruct;
import com.sensoro.smartcity.server.bean.SensorTypeStyles;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;


/**
 * Created by sensoro on 17/7/31.
 */

public class WidgetUtil {

    public static void judgeSensorType(SensorStruct sensorStruct, TextView valueTextView, TextView unitTextView) {
        try {
            Object value = sensorStruct.getValue();
            String sensorType = sensorStruct.getSensorType();
            boolean isBoolean = false;
            if (sensorType.equals("jinggai")) {
                isBoolean = true;
                Boolean isOk = (Boolean) value;
                if (isOk) {
                    valueTextView.setText(R.string.normal);
                } else {
                    valueTextView.setText(R.string.jinggai_false);
                }
                unitTextView.setText("");
            } else if (sensorType.equals("cover")) {
                isBoolean = true;
                Boolean isOk = (Boolean) value;
                if (isOk) {
                    valueTextView.setText(R.string.cover_true);
                } else {
                    valueTextView.setText(R.string.cover_false);
                }
//                    unitTextView.setEditText(R.string.cover_unit);
            } else if (sensorType.equals("level")) {
                isBoolean = true;
                Boolean isOk = (Boolean) value;
                if (isOk) {
                    valueTextView.setText(R.string.level_true);
                } else {
                    valueTextView.setText(R.string.level_false);
                }
//                    unitTextView.setEditText(R.string.level_unit);
            } else if (sensorType.equals("collision")) {
                isBoolean = true;
                Boolean isOk = (Boolean) value;
                if (isOk) {
                    valueTextView.setText(R.string.collision_true);
                } else {
                    valueTextView.setText(R.string.normal);
                }
                unitTextView.setText("");
            } else if (sensorType.equals("alarm")) {
                isBoolean = true;
                Boolean isOk = (Boolean) value;
                if (isOk) {
                    valueTextView.setText(R.string.status_alarm_true);
                } else {
                    valueTextView.setText(R.string.normal);
                }
                unitTextView.setText("");
            } else if (sensorType.equals("smoke")) {
                isBoolean = true;
                Boolean isOk = (Boolean) value;
                if (isOk) {
                    valueTextView.setText(R.string.smoke_true);
                } else {
                    valueTextView.setText(R.string.smoke_false);
                }
//                    unitTextView.setEditText(R.string.sensor_smoke);
            } else if (sensorType.equals("connection")) {
                isBoolean = true;
                Boolean isOk = (Boolean) value;
                if (isOk) {
                    valueTextView.setText(R.string.connection_true);
                } else {
                    valueTextView.setText(R.string.connection_false);
                }
//                    unitTextView.setEditText(R.string.sensor_smoke);
            } else if (sensorType.equalsIgnoreCase("installed")) {
                isBoolean = true;
                Boolean isTrue = (Boolean) value;
                if (isTrue) {
                    valueTextView.setText(R.string.installed);
                } else {
                    valueTextView.setText(R.string.un_installed);
                }
//                    unitTextView.setText("");
            } else if (sensorType.equals("flame")) {
                isBoolean = true;
                Boolean isOk = (Boolean) value;
                if (isOk) {
                    valueTextView.setText(R.string.flame_true);
                } else {
                    valueTextView.setText(R.string.flame_false);
                }
//                    unitTextView.setEditText(R.string.sensor_fire);
            } else if (sensorType.equals("magnetic")) {
                isBoolean = true;
                Boolean isTrue = (Boolean) value;
                if (isTrue) {
                    valueTextView.setText(R.string.magnetic_true);
                } else {
                    valueTextView.setText(R.string.magnetic_false);
                }
//                    unitTextView.setEditText(R.string.sensor_magnetic);
            } else if (sensorType.equals("door")) {
                isBoolean = true;
                Boolean isTrue = (Boolean) value;
                if (isTrue) {
                    valueTextView.setText(R.string.magnetic_false);
                } else {
                    valueTextView.setText(R.string.magnetic_true);
                }
//                    unitTextView.setEditText(R.string.sensor_magnetic);
            } else if (sensorType.equals("infrared")) {
                isBoolean = true;
                Boolean isTrue = (Boolean) value;
                if (isTrue) {
                    valueTextView.setText(R.string.status_alarm_true);
                } else {
                    valueTextView.setText(R.string.normal);
                }
//                    unitTextView.setEditText(R.string.sensor_magnetic);
            } else if (sensorType.equals("manual_alarm")) {
                isBoolean = true;
                Boolean isTrue = (Boolean) value;
                if (isTrue) {
                    valueTextView.setText(R.string.status_alarm_true);
                } else {
                    valueTextView.setText(R.string.normal);
                }
//                    unitTextView.setEditText(R.string.sensor_magnetic);
            } else if (sensorType.equals("sound_light_alarm")) {
                isBoolean = true;
                Boolean isTrue = (Boolean) value;
                if (isTrue) {
                    valueTextView.setText(R.string.status_alarm_true);
                } else {
                    valueTextView.setText(R.string.normal);
                }
//                    unitTextView.setEditText(R.string.sensor_magnetic);
            } else if (sensorType.equals("drop")) {
                isBoolean = true;
                if (value instanceof Double) {
                    double d = (double) value;
                    if (d == 0) {
                        valueTextView.setText(R.string.drop_false);
                    } else {
                        valueTextView.setText(R.string.drop_true);
                    }
                }
                unitTextView.setText("");
            }
            if (!isBoolean) {
                if (sensorType.equalsIgnoreCase("longitude") || sensorType.equalsIgnoreCase("latitude")) {
                    DecimalFormat df = new DecimalFormat("###.##");
                    setDetailTextStyle(df.format(sensorStruct.getValue()), valueTextView);
                } else if (sensorType.equalsIgnoreCase("co") || sensorType.equalsIgnoreCase("temperature") ||
                        sensorType.equalsIgnoreCase("temp1") ||
                        sensorType
                                .equalsIgnoreCase
                                        ("humidity") || sensorType.equalsIgnoreCase("waterPressure") ||
                        sensorType.equalsIgnoreCase("no2")) {
                    DecimalFormat df = new DecimalFormat("###.#");
                    String format = df.format(sensorStruct.getValue());
                    setDetailTextStyle(format, valueTextView);
                } else {
                    valueTextView.setText(String.format("%.0f", Double.valueOf(sensorStruct.getValue()
                            .toString())));
                }
                String unit = sensorStruct.getUnit();
                unitTextView.setText(unit);
            }
        } catch (Exception e) {
            valueTextView.setText("-");
            unitTextView.setText("-");
        }
    }

    private static void setDetailTextStyle(String text, TextView textView) {
        if (!TextUtils.isEmpty(text)) {
            if (text.contains(".")) {
                SpannableString styledText = new SpannableString(text);
                styledText.setSpan(new TextAppearanceSpan(textView.getContext(), R.style.text_detail_integer), 0,
                        text.lastIndexOf("."), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                styledText.setSpan(new TextAppearanceSpan(textView.getContext(), R.style.text_detail_decimal), text
                        .lastIndexOf("."), text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                textView.setText(styledText, TextView.BufferType.SPANNABLE);
            } else {
                textView.setText(text);
            }
        }

    }

    //文件转化成bitmap
    public static String bitmap2File(Bitmap bitmap, String path) {

        String pathname = path.substring(0, path.lastIndexOf(".")) + ".jpg";
        try {
            LogUtils.loge("pathname = " + pathname);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        if (TextUtils.isEmpty(pathname)) {
            return "";
        }
        File f = new File(pathname);
        if (f.exists()) f.delete();
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (fOut != null) {
                    fOut.close();
                }
                if (bitmap != null) {
                    bitmap.recycle();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return f.getAbsolutePath();
    }

    // 获取视频缩略图
    public static Bitmap getVideoThumbnail(String filePath) {
        Bitmap b = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            b = retriever.getFrameAtTime();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();

        } finally {
            try {
                retriever.release();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        return b;

    }

    private static void setIndexTextStyle(String text, TextView textView) {
        if (!TextUtils.isEmpty(text)) {
//            if (text.contains(".")) {
//                SpannableString styledText = new SpannableString(text);
//                styledText.setSpan(new TextAppearanceSpan(textView.getContext(), R.style.text_index_integer), 0,
//                        text.lastIndexOf("."), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                styledText.setSpan(new TextAppearanceSpan(textView.getContext(), R.style.text_index_decimal), text
//                        .lastIndexOf("."), text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//                textView.setText(styledText, TextView.BufferType.SPANNABLE);
//            } else {
//                textView.setText(text);
//            }
            textView.setText(text);
        }

    }

    public static int judgeSensorType(String[] sensorTypes) {
        if (sensorTypes.length > 0) {
            List<String> tempList = Arrays.asList(sensorTypes);
            if (tempList.contains("co")) {
                return R.mipmap.ic_sensor_co;
            } else if (tempList.contains("co2")) {
                return R.mipmap.ic_sensor_co2;
            } else if (tempList.contains("no2")) {
                return R.mipmap.ic_sensor_no2;
            } else if (tempList.contains("ch4")) {
                return R.mipmap.ic_sensor_ch4;
            } else if (tempList.contains("cover") || tempList.contains("jinggai") || tempList.contains("level")) {
                return R.mipmap.ic_sensor_cover;
            } else if (tempList.contains("pm10") || tempList.contains("pm2_5")) {
                return R.mipmap.ic_sensor_pm;
            } else if (tempList.contains("light")) {
                return R.mipmap.ic_sensor_light;
            } else if (tempList.size() > 1 && (tempList.contains("collision") || tempList.contains("pitch") ||
                    tempList.contains("roll"))) {
                return R.mipmap.ic_sensor_angle;
            } else if (tempList.contains("temperature") || tempList.contains("humidity")) {
                return R.mipmap.ic_sensor_temp_humi;
            } else if (tempList.contains("smoke") || tempList.contains("installed")) {
                return R.mipmap.ic_sensor_smoke;
            } else if (tempList.contains("drop")) {
                return R.mipmap.ic_sensor_drop;
            } else if (tempList.contains("distance")) {
                return R.mipmap.ic_sensor_level;
            } else if (tempList.contains("alarm") && tempList.size() == 1) {
                return R.mipmap.ic_sensor_call;
            } else if (tempList.contains("flame")) {
                return R.mipmap.ic_sensor_flame;
            } else if (tempList.contains("connection")) {
                return R.mipmap.ic_sensor_connection;
            } else if (tempList.contains("lpg")) {
                return R.mipmap.ic_sensor_lpg;
            } else if (tempList.contains("door")) {
                return R.mipmap.ic_sensor_lock;
            } else if (tempList.contains("magnetic")) {
                return R.mipmap.ic_sensor_magnetic;
            } else if (tempList.contains("waterPressure")) {
                return R.mipmap.ic_sensor_water_pressure;
            } else if (tempList.contains("altitude") || tempList.contains("longitude") || tempList.contains
                    ("latitude")) {
                return R.mipmap.ic_sensor_tracker;
            } else if (tempList.contains("leakage_val") || tempList.contains("temp_val")) {
                return R.mipmap.ic_sensor_electric_alarm_bg;
            } else if (tempList.contains("CURRENT_A") || tempList.contains("CURRENT_B") || tempList.contains
                    ("CURRENT_C") || tempList.contains("TOTAL_POWER") || tempList.contains("VOLTAGE_A") || tempList
                    .contains("VOLTAGE_B") || tempList
                    .contains("VOLTAGE_C")) {
                //CURRENT_A|CURRENT_B|CURRENT_C|ID|TOTAL_POWER|VOLTAGE_A|VOLTAGE_B|VOLTAGE_C
                return R.mipmap.ic_seneor_electric_meter_bg;
            } else if (tempList.contains("infrared")) {
                return R.mipmap.ic_sensor_infrared_bg;
            } else if (tempList.contains("manual_alarm")) {
                return R.mipmap.ic_sensor_manual_alarm_bg;
            } else if (tempList.contains("sound_light_alarm")) {
                return R.mipmap.ic_sensor_sound_light_alarm_bg;
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }


    public static void judgeSensorType(Context context, ImageView srcImageView, String sensorType) {
        int x_ = context.getResources().getDimensionPixelSize(R.dimen.x300);
        int y_ = context.getResources().getDimensionPixelSize(R.dimen.y400);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(210, 210);
        int pixel = -30;
        int bottom_pixel = -40;
        int default_pixel = -30;
        srcImageView.setVisibility(View.VISIBLE);
        if (sensorType.equalsIgnoreCase("co")) {
            srcImageView.setImageResource(R.mipmap.ic_sensor_bg_co);
            layoutParams.setMargins(0, 0, pixel, default_pixel);
        } else if (sensorType.equalsIgnoreCase("co2")) {
            srcImageView.setImageResource(R.mipmap.ic_sensor_bg_co2);
            layoutParams.setMargins(0, 0, pixel, bottom_pixel);
        } else if (sensorType.equalsIgnoreCase("no2")) {
            srcImageView.setImageResource(R.mipmap.ic_sensor_bg_no2);
            layoutParams.setMargins(0, 0, pixel, default_pixel);
        } else if (sensorType.equalsIgnoreCase("pm2_5") || sensorType.equalsIgnoreCase("pm10")) {
            srcImageView.setImageResource(R.mipmap.ic_sensor_bg_pm);
            layoutParams.setMargins(0, 0, pixel, default_pixel);
        } else if (sensorType.equalsIgnoreCase("ch4")) {
            srcImageView.setImageResource(R.mipmap.ic_sensor_bg_ch4);
            layoutParams.setMargins(0, 0, pixel, default_pixel);
        } else if (sensorType.equalsIgnoreCase("connection")) {
            srcImageView.setImageResource(R.mipmap.ic_sensor_connection_bg);
            layoutParams.setMargins(0, 0, pixel, default_pixel);
        } else if (sensorType.equalsIgnoreCase("temperature") || sensorType.equalsIgnoreCase("humidity")) {
            srcImageView.setImageResource(R.mipmap.ic_sensor_bg_temp_humi);
            layoutParams.setMargins(0, 0, pixel, bottom_pixel);
        } else if (sensorType.equalsIgnoreCase("smoke") || sensorType.equalsIgnoreCase("installed")) {
            srcImageView.setImageResource(R.mipmap.ic_sensor_bg_smoke);
            layoutParams.setMargins(0, 0, pixel, default_pixel);
        } else if (sensorType.equalsIgnoreCase("distance")) {
            srcImageView.setImageResource(R.mipmap.ic_sensor_bg_level);
            layoutParams.setMargins(0, 0, pixel, bottom_pixel);
        } else if (sensorType.equalsIgnoreCase("level") || sensorType.equalsIgnoreCase("distance")) {
            srcImageView.setImageResource(R.mipmap.ic_sensor_bg_level);
            layoutParams.setMargins(0, 0, pixel, bottom_pixel);
        } else if (sensorType.equalsIgnoreCase("cover")) {
            srcImageView.setImageResource(R.mipmap.ic_sensor_bg_cover);
            layoutParams.setMargins(0, 0, pixel, bottom_pixel);
        } else if (sensorType.equalsIgnoreCase("drop")) {
            srcImageView.setImageResource(R.mipmap.ic_sensor_bg_drop);
            layoutParams.setMargins(0, 0, pixel, bottom_pixel);
        } else if (sensorType.equalsIgnoreCase("light")) {
            srcImageView.setImageResource(R.mipmap.ic_sensor_bg_light);
            layoutParams.setMargins(0, 0, pixel, bottom_pixel);
        } else if (sensorType.equalsIgnoreCase("lpg")) {
            srcImageView.setImageResource(R.mipmap.ic_sensor_bg_lpg);
            layoutParams.setMargins(0, 0, pixel, bottom_pixel);
        } else if (sensorType.equalsIgnoreCase("roll") || sensorType.equalsIgnoreCase("yaw") || sensorType
                .equalsIgnoreCase("pitch") || sensorType.equalsIgnoreCase("angle")) {
            srcImageView.setImageResource(R.mipmap.ic_sensor_bg_angle);
            layoutParams.setMargins(0, 0, pixel, bottom_pixel);
        } else if (sensorType.equalsIgnoreCase("collision")) {
            srcImageView.setImageResource(R.mipmap.ic_sensor_bg_angle);
            layoutParams.setMargins(0, 0, pixel, bottom_pixel);
        } else if (sensorType.equalsIgnoreCase("alarm")) {//alarm
            srcImageView.setImageResource(R.mipmap.ic_sensor_bg_call);
            layoutParams.setMargins(0, 0, pixel, bottom_pixel);
        } else if (sensorType.equalsIgnoreCase("flame")) { //
            srcImageView.setImageResource(R.mipmap.ic_sensor_bg_flame);
            layoutParams.setMargins(0, 0, pixel, bottom_pixel);
        } else if (sensorType.equals("magnetic")) {
            srcImageView.setImageResource(R.mipmap.ic_sensor_bg_magnetic);
            layoutParams.setMargins(0, 0, pixel, bottom_pixel);
        } else if (sensorType.equals("door")) {
            srcImageView.setImageResource(R.mipmap.ic_sensor_bg_lock);
            layoutParams.setMargins(0, 0, pixel, bottom_pixel);
        } else if (sensorType.equals("waterPressure")) {
            srcImageView.setImageResource(R.mipmap.ic_sensor_bg_water_pressure);
            layoutParams.setMargins(0, 0, pixel, bottom_pixel);
        } else if (sensorType.equals("latitude") || sensorType.equals("longitude") || sensorType.equals("altitude")) {
            srcImageView.setImageResource(R.mipmap.ic_sensor_bg_tracker);
            layoutParams.setMargins(0, 0, pixel, bottom_pixel);
        } else if (sensorType.equals("leakage_val") || sensorType.equals("temp_val")) {
            srcImageView.setImageResource(R.mipmap.ic_sensor_electric_alarm_bg);
            layoutParams.setMargins(0, 0, pixel, bottom_pixel);
        } else if (sensorType.equals("TOTAL_POWER")) {
            srcImageView.setImageResource(R.mipmap.ic_seneor_electric_meter_bg);
            layoutParams.setMargins(0, 0, pixel, bottom_pixel);
        } else if (sensorType.equalsIgnoreCase("infrared")) {
            srcImageView.setImageResource(R.mipmap.ic_sensor_infrared_bg);
            layoutParams.setMargins(0, 0, pixel, default_pixel);
        } else if (sensorType.equalsIgnoreCase("manual_alarm")) {
            srcImageView.setImageResource(R.mipmap.ic_sensor_manual_alarm_bg);
            layoutParams.setMargins(0, 0, pixel, default_pixel);
        } else if (sensorType.equalsIgnoreCase("sound_light_alarm")) {
            srcImageView.setImageResource(R.mipmap.ic_sensor_sound_light_alarm_bg);
            layoutParams.setMargins(0, 0, pixel, default_pixel);
        } else {
            srcImageView.setVisibility(View.GONE);
        }
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        srcImageView.setLayoutParams(layoutParams);
    }

    public static void judgeSensorTypeNew(ImageView srcImageView, String sensorType) {
        int tempResId = R.drawable.type_smoke;
        if (sensorType.equalsIgnoreCase("co")) {
            tempResId = R.drawable.type_co;
        } else if (sensorType.equalsIgnoreCase("co2")) {
            tempResId = R.drawable.type_co2;
        } else if (sensorType.equalsIgnoreCase("no2")) {
            tempResId = R.drawable.type_no2;
        } else if (sensorType.equalsIgnoreCase("pm2_5") || sensorType.equalsIgnoreCase("pm10")) {
            tempResId = R.drawable.type_pm;
        } else if (sensorType.equalsIgnoreCase("ch4")) {
            tempResId = R.drawable.type_ch4;
        } else if (sensorType.equalsIgnoreCase("connection")) {
            tempResId = R.drawable.type_on_off_monitoring;
        } else if (sensorType.equalsIgnoreCase("temperature") || sensorType.equalsIgnoreCase("humidity")) {
            tempResId = R.drawable.type_tempature_humidity;
        } else if (sensorType.equalsIgnoreCase("smoke") || sensorType.equalsIgnoreCase("installed")) {
            tempResId = R.drawable.type_smoke;
        } else if (sensorType.equalsIgnoreCase("distance")) {
            tempResId = R.drawable.type_water_monitoring;
        } else if (sensorType.equalsIgnoreCase("level") || sensorType.equalsIgnoreCase("distance")) {
            tempResId = R.drawable.type_well_position;
        } else if (sensorType.equalsIgnoreCase("cover")) {
            tempResId = R.drawable.type_well_position;
        } else if (sensorType.equalsIgnoreCase("drop")) {
            tempResId = R.drawable.type_leak;
        } else if (sensorType.equalsIgnoreCase("light")) {
            tempResId = R.drawable.type_light;
        } else if (sensorType.equalsIgnoreCase("lpg")) {
            tempResId = R.drawable.type_gas;
        } else if (sensorType.equalsIgnoreCase("roll") || sensorType.equalsIgnoreCase("yaw") || sensorType
                .equalsIgnoreCase("pitch") || sensorType.equalsIgnoreCase("angle")) {
            tempResId = R.drawable.type_inclination;
        } else if (sensorType.equalsIgnoreCase("collision")) {
            tempResId = R.drawable.type_inclination;
        } else if (sensorType.equalsIgnoreCase("alarm")) {//alarm
            tempResId = R.drawable.type_emergency_call;
        } else if (sensorType.equalsIgnoreCase("flame")) { //
            tempResId = R.drawable.type_flame;
        } else if (sensorType.equals("magnetic")) {
            tempResId = R.drawable.type_geomagnetic;
        } else if (sensorType.equals("door")) {
            tempResId = R.drawable.type_lock_monitoring;
        } else if (sensorType.equals("waterPressure")) {
            tempResId = R.drawable.type_fire_hydraulic;
        } else if (sensorType.equals("latitude") || sensorType.equals("longitude") || sensorType.equals("altitude")) {
            tempResId = R.drawable.type_tracking_device;
        } else if (sensorType.equals("leakage_val") || sensorType.equals("temp_val")) {
            tempResId = R.drawable.type_tempature_humidity;
        } else if (sensorType.equals("TOTAL_POWER")) {
            tempResId = R.drawable.type_ammeter;
        } else if (sensorType.equalsIgnoreCase("infrared")) {
            tempResId = R.mipmap.ic_sensor_infrared;
        } else if (sensorType.equalsIgnoreCase("manual_alarm")) {
            tempResId = R.mipmap.ic_sensor_manual_alarm;
        } else if (sensorType.equalsIgnoreCase("sound_light_alarm")) {
            tempResId = R.mipmap.ic_sensor_sound_light_alarm;
        } else {
//            srcImageView.setVisibility(View.INVISIBLE);
        }
        srcImageView.setImageResource(tempResId);
    }

    private static void changeColor(Context context, ImageView srcImageView, int imageResId, int resColor) {
//        Drawable drawable = ContextCompat.getDrawable(context, imageResId);
//        Drawable.ConstantState statusTitle = drawable.getConstantState();
//        Drawable drawableNew = DrawableCompat.wrap(statusTitle == null ? drawable : statusTitle.newDrawable()).mutate();
//        drawableNew.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
//        DrawableCompat.setTint(drawable, ContextCompat.getColor(context, resColor));
////            srcImageView.setImageDrawable(drawable);
//        srcImageView.setImageDrawable(drawableNew);

        srcImageView.setColorFilter(context.getResources().getColor(resColor));
    }

    //
    public static void judgeIndexSensorType(TextView valueTextView, String
            sensorType, boolean isBool, SensorStruct sensorStruct) {
        Object value = sensorStruct.getValue();
        if (isBool) {
            if (sensorType.equalsIgnoreCase("smoke")) {
                Boolean isTrue = (Boolean) value;
                if (isTrue) {
                    valueTextView.setText(R.string.smoke_true);
                } else {
                    valueTextView.setText(R.string.smoke_false);
                }
            } else if (sensorType.equalsIgnoreCase("installed")) {
                Boolean isTrue = (Boolean) value;
                if (isTrue) {
                    valueTextView.setText(R.string.installed);
                } else {
                    valueTextView.setText(R.string.un_installed);
                }
            } else if (sensorType.equalsIgnoreCase("level")) {
                Boolean isTrue = (Boolean) value;
                if (isTrue) {
                    valueTextView.setText(R.string.level_true);
                } else {
                    valueTextView.setText(R.string.level_false);
                }
            } else if (sensorType.equalsIgnoreCase("cover") || sensorType.equalsIgnoreCase("jinggai")) {
                Boolean isTrue = (Boolean) value;
                if (isTrue) {
                    valueTextView.setText(R.string.cover_true);
                } else {
                    valueTextView.setText(R.string.cover_false);
                }
            } else if (sensorType.equalsIgnoreCase("drop")) {
                if (value instanceof Double) {
                    double d = (double) value;
                    if (d == 0) {
                        valueTextView.setText(R.string.drop_false);
                    } else {
                        valueTextView.setText(R.string.drop_true);
                    }
                }
            } else if (sensorType.equalsIgnoreCase("collision")) {
                Boolean isTrue = (Boolean) value;
                if (isTrue) {
                    valueTextView.setText(R.string.collision_true);
                } else {
                    valueTextView.setText(R.string.normal);
                }
            } else if (sensorType.equalsIgnoreCase("alarm")) {
                Boolean isTrue = (Boolean) value;
                if (isTrue) {
                    valueTextView.setText(R.string.status_alarm_true);
                } else {
                    valueTextView.setText(R.string.normal);
                }
            } else if (sensorType.equalsIgnoreCase("flame")) { //
                Boolean isTrue = (Boolean) value;
                if (isTrue) {
                    valueTextView.setText(R.string.flame_true);
                } else {
                    valueTextView.setText(R.string.flame_false);
                }
            } else if (sensorType.equals("magnetic")) {
                Boolean isTrue = (Boolean) value;
                if (isTrue) {
                    valueTextView.setText(R.string.magnetic_true);
                } else {
                    valueTextView.setText(R.string.magnetic_false);
                }
            } else if (sensorType.equals("door")) {
                Boolean isTrue = (Boolean) value;
                if (isTrue) {
                    valueTextView.setText(R.string.magnetic_false);
                } else {
                    valueTextView.setText(R.string.magnetic_true);
                }
            } else if (sensorType.equals("connection")) {
                Boolean isTrue = (Boolean) value;
                if (isTrue) {
                    valueTextView.setText(R.string.connection_true);
                } else {
                    valueTextView.setText(R.string.connection_false);
                }
            } else if (sensorType.equalsIgnoreCase("installed")) {
                Boolean isTrue = (Boolean) value;
                if (isTrue) {
                    valueTextView.setText(R.string.installed);
                } else {
                    valueTextView.setText(R.string.un_installed);
                }
            } else if (sensorType.equalsIgnoreCase("infrared")) {
                Boolean isTrue = (Boolean) value;
                if (isTrue) {
                    valueTextView.setText(R.string.status_alarm_true);
                } else {
                    valueTextView.setText(R.string.normal);
                }
            } else if (sensorType.equalsIgnoreCase("manual_alarm")) {
                Boolean isTrue = (Boolean) value;
                if (isTrue) {
                    valueTextView.setText(R.string.status_alarm_true);
                } else {
                    valueTextView.setText(R.string.normal);
                }
            } else if (sensorType.equalsIgnoreCase("sound_light_alarm")) {
                Boolean isTrue = (Boolean) value;
                if (isTrue) {
                    valueTextView.setText(R.string.status_alarm_true);
                } else {
                    valueTextView.setText(R.string.normal);
                }
            }
        } else {
            if (value instanceof String) {
                if (sensorType.equalsIgnoreCase("longitude") || sensorType.equalsIgnoreCase("latitude")) {
                    setIndexTextStyle((String) value, valueTextView);
                } else if (sensorType.equalsIgnoreCase("co") || sensorType.equalsIgnoreCase("temperature") || sensorType
                        .equalsIgnoreCase
                                ("humidity") || sensorType.equalsIgnoreCase("waterPressure") || sensorType
                        .equalsIgnoreCase("no2") || sensorType.equalsIgnoreCase("temp1")) {
                    setIndexTextStyle((String) value, valueTextView);
                } else {
                    valueTextView.setText((String) value);
                }
            } else if (value instanceof Number) {
                if (sensorType.equalsIgnoreCase("longitude") || sensorType.equalsIgnoreCase("latitude")) {
                    DecimalFormat df = new DecimalFormat("###.##");
                    setIndexTextStyle(df.format(value), valueTextView);
                } else if (sensorType.equalsIgnoreCase("co") || sensorType.equalsIgnoreCase("temperature") || sensorType
                        .equalsIgnoreCase
                                ("humidity") || sensorType.equalsIgnoreCase("waterPressure") || sensorType
                        .equalsIgnoreCase("no2") || sensorType.equalsIgnoreCase("temp1")) {
                    DecimalFormat df = new DecimalFormat("###.#");
                    setIndexTextStyle(df.format(value), valueTextView);
                } else {
                    valueTextView.setText("" + String.format("%.0f", Double.valueOf(value
                            .toString())));
                }
            }

        }
    }

    public static void judgeIndexSensorType(MonitoringPointRcContentAdapterModel monitoringPointRcContentAdapterModel, String
            sensorType, Object value) {
        if (value instanceof String) {
            if (sensorType.equalsIgnoreCase("longitude") || sensorType.equalsIgnoreCase("latitude")) {
                monitoringPointRcContentAdapterModel.content = (String) value;
            } else if (sensorType.equalsIgnoreCase("co") || sensorType.equalsIgnoreCase("temperature") || sensorType
                    .equalsIgnoreCase
                            ("humidity") || sensorType.equalsIgnoreCase("waterPressure") || sensorType
                    .equalsIgnoreCase("no2") || sensorType.equalsIgnoreCase("temp1")) {
                monitoringPointRcContentAdapterModel.content = (String) value;
            } else {
                monitoringPointRcContentAdapterModel.content = (String) value;
            }
        } else if (value instanceof Number) {
            SensorTypeStyles sensorTypeStyles = PreferencesHelper.getInstance().getConfigSensorType(sensorType);
            if (sensorTypeStyles != null) {
                Integer precision = sensorTypeStyles.getPrecision();
                if (precision != null) {
                    //不留0
                    Double valueStr = (Double) value;
                    BigDecimal b = new BigDecimal(valueStr);
                    //留0
//                  NumberFormat nf = NumberFormat.getNumberInstance();
//                  nf.setMaximumFractionDigits(precision);
//                  String format = nf.format(value);
                    monitoringPointRcContentAdapterModel.content = b.setScale(precision, BigDecimal.ROUND_HALF_UP).toString();
                    return;
                }
            }
            //TODO ﻿precision 字段
            if (sensorType.equalsIgnoreCase("longitude") || sensorType.equalsIgnoreCase("latitude")) {
                DecimalFormat df = new DecimalFormat("###.##");
                monitoringPointRcContentAdapterModel.content = df.format(value);
            } else if (sensorType.equalsIgnoreCase("co") || sensorType.equalsIgnoreCase("temperature") || sensorType
                    .equalsIgnoreCase
                            ("humidity") || sensorType.equalsIgnoreCase("waterPressure") || sensorType
                    .equalsIgnoreCase("no2") || sensorType.equalsIgnoreCase("temp1")) {
                DecimalFormat df = new DecimalFormat("###.#");
                monitoringPointRcContentAdapterModel.content = df.format(value);
            } else {
                monitoringPointRcContentAdapterModel.content = String.format("%.0f", Double.valueOf(value
                        .toString()));

            }
        }

    }

    public static void judgeIndexSensorType(Elect3DetailModel elect3DetailModel, String
            sensorType, Object value, String unit) {
        StringBuilder builder = new StringBuilder();
        if (value instanceof String) {
            if (sensorType.equalsIgnoreCase("longitude") || sensorType.equalsIgnoreCase("latitude")) {
                builder.append((String) value);
            } else if (sensorType.equalsIgnoreCase("co") || sensorType.equalsIgnoreCase("temperature") || sensorType
                    .equalsIgnoreCase
                            ("humidity") || sensorType.equalsIgnoreCase("waterPressure") || sensorType
                    .equalsIgnoreCase("no2") || sensorType.equalsIgnoreCase("temp1")) {
                builder.append((String) value);
            } else {
                builder.append((String) value);
            }
        } else if (value instanceof Number) {
            SensorTypeStyles sensorTypeStyles = PreferencesHelper.getInstance().getConfigSensorType(sensorType);
            if (sensorTypeStyles != null) {
                Integer precision = sensorTypeStyles.getPrecision();
                if (precision != null) {
                    Double valueStr = (Double) value;
                    BigDecimal b = new BigDecimal(valueStr);
                    //留0
//                  NumberFormat nf = NumberFormat.getNumberInstance();
//                  nf.setMaximumFractionDigits(precision);
//                  String format = nf.format(value);
                    builder.append(b.setScale(precision, BigDecimal.ROUND_HALF_UP).toString());
                    if (!TextUtils.isEmpty(unit)) {
                        builder.append(unit);
                    }
                    elect3DetailModel.text = builder.toString();
                    return;
                }
            }
            //TODO 当没有precision字段时
            if (sensorType.equalsIgnoreCase("longitude") || sensorType.equalsIgnoreCase("latitude")) {
                DecimalFormat df = new DecimalFormat("###.##");
                builder.append(df.format(value));
            } else if (sensorType.equalsIgnoreCase("co") || sensorType.equalsIgnoreCase("temperature") || sensorType
                    .equalsIgnoreCase
                            ("humidity") || sensorType.equalsIgnoreCase("waterPressure") || sensorType
                    .equalsIgnoreCase("no2") || sensorType.equalsIgnoreCase("temp1")) {
                DecimalFormat df = new DecimalFormat("###.#");
                builder.append(df.format(value));
            } else {
                builder.append(String.format("%.0f", Double.valueOf(value
                        .toString())));
            }
        }
        if (!TextUtils.isEmpty(unit)) {
            builder.append(unit);
        }
        elect3DetailModel.text = builder.toString();
    }

    public static void judgeIndexSensorType(TextView valueTextView, TextView unitTextView, String
            sensorType, SensorStruct sensorStruct) {
        boolean isBool = false;
        Object value = sensorStruct.getValue();

        if (sensorType.equalsIgnoreCase("smoke")) {
            isBool = true;
            Boolean isTrue = (Boolean) value;
            if (isTrue) {
                valueTextView.setText(R.string.smoke_true);
            } else {
                valueTextView.setText(R.string.smoke_false);
            }
            unitTextView.setText("");
        } else if (sensorType.equalsIgnoreCase("installed")) {
            isBool = true;
            Boolean isTrue = (Boolean) value;
            if (isTrue) {
                valueTextView.setText(R.string.installed);
            } else {
                valueTextView.setText(R.string.un_installed);
            }
            unitTextView.setText("");
        } else if (sensorType.equalsIgnoreCase("level")) {
            Boolean isTrue = (Boolean) value;
            isBool = true;
            if (isTrue) {
                valueTextView.setText(R.string.level_true);
            } else {
                valueTextView.setText(R.string.level_false);
            }
            unitTextView.setText("");
        } else if (sensorType.equalsIgnoreCase("cover") || sensorType.equalsIgnoreCase("jinggai")) {
            Boolean isTrue = (Boolean) value;
            isBool = true;
            if (isTrue) {
                valueTextView.setText(R.string.cover_true);
            } else {
                valueTextView.setText(R.string.cover_false);
            }
            unitTextView.setText("");
        } else if (sensorType.equalsIgnoreCase("drop")) {
            isBool = true;
            if (value instanceof Double) {
                double d = (double) value;
                if (d == 0) {
                    valueTextView.setText(R.string.drop_false);
                } else {
                    valueTextView.setText(R.string.drop_true);
                }
            }
            unitTextView.setText("");
        } else if (sensorType.equalsIgnoreCase("collision")) {
            isBool = true;
            Boolean isTrue = (Boolean) value;
            if (isTrue) {
                valueTextView.setText(R.string.collision_true);
            } else {
                valueTextView.setText(R.string.normal);
            }
            unitTextView.setText("");
        } else if (sensorType.equalsIgnoreCase("alarm")) {
            isBool = true;
            Boolean isTrue = (Boolean) value;
            if (isTrue) {
                valueTextView.setText(R.string.status_alarm_true);
            } else {
                valueTextView.setText(R.string.normal);
            }
            unitTextView.setText("");
        } else if (sensorType.equalsIgnoreCase("flame")) { //
            isBool = true;
            Boolean isTrue = (Boolean) value;
            if (isTrue) {
                valueTextView.setText(R.string.flame_true);
            } else {
                valueTextView.setText(R.string.flame_false);
            }
            unitTextView.setText("");
        } else if (sensorType.equals("magnetic")) {
            isBool = true;
            Boolean isTrue = (Boolean) value;
            if (isTrue) {
                valueTextView.setText(R.string.magnetic_true);
            } else {
                valueTextView.setText(R.string.magnetic_false);
            }
            unitTextView.setText("");
        } else if (sensorType.equals("door")) {
            isBool = true;
            Boolean isTrue = (Boolean) value;
            if (isTrue) {
                valueTextView.setText(R.string.magnetic_false);
            } else {
                valueTextView.setText(R.string.magnetic_true);
            }
            unitTextView.setText("");
        } else if (sensorType.equals("connection")) {
            isBool = true;
            Boolean isTrue = (Boolean) value;
            if (isTrue) {
                valueTextView.setText(R.string.connection_true);
            } else {
                valueTextView.setText(R.string.connection_false);
            }
            unitTextView.setText("");
        } else if (sensorType.equalsIgnoreCase("installed")) {
            isBool = true;
            Boolean isTrue = (Boolean) value;
            if (isTrue) {
                valueTextView.setText(R.string.installed);
            } else {
                valueTextView.setText(R.string.un_installed);
            }
            unitTextView.setText("");
        } else if (sensorType.equalsIgnoreCase("infrared")) {
            isBool = true;
            Boolean isTrue = (Boolean) value;
            if (isTrue) {
                valueTextView.setText(R.string.status_alarm_true);
            } else {
                valueTextView.setText(R.string.normal);
            }
            unitTextView.setText("");
        } else if (sensorType.equalsIgnoreCase("manual_alarm")) {
            isBool = true;
            Boolean isTrue = (Boolean) value;
            if (isTrue) {
                valueTextView.setText(R.string.status_alarm_true);
            } else {
                valueTextView.setText(R.string.normal);
            }
            unitTextView.setText("");
        } else if (sensorType.equalsIgnoreCase("sound_light_alarm")) {
            isBool = true;
            Boolean isTrue = (Boolean) value;
            if (isTrue) {
                valueTextView.setText(R.string.status_alarm_true);
            } else {
                valueTextView.setText(R.string.normal);
            }
            unitTextView.setText("");
        }
        if (!isBool) {
            if (sensorType.equalsIgnoreCase("longitude") || sensorType.equalsIgnoreCase("latitude")) {
                DecimalFormat df = new DecimalFormat("###.##");
                setIndexTextStyle(df.format(value), valueTextView);
            } else if (sensorType.equalsIgnoreCase("co") || sensorType.equalsIgnoreCase("temperature") || sensorType
                    .equalsIgnoreCase
                            ("humidity") || sensorType.equalsIgnoreCase("waterPressure") || sensorType
                    .equalsIgnoreCase("no2") || sensorType.equalsIgnoreCase("temp1")) {
                DecimalFormat df = new DecimalFormat("###.#");
                setIndexTextStyle(df.format(value), valueTextView);
            } else {
                valueTextView.setText("" + String.format("%.0f", Double.valueOf(value
                        .toString())));
            }
            String unit = sensorStruct.getUnit();
            unitTextView.setText(unit);
        }
    }


    /**
     * 根据标识显示相关的状态信息和数据
     *
     * @param sensorType
     * @param thresholds
     * @param status
     * @return
     */
    public static String getAlarmDetailInfo(String sensorType, int thresholds, int status) {
        String info;
        if (status == 0) {
            switch (sensorType) {
                case "smoke":
                    info = "无烟，恢复正常";
                    break;
                case "cover":
                case "jinggai":
                    info = "井盖闭合，恢复正常";
                    break;
                case "level":
                    info = "水位未溢出, 恢复正常";
                    break;
                case "alarm":
                    info = "紧急呼叫解除，恢复正常";
                    break;
                case "flame":
                    info = "未检测到火焰，恢复正常";
                    break;
                case "collision":
                    info = "碰撞解除，恢复正常";
                    break;
                case "drop":
                case "leak":
                    info = "未检测到滴漏，恢复正常";
                    break;
                case "connection":
                    info = "连通，恢复正常";
                    break;
                case "door":
                    info = "门锁关闭，恢复正常";
                    break;
                case "temperature":
                case "temp1":
                    info = "温度低于预警值, 恢复正常";
                    break;
                case "humidity":
                    info = "湿度低于预警值, 恢复正常";
                    break;
                case "battery":
                    info = "电量低于预警值, 恢复正常";
                    break;
                case "co":
                    info = "一氧化碳低于预警值, 恢复正常";
                    break;
                case "co2":
                    info = "二氧化碳低于预警值, 恢复正常";
                    break;
                case "lpg":
                    info = "液化石油气低于预警值, 恢复正常";
                    break;
                case "ch4":
                    info = "甲烷低于预警值, 恢复正常";
                    break;
                case "no2":
                    info = "二氧化氮低于预警值, 恢复正常";
                    break;
                case "pm2_5":
                    info = "PM2.5低于预警值, 恢复正常";
                    break;
                case "pm10":
                    info = "PM10低于预警值, 恢复正常";
                    break;
                case "distance":
                    info = "液位低于预警值, 恢复正常";
                    break;
                case "light":
                    info = "光线低于预警值, 恢复正常";
                    break;
                case "pitch":
                    info = "俯仰角低于预警值, 恢复正常";
                    break;
                case "roll":
                    info = "横滚角低于预警值, 恢复正常";
                    break;
                case "altitude":
                    info = "海拔低于预警值, 恢复正常";
                    break;
                case "latitude":
                    info = "维度低于预警值, 恢复正常";
                    break;
                case "longitude":
                    info = "经度低于预警值, 恢复正常";
                    break;
                //CURRENT_A|CURRENT_B|CURRENT_C|ID|TOTAL_POWER|VOLTAGE_A|VOLTAGE_B|VOLTAGE_C
                case "CURRENT_A":
                case "CURRENT_B":
                case "CURRENT_C":
                case "ID":
                case "TOTAL_POWER":
                case "VOLTAGE_A":
                case "VOLTAGE_B":
                case "VOLTAGE_C":
                    info = "电量低于预警值, 恢复正常";
                    break;
                case "installed":
                    info = "已安装，恢复正常";
                    break;
                case "leakage_val":
                    info = "漏电流低于预警值, 恢复正常";
                    break;
                case "temp_val":
                    info = "电线温度低于预警值, 恢复正常";
                    break;
                case "infrared":
                    info = "红外线未感应到物体, 恢复正常";
                    break;
                case "manual_alarm":
                    info = "未触发手动报警, 恢复正常";
                    break;
                case "sound_light_alarm":
                    info = "未发生声光报警, 恢复正常";
                    break;
                default:
                    info = "未知传感器低于预警值, 恢复正常";
                    break;
            }
        } else {
            switch (sensorType) {
                case "smoke":
                    info = "烟雾浓度高，设备预警";
                    break;
                case "installed":
                    info = "被拆卸，设备预警";
                    break;
                case "cover":
                case "jinggai":
                    info = "井盖打开，设备预警";
                    break;
                case "level":
                    info = "水位溢出, 设备预警";
                    break;
                case "alarm":
                    info = "触发紧急呼叫，设备预警";
                    break;
                case "flame":
                    info = "检测到火焰，设备预警";
                    break;
                case "collision":
                    info = "碰撞解除，恢复正常";
                    break;
                case "drop":
                    info = "发生滴漏，设备预警";
                    break;
                case "leak":
                    info = "发生滴漏，设备预警";
                    break;
                case "door":
                    info = "门锁打开，设备预警";
                    break;
                case "infrared":
                    info = "红外线感应到物体，设备预警";
                    break;
                case "manual_alarm":
                    info = "触发手动报警，设备预警";
                    break;
                case "sound_light_alarm":
                    info = "发生声光报警，设备预警";
                    break;
                case "connection":
                    info = "断开，设备预警";
                    break;
                case "temperature":
                case "temp1":
                    info = "温度 值为 " + thresholds + "°C 达到预警值";
                    break;
                case "humidity":
                    info = "湿度 值为 " + thresholds + "% 达到预警值";
                    break;
                case "battery":
                    info = "电量 值为 " + thresholds + "% 达到预警值";
                    break;
                case "co":
                    info = "一氧化碳 值为 " + thresholds + "ppm 达到预警值";
                    break;
                case "co2":
                    info = "二氧化碳 值为 " + thresholds + "ppm 达到预警值";
                    break;
                case "lpg":
                    info = "液化石油气 值为 " + thresholds + "达到预警值";
                    break;
                case "ch4":
                    info = "甲烷 值为 " + thresholds + "ppm 达到预警值";
                    break;
                case "no2":
                    info = "二氧化氮 值为 " + thresholds + "ug/m³ 达到预警值";
                    break;
                case "pm2_5":
                    info = "PM2.5 值为 " + thresholds + "ug/m³ 达到预警值";
                    break;
                case "pm10":
                    info = "PM10 值为 " + thresholds + "ug/m³ 达到预警值";
                    break;
                case "distance":
                    info = "液位 值为 " + thresholds + "cm 达到预警值";
                    break;
                case "light":
                    info = "光线 值为 " + thresholds + "Lux 达到预警值";
                    break;
                case "pitch":
                    info = "俯仰角 值为 " + thresholds + "  达到预警值";
                    break;
                case "roll":
                    info = "横滚角 值为 " + thresholds + "  达到预警值";
                    break;
                case "altitude":
                    info = "海拔 值为 " + thresholds + "  达到预警值";
                    break;
                case "latitude":
                    info = "维度 值为 " + thresholds + "  达到预警值";
                    break;
                case "longitude":
                    info = "经度 值为 " + thresholds + " 达到预警值";
                    break;
                case "CURRENT_A":
                case "CURRENT_B":
                case "CURRENT_C":
                case "ID":
                case "TOTAL_POWER":
                case "VOLTAGE_A":
                case "VOLTAGE_B":
                case "VOLTAGE_C":
                    info = "电量 值为 " + thresholds + " 达到预警值";
                    break;
                case "leakage_val":
                    info = "漏电流 值为 " + thresholds + " 达到预警值";
                    break;
                case "temp_val":
                    info = "电线温度 值为 " + thresholds + " 达到预警值";
                    break;
                default:
                    info = "未知传感器 值为 " + thresholds + "  达到预警值";
                    break;
            }
        }
        return info;
    }


    /**
     * 去掉多余的0
     *
     * @param s
     * @return
     */
    public static String subZeroAndDot(String s) {
        if (s.indexOf(".") > 0) {
            s = s.replaceAll("0+?$", "");//去掉多余的0
            s = s.replaceAll("[.]$", "");//如最后一位是.则去掉
        }
        return s;
    }



    @NonNull
    public static void changeIconColor(Context context, ImageView imageView, int resColor) {
        Drawable drawable = imageView.getDrawable();
        Drawable.ConstantState state = drawable.getConstantState();
        DrawableCompat.wrap(state == null ? drawable : state.newDrawable()).mutate();
        drawable.setBounds(0, 0, drawable.getIntrinsicHeight(), drawable.getIntrinsicHeight());
        DrawableCompat.setTint(drawable, context.getResources().getColor(resColor));
        imageView.setImageDrawable(drawable);
    }

    public static BitmapDrawable createBitmapDrawable(Context context, String sensorType, Bitmap srcBitmap, Bitmap
            targetBitmap) {
        int width = srcBitmap.getWidth();
        int height = srcBitmap.getHeight();
        Bitmap imgTemp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(imgTemp);
        Paint paint = new Paint(); // 建立画笔
        paint.setDither(true);
        paint.setFilterBitmap(true);
        Rect src = new Rect(0, 0, width, height);
        Rect dst = new Rect(0, 0, width, height);
        canvas.drawBitmap(srcBitmap, src, dst, paint);
        int x_offset = -1;
        int y_offset = 2;
        if (sensorType.contains("pm")) {
            y_offset -= 5;
            x_offset += 2;
        } else if (sensorType.contains("alarm")) {
            x_offset -= 5;
            y_offset -= 5;
        } else if (sensorType.contains("smoke") || sensorType.contains("installed")) {
            y_offset -= 4;
            x_offset -= 0;
        } else if (sensorType.contains("cover")) {
            x_offset += 3;
            y_offset += 3;
        } else if (sensorType.contains("co") || sensorType.contains("co2")) {
            x_offset += 2;
            y_offset -= 5;
        } else if (sensorType.contains("ch4")) {
            y_offset -= 5;
        } else if (sensorType.contains("level") || sensorType.contains("distance")) {
            x_offset += -2;
        } else if (sensorType.contains("no2")) {
            x_offset += 2;
            y_offset -= 2;
        } else if (sensorType.contains("latitude") || sensorType.contains("longitude")) {
            y_offset += 2;
        } else if (sensorType.contains("yaw") || sensorType.contains("roll") || sensorType.contains("pitch")) {
            y_offset += 2;
        } else if (sensorType.contains("temperature") || sensorType.contains("humidity")) {
            y_offset += 2;
        }

        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG
                | Paint.DEV_KERN_TEXT_FLAG);
        textPaint.setTextSize(25.0f);
        textPaint.setTypeface(Typeface.DEFAULT_BOLD); // 采用默认的宽度
        textPaint.setColor(Color.WHITE);
        Matrix matrix = new Matrix();
        matrix.postScale(0.8f, 0.8f);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(targetBitmap, 0, 0, targetBitmap.getWidth(), targetBitmap.getHeight(),
                matrix,
                true);

        canvas.drawBitmap(newbm, width / 5 - x_offset, height / 5 - 5 - y_offset,
                textPaint);
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();

        return new BitmapDrawable(context.getResources(), imgTemp);

    }

    public static Bitmap tintBitmap(Bitmap inBitmap, int tintColor) {
        if (inBitmap == null) {
            return null;
        }

        Bitmap outBitmap = Bitmap.createBitmap(inBitmap.getWidth(), inBitmap.getHeight(), inBitmap.getConfig());
        Canvas canvas = new Canvas(outBitmap);
        Paint paint = new Paint();
        paint.setColorFilter(new PorterDuffColorFilter(tintColor, PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(inBitmap, 0, 0, paint);
        return outBitmap;
    }

    public static String getInspectionDeviceName(String deviceType) {
        //
        DeviceTypeStyles deviceTypeStyles = PreferencesHelper.getInstance().getConfigDeviceType(deviceType);
        if (deviceTypeStyles != null) {
            String category = deviceTypeStyles.getCategory();
            String mergeType = deviceTypeStyles.getMergeType();
            MergeTypeStyles mergeTypeStyles = PreferencesHelper.getInstance().getConfigMergeType(mergeType);
            if (mergeTypeStyles != null) {
                String name = mergeTypeStyles.getName();
                if (!TextUtils.isEmpty(category)) {
                    return name + category;
                }
                if (!TextUtils.isEmpty(name)) {
                    return name;
                }

            }

        }
        return SensoroCityApplication.getInstance().getResources().getString(R.string.unknown);


//        if (!TextUtils.isEmpty(deviceType)) {
//            List<DeviceTypeMutualModel.MergeTypeInfosBean> mergeTypeInfos = SensoroCityApplication.getInstance().mDeviceTypeMutualModel.getMergeTypeInfos();
//            if (mergeTypeInfos != null) {
//                for (DeviceTypeMutualModel.MergeTypeInfosBean mergeTypeInfosBean : mergeTypeInfos) {
//                    List<String> deviceTypes = mergeTypeInfosBean.getDeviceTypes();
//                    if (deviceType != null && deviceTypes.contains(deviceType)) {
//                        return mergeTypeInfosBean.getName();
//                    }
//                }
//            }
//        }
    }

    /**
     * 检测是否全面屏
     *
     * @param activity
     * @return
     */
    public static boolean navigationBarExist(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (Settings.Global.getInt(activity.getApplicationContext().getContentResolver(), "force_fsg_nav_bar", 0) != 0) {
                return false;
            }
        }

        final WindowManager windowManager = activity.getWindowManager();
        final Display d = windowManager.getDefaultDisplay();

        final DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            d.getRealMetrics(realDisplayMetrics);
        }

        int realHeight = realDisplayMetrics.heightPixels;
        int realWidth = realDisplayMetrics.widthPixels;

        final DisplayMetrics displayMetrics = new DisplayMetrics();
        d.getMetrics(displayMetrics);

        int displayHeight = displayMetrics.heightPixels;
        int displayWidth = displayMetrics.widthPixels;

        return (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
    }

    /**
     * 改变文字的颜色，及startDrawable的颜色，主要用于巡检任务，任务状态文本的改变
     *
     * @param context
     * @param tv
     * @param colorId
     * @param text
     */
    public static void changeTvState(Context context, TextView tv, int colorId, String text) {
        Resources resources = context.getResources();
        GradientDrawable gd = (GradientDrawable) resources.getDrawable(R.drawable.shape_small_oval_29c);
        gd.setBounds(0, 0, gd.getMinimumWidth(), gd.getMinimumHeight());
        int color = resources.getColor(colorId);
        gd.setColor(color);
        tv.setCompoundDrawables(gd, null, null, null);
        tv.setTextColor(color);
        tv.setText(text);
    }

    public static String handleMergeType(String deviceType) {
        DeviceTypeStyles deviceTypeStyles = PreferencesHelper.getInstance().getConfigDeviceType(deviceType);
        if (deviceTypeStyles != null) {
            return deviceTypeStyles.getMergeType();
        }
        return null;
    }

    public static String getDeviceMainTypeName(String deviceType) {
        String mergeType = handleMergeType(deviceType);
        MergeTypeStyles configMergeType = PreferencesHelper.getInstance().getConfigMergeType(mergeType);
        if (configMergeType != null) {
            String name = configMergeType.getName();
            if (!TextUtils.isEmpty(name)) {
                return name;
            }
        }
        return SensoroCityApplication.getInstance().getResources().getString(R.string.unknown);
    }

    public static String handlerNumber(String text) {
        if (!TextUtils.isEmpty(text)) {
            StringBuilder stringBuilder = new StringBuilder();
            if (text.length() > 3) {
                String t1 = text.substring(0, text.length() - 3);
                String t2 = text.substring(text.length() - 3);
                return stringBuilder.append(t1).append(",").append(t2).toString();
            }

        }
        return text;
    }

    public static String getFormatDouble(double d, int precision) {
        try {
            BigDecimal b = new BigDecimal(d);
            return b.setScale(precision, BigDecimal.ROUND_HALF_UP).toString();
        } catch (Exception e) {
            return String.valueOf(d);
        }
    }

    public static boolean isNewVersion(String oldVersion, String newVersion) {
        if (TextUtils.isEmpty(oldVersion) && TextUtils.isEmpty(newVersion)) {
            return false;
        } else {
            if (TextUtils.isEmpty(oldVersion)) {
                return !TextUtils.isEmpty(newVersion);
            } else {
                if (TextUtils.isEmpty(newVersion)) {
                    return false;
                } else {
                    try {
                        return compareVersion(newVersion, oldVersion) > 0;
                    } catch (Exception e) {
                        return false;
                    }
                }
            }
        }
    }

    public static boolean isContainVersion(String oldVersion, String newVersion) {
        if (TextUtils.isEmpty(oldVersion) && TextUtils.isEmpty(newVersion)) {
            return false;
        } else {
            if (TextUtils.isEmpty(oldVersion)) {
                return !TextUtils.isEmpty(newVersion);
            } else {
                if (TextUtils.isEmpty(newVersion)) {
                    return false;
                } else {
                    try {
                        return compareVersion(newVersion, oldVersion) >= 0;
                    } catch (Exception e) {
                        return false;
                    }
                }
            }
        }
    }

    /**
     * 版本号比较
     *
     * @param v1
     * @param v2
     * @return 0代表相等，1代表左边大，-1代表右边大
     * Utils.compareVersion("1.0.358_20180820090554","1.0.358_20180820090553")=1
     */
    public static int compareVersion(String v1, String v2) {
        if (v1.equals(v2)) {
            return 0;
        }
        String[] version1Array = v1.split("[._]");
        String[] version2Array = v2.split("[._]");
        int index = 0;
        int minLen = Math.min(version1Array.length, version2Array.length);
        long diff = 0;

        while (index < minLen
                && (diff = Long.parseLong(version1Array[index])
                - Long.parseLong(version2Array[index])) == 0) {
            index++;
        }
        if (diff == 0) {
            for (int i = index; i < version1Array.length; i++) {
                if (Long.parseLong(version1Array[i]) > 0) {
                    return 1;
                }
            }

            for (int i = index; i < version2Array.length; i++) {
                if (Long.parseLong(version2Array[i]) > 0) {
                    return -1;
                }
            }
            return 0;
        } else {
            return diff > 0 ? 1 : -1;
        }
    }

}
