package com.sensoro.smartcity.imainviews;

import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;

public interface IPersonLocusView extends IToast , IActivityIntent, IProgressDialog {
    void setMapCenter(CameraUpdate cameraUpdate);

    void addMarker(MarkerOptions markerOptions, int tag);

    void addPolyLine(PolylineOptions linePoints);

    void setTimeText(String mothDayHourMinuteFormatDate);

    void setMoveLeftClickable(boolean clickable);

    void setMoveRightClickable(boolean clickable);

    void removeAvatarMarker();
}
