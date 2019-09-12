package com.sensoro.smartcity.factory;

import com.sensoro.common.constant.Constants;
import com.sensoro.common.model.DeployAnalyzerModel;
import com.sensoro.common.server.CityObserver;
import com.sensoro.common.server.RetrofitServiceHelper;
import com.sensoro.common.server.bean.DeviceInfo;
import com.sensoro.common.server.response.ResponseResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class OfflineDeployTimePoolFactory {
    public static ArrayList<DeployAnalyzerModel> createDeployMaps(LinkedHashMap<String, DeployAnalyzerModel> allTask, OnOfflineDeployTimePoolFactoryListener listener) {
        ArrayList<DeployAnalyzerModel> deviceInfos = new ArrayList<>();
        //设备列表集合
        List<String> deviceSN = new ArrayList<>();
        //摄像头列表集合
        List<String> cameraSN = new ArrayList<>();
        //基站列表集合
        List<String> stationSN = new ArrayList<>();
        //
        if (null != allTask && allTask.size() > 0) {
            Iterator iter = allTask.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                DeployAnalyzerModel val = (DeployAnalyzerModel) entry.getValue();
                if (Constants.TYPE_SCAN_DEPLOY_CAMERA == val.deployType) {
                    cameraSN.add(val.sn);
                } else if (Constants.TYPE_SCAN_DEPLOY_STATION == val.deployType) {
                    stationSN.add(val.sn);
                } else {
                    deviceSN.add(val.sn);
                }
                deviceInfos.add(val);
            }
            RetrofitServiceHelper.getInstance().getDeviceBriefInfoList(deviceSN, 1, 10000, null, null, null, null).subscribeOn
                    (Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<List<DeviceInfo>>>(this) {
                @Override
                public void onCompleted(ResponseResult<List<DeviceInfo>> deviceInfoListRsp) {
                    deviceInfoListRsp
                }

                @Override
                public void onErrorMsg(int errorCode, String errorMsg) {

                }
            });

        }
    }

    public interface OnOfflineDeployTimePoolFactoryListener {
        void onCompleted(ArrayList<DeployAnalyzerModel> deviceInfos);

        void onErrorMsg(int errorCode, String errorMsg);
    }
}
