package com.sensoro.smartcity.imainviews;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;

import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.common.iwidget.IProgressDialog;
import com.sensoro.common.iwidget.IToast;

import java.util.List;

public interface IInspectionTaskDetailActivityView extends IToast,IProgressDialog,IActivityIntent{
    void updateTagsData(List<String> tagList);

    void setTvState(@ColorRes int colorId, String text);

    void setTvTaskNumber(String id);

    void setTvTaskTime(String time);

    void setTvbtnStartState(@DrawableRes int drawableRes, @ColorRes int color, String text);
}
