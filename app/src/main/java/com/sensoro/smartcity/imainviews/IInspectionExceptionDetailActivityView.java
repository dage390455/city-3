package com.sensoro.smartcity.imainviews;

import android.support.annotation.ColorRes;

import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.common.iwidget.IProgressDialog;
import com.sensoro.common.iwidget.IToast;
import com.sensoro.common.server.bean.ScenesData;

import java.util.List;

public interface IInspectionExceptionDetailActivityView extends IToast,IActivityIntent,IProgressDialog{
    void updateTagsData(List<String> list);

    void updateExceptionTagsData(List<String> list);

    void setTvName(String name);

    void setTvSn(String sn);

    void setTvStatus(@ColorRes int colorRes,String text);

    void setTvRemark(String remark);

    void updateRcPhotoAdapter(List<ScenesData> imageUrls);

    void updateRcCameraAdapter(List<ScenesData> videoThumbUrls);
}
