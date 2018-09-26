package com.sensoro.smartcity.imainviews;

import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;

import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;

import java.util.List;

public interface IInspectionTaskDetailActivityView extends IToast,IProgressDialog,IActivityIntent{
    void updateTagsData(List<String> tagList);

    void setTvState(@ColorRes int colorId, String text);

    void setTvTaskNumber(String id);

    void setTvTaskTime(String time);

    void setTvbtnStartState(@DrawableRes int drawableRes, @ColorRes int color, String text);
}
