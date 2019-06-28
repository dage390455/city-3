package com.sensoro.city_camera.IMainViews;

import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.common.iwidget.IToast;

import java.util.List;

/**
 * @author : bin.tian
 * date   : 2019-06-28
 */
public interface IPhotoPreviewView extends IActivityIntent, IToast {
    void updatePhotoList(List<String> urlList, int position);
    void updatePhotoInfo(int securityType, String title, String subTitle);
}
