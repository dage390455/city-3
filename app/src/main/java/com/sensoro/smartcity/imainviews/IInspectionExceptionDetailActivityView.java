package com.sensoro.smartcity.imainviews;

import android.support.annotation.ColorRes;

import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;
import com.sensoro.smartcity.server.bean.ScenesData;

import java.util.ArrayList;
import java.util.List;

public interface IInspectionExceptionDetailActivityView extends IToast,IActivityIntent,IProgressDialog{
    void updateTagsData(List<String> list);

    void updateExceptionTagsData(List<String> list);

    void setTvName(String name);

    void setTvSn(String sn);

    void setTvStatus(@ColorRes int colorRes,String text);

    void setTvReamrk(String remark);

    void updateRcPhotoAdapter(List<ScenesData> imageUrls);

    void updateRcCameraAdapter(List<ScenesData> videoThumbUrls);
}
