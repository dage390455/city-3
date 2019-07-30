package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;

import com.google.gson.internal.LinkedTreeMap;
import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.helper.PreferencesHelper;
import com.sensoro.common.model.DeployAnalyzerModel;
import com.sensoro.common.model.DeployResultModel;
import com.sensoro.common.server.bean.ScenesData;
import com.sensoro.smartcity.imainviews.IOfflineDeployActivityView;
import com.sensoro.smartcity.util.DeployRetryUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class OfflineDeployPresenter extends BasePresenter<IOfflineDeployActivityView> {
    private Activity mActivity;
    private DeployRetryUtil deployRetryUtil;

    private ArrayList<DeployAnalyzerModel> deviceInfos = new ArrayList<>();

    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;

        deployRetryUtil = DeployRetryUtil.getInstance();
//        LinkedHashMap<String, DeployAnalyzerModel> allTask = deployRetryUtil.getAllTask();
        LinkedTreeMap<String, DeployAnalyzerModel> allTask = PreferencesHelper.getInstance().getofflineDeployData();

        if (null != allTask && allTask.size() > 0) {
            Iterator iter = allTask.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
//                Object key = entry.getKey();
                DeployAnalyzerModel val = (DeployAnalyzerModel) entry.getValue();
                deviceInfos.add(val);
                getView().updateAdapter(deviceInfos);

            }

        }

    }

    public void removeTask(DeployAnalyzerModel model) {

        deployRetryUtil.removeTask(model);
    }

    /**
     * 批量
     */
    public void dobatch() {


//        uploadTask();

    }

    /**
     * 单个
     *
     * @param model
     */

    public void uploadTask(DeployAnalyzerModel model) {


        getView().setCurrentTaskIndex(deviceInfos.indexOf(model));


        deployRetryUtil.retryTry(mActivity, model, new DeployRetryUtil.OnRetryListener() {
            @Override
            public void onCompleted(DeployResultModel deployResultModel) {
                deviceInfos.remove(model);
//                deployRetryUtil.removeTask(model);
                getView().updateAdapter(deviceInfos);

            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().setCurrentTaskIndex(-1);

            }

            @Override
            public void onStart() {

            }

            @Override
            public void onComplete(List<ScenesData> scenesDataList) {

            }

            @Override
            public void onError(String errMsg) {
                getView().setCurrentTaskIndex(-1);

            }

            @Override
            public void onProgress(String content, double percent) {

            }
        });
    }


    @Override

    public void onDestroy() {


    }
}
