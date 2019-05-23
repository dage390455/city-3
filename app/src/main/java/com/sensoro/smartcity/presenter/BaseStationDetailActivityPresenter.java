package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.model.EventData;
import com.sensoro.common.model.ImageItem;
import com.sensoro.common.server.CityObserver;
import com.sensoro.common.server.RetrofitServiceHelper;
import com.sensoro.common.server.bean.BaseStationDetailModel;
import com.sensoro.common.server.bean.DeviceInfo;
import com.sensoro.common.server.bean.ScenesData;
import com.sensoro.common.server.response.BaseStationDetailRsp;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.FrequencyPointActivity;
import com.sensoro.smartcity.imainviews.IBaseStationDetailActivityView;
import com.sensoro.smartcity.widget.imagepicker.ImagePicker;
import com.sensoro.smartcity.widget.imagepicker.ui.ImagePreviewDelActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.sensoro.smartcity.constant.Constants.EVENT_DATA_DEVICE_POSITION_CALIBRATION;
import static com.sensoro.smartcity.constant.Constants.EXTRA_JUST_DISPLAY_PIC;
import static com.sensoro.smartcity.constant.Constants.REQUEST_CODE_PREVIEW;

public class BaseStationDetailActivityPresenter extends BasePresenter<IBaseStationDetailActivityView> {
    private Activity mContext;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm:ss");
    private DecimalFormat decimalFormat = new DecimalFormat(".00");


    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessageEvent(EventData eventData) {
        int code = eventData.code;
        Object data = eventData.data;
        switch (code) {

            //位置校准的逻辑更新
            case EVENT_DATA_DEVICE_POSITION_CALIBRATION:
                if (data instanceof DeviceInfo) {
                    final DeviceInfo pushDeviceInfo = (DeviceInfo) data;
//                    if (pushDeviceInfo.getSn().equals(mDeviceInfo.getSn())) {
//                        mContext.runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                if (isAttachedView()) {
//                                    mDeviceInfo.cloneSocketData(pushDeviceInfo);
//                                    freshLocationDeviceInfo();
//                                    freshTopData();
//                                    handleDeviceInfoAdapter();
//                                }
//                            }
//                        });
//                    }
                }

                break;
            default:
                break;

        }
    }


    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        EventBus.getDefault().register(this);
        String sn = mContext.getIntent().getStringExtra("sn");

        if (!TextUtils.isEmpty(sn)) {
            requestDetailData(sn);
        }

    }


    public void requestDetailData(String sn) {

        RetrofitServiceHelper.getInstance().getBaseStatioDetail(sn).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<BaseStationDetailRsp>(this) {
            @Override
            public void onCompleted(BaseStationDetailRsp deviceCameraListRsp) {

                BaseStationDetailModel data = deviceCameraListRsp.getData();

                getView().dismissProgressDialog();
            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().dismissProgressDialog();
                getView().toastShort(errorMsg);

            }
        });

    }


    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);

    }

    /**
     * 获取图表数据并处理
     */
    public void requestData() {


        ArrayList<Entry> values1 = new ArrayList<>();

        ArrayList<Entry> values2 = new ArrayList<>();
        Random rand = new Random();


        for (int i = 0; i < 15; i++) {
            float val = rand.nextInt(68 - 45 + 1) + 45;

            values1.add(new Entry(1557901082 + i * 100, val));

        }

        for (int i = 0; i < 15; i++) {
            float val = rand.nextInt(38 - 10 + 1) + 10;
            values2.add(new Entry(1557901082 + i * 100, val));
        }


        LineDataSet set1, set2;

//            if (chart.getData() != null &&
//                    chart.getData().getDataSetCount() > 0) {
//                set1 = (LineDataSet) chart.getData().getDataSetByIndex(0);
//                set2 = (LineDataSet) chart.getData().getDataSetByIndex(1);
//                set1.setValues(values1);
//                set2.setValues(values2);
//                chart.getData().notifyDataChanged();
//                chart.notifyDataSetChanged();
//            } else {
        // create a dataset and give it a type
        set1 = new LineDataSet(values1, "DataSet 1");

//            set1.setAxisDependency(AxisDependency.LEFT);
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
//            set2.setFillColor(Color.parseColor("#6D5EAC"));


        set2.setDrawCircleHole(false);

        set2.setDrawCircles(false);

        set2.setDrawHighlightIndicators(false);
        set2.setDrawVerticalHighlightIndicator(true);
        set2.setDrawHorizontalHighlightIndicator(false);
//            set2.setHighLightColor(Color.parseColor("#37B0E9"));

        //set2.setFillFormatter(new MyFillFormatter(900f));


        // create a data object with the data sets
        LineData data = new LineData(set1, set2);
        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(9f);
        data.setDrawValues(false);

        // set data
//                chart.setData(data);
        getView().updateChartData(data);
//            }


    }


    /**
     * 周，日
     */
    public void switchCharType() {


    }

    public String stampToDate(String stap) {
        String time;
        long lt = Float.valueOf(stap).longValue();
        Date date = new Date(lt);
        time = simpleDateFormat.format(date);


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

        getView().updateTopView(time, decimalFormat.format(first) + "\u2103", decimalFormat.format(second) + "\u2103");

    }

    public void imageClick() {


    }

    public void startNetWorkInfoActivity() {


    }

    public void startFrequencyPointActivity() {

        mContext.startActivity(new Intent(mContext, FrequencyPointActivity.class));

    }

    public void startSelfCheckActivity() {


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

    public void doNavigation() {
//        List<Double> lonlat = mDeviceInfo.getLonlat();
//        if (lonlat.size() == 2) {
//            double v = lonlat.get(1);
//            double v1 = lonlat.get(0);
//            if (v == 0 || v1 == 0) {
//                getView().toastShort(mContext.getString(R.string.location_information_not_set));
//                return;
//            }
//        } else {
//            getView().toastShort(mContext.getString(R.string.location_information_not_set));
//            return;
//        }
//        Intent intent = new Intent();
//        if (AppUtils.isChineseLanguage()) {
//            intent.setClass(mContext, MonitorPointMapActivity.class);
//        } else {
//            intent.setClass(mContext, MonitorPointMapENActivity.class);
//        }
//        intent.putExtra(EXTRA_DEVICE_INFO, mDeviceInfo);
//        getView().startAC(intent);
    }
}