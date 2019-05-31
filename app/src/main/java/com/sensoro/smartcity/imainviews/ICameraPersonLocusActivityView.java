package com.sensoro.smartcity.imainviews;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;

import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.common.iwidget.IProgressDialog;
import com.sensoro.common.iwidget.IToast;

public interface ICameraPersonLocusActivityView extends IToast, IActivityIntent, IProgressDialog {
    void setMapCenter(CameraUpdate cameraUpdate);

    void addMarker(MarkerOptions markerOptions, int tag);

    void addPolyLine(PolylineOptions linePoints, boolean b);

    void setMoveLeftClickable(boolean clickable);

    void setMoveRightClickable(boolean clickable);

    void removeAllMarker();

    void initSeekBar(int size);

    void clearDisplayLine();

    void updateSeekBar(int index);

    void updateAvatarMarker(LatLng latLng, Bitmap resource);

    void startPlay(String url1);

    void playError(int index);

    void setMarkerTime(String time);

    void setMarkerAddress(String address);

    void removeNormalMarker(Integer tag);

    void clearNormalMarker();

    void setSelectDayBg(int day);

    void setSeekBarTime(String time);

    void moveAvatarMarker(LatLng latLng);

    void setLastCover(BitmapDrawable bitmapDrawable);

    void setSeekBarVisible(boolean isVisible);

    void clearDisplayNormalLine();

    void setSeekBarTimeVisible(boolean isVisible);

    void setCityPlayState(int state);

    void setVerOrientationUtil(boolean enable);

    int getCurrentState();

    void clickCityStartIcon();

    View getPlayAndRetryBtn();

    void backFromWindowFull();

    void onVideoResume();

    void onVideoPause();
}
