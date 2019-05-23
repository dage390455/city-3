package com.sensoro.smartcity.imainviews;

import androidx.annotation.ColorRes;

import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.common.iwidget.IProgressDialog;
import com.sensoro.common.iwidget.IToast;

import java.util.List;

public interface IDeployNameplateActivityView extends IToast, IProgressDialog, IActivityIntent {
    void setName(String name, @ColorRes int color);

    void updateTagsData(List<String> tagList);
}
