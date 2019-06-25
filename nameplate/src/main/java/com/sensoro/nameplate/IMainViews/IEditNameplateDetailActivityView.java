package com.sensoro.nameplate.IMainViews;

import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.common.iwidget.IToast;

import java.util.List;

public interface IEditNameplateDetailActivityView extends IToast, IActivityIntent {
    void updateTags(List<String> tags);

    void showDialogWithEdit(String text, int position);

    void dismissDialog();

    void updateSaveStatus(boolean isEnable);

    void updateNameplateName(String nameplateName);

}
