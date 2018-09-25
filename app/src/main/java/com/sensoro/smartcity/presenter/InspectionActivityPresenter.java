package com.sensoro.smartcity.presenter;

import android.content.Context;
import android.content.Intent;

import com.sensoro.smartcity.activity.InspectionTaskDetailActivity;
import com.sensoro.smartcity.activity.InspectionUploadExceptionActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IInspectionActivityView;

import java.util.ArrayList;

public class InspectionActivityPresenter extends BasePresenter<IInspectionActivityView>
implements Constants{
    private Context mContext;
    private long startTime;

    @Override
    public void initData(Context context) {
        mContext = context;
        startTime = System.currentTimeMillis();
        //临时数据
        ArrayList<String> list = new ArrayList<>();
        list.add("5");
        list.add("望京soho");
        getView().updateTagsData(list);
    }

    @Override
    public void onDestroy() {

    }

    public void doInspectionDetail() {
        Intent intent = new Intent(mContext, InspectionTaskDetailActivity.class);
        getView().startAC(intent);
    }

    public void doUploadException() {
        Intent intent = new Intent(mContext, InspectionUploadExceptionActivity.class);
        intent.putExtra(EXTRA_INSPECTION_START_TIME,startTime);
        getView().startAC(intent);
    }

    public void doNormal() {
        getView().showNormalDialog();
    }

}
