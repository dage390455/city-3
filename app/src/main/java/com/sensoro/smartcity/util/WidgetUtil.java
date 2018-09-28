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
import com.sensoro.smartcity.server.bean.SensorStruct;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
                    valueTextView.setText(R.string.jinggai_true);
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
                    valueTextView.setText(R.string.collision_false);
                }
                unitTextView.setText("");
            } else if (sensorType.equals("alarm")) {
                isBoolean = true;
                Boolean isOk = (Boolean) value;
                if (isOk) {
                    valueTextView.setText(R.string.alarm_true);
                } else {
                    valueTextView.setText(R.string.alarm_false);
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
                    valueTextView.setText(R.string.alarm_true);
                } else {
                    valueTextView.setText(R.string.alarm_false);
                }
//                    unitTextView.setEditText(R.string.sensor_magnetic);
            } else if (sensorType.equals("manual_alarm")) {
                isBoolean = true;
                Boolean isTrue = (Boolean) value;
                if (isTrue) {
                    valueTextView.setText(R.string.alarm_true);
                } else {
                    valueTextView.setText(R.string.alarm_false);
                }
//                    unitTextView.setEditText(R.string.sensor_magnetic);
            } else if (sensorType.equals("sound_light_alarm")) {
                isBoolean = true;
                Boolean isTrue = (Boolean) value;
                if (isTrue) {
                    valueTextView.setText(R.string.alarm_true);
                } else {
                    valueTextView.setText(R.string.alarm_false);
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
        LogUtils.loge("pathname = " + pathname);
        File f = new File(pathname);
        if (f.exists()) f.delete();
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();

        } catch (IOException e) {
            return null;
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
            if (text.contains(".")) {
                SpannableString styledText = new SpannableString(text);
                styledText.setSpan(new TextAppearanceSpan(textView.getContext(), R.style.text_index_integer), 0,
                        text.lastIndexOf("."), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                styledText.setSpan(new TextAppearanceSpan(textView.getContext(), R.style.text_index_decimal), text
                        .lastIndexOf("."), text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                textView.setText(styledText, TextView.BufferType.SPANNABLE);
            } else {
                textView.setText(text);
            }
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
                valueTextView.setText(R.string.collision_false);
            }
            unitTextView.setText("");
        } else if (sensorType.equalsIgnoreCase("alarm")) {
            isBool = true;
            Boolean isTrue = (Boolean) value;
            if (isTrue) {
                valueTextView.setText(R.string.alarm_true);
            } else {
                valueTextView.setText(R.string.alarm_false);
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
                valueTextView.setText(R.string.alarm_true);
            } else {
                valueTextView.setText(R.string.alarm_false);
            }
            unitTextView.setText("");
        } else if (sensorType.equalsIgnoreCase("manual_alarm")) {
            isBool = true;
            Boolean isTrue = (Boolean) value;
            if (isTrue) {
                valueTextView.setText(R.string.alarm_true);
            } else {
                valueTextView.setText(R.string.alarm_false);
            }
            unitTextView.setText("");
        } else if (sensorType.equalsIgnoreCase("sound_light_alarm")) {
            isBool = true;
            Boolean isTrue = (Boolean) value;
            if (isTrue) {
                valueTextView.setText(R.string.alarm_true);
            } else {
                valueTextView.setText(R.string.alarm_false);
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


    public static String getSensorTypeChinese(String sensorType) {
        String value = "";
        if (sensorType.equalsIgnoreCase("temperature") || sensorType.equalsIgnoreCase("temp1")) {
            value = "温度";
        } else if (sensorType.equalsIgnoreCase("humidity")) {
            value = "湿度";
        } else if (sensorType.equalsIgnoreCase("co")) {
            value = "一氧化碳";
        } else if (sensorType.equalsIgnoreCase("co2")) {
            value = "二氧化碳";
        } else if (sensorType.equalsIgnoreCase("pm10")) {
            value = "PM10";
        } else if (sensorType.equalsIgnoreCase("pm2_5")) {
            value = "PM2.5";
        } else if (sensorType.equalsIgnoreCase("ch4")) {
            value = "甲烷";
        } else if (sensorType.equalsIgnoreCase("so2")) {
            value = "二氧化硫";
        } else if (sensorType.equalsIgnoreCase("no2")) {
            value = "二氧化氮";
        } else if (sensorType.equalsIgnoreCase("yaw")) {
            value = "偏航角";
        } else if (sensorType.equalsIgnoreCase("roll")) {
            value = "横滚角";
        } else if (sensorType.equalsIgnoreCase("pitch")) {
            value = "俯仰角";
        } else if (sensorType.equalsIgnoreCase("collision")) {
            value = "撞击";
        } else if (sensorType.equalsIgnoreCase("distance")) {
            value = "距离水位";
        } else if (sensorType.equalsIgnoreCase("light")) {
            value = "光线";
        } else if (sensorType.equalsIgnoreCase("cover")) {
            value = "井盖";
        } else if (sensorType.equalsIgnoreCase("level")) {
            value = "水位";
        } else if (sensorType.equalsIgnoreCase("drop")) {
            value = "滴漏";
        } else if (sensorType.equalsIgnoreCase("smoke")) {
            value = "烟感";
        } else if (sensorType.equalsIgnoreCase("altitude")) {
            value = "高度";
        } else if (sensorType.equalsIgnoreCase("latitude")) {
            value = "纬度";
        } else if (sensorType.equalsIgnoreCase("longitude")) {
            value = "经度";
        } else if (sensorType.equalsIgnoreCase("alarm")) {
            value = "紧急报警器";
        } else if (sensorType.equalsIgnoreCase("lpg")) {
            value = "液化石油气";
        } else if (sensorType.equalsIgnoreCase("flame")) {
            value = "火焰";
        } else if (sensorType.equalsIgnoreCase("artificialGas")) {
            value = "人工煤气";
        } else if (sensorType.equalsIgnoreCase("waterPressure")) {
            value = "消防液压";
        } else if (sensorType.equalsIgnoreCase("magnetic")) {
            value = "地磁";
        } else if (sensorType.equalsIgnoreCase("door")) {
            value = "门锁检测";
        } else if (sensorType.equalsIgnoreCase("CURRENT_A")) {
            value = "电流A";
        } else if (sensorType.equalsIgnoreCase("CURRENT_B")) {
            value = "电流B";
        } else if (sensorType.equalsIgnoreCase("CURRENT_C")) {
            value = "电流C";
        } else if (sensorType.equalsIgnoreCase("ID")) {
            value = "电表ID";
        } else if (sensorType.equalsIgnoreCase("TOTAL_POWER")) {
            value = "总电量";
        } else if (sensorType.equalsIgnoreCase("VOLTAGE_A")) {
            value = "电压A";
        } else if (sensorType.equalsIgnoreCase("VOLTAGE_B")) {
            value = "电压B";
        } else if (sensorType.equalsIgnoreCase("VOLTAGE_C")) {
            value = "电压C";
        } else if (sensorType.equalsIgnoreCase("installed")) {
            value = "安装状态";
        } else if (sensorType.equalsIgnoreCase("leakage_val")) {
            value = "漏电流";
        } else if (sensorType.equalsIgnoreCase("temp_val")) {
            value = "电线温度";
        } else if (sensorType.equalsIgnoreCase("infrared")) {
            value = "红外线";
        } else if (sensorType.equalsIgnoreCase("manual_alarm")) {
            value = "手动报警";
        } else if (sensorType.equalsIgnoreCase("sound_light_alarm")) {
            value = "声光报警";
        } else if (sensorType.equalsIgnoreCase("connection")) {
            value = "通断检测";
        }
        //CURRENT_A|CURRENT_B|CURRENT_C|ID|TOTAL_POWER|VOLTAGE_A|VOLTAGE_B|VOLTAGE_C

        return value;
    }

    public static String getSensorTypeSingleChinese(String sensorType) {
        String value;
        if (sensorType.equalsIgnoreCase("temperature")) {
            value = "温度";
        } else if (sensorType.equalsIgnoreCase("temp1")) {
            value = "温度贴片";
        } else if (sensorType.equalsIgnoreCase("humidity")) {
            value = "湿度";
        } else if (sensorType.equalsIgnoreCase("co")) {
            value = "一氧化碳";
        } else if (sensorType.equalsIgnoreCase("co2")) {
            value = "二氧化碳";
        } else if (sensorType.equalsIgnoreCase("pm10")) {
            value = "PM10";
        } else if (sensorType.equalsIgnoreCase("pm2_5")) {
            value = "PM2.5";
        } else if (sensorType.equalsIgnoreCase("ch4")) {
            value = "甲烷";
        } else if (sensorType.equalsIgnoreCase("so2")) {
            value = "二氧化硫";
        } else if (sensorType.equalsIgnoreCase("no2")) {
            value = "二氧化氮";
        } else if (sensorType.equalsIgnoreCase("yaw")) {
            value = "偏航角";
        } else if (sensorType.equalsIgnoreCase("roll")) {
            value = "横滚角";
        } else if (sensorType.equalsIgnoreCase("pitch")) {
            value = "俯仰角";
        } else if (sensorType.equalsIgnoreCase("collision")) {
            value = "撞击";
        } else if (sensorType.equalsIgnoreCase("distance")) {
            value = "距离水位";
        } else if (sensorType.equalsIgnoreCase("light")) {
            value = "光线";
        } else if (sensorType.equalsIgnoreCase("cover")) {
            value = "井盖";
        } else if (sensorType.equalsIgnoreCase("level")) {
            value = "水位";
        } else if (sensorType.equalsIgnoreCase("drop")) {
            value = "滴漏";
        } else if (sensorType.equalsIgnoreCase("smoke")) {
            value = "烟感";
        } else if (sensorType.equalsIgnoreCase("altitude")) {
            value = "高度";
        } else if (sensorType.equalsIgnoreCase("latitude")) {
            value = "纬度";
        } else if (sensorType.equalsIgnoreCase("longitude")) {
            value = "经度";
        } else if (sensorType.equalsIgnoreCase("alarm")) {
            value = "紧急报警器";
        } else if (sensorType.equalsIgnoreCase("lpg")) {
            value = "液化石油气";
        } else if (sensorType.equalsIgnoreCase("flame")) {
            value = "火焰";
        } else if (sensorType.equalsIgnoreCase("artificialGas")) {
            value = "人工煤气";
        } else if (sensorType.equalsIgnoreCase("waterPressure")) {
            value = "消防液压";
        } else if (sensorType.equalsIgnoreCase("magnetic")) {
            value = "地磁";
        } else if (sensorType.equalsIgnoreCase("door")) {
            value = "门锁检测";
        } else if (sensorType.equalsIgnoreCase("CURRENT_A")) {
            value = "电流A";
        } else if (sensorType.equalsIgnoreCase("CURRENT_B")) {
            value = "电流B";
        } else if (sensorType.equalsIgnoreCase("CURRENT_C")) {
            value = "电流C";
        } else if (sensorType.equalsIgnoreCase("ID")) {
            value = "电表ID";
        } else if (sensorType.equalsIgnoreCase("TOTAL_POWER")) {
            value = "总电量";
        } else if (sensorType.equalsIgnoreCase("VOLTAGE_A")) {
            value = "电压A";
        } else if (sensorType.equalsIgnoreCase("VOLTAGE_B")) {
            value = "电压B";
        } else if (sensorType.equalsIgnoreCase("VOLTAGE_C")) {
            value = "电压C";
        } else if (sensorType.equalsIgnoreCase("installed")) {
            value = "安装状态";
        } else if (sensorType.equalsIgnoreCase("leakage_val")) {
            value = "漏电流";
        } else if (sensorType.equalsIgnoreCase("temp_val")) {
            value = "电线温度";
        } else if (sensorType.equalsIgnoreCase("infrared")) {
            value = "红外线";
        } else if (sensorType.equalsIgnoreCase("manual_alarm")) {
            value = "手动报警";
        } else if (sensorType.equalsIgnoreCase("sound_light_alarm")) {
            value = "声光报警";
        } else if (sensorType.equalsIgnoreCase("connection")) {
            value = "通断检测";
        } else {
            value = "未知";
        }

        //CURRENT_A|CURRENT_B|CURRENT_C|ID|TOTAL_POWER|VOLTAGE_A|VOLTAGE_B|VOLTAGE_C

        return value;
    }


    /**
     * 区分联系人
     *
     * @return
     */
    public static String distinguishContacts(String source) {
        if (!TextUtils.isEmpty(source)) {
            switch (source) {
                case "attach":
                    return "单独联系人";
                case "group":
                    return "分组联系人";
                case "notification":
                    return "账户联系人";
                default:
                    return "";
            }
        }
        return "";

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
     * 判断设备是什么类型
     *
     * @param context
     * @param sensorTypes
     * @return
     */
    public static String parseSensorTypes(Context context, List<String> sensorTypes) {
        if (sensorTypes.size() > 1) {
            if (sensorTypes.contains("temp1")) {
                return "温度贴片";
            }
            if (sensorTypes.contains("temperature") || sensorTypes.contains("light")) {
                return "温湿度";
            } else if (sensorTypes.contains("cover") || sensorTypes.contains("level")) {
                return "井位";
            } else if (sensorTypes.contains("pm") || sensorTypes.contains("pm2_5")) {
                return "PM2.5/PM10";
            } else if (sensorTypes.contains("pitch") || sensorTypes.contains("roll") || sensorTypes.contains("yaw")) {
                return "倾角传";
            } else if (sensorTypes.contains("latitude") || sensorTypes.contains("altitude") || sensorTypes.contains("longitude")) {
                return "追踪器";
            } else if (sensorTypes.contains("CURRENT") || sensorTypes.contains("VOLTAGE") || sensorTypes.contains("TOTAL_POWER")) {
                return "电表";
            } else if (sensorTypes.contains("installed") && sensorTypes.contains("smoke")) {
                return "烟感";
            } else if (sensorTypes.contains("curr_val") || sensorTypes.contains("elec_energy_val") || sensorTypes.contains("leakage_val")
                    || sensorTypes.contains("temp_val")) {
                return "电气火灾";
            } else {
                return context.getString(R.string.unknown);
            }
        } else {
            String sensorType = sensorTypes.get(0);
            if (sensorType.equals("temp1")) {
                return "温度贴片";
            }
            if (sensorType.equals("light") || sensorType.equals("temperature")) {
                return "温湿度";
            } else if (sensorType.equals("pitch") || sensorType.equals("roll") || sensorType.equals("yaw")) {
                return "倾角";
            } else if (sensorType.equals("cover") || sensorType.equals("level")) {
                return "井位";
            } else if (sensorType.equals("pm2_5") || sensorType.equals("pm10")) {
                return "PM2.5/PM10";
            } else if (sensorType.equals("ch4")) {
                return "甲烷";
            } else if (sensorType.equals("co")) {
                return "一氧化碳";
            } else if (sensorType.equals("co2")) {
                return "二氧化碳";
            } else if (sensorType.equals("leak")) {
                return "跑冒滴漏";
            } else if (sensorType.equals("smoke")) {
                return "烟感";
            } else if (sensorType.equalsIgnoreCase("installed")) {
                return "安装状态";
            } else if (sensorType.equals("lpg")) {
                return "液化石油气";
            } else if (sensorType.equals("no2")) {
                return "二氧化氮";
            } else if (sensorType.equals("so2")) {
                return "二氧化硫";
            } else if (sensorType.equals("artificialGas")) {
                return "人工煤气";
            } else if (sensorType.equals("waterPressure")) {
                return "消防液压";
            } else if (sensorType.equals("magnetic")) {
                return "地磁";
            } else if (sensorType.equals("door")) {
                return "门锁检测";
            } else if (sensorType.equals("flame")) {
                return "火焰";
            } else if (sensorType.equalsIgnoreCase("cover")) {
                return "井盖";
            } else if (sensorType.equalsIgnoreCase("level")) {
                return "水位";
            } else if (sensorType.equalsIgnoreCase("drop")) {
                return "跑冒滴漏";
            } else if (sensorType.equalsIgnoreCase("smoke")) {
                return "烟感";
            } else if (sensorType.equalsIgnoreCase("installed")) {
                return "安装状态";
            } else if (sensorType.equalsIgnoreCase("altitude")) {
                return "追踪器";
            } else if (sensorType.equalsIgnoreCase("latitude")) {
                return "追踪器";
            } else if (sensorType.equalsIgnoreCase("longitude")) {
                return "追踪器";
            } else if (sensorType.equalsIgnoreCase("alarm")) {
                return "紧急呼叫";
            } else if (sensorType.equalsIgnoreCase("distance")) {
                return "距离水位";
            } else if (sensorType.equalsIgnoreCase("infrared")) {
                return "红外线";
            } else if (sensorType.equalsIgnoreCase("manual_alarm")) {
                return "手动报警";
            } else if (sensorType.equalsIgnoreCase("sound_light_alarm")) {
                return "声光报警";
            } else if (sensorType.equalsIgnoreCase("connection")) {
                return "通断检测";
            } else {
                return context.getString(R.string.unknown);
            }
        }
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

    public static String getBooleanAlarm(String sensorType) {
        switch (sensorType) {
            case "installed":
                //TODO 目前只有烟感存在此属性 以后扩展需要分离
                return "被拆卸,烟雾浓度高时报警";
            case "alarm":
                return "发生报警时报警";
            case "flame":
                return "监测到火焰时报警";
            case "collision":
                return "发生撞击时报警";
            case "drop":
                return "发生滴漏时报警";
            case "level":
                return "溢出时报警";
            case "magnetic":
                return "车辆经过时报警";
            case "smoke":
                return "烟雾浓度高时报警";
            case "jinggai":
            case "cover":
            case "door":
                return "打开时报警";
            case "connection":
                return "断开时报警";
            case "infrared":
                return "感应到物体时报警";
            case "manual_alarm":
                return "触发手动报警时报警";
            case "sound_light_alarm":
                return "发生声光报警时报警";
            default:
                return null;
        }
    }

    public static boolean needDrawKLayout(String sensoroType) {
        switch (sensoroType) {
//            "level","cover","flame","alarm","smoke","drop":
            case "level":
            case "cover":
            case "flame":
            case "alarm":
            case "smoke":
            case "drop":
            case "magnetic":
            case "door":
            case "collision":
            case "installed":
            case "infrared":
            case "manual_alarm":
            case "sound_light_alarm":
            case "connection":
                return false;
            default:
                return true;
        }
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
    //TODO dont del

    //    public static String getSensorTypesChinese(List<String> sensorType) {
//        if (sensorType.contains("temperature") || sensorType.contains("humidity") || sensorType.contains("temp1")) {
//            return "温湿度";
//        } else if (sensorType.contains("pm2_5") || sensorType.contains("pm10")) {
//            return "PM10/2.5";
//        } else if (sensorType.contains("latitude") || sensorType.contains("longitude")) {
//            return "经纬度";
//        } else if (sensorType.contains("level") || sensorType.contains("cover")) {
//            return "井盖";
//        } else if (sensorType.contains("pitch") || sensorType.contains("roll") || sensorType.contains("collision")) {
//            return "角度";
//        } else if (sensorType.contains("temp_val") || sensorType.contains("leakage_val")) {
//            return "电气火灾";
//        } else if (sensorType.contains("infrared")) {
//            return "红外线";
//        } else if (sensorType.contains("manual_alarm")) {
//            return "手动报警";
//        } else if (sensorType.contains("sound_light_alarm")) {
//            return "声光报警";
//        } else if (sensorType.contains("connection")) {
//            return "通断检测";
//        } else if (sensorType.contains("door")) {
//            return "门锁检测";
//        } else if (sensorType.contains("magnetic")) {
//            return "地磁";
//        } else if (sensorType.contains("co")) {
//            return "一氧化碳";
//        } else if (sensorType.contains("co")) {
//            return "一氧化碳";
//        } else {
//            return "-";
//        }
//    }

    //    public static String getContractDeviceTypeChinese(String deviceType) {
//        switch (deviceType) {
//            case "smoke":
//                return "烟雾传感器";
//            case "co":
//                return "一氧化碳传感器";
//            default:
//                return "Sensoro传感器";
//        }
//    }
    //
//    public static BitmapDrawable createBitmapDrawable(Context context, Bitmap imgMarker, String text) {
//        int width = imgMarker.getWidth();
//        int height = imgMarker.getHeight();
//        Bitmap imgTemp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(imgTemp);
//        Paint paint = new Paint(); // 建立画笔
//        paint.setDither(true);
//        paint.setFilterBitmap(true);
//        Rect src = new Rect(0, 0, width, height);
//        Rect dst = new Rect(0, 0, width, height);
//        canvas.drawBitmap(imgMarker, src, dst, paint);
//
//        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG
//                | Paint.DEV_KERN_TEXT_FLAG);
//        textPaint.setTextSize(25.0f);
//        textPaint.setTypeface(Typeface.DEFAULT_BOLD); // 采用默认的宽度
//        textPaint.setColor(Color.WHITE);
//
//        canvas.drawText(text, width / 2 - 5, height / 2 + 5,
//                textPaint);
//        canvas.save(Canvas.ALL_SAVE_FLAG);
//        canvas.restore();
//        return new BitmapDrawable(context.getResources(), imgTemp);
//
//    }
//
//    public static BitmapDrawable createBigBitmapDrawable(Context context, String sensorType, Bitmap srcBitmap, Bitmap
//            targetBitmap) {
//        int width = srcBitmap.getWidth();
//        int height = srcBitmap.getHeight();
//        int x_offset = 0;
//        int y_offset = 0;
//        if (sensorType.contains("pm")) {
//            y_offset = -5;
//            x_offset = -5;
//        } else if (sensorType.contains("distance")) {
//            x_offset = -10;
//            y_offset = -5;
//        } else if (sensorType.contains("alarm")) {
//            x_offset = -8;
//            y_offset = -5;
//        }
//        Bitmap imgTemp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(imgTemp);
//        Paint paint = new Paint(); // 建立画笔
//        paint.setDither(true);
//        paint.setFilterBitmap(true);
//        Rect src = new Rect(0, 0, width, height);
//        Rect dst = new Rect(0, 0, width, height);
//        canvas.drawBitmap(srcBitmap, src, dst, paint);
//
//        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG
//                | Paint.DEV_KERN_TEXT_FLAG);
//        textPaint.setTextSize(25.0f);
//        textPaint.setTypeface(Typeface.DEFAULT_BOLD); // 采用默认的宽度
//        textPaint.setColor(Color.WHITE);
//        canvas.drawBitmap(targetBitmap, width / 4 - x_offset, height / 4 - y_offset,
//                textPaint);
//        canvas.save(Canvas.ALL_SAVE_FLAG);
//        canvas.restore();
//        return new BitmapDrawable(context.getResources(), imgTemp);
//
//    }
    //
//    public static void judgeSensorType(Context context, ImageView srcImageView, TextView valueTextView, TextView
//            unitTextView, String sensorType, Object value, String unit) {
//        boolean isBool = false;
//        int x_ = context.getResources().getDimensionPixelSize(R.dimen.x300);
//        int y_ = context.getResources().getDimensionPixelSize(R.dimen.y400);
//        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(210, 210);
//        int pixel = -30;
//        int bottom_pixel = -40;
//        int default_pixel = -30;
//        srcImageView.setVisibility(View.VISIBLE);
//        if (sensorType.equalsIgnoreCase("co")) {
//            srcImageView.setImageResource(R.mipmap.ic_sensor_bg_co);
//            layoutParams.setMargins(0, 0, pixel, default_pixel);
//        } else if (sensorType.equalsIgnoreCase("co2")) {
//            srcImageView.setImageResource(R.mipmap.ic_sensor_bg_co2);
//            layoutParams.setMargins(0, 0, pixel, default_pixel);
//        } else if (sensorType.equalsIgnoreCase("no2")) {
//            srcImageView.setImageResource(R.mipmap.ic_sensor_bg_no2);
//            layoutParams.setMargins(0, 0, pixel, default_pixel);
//        } else if (sensorType.equalsIgnoreCase("pm2_5") || sensorType.equalsIgnoreCase("pm10")) {
//            srcImageView.setImageResource(R.mipmap.ic_sensor_bg_pm);
//            layoutParams.setMargins(0, 0, pixel, default_pixel);
//        } else if (sensorType.equalsIgnoreCase("ch4")) {
//            srcImageView.setImageResource(R.mipmap.ic_sensor_bg_ch4);
//            layoutParams.setMargins(0, 0, pixel, default_pixel);
//        } else if (sensorType.equalsIgnoreCase("temperature") || sensorType.equalsIgnoreCase("humidity")) {
//            srcImageView.setImageResource(R.mipmap.ic_sensor_bg_temp_humi);
//            layoutParams.setMargins(0, 0, pixel, bottom_pixel);
//        } else if (sensorType.equalsIgnoreCase("smoke")) {
//            srcImageView.setImageResource(R.mipmap.ic_sensor_bg_smoke);
//            isBool = true;
//            Boolean isTrue = (Boolean) value;
//            if (isTrue) {
//                valueTextView.setText(R.string.smoke_true);
//            } else {
//                valueTextView.setText(R.string.smoke_false);
//            }
//            layoutParams.setMargins(0, 0, pixel, default_pixel);
//            unitTextView.setText("");
//        } else if (sensorType.equalsIgnoreCase("installed")) {
//            srcImageView.setImageResource(R.mipmap.ic_sensor_bg_smoke);
//            isBool = true;
//            Boolean isTrue = (Boolean) value;
//            if (isTrue) {
//                valueTextView.setText(R.string.installed);
//            } else {
//                valueTextView.setText(R.string.un_installed);
//            }
//            layoutParams.setMargins(0, 0, pixel, default_pixel);
//            unitTextView.setText("");
//        } else if (sensorType.equalsIgnoreCase("distance")) {
//            srcImageView.setImageResource(R.mipmap.ic_sensor_bg_level);
//            layoutParams.setMargins(0, 0, pixel, bottom_pixel);
//        } else if (sensorType.equalsIgnoreCase("level")) {
//            srcImageView.setImageResource(R.mipmap.ic_sensor_bg_level);
//            Boolean isTrue = (Boolean) value;
//            isBool = true;
//            if (isTrue) {
//                valueTextView.setText(R.string.level_true);
//            } else {
//                valueTextView.setText(R.string.level_false);
//            }
//            layoutParams.setMargins(0, 0, pixel, bottom_pixel);
//            unitTextView.setText("");
//        } else if (sensorType.equalsIgnoreCase("cover")) {
//            srcImageView.setImageResource(R.mipmap.ic_sensor_bg_cover);
//            Boolean isTrue = (Boolean) value;
//            isBool = true;
//            if (isTrue) {
//                valueTextView.setText(R.string.cover_true);
//            } else {
//                valueTextView.setText(R.string.cover_false);
//            }
//            layoutParams.setMargins(0, 0, pixel, bottom_pixel);
//            unitTextView.setText("");
//        } else if (sensorType.equalsIgnoreCase("drop")) {
//            srcImageView.setImageResource(R.mipmap.ic_sensor_bg_drop);
//            isBool = true;
//            if (value instanceof Double) {
//                double d = (double) value;
//                if (d == 0) {
//                    valueTextView.setText(R.string.drop_false);
//                } else {
//                    valueTextView.setText(R.string.drop_true);
//                }
//                layoutParams.setMargins(0, 0, pixel, bottom_pixel);
//                unitTextView.setText("");
//            }
//        } else if (sensorType.equalsIgnoreCase("light")) {
//            srcImageView.setImageResource(R.mipmap.ic_sensor_bg_light);
//            layoutParams.setMargins(0, 0, pixel, bottom_pixel);
//        } else if (sensorType.equalsIgnoreCase("waterPressure")) {
//            srcImageView.setImageResource(R.mipmap.ic_sensor_bg_water_pressure);
//            layoutParams.setMargins(0, 0, pixel, bottom_pixel);
//        } else if (sensorType.equalsIgnoreCase("magnetic")) {
//            isBool = true;
//            Boolean isTrue = (Boolean) value;
//            if (isTrue) {
//                valueTextView.setText(R.string.magnetic_true);
//            } else {
//                valueTextView.setText(R.string.magnetic_false);
//            }
//            unitTextView.setText("");
//            srcImageView.setImageResource(R.mipmap.ic_sensor_bg_magnetic);
//            layoutParams.setMargins(0, 0, pixel, bottom_pixel);
//        } else if (sensorType.equalsIgnoreCase("door")) {
//            isBool = true;
//            Boolean isTrue = (Boolean) value;
//            if (isTrue) {
//                valueTextView.setText(R.string.magnetic_false);
//            } else {
//                valueTextView.setText(R.string.magnetic_true);
//            }
//            unitTextView.setText("");
//            srcImageView.setImageResource(R.mipmap.ic_sensor_bg_lock);
//            layoutParams.setMargins(0, 0, pixel, bottom_pixel);
//        } else if (sensorType.equalsIgnoreCase("connection")) {
//            srcImageView.setImageResource(R.mipmap.ic_sensor_connection_bg);
//            isBool = true;
//            Boolean isTrue = (Boolean) value;
//            if (isTrue) {
//                valueTextView.setText(R.string.connection_true);
//            } else {
//                valueTextView.setText(R.string.connection_false);
//            }
//            layoutParams.setMargins(0, 0, pixel, default_pixel);
//            unitTextView.setText("");
//        } else if (sensorType.equalsIgnoreCase("lpg")) {
//            srcImageView.setImageResource(R.mipmap.ic_sensor_bg_lpg);
//            layoutParams.setMargins(0, 0, pixel, bottom_pixel);
//        } else if (sensorType.equalsIgnoreCase("roll") || sensorType.equalsIgnoreCase("yaw") || sensorType
//                .equalsIgnoreCase("pitch") || sensorType.equalsIgnoreCase("angle")) {
//            srcImageView.setImageResource(R.mipmap.ic_sensor_bg_angle);
//            layoutParams.setMargins(0, 0, pixel, bottom_pixel);
//        } else if (sensorType.equalsIgnoreCase("collision")) {
//            srcImageView.setImageResource(R.mipmap.ic_sensor_bg_angle);
//            isBool = true;
//            Boolean isTrue = (Boolean) value;
//            if (isTrue) {
//                valueTextView.setText(R.string.collision_true);
//            } else {
//                valueTextView.setText(R.string.collision_false);
//            }
//            unitTextView.setText("");
//            layoutParams.setMargins(0, 0, pixel, bottom_pixel);
//        } else if (sensorType.equalsIgnoreCase("alarm")) {//alarm
//            srcImageView.setImageResource(R.mipmap.ic_sensor_bg_call);
//            isBool = true;
//            Boolean isTrue = (Boolean) value;
//            if (isTrue) {
//                valueTextView.setText(R.string.alarm_true);
//            } else {
//                valueTextView.setText(R.string.alarm_false);
//            }
//            unitTextView.setText("");
//            layoutParams.setMargins(0, 0, pixel, bottom_pixel);
//        } else if (sensorType.equalsIgnoreCase("flame")) { //
//            srcImageView.setImageResource(R.mipmap.ic_sensor_bg_flame);
//            isBool = true;
//            Boolean isTrue = (Boolean) value;
//            if (isTrue) {
//                valueTextView.setText(R.string.flame_true);
//            } else {
//                valueTextView.setText(R.string.flame_false);
//            }
//            unitTextView.setText("");
//            layoutParams.setMargins(0, 0, pixel, bottom_pixel);
//        } else if (sensorType.equals("latitude") || sensorType.equals("longitude") || sensorType.equals("altitude")) {
//            srcImageView.setImageResource(R.mipmap.ic_sensor_bg_tracker);
//            layoutParams.setMargins(0, 0, pixel, bottom_pixel);
//        } else if (sensorType.equalsIgnoreCase("infrared")) {
//            srcImageView.setImageResource(R.mipmap.ic_sensor_infrared_bg);
//            Boolean isTrue = (Boolean) value;
//            isBool = true;
//            if (isTrue) {
//                valueTextView.setText(R.string.alarm_true);
//            } else {
//                valueTextView.setText(R.string.alarm_false);
//            }
//            layoutParams.setMargins(0, 0, pixel, bottom_pixel);
//            unitTextView.setText("");
//        } else if (sensorType.equalsIgnoreCase("manual_alarm")) {
//            srcImageView.setImageResource(R.mipmap.ic_sensor_manual_alarm_bg);
//            Boolean isTrue = (Boolean) value;
//            isBool = true;
//            if (isTrue) {
//                valueTextView.setText(R.string.alarm_true);
//            } else {
//                valueTextView.setText(R.string.alarm_false);
//            }
//            layoutParams.setMargins(0, 0, pixel, bottom_pixel);
//            unitTextView.setText("");
//        } else if (sensorType.equalsIgnoreCase("sound_light_alarm")) {
//            srcImageView.setImageResource(R.mipmap.ic_sensor_sound_light_alarm_bg);
//            Boolean isTrue = (Boolean) value;
//            isBool = true;
//            if (isTrue) {
//                valueTextView.setText(R.string.alarm_true);
//            } else {
//                valueTextView.setText(R.string.alarm_false);
//            }
//            layoutParams.setMargins(0, 0, pixel, bottom_pixel);
//            unitTextView.setText("");
//        } else {
//            srcImageView.setVisibility(View.GONE);
//        }
//        srcImageView.setLayoutParams(layoutParams);
//        //
//        if (!isBool && !(value instanceof Boolean)) {
//            //
//            if (sensorType.equalsIgnoreCase("longitude") || sensorType.equalsIgnoreCase("latitude")) {
//                DecimalFormat df = new DecimalFormat("###.##");
//                setIndexTextStyle(df.format(value), valueTextView);
//
//            } else if (sensorType.equalsIgnoreCase("co") || sensorType.equalsIgnoreCase("temperature") || sensorType
//                    .equalsIgnoreCase
//                            ("humidity") || sensorType.equalsIgnoreCase("waterPressure") || sensorType
//                    .equalsIgnoreCase("no2") || sensorType.equalsIgnoreCase("temp1")) {
//                DecimalFormat df = new DecimalFormat("###.#");
//                LogUtils.loge("value = " + value);
//                String format = df.format(value);
//                setIndexTextStyle(format, valueTextView);
//            } else {
//                valueTextView.setText(String.format("%.0f", Double.valueOf(value
//                        .toString())));
//            }
//            unitTextView.setText(unit);
//            //
//        }
//    }

    ///////////////////////
    //    public static void judgeSensor(SensorStruct sensorStruct, TextView valueTextView, TextView unitTextView) {
//        boolean isBool = false;
//        Object value = sensorStruct.getValue();
//        if (sensorStruct.getSensorType().equalsIgnoreCase("co")) {
//        } else if (sensorStruct.getSensorType().equalsIgnoreCase("co2")) {
//        } else if (sensorStruct.getSensorType().equalsIgnoreCase("so2")) {
//        } else if (sensorStruct.getSensorType().equalsIgnoreCase("no2")) {
//        } else if (sensorStruct.getSensorType().equalsIgnoreCase("pm2_5") || sensorStruct.getSensorType()
//                .equalsIgnoreCase("pm10")) {
//
//        } else if (sensorStruct.getSensorType().equalsIgnoreCase("temperature") || sensorStruct.getSensorType()
//                .equalsIgnoreCase("humidity")) {
//        } else if (sensorStruct.getSensorType().equalsIgnoreCase("smoke")) {
//            isBool = true;
//            Boolean isTrue = (Boolean) value;
//            if (isTrue) {
//                valueTextView.setText(R.string.smoke_true);
//            } else {
//                valueTextView.setText(R.string.smoke_false);
//            }
//            unitTextView.setText("");
//        } else if (sensorStruct.getSensorType().equalsIgnoreCase("leak")) {
//        } else if (sensorStruct.getSensorType().equalsIgnoreCase("cover") || sensorStruct.getSensorType()
//                .equalsIgnoreCase("level") || sensorStruct.getSensorType().equalsIgnoreCase("jinggai")) {
//            Boolean isTrue = (Boolean) value;
//            isBool = true;
//            if (isTrue) {
//                valueTextView.setText(R.string.cover_true);
//            } else {
//                valueTextView.setText(R.string.cover_false);
//            }
//            unitTextView.setText("");
//        } else if (sensorStruct.getSensorType().equalsIgnoreCase("ch4")) {
//        } else if (sensorStruct.getSensorType().equalsIgnoreCase("drop")) {
//            isBool = true;
//            Boolean isTrue = (Boolean) value;
//            if (isTrue) {
//                valueTextView.setText(R.string.drop_true);
//            } else {
//                valueTextView.setText(R.string.drop_false);
//            }
//            unitTextView.setText("");
//        } else if (sensorStruct.getSensorType().equalsIgnoreCase("light")) {
//        } else if (sensorStruct.getSensorType().equalsIgnoreCase("distance")) {
//        } else if (sensorStruct.getSensorType().equalsIgnoreCase("lpg")) {
//        } else if (sensorStruct.getSensorType().equalsIgnoreCase("roll") || sensorStruct.getSensorType()
//                .equalsIgnoreCase("yaw") || sensorStruct.getSensorType().equalsIgnoreCase("pitch") || sensorStruct
//                .getSensorType().equalsIgnoreCase("angle")) {
//        } else if (sensorStruct.getSensorType().equalsIgnoreCase("collision")) {
//            isBool = true;
//            Boolean isTrue = (Boolean) value;
//            if (isTrue) {
//                valueTextView.setText(R.string.collision_true);
//            } else {
//                valueTextView.setText(R.string.collision_false);
//            }
//            unitTextView.setText("");
//        } else if (sensorStruct.getSensorType().equalsIgnoreCase("alarm")) {//alarm
//            isBool = true;
//            Boolean isTrue = (Boolean) value;
//            if (isTrue) {
//                valueTextView.setText(R.string.alarm_true);
//            } else {
//                valueTextView.setText(R.string.alarm_false);
//            }
//            unitTextView.setText("");
//        } else if (sensorStruct.getSensorType().equalsIgnoreCase("flame")) { //
//            isBool = true;
//            Boolean isTrue = (Boolean) value;
//            if (isTrue) {
//                valueTextView.setText(R.string.flame_true);
//            } else {
//                valueTextView.setText(R.string.flame_false);
//            }
//            unitTextView.setText("");
//        } else if (sensorStruct.getSensorType().equals("magnetic")) {
//            isBool = true;
//            Boolean isTrue = (Boolean) value;
//            if (isTrue) {
//                valueTextView.setText(R.string.magnetic_true);
//            } else {
//                valueTextView.setText(R.string.magnetic_false);
//            }
//            unitTextView.setText("");
//        } else if (sensorStruct.getSensorType().equals("waterPressure")) {
//        } else if (sensorStruct.getSensorType().equals("artificialGas")) {
//        } else if (sensorStruct.getSensorType().equals("latitude") || sensorStruct.getSensorType().equals
//                ("longitude") || sensorStruct.getSensorType().equals("altitude")) {
//        }
//
//        if (!isBool) {
//            valueTextView.setText("" + String.format("%.0f", Double.valueOf(value.toString())));
//            unitTextView.setText(sensorStruct.getUnit());
//        }
//    }


//    public static String getAlarmDetailInfo(String sensorType, int status) {
//        String info = null;
//        if (status == 0) {
//            switch (sensorType) {
//                case "smoke":
//                    info = "无烟，恢复正常";
//                    break;
//                case "cover":
//                    info = "井盖闭合，恢复正常";
//                    break;
//                case "jinggai":
//                    info = "井盖闭合，恢复正常";
//                    break;
//                case "level":
//                    info = "水位未溢出, 恢复正常";
//                    break;
//                case "alarm":
//                    info = "紧急呼叫解除，恢复正常";
//                    break;
//                case "flame":
//                    info = "未检测到火焰，恢复正常";
//                    break;
//                case "collision":
//                    info = "碰撞解除，恢复正常";
//                    break;
//                case "drop":
//                    info = "未检测到滴漏，恢复正常";
//                    break;
//                case "leak":
//                    info = "未检测到滴漏，恢复正常";
//                    break;
//                case "door":
//                    info = "门锁关闭，恢复正常";
//                    break;
//                default:
//                    info = "低于预警值，恢复正常";
//                    break;
//            }
//        } else {
//            switch (sensorType) {
//                case "smoke":
//                    info = "烟雾浓度高，设备预警";
//                    break;
//                case "cover":
//                    info = "井盖打开，设备预警";
//                    break;
//                case "jinggai":
//                    info = "井盖打开，设备预警";
//                    break;
//                case "level":
//                    info = "水位溢出, 设备预警";
//                    break;
//                case "alarm":
//                    info = "触发紧急呼叫，设备预警";
//                    break;
//                case "flame":
//                    info = "检测到火焰，设备预警";
//                    break;
//                case "collision":
//                    info = "碰撞解除，恢复正常";
//                    break;
//                case "drop":
//                    info = "发生滴漏，设备预警";
//                    break;
//                case "leak":
//                    info = "发生滴漏，设备预警";
//                    break;
//                case "door":
//                    info = "门锁打开，设备预警";
//                    break;
//                default:
//                    info = null;
//                    break;
//            }
//        }
//        return info;
//    }


//    public static String getDefaultUnit(Context context, String sensorType) {
//        String unit = "";
//        if (sensorType.equals("temperature")) {
//            unit = "°C";
//        } else if (sensorType.equals("humidity")) {
//            unit = "%";
//        } else if (sensorType.equals("co") || sensorType.equals("co2") || sensorType.equals("ch4")) {
//            unit = "ppm";
//        } else if (sensorType.equals("no2") || sensorType.equals("so2")) {
//            unit = "ug/m3";
//        } else if (sensorType.equals("yaw")) {
//            unit = context.getString(R.string.sensor_yaw);
//        } else if (sensorType.equals("roll")) {
//            unit = context.getString(R.string.sensor_roll);
//        } else if (sensorType.equals("pitch")) {
//            unit = context.getString(R.string.sensor_pitch);
//        } else if (sensorType.equals("collision")) {
//            unit = context.getString(R.string.sensor_collision);
//        } else if (sensorType.equals("distance")) {
//            unit = "cm";
//        } else if (sensorType.equals("light")) {
//            unit = "Lux";
//        } else if (sensorType.equals("cover")) {
//            unit = context.getString(R.string.sensor_cover);
//        } else if (sensorType.equals("level")) {
//            unit = context.getString(R.string.sensor_level);
//        } else if (sensorType.equals("drop")) {
//            unit = context.getString(R.string.sensor_drop);
//        } else if (sensorType.equals("smoke")) {
//            unit = context.getString(R.string.sensor_smoke);
//        }
//        return unit;
//    }

    //
//    public static String getDefaultUnitWithEn(Context context, String sensorType) {
//        String unit = "";
//        if (sensorType.equals("temperature")) {
//            unit = "°C";
//        } else if (sensorType.equals("humidity")) {
//            unit = "%";
//        } else if (sensorType.equals("co") || sensorType.equals("co2") || sensorType.equals("ch4")) {
//            unit = "ppm";
//        } else if (sensorType.equals("no2") || sensorType.equals("so2")) {
//            unit = "ug/m3";
//        } else if (sensorType.equals("yaw")) {
//            unit = "";
//        } else if (sensorType.equals("roll")) {
//            unit = "";
//        } else if (sensorType.equals("pitch")) {
//            unit = "";
//        } else if (sensorType.equals("collision")) {
//            unit = "";
//        } else if (sensorType.equals("distance")) {
//            unit = "cm";
//        } else if (sensorType.equals("light")) {
//            unit = "Lux";
//        } else if (sensorType.equals("cover")) {
//            unit = "";
//        } else if (sensorType.equals("level")) {
//            unit = "";
//        } else if (sensorType.equals("drop")) {
//            unit = "";
//        } else if (sensorType.equals("smoke")) {
//            unit = "";
//        }
//        return unit;
//    }


    //    public static void judgeSensorTypeWithEnUnit(Context context, SensorStruct sensorStruct, TextView
    // valueTextView,
//                                                 TextView unitTextView) {
//        if (sensorStruct != null) {
//            LinearLayout.LayoutParams unitParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
//                    ViewGroup.LayoutParams.WRAP_CONTENT);
//            int unit_bottomMargin = context.getResources().getDimensionPixelSize(R.dimen.y10);
//            int unit_leftMargin = context.getResources().getDimensionPixelSize(R.dimen.x30);
//            int unit_topMargin = 0;
//            Object value = sensorStruct.getValue();
//            String sensorType = sensorStruct.getSensorType();
//            if (value instanceof Boolean) {
//                if (sensorType.equals("jinggai")) {
//                    Boolean isOk = (Boolean) value;
//                    if (isOk) {
//                        valueTextView.setText(R.string.jinggai_true);
//                    } else {
//                        valueTextView.setText(R.string.jinggai_false);
//                    }
//                    unitTextView.setText("");
//                } else if (sensorType.equals("cover")) {
//                    Boolean isOk = (Boolean) value;
//                    if (isOk) {
//                        valueTextView.setText(R.string.cover_true);
//                    } else {
//                        valueTextView.setText(R.string.cover_false);
//                    }
//                    unitTextView.setText("");
//                } else if (sensorType.equals("level")) {
//                    Boolean isOk = (Boolean) value;
//                    if (isOk) {
//                        valueTextView.setText(R.string.level_true);
//                    } else {
//                        valueTextView.setText(R.string.level_false);
//                    }
//                    unitTextView.setText("");
//                } else if (sensorType.equals("collision")) {
//                    Boolean isOk = (Boolean) value;
//                    if (isOk) {
//                        valueTextView.setText(R.string.collision_true);
//                    } else {
//                        valueTextView.setText(R.string.collision_false);
//                    }
//                    unitTextView.setText("");
//                } else if (sensorType.equals("alarm")) {
//                    Boolean isOk = (Boolean) value;
//                    if (isOk) {
//                        valueTextView.setText(R.string.alarm_true);
//                    } else {
//                        valueTextView.setText(R.string.alarm_false);
//                    }
//                    unitTextView.setText("");
//                } else if (sensorType.equals("smoke")) {
//                    Boolean isOk = (Boolean) value;
//                    if (isOk) {
//                        valueTextView.setText(R.string.smoke_true);
//                    } else {
//                        valueTextView.setText(R.string.smoke_false);
//                    }
//                    unitTextView.setText("");
//                } else if (sensorType.equals("flame")) {
//                    Boolean isOk = (Boolean) value;
//                    if (isOk) {
//                        valueTextView.setText(R.string.flame_true);
//                    } else {
//                        valueTextView.setText(R.string.flame_false);
//                    }
//                    unitTextView.setText("");
//                } else if (sensorType.equals("magnetic")) {
//                    Boolean isTrue = (Boolean) value;
//                    if (isTrue) {
//                        valueTextView.setText(R.string.magnetic_true);
//                    } else {
//                        valueTextView.setText(R.string.magnetic_false);
//                    }
//                    unitTextView.setText(R.string.sensor_magnetic);
//                } else {
//                    valueTextView.setText("-");
//                    unitTextView.setText("-");
//                }
//            } else {
//                if (sensorStruct.getSensorType().equals("pitch") || sensorStruct.getSensorType().equals("roll") ||
//                        sensorStruct.getSensorType().equals("yaw") || sensorStruct.getSensorType().equals
//                        ("collision")) {
////                    unit_bottomMargin = context.getResources().getDimensionPixelSize(R.dimen.y150);
//                    unit_topMargin = -80;
//                } else if (sensorStruct.getSensorType().equals("temperature")) {
//                    unit_bottomMargin = context.getResources().getDimensionPixelSize(R.dimen.y15);
//                } else if (sensorStruct.getSensorType().equals("humidity")) {
//                    unit_bottomMargin = context.getResources().getDimensionPixelSize(R.dimen.y15);
//                } else if (sensorType.equals("co") || sensorType.equals("co2") || sensorType.equals("ch4")) {
//                    unit_bottomMargin = context.getResources().getDimensionPixelSize(R.dimen.y20);
//                } else if (sensorType.equals("no2") || sensorType.equals("so2") || sensorType.equalsIgnoreCase
//                        ("pm2_5") || sensorType.equalsIgnoreCase("pm10")) {
//                    unit_bottomMargin = context.getResources().getDimensionPixelSize(R.dimen.y10);
//                } else if (sensorType.equals("distance")) {
//                    unit_bottomMargin = context.getResources().getDimensionPixelSize(R.dimen.y10);
//                } else if (sensorType.equals("light")) {
//                    unit_bottomMargin = context.getResources().getDimensionPixelSize(R.dimen.y10);
//
//                }
//                String valueString = sensorStruct.getValue().toString();
//                if (sensorType.equals("drop")) {
//                    if (valueString.equals("0") || valueString.equals("0.0")) {
//                        valueTextView.setText(R.string.drop_false);
//                    } else {
//                        valueTextView.setText(R.string.drop_true);
//                    }
//                    unitTextView.setText("");
//                } else {
//                    if (valueString.equals("-")) {
//                        valueTextView.setText("--");
//                        unitTextView.setText(sensorStruct.getUnit());
//                    } else {
//                        unitTextView.setText("" + sensorStruct.getUnit());
//                        valueTextView.setText("" + String.format("%.1f", Double.valueOf(sensorStruct.getValue()
//                                .toString())));
//
//                    }
//                }
//                unitParams.setMargins(unit_leftMargin, unit_topMargin, 0, unit_bottomMargin);
//                unitTextView.setLayoutParams(unitParams);
//            }
//
//        } else {
//            valueTextView.setText("-");
//            unitTextView.setText("-");
//        }
//
//    }

//    public static void judgeSensorTypeWithEnUnit(SensorStruct sensorStruct, TextView valueTextView, TextView
//            unitTextView) {
//        if (sensorStruct != null) {
//            Object value = sensorStruct.getValue();
//            String sensorType = sensorStruct.getSensorType();
//            if (value instanceof Boolean) {
//                if (sensorType.equals("jinggai")) {
//                    Boolean isOk = (Boolean) value;
//                    if (isOk) {
//                        valueTextView.setText(R.string.jinggai_true);
//                    } else {
//                        valueTextView.setText(R.string.jinggai_false);
//                    }
//                    unitTextView.setText("");
//                } else if (sensorType.equals("cover")) {
//                    Boolean isOk = (Boolean) value;
//                    if (isOk) {
//                        valueTextView.setText(R.string.cover_true);
//                    } else {
//                        valueTextView.setText(R.string.cover_false);
//                    }
//                    unitTextView.setText("");
//                } else if (sensorType.equals("level")) {
//                    Boolean isOk = (Boolean) value;
//                    if (isOk) {
//                        valueTextView.setText(R.string.level_true);
//                    } else {
//                        valueTextView.setText(R.string.level_false);
//                    }
//                    unitTextView.setText("");
//                } else if (sensorType.equals("collision")) {
//                    Boolean isOk = (Boolean) value;
//                    if (isOk) {
//                        valueTextView.setText(R.string.collision_true);
//                    } else {
//                        valueTextView.setText(R.string.collision_false);
//                    }
//                    unitTextView.setText("");
//                } else if (sensorType.equals("alarm")) {
//                    Boolean isOk = (Boolean) value;
//                    if (isOk) {
//                        valueTextView.setText(R.string.alarm_true);
//                    } else {
//                        valueTextView.setText(R.string.alarm_false);
//                    }
//                    unitTextView.setText("");
//                } else if (sensorType.equals("smoke")) {
//                    Boolean isOk = (Boolean) value;
//                    if (isOk) {
//                        valueTextView.setText(R.string.smoke_true);
//                    } else {
//                        valueTextView.setText(R.string.smoke_false);
//                    }
//                    unitTextView.setText("");
//                } else if (sensorType.equals("flame")) {
//                    Boolean isOk = (Boolean) value;
//                    if (isOk) {
//                        valueTextView.setText(R.string.flame_true);
//                    } else {
//                        valueTextView.setText(R.string.flame_false);
//                    }
//                    unitTextView.setText("");
//                } else if (sensorType.equals("magnetic")) {
//                    Boolean isTrue = (Boolean) value;
//                    if (isTrue) {
//                        valueTextView.setText(R.string.magnetic_true);
//                    } else {
//                        valueTextView.setText(R.string.magnetic_false);
//                    }
//                    unitTextView.setText(R.string.sensor_magnetic);
//                } else {
//                    valueTextView.setText("-");
//                    unitTextView.setText("-");
//                }
//            } else {
//                String valueString = sensorStruct.getValue().toString();
//                if (sensorType.equals("drop")) {
//                    if (valueString.equals("0") || valueString.equals("0.0")) {
//                        valueTextView.setText(R.string.drop_false);
//                    } else {
//                        valueTextView.setText(R.string.drop_true);
//                    }
//                    unitTextView.setText("");
//                } else {
//                    if (valueString.equals("-")) {
//                        valueTextView.setText("--");
//                        unitTextView.setText(sensorStruct.getUnit());
//                    } else {
////                        if (sensorStruct.getSensorType().equals("pitch")) {
////                            unitTextView.setEditText(R.string.sensor_pitch);
////                        } else if (sensorStruct.getSensorType().equals("roll")) {
////                            unitTextView.setEditText(R.string.sensor_roll);
////                        } else if (sensorStruct.getSensorType().equals("yaw")) {
////                            unitTextView.setEditText(R.string.sensor_yaw);
////                        } else {
////                            unitTextView.setEditText("" + sensorStruct.getUnit());
////                        }
//                        unitTextView.setText("" + sensorStruct.getUnit());
//                        valueTextView.setText("" + String.format("%.1f", Double.valueOf(sensorStruct.getValue()
//                                .toString())));
//
//                    }
//                }
//            }
//        } else {
//            valueTextView.setText("-");
//            unitTextView.setText("-");
//        }
//
//    }
}
