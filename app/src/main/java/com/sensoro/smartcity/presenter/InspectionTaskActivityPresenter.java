package com.sensoro.smartcity.presenter;

import android.content.Context;
import android.content.Intent;

import com.sensoro.smartcity.activity.InspectionActivity;
import com.sensoro.smartcity.activity.InspectionExceptionDetailActivity;
import com.sensoro.smartcity.activity.InspectionTaskActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.imainviews.IInspectionTaskActivityView;

public class InspectionTaskActivityPresenter extends BasePresenter<IInspectionTaskActivityView> {
    private Context mContext;

    @Override
    public void initData(Context context) {
        mContext = context;
    }

    @Override
    public void onDestroy() {

    }

    public void doItemClick(int state) {
        Intent intent = new Intent();
        switch (state){
            case 0:
                intent.setClass(mContext,InspectionActivity.class);
                break;
            case 1:
                intent.setClass(mContext,InspectionExceptionDetailActivity.class);
                break;
        }
        getView().startAC(intent);
    }
}
