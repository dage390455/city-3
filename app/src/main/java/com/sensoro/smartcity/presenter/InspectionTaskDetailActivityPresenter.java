package com.sensoro.smartcity.presenter;

import android.content.Context;
import android.content.Intent;

import com.sensoro.smartcity.activity.InspectionInstructionActivity;
import com.sensoro.smartcity.activity.InspectionTaskActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.imainviews.IInspectionTaskDetailActivityView;

import java.util.ArrayList;

public class InspectionTaskDetailActivityPresenter extends BasePresenter<IInspectionTaskDetailActivityView>{
    private Context mContext;

    @Override
    public void initData(Context context) {
        mContext = context;

        //临时数据
        ArrayList<String> list = new ArrayList<>();
        list.add("烟感(100)");
        list.add("一氧化碳(100)");
        list.add("电气火灾(100)");


    }

    @Override
    public void onDestroy() {

    }

    public void doRlContent() {
        Intent intent = new Intent(mContext, InspectionInstructionActivity.class);
        getView().startAC(intent);
    }

    public void doBtnStart() {
        Intent intent = new Intent(mContext, InspectionTaskActivity.class);
        getView().startAC(intent);
    }
}
