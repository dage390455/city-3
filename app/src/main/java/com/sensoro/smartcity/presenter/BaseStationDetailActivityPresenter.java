package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.geocoder.RegeocodeRoad;
import com.amap.api.services.geocoder.StreetNumber;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.model.EventData;
import com.sensoro.common.model.ImageItem;
import com.sensoro.common.server.CityObserver;
import com.sensoro.common.server.RetrofitServiceHelper;
import com.sensoro.common.server.bean.BaseStationChartDetailModel;
import com.sensoro.common.server.bean.BaseStationDetailModel;
import com.sensoro.common.server.bean.DeviceInfo;
import com.sensoro.common.server.bean.ScenesData;
import com.sensoro.common.server.response.BaseStationChartDetailRsp;
import com.sensoro.common.server.response.BaseStationDetailRsp;
import com.sensoro.common.utils.DateUtil;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.FrequencyPointActivity;
import com.sensoro.smartcity.activity.MonitorPointMapActivity;
import com.sensoro.smartcity.activity.MonitorPointMapENActivity;
import com.sensoro.smartcity.activity.NetWorkInfoActivity;
import com.sensoro.smartcity.activity.SelfCheckActivity;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IBaseStationDetailActivityView;
import com.sensoro.smartcity.util.AppUtils;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.widget.imagepicker.ImagePicker;
import com.sensoro.smartcity.widget.imagepicker.ui.ImagePreviewDelActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.sensoro.smartcity.constant.Constants.EVENT_DATA_UPDATEBASESTATION;
import static com.sensoro.smartcity.constant.Constants.EXTRA_DEVICE_INFO;
import static com.sensoro.smartcity.constant.Constants.EXTRA_JUST_DISPLAY_PIC;
import static com.sensoro.smartcity.constant.Constants.REQUEST_CODE_PREVIEW;

public class BaseStationDetailActivityPresenter extends BasePresenter<IBaseStationDetailActivityView> implements GeocodeSearch.OnGeocodeSearchListener {
    private Activity mContext;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm:ss");
    private SimpleDateFormat hmssimpleDateFormat = new SimpleDateFormat("HH:mm:ss");
    private DecimalFormat decimalFormat = new DecimalFormat(".00");
    private String sn;

    private BaseStationDetailModel data;
    private GeocodeSearch geocoderSearch;

    private String curentType = "day";
    DeviceInfo mDeviceInfo = new DeviceInfo();

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessageEvent(EventData eventData) {
        int code = eventData.code;
        Object dataevent = eventData.data;
        switch (code) {

            //位置校准的逻辑更新
            case EVENT_DATA_UPDATEBASESTATION:


                final ArrayList<Double> pushDeviceInfo = (ArrayList<Double>) dataevent;
                data.setLonlatLabel(pushDeviceInfo);
                freshLocationDeviceInfo();


                break;
            default:
                break;

        }
    }


    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        EventBus.getDefault().register(this);
        sn = mContext.getIntent().getStringExtra("sn");
        geocoderSearch = new GeocodeSearch(mContext);
        geocoderSearch.setOnGeocodeSearchListener(this);
        if (!TextUtils.isEmpty(sn)) {
            requestDetailData(sn);
            requestChartDetailData(curentType);
        }

    }

    private void freshLocationDeviceInfo() {


        List<Double> lonlat = data.getLonlatLabel();
        try {
            double v = lonlat.get(1);
            double v1 = lonlat.get(0);
            if (v == 0 || v1 == 0) {
                getView().setDeviceLocation(mContext.getString(R.string.not_positioned), false);
                getView().setDeviceLocationTextColor(R.color.c_a6a6a6);
                return;
            }
            RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(v, v1), 200, GeocodeSearch.AMAP);
            geocoderSearch.getFromLocationAsyn(query);
        } catch (Exception e) {
            e.printStackTrace();
            getView().setDeviceLocationTextColor(R.color.c_a6a6a6);
            getView().setDeviceLocation(mContext.getString(R.string.not_positioned), false);
        }
    }

    public void requestDetailData(String sn) {
        getView().showProgressDialog();

        RetrofitServiceHelper.getInstance().getBaseStatioDetail(sn).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<BaseStationDetailRsp>(this) {
            @Override
            public void onCompleted(BaseStationDetailRsp deviceCameraListRsp) {

                data = deviceCameraListRsp.getData();

                if (null != data.getVpn() && null != data.getNetwork()) {
                    data.getNetwork().setVpn(data.getVpn().getIp());
                }

                getView().updateDetailData(data);
                freshLocationDeviceInfo();
                if (null != data.getImages() && data.getImages().size() > 0) {
                    ArrayList<ScenesData> list = new ArrayList<>();
                    for (String url : data.getImages()) {
                        ScenesData scenesData = new ScenesData();
                        scenesData.url = url;
                        list.add(scenesData);
                    }
                    getView().updateMonitorPhotos(list);
                }


                if (null != data.getDataMessage() && data.getDataMessage().size() > 0) {


                    //取时间最晚的一条
                    ArrayList<Integer> delays = new ArrayList<>();
                    for (BaseStationDetailModel.NetDelay netDelay : data.getDataMessage()) {
                        delays.add(netDelay.getTime());
                    }

                    Integer max = Collections.max(delays);
                    for (BaseStationDetailModel.NetDelay netDelay : data.getDataMessage()) {
                        if (netDelay.getTime() == max) {
                            int timeout = Integer.parseInt(netDelay.getTimeout());

                            int color = R.color.c_1dbb99;
                            if (timeout >= 0 && timeout < 300) {
                                color = R.color.c_1dbb99;
                            } else if (timeout >= 300 && timeout < 500) {

                                color = R.color.c_f48f57;
                            } else if (timeout >= 500) {
                                color = R.color.color_alarm_pup_red;
                            }
                            getView().updateNetDelay(netDelay.getTimeout(), color);

                            break;
                        }
                    }


                }
//                getView().dismissProgressDialog();
            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().dismissProgressDialog();
                getView().toastShort(errorMsg);

            }
        });

    }

    /**
     * 获取表格数据
     */

    public void requestChartDetailData(String dayOrWeek) {


        curentType = dayOrWeek;
        long from;
//'1m'|'1d
        String interval = "1h";

        if ("day".equals(dayOrWeek)) {
//            Date dayBegin = DateUtil.getDayBegin();

            from = DateUtil.getPastDate(1).getTime();


//            from = dayBegin.getTime();
            interval = "30m";
        } else {
            Date beginDayOfWeek = DateUtil.getBeginDayOfWeek();
//            from = beginDayOfWeek.getTime();

            from = DateUtil.getPastDate(7).getTime();

            interval = "1h";

        }
        getView().showProgressDialog();


        RetrofitServiceHelper.getInstance().getBaseStationChartDetail(sn, "temperature", interval, from, System.currentTimeMillis()).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<BaseStationChartDetailRsp>(this) {
            @Override
            public void onCompleted(BaseStationChartDetailRsp deviceCameraListRsp) {

                List<BaseStationChartDetailModel> data = deviceCameraListRsp.getData();
                if (null != data && data.size() > 0) {
                    processChartData(data);
                } else {
                    getView().updateCharEmpty();

                }
                getView().dismissProgressDialog();


            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().dismissProgressDialog();
                getView().toastShort(errorMsg);
                getView().updateCharEmpty();


            }
        });

    }


    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);

    }

    /**
     * 图表数据并处理
     *
     * @param modelList
     */
    public void processChartData(List<BaseStationChartDetailModel> modelList) {


        ArrayList<Entry> values1 = new ArrayList<>();

        ArrayList<Entry> values2 = new ArrayList<>();

        List<Float> list1 = new ArrayList<>();
        List<Float> list2 = new ArrayList<>();

        for (BaseStationChartDetailModel model : modelList) {
            list1.add(model.getBoard());
            list2.add(model.getShell());

            values1.add(new Entry(model.getKey(), model.getBoard()));

            values2.add(new Entry(model.getKey(), model.getShell()));

        }
        Float max1 = Collections.max(list1);
        Float max2 = Collections.max(list2);
        float max = Math.max(max1, max2);


        Float min1 = Collections.min(list1);
        Float min2 = Collections.min(list2);

        float min = Math.min(min1, min2);

        LineDataSet set1, set2;

        set1 = new LineDataSet(values1, "DataSet 1");

        set1.setLineWidth(2f);
        set1.setCircleRadius(0f);
        set1.setFillAlpha(65);
        set1.setHighLightColor(Color.BLACK);
        set1.setFillColor(Color.parseColor("#6D5EAC"));

        set1.setColor(Color.parseColor("#6D5EAC"));
        set1.setDrawCircleHole(false);
        set1.setDrawValues(false);
        set1.setDrawCircles(false);


        set1.setDrawVerticalHighlightIndicator(true);
        set1.setDrawHorizontalHighlightIndicator(false);

        // create a dataset and give it a type
        set2 = new LineDataSet(values2, "DataSet 2");
        set2.setLineWidth(2f);
        set2.setFillAlpha(65);
        set2.setHighLightColor(Color.BLACK);

        set2.setFillColor(Color.parseColor("#37B0E9"));
        set2.setColor(Color.parseColor("#37B0E9"));


        set2.setDrawCircleHole(false);

        set2.setDrawCircles(false);

        set2.setDrawHighlightIndicators(false);
        set2.setDrawVerticalHighlightIndicator(true);
        set2.setDrawHorizontalHighlightIndicator(false);
        LineData data = new LineData(set1, set2);
        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(9f);
        data.setDrawValues(false);
        getView().updateChartData(data, max + 1f, min);

    }


    public String stampToDate(String stap) {
        String time;
        long lt = Float.valueOf(stap).longValue();
        Date date = new Date(lt);


//        if (curentType.equals("day")) {
//            time = hmssimpleDateFormat.format(date);
//
//        } else {
        time = simpleDateFormat.format(date);

//        }


        return time;
    }

    /**
     * 手势拖动点击表格
     *
     * @param e
     * @param h
     * @param data
     */
    public void drawHighlight(Entry e, Highlight h, LineData data) {
        float first = 0, second = 0;
        int dataSetIndex = h.getDataSetIndex();

        if (dataSetIndex == 0) {
            LineDataSet dataSetByIndex = (LineDataSet) data.getDataSetByIndex(1);

            LineDataSet dataSetByIndex0 = (LineDataSet) data.getDataSetByIndex(0);


            boolean setIcon = false;
            for (int i = 0; i < dataSetByIndex0.getValues().size(); i++) {
                dataSetByIndex0.getValues().get(i).setIcon(null);
            }
            for (int i = 0; i < dataSetByIndex.getValues().size(); i++) {
                Entry entry = dataSetByIndex.getValues().get(i);
                if (e.getX() != entry.getX()) {
                    entry.setIcon(null);
                } else {

                    if (!setIcon) {
                        entry.setIcon(mContext.getResources().getDrawable(R.drawable.chart_black_dot));
                        setIcon = true;
                    }
                }
            }

            first = e.getY();
            List<Entry> entriesForXValue = dataSetByIndex.getEntriesForXValue(h.getX());

            second = entriesForXValue.get(entriesForXValue.size() - 1).getY();


        } else if (dataSetIndex == 1) {
            LineDataSet dataSetByIndex = (LineDataSet) data.getDataSetByIndex(0);
            //防止多个相同的x坐标黑点绘制多次
            boolean setIcon = false;


            for (int i = 0; i < dataSetByIndex.getValues().size(); i++) {
                Entry entry = dataSetByIndex.getValues().get(i);
                if (e.getX() != entry.getX()) {
                    entry.setIcon(null);
                } else {
                    if (!setIcon) {
                        entry.setIcon(mContext.getResources().getDrawable(R.drawable.chart_black_dot));
                        setIcon = true;
                    }


                }
            }


            LineDataSet dataSetByIndex1 = (LineDataSet) data.getDataSetByIndex(1);
            for (int i = 0; i < dataSetByIndex1.getValues().size(); i++) {
                dataSetByIndex1.getValues().get(i).setIcon(null);
            }

            second = e.getY();
            List<Entry> entriesForXValue = dataSetByIndex.getEntriesForXValue(h.getX());

            first = entriesForXValue.get(entriesForXValue.size() - 1).getY();

        }


        String time = stampToDate(Float.toString(e.getX()));

        getView().updateTopView(time, "内部：" + decimalFormat.format(first) + "\u2103", "外部：" + decimalFormat.format(second) + "\u2103");

    }


    public void startNetWorkInfoActivity() {
        if (null != data) {
            Intent intent = new Intent(mContext, NetWorkInfoActivity.class);
            intent.putExtra("network", data.getNetwork());
            mContext.startActivity(intent);
        }

    }

    public void startFrequencyPointActivity() {

        if (null != data) {
            Intent intent = new Intent(mContext, FrequencyPointActivity.class);
            intent.putStringArrayListExtra("channels", data.getChannels());
            mContext.startActivity(intent);
        }

    }

    public void startSelfCheckActivity() {

        if (null != data) {
            Intent intent = new Intent(mContext, SelfCheckActivity.class);
            intent.putStringArrayListExtra("selftest", data.getSelftest());
            mContext.startActivity(intent);
        }


    }


    public void doNavigation() {
        List<Double> lonlat = data.getLonlatLabel();
        if (lonlat.size() == 2) {
            double v = lonlat.get(1);
            double v1 = lonlat.get(0);
            if (v == 0 || v1 == 0) {
                getView().toastShort(mContext.getString(R.string.location_information_not_set));
                return;
            }
        } else {
            getView().toastShort(mContext.getString(R.string.location_information_not_set));
            return;
        }
        Intent intent = new Intent();
        if (AppUtils.isChineseLanguage()) {
            intent.setClass(mContext, MonitorPointMapActivity.class);
        } else {
            intent.setClass(mContext, MonitorPointMapENActivity.class);
        }



        mDeviceInfo.setLonlat(data.getLonlatLabel());
        mDeviceInfo.setSourceType(Constants.DEPLOY_MAP_SOURCE_TYPE_BASESTATION);
        mDeviceInfo.setSn(sn);
        intent.putExtra(EXTRA_DEVICE_INFO, mDeviceInfo);
        getView().startAC(intent);
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        RegeocodeAddress regeocodeAddress = regeocodeResult.getRegeocodeAddress();
        String address;
        if (AppUtils.isChineseLanguage()) {
//            address = regeocodeResult.getRegeocodeAddress().getFormatAddress();

//                改为自定义
            StringBuilder stringBuilder = new StringBuilder();
            //
            String province = regeocodeAddress.getProvince();
            //
            String district = regeocodeAddress.getDistrict();// 区或县或县级市
            //
            //
            String township = regeocodeAddress.getTownship();// 乡镇
            //
            String streetName = null;// 道路
            List<RegeocodeRoad> regeocodeRoads = regeocodeAddress.getRoads();// 道路列表
            if (regeocodeRoads != null && regeocodeRoads.size() > 0) {
                RegeocodeRoad regeocodeRoad = regeocodeRoads.get(0);
                if (regeocodeRoad != null) {
                    streetName = regeocodeRoad.getName();
                }
            }
            //
            String streetNumber = null;// 门牌号
            StreetNumber number = regeocodeAddress.getStreetNumber();
            if (number != null) {
                String street = number.getStreet();
                if (street != null) {
                    streetNumber = street + number.getNumber();
                } else {
                    streetNumber = number.getNumber();
                }
            }
            //
            String building = regeocodeAddress.getBuilding();// 标志性建筑,当道路为null时显示
            //区县
            if (!TextUtils.isEmpty(province)) {
                stringBuilder.append(province);
            }
            if (!TextUtils.isEmpty(district)) {
                stringBuilder.append(district);
            }
            //乡镇
            if (!TextUtils.isEmpty(township)) {
                stringBuilder.append(township);
            }
            //道路
            if (!TextUtils.isEmpty(streetName)) {
                stringBuilder.append(streetName);
            }
            //标志性建筑
            if (!TextUtils.isEmpty(building)) {
                stringBuilder.append(building);
            } else {
                //门牌号
                if (!TextUtils.isEmpty(streetNumber)) {
                    stringBuilder.append(streetNumber);
                }
            }
            if (TextUtils.isEmpty(stringBuilder)) {
                address = township;
            } else {
                address = stringBuilder.append("附近").toString();
            }
            //
            try {
                LogUtils.loge(this, "onRegeocodeSearched: " + "code = " + i + ",address = " + address);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        } else {
            StringBuilder stringBuilder = new StringBuilder();
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
            if (TextUtils.isEmpty(thf)) {
                if (!TextUtils.isEmpty(fn)) {
                    stringBuilder.append(fn);
                }
            }
            if (subLoc != null) {
                stringBuilder.append(subLoc);
            }
            if (ts != null) {
                stringBuilder.append(ts);
            }
            if (thf != null) {
                stringBuilder.append(thf);
            }
            if (subthf != null) {
                stringBuilder.append(subthf);
            }
            address = stringBuilder.toString();
            if (TextUtils.isEmpty(address)) {
                address = ts;
            }
        }
        if (TextUtils.isEmpty(address)) {
            address = mContext.getString(R.string.unknown_street);
        }
        mDeviceInfo.setAddress(address);
        if (isAttachedView()) {
            getView().setDeviceLocation(address, true);
        }
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
        try {
            LogUtils.loge(this, "onGeocodeSearched: " + "onGeocodeSearched");
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public void toPhotoDetail(int position, List<ScenesData> images) {
        if (images.size() > 0) {
            ArrayList<ImageItem> items = new ArrayList<>();
            for (ScenesData scenesData : images) {
                ImageItem imageItem = new ImageItem();
                imageItem.isRecord = false;
                imageItem.fromUrl = true;
                imageItem.path = scenesData.url;
                items.add(imageItem);
            }
            Intent intentPreview = new Intent(mContext, ImagePreviewDelActivity.class);
            intentPreview.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, items);
            intentPreview.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, position);
            intentPreview.putExtra(ImagePicker.EXTRA_FROM_ITEMS, true);
            intentPreview.putExtra(EXTRA_JUST_DISPLAY_PIC, true);
            getView().startACForResult(intentPreview, REQUEST_CODE_PREVIEW);
        } else {
            getView().toastShort(mContext.getString(R.string.no_photos_added));
        }
    }
}