package com.sensoro.nameplate.IMainViews;

import androidx.annotation.ColorRes;

import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.common.iwidget.IProgressDialog;
import com.sensoro.common.iwidget.IToast;

import java.util.List;

public interface IDeployNameplateActivityView extends IToast, IProgressDialog, IActivityIntent {
    void setName(String name, @ColorRes int color);

    void updateTagsData(List<String> tagList);

    void setAssociateSensorSize(int size);

    void setDeployPhotoTextSize(int size);

    void setUploadStatus(boolean isUpload);

    void showStartUploadProgressDialog();

    void dismissUploadProgressDialog();

    void showUploadProgressDialog(String content, double percent);

    void setNameplateId(String mNameplateId);
}
