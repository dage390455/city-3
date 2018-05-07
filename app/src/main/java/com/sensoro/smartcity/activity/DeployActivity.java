package com.sensoro.smartcity.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.geocoder.RegeocodeRoad;
import com.amap.api.services.geocoder.StreetNumber;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.baidu.mobstat.StatService;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.server.bean.AlarmInfo;
import com.sensoro.smartcity.server.bean.DeviceInfo;
import com.sensoro.smartcity.server.response.DeviceDeployRsp;
import com.sensoro.smartcity.server.response.DeviceInfoListRsp;
import com.sensoro.smartcity.server.response.ResponseBase;
import com.sensoro.smartcity.widget.SensoroToast;
import com.sensoro.smartcity.widget.statusbar.StatusBarCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by sensoro on 17/8/3.
 */

public class DeployActivity extends BaseActivity implements Constants, AMapLocationListener, AMap.OnMapClickListener,
        AMap.OnCameraChangeListener, GeocodeSearch.OnGeocodeSearchListener, AMap.InfoWindowAdapter, AMap
                .OnMapLoadedListener, AMap.OnMarkerClickListener, AMap.OnMapTouchListener {


    @BindView(R.id.deploy_name_address_et)
    TextView nameAddressEditText;
    @BindView(R.id.deploy_location_et)
    TextView locationEditText;
    @BindView(R.id.deploy_contact_et)
    TextView contactEditText;
    @BindView(R.id.deploy_upload_btn)
    Button uploadButton;
    @BindView(R.id.deploy_title)
    TextView titleTextView;
    @BindView(R.id.deploy_device_signal)
    TextView signalButton;
    @BindView(R.id.deploy_map)
    TextureMapView mMapView;
    @BindView(R.id.deploy_tag_layout)
    LinearLayout tagLayout;
    private AMap aMap;
    private Marker smoothMoveMarker;
    private CameraUpdate mUpdata;
    private LatLng latLng;
    private MyLocationStyle myLocationStyle;
    private GeocodeSearch geocoderSearch;
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;
    private ProgressDialog mProgressDialog;
    private List<String> tagList = new ArrayList<>();
    private String contact = null;
    private String content = null;
    private boolean isNeedSlide = true;
    private RegeocodeQuery query;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deploy);
        ButterKnife.bind(this);
        mMapView.onCreate(savedInstanceState);
        init();
        this.getWindow().getDecorView().postInvalidate();
        StatusBarCompat.setStatusBarColor(this);
    }

    @Override
    protected void onDestroy() {
        if (mProgressDialog != null) {
            mProgressDialog.cancel();
            mProgressDialog = null;
        }
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
            mLocationClient = null;
        }
        if (uploadButton != null) {
            uploadButton.setEnabled(true);
        }
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    @Override
    protected boolean isNeedSlide() {
        return isNeedSlide;
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        StatService.onResume(this);
        if (uploadButton != null) {
            uploadButton.setEnabled(true);
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
        StatService.onPause(this);
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
        StatusBarCompat.setStatusBarColor(this);
    }

    private void init() {
        try {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            DeviceInfo deviceInfo = (DeviceInfo) getIntent().getSerializableExtra(EXTRA_DEVICE_INFO);
            if (deviceInfo != null) {
                String sn = deviceInfo.getSn();
                String styleName = "custom_config.data";
                titleTextView.setText(sn);
                nameAddressEditText.setText(deviceInfo.getName());
                if (deviceInfo.getAlarms() != null) {
                    AlarmInfo alarmInfo = deviceInfo.getAlarms();
                    String contact = alarmInfo.getNotification().getContact();
                    if (contact != null) {
                        this.contact = contact;
                        String content = alarmInfo
                                .getNotification().getContent();
                        this.content = content;
                        contactEditText.setText(contact + ":" + content);
                    }
                }

                String tags[] = deviceInfo.getTags();
                if (tags != null) {
                    for (int i = 0; i < tags.length; i++) {
                        String tag = tags[i];
                        if (!TextUtils.isEmpty(tag)) {
                            tagList.add(tag);
                        }
                    }
                    refreshTagLayout();
                    if (tagList.size() == 0) {
                        addDefaultTextView();
                    }
                } else {
                    addDefaultTextView();
                }
                String signal = deviceInfo.getSignal();
                refreshSignal(deviceInfo.getUpdatedTime(), signal);
                aMap = mMapView.getMap();
                aMap.getUiSettings().setTiltGesturesEnabled(false);
                aMap.getUiSettings().setZoomControlsEnabled(false);
                aMap.getUiSettings().setMyLocationButtonEnabled(false);
                aMap.setMyLocationEnabled(true);
                aMap.setMapCustomEnable(true);
                aMap.setCustomMapStylePath(getFilesDir().getAbsolutePath() + "/" + styleName);
                aMap.setOnMapClickListener(this);
                aMap.setOnCameraChangeListener(this);
                aMap.setOnMapLoadedListener(this);
                aMap.setOnMarkerClickListener(this);
                aMap.setInfoWindowAdapter(this);
                aMap.setOnMapTouchListener(this);
                myLocationStyle = new MyLocationStyle();
                myLocationStyle.radiusFillColor(Color.argb(25, 73, 144, 226));
                myLocationStyle.strokeWidth(0);
                myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);
                myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_direction));
                myLocationStyle.showMyLocation(true);

                aMap.setMyLocationStyle(myLocationStyle);
                BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.mipmap.ic_move_location);
                MarkerOptions markerOption = new MarkerOptions().icon(bitmapDescriptor)
                        .anchor(0.5f, 0.5f)
                        .draggable(true);
                smoothMoveMarker = aMap.addMarker(markerOption);

                geocoderSearch = new GeocodeSearch(this);
                geocoderSearch.setOnGeocodeSearchListener(this);
                uploadButton.setEnabled(true);
                setMapCustomStyleFile();
                locate();
            } else {
                Intent intent = new Intent();
                intent.setClass(this, DeployResultActivity.class);
                intent.putExtra(EXTRA_SENSOR_RESULT, -1);
                startActivityForResult(intent, REQUEST_CODE_DEPLOY);
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.tips_data_error, Toast.LENGTH_SHORT).show();
        }

    }

    public void refreshTagLayout() {
        tagLayout.removeAllViews();
        int textSize = getResources().getDimensionPixelSize(R.dimen.tag_default_size);
        for (int i = 0; i < tagList.size(); i++) {
            TextView textView = new TextView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
            params.setMargins(10, 0, 0, 0);
            textView.setTextColor(getResources().getColor(R.color.white));
            textView.setText(tagList.get(i));
            textView.setPadding(5, 0, 0, 0);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            textView.setGravity(Gravity.CENTER);
            textView.setCompoundDrawables(null, null, null, null);
            textView.setBackground(getResources().getDrawable(R.drawable.shape_textview));
            tagLayout.addView(textView, i, params);

        }
    }

    private void refreshSignal(long updateTime, String signal) {
        String signal_text = null;
        long time_diff = System.currentTimeMillis() - updateTime;
        if (signal != null && (time_diff < 300000)) {
            switch (signal) {
                case "good":
                    signal_text = "信号质量：优";
                    signalButton.setBackground(getResources().getDrawable(R.drawable.shape_signal_good));
                    break;
                case "normal":
                    signal_text = "信号质量：良";
                    signalButton.setBackground(getResources().getDrawable(R.drawable.shape_signal_normal));
                    break;
                case "bad":
                    signal_text = "信号质量：差";
                    signalButton.setBackground(getResources().getDrawable(R.drawable.shape_signal_bad));
                    break;
            }
        } else {
            signal_text = "无信号";
            signalButton.setBackground(getResources().getDrawable(R.drawable.shape_signal_none));
        }
        signalButton.setText(signal_text);
        signalButton.setPadding(6, 10, 6, 10);
    }

    public void addDefaultTextView() {
        int textSize = getResources().getDimensionPixelSize(R.dimen.tag_default_size);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        params.setMargins(0, 0, 20, 0);
        TextView textView = new TextView(this);
        textView.setTextColor(getResources().getColor(R.color.c_888888));
        textView.setLayoutParams(params);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        textView.setText(R.string.tips_hint_tag);
        tagLayout.addView(textView);
    }

    private void setMapCustomStyleFile() {
        String styleName = "custom_config.data";
        FileOutputStream outputStream = null;
        InputStream inputStream = null;
        String filePath = null;
        try {
            inputStream = getAssets().open(styleName);
            byte[] b = new byte[inputStream.available()];
            inputStream.read(b);

            filePath = getFilesDir().getAbsolutePath();
            File file = new File(filePath + "/" + styleName);
            if (!file.exists()) {
                file.createNewFile();
            }
            outputStream = new FileOutputStream(file);
            outputStream.write(b);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        aMap.setCustomMapStylePath(filePath + "/" + styleName);

    }

    public void locate() {

        mLocationClient = new AMapLocationClient(this);
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为Hight_Accuracy高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(true);
        mLocationOption.setOnceLocationLatest(true);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    private String tagListToString() {
        if (tagList.size() == 0) {
            return null;
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < tagList.size(); i++) {
                String tag = tagList.get(i);
                if (!TextUtils.isEmpty(tag)) {
                    if (i == tagList.size() - 1) {
                        stringBuilder.append(tag);
                    } else {
                        stringBuilder.append(tag + ",");
                    }
                }

            }
            return stringBuilder.toString();
        }
    }

    private void requestUpload() {
        String sn = titleTextView.getText().toString();
        final String name = nameAddressEditText.getText().toString();
        String tags = tagListToString();
        String name_default = getString(R.string.tips_hint_name_address);
        if (TextUtils.isEmpty(name) || name.equals(name_default)) {
            SensoroToast sensoroToast = SensoroToast.makeText(this, getString(R.string
                    .tips_input_name), Toast.LENGTH_SHORT);
            sensoroToast.setGravity(Gravity.CENTER, 0, -10);
            sensoroToast.show();
            if (uploadButton != null) {
                uploadButton.setEnabled(true);
            }
        } else if (latLng == null) {
            SensoroToast.makeText(this, getString(R.string.tips_hint_location), Toast.LENGTH_SHORT).setGravity
                    (Gravity.CENTER, 0, -10)
                    .show();
            if (uploadButton != null) {
                uploadButton.setEnabled(true);
            }
        } else if (TextUtils.isEmpty(contact) || name.equals(content)) {
            SensoroToast.makeText(this, "联系人姓名或电话不能为空！", Toast.LENGTH_SHORT).setGravity(Gravity.CENTER, 0, -10)
                    .show();
            if (uploadButton != null) {
                uploadButton.setEnabled(true);
            }
        } else {
            final double lon = latLng.longitude;
            final double lan = latLng.latitude;
            SensoroCityApplication.getInstance().smartCityServer.doDevicePointDeploy(sn, lon, lan, tags, name,
                    contact, content,
                    new Response.Listener<DeviceDeployRsp>() {
                        @Override
                        public void onResponse(DeviceDeployRsp response) {
                            int errCode = response.getErrcode();
                            int resultCode = 1;
                            if (errCode != ResponseBase.CODE_SUCCESS) {
                                resultCode = errCode;
                            }
                            Intent intent = new Intent(DeployActivity.this, DeployResultActivity.class);
                            intent.putExtra(EXTRA_SENSOR_RESULT, resultCode);
                            intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                            intent.putExtra(EXTRA_DEVICE_INFO, response.getData());
                            intent.putExtra(EXTRA_SENSOR_LON, String.valueOf(lon));
                            intent.putExtra(EXTRA_SENSOR_LAN, String.valueOf(lan));
                            intent.putExtra(EXTRA_SETTING_CONTACT, contact);
                            intent.putExtra(EXTRA_SETTING_CONTENT, content);
                            startActivity(intent);
                            finish();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            if (volleyError.networkResponse != null) {
                                byte[] data = volleyError.networkResponse.data;
                                Toast.makeText(DeployActivity.this, new String(data), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(DeployActivity.this, R.string.tips_network_error, Toast.LENGTH_SHORT)
                                        .show();
                            }
                            if (uploadButton != null) {
                                uploadButton.setEnabled(true);
                            }

                        }
                    });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CODE_SETTING_NAME_ADDRESS) {
            String name = data.getStringExtra(EXTRA_SETTING_NAME_ADDRESS);
            nameAddressEditText.setText(name);
        } else if (resultCode == RESULT_CODE_SETTING_TAG) {
            List<String> tempList = data.getStringArrayListExtra(EXTRA_SETTING_TAG_LIST);
            tagList.clear();
            tagList.addAll(tempList);
            refreshTagLayout();
        } else if (resultCode == RESULT_CODE_SETTING_CONTACT) {
            contact = data.getStringExtra(EXTRA_SETTING_CONTACT);
            content = data.getStringExtra(EXTRA_SETTING_CONTENT);
            contactEditText.setText(contact + ":" + content);
        }
    }

    @OnClick(R.id.deploy_name_relative_layout)
    public void doSettingByNameAndAddress() {
        Intent intent = new Intent(this, DeploySettingNameActivity.class);
        intent.putExtra(EXTRA_SETTING_INDEX, SETTING_NAME_ADDRESS);
        intent.putExtra(EXTRA_SETTING_NAME_ADDRESS, nameAddressEditText.getText().toString());
        startActivityForResult(intent, REQUEST_SETTING_NAME_ADDRESS);
    }

    @OnClick(R.id.deploy_tag_relative_layout)
    public void doSettingByTag() {
        Intent intent = new Intent(this, DeploySettingTagActivity.class);
        intent.putExtra(EXTRA_SETTING_INDEX, SETTING_TAG);
        intent.putStringArrayListExtra(EXTRA_SETTING_TAG_LIST, (ArrayList<String>) tagList);
        startActivityForResult(intent, REQUEST_SETTING_TAG);
    }

    @OnClick(R.id.deploy_contact_relative_layout)
    public void doSettingContact() {
        Intent intent = new Intent(this, DeploySettingContactActivity.class);
        intent.putExtra(EXTRA_SETTING_INDEX, SETTING_CONTACT);
        if (contact != null) {
            intent.putExtra(EXTRA_SETTING_CONTACT, contact);
            intent.putExtra(EXTRA_SETTING_CONTENT, content);
        }
        startActivityForResult(intent, REQUEST_SETTING_CONTACT);
    }

    @OnClick(R.id.deploy_device_signal)
    public void doSignal() {
        mProgressDialog.show();
        String sns = titleTextView.getText().toString();
        SensoroCityApplication.getInstance().smartCityServer.getDeviceDetailInfoList(sns, null, 1,
                new Response.Listener<DeviceInfoListRsp>() {
                    @Override
                    public void onResponse(DeviceInfoListRsp response) {
                        mProgressDialog.dismiss();
                        if (response.getData().size() > 0) {
                            DeviceInfo deviceInfo = response.getData().get(0);
                            String signal = deviceInfo.getSignal();
                            refreshSignal(deviceInfo.getUpdatedTime(), signal);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        mProgressDialog.dismiss();
                        if (volleyError.networkResponse != null) {
                            String reason = new String(volleyError.networkResponse.data);
                            try {
                                JSONObject jsonObject = new JSONObject(reason);
                                Toast.makeText(DeployActivity.this, jsonObject.getString("errmsg"), Toast
                                        .LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (Exception e) {

                            }
                        } else {
                            Toast.makeText(DeployActivity.this, R.string.tips_network_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    @OnClick(R.id.deploy_upload_btn)
    public void deploy() {
        uploadButton.setEnabled(false);
        requestUpload();
    }

    @OnClick(R.id.deploy_back)
    public void back() {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_CONTAINS_DATA, false);
        setResult(RESULT_CODE_DEPLOY, intent);
        this.finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            back();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        System.out.println("====>onLocationChanged");
        if (aMapLocation != null) {
            double lat = aMapLocation.getLatitude();//获取纬度
            double lon = aMapLocation.getLongitude();//获取经度
            latLng = new LatLng(lat, lon);
            if (aMap != null) {
                //可视化区域，将指定位置指定到屏幕中心位置
                mUpdata = CameraUpdateFactory
                        .newCameraPosition(new CameraPosition(latLng, 15, 0, 30));
                aMap.moveCamera(mUpdata);
            }
            smoothMoveMarker.setPosition(latLng);
        } else {
            //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
            Log.e("地图错误", "定位失败, 错误码:" + aMapLocation.getErrorCode() + ", 错误信息:"
                    + aMapLocation.getErrorInfo());
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
    }


    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        latLng = cameraPosition.target;
        smoothMoveMarker.setPosition(latLng);
        System.out.println("====>onCameraChange");
    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        latLng = cameraPosition.target;
        smoothMoveMarker.setPosition(latLng);
        LatLonPoint lp = new LatLonPoint(latLng.latitude, latLng.longitude);
        System.out.println("====>onCameraChangeFinish=>" + lp.getLatitude() + "&" + lp.getLongitude());
        query = new RegeocodeQuery(lp, 200, GeocodeSearch.AMAP);
        geocoderSearch.getFromLocationAsyn(query);
    }

    public void setMarkerAddress(RegeocodeAddress regeocodeAddress) {
        StringBuffer stringBuffer = new StringBuffer();
        String subLoc = regeocodeAddress.getDistrict();// 区或县或县级市
        String ts = regeocodeAddress.getTownship();// 乡镇
        String thf = null;// 道路
        List<RegeocodeRoad> regeocodeRoads = regeocodeAddress.getRoads();// 道路列表
        if (regeocodeRoads != null && regeocodeRoads.size() > 0) {
            RegeocodeRoad regeocodeRoad = regeocodeRoads.get(0);
            if (regeocodeRoad != null) {
                thf = regeocodeRoad.getName();
            }
        }
        String subthf = null;// 门牌号
        StreetNumber streetNumber = regeocodeAddress.getStreetNumber();
        if (streetNumber != null) {
            subthf = streetNumber.getNumber();
        }
        String fn = regeocodeAddress.getBuilding();// 标志性建筑,当道路为null时显示
        if (subLoc != null) {
            stringBuffer.append(subLoc);
        }
        if (ts != null) {
            stringBuffer.append(ts);
        }
        if (thf != null) {
            stringBuffer.append(thf);
        }
        if (subthf != null) {
            stringBuffer.append(subthf);
        }
        System.out.println(stringBuffer.toString());
        smoothMoveMarker.setTitle(stringBuffer.toString());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                smoothMoveMarker.showInfoWindow();
            }
        });


    }

    private Handler mHandler = new Handler();

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        System.out.println("====>onRegeocodeSearched");
        try {
            RegeocodeAddress regeocodeAddress = regeocodeResult.getRegeocodeAddress();
            setMarkerAddress(regeocodeAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }

    @Override
    public void onMapLoaded() {
        mMapView.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        return true;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        View view = getLayoutInflater().inflate(R.layout.layout_marker, null);
        TextView info = (TextView) view.findViewById(R.id.marker_info);
        info.setText(marker.getTitle());

        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public void onTouch(MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_MOVE:
                isNeedSlide = false;
                break;
            case MotionEvent.ACTION_UP:
                isNeedSlide = true;
                break;
        }
    }

}
