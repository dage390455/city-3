package com.sensoro.smartcity.imainviews;

import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.model.MarkerOptions;
import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;

public interface IPersonLocusView extends IToast , IActivityIntent, IProgressDialog {
    void setMapCenter(CameraUpdate cameraUpdate);

    void addMarker(MarkerOptions markerOptions);
}
