package com.sensoro.smartcity.presenter;

import android.content.Context;
import android.content.Intent;

import com.sensoro.smartcity.activity.InspectionTaskDetailActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.imainviews.IInspectionTaskListActivityView;

public class InspectionTaskListActivityPresenter extends BasePresenter<IInspectionTaskListActivityView>{
    private Context mContext;

    @Override
    public void initData(Context context) {
        mContext = context;
    }

    @Override
    public void onDestroy() {

    }

    public void doItemClick() {
        Intent intent = new Intent(mContext, InspectionTaskDetailActivity.class);
        getView().startAC(intent);
    }
}
