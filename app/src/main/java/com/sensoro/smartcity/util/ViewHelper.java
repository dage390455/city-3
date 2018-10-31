package com.sensoro.smartcity.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.fengmap.android.map.geometry.FMMapCoord;
import com.fengmap.android.map.marker.FMImageMarker;
import com.fengmap.android.map.marker.FMTextMarker;
import com.sensoro.smartcity.R;

/**
 * 控件控制帮助类
 *
 * @author hezutao@fengmap.com
 * @version 2.0.0
 */
public class ViewHelper {

    /**
     * 获取控件
     *
     * @param activity Activity
     * @param id       控件id
     * @param <T>      控件继承于View
     * @return id对应的控件
     */
    public static <T extends View> T getView(Activity activity, int id) {
        return (T) activity.findViewById(id);
    }

    /**
     * 获取控件
     *
     * @param view 视图
     * @param id   控件id
     * @param <T>  继承于View
     * @return id对应的控件
     */
    public static <T extends View> T getView(View view, int id) {
        return (T) view.findViewById(id);
    }

    /**
     * 设置控件的点击事件
     *
     * @param activity Activity
     * @param id       控件id
     * @param listener 点击监听事件
     */
    public static void setViewClickListener(Activity activity, int id, View.OnClickListener listener) {
        View view = getView(activity, id);
        view.setOnClickListener(listener);
    }

    /**
     * 设置控件文字
     *
     * @param activity Activity
     * @param id       控件id
     * @param text     文字
     */
    public static void setViewText(Activity activity, int id, String text) {
        TextView view = getView(activity, id);
        view.setText(text);
    }

    /**
     * 设置控件的选中状态改变事件监听
     *
     * @param activity Activity
     * @param id       控件id
     * @param listener CheckBox选中状态改变事件
     */
    public static void setViewCheckedChangeListener(Activity activity, int id,
                                                    CompoundButton.OnCheckedChangeListener listener) {
        CheckBox view = getView(activity, id);
        view.setOnCheckedChangeListener(listener);
    }

    /**
     * 添加图片标注
     *
     * @param resources 资源
     * @param mapCoord  坐标
     * @param resId     资源id
     * @return 图片标注
     */
    public static FMImageMarker buildImageMarker(Resources resources, FMMapCoord mapCoord, int resId) {
        Bitmap bitmap = BitmapFactory.decodeResource(resources, resId);
        FMImageMarker imageMarker = new FMImageMarker(mapCoord, bitmap);
        //设置图片宽高
        imageMarker.setMarkerWidth(90);
        imageMarker.setMarkerHeight(90);
        //设置图片在模型之上
        imageMarker.setFMImageMarkerOffsetMode(FMImageMarker.FMImageMarkerOffsetMode.FMNODE_MODEL_ABOVE);
        return imageMarker;
    }

    /**
     * 创建文字标注
     *
     * @param mapCoord 坐标
     * @param text     文字
     * @return 文字标注
     */
    public static FMTextMarker buildTextMarker(FMMapCoord mapCoord, String text) {
        FMTextMarker textMarker = new FMTextMarker(mapCoord, text);
        textMarker.setTextFillColor(Color.RED);
        textMarker.setTextStrokeColor(Color.RED);
        textMarker.setTextSize(30);
        //设置文字在模型之上
        textMarker.setFMTextMarkerOffsetMode(FMTextMarker.FMTextMarkerOffsetMode.FMNODE_MODEL_ABOVE);
        return textMarker;
    }

    /**
     * 设置控件的点击事件
     *
     * @param activity Activity
     * @param id       控件id
     * @param enabled  是否可用
     */
    public static void setViewEnable(Activity activity, int id, boolean enabled) {
        View view = getView(activity, id);
        view.setEnabled(enabled);
    }

    /**
     * 改变文字的颜色，及startDrawable的颜色，主要用于巡检任务，任务状态文本的改变
     * @param context
     * @param tv
     * @param colorId
     * @param text
     */
    public static void changeTvState(Context context, TextView tv, int colorId, String text) {
        Resources resources = context.getResources();
        GradientDrawable gd = (GradientDrawable) resources.getDrawable(R.drawable.shape_small_oval_29c);
        gd.setBounds(0,0,gd.getMinimumWidth(),gd.getMinimumHeight());
        int color = resources.getColor(colorId);
        gd.setColor(color);
        tv.setCompoundDrawables(gd,null,null,null);
        tv.setTextColor(color);
        tv.setText(text);
    }
}
