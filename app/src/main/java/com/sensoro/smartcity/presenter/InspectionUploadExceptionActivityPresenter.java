package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.lzy.imagepicker.bean.ImageItem;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.ScanActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IInspectionUploadExceptionActivityView;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.response.ResponseBase;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class InspectionUploadExceptionActivityPresenter extends BasePresenter<IInspectionUploadExceptionActivityView>
implements  View.OnClickListener,Constants{
    private Activity mContext;
    private List<String> exceptionTags = new ArrayList<>();
    private long startTime;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;

        startTime = mContext.getIntent().getLongExtra(EXTRA_INSPECTION_START_TIME,0);
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

    /**
     * dialog点击事件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_tv_exception:
                getView().dismissExceptionDialog();
                getView().showProgressDialog();
                doUploadInspectionException();

                break;
            case R.id.dialog_tv_upload_change_device:
                getView().dismissExceptionDialog();
                doUploadAndChange();
                break;
            case R.id.dialog_tv_waite:
                getView().dismissExceptionDialog();
                break;
        }
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

    public void doUploadInspectionException() {
        long finishTime = System.currentTimeMillis();
        List<Integer> selectTags = getView().getSelectTags();
        String remarkMessage = getView().getRemarkMessage();

        Log.e("hcs","remarkMessage:::");
        for (Integer selectTag : selectTags) {
            Log.e("hcs",":selectTag::"+selectTag);
        }
        RetrofitServiceHelper.INSTANCE.doUploadInspectionResult("5b988dad5b0b9bece7d46fa2",1,startTime,finishTime,remarkMessage,
                null,selectTags).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        .subscribe(new CityObserver<ResponseBase>(this) {
            @Override
            public void onCompleted(ResponseBase responseBase) {
                Log.e("hcs",":正常::"+responseBase.toString());
                getView().dismissProgressDialog();
            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                Log.e("hcs",":错误了::"+errorMsg);
                getView().dismissProgressDialog();
            }
        });
    }
}
