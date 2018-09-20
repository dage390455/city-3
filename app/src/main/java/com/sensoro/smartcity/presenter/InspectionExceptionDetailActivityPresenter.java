package com.sensoro.smartcity.presenter;

import android.content.Context;

import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.imainviews.IInspectionExceptionDetailActivityView;

import java.util.ArrayList;

public class InspectionExceptionDetailActivityPresenter extends BasePresenter<IInspectionExceptionDetailActivityView>{
    private Context mContext;

    @Override
    public void initData(Context context) {
        mContext = context;

        //临时数据
        ArrayList<String> list = new ArrayList<>();
        list.add("5");
        list.add("望京soho");
        getView().updateTagsData(list);

        //临时数据
        ArrayList<String> list1 = new ArrayList<>();
        list1.add("蜂鸣器不响");
        list1.add("指示灯不亮");
        getView().updateExceptionTagsData(list1);

    }

    @Override
    public void onDestroy() {

    }
}
