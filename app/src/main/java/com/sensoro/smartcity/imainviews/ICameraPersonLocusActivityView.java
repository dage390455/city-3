package com.sensoro.smartcity.imainviews;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;

public interface ICameraPersonLocusActivityView extends IToast , IActivityIntent, IProgressDialog {
    void setMapCenter(CameraUpdate cameraUpdate);

    void addMarker(MarkerOptions markerOptions, int tag);

    void addPolyLine(PolylineOptions linePoints, boolean b);

    void setTimeText(String mothDayHourMinuteFormatDate);

    void setMoveLeftClickable(boolean clickable);

    void setMoveRightClickable(boolean clickable);

    void removeAvatarMarker();

    ImageView getIMv();

    void clearIMv();

    void initSeekBar(int size);

    void clearDisplayLine();

    void updateSeekBar(int index);

    void refreshMap(LatLng latLng, Bitmap resource);

    void startPlay(String url1);

    void playError(int index);
}
