package com.sensoro.smartcity.presenter;

import android.content.Context;
import android.content.Intent;

import com.lzy.imagepicker.bean.ImageItem;
import com.sensoro.smartcity.activity.ScanActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IInspectionUploadExceptionActivityView;

import java.util.ArrayList;
import java.util.List;

public class InspectionUploadExceptionActivityPresenter extends BasePresenter<IInspectionUploadExceptionActivityView> {
    private Context mContext;
    private List<String> exceptionTags = new ArrayList<>();

    @Override
    public void initData(Context context) {
        mContext = context;

        initExceptionTag();

    }

    private void initExceptionTag() {
        exceptionTags.add("指示灯不亮");
        exceptionTags.add("蜂鸣器不响");
        exceptionTags.add("外观损坏");
        exceptionTags.add("安置位置不对");
        exceptionTags.add("设备丢失");
        exceptionTags.add("按键故障");
        exceptionTags.add("蓝牙无效");
        getView().updateExceptionTagAdapter(exceptionTags);
    }

    @Override
    public void onDestroy() {

    }

    public List<ImageItem> getSelImageList() {
        //先返回一个空的，
        return new ArrayList<ImageItem>();
    }

    public void doUploadAndChange() {
        //上传异常

        Intent intent = new Intent(mContext, ScanActivity.class);
        intent.putExtra("type", Constants.TYPE_SCAN_CHANGE_DEVICE);
        getView().startAC(intent);
    }
}
